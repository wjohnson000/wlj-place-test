/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.ws.httpclient;

import java.util.Collections;
import java.util.Map;

/**
 * Simple HTTP response wrapper that contains vital information about an HTTP request.
 * 
 * @author wjohnson000
 *
 */
public class WebResponse {

    private int status;
    private String body;
    private Map<String, String> headers;
    private Exception exception;

    public WebResponse(int status, String body, Map<String, String> headers) {
        this(status, body, headers, null);
    }

    public WebResponse(int status, String body, Map<String, String> headers, Exception exception) {
        this.status = status;
        this.body   = body;
        if (headers == null) {
            this.headers = Collections.emptyMap();
        } else {
            this.headers = Collections.unmodifiableMap(headers);
        }
        this.exception = exception;
    }

    public boolean isOK() {
        return exception == null;
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Exception getException() {
        return exception;
    }
}
