package edu.wpi.cs3733.D22.teamX.entity;

/** Represents a janitorial service request */
public class JanitorServiceRequest extends ServiceRequest {
  private String serviceType;

  public JanitorServiceRequest() {
    super();
    this.serviceType = "";
  }

  public JanitorServiceRequest(
      String requestID, Location destination, String status, String assignee, String serviceType) {
    super(requestID, destination, status, assignee);
    this.serviceType = serviceType;
  }

  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  @Override
  public String makeRequestID() {
    return "sample";
  }
}