package cn.com.cennavi.codec.core.base;

import cn.com.cennavi.codec.exception.CodecEncodeException;
import cn.com.cennavi.codec.util.Crc16Util;
import cn.com.cennavi.codec.util.NTransUtil;

/**
 * 描述CRC结构体的对象
 * 
 * @author 冯贺亮
 * @version 1.0
 * @since 2011-03-09 00:50:00
 */
public class CRC extends BaseItem {

	private byte[] needCrc;

	public CRC(int objectLength) {
		super(objectLength);
	}

	private static final long serialVersionUID = 4288048612351989006L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.cennavi.tpeg.core.Item#encoding()
	 */
	@Override
	public void encoding() {
		if (this.needCrc == null) {
			throw new CodecEncodeException("CRC is empty to be compiled");
		} else {
			this.setEncodedArray(Crc16Util.getCrcValue(needCrc));
			this.setEncoded(true);
		}

	}

	public byte[] getNeedCrc() {
		return needCrc;
	}

	public void setNeedCrc(byte[] needCrc) {
		this.needCrc = needCrc;
	}

	@Override
	public void decoding() {

	}

	@Override
	public int getEncodedMaxSize() {
		return this.getItemLength();
	}

	@Override
	public String toString() {
		if(needCrc==null){
			return "";
		}
		return NTransUtil.hexByteToLengthHexStr(needCrc);
	}

}
