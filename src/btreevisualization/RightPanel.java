package btreevisualization;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel kanan aplikasi yang berisi log operasi, informasi pohon, pencarian,
 * dan riwayat undo/redo.
 *
 * <p>Panel ini menyusun empat sub-panel vertikal:</p>
 * <ul>
 *   <li><b>Log Operasi</b> — area teks read-only berisi pesan setiap langkah
 *       animasi dan aksi kontrol.</li>
 *   <li><b>Informasi Pohon</b> — grid nilai orde, jumlah node, jumlah kunci,
 *       dan tinggi pohon; nilainya diperbarui oleh
 *       {@code BTreeVisualization.updateTreeInfo()}.</li>
 *   <li><b>Pencarian Node</b> — kolom input pencarian, tombol cari, dan area
 *       hasil yang menampilkan apakah kunci ditemukan.</li>
 *   <li><b>Riwayat</b> — tombol Undo dan Redo untuk memulihkan snapshot
 *       sebelumnya.</li>
 * </ul>
 *
 * <p>Seperti {@link LeftPanel}, panel ini murni UI. Semua interaksi dihubungkan
 * ke {@link BTreeController} oleh {@link BTreeVisualization#initUI()}, dan
 * label-label diisi ulang melalui {@link #updateTexts()} sesuai bahasa aktif.</p>
 *
 * @author Edison Siahaan_L
 */
public class RightPanel extends JPanel {

    /** Kolom input untuk pencarian kunci. */
    public JTextField txtSearch;

    /** Area hasil pencarian (read-only), diwarnai hijau/merah sesuai hasil. */
    public JTextField txtResult;

    public JButton btnSearch, btnUndo, btnRedo;

    /** Area log operasi. Diisi oleh {@code BTreeVisualization.logMessage()}. */
    public JTextArea logArea;

    /** Label nilai statistik pohon yang ditampilkan di sub-panel informasi. */
    public JLabel lblInfoOrde, lblInfoNode, lblInfoKunci, lblInfoTinggi;

    // Label judul sub-panel dan label statis (diisi lewat updateTexts()).
    private JLabel lblTitleLog, lblTitleInfo, lblTitleSearch, lblTitleHistory;
    private JLabel lblSearchValue, lblResult;
    private JLabel lblInfoOrdeTitle, lblInfoNodeTitle, lblInfoKunciTitle, lblInfoTinggiTitle;

    /**
     * Membuat panel kanan beserta seluruh sub-panel dan kontrolnya.
     *
     * @param currentOrder orde pohon saat ini, ditampilkan sebagai nilai awal
     *                     pada label informasi orde.
     */
    public RightPanel(int currentOrder) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 0));
        setBackground(new Color(250, 252, 255));

        // ---- 1. Sub-panel Log Operasi ----
        JPanel logPanel = UIComponents.createSubPanel("", UIComponents.IconType.DOC);
        lblTitleLog = (JLabel) ((JPanel) logPanel.getComponent(0)).getComponent(1);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(280, 150));
        logPanel.add(logScroll);

        // ---- 2. Sub-panel Informasi Pohon ----
        JPanel infoPanel = UIComponents.createSubPanel("", UIComponents.IconType.INFO);
        lblTitleInfo = (JLabel) ((JPanel) infoPanel.getComponent(0)).getComponent(1);

        JPanel infoContent = new JPanel(new GridLayout(4, 2, 10, 10));
        infoContent.setBackground(Color.WHITE);
        infoContent.setBorder(new EmptyBorder(5, 5, 5, 5));

        lblInfoOrdeTitle = new JLabel();
        infoContent.add(lblInfoOrdeTitle);
        lblInfoOrde = createInfoValueLabel(String.valueOf(currentOrder));
        infoContent.add(lblInfoOrde);

        lblInfoNodeTitle = new JLabel();
        infoContent.add(lblInfoNodeTitle);
        lblInfoNode = createInfoValueLabel("0");
        infoContent.add(lblInfoNode);

        lblInfoKunciTitle = new JLabel();
        infoContent.add(lblInfoKunciTitle);
        lblInfoKunci = createInfoValueLabel("0");
        infoContent.add(lblInfoKunci);

        lblInfoTinggiTitle = new JLabel();
        infoContent.add(lblInfoTinggiTitle);
        lblInfoTinggi = createInfoValueLabel("0");
        infoContent.add(lblInfoTinggi);

        infoPanel.add(infoContent);

        // ---- 3. Sub-panel Pencarian Node ----
        JPanel searchPanel = UIComponents.createSubPanel("", UIComponents.IconType.SEARCH);
        lblTitleSearch = (JLabel) ((JPanel) searchPanel.getComponent(0)).getComponent(1);

        JPanel searchContent = new JPanel();
        searchContent.setLayout(new BoxLayout(searchContent, BoxLayout.Y_AXIS));
        searchContent.setBackground(Color.WHITE);
        searchContent.setBorder(new EmptyBorder(5, 5, 5, 5));

        lblSearchValue = new JLabel();
        lblSearchValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchContent.add(lblSearchValue);
        searchContent.add(Box.createRigidArea(new Dimension(0, 5)));

        // Kolom input dan tombol cari berada pada satu baris.
        JPanel searchInputPanel = new JPanel(new BorderLayout(5, 0));
        searchInputPanel.setBackground(Color.WHITE);
        searchInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtSearch = new JTextField();
        txtSearch.setBorder(BorderFactory.createLineBorder(UIComponents.COLOR_BORDER));
        txtSearch.setPreferredSize(new Dimension(180, 28));
        btnSearch = new UIComponents.FlatButton("", null, UIComponents.COLOR_BTN_BLUE, Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(60, 28));
        searchInputPanel.add(txtSearch, BorderLayout.CENTER);
        searchInputPanel.add(btnSearch, BorderLayout.EAST);
        searchInputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        searchContent.add(searchInputPanel);
        searchContent.add(Box.createRigidArea(new Dimension(0, 10)));

        lblResult = new JLabel();
        lblResult.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchContent.add(lblResult);
        txtResult = new JTextField("-");
        txtResult.setEditable(false);
        txtResult.setBackground(new Color(255, 252, 225));
        txtResult.setBorder(BorderFactory.createLineBorder(UIComponents.COLOR_BORDER));
        txtResult.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtResult.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        searchContent.add(txtResult);
        searchPanel.add(searchContent);

        // ---- 4. Sub-panel Riwayat (Undo/Redo) ----
        JPanel undoPanel = UIComponents.createSubPanel("", UIComponents.IconType.HISTORY);
        lblTitleHistory = (JLabel) ((JPanel) undoPanel.getComponent(0)).getComponent(1);

        JPanel undoContent = new JPanel(new GridLayout(1, 2, 5, 0));
        undoContent.setBackground(Color.WHITE);
        undoContent.setBorder(new EmptyBorder(5, 5, 5, 5));
        btnUndo = new UIComponents.FlatButton("↶ Undo", null, UIComponents.COLOR_BTN_BLUE, Color.WHITE);
        btnRedo = new UIComponents.FlatButton("↷ Redo", null, UIComponents.COLOR_BTN_BLUE, Color.WHITE);
        undoContent.add(btnUndo);
        undoContent.add(btnRedo);
        undoPanel.add(undoContent);

        // Susun keempat sub-panel secara vertikal.
        add(logPanel);    add(Box.createRigidArea(new Dimension(0, 10)));
        add(infoPanel);   add(Box.createRigidArea(new Dimension(0, 10)));
        add(searchPanel); add(Box.createRigidArea(new Dimension(0, 10)));
        add(undoPanel);
    }

    /**
     * Membuat label nilai berformat kotak dengan latar biru muda, dipakai
     * untuk menampilkan angka statistik pohon.
     */
    private JLabel createInfoValueLabel(String value) {
        JLabel label = new JLabel(value, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(235, 240, 250));
        label.setBorder(BorderFactory.createLineBorder(UIComponents.COLOR_BORDER));
        return label;
    }

    /**
     * Memperbarui seluruh teks label pada panel ini sesuai bahasa yang aktif.
     * Dipanggil dari {@link BTreeVisualization#updateUITexts()}.
     */
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