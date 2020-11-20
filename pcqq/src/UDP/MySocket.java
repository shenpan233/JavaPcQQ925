package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MySocket {
	private int port;
	private String host;
	private DatagramSocket udp;

	public boolean link(String ip, int c_port) {
		try {
			this.udp = new DatagramSocket();
			this.port = c_port;
			this.host = ip;
			this.udp.setSoTimeout(3000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean send(byte[] data) {
		try {
			DatagramPacket packet = new DatagramPacket(data, 0, data.length,
					new InetSocketAddress(this.host, this.port));
			this.udp.send(packet);
			// udp.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public byte[] getbin() {
		try {
			byte[] data = new byte[1024 * 2];
			DatagramPacket pack = new DatagramPacket(data, 0, data.length);
			udp.receive(pack);
			byte[] datas = pack.getData();
			int len = pack.getLength();
			datas = this.subBytes(datas, 0, len);
			return datas;
		} catch (Exception e) {

			return null;
		}
	}

	private static byte[] subBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		System.arraycopy(src, begin, bs, 0, count);
		return bs;
	}

	public void close2() {
		this.udp.close();

	}
}