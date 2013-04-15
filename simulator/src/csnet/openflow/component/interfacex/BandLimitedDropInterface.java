package csnet.openflow.component.interfacex;

import csnet.openflow.component.link.Link;
import csnet.openflow.packet.model.Packet;

public class BandLimitedDropInterface implements Interface {
	private Link input;
	private Link output;
	private double thresholdTime;
	private double timePerByte;
    private Packet.SizeType type;

	public BandLimitedDropInterface() {
		this.thresholdTime = 0;
	}
    
    public void config(long bandwidth, Packet.SizeType type) {
        if (bandwidth == 0) {
			this.timePerByte = 0;
		} else {
			this.timePerByte = 1.0 * 1000 * 1000 / bandwidth;
		}
        this.type = type;
    }

	private void setCurrentTime(long currentTime) {
		input.setCurrentTime(currentTime);
	}

	private Packet receive() {
		Packet packet = null;
		while ((packet = input.receive()) != null) {
			if (packet.getTimestamp() >= thresholdTime) {
				thresholdTime = packet.getSize(type) * timePerByte
						+ packet.getTimestamp();
				return packet;
			} else {
				;
			}
		}
		return null;
	}

	@Override
	public void connect(Link input, Link output) {
		this.input = input;
		this.output = output;
	}

	@Override
	public boolean schedule(long timestamp) {
		setCurrentTime(timestamp);
		Packet packet = null;
		while ((packet = receive()) != null) {
			output.send(packet);
		}
		return false;
	}
}
