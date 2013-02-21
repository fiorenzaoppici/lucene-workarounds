import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.BaseParser;
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

    public ArrayList<String> extractPDFText(String dir){

        ArrayList<String> extractedTexts = new ArrayList<String>();
        ArrayList<String> filenames = PDFSearch(dir);

        if(null == filenames){
          System.out.println("Couldn't find no pdf files here");
            return null;
        }
        else{
            for (String filename: filenames){
                filename=dir+"/"+filename;
                 String extractedText = extractText(filename);
                 if(extractedText != null){
                     extractedTexts.add(extractedText);
                 }
            }
            if (extractedTexts.isEmpty()){
                System.out.println("There were problems with the files in this directory");
                return null;
            }
        }
        return extractedTexts;
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


    public static void main(String[] args) {
        PDFTextExtractor pdfte =new PDFTextExtractor();
        String dir1="./PDFCorpus";
        String dir2 = "./Lucene Workarounds.iml";
        ArrayList<String> extractedTexts = pdfte.extractPDFText(dir1);
        ArrayList<String> extractedTexts2 = pdfte.extractPDFText(dir2);

        for(String text:extractedTexts){
            System.out.println(text);
        }


    }
}