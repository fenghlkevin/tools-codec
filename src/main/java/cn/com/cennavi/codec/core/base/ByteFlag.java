package cn.com.cennavi.codec.core.base;

import cn.com.cennavi.codec.exception.CodecBaseException;
import cn.com.cennavi.codec.exception.CodecOutOfRangeException;
import cn.com.cennavi.codec.util.NTransUtil;
import cn.com.cennavi.codec.util.StrHelper;

/**
 * 用比特位表示true/false selector--0: 最高位表示是否还有下一字节。每一位的位置如下 ：A012 3456 其中A不使用。
 * bitSwitch--1：严格按照注释表示的填写 每位位置如下 7654 3210
 * 
 * @author 冯贺亮
 * @version 1.0
 * @since 2011-03-09 00:50:00
 */
public class ByteFlag extends BaseItem {

	private static final long serialVersionUID = 3168750391955200155L;

	/**
	 * type of selector
	 */
	public static final int TYPE_SELECTOR = 0;

	/**
	 * type of bitswitch
	 */
	public static final int TYPE_BITSWITCH = 1;

	/**
	 * 表示 当前对象的类型
	 */
	private int type;

	private StringBuffer binValue;

	public String test() {
		return binValue.toString();
	}

	/**
	 * 
	 * @param objectLength
	 * @param type
	 */
	public ByteFlag(int objectLength, int type) {
		super(objectLength);

		if (type != TYPE_BITSWITCH && type != TYPE_SELECTOR) {
			throw new CodecBaseException("type value is not right.");
		}

		if (type == TYPE_BITSWITCH && objectLength <= 0) {
			throw new CodecBaseException("ByteFlag TYPE_BITSWITCH Length can not write less then 1.");
		}

		binValue = new StringBuffer();

		if (objectLength > 0) {
			binValue.append(StrHelper.fillString("", Math.abs(objectLength) * 8, "0"));
		}else{
			binValue.append(StrHelper.fillString("", 8, "0"));
		}

		if (type == TYPE_SELECTOR && objectLength > 1) {
			// 如果是selector，并且1byte长度，需要对首位进行补字符。
			// 比如2byte长度，补字符前为：000000000 000000000 .补字符后为10000000 00000000
			
			int count = objectLength;
			while (count > 1) {
				int interval = (count - 2) * 8;
				binValue.replace(interval, interval + 1, "1");
				count--;
			}
		}
		// TYPE_SELECTOR objectLength<0时，只创建第一字节，如果输入了第二字节内容，会补充一个字节使用

		this.type = type;
	}

	public boolean getLocationValue(int location) {
		char temp = 10;
		if (this.type == TYPE_BITSWITCH) {
			this.validateLocation(location);
			temp = binValue.charAt(binValue.length() - 1 - location);

		} else if (this.type == TYPE_SELECTOR) {
			int at = location + (location / 7) + 1;
			if (super.getItemLength() > 0 && binValue.length() <= at) {
				throw new CodecOutOfRangeException("byteflag is out of range. max is: {0};index is {1}", new Object[] { new Integer(binValue.length() - 1), new Integer(at) });
			} else if (super.getItemLength() < 0) {
				if (binValue.length() <= at) {
					if (Math.abs(super.getItemLength()) * 8 <= at) {
						throw new CodecOutOfRangeException("byteflag is out of range. max is: {0};index is {1}", new Object[] { new Integer(Math.abs(super.getItemLength()) * 8 - 1), new Integer(at) });
					} else {
						return false;
					}
				}
			}
			temp = binValue.charAt(at);
		}
		return temp != '0';
	}

	private void validateLocation(int location) {
		if (location < 0) {
			throw new CodecOutOfRangeException("ByteFlag location is out of range,location is {0}", new Object[] { new Integer(location) });
		}
		// else if (binValue.length() < location) {
		// throw new
		// TPEGOutOfRangeException("ByteFlag location is out of range [{0}]",
		// new Object[] { new Integer(location) });
		// }
		// else if(this.type == TYPE_SELECTOR&&binValue.length() < location){
		// throw new
		// TPEGOutOfRangeException("ByteFlag SELECTOR location is out of range [{0}]",
		// new Object[] { new Integer(location) });
		// }
	}

	/**
	 * 修改某一位的标记
	 * 
	 * @param value
	 *            :修改值
	 * @param location
	 *            ：属于第几位（不能超过最长位数）
	 */
	public void changeLocationValue(int value, int location) {
		validateLocation(location);
		if (this.type == TYPE_BITSWITCH) {
			binValue.replace(binValue.length() - 1 - location, binValue.length() - 1 - location + 1, String.valueOf(value));
		} else if (this.type == TYPE_SELECTOR && super.getItemLength() > 0) {
			// if (location != 0 && location % 7 == 0) {
			// throw new
			// TPEGOutOfRangeException("Selector is out of range [{0}], can not use 7,14,21 and so on",
			// new Object[] { new Integer(location) });
			// }
			int in = (location / 7) + 1;
			binValue.replace(location + in, location + in + 1, String.valueOf(value));
		} else if (this.type == TYPE_SELECTOR && super.getItemLength() < 0) {
			int in = (location / 7) + 1;
			if (in > Math.abs(super.getItemLength())) {
				throw new CodecOutOfRangeException("Selector index out of range : [{0}].Max Length is [{1}]", new Object[] { new Integer(location),
						new Integer(Math.abs(super.getItemLength()) * 8 - Math.abs(super.getItemLength())) });
			}
			if (binValue.length() < (in * 8)) {
				int inv = (binValue.length() / 8 - 1) * 8;
				binValue.replace(inv, inv + 1, "1");
				binValue.append("00000000");
			}

			binValue.replace(location + in, location + in + 1, String.valueOf(value));
		}
	}

	/**
	 * 修改某一位的标记
	 * 
	 * @param flag
	 *            属性true:1 false:0
	 * @param location
	 *            属于第几位（不能超过最长位数）
	 */
	public void changeLocationValue(boolean flag, int location) {
		changeLocationValue(flag ? 1 : 0, location);
	}

	public static void main(String[] args) {
		ByteFlag bf = new ByteFlag(2, ByteFlag.TYPE_SELECTOR);
		bf.changeLocationValue(true, 0);
		bf.changeLocationValue(true, 4);
		bf.changeLocationValue(true, 8);
		// bf.changeLocationValue(true, 15);
		// bf.changeLocationValue(true, 4);
		bf.encoding();
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
		byte[] temp = NTransUtil.binStrToLengthHexByte(binValue.toString());
		temp = NTransUtil.binStrToLengthHexByte(binValue.toString());
		if (temp.length > Math.abs(this.getItemLength())) {
			throw new CodecOutOfRangeException("filed value is out of length. byte length {0}", new Object[] { new Integer(temp.length) });
		}

		byte[] st = new byte[Math.abs(this.getItemLength()) - temp.length];
		temp = StrHelper.integrateByteArray(st, temp);
		this.setEncodedArray(temp);
		super.setEncoded(true);
	}

	@Override
	public int getEncodedMaxSize() {
		return Math.abs(this.getItemLength());
	}

	@Override
	public void decoding() {
		binValue.setLength(0);
		binValue.append(NTransUtil.hexByteToLengthBinStr(getEncodedArray()));
		// if (this.getItemLength() > 0) {
		// binValue.append(NTransUtil.hexByteToLengthBinStr(getEncodedArray()));
		// } else if (this.getItemLength() < 0) {
		// binValue.append(NTransUtil.longerHexByteToBinStr(getEncodedArray()));
		// }

	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return binValue.toString();
	}
	
	

}
