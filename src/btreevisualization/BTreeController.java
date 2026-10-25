/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BTreeController {
    private BTree tree;
    private AnimationManager animManager;
    private StepRecorder stepRecorder;
    private UndoRedoManager undoRedoManager;
    private InputValidator validator;
    
    private boolean lastSearchFound = false;
    private String lastSearchKey = "";

    public BTreeController(BTree tree, AnimationManager anim, StepRecorder rec, UndoRedoManager undo, InputValidator val) {
        this.tree = tree;
        this.animManager = anim;
        this.stepRecorder = rec;
        this.undoRedoManager = undo;
        this.validator = val;
    }

    public void setAnimationListener(AnimationListener listener) { this.animManager.setListener(listener); }
    
    public void setNumericMode(boolean isNumeric) { this.validator.setNumeric(isNumeric); }
    public boolean isNumericMode() { return validator.isNumeric(); }

    private String fmt(String key) {
        if (key != null && key.matches("^0\\d+$")) return key.replaceFirst("^0+(?!$)", "");
        return key;
    }

    public ValidationResult insert(String rawInput) {
        ValidationResult res = validator.validateKey(rawInput);
        if (!res.valid) return res;
        
        String key = res.keys.get(0);
        
        if (tree.contains(key)) {
            return new ValidationResult(false, "Kunci " + rawInput + " sudah ada di dalam pohon.", null);
        }

        animManager.stop();
        undoRedoManager.saveState(tree.createSnapshot());
        stepRecorder.clear();
        tree.insert(key);
        TreeSnapshot finalSnap = tree.createSnapshot();
        stepRecorder.getRecordedSteps().add(new AnimationStep(StepType.FINISHED, "Selesai", new HashSet<>(), finalSnap));
        animManager.playAnimation(stepRecorder.getRecordedSteps());
        return res;
    }
    
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
            return new ValidationResult(false, "Semua kunci yang dimasukkan sudah ada di dalam pohon.", null);
        }

        animManager.stop();
        undoRedoManager.saveState(tree.createSnapshot());
        stepRecorder.clear();
        
        for (String key : uniqueKeys) {
            tree.insert(key);
        }
        
        TreeSnapshot finalSnap = tree.createSnapshot();
        stepRecorder.getRecordedSteps().add(new AnimationStep(StepType.FINISHED, "Batch Insert Selesai", new HashSet<>(), finalSnap));
        animManager.playAnimation(stepRecorder.getRecordedSteps());
        return new ValidationResult(true, "OK", uniqueKeys);
    }
    
    public ValidationResult batchDelete(String rawInput) {
        ValidationResult res = validator.validateBatch(rawInput);
        if (!res.valid) return res;
        if (tree.createSnapshot().root == null) return new ValidationResult(false, "Pohon kosong, tidak ada yang bisa dihapus.", null);
        animManager.stop();
        undoRedoManager.saveState(tree.createSnapshot());
        stepRecorder.clear();
        for (String key : res.keys) tree.delete(key);
        TreeSnapshot finalSnap = tree.createSnapshot();
        stepRecorder.getRecordedSteps().add(new AnimationStep(StepType.FINISHED, "Batch Delete Selesai", new HashSet<>(), finalSnap));
        animManager.playAnimation(stepRecorder.getRecordedSteps());
        return res;
    }
    
    public ValidationResult delete(String rawInput) {
        ValidationResult res = validator.validateKey(rawInput);
        if (!res.valid) return res;
        if (tree.createSnapshot().root == null) return new ValidationResult(false, "Pohon kosong, tidak ada yang bisa dihapus.", null);
        String key = res.keys.get(0);
        animManager.stop();
        undoRedoManager.saveState(tree.createSnapshot());
        stepRecorder.clear();
        tree.delete(key);
        TreeSnapshot finalSnap = tree.createSnapshot();
        stepRecorder.getRecordedSteps().add(new AnimationStep(StepType.FINISHED, "Selesai", new HashSet<>(), finalSnap));
        animManager.playAnimation(stepRecorder.getRecordedSteps());
        return res;
    }
    
    public ValidationResult search(String rawInput) {
        ValidationResult res = validator.validateKey(rawInput);
        if (!res.valid) return res;
        if (tree.createSnapshot().root == null) return new ValidationResult(false, "Pohon kosong, tidak ada yang bisa dicari.", null);
        String key = res.keys.get(0);
        lastSearchKey = key;
        animManager.stop();
        stepRecorder.clear();
        lastSearchFound = tree.search(key);
        TreeSnapshot finalSnap = tree.createSnapshot();
        String msg = lastSearchFound ? "Pencarian selesai: " + fmt(key) + " Ditemukan" : "Pencarian selesai: " + fmt(key) + " Tidak Ditemukan";
        stepRecorder.getRecordedSteps().add(new AnimationStep(StepType.FINISHED, msg, new HashSet<>(), finalSnap));
        animManager.playAnimation(stepRecorder.getRecordedSteps());
        return res;
    }

    public boolean isLastSearchFound() { return lastSearchFound; }
    public String getLastSearchKey() { return lastSearchKey; }

    public TreeSnapshot undo() {
        animManager.stop();
        TreeSnapshot current = tree.createSnapshot();
        TreeSnapshot prev = undoRedoManager.undo(current);
        if (prev != null) tree.restoreFromSnapshot(prev);
        return prev;
    }

    public TreeSnapshot redo() {
        animManager.stop();
        TreeSnapshot current = tree.createSnapshot();
        TreeSnapshot next = undoRedoManager.redo(current);
        if (next != null) tree.restoreFromSnapshot(next);
        return next;
    }

    public boolean canUndo() { return undoRedoManager.canUndo(); }
    public boolean canRedo() { return undoRedoManager.canRedo(); }
    public void setSpeed(int ms) { animManager.setSpeed(ms); }
    public void togglePause() { animManager.togglePause(); }
    public boolean isPaused() { return animManager.isPaused(); }
    public void stepForward() { animManager.stepForward(); }
    
    public void resetTree(int newOrder) {
        animManager.stop();
        this.tree = new BTree(newOrder);
        this.stepRecorder.clear();
        this.undoRedoManager.clear();
        this.tree.addListener(stepRecorder);
    }

    public TreeSnapshot getCurrentSnapshot() { return tree.createSnapshot(); }
}