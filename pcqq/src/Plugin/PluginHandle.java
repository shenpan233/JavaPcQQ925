package Plugin;

import java.net.DatagramPacket;

import main.qq;

import com.alibaba.fastjson.JSONObject;

public class PluginHandle {
	private PluginInfo[] pluginInfo = new PluginInfo[0];

	public void MessageHandle(String JsonData, qq qqmain, DatagramPacket p) {
		try {
			JSONObject JSON = JSONObject.parseObject(JsonData);
			String cmd = JSON.getString("cmd");
			switch (cmd) {
			case "HreatBeat":
				break;
			case "SendGroupMsg":
				String GroupUin = JSON.getString("GroupUin");
				String Msg = JSON.getString("Msg");
				System.out.println();
				qqmain.SendGroupMsg(GroupUin, Msg);
				System.out
						.println("���->����Ⱥ��Ϣ->Ⱥ��:" + GroupUin + ",��Ϣ����:" + Msg);
				break;
			case "SendFriendMsg":
				String RecverQQ = JSON.getString("FriendQQ");
				String Friend_Msg = JSON.getString("Msg");
				qqmain.SendFriendMsg(RecverQQ, Friend_Msg);
				System.out.println("���->���ͺ�����Ϣ->����:" + RecverQQ + ",��Ϣ����:"
						+ Friend_Msg);
				break;
			case "QQLogin":
				String qquin = JSON.getString("qquin");
				String password = JSON.getString("password");
				qqmain.QQLogin(qquin, password);
				System.out.println("���->��¼QQ->QQUin:" + qquin);
				break;
			case "Init":
				String Name = JSON.getString("Name");
				String Writer = JSON.getString("Writer");
				System.out.println("ϵͳ->������->����:" + Name + ",����:" + Writer);

				PluginInfo tmp = new PluginInfo();
				tmp.inet = p.getAddress().getHostAddress();
				tmp.port = p.getPort();

				this.pluginInfo = this.Add(this.pluginInfo, tmp);
				break;

			default:
				System.out.println(JsonData);
				break;
			}

		} catch (Exception e) {
			return;
		}

	}

	private PluginInfo[] Add(PluginInfo[] old, PluginInfo New) {

		PluginInfo[] tmp = new PluginInfo[old.length + 1];

		for (int i = 0; i < tmp.length; i++) { // �ҳ���û���ظ���
			if (i != old.length) {
				if (old[i].port == New.port) {

					return old;
				}
			}
			// û���ظ�ֱ�Ӹ�ֵ
			if (i == old.length) {
				tmp[i] = new PluginInfo();
				tmp[i] = New;
				break;
			}

			tmp[i] = new PluginInfo();
			tmp[i] = old[i];

		}

		return tmp;
	}

	public PluginInfo[] GetPluginInfo() {

		return pluginInfo;
	}

	public int GetPluginInfo_l() {

		return pluginInfo.length;
	}
}
