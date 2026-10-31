package btreevisualization;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel tengah yang menampilkan kanvas visualisasi pohon B-Tree.
 *
 * <p>{@code CenterPanel} menyusun tiga bagian utama:</p>
 * <ul>
 *   <li><b>Header</b> — judul panel (teksnya diisi lewat
 *       {@link #updateTexts()} agar mengikuti bahasa yang aktif).</li>
 *   <li><b>Kanvas pohon</b> — {@link BTreePanel} yang dibungkus
 *       {@link JScrollPane}, dengan {@link #lblPauseStatus} di pojok kanan
 *       atas sebagai indikator animasi dijeda.</li>
 *   <li><b>Legenda</b> — panel di bagian bawah yang menjelaskan arti warna
 *       node (internal, leaf, highlight) dan tanda panah.</li>
 * </ul>
 *
 * <p>Panel ini tidak menangani logika operasi pohon; semua perubahan state
 * dikelola oleh {@link BTreeController} dan divisualisasikan melalui
 * {@link BTreePanel#renderStep(AnimationStep)} dan
 * {@link BTreePanel#renderSnapshot(TreeSnapshot)}.</p>
 *
 * @author Edison Siahaan_L
 */
public class CenterPanel extends JPanel {

    /** Kanvas tempat pohon digambar. Diakses oleh kelas utama untuk render. */
    public BTreePanel treePanel;

    /** Indikator "Animasi Dijeda" yang muncul di pojok kanan atas kanvas. */
    public JLabel lblPauseStatus;

    /** Panel legenda warna node dan tanda panah. */
    public JPanel legendPanel;

    /**
     * Membuat panel tengah beserta seluruh komponennya.
     */
    public CenterPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(UIComponents.COLOR_BORDER));

        // Header panel: judulnya dikosongkan di sini karena akan diisi
        // oleh updateTexts() sesuai bahasa yang aktif.
        JPanel headerPanel = UIComponents.createPanelHeader("", UIComponents.IconType.TREE);
        add(headerPanel, BorderLayout.NORTH);

        // Kanvas pohon dalam scroll pane, agar pohon besar tetap bisa
        // dijelajahi tanpa memotong gambar.
        treePanel = new BTreePanel();
        JScrollPane scrollPane = new JScrollPane(treePanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        // Indikator status jeda. Ditempatkan di corner scroll pane agar
        // tetap terlihat walaupun isi kanvas di-scroll.
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

        // Panel legenda di bagian bawah. Isinya diisi oleh updateTexts().
        legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIComponents.COLOR_BORDER));
        add(legendPanel, BorderLayout.SOUTH);
    }

    /**
     * Memperbarui seluruh teks pada panel sesuai bahasa yang aktif:
     * judul header, teks indikator jeda, dan isi legenda.
     */
    public void updateTexts() {
        // Header disimpan sebagai komponen pertama (BorderLayout.NORTH).
        // Di dalam header, komponen indeks 0 adalah ikon dan indeks 1 adalah
        // label judul.
        ((JLabel) ((JPanel) getComponent(0)).getComponent(1))
                .setText(LanguageManager.get("panel.center"));
        lblPauseStatus.setText(LanguageManager.get("lbl.paused"));

        // Bangun ulang isi legenda dari awal agar mudah menyesuaikan bahasa.
        legendPanel.removeAll();
        legendPanel.add(UIComponents.createLegendItem(
                new Color(210, 230, 255), LanguageManager.get("legend.internal")));
        legendPanel.add(UIComponents.createLegendItem(
                new Color(210, 245, 220), LanguageManager.get("legend.leaf")));
        legendPanel.add(UIComponents.createLegendItem(
                new Color(255, 230, 100), LanguageManager.get("legend.highlight")));

        // Legenda khusus untuk tanda panah (bukan kotak warna biasa).
        JPanel arrowLegend = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        arrowLegend.setBackground(Color.WHITE);
        arrowLegend.add(new JLabel(new UIComponents.CustomIcon(
                UIComponents.IconType.ARROW, 30, 15)));
        arrowLegend.add(new JLabel(LanguageManager.get("legend.arrow")));
        legendPanel.add(arrowLegend);

        legendPanel.revalidate();
        legendPanel.repaint();
    }
}