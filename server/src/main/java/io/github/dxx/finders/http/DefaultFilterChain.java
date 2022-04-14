package io.github.dxx.finders.http;

import io.vertx.ext.web.RoutingContext;

/**
 * The default filter chain.
 *
 * @author dxx
 */
public class DefaultFilterChain extends AbstractFilter {

    private final AbstractFilter first = new AbstractFilter() {

        @Override
        public boolean doFilter(RoutingContext context) {
            return super.fireDoFilter(context);
        }

    };

    private AbstractFilter last = first;

    public void addFirst(AbstractFilter abstractFilter) {
        if (abstractFilter != null) {
            abstractFilter.setNext(first.getNext());
            first.setNext(abstractFilter);
            if (last == first) {
                last = abstractFilter;
            }
        }
    }

    public void addLast(AbstractFilter abstractFilter) {
        if (abstractFilter != null) {
            last.setNext(abstractFilter);
            last = abstractFilter;
        }
    }

    @Override
    public boolean doFilter(RoutingContext context) {
        return first.doFilter(context);
    }

}
