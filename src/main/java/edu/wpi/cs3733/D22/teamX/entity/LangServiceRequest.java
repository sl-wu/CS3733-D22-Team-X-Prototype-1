package edu.wpi.cs3733.D22.teamX.entity;

import java.time.LocalDateTime;

/** Represents a Language Interpreter Service Request */
public class LangServiceRequest extends ServiceRequest {
  String language;

  public LangServiceRequest() {
    super();
    this.language = "";
  }

  public LangServiceRequest(
      String requestID, Location destination, String status, Employee assignee, String language) {
    super(requestID, destination, status, assignee);
    this.language = language;
  }

  public LangServiceRequest(
      String requestID,
      Location destination,
      String status,
      Employee assignee,
      LocalDateTime creationTime,
      LocalDateTime PROCTime,
      LocalDateTime DONETime,
      String language) {
    super(requestID, destination, status, assignee, creationTime, PROCTime, DONETime);
    this.language = language;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
