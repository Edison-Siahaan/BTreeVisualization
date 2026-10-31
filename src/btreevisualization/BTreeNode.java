package btreevisualization;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representasi satu node di dalam {@link BTree}.
 *
 * <p>Setiap node menyimpan daftar kunci terurut dan daftar anaknya. Untuk node
 * internal (bukan leaf), jumlah anak selalu sama dengan jumlah kunci ditambah
 * satu. Node leaf tidak memiliki anak.</p>
 *
 * <p>ID unik ({@link UUID}) diberikan pada setiap node agar dapat digunakan
 * sebagai referensi stabil untuk penyorotan (highlight) di UI. ID ini
 * dipertahankan ketika pohon dipulihkan dari {@link TreeSnapshot}, sehingga
 * animasi dan undo/redo tetap konsisten.</p>
 *
 * @author Edison Siahaan_L
 */
public class BTreeNode {

    /** ID unik node, dipakai untuk highlight dan referensi di UI. */
    public UUID id = UUID.randomUUID();

    /** Daftar kunci terurut secara leksikografis. */
    public List<String> keys = new ArrayList<>();

    /** Daftar anak node, kosong jika node ini adalah leaf. */
    public List<BTreeNode> children = new ArrayList<>();

    /** Menandakan apakah node ini adalah leaf (tanpa anak). */
    public boolean isLeaf;

    /**
     * Membuat node baru.
     *
     * @param isLeaf {@code true} jika node ini adalah leaf, {@code false} jika internal.
     */
    public BTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }
}