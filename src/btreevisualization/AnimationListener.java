package btreevisualization;

/**
 * Listener untuk menerima callback dari {@link AnimationManager} selama proses
 * animasi berlangsung.
 *
 * <p>Implementasi antarmuka ini biasanya dilakukan oleh komponen UI (misalnya
 * {@code BTreeVisualization}) agar dapat memperbarui tampilan pohon dan status
 * kontrol setiap kali satu langkah animasi dieksekusi atau animasi selesai.</p>
 *
 * @author Edison Siahaan_L
 */
public interface AnimationListener {

    /**
     * Dipanggil setiap kali satu langkah animasi siap ditampilkan.
     *
     * @param step langkah animasi yang berisi tipe, pesan, ID node yang
     *             di-highlight, snapshot pohon, dan kunci aktif (opsional).
     */
    void onStep(AnimationStep step);

    /**
     * Dipanggil setelah seluruh langkah animasi selesai dieksekusi.
     * Digunakan untuk mengembalikan status UI (misalnya mengaktifkan kembali
     * tombol input, menyembunyikan indikator pause, atau menampilkan hasil
     * pencarian).
     */
    void onFinished();
}