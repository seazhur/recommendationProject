import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class custSql extends MediaInput {

    public static void main(String args[]) {
        
        dateParse("2037693"); // Testing with this ID

    }

    // public static bool custExist(String customerid) {
    //     String sqlStatement = "SELECT customerid FROM customerid WHERE customerid='" + customerid + "';";

    // }

    public static Boolean dateParse(String customerid) {

        ArrayList<Customer> cust = getCustomerInfo();
        Date lastActiveDate = new Date();
        Boolean exists = false;

        //Check if ID exists (Optimize Next week)
        // String sqlStatement = "SELECT customerid FROM customerid WHERE customerid='" + customerid + "';";

        // Checking if the customerID exists in the table
        for (int i = 0; i < cust.size(); i++) {
            if (customerid.equals(cust.get(i).customerId)) {
                exists = true;
                break;
            }
        }

        for (int i = 0; i < cust.size(); i++) {
            if (customerid.equals(cust.get(i).customerId)) {
                lastActiveDate = cust.get(i).lastActive;
                break;
            }
        }

        ArrayList<String> oneDay = oneDay(customerid, lastActiveDate);
        ArrayList<String> oneWeek = oneWeek(customerid, lastActiveDate);
        ArrayList<String> oneMonth = oneMonth(customerid, lastActiveDate);
        ArrayList<String> oneYear = oneYear(customerid, lastActiveDate);
        ArrayList<String> threeYear = threeYear(customerid, lastActiveDate);

        // Viewing Output
        System.out.println(oneDay);
        System.out.println(oneWeek);
        System.out.println(oneMonth);
        System.out.println(oneYear);
        System.out.println(threeYear);

        return exists;
    }

    public static ArrayList<String> oneDay(String customerid, Date lastActiveDate) {
        // Get databse connection
        Connection conn = openDatabaseConnection();
        ArrayList<String> watchHistory= new ArrayList<String>();
        ResultSet result;

        // Build SQL Statement
        Calendar c = Calendar.getInstance();
        c.setTime(lastActiveDate);
        c.add(Calendar.DAY_OF_YEAR, -1);
        Date date = c.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String newDate = format1.format(date);

        String sqlStatement = "SELECT m.title FROM watchhistory wh INNER JOIN media m on m.mediaid = wh.titleid WHERE customerid='" + customerid + 
        "' AND date BETWEEN '" + newDate + "' AND '" + lastActiveDate + "';";

        // Execute Statement to Get Watch History titles and averages
        try {
            // Execute Query
            Statement stmt = conn.createStatement();
            result= stmt.executeQuery(sqlStatement);

            // Get watch history
            while(result.next()) {
                String title = result.getString("title");
                watchHistory.add(title);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        // Close database connection
        closeDatabaseConnection(conn);

        return watchHistory;
        
    }

    public static ArrayList<String> oneWeek(String customerid, Date lastActiveDate) {
        // Get databse connection
        Connection conn = openDatabaseConnection();
        ArrayList<String> watchHistory= new ArrayList<String>();
        ResultSet result;

        // Build SQL Statement
        Calendar c = Calendar.getInstance();
        c.setTime(lastActiveDate);
        c.add(Calendar.WEEK_OF_YEAR, -1);
        Date date = c.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String newDate = format1.format(date);

        String sqlStatement = "SELECT m.title FROM watchhistory wh INNER JOIN media m on m.mediaid = wh.titleid WHERE customerid='" + customerid + 
        "' AND date BETWEEN '" + newDate + "' AND '" + lastActiveDate + "';";

        // Execute Statement to Get Watch History titles and averages
        try {
            // Execute Query
            Statement stmt = conn.createStatement();
            result= stmt.executeQuery(sqlStatement);

            // Get watch history
            while(result.next()) {
                String title = result.getString("title");
                watchHistory.add(title);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        // Close database connection
        closeDatabaseConnection(conn);

        return watchHistory;
    }

    public static ArrayList<String> oneMonth(String customerid, Date lastActiveDate) {
        // Get databse connection
        Connection conn = openDatabaseConnection();
        ArrayList<String> watchHistory= new ArrayList<String>();
        ResultSet result;

        Calendar c = Calendar.getInstance();
        c.setTime(lastActiveDate);
        c.add(Calendar.MONTH, -1);
        Date date = c.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String newDate = format1.format(date);

        String sqlStatement = "SELECT m.title FROM watchhistory wh INNER JOIN media m on m.mediaid = wh.titleid WHERE customerid='" + customerid + 
        "' AND date BETWEEN '" + newDate + "' AND '" + lastActiveDate + "';";

        // Execute Statement to Get Watch History titles and averages
        try {
            // Execute Query
            Statement stmt = conn.createStatement();
            result= stmt.executeQuery(sqlStatement);

            // Get watch history
            while(result.next()) {
                String title = result.getString("title");
                watchHistory.add(title);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        // Close database connection
        closeDatabaseConnection(conn);

        return watchHistory;
    }

    public static ArrayList<String> oneYear(String customerid, Date lastActiveDate) {
        // Get databse connection
        Connection conn = openDatabaseConnection();
        ArrayList<String> watchHistory= new ArrayList<String>();
        ResultSet result;

        // Build Statement
        Calendar c = Calendar.getInstance();
        c.setTime(lastActiveDate);
        c.add(Calendar.YEAR, -1);
        Date date = c.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String newDate = format1.format(date);

        String sqlStatement = "SELECT m.title FROM watchhistory wh INNER JOIN media m on m.mediaid = wh.titleid WHERE customerid='" + customerid + 
        "' AND date BETWEEN '" + newDate + "' AND '" + lastActiveDate + "';";

        // Execute Statement to Get Watch History titles and averages
        try {
            // Execute Query
            Statement stmt = conn.createStatement();
            result= stmt.executeQuery(sqlStatement);

            // Get watch history
            while(result.next()) {
                String title = result.getString("title");
                watchHistory.add(title);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        // Close database connection
        closeDatabaseConnection(conn);

        return watchHistory;
    }

    public static ArrayList<String> threeYear(String customerid, Date lastActiveDate) {
        // Get databse connection
        Connection conn = openDatabaseConnection();
        ArrayList<String> watchHistory= new ArrayList<String>();
        ResultSet result;

        // Build Statement
        Calendar c = Calendar.getInstance();
        c.setTime(lastActiveDate);
        c.add(Calendar.YEAR, -3);
        Date date = c.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String newDate = format1.format(date);

        String sqlStatement = "SELECT m.title FROM watchhistory wh INNER JOIN media m on m.mediaid = wh.titleid WHERE customerid='" + customerid + 
        "' AND date BETWEEN '" + newDate + "' AND '" + lastActiveDate + "';";

        // Execute Statement to Get Watch History titles and averages
        try {
            // Execute Query
            Statement stmt = conn.createStatement();
            result= stmt.executeQuery(sqlStatement);

            // Get watch history
            while(result.next()) {
                String title = result.getString("title");
                watchHistory.add(title);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        // Close database connection
        closeDatabaseConnection(conn);

        return watchHistory;
    }

    // Helper Function
    public static Connection openDatabaseConnection() {
        // Get Database Credentials
        Connection conn = null;
        String dbName = ""; String conecString = ""; String userName = ""; String var = "";
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

        //Connecting to the database 
        try {
            conn = DriverManager.getConnection(conecString,userName, var);
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        return conn;
    }

    public static void closeDatabaseConnection(Connection conn) {
        //closing the connection
        try {
            conn.close();
            System.out.println("Connection Closed.");
        } catch(Exception e) {
            System.out.println("Connection NOT Closed.");
        }
    }
}