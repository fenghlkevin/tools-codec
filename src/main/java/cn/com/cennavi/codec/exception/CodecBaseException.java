package cn.com.cennavi.codec.exception;

import cn.com.cennavi.codec.core.annotation.CoderItem;
import cn.com.cennavi.codec.util.StrHelper;

public class CodecBaseException extends RuntimeException {

	private static final long serialVersionUID = 3447282960803744352L;

	protected static String getExceptionName() {
		return CodecBaseException.class.getName();
	}

	public CodecBaseException() {
		super();
	}

	public CodecBaseException(String args, Object[] objs) {
		super(getExceptionName().concat(" Error : [").concat(StrHelper.getContent(args, objs))
				.concat("]"));
	}
	public CodecBaseException(String args, Object[] objs, Class<?> clazz, CoderItem ci,Exception e) {
		super(getExceptionName().concat(" Error : [").concat(StrHelper.getContent(args, objs))
				.concat("]").concat(
						clazz == null ? "" : " [Class Name : ".concat(clazz.getName())
								.concat(" ] ")).concat(
						ci == null ? "" : "[CoderItem ID : ".concat(ci.id()).concat(" ]")),e);

	}
	
	public CodecBaseException(String args, Object[] objs, Class<?> clazz, CoderItem ci) {
		super(getExceptionName().concat(" Error : [").concat(StrHelper.getContent(args, objs))
				.concat("]").concat(
						clazz == null ? "" : " [Class Name : ".concat(clazz.getName())
								.concat(" ] ")).concat(
						ci == null ? "" : "[CoderItem ID : ".concat(ci.id()).concat(" ]")));

	}

	public CodecBaseException(String args) {
		super(getExceptionName().concat(" Error : [").concat(args).concat("]"));
	}

	public CodecBaseException(Throwable e) {
		super(getExceptionName().concat(" Error : [Unknown]"), e);
	}

	public CodecBaseException(String args, Object[] objs, Throwable e) {
		super(getExceptionName().concat(" Error : [").concat(StrHelper.getContent(args, objs))
				.concat("]"), e);
	}

	public CodecBaseException(String args, Throwable e) {
		super(getExceptionName().concat(" Error : [").concat(args).concat("]"), e);
	}

}
