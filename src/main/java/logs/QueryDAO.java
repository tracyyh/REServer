package logs;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;



public class QueryDAO {

   private MongoCollection<Document> collection;

   public QueryDAO() {
    try {
        // Connect to Neon PostgreSQL database using JDBC
        String url = "jdbc:postgresql://ep-wild-cherry-a7rmik76-pooler.ap-southeast-2.aws.neon.tech/neondb?user=neondb_owner&password=npg_7MXptljGJe0D&sslmode=require&channelBinding=require";
        String user = "neondb_owner";
        String password = "npg_HavSVn2Zy6bi";
 
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to Neon database!");

            // Example query
            Statement stmt = conn.createStatement();
            String sqlStr = "SELECT * from sales LIMIT 100";
            ResultSet rs = stmt.executeQuery(sqlStr);
            while (rs.next()) {
                System.out.println("Property ID " + rs.getString("property_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Connected to Neon PostgreSQL database (JDBC).");
        } catch (Exception e) {
            System.err.println("Failed to connect to Neon PostgreSQL: " + e.getMessage());
        }
    }

    public static void main (String[] args) {
        QueryDAO dao = new QueryDAO();
    }


    public int getPostcodeCount(int postcode) {
        Bson query = eq("params", "post_code=" + Integer.toString(postcode));
        long count = collection.countDocuments(query);
        return (int) count;
    }

    public int getPropertyCount(int propertyId) {
        Bson query = eq("params", "property_id=" + Integer.toString(propertyId));
        long count = collection.countDocuments(query);

        return (int) count;
    }
}