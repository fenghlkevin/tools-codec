package cn.com.cennavi.codec.util;

import java.io.FileInputStream;

public final class Crc16Util {

	public static byte[] getCrcValue(byte[] value) {
		int crc = 0xFFFF; // initial value
		int polynomial = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)

		for (byte b : value) {
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b >> (7 - i) & 1) == 1);
				boolean c15 = ((crc >> 15 & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit)
					crc ^= polynomial;
			}
		}
		crc = ~crc;
		crc &= 0xffff;
		String crcStr=Integer.toHexString(crc);
		StringBuffer str=new StringBuffer();
		if(crcStr.length()<4){
			for(int i=0;i<4-crcStr.length();i++){
				str.append("0");
			}
			
		}
		str.append(crcStr);
		return NTransUtil.hexToLengthHexByte(str.toString());
	}

	public static void main(String[] args) throws Exception {
		FileInputStream f=new FileInputStream("encode_pro.tpeg");
		byte[] bs=new byte[f.available()];
		f.read(bs);
//		byte[] cs = new byte[] { 0x02 ,0x35 ,-111 ,0x00 ,-1 ,0x00 ,0x32 ,0x00 ,0x01 ,0x0A ,0x09 ,-125 ,-49 ,0x38 ,0x29 ,0x4D };
		
		System.out.println(NTransUtil.hexByteToLengthHexStr(getCrcValue(bs)));
//		TypeConversion.intToBytes(0x91);
//		TypeConversion.intToBytes(0xFF);
//		TypeConversion.intToBytes(0x83);
//		TypeConversion.intToBytes(0xCF);
//		System.out.println(NTransUtil.hexByteToLengthHexStr(new byte[]{-1}));
	}

}
