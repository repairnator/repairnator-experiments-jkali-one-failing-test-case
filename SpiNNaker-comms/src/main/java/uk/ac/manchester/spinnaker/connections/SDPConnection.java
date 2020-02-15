package uk.ac.manchester.spinnaker.connections;

import static uk.ac.manchester.spinnaker.messages.sdp.SDPHeader.Flag.REPLY_EXPECTED;
import static uk.ac.manchester.spinnaker.transceiver.Utils.newMessageBuffer;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;

import uk.ac.manchester.spinnaker.connections.model.SDPReceiver;
import uk.ac.manchester.spinnaker.connections.model.SDPSender;
import uk.ac.manchester.spinnaker.machine.ChipLocation;
import uk.ac.manchester.spinnaker.machine.HasChipLocation;
import uk.ac.manchester.spinnaker.messages.sdp.SDPMessage;

/** A UDP socket connection that talks SDP to SpiNNaker. */
public class SDPConnection extends UDPConnection<SDPMessage>
		implements SDPReceiver, SDPSender {
	private static final ChipLocation ONE_WAY_SOURCE = new ChipLocation(0, 0);
	private ChipLocation chip;

	/**
	 * @param remoteChip
	 *            Which chip are we talking to? This is not necessarily the chip
	 *            that is connected to the Ethernet connector on the SpiNNaker
	 *            board, or even on the same board.
	 * @param remoteHost
	 *            The address of the SpiNNaker board to route UDP packets to.
	 * @param remotePort
	 *            The UDP port on the SpiNNaker board to use.
	 * @throws IOException
	 *             If anything goes wrong with the setup.
	 */
	public SDPConnection(HasChipLocation remoteChip, String remoteHost,
			Integer remotePort) throws IOException {
		this(remoteChip, null, null, remoteHost, remotePort);
	}

	/**
	 * @param remoteChip
	 *            Which chip are we talking to? This is not necessarily the chip
	 *            that is connected to the Ethernet connector on the SpiNNaker
	 *            board, or even on the same board.
	 * @param localHost
	 *            The local host address to bind to, or <tt>null</tt> to bind to
	 *            all relevant local addresses.
	 * @param localPort
	 *            The local port to bind to, or <tt>null</tt> to pick a random
	 *            free port.
	 * @param remoteHost
	 *            The address of the SpiNNaker board to route UDP packets to.
	 * @param remotePort
	 *            The UDP port on the SpiNNaker board to use.
	 * @throws IOException
	 *             If anything goes wrong with the setup (e.g., if the local
	 *             port is specified and already bound).
	 */
	public SDPConnection(HasChipLocation remoteChip, String localHost,
			Integer localPort, String remoteHost, Integer remotePort)
			throws IOException {
		super(localHost, localPort, remoteHost, remotePort);
		this.chip = remoteChip.asChipLocation();
	}

	@Override
	public MessageReceiver<SDPMessage> getReceiver() {
		return this::receiveSDPMessage;
	}

	@Override
	public void sendSDPMessage(SDPMessage sdpMessage) throws IOException {
		if (sdpMessage.sdpHeader.getFlags() == REPLY_EXPECTED) {
			sdpMessage.updateSDPHeaderForUDPSend(chip);
		} else {
			sdpMessage.updateSDPHeaderForUDPSend(ONE_WAY_SOURCE);
		}
		ByteBuffer buffer = newMessageBuffer();
		buffer.putShort((short) 0);
		sdpMessage.addToBuffer(buffer);
		buffer.flip();
		send(buffer);
	}

	@Override
	public SDPMessage receiveSDPMessage(Integer timeout)
			throws IOException, InterruptedIOException {
		return new SDPMessage(receive());
	}

	/**
	 * @return The SpiNNaker chip that we are talking to with this connection.
	 */
	public ChipLocation getChip() {
		return chip;
	}

	/**
	 * Set the SpiNNaker chip that we are talking to with this connection.
	 *
	 * @param chip
	 *            The chip to talk to.
	 */
	public void setChip(HasChipLocation chip) {
		this.chip = chip.asChipLocation();
	}
}
