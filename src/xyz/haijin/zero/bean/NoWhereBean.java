package xyz.haijin.zero.bean;

public class NoWhereBean {
    private String value;
    private String version;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Where{" +
                "value='" + value + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
