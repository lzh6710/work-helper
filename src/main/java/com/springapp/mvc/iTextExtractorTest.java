package com.springapp.mvc;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class iTextExtractorTest {
	public static void main(String[] args) throws Exception {
		test2(args);
	}

	public static void test2(String[] args) throws Exception {
		String pdfPath = "C:\\Users\\Lzh\\git\\work-helper\\other\\0807.pdf";
		FileInputStream bufIn = new FileInputStream(pdfPath);
		PDDocument pdd = PDDocument.load(bufIn);
		@SuppressWarnings("unchecked")
		List<Object> allPages = pdd.getDocumentCatalog().getAllPages();
		pdd.close();
		FileInputStream fis = new FileInputStream(pdfPath);
		PDFParser p = new PDFParser(fis);
		String paragraphChars = "\t";
		p.parse();
		// response.setContentType("text/csv;charset=utf-8");
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				"download.csv");
		// response.setHeader(headerKey, headerValue);
		String csvPath = "C:\\Users\\Lzh\\git\\work-helper\\other\\0807.csv";
		FileWriterWithEncoding fw = new FileWriterWithEncoding(csvPath, "utf-8");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(fw,
				CsvPreference.STANDARD_PREFERENCE);
		String[] header = { "rowNum", "address", "code" };

		for (int i = 1; i < allPages.size(); i = i + 2) {
			PDFTextStripper ts = new PDFTextStripper();
			ts.setStartPage(i);
			ts.setEndPage(i);
			ts.setParagraphEnd(paragraphChars);
			String s = ts.getText(p.getPDDocument());
			String[] ps = s.split(paragraphChars);
			TraceInfo ti = new TraceInfo();
			ti.setRowNum((int) (i / 2));
			if (ps.length == 8) {
			} else {
				ti.setAddress(formatAddress(ps[6]));
				ti.setCode(ps[8]);
			}
			csvWriter.write(ti, header);
		}
		bufIn.close();
		csvWriter.close();
	}

	public static String formatAddress(String address) {
		String[] splitRes = address.replace(":", "").split("\r\n");
		List<String> splitRes1 = new ArrayList<String>();
		for (int i = 0; i < splitRes.length; i++) {
			String sp1 = splitRes[i];
			boolean isOverlap = false;
			for (int j = 0; j < splitRes1.size(); j++) {
				String sp2 = splitRes1.get(j);
				double similar = StringSimilarity.similarity(
						sp1.replace(",", ""), sp2.replace(",", ""));
				if (similar > 0.7) {
					isOverlap = true;
					if (sp1.length() > sp2.length()) {
						splitRes1.set(j, sp1.trim());
					}
				}
			}
			if (!isOverlap)
				splitRes1.add(sp1.trim());
		}
		String ret = "";
		for (int i = 0; i < splitRes1.size(); i++) {
			ret += splitRes1.get(i) + "\r\n";
		}
		ret = ret.trim();
		return ret;
	}

	public static void test1(String[] args) throws Exception {
		String pdfPath = "C:\\Users\\Lzh\\git\\work-helper\\other\\0807.pdf";
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
			if (ps.length == 8) {
				System.out.println(ps[5]);
				System.out.println(ps[6]);
			} else {
				System.out.println(ps[6]);
				System.out.println(ps[8]);
			}
		}
		fis.close();
	}
}
