package cn.com.cennavi.codec.util;

public class TypeConversion {

	/**
	 * int类型转换成byte[]
	 * 
	 * @param num
	 *            int数
	 * @return byte[]
	 */
	public static byte[] intToBytes(int num) {

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	/*
	 * public static void main(String args[]) { byte[] s =
	 * TypeConversion.intToBytes(1);
	 * 
	 * String str = "a"; String str1 = "你";
	 * System.out.println("s:"+s.length);//输出的值为4
	 * System.out.println("a:"+str.getBytes().length);//输出的值为1
	 * System.out.println("你:"+str1.getBytes().length);//输出的值为2 //由此可见,Java中的:
	 * 一个int=4个byte 一个String str1='a'; =1个byte 一个String str1='中'; =2个byte
	 * 
	 * }
	 */

	/**
	 * short类型转换成byte[]
	 * 
	 * @param num
	 *            short数
	 * @return byte[]
	 */
	public static byte[] shortToBytes(short num) {
		byte[] b = new byte[2];
		for (int i = 0; i < 2; i++) {
			b[i] = (byte) (num >>> (i * 8));

		}
		return b;
	}

	/**
	 * byte[]转换成int数
	 * 
	 * @param data
	 *            包括int的byte[]
	 * @param offset
	 *            偏移量
	 * @return int数
	 */
	public static int bytesToInt(byte[] data, int offset) {
		int num = 0;
		for (int i = offset; i < offset + 4; i++) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}

	/**
	 * long类型转换成byte[]
	 * 
	 * @param num
	 *            long数
	 * @return byte[]
	 */
	public static byte[] longToBytes(long num) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (num >>> (56 - i * 8));
		}
		return b;
	}

	/**
	 * byte[]转换成long数
	 * 
	 * @param data
	 *            包括long的byte[]
	 * @param offset
	 *            偏移量
	 * @return long数
	 */
	public static long bytesToLong(byte[] data, int offset) {
		long num = 0;
		for (int i = offset; i < offset + 8; i++) {
			num <<= 8;
			num |= (data[i] & 0xff);
		}
		return num;
	}

}
