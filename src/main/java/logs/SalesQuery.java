package logs;

import java.time.LocalDateTime;

public class SalesQuery {
    private String queryType;
    private LocalDateTime queryDateTime;
    private String params;
    private int status;

    public SalesQuery() {
        // Default constructor
    }

    public SalesQuery(String queryType, LocalDateTime queryDateTime, String params, int status) {
        this.queryType = queryType;
        this.queryDateTime = queryDateTime;
        this.params = params;
        this.status = status;
    }
}
