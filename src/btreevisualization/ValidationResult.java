package btreevisualization;

import java.util.List;

/**
 * Hasil validasi input yang dihasilkan oleh {@link InputValidator}.
 *
 * <p>Kelas ini adalah data container sederhana yang menyampaikan tiga hal
 * sekaligus dari validator ke pemanggil:</p>
 * <ul>
 *   <li>Apakah input valid ({@link #valid}).</li>
 *   <li>Pesan untuk ditampilkan ke pengguna ({@link #message}) — berisi alasan
 *       kegagalan ketika tidak valid, atau teks "OK" ketika berhasil.</li>
 *   <li>Daftar kunci yang sudah dinormalisasi ({@link #keys}) — siap
 *       diteruskan ke {@link BTree}, atau {@code null} ketika tidak valid.</li>
 * </ul>
 *
 * <p>Dengan menggabungkan ketiganya dalam satu objek, pemanggil seperti
 * {@link BTreeController} dapat langsung memeriksa {@link #valid}, dan jika
 * berhasil langsung membaca {@link #keys}, tanpa perlu memanggil validator dua
 * kali. Pola ini juga memudahkan pengujian: satu objek mewakili satu hasil
 * lengkap.</p>
 *
 * @author Edison Siahaan_L
 */
public class ValidationResult {

    /** {@code true} jika input lolos validasi. */
    public final boolean valid;

    /**
     * Pesan untuk pengguna: alasan kegagalan ketika {@link #valid} bernilai
     * {@code false}, atau teks "OK" ketika berhasil.
     */
    public final String message;

    /**
     * Daftar kunci yang sudah dinormalisasi (misalnya berformat tiga digit
     * untuk mode numerik), atau {@code null} jika validasi gagal.
     */
    public final List<String> keys;

    /**
     * Membuat hasil validasi baru.
     *
     * @param valid   {@code true} jika input lolos validasi.
     * @param message pesan untuk pengguna.
     * @param keys    daftar kunci yang sudah dinormalisasi, atau {@code null}
     *                jika tidak valid.
     */
    public ValidationResult(boolean valid, String message, List<String> keys) {
        this.valid = valid;
        this.message = message;
        this.keys = keys;
    }
}