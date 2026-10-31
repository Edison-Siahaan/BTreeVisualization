package btreevisualization;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Penghitung tata letak node B-Tree pada kanvas visualisasi.
 *
 * <p>Kelas ini murni kalkulasi geometri tanpa menyentuh komponen Swing. Hasil
 * akhirnya adalah peta dari {@link UUID} node ke {@link Rectangle} (posisi dan
 * ukuran) yang dipakai oleh {@link BTreePanel} untuk menggambar node dan sisi
 * penghubung.</p>
 *
 * <p>Algoritma tata letak bersifat <b>bottom-up untuk lebar</b> dan
 * <b>top-down untuk posisi</b>:</p>
 * <ol>
 *   <li>{@link #calcSubtreeWidth(NodeSnapshot)} menghitung total lebar yang
 *       dibutuhkan sebuah subtree (rekursif).</li>
 *   <li>{@link #assignPosition(NodeSnapshot, int, int, Map)} menempatkan node
 *       pada posisi y berdasarkan kedalaman, dan memusatkan node di atas
 *       anak-anaknya pada sumbu x.</li>
 *   <li>{@link #calculateLayout(TreeSnapshot, int)} memusatkan seluruh pohon
 *       secara horizontal di dalam lebar panel.</li>
 * </ol>
 *
 * <p>Ukuran tiap node bergantung pada jumlah kuncinya: lebar dasar
 * {@link #nodeWidth} ditambah {@code 30} piksel per kunci, sehingga node
 * dengan lebih banyak kunci otomatis lebih lebar.</p>
 *
 * @author Edison Siahaan_L
 */
public class TreeLayout {

    /** Tinggi tetap setiap kotak node. */
    private final int nodeHeight = 40;

    /** Lebar dasar node; lebar aktualnya ditambah 30 piksel per kunci. */
    private final int nodeWidth = 60;

    /** Jarak vertikal antar level pohon. */
    private final int gapY = 80;

    /** Jarak horizontal antar subtree yang bersebelahan. */
    private final int gapX = 20;

    /**
     * Menghitung posisi seluruh node dalam pohon untuk panel dengan lebar
     * tertentu.
     *
     * <p>Pohon diposisikan agar berada di tengah secara horizontal, tetapi
     * tidak pernah lebih dekat dari {@link #gapX} ke tepi kiri panel.</p>
     *
     * @param snapshot    snapshot pohon yang akan ditata.
     * @param panelWidth  lebar area gambar yang tersedia.
     * @return peta dari ID node ke {@link Rectangle} posisinya.
     */
    public Map<UUID, Rectangle> calculateLayout(TreeSnapshot snapshot, int panelWidth) {
        Map<UUID, Rectangle> bounds = new HashMap<>();
        if (snapshot.root == null) return bounds;

        int treeWidth = calcSubtreeWidth(snapshot.root);
        int startX = Math.max(gapX, (panelWidth - treeWidth) / 2);

        assignPosition(snapshot.root, startX, 50, bounds);
        return bounds;
    }

    /**
     * Lebar total konten pohon (termasuk padding kiri-kanan sebesar
     * {@link #gapX}). Dipakai oleh {@link BTreePanel#getPreferredSize()} agar
     * scroll pane tahu seberapa lebar kanvas seharusnya.
     *
     * @param snapshot snapshot pohon.
     * @return lebar konten dalam piksel, atau 0 jika pohon kosong.
     */
    public int getContentWidth(TreeSnapshot snapshot) {
        if (snapshot.root == null) return 0;
        return calcSubtreeWidth(snapshot.root) + gapX * 2;
    }

    /**
     * Tinggi total konten pohon (termasuk ruang atas dan bawah). Dipakai oleh
     * {@link BTreePanel#getPreferredSize()}.
     *
     * @param snapshot snapshot pohon.
     * @return tinggi konten dalam piksel, atau 0 jika pohon kosong.
     */
    public int getContentHeight(TreeSnapshot snapshot) {
        if (snapshot.root == null) return 0;
        int depth = calcDepth(snapshot.root);
        return 50 + (depth - 1) * gapY + nodeHeight + 30;
    }

    /**
     * Menghitung kedalaman pohon (jumlah level), dihitung dari leaf sebagai 1.
     */
    private int calcDepth(NodeSnapshot node) {
        if (node.children.isEmpty()) return 1;
        int max = 0;
        for (NodeSnapshot c : node.children) {
            max = Math.max(max, calcDepth(c));
        }
        return max + 1;
    }

    /**
     * Menghitung lebar yang dibutuhkan sebuah subtree secara rekursif.
     *
     * <p>Lebar subtree adalah nilai maksimum antara:</p>
     * <ul>
     *   <li>lebar node itu sendiri, dan</li>
     *   <li>total lebar seluruh anak beserta jarak antar-anaknya.</li>
     * </ul>
     * <p>Dengan aturan ini, node dengan banyak kunci tetapi sedikit anak tetap
     * mendapat ruang yang cukup, dan sebaliknya.</p>
     */
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

    /**
     * Menempatkan node pada posisi tertentu dan secara rekursif menempatkan
     * seluruh anaknya.
     *
     * <p>Node dipusatkan secara horizontal di atas area yang ditempati
     * anak-anaknya. Anak-anak kemudian ditempatkan berurutan dari kiri ke
     * kanan, masing-masing di tengah lebar subtreenya sendiri.</p>
     *
     * @param node   node yang akan ditempatkan.
     * @param x      koordinat x area yang disediakan untuk subtree ini.
     * @param y      koordinat y untuk node ini (menentukan levelnya).
     * @param bounds peta tujuan tempat hasil posisi disimpan.
     */
    private void assignPosition(NodeSnapshot node, int x, int y, Map<UUID, Rectangle> bounds) {
        int sw = calcSubtreeWidth(node);
        int nodeWidthHere = nodeWidth + (node.keys.size() * 30);

        // Pusatkan node di dalam area subtree-nya.
        int nodeX = x + (sw - nodeWidthHere) / 2;
        bounds.put(node.id, new Rectangle(nodeX, y, nodeWidthHere, nodeHeight));

        if (node.children.isEmpty()) return;

        // Hitung total lebar seluruh anak beserta jarak antar-anaknya.
        int totalChildrenWidth = 0;
        for (NodeSnapshot child : node.children) {
            totalChildrenWidth += calcSubtreeWidth(child) + gapX;
        }
        totalChildrenWidth -= gapX;

        // Mulai dari posisi x yang membuat kelompok anak terpusat di bawah node.
        int currentX = x + (sw - totalChildrenWidth) / 2;

        for (NodeSnapshot child : node.children) {
            assignPosition(child, currentX, y + gapY, bounds);
            currentX += calcSubtreeWidth(child) + gapX;
        }
    }
}