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
            //Retrieval and indexing of all PDF filenames in the PDFCorpus directory
            String searchDir = "/home/fiorenza/Scrivania/nexatirocinio/Lucene workarounds/web/PDFCorpus";
            String queryTerm = request.getParameter("queryString");
            pdfQuerier.buildIndex(searchDir);
            RAMDirectory indexDir = pdfQuerier.getIndexDir();
            ArrayList<String> titles = pdfUtilities.PDFSearch(searchDir);
        %>
        <div id="search">
            <div id="searchpane">
                <form id="fields" method="get" action="index.jsp">
                    <span>Search terms:</span>
                    <%
                        if (queryTerm != null && queryTerm != "") {
                    %>
                    <input name="queryString" value="<%=queryTerm%>">
                    <%
                    } else {
                    %>
                    <input name="queryString" value="">
                    <%            }
                    %>
                    <button id="submit"></button>
                </form>
                <div id="keywords">
                    <button id="AND" class="keywordsHelper">AND</button>
                    <button id="OR" class="keywordsHelper">OR</button>
                    <button id="NOT" class="keywordsHelper">NOT</button>
                </div>
            </div>
            <div id="showResults">
                <%
                    // if the page is loaded/no query was submitted
                    // find all PDFs and display them to the user in a grid layout
                    // with 4 columns.
                    if (queryTerm == null || queryTerm == "") {
                %>
                <table>
                    <tbody>
                        <%  int columnNumber = 4;
                            for (int i = 0; i < titles.size(); i++) {
                                String title = titles.get(i);
                                String relativePath = "./PDFCorpus/" + title;
                                //checks if it's necessary to add another row
                                if (i % 4 == 0) {
                        %>
                        <tr>
                            <% }%>
                            <td>
                                <div class="pdfElement">
                                    <embed src="<%=relativePath%>">
                                    <a class="pdfLink" href="<%=relativePath%>">
                                        <%=title%>
                                    </a>
                                </div>
                            </td>
                            <%  //this checks if it's the closing element
                    if (i % 4 == 3) {%>
                        </tr>
                        <%   }
                            if (i == titles.size() - 1) {
                        %>
                    </tbody>
                    <%    }
                        }
                    } else {
                        //If the user sent a query, return the documents from it.
                        TopDocs td = pdfQuerier.searchQuery(queryTerm);
                        IndexSearcher is = new IndexSearcher(IndexReader.open(indexDir, true));
                        Document[] sdArray = pdfUtilities.getTopDocs(td, is);
                    %>
                    <div id="showResultsInfo">
                        <%
                            if (sdArray.length > 0) {

                        %>
                        Your search for <span>"<%=queryTerm%>"</span> returned
                        <span><%=sdArray.length%></span> results:
                    </div>
                    <%
                 } else {%>
                    <div id="errorPane">
                        No documents found for search terms <%=queryTerm%>
                    </div>
                    <% }
                        for (int i = 0; i < sdArray.length; i++) {
                            String title = sdArray[i].get("Title");
                            String textDesc = sdArray[i].get("Text");
                            textDesc = textDesc.substring(0, 200);
                            String relativePath = "./PDFCorpus/" + title;
                    %>
                    <div>
                        <a class="resultAnchor" href="<%=relativePath%>"><%=title%></a>
                        <div class="textDesc"><%=textDesc%></div>
                    </div>
                    <%      }
                        }
                    %>
            </div>
        </div>
        <div id="about">
            <img src="images/eng.jpg">
            <h1>About</h1>
            <p>This is a very simple search engine integrating
                <a href="http://lucene.apache.org/">Lucene 3.5 </a>with
                <a href="http://www.oracle.com/technetwork/java/javaee/jsp/index.html">JSP</a>.<br>
            </p>
            <p>
                PDFinder is designed to return search results from a small set of
                inline PDF documents on various topics (Pop and rock music,
                programming tools, cinema and literary essays, etcetera).
            </p>
            <p>
                In the next future, PDFinder will be able to index and perform
                search on dbpedia entries.
            </p>
            <p>
                This page was written by Fiorenza Oppici <sub>italian
                .devotchka[at]gmail.com</sub> as a sandbox for learning
                 Lucene tools.</br>
                You can find the full repo with commits at
                <a href=https://github.com/fiorenzaoppici/lucene-workarounds>
                fiorenzaoppici/lucene-workarounds</a>.
            </p>
        </div>
    </body>
</html>