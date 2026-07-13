package com.jasperlearning.report;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ONE reporting service for the whole project.
 *
 * The simple concept: read the .jrxml from the classpath, compile it, wrap the
 * data, fill, export - generalised so every example (beans / SQL / static /
 * custom data source, and any of the five output formats) goes through this
 * single class instead of a separate Compiler + Fill + Export layer.
 *
 * ----------------------------------------------------------------------------
 * THE REPORT LIFECYCLE, IN ONE PLACE
 *   1. COMPILE  jrxml (XML design) -> JasperReport (compiled, expressions ready)
 *   2. FILL     JasperReport + parameters + data -> JasperPrint (laid-out doc)
 *   3. EXPORT   JasperPrint -> bytes in the requested format (PDF/XLSX/DOCX/...)
 * The same compiled report and the same JasperPrint can be reused across
 * formats - "design once, export anywhere".
 * ----------------------------------------------------------------------------
 */
@Service
@Slf4j
public class JasperReportService {

    /** H2 DataSource - only used by the "SQL data source" examples (renderFromSql). */
    private final DataSource dataSource;

    /**
     * Compiled-report cache. Compiling is the expensive step, so we do it once
     * per template and reuse the result. Restarting picks up jrxml edits.
     */
    private final Map<String, JasperReport> compiledCache = new ConcurrentHashMap<>();

    public JasperReportService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ========================================================================
    // STAGE 1 - COMPILE
    // ========================================================================

    /**
     * Compiles a classpath .jrxml into a JasperReport (cached).
     * Public because subreport/master examples need to pre-compile the child
     * template and pass the JasperReport in as a parameter.
     *
     * @param classpathJrxml e.g. "reports/beginner/01_hello_world.jrxml"
     */
    public JasperReport compile(String classpathJrxml) {
        return compiledCache.computeIfAbsent(classpathJrxml, path -> {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                throw new ReportGenerationException("Report template not found on classpath: " + path);
            }
            log.info("Compiling JasperReports template: {}", path);
            try (InputStream in = resource.getInputStream()) {
                // JRXmlLoader.load -> JasperDesign (object model), then compile.
                // Equivalent to JasperCompileManager.compileReport(inputStream);
                // done in two steps so the intent of each stage is visible.
                return JasperCompileManager.compileReport(JRXmlLoader.load(in));
            } catch (JRException e) {
                throw new ReportGenerationException("Failed to compile report: " + path, e);
            } catch (IOException e) {
                throw new ReportGenerationException("Failed to read report template: " + path, e);
            }
        });
    }

    // ========================================================================
    // STAGE 2 - FILL (four convenience entry points) + STAGE 3 - EXPORT
    // Each returns a GeneratedReport (bytes + format) ready to stream back.
    // ========================================================================

    /** No data at all - static reports like "Hello World" or a fixed certificate. */
    public GeneratedReport renderStatic(String template, @Nullable Map<String, Object> params, ReportFormat format) {
        return export(fill(template, params, new JREmptyDataSource()), format);
    }

    /** The common case: a List of JavaBeans/DTOs as the row source. */
    public GeneratedReport renderWithBeans(String template, @Nullable Map<String, Object> params,
                                           Collection<?> beans, ReportFormat format) {
        return export(fill(template, params, new JRBeanCollectionDataSource(beans)), format);
    }

    /** Any custom JRDataSource (nested data sources, hand-rolled sources, etc.). */
    public GeneratedReport renderWithDataSource(String template, @Nullable Map<String, Object> params,
                                                JRDataSource jrDataSource, ReportFormat format) {
        return export(fill(template, params, jrDataSource), format);
    }

    /** Lets JasperReports run the &lt;queryString&gt; in the .jrxml against the H2 DataSource. */
    public GeneratedReport renderFromSql(String template, @Nullable Map<String, Object> params, ReportFormat format) {
        JasperReport report = compile(template);
        try (Connection connection = dataSource.getConnection()) {
            JasperPrint print = JasperFillManager.fillReport(report, withDefaults(params), connection);
            return export(print, format);
        } catch (JRException e) {
            throw new ReportGenerationException("Failed to fill report from SQL: " + template, e);
        } catch (SQLException e) {
            throw new ReportGenerationException("Failed to obtain a DB connection for: " + template, e);
        }
    }

    // ---- internal fill (compile + fillReport with an in-memory data source) ----
    private JasperPrint fill(String template, @Nullable Map<String, Object> params, JRDataSource ds) {
        JasperReport report = compile(template);
        try {
            return JasperFillManager.fillReport(report, withDefaults(params), ds);
        } catch (JRException e) {
            throw new ReportGenerationException("Failed to fill report: " + template, e);
        }
    }

    // ========================================================================
    // STAGE 3 - EXPORT (one JasperPrint -> any of the five formats)
    // ========================================================================
    private GeneratedReport export(JasperPrint print, ReportFormat format) {
        try {
            byte[] bytes = switch (format) {
                case PDF -> toPdf(print);
                case XLSX -> toXlsx(print);
                case DOCX -> toDocx(print);
                case HTML -> toHtml(print);
                case CSV -> toCsv(print);
            };
            return new GeneratedReport(bytes, format);
        } catch (JRException e) {
            throw new ReportGenerationException("Failed to export report to " + format, e);
        }
    }

    private byte[] toPdf(JasperPrint print) throws JRException {
        // The simplest exporter of all - one static helper does everything.
        return JasperExportManager.exportReportToPdf(print);
    }

    private byte[] toXlsx(JasperPrint print) throws JRException {
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

        // Make a PDF-oriented layout behave like a real spreadsheet:
        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        config.setOnePagePerSheet(false);                    // one continuous sheet
        config.setRemoveEmptySpaceBetweenRows(true);
        config.setRemoveEmptySpaceBetweenColumns(true);
        config.setWhitePageBackground(false);
        config.setDetectCellType(true);                      // numbers stay numeric in Excel
        exporter.setConfiguration(config);
        exporter.exportReport();
        return out.toByteArray();
    }

    private byte[] toDocx(JasperPrint print) throws JRException {
        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
        exporter.exportReport();
        return out.toByteArray();
    }

    private byte[] toHtml(JasperPrint print) throws JRException {
        HtmlExporter exporter = new HtmlExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // No image handler -> images are embedded as base64 data URIs, so the
        // HTML is a single self-contained file (no separate image directory).
        exporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
        exporter.exportReport();
        return out.toByteArray();
    }

    private byte[] toCsv(JasperPrint print) throws JRException {
        JRCsvExporter exporter = new JRCsvExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleWriterExporterOutput(out));
        exporter.exportReport();
        return out.toByteArray();
    }

    // ========================================================================
    // Shared parameter defaults
    // ========================================================================

    /**
     * Adds report parameters every template can rely on:
     *  - REPORT_LOCALE : fixed to US so date/number formatting is deterministic
     *  - IMAGES_BASE_PATH : classpath-absolute path used by the dynamic-image example
     * Undeclared parameters are simply ignored by JasperReports, so it's safe
     * to pass these to every report.
     */
    private Map<String, Object> withDefaults(@Nullable Map<String, Object> params) {
        Map<String, Object> merged = new HashMap<>();
        merged.put(JRParameter.REPORT_LOCALE, Locale.US);
        merged.put("IMAGES_BASE_PATH", resolveImagesBasePath());
        if (params != null) {
            merged.putAll(params);
        }
        return merged;
    }

    private String resolveImagesBasePath() {
        try {
            return new ClassPathResource("images/").getFile().getAbsolutePath() + "/";
        } catch (IOException e) {
            // Packaged jar: images resolve via the classpath instead of a file path.
            return "";
        }
    }
}
