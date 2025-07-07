package app;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import sales.SalesController;
import sales.SalesDAO;
import logs.QueryDAO;
import logs.QueryController;

public class REServer {
        public static void main(String[] args) {

            // in memory test data store
            var sales = new SalesDAO();
            var queries = new QueryDAO();

            // API implementation
            SalesController salesHandler = new SalesController(sales);
            QueryController queryHandler = new QueryController(queries);

            // start Javalin on port 7070
            var app = Javalin.create()
                    .get("/", ctx -> ctx.result("Real Estate server is running"))
                    .start(7070);

            // configure endpoint handlers to process HTTP requests
            JavalinConfig config = new JavalinConfig();
            config.router.apiBuilder(() -> {
                // Sales records are immutable hence no PUT and DELETE

                // return a sale by sale ID
                app.get("/sales/{saleID}", ctx -> {
                    salesHandler.getSaleByID(ctx, ctx.pathParam("saleID"));
                });
                // get all sales records - could be big!
                app.get("/sales", ctx -> {
                    salesHandler.getAllSales(ctx);
                });
                // create a new sales record
                app.post("/sales", ctx -> {
                    salesHandler.createSale(ctx);
                });
                // Get all sales for a specified postcode
                app.get("/sales/postcode/{postcode}", ctx -> {
                    salesHandler.findSaleByPostCode(ctx, ctx.pathParam("postcode"));
                });

                // Get all sales sorted by price in ascending order
                app.get("/sort/price", ctx -> {
                    salesHandler.sortSalesByPrice(ctx);
                });

                // Get all sales sorted by price per area in ascending order 
                app.get("/sort/price-per-area", ctx -> {
                    salesHandler.sortSalesByPricePerArea(ctx);
                });
                // Get the average sale price for a specified postcode
                app.get("/sales/postcode/{postcode}/average", ctx -> {
                    salesHandler.getAvgPriceByPostCode(ctx, ctx.pathParam("postcode"));
                });        
                app.get("/sales/price/{low}/{high}", ctx -> {
                    int low = Integer.parseInt(ctx.pathParam("low"));
                    int high = Integer.parseInt(ctx.pathParam("high"));
                    salesHandler.getSalesByPriceRange(ctx, low, high);
                });

                app.get("/metrics/postcode-count/{postcode}" , ctx -> {
                    int postcode = Integer.parseInt(ctx.pathParam("postcode"));
                    queryHandler.getPostcodeCount(ctx, postcode);
                });

                app.get("/metrics/property-count/{saleID}", ctx -> {
                    int saleID = Integer.parseInt(ctx.pathParam("saleID"));
                    queryHandler.getPropertyCount(ctx, saleID);
                });
            });
        }
}


