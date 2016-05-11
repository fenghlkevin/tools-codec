package cn.com.cennavi.codec.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述Filed的配置对象
 * @author 冯贺亮
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CoderItem {

	/**
	 * 设定Field的ID
	 * 
	 * @return
	 */
	String id();
	
	/**
	 * 对象长度
	 * 0:当前对象本身无长度,需要进入到对象内部进行编码，解码
	 * <=CoderItem.ITEMLENGTH_NOMAXLENGTH:无法解码，需要手动解码
	 * >0：对象长度
	 * <0：变长长度
	 * @return
	 */
	int length();

	/**
	 * Field 类型
	 * 
	 * @return
	 */
	CoderItemType type();
	
	/**
	 * AbstractCoderItem.toMapper(ture)，则输出会根据该字段配置输出内容。</p>
	 * AbstractCoderItem.toMapper(false)，则忽略该字段</p>
	 * @return
	 */
	boolean toMapper() default false;
	
	Class<? extends IMapperConverter> toMapperClass() default IMapperConverter.class;

	/**
	 * 依赖配置 CRC:依赖 dependentField，dependentFieldDigit Length：依赖 dependentField
	 * 
	 * @return
	 */
	Dependent dependent() default @Dependent;
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	@interface Dependent {

		/**
		 * 读取Field对应的长度
		 * CRC: 负数：字段往前数位数 即  itemA,itemB,CRC,itemC 读取itemB,itemA的时候使用负数
		 * 	    正数：字段往后数位数 即  itemA,itemB,CRC，itemC 读取itemC的时候使用正数
		 * BYTEITEM:配置byteflag中属性的存储位置，根据byteflag类型不同，插入不同位置
		 * @return
		 */
		int[] dependentFieldDigit() default {};

		/**
		 * ITEM:该对象依赖的Field,或byteflag所标示的对象
		 * BYTEITEM:byteflag所依赖的字段，如果字段配置为空则不能进行编码和解码，如果配置字段名称为"default-yes",则默认填写1，如配置字段为"default-no",则默认填写0
		 * @return
		 */
		String[] dependentField() default {};
	}
	
	public static final int ITEMLENGTH_NOMAXLENGTH=-99999;
	
	public static final String BYTEFLAG_dependentField_DEFAUTL_YES="default-yes";
	
	public static final String BYTEFLAG_dependentField_DEFAUTL_NO="default-no";

	enum CoderItemType {

		/**
		 * 无特殊情况的类型，直接编译
		 */
		ITEM,
		
		/**
		 * byteflag类型:seclector
		 */
		FLAGITEM_SECLECTOR,
		
		/**
		 * byteflag类型:bitswitch
		 */
		FLAGITEM_BITSWITCH,

		/**
		 * 自身循环进行编译
		 */
		LIST,

		/**
		 * 需要编译其他对象
		 */
		CRC,

		/**
		 * 需要计算其他字段长度后，编译自身
		 */
		LENGTH
	}
	

}
