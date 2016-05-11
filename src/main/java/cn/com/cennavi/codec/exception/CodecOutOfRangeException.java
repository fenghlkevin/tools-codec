package cn.com.cennavi.codec.exception;

public class CodecOutOfRangeException extends CodecBaseException {

	private static final long serialVersionUID = -7282845436961426967L;
	
	protected static String getExceptionName() {
		return CodecOutOfRangeException.class.getName();
	};
	public CodecOutOfRangeException(String args, Object[] objs) {
		super(args, objs);
	}

	public CodecOutOfRangeException(String args) {
		super(args);
	}

	public CodecOutOfRangeException() {
		super();
	}

	public CodecOutOfRangeException(String args, Object[] objs, Throwable e) {
		super(args, objs, e);
	}

	public CodecOutOfRangeException(String args, Throwable e) {
		super(args, e);
	}

	public CodecOutOfRangeException(Throwable e) {
		super(e);
	}
}
