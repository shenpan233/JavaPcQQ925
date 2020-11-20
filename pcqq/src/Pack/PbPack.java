package Pack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class PbPack {

	private HashMap<String, Node<?>> map = new HashMap<>();
	private Packet pack;
	private Node<?> root;
	private int mLen;
	private byte[] buffer;

	public PbPack() {
		root = new Node<>();
		root.children = new LinkedList<>();
		root.parent = null;
		mLen = 0;
	}

	public void Empty() {
		root.children.clear();
		mLen = 0;
		pack.empty();
		map.clear();
		buffer = null;
	}

	public void setVarint(String path, long val) {
		int valLen = computeRawVarint64Size(val);
		makePath(path, WireFormatMicro.WIRETYPE_VARINT, valLen, val);
	}

	public void setInt32(String path, int val) {
		makePath(path, WireFormatMicro.WIRETYPE_FIXED32, 4, val);
	}

	public void setInt64(String path, long val) {
		makePath(path, WireFormatMicro.WIRETYPE_FIXED64, 8, val);
	}

	public void setBytes(String path, byte[] val) {
		int valLen = val.length;
		makePath(path, WireFormatMicro.WIRETYPE_LENGTH_DELIMITED, valLen, val);
	}

	public void setString(String path, String val) {
		byte[] buf = val.getBytes();
		int valLen = buf.length;
		makePath(path, WireFormatMicro.WIRETYPE_LENGTH_DELIMITED, valLen, buf);
	}

	public byte[] array() {
		if (buffer != null && buffer.length > 0)
			return buffer;
		// System.out.println("mLen = " + mLen);
		pack = new Packet(mLen);
		writeToPacket(root);
		buffer = pack.array();
		return buffer;
	}

	private void writeToPacket(Node<?> parent) {
		if (parent == null)
			return;

		if (parent != root) {
			pack.setVarint32(parent.key);
		}
		if (parent.children == null) {
			// 叶子节点
			if (parent.valType == WireFormatMicro.WIRETYPE_LENGTH_DELIMITED) {
				pack.setVarint32(parent.valLen);
				pack.setBytes((byte[]) parent.val);
			} else if (parent.valType == WireFormatMicro.WIRETYPE_VARINT) {
				pack.setVarint64((Long) parent.val);
			} else if (parent.valType == WireFormatMicro.WIRETYPE_FIXED64) {
				pack.setLong((Long) parent.val);
			} else if (parent.valType == WireFormatMicro.WIRETYPE_FIXED32) {
				pack.setInt((Integer) parent.val);
			}
		} else {
			if (parent != root) {
				pack.setVarint32(parent.valLen);
			}
			for (int i = 0; i < parent.children.size(); i++) {
				writeToPacket(parent.children.get(i));
			}
		}

	}

	/**
	 * 根据路径构造树
	 *
	 * @param path
	 * @param valType
	 * @param valLen
	 * @param val
	 */
	private void makePath(String path, int valType, int valLen, byte[] val) {
		String lastPath = "";
		int key = 0;
		int keyLen = 0;
		int lenLen = 0;
		if (path.contains(".")) {
			key = GetKeyForPath(path, valType);
		} else {
			// 单层
			lastPath = GetPathNoIndex(path);
			key = WireFormatMicro.makeTag(Integer.parseInt(lastPath), valType);
		}
		keyLen = computeRawVarint32Size(key);

		if (valType == WireFormatMicro.WIRETYPE_LENGTH_DELIMITED) {
			lenLen = computeRawVarint32Size(valLen);
		} else {
			lenLen = 0;
		}

		ArrayList<String> fathers = GetFathers(path);
		InitFathersNode(fathers);
		Node<byte[]> thisNode = new Node<>();
		thisNode.parent = GetFatherNode(path);
		thisNode.valType = valType;
		thisNode.key = key;
		thisNode.val = val;
		thisNode.valLen = valLen;
		thisNode.lenLen = lenLen;
		thisNode.children = null;
		thisNode.parent.children.add(thisNode);
		mLen += keyLen + lenLen + valLen;
		UpdateFathersNode(fathers, valType, keyLen, valLen, lenLen);
	}

	/**
	 * 根据路径构造树
	 *
	 * @param path
	 * @param valType
	 * @param valLen
	 * @param val
	 */
	private void makePath(String path, int valType, int valLen, long val) {
		String lastPath = "";
		int key = 0;
		int keyLen = 0;
		int lenLen = 0;
		if (path.contains(".")) {
			key = GetKeyForPath(path, valType);
		} else {
			// 单层
			lastPath = GetPathNoIndex(path);
			key = WireFormatMicro.makeTag(Integer.parseInt(lastPath), valType);
		}
		keyLen = computeRawVarint32Size(key);

		if (valType == WireFormatMicro.WIRETYPE_LENGTH_DELIMITED) {
			lenLen = computeRawVarint32Size(valLen);
		} else {
			lenLen = 0;
		}

		ArrayList<String> fathers = GetFathers(path);
		InitFathersNode(fathers);
		Node<Long> thisNode = new Node<>();
		thisNode.parent = GetFatherNode(path);
		thisNode.valType = valType;
		thisNode.key = key;
		thisNode.val = val;
		thisNode.valLen = valLen;
		thisNode.lenLen = lenLen;
		thisNode.children = null;
		thisNode.parent.children.add(thisNode);
		mLen += keyLen + lenLen + valLen;
		UpdateFathersNode(fathers, valType, keyLen, valLen, lenLen);
	}

	/**
	 * 根据路径构造树
	 *
	 * @param path
	 * @param valType
	 * @param valLen
	 * @param val
	 */
	private void makePath(String path, int valType, int valLen, int val) {
		String lastPath = "";
		int key = 0;
		int keyLen = 0;
		int lenLen = 0;
		if (path.contains(".")) {
			key = GetKeyForPath(path, valType);
		} else {
			// 单层
			lastPath = GetPathNoIndex(path);
			key = WireFormatMicro.makeTag(Integer.parseInt(lastPath), valType);
		}
		keyLen = computeRawVarint32Size(key);

		if (valType == WireFormatMicro.WIRETYPE_LENGTH_DELIMITED) {
			lenLen = computeRawVarint32Size(valLen);
		} else {
			lenLen = 0;
		}

		ArrayList<String> fathers = GetFathers(path);
		InitFathersNode(fathers);
		Node<Integer> thisNode = new Node<>();
		thisNode.parent = GetFatherNode(path);
		thisNode.valType = valType;
		thisNode.key = key;
		thisNode.val = val;
		thisNode.valLen = valLen;
		thisNode.lenLen = lenLen;
		thisNode.children = null;
		thisNode.parent.children.add(thisNode);
		mLen += keyLen + lenLen + valLen;
		UpdateFathersNode(fathers, valType, keyLen, valLen, lenLen);
	}

	private void UpdateFathersNode(ArrayList<String> list, int valType,
			int keyLen, int valLen, int lenLen) {
		int allInc = keyLen + valLen + lenLen;
		int inc = allInc;

		for (int i = 0; i < list.size(); i++) {
			String thisPath = list.get(i);
			Node thisNode = map.get(thisPath);
			if (thisNode == null)
				throw new RuntimeException("找不到当前路径的节点[" + thisPath + "]");
			int oldValLen = thisNode.valLen;
			int oldLenLen = thisNode.lenLen;
			int thisKey = thisNode.key;

			int newValLen = 0;
			int newLenLen = 0;
			if (oldValLen == 0) {
				// 首次初始化这个节点
				newValLen = allInc;
				allInc += computeRawVarint32Size(thisKey);
				allInc += computeRawVarint32Size(newValLen);
				newLenLen = computeRawVarint32Size(newValLen);
				mLen += newLenLen;
			} else {
				newValLen += allInc + oldValLen;
				newLenLen = computeRawVarint32Size(newValLen);
				mLen = mLen + newLenLen - oldLenLen;
				if (newLenLen > oldLenLen) {
					allInc += newLenLen - oldLenLen;
				}
			}
			thisNode.valLen = newValLen;
			thisNode.lenLen = newLenLen;
		}

	}

	/**
	 * 初始化路径上的所有父节点
	 *
	 * @param list
	 *            有顺序的父节点们
	 */
	private void InitFathersNode(ArrayList<String> list) {
		for (int i = list.size() - 1; i >= 0; i--) {
			String thisPath = list.get(i);
			if (map.containsKey(thisPath)) {
				// 已存在 忽略
			} else {
				// 不存在
				Node<?> fatherNode = GetFatherNode(thisPath);
				if (fatherNode == null)
					throw new RuntimeException("找不到[" + thisPath + "]的父节点");
				Node<?> n = new Node<>();
				n.children = new LinkedList<>();// 儿子们做房子

				// 认亲开始
				n.parent = fatherNode;
				fatherNode.children.add(n);
				// 认亲结束
				n.valType = WireFormatMicro.WIRETYPE_LENGTH_DELIMITED;// 爹类型
				n.key = GetKeyForPath(thisPath,
						WireFormatMicro.WIRETYPE_LENGTH_DELIMITED);
				n.lenLen = 0;
				mLen += computeRawVarint32Size(n.key);
				map.put(thisPath, n);
			}
		}
	}

	/**
	 * 删除路径尾部的索引
	 *
	 * @param path
	 *            路径
	 * @return
	 */
	private String GetPathNoIndex(String path) {
		int lastIndex = path.lastIndexOf(".");
		if (lastIndex == -1) {// 顶层节点
			int index = path.indexOf("[");
			if (index == -1) {// 无索引
				return path;
			} else {
				// 顶层节点 无需倒找
				return path.substring(0, index);
			}
		} else {
			int index = path.indexOf("[", lastIndex);
			if (index == -1) {// 无索引
				return path;
			} else {
				// 顶层节点 无需倒找
				return path.substring(0, index);
			}
		}
	}

	/**
	 * 通过路径和类型取节点key
	 *
	 * @param path
	 *            路径
	 * @param type
	 *            类型
	 * @return 节点key
	 */
	private int GetKeyForPath(String path, int type) {
		int index = path.indexOf(".");
		int Tag = 0;
		if (index == -1) {// 顶层节点
			Tag = Integer.parseInt(GetPathNoIndex(path));// 顶层节点 直接转int
		} else {// 非顶层节点
			String lastPath = GetPathNoIndex(path);
			Tag = Integer.parseInt(StringUtils.rightString(lastPath, "."));
		}
		return WireFormatMicro.makeTag(Tag, type);
	}

	/**
	 * 通过key 返回key的索引
	 *
	 * @param key
	 *            通过key获取索引
	 * @return
	 */
	private int GetIndex(String key) {
		int index = key.indexOf("[");
		if (index == -1)
			return 1;
		return Integer.parseInt(StringUtils.subString(key, "[", "]"));
	}

	/**
	 * 获取此节点的所有父级路径
	 *
	 * @param path
	 *            节点全路径
	 * @return 此节点的所有父级路径
	 */
	private ArrayList<String> GetFathers(String path) {
		ArrayList<String> list = new ArrayList<>();

		String lastPath = path;
		String newPath = "";
		do {
			newPath = StringUtils.leftString(lastPath, ".", true);
			if ("".equals(newPath))
				break;
			list.add(newPath);
			lastPath = newPath;
		} while (true);
		return list;
	}

	/**
	 * 通过当前节点路径取父亲节点
	 *
	 * @param path
	 *            节点全路径
	 * @return 父节点
	 */
	private Node<?> GetFatherNode(String path) {
		int index = path.indexOf(".");
		if (index == -1) {
			return root;
		} else {
			String fatherPath = StringUtils.leftString(path, ".", true);
			return map.get(fatherPath);
		}
	}

	private int computeRawVarint32Size(int i) {
		return (i & -128) == 0 ? 1 : (i & -16384) == 0 ? 2
				: (-2097152 & i) == 0 ? 3 : (-268435456 & i) == 0 ? 4 : 5;
	}

	private int computeRawVarint64Size(long j) {
		return (-128 & j) == 0 ? 1
				: (-16384 & j) == 0 ? 2
						: (-2097152 & j) == 0 ? 3
								: (-268435456 & j) == 0 ? 4
										: (-34359738368L & j) == 0 ? 5
												: (-4398046511104L & j) == 0 ? 6
														: (-562949953421312L & j) == 0 ? 7
																: (-72057594037927936L & j) == 0 ? 8
																		: (Long.MIN_VALUE & j) == 0 ? 9
																				: 10;
	}

	protected static class Node<T> {
		public Node<?> parent;
		public LinkedList<Node<?>> children;
		public int key;
		public T val;
		public int valType;
		public int valLen;
		public int lenLen;
	}

}
