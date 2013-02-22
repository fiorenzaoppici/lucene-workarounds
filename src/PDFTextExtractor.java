import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;



/*
  Expansion of the PDFTextParser class example by Prasanna Seshandri,
  http://www.prasannatech.net/2009/01/convert-pdf-text-parser-java-api-pdfbox.html
 */

public class PDFTextExtractor{
        PDFParser parser;
        String parsedText;
        PDFTextStripper pdfStripper;
        PDDocument pdDoc;
        COSDocument cosDoc;
        PDDocumentInformation pdDocInfo;

        public PDFTextExtractor(){

        }

    //method that parses a PDF file into plain text retrieved from a filename
    public String extractText(String filename){
        System.out.println("Extracting text from PDF document "+filename+".");
        File f=new File(filename);

        //check if the filename is valid
        if(!f.isFile()){
            System.out.println("File "+filename+" doesn't exist.");
            return null;
        }

        //let's try to open the PDFParser on a bytestream
        try{
            FileInputStream fis = new FileInputStream(f);
            parser=new PDFParser(fis);
        }catch(Exception e){
            System.out.println("Couldn't open PDFParser");
            return null;
        }

        //let's try to parse the text and extract the string text from the returned document
        try{
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
            cosDoc.close();
        }catch(Exception e){
            System.out.print("Couldn't properly parse the PDF document");
            e.printStackTrace();

            //try to see if the document streams have been declared, and close them in case (saves memory)
                try{
                      if (cosDoc != null){
                          cosDoc.close();
                      }
                      if (pdDoc != null){
                          pdDoc.close();
                      }
                }catch(Exception e1){
                    e.printStackTrace();
                }
            return null;
        }
      return parsedText;
    }

    public IndexWriter indexPDFDir(IndexWriter w,String dir) throws IOException {

        ArrayList<String> filenames = PDFSearch(dir);

        if(null == filenames){
          System.out.println("Couldn't find no pdf files here");
            return null;
        }
        else{
            for (String filename: filenames){
                String fullFilename=dir+"/"+filename;
                 String extractedText = extractText(fullFilename);
                 if(extractedText != null){
                     Document doc = new Document();
                     doc.add(new Field("Title" , filename, Field.Store.YES, Field.Index.ANALYZED));
                     doc.add(new Field ("Text" , extractedText, Field.Store.YES, Field.Index.ANALYZED));
                     w.addDocument(doc);
                 }
            }
        }
        System.out.println(w.numDocs());
        return w;
    }

    public ArrayList<String> PDFSearch(String dir){

        String directory = dir;
        String prettydirectory=dir.substring(2);
        ArrayList<String>filenames = new ArrayList<String>();
        File f= new File(dir);

        if (f.isDirectory()){
            File[] allFiles = f.listFiles();
            System.out.println();
            System.out.println("All the .pdf files in the "+ prettydirectory +" directory:");
            System.out.println();
            for(File f1: allFiles) {
                if (f1.isFile()){
                    if(f1.getName().endsWith("pdf")||f1.getName().endsWith("PDF"))
                        filenames.add(f1.getName());
                }
            }
            if (filenames.isEmpty()){
                System.out.println();
                System.out.println("No file found in selected directory");
                return null;
            }
            for (int i=0; i<filenames.size();i++){
                System.out.print(i+1+": ");
                System.out.println(filenames.get(i));
            }
        }else{
            System.out.println();
            System.out.println("The directory "+prettydirectory+" is invalid");
            return null;
        }
        return filenames;
    }

    public static void printTopDocs(TopDocs hits, IndexSearcher searcher) throws IOException{
        System.out.println("Found " + hits.totalHits + " hits.");
        for(int i=0;i<hits.totalHits;++i) {
            float docScore =hits.scoreDocs[i].score;
            int docId = hits.scoreDocs[i].doc;
            Document d = searcher.doc(docId);
            System.out.print((i + 1) + ". " + d.get("Title")+" Score:"+docScore) ;
            System.out.println();
        }
    }

}