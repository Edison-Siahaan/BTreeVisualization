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

public class StepRecorder implements BTreeListener {
    private List<AnimationStep> steps = new ArrayList<>();

    @Override
    public void onEvent(BTreeEvent event) {
        StepType type;
        switch (event.type) {
            case VISIT_NODE: type = StepType.HIGHLIGHT; break;
            case KEY_INSERTED: type = StepType.INSERT_KEY; break;
            case NODE_SPLIT: type = StepType.SPLIT; break;
            case DELETE_KEY: type = StepType.DELETE; break;
            case BORROW: type = StepType.BORROW; break;
            case MERGE: type = StepType.MERGE; break;
            default: type = StepType.HIGHLIGHT; break;
        }
        steps.add(new AnimationStep(type, event.message, event.affectedIds, event.snapshot, event.activeKey));
    }

    public List<AnimationStep> getRecordedSteps() { return steps; }
    public void clear() { steps.clear(); }
}