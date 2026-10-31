package btreevisualization;

import javax.swing.Timer;
import javax.swing.SwingUtilities;
import java.util.List;

/**
 * Pengendali pemutaran animasi langkah-langkah B-Tree.
 *
 * <p>{@code AnimationManager} menerima daftar {@link AnimationStep} yang telah
 * direkam oleh {@link StepRecorder}, lalu mengeksekusinya satu per satu dengan
 * jeda waktu tertentu menggunakan {@link javax.swing.Timer}. Setiap langkah
 * yang dieksekusi diteruskan ke {@link AnimationListener} agar UI dapat
 * memperbarui tampilan pohon.</p>
 *
 * <p>Fitur yang didukung:</p>
 * <ul>
 *   <li>Memutar ulang animasi dari awal melalui {@link #playAnimation(List)}.</li>
 *   <li>Mengubah kecepatan animasi saat berjalan melalui {@link #setSpeed(int)}.</li>
 *   <li>Menjeda/melanjutkan animasi melalui {@link #togglePause()}.</li>
 *   <li>Maju satu langkah saat animasi dijeda melalui {@link #stepForward()}.</li>
 *   <li>Menghentikan animasi secara paksa melalui {@link #stop()}.</li>
 * </ul>
 *
 * @author Edison Siahaan_L
 */
public class AnimationManager {

    /** Timer yang memicu eksekusi langkah animasi berikutnya. */
    private Timer timer;

    /** Daftar langkah animasi yang sedang diputar. */
    private List<AnimationStep> steps;

    /** Indeks langkah animasi yang akan dieksekusi berikutnya. */
    private int currentIndex;

    /** Jeda antar langkah animasi dalam milidetik. */
    private int speedMs = 1000;

    /** Menandakan apakah animasi sedang aktif (berjalan atau dijeda). */
    private boolean running = false;

    /** Menandakan apakah animasi sedang dijeda oleh pengguna. */
    private boolean paused = false;

    /** Penerima callback untuk setiap langkah dan saat animasi selesai. */
    private AnimationListener listener;

    /**
     * Membuat instance baru dengan kecepatan default 1000 ms per langkah.
     */
    public AnimationManager() {
        timer = new Timer(speedMs, e -> processNextStep());
        timer.setInitialDelay(speedMs);
    }

    /**
     * Menetapkan listener yang akan menerima callback animasi.
     *
     * @param listener objek yang mengimplementasikan {@link AnimationListener}.
     */
    public void setListener(AnimationListener listener) {
        this.listener = listener;
    }

    /**
     * Memulai pemutaran daftar langkah animasi dari awal.
     *
     * <p>Langkah pertama dijadwalkan melalui {@link SwingUtilities#invokeLater}
     * agar pemanggil (misalnya action listener tombol) sempat menyelesaikan
     * prosesnya terlebih dahulu. Ini penting ketika daftar langkah kosong atau
     * sangat pendek, sehingga {@link #onFinished()} tidak dipanggil secara
     * sinkron di tengah proses yang sedang berjalan.</p>
     *
     * @param steps daftar langkah animasi yang akan diputar.
     */
    public void playAnimation(List<AnimationStep> steps) {
        this.steps = steps;
        this.currentIndex = 0;
        this.running = true;
        this.paused = false;
        timer.setDelay(speedMs);
        timer.setInitialDelay(speedMs);

        SwingUtilities.invokeLater(this::processNextStep);
    }

    /**
     * Mengeksekusi langkah berikutnya lalu menjadwalkan langkah selanjutnya
     * jika masih ada. Jika sudah tidak ada langkah tersisa, animasi diakhiri.
     */
    private void processNextStep() {
        if (!running || paused) return;

        executeStep();

        if (currentIndex < steps.size()) {
            timer.start();
        } else {
            finish();
        }
    }

    /**
     * Mengambil langkah pada indeks saat ini dan meneruskannya ke listener,
     * kemudian menaikkan indeks ke langkah berikutnya.
     */
    private void executeStep() {
        if (currentIndex < steps.size()) {
            listener.onStep(steps.get(currentIndex));
            currentIndex++;
        }
    }

    /**
     * Mengakhiri animasi: menghentikan timer dan memberi tahu listener
     * bahwa seluruh langkah telah selesai diputar.
     */
    private void finish() {
        running = false;
        paused = false;
        timer.stop();
        listener.onFinished();
    }

    /**
     * Mengubah kecepatan animasi.
     *
     * @param ms jeda antar langkah dalam milidetik.
     */
    public void setSpeed(int ms) {
        this.speedMs = ms;
        timer.setDelay(ms);
        timer.setInitialDelay(ms);
    }

    /**
     * Menghentikan animasi secara paksa tanpa memanggil
     * {@link AnimationListener#onFinished()}.
     * Biasanya dipanggil sebelum memulai operasi baru.
     */
    public void stop() {
        running = false;
        paused = false;
        timer.stop();
    }

    /**
     * Menjeda atau melanjutkan animasi yang sedang berjalan.
     * Jika animasi tidak sedang berjalan, metode ini tidak melakukan apa pun.
     */
    public void togglePause() {
        if (!running) return;
        paused = !paused;
        if (paused) timer.stop();
        else timer.start();
    }

    /**
     * @return {@code true} jika animasi sedang dijeda.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Memajukan animasi sebanyak satu langkah.
     * Hanya berfungsi ketika animasi sedang berjalan dan dalam keadaan dijeda.
     * Jika langkah terakhir baru saja dieksekusi, animasi akan diakhiri.
     */
    public void stepForward() {
        if (running && paused) {
            executeStep();
            if (currentIndex >= steps.size()) {
                finish();
            }
        }
    }
}