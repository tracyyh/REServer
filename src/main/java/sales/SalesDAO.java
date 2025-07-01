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

            MongoDatabase database = mongoClient.getDatabase("HomeSale"); // Replace if needed
            collection = database.getCollection("Sales"); // Replace if needed

        } catch (MongoException e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
    }

    public boolean newSale(HomeSale homeSale) {
        try {
            Document doc = new Document("property_id", homeSale.property_id)
                    .append("download_date", homeSale.download_date)
                    .append("council_name", homeSale.council_name)
                    .append("purchase_price", homeSale.purchase_price)
                    .append("address", homeSale.address)
                    .append("post_code", homeSale.post_code)
                    .append("property_type", homeSale.property_type)
                    .append("strata_lot_number", homeSale.strata_lot_number)
                    .append("property_name", homeSale.property_name)
                    .append("area", homeSale.area)
                    .append("area_type", homeSale.area_type)
                    .append("contract_data", homeSale.contract_data)
                    .append("settlement_date", homeSale.settlement_date)
                    .append("zoning", homeSale.zoning)
                    .append("nature_of_property", homeSale.nature_of_property)
                    .append("primary_purpose", homeSale.primary_purpose)
                    .append("legal_description", homeSale.legal_description);
            collection.insertOne(doc);
            return true;
        } catch (MongoException e) {
            System.err.println("Error inserting document: " + e.getMessage());
            return false;
        }
    }

    public Optional<HomeSale> getSaleById(int propertyId) {
        Document doc = collection.find(new Document("property_id", propertyId)).first();
        if (doc != null) {
            return Optional.of(documentToHomeSale(doc));
        }
        return Optional.empty();
    }

    public List<HomeSale> getSalesByPostCode(int postCode) {
        List<HomeSale> result = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(new Document("post_code", postCode)).iterator()) {
            while (cursor.hasNext()) {
                result.add(documentToHomeSale(cursor.next()));
            }
        }
        return result;
    }

    public List<Integer> getAllSalePrices() {
        List<Integer> prices = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                prices.add(doc.getInteger("purchase_price", 0));
            }
        }
        return prices;
    }

    public List<HomeSale> getAllSales() {
        List<HomeSale> allSales = new ArrayList<>();
        int count = 0;
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext() & count < 1000) {
                allSales.add(documentToHomeSale(cursor.next()));
                count++;
            }
        }
        return allSales;
    }

    private HomeSale documentToHomeSale(Document doc) {
        return new HomeSale(
                doc.getInteger("property_id", 0),
                doc.getString("download_date"),
                doc.getString("council_name"),
                doc.getInteger("purchase_price", 0),
                doc.getString("address"),
                doc.getInteger("post_code", 0),
                doc.getString("property_type"),
                doc.getString("strata_lot_number"),
                doc.getString("property_name"),
                doc.getInteger("area", 0),
                doc.getString("area_type"),
                doc.getString("contract_data"),
                doc.getString("settlement_date"),
                doc.getString("zoning"),
                doc.getString("nature_of_property"),
                doc.getString("primary_purpose"),
                doc.getString("legal_description")
        );
    }
}
