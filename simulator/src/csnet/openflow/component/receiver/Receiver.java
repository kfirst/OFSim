package csnet.openflow.component.receiver;

import csnet.openflow.component.Device;
import csnet.openflow.component.link.Link;
import csnet.openflow.logger.Logger;
import csnet.openflow.packet.model.Packet;

public class Receiver implements Device {
	private Link input;
	private Logger logger;

	@Override
	public boolean schedule(long timestamp) {
		input.setCurrentTime(timestamp);
		Packet packet = null;
		int num = 0;
		while ((packet = input.receive()) != null) {
			++num;
			recordPacket(packet);
		}
		return (num > 0);
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void connect(Link input) {
		this.input = input;
	}

	private void recordPacket(Packet packet) {
		logger.logPacketOfReceiver(packet);
	}
}
