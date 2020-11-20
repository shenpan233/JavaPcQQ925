package Pack;

public class StringUtils {

	public static void main(String[] args) {
		String str = "¹þ1°¡¹þ12¹þ¹þ1¹þÅ£1±Æ324";
		System.out.println(subString(str, "1", "2", false));// °¡¹þ1
		System.out.println(subString(str, "1", "2", true));// ±Æ3

		System.out.println(leftString(str, "1", false));// ¹þ
		System.out.println(leftString(str, "1", true));// ¹þ1°¡¹þ12¹þ¹þ1¹þÅ£

		System.out.println(rightString(str, "1", false));// ±Æ324
		System.out.println(rightString(str, "1", true));// °¡¹þ12¹þ¹þ1¹þÅ£1±Æ324
		/*
		 * °¡¹þ1 ±Æ3 ¹þ ¹þ1°¡¹þ12¹þ¹þ1¹þÅ£ ±Æ324 °¡¹þ12¹þ¹þ1¹þÅ£1±Æ324
		 */
	}

	public static String subString(String src, String left, String right) {
		return subString(src, left, right, false);
	}

	public static String subString(String src, String left, String right,
			boolean startForRight) {
		if (startForRight) {
			int leftIndex = src.lastIndexOf(left);
			if (leftIndex == -1)
				return "";
			int rightIndex = src.indexOf(right, leftIndex + left.length());
			if (rightIndex == -1)
				return "";
			return src.substring(leftIndex + left.length(), rightIndex);
		} else {
			int leftIndex = src.indexOf(left);
			if (leftIndex == -1)
				return "";
			int rightIndex = src.indexOf(right, leftIndex + left.length());
			if (rightIndex == -1)
				return "";
			return src.substring(leftIndex + left.length(), rightIndex);
		}
	}

	public static String leftString(String src, String left) {
		return leftString(src, left, false);
	}

	public static String leftString(String src, String left,
			boolean startForRight) {
		if (startForRight) {
			int lastIndex = src.lastIndexOf(left);
			if (lastIndex == -1)
				return "";
			return src.substring(0, lastIndex);
		} else {
			int index = src.indexOf(left);
			if (index == -1)
				return "";
			return src.substring(0, index);
		}
	}

	public static String rightString(String src, String right) {
		return rightString(src, right, false);
	}

	public static String rightString(String src, String right,
			boolean startForLeft) {
		if (startForLeft) {
			int index = src.indexOf(right);
			if (index == -1)
				return "";
			return src.substring(index + right.length(), src.length());
		} else {
			int lastIndex = src.lastIndexOf(right);
			if (lastIndex == -1)
				return "";
			return src.substring(lastIndex + right.length(), src.length());
		}
	}

}