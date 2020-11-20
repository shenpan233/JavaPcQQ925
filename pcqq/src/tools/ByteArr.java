package tools;

import java.security.MessageDigest;

public class ByteArr {

	private ByteArr() {

	}

	public static int random(int min, int max) {
		return (int) ((Math.random() * (max - min) + min));
	}

	public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
		if (bt1 == null || bt2 == null) {
			return null;
		}
		byte[] bt3 = new byte[bt1.length + bt2.length];
		System.arraycopy(bt1, 0, bt3, 0, bt1.length);
		System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
		return bt3;
	}

	public static byte[] hexStrToBytes(String hexString) {
		if (hexString == null || hexString.trim().length() < 2) {
			return null;
		}
		hexString = hexString.replaceAll(" ", "");
		int len = hexString.length();
		int index = 0;
		byte[] bytes = new byte[len / 2];
		try {
			while (index < len) {
				String sub = hexString.substring(index, index + 2);
				bytes[index / 2] = (byte) Integer.parseInt(sub, 16);
				index += 2;
			}
		} catch (Exception ignored) {
			return null;
		}
		return bytes;
	}

	public static String bytesToHexStr(byte[] bytes) {
		if (bytes == null) {
			return "";
		}
		String hexStr = "0123456789ABCDEF";
		StringBuilder result = new StringBuilder();
		String hex;
		for (byte b : bytes) {
			hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
			hex += String.valueOf(hexStr.charAt(b & 0x0F));
			result.append(hex).append(" ");
		}
		return result.toString();
	}

	public static byte[] intToBytes(int num) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (num >>> 24);
		bytes[1] = (byte) (num >>> 16);
		bytes[2] = (byte) (num >>> 8);
		bytes[3] = (byte) num;
		return bytes;
	}

	// byte鏁扮粍杞崲鎴恑nt
	public static int bytesToInt(byte[] bytes) {
		if (bytes == null || bytes.length < 4) {
			return 0;
		}
		int result;
		result = bytes[0] & 0xff;
		result = result << 8 | bytes[1] & 0xff;
		result = result << 8 | bytes[2] & 0xff;
		result = result << 8 | bytes[3] & 0xff;
		return result;
	}

	// 鍙栭殢鏈篵yte[]
	public static byte[] getRandomBytes(int i) {
		i = i <= 0 ? 4 : i;
		byte[] bytes = new byte[i];
		for (int j = 0; j < i; j++) {
			bytes[j] = (byte) ByteArr.random(1, 100);
		}
		return bytes;
	}

	public static byte[] longToBytes(long data) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data >> 8) & 0xff);
		bytes[2] = (byte) ((data >> 16) & 0xff);
		bytes[3] = (byte) ((data >> 24) & 0xff);
		bytes[4] = (byte) ((data >> 32) & 0xff);
		bytes[5] = (byte) ((data >> 40) & 0xff);
		bytes[6] = (byte) ((data >> 48) & 0xff);
		bytes[7] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	public static long bytesToLong(byte[] b) {
		if (b == null || b.length < 8) {
			return 0;
		}
		long s;
		long s0 = b[0] & 0xff;
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff;
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	}

	// shor杞崲鎴恇yte[]
	public static byte[] shortToBytes(short s) {
		byte[] targets = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (targets.length - 1 - i) * 8;
			targets[i] = (byte) ((s >>> offset) & 0xff);
		}
		return targets;
	}

	public static int bytesToShort(byte[] bytes) {
		if (bytes == null || bytes.length < 2) {
			return 0;
		}
		int high = bytes[0];
		int low = bytes[1];
		return (high << 8 & 0xFF00) | (low & 0xFF);
	}

	public static byte[] flip(byte[] bytes) {
		if (bytes == null || bytes.length <= 1) {
			return bytes;
		}
		byte[] arr = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			arr[i] = bytes[bytes.length - i - 1];
		}
		return arr;
	}

	public static byte[] getByteLeft(byte[] bytes, int index) {
		if (bytes == null || bytes.length <= index) {
			return bytes;
		}
		if (index <= 0) {
			return bytes;
		}
		byte[] b = new byte[index];
		System.arraycopy(bytes, 0, b, 0, index);
		return b;
	}

	public static byte[] getByteRight(byte[] bytes, int index) {
		if (bytes == null || bytes.length <= index) {
			return bytes;
		}
		if (index <= 0) {
			return null;
		}
		byte[] b = new byte[index];
		int j = index - 1;
		for (int i = 0; i < index; i++) {
			b[j] = bytes[bytes.length - i - 1];
			j--;
		}
		return b;
	}

	public static byte[] Md5_(byte[] src) {
		try {
			return MessageDigest.getInstance("md5").digest(src);
		} catch (Exception ignored) {

		}
		return null;
	}

	public static int getByteIndexOf(byte[] sources, byte[] src, int index) {
		if (sources == null || src == null || sources.length == 0
				|| src.length == 0) {
			return -1;
		}
		index = index < 0 ? 0 : index;
		if (index + src.length > sources.length) {
			return -1;
		}
		int i, j;
		for (i = index; i < sources.length; i++) {
			if (sources[i] == src[0]) {
				for (j = 1; j < src.length; j++) {
					if (sources[i + j] != src[j]) {
						break;
					}
				}
				if (j == src.length) {
					return i;
				}
			}
		}
		return -1;
	}

}
