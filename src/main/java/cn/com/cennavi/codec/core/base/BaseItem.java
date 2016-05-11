package cn.com.cennavi.codec.core.base;

import java.io.InputStream;

import cn.com.cennavi.codec.Item;
import cn.com.cennavi.codec.exception.CodecEncodeException;
import cn.com.cennavi.codec.util.NTransUtil;

/**
 * 基本对象的基础类
 * 
 * @author 冯贺亮
 * @version 1.0
 * @since 2011-03-09 00:50:00
 */
public abstract class BaseItem implements Item {
	
	private static final long serialVersionUID = 3845128640696197123L;

	private byte[] encodedArray;
	
	private String itemName;

	private boolean isEncoded = false;

	public int getItemLength() {
		return itemLength;
	}

	public void setItemLength(int itemLength) {
		this.itemLength = itemLength;
	}

	private int itemLength;
	
	/**
	 * 构造函数
	 * 
	 * @param objectLength
	 *            1=1字节
	 */
	public BaseItem(int objectLength) {
//		if(objectLength==0){
//			throw new TPEGOutOfRangeException("Item length can not be zero");
//		}
		this.itemLength = objectLength;
	}

	/**
	 * 输出已经编译好的TPEGxml编码
	 */
	public StringBuffer getEncodedXml() {
		throw new RuntimeException("Temporarily to achieve Method");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.cennavi.tpeg.core.Item#getEncodedSize()
	 */
	public int getEncodedSize() {
		if (this.isEncoded()) {
			return this.getEncodedArray().length;
		} else {
			return -1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.cennavi.tpeg.core.Item#getEncodedStream()
	 */
	public byte[] getEncodedStream() {
		if (this.isEncoded()) {
			return this.getEncodedArray();
		} else {
			throw new CodecEncodeException("Object has not encoded. Please use Method [encoding]");
		}
	}
	
	public byte[] getEncodedArray() {
		return encodedArray;
	}

	public void setEncodedArray(byte[] encodedArray) {
		this.encodedArray = encodedArray;
	}

	public boolean isEncoded() {
		return isEncoded;
	}

	public void setEncoded(boolean isEncoded) {
		this.isEncoded = isEncoded;
	}

	@Override
	public void reEncoding() {
		encoding();
	}
	
	/* (non-Javadoc)
	 * @see cn.com.cennavi.tpeg.core.Item#setEncodedStream(java.io.ByteArrayInputStream)
	 */
	@Override
	@Deprecated
	public void setEncodedStream(InputStream stream) {
	}
	

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName=itemName;
	}
	
	public String getHexStr() {
		if(encodedArray==null){
			return "-1";
		}
		return NTransUtil.hexByteToLengthHexStr(encodedArray);
	}

	public String getIntStr() {
		if(encodedArray==null){
			return "-1";
		}
		return String.valueOf(NTransUtil.hexByteToInt(encodedArray));
	}
	
	public String getLongStr() {
		if(encodedArray==null){
			return "-1";
		}
		return String.valueOf(NTransUtil.hexByteToLong(encodedArray));
		
	}
	
	public String getBinStr() {
		if(encodedArray==null){
			return "-1";
		}
		return String.valueOf(NTransUtil.hexByteToLengthBinStr(encodedArray));
	}

}
