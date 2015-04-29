package com.springapp.mvc;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

import org.apache.pdfbox.PDFReader;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.util.StringUtils;

public class iTextExtractorTest {
	public static void main(String[] args) throws Exception {
		String pdfPath = "C:\\Users\\Lzh\\git\\upload.spring\\other\\150428EUB.pdf";
		FileInputStream fis = new FileInputStream(pdfPath);
		// BufferedWriter writer = new BufferedWriter(new FileWriter(
		// "F:\\task\\pdf_change.txt"));
		PDFParser p = new PDFParser(fis);
		String paragraphChars = "\t";
		p.parse();
		PDDocument pdd = PDDocument.load(pdfPath);
		List allPages = pdd.getDocumentCatalog().getAllPages();
		pdd.close();
		for (int i = 1; i < allPages.size(); i = i + 2) {
			PDFTextStripper ts = new PDFTextStripper();
			ts.setStartPage(i);
			ts.setEndPage(i);
			ts.setParagraphEnd(paragraphChars);
			String s = ts.getText(p.getPDDocument());
			String[] ps = s.split(paragraphChars);
			if(ps.length==8){
				System.out.println(ps[5]);
				System.out.println(ps[6]);
			}else{
				System.out.println(ps[6]);
				System.out.println(ps[8]);
			}
		}
		fis.close();
	}
}
