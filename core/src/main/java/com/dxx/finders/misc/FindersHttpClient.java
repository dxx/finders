package com.dxx.finders.misc;

import com.dxx.finders.constant.Loggers;
import com.dxx.finders.util.StringUtils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * HTTP client.
 *
 * @author dxx
 */
public class FindersHttpClient {

    private static final Vertx vertx = Vertx.vertx();

    private static final int CONNECT_TIMEOUT = 5000;

    private static final int RESPONSE_TIMEOUT = 10000;

    private static final int IDLE_TIME = 10;

    private static final int POOl_SIZE = 50;

    private static final int POOL_CLEANER_PERIOD = 120000;

    private static final WebClientOptions CLIENT_OPTIONS = new WebClientOptions();

    static {
        CLIENT_OPTIONS.setConnectTimeout(CONNECT_TIMEOUT)
                .setIdleTimeout(IDLE_TIME)
                .setMaxPoolSize(POOl_SIZE)
                .setPoolCleanerPeriod(POOL_CLEANER_PERIOD);
    }

    public static String get(String url) {
        WebClient webClient = WebClient.create(vertx, CLIENT_OPTIONS);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<String> reference = new AtomicReference<>();
        webClient.getAbs(url).timeout(RESPONSE_TIMEOUT).send().onSuccess(response -> {
            if (response.statusCode() != HttpResponseStatus.OK.code()) {
                if (Loggers.HTTP_CLIENT.isDebugEnabled()) {
                    Loggers.HTTP_CLIENT.debug("{} {} response status {} {}", "GET", url,
                            response.statusCode(), response.statusMessage());
                }
                countDownLatch.countDown();
                return;
            }
            reference.set(response.bodyAsString());
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Loggers.HTTP_CLIENT.error("Await response error: ", e);
        }
        return reference.get();
    }

    public static String get(String url, Map<String, String> queryParams) {
        List<String> paramList = new ArrayList<>();
        for (String key : queryParams.keySet()) {
            String value = queryParams.get(key);
            try {
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Loggers.HTTP_CLIENT.error("Encode param error: ", e);
            }
        }
        if (StringUtils.isNotEmpty(url) && paramList.size() > 0) {
            String param = String.join("&", paramList.toArray(new String[0]));
            if (url.indexOf("?") > 0) {
                url += url.endsWith("&") ? param : "&" + param;
            } else {
                url += "?" + param;
            }
        }
        return get(url);
    }

    public static String post(String url) {
        return post(url, null);
    }

    public static String post(String url, String body) {
        WebClient webClient = WebClient.create(vertx, CLIENT_OPTIONS);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<String> reference = new AtomicReference<>();
        HttpRequest<Buffer> request = webClient.postAbs(url).timeout(RESPONSE_TIMEOUT);
        Future<HttpResponse<Buffer>> future = request.send();
        if (StringUtils.isNotEmpty(body)) {
            future = request.sendBuffer(Buffer.buffer(body));
        }
        future.onSuccess(response -> {
            if (response.statusCode() != HttpResponseStatus.OK.code()) {
                if (Loggers.HTTP_CLIENT.isDebugEnabled()) {
                    Loggers.HTTP_CLIENT.debug("{} {} response status {} {}", "POST", url,
                            response.statusCode(), response.statusMessage());
                }
                countDownLatch.countDown();
                return;
            }
            reference.set(response.bodyAsString());
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Loggers.HTTP_CLIENT.error("Await response error: ", e);
        }
        return reference.get();
    }

    public static void asyncGet(String url, AsyncHttpCallback<String> callback) {
        WebClient webClient = WebClient.create(vertx, CLIENT_OPTIONS);
        webClient.getAbs(url).timeout(RESPONSE_TIMEOUT).send().onSuccess(response -> {
            if (response.statusCode() != HttpResponseStatus.OK.code()) {
                if (Loggers.HTTP_CLIENT.isDebugEnabled()) {
                    Loggers.HTTP_CLIENT.debug("{} {} response status {} {}", "GET", url,
                            response.statusCode(), response.statusMessage());
                }
                callback.onSuccess(null);
                return;
            }
            callback.onSuccess(response.bodyAsString());
        }).onFailure(e -> {
            Loggers.HTTP_CLIENT.error("GET " + url + " response error: ", e);

            callback.onError(e);
        });
    }

    public static void asyncPost(String url, String body, AsyncHttpCallback<String> callback) {
        WebClient webClient = WebClient.create(vertx, CLIENT_OPTIONS);
        HttpRequest<Buffer> request = webClient.postAbs(url).timeout(RESPONSE_TIMEOUT);
        Future<HttpResponse<Buffer>> future = request.send();
        if (StringUtils.isNotEmpty(body)) {
            future = request.sendBuffer(Buffer.buffer(body));
        }
        future.onSuccess(response -> {
            if (response.statusCode() != HttpResponseStatus.OK.code()) {
                if (Loggers.HTTP_CLIENT.isDebugEnabled()) {
                    Loggers.HTTP_CLIENT.debug("{} {} response status {} {}", "POST", url,
                            response.statusCode(), response.statusMessage());
                }
                callback.onSuccess(null);
                return;
            }
            callback.onSuccess(response.bodyAsString());
        });
        future.onFailure(e -> {
            Loggers.HTTP_CLIENT.error("POST " + url + " response error: ", e);

            callback.onError(e);
        });
    }

}
