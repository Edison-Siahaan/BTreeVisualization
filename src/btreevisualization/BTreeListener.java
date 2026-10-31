package btreevisualization;

/**
 * Antarmuka untuk menerima notifikasi event dari {@link BTree}.
 *
 * <p>Implementasi antarmuka ini akan dipanggil setiap kali {@link BTree}
 * memancarkan {@link BTreeEvent}, yaitu pada momen-momen penting selama operasi
 * pohon berlangsung: kunjungan node, penyisipan kunci, pemecahan node (split),
 * penghapusan kunci, peminjaman kunci (borrow), dan penggabungan node (merge).</p>
 *
 * <p>Implementasi utama dalam aplikasi ini adalah {@link StepRecorder}, yang
 * mengubah setiap event menjadi {@link AnimationStep} agar dapat diputar
 * kembali oleh {@link AnimationManager}. Namun antarmuka ini juga dapat
 * diimplementasikan oleh kelas lain, misalnya untuk logging, pengujian, atau
 * pemantauan performa tanpa visualisasi.</p>
 *
 * @author Edison Siahaan_L
 */
public interface BTreeListener {

    /**
     * Dipanggil setiap kali {@link BTree} memancarkan event.
     *
     * @param event event yang berisi tipe, node terpengaruh, pesan, snapshot
     *              pohon, dan kunci aktif (opsional).
     */
    void onEvent(BTreeEvent event);
}