/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btreevisualization;

/**
 *
 * @author Edison Siahaan_L
 */
import java.util.Set;
import java.util.UUID;

public class BTreeEvent {
    public BTreeEventType type;
    public Set<UUID> affectedIds;
    public String message;
    public TreeSnapshot snapshot;
    public String activeKey; // Tipe String

    public BTreeEvent(BTreeEventType type, Set<UUID> ids, String msg, TreeSnapshot snap) {
        this(type, ids, msg, snap, null);
    }

    public BTreeEvent(BTreeEventType type, Set<UUID> ids, String msg, TreeSnapshot snap, String activeKey) {
        this.type = type; 
        this.affectedIds = ids; 
        this.message = msg; 
        this.snapshot = snap;
        this.activeKey = activeKey;
    }
}