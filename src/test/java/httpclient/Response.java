package httpclient;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Response extends ValueType {
    public final String body;
    public final int statusCode;
    private final String protocol;
    private final Headers headers;

    Response(String body, int statusCode, String protocol, Headers headers) {
        this.body = body;
        this.statusCode = statusCode;
        this.protocol = protocol;
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    // Which is better??
    public String getContentType() {
        return headers.toString().split("\n")[3];
//        String[] s = headers.toString().split("\n");
//        return Arrays.stream(s)
//                .filter(x -> "Content-Type: application/json".equals(x))
//                .map(x -> x.split(" ")[1])
//                .collect(Collectors.joining());
    }

    public static Response fromApacheResponse(HttpResponse response) {
        try {
            return new Response(EntityUtils.toString(response.getEntity()), response.getStatusLine().getStatusCode(), response.getProtocolVersion().toString(), Headers.fromApacheHeaders(response.getAllHeaders()));
        } catch (IOException exception) {
            throw new RuntimeException("TODO make sure that then response has a proper format", exception);
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s%n%s%n%n%s", protocol, statusCode, headers, body);
    }
}
