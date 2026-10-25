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
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CenterPanel extends JPanel {
    public BTreePanel treePanel;
    public JLabel lblPauseStatus;
    public JPanel legendPanel;

    public CenterPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(UIComponents.COLOR_BORDER));

        JPanel headerPanel = UIComponents.createPanelHeader("", UIComponents.IconType.TREE);
        add(headerPanel, BorderLayout.NORTH);

        treePanel = new BTreePanel();
        JScrollPane scrollPane = new JScrollPane(treePanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        lblPauseStatus = new JLabel();
        lblPauseStatus.setOpaque(true);
        lblPauseStatus.setBackground(new Color(255, 235, 235));
        lblPauseStatus.setForeground(new Color(200, 50, 50));
        lblPauseStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPauseStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 50, 50), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        lblPauseStatus.setVisible(false); 
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, lblPauseStatus);
        add(scrollPane, BorderLayout.CENTER);

        legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIComponents.COLOR_BORDER));
        add(legendPanel, BorderLayout.SOUTH);
    }

    public void updateTexts() {
        ((JLabel) ((JPanel) getComponent(0)).getComponent(1)).setText(LanguageManager.get("panel.center"));
        lblPauseStatus.setText(LanguageManager.get("lbl.paused"));

        legendPanel.removeAll();
        legendPanel.add(UIComponents.createLegendItem(new Color(210, 230, 255), LanguageManager.get("legend.internal")));
        legendPanel.add(UIComponents.createLegendItem(new Color(210, 245, 220), LanguageManager.get("legend.leaf")));
        legendPanel.add(UIComponents.createLegendItem(new Color(255, 230, 100), LanguageManager.get("legend.highlight")));
        
        JPanel arrowLegend = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        arrowLegend.setBackground(Color.WHITE);
        arrowLegend.add(new JLabel(new UIComponents.CustomIcon(UIComponents.IconType.ARROW, 30, 15)));
        arrowLegend.add(new JLabel(LanguageManager.get("legend.arrow")));
        legendPanel.add(arrowLegend);
        
        legendPanel.revalidate();
        legendPanel.repaint();
    }
}
