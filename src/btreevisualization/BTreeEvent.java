package btreevisualization;

import java.util.Set;
import java.util.UUID;

/**
 * Representasi event yang dipancarkan oleh {@link BTree} setiap kali terjadi
 * langkah penting selama operasi pada pohon (kunjungan node, sisip, split,
 * hapus, pinjam, atau merge).
 *
 * <p>Event ini diteruskan ke seluruh {@link BTreeListener} terdaftar. Implementasi
 * umumnya ({@link StepRecorder}) mengubah event menjadi {@link AnimationStep}
 * agar dapat diputar kembali oleh {@link AnimationManager}.</p>
 *
 * <p>Setiap event membawa {@link TreeSnapshot} kondisi pohon saat event terjadi,
 * sehingga listener dapat menampilkan visualisasi tanpa mengakses struktur
 * internal pohon secara langsung.</p>
 *
 * <p>Kelas ini adalah data container sederhana; field dibuat publik agar
 * listener dapat membacanya tanpa getter tambahan.</p>
 *
 * @author Edison Siahaan_L
 */
public class BTreeEvent {

    /** Tipe event yang terjadi. */
    public BTreeEventType type;

    /** ID node yang terpengaruh oleh event ini. */
    public Set<UUID> affectedIds;

    /** Pesan deskriptif yang dapat ditampilkan pada log operasi. */
    public String message;

    /** Snapshot pohon pada saat event terjadi. */
    public TreeSnapshot snapshot;

    /**
     * Kunci yang sedang aktif diproses saat event terjadi, atau {@code null}
     * jika tidak ada kunci aktif (misalnya pada event split atau merge).
     */
    public String activeKey;

    /**
     * Membuat event tanpa kunci aktif.
     *
     * @param type tipe event
     * @param ids  ID node yang terpengaruh
     * @param msg  pesan deskriptif
     * @param snap snapshot pohon saat event terjadi
     */
    public BTreeEvent(BTreeEventType type, Set<UUID> ids, String msg, TreeSnapshot snap) {
        this(type, ids, msg, snap, null);
    }

    /**
     * Membuat event lengkap dengan kunci aktif.
     *
     * @param type      tipe event
     * @param ids       ID node yang terpengaruh
     * @param msg       pesan deskriptif
     * @param snap      snapshot pohon saat event terjadi
     * @param activeKey kunci yang sedang aktif, atau {@code null}
     */
    public BTreeEvent(BTreeEventType type, Set<UUID> ids, String msg, TreeSnapshot snap, String activeKey) {
        this.type = type;
        this.affectedIds = ids;
        this.message = msg;
        this.snapshot = snap;
        this.activeKey = activeKey;
    }
}