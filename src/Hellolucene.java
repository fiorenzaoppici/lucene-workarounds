import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

public class Hellolucene {

    public static void main(String[] args) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);

        // 1. create the index
        Directory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);

        IndexWriter w = new IndexWriter(index, config);
        addDoc(w, "Lucene in Action", "");
        addDoc(w, "Lucene for Dummies", "there's hope for everybody");
        addDoc(w, "Luciano for Dummies", "how to communicate with the average italian dad");
        addDoc(w, "Lucene for Smartasses", "");
        addDoc(w, "Managing Gigabytes", "or the art of fast bittorrent downloading");
        addDoc(w, "The Art of Computer Science", "");
        w.close();

        // 2. query
        String querystr ="Lucene";

        QueryParser parser = new QueryParser(Version.LUCENE_35,"title", analyzer);
        Query query = parser.parse("luc* OR dummies OR Computer");

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = new QueryParser(Version.LUCENE_35, "title", analyzer).parse(querystr);

        // 3. search
        int hitsPerPage = 10;
        IndexReader ir = IndexReader.open(index,false);
        IndexSearcher searcher = new IndexSearcher(ir);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        TopScoreDocCollector collector2 = TopScoreDocCollector.create(5, true);
        searcher.search(q, collector);
        searcher.search(query,collector2);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        ScoreDoc[] hits2 = collector2.topDocs().scoreDocs;

        // 4. display results
        printScoreDocs(hits,searcher);
        printScoreDocs(hits2, searcher);

        // searcher can only be closed when there
        // is no need to access the documents any more.
        searcher.close();
    }

    /*
    given an IndexWriter and a String, this creates a new documents with the title
    field set to value, and adds it to the Index.
    */

    private static void addDoc(IndexWriter w, String title, String subtitle) throws IOException {
        Document doc = new Document();
        doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("subtitle", subtitle , Field.Store.YES, Field.Index.ANALYZED));
        w.addDocument(doc);
    }

    //this method prints all documents' info for every document in a ScoreDoc array

    private static void printScoreDocs(ScoreDoc[] hits, IndexSearcher searcher) throws IOException{
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.print((i + 1) + ". " + d.get("title")) ;
            if(d.get("subtitle").length()>0)
                System.out.println(": " + d.get("subtitle"));
            else System.out.println();
        }
    }
}
