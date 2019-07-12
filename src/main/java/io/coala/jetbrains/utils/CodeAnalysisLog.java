package io.coala.jetbrains.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CodeAnalysisLog {

  private final CodeInspectionSeverity severity;
  private final String message;
  private final Date date;

  public CodeAnalysisLog(String message, String timestamp, String level) {
    this.message = message;
    this.date = getDefaultDate(timestamp);
    this.severity = getSeverityLevelFromString(level);
  }

  public CodeInspectionSeverity getSeverity() {
    return severity;
  }

  public String getMessage() {
    return message;
  }

  public Date getDate() {
    return date;
  }

  /**
   * This methods returns the log time stamp in coala friendly format that can be directly
   * printed to the console.
   *
   * @return time stamp string in the required format
   */
  public String getPrintableTimeStamp() {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
    simpleDateFormat.setTimeZone(TimeZone.getDefault());
    return simpleDateFormat.format(date);
  }

  private Date getDefaultDate(String timestamp) {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    try {
      return simpleDateFormat.parse(timestamp);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return null;
  }

  private CodeInspectionSeverity getSeverityLevelFromString(String severity) {
    switch (severity) {
      case "ERROR":
        return CodeInspectionSeverity.ERROR;
      case "WARNING":
        return CodeInspectionSeverity.WARNING;
      case "DEBUG":
        return CodeInspectionSeverity.DEBUG;
      default:
        return null;
    }
  }
}
