package Pack;

import tools.ByteArr;

public class JceOutputStream {

	private _Pack pack;

	public JceOutputStream() {
		this.pack = new _Pack();
	}

	public void clear() {
		this.pack.Empty();
	}

	public byte[] toByteArray() {
		return this.pack.GetAll();
	}

	public void wrap(byte[] bin) {
		this.pack.SetBin(bin);
	}

	private void writeHead(byte p_val, int p_tag) {
		if (p_tag >= 15) {
			this.pack.SetByte((byte) (p_val | 240));
			this.pack.SetByte((byte) p_tag);
		} else {
			this.pack.SetByte((byte) (p_val | (p_tag << 4)));
		}
	}

	public void writeObj(byte p_type, byte[] p_val, int p_tag) {
		switch (p_type) {
		case 0:
			this.writeByte((byte) ByteArr.bytesToInt(p_val), p_tag);
			break;
		case 1:
			this.writeShort((short) ByteArr.bytesToInt(p_val), p_tag);
			break;
		case 2:
			this.writeInt(ByteArr.bytesToInt(p_val), p_tag);
			break;
		case 3:
			this.writeLong(ByteArr.bytesToInt(p_val), p_tag);
			break;
		case 13:
			this.writeSimpleList(p_val, p_tag);
			break;
		case 8:
			System.out.println("error can't write map");
			break;
		case 6:
		case 7:
			this.writeStringByte(p_val, p_tag);
			break;
		case 9:
			this.writeList(p_val, p_tag);
			break;
		default:
			System.out.println("error WriteObj  type=" + p_type);
			break;

		}
	}

	public void writeByte(byte p_val, int p_tag) {
		if (p_val == 0) {
			this.writeHead((byte) 12, p_tag);
		} else {
			this.writeHead((byte) 0, p_tag);
			this.pack.SetByte(p_val);
		}
	}

	public void writeShort(short p_val, int p_tag) {
		if (p_val <= 127 && p_val >= -128) {
			this.writeByte((byte) p_val, p_tag);
		} else {
			this.writeHead((byte) 1, p_tag);
			this.pack.SetShort(p_val);
		}
	}

	public void writeInt(int luin, int p_tag) {
		if (luin <= 32767 && luin >= -32768) {
			this.writeShort((short) luin, p_tag);
		} else {
			this.writeHead((byte) 2, p_tag);
			this.pack.SetInt(luin);
		}
	}

	public void wirteHex(String hex) {
		this.pack.SetHex(hex);
	}

	public void writeLong(long p_val, int p_tag) {
		if (p_val <= 2147483647 && p_val >= -2147483648) {
			this.writeInt((int) p_val, p_tag);
		} else {
			this.writeHead((byte) 3, p_tag);
			this.pack.SetBin(ByteArr.longToBytes(p_val));
		}
	}

	/**
	 * Ð´HEXÊý¾ÝµÄ
	 * 
	 * @param p_val
	 * @param p_tag
	 */
	public void writeByteString(String p_val, int p_tag) {
		byte[] t_val = ByteArr.hexStrToBytes(p_val.trim());
		if (t_val.length > 255) {
			this.writeHead((byte) 7, p_tag);
			this.pack.SetInt(t_val.length);
			this.pack.SetBin(t_val);
		} else {
			this.writeHead((byte) 6, p_tag);
			this.pack.SetByte((byte) t_val.length);
			this.pack.SetBin(t_val);
		}
	}

	public void writeStringByte(byte[] p_val, int p_tag) {
		// byte[] t_val = p_val.getBytes();
		if (ByteArr.getByteLeft(p_val, p_val.length - 1).length > 255) {
			this.writeHead((byte) 7, p_tag);
			this.pack.SetInt(p_val.length);
			this.pack.SetBin(p_val);
		} else {
			this.writeHead((byte) 6, p_tag);
			this.pack.SetByte((byte) p_val.length);
			this.pack.SetBin(p_val);
		}
	}

	public void writeJceStruct(byte[] p_val, int p_tag) {
		this.writeHead((byte) 10, p_tag);
		pack.SetBin(p_val);
		this.writeHead((byte) 11, 0);
	}

	public void writeSimpleList(byte[] p_val, int p_tag) {
		this.writeHead((byte) 13, p_tag);
		this.writeHead((byte) 0, 0);
		this.writeInt(p_val.length, 0);
		this.pack.SetBin(p_val);
	}

	public void writeList(byte[] p_val, int p_tag) {
		this.writeHead((byte) 9, p_tag);
		this.writeInt(p_val.length, 0);
		for (byte b : p_val) {
			this.writeInt(b, 0);
		}
	}

	public void putHex(String hex) {
		this.pack.SetStr(hex);
	}
}
