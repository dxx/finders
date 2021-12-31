package io.github.dxx.finders.http;

import io.github.dxx.finders.constant.Loggers;

import java.util.ServiceLoader;

/**
 * Filter builder.
 *
 * @author dxx
 */
public class FilterBuilder {

    public static AbstractFilter build() {
        DefaultFilterChain filterChain = new DefaultFilterChain();
        try {
            // Use spi to load all filter
            ServiceLoader<Filter> serviceLoader = ServiceLoader.load(Filter.class);
            for (Filter filter : serviceLoader) {
                filterChain.addLast((AbstractFilter) filter);
            }
        } catch (Throwable t) {
            Loggers.CORE.error("ERROR: build filter chain failed", t);
        }
        return filterChain;
    }

}
