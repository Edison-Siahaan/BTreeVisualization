package btreevisualization;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Potret (snapshot) kondisi satu node pada saat tertentu, digunakan untuk
 * keperluan visualisasi dan undo/redo.
 *
 * <p>{@code NodeSnapshot} berbeda dari {@link BTreeNode}: kelas ini bersifat
 * <b>immutable</b>. Daftar {@code keys} dan {@code children} dibungkus dengan
 * {@link Collections#unmodifiableList(List)} pada konstruktor, sehingga
 * snapshot yang sudah dibuat tidak dapat berubah meskipun pohon aslinya
 * dimodifikasi kemudian.</p>
 *
 * <p>Sifat immutable ini penting karena snapshot disimpan untuk:</p>
 * <ul>
 *   <li>Riwayat undo/redo melalui {@link UndoRedoManager}.</li>
 *   <li>Setiap {@link AnimationStep} yang direkam oleh {@link StepRecorder}
 *       — snapshot harus tetap konsisten meskipun animasi diputar ulang
 *       berkali-kali.</li>
 * </ul>
 *
 * <p>Struktur pohon snapshot bersifat rekursif: setiap {@code NodeSnapshot}
 * menyimpan daftar {@code NodeSnapshot} anak, membentuk salinan penuh pohon.</p>
 *
 * @author Edison Siahaan_L
 */
public class NodeSnapshot {

    /** ID unik node asal, dipakai untuk menyorot node yang sama di UI. */
    public final UUID id;

    /** Daftar kunci node, sudah tidak dapat diubah (immutable). */
    public final List<String> keys;

    /** Daftar anak node, sudah tidak dapat diubah (immutable). */
    public final List<NodeSnapshot> children;

    /** Menandakan apakah node ini adalah leaf pada saat snapshot dibuat. */
    public final boolean isLeaf;

    /**
     * Membuat snapshot node baru.
     *
     * <p>Daftar {@code keys} dan {@code children} yang diberikan akan dibungkus
     * menjadi view immutable. Pemanggil sebaiknya tidak lagi memodifikasi
     * daftar tersebut setelah pemanggilan konstruktor ini.</p>
     *
     * @param id       ID unik node.
     * @param keys     daftar kunci node (akan dibungkus menjadi immutable).
     * @param children daftar anak node (akan dibungkus menjadi immutable).
     * @param isLeaf   {@code true} jika node ini adalah leaf.
     */
    public NodeSnapshot(UUID id, List<String> keys, List<NodeSnapshot> children, boolean isLeaf) {
        this.id = id;
        this.keys = Collections.unmodifiableList(keys);
        this.children = Collections.unmodifiableList(children);
        this.isLeaf = isLeaf;
    }
}