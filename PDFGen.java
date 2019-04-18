package com.nissan.dev;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import be.quodlibet.boxable.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.List;

import static javax.swing.text.html.CSS.Attribute.FONT_SIZE;


/**
 * Main class to generate PDF
 *
*
 */
public class PDFGen  {
    /**
     * Standard constructor for this class that fixes the color & fontSize
     *
     * @param inputFile base file to be used
     * @param imagePath used to specify the image to be used as a stamp
     * @param outputFile filename to be generated, if it already exists it will be replaced
     */
    Logger logger = LoggerFactory.getLogger(PDFGen.class);

    private static PDFont FONT = PDType1Font.HELVETICA;
    private static float FONT_SIZE = 14;
    private static final float LINE_SOACING = -1.5f;
    private static float LEADING = LINE_SOACING * FONT_SIZE;
    private static Cell<PDPage> cell;
    private static Row<PDPage> headerRow;
    private static BaseTable table;
    private static Row<PDPage> row;
    private static Row<PDPage> row2;

    public void createPDFFromImage(String inputFile, String imagePath, String outputFile) throws IOException {



       // try (PDDocument doc = PDDocument.load(new File(inputFile))) {
        try(PDDocument doc = new PDDocument()){
            PDType0Font font = PDType0Font.load(doc, new File("/Users/damiencontreras/Desktop/kosugi_Maru/KosugiMaru-Regular.ttf"));

            PDPage page = new PDPage();
            doc.addPage(page);

            PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {

                //draw rectangle
                int cursorX = 70;
                int cursorY = 500;

               // contents.setNonStrokingColor(200, 200, 200); //gray background
               // contents.fillRect(cursorX, cursorY, 100, 50);


                /*
                contents.beginText();
                contents.setFont(font, 12);
                contents.newLineAtOffset(100, 700); // page size
                contents.showText("完成検査票");
                contents.endText();*/

                //contents.beginText();
                //contents.setFont(font, 12);
                //contents.newLineAtOffset(100, 712);
                //contents.showText("日産自動車株式会社　追浜工場品質保証部");
                //contents.endText();

                float marginTop = 50;
                float marginLeft = 7;
                int topOffset = 20;


                DTextBox t0 = new DTextBox(doc, contents, "完成検査票", marginLeft, page.getMediaBox().getHeight()-topOffset, 200, 20, new Color(255,255,255));
                //t0.addBackground();

                //line 2
                topOffset += 40;
                DTextBox t1 = new DTextBox(doc, contents);
                // , "日産自動車株式会社　追浜工場品質保証部", 0f, 700f, 200, 25, new Color(255,255,255));
                t1.addPosition(40 + marginLeft, page.getMediaBox().getHeight()-topOffset);
                t1.addSize(200,20);
                t1.addBackground(new Color(221,235, 247));
                t1.addText("日産自動車株式会社　追浜工場品質保証部");

                //header line 1
                topOffset += 40;
                DHeader t2 = new DHeader(doc, contents, "車種", marginLeft, page.getMediaBox().getHeight()- topOffset, 50, 20);
                DHeader t3 = new DHeader(doc, contents, "車種記号", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t4 = new DHeader(doc, contents, "CCR NO. (車両一貫NO)", marginLeft + 50*2, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t5 = new DHeader(doc, contents, "車台番号", marginLeft + 50*3, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t6 = new DHeader(doc, contents, "カラー", marginLeft + 50*4, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t7 = new DHeader(doc, contents, "内張", marginLeft + 50*5, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t8 = new DHeader(doc, contents, "仕向地", marginLeft + 50*6, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t9 = new DHeader(doc, contents, "型式", marginLeft + 50*7, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t10 = new DHeader(doc, contents, "原動機型式 電動機型式", marginLeft + 50*8, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t11 = new DHeader(doc, contents, "EX－NO", marginLeft + 50*9, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t12 = new DHeader(doc, contents, "ラインNO", marginLeft + 50*10, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t13 = new DHeader(doc, contents, "", marginLeft + 50*11, page.getMediaBox().getHeight()-topOffset, 50, 20);

                //header line 2
                topOffset += 30;
                DHeader t14 = new DHeader(doc, contents, "区分", marginLeft, page.getMediaBox().getHeight()- topOffset, 50, 20);
                DHeader t15 = new DHeader(doc, contents, "検査項目", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t16 = new DHeader(doc, contents, "検査内容", marginLeft + 50*2, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t17 = new DHeader(doc, contents, "検査基準", marginLeft + 50*3, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t18 = new DHeader(doc, contents, "測定値", marginLeft + 50*4, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t19 = new DHeader(doc, contents, "判定", marginLeft + 50*5, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t20 = new DHeader(doc, contents, "検査員", marginLeft + 50*6, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t21 = new DHeader(doc, contents, "手直し者", marginLeft + 50*7, page.getMediaBox().getHeight()-topOffset, 50, 20);
                DHeader t22 = new DHeader(doc, contents, "確認 (再検査者)", marginLeft + 50*8, page.getMediaBox().getHeight()-topOffset, 50, 20);

                //Cells line 1
                topOffset += 15;
                DCellLabel t23 = new DCellLabel(doc, contents, "型式", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t24 = new DCellLabel(doc, contents, "車台番号", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t25 = new DCellLabel(doc, contents, "原動機型式", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t26 = new DCellLabel(doc, contents, "電動機型式", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t27 = new DCellLabel(doc, contents, "始動性", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t28 = new DCellLabel(doc, contents, "異音", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t29 = new DCellLabel(doc, contents, "振動", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t30 = new DCellLabel(doc, contents, "作動", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t31 = new DCellLabel(doc, contents, "油もれ・水もれ", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t32 = new DCellLabel(doc, contents, "取付状態", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t33 = new DCellLabel(doc, contents, "起動性", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t34 = new DCellLabel(doc, contents, "異音", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t35 = new DCellLabel(doc, contents, "振動", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t36 = new DCellLabel(doc, contents, "作動", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t37 = new DCellLabel(doc, contents, "水もれ", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t38 = new DCellLabel(doc, contents, "取付状態", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t39 = new DCellLabel(doc, contents, "水もれ", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t40 = new DCellLabel(doc, contents, "他部品との隙", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t41 = new DCellLabel(doc, contents, "クランプの締付", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t42 = new DCellLabel(doc, contents, "ベルトの張り", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t43 = new DCellLabel(doc, contents, "作動状態", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t44 = new DCellLabel(doc, contents, "液もれ　（除：機械式）", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t45 = new DCellLabel(doc, contents, "操作性", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t46 = new DCellLabel(doc, contents, "油もれ", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t47 = new DCellLabel(doc, contents, "異音", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t48 = new DCellLabel(doc, contents, "変速性能", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t49 = new DCellLabel(doc, contents, "機能", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t50 = new DCellLabel(doc, contents, "油もれ", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t51 = new DCellLabel(doc, contents, "取付部のガタ", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t52 = new DCellLabel(doc, contents, "振れ", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t53 = new DCellLabel(doc, contents, "取付部のガタ", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t54 = new DCellLabel(doc, contents, "ブーツの亀裂", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t55 = new DCellLabel(doc, contents, "作動", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t56 = new DCellLabel(doc, contents, "警報音", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t57 = new DCellLabel(doc, contents, "表示灯の点灯", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t58 = new DCellLabel(doc, contents, "操作・セット車速", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t59 = new DCellLabel(doc, contents, "操作・セット車速", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;
                DCellLabel t60 = new DCellLabel(doc, contents, "警告灯の点灯", marginLeft + 50, page.getMediaBox().getHeight()-topOffset, 50, 15);topOffset +=15;


                // createBox(contents, font, "test-------------", 0f, 700f, 200, 25, 1, 1, 1, 1);


                float margin = 50;
                float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
                float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
                boolean drawContent = true;
                float yStart = yStartNewPage;
                float bottomMargin = 70;
                float yPosition = 550;

;




/*


                    BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);

                Row<PDPage> headerRow = table.createRow(15f);
                Cell<PDPage> cell = headerRow.createCell(11, "区分");
                cell.setFont(font);
                cell.setFillColor(Color.LIGHT_GRAY);
                cell = headerRow.createCell(11, "検査項目");
                cell.setFont(font);
               cell = headerRow.createCell(11, "検査内容");
                cell.setFont(font);
                cell = headerRow.createCell(11, "検査基準");
                cell.setFont(font);
                cell = headerRow.createCell(11, "測定値");
                cell.setFont(font);
                cell = headerRow.createCell(11, "判定");
                cell.setFont(font);
                cell = headerRow.createCell(11, "検査員");
                cell.setFont(font);
                cell = headerRow.createCell(11, "手直し者");
                cell.setFont(font);
                cell = headerRow.createCell(11, "確認 (再検査者)");
                cell.setFont(font);


                table.addHeaderRow(headerRow);



                Row<PDPage> row = table.createRow(30);
                yStart -= row.getHeight();

                float pageBottomMargin = 70;
                float pageTopMargin = 2*margin;

                cell = row.createCell(11, "車両");
                cell.setFont(font);
                cell = row.createCell(70, "1234 漢字も大丈夫 test");
                cell.setFont(font);

                Row<PDPage> row2 = table.createRow(30);
                cell = row2.createCell(11, "車両");
                cell.setFont(font);
                cell = row2.createTableCell(70, "<table><tr><td>1</td><td>b1</td></tr><tr><td>b1</td></tr></table>",doc, page, yStart, pageBottomMargin, 0);
                        cell.setFont(font);


                for(int i=0;i<200;i++) {
                    row = table.createRow(12);
                    cell = row.createCell(11, "車両" + i);
                    cell.setFont(font);
                    cell = row.createCell(70, "1234 漢字も大丈夫");
                    cell.setFont(font);
                }
                table.draw();

                float scale = 0.2f;
                contents.drawImage(pdImage, 20, 20, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
*/

            }

            //encryption
            int keyLength = 256;
            AccessPermission ap = new AccessPermission();

            StandardProtectionPolicy spp = new StandardProtectionPolicy("12345", "", ap);
            spp.setEncryptionKeyLength(keyLength);
            spp.setPermissions(ap);
            doc.protect(spp);

            doc.save(outputFile);
            doc.close();
        }


        /*

        */




    }
    /**
     * For test purpose only
     *
     */
    public void createBox(PDPageContentStream contents, PDType0Font font,  String boxContent, float x, float y, int width, int height, int borderLeft, int borderRight, int borderTop, int borderBottom){
        try {
            contents.setNonStrokingColor(221, 235, 247); //gray background
            contents.fillRect(x, y, width, height);

            //borders
            contents.drawLine(x, y, x+width, y); // border bottom
            contents.drawLine(x, y+height, x+width, y+height); // border top
            contents.drawLine(x, y, x, y+height); // border left
            contents.drawLine(x+width, y, x+width, y+height); //border right

            //text content
            contents.beginText();
            writeText(contents, width,height, x, y, boxContent, false);
            /*contents.setFont(font, 20);
            contents.setNonStrokingColor(0, 0, 0);
            contents.moveTextPositionByAmount(x+5, y+5);
            //contents.newLineAtOffset(x, y);
            contents.drawString(boxContent);*/
            contents.endText();

        }catch (IOException ex){
            System.err.println("Error");
        }


    }

    /**
     * Test function to sign PDF - not used
     *
     */
    public void signDetached(PDDocument document, OutputStream output)
throws IOException
    {
        	        int accessPermissions = SigUtils.getMDPPermission(document);
                if (accessPermissions == 1)
            	        {
                        throw new IllegalStateException("No changes to the document are permitted due to DocMDP transform parameters dictionary");
            	        }

                // create signature dictionary
        /*
        	        PDSignature signature = new PDSignature();
        	        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        	        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        	        signature.setName("Damien Contreras");
        	        signature.setLocation("Tokyo, JP");
        	        signature.setReason("Testing");
        	        // TODO extract the above details from the signing certificate? Reason as a parameter?

        	        // the signing date, needed for valid signature
        	        signature.setSignDate(Calendar.getInstance());

        	        // Optional: certify
        	        if (accessPermissions == 0)
            	        {
            	            SigUtils.setMDPPermission(document, signature, 2);
            	        }
                /*
                if (isExternalSigning())
                    {
            	            System.out.println("Sign externally...");
            	            document.addSignature(signature);
                        ExternalSigningSupport externalSigning =
                                      document.saveIncrementalForExternalSigning(output);
                        // invoke external signature service
            	            byte[] cmsSignature = sign(externalSigning.getContent());
            	            // set signature bytes received from the service
            	            externalSigning.setSignature(cmsSignature);
            	        }
        	        else
        	        {

            	    //    }

        */



        	    }
    // this is method to call number of lines loop document to write

    private void writeText(PDPageContentStream contentStream, float width,float height, float sx,
                           float sy, String text, boolean justify) throws IOException {
        List<String> lines = loopLines(text,width,height);
        contentStream.setFont(FONT, FONT_SIZE);
        System.out.println("######### FONT_SIZE : "+FONT_SIZE);
        System.out.println("######### lines : "+lines.size());
        contentStream.newLineAtOffset(sx, (sy+(height-FONT_SIZE)));// cursore first line to print to adject into box
        for (String line: lines) { // number of lines to loop
            float charSpacing = 0;
            if (justify){ // text wrapp
                if (line.length() > 1) {
                    float size = FONT_SIZE * FONT.getStringWidth(line) / 1000;
                    float free = width - size;
                    if (free > 0 && !lines.get(lines.size() - 1).equals(line)) {
                        charSpacing = free / (line.length() - 1);
                    }
                }
            }
            contentStream.setCharacterSpacing(charSpacing);
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, LEADING);// next line to move cursore
        }
    }

    private String heightwidthString(String text, float width,float height) throws IOException {
        List<String> lines;// = parseLines(text, width);
        boolean isTrue=true;
        do {
            FONT_SIZE = FONT_SIZE - 1;
            LEADING = LINE_SOACING * FONT_SIZE;
            lines = parseText2Lines(text, width);
            float fheight = ( FONT.getFontDescriptor().getCapHeight()) / 1000 * FONT_SIZE;
            System.out.println("######### (lines.size()*height) : "+(lines.size()*(fheight*1.7)));
            System.out.println("######### (lines.size()) : "+lines.size());
            System.out.println("######### height : "+fheight);
            System.out.println("######### FONT_SIZE : "+FONT_SIZE);
            System.out.println("***********************************************************************");
            if(!((lines.size()*(fheight*1.7))>height)) isTrue= false;
        }while(isTrue);
        System.out.println("######### (lines.size()) : "+lines.size());

        String tempString ="";
        for (int i=0; i<lines.size(); i++)
            tempString+=lines.get(i)+"<br />";
        return tempString;
    }



    // this method is used to loop height width, to adject the height and to check height
    private List<String> loopLines(String text, float width,float height) throws IOException {
        List<String> lines;// = parseLines(text, width);
        boolean isTrue=true;
        do {
            FONT_SIZE = FONT_SIZE - 1;
            LEADING = LINE_SOACING * FONT_SIZE;
            lines = parseText2Lines(text, width);
            float fheight = ( FONT.getFontDescriptor().getCapHeight()) / 1000 * FONT_SIZE;
            System.out.println("######### (lines.size()*height) : "+(lines.size()*(fheight*1.7)));
            System.out.println("######### (lines.size()) : "+lines.size());
            System.out.println("######### height : "+fheight);
            System.out.println("######### FONT_SIZE : "+FONT_SIZE);
            System.out.println("***********************************************************************");
            if(!((lines.size()*(fheight*1.7))>height)) isTrue= false; // @ check height
            // FONT_SIZE = FONT_SIZE - 1;
            //LEADING = LINE_SOACING * FONT_SIZE;

        }while(isTrue);
        System.out.println("######### (lines.size()) : "+lines.size());
        return lines;
    }

    // passing line to check width in to box number of line to wrapping function

    private List<String> parseText2Lines(String text, float width) throws IOException {
        List<String> lines = new ArrayList<String>();
        int lastSpace = -1;
        System.out.println("######### FONT_SIZE : "+FONT_SIZE);
        while (text.length() > 0) {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length();
            String subString = text.substring(0, spaceIndex);

            float size = FONT_SIZE * FONT.getStringWidth(subString) / 1000;
            if (size > width) {
                if (lastSpace < 0){
                    lastSpace = spaceIndex;
                }
                subString = text.substring(0, lastSpace);
                lines.add(subString);
                text = text.substring(lastSpace).trim();
                lastSpace = -1;
            } else if (spaceIndex == text.length()) {
                lines.add(text);
                text = "";
            } else {
                lastSpace = spaceIndex;
            }
        }
        return lines;
    }

    // return string

    private String parseStrings(String text, float width) throws IOException {
        String lines = "";
        int lastSpace = -1;
        System.out.println("######### FONT_SIZE : "+FONT_SIZE);
        while (text.length() > 0) {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length();
            String subString = text.substring(0, spaceIndex);

            float size = FONT_SIZE * FONT.getStringWidth(subString) / 1000;
            if (size > width) {
                if (lastSpace < 0){
                    lastSpace = spaceIndex;
                }
                subString = text.substring(0, lastSpace);
                lines+=subString+"<br />";
                text = text.substring(lastSpace).trim();
                lastSpace = -1;
            } else if (spaceIndex == text.length()) {
                lines+=text+"<br />";
                text = "";
            } else {
                lastSpace = spaceIndex;
            }
        }
        return lines;
    }

    /**
     * Main function no parameters expected
     *
     */
    public static void main(String[] args) {
        PDFGen app = new PDFGen();
        try {
            app.createPDFFromImage("C:/Test/Kylo-Edge.pdf", "C:/Test/minions.png", "C:/Test/test2.pdf");
        }catch (IOException ex){
            System.err.println("Error");
            ex.printStackTrace();
        }

    }


}