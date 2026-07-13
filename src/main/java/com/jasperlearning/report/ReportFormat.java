package com.jasperlearning.report;

/**
 * The export formats demonstrated by this project. Each maps to a distinct
 * JasperReports exporter (see ReportExportService) - the same compiled
 * .jasper + filled JasperPrint is reused for every one of them, which is the
 * whole point of JasperReports' report-once-export-anywhere design.
 */
public enum ReportFormat {
    PDF("application/pdf", "pdf"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    HTML("text/html", "html"),
    CSV("text/csv", "csv");

    private final String contentType;
    private final String fileExtension;

    ReportFormat(String contentType, String fileExtension) {
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

    public String contentType() {
        return contentType;
    }

    public String fileExtension() {
        return fileExtension;
    }

    /** Case-insensitive lookup used by controllers' `?format=` query parameter, defaulting to PDF. */
    public static ReportFormat from(String value) {
        if (value == null || value.isBlank()) {
            return PDF;
        }
        try {
            return ReportFormat.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ReportGenerationException(
                    "Unsupported format '" + value + "'. Supported: pdf, xlsx, docx, html, csv");
        }
    }
}
