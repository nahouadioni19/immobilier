/*
 * package com.app.service.common;
 * 
 * 
 * import java.io.File; import java.io.FileInputStream; import
 * java.io.FileNotFoundException; import java.io.FileOutputStream; import
 * java.io.IOException; import java.io.InputStream; import java.io.OutputStream;
 * import java.sql.Connection; import java.sql.SQLException; import
 * java.util.HashMap; import java.util.Map; import java.util.Properties; import
 * javax.naming.InitialContext; import jakarta.servlet.ServletOutputStream;
 * import jakarta.servlet.http.HttpServletResponse; import javax.sql.DataSource;
 * import org.apache.commons.io.FileUtils; import
 * org.apache.commons.lang3.StringUtils; import
 * org.springframework.stereotype.Service; import
 * com.lowagie.text.pdf.PdfWriter; import com.app.exceptions.CustomException;
 * import com.app.entities.rapport.POJOEtat; import com.app.utils.Constants;
 * import com.app.utils.JUtils; import lombok.RequiredArgsConstructor; import
 * lombok.extern.slf4j.Slf4j;
 * 
 * import net.sf.jasperreports.engine.JRAbstractExporter; import
 * net.sf.jasperreports.engine.JRDataSource; import
 * net.sf.jasperreports.engine.JRException; import
 * net.sf.jasperreports.engine.JasperCompileManager; import
 * net.sf.jasperreports.engine.JasperExportManager; import
 * net.sf.jasperreports.engine.JasperFillManager; import
 * net.sf.jasperreports.engine.JasperPrint; import
 * net.sf.jasperreports.engine.JasperReport; import
 * net.sf.jasperreports.engine.export.JRCsvExporter; import
 * net.sf.jasperreports.engine.export.JRPdfExporter; import
 * net.sf.jasperreports.engine.export.JRTextExporter; import
 * net.sf.jasperreports.engine.export.ooxml.JRDocxExporter; import
 * net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter; import
 * net.sf.jasperreports.export.SimpleCsvExporterConfiguration; import
 * net.sf.jasperreports.export.SimpleDocxExporterConfiguration; import
 * net.sf.jasperreports.export.SimpleExporterInput; import
 * net.sf.jasperreports.export.SimpleOutputStreamExporterOutput; import
 * net.sf.jasperreports.export.SimplePdfExporterConfiguration; import
 * net.sf.jasperreports.export.SimpleTextExporterConfiguration; import
 * net.sf.jasperreports.export.SimpleXlsxExporterConfiguration;
 * 
 * @Slf4j
 * 
 * @Service
 * 
 * @RequiredArgsConstructor public class JasperreportService {
 * 
 * public static final String PDF = "pdf"; public static final String EXCEL =
 * "excel"; public static final String CSV = "csv"; public static final String
 * TEXT = "text"; public static final String WORD = "docx";
 * 
 * public static final String RTF = "rtf"; private static final String
 * COMMON_PATH = "reports/";
 * 
 * private final DataSource dataSource;
 * 
 * private String format = PDF;
 * 
 * private JRDataSource jrDs;
 * 
 * private String jrxml;
 * 
 * private Map<String, Object> params;
 * 
 *//**
	 * Data to print.
	 * <p>
	 * If return null then a JDBC connection is sent to JasperReport, this is for
	 * the case of a SQL inside JasperReport design.
	 */
/*
 * private JRDataSource getJrDs() { return jrDs; }
 * 
 * public void setJrDs(JRDataSource jrDs) { this.jrDs = jrDs; }
 * 
 *//**
	 * The name of the XML with the JasperReports design.
	 * <p>
	 * If it is a relative path (as <code>reports/myreport.jrxml</code> has to be in
	 * classpath. If it is a absolute path (as
	 * <code>/home/java/reports/myreport.xml</code> or
	 * <code>C:\\JAVA\\REPORTS\MYREPORT.JRXML</code> then it look at the file
	 * system.
	 */
/*
 * private String getJrxml() { return jrxml; }
 * 
 * public void setJrxml(String jrxml) { this.jrxml = jrxml; }
 * 
 *//**
	 * Parameters to send to report.
	 */
/*
 * private Map<String, Object> getParams() { return params; }
 * 
 * public void setParams(Map<String, Object> params) { this.params = params; }
 * 
 *//**
	 * Output report format, it can be 'pdf' or 'excel'.
	 * <p>
	 */
/*
 * public String getFormat() { return format; }
 * 
 *//**
	 * Output report format, it can be 'pdf', 'excel' or 'rtf'.
	 * <p>
	 */
/*
 * public void setFormat(String format) { if (format == null) format = PDF; if
 * (!EXCEL.equalsIgnoreCase(format) && !PDF.equalsIgnoreCase(format) &&
 * !WORD.equalsIgnoreCase(format) && !RTF.equalsIgnoreCase(format)) throw new
 * CustomException("invalid_report_format"); this.format = format; }
 * 
 *//**
	 * Method execute.
	 *
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
/*
 * public JasperPrint execute() throws SQLException { try (InputStream xmlDesign
 * = getXmlDesignStream()) { JasperReport report =
 * JasperCompileManager.compileReport(xmlDesign); return getPrintReport(report);
 * } catch (Exception e) { log.error(e.getMessage(), e); } return null; }
 * 
 * private JasperPrint getPrintReport(JasperReport report) throws JRException,
 * SQLException { JasperPrint jprint = null; Map<String, Object> parameters =
 * getParams(); JRDataSource ds = getJrDs(); try (Connection conn =
 * dataSource.getConnection()) { if (ds == null) { jprint =
 * JasperFillManager.fillReport(report, parameters, conn); } else jprint =
 * JasperFillManager.fillReport(report, parameters, ds);
 * log.info("Etat selected: " + getJrxml() + "  with parameters: " +
 * parameters); }
 * 
 * return jprint; }
 * 
 * private InputStream getXmlDesignStream() throws FileNotFoundException,
 * CustomException { InputStream xmlDesign = null; if
 * (isAbsolutePath(getJrxml())) xmlDesign = new FileInputStream(getJrxml());
 * else xmlDesign = JasperreportService.class.getResourceAsStream("/" +
 * getJrxml()); if (xmlDesign == null) throw new
 * CustomException("design_not_found"); return xmlDesign; }
 * 
 * public static Properties getDefaultProperties() { Properties
 * defaultProperties = new Properties(); try { InitialContext context = new
 * InitialContext(); defaultProperties.putAll(context.getEnvironment());
 * log.info("" + defaultProperties); } catch (Exception e) {
 * log.error(e.getMessage(), e); } return defaultProperties; }
 * 
 * private boolean isAbsolutePath(String design) { return design.startsWith("/")
 * || design.length() > 2 && design.charAt(1) == ':' &&
 * Character.isLetter(design.charAt(0)); }
 * 
 *//**
	 * @param response
	 * @param jprint
	 * @param filename
	 * @param option
	 * @throws JRException
	 * @throws IOException
	 *//*
		 * @SuppressWarnings("all") public void doExport(HttpServletResponse response,
		 * JasperPrint jprint, Map<String, String> options) throws JRException,
		 * IOException { Map<String, String> fileOptions =
		 * this.getFileOptions(options.get(Constants.ETAT_FILE_TYPE)); String disHeader
		 * = String.format(" filename=%s.%s", options.get(Constants.ETAT_FILE_NAME),
		 * fileOptions.get("extension")); switch
		 * (options.get(Constants.ETAT_RENDER_TYPE)) { case Constants.SHOW_IN_BROWSER:
		 * response.setContentType(fileOptions.get("contentType")); disHeader =
		 * "inline;" + disHeader; response.setHeader("Content-Disposition", disHeader);
		 * break; case Constants.DIRECT_DOWNLOAD:
		 * response.setContentType("APPLICATION/OCTET-STREAM"); disHeader =
		 * "Attachment;" + disHeader; response.setHeader("Content-Disposition",
		 * disHeader); break; default: break; }
		 * 
		 * JRAbstractExporter exporter = null;
		 * 
		 * switch (this.getFormat()) { case EXCEL: exporter = this.getXlsExporter();
		 * break; case CSV: exporter = this.getCsvExporter(); break; case WORD: exporter
		 * = this.getWordExporter(); break; case TEXT: exporter =
		 * this.getTextExporter(); break; case PDF: default: exporter =
		 * this.getPDFExporter(); break; }
		 * 
		 * exporter.setExporterInput(new SimpleExporterInput(jprint));
		 * ServletOutputStream servletOutputStream = response.getOutputStream();
		 * exporter.setExporterOutput(new
		 * SimpleOutputStreamExporterOutput(servletOutputStream));
		 * exporter.exportReport(); servletOutputStream.flush();
		 * servletOutputStream.close(); }
		 * 
		 * private Map<String, String> getFileOptions(String fileType) { var map = new
		 * HashMap<String, String>(); String extension = ""; String contentType = "";
		 * switch (fileType) { case EXCEL: extension = "xlsx"; contentType =
		 * "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"; break;
		 * case CSV: extension = "csv"; contentType = "text/csv"; break; case TEXT:
		 * extension = "txt"; contentType = "text/plain"; break; case WORD: extension =
		 * "docx"; contentType =
		 * "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		 * break; case PDF: default: extension = "pdf"; contentType = "application/pdf";
		 * break; } map.put("extension", extension); map.put("contentType",
		 * contentType); return map; }
		 * 
		 * private JRPdfExporter getPDFExporter() { JRPdfExporter exporter = new
		 * JRPdfExporter(); SimplePdfExporterConfiguration config = new
		 * SimplePdfExporterConfiguration(); config.setPermissions(PdfWriter.ALLOW_COPY
		 * | PdfWriter.ALLOW_PRINTING); config.setMetadataAuthor(Constants.APP_NAME);
		 * exporter.setConfiguration(config); return exporter; }
		 * 
		 * private JRXlsxExporter getXlsExporter() { JRXlsxExporter exporter = new
		 * JRXlsxExporter(); SimpleXlsxExporterConfiguration config = new
		 * SimpleXlsxExporterConfiguration();
		 * config.setMetadataAuthor(Constants.APP_NAME);
		 * exporter.setConfiguration(config); return exporter; }
		 * 
		 * private JRDocxExporter getWordExporter() { JRDocxExporter exporter = new
		 * JRDocxExporter(); SimpleDocxExporterConfiguration config = new
		 * SimpleDocxExporterConfiguration();
		 * config.setMetadataAuthor(Constants.APP_NAME);
		 * exporter.setConfiguration(config); return exporter; }
		 * 
		 * private JRCsvExporter getCsvExporter() { JRCsvExporter exporter = new
		 * JRCsvExporter(); SimpleCsvExporterConfiguration config = new
		 * SimpleCsvExporterConfiguration(); config.setFieldDelimiter(",");
		 * exporter.setConfiguration(config); return exporter; }
		 * 
		 * private JRTextExporter getTextExporter() { JRTextExporter exporter = new
		 * JRTextExporter(); SimpleTextExporterConfiguration config = new
		 * SimpleTextExporterConfiguration(); exporter.setConfiguration(config); return
		 * exporter; }
		 * 
		 * public String printToTempFile(JasperPrint jprint, String pathFilePdf) throws
		 * JRException, IOException { OutputStream output = new FileOutputStream(new
		 * File(pathFilePdf)); JasperExportManager.exportReportToPdfStream(jprint,
		 * output); output.flush(); output.close(); return pathFilePdf; }
		 * 
		 * private Map<String, Object> getReportParams(POJOEtat form) { Map<String,
		 * Object> rptParams = new HashMap<>(); if
		 * (StringUtils.isNotBlank(form.getParams())) rptParams =
		 * JUtils.jsonToMap(form.getParams()); return rptParams; }
		 * 
		 * public JasperPrint generateReport(POJOEtat form) throws SQLException {
		 * JasperPrint jprint = null; if (form.getInfoJrxml() != null) {
		 * setFormat(form.getFormat()); setJrxml(COMMON_PATH + form.getInfoJrxml() +
		 * ".jrxml"); // set datasource à null setJrDs(null);
		 * setParams(getReportParams(form)); jprint = execute(); } else throw new
		 * CustomException("design_not_found"); return jprint; }
		 * 
		 * public File generateReportToTempFile(POJOEtat form, String fileName) throws
		 * FileNotFoundException, JRException, SQLException { JasperPrint jprint =
		 * generateReport(form); File tempFile = new File(FileUtils.getTempDirectory(),
		 * fileName); OutputStream output = new FileOutputStream(tempFile);
		 * JasperExportManager.exportReportToPdfStream(jprint, output);
		 * 
		 * return tempFile; } }
		 * 
		 */