package com.springapp.mvc;

import java.io.FileInputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

@Controller
@RequestMapping("/")
public class UploadController {

	@Autowired
	private UploadService uploadService;

	private static Logger logger = LoggerFactory
			.getLogger(UploadController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String index(@ModelAttribute Upload upload) {
		return "index";
	}

	@RequestMapping(value = "http", method = RequestMethod.POST)
	public String uploadHttp(@ModelAttribute Upload upload,
			BindingResult result, HttpServletResponse response) {
		new Validator() {
			@Override
			public boolean supports(Class<?> clazz) {
				return Upload.class.isAssignableFrom(clazz);
			}

			@Override
			public void validate(Object target, Errors errors) {
				Upload upload = (Upload) target;

				if (upload.getFile().isEmpty()) {
					errors.rejectValue("file", "upload.file.empty");
				}
			}
		}.validate(upload, result);

		if (result.hasErrors()) {
			return "index";
		}
		try {

			CommonsMultipartFile file = upload.getFile();
			String filePath = uploadService.transferFile(file,
					file.getOriginalFilename());
			FileInputStream bufIn = new FileInputStream(filePath);
			PDDocument pdd = PDDocument.load(bufIn);
			@SuppressWarnings("unchecked")
			List<Object> allPages = pdd.getDocumentCatalog().getAllPages();
			pdd.close();
			bufIn = new FileInputStream(filePath);
			PDFParser p = new PDFParser(bufIn);
			String paragraphChars = "\t";
			p.parse();
			response.setContentType("text/csv;charset=utf-8");
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					"download.csv");
			response.setHeader(headerKey, headerValue);
			ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
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
					ti.setAddress(ps[6]);
					ti.setCode(ps[8]);
				}
				csvWriter.write(ti, header);
			}
			bufIn.close();
			csvWriter.close();
		} catch (Exception e) {
			logger.error("error", e);
		}
		logger.debug("Upload success : " + upload);

		return null;
	}

	/*
	 * @RequestMapping(value = "http", method = RequestMethod.POST) public
	 * String uploadHttp_old(@ModelAttribute Upload upload, BindingResult
	 * result){ new Validator(){
	 * 
	 * @Override public boolean supports(Class<?> clazz) { return
	 * Upload.class.isAssignableFrom(clazz); }
	 * 
	 * @Override public void validate(Object target, Errors errors) { Upload
	 * upload = (Upload) target;
	 * 
	 * if(upload.getFile().isEmpty()){ errors.rejectValue("file",
	 * "upload.file.empty"); } } }.validate(upload, result);
	 * 
	 * if(result.hasErrors()){ return "index"; }
	 * 
	 * CommonsMultipartFile file = upload.getFile();
	 * uploadService.transferFile(file, file.getOriginalFilename());
	 * logger.debug("Upload success : " + upload);
	 * 
	 * return "redirect:/"; }
	 */

	@Deprecated
	@RequestMapping(value = "json", method = RequestMethod.POST)
	@ResponseBody
	public Boolean uploadJson(@RequestBody Upload upload) {

		CommonsMultipartFile file = upload.getFile();
		uploadService.transferFile(file, file.getOriginalFilename());
		logger.debug("Upload success : " + upload);

		return true;
	}

}