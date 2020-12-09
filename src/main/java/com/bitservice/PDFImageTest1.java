package com.bitservice;

import com.bitservice.PdfKeywordFinder;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author ：mdx
 * @description: TODO
 * @date ：2020/11/25 22:10
 */
public class PDFImageTest1 {
    public static void PdfImage() throws IOException, DocumentException {
        String pdfname="D:\\demo.pdf";
        //查找签名位置
        float[] position= PdfKeywordFinder.getAddImagePositionXY(pdfname,"乙方(法定代表人):");
        //Read file using PdfReader
        PdfReader pdfReader = new PdfReader(pdfname);
        System.out.println("x:"+position[1]+" y:"+position[2]);
//        float pageNum=positions.get(0)[0];
//        float x=positions.get(0)[1];
//        float y=positions.get(0)[2];
//        float charWidth=positions.get(0)[3];

//        System.out.println(Arrays.toString(positions.get(0)));
//        float temp[]=PdfKeywordFinder.getKeywordPositionXY(pdfname,"甲方签字");
//        for(int i=0;i<temp.length;i++){
//            System.out.println(temp[i]);
//        }


        //Modify file using PdfReader
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream("D:\\demo1.pdf"));

        Image image = Image.getInstance("D:\\公章.png");
        //Fixed Positioning
        image.scaleAbsolute(140, 140);
        //Scale to new height and new width of image
        image.setAbsolutePosition(position[1], position[2]-400);

        System.out.println("pages:"+pdfReader.getNumberOfPages());


        PdfContentByte content = pdfStamper.getUnderContent((int) position[0]);
        content.addImage(image);


        pdfStamper.close();
    }
}
