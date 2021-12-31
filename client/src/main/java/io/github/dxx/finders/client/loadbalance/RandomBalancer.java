package io.github.dxx.finders.client.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Random balancer.
 *
 * @author dxx
 */
public class RandomBalancer implements LoadBalancer {

    private final List<String> serverList;

    public RandomBalancer(List<String> serverList) {
        this.serverList = serverList;
    }

    @Override
    public String chooseServer() {
        synchronized (this) {
            return this.serverList.get(randomInt(this.serverList.size()));
        }
    }

    private int randomInt(int serverCount) {
        return ThreadLocalRandom.current().nextInt(serverCount);
    }

}
