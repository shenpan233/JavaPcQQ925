package Pack;

import java.io.UnsupportedEncodingException;

import tools.ByteArr;

public class _Unpack {
	byte[] bin = new byte[0];

	/**
	 * ������
	 * 
	 * @param b
	 *            Ҫ���������
	 */
	public void SetData(byte[] b) {
		bin = b;
	}

	/**
	 * ȡʣ�೤��
	 * 
	 * @return ������
	 */
	public int Len() {
		if (bin == null) {
			return 0;
		}
		return bin.length;
	}

	/**
	 * ȡ�ֽڼ���ɾ����ȡ��
	 * 
	 * @param len
	 *            Ҫȡ�ĳ���
	 * @return Ҫȡ��
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
	 * @return ����һ���ֽ�
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
	 * ȡǰ�����ֽ�
	 * 
	 * @return �������ֽ�->����
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
	 * ȡǰ��8�ֽ�
	 * 
	 * @return ����8�ֽ�->������
	 */
	public long GetLong() {
		return ByteArr.bytesToLong(GetBin(8));
	}

	/**
	 * ����2�ֽ�->������
	 * 
	 * @return ����2�ֽ�->������
	 */
	public int GetShort() {
		int i = ByteArr.bytesToShort(GetBin(2));
		return i;
	}

	/**
	 * Shout + bin��ʽ ͨ��GetShort �� GetBin tlv��Ч������
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
