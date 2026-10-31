package btreevisualization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Penghubung antara UI, struktur {@link BTree}, dan mekanisme animasi.
 *
 * <p>{@code BTreeController} mengoordinasikan alur setiap operasi pengguna
 * (sisip, hapus, cari, batch, undo/redo) dengan komponen pendukung:</p>
 * <ul>
 *   <li>{@link InputValidator} — memvalidasi input sebelum diproses.</li>
 *   <li>{@link UndoRedoManager} — menyimpan snapshot sebelum mutasi pohon.</li>
 *   <li>{@link StepRecorder} — merekam event pohon menjadi langkah animasi.</li>
 *   <li>{@link AnimationManager} — memutar langkah animasi ke UI.</li>
 * </ul>
 *
 * <p>Alur umum sebuah operasi mutasi:</p>
 * <ol>
 *   <li>Validasi input.</li>
 *   <li>Hentikan animasi yang sedang berjalan.</li>
 *   <li>Simpan snapshot ke undo stack.</li>
 *   <li>Kosongkan rekaman langkah, lalu jalankan operasi pada pohon
 *       (event otomatis terekam oleh {@link StepRecorder}).</li>
 *   <li>Tambahkan langkah {@link StepType#FINISHED} secara manual.</li>
 *   <li>Putar ulang seluruh langkah melalui {@link AnimationManager}.</li>
 * </ol>
 *
 * <p>Operasi pencarian tidak mengubah pohon, sehingga tidak menyimpan state
 * undo/redo, tetapi tetap merekam langkah untuk animasi dan menyimpan hasil
 * pencarian terakhir agar dapat ditampilkan setelah animasi selesai.</p>
 *
 * @author Edison Siahaan_L
 */
public class BTreeController {

    /** Pohon yang sedang dikelola. */
    private BTree tree;

    /** Pengendali pemutaran animasi langkah. */
    private AnimationManager animManager;

    /** Perekam event pohon menjadi langkah animasi. */
    private StepRecorder stepRecorder;

    /** Pengelola riwayat undo/redo berbasis snapshot. */
    private UndoRedoManager undoRedoManager;

    /** Validator input pengguna. */
    private InputValidator validator;

    /** Hasil pencarian terakhir: {@code true} jika kunci ditemukan. */
    private boolean lastSearchFound = false;

    /** Kunci yang terakhir dicari. */
    private String lastSearchKey = "";

    /**
     * Membuat controller baru dengan dependensi yang diberikan.
     *
     * @param tree          pohon yang akan dikelola.
     * @param anim          pengendali animasi.
     * @param rec           perekam langkah animasi.
     * @param undo          pengelola undo/redo.
     * @param val           validator input.
     */
    public BTreeController(BTree tree, AnimationManager anim, StepRecorder rec,
                           UndoRedoManager undo, InputValidator val) {
        this.tree = tree;
        this.animManager = anim;
        this.stepRecorder = rec;
        this.undoRedoManager = undo;
        this.validator = val;
    }

    /**
     * Menetapkan listener yang akan menerima callback animasi.
     *
     * @param listener listener animasi (biasanya komponen UI utama).
     */
    public void setAnimationListener(AnimationListener listener) {
        this.animManager.setListener(listener);
    }

    /**
     * Mengaktifkan atau menonaktifkan mode numerik pada validator.
     *
     * @param isNumeric {@code true} untuk mode numerik, {@code false} untuk string.
     */
    public void setNumericMode(boolean isNumeric) {
        this.validator.setNumeric(isNumeric);
    }

    /**
     * @return {@code true} jika validator sedang dalam mode numerik.
     */
    public boolean isNumericMode() {
        return validator.isNumeric();
    }

    /**
     * Menghilangkan padding nol di depan pada kunci numerik untuk keperluan
     * tampilan pesan.
     */
    private String fmt(String key) {
        if (key != null && key.matches("^0\\d+$")) return key.replaceFirst("^0+(?!$)", "");
        return key;
    }

    /**
     * Menyisipkan satu kunci ke dalam pohon.
     *
     * @param rawInput input mentah dari pengguna.
     * @return hasil validasi; jika tidak valid, pesan error disertakan.
     */
    public ValidationResult insert(String rawInput) {
        ValidationResult res = validator.validateKey(rawInput);
        if (!res.valid) return res;

        String key = res.keys.get(0);

        if (tree.contains(key)) {
            return new ValidationResult(false,
                    "Kunci " + rawInput + " sudah ada di dalam pohon.", null);
        }

        animManager.stop();
        undoRedoManager.saveState(tree.createSnapshot());
        stepRecorder.clear();
        tree.insert(key);

        TreeSnapshot finalSnap = tree.createSnapshot();
        stepRecorder.getRecordedSteps().add(
                new AnimationStep(StepType.FINISHED, "Selesai", new HashSet<>(), finalSnap));
        animManager.playAnimation(stepRecorder.getRecordedSteps());
        return res;
    }

    /**
     * Menyisipkan beberapa kunci sekaligus dari input batch.
     * Kunci yang sudah ada di pohon dilewati.
     *
     * @param rawInput input batch (dipisah koma).
     * @return hasil validasi; {@code keys} berisi kunci yang benar-benar disisipkan.
     */
    public ValidationResult batchInsert(String rawInput) {
        ValidationResult res = validator.validateBatch(rawInput);
        if (!res.valid) return res;

        List<String> uniqueKeys = new ArrayList<>();
        for (String key : res.keys) {
            if (!tree.contains(key)) {
                uniqueKeys.add(key);
            }
        }

        if (uniqueKeys.isEmpty()) {
            return new ValidationResult(false,
                    "Semua kunci yang dimasukkan sudah ada di dalam pohon.", null);
        }

        animManager.stop();
        undoRedoManager.saveState(tree.createSnapshot());
        stepRecorder.clear();

        for (String key : uniqueKeys) {
            tree.insert(key);
        }

        TreeSnapshot finalSnap = tree.createSnapshot();
        stepRecorder.getRecordedSteps().add(
                new AnimationStep(StepType.FINISHED, "Batch Insert Selesai",
                        new HashSet<>(), finalSnap));
        animManager.playAnimation(stepRecorder.getRecordedSteps());
        return new ValidationResult(true, "OK", uniqueKeys);
    }

    /**
     * Menghapus beberapa kunci sekaligus dari input batch.
     *
     * @param rawInput input batch (dipisah koma).
     * @return hasil validasi.
     */
    public ValidationResult batchDelete(String rawInput) {
        ValidationResult res = validator.validateBatch(rawInput);
        if (!res.valid) return res;
        if (tree.createSnapshot().root == null)
            return new ValidationResult(false,
                    "Pohon kosong, tidak ada yang bisa dihapus.", null);

        animManager.stop();
        undoRedoManager.saveState(tree.createSnapshot());
        stepRecorder.clear();
        for (String key : res.keys) tree.delete(key);

        TreeSnapshot finalSnap = tree.createSnapshot();
        stepRecorder.getRecordedSteps().add(
                new AnimationStep(StepType.FINISHED, "Batch Delete Selesai",
                        new HashSet<>(), finalSnap));
        animManager.playAnimation(stepRecorder.getRecordedSteps());
        return res;
    }

    /**
     * Menghapus satu kunci dari pohon.
     *
     * @param rawInput input mentah dari pengguna.
     * @return hasil validasi.
     */
    public ValidationResult delete(String rawInput) {
        ValidationResult res = validator.validateKey(rawInput);
        if (!res.valid) return res;
        if (tree.createSnapshot().root == null)
            return new ValidationResult(false,
                    "Pohon kosong, tidak ada yang bisa dihapus.", null);

        String key = res.keys.get(0);
        animManager.stop();
        undoRedoManager.saveState(tree.createSnapshot());
        stepRecorder.clear();
        tree.delete(key);

        TreeSnapshot finalSnap = tree.createSnapshot();
        stepRecorder.getRecordedSteps().add(
                new AnimationStep(StepType.FINISHED, "Selesai", new HashSet<>(), finalSnap));
        animManager.playAnimation(stepRecorder.getRecordedSteps());
        return res;
    }

    /**
     * Mencari kunci di dalam pohon.
     *
     * <p>Tidak menyimpan state undo/redo karena tidak mengubah pohon.
     * Hasil pencarian disimpan di {@link #lastSearchFound} dan
     * {@link #lastSearchKey} agar dapat ditampilkan setelah animasi selesai.</p>
     *
     * @param rawInput input mentah dari pengguna.
     * @return hasil validasi.
     */
    public ValidationResult search(String rawInput) {
        ValidationResult res = validator.validateKey(rawInput);
        if (!res.valid) return res;
        if (tree.createSnapshot().root == null)
            return new ValidationResult(false,
                    "Pohon kosong, tidak ada yang bisa dicari.", null);

        String key = res.keys.get(0);
        lastSearchKey = key;
        animManager.stop();
        stepRecorder.clear();
        lastSearchFound = tree.search(key);

        TreeSnapshot finalSnap = tree.createSnapshot();
        String msg = lastSearchFound
                ? "Pencarian selesai: " + fmt(key) + " Ditemukan"
                : "Pencarian selesai: " + fmt(key) + " Tidak Ditemukan";
        stepRecorder.getRecordedSteps().add(
                new AnimationStep(StepType.FINISHED, msg, new HashSet<>(), finalSnap));
        animManager.playAnimation(stepRecorder.getRecordedSteps());
        return res;
    }

    /**
     * @return {@code true} jika pencarian terakhir menemukan kunci.
     */
    public boolean isLastSearchFound() {
        return lastSearchFound;
    }

    /**
     * @return kunci yang terakhir dicari.
     */
    public String getLastSearchKey() {
        return lastSearchKey;
    }

    /**
     * Mengembalikan pohon ke kondisi snapshot sebelumnya.
     *
     * @return snapshot yang dipulihkan, atau {@code null} jika tidak ada riwayat.
     */
    public TreeSnapshot undo() {
        animManager.stop();
        TreeSnapshot current = tree.createSnapshot();
        TreeSnapshot prev = undoRedoManager.undo(current);
        if (prev != null) tree.restoreFromSnapshot(prev);
        return prev;
    }

    /**
     * Memajukan pohon ke kondisi snapshot berikutnya (kebalikan dari undo).
     *
     * @return snapshot yang dipulihkan, atau {@code null} jika tidak ada riwayat.
     */
    public TreeSnapshot redo() {
        animManager.stop();
        TreeSnapshot current = tree.createSnapshot();
        TreeSnapshot next = undoRedoManager.redo(current);
        if (next != null) tree.restoreFromSnapshot(next);
        return next;
    }

    /**
     * @return {@code true} jika operasi undo masih tersedia.
     */
    public boolean canUndo() {
        return undoRedoManager.canUndo();
    }

    /**
     * @return {@code true} jika operasi redo masih tersedia.
     */
    public boolean canRedo() {
        return undoRedoManager.canRedo();
    }

    /**
     * Mengubah kecepatan animasi.
     *
     * @param ms jeda antar langkah dalam milidetik.
     */
    public void setSpeed(int ms) {
        animManager.setSpeed(ms);
    }

    /**
     * Menjeda atau melanjutkan animasi yang sedang berjalan.
     */
    public void togglePause() {
        animManager.togglePause();
    }

    /**
     * @return {@code true} jika animasi sedang dijeda.
     */
    public boolean isPaused() {
        return animManager.isPaused();
    }

    /**
     * Memajukan animasi sebanyak satu langkah (hanya saat dijeda).
     */
    public void stepForward() {
        animManager.stepForward();
    }

    /**
     * Mereset pohon dengan orde baru, sekaligus membersihkan riwayat langkah
     * animasi dan riwayat undo/redo. Listener {@link StepRecorder} dipasang
     * ulang pada pohon yang baru dibuat.
     *
     * @param newOrder orde baru untuk pohon.
     */
    public void resetTree(int newOrder) {
        animManager.stop();
        this.tree = new BTree(newOrder);
        this.stepRecorder.clear();
        this.undoRedoManager.clear();
        this.tree.addListener(stepRecorder);
    }

    /**
     * @return snapshot kondisi pohon saat ini.
     */
    public TreeSnapshot getCurrentSnapshot() {
        return tree.createSnapshot();
    }
}