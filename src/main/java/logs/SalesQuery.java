package logs;

public class SalesQuery {
    private int queryID;
    private String queryType;
    private String queryDatetime;
    private String params;
    private int status;

    public SalesQuery() {
        // Default constructor
    }

    public SalesQuery(int queryID, String queryType, String queryDatetime, String params, int status) {
        this.queryID = queryID;
        this.queryType = queryType;
        this.queryDatetime = queryDatetime;
        this.params = params;
        this.status = status;
    }
}
