package com.kevin.iestudio.codec.exception;

public class CodecParseException extends CodecBaseException {

	private static final long serialVersionUID = -444925744841793431L;
	
	protected static String getExceptionName() {
		return CodecParseException.class.getName();
	};
	

	public CodecParseException(String args, Object[] objs) {
		super(args, objs);
	}

	public CodecParseException(String args) {
		super(args);
	}

	public CodecParseException() {
		super();
	}

	public CodecParseException(String args, Object[] objs, Throwable e) {
		super(args, objs, e);
	}

	public CodecParseException(String args, Throwable e) {
		super(args, e);
	}

	public CodecParseException(Throwable e) {
		super(e);
	}

}
