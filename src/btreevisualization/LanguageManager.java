package btreevisualization;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Pengelola pesan teks multi-bahasa (i18n) untuk aplikasi.
 *
 * <p>Pesan disimpan dalam file {@code messages_id.properties} dan
 * {@code messages_en.properties}. Kelas ini menyediakan akses statis ke
 * {@link ResourceBundle} aktif sehingga seluruh komponen UI dapat memanggil
 * {@link #get(String)} tanpa perlu menyimpan referensi bundle sendiri.</p>
 *
 * <p>Bahasa default saat aplikasi pertama kali dibuka adalah Bahasa Indonesia
 * ({@code "id"}). Mengubah bahasa melalui {@link #setLanguage(String)} akan
 * memuat ulang bundle; pemanggil bertanggung jawab memicu pembaruan teks UI
 * (lihat {@code BTreeVisualization.updateUITexts()}).</p>
 *
 * @author Edison Siahaan_L
 */
public class LanguageManager {

    /** Bundle pesan aktif sesuai bahasa yang dipilih. */
    private static ResourceBundle bundle;

    // Muat bahasa default saat kelas pertama kali dipakai.
    static {
        setLanguage("id");
    }

    /**
     * Mengganti bahasa aktif dengan memuat ulang {@link ResourceBundle} dari
     * file {@code messages_<locale>.properties}.
     *
     * <p>Jika file untuk locale tersebut tidak ditemukan, bundle lama tetap
     * dipertahankan dan pesan error dicetak ke {@code System.out}.</p>
     *
     * @param localeCode kode locale dua huruf, misalnya {@code "id"} atau {@code "en"}.
     */
    public static void setLanguage(String localeCode) {
        Locale locale = new Locale(localeCode);
        try {
            bundle = ResourceBundle.getBundle("messages", locale);
        } catch (Exception e) {
            System.out.println("Gagal memuat file bahasa untuk: " + localeCode);
        }
    }

    /**
     * Mengambil pesan untuk kunci tertentu pada bahasa aktif.
     *
     * <p>Jika kunci tidak ditemukan, dikembalikan penanda {@code !kunci!}
     * agar kesalahan terlihat jelas saat pengujian, bukan silently kosong.</p>
     *
     * @param key kunci pesan di file properties.
     * @return teks pesan, atau {@code !key!} jika tidak ditemukan.
     */
    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }
}