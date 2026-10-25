/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */


import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class NodeSnapshot {
    public final UUID id;
    public final List<String> keys; // Tipe String
    public final List<NodeSnapshot> children;
    public final boolean isLeaf;

    public NodeSnapshot(UUID id, List<String> keys, List<NodeSnapshot> children, boolean isLeaf) {
        this.id = id;
        this.keys = Collections.unmodifiableList(keys);
        this.children = Collections.unmodifiableList(children);
        this.isLeaf = isLeaf;
    }
}