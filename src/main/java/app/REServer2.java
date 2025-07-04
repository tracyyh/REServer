package app;

import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import myopenai.user.UserController;
import sales.SalesDAO;
import sales.SalesController;

import static io.javalin.apibuilder.ApiBuilder.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class REServer2 {

    private static final Logger LOG = LoggerFactory.getLogger(REServer.class);

    public static void main(String[] args) {

        // In-memory test data store
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

                // User routes
                path("users", () -> {
                    get(UserController::getAll);
                    post(UserController::create);
                    path("{userId}", () -> {
                        get(UserController::getOne);
                        patch(UserController::update);
                        delete(UserController::delete);
                    });
                });

                // // Sales routes
                // path("sales", () -> {
                //     get(ctx -> salesHandler.getAllSales(ctx));
                //     post(ctx -> salesHandler.createSale(ctx));

                //     path("{saleID}", () -> {
                //         get(ctx -> salesHandler.getSaleByID(ctx, ctx.pathParam("saleID")));
                //     });

                //     path("postcode/{postcode}", () -> {
                //         get(ctx -> salesHandler.findSaleByPostCode(ctx, ctx.pathParam("postcode")));
                //     });
                // });
            });
        }).start(7002);

        LOG.info("Real Estate server started at http://localhost:7002");
        LOG.info("Swagger UI available at http://localhost:7002/swagger");
    }
}
