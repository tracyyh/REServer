package logs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;



public class QueryDAO {

   private Connection connection;

   public QueryDAO() {
    try {
        String url = "jdbc:postgresql://ep-wild-cherry-a7rmik76-pooler.ap-southeast-2.aws.neon.tech/neondb?user=neondb_owner&password=npg_7MXptljGJe0D&sslmode=require&channelBinding=require";
        String user = "neondb_owner";
        String password = "npg_HavSVn2Zy6bi";
 
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to Neon database!");

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Connected to Neon PostgreSQL database (JDBC).");
        } catch (Exception e) {
            System.err.println("Failed to connect to Neon PostgreSQL: " + e.getMessage());
        }
    }

    public int getPostcodeCount(int postcode) {
        String cond = "'postcode=" + postcode + "'";
        String query = "SELECT COUNT(*) from sales_query where query_params = " + cond;
        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1; 
    }
        return 0;
}

    public int getPropertyCount(int propertyId) {
        String cond = "'postcode=" + propertyId + "'";
        String query = "SELECT COUNT(*) FROM sales_query WHERE query_params = " + cond;
        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}