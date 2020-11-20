package tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * ���ܽ���QQ��Ϣ�Ĺ�����. QQ��Ϣ�ļ����㷨��һ��16�εĵ������̣������Ƿ����ģ�ÿһ�����ܵ�Ԫ��8�ֽڣ����Ҳ��8�ֽڣ���Կ��16�ֽ�
 * ������prePlain��ʾǰһ�����Ŀ飬plain��ʾ��ǰ���Ŀ飬crypt��ʾ��ǰ���Ŀ���ܵõ������Ŀ飬preCrypt��ʾǰһ�����Ŀ�
 * f��ʾ�����㷨��d��ʾ�����㷨 ��ô��plain�õ�crypt�Ĺ�����: crypt = f(plain &circ; preCrypt) &circ;
 * prePlain ���ԣ���crypt�õ�plain�Ĺ�����Ȼ�� plain = d(crypt &circ; prePlain) &circ;
 * preCrypt ���⣬�㷨�����������ƣ����������ǰ�����ĺ�ֱ����һ�����ֽ������Ա�֤���ĳ�����8�ֽڵı���
 * �����ֽ�����ԭʼ���ĳ����йأ����ķ�����:
 *
 * <pre>
 * <code>
 * 
 *      ------- ��Ϣ����㷨 -----------
 *      a = (���ĳ��� + 10) mod 8
 *      if(a ������ 0) a = 8 - a;
 *      b = ����� &amp; 0xF8 | a;              ����������ǰ�a��ֵ����������
 *      plain[0] = b;                   Ȼ���b��Ϊ���ĵĵ�0���ֽڣ�������0���ֽھͱ�����a����Ϣ�������Ϣ�ڽ���ʱ��Ҫ�����ҵ��������ĵ���ʼλ��
 *      plain[1 �� a+2] = ����� &amp; 0xFF;    �����������������ĵĵ�1����a+2���ֽ�
 *      plain[a+3 �� a+3+���ĳ���-1] = ����; ��a+3�ֽڿ�ʼ��������������
 *      plain[a+3+���ĳ���, ���] = 0;       ��������0����䵽�ܳ���Ϊ8������Ϊֹ������Ϊֹ�������ˣ���������õ���Ҫ���ܵ���������
 *      ------- ��Ϣ����㷨 ------------
 * 
 * </code>
 * </pre>
 *
 * @author luma
 * @author notXX
 */
public class TEA {
	// ָ��ǰ�����Ŀ�
	private byte[] plain;
	// ��ָ��ǰ��һ�����Ŀ�
	private byte[] prePlain;
	// ��������Ļ�������
	private byte[] out;
	// ��ǰ���ܵ�����λ�ú���һ�μ��ܵ����Ŀ�λ�ã��������8
	private int crypt, preCrypt;
	// ��ǰ����ļ��ܽ��ܿ��λ��
	private int pos;
	// �����
	private int padding;
	// ��Կ
	private byte[] key;
	// ���ڼ���ʱ����ʾ��ǰ�Ƿ��ǵ�һ��8�ֽڿ飬��Ϊ�����㷨�Ƿ�����
	// �����ʼ��8���ֽ�û�з������ã�������Ҫ�����������
	private boolean header = true;
	// �����ʾ��ǰ���ܿ�ʼ��λ�ã�֮����Ҫ��ôһ��������Ϊ�˱��⵱���ܵ����ʱ
	// �����Ѿ�û�����ݣ���ʱ��ͻ��������������������ж����������ó���
	private int contextStart;
	// ���������
	private static Random random = new Random();
	// �ֽ������
	private ByteArrayOutputStream baos;

	public String Bin2Hex(byte[] bin) {
		return ByteArr.bytesToHexStr(bin);
	}

	public byte[] HextoBin(String hex) {
		return ByteArr.hexStrToBytes(hex.replace(" ", ""));
	}

	public static void main(String[] args) throws IOException {
		TEA teaUtil = new TEA();
		byte[] KEY = teaUtil
				.HextoBin("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

		byte[] content = teaUtil.HextoBin("12");

		byte[] enByte = teaUtil.encrypt(content, KEY); // ���ܺ���ֽ�
		byte[] deByte = teaUtil.decrypt(enByte, KEY); // ���ܺ���ֽ�
		System.out.println(ByteArr.bytesToHexStr(enByte));
		System.out.println(ByteArr.bytesToHexStr(deByte));
	}

	/**
	 * ���캯��
	 */
	public TEA() {
		baos = new ByteArrayOutputStream(8);
	}

	/**
	 * ���ֽ������offset��ʼ��len���ֽ�ת����һ��unsigned int�� ��Ϊjava����û��unsigned������unsigned
	 * intʹ��long��ʾ�ģ� ���len����8������Ϊlen����8�����lenС��8�����λ��0 <br>
	 * (edited by notxx) �ı����㷨, ������΢��һ��. ���ҵĻ����ϲ���10000��, ԭʼ�㷨����18s, ����㷨����12s.
	 *
	 * @param in
	 *            �ֽ�����.
	 * @param offset
	 *            �����￪ʼת��.
	 * @param len
	 *            ת������, ���len����8����Ժ����
	 * @return
	 */
	private static long getUnsignedInt(byte[] in, int offset, int len) {
		long ret = 0;
		int end = 0;
		if (len > 8)
			end = offset + 8;
		else
			end = offset + len;
		for (int i = offset; i < end; i++) {
			ret <<= 8;
			ret |= in[i] & 0xff;
		}
		return (ret & 0xffffffffl) | (ret >>> 32);
	}

	/**
	 * ����
	 * 
	 * @param in
	 *            ����
	 * @param offset
	 *            ���Ŀ�ʼ��λ��
	 * @param len
	 *            ���ĳ���
	 * @param k
	 *            ��Կ
	 * @return ����
	 */
	public byte[] decrypt(byte[] in, int offset, int len, byte[] k) {
		// �����Կ
		if (k == null)
			return null;

		crypt = preCrypt = 0;
		this.key = k;
		int count;
		byte[] m = new byte[offset + 8];

		// ��ΪQQ��Ϣ����֮��������16�ֽڣ����ҿ϶���8�ı������������������
		if ((len % 8 != 0) || (len < 16))
			return null;
		// �õ���Ϣ��ͷ�����ؼ��ǵõ��������Ŀ�ʼ��λ�ã������Ϣ���ڵ�һ���ֽ����棬�������ý��ܵõ��ĵ�һ���ֽ���7����
		prePlain = decipher(in, offset);
		pos = prePlain[0] & 0x7;
		// �õ��������ĵĳ���
		count = len - pos - 10;
		// ������ĳ���С��0���ǿ϶��ǳ����ˣ����紫�����֮��ģ�����
		if (count < 0)
			return null;

		// �������ʱ��preCrypt���ͼ���ʱ��һ��8�ֽڿ�û��prePlainһ��������ʱ
		// ��һ��8�ֽڿ�Ҳû��preCrypt���������ｨһ��ȫ0��
		for (int i = offset; i < m.length; i++)
			m[i] = 0;
		// ͨ��������Ĵ��룬����Ӧ����û�������ˣ����Ƿ������������
		out = new byte[count];
		// ����preCrypt��λ�õ���0��ע��Ŀǰ��preCryptλ����ָ��m�ģ���Ϊjavaû��ָ�룬���������ں���Ҫ���Ƶ�ǰ����buf������
		preCrypt = 0;
		// ��ǰ������λ�ã�Ϊʲô��8����0�أ�ע��ǰ�������Ѿ�������ͷ����Ϣ�ˣ����ڵ�Ȼ��8��
		crypt = 8;
		// ��Ȼ���Ҳ��8
		contextStart = 8;
		// ��1���ͼ����㷨�Ƕ�Ӧ��
		pos++;

		// ��ʼ����ͷ����������������������8�ֽڣ��������һ��
		// ��Ϊ�ǽ�����һ�飬����������һ����� m = in����һ�鵱Ȼ��preCrypt�ˣ����ǲ�����m��
		// �����������8����˵����ʲô��˵����ͷ8���ֽڵ������ǰ�����������Ϣ�ģ���Ȼ����Ҫ��m������Ū����
		// ���ԣ�����Ȼ������8�Ļ���˵����ͷ8���ֽڵ����ĳ���һ��������Ϣ����֮�⣬�����������õ����
		padding = 1;
		while (padding <= 2) {
			if (pos < 8) {
				pos++;
				padding++;
			}
			if (pos == 8) {
				m = in;
				if (!decrypt8Bytes(in, offset, len))
					return null;
			}
		}

		// �����ǽ��ܵ���Ҫ�׶Σ����ʱ��ͷ������䶼�Ѿ������ˣ���ʼ����
		// ע���������һ��whileû����8�������һ��if�����õľ���ԭʼ��m���������m����in��
		int i = 0;
		while (count != 0) {
			if (pos < 8) {
				out[i] = (byte) (m[offset + preCrypt + pos] ^ prePlain[pos]);
				i++;
				count--;
				pos++;
			}
			if (pos == 8) {
				m = in;
				preCrypt = crypt - 8;
				if (!decrypt8Bytes(in, offset, len))
					return null;
			}
		}

		// ���Ľ��ܲ��֣�����һ��while�Ѿ������Ķ�������ˣ���ʣ��β��������ˣ�Ӧ��ȫ��0
		// ���������м���Ƿ������֮���ǲ���0��������ǵĻ��ǿ϶������ˣ�����null
		for (padding = 1; padding < 8; padding++) {
			if (pos < 8) {
				if ((m[offset + preCrypt + pos] ^ prePlain[pos]) != 0)
					return null;
				pos++;
			}
			if (pos == 8) {
				m = in;
				preCrypt = crypt;
				if (!decrypt8Bytes(in, offset, len))
					return null;
			}
		}
		if (out == in) {
			return null;
		}
		return out;
	}

	/**
	 * @param in
	 *            ��Ҫ�����ܵ�����
	 * @paraminLen ���ĳ���
	 * @param k
	 *            ��Կ
	 * @return Message �ѽ��ܵ���Ϣ
	 */
	public byte[] decrypt(byte[] in, byte[] k) {
		return decrypt(in, 0, in.length, k);
	}

	/**
	 * ����
	 * 
	 * @param in
	 *            �����ֽ�����
	 * @param offset
	 *            ��ʼ���ܵ�ƫ��
	 * @param len
	 *            ���ܳ���
	 * @param k
	 *            ��Կ
	 * @return �����ֽ�����
	 */
	public byte[] encrypt(byte[] in, int offset, int len, byte[] k) {
		// �����Կ
		if (k == null)
			return in;

		plain = new byte[8];
		prePlain = new byte[8];
		pos = 1;
		padding = 0;
		crypt = preCrypt = 0;
		this.key = k;
		header = true;

		// ����ͷ������ֽ���
		pos = (len + 0x0A) % 8;
		if (pos != 0)
			pos = 8 - pos;
		// ������������ĳ���
		out = new byte[len + pos + 10];
		// ����Ĳ�����pos�浽��plain�ĵ�һ���ֽ�����
		// 0xF8������λ�ǿյģ���������pos����Ϊpos��0��7��ֵ����ʾ�ı���ʼ���ֽ�λ��
		plain[0] = (byte) ((rand() & 0xF8) | pos);

		// ��������������������plain[1]��plain[pos]֮�������
		for (int i = 1; i <= pos; i++)
			plain[i] = (byte) (rand() & 0xFF);
		pos++;
		// �������prePlain����һ��8�ֽڿ鵱Ȼû��prePlain������������һ��ȫ0�ĸ���һ��8�ֽڿ�
		for (int i = 0; i < 8; i++)
			prePlain[i] = 0x0;

		// �������2���ֽڵ������������������������8�ֽھͼ���֮
		padding = 1;
		while (padding <= 2) {
			if (pos < 8) {
				plain[pos++] = (byte) (rand() & 0xFF);
				padding++;
			}
			if (pos == 8)
				encrypt8Bytes();
		}

		// ͷ��������ˣ����￪ʼ�������������ˣ�Ҳ������8�ֽھͼ��ܣ�һֱ�����Ķ���
		int i = offset;
		while (len > 0) {
			if (pos < 8) {
				plain[pos++] = in[i++];
				len--;
			}
			if (pos == 8)
				encrypt8Bytes();
		}

		// �������0���Ա�֤��8�ֽڵı���
		padding = 1;
		while (padding <= 7) {
			if (pos < 8) {
				plain[pos++] = 0x0;
				padding++;
			}
			if (pos == 8)
				encrypt8Bytes();
		}

		return out;
	}

	/**
	 * @param in
	 *            ��Ҫ���ܵ�����
	 * @paraminLen ���ĳ���
	 * @param k
	 *            ��Կ
	 * @return Message ����
	 */
	public byte[] encrypt(byte[] bin, byte[] key) {
		return encrypt(bin, 0, bin.length, key);
	}

	/**
	 * ����һ��8�ֽڿ�
	 *
	 * @param in
	 *            �����ֽ�����
	 * @return �����ֽ�����
	 */
	private byte[] encipher(byte[] in) {
		// ����������16��
		int loop = 0x10;
		// �õ����ĺ���Կ�ĸ������֣�ע��javaû���޷������ͣ�����Ϊ�˱�ʾһ���޷��ŵ�����
		// ��������long�����long��ǰ32λ��ȫ0�ģ�����ͨ�����ַ�ʽģ���޷��������������õ���longҲ����һ����
		// ����Ϊ�˱�֤ǰ32λΪ0����Ҫ��0xFFFFFFFF��һ��λ��
		long y = getUnsignedInt(in, 0, 4);
		long z = getUnsignedInt(in, 4, 4);
		long a = getUnsignedInt(key, 0, 4);
		long b = getUnsignedInt(key, 4, 4);
		long c = getUnsignedInt(key, 8, 4);
		long d = getUnsignedInt(key, 12, 4);
		// �����㷨��һЩ���Ʊ�����Ϊʲôdelta��0x9E3779B9�أ�
		// �������TEA�㷨��delta��ʵ���Ǿ���(sqr(5) - 1) * 2^31 (����5����1���ٳ�2��31�η�)
		long sum = 0;
		long delta = 0x9E3779B9;
		delta &= 0xFFFFFFFFL;

		// ��ʼ�����ˣ����߰���ģ���Ҳ��������������DES֮��Ĳ�࣬��������������ȥ
		while (loop-- > 0) {
			sum += delta;
			sum &= 0xFFFFFFFFL;
			y += ((z << 4) + a) ^ (z + sum) ^ ((z >>> 5) + b);
			y &= 0xFFFFFFFFL;
			z += ((y << 4) + c) ^ (y + sum) ^ ((y >>> 5) + d);
			z &= 0xFFFFFFFFL;
		}

		// �������������ģ���Ϊ���õ�long��������Ҫǿ��ת��һ�±��int
		baos.reset();
		writeInt((int) y);
		writeInt((int) z);
		return baos.toByteArray();
	}

	/**
	 * ���ܴ�offset��ʼ��8�ֽ�����
	 *
	 * @param in
	 *            �����ֽ�����
	 * @param offset
	 *            ���Ŀ�ʼλ��
	 * @return ����
	 */
	private byte[] decipher(byte[] in, int offset) {
		// ����������16��
		int loop = 0x10;
		// �õ����ĺ���Կ�ĸ������֣�ע��javaû���޷������ͣ�����Ϊ�˱�ʾһ���޷��ŵ�����
		// ��������long�����long��ǰ32λ��ȫ0�ģ�����ͨ�����ַ�ʽģ���޷��������������õ���longҲ����һ����
		// ����Ϊ�˱�֤ǰ32λΪ0����Ҫ��0xFFFFFFFF��һ��λ��
		long y = getUnsignedInt(in, offset, 4);
		long z = getUnsignedInt(in, offset + 4, 4);
		long a = getUnsignedInt(key, 0, 4);
		long b = getUnsignedInt(key, 4, 4);
		long c = getUnsignedInt(key, 8, 4);
		long d = getUnsignedInt(key, 12, 4);
		// �㷨��һЩ���Ʊ�����sum������Ҳ�����ˣ����sum�͵��������й�ϵ
		// ��Ϊdelta����ô�࣬����sum�������ô��Ļ���������ʱ�����������16�Σ����
		// �õ�0�����������Ϊ�˵õ��ͼ���ʱ�෴˳��Ŀ��Ʊ������������ܽ���ѽ����
		long sum = 0xE3779B90;
		sum &= 0xFFFFFFFFL;
		long delta = 0x9E3779B9;
		delta &= 0xFFFFFFFFL;

		// ������ʼ�ˣ� @_@
		while (loop-- > 0) {
			z -= ((y << 4) + c) ^ (y + sum) ^ ((y >>> 5) + d);
			z &= 0xFFFFFFFFL;
			y -= ((z << 4) + a) ^ (z + sum) ^ ((z >>> 5) + b);
			y &= 0xFFFFFFFFL;
			sum -= delta;
			sum &= 0xFFFFFFFFL;
		}

		baos.reset();
		writeInt((int) y);
		writeInt((int) z);
		return baos.toByteArray();
	}

	/**
	 * д��һ�����͵�����������ֽ�����
	 *
	 * @param t
	 */
	private void writeInt(int t) {
		baos.write(t >>> 24);
		baos.write(t >>> 16);
		baos.write(t >>> 8);
		baos.write(t);
	}

	/**
	 * ����
	 *
	 * @param in
	 *            ����
	 * @return ����
	 */
	private byte[] decipher(byte[] in) {
		return decipher(in, 0);
	}

	/**
	 * ����8�ֽ�
	 */
	private void encrypt8Bytes() {
		// �ⲿ�������������˵�� plain ^
		// preCrypt��ע�������ж����ǲ��ǵ�һ��8�ֽڿ飬����ǵĻ����Ǹ�prePlain�͵���preCrypt��
		for (pos = 0; pos < 8; pos++) {
			if (header)
				plain[pos] ^= prePlain[pos];
			else
				plain[pos] ^= out[preCrypt + pos];
		}
		// ������������˵�� f(plain ^ preCrypt)
		byte[] crypted = encipher(plain);
		// ���ûʲô�����ǿ���һ�£�java����c��������ֻ����ô�ɣ�c�Ͳ�����һ����
		System.arraycopy(crypted, 0, out, crypt, 8);

		// �������� f(plain ^ preCrypt) ^ prePlain��ok�����濽��һ�¾�����
		for (pos = 0; pos < 8; pos++)
			out[crypt + pos] ^= prePlain[pos];
		System.arraycopy(plain, 0, prePlain, 0, 8);

		// ����˼��ܣ������ǵ���crypt��preCrypt�ȵȶ�����ʱ����
		preCrypt = crypt;
		crypt += 8;
		pos = 0;
		header = false;
	}

	/**
	 * ����8���ֽ�
	 *
	 * @param in
	 *            �����ֽ�����
	 * @param offset
	 *            �Ӻδ���ʼ����
	 * @param len
	 *            ���ĵĳ���
	 * @return true��ʾ���ܳɹ�
	 */
	private boolean decrypt8Bytes(byte[] in, int offset, int len) {
		// �����һ�������жϺ��滹��û�����ݣ�û�оͷ��أ�����У���ִ�� crypt ^ prePlain
		for (pos = 0; pos < 8; pos++) {
			if (contextStart + pos >= len)
				return true;
			prePlain[pos] ^= in[offset + crypt + pos];
		}

		// �ã�����ִ�е��� d(crypt ^ prePlain)
		prePlain = decipher(prePlain);
		if (prePlain == null)
			return false;

		// ������ɣ����һ������û����
		// �������һ���ŵ�decrypt����ȥ���ˣ���Ϊ���ܵĲ����е㲻̫һ��
		// ������Щ������ֵ��
		contextStart += 8;
		crypt += 8;
		pos = 0;
		return true;
	}

	/**
	 * ���Ǹ�������Ӳ��������������ͷ���ģ����Ϊ�˵��ԣ�������һ���̶�ֵ ������ӿ���ʹ��ͬ������ÿ�μ��ܳ��������Ķ���һ��
	 *
	 * @return �������
	 */
	private int rand() {
		return random.nextInt();
	}
}
