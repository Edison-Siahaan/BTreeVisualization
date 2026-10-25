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

public class AnimationStep {
    public StepType type;
    public String message;
    public Set<UUID> highlightIds;
    public TreeSnapshot snapshot;
    public String activeKey; // Tipe String

    public AnimationStep(StepType type, String msg, Set<UUID> ids, TreeSnapshot snap) {
        this(type, msg, ids, snap, null);
    }

    public AnimationStep(StepType type, String msg, Set<UUID> ids, TreeSnapshot snap, String activeKey) {
        this.type = type; 
        this.message = msg; 
        this.highlightIds = ids; 
        this.snapshot = snap;
        this.activeKey = activeKey;
    }
}