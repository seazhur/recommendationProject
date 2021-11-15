import java.sql.*;
import java.io.*;
import java.util.*;

public class AnalystStats extends MediaInput {
    public static void main(String[] args) {
        // ArrayList<MediaStat> ms = getAllTimeTopTen();
        // for (int i = 0; i < ms.size(); i++) {
        //     System.out.println(ms.get(i));
        // }

        List<ActorPair> ap = getTopActorPairs();
        for (int i = 0; i < ap.size(); i++) {
            System.out.println(ap.get(i));
        }
    }

    public static ArrayList<MediaStat> getAllTimeTopTen() {
        // Get databse connection
        Connection conn = openDatabaseConnection();
        ArrayList<MediaStat> topTenMedia = new ArrayList<MediaStat>();
        ResultSet topTenResultQuery;

        // Execute Statement to Get Watch History titles and averages
        try {
            // Execute Query
            Statement stmt = conn.createStatement();
            String topTenSqlStatement = "SELECT m.title, count(wh.*) as Views FROM watchhistory wh INNER JOIN media m on m.mediaid = wh.titleid";
            topTenSqlStatement += " GROUP BY m.title ORDER BY Views DESC LIMIT 10;";
            topTenResultQuery = stmt.executeQuery(topTenSqlStatement);

            // Get top 10
            while (topTenResultQuery.next()) {
                int views = topTenResultQuery.getInt("views");
                String title = topTenResultQuery.getString("title");
                MediaStat ms = new MediaStat(title, views);
                topTenMedia.add(ms);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        // Close database connection
        closeDatabaseConnection(conn);

        // Return top 10
        return topTenMedia;
    }

    public static ArrayList<MediaStat> getYearlyTopTen(String year) {
        // Get database connection
        Connection conn = openDatabaseConnection();
        ArrayList<MediaStat> topTenMedia = new ArrayList<MediaStat>();
        ResultSet topTenResultQuery;

        // Execute Statement to Get Watch History titles and averages
        try {
            // Execute Query
            Statement stmt = conn.createStatement();
            String topTenSqlStatement;
            topTenSqlStatement = "SELECT m.title, LEFT(wh.date,4) as Year, count(wh.*) as Views FROM watchhistory wh INNER JOIN media m on m.mediaid = wh.titleid WHERE LEFT(date,4)=\'";
            topTenSqlStatement += year + "\' GROUP BY m.title, Year ORDER BY Views DESC limit 10;";
            topTenResultQuery = stmt.executeQuery(topTenSqlStatement);

            // Get top 10
            while (topTenResultQuery.next()) {
                int views = topTenResultQuery.getInt("views");
                String title = topTenResultQuery.getString("title");
                MediaStat ms = new MediaStat(title, views);
                topTenMedia.add(ms);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        // Close database connection
        closeDatabaseConnection(conn);

        // Return top 10
        return topTenMedia;
    }

    public static List<ActorPair> getTopActorPairs() {
        // Get database connection
        Connection conn = openDatabaseConnection();
        ResultSet queryResult;
        List<ActorPair> actorPairList = new ArrayList<ActorPair>();

        // Build SQL to get pairs
        String sqlCommand = "SELECT averagerating, actors from media where actors is not null AND averagerating is not null;";

        try {
            // Generate Pairs
            Statement stmt = conn.createStatement();
            queryResult = stmt.executeQuery(sqlCommand);
            Map<String, ActorPair> actorPairMap = new HashMap<String, ActorPair>();
            while (queryResult.next()) {
                Array sqlArray = queryResult.getArray("actors");
                String actors[] = (String[]) sqlArray.getArray();
                float average = queryResult.getFloat("averagerating");
                for (int i = 0; i < actors.length; i++) {
                    for (int j = i + 1; j < actors.length; j++) {
                        String pairId = actors[i] + actors[j];
                        String altPairId = actors[j] + actors[i];
                        if (actorPairMap.get(pairId) == null && actorPairMap.get(altPairId) == null) {
                            ActorPair ap = new ActorPair(pairId, actors[i], actors[j], average);
                            ap.calculateChemistry();
                            actorPairMap.put(pairId, ap);
                        } else if(actorPairMap.get(pairId) == null) {
                            actorPairMap.get(altPairId).increaseSharedscore(average);
                            actorPairMap.get(altPairId).incrementSharedCount();
                            actorPairMap.get(altPairId).calculateChemistry();
                        } 
                        else {
                            actorPairMap.get(pairId).increaseSharedscore(average);
                            actorPairMap.get(pairId).incrementSharedCount();
                            actorPairMap.get(pairId).calculateChemistry();
                        }
                    }
                }

            }

            // Get top ten pairs
            actorPairList = new ArrayList<ActorPair>(actorPairMap.values());
            Collections.sort(actorPairList);
            actorPairList = actorPairList.subList(actorPairList.size() - 10, actorPairList.size());

            // Get top ten pair names
            for (int i = 0; i < actorPairList.size(); i++) {
                String actor1NameQuery = "SELECT name from actors WHERE actorid='" + actorPairList.get(i).actor1Id
                        + "';";
                String actor2NameQuery = "SELECT name from actors WHERE actorid='" + actorPairList.get(i).actor2Id
                        + "';";
                queryResult = stmt.executeQuery(actor1NameQuery);
                if (queryResult.next()) {
                    actorPairList.get(i).updateActor1(queryResult.getString("name"));
                }
                queryResult = stmt.executeQuery(actor2NameQuery);
                if (queryResult.next()) {
                    actorPairList.get(i).updateActor2(queryResult.getString("name"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        // Close database connection
        closeDatabaseConnection(conn);

        return actorPairList;
    }

    // Helper Function
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
            System.out.println("Opened database successfully");
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
            System.out.println("Connection Closed.");
        } catch (Exception e) {
            System.out.println("Connection NOT Closed.");
        }
    }
}

class MediaStat {
    String mediaTitle;
    int mediaWatchCount;

    public MediaStat() {
        mediaTitle = "";
        mediaWatchCount = 0;
    }

    public MediaStat(String title, int watchCount) {
        mediaTitle = title;
        mediaWatchCount = watchCount;
    }

    public String toString() {
        String output = "Title: " + mediaTitle + "\n";
        output += "Watch Count: " + mediaWatchCount + "\n";
        return output;
    }
}

class ActorPair implements Comparable<ActorPair> {
    String pairId;
    String actor1;
    String actor2;
    String actor1Id;
    String actor2Id;
    double sharedScore;
    int sharedTitleCount;
    double chemistyScore;

    public ActorPair() {
        pairId = "";
        actor1 = "";
        actor2 = "";
        actor1Id = "";
        actor2Id = "";
        sharedScore = 0;
        sharedTitleCount = 0;
        chemistyScore = 0;
    }

    public ActorPair(String pId, String a1Id, String a2Id, float score) {
        pairId = pId;
        actor1 = "";
        actor2 = "";
        actor1Id = a1Id;
        actor2Id = a2Id;
        sharedScore = score;
        sharedTitleCount = 1;
        chemistyScore = 0;
    }

    @Override
    public int compareTo(ActorPair ap) {
        if (this.chemistyScore > ap.chemistyScore) {
            return 1;
        } else if (this.chemistyScore < ap.chemistyScore) {
            return -1;
        }
        return 0;
    }

    public void incrementSharedCount() {
        sharedTitleCount++;
    }

    public void increaseSharedscore(double value) {
        sharedScore += value;
    }

    public void calculateChemistry() {
        chemistyScore = sharedScore / sharedTitleCount;
    }

    public void updateActor1(String actor) {
        actor1 = actor;
    }

    public void updateActor2(String actor) {
        actor2 = actor;
    }

    public String toString() {
        String output = "Actor 1: " + actor1 + "-" + actor1Id + "\n";
        output += "Actor 2: " + actor2 + "-" + actor2Id + "\n";
        output += "Chemistry: " + chemistyScore + "\n";
        return output;
    }
}
