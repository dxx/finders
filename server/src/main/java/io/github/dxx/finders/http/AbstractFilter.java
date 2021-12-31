package io.github.dxx.finders.http;

import io.vertx.ext.web.RoutingContext;

/**
 * Abstract filter.
 *
 * @author dxx
 */
public abstract class AbstractFilter implements Filter {

    private AbstractFilter next;

    @Override
    public boolean fireDoFilter(RoutingContext context) {
        if (next != null) {
            return next.doFilter(context);
        }
        return true;
    }

    public AbstractFilter getNext() {
        return next;
    }

    public void setNext(AbstractFilter next) {
        this.next = next;
    }
}
