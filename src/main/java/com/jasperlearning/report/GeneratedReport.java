package com.jasperlearning.report;

/** Bytes ready to stream back over HTTP, plus enough metadata to set response headers. */
public record GeneratedReport(byte[] content, ReportFormat format) {
    public String contentType() {
        return format.contentType();
    }

    public String suggestedFileName(String baseName) {
        return baseName + "." + format.fileExtension();
    }
}
