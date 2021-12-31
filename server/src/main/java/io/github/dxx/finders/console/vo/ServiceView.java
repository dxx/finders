package io.github.dxx.finders.console.vo;

import java.util.List;

/**
 * Service view.
 *
 * @author dxx
 */
public class ServiceView {

    private int page;

    private int size;

    private int count;

    private List<ServiceInfo> serviceList;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ServiceInfo> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<ServiceInfo> serviceList) {
        this.serviceList = serviceList;
    }
}
