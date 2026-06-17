package com.nodequest.game;

import com.nodequest.model.MapNode;
import com.nodequest.model.NodeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class MapGenerator {
    private static final int ROWS = 20;
    private static final int NODES_PER_ROW = 5;
    private final Random random = new Random();

    public GameMap generate() {
        List<List<MapNode>> rows = new ArrayList<>();
        Map<Integer, MapNode> nodesById = new HashMap<>();
        int nextId = 0;

        for (int row = 0; row < ROWS; row++) {
            List<MapNode> rowNodes = new ArrayList<>();
            int count = row == 0 || row == ROWS - 1 ? 1 : NODES_PER_ROW;
            for (int col = 0; col < count; col++) {
                NodeType type = pickType(row, col, count);
                MapNode node = new MapNode(nextId++, type, row, col);
                rowNodes.add(node);
                nodesById.put(node.getId(), node);
            }
            rows.add(rowNodes);
        }

        connectRows(rows, nodesById);

        MapNode start = rows.get(0).get(0);
        start.setVisited(true);
        start.setAvailable(false);
        for (int outgoingId : start.getOutgoingIds()) {
            nodesById.get(outgoingId).setAvailable(true);
        }

        return new GameMap(rows, nodesById, start.getId());
    }

    private NodeType pickType(int row, int col, int count) {
        if (row == 0) {
            return NodeType.START;
        }
        if (row == ROWS - 1) {
            return NodeType.BOSS;
        }
        if (row == ROWS - 2) {
            return NodeType.REST;
        }
        if (row == ROWS / 2 && col == 0) return NodeType.SHOP;

        double roll = random.nextDouble();
        if (roll < 0.50) {
            return NodeType.COMBAT;
        }
        if (roll < 0.65) {
            return NodeType.REST;
        }
        if (roll < 0.78) {
            return NodeType.TREASURE;
        }
        if (roll < 0.88) {
            return NodeType.ELITE;
        }
        return NodeType.SHOP;
    }

    private void connectRows(List<List<MapNode>> rows, Map<Integer, MapNode> nodesById) {
        for (int row = 0; row < rows.size() - 1; row++) {
            List<MapNode> current = rows.get(row);
            List<MapNode> next = rows.get(row + 1);

            if (next.size() == 1) {
                for (MapNode node : current) {
                    node.addOutgoing(next.get(0).getId());
                }
                continue;
            }

            if (current.size() == 1) {
                for (MapNode node : next) {
                    current.get(0).addOutgoing(node.getId());
                }
                continue;
            }

            for (int i = 0; i < next.size(); i++) {
                MapNode target = next.get(i);
                List<MapNode> candidates = new ArrayList<>();
                for (MapNode source : current) {
                    if (Math.abs(source.getColumn() - target.getColumn()) <= 1) {
                        candidates.add(source);
                    }
                }
                if (candidates.isEmpty()) {
                    candidates.add(current.get(Math.min(i, current.size() - 1)));
                }
                MapNode chosen = candidates.get(random.nextInt(candidates.size()));
                chosen.addOutgoing(target.getId());
            }

            for (MapNode source : current) {
                if (source.getOutgoingIds().isEmpty()) {
                    MapNode nearest = next.get(Math.min(source.getColumn(), next.size() - 1));
                    source.addOutgoing(nearest.getId());
                }
            }
        }
    }
}
