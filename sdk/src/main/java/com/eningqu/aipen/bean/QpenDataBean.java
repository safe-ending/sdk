package com.eningqu.aipen.bean;

import java.util.List;

public class QpenDataBean {
    private String notebook_id;
    private String notebook_name;
    private String cover_id;
    private String book_no;
    private String notebook_width;
    private String notebook_height;
    private List<PageSBean> pages;

    public String getNotebook_id() {
        return notebook_id;
    }

    public void setNotebook_id(String notebook_id) {
        this.notebook_id = notebook_id;
    }

    public String getNotebook_name() {
        return notebook_name;
    }

    public void setNotebook_name(String notebook_name) {
        this.notebook_name = notebook_name;
    }

    public String getCover_id() {
        return cover_id;
    }

    public void setCover_id(String cover_id) {
        this.cover_id = cover_id;
    }

    public String getBook_no() {
        return book_no;
    }

    public void setBook_no(String book_no) {
        this.book_no = book_no;
    }

    public String getNotebook_width() {
        return notebook_width;
    }

    public void setNotebook_width(String notebook_width) {
        this.notebook_width = notebook_width;
    }

    public String getNotebook_height() {
        return notebook_height;
    }

    public void setNotebook_height(String notebook_height) {
        this.notebook_height = notebook_height;
    }

    public List<PageSBean> getPages() {
        return pages;
    }

    public void setPages(List<PageSBean> pages) {
        this.pages = pages;
    }

    public static class PageSBean {
        private String page_no;
        private String page_name;
        private String create_at;

        public String getPage_no() {
            return page_no;
        }

        public void setPage_no(String page_no) {
            this.page_no = page_no;
        }

        public String getPage_name() {
            return page_name;
        }

        public void setPage_name(String page_name) {
            this.page_name = page_name;
        }

        public String getCreate_at() {
            return create_at;
        }

        public void setCreate_at(String create_at) {
            this.create_at = create_at;
        }
    }
}
