package edu.wpi.cs3733.D22.teamX.entity;

import java.util.List;

public interface EquipmentServiceRequestDAO {
  public List<EquipmentServiceRequest> getAllEquipmentServiceRequests();

  public EquipmentServiceRequest getEquipmentServiceRequest(String requestID);

  public void deleteEquipmentServiceRequest(EquipmentServiceRequest equipmentServiceRequest);

  public void updateEquipmentServiceRequest(EquipmentServiceRequest equipmentServiceRequest);

  public void addEquipmentServiceRequest(EquipmentServiceRequest equipmentServiceRequest);
}