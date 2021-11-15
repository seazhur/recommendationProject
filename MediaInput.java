import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

/*
*    This file exists to contain all the functions for getting the 
*   various media needed for our database from their files
*/
public class MediaInput {

    public static void main(String[] args) {
        getCustomerInfo();
        getWatchHistory();
        getDirectors();
        getMedia();
        System.out.println("All data has successfully been gotten");
    }

    public static ArrayList<Customer> getCustomerInfo() {
        // Create and Prepare Scanner
        File file = new File("./data/customer_ratings.csv");
        Scanner scan;
        try {
            scan = new Scanner(file);

        } catch (IOException e) {
            throw new Error("Could not open customer ratings file");
        }
        scan.nextLine();

        // Read File to get customer ID's
        ArrayList<Customer> customers = new ArrayList<Customer>();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] ratingParts = line.split("\t");
            int index = indexOfCustomer(customers, ratingParts[1]);
            if (index == -1) {
                Date lastActiveDate = Date.valueOf(ratingParts[3]);
                Customer customer = new Customer(ratingParts[1], lastActiveDate);
                customers.add(customer);
            } else {
                Date lastActiveDate = Date.valueOf(ratingParts[3]);
                updateLastActive(customers.get(index), lastActiveDate);
            }
        }

        // Close Scanner
        scan.close();

        // Return CustomerIDs
        return customers;
    }

    public static ArrayList<WatchHistory> getWatchHistory() {
        // Create and Prepare Scanner
        File file = new File("./data/customer_ratings.csv");
        Scanner scan;
        try {
            scan = new Scanner(file);

        } catch (IOException e) {
            throw new Error("Could not open customer ratings file");
        }
        scan.nextLine();

        // Read File to get Watch Histories
        ArrayList<WatchHistory> watchHistories = new ArrayList<WatchHistory>();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] ratingParts = line.split("\t");
            double rating = Double.parseDouble(ratingParts[2]);
            WatchHistory wh = new WatchHistory(ratingParts[1], ratingParts[4], rating, ratingParts[3]);
            watchHistories.add(wh);
        }

        // Close Scanner
        scan.close();

        // Return Watch Histories
        return watchHistories;
    }

    public static ArrayList<Director> getDirectors() {
        // Create and Prepare Scanner
        File file = new File("./data/names.csv");
        Scanner scan;
        try {
            scan = new Scanner(file);

        } catch (IOException e) {
            throw new Error("Could not open names file");
        }
        scan.nextLine();

        // Read File to get Directors
        ArrayList<Director> directors = new ArrayList<Director>();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] ratingParts = line.split("\t");

            if (isDirector(ratingParts[ratingParts.length - 1])) {
                Director director = new Director(ratingParts[2], ratingParts[1]);
                directors.add(director);
            }
        }

        // Close Scanner
        scan.close();

        // Return Directors
        return directors;
    }

    public static ArrayList<Actor> getActors() {
        // Create and Prepare Scanner
        File file = new File("./data/names.csv");
        FileReader nameFileReader;
        Scanner scan;
        try {
            nameFileReader = new FileReader(file);
            scan = new Scanner(nameFileReader);

        } catch (IOException e) {
            throw new Error("Could not open names file");
        }
        scan.nextLine();

        // Read File to get Actors
        ArrayList<Actor> actors = new ArrayList<Actor>();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] ratingParts = line.split("\t");

            if (isActor(ratingParts[ratingParts.length - 1])) {
                Actor actor = new Actor(ratingParts[2], ratingParts[1]);
                actors.add(actor);
            }
        }

        // Close Scanner
        scan.close();

        // Return Actors
        return actors;
    }

    public static ArrayList<Media> getMedia() {
        // Create and Prepare Title Scanner
        File titleFile = new File("./data/titles.csv");

        Scanner titleScan;
        try {
            titleScan = new Scanner(titleFile);
        } catch (IOException e) {
            throw new Error("Could not open titles file");
        }

        titleScan.nextLine();

        // Read File to build medias without directors
        ArrayList<Media> medias = new ArrayList<Media>();
        while (titleScan.hasNextLine()) {
            String line = titleScan.nextLine();
            String[] ratingParts = line.split("\t");
            int mediaIndex = indexOfMedia(medias, ratingParts[1]);
            if (mediaIndex == -1) {
                String[] genreList = ratingParts[7].split(",");
                if (ratingParts[6].isEmpty()) {
                    continue;
                }
                int runtime = Integer.parseInt(ratingParts[6]);
                int releaseYear = ratingParts[4].isEmpty() ? Integer.parseInt(ratingParts[8])
                        : Integer.parseInt(ratingParts[4]);
                Media newMedia = new Media(ratingParts[1], ratingParts[3], genreList, runtime, releaseYear);
                medias.add(newMedia);
            } else {
                updateReleaseDate(medias.get(mediaIndex), Integer.parseInt(ratingParts[4]));
            }
        }

        // Close Title Scanner
        titleScan.close();

        // Open Principals Scanner
        File principalFile = new File("./data/principals.csv");
        FileReader principaFileReader;
        Scanner principalScan;

        try {
            principaFileReader = new FileReader(principalFile);
        } catch (IOException e) {
            throw new Error("Could not open principals file reader");
        }
        principalScan = new Scanner(principaFileReader);
        principalScan.nextLine();

        // Get directorID for the media
        // int count = 0;
        while (principalScan.hasNextLine()) {
            String line = principalScan.nextLine();
            String[] principalParts = line.split("\t");
            int mediaIndex = indexOfMedia(medias, principalParts[1]);

            if (mediaIndex != -1 && isDirector(principalParts[3])) {
                medias.get(mediaIndex).directorId = principalParts[2];
            }
        }

        // Close Scanners
        principalScan.close();

        // Return Media List
        return medias;
    }

    public static void insertActors() {
        // Open Principals Scanner
        File principalFile = new File("./data/principals.csv");
        FileReader principaFileReader;
        Scanner principalScan;

        try {
            principaFileReader = new FileReader(principalFile);
        } catch (IOException e) {
            throw new Error("Could not open principals file reader");
        }
        principalScan = new Scanner(principaFileReader);
        principalScan.nextLine();

        // Open connection
        Connection conn = openDatabaseConnection();
        String command = "UPDATE media SET Actors = array_append(Actors, ?::text) WHERE mediaid=?;";
        String clearCommand = "update media set actors=null;";

        // Execute Insertion of Actors into Media
        try {
            // Clear Column
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(clearCommand);

            // Execute insertion
            PreparedStatement preparedStatement = conn.prepareStatement(command);
            while (principalScan.hasNext()) {
                String line = principalScan.nextLine();
                String[] principalParts = line.split("\t");
                if (isActor(principalParts[3])) {
                    preparedStatement.setString(1, principalParts[2]);
                    preparedStatement.setString(2, principalParts[1]);
                    preparedStatement.addBatch();
                }
            }
            System.out.println(preparedStatement);

            System.out.println("Executing batch update for actors");
            int[] results = preparedStatement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Close Scanner
        principalScan.close();

        // Close Databse Connection
        closeDatabaseConnection(conn);
        System.out.println("Inserting Actors finished");
    }

    public static void insertMediaAverages() {
        // Get connection
        Connection conn = openDatabaseConnection();

        // Query for titleid's
        String averageInsert = "UPDATE media SET averagerating=? WHERE mediaid=?;";

        // Create and Prepare Scanner
        File file = new File("./data/customer_ratings.csv");
        Scanner scan;
        try {
            scan = new Scanner(file);

        } catch (IOException e) {
            throw new Error("Could not open customer ratings file");
        }
        scan.nextLine();

        // Read File to get Averages
        Map<String, TitleAverage> titleMap = new HashMap<String, TitleAverage>();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] ratingParts = line.split("\t");
            float rating = Float.parseFloat(ratingParts[2]);
            String titleId = ratingParts[4];
            if (titleMap.get(titleId) == null) {
                TitleAverage ta = new TitleAverage(titleId, rating);
                titleMap.put(titleId, ta);
            } else {
                titleMap.get(titleId).updateTotalScore(rating);
                titleMap.get(titleId).updateAverage();
            }
        }

        // Execute the query and get the average
        try {
            // Clear Column
            Statement stmt = conn.createStatement();

            PreparedStatement preparedStatement = conn.prepareStatement(averageInsert);

            ArrayList<TitleAverage> titleAverages = new ArrayList<TitleAverage>(titleMap.values());
            for(int i = 0; i < titleAverages.size(); i++) {
                preparedStatement.setFloat(1, titleAverages.get(i).average);
                preparedStatement.setString(2, titleAverages.get(i).title);
                preparedStatement.addBatch();
            }

            System.out.println(preparedStatement.toString());

            // Execute update
            System.out.println("Executing batch update for averages");
            int[] results = preparedStatement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        // Close Scanner
        scan.close();

        // Close Connection
        closeDatabaseConnection(conn);
    }

    // Helper Functions
    private static boolean isDirector(String roleList) {
        return roleList.indexOf("director") != -1;
    }

    private static boolean isActor(String roleList) {
        return roleList.indexOf("actor") != -1 || roleList.indexOf("actress") != -1;
    }

    private static boolean isMovie(String mediaType) {
        return mediaType.equals("movie");
    }

    private static int indexOfCustomer(ArrayList<Customer> customerList, String testCustomerId) {
        for (int i = 0; i < customerList.size(); i++) {
            if (customerList.get(i).customerId.equals(testCustomerId)) {
                return i;
            }
        }

        return -1;
    }

    private static int indexOfMedia(ArrayList<Media> mediaList, String testMediaId) {
        for (int i = 0; i < mediaList.size(); i++) {
            if (mediaList.get(i).mediaId.equals(testMediaId)) {
                return i;
            }
        }

        return -1;
    }

    private static void updateReleaseDate(Media originalMedia, int alternateYear) {
        if (originalMedia.releaseDate < alternateYear) {
            originalMedia.releaseDate = alternateYear;
        }
    }

    private static void updateLastActive(Customer customer, Date testDate) {
        if (customer.lastActive.before(testDate)) {
            customer.lastActive = testDate;
        }
    }

    private static float calculateAverageScore(String mediaId, Connection conn) {
        // Build the command
        String sqlCommand = "SELECT rating::FLOAT, COUNT(*) AS Views FROM watchhistory where titleid='" + mediaId
                + "' GROUP BY rating ORDER BY Views;";
        ResultSet queryResult;
        float average = 0;
        int rating = 0;
        int count = 0;

        // Execute the query and get the average
        try {
            Statement stmt = conn.createStatement();
            queryResult = stmt.executeQuery(sqlCommand);
            System.out.println("Test");
            while (queryResult.next()) {
                int ratingNum = Integer.parseInt(queryResult.getString("rating"));
                int ratingCount = Integer.parseInt(queryResult.getString("Views"));
                rating += ratingNum * ratingCount;
                count += ratingCount;
            }
            average = count == 0 ? 0 : rating / count;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        // Return the average
        return average;
    }

    public static Connection openDatabaseConnection() {
        // Get Database Credentials
        Connection conn = null;
        String dbName = "";
        String conecString = "";
        String userName = "";
        String var = "";
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

        // Connecting to the database
        try {
            conn = DriverManager.getConnection(conecString, userName, var);
            // System.out.println("Opened database successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return conn;
    }

    public static void closeDatabaseConnection(Connection conn) {
        // closing the connection
        try {
            conn.close();
            // System.out.println("Connection Closed.");
        } catch (Exception e) {
            System.out.println("Connection NOT Closed.");
        }
    }
}

/*
 * This class contains a watch history for a user. These histories will be
 * stored in the WatchHistory database.
 */
class WatchHistory {
    public String customerId;
    public String titleId;
    public double rating; // Double for now, will cahnge after talking with team
    public String date;

    public WatchHistory() {
        customerId = "";
        titleId = "";
        rating = 0;
        date = "";
    }

    public WatchHistory(String cId, String tId, double cRating, String rDate) {
        customerId = cId;
        titleId = tId;
        rating = cRating;
        date = rDate;
    }

    public String toString() {
        String output = "Watch History: " + customerId + " " + titleId + " " + rating + " " + date;
        return output;
    }
}

/*
 * The Customer object contains the customer's ID and Nlast active date. Will be
 * stored in the Customers database
 */
class Customer {
    public String customerId;
    public Date lastActive;

    public Customer() {
        long millis = System.currentTimeMillis();
        customerId = "";
        lastActive = new java.sql.Date(millis);
    }

    public Customer(String cId, Date lastAct) {
        customerId = cId;
        lastActive = lastAct;
    }

    public String toString() {
        String output = customerId + " " + lastActive.toString();
        return output;
    }
}

/*
 * The Director object contains the director's ID and Name. Will be stored in
 * the Directors database
 */
class Director {
    public String name;
    public String directorId;

    public Director() {
        name = "";
        directorId = "";
    }

    public Director(String directorName, String dId) {
        name = directorName;
        directorId = dId;
    }

    public String toString() {
        String output = directorId + " = " + name;
        return output;
    }
}

/*
 * The Actor object contains the Actor's ID and Name. Will be stored in the
 * Actors database
 */
class Actor {
    public String name;
    public String actorId;

    public Actor() {
        name = "";
        actorId = "";
    }

    public Actor(String actorName, String aId) {
        name = actorName;
        actorId = aId;
    }

    public String toString() {
        String output = actorId + " = " + name;
        return output;
    }
}

/*
 * Media objects contain important info about a piece of media. Will be stored
 * in the Media database
 */
class Media {
    public String mediaId;
    public String directorId;
    public String title;
    public ArrayList<String> genres;
    public int runtime;
    public int releaseDate;

    public Media() {
        mediaId = "";
        directorId = "";
        title = "";
        genres = new ArrayList<String>();
        runtime = 0;
        releaseDate = 0;
    }

    public Media(String mId, String mediaTitle, String[] mediaGenres, int rnTime, int rlsDate) {
        mediaId = mId;
        directorId = "";
        title = mediaTitle;
        genres = new ArrayList<String>(Arrays.asList(mediaGenres));
        runtime = rnTime;
        releaseDate = rlsDate;
    }

    public Media(String mId, String dId, String mediaTitle, String[] mediaGenres, int rnTime, int rlsDate) {
        mediaId = mId;
        directorId = dId;
        title = mediaTitle;
        genres = new ArrayList<String>(Arrays.asList(mediaGenres));
        runtime = rnTime;
        releaseDate = 0;
    }

    public String toString() {
        String output = "MediaID: " + mediaId + "\n";
        output += "DirectorID: " + directorId + "\n";
        output += "Title: " + title + "\n";
        output += "Genres: " + genres.toString() + "\n";
        output += "Runtime: " + runtime + "\n";
        output += "Release Date: " + releaseDate;
        return output;
    }
}

class TitleAverage {
    String title;
    float totalScore;
    float average;
    int count;

    public TitleAverage(String t, float score) {
        title = t;
        totalScore = score;
        count = 1;
        average = score;
    }

    public void updateTotalScore(float score) {
        totalScore += score;
        count++;
    }

    public void updateAverage() {
        average = totalScore / count;
    }
}
