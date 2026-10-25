
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import javax.imageio.ImageIO;

public class BTreeVisualization extends JFrame implements AnimationListener {
    private BTreeController controller;
    private int currentOrder = 3; 
    
    private LeftPanel leftPanel;
    private CenterPanel centerPanel;
    private RightPanel rightPanel;

    private boolean isAnimating = false; 
    private boolean isSearching = false; 
    
    private JMenu fileMenu, settingsMenu, languageMenu, keyTypeMenu, helpMenu;
    private JMenuItem menuItemNew, menuItemImport, menuItemExport, menuItemExit, menuItemGuide, menuItemAbout;
    private JRadioButtonMenuItem langIdItem, langEnItem, typeNumericItem, typeStringItem;

    public BTreeVisualization() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setSize(1300, 850); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        initController();
        initUI();
        updateUITexts(); 
    }

    private void initController() {
        BTree tree = new BTree(currentOrder); 
        AnimationManager animMgr = new AnimationManager();
        StepRecorder recorder = new StepRecorder();
        UndoRedoManager undoRedo = new UndoRedoManager(20);
        InputValidator validator = new InputValidator(1, 999, 20); 
        
        tree.addListener(recorder);
        controller = new BTreeController(tree, animMgr, recorder, undoRedo, validator);
        controller.setAnimationListener(this);
    }

    private void initUI() {
        setJMenuBar(createMenuBar());

        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContent.setBackground(new Color(250, 252, 255));

        leftPanel = new LeftPanel();
        centerPanel = new CenterPanel();
        rightPanel = new RightPanel(currentOrder);

        mainContent.add(leftPanel, BorderLayout.WEST);
        mainContent.add(centerPanel, BorderLayout.CENTER);
        mainContent.add(rightPanel, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);
        
        // --- MENGHUBUNGKAN EVENT LISTENER ---
        leftPanel.btnInsert.addActionListener(e -> processInsert());
        leftPanel.btnDelete.addActionListener(e -> processDelete());
        leftPanel.btnBatch.addActionListener(e -> processBatch());
        leftPanel.btnReset.addActionListener(e -> processReset());
        leftPanel.btnPause.addActionListener(e -> processPause());
        leftPanel.btnStep.addActionListener(e -> processStep());
        leftPanel.sliderSpeed.addChangeListener(e -> controller.setSpeed(leftPanel.sliderSpeed.getValue()));
        leftPanel.btnApplyOrder.addActionListener(e -> processApplyOrder());
        
        rightPanel.btnSearch.addActionListener(e -> processSearch());
        rightPanel.btnUndo.addActionListener(e -> processUndo());
        rightPanel.btnRedo.addActionListener(e -> processRedo());

        updateControlStates();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        fileMenu = new JMenu();
        
        menuItemNew = new JMenuItem();
        menuItemNew.addActionListener(e -> leftPanel.btnReset.doClick()); 
        fileMenu.add(menuItemNew);
        fileMenu.addSeparator();
        
        menuItemImport = new JMenuItem();
        menuItemImport.addActionListener(e -> importData());
        fileMenu.add(menuItemImport);
        
        menuItemExport = new JMenuItem();
        menuItemExport.addActionListener(e -> exportAsImage());
        fileMenu.add(menuItemExport);
        fileMenu.addSeparator();
        
        settingsMenu = new JMenu();
        languageMenu = new JMenu();
        langIdItem = new JRadioButtonMenuItem("Bahasa Indonesia", true);
        langEnItem = new JRadioButtonMenuItem("English");
        ButtonGroup langGroup = new ButtonGroup(); langGroup.add(langIdItem); langGroup.add(langEnItem);
        langIdItem.addActionListener(e -> { LanguageManager.setLanguage("id"); updateUITexts(); });
        langEnItem.addActionListener(e -> { LanguageManager.setLanguage("en"); updateUITexts(); });
        languageMenu.add(langIdItem); languageMenu.add(langEnItem); settingsMenu.add(languageMenu);
        
        keyTypeMenu = new JMenu();
        typeNumericItem = new JRadioButtonMenuItem("Numerik", true);
        typeStringItem = new JRadioButtonMenuItem("String");
        ButtonGroup keyTypeGroup = new ButtonGroup(); keyTypeGroup.add(typeNumericItem); keyTypeGroup.add(typeStringItem);
        typeNumericItem.addActionListener(e -> { controller.setNumericMode(true); leftPanel.btnReset.doClick(); });
        typeStringItem.addActionListener(e -> { controller.setNumericMode(false); leftPanel.btnReset.doClick(); });
        keyTypeMenu.add(typeNumericItem); keyTypeMenu.add(typeStringItem); settingsMenu.add(keyTypeMenu);
        
        fileMenu.add(settingsMenu); fileMenu.addSeparator();
        
        menuItemExit = new JMenuItem();
        menuItemExit.addActionListener(e -> System.exit(0));
        fileMenu.add(menuItemExit);
        
        helpMenu = new JMenu();
        menuItemGuide = new JMenuItem();
        menuItemGuide.addActionListener(e -> CustomDialogs.showGuide(this)); 
        helpMenu.add(menuItemGuide); helpMenu.addSeparator();
        menuItemAbout = new JMenuItem();
        menuItemAbout.addActionListener(e -> CustomDialogs.showAbout(this)); 
        helpMenu.add(menuItemAbout);
        
        menuBar.add(fileMenu); menuBar.add(helpMenu);
        return menuBar;
    }
    
    private void updateUITexts() {
        setTitle(LanguageManager.get("app.title"));
        fileMenu.setText(LanguageManager.get("menu.file"));
        settingsMenu.setText(LanguageManager.get("menu.settings"));
        languageMenu.setText(LanguageManager.get("menu.settings.language"));
        keyTypeMenu.setText(LanguageManager.get("menu.settings.key_type"));
        helpMenu.setText(LanguageManager.get("menu.help"));
        menuItemNew.setText(LanguageManager.get("menu.file.new"));
        menuItemImport.setText(LanguageManager.get("menu.file.import")); 
        menuItemExport.setText(LanguageManager.get("menu.file.export"));
        menuItemExit.setText(LanguageManager.get("menu.file.exit"));
        menuItemGuide.setText(LanguageManager.get("menu.help.guide")); 
        menuItemAbout.setText(LanguageManager.get("menu.help.about"));
        langIdItem.setText(LanguageManager.get("menu.settings.lang_id"));
        langEnItem.setText(LanguageManager.get("menu.settings.lang_en"));
        typeNumericItem.setText(LanguageManager.get("menu.settings.key_numeric"));
        typeStringItem.setText(LanguageManager.get("menu.settings.key_string"));

        leftPanel.updateTexts();
        centerPanel.updateTexts();
        rightPanel.updateTexts();
        
        updateControlStates();
    }

    // =========================================================================
    // EVENT HANDLERS
    // =========================================================================
    private void processInsert() {
        ValidationResult res = controller.insert(leftPanel.txtInput.getText());
        if (!res.valid) {
            JOptionPane.showMessageDialog(this, res.message, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            leftPanel.txtInput.setText("");
            centerPanel.treePanel.setSearchPathActive(false); centerPanel.treePanel.clearPath(); 
            isAnimating = true; isSearching = false;
            updateControlStates();
            centerPanel.lblPauseStatus.setVisible(false); 
        }
    }

    private void processDelete() {
        ValidationResult res = controller.delete(leftPanel.txtInput.getText());
        if (!res.valid) {
            JOptionPane.showMessageDialog(this, res.message, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            leftPanel.txtInput.setText("");
            centerPanel.treePanel.setSearchPathActive(false); centerPanel.treePanel.clearPath(); 
            isAnimating = true; isSearching = false;
            updateControlStates();
            centerPanel.lblPauseStatus.setVisible(false); 
        }
    }

    private void processBatch() {
        ValidationResult res = leftPanel.rbInsert.isSelected() ? 
                controller.batchInsert(leftPanel.txtBatch.getText()) : controller.batchDelete(leftPanel.txtBatch.getText());
        if (!res.valid) {
            JOptionPane.showMessageDialog(this, res.message, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            leftPanel.txtBatch.setText("");
            centerPanel.treePanel.setSearchPathActive(false); centerPanel.treePanel.clearPath(); 
            isAnimating = true; isSearching = false;
            updateControlStates();
            centerPanel.lblPauseStatus.setVisible(false); 
        }
    }

    private void processSearch() {
        ValidationResult res = controller.search(rightPanel.txtSearch.getText());
        if (!res.valid) {
            JOptionPane.showMessageDialog(this, res.message, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            centerPanel.treePanel.setSearchPathActive(true); centerPanel.treePanel.clearPath(); 
            isSearching = true; isAnimating = true;
            rightPanel.txtResult.setText("..."); rightPanel.txtResult.setForeground(Color.BLACK);
            updateControlStates();
            centerPanel.lblPauseStatus.setVisible(false);
        }
    }

    private void processReset() {
        int confirm = JOptionPane.showConfirmDialog(this, LanguageManager.get("dlg.reset_msg"), LanguageManager.get("dlg.reset_title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.resetTree(currentOrder);
            centerPanel.treePanel.renderSnapshot(controller.getCurrentSnapshot());
            updateTreeInfo(null);
            leftPanel.txtInput.setText(""); leftPanel.txtBatch.setText("");
            rightPanel.txtSearch.setText(""); rightPanel.txtResult.setText("-"); rightPanel.txtResult.setForeground(Color.BLACK);
            rightPanel.logArea.setText("");
            logMessage(LanguageManager.get("log.reset"));
            centerPanel.lblPauseStatus.setVisible(false);
            updateControlStates();
        }
    }

    private void processPause() { 
        controller.togglePause(); updateControlStates(); 
        boolean paused = controller.isPaused();
        centerPanel.lblPauseStatus.setVisible(paused);
        if (paused) logMessage("[KONTROL] Animasi dijeda (Paused).");
        else logMessage("[KONTROL] Animasi dilanjutkan.");
    }
    
    private void processStep() {
        controller.stepForward();
        if (controller.isPaused()) centerPanel.lblPauseStatus.setVisible(true);
    }

    private void processApplyOrder() {
        int newOrder = (Integer) leftPanel.spinnerOrde.getValue();
        if (newOrder == currentOrder) {
            JOptionPane.showMessageDialog(this, "Orde saat ini sudah " + currentOrder, "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, LanguageManager.get("dlg.reset_order"), LanguageManager.get("dlg.reset_title"), JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            currentOrder = newOrder;
            controller.resetTree(currentOrder);
            rightPanel.lblInfoOrde.setText(String.valueOf(currentOrder)); 
            centerPanel.treePanel.renderSnapshot(controller.getCurrentSnapshot());
            updateTreeInfo(null);
            leftPanel.txtInput.setText(""); leftPanel.txtBatch.setText("");
            rightPanel.txtSearch.setText(""); rightPanel.txtResult.setText("-"); rightPanel.txtResult.setForeground(Color.BLACK);
            rightPanel.logArea.setText("");
            logMessage("Pohon di-reset dengan Orde " + currentOrder);
            updateControlStates();
        } else {
            leftPanel.spinnerOrde.setValue(currentOrder);
        }
    }

    private void processUndo() {
        TreeSnapshot snap = controller.undo();
        if (snap != null) { centerPanel.treePanel.renderSnapshot(snap); updateTreeInfo(snap); }
        isAnimating = false; updateControlStates(); centerPanel.lblPauseStatus.setVisible(false);
    }

    private void processRedo() {
        TreeSnapshot snap = controller.redo();
        if (snap != null) { centerPanel.treePanel.renderSnapshot(snap); updateTreeInfo(snap); }
        isAnimating = false; updateControlStates(); centerPanel.lblPauseStatus.setVisible(false); 
    }

    // =========================================================================
    // ANIMATION CALLBACKS & UTILS
    // =========================================================================
    @Override
    public void onStep(AnimationStep step) {
        centerPanel.treePanel.renderStep(step);
        updateTreeInfo(step.snapshot);
        logMessage("[" + step.type + "] " + step.message);
        isAnimating = true;
        updateControlStates();
    }

    @Override
    public void onFinished() {
        isAnimating = false; updateControlStates();
        logMessage("--- Selesai ---");
        centerPanel.lblPauseStatus.setVisible(false); 
        centerPanel.treePanel.setSearchPathActive(false); centerPanel.treePanel.clearPath(); 
        
        if (isSearching) {
            if (controller.isLastSearchFound()) {
                rightPanel.txtResult.setText(fmt(controller.getLastSearchKey()) + " Ditemukan");
                rightPanel.txtResult.setForeground(new Color(0, 128, 0)); 
            } else {
                rightPanel.txtResult.setText(fmt(controller.getLastSearchKey()) + " Tidak Ditemukan");
                rightPanel.txtResult.setForeground(Color.RED);
            }
            isSearching = false;
        }
    }

    private void updateControlStates() {
        boolean paused = controller.isPaused();
        leftPanel.txtInput.setEnabled(!isAnimating);
        leftPanel.txtBatch.setEnabled(!isAnimating);
        rightPanel.txtSearch.setEnabled(!isAnimating);
        leftPanel.spinnerOrde.setEnabled(!isAnimating);
        leftPanel.btnApplyOrder.setEnabled(!isAnimating);
        leftPanel.btnInsert.setEnabled(!isAnimating);
        leftPanel.btnDelete.setEnabled(!isAnimating);
        rightPanel.btnSearch.setEnabled(!isAnimating);
        leftPanel.btnBatch.setEnabled(!isAnimating);
        leftPanel.rbInsert.setEnabled(!isAnimating);
        leftPanel.rbDelete.setEnabled(!isAnimating);
        rightPanel.btnUndo.setEnabled(!isAnimating && controller.canUndo());
        rightPanel.btnRedo.setEnabled(!isAnimating && controller.canRedo());
        leftPanel.btnReset.setEnabled(!isAnimating); 

        if (isAnimating) {
            leftPanel.btnPause.setEnabled(true);
            leftPanel.btnPause.setText(paused ? LanguageManager.get("btn.resume") : LanguageManager.get("btn.pause"));
            leftPanel.btnStep.setEnabled(paused);
        } else {
            leftPanel.btnPause.setEnabled(false);
            leftPanel.btnPause.setText(LanguageManager.get("btn.pause"));
            leftPanel.btnStep.setEnabled(false);
        }
    }

    private void updateTreeInfo(TreeSnapshot snapshot) {
        if (snapshot == null || snapshot.root == null) {
            rightPanel.lblInfoNode.setText("0"); rightPanel.lblInfoKunci.setText("0"); rightPanel.lblInfoTinggi.setText("0");
            return;
        }
        int[] stats = new int[3]; 
        calculateStats(snapshot.root, 0, stats); 
        rightPanel.lblInfoNode.setText(String.valueOf(stats[0]));
        rightPanel.lblInfoKunci.setText(String.valueOf(stats[1]));
        rightPanel.lblInfoTinggi.setText(String.valueOf(stats[2]));
    }

    private void calculateStats(NodeSnapshot node, int depth, int[] stats) {
        if (node == null) return;
        stats[0]++; stats[1] += node.keys.size(); stats[2] = Math.max(stats[2], depth); 
        for (NodeSnapshot child : node.children) calculateStats(child, depth + 1, stats);
    }

    private void logMessage(String msg) {
        rightPanel.logArea.append(msg + "\n");
        rightPanel.logArea.setCaretPosition(rightPanel.logArea.getDocument().getLength());
    }
    
    private String fmt(String key) {
        if (key != null && key.matches("^0\\d+$")) return key.replaceFirst("^0+(?!$)", "");
        return key;
    }

    // =========================================================================
    // IMPORT & EXPORT
    // =========================================================================
    private void importData() {
        if (isAnimating) { JOptionPane.showMessageDialog(this, "Animasi berjalan.", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try {
                List<String> lines = Files.readAllLines(fileToOpen.toPath());
                StringBuilder sb = new StringBuilder();
                int newOrder = currentOrder; boolean orderChanged = false;
                boolean newNumericMode = controller.isNumericMode(); boolean typeChanged = false;
                
                for (String line : lines) {
                    String cleanLine = line.trim();
                    if (cleanLine.isEmpty()) continue;
                    String upperLine = cleanLine.toUpperCase();
                    
                    if (upperLine.startsWith("ORDER=") || upperLine.startsWith("ORDE=")) {
                        try {
                            int parsedOrder = Integer.parseInt(cleanLine.split("=")[1].trim());
                            if (parsedOrder >= 3 && parsedOrder <= 10) { newOrder = parsedOrder; orderChanged = true; }
                        } catch (Exception ignored) {}
                    } else if (upperLine.startsWith("TYPE=") || upperLine.startsWith("TIPE=")) {
                        try {
                            String typeStr = cleanLine.split("=")[1].trim().toUpperCase();
                            if (typeStr.equals("NUMERIC") || typeStr.equals("NUMERIK")) { newNumericMode = true; typeChanged = true; } 
                            else if (typeStr.equals("STRING") || typeStr.equals("TEKS")) { newNumericMode = false; typeChanged = true; }
                        } catch (Exception ignored) {}
                    } else {
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(cleanLine);
                    }
                }
                
                if (typeChanged && newNumericMode != controller.isNumericMode()) {
                    controller.setNumericMode(newNumericMode);
                    if (newNumericMode) typeNumericItem.setSelected(true); else typeStringItem.setSelected(true);
                    orderChanged = true; 
                }

                if (orderChanged) {
                    currentOrder = newOrder; controller.resetTree(currentOrder);
                    leftPanel.spinnerOrde.setValue(currentOrder);
                    rightPanel.lblInfoOrde.setText(String.valueOf(currentOrder));
                    centerPanel.treePanel.renderSnapshot(controller.getCurrentSnapshot());
                    updateTreeInfo(null);
                }

                leftPanel.txtBatch.setText(sb.toString()); leftPanel.rbInsert.setSelected(true); 
                JOptionPane.showMessageDialog(this, "Data berhasil diimpor.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void exportAsImage() {
        if (centerPanel.treePanel.getComponentCount() == 0 && controller.getCurrentSnapshot().root == null) {
            JOptionPane.showMessageDialog(this, LanguageManager.get("err.export_empty"), LanguageManager.get("err.title"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Image (*.png)", "png"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".png")) filePath += ".png";
            try {
                Dimension prefSize = centerPanel.treePanel.getPreferredSize();
                Dimension originalSize = centerPanel.treePanel.getSize();
                centerPanel.treePanel.setSize(prefSize);
                BufferedImage image = new BufferedImage(prefSize.width, prefSize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = image.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                centerPanel.treePanel.paint(g2);
                g2.dispose();
                centerPanel.treePanel.setSize(originalSize);
                ImageIO.write(image, "png", new File(filePath));
                JOptionPane.showMessageDialog(this, LanguageManager.get("dlg.export_success") + "\n" + filePath, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BTreeVisualization().setVisible(true));
    }
}