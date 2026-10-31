package btreevisualization;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Panel kustom untuk menggambar visualisasi {@link BTree}.
 *
 * <p>Panel ini menerima {@link TreeSnapshot} (kondisi statis) maupun
 * {@link AnimationStep} (kondisi dinamis dengan highlight) dan merendernya
 * menjadi gambar node-node B-Tree beserta sisi penghubungnya. Posisi setiap
 * node dihitung oleh {@link TreeLayout}.</p>
 *
 * <p>Fitur tampilan yang didukung:</p>
 * <ul>
 *   <li>Penyorotan node yang terlibat pada langkah animasi aktif.</li>
 *   <li>Penyorotan kunci yang sedang dibandingkan (melalui {@code activeKey}).</li>
 *   <li>Penandaan jalur pencarian dengan warna berbeda pada sisi penghubung.</li>
 *   <li>Pembersihan padding nol di depan pada kunci numerik agar mudah dibaca.</li>
 * </ul>
 *
 * <p>Panel menggunakan {@link TreeLayout} untuk menghitung bounding box setiap
 * node; hasilnya dipakai bersama oleh {@link #drawEdges} dan {@link #drawNodes}
 * agar posisi node dan garis konsisten.</p>
 *
 * @author Edison Siahaan_L
 */
public class BTreePanel extends JPanel {

    /** Snapshot pohon yang sedang ditampilkan. */
    private TreeSnapshot currentSnapshot;

    /** ID node yang perlu di-highlight pada render saat ini. */
    private Set<UUID> highlightIds = new HashSet<>();

    /** ID node yang pernah dilalui selama pencarian aktif (untuk jalur pencarian). */
    private Set<UUID> traversedNodes = new HashSet<>();

    /** Kunci yang sedang dibandingkan, ditampilkan dengan warna berbeda. */
    private String activeKey = null;

    /** Penghitung tata letak node pada panel. */
    private final TreeLayout layout = new TreeLayout();

    /**
     * Menandakan apakah mode jalur pencarian sedang aktif. Jika aktif, setiap
     * langkah {@link StepType#HIGHLIGHT} akan menambah node ke
     * {@link #traversedNodes} sehingga jalur pencarian dapat digambar.
     */
    private boolean isSearchPathActive = false;

    /**
     * Membuat panel kosong dengan latar putih.
     * Tata letak di-set {@code null} karena posisi anak tidak dikelola
     * oleh layout manager mana pun — semua node digambar secara manual.
     */
    public BTreePanel() {
        setBackground(Color.WHITE);
        setLayout(null);
    }

    /**
     * Mengaktifkan atau menonaktifkan mode jalur pencarian.
     *
     * @param active {@code true} untuk merekam jalur pencarian berikutnya.
     */
    public void setSearchPathActive(boolean active) {
        this.isSearchPathActive = active;
    }

    /**
     * Menampilkan snapshot pohon tanpa highlight.
     * Biasanya dipakai setelah operasi undo/redo atau reset.
     *
     * @param snapshot snapshot yang akan ditampilkan.
     */
    public void renderSnapshot(TreeSnapshot snapshot) {
        this.currentSnapshot = snapshot;
        this.highlightIds.clear();
        this.traversedNodes.clear();
        this.activeKey = null;
        revalidate();
        repaint();
    }

    /**
     * Menampilkan satu langkah animasi: memperbarui snapshot, highlight, kunci
     * aktif, dan (jika mode jalur pencarian aktif) menambah node ke jalur
     * pencarian.
     *
     * @param step langkah animasi yang akan dirender.
     */
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

    /**
     * Menghapus rekaman jalur pencarian.
     * Dipanggil misalnya setelah animasi pencarian selesai.
     */
    public void clearPath() {
        traversedNodes.clear();
        repaint();
    }

    /**
     * Menghitung ukuran panel yang disarankan berdasarkan ukuran konten pohon.
     * Ukuran minimum dijaga agar panel tidak terlalu kecil ketika pohon kosong.
     */
    @Override
    public Dimension getPreferredSize() {
        if (currentSnapshot == null || currentSnapshot.root == null) return new Dimension(600, 400);
        int w = Math.max(layout.getContentWidth(currentSnapshot), 400);
        int h = Math.max(layout.getContentHeight(currentSnapshot), 300);
        return new Dimension(w, h);
    }

    /**
     * Menggambar seluruh pohon: sisi penghubung terlebih dahulu, lalu node.
     * Urutan ini penting agar node tidak tertutup garis.
     */
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

    /**
     * Menggambar sisi antara sebuah node dan anak-anaknya secara rekursif.
     * Sisi yang kedua ujungnya termasuk dalam jalur pencarian digambar dengan
     * warna oranye dan ketebalan lebih besar.
     */
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

    /**
     * Menggambar node secara rekursif: kotak node, kunci-kuncinya, dan garis
     * pemisah antar kunci.
     *
     * <p>Warna latar node:</p>
     * <ul>
     *   <li>Kuning — node sedang di-highlight pada langkah ini.</li>
     *   <li>Hijau muda — node leaf.</li>
     *   <li>Biru muda — node internal.</li>
     * </ul>
     */
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

            // Bersihkan padding nol di depan untuk tampilan jika itu angka.
            String displayKey = keyValue;
            if (keyValue.matches("^0\\d+$")) {
                displayKey = keyValue.replaceFirst("^0+(?!$)", "");
            }

            // Kunci aktif (sedang dibandingkan) ditampilkan lebih tebal dan merah.
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

            // Garis pemisah antar kunci (kecuali setelah kunci terakhir).
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