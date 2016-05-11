package cn.com.cennavi.codec.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述Class的配置对象
 * @author 冯贺亮
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CoderLinks {

	/**
	 * 构建顺序
	 * 
	 * @return
	 */
	EncoderBuildOrder buildOrder();

	/**
	 * 编译顺序
	 * 
	 * @return
	 */
	EncoderBuildOrder compileOrder();

	/**
	 * 类对象使用，设定该层对象的构建顺序，其执行顺序在EncoderCompileOrder之后
	 * 
	 * @author 冯贺亮
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	@interface EncoderBuildOrder {
		/**
		 * 配置Field设定的ID值，如果填写了没有配置的ID，则会抛出TPEGEncodeException
		 * 
		 * @return
		 */
		String[] order();
	}

	/**
	 * 类对象使用，设定该层对象的编译顺序
	 * 
	 * @author 冯贺亮
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	@interface EncoderCompileOrder {
		/**
		 * 配置Field设定的ID值，如果填写了没有配置的ID，则会抛出TPEGEncodeException
		 * 
		 * @return
		 */
		String[] order();
	}
}
