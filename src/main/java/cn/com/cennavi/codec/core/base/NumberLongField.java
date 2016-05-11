package cn.com.cennavi.codec.core.base;

import cn.com.cennavi.codec.exception.CodecOutOfRangeException;
import cn.com.cennavi.codec.util.NTransUtil;

/**
 * 长度对象
 * 
 * @author 冯贺亮
 * @version 1.0
 * @since 2011-03-09 00:50:00
 */
public class NumberLongField extends BaseItem {

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	private static final long serialVersionUID = 2316904772553616233L;

	private long number;

	/**
	 * 构造长度对象，如填写负数 则为可变长度，并且二进制最高位表示 其后面是否还存在一个字节
	 * 
	 * @param number
	 *            数据内容
	 * @param length
	 *            编译后占位长度 1=1字节
	 */
	public NumberLongField(Long number, int length) {
		super(length);
		this.number = number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.cennavi.tpeg.core.Item#encoding()
	 */
	public void encoding() {
		if (isEncoded()) {
			return;
		}
		byte[] temp = null;
		if (this.getItemLength() == 0) {
			temp = new byte[0];
		} else if (this.getItemLength() > 0) {
			// 定长
			temp = NTransUtil.intToLengthHexByte(number, getItemLength());
		} else if (this.getItemLength() < 0) {
			// 变长
//			String bins = Integer.toBinaryString(number);
			String bins =Long.toBinaryString(number);
			temp = NTransUtil.binStrToLongerHexbyte(bins);

		}
		if (temp.length > Math.abs(this.getItemLength())) {
			throw new CodecOutOfRangeException("filed value is out of length");
		}
		this.setEncodedArray(temp);
		super.setEncoded(true);
	}

	@Override
	public int getEncodedMaxSize() {
		return Math.abs(this.getItemLength());
	}

	@Override
	public void decoding() {
		if (this.getItemLength() > 0) {
			// 定长
			this.number = NTransUtil.hexByteToInt(this.getEncodedArray());
		} else if (this.getItemLength() < 0) {
			// 变长
			this.number = NTransUtil.longerHexByteToInt(this.getEncodedArray());
		}
	}

	@Override
	public String toString() {
		return String.valueOf(number);
	}
	
	
}
