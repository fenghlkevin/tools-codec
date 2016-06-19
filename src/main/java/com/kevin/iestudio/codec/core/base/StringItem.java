package com.kevin.iestudio.codec.core.base;

import com.kevin.iestudio.codec.Item;
import com.kevin.iestudio.codec.core.annotation.CoderItem;
import com.kevin.iestudio.codec.exception.CodecEncodeException;
import com.kevin.iestudio.codec.exception.CodecOutOfRangeException;
import com.kevin.iestudio.codec.util.NTransUtil;
import com.kevin.iestudio.codec.util.StrHelper;

public class StringItem extends BaseItem implements Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8614853974832161134L;

	private String str;

	/**
	 * 构造长度对象，如填写负数 则为可变长度，并且二进制最高位表示 其后面是否还存在一个字节
	 * 
	 * @param str
	 *            :存储的数据
	 * @param objectLength
	 *            :编译后占位长度 1=1字节
	 */
	public StringItem(String str, int objectLength) {
		super(objectLength);
		this.str = str;
	}

	@Override
	public void encoding() {
		if (str == null) {
			throw new CodecEncodeException("string is null");
		}
		if (isEncoded()) {
			return;
		}
		byte[] temp = null;
		if (this.getItemLength() == 0) {
			temp = new byte[0];
		} else if (this.getItemLength() > 0 || this.getItemLength() == CoderItem.ITEMLENGTH_NOMAXLENGTH) {
			// 定长
			temp = str.getBytes();
			if (this.getItemLength() > 0&&temp.length > this.getItemLength()) {
				throw new CodecOutOfRangeException("filed value is out of length");
			}
			if(this.getItemLength() > 0){
				byte[] st = new byte[this.getItemLength() - temp.length];
				temp = StrHelper.integrateByteArray(st, temp);
			}
		} else if (this.getItemLength() < 0) {
			// 变长
			temp = str.getBytes();
			String bin = NTransUtil.binBytesTobin(temp);
			temp = NTransUtil.binStrToLongerHexbyte(bin);
			if (temp.length > Math.abs(this.getItemLength())) {
				throw new CodecOutOfRangeException("filed value is out of length");
			}
		}
		this.setEncodedArray(temp);
		super.setEncoded(true);
	}

	@Override
	public int getEncodedMaxSize() {
		return Math.abs(this.getItemLength());
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	@Override
	public void decoding() {
		if (this.getItemLength() > 0) {
			// 定长
			this.str = new String(this.getEncodedArray());
		} else if (this.getItemLength() < 0) {
			// 变长
			String temp1 = NTransUtil.longerHexByteToBinStr(this.getEncodedArray());
			this.str = NTransUtil.binStrToString(temp1);

		}
	}

	@Override
	public String toString() {
		return str;
	}
}
