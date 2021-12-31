package io.github.dxx.finders.client.http;

import io.github.dxx.finders.client.FindersRuntimeException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Finders HTTP client.
 *
 * @author dxx
 */
public class FindersHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindersHttpClient.class);

    private static final int CONNECT_TIMEOUT = 2000;

    private static final int READ_TIMEOUT = 3000;

    private static final int MAX_IDLE_CONNECTIONS = 10;

    private static final int KEEP_ALIVE_DURATION = 5000;

    private static final OkHttpClient OK_HTTP_CLIENT;

    static {
        ConnectionPool connectionPool = new ConnectionPool(
                MAX_IDLE_CONNECTIONS,
                KEEP_ALIVE_DURATION, TimeUnit.MILLISECONDS);

        OK_HTTP_CLIENT = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofMillis(CONNECT_TIMEOUT))
                .readTimeout(Duration.ofMillis(READ_TIMEOUT))
                .connectionPool(connectionPool)
                .build();
    }

    public static String get(String url, Map<String, String> header) {
        return request(url, HttpMethod.GET, null, header);
    }

    public static String post(String url, String body, Map<String, String> header) {
        return request(url, HttpMethod.POST, body, header);
    }

    public static String put(String url, String body, Map<String, String> header) {
        return request(url, HttpMethod.PUT, body, header);
    }

    public static String delete(String url, String body, Map<String, String> header) {
        return request(url, HttpMethod.DELETE, body, header);
    }

    public static String request(String url, HttpMethod method, String body, Map<String, String> header) {
        Request.Builder builder = new Request.Builder().url(url);
        if (header != null) {
            Headers.Builder headerBuilder = new Headers.Builder();
            header.forEach(headerBuilder::set);
            builder = builder.headers(headerBuilder.build());
        }
        if (body != null && !body.equals("")) {
            RequestBody requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), body);
            switch (method) {
                case POST:
                    builder = builder.post(requestBody);
                    break;
                case PUT:
                    builder = builder.put(requestBody);
                    break;
                case DELETE:
                    builder = builder.delete(requestBody);
                    break;
                default:
                    throw new FindersRuntimeException("Unsupported method " + method);
            }
        }

        Request request = builder.build();
        try {
            Response response = OK_HTTP_CLIENT.newCall(request).execute();
            if (response.code() == 200 && response.body() != null) {
                return response.body().string();
            }
            LOGGER.debug("{} {} response status {} {}", method, url,
                    response.code(), response.message());
        } catch (Exception e) {
            throw new FindersRuntimeException(e);
        }
        return null;
    }
}
