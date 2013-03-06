package lucene.pdf;


import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * Created with IntelliJ IDEA.
 * User: fiorenza
 * Date: 22/02/13
 * Time: 11.07
 * To change this template use File | Settings | File Templates.
 */
public class PDFTest {
    
        IndexReader ir;
        IndexWriter iw;
        IndexSearcher is;
        IndexWriterConfig iwc;
        RAMDirectory indexDir;  //faster than fsddirectory, non-permanent
        PDFTextExtractor pdfte;

        QueryParser qp;
        Query q;
        String queryString = "";
        Sort s;
        SortField sf;

        StandardAnalyzer sa;
        String searchDir;

    public TopDocs searchQuery() throws IOException, ParseException{
        //definizione dell' indice e dell'analizzatore
        indexDir = new RAMDirectory();
        searchDir = "./PDFCorpus";
        sa =new StandardAnalyzer(Version.LUCENE_35);
        iwc = new IndexWriterConfig(Version.LUCENE_35 , sa);
        iw = new IndexWriter(indexDir,iwc);

        //invocazione dell'indicizzazione speciale per i PDF
        pdfte = new PDFTextExtractor();
        pdfte.indexPDFDir(iw, searchDir);

        iw.close();
        //definizione della query
        q =new QueryParser(Version.LUCENE_35,"Text",sa ).parse(queryString);

        //dovrebbe funzionare per ordinare i riusltati in ordine alfabetico ma nn funza.
        sf = new SortField("Title",SortField.STRING);
        s = new Sort(sf);
        TopFieldCollector tpfc = TopFieldCollector.create(s,10, true, true, true, true);
        //ricerca della query
        ir = IndexReader.open(indexDir,true);
        is = new IndexSearcher(ir);
        is.search(q,tpfc);

        TopDocs td = tpfc.topDocs(0,10);

        ir.close();
        is.close();
        
        return td;
    }
    
    public String getQueryString(){
        return this.queryString;
    }
    
    public void setQueryString(String qstr){
        this.queryString = qstr;
    }
    
}


