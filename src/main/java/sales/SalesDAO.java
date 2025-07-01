package sales;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class SalesDAO {

    private MongoCollection<Document> collection;

    public SalesDAO() {
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

            MongoDatabase database = mongoClient.getDatabase("realestate"); // Replace with your DB name
            collection = database.getCollection("sales"); // Replace with your collection name

        } catch (MongoException e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
    }

    public boolean newSale(HomeSale homeSale) {
        try {
            Document doc = new Document("saleID", homeSale.saleID)
                    .append("postcode", homeSale.postcode)
                    .append("salePrice", homeSale.salePrice);
            collection.insertOne(doc);
            return true;
        } catch (MongoException e) {
            System.err.println("Error inserting document: " + e.getMessage());
            return false;
        }
    }

    public Optional<HomeSale> getSaleById(String saleID) {
        Document doc = collection.find(new Document("saleID", saleID)).first();
        if (doc != null) {
            return Optional.of(documentToHomeSale(doc));
        }
        return Optional.empty();
    }

    public List<HomeSale> getSalesByPostCode(String postCode) {
        List<HomeSale> result = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(new Document("postcode", postCode)).iterator()) {
            while (cursor.hasNext()) {
                result.add(documentToHomeSale(cursor.next()));
            }
        }
        return result;
    }

    public List<String> getAllSalePrices() {
        List<String> prices = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                prices.add(cursor.next().getString("salePrice"));
            }
        }
        return prices;
    }

    public List<HomeSale> getAllSales() {
        List<HomeSale> allSales = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                allSales.add(documentToHomeSale(cursor.next()));
            }
        }
        return allSales;
    }

    private HomeSale documentToHomeSale(Document doc) {
        return new HomeSale(
                doc.getString("saleID"),
                doc.getString("postcode"),
                doc.getString("salePrice")
        );
    }
}
