package com.eningqu.aipen.bean;

import java.util.List;

/**
 * @author wtj
 * @filename MSBean
 * @date 2019/9/5
 * @email wtj@eningqu.com
 **/
public class MSBean {


    /**
     * language : en-US
     * strokes : [{"id":43,"points":"5.1365, 12.3845,         4.9534, 12.1301,         4.8618, 12.1199,         4.7906, 12.2217,         4.7906, 12.5372,         4.8211, 12.9849,         4.9534, 13.6667,         5.0958, 14.4503,         5.3299, 15.2441,         5.6555, 16.0480,         ..."},"..."]
     */

    private String language;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    private int version;
    private List<StrokesBean> strokes;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<StrokesBean> getStrokes() {
        return strokes;
    }

    public void setStrokes(List<StrokesBean> strokes) {
        this.strokes = strokes;
    }

    public static class StrokesBean {
        /**
         * id : 43
         * points : 5.1365, 12.3845,         4.9534, 12.1301,         4.8618, 12.1199,         4.7906, 12.2217,         4.7906, 12.5372,         4.8211, 12.9849,         4.9534, 13.6667,         5.0958, 14.4503,         5.3299, 15.2441,         5.6555, 16.0480,         ...
         */

        private int id;
        private String points;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }
    }
}
