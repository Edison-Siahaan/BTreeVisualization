package btreevisualization;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Pengelola riwayat undo/redo berbasis snapshot pohon.
 *
 * <p>Mekanisme yang dipakai adalah pola <b>state snapshot</b>: sebelum setiap
 * operasi mutasi, {@link BTreeController} memanggil {@link #saveState(TreeSnapshot)}
 * untuk menyimpan kondisi pohon saat itu. Untuk membatalkan operasi, controller
 * memanggil {@link #undo(TreeSnapshot)} dengan kondisi pohon saat ini; snapshot
 * sebelumnya dikembalikan dan kondisi saat ini didorong ke stack redo.</p>
 *
 * <p>Kedua stack memiliki semantik LIFO melalui {@link ArrayDeque}:</p>
 * <ul>
 *   <li>{@link #undoStack} — snapshot dari kondisi <em>sebelum</em> setiap
 *       operasi, diurutkan dari yang terbaru ke terlama.</li>
 *   <li>{@link #redoStack} — snapshot yang di-undo, diurutkan dari undo
 *       terbaru ke undo terlama.</li>
 * </ul>
 *
 * <p>Operasi {@link #saveState(TreeSnapshot)} <b>mengosongkan</b>
 * {@link #redoStack}, mengikuti perilaku undo/redo standar: setelah pengguna
 * melakukan operasi baru, riwayat redo tidak lagi relevan.</p>
 *
 * <p>Ukuran riwayat dibatasi oleh {@link #maxHistory}. Ketika melebihi batas,
 * snapshot terlama di {@link #undoStack} dibuang sehingga memori tetap
 * terkendali meskipun pengguna melakukan banyak operasi berturut-turut.</p>
 *
 * @author Edison Siahaan_L
 */
public class UndoRedoManager {

    /** Snapshot kondisi sebelum setiap operasi, terbaru di atas. */
    private final Deque<TreeSnapshot> undoStack = new ArrayDeque<>();

    /** Snapshot yang telah di-undo, terbaru di atas. */
    private final Deque<TreeSnapshot> redoStack = new ArrayDeque<>();

    /** Batas maksimum jumlah snapshot yang disimpan di {@link #undoStack}. */
    private final int maxHistory;

    /**
     * Membuat pengelola riwayat baru.
     *
     * @param maxHistory batas maksimum snapshot yang disimpan untuk undo.
     *                   Nilai kecil (misalnya 20) sudah cukup untuk kebutuhan
     *                   interaktif dan menjaga memori tetap ringan.
     */
    public UndoRedoManager(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    /**
     * Menyimpan snapshot kondisi pohon saat ini sebagai titik undo.
     *
     * <p>Dipanggil tepat sebelum operasi mutasi dijalankan. Metode ini juga
     * mengosongkan {@link #redoStack} karena operasi baru membatalkan riwayat
     * redo yang lama.</p>
     *
     * <p>Jika {@code snapshot} bernilai {@code null}, pemanggilan diabaikan.
     * Ini memungkinkan pemanggil meneruskan hasil {@code createSnapshot()}
     * secara langsung tanpa pengecekan tambahan.</p>
     *
     * @param snapshot kondisi pohon yang akan disimpan sebagai titik undo.
     */
    public void saveState(TreeSnapshot snapshot) {
        if (snapshot == null) return;
        undoStack.push(snapshot);
        if (undoStack.size() > maxHistory) undoStack.removeLast();
        redoStack.clear();
    }

    /**
     * Mengembalikan snapshot kondisi sebelum operasi terakhir.
     *
     * <p>Snapshot {@code current} (kondisi pohon saat ini) didorong ke
     * {@link #redoStack} agar dapat dikembalikan oleh {@link #redo(TreeSnapshot)}.</p>
     *
     * @param current kondisi pohon saat ini sebelum undo dijalankan.
     * @return snapshot yang harus dipulihkan, atau {@code null} jika riwayat
     *         undo kosong.
     */
    public TreeSnapshot undo(TreeSnapshot current) {
        if (undoStack.isEmpty()) return null;
        redoStack.push(current);
        return undoStack.pop();
    }

    /**
     * Memajukan pohon ke kondisi setelah operasi yang sebelumnya di-undo.
     *
     * <p>Snapshot {@code current} (kondisi pohon saat ini) didorong kembali ke
     * {@link #undoStack} agar operasi undo dapat diulang lagi.</p>
     *
     * @param current kondisi pohon saat ini sebelum redo dijalankan.
     * @return snapshot yang harus dipulihkan, atau {@code null} jika riwayat
     *         redo kosong.
     */
    public TreeSnapshot redo(TreeSnapshot current) {
        if (redoStack.isEmpty()) return null;
        undoStack.push(current);
        return redoStack.pop();
    }

    /**
     * @return {@code true} jika masih ada snapshot yang dapat di-undo.
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * @return {@code true} jika masih ada snapshot yang dapat di-redo.
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Menghapus seluruh riwayat undo dan redo.
     *
     * <p>Dipanggil ketika pohon di-reset (misalnya karena perubahan orde atau
     * tipe kunci), karena snapshot dari pohon lama tidak lagi valid untuk
     * dipulihkan ke pohon yang baru.</p>
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}