package app;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.redoc.ReDocPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import myopenai.user.UserController;
import sales.SalesDAO;
import sales.SalesController;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.patch;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class REServer {
        private static final Logger LOG = LoggerFactory.getLogger(REServer.class);

        public static void main(String[] args) {

            // in memory test data store
            var sales = new SalesDAO();

            // API implementation
            SalesController salesHandler = new SalesController(sales);

            // start Javalin on port 7070
            // var app = Javalin.create()
            //         .get("/", ctx -> ctx.result("Real Estate server is running"))
            //         .start(7070);

            var app = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
                pluginConfig.withDefinitionConfiguration((version, definition) -> {
                    definition.withOpenApiInfo(info -> info.setTitle("Javalin OpenAPI example"));
                });
            }));
            config.registerPlugin(new SwaggerPlugin());
            //config.registerPlugin(new ReDocPlugin());
            config.registerPlugin(new SwaggerPlugin());
            config.router.apiBuilder(() -> {
                path("users", () -> {
                    get(UserController::getAll);
                    post(UserController::create);
                    path("{userId}", () -> {
                        get(UserController::getOne);
                        patch(UserController::update);
                        delete(UserController::delete);
                    });
                });

                // path("sales", () -> {
                //     get(SalesController::getAllSales);
                //     post(SalesController::createSale);
                //     path("{saleID}", () -> {
                //         get(SalesController::getSaleByID);
                //     });
                //     path("postcode/{postcode}", () -> {
                //         get(SalesController::findSaleByPostCode);
                //     });
                // });
            });
        }).start(7002);

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
            });


        }
}

