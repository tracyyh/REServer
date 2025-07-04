package sales;

import io.javalin.http.Context;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;

import myopenai.ErrorResponse;
import myopenai.user.NewUserRequest;
import java.util.List;
import java.util.Optional;

import myopenai.ErrorResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.openapi.*;

public class SalesController {

    private SalesDAO homeSales;

    public SalesController(SalesDAO homeSales) {
        this.homeSales = homeSales;
    }

    @OpenApi(
        summary = "Create sale",
        operationId = "createSale",
        path = "/sales",
        methods = HttpMethod.GET,
        tags = {"Sale"},
        requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewUserRequest.class)}),
        responses = {
            @OpenApiResponse(status = "201"),
            @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public void createSale(Context ctx) {

        // Extract Home Sale from request body
        // TO DO override Validator exception method to report better error message
        HomeSale sale = ctx.bodyValidator(HomeSale.class)
                            .get();

        // store new sale in data set
        if (homeSales.newSale(sale)) {
            ctx.result("Sale Created");
            ctx.status(201);
        } else {
            ctx.result("Failed to add sale");
            ctx.status(400);
        }
    }

    @OpenApi(
        summary = "Create user",
        operationId = "createUser",
        path = "/users",
        methods = HttpMethod.POST,
        tags = {"User"},
        requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewUserRequest.class)}),
        responses = {
            @OpenApiResponse(status = "201"),
            @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public void getAllSales(Context ctx) {
        List <HomeSale> allSales = homeSales.getAllSales();
        if (allSales.isEmpty()) {
            ctx.result("No Sales Found");
            ctx.status(404);
        } else {
            ctx.json(allSales);
            ctx.status(200);
        }
    }

    @OpenApi(
        summary = "Create user",
        operationId = "createUser",
        path = "/users",
        methods = HttpMethod.POST,
        tags = {"User"},
        requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewUserRequest.class)}),
        responses = {
            @OpenApiResponse(status = "201"),
            @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public void getSaleByID(Context ctx, String id) {

        Optional<HomeSale> sale = homeSales.getSaleById(Integer.parseInt(id));
        sale.map(ctx::json)
                .orElseGet (() -> error (ctx, "Sale not found", 404));

    }

    @OpenApi(
        summary = "Create user",
        operationId = "createUser",
        path = "/users",
        methods = HttpMethod.POST,
        tags = {"User"},
        requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewUserRequest.class)}),
        responses = {
            @OpenApiResponse(status = "201"),
            @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public void findSaleByPostCode(Context ctx, String postCode) {
        List<HomeSale> sales = homeSales.getSalesByPostCode(Integer.parseInt(postCode));
        if (sales.isEmpty()) {
            ctx.result("No sales for postcode found");
            ctx.status(404);
        } else {
            ctx.json(sales);
            ctx.status(200);
        }
    }

    public void getSalesByPriceRange(Context ctx, String low, String high) {
        try {
            int lowPrice = Integer.parseInt(low);
            int highPrice = Integer.parseInt(high);
            List<HomeSale> salesInRange = homeSales.getSalesByPriceRange(lowPrice, highPrice);
            if (salesInRange.size() == 0) {
                ctx.result("No sales found in the specified price range");
                ctx.status(404);
            } else {
                ctx.json(salesInRange);
                ctx.status(200);
            }
        } catch (NumberFormatException e) {
            error(ctx, "Invalid price range format", 400);
        }
    }

    private Context error(Context ctx, String msg, int code) {
        ctx.result(msg);
        ctx.status(code);
        return ctx;
    }



    


}
