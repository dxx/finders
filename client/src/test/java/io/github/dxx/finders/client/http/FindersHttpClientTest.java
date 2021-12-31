package io.github.dxx.finders.client.http;

import org.junit.jupiter.api.Test;

/**
 * FindersHttpClient Test
 *
 * @author dxx
 */
public class FindersHttpClientTest {

    @Test
    public void testGet() {
        String res = FindersHttpClient.get("https://httpbin.org/get", null);
        System.out.println(res);
    }

    @Test
    public void testPost() {
        String res = FindersHttpClient.post("https://httpbin.org/post","post data", null);
        System.out.println(res);
    }

    @Test
    public void testPut() {
        String res = FindersHttpClient.put("https://httpbin.org/put", "put data", null);
        System.out.println(res);
    }

}
