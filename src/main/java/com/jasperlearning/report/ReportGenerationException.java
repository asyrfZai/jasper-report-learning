package com.jasperlearning.report;

/** Wraps the checked JRException/IOException the JasperReports API throws everywhere. */
public class ReportGenerationException extends RuntimeException {
    public ReportGenerationException(String message) {
        super(message);
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
