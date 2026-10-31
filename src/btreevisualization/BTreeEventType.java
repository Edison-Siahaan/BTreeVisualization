package btreevisualization;

/**
 * Jenis-jenis event yang dapat dipancarkan oleh {@link BTree} selama operasi
 * pada pohon berlangsung.
 *
 * <p>Setiap nilai enum merepresentasikan satu kategori langkah yang penting
 * untuk divisualisasikan. {@link StepRecorder} memetakan setiap nilai enum ini
 * ke {@link StepType} agar dapat diputar oleh {@link AnimationManager}.</p>
 *
 * @author Edison Siahaan_L
 */
public enum BTreeEventType {

    /** Node sedang dikunjungi, misalnya saat traversal atau perbandingan kunci. */
    VISIT_NODE,

    /** Kunci baru berhasil disisipkan ke dalam sebuah node. */
    KEY_INSERTED,

    /** Node dipecah karena jumlah kunci melebihi batas maksimum. */
    NODE_SPLIT,

    /** Kunci dihapus dari sebuah node. */
    DELETE_KEY,

    /** Node meminjam kunci dari saudara kiri atau kanannya untuk mengatasi underflow. */
    BORROW,

    /** Dua node digabungkan menjadi satu untuk mengatasi underflow. */
    MERGE
}