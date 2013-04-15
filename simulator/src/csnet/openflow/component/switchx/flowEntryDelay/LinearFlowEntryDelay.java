package csnet.openflow.component.switchx.flowEntryDelay;

import csnet.openflow.component.switchx.flowTable.FlowTable;

public class LinearFlowEntryDelay extends AbstractFlowEntryDelay implements
        FlowEntryDelay {

    private double a;
    private double b;

    public LinearFlowEntryDelay(FlowTable flowTable) {
        super(flowTable);
    }

    public void config(int bufferSize, double a, double b) {
        config(bufferSize);
        this.a = a;
        this.b = b;
    }

    @Override
    protected double getWaitingTime() {
        return a * flowTable.size() + b;
    }
}
