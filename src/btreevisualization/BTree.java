package btreevisualization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Implementasi struktur data B-Tree yang mendukung operasi sisip, cari, dan
 * hapus, sekaligus memancarkan event ke {@link BTreeListener} pada setiap
 * langkah penting agar dapat divisualisasikan.
 *
 * <p>B-Tree di sini bersifat umum untuk orde berapa pun (minimal 3) dengan
 * ketentuan:</p>
 * <ul>
 *   <li>{@code maxKeys = order - 1}</li>
 *   <li>{@code minKeys = ceil(order / 2) - 1}</li>
 * </ul>
 *
 * <p>Kunci disimpan sebagai {@link String}. Saat mode numerik dipakai,
 * kunci dinormalisasi menjadi format tiga digit (misalnya {@code 007}) agar
 * perbandingan leksikografis konsisten dengan urutan numerik. Untuk tampilan
 * UI, padding nol di depan dihilangkan kembali melalui {@link #fmt(String)}.</p>
 *
 * <p>Setiap event yang dipancarkan membawa {@link TreeSnapshot} berisi kondisi
 * pohon saat itu, sehingga listener (misalnya {@link StepRecorder}) dapat
 * merekam langkah animasi tanpa perlu mengakses struktur internal.</p>
 *
 * @author Edison Siahaan_L
 */
public class BTree {

    /** Node akar pohon. {@code null} jika pohon kosong. */
    private BTreeNode root;

    /** Orde maksimum pohon (jumlah anak maksimum per node). */
    private final int order;

    /** Jumlah kunci maksimum per node ({@code order - 1}). */
    private final int maxKeys;

    /** Jumlah kunci minimum per node (kecuali root). */
    private final int minKeys;

    /** Daftar listener yang menerima notifikasi setiap event. */
    private final List<BTreeListener> listeners = new ArrayList<>();

    /**
     * Kunci yang sedang diproses pada operasi aktif. Nilainya dipakai untuk
     * menyorot kunci pada visualisasi dan di-reset ke {@code null} setelah
     * operasi selesai.
     */
    private String currentActiveKey = null;

    /**
     * Membuat B-Tree baru dengan orde tertentu.
     *
     * @param order orde pohon, minimal 3.
     * @throws IllegalArgumentException jika {@code order < 3}.
     */
    public BTree(int order) {
        if (order < 3) throw new IllegalArgumentException("Orde minimal 3");
        this.order = order;
        this.maxKeys = order - 1;
        this.minKeys = (int) Math.ceil(order / 2.0) - 1;
    }

    /**
     * Mendaftarkan listener yang akan menerima event dari pohon ini.
     *
     * @param listener listener yang ingin menerima notifikasi event.
     */
    public void addListener(BTreeListener listener) {
        listeners.add(listener);
    }

    /**
     * Menghilangkan padding nol di depan pada kunci numerik agar tampilan UI
     * lebih mudah dibaca (misalnya {@code "007"} menjadi {@code "7"}).
     * Kunci non-numerik dikembalikan apa adanya.
     *
     * @param key kunci yang akan diformat.
     * @return kunci tanpa padding nol di depan.
     */
    private String fmt(String key) {
        if (key != null && key.matches("^0\\d+$")) {
            return key.replaceFirst("^0+(?!$)", "");
        }
        return key;
    }

    /**
     * Memancarkan event dengan kunci aktif saat ini.
     *
     * @see #fireEvent(BTreeEventType, HashSet, String, String)
     */
    private void fireEvent(BTreeEventType type, HashSet<UUID> ids, String msg) {
        fireEvent(type, ids, msg, currentActiveKey);
    }

    /**
     * Membuat snapshot pohon dan meneruskan event ke seluruh listener.
     *
     * <p>Jika tidak ada listener terdaftar, pembuatan snapshot dilewati untuk
     * menghindari biaya yang tidak perlu (misalnya saat unit test performa
     * tanpa visualisasi).</p>
     *
     * @param type      tipe event.
     * @param ids       ID node yang terpengaruh.
     * @param msg       pesan deskriptif untuk log.
     * @param activeKey kunci yang sedang aktif (boleh {@code null}).
     */
    private void fireEvent(BTreeEventType type, HashSet<UUID> ids, String msg, String activeKey) {
        if (listeners.isEmpty()) return;

        TreeSnapshot snap = createSnapshot();
        BTreeEvent event = new BTreeEvent(type, ids, msg, snap, activeKey);
        for (BTreeListener l : listeners) l.onEvent(event);
    }

    /**
     * Menyisipkan kunci baru ke dalam pohon.
     * Jika kunci sudah ada, operasi diabaikan.
     *
     * @param key kunci yang akan disisipkan.
     */
    public void insert(String key) {
        if (contains(key)) return;

        this.currentActiveKey = key;
        try {
            // Kasus khusus: pohon kosong, kunci menjadi root baru.
            if (root == null) {
                root = new BTreeNode(true);
                root.keys.add(key);
                fireEvent(BTreeEventType.KEY_INSERTED,
                        new HashSet<>(Arrays.asList(root.id)),
                        "Membuat root baru, sisip " + fmt(key));
                return;
            }

            // Kasus umum: lakukan sisip rekursif. Jika root ikut terpecah,
            // buat root baru dengan median sebagai satu-satunya kunci.
            SplitResult split = insertRec(root, key);
            if (split != null) {
                BTreeNode newRoot = new BTreeNode(false);
                newRoot.keys.add(split.median);
                newRoot.children.add(root);
                newRoot.children.add(split.rightSibling);
                root = newRoot;
                fireEvent(BTreeEventType.NODE_SPLIT,
                        new HashSet<>(Arrays.asList(newRoot.id,
                                root.children.get(0).id,
                                root.children.get(1).id)),
                        "Root dipecah, median " + fmt(split.median) + " menjadi root baru");
            }
        } finally {
            this.currentActiveKey = null;
        }
    }

    /**
     * Sisip rekursif ke subtree yang sesuai.
     *
     * @return hasil split jika node ini terpecah, atau {@code null} jika tidak.
     */
    private SplitResult insertRec(BTreeNode node, String key) {
        fireEvent(BTreeEventType.VISIT_NODE,
                new HashSet<>(Arrays.asList(node.id)), "Mengunjungi node");

        // Kasus leaf: sisipkan langsung di posisi terurut.
        if (node.isLeaf) {
            int pos = 0;
            while (pos < node.keys.size() && key.compareTo(node.keys.get(pos)) > 0) pos++;
            node.keys.add(pos, key);
            fireEvent(BTreeEventType.KEY_INSERTED,
                    new HashSet<>(Arrays.asList(node.id)),
                    "Sisipkan " + fmt(key) + " ke leaf");
            if (node.keys.size() > maxKeys) return splitNode(node);
            return null;
        }

        // Kasus internal: cari anak yang tepat, lalu sisip rekursif.
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) i++;
        SplitResult childSplit = insertRec(node.children.get(i), key);

        // Jika anak terpecah, naikkan median ke node ini.
        if (childSplit != null) {
            node.keys.add(i, childSplit.median);
            node.children.add(i + 1, childSplit.rightSibling);
            fireEvent(BTreeEventType.NODE_SPLIT,
                    new HashSet<>(Arrays.asList(node.id, childSplit.rightSibling.id)),
                    "Split anak, median " + fmt(childSplit.median) + " naik ke parent");
            if (node.keys.size() > maxKeys) return splitNode(node);
        }
        return null;
    }

    /**
     * Memecah node yang kelebihan kunci menjadi dua node.
     *
     * <p>Median (kunci tengah) dikembalikan kepada pemanggil untuk dinaikkan
     * ke parent, sedangkan sisa kunci di kanan median dipindahkan ke node
     * saudara baru. Anak-anak node juga dibagi sesuai posisi median.</p>
     *
     * @param node node yang akan dipecah (jumlah kunci melebihi {@code maxKeys}).
     * @return hasil split berisi median dan node saudara kanan yang baru.
     */
    private SplitResult splitNode(BTreeNode node) {
        int medianIndex = node.keys.size() / 2;
        String median = node.keys.get(medianIndex);
        BTreeNode right = new BTreeNode(node.isLeaf);

        // Pindahkan kunci di kanan median ke node saudara baru.
        for (int i = medianIndex + 1; i < node.keys.size(); i++) right.keys.add(node.keys.get(i));
        while (node.keys.size() > medianIndex) node.keys.remove(node.keys.size() - 1);

        // Jika internal, pindahkan juga anak-anak di kanan median.
        if (!node.isLeaf) {
            for (int i = medianIndex + 1; i < node.children.size(); i++) right.children.add(node.children.get(i));
            while (node.children.size() > medianIndex + 1) node.children.remove(node.children.size() - 1);
        }

        SplitResult result = new SplitResult();
        result.median = median;
        result.rightSibling = right;
        return result;
    }

    /**
     * Memeriksa keberadaan kunci di dalam pohon tanpa memancarkan event.
     * Dipakai sebagai pengecekan cepat sebelum operasi sisip.
     *
     * @param key kunci yang dicari.
     * @return {@code true} jika kunci ditemukan.
     */
    public boolean contains(String key) {
        return containsRec(root, key);
    }

    private boolean containsRec(BTreeNode node, String key) {
        if (node == null) return false;
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) i++;
        if (i < node.keys.size() && key.equals(node.keys.get(i))) return true;
        if (node.isLeaf) return false;
        return containsRec(node.children.get(i), key);
    }

    /**
     * Mencari kunci di dalam pohon sambil memancarkan event pada setiap node
     * yang dikunjungi dan setiap perbandingan kunci, sehingga proses pencarian
     * dapat dianimasikan.
     *
     * @param key kunci yang dicari.
     * @return {@code true} jika kunci ditemukan.
     */
    public boolean search(String key) {
        return searchRec(root, key);
    }

    private boolean searchRec(BTreeNode node, String key) {
        if (node == null) return false;
        fireEvent(BTreeEventType.VISIT_NODE,
                new HashSet<>(Arrays.asList(node.id)), "Mengunjungi node");

        for (int i = 0; i < node.keys.size(); i++) {
            String currentKey = node.keys.get(i);
            fireEvent(BTreeEventType.VISIT_NODE,
                    new HashSet<>(Arrays.asList(node.id)),
                    "Membandingkan " + fmt(key) + " dengan " + fmt(currentKey),
                    currentKey);

            if (key.equals(currentKey)) {
                fireEvent(BTreeEventType.VISIT_NODE,
                        new HashSet<>(Arrays.asList(node.id)),
                        "Kunci " + fmt(key) + " DITEMUKAN", currentKey);
                return true;
            } else if (key.compareTo(currentKey) < 0) {
                if (node.isLeaf) {
                    fireEvent(BTreeEventType.VISIT_NODE,
                            new HashSet<>(Arrays.asList(node.id)),
                            "Kunci " + fmt(key) + " TIDAK DITEMUKAN (Leaf)", null);
                    return false;
                }
                return searchRec(node.children.get(i), key);
            }
        }

        // Kunci lebih besar dari semua kunci di node: lanjut ke anak paling kanan.
        if (node.isLeaf) {
            fireEvent(BTreeEventType.VISIT_NODE,
                    new HashSet<>(Arrays.asList(node.id)),
                    "Kunci " + fmt(key) + " TIDAK DITEMUKAN (Leaf)", null);
            return false;
        }
        return searchRec(node.children.get(node.keys.size()), key);
    }

    /**
     * Menghapus kunci dari pohon (jika ada) sambil memancarkan event untuk
     * setiap langkah, termasuk penanganan underflow.
     *
     * <p>Setelah penghapusan, jika root tidak lagi memiliki kunci:</p>
     * <ul>
     *   <li>root internal diganti oleh anak tunggalnya, atau</li>
     *   <li>root leaf dihapus sehingga pohon menjadi kosong.</li>
     * </ul>
     *
     * @param key kunci yang akan dihapus.
     */
    public void delete(String key) {
        if (root == null) return;
        this.currentActiveKey = key;
        try {
            deleteRec(root, key);
            if (root.keys.isEmpty() && !root.isLeaf) root = root.children.get(0);
            else if (root.keys.isEmpty() && root.isLeaf) root = null;
        } finally {
            this.currentActiveKey = null;
        }
    }

    private void deleteRec(BTreeNode node, String key) {
        fireEvent(BTreeEventType.VISIT_NODE,
                new HashSet<>(Arrays.asList(node.id)), "Mencari kunci " + fmt(key));
        int idx = 0;
        while (idx < node.keys.size() && node.keys.get(idx).compareTo(key) < 0) idx++;

        // Kunci ditemukan di node ini.
        if (idx < node.keys.size() && node.keys.get(idx).equals(key)) {
            if (node.isLeaf) {
                // Kasus sederhana: hapus langsung dari leaf.
                node.keys.remove(idx);
                fireEvent(BTreeEventType.DELETE_KEY,
                        new HashSet<>(Arrays.asList(node.id)),
                        "Menghapus kunci " + fmt(key) + " dari leaf");
            } else {
                // Kasus internal: tukar dengan penerus (successor) di subtree kanan,
                // lalu hapus penerus tersebut dari leaf.
                BTreeNode succNode = getSuccessorNode(node, idx);
                String succValue = succNode.keys.get(0);

                succNode.keys.set(0, key);
                node.keys.set(idx, succValue);

                fireEvent(BTreeEventType.DELETE_KEY,
                        new HashSet<>(Arrays.asList(node.id, succNode.id)),
                        "Menukar kunci " + fmt(key) + " dengan penerusnya (" + fmt(succValue) + ")");

                BTreeNode childToVisit = node.children.get(idx + 1);
                deleteRec(childToVisit, key);
                if (childToVisit.keys.size() < minKeys) fixUnderflow(node, idx + 1);
            }
        } else {
            // Kunci tidak ada di node ini: lanjut ke anak yang sesuai.
            if (node.isLeaf) {
                fireEvent(BTreeEventType.VISIT_NODE,
                        new HashSet<>(Arrays.asList(node.id)),
                        "Kunci " + fmt(key) + " tidak ditemukan");
                return;
            }
            BTreeNode childToVisit = node.children.get(idx);
            deleteRec(childToVisit, key);
            if (childToVisit.keys.size() < minKeys) fixUnderflow(node, idx);
        }
    }

    /**
     * Mengambil node leaf yang berisi penerus langsung dari kunci pada posisi
     * {@code idx} (yaitu kunci terkecil di subtree kanan).
     */
    private BTreeNode getSuccessorNode(BTreeNode node, int idx) {
        BTreeNode curr = node.children.get(idx + 1);
        while (!curr.isLeaf) curr = curr.children.get(0);
        return curr;
    }

    /**
     * Menangani kondisi underflow pada anak {@code childIdx} dari {@code parent}.
     *
     * <p>Urutan strategi:</p>
     * <ol>
     *   <li>Pinjam dari saudara kiri jika memungkinkan.</li>
     *   <li>Pinjam dari saudara kanan jika memungkinkan.</li>
     *   <li>Jika keduanya tidak bisa, gabungkan (merge) dengan salah satu saudara.</li>
     * </ol>
     *
     * <p>Jika {@code parent} adalah root dan kosong, penanganan dilewati karena
     * tinggi pohon akan dikurangi di {@link #delete(String)}.</p>
     */
    private void fixUnderflow(BTreeNode parent, int childIdx) {
        if (childIdx < 0 || childIdx >= parent.children.size()) return;
        BTreeNode child = parent.children.get(childIdx);
        if (parent == root && parent.keys.isEmpty()) return;
        if (child.keys.size() >= minKeys) return;

        // Coba pinjam dari saudara kiri.
        if (childIdx > 0) {
            BTreeNode leftSib = parent.children.get(childIdx - 1);
            if (leftSib.keys.size() > minKeys) {
                child.keys.add(0, parent.keys.get(childIdx - 1));
                parent.keys.set(childIdx - 1, leftSib.keys.remove(leftSib.keys.size() - 1));
                if (!leftSib.isLeaf) child.children.add(0, leftSib.children.remove(leftSib.children.size() - 1));
                fireEvent(BTreeEventType.BORROW,
                        new HashSet<>(Arrays.asList(child.id, leftSib.id)),
                        "Meminjam kunci dari saudara kiri");
                return;
            }
        }

        // Coba pinjam dari saudara kanan.
        if (childIdx < parent.children.size() - 1) {
            BTreeNode rightSib = parent.children.get(childIdx + 1);
            if (rightSib.keys.size() > minKeys) {
                child.keys.add(parent.keys.get(childIdx));
                parent.keys.set(childIdx, rightSib.keys.remove(0));
                if (!rightSib.isLeaf) child.children.add(rightSib.children.remove(0));
                fireEvent(BTreeEventType.BORROW,
                        new HashSet<>(Arrays.asList(child.id, rightSib.id)),
                        "Meminjam kunci dari saudara kanan");
                return;
            }
        }

        // Kedua saudara juga minim: gabungkan dengan salah satunya.
        if (childIdx > 0) {
            // Merge dengan saudara kiri.
            BTreeNode leftSib = parent.children.get(childIdx - 1);
            leftSib.keys.add(parent.keys.remove(childIdx - 1));
            leftSib.keys.addAll(child.keys);
            leftSib.children.addAll(child.children);
            parent.children.remove(childIdx);
            fireEvent(BTreeEventType.MERGE,
                    new HashSet<>(Arrays.asList(leftSib.id, child.id)),
                    "Menggabungkan node dengan saudara kiri");
        } else {
            // Merge dengan saudara kanan.
            BTreeNode rightSib = parent.children.get(childIdx + 1);
            child.keys.add(parent.keys.remove(childIdx));
            child.keys.addAll(rightSib.keys);
            child.children.addAll(rightSib.children);
            parent.children.remove(childIdx + 1);
            fireEvent(BTreeEventType.MERGE,
                    new HashSet<>(Arrays.asList(child.id, rightSib.id)),
                    "Menggabungkan node dengan saudara kanan");
        }
    }

    /**
     * Membuat snapshot kondisi pohon saat ini.
     * Snapshot bersifat immutable dan aman disimpan untuk keperluan undo/redo
     * maupun rekaman animasi.
     *
     * @return snapshot pohon saat ini (root {@code null} jika pohon kosong).
     */
    public TreeSnapshot createSnapshot() {
        return new TreeSnapshot(createNodeSnapshot(root));
    }

    private NodeSnapshot createNodeSnapshot(BTreeNode node) {
        if (node == null) return null;
        List<NodeSnapshot> childSnaps = new ArrayList<>();
        for (BTreeNode child : node.children) childSnaps.add(createNodeSnapshot(child));
        return new NodeSnapshot(node.id, new ArrayList<>(node.keys), childSnaps, node.isLeaf);
    }

    /**
     * Memulihkan isi pohon dari sebuah snapshot.
     * Dipakai oleh fitur undo/redo. ID node dipertahankan agar highlight tetap
     * konsisten ketika snapshot dipulihkan.
     *
     * @param snapshot snapshot yang akan dipulihkan.
     */
    public void restoreFromSnapshot(TreeSnapshot snapshot) {
        this.root = restoreNode(snapshot.root);
    }

    private BTreeNode restoreNode(NodeSnapshot snap) {
        if (snap == null) return null;
        BTreeNode node = new BTreeNode(snap.isLeaf);
        node.id = snap.id;
        node.keys.addAll(snap.keys);
        for (NodeSnapshot childSnap : snap.children) node.children.add(restoreNode(childSnap));
        return node;
    }

    /**
     * Hasil pemecahan node: kunci median yang harus dinaikkan ke parent,
     * beserta node saudara kanan yang baru terbentuk.
     */
    private static class SplitResult {
        String median;
        BTreeNode rightSibling;
    }
}