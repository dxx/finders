package com.dxx.finders.client.loadbalance;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * RandomBalancer Test.
 *
 * @author dxx
 */
public class RandomBalancerTest {

    @Test
    public void test() {
        List<String> serverList = new ArrayList<>();
        serverList.add("127.0.0.1:8000");
        serverList.add("127.0.0.1:8001");
        serverList.add("127.0.0.1:8002");
        serverList.add("127.0.0.1:8003");
        serverList.add("127.0.0.1:8004");
        serverList.add("127.0.0.1:8005");
        serverList.add("127.0.0.1:8006");
        serverList.add("127.0.0.1:8007");
        serverList.add("127.0.0.1:8008");
        serverList.add("127.0.0.1:8009");
        RandomBalancer randomBalancer = new RandomBalancer(serverList);

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(randomBalancer.chooseServer());
            }).start();
        }
    }

}
