package btreevisualization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validator input pengguna sebelum kunci diproses oleh {@link BTreeController}.
 *
 * <p>Validator mendukung dua mode:</p>
 * <ul>
 *   <li><b>Numerik</b> — input harus berupa bilangan bulat dalam rentang
 *       {@code [minKey, maxKey]}. Kunci dinormalisasi menjadi format tiga digit
 *       (misalnya {@code 7} menjadi {@code "007"}) agar perbandingan
 *       leksikografis pada B-Tree konsisten dengan urutan numerik.</li>
 *   <li><b>String</b> — input harus berupa 1–5 huruf alfabet (A–Z, a–z).
 *       Kunci disimpan apa adanya tanpa normalisasi.</li>
 * </ul>
 *
 * <p>Validator juga membatasi jumlah item dalam satu batch (dipisah koma)
 * melalui {@link #validateBatch(String)} untuk mencegah input yang terlalu
 * besar dalam satu operasi.</p>
 *
 * <p>Seluruh pesan kesalahan diambil dari {@link LanguageManager} agar
 * mengikuti bahasa aktif. Kunci pesan yang dipakai berawalan {@code val.}
 * (lihat {@code messages_id.properties} dan {@code messages_en.properties}).</p>
 *
 * @author Edison Siahaan_L
 */
public class InputValidator {

    /** Nilai numerik minimum yang diizinkan (hanya dipakai pada mode numerik). */
    private int minKey;

    /** Nilai numerik maksimum yang diizinkan (hanya dipakai pada mode numerik). */
    private int maxKey;

    /** Jumlah maksimum item dalam satu input batch. */
    private int maxBatch;

    /** Menandakan apakah validator sedang dalam mode numerik. */
    private boolean isNumeric = true;

    /**
     * Membuat validator baru.
     *
     * @param min   nilai numerik minimum.
     * @param max   nilai numerik maksimum.
     * @param batch jumlah maksimum item per batch.
     */
    public InputValidator(int min, int max, int batch) {
        this.minKey = min;
        this.maxKey = max;
        this.maxBatch = batch;
    }

    /**
     * Mengubah mode validasi.
     *
     * @param numeric {@code true} untuk mode numerik, {@code false} untuk mode string.
     */
    public void setNumeric(boolean numeric) {
        this.isNumeric = numeric;
    }

    /**
     * @return {@code true} jika validator sedang dalam mode numerik.
     */
    public boolean isNumeric() {
        return isNumeric;
    }

    /**
     * Memvalidasi satu kunci input.
     *
     * <p>Pada mode numerik, kunci yang valid dikembalikan dalam format tiga
     * digit (misalnya {@code "007"}). Pada mode string, kunci dikembalikan apa
     * adanya. Jika tidak valid, {@link ValidationResult#valid} bernilai
     * {@code false} dan {@link ValidationResult#message} berisi alasan
     * (sudah terlokalisasi).</p>
     *
     * @param text input mentah dari pengguna.
     * @return hasil validasi berisi kunci yang sudah dinormalisasi jika valid.
     */
    public ValidationResult validateKey(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ValidationResult(false, LanguageManager.get("val.empty"), null);
        }

        String cleanText = text.trim();

        if (isNumeric) {
            try {
                int val = Integer.parseInt(cleanText);
                if (val < minKey || val > maxKey) {
                    return new ValidationResult(false,
                            LanguageManager.get("val.out_of_range", minKey, maxKey),
                            null);
                }
                return new ValidationResult(true, "OK",
                        Arrays.asList(String.format("%03d", val)));
            } catch (NumberFormatException e) {
                return new ValidationResult(false,
                        LanguageManager.get("val.not_a_number"), null);
            }
        } else {
            // Mode string: hanya huruf alfabet, maksimal 5 karakter.
            if (!cleanText.matches("^[a-zA-Z]{1,5}$")) {
                return new ValidationResult(false,
                        LanguageManager.get("val.string_format"), null);
            }
            return new ValidationResult(true, "OK", Arrays.asList(cleanText));
        }
    }

    /**
     * Memvalidasi input batch yang berisi beberapa kunci dipisah koma.
     *
     * <p>Item kosong di antara koma dilewati, sehingga input seperti
     * {@code "1,,2,,"} tetap valid dan menghasilkan kunci {@code ["001", "002"]}.
     * Jika seluruh item kosong atau melebihi {@link #maxBatch}, hasil validasi
     * ditandai gagal dengan pesan yang sudah terlokalisasi.</p>
     *
     * @param text input mentah berupa daftar kunci dipisah koma.
     * @return hasil validasi berisi daftar kunci yang sudah dinormalisasi.
     */
    public ValidationResult validateBatch(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ValidationResult(false, LanguageManager.get("val.batch_empty"), null);
        }

        String[] parts = text.split(",");
        if (parts.length > maxBatch) {
            return new ValidationResult(false,
                    LanguageManager.get("val.batch_too_many", maxBatch), null);
        }

        List<String> keys = new ArrayList<>();
        for (String p : parts) {
            String clean = p.trim();
            if (clean.isEmpty()) continue;

            if (isNumeric) {
                try {
                    int val = Integer.parseInt(clean);
                    if (val < minKey || val > maxKey) {
                        return new ValidationResult(false,
                                LanguageManager.get("val.batch_value_out_of_range",
                                        val, minKey, maxKey),
                                null);
                    }
                    keys.add(String.format("%03d", val));
                } catch (NumberFormatException e) {
                    return new ValidationResult(false,
                            LanguageManager.get("val.batch_not_number", clean), null);
                }
            } else {
                if (!clean.matches("^[a-zA-Z]{1,5}$")) {
                    return new ValidationResult(false,
                            LanguageManager.get("val.batch_not_string", clean), null);
                }
                keys.add(clean);
            }
        }

        if (keys.isEmpty()) {
            return new ValidationResult(false, LanguageManager.get("val.batch_no_valid"), null);
        }
        return new ValidationResult(true, "OK", keys);
    }
}