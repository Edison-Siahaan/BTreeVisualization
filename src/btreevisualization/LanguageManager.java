package btreevisualization;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
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
 * <p>Dua bentuk pengambilan pesan tersedia:</p>
 * <ul>
 *   <li>{@link #get(String)} — untuk pesan statis tanpa parameter.</li>
 *   <li>{@link #get(String, Object...)} — untuk pesan dengan placeholder
 *       {@code {0}}, {@code {1}}, dst. yang diisi melalui
 *       {@link MessageFormat}.</li>
 * </ul>
 *
 * <p>Jika kunci tidak ditemukan, dikembalikan penanda {@code !kunci!} agar
 * kesalahan terlihat jelas saat pengujian, bukan silently kosong. Jika
 * formatting argumen gagal (misalnya placeholder tidak cocok), pesan template
 * mentah tetap dikembalikan agar aplikasi tidak crash.</p>
 *
 * @author Edison Siahaan_L
 */
public class LanguageManager {

    /** Bundle pesan aktif sesuai bahasa yang dipilih. */
    private static ResourceBundle bundle;

    /** Kode locale aktif, disimpan agar bisa dipakai untuk reload/debug. */
    private static String currentLocale = "id";

    // Muat bahasa default saat kelas pertama kali dipakai.
    static {
        setLanguage(currentLocale);
    }

    /**
     * Mengganti bahasa aktif dengan memuat ulang {@link ResourceBundle} dari
     * file {@code messages_<locale>.properties}.
     *
     * <p>Jika file untuk locale tersebut tidak ditemukan, bundle lama tetap
     * dipertahankan dan pesan error dicetak ke {@code System.out}. Perilaku
     * ini menjaga aplikasi tetap berjalan meskipun file terjemahan hilang.</p>
     *
     * @param localeCode kode locale dua huruf, misalnya {@code "id"} atau {@code "en"}.
     */
    public static void setLanguage(String localeCode) {
        if (localeCode == null || localeCode.isEmpty()) return;
        Locale locale = new Locale(localeCode);
        try {
            bundle = ResourceBundle.getBundle("messages", locale);
            currentLocale = localeCode;
        } catch (MissingResourceException e) {
            System.out.println("Gagal memuat file bahasa untuk: " + localeCode);
        }
    }

    /**
     * @return kode locale yang sedang aktif, misalnya {@code "id"} atau {@code "en"}.
     */
    public static String getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Mengambil pesan statis (tanpa argumen) untuk kunci tertentu pada bahasa
     * aktif.
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

    /**
     * Mengambil pesan berparameter untuk kunci tertentu pada bahasa aktif.
     *
     * <p>Template pesan boleh berisi placeholder {@code {0}}, {@code {1}},
     * dst. (lihat dokumentasi {@link MessageFormat}). Argumen akan disisipkan
     * sesuai urutan.</p>
     *
     * <p>Contoh:</p>
     * <pre>{@code
     * // messages_id.properties: log.tree_reset=Pohon di-reset dengan Orde {0}
     * LanguageManager.get("log.tree_reset", 5);
     * // => "Pohon di-reset dengan Orde 5"
     * }</pre>
     *
     * <p>Jika {@code args} kosong atau {@code null}, hasilnya sama dengan
     * {@link #get(String)}. Jika formatting gagal (misalnya argumen tidak
     * sesuai placeholder), template mentah dikembalikan sebagai fallback agar
     * log/dialog tidak menampilkan pesan kosong.</p>
     *
     * @param key  kunci pesan di file properties.
     * @param args argumen untuk mengisi placeholder; boleh kosong.
     * @return teks pesan yang sudah terformat, atau {@code !key!} jika kunci
     *         tidak ditemukan.
     */
    public static String get(String key, Object... args) {
        String template = get(key);
        if (args == null || args.length == 0) return template;

        // Fallback: jika kunci tidak ditemukan, jangan coba format penanda !key!
        if (template.startsWith("!") && template.endsWith("!") && template.length() > 1) {
            return template;
        }
        try {
            return MessageFormat.format(template, args);
        } catch (IllegalArgumentException e) {
            // Placeholder tidak cocok dengan argumen; kembalikan template mentah
            // agar aplikasi tidak crash di tengah animasi.
            return template;
        }
    }
}