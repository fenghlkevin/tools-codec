package com.kevin.iestudio.codec;

import java.io.InputStream;
import java.io.Serializable;

/**
 * TEPG基础类，所有TEPG对象都需要继承该对象
 * 
 * @author 冯贺亮
 * @version 1.0
 * @since 2011-03-09 00:50:00
 */
public interface Item extends Serializable {
	
	/**
	 * 编码当前对象
	 * @return 编译成功或失败
	 */
	public void encoding();
	
	/**
	 * 重新编译当前对象
	 * @return
	 */
	public void reEncoding();
	
	/**
	 * 返回该对象编译后所有的长度
	 * @return 返回编译后的长度
	 */
	public int getEncodedSize();
	/**
	 * 返回该对象编译后所能达到的最大长度
	 * @return 返回编译后的长度
	 */
	public int getEncodedMaxSize();

	/**
	 * 输出已经编译好的TPEG编码
	 * @return 编译后的byte数组
	 */
	public byte[] getEncodedStream();

//	/**
//	 * 输出已经编译好的TPEGxml编码
//	 * @return 输出xml格式的tpeg编码，暂不使用
//	 */
//	public StringBuffer getEncodedXml();
	
	/**
	 * 设置已经编译好的16进制数据到对象中
	 * @param stream
	 */
	public void setEncodedStream(InputStream stream);
	
	/**
	 * 解码当前对象
	 */
	public void decoding();
	
	/**
	 * 从某个字段开始编译，不包括该字段
	 * 
	 * @param startfield
	 *            :从字段开始，不包括该字段
	 */
//	public void decoding(Field startfield);
	
	/**
	 * 从某个字段开始编译，不包括该字段
	 * 
	 * @param startfield从字段开始，不包括该字段。如该字段为null，则从配置的第一个字段开始
	 * 
	 * @param endfield:到该字段结束，包括该字段。如该字段为null，则解码到配置的最后一个字段结束
	 */
//	public void decoding(Field startfield,Field endfield);
	
//	/**
//	 * 存在的子对象个数
//	 * @return
//	 */
//	public int innerItemsCount();
	
}
