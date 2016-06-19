package com.kevin.iestudio.codec.exception;

import com.kevin.iestudio.codec.core.annotation.CoderItem;

public class CodecDecodeException extends CodecBaseException {

	private static final long serialVersionUID = -1832025652035203576L;

	protected static String getExceptionName() {
		return CodecDecodeException.class.getName();
	}
	
	public CodecDecodeException(String args, Object[] objs, Class<?> clazz, CoderItem ci){
		super(args,objs,clazz,ci);
	}
	
	public CodecDecodeException(String args, Object[] objs, Class<?> clazz, CoderItem ci,Exception e) {
		super(args,objs,clazz,ci,e);
	}

	public CodecDecodeException(String args, Object[] objs) {
		super(args, objs);
	}

	public CodecDecodeException(String args) {
		super(args);
	}

	public CodecDecodeException() {
		super();
	}

	public CodecDecodeException(String args, Object[] objs, Throwable e) {
		super(args, objs, e);
	}

	public CodecDecodeException(String args, Throwable e) {
		super(args, e);
	}

	public CodecDecodeException(Throwable e) {
		super(e);
	}

}
