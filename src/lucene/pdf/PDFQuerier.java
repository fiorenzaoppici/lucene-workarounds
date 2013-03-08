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
public class PDFQuerier {
    
        private IndexReader ir;
        private IndexWriter iw;
        private IndexSearcher is;
        private IndexWriterConfig iwc;
        private RAMDirectory indexDir;  //faster than fsddirectory, non-permanent
        private PDFTextExtractor pdfte;
        private StandardAnalyzer sa;
        
    public void buildIndex(String searchDir)throws IOException{
        indexDir = new RAMDirectory();
        sa =new StandardAnalyzer(Version.LUCENE_35);
        iwc = new IndexWriterConfig(Version.LUCENE_35 , sa);
        iw = new IndexWriter(indexDir,iwc);
        //invocazione dell'indicizzazione speciale per i PDF
        pdfte = new PDFTextExtractor();
        pdfte.indexPDFDir(iw, searchDir);

        iw.close();
    }    

    public TopDocs searchQuery(String queryString) throws IOException, ParseException{

        //definizione della query
        Query q =new QueryParser(Version.LUCENE_35,"Text",sa ).parse(queryString);

        //dovrebbe funzionare per ordinare i riusltati in ordine alfabetico ma nn funza.
        SortField sf = new SortField("Title",SortField.STRING);
        Sort s = new Sort(sf);
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
    
    public RAMDirectory getIndexDir(){
        return indexDir;
    }
    
}