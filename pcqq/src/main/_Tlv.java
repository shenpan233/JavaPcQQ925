package main;

import tools.ByteArr;
import tools.TEA;
import Pack._Pack;

public class _Tlv {
	String tlv_ver = "01 03";// wDHVer - 降级成0102就不用二次ecdh 0103要二次
	// String sso_ver = "00 00 04 53";// 1107
	// String dwClient_ver = "00 00 15 85";// ClientVer:5509
	// String dwpubno_ver = "00 00 69 5D";
	// 下面新版的
	String sso_ver = "00 00 04 57";
	String dwClient_ver = "00 00 16 0F";
	String dwpubno_ver = "00 00 69 34";
	TEA tea = new TEA();

	private byte[] pack(String cmd, byte[] data) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex(cmd);
		pack.SetShort(data.length);
		pack.SetBin(data);
		return pack.GetAll();
	}

	/**
	 * 
	 * @param qqhex
	 * @param login_num
	 *            登录次数/重定向次数
	 * @return
	 */
	public byte[] tlv018(String qqhex, int login_num) {
		_Pack pack = new _Pack();

		pack.SetShort(1);
		pack.SetHex(this.sso_ver);
		pack.SetInt(1);
		pack.SetHex(dwClient_ver);
		pack.SetHex(qqhex);
		pack.SetInt(login_num);

		return pack("00 18", pack.GetAll());
	}

	/**
	 * 
	 * @param ip
	 *            IP的hex
	 * @param u
	 *            是否不使用上次IP登录?
	 * @return
	 */
	public byte[] tlv309(String ip, boolean u) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex("00 01");
		pack.SetHex(ip);
		pack.SetByte(0);// cPingType 重定向，0x05,

		if (u) {// 利用上次登录的IP，0x04, 否则，0x01或者0x02
			pack.SetByte(0x2);
		} else {
			pack.SetByte(0x4);

		}
		return pack("03 09", pack.GetAll());

	}

	public byte[] tlv036() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetShort(2);
		pack.SetShort(1);
		pack.SetInt(5);
		pack.SetHex("00 00 00 00 00 00 00 00 00 00");

		return pack("00 36", pack.GetAll());
	}

	public byte[] tlv114(byte[] publickey) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex(tlv_ver);// wDHVer - 降级成0102就不用二次ecdh 0103要二次
		pack.SetToken(publickey);
		return pack("01 14", pack.GetAll());
	}

	public byte[] tlv103(byte[] publicKey) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetBin(publicKey);
		return pack("01 03", pack.GetAll());
	}

	public byte[] tlv000(byte[] randmokey) {
		_Pack pack = new _Pack();
		pack.Empty();
		if (randmokey == null) {
			randmokey = ByteArr.getRandomBytes(16);
		}
		pack.SetBin(randmokey);

		return pack("00 00", pack.GetAll());
	}

	public byte[] tlv112(byte[] _0825token) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetBin(_0825token);

		return pack("01 12", pack.GetAll());
	}

	public byte[] tlv30F(String pc_name) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetToken(pc_name.getBytes());

		return pack("03 0F", pack.GetAll());
	}

	public byte[] tlv005(String qqhex) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetShort(2);
		pack.SetHex(qqhex);
		return pack("00 05", pack.GetAll());
	}

	public byte[] tlv006(String qqhex, String MD51, byte[] MD52,
			String timehex, String ip_hex, byte[] tgtkey, byte[] tlv000) {
		_Pack pack = new _Pack();
		TEA tea = new TEA();
		pack.Empty();
		pack.SetBin(ByteArr.getRandomBytes(4));
		pack.SetShort(2);
		pack.SetHex(qqhex);
		pack.SetHex(sso_ver);
		pack.SetZero(2);
		pack.SetShort(1);
		pack.SetHex(dwClient_ver);
		pack.SetZero(2);
		pack.SetByte(0);// 是否记住密码记住为1，否则为0
		pack.SetHex(MD51);
		pack.SetHex(timehex);
		pack.SetZero(13);
		pack.SetHex(ip_hex);
		pack.SetZero(6);
		pack.SetBin(tlv000);
		pack.SetBin(tgtkey);
		pack.SetData(tea.encrypt(pack.GetAll(), MD52));
		return pack("00 06", pack.GetAll());
	}

	public byte[] tlv015(String machine_code) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex("00 01 01 93 2B 7E A2 00 10 6A 0A 76 34 00 00 00 00 00 00 00 00 00 00 00 00 02 53 E6 EF B6 00 10");
		pack.SetHex(machine_code);
		return pack("00 15", pack.GetAll());
	}

	public byte[] tlv01A(byte[] tlv015, byte[] tgtkey) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetBin(tlv015);
		pack.SetData(tea.encrypt(pack.GetAll(), tgtkey));
		return pack("00 1A", pack.GetAll());
	}

	public byte[] tlv312() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex("01 00 00 00 01");
		return pack("03 12", pack.GetAll());
	}

	public byte[] tlv313() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetByte(1);
		pack.SetBin(pack("01 02", ByteArr.getRandomBytes(16)));
		pack.SetHex("00 00 00 1F");
		return pack("03 13", pack.GetAll());
	}

	public byte[] tlv102(byte[] _0836token, String crc_data, String crc32) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetShort(1);
		pack.SetBin(ByteArr.getRandomBytes(16));
		pack.SetToken(_0836token);
		pack.SetShort(20);
		pack.SetHex(crc_data);
		pack.SetHex(crc32);

		return pack("01 02", pack.GetAll());
	}

	public byte[] tlv508() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex("01 00 00 00 02");
		return pack("05 08", pack.GetAll());
	}

	public byte[] tlv103_2(byte[] VerificationCode_key) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetShort(1);
		pack.SetShort(16);
		pack.SetBin(VerificationCode_key);
		return pack("01 03", pack.GetAll());
	}

	public byte[] tlv032(String md51, byte[] _0825key) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex("3E 00 63 02 04 05 00 09 00 04 00 57 7B 00 00 00 00 CB 07 71 4D FF 17 61 6A 83 19 04 3E 67 5F A5 4C 05 01");
		pack.SetHex(dwpubno_ver);
		pack.SetHex("16 0F 00 00 01 02 00 00 08 04 07 DF 00 0A 00 0C 00 01 00 04 00 05 00 00 24 A4 00 0A");
		pack.SetHex(md51);
		pack.SetBin(_0825key);

		return pack("00 32", pack.GetAll());
	}

	public byte[] tlv305() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex("00 00 00 00 00 00 00 05 00 00 00 04 00 00 00 00 00 00 00 48 00 00 00 02 00 00 00 02 00 00");
		return pack("03 05", pack.GetAll());
	}

	public byte[] tlv019() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex("00 01");
		pack.SetHex(sso_ver);
		pack.SetHex("00 00 00 01");
		pack.SetHex(dwClient_ver);
		pack.SetHex("00 00");
		return pack("00 19", pack.GetAll());
	}

	/**
	 * 二维码使用！
	 * 
	 * @param qrsing
	 *            随机
	 * @return
	 */
	public byte[] tlv301(byte[] qrsing) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetToken(qrsing);
		return pack("03 01", pack.GetAll());
	}

	/**
	 * 0819扫码成功后返回的
	 * 
	 * @param _0819token
	 *            0819扫码成功后返回
	 * @return
	 */
	public byte[] tlv303(byte[] _0819token) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetBin(_0819token);
		return pack("03 03", pack.GetAll());
	}

	public byte[] tlv507(boolean isGoOnline) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetByte(1);
		if (isGoOnline) {
			pack.SetZero(16);
		} else {
			pack.SetHex("D2 19 DF 14 15 90 A1 E3 56 66 34 E6 05 D0 61 31");
		}
		return pack("05 07", pack.GetAll());
	}

	/**
	 * 
	 * @param token36
	 *            len56
	 * @return
	 */
	public byte[] tlv110(byte[] token0836) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetShort(1);
		pack.SetShort(56);
		pack.SetToken(token0836);

		return pack("01 10", pack.GetAll());
	}

	public byte[] SetToken(String CMD, byte[] token) {
		return pack(CMD, token);
	}

	public byte[] tlv007(byte[] _88token) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetBin(_88token);

		return pack("00 07", pack.GetAll());
	}

	public byte[] tlv00C(String login_ip) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetShort(2);
		pack.SetShort(0);
		pack.SetShort(0);
		pack.SetInt(5);
		pack.SetInt(0);
		if (login_ip == null) {
			login_ip = "3B 24 77 1A";
		}
		pack.SetHex(login_ip);
		pack.SetShort(8000);
		pack.SetInt(0);

		return pack("00 0C", pack.GetAll());
	}

	/**
	 * 32位机器码不会生成只能固定了
	 * 
	 * @return
	 */
	public byte[] tlv01F() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetShort(1);
		pack.SetHex("28 1C 3E 96 35 D1 F5 92 F6 80 86 2B 1E BC AB DD 9A 27 D4 21 25 42 F3 5A 38 0F 49 22 DF AB A0 99");
		return pack("00 1F", pack.GetAll());
	}

	public byte[] tlv01B() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetShort(2);
		pack.SetHex("58 D1 6F C1 80 EF 1F F1 0D FA 98 98 F8 DF 75 AD");
		pack.SetByte(16);
		pack.SetInt(0);
		pack.SetInt(2);
		pack.SetInt(0);
		pack.SetInt(0);
		pack.SetShort(0);
		return pack("00 1B", pack.GetAll());
	}

	/**
	 * 
	 * @return
	 */
	public byte[] tlv02D() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetShort(1);
		pack.SetHex("C0 A8 0A 01");// 内网IP 192.168.10.1
		return pack("00 2D", pack.GetAll());
	}

	public byte[] tlv105() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetHex("00 01 01 02");
		pack.SetHex("00 40 02 01 03 3C 01 03 00 00");
		pack.SetBin(ByteArr.getRandomBytes(56));
		pack.SetHex("00 40 02 02 03 3C 01 03 00");
		pack.SetBin(ByteArr.getRandomBytes(56));
		return pack("01 05", pack.GetAll());
	}

	public byte[] tlv10B() {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetShort(2);
		pack.SetHex("58 D1 6F C1 80 EF 1F F1 0D FA 98 98 F8 DF 75 AD");
		pack.SetBin(ByteArr.getRandomBytes(1));
		pack.SetByte(16);
		pack.SetInt(16);
		pack.SetInt(2);
		pack.SetInt(0);
		pack.SetShort(0);
		return pack("01 0B", pack.GetAll());
	}

	public byte[] tlv030(byte[] _0828token) {
		_Pack pack = new _Pack();
		pack.Empty();
		pack.SetToken(_0828token);
		return pack("00 30", pack.GetAll());
	}

	public byte[] tlv() {
		_Pack pack = new _Pack();
		pack.Empty();

		return pack("", pack.GetAll());
	}

}
