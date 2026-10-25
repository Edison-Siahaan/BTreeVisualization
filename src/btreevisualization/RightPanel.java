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

public class RightPanel extends JPanel {
    public JTextField txtSearch, txtResult;
    public JButton btnSearch, btnUndo, btnRedo;
    public JTextArea logArea;
    public JLabel lblInfoOrde, lblInfoNode, lblInfoKunci, lblInfoTinggi;
    
    private JLabel lblTitleLog, lblTitleInfo, lblTitleSearch, lblTitleHistory;
    private JLabel lblSearchValue, lblResult, lblInfoOrdeTitle, lblInfoNodeTitle, lblInfoKunciTitle, lblInfoTinggiTitle;

    public RightPanel(int currentOrder) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 0));
        setBackground(new Color(250, 252, 255));

        // 1. Log Operasi
        JPanel logPanel = UIComponents.createSubPanel("", UIComponents.IconType.DOC);
        lblTitleLog = (JLabel) ((JPanel) logPanel.getComponent(0)).getComponent(1);
        logArea = new JTextArea(); logArea.setEditable(false); logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(logArea); logScroll.setPreferredSize(new Dimension(280, 150));
        logPanel.add(logScroll);

        // 2. Informasi Pohon
        JPanel infoPanel = UIComponents.createSubPanel("", UIComponents.IconType.INFO);
        lblTitleInfo = (JLabel) ((JPanel) infoPanel.getComponent(0)).getComponent(1);
        JPanel infoContent = new JPanel(new GridLayout(4, 2, 10, 10));
        infoContent.setBackground(Color.WHITE); infoContent.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        lblInfoOrdeTitle = new JLabel(); infoContent.add(lblInfoOrdeTitle);
        lblInfoOrde = createInfoValueLabel(String.valueOf(currentOrder)); infoContent.add(lblInfoOrde);
        
        lblInfoNodeTitle = new JLabel(); infoContent.add(lblInfoNodeTitle);
        lblInfoNode = createInfoValueLabel("0"); infoContent.add(lblInfoNode);
        
        lblInfoKunciTitle = new JLabel(); infoContent.add(lblInfoKunciTitle);
        lblInfoKunci = createInfoValueLabel("0"); infoContent.add(lblInfoKunci);
        
        lblInfoTinggiTitle = new JLabel(); infoContent.add(lblInfoTinggiTitle);
        lblInfoTinggi = createInfoValueLabel("0"); infoContent.add(lblInfoTinggi);
        infoPanel.add(infoContent);

        // 3. Pencarian Node
        JPanel searchPanel = UIComponents.createSubPanel("", UIComponents.IconType.SEARCH);
        lblTitleSearch = (JLabel) ((JPanel) searchPanel.getComponent(0)).getComponent(1);
        JPanel searchContent = new JPanel(); searchContent.setLayout(new BoxLayout(searchContent, BoxLayout.Y_AXIS));
        searchContent.setBackground(Color.WHITE); searchContent.setBorder(new EmptyBorder(5, 5, 5, 5));

        lblSearchValue = new JLabel(); lblSearchValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchContent.add(lblSearchValue); searchContent.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JPanel searchInputPanel = new JPanel(new BorderLayout(5, 0));
        searchInputPanel.setBackground(Color.WHITE); searchInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtSearch = new JTextField(); txtSearch.setBorder(BorderFactory.createLineBorder(UIComponents.COLOR_BORDER)); txtSearch.setPreferredSize(new Dimension(180, 28));
        btnSearch = new UIComponents.FlatButton("", null, UIComponents.COLOR_BTN_BLUE, Color.WHITE); btnSearch.setPreferredSize(new Dimension(60, 28));
        searchInputPanel.add(txtSearch, BorderLayout.CENTER); searchInputPanel.add(btnSearch, BorderLayout.EAST);
        searchInputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        
        searchContent.add(searchInputPanel); searchContent.add(Box.createRigidArea(new Dimension(0, 10)));
        
        lblResult = new JLabel(); lblResult.setAlignmentX(Component.LEFT_ALIGNMENT); searchContent.add(lblResult);
        txtResult = new JTextField("-"); txtResult.setEditable(false); txtResult.setBackground(new Color(255, 252, 225));
        txtResult.setBorder(BorderFactory.createLineBorder(UIComponents.COLOR_BORDER)); txtResult.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtResult.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28)); searchContent.add(txtResult);
        searchPanel.add(searchContent);

        // 4. Undo/Redo Panel
        JPanel undoPanel = UIComponents.createSubPanel("", UIComponents.IconType.HISTORY);
        lblTitleHistory = (JLabel) ((JPanel) undoPanel.getComponent(0)).getComponent(1);
        JPanel undoContent = new JPanel(new GridLayout(1, 2, 5, 0));
        undoContent.setBackground(Color.WHITE); undoContent.setBorder(new EmptyBorder(5, 5, 5, 5));
        btnUndo = new UIComponents.FlatButton("↶ Undo", null, UIComponents.COLOR_BTN_BLUE, Color.WHITE);
        btnRedo = new UIComponents.FlatButton("↷ Redo", null, UIComponents.COLOR_BTN_BLUE, Color.WHITE);
        undoContent.add(btnUndo); undoContent.add(btnRedo); undoPanel.add(undoContent);

        add(logPanel); add(Box.createRigidArea(new Dimension(0, 10)));
        add(infoPanel); add(Box.createRigidArea(new Dimension(0, 10)));
        add(searchPanel); add(Box.createRigidArea(new Dimension(0, 10)));
        add(undoPanel);
    }

    private JLabel createInfoValueLabel(String value) {
        JLabel label = new JLabel(value, SwingConstants.CENTER);
        label.setOpaque(true); label.setBackground(new Color(235, 240, 250));
        label.setBorder(BorderFactory.createLineBorder(UIComponents.COLOR_BORDER));
        return label;
    }

    public void updateTexts() {
        lblTitleLog.setText(LanguageManager.get("lbl.log"));
        lblTitleInfo.setText(LanguageManager.get("lbl.info"));
        lblTitleSearch.setText(LanguageManager.get("panel.search"));
        lblTitleHistory.setText(LanguageManager.get("lbl.history"));
        lblSearchValue.setText(LanguageManager.get("lbl.search"));
        lblResult.setText(LanguageManager.get("lbl.result"));
        btnSearch.setText(LanguageManager.get("btn.search"));
        lblInfoOrdeTitle.setText(LanguageManager.get("info.order"));
        lblInfoNodeTitle.setText(LanguageManager.get("info.nodes"));
        lblInfoKunciTitle.setText(LanguageManager.get("info.keys"));
        lblInfoTinggiTitle.setText(LanguageManager.get("info.height"));
    }
}
