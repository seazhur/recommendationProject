import java.sql.*;
import java.io.*;
import java.util.ArrayList;

/*
CSCE 315
9-27-2021 Lab
 */
public class scriptFile extends MediaInput {

  // Helper functions

  public static void DeleteTables(Connection conn) {
    String sqlStatementWatchHistory = "DROP TABLE IF EXISTS watchhistory;";
    String sqlStatementCustomerIDs = "DROP TABLE IF EXISTS customerid;";
    String sqlStatementDirectors = "DROP TABLE IF EXISTS directors;";
    String sqlStatementMedia = "DROP TABLE IF EXISTS media;";
    String sqlStatementActors = "DROP TABLE IF EXISTS actors";

    // EXECUTE STATEMENTS with DB
    try {
      Statement stmt = conn.createStatement();
      int result = stmt.executeUpdate(sqlStatementWatchHistory);
      System.out.println("SQL Update Result: " + result);
      result = stmt.executeUpdate(sqlStatementDirectors);
      System.out.println("SQL Update Result: " + result);
      result = stmt.executeUpdate(sqlStatementCustomerIDs);
      System.out.println("SQL Update Result: " + result);
      result = stmt.executeUpdate(sqlStatementMedia);
      System.out.println("SQL Update Result: " + result);
      result = stmt.executeUpdate(sqlStatementActors);
      System.out.println("SQL Update Result: " + result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void CreateTable(Connection conn) {
    String sqlStatementWatchHistory = "CREATE TABLE IF NOT EXISTS WatchHistory(CustomerID text, TitleID text, Rating text, Date text);";
    String sqlStatementCustomerIDs = "CREATE TABLE IF NOT EXISTS Customers(CustomerID text, LastActive Date);";
    String sqlStatementDirectors = "CREATE TABLE IF NOT EXISTS Directors(Name text, DirectorID text);";
    String sqlStatementMedia = "CREATE TABLE IF NOT EXISTS Media(MediaID text, DirectorID text, Title text, Genre text, Runtime text, ReleaseDate text, AverageRating float, Actors text[]);";
    String sqlStatementActors = "CREATE TABLE IF NOT EXISTS Actors(Name text, ActorID text);";
    // EXECUTE STATEMENTS with DB
    try {
      Statement stmt = conn.createStatement();
      int result = stmt.executeUpdate(sqlStatementWatchHistory);
      System.out.println("SQL Update Result: " + result);
      result = stmt.executeUpdate(sqlStatementDirectors);
      System.out.println("SQL Update Result: " + result);
      result = stmt.executeUpdate(sqlStatementCustomerIDs);
      System.out.println("SQL Update Result: " + result);
      result = stmt.executeUpdate(sqlStatementMedia);
      System.out.println("SQL Update Result: " + result);
      result = stmt.executeUpdate(sqlStatementActors);
      System.out.println("SQL Update Result: " + result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void PopulateTables(Connection conn) {
    // Watch History
    ArrayList<WatchHistory> WatchHistory = getWatchHistory();
    // String sqlStatement = "INSERT INTO WatchHistory(CustomerID, TitleID, Rating,
    // Date) VALUES (" + WatchHistory.get(i).customerId + ", " +
    // WatchHistory.get(i).titleId + ", " + WatchHistory.get(i).rating + ", " +
    // WatchHistory.get(i).date + ");";
    // EXECUTE STATEMENTS with DB
    try {
      Statement stmt = conn.createStatement();
      // int result = stmt.executeUpdate(sqlStatement);
      // System.out.println("SQL Update Result: " + result);

      PreparedStatement preparedStatement = conn
          .prepareStatement("INSERT INTO WatchHistory(CustomerID, TitleID, Rating, Date) VALUES (?, ?, ?, ?)");

      for (int i = 0; i < WatchHistory.size(); i++) {
        preparedStatement.setString(1, WatchHistory.get(i).customerId);
        preparedStatement.setString(2, WatchHistory.get(i).titleId);
        preparedStatement.setDouble(3, WatchHistory.get(i).rating);
        preparedStatement.setString(4, WatchHistory.get(i).date);
        preparedStatement.addBatch();
      }

      int[] inserted = preparedStatement.executeBatch();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Watch History Finished");

    // Directors
    ArrayList<Director> Directors = getDirectors();
    try {
      Statement stmt = conn.createStatement();
      // int result = stmt.executeUpdate(sqlStatement);
      // System.out.println("SQL Update Result: " + result);

      PreparedStatement preparedStatement = conn
          .prepareStatement("INSERT INTO Directors(Name, DirectorID) VALUES (?, ?)");

      for (int i = 0; i < Directors.size(); i++) {
        preparedStatement.setString(1, Directors.get(i).name);
        preparedStatement.setString(2, Directors.get(i).directorId);
        preparedStatement.addBatch();
      }

      int[] inserted = preparedStatement.executeBatch();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Directors Finished");

    // Actors
    ArrayList<Actor> Actors = getActors();
    try {
      Statement stmt = conn.createStatement();
      // int result = stmt.executeUpdate(sqlStatement);
      // System.out.println("SQL Update Result: " + result);

      PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO Actors(Name, ActorID) VALUES (?, ?)");

      for (int i = 0; i < Actors.size(); i++) {
        preparedStatement.setString(1, Actors.get(i).name);
        preparedStatement.setString(2, Actors.get(i).actorId);
        preparedStatement.addBatch();
      }

      int[] inserted = preparedStatement.executeBatch();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Actors Finished");

    // Media
    ArrayList<Media> Media = getMedia();
    try {
      Statement stmt = conn.createStatement();
      // int result = stmt.executeUpdate(sqlStatement);
      // System.out.println("SQL Update Result: " + result);

      PreparedStatement preparedStatement = conn.prepareStatement(
          "INSERT INTO Media(MediaID, DirectorID, Title, Genre, Runtime, ReleaseDate) VALUES (?, ?, ?, ?, ?, ?)");

      for (int i = 0; i < Media.size(); i++) {
        preparedStatement.setString(1, Media.get(i).mediaId);
        preparedStatement.setString(2, Media.get(i).directorId);
        preparedStatement.setString(3, Media.get(i).title);
        preparedStatement.setString(4, Media.get(i).genres.toString());
        preparedStatement.setInt(5, Media.get(i).runtime);
        preparedStatement.setInt(6, Media.get(i).releaseDate);
        preparedStatement.addBatch();
      }

      int[] inserted = preparedStatement.executeBatch();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Initial Media Creation Finished");

    // Customers
    ArrayList<Customer> Customer = getCustomerInfo();
    try {
      Statement stmt = conn.createStatement();
      // int result = stmt.executeUpdate(sqlStatement);
      // System.out.println("SQL Update Result: " + result);

      PreparedStatement preparedStatement = conn
          .prepareStatement("INSERT INTO Customers(CustomerID, LastActive) VALUES (?, ?)");

      for (int i = 0; i < Customer.size(); i++) {
        preparedStatement.setString(1, Customer.get(i).customerId);
        preparedStatement.setDate(2, Customer.get(i).lastActive);
        preparedStatement.addBatch();
      }

      int[] inserted = preparedStatement.executeBatch();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Customer Finished");

    // Updates to media
    insertActors();
    System.out.println("Media Update 1 Successful");
    insertMediaAverages();
    System.out.println("All Media Updates Successful");
  }

  // Commands to run this script
  // This will compile all java files in this directory
  // javac *.java
  // This command tells the file where to find the postgres jar which it needs to
  // execute postgres commands, then executes the code
  // java -cp ".;postgresql-42.2.8.jar" jdbcpostgreSQL

  // MAKE SURE YOU ARE ON VPN or TAMU WIFI TO ACCESS DATABASE
  public static void main(String args[]) {

    // Building the connection with your credentials
    // TODO: update dbName, userName, and userPassword here

    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////

    // Reading connection variables

    Connection conn = null;
    String dbName = "";
    String conecString = "";
    String userName = "";
    String var = "";
    // FileInputStream fstream = new FileInputStream("CS.txt");
    // BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader("CS.txt"));
      // guaranteed four lines in text file
      dbName = br.readLine();
      conecString = br.readLine() + dbName;
      userName = br.readLine();
      var = br.readLine();
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // System.out.println(dbName);
    // System.out.println(conecString);
    // System.out.println(userName);
    // System.out.println(var);

    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////

    // Connecting to the database
    try {
      conn = DriverManager.getConnection(conecString, userName, var);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }

    System.out.println("Opened database successfully");

    try {
      //////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////

      // Manipulating Database by reading SQL statements
      // DeleteTables(conn);
      // CreateTable(conn);
      // PopulateTables(conn);
      // insertActors();
      // System.out.println("Media Update 1 Successful");
      // System.out.println("Starting Avg Insertion");
      // insertMediaAverages();
      // System.out.println("All Media Updates Successful");

      // Validation via txt file w SQL statements

      // Statement stmt = conn.createStatement();
      // String sqlStatement = "blank";

      // BufferedReader br2;
      // try {
      // br2 = new BufferedReader(new FileReader("sqlQueries.txt"));
      // // parse through all lines
      // while (sqlStatement != null) {
      // // get sql query and add to bash
      // // every other line is a comment in txt file
      // sqlStatement = br2.readLine();
      // System.out.println(sqlStatement);
      // sqlStatement = br2.readLine();
      // System.out.println(sqlStatement);
      // int result = stmt.executeQuery(sqlStatement); // retrieve data
      // }
      // br2.close();
      // } catch (IOException e) {
      // e.printStackTrace();
      // }

      //////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }

    // closing the connection
    try {
      conn.close();
      System.out.println("Connection Closed.");
    } catch (Exception e) {
      System.out.println("Connection NOT Closed.");
    } // end try catch
  }// end main
}// end Class
