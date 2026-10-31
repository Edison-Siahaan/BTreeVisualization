package btreevisualization;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel kiri aplikasi yang berisi seluruh kontrol input dan pengaturan.
 *
 * <p>Panel ini menyusun empat sub-panel vertikal:</p>
 * <ul>
 *   <li><b>Parameter B-Tree</b> — spinner orde dan tombol terapkan, dengan
 *       deskripsi singkat mengenai arti orde.</li>
 *   <li><b>Operasi Manual</b> — kolom input tunggal beserta tombol
 *       sisip, hapus, dan reset.</li>
 *   <li><b>Input Batch</b> — area teks multi-baris untuk beberapa kunci
 *       sekaligus, radio pilihan sisip/hapus, dan tombol eksekusi.</li>
 *   <li><b>Kecepatan Animasi</b> — slider kecepatan serta tombol jeda dan
 *       maju-satu-langkah.</li>
 * </ul>
 *
 * <p>Panel ini murni UI: tidak ada logika pohon di sini. Semua interaksi
 * (klik tombol, geser slider) dihubungkan ke {@link BTreeController} oleh
 * {@link BTreeVisualization#initUI()}. Teks label diisi ulang melalui
 * {@link #updateTexts()} agar mengikuti bahasa yang aktif.</p>
 *
 * @author Edison Siahaan_L
 */
public class LeftPanel extends JPanel {

    /** Kolom input untuk operasi sisip/hapus satu kunci. */
    public JTextField txtInput;

    /** Area teks untuk input batch (beberapa kunci dipisah koma). */
    public JTextArea txtBatch;

    /** Slider kecepatan animasi (nilai dalam milidetik, terbalik). */
    public JSlider sliderSpeed;

    /** Spinner untuk memilih orde B-Tree. */
    public JSpinner spinnerOrde;

    public JButton btnApplyOrder, btnInsert, btnDelete, btnReset, btnBatch, btnPause, btnStep;
    public JRadioButton rbInsert, rbDelete;

    // Label yang teksnya diisi lewat updateTexts().
    private JLabel lblTitleParam, lblTitleManual, lblTitleBatch, lblTitleSpeed;
    private JLabel lblInput, lblBatch, lblOrder, lblSpeedSlow, lblSpeedNormal, lblSpeedFast;
    private JTextArea txtParamDesc;

    /**
     * Membuat panel kiri beserta seluruh sub-panel dan kontrolnya.
     */
    public LeftPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(320, 0));
        setBackground(new Color(250, 252, 255));

        // ---- 1. Sub-panel Parameter B-Tree ----
        JPanel paramPanel = UIComponents.createSubPanel("", UIComponents.IconType.GEAR);
        lblTitleParam = (JLabel) ((JPanel) paramPanel.getComponent(0)).getComponent(1);

        JPanel paramContent = new JPanel(new BorderLayout(5, 5));
        paramContent.setBackground(Color.WHITE);
        paramContent.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        spinnerPanel.setBackground(Color.WHITE);
        lblOrder = new JLabel();
        spinnerPanel.add(lblOrder);
        spinnerOrde = new JSpinner(new SpinnerNumberModel(3, 3, 10, 1));
        spinnerPanel.add(spinnerOrde);
        btnApplyOrder = new UIComponents.FlatButton("", null, UIComponents.COLOR_BTN_BLUE, Color.WHITE);
        btnApplyOrder.setPreferredSize(new Dimension(80, 24));
        btnApplyOrder.setMaximumSize(new Dimension(80, 24));
        spinnerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        spinnerPanel.add(btnApplyOrder);

        txtParamDesc = new JTextArea();
        txtParamDesc.setEditable(false);
        txtParamDesc.setBackground(Color.WHITE);
        txtParamDesc.setFont(new Font("SansSerif", Font.PLAIN, 11));
        txtParamDesc.setForeground(Color.GRAY);
        txtParamDesc.setLineWrap(true);
        txtParamDesc.setWrapStyleWord(true);

        paramContent.add(spinnerPanel, BorderLayout.NORTH);
        paramContent.add(txtParamDesc, BorderLayout.CENTER);
        paramPanel.add(paramContent);

        // ---- 2. Sub-panel Operasi Manual ----
        JPanel manualPanel = UIComponents.createSubPanel("", UIComponents.IconType.DOC);
        lblTitleManual = (JLabel) ((JPanel) manualPanel.getComponent(0)).getComponent(1);

        JPanel manualContent = new JPanel();
        manualContent.setLayout(new BoxLayout(manualContent, BoxLayout.Y_AXIS));
        manualContent.setBackground(Color.WHITE);
        manualContent.setBorder(new EmptyBorder(5, 5, 5, 5));

        lblInput = new JLabel();
        lblInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        manualContent.add(lblInput);
        manualContent.add(Box.createRigidArea(new Dimension(0, 5)));

        txtInput = new JTextField();
        txtInput.setBackground(Color.WHITE);
        txtInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtInput.setBorder(BorderFactory.createLineBorder(UIComponents.COLOR_BORDER));
        manualContent.add(txtInput);
        manualContent.add(Box.createRigidArea(new Dimension(0, 10)));

        btnInsert = new UIComponents.FlatButton("",
                new UIComponents.CustomIcon(UIComponents.IconType.PLUS, 14, 14),
                UIComponents.COLOR_BTN_GREEN, Color.WHITE);
        btnDelete = new UIComponents.FlatButton("",
                new UIComponents.CustomIcon(UIComponents.IconType.MINUS, 14, 14),
                UIComponents.COLOR_BTN_RED, Color.WHITE);
        btnReset = new UIComponents.FlatButton("",
                new UIComponents.CustomIcon(UIComponents.IconType.RESET, 14, 14),
                UIComponents.COLOR_BTN_GRAY, Color.BLACK);

        manualContent.add(btnInsert);
        manualContent.add(Box.createRigidArea(new Dimension(0, 8)));
        manualContent.add(btnDelete);
        manualContent.add(Box.createRigidArea(new Dimension(0, 8)));
        manualContent.add(btnReset);
        manualPanel.add(manualContent);

        // ---- 3. Sub-panel Input Batch ----
        JPanel batchPanel = UIComponents.createSubPanel("", UIComponents.IconType.DOC);
        lblTitleBatch = (JLabel) ((JPanel) batchPanel.getComponent(0)).getComponent(1);

        JPanel batchContent = new JPanel();
        batchContent.setLayout(new BoxLayout(batchContent, BoxLayout.Y_AXIS));
        batchContent.setBackground(Color.WHITE);
        batchContent.setBorder(new EmptyBorder(5, 5, 5, 5));

        lblBatch = new JLabel();
        lblBatch.setAlignmentX(Component.LEFT_ALIGNMENT);
        batchContent.add(lblBatch);
        batchContent.add(Box.createRigidArea(new Dimension(0, 5)));

        txtBatch = new JTextArea();
        txtBatch.setLineWrap(true);
        JScrollPane batchScroll = new JScrollPane(txtBatch);
        batchScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        batchScroll.setBorder(BorderFactory.createLineBorder(UIComponents.COLOR_BORDER));
        batchScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        batchContent.add(batchScroll);
        batchContent.add(Box.createRigidArea(new Dimension(0, 10)));

        // Radio pilihan untuk menentukan batch berisi perintah sisip atau hapus.
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rbInsert = new JRadioButton("", true);
        rbInsert.setBackground(Color.WHITE);
        rbDelete = new JRadioButton("");
        rbDelete.setBackground(Color.WHITE);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbInsert);
        bg.add(rbDelete);
        radioPanel.add(rbInsert);
        radioPanel.add(rbDelete);

        batchContent.add(radioPanel);
        batchContent.add(Box.createRigidArea(new Dimension(0, 10)));

        btnBatch = new UIComponents.FlatButton("",
                new UIComponents.CustomIcon(UIComponents.IconType.PLAY, 12, 12),
                UIComponents.COLOR_BTN_BLUE, Color.WHITE);
        batchContent.add(btnBatch);
        batchPanel.add(batchContent);

        // ---- 4. Sub-panel Kecepatan Animasi ----
        JPanel speedPanel = UIComponents.createSubPanel("", UIComponents.IconType.HISTORY);
        lblTitleSpeed = (JLabel) ((JPanel) speedPanel.getComponent(0)).getComponent(1);

        JPanel speedContent = new JPanel(new BorderLayout(0, 5));
        speedContent.setBackground(Color.WHITE);
        speedContent.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Slider dibalik agar posisi kanan = animasi lebih cepat
        // (nilai milidetik lebih kecil), sesuai intuisi pengguna.
        sliderSpeed = new JSlider(200, 5000, 1000);
        sliderSpeed.setInverted(true);
        sliderSpeed.setBackground(Color.WHITE);

        JPanel sliderLabels = new JPanel(new GridLayout(1, 3));
        sliderLabels.setBackground(Color.WHITE);
        lblSpeedSlow = new JLabel();
        lblSpeedNormal = new JLabel("", SwingConstants.CENTER);
        lblSpeedFast = new JLabel("", SwingConstants.RIGHT);
        sliderLabels.add(lblSpeedSlow);
        sliderLabels.add(lblSpeedNormal);
        sliderLabels.add(lblSpeedFast);

        JPanel animBtnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        animBtnPanel.setBackground(Color.WHITE);
        btnPause = new UIComponents.FlatButton("", null, UIComponents.COLOR_BTN_GRAY, Color.BLACK);
        btnStep = new UIComponents.FlatButton("", null, UIComponents.COLOR_BTN_GRAY, Color.BLACK);
        animBtnPanel.add(btnPause);
        animBtnPanel.add(btnStep);

        JPanel speedCenter = new JPanel();
        speedCenter.setLayout(new BoxLayout(speedCenter, BoxLayout.Y_AXIS));
        speedCenter.setBackground(Color.WHITE);
        speedCenter.add(sliderSpeed);
        speedCenter.add(sliderLabels);
        speedCenter.add(Box.createRigidArea(new Dimension(0, 5)));
        speedCenter.add(animBtnPanel);

        speedContent.add(speedCenter, BorderLayout.CENTER);
        speedPanel.add(speedContent);

        // Susun keempat sub-panel secara vertikal.
        add(paramPanel);   add(Box.createRigidArea(new Dimension(0, 10)));
        add(manualPanel);  add(Box.createRigidArea(new Dimension(0, 10)));
        add(batchPanel);   add(Box.createRigidArea(new Dimension(0, 10)));
        add(speedPanel);
    }

    /**
     * Memperbarui seluruh teks label pada panel ini sesuai bahasa yang aktif.
     * Dipanggil dari {@link BTreeVisualization#updateUITexts()}.
     */
    public void updateTexts() {
        lblTitleParam.setText(LanguageManager.get("panel.param"));
        lblTitleManual.setText(LanguageManager.get("panel.manual"));
        lblTitleBatch.setText(LanguageManager.get("panel.batch"));
        lblTitleSpeed.setText(LanguageManager.get("panel.speed"));
        txtParamDesc.setText(LanguageManager.get("param.desc"));
        lblInput.setText(LanguageManager.get("lbl.input"));
        lblBatch.setText(LanguageManager.get("lbl.batch"));
        lblOrder.setText(LanguageManager.get("lbl.order"));
        btnInsert.setText(LanguageManager.get("btn.insert"));
        btnDelete.setText(LanguageManager.get("btn.delete"));
        btnReset.setText(LanguageManager.get("btn.reset"));
        btnBatch.setText(LanguageManager.get("btn.batch"));
        btnApplyOrder.setText(LanguageManager.get("btn.apply"));
        rbInsert.setText(LanguageManager.get("btn.insert"));
        rbDelete.setText(LanguageManager.get("btn.delete"));
        lblSpeedSlow.setText(LanguageManager.get("lbl.speed_slow"));
        lblSpeedNormal.setText(LanguageManager.get("lbl.speed_normal"));
        lblSpeedFast.setText(LanguageManager.get("lbl.speed_fast"));
    }
}