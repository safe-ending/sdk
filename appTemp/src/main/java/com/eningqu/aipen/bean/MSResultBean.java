package com.eningqu.aipen.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author wtj
 * @filename MSResultBean
 * @date 2019/9/5
 * @email wtj@eningqu.com
 **/
public class MSResultBean {


    private List<RecognitionUnitsBean> recognitionUnits;

    public List<RecognitionUnitsBean> getRecognitionUnits() {
        return recognitionUnits;
    }

    public void setRecognitionUnits(List<RecognitionUnitsBean> recognitionUnits) {
        this.recognitionUnits = recognitionUnits;
    }

    public static class RecognitionUnitsBean implements Comparable<RecognitionUnitsBean>{
        /**
         * alternates : [{"category":"inkWord","recognizedString":"旱"},{"category":"inkWord","recognizedString":"军"},{"category":"inkWord","recognizedString":"罕"},{"category":"inkWord","recognizedString":"羊"},{"category":"inkWord","recognizedString":"前"}]
         * boundingRectangle : {"height":19.709999084472656,"topX":121.30000305175781,"topY":68.80000305175781,"width":9.210000038146973}
         * category : inkWord
         * class : leaf
         * id : 4
         * parentId : 3
         * recognizedText : 早
         * rotatedBoundingRectangle : [{"x":121.83999633789062,"y":68.54000091552734},{"x":130.5399932861328,"y":68.91999816894531},{"x":129.6999969482422,"y":88.62999725341797},{"x":121,"y":88.26000213623047}]
         * strokeIds : [1,3,2]
         * childIds : [4,5,6]
         * confidence : 1
         * points : []
         * recognizedObject : drawing
         * rotationAngle : 0
         */

        private BoundingRectangleBean boundingRectangle;
        private String category;
        @SerializedName("class")
        private String classX;
        private int id;
        private int parentId;
        private String recognizedText;
        private int confidence;
        private String recognizedObject;
        private int rotationAngle;
        private List<AlternatesBean> alternates;
        private List<RotatedBoundingRectangleBean> rotatedBoundingRectangle;
        private List<Integer> strokeIds;
        private List<Integer> childIds;
        private List<?> points;

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

        public int getConfidence() {
            return confidence;
        }

        public void setConfidence(int confidence) {
            this.confidence = confidence;
        }

        public String getRecognizedObject() {
            return recognizedObject;
        }

        public void setRecognizedObject(String recognizedObject) {
            this.recognizedObject = recognizedObject;
        }

        public int getRotationAngle() {
            return rotationAngle;
        }

        public void setRotationAngle(int rotationAngle) {
            this.rotationAngle = rotationAngle;
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

        public List<?> getPoints() {
            return points;
        }

        public void setPoints(List<?> points) {
            this.points = points;
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
             * height : 19.709999084472656
             * topX : 121.30000305175781
             * topY : 68.80000305175781
             * width : 9.210000038146973
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
             * recognizedString : 旱
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
             * x : 121.83999633789062
             * y : 68.54000091552734
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
