package btreevisualization;

/**
 * Jenis langkah animasi yang dapat direkam oleh {@link StepRecorder} dan
 * diputar oleh {@link AnimationManager}.
 *
 * <p>Enum ini adalah pasangan dari {@link BTreeEventType} di sisi model:
 * event pohon dipetakan menjadi {@code StepType} sebelum dirender oleh
 * {@link BTreePanel}. Pemisahan ini penting karena tidak semua langkah yang
 * ditampilkan ke pengguna berasal langsung dari operasi pohon — misalnya
 * {@link #FINISHED} ditambahkan oleh {@link BTreeController} setelah seluruh
 * langkah operasi selesai, bukan dipancarkan oleh {@link BTree}.</p>
 *
 * <p>Pemetaan dari {@link BTreeEventType} yang dilakukan oleh
 * {@link StepRecorder}:</p>
 * <ul>
 *   <li>{@code VISIT_NODE} → {@link #HIGHLIGHT}</li>
 *   <li>{@code KEY_INSERTED} → {@link #INSERT_KEY}</li>
 *   <li>{@code NODE_SPLIT} → {@link #SPLIT}</li>
 *   <li>{@code DELETE_KEY} → {@link #DELETE}</li>
 *   <li>{@code BORROW} → {@link #BORROW}</li>
 *   <li>{@code MERGE} → {@link #MERGE}</li>
 * </ul>
 *
 * @author Edison Siahaan_L
 */
public enum StepType {

    /**
     * Sebuah node sedang dikunjungi atau dibandingkan dengan kunci yang dicari.
     * Dipakai juga untuk menandai jalur pencarian di {@link BTreePanel}.
     */
    HIGHLIGHT,

    /** Kunci baru berhasil disisipkan ke dalam sebuah node. */
    INSERT_KEY,

    /** Node dipecah karena jumlah kunci melebihi batas maksimum. */
    SPLIT,

    /** Kunci dihapus dari sebuah node. */
    DELETE,

    /** Node meminjam kunci dari saudara kiri atau kanannya untuk mengatasi underflow. */
    BORROW,

    /** Dua node digabungkan menjadi satu untuk mengatasi underflow. */
    MERGE,

    /**
     * Langkah penutup yang ditambahkan sendiri oleh {@link BTreeController}
     * setelah operasi selesai. Tidak berasal dari event pohon; membawa pesan
     * ringkasan (misalnya hasil pencarian) dan snapshot kondisi akhir pohon.
     */
    FINISHED
}