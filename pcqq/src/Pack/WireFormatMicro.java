package Pack;

public final class WireFormatMicro {
	public static final int MESSAGE_SET_ITEM = 1;
	public static final int MESSAGE_SET_ITEM_END_TAG = makeTag(1, 4);
	public static final int MESSAGE_SET_ITEM_TAG = makeTag(1, 3);
	public static final int MESSAGE_SET_MESSAGE = 3;
	public static final int MESSAGE_SET_MESSAGE_TAG = makeTag(3, 2);
	public static final int MESSAGE_SET_TYPE_ID = 2;
	public static final int MESSAGE_SET_TYPE_ID_TAG = makeTag(2, 0);
	public static final int TAG_TYPE_BITS = 3;
	public static final int TAG_TYPE_MASK = 7;
	public static final int WIRETYPE_END_GROUP = 4;
	public static final int WIRETYPE_FIXED32 = 5;
	public static final int WIRETYPE_FIXED64 = 1;
	public static final int WIRETYPE_LENGTH_DELIMITED = 2;
	public static final int WIRETYPE_START_GROUP = 3;
	public static final int WIRETYPE_VARINT = 0;

	private WireFormatMicro() {
	}

	public static int getTagWireType(int i) {
		return i & 7;
	}

	public static int getTagFieldNumber(int i) {
		return i >>> 3;
	}

	public static int makeTag(int tag, int type) {
		return (tag << 3) | type;
	}
}