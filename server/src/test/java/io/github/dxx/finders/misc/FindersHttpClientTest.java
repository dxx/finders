package io.github.dxx.finders.misc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * HTTP client test.
 *
 * @author dxx
 */
public class FindersHttpClientTest {

    @Test
    public void testGet() {
        Map<String, String> params = new HashMap<>();
        params.put("p1", "?");
        params.put("p2", "2");
        String res = FindersHttpClient.get("https://httpbin.org/get", params);
        System.out.println(res);
    }

    @Test
    public void testPost() {
        String res = FindersHttpClient.post("https://httpbin.org/post", "111");
        System.out.println(res);
    }

    @Test
    public void testPostEmpty() {
        String res = FindersHttpClient.post("https://httpbin.org/post", "");
        System.out.println(res);
    }

    @Test
    public void testAsyncGet() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        FindersHttpClient.asyncGetRequest("https://httpbin.org/get", new AsyncHttpCallback<String>() {
            @Override
            public void onSuccess(String s) {
                System.out.println(s);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.err.println(e);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    @Test
    public void testAsyncPost() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        FindersHttpClient.asyncPostRequest("https://httpbin.org/post", "", new AsyncHttpCallback<String>() {
            @Override
            public void onSuccess(String s) {
                System.out.println(s);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.err.println(e);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

}
