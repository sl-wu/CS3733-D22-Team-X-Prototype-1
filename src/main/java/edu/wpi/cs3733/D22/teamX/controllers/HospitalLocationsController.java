package edu.wpi.cs3733.D22.teamX.controllers;

import edu.wpi.cs3733.D22.teamX.App;
import edu.wpi.cs3733.D22.teamX.Location;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HospitalLocationsController implements Initializable {
  @FXML private TableView<Location> table;
  @FXML private TableColumn<Location, String> nodeID;
  @FXML private TableColumn<Location, Integer> xCoord;
  @FXML private TableColumn<Location, Integer> yCoord;
  @FXML private TableColumn<Location, String> floor;
  @FXML private TableColumn<Location, String> building;
  @FXML private TableColumn<Location, String> nodeType; // Add more columns or remove if need be

  @FXML
  private TableColumn<Location, String> longName; // Put an entity in these columns that makes sense

  @FXML private TableColumn<Location, String> shortName;

  @FXML
  void ToMainMenu() throws IOException {
    App.switchScene(
        FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D22/teamX/views/app.fxml")));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    nodeID.setCellValueFactory(new PropertyValueFactory<Location, String>("nodeID"));
    xCoord.setCellValueFactory(new PropertyValueFactory<Location, Integer>("xCoord"));
    yCoord.setCellValueFactory(new PropertyValueFactory<Location, Integer>("yCoord"));
    floor.setCellValueFactory(new PropertyValueFactory<Location, String>("floor"));
    building.setCellValueFactory(new PropertyValueFactory<Location, String>("building"));
    nodeType.setCellValueFactory(new PropertyValueFactory<Location, String>("nodeType"));
    longName.setCellValueFactory(new PropertyValueFactory<Location, String>("longName"));
    shortName.setCellValueFactory(new PropertyValueFactory<Location, String>("shortName"));
    table.getItems().addAll(fillTable());
  }

  private ObservableList<Location> fillTable() { // Helper to fill list displayed in table
    ObservableList<Location> items = FXCollections.observableArrayList();
    items.add(new Location("ID", 1, 2, "L1", "Tower", "STOR", "OR Bed Park", "OR Bed Park"));
    return items;
  }
}
