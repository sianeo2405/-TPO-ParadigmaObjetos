package com.nodequest.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MapNode implements java.io.Serializable {
    private final int id;
    private final NodeType type;
    private final int row;
    private final int column;
    private final List<Integer> outgoingIds = new ArrayList<>();
    private boolean visited;
    private boolean available;

    public MapNode(int id, NodeType type, int row, int column) {
        this.id = id;
        this.type = type;
        this.row = row;
        this.column = column;
    }

    public int getId() {
        return id;
    }

    public NodeType getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public List<Integer> getOutgoingIds() {
        return Collections.unmodifiableList(outgoingIds);
    }

    public void addOutgoing(int nodeId) {
        if (!outgoingIds.contains(nodeId)) {
            outgoingIds.add(nodeId);
        }
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
