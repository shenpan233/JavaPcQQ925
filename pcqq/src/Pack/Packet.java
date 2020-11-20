package Pack;

public class Packet {

	private byte[] buf;
	private int writerPos = 0;
	private int readerPos = 0;
	private int limit = 0;
	private int initPos = 0;

	public Packet() {
		this(1024);
	}

	public Packet(byte[] desc, int offset, int len) {
		buf = desc;
		readerPos = offset;
		writerPos = offset;
		initPos = offset;
		limit = offset + len;
	}

	public Packet(int capacity) {
		if (capacity <= 0)
			throw new IllegalArgumentException("capacity must be > 0");
		buf = new byte[capacity];
		readerPos = 0;
		writerPos = 0;
		initPos = 0;
		limit = capacity;
	}

	public Packet(byte[] desc) {
		this(desc, 0, desc.length);
	}

	public Packet(byte[] desc, boolean isCopy) {
		if (isCopy) {
			if (desc.length <= 0)
				throw new IllegalArgumentException("desc.length must be > 0");
			buf = new byte[desc.length];
			System.arraycopy(desc, 0, buf, 0, desc.length);
		} else {
			buf = desc;
		}
		readerPos = 0;
		writerPos = 0;
		limit = desc.length;
	}

	public void empty() {
		writerPos = 0;
	}

	public void setByte(int val) {
		checkWriteBound(1);
		buf[writerPos] = (byte) val;
		writerPos++;
	}

	public void setShort(int val) {
		checkWriteBound(2);
		buf[writerPos + 1] = (byte) (val >> 0);
		buf[writerPos + 0] = (byte) (val >> 8);
		writerPos += 2;
	}

	public void setToken(byte[] bytes) {
		if (bytes == null)
			return;
		checkWriteBound(2 + bytes.length);
		setShort(bytes.length);
		setBytes(bytes);
	}

	public void setInt(long val) {
		checkWriteBound(4);
		buf[writerPos + 3] = (byte) (val >> 0);
		buf[writerPos + 2] = (byte) (val >> 8);
		buf[writerPos + 1] = (byte) (val >> 16);
		buf[writerPos + 0] = (byte) (val >> 24);
		writerPos += 4;
	}

	public void setFixed32(long val) {
		checkWriteBound(4);
		buf[writerPos + 3] = (byte) (val >> 0);
		buf[writerPos + 2] = (byte) (val >> 8);
		buf[writerPos + 1] = (byte) (val >> 16);
		buf[writerPos + 0] = (byte) (val >> 24);
		writerPos += 4;
	}

	public void setFixed32LE(long val) {
		checkWriteBound(4);
		buf[writerPos + 0] = (byte) (val >> 0);
		buf[writerPos + 1] = (byte) (val >> 8);
		buf[writerPos + 2] = (byte) (val >> 16);
		buf[writerPos + 3] = (byte) (val >> 24);
		writerPos += 4;
	}

	public void setFixed64(long val) {
		checkWriteBound(8);
		buf[writerPos + 7] = (byte) (val >> 0);
		buf[writerPos + 6] = (byte) (val >> 8);
		buf[writerPos + 5] = (byte) (val >> 16);
		buf[writerPos + 4] = (byte) (val >> 24);
		buf[writerPos + 3] = (byte) (val >> 32);
		buf[writerPos + 2] = (byte) (val >> 40);
		buf[writerPos + 1] = (byte) (val >> 48);
		buf[writerPos + 0] = (byte) (val >> 56);
		writerPos += 8;
	}

	public void setFixedLE(long val) {
		checkWriteBound(8);
		buf[writerPos + 0] = (byte) (val >> 0);
		buf[writerPos + 1] = (byte) (val >> 8);
		buf[writerPos + 2] = (byte) (val >> 16);
		buf[writerPos + 3] = (byte) (val >> 24);
		buf[writerPos + 4] = (byte) (val >> 32);
		buf[writerPos + 5] = (byte) (val >> 40);
		buf[writerPos + 6] = (byte) (val >> 48);
		buf[writerPos + 7] = (byte) (val >> 56);
		writerPos += 8;
	}

	public void setVarint32(int tag, int val) {
		setVarint32(WireFormatMicro.makeTag(tag,
				WireFormatMicro.WIRETYPE_VARINT));
		setVarint32(val);
	}

	public void setVarint32(int val) {
		int ret = val;
		while ((ret & -128) != 0) {
			setByte((ret & 127) | 128);
			ret >>>= 7;
		}
		setByte(ret);
	}

	public void setVarint64(long val) {
		long ret = val;
		while ((ret & -128) != 0) {
			setByte((int) ((ret & 127) | 128));
			ret >>>= 7;
		}
		setByte((int) ret);
	}

	public void setPbBytes(int tag, byte[] val) {
		setVarint32(WireFormatMicro.makeTag(tag,
				WireFormatMicro.WIRETYPE_LENGTH_DELIMITED));
		setVarint32(val.length);
		if (val.length > 0) {
			setBytes(val);
		}
	}

	public void setLong(long val) {
		checkWriteBound(8);
		buf[writerPos + 7] = (byte) ((int) (val >> 0));
		buf[writerPos + 6] = (byte) ((int) (val >> 8));
		buf[writerPos + 5] = (byte) ((int) (val >> 16));
		buf[writerPos + 4] = (byte) ((int) (val >> 24));
		buf[writerPos + 3] = (byte) ((int) (val >> 32));
		buf[writerPos + 2] = (byte) ((int) (val >> 40));
		buf[writerPos + 1] = (byte) ((int) (val >> 48));
		buf[writerPos + 0] = (byte) ((int) (val >> 56));
		writerPos += 8;
	}

	public void setBytes(byte[] src) {
		if (src == null || src.length == 0)
			return;
		checkWriteBound(src.length);
		System.arraycopy(src, 0, buf, writerPos, src.length);
		writerPos += src.length;
	}

	private void checkWriteBound(int len) {
		if (writerPos + len > buf.length) {
			int newLen = (writerPos + len) * 2;
			byte[] buffer = new byte[newLen];
			System.arraycopy(buf, 0, buffer, 0, writerPos);
			buf = buffer;
		}
	}

	private void checkReadBound(int len) {
		if (len + readerPos > limit) {
			try {
				throw new IllegalAccessException(
						String.format(
								"initPos: %d, current readerPos: %d, limit: %d, require len: %d",
								initPos, readerPos, limit, len));
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			}
		}
	}

	public int getWriterPos() {
		return writerPos;
	}

	public void setWriterPos(int writerPos) {
		this.writerPos = writerPos;
	}

	public byte[] array() {
		if (writerPos == buf.length)
			return buf;
		byte[] tmp = new byte[writerPos];
		System.arraycopy(buf, 0, tmp, 0, writerPos);
		return tmp;
	}

	// read

	public byte peek() {
		checkReadBound(1);
		byte b = buf[readerPos];
		readerPos++;
		return b;
	}

	public byte read() {
		checkReadBound(1);
		byte b = buf[readerPos];
		readerPos++;
		return b;
	}

	public short readShort() {
		checkReadBound(2);
		byte b1 = buf[readerPos];
		byte b2 = buf[readerPos + 1];
		short s = (short) (((b1 << 8) & 65280) + ((b2) & 255));
		readerPos += 2;
		return s;
	}

	private static final int MAX_SHORT_VALUE = 65535;

	public int readUShort() {
		checkReadBound(2);
		int i = (((buf[readerPos] << 8) & 65280) + (buf[readerPos + 1] & 255));
		readerPos += 2;
		return i & MAX_SHORT_VALUE;
	}

	public int readInt() {
		checkReadBound(4);
		int i = ((((buf[readerPos] << 24) & -16777216) + ((buf[readerPos + 1] << 16) & 16711680)) + ((buf[readerPos + 2] << 8) & 65280))
				+ ((buf[readerPos + 3] << 0) & 255);

		readerPos += 4;
		return i;
	}

	public int readIntLE() {
		checkReadBound(4);
		int i = buf[readerPos] & 0xff | (buf[readerPos + 1] & 0xff) << 8
				| (buf[readerPos + 2] & 0xff) << 16
				| (buf[readerPos + 3] & 0xff) << 24;
		readerPos += 4;
		return i;
	}

	private static final long MAX_INT_VALUE = 4294967295L;

	public long readUInt() {
		checkReadBound(4);
		int i = ((((buf[readerPos] << 24) & -16777216) + ((buf[readerPos + 1] << 16) & 16711680)) + ((buf[readerPos + 2] << 8) & 65280))
				+ ((buf[readerPos + 3] << 0) & 255);
		readerPos += 4;
		return i & MAX_INT_VALUE;
	}

	public long readLong() {
		checkReadBound(8);
		long l = ((long) buf[readerPos] & 0xff) << 56
				| ((long) buf[readerPos + 1] & 0xff) << 48
				| ((long) buf[readerPos + 2] & 0xff) << 40
				| ((long) buf[readerPos + 3] & 0xff) << 32
				| ((long) buf[readerPos + 4] & 0xff) << 24
				| ((long) buf[readerPos + 5] & 0xff) << 16
				| ((long) buf[readerPos + 6] & 0xff) << 8
				| (long) buf[readerPos + 7] & 0xff;

		readerPos += 8;
		return l;
	}

	public long readLongLE() {
		checkReadBound(8);
		long l = (long) buf[readerPos] & 0xff
				| ((long) buf[readerPos + 1] & 0xff) << 8
				| ((long) buf[readerPos + 2] & 0xff) << 16
				| ((long) buf[readerPos + 3] & 0xff) << 24
				| ((long) buf[readerPos + 4] & 0xff) << 32
				| ((long) buf[readerPos + 5] & 0xff) << 40
				| ((long) buf[readerPos + 6] & 0xff) << 48
				| ((long) buf[readerPos + 7] & 0xff) << 56;

		readerPos += 8;
		return l;
	}

	public void readBytes(byte[] dst, int off, int size) {
		checkReadBound(size);
		System.arraycopy(buf, readerPos, dst, off, size);
		readerPos += size;
	}

	public byte[] readBytes(int size) {
		checkReadBound(size);
		byte[] bytes = new byte[size];
		System.arraycopy(buf, readerPos, bytes, 0, size);
		readerPos += size;
		return bytes;
	}

	// read protobuf type

	public long readRawVarint64() {
		long j = 0;
		for (int i = 0; i < 64; i += 7) {
			int b = read();
			j |= ((long) (b & Byte.MAX_VALUE)) << i;
			if ((b & 128) == 0) {
				return j;
			}
		}
		return 0;
	}

	public int readRawVarint32() {
		int j = 0;
		for (int i = 0; i < 64; i += 7) {
			int b = read();
			j |= (b & Byte.MAX_VALUE) << i;
			if ((b & 128) == 0) {
				return j;
			}
		}
		return 0;
	}

	public int peekVarintSize() {
		int markPos = getReaderPos();
		int size = 0;
		while (remaining() > 0) {
			int b = read();
			size++;
			if ((b & 128) == 0) {
				setReaderPos(markPos);
				return size;
			}
		}
		setReaderPos(markPos);
		return size;
	}

	public int getReaderPos() {
		return readerPos;
	}

	public void setReaderPos(int readerPos) {
		this.readerPos = readerPos;
	}

	public int remaining() {
		return limit - readerPos;
	}

	public int getLimit() {
		return limit;
	}

	public byte[] getBuf() {
		return buf;
	}

	public int getInitPos() {
		return initPos;
	}
}