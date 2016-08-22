package org.vlis.operations.event.typeguess.bean;

import java.util.List;

public class EventBean extends RuleBean {
    private String transactionId;
    private String enterUrl;
    private List<Key> keys;

    public EventBean(String project, int action, String id, String transactionId, String enterUrl, List<Key> keys) {
        super(project, action, id);
        this.transactionId = transactionId;
        this.enterUrl = enterUrl;
        this.keys = keys;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getEnterUrl() {
        return enterUrl;
    }

    public void setEnterUrl(String enterUrl) {
        this.enterUrl = enterUrl;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    public static class Key {
        public static final String BOOLEAN = "Boolean";
        public static final String LONG = "Long";
        public static final String DOUBLE = "Double";
        public static final String STRING = "String";
        public static final String DATE = "Date";

        private int attributeId;
        private String attributeName;
        private String jsonPath;
        private String attributeType;

        public Key(int attributeId, String attributeName, String jsonPath, String attributeType) {
            this.attributeId = attributeId;
            this.attributeName = attributeName;
            this.jsonPath = jsonPath;
            this.attributeType = attributeType;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        public String getJsonPath() {
            return jsonPath;
        }

        public void setJsonPath(String jsonPath) {
            this.jsonPath = jsonPath;
        }

        public String getAttributeType() {
            return attributeType;
        }

        public void setAttributeType(String attributeType) {
            this.attributeType = attributeType;
        }

        public int getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(int attributeId) {
            this.attributeId = attributeId;
        }

        @Override
        public String toString() {
            return "Key [attributeId=" + attributeId + ", attributeName=" + attributeName + ", jsonPath=" + jsonPath
                    + ", attributeType=" + attributeType + "]";
        }
    }
}
