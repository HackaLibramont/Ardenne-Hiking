package org.ardennes.pojo.graph;

public class Edge {
    String source;
    String target;
    Integer number;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Edge(String source, String target) {
        this.source = source;
        this.target = target;
    }
}
