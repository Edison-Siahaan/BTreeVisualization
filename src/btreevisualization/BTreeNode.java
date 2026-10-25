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
import java.util.List;
import java.util.UUID;

public class BTreeNode {
    public UUID id = UUID.randomUUID();
    public List<String> keys = new ArrayList<>(); // Tipe String
    public List<BTreeNode> children = new ArrayList<>();
    public boolean isLeaf;

    public BTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }
}