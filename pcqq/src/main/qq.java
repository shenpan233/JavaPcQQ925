package main;

import info.Key;
import info.MsgRestructrue;
import info.SDK;
import info.TlvPack;
import info.qqinfo;
import info.tlvtoken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import tools.ByteArr;
import tools.TEA;
import Mysql.Mysql;
import Pack.PbPack;
import Pack._Pack;
import Pack._Unpack;
import Plugin.PluginServer;
import UDP.MySocket;

public class qq {
	private static final Object[] String = null;
	private SDK sdk = new SDK();
	private qqinfo qq = new qqinfo();
	private Key key = new Key();
	private _Tlv tlv = new _Tlv();
	private TEA tea = new TEA();
	private MySocket udp = new MySocket();
	private tlvtoken tlv_token = new tlvtoken();
	private int IpIdx = 0;
	public byte[] sessionkey = new byte[0];
	public PluginServer p = new PluginServer();
	private int cdxcs = 0;
	private boolean iVerifyQrcode = false;
	private Mysql DB = new Mysql();

	// //////////////////////////////////////////////////////////////////////////////////////////

	public boolean info(String user, String pass) {

		try {
			Long.parseLong(user);
		} catch (Exception e) {
			System.err.println("登陆失败!QQ号不是纯数字");
			return false;
		}
		// if (!DB.Init("localhost", "3306", "java", "java233", "123456")) {
		// System.err.println("可恶，你的数据库配错了啊!");
		// }
		sdk.head = "02";
		sdk.tail = "03";
		sdk.pc_name = "QWQ";
		sdk.ver_qq = 14383;
		sdk.dwpubno = "00 00 69 34"; // 新的
		// sdk.ver_qq = 14395;
		// sdk.dwpubno = "00 00 69 5D"; // 旧的
		sdk.machine_code = "CB 07 71 4D FF 17 61 6A 83 19 04 3E 67 5F A5 4C";
		// sdk.crc_data = Bin2Hex(GetRandomBin(16));
		sdk.crc_data = "BF 3A D8 48 F0 C4 04 60 5C 56 87 21 E7 D5 15 7B";
		sdk.crc_32 = this.GetCrc32(sdk.crc_data);

		// sdk.crc_32 = "22 68 D6 99 ";// 不会生成只好固定了QWQ
		// //////////////////////////////////////////////////////
		qq.qquin = user;
		qq.luin = Long.valueOf(user);
		qq.qqhex = QQ2Hex(qq.luin);
		qq.new_ip = "0.0.0.0";
		qq.login_num = 0;
		// ////////////////////////////////////////////////////////////
		key.MD51 = Bin2Hex(ByteArr.Md5_(pass.getBytes()));
		key.MD52 = ByteArr.Md5_(HextoBin(key.MD51 + "00 00 00 00" + qq.qqhex));
		key.TGTkey = GetRandomBin(16);
		key._0818key = GetRandomBin(16);
		key._0825key = GetRandomBin(16);
		key._0836token = GetRandomBin(56);
		key.Sharekey = HextoBin("68 96 37 28 F5 A7 59 8B C9 10 05 19 C4 11 CB 2E");
		key.PubKey = HextoBin("03 E1 E6 F6 94 5E F0 8D 67 86 81 A5 DA 01 52 52 97 5F 02 E1 AD C0 52 72 1B");

		// /////////////写SQL数据////////////
		// String sql = "SELECT * FROM `qqinfo` WHERE qquin = '" + user + "'";
		// try {
		// ResultSet rs = DB.select(sql);
		//
		// if (rs == null) {
		// sql =
		// "INSERT INTO `qqinfo` (`id`, `qquin`, `password`, `SessionKey`, `nick`) VALUES (NULL, '"
		// + user + "', NULL, NULL, NULL)";
		// DB.query(sql);
		// }
		// rs.close();
		// } catch (SQLException e) {
		// }
		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	public void link_tx() {
		this.IpIdx = this.GetRandmo(1, 9);
		String ip = this.GetTxServerIp();

		if (udp.link(ip, 8000)) {
			System.out.println("已连接到TX服务器:" + ip);
		} else {
			System.err.println("ip:" + ip + "连接失败");
		}

	}

	public void link_tx_2(String ip) {
		if (udp.link(ip, 8000)) {
			System.out.println("已连接到TX服务器:" + ip);
		} else {
			System.err.println("ip:" + ip + "连接失败");
		}

	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	// 这些都是登录用的包
	/**
	 * 
	 * @param new_ip
	 *            重定向IP使用
	 * @param u
	 *            是否使用上次重定向IP登录
	 * @return
	 */
	public byte[] pack_0825(boolean u) {
		_Pack pack = new _Pack();
		_Pack enPack = new _Pack();
		enPack.Empty();
		enPack.SetBin(make_login_head("08 25"));
		enPack.SetBin(key._0825key);
		pack.Empty();
		pack.SetBin(tlv.tlv018(qq.qqhex, this.cdxcs));
		pack.SetBin(tlv.tlv309(IPtoHex(qq.new_ip), u));
		pack.SetBin(tlv.tlv036());
		pack.SetBin(tlv.tlv114(key.PubKey));
		enPack.SetBin(tea.encrypt(pack.GetAll(), key._0825key));
		enPack.SetHex(sdk.tail);
		return enPack.GetAll();

	}

	public byte[] pack_0836_CommonLogin() {
		_Pack pack = new _Pack();
		_Pack enPack = new _Pack();
		enPack.Empty();
		enPack.SetBin(this.make_login_head("08 36"));
		// 这里莫名两个tlv
		enPack.SetShort(1);// 这是某个版本 2的话要二次sharekey 1不用
		pack.Empty();
		pack.SetBin(tlv.tlv103(key.PubKey));
		pack.SetBin(tlv.tlv000(null));
		enPack.SetBin(pack.GetAll());
		// /////////////
		byte[] tlv000 = tlv.tlv000(HextoBin(sdk.machine_code));
		byte[] tlv015 = tlv.tlv015(sdk.machine_code);
		pack.Empty();
		pack.SetBin(tlv.tlv112(key._0825token));
		pack.SetBin(tlv.tlv30F(sdk.pc_name));
		pack.SetBin(tlv.tlv005(qq.qqhex));
		pack.SetBin(tlv.tlv006(qq.qqhex, key.MD51, key.MD52, sdk.time, sdk.ip,
				key.TGTkey, tlv000));
		pack.SetBin(tlv015);
		pack.SetBin(tlv.tlv01A(tlv015, key.TGTkey));
		pack.SetBin(tlv.tlv018(qq.qqhex, 0));
		pack.SetBin(tlv
				.tlv103_2(HextoBin("0B E6 A1 02 64 3C 3D 7C E7 9D 7E 44 09 9B AE 82")));
		pack.SetBin(tlv.tlv312());
		pack.SetBin(tlv.tlv508());
		pack.SetBin(tlv.tlv313());
		pack.SetBin(tlv.tlv102(key._0836token, sdk.crc_data, sdk.crc_32));
		// ////////////
		enPack.SetBin(tea.encrypt(pack.GetAll(), key.Sharekey));
		enPack.SetHex(this.sdk.tail);
		return enPack.GetAll();
	}

	public byte[] pack_0836_2() {
		_Pack pack = new _Pack();
		_Pack enPack = new _Pack();
		enPack.Empty();
		enPack.SetBin(this.make_login_head("08 36"));
		// 这里莫名两个tlv
		enPack.SetShort(1);// 这是某个版本 2的话要二次sharekey 1不用
		pack.Empty();
		pack.SetBin(tlv.tlv103(key.PubKey));
		pack.SetBin(tlv.tlv000(null));
		enPack.SetBin(pack.GetAll());
		// /////////////
		byte[] tlv015 = tlv.tlv015(sdk.machine_code);
		pack.Empty();
		pack.SetBin(tlv.tlv112(key._0825token));
		pack.SetBin(tlv.tlv30F(sdk.pc_name));
		pack.SetBin(tlv.tlv005(qq.qqhex));
		pack.SetBin(tlv.SetToken("00 06", tlv_token._0006));
		pack.SetBin(tlv.tlv01A(tlv015, key.TGTkey));
		pack.SetBin(tlv.tlv018(qq.qqhex, 0));
		pack.SetBin(tlv
				.tlv103_2(HextoBin("0B E6 A1 02 64 3C 3D 7C E7 9D 7E 44 09 9B AE 82")));
		pack.SetBin(tlv.SetToken("01 10", tlv_token._0110));
		pack.SetBin(tlv.tlv032(key.MD51, key._0825key));
		pack.SetBin(tlv.tlv312());
		pack.SetBin(tlv.tlv508());
		pack.SetBin(tlv.tlv313());
		pack.SetBin(tlv.tlv102(key._0836token, sdk.crc_data, sdk.crc_32));

		// /////////////
		enPack.SetBin(this.tea.encrypt(pack.GetAll(), this.key.Sharekey));
		enPack.SetHex(this.sdk.tail);
		return enPack.GetAll();
	}

	public byte[] pack_0836_QrcodeLogin() {
		_Pack pack = new _Pack();
		_Pack enPack = new _Pack();
		enPack.Empty();
		enPack.SetBin(this.make_login_head("08 36"));
		// 这里莫名两个tlv
		enPack.SetShort(1);// 这是某个版本 2的话要二次sharekey 1不用
		pack.Empty();
		pack.SetBin(tlv.tlv103(key.PubKey));
		pack.SetBin(tlv.tlv000(null));
		enPack.SetBin(pack.GetAll());
		// /////////////
		byte[] tlv000 = tlv.tlv000(HextoBin(sdk.machine_code));
		byte[] tlv015 = tlv.tlv015(sdk.machine_code);

		pack.Empty();
		pack.SetBin(tlv.tlv112(key._0825token));
		pack.SetBin(tlv.tlv30F(sdk.pc_name));
		pack.SetBin(tlv.tlv005(qq.qqhex));
		pack.SetBin(tlv.tlv303(this.key._0819token));
		pack.SetBin(tlv015);
		pack.SetBin(tlv.tlv01A(tlv015, key.TGTkey));
		pack.SetBin(tlv.tlv018(qq.qqhex, 0));
		pack.SetBin(tlv
				.tlv103_2(HextoBin("0B E6 A1 02 64 3C 3D 7C E7 9D 7E 44 09 9B AE 82")));
		pack.SetBin(tlv.tlv312());
		pack.SetBin(tlv.tlv313());
		pack.SetBin(tlv.tlv102(key._0836token, sdk.crc_data, sdk.crc_32));
		// ////////////
		enPack.SetBin(tea.encrypt(pack.GetAll(), key.Sharekey));
		enPack.SetHex(this.sdk.tail);
		return enPack.GetAll();

	}

	public byte[] pack_0818() {
		_Pack pack = new _Pack();
		_Pack en_Pack = new _Pack();
		en_Pack.Empty();
		en_Pack.SetBin(make_login_head("08 18"));
		en_Pack.SetBin(this.key._0818key);

		pack.Empty();
		pack.SetBin(tlv.tlv019());
		pack.SetBin(tlv.tlv114(this.key.PubKey));
		pack.SetBin(tlv.tlv305());
		pack.SetBin(tlv.tlv015(this.sdk.machine_code));

		en_Pack.SetBin(this.tea.encrypt(pack.GetAll(), this.key._0818key));
		en_Pack.SetHex(this.sdk.tail);

		return en_Pack.GetAll();
	}

	public byte[] pack_0819() {
		_Pack pack = new _Pack();
		_Pack en_Pack = new _Pack();
		en_Pack.Empty();
		en_Pack.SetBin(make_login_head("08 19"));
		en_Pack.SetBin(tlv.tlv030(key._0818token));
		pack.Empty();
		pack.SetBin(tlv.tlv019());
		pack.SetBin(tlv.tlv301(key.qrsing));
		en_Pack.SetBin(tea.encrypt(pack.GetAll(), key._0819key));
		en_Pack.SetHex(this.sdk.tail);
		return en_Pack.GetAll();
	}

	public byte[] pack_0828() {
		_Pack pack = new _Pack();
		_Pack enPack = new _Pack();
		enPack.Empty();
		enPack.SetBin(make_login_head("08 28"));
		pack.Empty();
		pack.SetBin(tlv.tlv007(tlv_token._0007));
		pack.SetBin(tlv.tlv00C(null));
		pack.SetBin(tlv.tlv015(sdk.machine_code));
		pack.SetBin(tlv.tlv036());
		pack.SetBin(tlv.tlv018(qq.qqhex, 0));
		pack.SetBin(tlv.tlv01F());
		pack.SetHex("01 05 00 30 " + "00 01 01 02 00 14 01 01 00 10 "
				+ Bin2Hex(GetRandomBin(16)) + " 00 14 01 02 00 10 "
				+ Bin2Hex(GetRandomBin(16)));
		pack.SetBin(tlv.tlv10B());
		pack.SetBin(tlv.tlv02D());
		enPack.SetBin(tlv.tlv030(tlv_token._0109));
		System.out.println("");
		System.out.println(pack.GetAll_Hex());
		enPack.SetBin(tea.encrypt(pack.GetAll(), key._0828key));
		enPack.SetHex(this.sdk.tail);
		return enPack.GetAll();
	}

	private void x0825(byte[] data) {
		_Unpack unpack = new _Unpack();
		unpack.SetData(tea.decrypt(data, key._0825key));
		if (unpack.Len() == 0) {
			System.err.println("0825返回包->解密失败!");
			return;
		}
		boolean _new = false;
		int type = unpack.GetByte();
		if (type == 0) {
			System.out.println("0825返回包->成功接触服务器");
		} else {
			System.err.println("0825返回包->需要重定向IP");
			_new = true;
		}
		TlvPack tlv_data = new TlvPack();
		_Unpack tlv_unpack = new _Unpack();

		while (true) {
			tlv_data.flag = Bin2Hex(unpack.GetBin(2)).trim();
			tlv_data.data = unpack.GetToken();
			tlv_unpack.SetData(tlv_data.data);
			switch (tlv_data.flag) {
			case "01 12":
				key._0825token = tlv_unpack.GetAll();
				break;
			case "00 17":
				tlv_unpack.GetShort();
				sdk.time = Bin2Hex(tlv_unpack.GetBin(4));
				sdk.ip = Bin2Hex(tlv_unpack.GetBin(4));
				break;
			case "00 0C":
				tlv_unpack.GetToken();
				tlv_unpack.GetInt();
				tlv_unpack.GetInt();
				this.qq.new_ip = this.HexToIp(Bin2Hex(tlv_unpack.GetBin(4)));
				break;
			default:
				break;
			}

			if (tlv_data.data == null) {

				break;
			}
		}

		if (_new) {
			this.udp.close2();
			this.link_tx_2(this.qq.new_ip);
			this.key._0825key = this.GetRandomBin(16);
			this.cdxcs++;
			this.udp.send(this.pack_0825(true));
		}
	}

	private void x0836(byte[] data, String en_type) {
		_Unpack unpack = new _Unpack();
		unpack.SetData(this.tea.decrypt(data, key.Sharekey));
		// System.out.println("0836加密方式:" + en_type);
		if (en_type == "00 08 00") {
			unpack.GetShort();
			byte[] TK = unpack.GetToken();// 计算sharekey2
			System.out.println(Bin2Hex(TK));
			byte[] Sharekey2 = new byte[0];// 不会算啊
			unpack.SetData(tea.decrypt(unpack.GetAll(), Sharekey2));
			System.err.println("0836返回包->使用二次sharekey,无法解密!");
		}
		// System.out.println("Sharekey:" + Bin2Hex(key.Sharekey));
		// System.out.println("TGTkey:" + Bin2Hex(key.TGTkey));
		byte[] bin = unpack.GetAll();
		int login_type = unpack.GetByte();
		if (login_type != 0x34 && login_type != -5) {
			unpack.SetData(bin);
			unpack.SetData(tea.decrypt(unpack.GetAll(), key.TGTkey));// 3成加密
			login_type = unpack.GetByte();
		}

		TlvPack tlv_data = new TlvPack();
		_Unpack tlv_unpack = new _Unpack();

		while (true) {
			tlv_data.flag = Bin2Hex(unpack.GetBin(2)).trim();
			tlv_data.data = unpack.GetToken();
			tlv_unpack.SetData(tlv_data.data);
			switch (tlv_data.flag) {
			case "01 15":
				byte[] token = tlv_unpack.GetAll();
				System.out.println("验证码token:" + Bin2Hex(token));
				break;
			case "00 1E":
				key.TGTkey = tlv_data.data;// 更新tgtkey 下个包使用
				break;
			case "00 06":
				// 不知道有鬼用？
				tlv_token._0006 = tlv_data.data;
				break;
			case "01 10":
				tlv_token._0110 = tlv_data.data;
				break;
			case "01 07":
				tlv_unpack.GetShort();
				tlv_unpack.GetToken();
				key.TGTkey = tlv_unpack.GetBin(16);// 更新后0828使用
				tlv_token._0007 = tlv_unpack.GetToken();
				System.out.println("newTGTkey:" + Bin2Hex(key.TGTkey));
				break;
			case "01 08":
				tlv_unpack.GetShort();
				tlv_unpack.GetShort();
				tlv_unpack.GetInt();
				this.qq.nick = tlv_unpack.GetString(tlv_unpack.GetByte());
				switch (unpack.GetByte()) {
				case 0:
					this.qq.gender = "未知";
					break;
				case 1:
					this.qq.gender = "男";
					break;
				case 2:
					this.qq.gender = "女";
					break;
				default:
					break;
				}
				break;
			case "01 09":
				tlv_unpack.GetShort();
				key._0828key = tlv_unpack.GetBin(16);
				System.out.println("0828key:" + Bin2Hex(key._0828key));
				tlv_token._0109 = tlv_unpack.GetToken();
				break;
			case "01 00":
				tlv_unpack.GetShort();
				tlv_unpack.GetBin(4);
				tlv_unpack.GetShort();
				System.out.println(tlv_unpack.GetUtf8_Short());
				return;
			case "01 04":
				tlv_unpack.GetBin(15);
				System.out.println("请先完成滑块验证码:");
				System.out.println(tlv_unpack.GetUtf8_Short());
				break;
			default:
				break;
			}

			if (tlv_data.data == null) {
				break;
			}
		}
		switch (login_type) {
		case 251:
			break;
		case 1:// 发送二次0836
			System.out.println("需要更新TGTKEY");
			this.udp.send(this.pack_0836_2());
			this.CmdDecide(this.udp.getbin());
			break;
		case 0:// 0836_2成功 发送0828
			udp.send(pack_0828());
			CmdDecide(udp.getbin());
			break;
		default:
			break;
		}

		System.out.println(unpack.GetAll_Hex());
	}

	private void x0828(byte[] data) {
		_Unpack unpack = new _Unpack();
		unpack.SetData(tea.decrypt(data, key.TGTkey));
		unpack.GetByte();
		// System.out.println(unpack.GetAll_Hex());
		TlvPack tlv_data = new TlvPack();
		_Unpack tlv_unpack = new _Unpack();
		while (true) {
			tlv_data.flag = Bin2Hex(unpack.GetBin(2)).trim();
			tlv_data.data = unpack.GetToken();
			tlv_unpack.SetData(tlv_data.data);
			switch (tlv_data.flag) {
			case "01 05":
				System.out.println("tlv_0105:" + tlv_unpack.GetAll_Hex());
				break;
			case "01 0C":
				tlv_unpack.GetShort();
				key.sessionkey = tlv_unpack.GetBin(16);
				System.out.println("SessionKey:" + Bin2Hex(key.sessionkey));
				break;
			default:
				break;
			}
			if (tlv_data.data == null) {
				break;
			}
		}
		udp.send(pack_00EC_ChaneLoginState());
		this.runthead();

	}

	private void x0818(byte[] data) {
		System.out.println(Bin2Hex(data));
		_Unpack unpack = new _Unpack();

		unpack.SetData(this.tea.decrypt(data, this.key.Sharekey));
		if (unpack.Len() == 0) {
			System.err.println("0818返回包->解密失败!");
			return;
		}
		int otype = unpack.GetByte();
		TlvPack tlv_data = new TlvPack();
		_Unpack tlv_unpack = new _Unpack();
		if (otype == 0) {
			while (true) {
				tlv_data.flag = Bin2Hex(unpack.GetBin(2)).trim();
				tlv_data.data = unpack.GetToken();
				tlv_unpack.SetData(tlv_data.data);
				switch (tlv_data.flag) {

				case "00 09":
					tlv_unpack.GetShort();
					key._0819key = tlv_unpack.GetAll();
					break;

				case "03 02":
					System.out.println("二维码图片:");
					System.out.println(Bin2Hex(tlv_unpack.GetToken())
							.replaceAll(" ", ""));
					break;

				case "03 01":
					key.qrsing = tlv_unpack.GetToken();
					break;

				case "00 30":
					key._0818token = tlv_unpack.GetToken();
					break;

				default:
					break;
				}
				if (tlv_data.data == null) {
					break;
				}
			}

		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////

	private void x0819(byte[] data) {

		_Unpack unpack = new _Unpack();
		unpack.SetData(tea.decrypt(data, key._0819key));
		if (unpack.Len() == 0) {
			System.err.println("0819返回包->解密失败!");
			return;
		}
		int otype = unpack.GetByte();
		TlvPack tlv_data = new TlvPack();
		_Unpack tlv_unpack = new _Unpack();

		if (otype == 0) {
			while (true) {
				tlv_data.flag = Bin2Hex(unpack.GetBin(2)).trim();
				tlv_data.data = unpack.GetToken();
				tlv_unpack.SetData(tlv_data.data);
				switch (tlv_data.flag) {

				case "03 03":
					key._0819token = tlv_unpack.GetAll();
					break;

				case "03 04":
					key.TGTkey = tlv_unpack.GetAll();
					System.out.println("TGTkey:" + Bin2Hex(key.TGTkey));
					break;
				case "03 07":
					break;
				case "00 04":
					tlv_unpack.GetShort();
					this.qq.qquin = tlv_unpack.GetUtf8_Short();
					this.qq.luin = Long.valueOf(this.qq.qquin);
					this.qq.qqhex = this.QQ2Hex(this.qq.luin);
					break;
				case "03 0C":
					break;

				default:
					break;
				}
				if (tlv_data.data == null) {
					break;
				}
			}
			iVerifyQrcode = true;
			System.out.println("QQ:" + this.qq.qquin + "->二维码扫描成功,正在登陆...");
			this.udp.send(this.pack_0825(true));
			this.CmdDecide(this.udp.getbin());
			this.udp.send(this.pack_0836_QrcodeLogin());
			this.CmdDecide(this.udp.getbin());
		} else if (otype == 1) {
			System.out.println("已经扫码,请点击登录!");
		} else if (otype == 2) {
			// System.err.println("二维码未被扫描!");
		} else {
			System.err.println("二维码已经失效！");
			System.out.println("正在刷新二维码:");
			this.key._0818key = GetRandomBin(16);
			this.udp.send(this.pack_0818());
			this.CmdDecide(this.udp.getbin());
		}

	}

	private void x0017(String ocmd, byte[] data, String flag) {
		_Unpack unpack = new _Unpack();
		unpack.SetData(tea.decrypt(data, key.sessionkey));
		String id = Bin2Hex(unpack.GetBin(16));

		unpack.GetShort();// 8000端口
		String cmd = Bin2Hex(unpack.GetBin(2)).trim();
		udp.send(pack_0017or00CE(ocmd, flag, id));
		switch (cmd) {
		case "02 10":
			break;
		case "00 52":
			GroupMsgUnpack(unpack.GetAll());
			break;
		case "00 A6":
			FriendMsgUnpack(unpack.GetAll());
			break;
		default:
			System.out.println("未知的" + ocmd + "子类型:" + cmd);
			break;
		}

	}

	private void GroupMsgUnpack(byte[] data) {
		_Unpack pack = new _Unpack();
		pack.SetData(data);
		pack.GetBin(31);
		String GroupUin = bin2qq(pack.GetBin(4));
		pack.GetByte();
		String FromQQ = bin2qq(pack.GetBin(4));
		String MsgFlag = bin2qq(pack.GetBin(4));
		String time = bin2qq(pack.GetBin(4));
		pack.GetInt();
		int len = pack.GetShort();
		pack.GetBin(18);
		pack.GetInt();
		String MsgId = bin2qq(pack.GetBin(4));
		pack.GetByte();
		int Red = pack.GetByte();
		int Blue = pack.GetByte();
		int Green = pack.GetByte();
		int FrontSize = pack.GetByte();
		int Encode = pack.GetShort();
		pack.GetByte();
		String Front = pack.GetUtf8_Short();
		pack.GetShort();
		_Unpack msg_unpack = new _Unpack();
		String msg = "";
		String GroupName = "";
		String FromQQName = "";
		udp.send(pack_0002_ToReadMsg(GroupUin, MsgId));
		while (pack.Len() > 0) {
			int type1 = pack.GetByte();
			int type = GroupMsgUnpack_MsgUnpack(type1);
			msg_unpack.SetData(pack.GetToken());
			// System.out.println();
			// System.out.println(msg_unpack.GetAll_Hex());
			// System.out.println();
			String msg_superposition = "";
			// System.out.println(type);
			// System.out.println(msg_unpack.GetAll_Hex());
			switch (type) {
			case 1:// 普通消息
				type = msg_unpack.GetByte();
				if (type == 1) {
					msg_superposition = msg_unpack.GetUtf8_Short();
					// System.out.println("剩余长度:"
					// + String.valueOf(msg_unpack.Len()));
					if (msg_unpack.Len() != 0) {
						type = msg_unpack.GetByte();
						if (type == 6) {
							msg_unpack.SetData(msg_unpack.GetToken());
							// System.out.println("一个At:"
							// + msg_unpack.GetAll_Hex());
							// System.out.println();
							msg_unpack.GetBin(7);
							String at = bin2qq(msg_unpack.GetBin(4));
							if (Long.valueOf(at) == 0) {
								msg_superposition = "[TR:at=all]";
							} else {
								msg_superposition = "[TR:at=" + at + "]";
							}
						} else {
							continue;
						}
					}
				}
				break;
			case 404:
				break;
			case 10:
				msg = "[TR:voice=" + msg_unpack.GetUtf8_Short() + "]";
				break;
			case 3:
				if (msg_unpack.GetByte() == 2) {
					msg_superposition = "[TR:pic=" + msg_unpack.GetUtf8_Short()
							+ "]";
				}
				break;
			case 5:
				msg_superposition = "[TR:face=" + msg_unpack.GetAll_Hex() + "]";
				break;
			case 18:
				while (msg_unpack.Len() > 0) {
					type = msg_unpack.GetByte();
					data = msg_unpack.GetToken();
					if (type == 2 || type == 1) {
						try {
							FromQQName = new String(data, "utf-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					} else if (type == 7) {
						try {
							GroupName = new String(data, "utf-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			default:
				break;
			}
			msg = msg + msg_superposition;
		}
		if (qq.luin == Long.valueOf(FromQQ)) {
			return;
		}
		System.out.println("GroupUin:" + GroupUin + "(" + GroupName + ")"
				+ " FromQQ:" + FromQQ + "(" + FromQQName + ")");
		System.out.println(" Front:" + Front + "(FrontSize="
				+ Integer.toString(FrontSize) + ",R=" + Integer.toString(Red)
				+ ",B=" + Integer.toString(Blue) + ",G="
				+ Integer.toString(Green) + ")");
		System.out.println(" Msg:" + msg);
		p.Plugin_MsgDistirtion(1, qq.qquin, GroupUin, FromQQ, msg);
	}

	// //////////////////////
	// //////////////////////

	private int GroupMsgUnpack_MsgUnpack(int type) {
		switch (type) {
		case 1:
			return 1;
		case 2:
			return 5;
		case 3:
			return 3;
		case 18:
			return 18;
		case 25:
			return 404;
		case 14:
			return 404;
		case 10:
			return 10;
		default:
			return 999;

		}
	}

	private void FriendMsgUnpack(byte[] data) {
		_Unpack pack = new _Unpack();
		pack.SetData(data);
		pack.GetBin(pack.GetInt());
		pack.GetShort();
		String FromQQ = bin2qq(pack.GetBin(4));
		pack.GetInt();// 自己的QQ
		pack.GetBin(16);
		String MsgFlag = bin2qq(pack.GetBin(4));
		String time = bin2qq(pack.GetBin(4));
		pack.GetBin(2);
		pack.GetBin(4);// 第二个时间
		pack.GetBin(13);
		pack.GetBin(4);// 里面有第三个时间 反正都相同的不要也罢
		pack.GetBin(4);// 不知道什么4字节
		pack.GetByte();
		int Red = pack.GetByte();
		int Blue = pack.GetByte();
		int Green = pack.GetByte();
		int FrontSize = pack.GetByte();
		pack.GetByte();
		int Encode = pack.GetShort();
		String Front = pack.GetUtf8_Short();
		pack.GetShort();
		_Unpack msg_unpack = new _Unpack();
		String msg = "";

		String FromQQName = "";
		udp.send(pack_0319_ToReadFriendMsg(FromQQ, Long.valueOf(time)));

		System.out.println(pack.GetAll_Hex());
		while (pack.Len() > 0) {
			int type1 = pack.GetByte();
			int type = GroupMsgUnpack_MsgUnpack(type1);
			msg_unpack.SetData(pack.GetToken());
			System.out.println();
			System.out.println(msg_unpack.GetAll_Hex());
			System.out.println();
			String msg_superposition = "";
			System.out.println(type);
			System.out.println(msg_unpack.GetAll_Hex());
			switch (type) {
			case 1:// 普通消息
				type = msg_unpack.GetByte();
				if (type == 1) {
					msg_superposition = msg_unpack.GetUtf8_Short();

				}

				break;
			case 404:
				break;
			case 10:
				msg = "[TR:voice=" + msg_unpack.GetUtf8_Short() + "]";
				break;
			case 3:
				if (msg_unpack.GetByte() == 2) {
					msg_superposition = "[TR:pic=" + msg_unpack.GetUtf8_Short()
							+ "]";
				}
				break;
			case 5:
				msg_superposition = "[TR:face=" + msg_unpack.GetAll_Hex() + "]";
				break;
			case 18:
				while (msg_unpack.Len() > 0) {
					type = msg_unpack.GetByte();
					data = msg_unpack.GetToken();
					if (type == 2 || type == 1) {
						try {
							FromQQName = new String(data, "utf-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			default:
				break;
			}
			msg = msg + msg_superposition;
		}

		System.out.println(" FromQQ:" + FromQQ + "(" + FromQQName + ")");
		System.out.println(" Front:" + Front + "(FrontSize="
				+ Integer.toString(FrontSize) + ",R=" + Integer.toString(Red)
				+ ",B=" + Integer.toString(Blue) + ",G="
				+ Integer.toString(Green) + ")");
		System.out.println(" Msg:" + msg);

	}

	private void x005C(byte[] data) {
		_Unpack unpack = new _Unpack();
		unpack.SetData(this.tea.decrypt(data, this.key.sessionkey));
		if (unpack.Len() == 0) {
			System.err.println("005C返回包->解密失败!");
			return;
		}
		int type = unpack.GetByte(); // 子类型
		System.err.println(type);
		if (type == 0x88) {
			unpack.GetBin(4);
			unpack.GetInt();
			int Grade = unpack.GetShort();
			int ActionDays = unpack.GetShort();
			int residueActive = unpack.GetShort();
			System.out.println(this.qq.qquin + "(" + this.qq.nick + ")->性别:"
					+ this.qq.gender + " 等级:" + Long.toString(Grade)
					+ " QQ活跃天数:" + Long.toString(ActionDays) + " (升级剩余活跃天数: "
					+ Long.toString(residueActive) + ")");

		}

	}

	private void x(byte[] data) {
		_Unpack unpack = new _Unpack();
		unpack.SetData(this.tea.decrypt(data, this.key.sessionkey));
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	public byte[] pack_00EC_ChaneLoginState() {
		// ' 01 00
		// ' 0A //这是登录状态(0A=在线，3C=Q我吧，1E=离开，32=忙碌，46=请勿打扰，28=隐身)
		// ' 00 02
		// ' 00 01
		// ' 00 04
		// ' 00 00 00 00
		// ' 05 07 //TagIndex:1,length:17
		// ' 00 11
		// ' 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
		// ' 00
		_Pack pack = new _Pack();
		_Pack enpack = new _Pack();
		enpack.Empty();
		enpack.SetBin(make_common_head("00 EC"));
		pack.Empty();
		pack.SetHex("01 00");
		pack.SetByte(10);
		pack.SetShort(2);
		pack.SetShort(1);
		pack.SetShort(4);
		pack.SetInt(0);
		pack.SetBin(tlv.tlv507(true));
		enpack.SetBin(tea.encrypt(pack.GetAll(), key.sessionkey));
		enpack.SetHex(sdk.tail);

		return enpack.GetAll();
	}

	public byte[] pack_0017or00CE(String cmd, String flag, String id) {
		_Pack pack = new _Pack();
		_Pack enpack = new _Pack();
		enpack.Empty();
		enpack.SetHex(sdk.head);
		enpack.SetShort(sdk.ver_qq);
		enpack.SetHex(cmd);
		enpack.SetHex(flag);
		enpack.SetHex(qq.qqhex);
		enpack.SetHex("02 00 00");
		enpack.SetHex("00 01 01 01");
		enpack.SetHex(sdk.dwpubno);
		pack.Empty();
		pack.SetHex(id);
		enpack.SetBin(this.tea.encrypt(pack.GetAll(), this.key.sessionkey));
		enpack.SetHex(this.sdk.tail);
		return enpack.GetAll();

	}

	public byte[] pack_0002_ToReadMsg(String GroupUin, String MsgId) {
		_Pack pack = new _Pack();
		_Pack enpack = new _Pack();
		enpack.Empty();
		enpack.SetBin(make_common_head("00 02"));
		pack.Empty();
		pack.SetHex("29");
		pack.SetHex(QQ2Hex(Long.valueOf(GroupUin)));
		pack.SetByte(2);
		pack.SetHex(QQ2Hex(Long.valueOf(MsgId)));
		enpack.SetBin(this.tea.encrypt(pack.GetAll(), this.key.sessionkey));
		enpack.SetHex(this.sdk.tail);
		return enpack.GetAll();

	}

	/**
	 * 
	 * @param GroupUin
	 *            群号
	 * @param ObjQQ
	 *            对象QQ
	 * @param time
	 *            0的时候解除禁言,不能大于30天
	 * @return
	 */
	public byte[] pack_0002_ShutUP(String GroupUin, String ObjQQ, int time) {
		if (time > 30 * 24 * 60 * 60 * 1000) {
			return null;
		}

		_Pack pack = new _Pack();
		_Pack enpack = new _Pack();
		enpack.Empty();
		enpack.SetBin(make_common_head("00 02"));
		pack.SetHex("7E");
		pack.SetHex(this.QQ2Hex(Long.valueOf(GroupUin)));
		pack.SetByte(0x20);
		if (Long.valueOf(ObjQQ) == 0) {
			if (time == 0) {
				pack.SetHex("FF FF FF FF");
			} else {
				pack.SetInt(0);
			}
		} else {
			pack.SetShort(1);
			pack.SetHex(this.QQ2Hex(Long.valueOf(ObjQQ)));
			pack.SetInt(0);
		}
		enpack.SetBin(this.tea.encrypt(pack.GetAll(), this.key.sessionkey));
		enpack.SetHex(this.sdk.tail);
		return enpack.GetAll();

	}

	public byte[] pack_0002_SendGroupMsg(String GroupUin, String Msg) {
		_Pack pack = new _Pack();
		_Pack enpack = new _Pack();
		enpack.Empty();
		enpack.SetBin(make_common_head("00 02"));
		pack.Empty();
		pack.SetHex("00 02");
		pack.SetByte(1);
		pack.SetByte(0);
		pack.SetHex("00 00");
		pack.SetInt(0);
		pack.SetHex("4D 53 47 00 00 00 00 00");
		pack.SetHex(GetCurrentTime_hex());
		pack.SetBin(GetRandomBin(4));
		pack.SetHex("00 00 00 00 0A 00 86 22");
		pack.SetShort(12);
		pack.SetHex("E5 BE AE E8 BD AF E9 9B 85 E9 BB 91 ");// 微软雅黑
		pack.SetShort(0);

		// ///////////////////////////////////////////////

		MsgRestructrue[] omg = GroupMsgRestructrue(Msg);
		for (int i = 0; i < omg.length; i++) {
			pack.SetBin(this.GroupMsg_pack(omg[i].type, omg[i].msg));
		}

		// /////////////////////////////////////

		byte[] bin = pack.GetAll();
		pack.Empty();
		pack.SetHex("2A");
		pack.SetHex(QQ2Hex(Long.valueOf(GroupUin)));
		pack.SetToken(bin);
		enpack.SetBin(this.tea.encrypt(pack.GetAll(), this.key.sessionkey));
		enpack.SetHex(this.sdk.tail);
		return enpack.GetAll();

	}

	public byte[] pack_0319_ToReadFriendMsg(String FromQQ, long time) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetBin(make_pbpack_head("03 19"));
		PbPack pb = new PbPack();
		pb.setVarint("0", 0);
		pb.setVarint("0[2]", 17);
		pb.setVarint("0[3]", 0);
		pb.setVarint("0[4]", 16);
		pb.setVarint("1", 1);
		pb.setVarint("2.19", 0);
		pb.setVarint("2.51", 777316216);
		pb.setVarint("2.52", 1);
		pb.setVarint("1.1", Long.valueOf(FromQQ));
		pb.setVarint("1.2", time);
		pb.setVarint("1.4", 0);
		pack.SetBin(this.tea.encrypt(pb.array(), this.key.sessionkey));
		pack.SetHex(this.sdk.tail);
		return pack.GetAll();
	}

	public byte[] pack_03E3_WithdrawGroupMsg(String objqq) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetBin(make_pbpack_head("03 E3"));
		PbPack pb = new PbPack();
		pb.setVarint("0", 0);
		pb.setVarint("0[2]", 25);
		pb.setVarint("0[3]", 0);
		pb.setVarint("0[4]", 18);
		pb.setVarint("1", 1);
		pb.setVarint("2.1", qq.luin);
		pb.setVarint("2.2", 995);
		pb.setVarint("2.19", 0);
		pb.setVarint("2.51", 407496568);
		pb.setVarint("2.52", 1);
		pb.setVarint("1[2]", 2021);
		pb.setVarint("2", 1);
		pb.setVarint("4.11", Long.valueOf(objqq));
		pb.setVarint("4.12", 10001);
		pb.setVarint("4.13", 1);
		pack.SetBin(this.tea.encrypt(pb.array(), this.key.sessionkey));
		pack.SetHex(this.sdk.tail);
		return pack.GetAll();
	}

	private MsgRestructrue[] GroupMsgRestructrue(String Msg) {
		String pattern = "\\[TR:pic=\\{[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\}\\.[jpginf]{3}]|\\[TR:at=[0-9]{5,11}\\]|\\[TR:at=all\\]|\\[TR:FACE=.*?\\]|\\[em=.*?]|.[^\\[\\{\\\\]{0,200}";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(Msg);

		MsgRestructrue[] arr = new MsgRestructrue[0];
		int i = 0;
		while (m.find()) {
			MsgRestructrue data = new MsgRestructrue();
			String str = m.group();

			if (Text_GetLeft(str, "[TR:at=".length()).indexOf("[TR:at=") > -1) {
				data.msg = Text_GetMiddleText(str, "[TR:at=", "]");
				data.type = 4;
			} else if (Text_GetLeft(str, "[TR:face=".length()).indexOf(
					"[TR:face=") > -1) {
				data.msg = Text_GetMiddleText(str, "[TR:face=", "]");
				data.type = 5;
			} else if (Text_GetLeft(str, "[em=".length()).indexOf("[em=") > -1) {
				data.msg = Text_GetMiddleText(str, "[em=", "]");
				data.type = 1;
			} else if (Text_GetLeft(str, "[TR:pic=".length()).indexOf(
					"[TR:pic=") > -1) {
				data.msg = Text_GetMiddleText(str, "[TR:pic=", "]");
				data.type = 3;
			} else {
				data.msg = str;
				data.type = 1;
			}
			// System.out.println("type:" + Integer.toString(data.type));
			// System.out.println("msg:" + data.msg);
			arr = amnslkan(arr, data);
		}
		return arr;

	}

	private MsgRestructrue[] amnslkan(MsgRestructrue[] a, MsgRestructrue b) {
		MsgRestructrue[] arr = new MsgRestructrue[a.length + 1];
		for (int i = 0; i < arr.length; i++) {
			if (i == a.length) {
				arr[i] = b;
				break;
			}
			arr[i] = new MsgRestructrue();
			arr[i] = a[i];

		}

		// System.out.println(a.length);
		// System.out.println(arr.length);
		return arr;
	}

	private byte[] GroupMsg_pack(int type, String msg) {
		_Pack pack = new _Pack();
		_Pack pack_msg = new _Pack();
		pack.Empty();
		switch (type) {
		case 1:
			pack.SetByte(1);
			pack_msg.SetByte(1);
			pack_msg.SetUTF8_Short(msg);
			pack.SetToken(pack_msg.GetAll());
			break;
		case 3:
			pack.SetHex("03");
			pack_msg.SetByte(2);
			if (msg.indexOf("-") == -1) {
				msg = Md5ToPicGuid(msg);
			}
			pack_msg.SetUTF8_Short(msg);
			pack_msg.SetHex("04 00 04 8D AD 36 0A 05 00 04 FE BA F1 78 06 00 04 00 00 00 50 07 00 01 00 08 00 00 09 00 01 01");
			pack_msg.SetHex("0B 00 00 14 00 04 00 00 00 00 15 00 04 00 00 00 51 16 00 04 00 00 00 30 18 00 04 00 00 0A 4B");
			pack_msg.SetHex("FF");
			pack_msg.SetHex("00 5C");
			pack_msg.SetHex("15 36 20");
			pack_msg.SetStr("92kA1");
			pack_msg.SetHex("00");
			pack_msg.SetStr("9b2659e829083c70");
			pack_msg.SetHex("20 20 20 20 20 20 35 30 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20");
			pack_msg.SetStr(msg);
			pack_msg.SetHex("41");
			pack.SetToken(pack_msg.GetAll());
			break;
		case 4:
			if (msg == "all") {

				pack.SetHex("01 00 20 01 00 0D 40 E5 85 A8 E4 BD 93 E6 88 90 E5 91 98 06 00 0D 00 01 00 00 00 05 01 00 00 00 00 00 00");

			} else {
				pack.SetByte(1);
				pack_msg.Empty();
				pack_msg.SetByte(1);
				pack_msg.SetUTF8_Short("@开发者太懒了没做识别昵称");
				pack_msg.SetHex("06 00 0D 00 01 00 00 00 04 00");
				pack_msg.SetHex(QQ2Hex(Long.valueOf(msg)));
				pack_msg.SetZero(2);
				pack.SetToken(pack_msg.GetAll());
			}
			break;
		case 5:
			pack.SetHex("02");
			pack.SetToken(HextoBin(msg));
			break;
		default:
			break;
		}
		return pack.GetAll();
	}

	public byte[] pack_00CD_SendFriendMsg(String RecverQQ, String Msg) {
		_Pack pack = new _Pack();
		_Pack enpack = new _Pack();
		String RecverQQ_Hex = this.QQ2Hex(Long.valueOf(RecverQQ));
		enpack.Empty();
		enpack.SetBin(make_common_head("00 CD"));
		pack.Empty();

		pack.SetHex(this.qq.qqhex);
		pack.SetHex(RecverQQ_Hex);
		pack.SetInt(13);
		pack.SetHex("00 01 00 04 00 00 00 00 00 03 00 01 01");
		pack.SetShort(this.sdk.ver_qq);
		pack.SetHex(this.qq.qqhex);
		pack.SetHex(RecverQQ_Hex);
		pack.SetBin(ByteArr.Md5_(this.HextoBin((RecverQQ_Hex + this
				.Bin2Hex(this.key.sessionkey)).replace(" ", "")

		)));
		pack.SetShort(11);
		pack.SetBin(this.GetRandomBin(2)); // 消息ID不会搞 乱来随机
		String time = this.GetCurrentTime_hex();
		pack.SetHex(time);
		pack.SetHex("00 00");
		pack.SetZero(4);
		pack.SetByte(1);// 总分片数
		pack.SetByte(0);// 第几个分片
		pack.SetHex("00 00");// 有分片时候为随机
		pack.SetByte(1);
		pack.SetHex("4D 53 47 00 00 00 00 00 ");
		pack.SetHex(time);
		pack.SetBin(this.GetRandomBin(4));
		pack.SetHex("00 00 00 00");
		pack.SetByte(10); // 字体大小
		pack.SetByte(0); // 字体样式
		pack.SetHex("86 22"); // 字体编码 utf8
		pack.SetUTF8_Short("微软雅黑"); // 字体名称
		pack.SetShort(0);
		MsgRestructrue[] omg = GroupMsgRestructrue(Msg);
		for (int i = 0; i < omg.length; i++) {
			pack.SetBin(this.FriendMsg_Pack(omg[i].type, omg[i].msg));
		}
		System.out.println(pack.GetAll_Hex());
		enpack.SetBin(this.tea.encrypt(pack.GetAll(), this.key.sessionkey));
		enpack.SetHex(this.sdk.tail);
		return enpack.GetAll();

	}

	private byte[] FriendMsg_Pack(int type, String Msg) {
		_Pack pack = new _Pack();
		_Pack msgp = new _Pack();

		pack.Empty();

		switch (type) {
		case 1:
			pack.SetByte(1);
			msgp.SetByte(1);
			msgp.SetUTF8_Short(Msg);
			pack.SetToken(msgp.GetAll());
			break;
		case 3:
			String[] p = Msg.split("-");
			String pic_md5 = "";
			if (p.length == 3) {
				pic_md5 = p[2];
			} else {
				System.err.println("发送的图片格式不正确!");
				return null;
			}
			pack.SetByte(0x6);
			msgp.SetByte(0x2);
			msgp.SetUTF8_Short("AB%%Q6I9~NNHXKDI7BW`5~1.png");
			msgp.SetByte(0x3);
			msgp.SetInt(373); // 图片大小,固定固定
			msgp.SetByte(0x4);
			msgp.SetUTF8_Short(Msg);
			msgp.SetByte(0x14);
			msgp.SetShort(4);
			msgp.SetHex("03 00 00");
			msgp.SetByte(0x0B);
			msgp.SetShort(0);
			msgp.SetByte(0x18);
			msgp.SetUTF8_Short(Msg);
			msgp.SetByte(0x19);// 宽
			msgp.SetShort(4);
			msgp.SetInt(100);
			msgp.SetByte(0x1A);// 高
			msgp.SetShort(4);
			msgp.SetInt(100);
			msgp.SetByte(0x1F);// ?未知
			msgp.SetShort(4);
			msgp.SetInt(1001);
			msgp.SetByte(0x1B);// 图片MD5
			msgp.SetShort(16);
			msgp.SetHex(pic_md5);
			msgp.SetByte(0x21);
			msgp.SetShort(4);
			msgp.SetHex("08 01 42 00");
			msgp.SetByte(0xFF);
			msgp.SetShort(116);
			msgp.SetHex("16 20");
			msgp.SetStr("116101051AB");
			msgp.SetHex("20 20 20 20 20 20 20");
			msgp.SetStr("373e");
			msgp.SetStr(pic_md5 + ".jpg");
			msgp.SetHex("77");
			msgp.SetStr(Msg);
			msgp.SetHex("41");
			pack.SetToken(msgp.GetAll());
			break;
		default:
			break;
		}
		return pack.GetAll();
	}

	private MsgRestructrue[] FriendMsgRestructrue(String Msg) {
		String pattern = "\\[TR:pic=\\{[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\}\\.[jpginf]{3}]|\\[TR:FACE=.*?\\]|\\[em=.*?]|.[^\\[\\{\\\\]{0,200}";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(Msg);

		MsgRestructrue[] arr = new MsgRestructrue[0];
		int i = 0;
		while (m.find()) {
			MsgRestructrue data = new MsgRestructrue();
			String str = m.group();

			if (Text_GetLeft(str, "[TR:at=".length()).indexOf("[TR:at=") > -1) {
				data.msg = Text_GetMiddleText(str, "[TR:at=", "]");
				data.type = 4;
			} else if (Text_GetLeft(str, "[TR:face=".length()).indexOf(
					"[TR:face=") > -1) {
				data.msg = Text_GetMiddleText(str, "[TR:face=", "]");
				data.type = 5;
			} else if (Text_GetLeft(str, "[em=".length()).indexOf("[em=") > -1) {
				data.msg = Text_GetMiddleText(str, "[em=", "]");
				data.type = 1;
			} else if (Text_GetLeft(str, "[TR:pic=".length()).indexOf(
					"[TR:pic=") > -1) {
				data.msg = Text_GetMiddleText(str, "[TR:pic=", "]");
				data.type = 3;
			} else {
				data.msg = str;
				data.type = 1;
			}
			// System.out.println("type:" + Integer.toString(data.type));
			// System.out.println("msg:" + data.msg);
			arr = amnslkan(arr, data);
		}
		return arr;

	}

	public byte[] pack_005C_GetOwnInfo() {
		_Pack pack = new _Pack();
		_Pack enpack = new _Pack();
		enpack.Empty();
		enpack.SetBin(make_common_head("00 5C"));
		pack.Empty();
		pack.SetHex("88");
		pack.SetHex(qq.qqhex);
		pack.SetHex("00");
		enpack.SetBin(this.tea.encrypt(pack.GetAll(), this.key.sessionkey));
		enpack.SetHex(this.sdk.tail);
		return enpack.GetAll();

	}

	public byte[] pack_() {
		_Pack pack = new _Pack();
		_Pack enpack = new _Pack();
		enpack.Empty();
		enpack.SetBin(make_common_head(""));
		pack.Empty();

		enpack.SetBin(this.tea.encrypt(pack.GetAll(), this.key.sessionkey));
		enpack.SetHex(this.sdk.tail);
		return enpack.GetAll();

	}

	// ////////////////////////////////////////////////////////////////////////////////////////

	private byte[] make_login_head(String cmd) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex(sdk.head);
		pack.SetShort(sdk.ver_qq);
		pack.SetHex(cmd);
		pack.SetBin(GetRandomBin(2));
		pack.SetHex(qq.qqhex);
		pack.SetHex("03 00 00");
		pack.SetHex("00 01 01 01");
		pack.SetHex(sdk.dwpubno);
		pack.SetHex("00 00 00 00");
		return pack.GetAll();
	}

	private byte[] make_common_head(String cmd) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex(sdk.head);
		pack.SetShort(sdk.ver_qq);
		pack.SetHex(cmd);
		pack.SetBin(GetRandomBin(2));
		pack.SetHex(qq.qqhex);
		pack.SetHex("02 00 00");
		pack.SetHex("00 01 01 01");
		pack.SetHex(sdk.dwpubno);
		return pack.GetAll();
	}

	private byte[] make_pbpack_head(String cmd) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex(sdk.head);
		pack.SetShort(sdk.ver_qq);
		pack.SetHex(cmd);
		pack.SetBin(GetRandomBin(2));
		pack.SetHex(qq.qqhex);
		pack.SetHex("04 00 00");
		pack.SetHex("00 01 01 01");
		pack.SetHex(sdk.dwpubno);
		pack.SetInt(0);
		pack.SetInt(0);
		return pack.GetAll();
	}

	public void cmd() throws IOException {
		Thread t = new Thread(new VerifyQrcode());
		BufferedReader buf = new BufferedReader(
				new InputStreamReader(System.in));
		System.out.println("请输入编号使用命令(输入cmd重新载入本界面)");
		System.out.println("1.账号密码登录");
		System.out.println("2.扫码登录");
		System.out.println("3.发送群消息");
		System.out.println("4.发送好友消息");
		String cmdString = buf.readLine();
		switch (cmdString) {
		case "cmd":
			cmd();
			break;
		case "1":
			System.out.println("请输入QQ账号:");
			String user = buf.readLine();
			System.out.println("请输入密码:");
			String pass = buf.readLine();
			if (this.info(user, pass)) {
				this.udp.send(this.pack_0825(false));
				this.CmdDecide(this.udp.getbin());
				this.udp.send(this.pack_0836_CommonLogin());
				this.CmdDecide(this.udp.getbin());
			}
			break;
		case "2":
			this.info("0", "0");
			this.udp.send(this.pack_0825(false));
			this.CmdDecide(this.udp.getbin());
			this.udp.send(this.pack_0818());
			this.CmdDecide(this.udp.getbin());
			t.start();
			break;
		case "3":
			System.out.println("请输入要发送的群:");
			String GroupUin = buf.readLine();
			System.out.println("请输入要发送的内容:");
			String msg = buf.readLine();

			this.udp.send(pack_0002_SendGroupMsg(GroupUin, msg));
			break;
		case "4":
			System.out.println("请输入好友QQ:");
			String RecverQQ = buf.readLine();
			System.out.println("请输入要发送的内容：");
			String Msg = buf.readLine();
			this.udp.send(this.pack_00CD_SendFriendMsg(RecverQQ, Msg));
		default:
			break;
		}
		System.out.println("");
		cmd();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) throws IOException {
		qq pcqq = new qq();
		pcqq.link_tx();
		pcqq.p.SetPcqqMain(pcqq);
		Thread plugintThread = new Thread(pcqq.p);
		plugintThread.start();
		System.out.println("插件系统已启动!");
		pcqq.cmd();

	}

	public void SendGroupMsg(String GroupUin, String Msg) {
		this.udp.send(this.pack_0002_SendGroupMsg(GroupUin, Msg));
	}

	public void SendFriendMsg(String FriendQQ, String Msg) {
		this.udp.send(this.pack_00CD_SendFriendMsg(FriendQQ, Msg));
	}

	public void QQLogin(String qquin, String password) {
		if (this.info(qquin, password)) {
			this.udp.send(this.pack_0825(false));
			this.CmdDecide(this.udp.getbin());
			this.udp.send(this.pack_0836_CommonLogin());
			this.CmdDecide(this.udp.getbin());
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	public String DelSpaceAll(String str) {
		return str.replaceAll(" ", "");
	}

	public String GetCurrentTime_hex() {
		return (this.QQ2Hex(System.currentTimeMillis() / 1000));
	}

	public String GetCrc32(String crcdata) {
		CRC32 crc32 = new CRC32();
		byte[] mCrc32Buf = new byte[4];
		crc32.update(HextoBin(crcdata));
		mCrc32Buf[0] = (byte) (crc32.getValue() & 0x000000FF);
		mCrc32Buf[1] = (byte) ((crc32.getValue() & 0x0000FF00) >> 8);
		mCrc32Buf[2] = (byte) ((crc32.getValue() & 0x00FF0000) >> 16);
		mCrc32Buf[3] = (byte) ((crc32.getValue() & 0xFF000000) >> 24);
		return Bin2Hex(mCrc32Buf);
	}

	public String IPtoHex(String ip) {
		if (ip == null) {
			return "00 00 00 00";
		}

		String[] arr = ip.split("\\.");
		String hex = "";
		String[] hexlit;
		String data = "";
		int b = 0;
		for (int i = 0; i < arr.length; i++) {

			b = Integer.parseInt(arr[i]);
			hex = Bin2Hex(ByteArr.intToBytes(b));
			hexlit = hex.split(" ");
			data = data + hexlit[3] + " ";
		}

		return data;
	}

	public String Bin2Str(byte[] bin) {
		String str;
		try {
			str = new String(bin, "UTF-8");
			return str;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String Bin2Hex(byte[] bin) {
		return ByteArr.bytesToHexStr(bin);
	}

	public byte[] HextoBin(String hex) {
		return ByteArr.hexStrToBytes(hex.replace(" ", ""));
	}

	public String HexToIp(String Hex) {
		byte[] bin = HextoBin(Hex);
		if (bin.length != 4) {
			return "请输入4字节Hex";
		}
		// 没办法太笨了只会这样写
		String[] HexLit = Hex.trim().split(" ");
		String data = "";
		for (int j = 0; j < HexLit.length; j++) {
			HexLit[j] = "00 00 00 " + HexLit[j];
			data = data + Integer.toString(Bin2Int(HextoBin(HexLit[j])));

			if (j + 1 != HexLit.length) {
				data += ".";
			}

		}
		return data;
	}

	public byte[] GetRandomBin(int i) {
		return ByteArr.getRandomBytes(i);
	}

	public String QQ2Hex(Long qquin) {
		return ByteArr.bytesToHexStr(ByteArr.getByteRight(
				ByteArr.flip(ByteArr.longToBytes(qquin)), 4));
	}

	public String bin2qq(byte[] bin) {
		if (bin.length != 4) {
			return "";
		}
		long i = ByteArr.bytesToInt(bin);
		if (i < 0) {
			i = ByteArr.bytesToLong(ByteArr.flip(ByteArr.byteMerger(
					HextoBin("00 00 00 00"), bin)));
		}
		return Long.toString(i);
	}

	public String Text_GetRight(String data, int len) {
		System.out.println(data.length() - len);
		return data.substring(data.length() - len, data.length());
	}

	public String Text_GetLeft(String data, int len) {
		if (len > data.length()) {
			return "";
		}
		return data.substring(0, len);

	}

	public String GetTxServerIp() {
		if (this.IpIdx > 9) {
			this.IpIdx = 1;
		}

		try {
			switch (this.IpIdx) {
			case 1:
				return InetAddress.getByName("sz.tencent.com").getHostAddress();
			case 2:
				return InetAddress.getByName("sz2.tencent.com")
						.getHostAddress();
			case 3:
				return InetAddress.getByName("sz3.tencent.com")
						.getHostAddress();
			case 4:
				return InetAddress.getByName("sz4.tencent.com")
						.getHostAddress();
			case 5:
				return InetAddress.getByName("sz5.tencent.com")
						.getHostAddress();
			case 6:
				return InetAddress.getByName("sz6.tencent.com")
						.getHostAddress();
			case 7:
				return InetAddress.getByName("183.60.56.29").getHostAddress();
			case 8:
				return InetAddress.getByName("sz8.tencent.com")
						.getHostAddress();
			case 9:
				return InetAddress.getByName("sz9.tencent.com")
						.getHostAddress();

			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
		return null;

	}

	public String Text_GetMiddleText(String data, String index, String foot) {
		int in = data.indexOf(index);
		if (in != -1) {
			in = in + index.length();
		}
		int ft = data.indexOf(foot, in);
		if (ft == -1 || in == -1) {
			return "";
		}
		return data.substring(in, ft);
	}

	public String Md5ToPicGuid(String md5) {
		String 格式 = Text_GetRight(md5, 4);
		if (格式 == "" || 格式 == null) {
			格式 = ".jpg";
		}
		md5 = md5.replaceAll("\\.", "").replaceAll("jpg", "")
				.replaceAll("png", "").replaceAll("gif", "");
		String guid = "{" + md5.substring(0, 8) + "-" + md5.substring(9, 13)
				+ "-" + md5.substring(13, 17) + "-" + md5.substring(17, 21)
				+ "-" + Text_GetRight(md5, 12) + "}" + 格式;
		return guid;

	}

	/**
	 * 取随机数
	 * 
	 * @param min
	 *            最小值
	 * @param max
	 *            最大值
	 * @return
	 */
	public int GetRandmo(int min, int max) {
		return ByteArr.random(min, max);
	}

	/**
	 * 字节集到整数
	 * 
	 * @param bin
	 * @return
	 */
	public int Bin2Int(byte[] bin) {
		return ByteArr.bytesToInt(bin);

	}

	// //////////////////////////////////////////////////////////////////////////////////////////

	public void CmdDecide(byte[] data) {
		if (data == null) {
			return;
		}
		_Unpack unpack = new _Unpack();
		String 加密方式 = null;
		unpack.SetData(data);
		unpack.GetBin(3);
		String cmd = Bin2Hex(unpack.GetBin(2)).trim();
		String flag = Bin2Hex(unpack.GetBin(2)).trim();
		unpack.GetInt();// QQ号
		加密方式 = Bin2Hex(unpack.GetBin(3)).trim();

		unpack.SetData(unpack.GetBin(unpack.Len() - 1));
		byte[] bin = unpack.GetAll();

		switch (cmd) {
		case "08 25":
			this.x0825(bin);
			break;
		case "08 36":
			this.x0836(bin, 加密方式);
			break;
		case "08 28":
			this.x0828(bin);
			break;
		case "08 18":
			this.x0818(bin);
			break;
		case "08 19":
			this.x0819(bin);
			break;
		case "00 17":
			this.x0017("00 17", bin, flag);
			break;
		case "00 CE":
			this.x0017("00 CE", bin, flag);
			break;
		case "00 5C":
			this.x005C(bin);
			break;
		case "00 EC":
			System.out.println(this.qq.qquin + "(" + this.qq.nick + ")->上线成功!");
			sessionkey = key.sessionkey;
			// String sql = "UPDATE `qqinfo` SET `password` = '"
			// + this.DelSpaceAll(this.Bin2Hex(this.key.MD52))
			// + "',`SessionKey`= '"
			// + this.DelSpaceAll(this.Bin2Hex(this.key.sessionkey))
			// + "' ,`nick`='" + this.qq.nick + "'"
			// + " WHERE `qqinfo`.`qquin` = " + this.qq.qquin;
			// DB.query(sql);

			this.udp.send(this.pack_005C_GetOwnInfo());
			Thread hreat = new Thread(new HreatBoot());
			hreat.start();

			break;
		case "00 02":
			// System.out.println(Bin2Hex(tea.decrypt(bin, key.sessionkey)));
			break;
		case "00 58":
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String dateStr = sdf.format(date);
			System.out.println("心跳包[" + dateStr + "]");
			break;
		default:
			System.out.println("cmd :" + cmd);
			System.out.println("加密方式:" + 加密方式);
			break;
		}
	}

	/**
	 * 循环接收线程
	 */
	public void runthead() {
		Thread thread = new Thread(new GetByte());
		thread.start();
	}

	class HreatBoot implements Runnable {

		@Override
		public void run() {

			while (true) {
				_Pack pack = new _Pack();
				_Pack enpack = new _Pack();
				enpack.Empty();
				enpack.SetBin(make_common_head("00 58"));
				pack.Empty();
				pack.SetShort(1);
				pack.SetShort(1);
				enpack.SetBin(tea.encrypt(pack.GetAll(), sessionkey));
				enpack.SetHex(sdk.tail);
				udp.send(enpack.GetAll());
				try {
					Thread.sleep(60000);// 一分钟一次
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class GetByte implements Runnable {
		@Override
		public void run() {
			while (true) {
				byte[] data = udp.getbin();
				if (data != null) {// 好原始的办法啊，本来想用其他组件的但是没说明
					CmdDecide(data);
				}

			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////

	class VerifyQrcode implements Runnable {
		@Override
		public void run() {
			while (!iVerifyQrcode) {

				try {
					udp.send(pack_0819());
					CmdDecide(udp.getbin());
					Thread.sleep(10000);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

	}

}
// //////////////////////////////////////////////////////////////////////////////////////////

