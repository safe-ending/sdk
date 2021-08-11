package com.eningqu.aipen.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/9/30 15:09
 * desc   :
 * version: 1.0
 */
public class MsHwrResultBean extends Object{


    private List<RecognitionUnitsBean> recognitionUnits;

    public List<RecognitionUnitsBean> getRecognitionUnits() {
        return recognitionUnits;
    }

    public void setRecognitionUnits(List<RecognitionUnitsBean> recognitionUnits) {
        this.recognitionUnits = recognitionUnits;
    }

    public static class RecognitionUnitsBean implements Comparable<RecognitionUnitsBean>{
        /**
         * alternates : [{"category":"inkWord","recognizedString":"Event"},{"category":"inkWord","recognizedString":"Ever"},{"category":"inkWord","recognizedString":"Evens"},{"category":"inkWord","recognizedString":"Elven"},{"category":"inkWord","recognizedString":"Evenk"},{"category":"inkWord","recognizedString":"Eleven"},{"category":"inkWord","recognizedString":"Eden"},{"category":"inkWord","recognizedString":"Ethen"},{"category":"inkWord","recognizedString":"Eluent"}]
         * boundingRectangle : {"height":6.78000020980835,"topX":5.039999961853027,"topY":38.13999938964844,"width":11.489999771118164}
         * category : inkWord
         * class : leaf
         * id : 4
         * parentId : 3
         * recognizedText : Even
         * rotatedBoundingRectangle : [{"x":4.639999866485596,"y":38.709999084472656},{"x":14.800000190734863,"y":35.84000015258789},{"x":16.649999618530273,"y":42.40999984741211},{"x":6.489999771118164,"y":45.27000045776367}]
         * strokeIds : [605,608,611,614,617]
         * childIds : [4,5,6,7,8]
         */

        private BoundingRectangleBean boundingRectangle;
        private String category;
        @SerializedName("class")
        private String classX;
        private int id;
        private int parentId;
        private String recognizedText;
        private List<AlternatesBean> alternates;
        private List<RotatedBoundingRectangleBean> rotatedBoundingRectangle;
        private List<Integer> strokeIds;
        private List<Integer> childIds;

        public BoundingRectangleBean getBoundingRectangle() {
            return boundingRectangle;
        }

        public void setBoundingRectangle(BoundingRectangleBean boundingRectangle) {
            this.boundingRectangle = boundingRectangle;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getClassX() {
            return classX;
        }

        public void setClassX(String classX) {
            this.classX = classX;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public String getRecognizedText() {
            return recognizedText;
        }

        public void setRecognizedText(String recognizedText) {
            this.recognizedText = recognizedText;
        }

        public List<AlternatesBean> getAlternates() {
            return alternates;
        }

        public void setAlternates(List<AlternatesBean> alternates) {
            this.alternates = alternates;
        }

        public List<RotatedBoundingRectangleBean> getRotatedBoundingRectangle() {
            return rotatedBoundingRectangle;
        }

        public void setRotatedBoundingRectangle(List<RotatedBoundingRectangleBean> rotatedBoundingRectangle) {
            this.rotatedBoundingRectangle = rotatedBoundingRectangle;
        }

        public List<Integer> getStrokeIds() {
            return strokeIds;
        }

        public void setStrokeIds(List<Integer> strokeIds) {
            this.strokeIds = strokeIds;
        }

        public List<Integer> getChildIds() {
            return childIds;
        }

        public void setChildIds(List<Integer> childIds) {
            this.childIds = childIds;
        }

        @Override
        public int compareTo(RecognitionUnitsBean o) {
            int i = (int)(this.boundingRectangle.topY - o.boundingRectangle.topY);//先按照Y排序
            if(i == 0){
                return (int)(this.boundingRectangle.topX - o.boundingRectangle.topX);//如果Y相等了再用X进行排序
            }
            return i;
        }

        public static class BoundingRectangleBean {
            /**
             * height : 6.78000020980835
             * topX : 5.039999961853027
             * topY : 38.13999938964844
             * width : 11.489999771118164
             */

            private double height;
            private double topX;
            private double topY;
            private double width;

            public double getHeight() {
                return height;
            }

            public void setHeight(double height) {
                this.height = height;
            }

            public double getTopX() {
                return topX;
            }

            public void setTopX(double topX) {
                this.topX = topX;
            }

            public double getTopY() {
                return topY;
            }

            public void setTopY(double topY) {
                this.topY = topY;
            }

            public double getWidth() {
                return width;
            }

            public void setWidth(double width) {
                this.width = width;
            }
        }

        public static class AlternatesBean {
            /**
             * category : inkWord
             * recognizedString : Event
             */

            private String category;
            private String recognizedString;

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public String getRecognizedString() {
                return recognizedString;
            }

            public void setRecognizedString(String recognizedString) {
                this.recognizedString = recognizedString;
            }
        }

        public static class RotatedBoundingRectangleBean {
            /**
             * x : 4.639999866485596
             * y : 38.709999084472656
             */

            private double x;
            private double y;

            public double getX() {
                return x;
            }

            public void setX(double x) {
                this.x = x;
            }

            public double getY() {
                return y;
            }

            public void setY(double y) {
                this.y = y;
            }
        }
    }
}
