package controller;

import model.MapNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Representa el mapa del juego, incluyendo los nodos, su disposición en filas y 
// la posición actual del jugador en el mapa.

public final class GameMap implements java.io.Serializable {
    private final List<List<MapNode>> rows;
    private final Map<Integer, MapNode> nodesById;
    private int currentNodeId;

    public GameMap(List<List<MapNode>> rows, Map<Integer, MapNode> nodesById, int startNodeId) {
        this.rows = rows;
        this.nodesById = nodesById;
        this.currentNodeId = startNodeId;
    }

    public List<List<MapNode>> getRows() {
        return rows;
    }

    public MapNode getCurrentNode() {
        return nodesById.get(currentNodeId);
    }

    public MapNode getNode(int id) {
        return nodesById.get(id);
    }

    public List<MapNode> getAvailableNodes() {
        List<MapNode> available = new ArrayList<>();
        for (MapNode node : nodesById.values()) {
            if (node.isAvailable()) {
                available.add(node);
            }
        }
        return available;
    }

    public boolean canMoveTo(int nodeId) {
        MapNode target = nodesById.get(nodeId);
        return target != null && target.isAvailable();
    }

    public MapNode moveTo(int nodeId) {
        if (!canMoveTo(nodeId)) {
            throw new IllegalStateException("Node is not reachable: " + nodeId);
        }

        for (MapNode node : nodesById.values()) {
            node.setAvailable(false);
        }

        MapNode target = nodesById.get(nodeId);
        target.setVisited(true);
        currentNodeId = nodeId;

        for (int outgoingId : target.getOutgoingIds()) {
            nodesById.get(outgoingId).setAvailable(true);
        }

        return target;
    }

    public List<MapNode> allNodes() {
        return List.copyOf(nodesById.values());
    }

    public int getTotalRows() {
        return rows.size();
    }
}
