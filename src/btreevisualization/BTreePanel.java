/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BTreePanel extends JPanel {
    private TreeSnapshot currentSnapshot;
    private Set<UUID> highlightIds = new HashSet<>();
    private Set<UUID> traversedNodes = new HashSet<>(); 
    private String activeKey = null; 
    private final TreeLayout layout = new TreeLayout();
    private boolean isSearchPathActive = false; 

    public BTreePanel() {
        setBackground(Color.WHITE);
        setLayout(null); 
    }

    public void setSearchPathActive(boolean active) { this.isSearchPathActive = active; }

    public void renderSnapshot(TreeSnapshot snapshot) {
        this.currentSnapshot = snapshot;
        this.highlightIds.clear();
        this.traversedNodes.clear(); 
        this.activeKey = null; 
        revalidate();
        repaint();
    }

    public void renderStep(AnimationStep step) {
        this.currentSnapshot = step.snapshot;
        this.highlightIds = step.highlightIds;
        this.activeKey = step.activeKey; 
        
        if (isSearchPathActive && step.type == StepType.HIGHLIGHT && step.highlightIds != null) {
            traversedNodes.addAll(step.highlightIds);
        }
        
        revalidate();
        repaint();
    }
    
    public void clearPath() {
        traversedNodes.clear();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (currentSnapshot == null || currentSnapshot.root == null) return new Dimension(600, 400);
        int w = Math.max(layout.getContentWidth(currentSnapshot), 400);
        int h = Math.max(layout.getContentHeight(currentSnapshot), 300);
        return new Dimension(w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentSnapshot == null || currentSnapshot.root == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Map<UUID, Rectangle> bounds = layout.calculateLayout(currentSnapshot, getWidth());
        drawEdges(g2, currentSnapshot.root, bounds);
        drawNodes(g2, currentSnapshot.root, bounds);
    }

    private void drawEdges(Graphics2D g2, NodeSnapshot node, Map<UUID, Rectangle> bounds) {
        Rectangle nodeBounds = bounds.get(node.id);
        if (nodeBounds == null) return;

        for (NodeSnapshot child : node.children) {
            Rectangle childBounds = bounds.get(child.id);
            if (childBounds != null) {
                boolean isPath = traversedNodes.contains(node.id) && traversedNodes.contains(child.id);
                
                if (isPath) {
                    g2.setColor(new Color(255, 140, 0)); 
                    g2.setStroke(new BasicStroke(3.0f)); 
                } else {
                    g2.setColor(new Color(150, 150, 150)); 
                    g2.setStroke(new BasicStroke(1.0f)); 
                }
                
                g2.drawLine(nodeBounds.x + nodeBounds.width / 2, nodeBounds.y + nodeBounds.height,
                            childBounds.x + childBounds.width / 2, childBounds.y);
                            
                drawEdges(g2, child, bounds);
            }
        }
        g2.setStroke(new BasicStroke(1.0f));
    }

    private void drawNodes(Graphics2D g2, NodeSnapshot node, Map<UUID, Rectangle> bounds) {
        Rectangle r = bounds.get(node.id);
        if (r == null) return;

        if (highlightIds != null && highlightIds.contains(node.id)) {
            g2.setColor(new Color(255, 230, 100));
        } else if (node.isLeaf) {
            g2.setColor(new Color(210, 245, 220));
        } else {
            g2.setColor(new Color(210, 230, 255));
        }

        g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g2.setColor(new Color(100, 150, 200));
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);

        int cellWidth = 30;
        int startX = r.x + (r.width - (node.keys.size() * cellWidth)) / 2;

        for (int i = 0; i < node.keys.size(); i++) {
            String keyValue = node.keys.get(i);
            
            // Bersihkan padding 0 di depan untuk tampilan jika itu adalah angka
            String displayKey = keyValue;
            if (keyValue.matches("^0\\d+$")) {
                displayKey = keyValue.replaceFirst("^0+(?!$)", "");
            }

            if (activeKey != null && keyValue.equals(activeKey)) {
                g2.setColor(new Color(220, 50, 50)); 
                g2.setFont(new Font("SansSerif", Font.BOLD, 16)); 
            } else {
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            }
            
            FontMetrics fm = g2.getFontMetrics();
            int tx = startX + (i * cellWidth) + (cellWidth - fm.stringWidth(displayKey)) / 2;
            int ty = r.y + (r.height + fm.getAscent()) / 2 - 2;
            g2.drawString(displayKey, tx, ty);

            if (i < node.keys.size() - 1) {
                g2.setColor(new Color(100, 150, 200)); 
                g2.drawLine(startX + (i + 1) * cellWidth, r.y + 5,
                            startX + (i + 1) * cellWidth, r.y + r.height - 5);
            }
        }

        for (NodeSnapshot child : node.children) {
            drawNodes(g2, child, bounds);
        }
    }
}