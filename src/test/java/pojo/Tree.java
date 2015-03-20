package pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tree {
    String URI;
    String name;
    Integer size;
    Set<Tree> children;

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Set<Tree> getChildren() {
        return children;
    }

    public void setChildren(Set<Tree> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tree)) return false;

        Tree node = (Tree) o;

        if (URI != null ? !URI.equals(node.URI) : node.URI != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return URI != null ? URI.hashCode() : 0;
    }
}
