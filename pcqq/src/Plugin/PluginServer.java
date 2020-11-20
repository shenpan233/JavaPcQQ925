package Plugin;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import main.qq;

import com.alibaba.fastjson.JSONObject;

public class PluginServer implements Runnable {
	private DatagramSocket udp;
	private final int port = 2333;
	private boolean state; // 0.未开启 1.已经开启
	private qq pcqq;
	private PluginHandle handle = new PluginHandle();

	public boolean Plugin_Server_Start() {
		try {
			this.udp = new DatagramSocket(this.port);
			this.state = true;
			return true;
		} catch (SocketException e) {
			this.state = false;
			return false;
		}

	}

	public void SetPcqqMain(qq pcq) {
		this.pcqq = pcq;
	}

	public void Plugin_Server_Close() {
		this.state = false;
		this.udp.close();
	}

	public boolean Plugin_Server_State() {
		return this.state;
	}

	public void SendToAll(String Msg) {
		PluginInfo[] plugin = new PluginInfo[this.handle.GetPluginInfo_l()];
		plugin = this.handle.GetPluginInfo();

		for (int i = 0; i < plugin.length; i++) {

			DatagramPacket p = new DatagramPacket(Msg.getBytes(), 0,
					Msg.getBytes().length, new InetSocketAddress(
							plugin[i].inet, plugin[i].port));

			try {

				this.udp.send(p);
			} catch (IOException e) {

			}
		}
	}

	public void SendTo(DatagramPacket p) throws IOException {
		this.udp.send(p);
	}

	/**
	 * 
	 * @param MsgType
	 *            1.群消息
	 * @param RobotQQ
	 *            机器人QQ
	 * @param GroupUin
	 *            来源群号，非群消息时为空
	 * @param SenderQQ
	 *            发送者QQ
	 * @param Msg
	 *            接收的消息内容
	 */
	public void Plugin_MsgDistirtion(int MsgType, String RobotQQ,
			String GroupUin, String SenderQQ, String Msg) {
		JSONObject json = new JSONObject();
		json.put("cmd", "MsgDistirtion");
		json.put("MsgType", MsgType);
		json.put("RobotQQ", RobotQQ);
		json.put("GroupUin", GroupUin);
		json.put("SenderQQ", SenderQQ);
		json.put("Msg", Msg);
		String jsondata = json.toJSONString();
		this.SendToAll(jsondata);

	}

	@Override
	public void run() {
		this.Plugin_Server_Start();
		try {
			while (this.Plugin_Server_State()) {
				byte[] data = new byte[1024];
				DatagramPacket pack = new DatagramPacket(data, 0, data.length);
				this.udp.receive(pack);
				byte[] datas = pack.getData();
				int len = pack.getLength();
				datas = this.subBytes(datas, 0, len);
				data = datas;

				if (data != null) {

					this.handle
							.MessageHandle(new String(data), this.pcqq, pack);

					this.SendTo(pack);

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("插件系统出现异常" + e.toString());
		}

	}

	private byte[] subBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		System.arraycopy(src, begin, bs, 0, count);
		return bs;
	}

}
