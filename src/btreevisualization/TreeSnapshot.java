package btreevisualization;

/**
 * Potret (snapshot) kondisi sebuah {@link BTree} pada satu titik waktu
 * tertentu.
 *
 * <p>Sebuah {@code TreeSnapshot} pada dasarnya hanyalah pembungkus tipis di
 * sekitar {@link NodeSnapshot} akar. Seluruh subtree dapat diakses dengan
 * menelusuri {@link NodeSnapshot#children} secara rekursif. Karena
 * {@code NodeSnapshot} bersifat immutable, snapshot ini juga immutable dan
 * aman disimpan jangka panjang.</p>
 *
 * <p>Snapshot pohon digunakan di dua tempat utama:</p>
 * <ul>
 *   <li><b>Riwayat undo/redo</b> — {@link UndoRedoManager} menyimpan snapshot
 *       sebelum setiap mutasi agar dapat dipulihkan melalui
 *       {@link BTree#restoreFromSnapshot(TreeSnapshot)}.</li>
 *   <li><b>Rekaman animasi</b> — setiap {@link AnimationStep} membawa snapshot
 *       agar {@link BTreePanel} dapat merender kondisi pohon pada langkah
 *       tersebut tanpa bergantung pada state pohon yang sedang berubah.</li>
 * </ul>
 *
 * @author Edison Siahaan_L
 */
public class TreeSnapshot {

    /**
     * Node akar snapshot, atau {@code null} jika pohon kosong.
     * Field ini {@code final} karena snapshot tidak boleh diubah setelah
     * dibuat; untuk memulihkan pohon ke kondisi ini, gunakan
     * {@link BTree#restoreFromSnapshot(TreeSnapshot)}.
     */
    public final NodeSnapshot root;

    /**
     * Membuat snapshot pohon baru dari node akar yang diberikan.
     *
     * @param root snapshot akar pohon, atau {@code null} untuk merepresentasikan
     *             pohon kosong.
     */
    public TreeSnapshot(NodeSnapshot root) {
        this.root = root;
    }
}