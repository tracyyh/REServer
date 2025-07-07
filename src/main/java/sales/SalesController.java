package sales;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


import io.javalin.http.Context;

public class SalesController {

    private final SalesDAO homeSales;

    public SalesController(SalesDAO homeSales) {
        this.homeSales = homeSales;
    }

    // implements POST /sales
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

    // implements Get /sales
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

    // implements GET /sales/{saleID}
    public void getSaleByID(Context ctx, String id) {

        Optional<HomeSale> sale = homeSales.getSaleById(Integer.parseInt(id));
        sale.map(ctx::json)
                .orElseGet (() -> error (ctx, "Sale not found", 404));

    }

    // Implements GET /sales/postcode/{postcodeID}
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
            if (salesInRange.isEmpty()) {
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

    // Implements GET /sales/postcode/{postcodeID}/average
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

    // Implements GET /sort/price
    public void sortSalesByPrice(Context ctx) {
        List <HomeSale> sortedSales = homeSales.getAllSales();
        sortedSales.sort((HomeSale sale1, HomeSale sale2) -> sale1.purchase_price - sale2.purchase_price);
        if (sortedSales.isEmpty()) {
            ctx.result("No Sales Found");
            ctx.status(404);
        } else {
            ctx.json(sortedSales);
            ctx.status(200);
        }
    }

    // Implements GET /sort/price-per-area
    public void sortSalesByPricePerArea(Context ctx) {
        List <HomeSale> allSales = homeSales.getAllSales();
        Iterator<HomeSale> salesIterator = allSales.iterator();

        List<HomeSale> salesWithArea = new ArrayList<>();
        
        while (salesIterator.hasNext()) {
            HomeSale sale = salesIterator.next();
            if (sale.area != 0) {
                salesWithArea.add(sale);
            } 
        }
        
        salesWithArea.sort((HomeSale sale1, HomeSale sale2) -> {
            int sale1PricePerArea = sale1.purchase_price/sale1.area;
            int sale2PricePerArea = sale2.purchase_price/sale2.area;
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

    // Implements GET /sales/price/{low}/{high}
    public void getSalesByPriceRange(Context ctx, int low, int high) {
        List<HomeSale> salesInRange = homeSales.getSalesByPriceRange(low, high);
        if (salesInRange.isEmpty()) {
            ctx.result("No sales found in the specified price range");
            ctx.status(404);
        } else {
            ctx.json(salesInRange);
            ctx.status(200);
        }
    }

    private Context error(Context ctx, String msg, int code) {
        ctx.result(msg);
        ctx.status(code);
        return ctx;
    }
}
