package csnet.openflow.component.switchx;

import csnet.openflow.component.Device;
import csnet.openflow.component.controller.Controller;
import csnet.openflow.component.interfacex.Interface;
import csnet.openflow.component.link.AbstractLink.Category;
import csnet.openflow.component.link.ConstantLatencyLink;
import csnet.openflow.component.link.Link;
import csnet.openflow.component.switchx.flowEntryDelay.FlowEntryDelay;
import csnet.openflow.component.switchx.flowTable.FlowTable;
import csnet.openflow.component.switchx.packetBuffer.PacketBuffer;
import csnet.openflow.logger.Logger;
import csnet.openflow.packet.model.Packet;
import java.util.List;

public class Switch implements Device {
    // private Link inLink;
    // private Link outLink;
    // private Link linkToController;
    // private Link linkFromController;

    private Interface inInterface;
    private Interface outInterface;
    private Interface toControllerInterface;
    private FlowEntryDelay flowEntryDelay;
    private PacketBuffer packetBuffer;
    private Controller controller;
    private FlowTable flowTable;
    private Link iToF;
    private Link fToO;
    private Link cToF;
    private Link fToC;

    public Switch() {
        iToF = new ConstantLatencyLink(Category.LIST, 0);
        fToO = new ConstantLatencyLink(Category.HEAP, 0);
        cToF = new ConstantLatencyLink(Category.LIST, 0);
        fToC = new ConstantLatencyLink(Category.LIST, 0);
    }

    public void connect(Link inLink, Link outLink, Link linkToController,
            Link linkFromController) {
        // this.inLink = inLink;
        // this.outLink = outLink;
        // this.linkToController = linkToController;
        // this.linkFromController = linkFromController;
        inInterface.connect(inLink, iToF);
        outInterface.connect(fToO, outLink);
        flowEntryDelay.connect(linkFromController, cToF);
        toControllerInterface.connect(fToC, linkToController);
    }

    @Override
    public boolean schedule(long timestamp) {
        inInterface.schedule(timestamp);
        scheduleFlowTable(timestamp);
        outInterface.schedule(timestamp);
        return false;
    }

    private void scheduleFlowTable(long timestamp) {
        iToF.setCurrentTime(timestamp);
        Packet packet = null;
        while ((packet = iToF.receive()) != null) {
            long current = packet.getTimestamp();
            flowTable.clear(current);
            toControllerInterface.schedule(current);
            installFlowEntries(current);
            if (flowTable.containsAndDelay(packet)) {
                fToO.send(packet);
            } else {
                packetBuffer.add(packet);
                fToC.send(packet);
            }
        }
    }

    private void installFlowEntries(long timestamp) {
        ;

        controller.schedule(timestamp);
        flowEntryDelay.schedule(timestamp);
        cToF.setCurrentTime(timestamp);
        Packet packet = null;
        while ((packet = cToF.receive()) != null) {
            flowTable.install(packet);
            List<Packet> toSent = packetBuffer.remove(packet);
            if (toSent != null) {
                long current = packet.getTimestamp();
                for (Packet p : toSent) {
                    p.setTimestamp(current);
                    p.statistic.flowIndex = packet.statistic.flowIndex;
                    fToO.send(p);
                }
            }
        }
    }

    public void setLogger(Logger logger) {
        flowTable.setLogger(logger);
    }

    public void setInInterface(Interface inInterface) {
        this.inInterface = inInterface;
    }

    public void setOutInterface(Interface outInterface) {
        this.outInterface = outInterface;
    }

    public void setFlowEntryDelay(FlowEntryDelay flowEntryDelay) {
        this.flowEntryDelay = flowEntryDelay;
    }

    public void setToControllerInterface(Interface toControllerInterface) {
        this.toControllerInterface = toControllerInterface;
    }

    public void setFlowTable(FlowTable flowTable) {
        this.flowTable = flowTable;
    }

    public void setPacketBuffer(PacketBuffer packetBuffer) {
        this.packetBuffer = packetBuffer;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
