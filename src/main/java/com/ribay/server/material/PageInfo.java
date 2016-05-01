package com.ribay.server.material;

/**
 * Created by CD on 01.05.2016.
 */
public class PageInfo {

    private int page_no;
    private int page_size;

    public int getPage_no() {
        return page_no;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getStart() {
        return (page_no - 1) * page_size;
    }

}
