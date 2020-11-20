package Pack;

import java.io.UnsupportedEncodingException;

import tools.ByteArr;

public class _Pack {
	private byte[] m_bin;

	public byte[] GetAll() {
		return m_bin;
	}

	public String GetAll_Hex() {
		return ByteArr.bytesToHexStr(GetAll());
	}

	public void Empty() {
		m_bin = null;
	}

	public int GetLen() {
		return m_bin.length;
	}

	public void SetBin(byte[] bytes) {
		if (bytes == null) {
			return;
		}
		if (this.m_bin != null) {
			int thisLength = this.m_bin.length;
			int length = bytes.length;
			byte[] newByte = new byte[thisLength + length];
			System.arraycopy(this.m_bin, 0, newByte, 0, thisLength);
			System.arraycopy(bytes, 0, newByte, thisLength, length);
			this.m_bin = newByte;
		} else {
			this.m_bin = bytes;
		}
	}

	public void SetByte(int by) {
		byte b = (byte) by;
		this.SetBin(new byte[] { b });
	}

	public void SetData(byte[] data) {
		m_bin = data;
	}

	public void SetHex(String Hex) {
		this.SetBin(ByteArr.hexStrToBytes(Hex));
	}

	public void SetStr(String t) {
		try {
			this.SetBin(t.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
	}

	public void SetUTF8_Short(String t) {
		try {
			this.SetShort((short) t.getBytes("UTF-8").length);
			this.SetBin(t.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
	}

	public void SetUtf8_Int(String t) {
		this.SetInt(t.length());
		this.SetStr(t);
	}

	public void SetInt(int i) {
		this.SetBin(ByteArr.intToBytes(i));
	}

	public void SetShort(int S) {
		this.SetBin(ByteArr.shortToBytes((short) S));
	}

	public void SetLong(Long l) {
		this.SetBin(ByteArr.longToBytes(l));
	}

	public void SetToken(byte[] token) {
		SetShort(token.length);
		SetBin(token);
	}

	public void SetZero(int num) {
		for (int i = 0; i < num; i++) {
			SetByte(0);
		}
	}

	private byte[] addByte(byte[] bt1, byte[] new_bt2) {
		byte[] bt3 = new byte[bt1.length + new_bt2.length];
		System.arraycopy(bt1, 0, bt3, 0, bt1.length);
		System.arraycopy(new_bt2, 0, bt3, bt1.length, new_bt2.length);
		return bt3;
	}
}
