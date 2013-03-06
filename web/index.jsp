<%@page import="java.util.ArrayList"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="style.css">
<jsp:useBean id="pdfUtilities" class="lucene.pdf.PDFTextExtractor" scope="session"></jsp:useBean>
<jsp:useBean id="pdfQuerier" class="lucene.pdf.PDFTest" scope="session"></jsp:useBean>
</head>
<body>
<div id="logodiv">
    <h1>PDFIndexer</h1>
</div>
<div id="menubar">
    <div class="menuitem">About</div>
    <div class="menuitem">Search</div>
</div>
<div id="searchpane">
    <form id="fields" method="get" action="index.jsp">
        <span>Search terms:</span>
        <input name="queryString" value="<%=request.getParameter("queryString")%>"></input>
        <button id="submit"></button>
    </div>
</div>
<div id="showResults">
    <% 
        // cerca tutti i file pdf presenti
        // indicizzali
        // presentali
        String fullDir = "/home/fiorenza/Scrivania/nexatirocinio/Lucene workarounds/web/PDFCorpus";
        ArrayList <String> titles = pdfUtilities.PDFSearch(fullDir);
        
        for (String title:titles){ 
        String relativePath = "./PDFCorpus/"+title;
    %>
 <div class="pdfElement">
     <embed src="<%=relativePath%>">
     <a class="pdfLink"href="<%=relativePath%>">
         <%=title%>
     </a>
 </div>
        <%}%>
</div>
</div>
</body>
</html>