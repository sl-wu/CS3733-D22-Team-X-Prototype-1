package edu.wpi.cs3733.D22.teamX.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.D22.teamX.entity.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class SharpsDisposalRequestController implements Initializable {
  // get all the DAOs
  private LocationDAO locationDAO = LocationDAO.getDAO(); // location DAO
  private EmployeeDAO employeeDAO = EmployeeDAO.getDAO(); // employee DAO
  private ServiceRequestDAO sharpDAO = ServiceRequestDAO.getDAO(); // sharpDAO
  // put Sharps Disposal Request DAO here

  // lists for drop downs
  private List<Location> locations;
  private List<Employee> employees;

  // FXML things
  @FXML private JFXComboBox<String> roomDropDown, statusChoiceBox, assigneeDropDown, typeDropDown;
  @FXML private JFXButton submitButton, resetButton;

  @FXML private Label errorText;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // populate lists from DAOs
    locations = locationDAO.getAllRecords();
    employees = employeeDAO.getAllRecords();
    // reset fields
    resetFields();
    // disable submit button
    submitButton.setDisable(true);
    // populate ChoiceBoxes
    roomDropDown.setItems(getLocationNames());
    assigneeDropDown.setItems(getEmployeeNames());
    statusChoiceBox.getItems().addAll("", "PROC", "DONE");
    typeDropDown.getItems().addAll("thin", "normal", "thick");
    // add on action functionality to check to enable submit button
    roomDropDown.setOnAction((ActionEvent event) -> enableSubmitButton());
    statusChoiceBox.setOnAction((ActionEvent event) -> enableSubmitButton());
    assigneeDropDown.setOnAction((ActionEvent event) -> enableSubmitButton());
    typeDropDown.setOnAction((ActionEvent event) -> enableSubmitButton());
  }
  // shouldn't this be private?
  public ObservableList<String> getLocationNames() {
    ObservableList<String> locationNames = FXCollections.observableArrayList();
    for (Location location : locations) {
      locationNames.add(location.getNodeID()); // this should probably be something that is unique.
    }
    return locationNames;
  }

  public ObservableList<String> getEmployeeNames() {
    ObservableList<String> employeeNames = FXCollections.observableArrayList();
    for (Employee employee : employees) {
      employeeNames.add(
          employee.getEmployeeID()); // should this include the first and last name of the employee
      // concatinated on.
    }
    return employeeNames;
  }

  @FXML
  private void resetFields() {
    roomDropDown.setValue("");
    statusChoiceBox.setValue("");
    assigneeDropDown.setValue("");
    typeDropDown.setValue("");
    errorText.setText("");
  }

  @FXML
  public void enableSubmitButton() {
    // make sure everything is populated
    submitButton.setDisable(
        roomDropDown.getValue().equals("")
            || assigneeDropDown.getValue().equals("")
            // || statusChoiceBox.getValue().equals("")
            || typeDropDown.getValue().equals(""));
  }

  public void submitButton() {
    boolean Success = true;
    String error = "";
    try {
      // make service Request
      SharpsDisposalRequest SDSR = new SharpsDisposalRequest();
      // populate fields
      SDSR.setAssignee(employeeDAO.getRecord(assigneeDropDown.getValue()));
      SDSR.setDestination(locationDAO.getRecord(roomDropDown.getValue()));
      SDSR.setStatus(statusChoiceBox.getValue());
      SDSR.setType(typeDropDown.getValue());
      // TODO use acctual makeID system in SharpsDisposal
      SDSR.setRequestID(sharpDAO.makeSharpsDisposalServiceRequestID());
      // submit the thing
      sharpDAO.addRecord(SDSR);
      // notify user of submission
    } catch (Exception e) {
      Success = false;
      error = e.toString();
    }

    if (Success) {
      errorText.setText("Request Submitted!");
      resetFields();
    } else {
      errorText.setText("an error has occured: " + error + ". request Not Submitted");
    }
    // reset Fields
  }
}
