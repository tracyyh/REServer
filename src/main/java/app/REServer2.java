package app;

import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import sales.SalesDAO;
import sales.SalesController;

import static io.javalin.apibuilder.ApiBuilder.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class REServer2 {

    private static final Logger LOG = LoggerFactory.getLogger(REServer2.class);

    public static void main(String[] args) {

        // MongoDB-backed data store
        var sales = new SalesDAO();

        // API implementation
        SalesController salesHandler = new SalesController(sales);

        // Start Javalin with OpenAPI and Swagger configured
        var app = Javalin.create(config -> {

            // OpenAPI Documentation
            config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
                pluginConfig.withDefinitionConfiguration((version, definition) -> {
                    definition.withOpenApiInfo(info -> info.setTitle("Real Estate API"));
                });
            }));

            // Swagger UI
            config.registerPlugin(new SwaggerPlugin());

            // API routes
            config.router.apiBuilder(() -> {
                // Sales records are immutable hence no PUT and DELETE

                // return a sale by sale ID
                get("/sales/{saleID}", ctx -> {
                    salesHandler.getSaleByID(ctx, ctx.pathParam("saleID"));
                });

                get("/", ctx -> {
                    ctx.result("Real Estate server is running");
                });

                // get all sales records - could be big!
                get("/sales", ctx -> {
                    salesHandler.getAllSales(ctx);
                });

                // create a new sales record
                post("/sales", ctx -> {
                    salesHandler.createSale(ctx);
                });

                // Get all sales for a specified postcode
                get("/sales/postcode/{postcode}", ctx -> {
                    salesHandler.findSaleByPostCode(ctx, ctx.pathParam("postcode"));
                });

                // Get all sales in a price range
                get("/sales/price/{low}/{high}", ctx -> {
                    int low = Integer.parseInt(ctx.pathParam("low"));
                    int high = Integer.parseInt(ctx.pathParam("high"));
                    ctx.json(sales.getSalesByPriceRange(low, high));
                });

                // Get the average sale price for a specified postcode
                get("/sales/postcode/{postcode}/average", ctx -> {
                    salesHandler.getAvgPriceByPostCode(ctx, ctx.pathParam("postcode"));
                });
            });
        });

        // Start server
        app.start(7002);

        LOG.info("Real Estate server started at http://localhost:7002");
        LOG.info("Swagger UI available at http://localhost:7002/swagger");
    }
}
