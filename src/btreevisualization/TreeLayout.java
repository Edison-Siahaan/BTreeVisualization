/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TreeLayout {
    private int nodeHeight = 40;
    private int nodeWidth = 60;
    private int gapY = 80;
    private int gapX = 20;

    public Map<UUID, Rectangle> calculateLayout(TreeSnapshot snapshot, int panelWidth) {
        Map<UUID, Rectangle> bounds = new HashMap<>();
        if (snapshot.root == null) return bounds;

        int treeWidth = calcSubtreeWidth(snapshot.root);
        int startX = Math.max(gapX, (panelWidth - treeWidth) / 2);

        assignPosition(snapshot.root, startX, 50, bounds);
        return bounds;
    }

    public int getContentWidth(TreeSnapshot snapshot) {
        if (snapshot.root == null) return 0;
        return calcSubtreeWidth(snapshot.root) + gapX * 2;
    }

    public int getContentHeight(TreeSnapshot snapshot) {
        if (snapshot.root == null) return 0;
        int depth = calcDepth(snapshot.root);
        return 50 + (depth - 1) * gapY + nodeHeight + 30;
    }

    private int calcDepth(NodeSnapshot node) {
        if (node.children.isEmpty()) return 1;
        int max = 0;
        for (NodeSnapshot c : node.children) {
            max = Math.max(max, calcDepth(c));
        }
        return max + 1;
    }

    private int calcSubtreeWidth(NodeSnapshot node) {
        if (node.children.isEmpty()) return nodeWidth + (node.keys.size() * 30);
        int childrenWidth = 0;
        for (NodeSnapshot child : node.children) {
            childrenWidth += calcSubtreeWidth(child) + gapX;
        }
        childrenWidth -= gapX;
        int nodeWidthHere = nodeWidth + (node.keys.size() * 30);
        return Math.max(nodeWidthHere, childrenWidth);
    }

    private void assignPosition(NodeSnapshot node, int x, int y, Map<UUID, Rectangle> bounds) {
        int sw = calcSubtreeWidth(node);
        int nodeWidthHere = nodeWidth + (node.keys.size() * 30);

        int nodeX = x + (sw - nodeWidthHere) / 2;
        bounds.put(node.id, new Rectangle(nodeX, y, nodeWidthHere, nodeHeight));

        if (node.children.isEmpty()) return;

        int totalChildrenWidth = 0;
        for (NodeSnapshot child : node.children) {
            totalChildrenWidth += calcSubtreeWidth(child) + gapX;
        }
        totalChildrenWidth -= gapX;

        int currentX = x + (sw - totalChildrenWidth) / 2;

        for (NodeSnapshot child : node.children) {
            assignPosition(child, currentX, y + gapY, bounds);
            currentX += calcSubtreeWidth(child) + gapX;
        }
    }
}