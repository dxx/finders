package com.dxx.finders.handler;

import com.dxx.finders.http.RequestMethod;
import com.dxx.finders.http.annotation.RequestMapping;

/**
 * Service handler.
 *
 * @author dxx
 */
public class ServiceHandler {

    @RequestMapping(path = "", method = RequestMethod.PUT)
    public void sync() {

    }
}
