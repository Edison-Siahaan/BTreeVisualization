/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import java.util.List;

public class AnimationManager {
    private Timer timer;
    private List<AnimationStep> steps;
    private int currentIndex;
    private int speedMs = 1000;
    private boolean running = false;
    private boolean paused = false;
    private AnimationListener listener;

    public AnimationManager() {
        timer = new Timer(speedMs, e -> processNextStep());
        timer.setInitialDelay(speedMs);
    }

    public void setListener(AnimationListener listener) { this.listener = listener; }

    public void playAnimation(List<AnimationStep> steps) {
        this.steps = steps;
        this.currentIndex = 0;
        this.running = true;
        this.paused = false;
        timer.setDelay(speedMs);
        timer.setInitialDelay(speedMs);
        
        // PERBAIKAN: Gunakan invokeLater agar UI sempat menyelesaikan action listener 
        // sebelum onFinished() dipanggil secara sinkron (misal saat pohon kosong).
        SwingUtilities.invokeLater(this::processNextStep);
    }

    private void processNextStep() {
        if (!running || paused) return;

        executeStep();

        if (currentIndex < steps.size()) {
            timer.start();
        } else {
            finish();
        }
    }

    private void executeStep() {
        if (currentIndex < steps.size()) {
            listener.onStep(steps.get(currentIndex));
            currentIndex++;
        }
    }

    private void finish() {
        running = false;
        paused = false;
        timer.stop();
        listener.onFinished();
    }

    public void setSpeed(int ms) { 
        this.speedMs = ms;
        timer.setDelay(ms);
        timer.setInitialDelay(ms);
    }

    public void stop() {
        running = false;
        paused = false;
        timer.stop();
    }

    public void togglePause() {
        if (!running) return;
        paused = !paused;
        if (paused) timer.stop();
        else timer.start();
    }

    public boolean isPaused() { return paused; }

    public void stepForward() {
        if (running && paused) {
            executeStep(); 
            if (currentIndex >= steps.size()) {
                finish();
            }
        }
    }
}