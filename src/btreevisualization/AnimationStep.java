package btreevisualization;

import java.util.Set;
import java.util.UUID;

/**
 * Representasi satu langkah animasi B-Tree yang direkam oleh
 * {@link StepRecorder} dan diputar oleh {@link AnimationManager}.
 *
 * <p>Setiap langkah menyimpan tipe langkah, pesan untuk log, kumpulan ID node
 * yang perlu di-highlight, snapshot pohon pada saat langkah tersebut terjadi,
 * serta kunci aktif (jika ada) yang sedang dibandingkan atau diproses.</p>
 *
 * <p>Kelas ini adalah data container sederhana. Field dibuat publik agar
 * komponen UI dapat membacanya langsung tanpa getter tambahan.</p>
 *
 * @author Edison Siahaan_L
 */
public class AnimationStep {

    /** Tipe langkah animasi (misalnya highlight, insert, split, delete). */
    public StepType type;

    /** Pesan deskriptif yang ditampilkan pada log operasi. */
    public String message;

    /** ID node yang perlu di-highlight pada langkah ini. */
    public Set<UUID> highlightIds;

    /** Snapshot pohon pada saat langkah ini terjadi. */
    public TreeSnapshot snapshot;

    /**
     * Kunci yang sedang aktif dibandingkan atau diproses pada langkah ini,
     * atau {@code null} jika tidak ada kunci aktif.
     */
    public String activeKey;

    /**
     * Membuat langkah animasi tanpa kunci aktif.
     *
     * @param type tipe langkah animasi
     * @param msg  pesan deskriptif untuk log
     * @param ids  ID node yang di-highlight
     * @param snap snapshot pohon pada langkah ini
     */
    public AnimationStep(StepType type, String msg, Set<UUID> ids, TreeSnapshot snap) {
        this(type, msg, ids, snap, null);
    }

    /**
     * Membuat langkah animasi lengkap dengan kunci aktif.
     *
     * @param type      tipe langkah animasi
     * @param msg       pesan deskriptif untuk log
     * @param ids       ID node yang di-highlight
     * @param snap      snapshot pohon pada langkah ini
     * @param activeKey kunci yang sedang aktif, atau {@code null}
     */
    public AnimationStep(StepType type, String msg, Set<UUID> ids, TreeSnapshot snap, String activeKey) {
        this.type = type;
        this.message = msg;
        this.highlightIds = ids;
        this.snapshot = snap;
        this.activeKey = activeKey;
    }
}