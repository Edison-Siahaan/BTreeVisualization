/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */
import java.util.ArrayDeque;
import java.util.Deque;

public class UndoRedoManager {
    private Deque<TreeSnapshot> undoStack = new ArrayDeque<>();
    private Deque<TreeSnapshot> redoStack = new ArrayDeque<>();
    private int maxHistory;

    public UndoRedoManager(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    public void saveState(TreeSnapshot snapshot) {
        if (snapshot == null) return;
        undoStack.push(snapshot);
        if (undoStack.size() > maxHistory) undoStack.removeLast();
        redoStack.clear();
    }

    public TreeSnapshot undo(TreeSnapshot current) {
        if (undoStack.isEmpty()) return null;
        redoStack.push(current);
        return undoStack.pop();
    }

    public TreeSnapshot redo(TreeSnapshot current) {
        if (redoStack.isEmpty()) return null;
        undoStack.push(current);
        return redoStack.pop();
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }
    
    // Metode baru untuk mereset riwayat
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}