package Pack;

import java.io.UnsupportedEncodingException;

import tools.ByteArr;

public class _Unpack {
	byte[] bin = new byte[0];

	/**
	 * 置数据
	 * 
	 * @param b
	 *            要置入的数据
	 */
	public void SetData(byte[] b) {
		bin = b;
	}

	/**
	 * 取剩余长度
	 * 
	 * @return 整数型
	 */
	public int Len() {
		if (bin == null) {
			return 0;
		}
		return bin.length;
	}

	/**
	 * 取字节集并删除已取得
	 * 
	 * @param len
	 *            要取的长度
	 * @return 要取的
	 */
	public byte[] GetBin(int len) {
		if (bin == null) {
			return null;
		}
		byte[] data = ByteArr.getByteLeft(bin, len);
		bin = ByteArr.getByteRight(bin, bin.length - len);

		return data;
	}

	/**
	 * 
	 * @return 返回一个字节
	 */
	public int GetByte() {
		byte[] data = GetBin(1);

		return data[0];
	}

	public byte GetByte_byte() {
		byte[] data = GetBin(1);
		data = ByteArr.flip(data);
		return data[0];
	}

	/**
	 * 取前面四字节
	 * 
	 * @return 返回四字节->整数
	 */
	public int GetInt() {
		if (this.bin == null) {
			return 0;
		}
		byte[] bin = ByteArr.getByteLeft(this.bin, 4);
		this.bin = ByteArr.getByteRight(this.bin, this.bin.length - 4);
		return ByteArr.bytesToInt(bin);
	}

	/**
	 * 取前面8字节
	 * 
	 * @return 返回8字节->短整数
	 */
	public long GetLong() {
		return ByteArr.bytesToLong(GetBin(8));
	}

	/**
	 * 返回2字节->短整数
	 * 
	 * @return 返回2字节->短整数
	 */
	public int GetShort() {
		int i = ByteArr.bytesToShort(GetBin(2));
		return i;
	}

	/**
	 * Shout + bin形式 通过GetShort 和 GetBin tlv用效果极佳
	 * 
	 * @return bin
	 */
	public byte[] GetToken() {
		int len = GetShort();
		return GetBin(len);
	}

	public String GetString(int len) {
		String str;
		try {
			str = new String(this.GetBin(len), "UTF-8");
			return str;
		} catch (UnsupportedEncodingException e) {
			return "";
		}

	}

	public String GetUtf8_Short() {
		String str;
		try {
			str = new String(GetToken(), "UTF-8");

			return str;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}

	}

	public byte[] GetAll() {
		return bin;
	}

	public String GetAll_Hex() {
		return ByteArr.bytesToHexStr(this.GetAll());
	}
}
