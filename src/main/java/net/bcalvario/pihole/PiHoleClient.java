import java.net.URI;
import java.net.http.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

/*
Brandon Calvario
PiHoleClient.java
uses Pi-hole v1 REST API and deploys tasks, enables/disables global DNS blocking and can provide a timed blocking status,
returns a parsed JSON tree containing the servers current status.
 */
public final class PiHoleClient {
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();
    private final String baseUrl, token;

    public PiHoleClient(String host, String token) {
        this.baseUrl = host.endsWith("/") ? host : host + "/";
        this.token = token;
    }

    public void toggleBlock(boolean enable, int sec) throws Exception {
        String path = enable ? "control/blocking/enable" : "control/blocking/disable";
        if (!enable && sec > 0)
            path += "/" + sec;
        send("POST", path, null);
    }

    public void addToBlockList(String domain) throws Exception {
        ObjectNode body = json.createObjectNode()
                .put("domain", domain)
                .put("type", "block")
                .put("exact", true);
        send("POST", "domain", body.toString());
    }

    public JsonNode stats() throws Exception {
        return json.readTree(send("GET", "metrics/summary", null));
    }

    private String send(String method, String path, String body) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "api/v1/" + path))
                .header("bcalvarioapikey1", token);
        if (body != null)
            b.method(method, HttpRequest
                            .BodyPublishers
                            .ofString(body))
                    .header("Content-Type", "application/json");
        else
            b.method(method, HttpRequest
                    .BodyPublishers
                    .noBody());

        HttpResponse<String> rsp = http.send(b.build(),
                HttpResponse
                        .BodyHandlers
                        .ofString());
        if (rsp.statusCode() >= 300)
            throw new IllegalStateException(rsp.statusCode() + " " + rsp.body());
        return rsp.body();

    }
}

