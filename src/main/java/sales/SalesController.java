package sales;

import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
        methods = HttpMethod.POST,
        tags = {"Sales"},
        requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = HomeSale.class)}),
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
        summary = "Get all sales",
        operationId = "getAllSales",
        path = "/sales",
        methods = HttpMethod.GET,
        tags = {"Sales"},
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
        summary = "Get sale by ID",
        operationId = "getSaleByID",
        path = "/sales/{saleID}",
        methods = HttpMethod.GET,
        tags = {"Sales"},
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
        summary = "Find sale by post code",
        operationId = "findSaleByPostcode",
        path = "/sales/postcode/{postcode}",
        methods = HttpMethod.GET,
        tags = {"Sales"},
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

    @OpenApi(
        summary = "Get sales by price range",
        operationId = "pricerange",
        path = "/sales/price/{low}/{high}",
        methods = HttpMethod.GET,
        tags = {"Sales"},
        responses = {
            @OpenApiResponse(status = "201"),
            @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
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

    @OpenApi(
        summary = "Get average price by postcode",
        operationId = "pricerange",
        path = "/sales/postcode/{postcode}/average",
        methods = HttpMethod.GET,
        tags = {"Price"},
        responses = {
            @OpenApiResponse(status = "201"),
            @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public void getAvgPriceByPostCode(Context ctx, String postCode) {
        int avgPrice = homeSales.getAvgPriceByPostCode(Integer.parseInt(postCode));
        if (avgPrice == -1) {
            ctx.result("No sales for postcode found");
            ctx.status(404);
        } else {
            ctx.json(avgPrice);
            ctx.status(200);
        }
    }

    @OpenApi(
        summary = "Sort sales by price",
        operationId = "pricerange",
        path = "/sort/price",
        methods = HttpMethod.GET,
        tags = {"Price"},
        responses = {
            @OpenApiResponse(status = "201"),
            @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public void sortSalesByPrice(Context ctx) {
        List <HomeSale> sortedSales = homeSales.getAllSales();
        sortedSales.sort((HomeSale sale1, HomeSale sale2) -> sale1.getPurchase_price() - sale2.getPurchase_price());
        if (sortedSales.isEmpty()) {
            ctx.result("No Sales Found");
            ctx.status(404);
        } else {
            ctx.json(sortedSales);
            ctx.status(200);
        }
    }

    @OpenApi(
        summary = "Sort sales by price per area",
        operationId = "pricerange",
        path = "/sort/price-per-area",
        methods = HttpMethod.GET,
        tags = {"Price"},
        responses = {
            @OpenApiResponse(status = "201"),
            @OpenApiResponse(status = "400", content = {@OpenApiContent(from = ErrorResponse.class)})
        }
    )
    public void sortSalesByPricePerArea(Context ctx) {
        List <HomeSale> allSales = homeSales.getAllSales();
        Iterator<HomeSale> salesIterator = allSales.iterator();

        List<HomeSale> salesWithArea = new ArrayList<>();
        
        while (salesIterator.hasNext()) {
            HomeSale sale = salesIterator.next();
            if (sale.getArea() != 0) {
                salesWithArea.add(sale);
            } 
        }
        
        salesWithArea.sort((HomeSale sale1, HomeSale sale2) -> {
            int sale1PricePerArea = sale1.getPurchase_price()/sale1.getArea();
            int sale2PricePerArea = sale2.getPurchase_price()/sale2.getArea();
            return sale1PricePerArea - sale2PricePerArea;
        });

        if (salesWithArea.isEmpty()) {
            ctx.result("No Sales Found");
            ctx.status(404);
        } else {
            ctx.json(salesWithArea);
            ctx.status(200);
        }
    }

    private Context error(Context ctx, String msg, int code) {
        ctx.result(msg);
        ctx.status(code);
        return ctx;
    }



    


}
