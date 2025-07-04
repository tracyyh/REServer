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


           MongoDatabase database = mongoClient.getDatabase("HomeSale");
           collection = database.getCollection("Sales");


       } catch (MongoException e) {
           System.err.println("Failed to connect to MongoDB: " + e.getMessage());
       }
   }


   public boolean newSale(HomeSale homeSale) {
       try {
           Document doc = new Document("property_id", homeSale.getProperty_id())
                   .append("download_date", homeSale.getDownload_date())
                   .append("council_name", homeSale.getCouncil_name())
                   .append("purchase_price", homeSale.getPurchase_price())
                   .append("address", homeSale.getAddress())
                   .append("post_code", homeSale.getPost_code())
                   .append("property_type", homeSale.getProperty_type())
                   .append("strata_lot_number", homeSale.getStrata_lot_number())
                   .append("property_name", homeSale.getProperty_name())
                   .append("area", homeSale.getArea())
                   .append("area_type", homeSale.getArea_type())
                   .append("contract_date", homeSale.getContract_date())
                   .append("settlement_date", homeSale.getSettlement_date())
                   .append("zoning", homeSale.getZoning())
                   .append("nature_of_property", homeSale.getNature_of_property())
                   .append("primary_purpose", homeSale.getPrimary_purpose())
                   .append("legal_description", homeSale.getLegal_description());
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

    public int getAvgPriceByPostCode(int postCode) {
        List<HomeSale> sales = this.getSalesByPostCode(postCode);
        if (sales.isEmpty()) {
            return -1;
        }
        long totalPrice = 0;
        for (HomeSale sale : sales) {
            totalPrice += sale.getPurchase_price();
        }
        long result = totalPrice / sales.size();
        return (int) result;
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
           while (cursor.hasNext() && count < 100) {
               allSales.add(documentToHomeSale(cursor.next()));
               count++;
           }
       }
       return allSales;
   }

    public List<HomeSale> getSalesByPriceRange(int low, int high) {
        List<HomeSale> salesInRange = new ArrayList<>();
        int count = 0;
        if (low < 0 || high < 0 || low > high) {
            System.err.println("Invalid price range: low = " + low + ", high = " + high);
            System.err.println("sales:" + salesInRange);
            return salesInRange;
        }
        try (MongoCursor<Document> cursor = collection.find(new Document("purchase_price", new Document("$gte", low).append("$lte", high))).iterator()) {
            while (cursor.hasNext() && count < 100) {
                salesInRange.add(documentToHomeSale(cursor.next()));
                count++;
        }
        } catch (MongoException e) {
            System.err.println("Error retrieving sales by price range: " + e.getMessage());
        }
        return salesInRange;
    }



   private int parseAreaField(Object areaValue) {
   if (areaValue instanceof Integer) {
       return (Integer) areaValue;
   } else if (areaValue instanceof Double) {
       return ((Double) areaValue).intValue();
   } else if (areaValue instanceof String) {
       try {
           return (int) Double.parseDouble((String) areaValue);
       } catch (NumberFormatException e) {
           return 0;
       }
   }
   return 0;
}
    private String parseStringField(Object fieldValue) {
        if (fieldValue instanceof Integer) {
            return Integer.toString((Integer) fieldValue);
        } else if (fieldValue instanceof String) {
            return (String) fieldValue;
        }
        return ""; 
    }

    private HomeSale documentToHomeSale(Document doc) {
        return new HomeSale(
                (doc.get("property_id") instanceof Integer ? doc.getInteger("property_id", 0) :  Integer.parseInt(doc.getString("property_id"))),
                doc.getString("download_date"),
                parseStringField(doc.get("council_name")),
                doc.getInteger("purchase_price", 0),
                doc.getString("address"),
                doc.getInteger("post_code", 0),
                doc.getString("property_type"),
                (doc.get("strata_lot_number") instanceof Integer ? Integer.toString(doc.getInteger("strata_lot_number")) : doc.getString("strata_lot_number")),
                doc.getString("property_name"),
                parseAreaField(doc.get("area")),
                doc.getString("area_type"),
                doc.getString("contract_date"),
                doc.getString("settlement_date"),
                doc.getString("zoning"),
                (doc.get("nature_of_property") instanceof Integer ? Integer.toString(doc.getInteger("nature_of_property")) : doc.getString("nature_of_property")),    
                doc.getString("primary_purpose"),
                doc.getString("legal_description")
        );
    }

}

