package com.kevin.iestudio.codec.exception;

import com.kevin.iestudio.codec.core.annotation.CoderItem;

public class CodecEncodeException extends CodecBaseException {

	private static final long serialVersionUID = -1832025652035203576L;

	protected static String getExceptionName() {
		return CodecEncodeException.class.getName();
	}
	
	public CodecEncodeException(String args, Object[] objs, Class<?> clazz, CoderItem ci){
		super(args,objs,clazz,ci);
	}
	
	public CodecEncodeException(String args, Object[] objs, Class<?> clazz, CoderItem ci,Exception e) {
		super(args,objs,clazz,ci,e);
	}

	public CodecEncodeException(String args, Object[] objs) {
		super(args, objs);
	}

	public CodecEncodeException(String args) {
		super(args);
	}

	public CodecEncodeException() {
		super();
	}

	public CodecEncodeException(String args, Object[] objs, Throwable e) {
		super(args, objs, e);
	}

	public CodecEncodeException(String args, Throwable e) {
		super(args, e);
	}

	public CodecEncodeException(Throwable e) {
		super(e);
	}

}
