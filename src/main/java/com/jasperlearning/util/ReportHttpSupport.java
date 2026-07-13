package com.jasperlearning.util;

import com.jasperlearning.report.GeneratedReport;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Turns a GeneratedReport into an HTTP response. Shared by every controller
 * so each one only has to say WHICH report and WHAT data - not repeat the
 * same header plumbing four different ways.
 */
public final class ReportHttpSupport {

    private ReportHttpSupport() {
    }

    public static ResponseEntity<byte[]> toResponse(GeneratedReport report, String baseFileName) {
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(report.suggestedFileName(baseFileName))
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(report.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(report.content());
    }
}
