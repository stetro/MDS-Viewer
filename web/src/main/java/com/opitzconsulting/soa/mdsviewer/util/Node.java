package com.opitzconsulting.soa.mdsviewer.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: str
 * Date: 22.11.12
 * Time: 13:00
 * Simple TreeModel to handle mds trees
 */
public class Node<T> {
    private final T data;

    private final List<Node<T>> children = new ArrayList<Node<T>>();

    public Node(T data) {
        this.data = data;
    }

    public Node() {
        this.data = null;
    }

    public T getData() {
        return data;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public void addChildrenOf(Node<T> node) {
        for (Node<T> n : node.getChildren()) {
            children.add(n);
        }
    }

    public void addChild(Node<T> node) {
        children.add(node);
    }

    public int depth() {
        return depth(0);
    }

    private int depth(int i) {
        i += 1;
        if (children.size() == 0) {
            return i;
        }
        int depth = 1;
        for (Node<T> node : children) {
            int new_depth = node.depth(i);
            if (new_depth > depth) {
                depth = new_depth;
            }
        }
        return depth;
    }
}
