package com.kevin.iestudio.codec.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * byte:byte数 bin:二进制 int:十进制 hex:16进制 Longer:变长 Length:定长 str:字符串类型
 * 
 * @author 冯贺亮
 * 
 */
public class NTransUtil {
	
	/**
	 * 二进制byte转二进制字符串
	 * @param barray
	 * @return
	 */
	public static final String binBytesTobin(byte[] barray) {
		if (barray == null) {
			return null;
		}
		StringBuffer str = new StringBuffer();
		for (byte b : barray) {
			str.append(StrHelper.fillString(Integer.toBinaryString(b), 8, "0"));
		}
		return str.toString();
	}

	/**
	 * 字符串转2进制字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String stringToLongerBinStr(String str) {
		char[] strChar = str.toCharArray();
		String result = "";
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < strChar.length; i++) {
			list.add(StrHelper.fillString(Integer.toBinaryString(strChar[i]), 8, "0"));
		}
		if (new Integer(list.get(list.size() - 1)) == 00000000) {
			list.remove(list.size() - 1);
		}
		for (String s : list) {
			result += s;
		}
		return result;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 16进制转换为定长的16进制byte
	 * 
	 * @param hexString
	 * @return
	 */
	public static byte[] hexToLengthHexByte(String hexString) {
		if (hexString == null || "".equalsIgnoreCase(hexString)) {
			return null;
		}
		StringBuffer str=new StringBuffer();
		if (hexString.length() % 2 != 0) {
			str.append("0");
		}
		str.append(hexString);
		hexString = str.toString().toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 十进制转换为定长的十六进制byte
	 * 
	 * @param args
	 * @param hexLength
	 * @return
	 */
	public static byte[] intToLengthHexByte(int args, int hexLength) {
		String s = Integer.toHexString(args);
//		if (s.length() % 2 != 0) {
//			s = "0" + s;
//		}
		byte[] re = hexToLengthHexByte(s);
		if (s.length() >= hexLength * 2) {
			return re;
		} else {
			byte[] newb = new byte[hexLength];
			System.arraycopy(re, 0, newb, hexLength - re.length, re.length);
			return newb;
		}
	}
	
	public static byte[] intToLengthHexByte(Long args, int hexLength) {
		String s = Long.toHexString(args);
//		if (s.length() % 2 != 0) {
//			s = "0" + s;
//		}
		byte[] re = hexToLengthHexByte(s);
		if (s.length() >= hexLength * 2) {
			return re;
		} else {
			byte[] newb = new byte[hexLength];
			System.arraycopy(re, 0, newb, hexLength - re.length, re.length);
			return newb;
		}
	}

	/**
	 * 二进制转换为定长的十六进制的byte
	 * 
	 * @param bins
	 * @return
	 */
	public static byte[] binStrToLengthHexByte(String bins) {
		if (bins.length() % 8 != 0) {
			throw new IllegalArgumentException("args is not a full bin");
		}
		int count = bins.length() / 8;
		String subargs = "";
		byte[] revalue = new byte[count];
		for (int i = 0; i < count; i++) {
			if (i == count - 1) {
				subargs = bins.substring(i * 8);
			} else {
				subargs = bins.substring(i * 8, (i + 1) * 8);
			}
			byte[] s = intToLengthHexByte(Integer.parseInt(subargs, 2), 1);
			if (s.length == 0) {
				s = new byte[1];
			}
			revalue[i] = s[0];
			subargs = "";
		}
		return revalue;
	}

	/**
	 * 二进制转换为变长的十六进制byte数组
	 * 
	 * @param bins
	 * @return
	 */
	public static byte[] binStrToLongerHexbyte(String bins) {
		if (!StrHelper.isNumber(bins)) {
			return null;
		}
		List<String> temps = new ArrayList<String>();
		boolean isfirst = true;
		String flag = "0";
		while (bins.length() > 0) {
			if (isfirst) {
				flag = "0";
			} else {
				flag = "1";
			}
			isfirst = false;
			if (bins.length() > 7) {
				temps.add(flag + bins.substring(bins.length() - 7, bins.length()));
				bins = bins.substring(0, bins.length() - 7);
			} else {
				bins = StrHelper.fillString(bins, 7, "0");
				temps.add(flag + bins);
				bins = "";
			}
		}
		byte[] tempbytes = new byte[0];
		for (int i = temps.size() - 1; i >= 0; i--) {
			if (i == temps.size() - 1 && new Integer(temps.get(i)) == 10000000) {
				temps.remove(i);
				continue;
			}
			tempbytes = StrHelper
					.integrateByteArray(tempbytes, binStrToLengthHexByte(temps.get(i)));
		}
		return tempbytes;
	}

	/**
	 * 十六进制byte转换成定长二进制字符串
	 * 
	 * @param args
	 * @return
	 */
	public static String hexByteToLengthBinStr(byte[] args) {
		return StrHelper.fillString(Integer.toBinaryString(Integer.parseInt(hexByteToLengthHexStr(args), 16)),"0",8);
	}

	/**
	 * 十六进制byte转十六进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String hexByteToLengthHexStr(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			// if (n<b.length-1) hs=hs+":";
		}
		return hs.toUpperCase();
	}

	/**
	 * 二进制字符串转String
	 * 
	 * @param binStr
	 * @return
	 */
	public static String binStrToString(String binStr) {
		List<String> l = new ArrayList<String>();
		for (int i = binStr.length() - 1; i >= 0;) {
			String s = "";
			for (int j = 0; j < 8; j++) {
				if (i < 0) {
					s = '0' + s;
				} else {
					s = binStr.charAt(i) + s;
				}
				i--;
			}
			l.add(s);
		}
		Collections.reverse(l);
		String[] tempStr = l.toArray(new String[0]);
		char[] tempChar = new char[tempStr.length];
		for (int i = 0; i < tempChar.length; i++) {
			tempChar[i] = binStrtoChar(tempStr[i]);
		}
		return String.valueOf(tempChar);
	}

	/**
	 * 二进制字符串转字符
	 * 
	 * @param binStr
	 * @return
	 */
	public static char binStrtoChar(String binStr) {
		int[] temp = binStrToInt(binStr);
		int sum = 0;

		for (int i = 0; i < temp.length; i++) {
			sum += temp[temp.length - 1 - i] << i;
		}
		return (char) sum;

	}

	/**
	 * 将二进制字符串转换成int数组
	 * 
	 * @param binStr
	 * @return
	 */
	public static int[] binStrToInt(String binStr) {
		char[] temp = binStr.toCharArray();
		int[] result = new int[temp.length];

		for (int i = 0; i < temp.length; i++) {
			result[i] = temp[i] - 48;
		}
		return result;
	}

	/**
	 * 变长十六进制byte转为定长二进制字符串
	 * 
	 * @param bins
	 * @return
	 */
	public static String longerHexByteToBinStr(byte[] bins) {
		StringBuffer str = new StringBuffer(bins.length * 7);
		for (byte b : bins) {
			str.append(StrHelper.fillString(hexByteToLengthBinStr(new byte[] { b }), 8, "0")
					.substring(1));
		}

		String tempStr = StrHelper.fillString(str.toString(), (int) Math
				.round(str.length() / 8 + 0.5) * 8, "0");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < tempStr.length();) {
			String temp = "";
			for (int j = 0; j < 8; j++) {
				temp += tempStr.charAt(i);
				i++;
			}
			list.add(temp);
		}
		while (list.size() > 0 && "00000000".equalsIgnoreCase(list.get(0))) {
			list.remove(0);
		}
		String result = "";
		for (String s : list) {
			result += s;
		}
		return result;
	}
	
	/**
	 * 十六进制byte转int
	 * @param args
	 * @return
	 */
	public static int hexByteToInt(byte[] args) {
		return Integer.parseInt(hexByteToLengthHexStr(args), 16);
	}
	
	public static long hexByteToLong(byte[] args) {
		return Long.parseLong(hexByteToLengthHexStr(args), 16);
	}

	private static final int SIZE_WIDTH = 7;
	private static final int SIZE_MASK = 0x7f; // 0111 1111
	private static final int FLAG_MASK = 0x80; // 1000 0000

	public static int longerHexByteToInt(byte[] bs) {
		int length = 0;
		int value = -1;
		int i = 0;
		do {
			value = bs[i++];
			//if (value == -1) {
				//return -1;
			//}
			length = (length << SIZE_WIDTH) | (value & SIZE_MASK);
		} while ((value & FLAG_MASK) != 0);

		// return length;
		return length;
	}

	public static void main(String[] args) throws Exception {
		int v=-100;
		int i= (v >>> 24) & 0xFF;
	      int j= (v >>> 16) & 0xFF;
	       int k= (v >>>  8) & 0xFF;
	     int x=(v >>>  0) & 0xFF;
	     
	     System.out.println(i);
	     System.out.println(j);
	     System.out.println(k);
	     System.out.println(x);

	}
}
