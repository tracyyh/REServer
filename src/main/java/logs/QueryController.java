package logs;

import io.javalin.http.Context;

public class QueryController {

    private final QueryDAO salesQueries;

    public QueryController(QueryDAO salesQueries) {
        this.salesQueries = salesQueries;
    }

    // Implements GET /metrics/postcode-count/{postcode}
    public void getPostcodeCount(Context ctx, int postcode) {
        int count = salesQueries.getPostcodeCount(postcode);
        if (count == -1) {
            ctx.result("No sales for postcode found");
            ctx.status(404);
        } else {
            ctx.json(count);
            ctx.status(200);
        }
    }

    // Implements GET /metrics/property-count/{saleID}
    public void getPropertyCount(Context ctx, int saleID) {
        int count = salesQueries.getPropertyCount(saleID);
        if (count == -1) {
            ctx.result("Sale not found");
            ctx.status(404);
        } else {
            ctx.json(count);
            ctx.status(200);
        }
    }
}