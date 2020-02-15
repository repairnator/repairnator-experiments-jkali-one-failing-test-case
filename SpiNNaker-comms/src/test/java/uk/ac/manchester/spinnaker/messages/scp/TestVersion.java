package uk.ac.manchester.spinnaker.messages.scp;

import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.manchester.spinnaker.messages.scp.SCPCommand.CMD_VER;
import static uk.ac.manchester.spinnaker.messages.scp.SCPResult.RC_OK;
import static uk.ac.manchester.spinnaker.messages.sdp.SDPHeader.Flag.REPLY_NOT_EXPECTED;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import uk.ac.manchester.spinnaker.machine.CoreLocation;
import uk.ac.manchester.spinnaker.messages.model.UnexpectedResponseCodeException;
import uk.ac.manchester.spinnaker.messages.model.Version;
import uk.ac.manchester.spinnaker.messages.scp.GetVersion.Response;

class TestVersion {

	@Test
	void testNewVersionRequest() {
		GetVersion ver_request = new GetVersion(new CoreLocation(0, 1, 2));
		assertEquals(CMD_VER, ver_request.scpRequestHeader.command);
		assertEquals(new CoreLocation(0, 1, 2),
				ver_request.sdpHeader.getDestination());
	}

	@Test
	void testParseVersionResponse() throws UnsupportedEncodingException,
			UnexpectedResponseCodeException {
		// SCP Stuff
		short rc = RC_OK.value;
		short seq = 105;
		short p2p_addr = 1024;
		byte phys_cpu = 31;
		byte virt_cpu = 14;
		short version = 234;
		short buffer = 250;
		int build_date = 103117;
		byte[] ver_string = "sark/spinnaker".getBytes("ASCII");

		// SDP stuff
		byte flags = REPLY_NOT_EXPECTED.value;
		byte tag = 5;
		byte dest_port_cpu = 0x4f;
		byte src_port_cpu = 0x6a;
		byte dest_x = 0x11;
		byte dest_y = (byte) 0xab;
		byte src_x = 0x7;
		byte src_y = 0x0;

		ByteBuffer data = allocate(39).order(LITTLE_ENDIAN);
		data.put(flags).put(tag).put(dest_port_cpu).put(src_port_cpu);
		data.put(dest_y).put(dest_x).put(src_y).put(src_x);
		data.putShort(rc).putShort(seq).putShort(p2p_addr);
		data.put(phys_cpu).put(virt_cpu).putShort(buffer).putShort(version);
		data.putInt(build_date).put(ver_string);
		data.flip();

		Response response = new GetVersion.Response(data);
		assertEquals("sark", response.versionInfo.name);
		assertEquals("sark/spinnaker", response.versionInfo.versionString);
		assertEquals(new Version(2, 34, 0), response.versionInfo.versionNumber);
	}
}
