package sales;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;


public class SalesDAO {

   private MongoCollection<Document> salesCollection;
   private MongoCollection<Document> salesQueryCollection;
   private Connection connection;


   public SalesDAO() {
    try {
        // Connect to Neon PostgreSQL database using JDBC
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


public int newSale(HomeSale homeSale) {
    String sqlStr = String.format(
        "INSERT INTO home_sales (" +
            "property_id," +
            "download_date," +
            "council_name," +
            "purchase_price," +
            "address," +
            "post_code," +
            "property_type," +
            "strata_lot_number," +
            "property_name," +
            "area," +
            "area_type," +
            "contract_date," +
            "settlement_date," +
            "zoning," +
            "nature_of_property," +
            "primary_purpose," +
            "legal_description" +
        ") VALUES (" +
            "%d, '%s', '%s', %d, '%s', %d, '%s', '%s', '%s', %d, '%s', '%s', '%s', '%s', '%s', '%s', '%s'" +
        ");",
        homeSale.property_id,
        homeSale.download_date,
        homeSale.council_name,
        homeSale.purchase_price,
        homeSale.address,
        homeSale.post_code,
        homeSale.property_type,
        homeSale.strata_lot_number,
        homeSale.property_name,
        homeSale.area,
        homeSale.area_type,
        homeSale.contract_date,
        homeSale.settlement_date,
        homeSale.zoning,
        homeSale.nature_of_property,
        homeSale.primary_purpose,
        homeSale.legal_description
    );
    try (Statement stmt = this.connection.createStatement()) {
        ResultSet rs = stmt.executeQuery(sqlStr);
        while (rs.next()) {
            String insertedPropertyId = rs.getString("property_id");
            String insertedDownloadDate = rs.getString("download_date");
            String insertedCouncilName = rs.getString("council_name");
            String insertedPurchasePrice = rs.getString("purchase_price");
            String insertedAddress = rs.getString("address");
            String insertedPostCode = rs.getString("post_code");
            String insertedPropertyType = rs.getString("property_type");
            String insertedStrataLotNumber = rs.getString("strata_lot_number");
            String insertedPropertyName = rs.getString("property_name");
            String insertedArea = rs.getString("area");
            String insertedAreaType = rs.getString("area_type");
            String insertedContractDate = rs.getString("contract_date");
            String insertedSettlementDate = rs.getString("settlement_date");
            String insertedZoning = rs.getString("zoning");
            String insertedNatureOfProperty = rs.getString("nature_of_property");
            String insertedPrimaryPurpose = rs.getString("primary_purpose");
            String insertedLegalDescription = rs.getString("legal_description");
            System.out.println(
                insertedPropertyId + ", " +
                insertedDownloadDate + ", " +
                insertedCouncilName + ", " +
                insertedPurchasePrice + ", " +
                insertedAddress + ", " +
                insertedPostCode + ", " +
                insertedPropertyType + ", " +
                insertedStrataLotNumber + ", " +
                insertedPropertyName + ", " +
                insertedArea + ", " +
                insertedAreaType + ", " +
                insertedContractDate + ", " +
                insertedSettlementDate + ", " +
                insertedZoning + ", " +
                insertedNatureOfProperty + ", " +
                insertedPrimaryPurpose + ", " +
                insertedLegalDescription
            );
        }
    } catch (SQLException e) {
        System.err.println(e);
    }
    return 200;
}
    public static void main(String[] args) {
        SalesDAO salesDAO = new SalesDAO();
        HomeSale sale = new HomeSale(
            12345, "2023-10-01", "Test Council", 500000, "123 Test St", 3000,
            "House", "1A", "Test Property", 200, "sqm", "2023-09-01",
            "2023-10-15", "Residential", "1", "Primary Residence", "Lot 1"
        );
        salesDAO.newSale(sale);
    }   


   public Optional<HomeSale> getSaleById(int propertyId) {
        try {
            Statement stmt = this.connection.createStatement();
            String sqlStr = "SELECT * from sales WHERE property_id = " + propertyId;
            ResultSet rs = stmt.executeQuery(sqlStr);
            while (rs.next()) {
                return Optional.of(documentToHomeSale(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }
       return Optional.empty();
   }


   public List<HomeSale> getSalesByPostCode(int postCode) {
       List<HomeSale> result = new ArrayList<>();
       try (MongoCursor<Document> cursor = salesCollection.find(new Document("post_code", postCode)).iterator()) {
           while (cursor.hasNext()) {
                result.add(documentToHomeSale(cursor.next()));
           }
       }
       Document query = new Document("queryType", "get").append("queryDatetime", LocalDateTime.now().toString())
                .append("params", "post_code=" + postCode)
                .append("status", result != null ? 200 : 404);
       salesQueryCollection.insertOne(query);
       return result;
   }

    public int getAvgPriceByPostCode(int postCode) {
        List<HomeSale> sales = this.getSalesByPostCode(postCode);

        if (sales.isEmpty()) {
            return -1;
        }

        long totalPrice = 0;
        for (HomeSale sale : sales) {
            totalPrice += sale.purchase_price;
        }
        long result = totalPrice / sales.size();
        return (int) result;
    }

   public List<Integer> getAllSalePrices() {
        List<Integer> prices = new ArrayList<>();
        try {
            Statement stmt = this.connection.createStatement();
            String sqlStr = "SELECT purchase_price FROM sales";
            ResultSet rs = stmt.executeQuery(sqlStr);
            while (rs.next()) {
                prices.add(rs.getInt("purchase_price"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

       return prices;
   }


   public List<HomeSale> getAllSales() {
       List<HomeSale> allSales = new ArrayList<>();
       int count = 0;
       try (Statement stmt = connection.createStatement()) {
            String sqlStr = "SELECT * from sales";
            ResultSet rs = stmt.executeQuery(sqlStr);
            while (rs.next() && count < 100) {
                allSales.add(documentToHomeSale(rs));
                count++;
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
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

        try (MongoCursor<Document> cursor = salesCollection.find(new Document("purchase_price", new Document("$gte", low).append("$lte", high))).iterator()) {
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

    private HomeSale documentToHomeSale(ResultSet rs) throws SQLException {
        return new HomeSale(
                (rs.getObject("property_id") instanceof Integer ? rs.getInt("property_id") :  Integer.parseInt(rs.getString("property_id"))),
                rs.getString("download_date"),
                parseStringField(rs.getObject("council_name")),
                rs.getInt("purchase_price"),
                rs.getString("address"),
                rs.getInt("post_code"),
                rs.getString("property_type"),
                (rs.getObject("strata_lot_number") instanceof Integer ? Integer.toString(rs.getInt("strata_lot_number")) : rs.getString("strata_lot_number")),
                rs.getString("property_name"),
                parseAreaField(rs.getObject("area")),
                rs.getString("area_type"),
                rs.getString("contract_date"),
                rs.getString("settlement_date"),
                rs.getString("zoning"),
                (rs.getObject("nature_of_property") instanceof Integer ? Integer.toString(rs.getInt("nature_of_property")) : rs.getString("nature_of_property")),    
                rs.getString("primary_purpose"),
                rs.getString("legal_description")
        );
        
    }

}


