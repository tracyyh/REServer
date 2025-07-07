package logs;

import org.bson.Document;
import org.bson.conversions.Bson;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;



public class QueryDAO {

   private MongoCollection<Document> collection;


   public QueryDAO() {
       try {
           ConnectionString connString = new ConnectionString("mongodb+srv://kulkarnisid:123@group5cluster.mygjmiu.mongodb.net/?retryWrites=true&w=majority&appName=Group5Cluster");


           ServerApi serverApi = ServerApi.builder()
                   .version(ServerApiVersion.V1)
                   .build();


           MongoClientSettings settings = MongoClientSettings.builder()
                   .applyConnectionString(connString)
                   .serverApi(serverApi)
                   .build();


           MongoClient mongoClient = MongoClients.create(settings);


           MongoDatabase database = mongoClient.getDatabase("HomeSale");
           collection = database.getCollection("SalesQuery");


       } catch (MongoException e) {
           System.err.println("Failed to connect to MongoDB: " + e.getMessage());
       }
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