package btreevisualization;

import java.util.ArrayList;
import java.util.List;

/**
 * Merekam setiap {@link BTreeEvent} yang dipancarkan oleh {@link BTree} dan
 * mengubahnya menjadi {@link AnimationStep} agar dapat diputar kembali oleh
 * {@link AnimationManager}.
 *
 * <p>{@code StepRecorder} berperan sebagai jembatan antara model pohon dan
 * lapisan animasi:</p>
 * <ol>
 *   <li>{@link BTree} memancarkan event pada setiap langkah penting.</li>
 *   <li>{@code StepRecorder} memetakan {@link BTreeEventType} ke
 *       {@link StepType} yang sesuai, lalu menyimpannya ke dalam daftar
 *       {@link #steps}.</li>
 *   <li>{@link BTreeController} memanggil {@link #getRecordedSteps()} setelah
 *       operasi selesai dan meneruskannya ke {@link AnimationManager}.</li>
 * </ol>
 *
 * <p>Siklus hidup yang umum: {@link #clear()} dipanggil sebelum operasi baru
 * dimulai, dan {@link #getRecordedSteps()} dibaca setelah operasi selesai
 * untuk memulai animasi.</p>
 *
 * @author Edison Siahaan_L
 */
public class StepRecorder implements BTreeListener {

    /** Daftar langkah animasi yang terekam sejak pemanggilan {@link #clear()} terakhir. */
    private final List<AnimationStep> steps = new ArrayList<>();

    /**
     * Menerjemahkan event pohon menjadi langkah animasi dan menambahkannya ke
     * daftar rekaman.
     *
     * <p>Pemetaan tipe event ke tipe langkah:</p>
     * <ul>
     *   <li>{@link BTreeEventType#VISIT_NODE} → {@link StepType#HIGHLIGHT}</li>
     *   <li>{@link BTreeEventType#KEY_INSERTED} → {@link StepType#INSERT_KEY}</li>
     *   <li>{@link BTreeEventType#NODE_SPLIT} → {@link StepType#SPLIT}</li>
     *   <li>{@link BTreeEventType#DELETE_KEY} → {@link StepType#DELETE}</li>
     *   <li>{@link BTreeEventType#BORROW} → {@link StepType#BORROW}</li>
     *   <li>{@link BTreeEventType#MERGE} → {@link StepType#MERGE}</li>
     * </ul>
     *
     * <p>Cabang {@code default} bersifat defensif: saat ini semua nilai
     * {@link BTreeEventType} sudah ditangani, tetapi jika enum bertambah di
     * masa depan, event tak dikenal akan dipetakan ke {@link StepType#HIGHLIGHT}
     * alih-alih dilewati.</p>
     *
     * @param event event yang dipancarkan oleh pohon.
     */
    @Override
    public void onEvent(BTreeEvent event) {
        StepType type;
        switch (event.type) {
            case VISIT_NODE:   type = StepType.HIGHLIGHT;  break;
            case KEY_INSERTED: type = StepType.INSERT_KEY; break;
            case NODE_SPLIT:   type = StepType.SPLIT;      break;
            case DELETE_KEY:   type = StepType.DELETE;     break;
            case BORROW:       type = StepType.BORROW;     break;
            case MERGE:        type = StepType.MERGE;      break;
            default:           type = StepType.HIGHLIGHT;  break;
        }
        steps.add(new AnimationStep(type, event.message, event.affectedIds,
                event.snapshot, event.activeKey));
    }

    /**
     * @return daftar langkah animasi yang telah terekam. Daftar ini adalah
     *         referensi langsung ke koleksi internal, sehingga pemanggil
     *         sebaiknya tidak memodifikasinya; gunakan {@link #clear()} untuk
     *         mengosongkan rekaman.
     */
    public List<AnimationStep> getRecordedSteps() {
        return steps;
    }

    /**
     * Mengosongkan seluruh rekaman langkah.
     * Dipanggil sebelum memulai operasi baru agar animasi yang diputar hanya
     * mencakup langkah-langkah dari operasi tersebut.
     */
    public void clear() {
        steps.clear();
    }
}