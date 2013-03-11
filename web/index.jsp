<%@page import="org.apache.lucene.index.IndexReader"%>
<%@page import="org.apache.lucene.search.ScoreDoc"%>
<%@page import="org.apache.lucene.store.RAMDirectory"%>
<%@page import="org.apache.lucene.search.IndexSearcher"%>
<%@page import="org.apache.lucene.document.Document"%>
<%@page import="org.apache.lucene.search.TopDocs"%>
<%@page import="java.util.ArrayList"%>
<!DOCTYPE HTML>
<html>
<head>
<link rel="stylesheet" type="text/css" href="style.css">
<jsp:useBean id="pdfUtilities" class="lucene.pdf.PDFTextExtractor" scope="request"></jsp:useBean>
<jsp:useBean id="pdfQuerier" class="lucene.pdf.PDFQuerier" scope="request"></jsp:useBean>
<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
<script src="js/index.js"></script>
</head>
<body>
<div id="logodiv">
    <h1>PDFinder</h1>
</div>
<div id="menubar">
    <div class="menuitem">About</div>
    <div class="menuitem">Search</div>
</div>
    <%  
        String searchDir = "C:\\Documents and Settings\\Fio\\Desktop\\Nexa Tirocinio\\TellMeFirst\\lucene-workarounds\\web\\PDFCorpus";
        String queryTerm = request.getParameter("queryString");
        pdfQuerier.buildIndex(searchDir);
        RAMDirectory indexDir = pdfQuerier.getIndexDir();
        ArrayList <String> titles = pdfUtilities.PDFSearch(searchDir);  
    %>
    
<div id="searchpane">
    <form id="fields" method="get" action="index.jsp">
        <span>Search terms:</span>
        <input name="queryString" value="<%=request.getParameter("queryString")%>">
        <button id="submit"></button>
    </form>  
    <div id="keywords">
        <button id="AND" class="keywordsHelper">AND</button>
        <button id="OR" class="keywordsHelper">OR</button>
        <button id="NOT" class="keywordsHelper">NOT</button>
        <button id="\*" class="keywordsHelper">any character</button>
    </div>  
    </div>
<div id="showResults">
    <% if(queryTerm==null||queryTerm==""){
        for (String title:titles){ 
        String relativePath = "./PDFCorpus/"+title;
    %>
 <div class="pdfElement">
     <embed src="<%=relativePath%>">
     <a class="pdfLink" href="<%=relativePath%>">
         <%=title%>
     </a>
 </div>
        <%}
    }else{
       TopDocs td = pdfQuerier.searchQuery(queryTerm);
       IndexSearcher is = new IndexSearcher(IndexReader.open(indexDir,true));
       Document[] sdArray = pdfUtilities.getTopDocs(td,is);
       for(int i = 0; i<sdArray.length; i++){     
           String title = sdArray[i].get("Title");
           String relativePath = "./PDFCorpus/"+title;        
      %>     
      <div>
          <a href="<%=relativePath%>"><%=title%></a>
      </div>
<%      }
       if (sdArray.length==0){%>
       <div id="errorPane">
           No documents found for search terms <%=queryTerm%>
       </div>
<%      }
    }
%>
</div>
<div id="about">
This is a very simple search engine integrating
<a href="http://lucene.apache.org/">Lucene 3.5 </a>with
<a href="http://www.oracle.com/technetwork/java/javaee/jsp/index.html">JSP</a>.<br>
PDFinder is designed to return search results from a small subset of 
inline PDF documents on various topics (Pop and rock music, programming tools, cinema and literary essays, etc).
</div>
</body>
</html>