package edu.wpi.cs3733.D22.teamX.entity;

import edu.wpi.cs3733.D22.teamX.DatabaseCreator;
import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class GiftDeliveryRequestDAO implements DAO<GiftDeliveryRequest> {
  private static List<GiftDeliveryRequest> giftDeliveryRequests =
      new ArrayList<GiftDeliveryRequest>();
  private static String csv = "GiftDeliveryRequests.csv";

  /** Creates a new GiftDeliveryRequestDAO object. */
  private GiftDeliveryRequestDAO() {}

  /** Singleton helper class. */
  private static class SingletonHelper {
    private static final GiftDeliveryRequestDAO giftDeliveryRequestDAO =
        new GiftDeliveryRequestDAO();
  }

  /**
   * Returns the DAO Singleton.
   *
   * @return the DAO Singleton object.
   */
  public static GiftDeliveryRequestDAO getDAO() {
    return SingletonHelper.giftDeliveryRequestDAO;
  }

  @Override
  public List<GiftDeliveryRequest> getAllRecords() {
    return giftDeliveryRequests;
  }

  @Override
  public GiftDeliveryRequest getRecord(String recordID) {
    for (GiftDeliveryRequest request : giftDeliveryRequests) {
      if (request.getRequestID().equals(recordID)) return request;
    }
    throw new NoSuchElementException("GiftDeliveryRequest does not exist.");
  }

  @Override
  public void deleteRecord(GiftDeliveryRequest recordObject) {
    int index = 0;
    // find index
    while (index < giftDeliveryRequests.size()) {
      if (giftDeliveryRequests.get(index).equals(recordObject)) {
        giftDeliveryRequests.remove(index);
        index--;
        break;
      }
      index++;
    }

    // if object exists, delete it from the table
    if (index < giftDeliveryRequests.size()) {
      try {
        Statement statement = connection.createStatement();
        statement.executeUpdate(
            "DELETE FROM GiftDeliveryRequest WHERE requestID = '"
                + recordObject.getRequestID()
                + "'");
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } else {
      throw new NoSuchElementException("Request does not exist. Deletion cannot be performed.");
    }
  }

  @Override
  public void updateRecord(GiftDeliveryRequest recordObject) {
    // iterate through the list of locations to find the location passed in and update it in
    // locations
    int index = 0;
    while (index < giftDeliveryRequests.size()) {
      if (giftDeliveryRequests.get(index).equals(recordObject)) {
        giftDeliveryRequests.set(index, recordObject);
        break;
      }
      index++;
    }

    // if the location is found, update the Location table
    if (index < giftDeliveryRequests.size()) {
      try {
        // create the statement
        Statement statement = connection.createStatement();
        // update sql object
        statement.executeUpdate(
            "UPDATE GiftDeliveryRequest SET requestID = "
                + recordObject.getRequestID()
                + ", destination = "
                + recordObject.getDestination().getNodeID()
                + ", status = '"
                + recordObject.getStatus()
                + "', assignee = '"
                + recordObject.getAssignee()
                + "', notes = '"
                + recordObject.getNotes()
                + "', giftType = '"
                + recordObject.getGiftType()
                + "' "
                + "WHERE requestID = '"
                + recordObject.getRequestID()
                + "'");

      } catch (SQLException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("GiftDeliveryRequest does not exist");
      throw new NoSuchElementException("request does not exist (in update)");
    }
  }

  @Override
  public void addRecord(GiftDeliveryRequest recordObject) {
    giftDeliveryRequests.add(recordObject);
    try {
      Statement initialization = connection.createStatement();
      StringBuilder sql = new StringBuilder();
      sql.append("INSERT INTO GiftDeliveryRequest VALUES(");
      sql.append("'" + recordObject.getRequestID() + "'" + ", ");
      sql.append(recordObject.getDestination().getNodeID() + ", ");
      sql.append(recordObject.getStatus() + ", ");
      sql.append("'" + recordObject.getAssignee() + "'" + ", ");
      sql.append("'" + recordObject.getNotes() + "'" + ", ");
      sql.append("'" + recordObject.getGiftType() + "'");
      sql.append(")");
      initialization.execute(sql.toString());
    } catch (SQLException e) {
      System.out.println("Database could not be updated");
      return;
    }
    recordObject.getDestination().addRequest(recordObject);
  }

  @Override
  public void createTable() {
    try {
      Statement statement = connection.createStatement();
      statement.execute(
          "CREATE TABLE GiftDeliveryRequest(requestID CHAR(10) PRIMARY KEY NOT NULL, "
              + "destination CHAR(10),"
              + "status CHAR(4),"
              + "assignee CHAR(8),"
              + "notes VARCHAR(140),"
              + "giftType CHAR(10))");
    } catch (SQLException e) {
      System.out.println("GiftDeliveryRequest table creation failed. Check output console.");
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public void dropTable() {
    try {
      Statement statement = connection.createStatement();
      statement.execute("DROP TABLE GiftDeliveryRequest");
    } catch (SQLException e) {
      System.out.println("GiftDeliveryRequest not dropped");
      // e.printStackTrace();
    }
  }

  @Override
  public boolean loadCSV() {
    try {
      InputStream tlCSV = DatabaseCreator.class.getResourceAsStream(csv);
      BufferedReader tlCSVReader = new BufferedReader(new InputStreamReader(tlCSV));
      tlCSVReader.readLine();
      String nextFileLine;
      LocationDAO locationDAO = LocationDAO.getDAO();
      while ((nextFileLine = tlCSVReader.readLine()) != null) {
        String[] currLine = nextFileLine.replaceAll("\r\n", "").split(",");
        if (currLine.length == 6) {
          GiftDeliveryRequest node =
              new GiftDeliveryRequest(
                  currLine[0],
                  locationDAO.getRecord(currLine[1]),
                  currLine[2],
                  currLine[3],
                  currLine[4],
                  currLine[5]);
          giftDeliveryRequests.add(node);
        } else {
          System.out.println("GiftDeliveryRequest CSV file formatted improperly");
          System.exit(1);
        }
      }
      tlCSV.close();
      tlCSVReader.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.out.println("File not found!");
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    // Insert locations from locationsFromCSV into db table
    for (int i = 0; i < giftDeliveryRequests.size(); i++) {
      try {
        Statement initialization = connection.createStatement();
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO GiftDeliveryRequest VALUES(");
        sql.append("'" + giftDeliveryRequests.get(i).getRequestID() + "'" + ", ");
        sql.append("'" + giftDeliveryRequests.get(i).getDestination().getNodeID() + "'" + ", ");
        sql.append("'" + giftDeliveryRequests.get(i).getStatus() + "'" + ", ");
        sql.append("'" + giftDeliveryRequests.get(i).getAssignee() + "'" + ", ");
        sql.append("'" + giftDeliveryRequests.get(i).getNotes() + "'" + ", ");
        sql.append("'" + giftDeliveryRequests.get(i).getGiftType() + "'");
        sql.append(")");
        initialization.execute(sql.toString());
      } catch (SQLException e) {
        System.out.println("Input for GiftDeliveryRequest " + i + " failed");
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean saveCSV(String dirPath) {
    try {
      FileWriter csvFile = new FileWriter(dirPath + csv, false);
      csvFile.write("requestID, destination, status, assignee, notes, giftType");
      for (int i = 0; i < giftDeliveryRequests.size(); i++) {
        csvFile.write("\n" + giftDeliveryRequests.get(i).getRequestID() + ",");
        if (giftDeliveryRequests.get(i).getDestination() == null) {
          csvFile.write(',');
        } else {
          csvFile.write(giftDeliveryRequests.get(i).getDestination().getNodeID() + ",");
        }
        if (giftDeliveryRequests.get(i).getStatus() == null) {
          csvFile.write(',');
        } else {
          csvFile.write(giftDeliveryRequests.get(i).getStatus() + ",");
        }
        if (giftDeliveryRequests.get(i).getAssignee() == null) {
          csvFile.write(',');
        } else {
          csvFile.write(giftDeliveryRequests.get(i).getAssignee() + ",");
        }
        if (giftDeliveryRequests.get(i).getNotes() == null) {
          csvFile.write(',');
        } else {
          csvFile.write(giftDeliveryRequests.get(i).getNotes() + ",");
        }
        if (giftDeliveryRequests.get(i).getGiftType() != null) {
          csvFile.write(giftDeliveryRequests.get(i).getGiftType());
        }
      }
      csvFile.flush();
      csvFile.close();
    } catch (IOException e) {
      System.out.println("Error occurred when updating GiftDeliveryRequests csv file.");
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public String makeID() {
    int nextIDFinalNum = this.getAllRecords().size() + 1;
    return String.format("GIFT%04d", nextIDFinalNum);
  }
}