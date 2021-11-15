import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;


public class tomato {
    public static class Node {
        String titleid;
        String userid;
        int nodeID;

        public Node(String _titleid, String _userid, int _nodeID) {
            titleid = _titleid;
            userid = _userid;
            nodeID = _nodeID;
        }

        public String getTitleID () {
            return this.titleid;
        }
        public String getUserID () {
            return this.userid;
        }
        public int getNodeID () {
            return this.nodeID;
        }
    }

    public static void main(String args[]) {
        freshTomatoNumber("tt1736672","tt9150214");
        // System.out.println(path2);
        // System.out.println(recursiveStop);
    }


    public static class pathHolder {
        String user;
        String titleid;
        public pathHolder(String _user, String _titleid) {
            user = _user;
            titleid = _titleid;
        }
    }

    public static boolean recursiveStop = false;
    public static String path2 = "";
    public static HashSet<String> set = new HashSet<String>();

    public static void recursiveSearch(String titleidA, String titleidB, Connection conn, String path) {
        // System.out.println("Beginning recursiveSearch");
        // System.out.println(path);
        if (recursiveStop) return;
        // System.out.println("Skipped if stmt");

        // String sqlStmt = "SELECT * FROM watchhistory WHERE ((watchhistory.rating='4' OR watchhistory.rating='5') AND (watchhistory.titleid='"+mediaIDA+"' OR watchhistory.titleid='"+mediaIDB+"'));"; 
        // String sqlStmt = "SELECT * FROM watchhistory WHERE (watchhistory.rating='4' OR watchhistory.rating='5');"; 
        String sqlGetUserIDsforMovieA = "SELECT customerid FROM watchhistory WHERE ((watchhistory.rating='4' OR watchhistory.rating='5') AND (watchhistory.titleid='"+titleidA+"')) limit 10;";
        
        ArrayList<String> customersList = new ArrayList<String>();
        // System.out.println("customerList size: " + customersList.size());
        ResultSet RS;
        
        try {
            // System.out.println("Executing first query");
            Statement stmt = conn.createStatement();
            RS = stmt.executeQuery(sqlGetUserIDsforMovieA);
            ArrayList<pathHolder> titleidList = new ArrayList<pathHolder>();

            while (RS.next()) {
                String cust = RS.getString("customerid");
                customersList.add(cust);
            }

            String sqlGetWHforUser = "SELECT titleid FROM watchhistory WHERE customerid ='";
            // String prepStmt = conn.prepareStatement(sqlGetWHforUser);
            
            
            for (int i = 0; i < customersList.size(); i++) {
                String stmtForTomato = sqlGetWHforUser + customersList.get(i) + "';";
                RS = stmt.executeQuery(stmtForTomato);
                
                while (RS.next()) {
                    String title = RS.getString("titleid");
                    if (set.add(title)) {
                        titleidList.add(new pathHolder(customersList.get(i), title));
                    }
                }
                
                for (int j = 0; j < titleidList.size(); j++) {
                    if (titleidList.get(j).titleid.equals(titleidB)) {
                        recursiveStop = true; // connection found
                        if (path.equals("")) {
                            path2 = titleidList.get(j).user;
                            return;
                        }
                        path2 = path + " " + titleidList.get(j).user;
                        return;
                    }
                }
            }
            // System.out.println("titleidList size: " + titleidList.size());

            
            for (int k = 0; k < titleidList.size(); k++) {
                recursiveSearch(titleidList.get(k).titleid, titleidB, conn, path + " " + titleidList.get(k).user);
            }
            

            // System.out.println("Ended last for loop");
            
            
        
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        return;
    }

    public static void freshTomatoNumber(String mediaIDA, String mediaIDB) {
        // connect to db
        Connection conn = openDatabaseConnection();

        recursiveSearch(mediaIDA, mediaIDB, conn, new String(""));


        // close db connection
        closeDatabaseConnection(conn);
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