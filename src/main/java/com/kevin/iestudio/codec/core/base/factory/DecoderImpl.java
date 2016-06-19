//package cn.com.cennavi.codec.core.base.factory;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import Item;
//import CoderItem;
//import CoderLinks;
//import CoderItem.CoderItemType;
//import AbstractCoderItem;
//import ByteFlag;
//import StrHelper;
//import cn.com.cennavi.tpeg.exception.TPEGDecodeException;
//
//public class DecoderImpl {
//	
//
//	/**
//	 * log日志对象
//	 */
//	private Log logger = LogFactory.getLog(DecoderImpl.class);
//	
//	public Item decoding(Field startfield, Field endfield,InputStream stream){
//		CoderLinks el = this.getClass().getAnnotation(CoderLinks.class);
//		if (el == null) {
//			throw new TPEGDecodeException("Object do not  Exist annotation CoderLinks.", null, this.getClass(), null);
//		}
//		String[] build = el.buildOrder().order();
//		if (build == null || build.length == 0) {
//			throw new TPEGDecodeException("Object do not Exist annotation right CompileOrder or BuildOrder. ", null, this.getClass(), null);
//		}
//		String id = null;
//		try {
//			//读取解析起始的位置
//			int startIndex = 0;
//			String startfieldID = "";
//			if (startfield != null) {
//				CoderItem ci = startfield.getAnnotation(CoderItem.class);
//				if (ci == null) {
//					throw new TPEGDecodeException("startfield {0} is not exist a Annotation typeof CoderItem.", new Object[] { startfield.getName() }, this.getClass(), ci);
//				}
//				startfieldID = ci.id();
//			}
//			if ("".equalsIgnoreCase(startfieldID)) {
//				logger.debug("startfield is null,so from the first element do decode");
//				startIndex = -1;
//			} else {
//				startIndex = StrHelper.searchString(build, startfieldID);
//			}
//			
//			//读取解析到达位置
//			int endIndex = build.length-1;
//			String endfieldID = "";
//			if (endfield != null) {
//				CoderItem ci = startfield.getAnnotation(CoderItem.class);
//				if (ci == null) {
//					throw new TPEGDecodeException("endfield {0} is not exist a Annotation typeof CoderItem.", new Object[] { startfield.getName() }, this.getClass(), ci);
//				}
//				endfieldID = ci.id();
//			}
//			if ("".equalsIgnoreCase(endfieldID)) {
//				logger.debug("endField is null,so decoding element to the last one");
//				endIndex = build.length-1;
//			} else {
//				endIndex = StrHelper.searchString(build, endfieldID);
//			}
//			
//			//对startIndex，endIndex进行校验
//
//			List<Field> selectors = new ArrayList<Field>(2);
//			for (int i = startIndex + 1; i <=endIndex; i++) {
//				id = build[i];
//				if (!doDecompile(id, stream, selectors)) {
//					break;
//				}
//			}
//		} catch (IllegalArgumentException e) {
//			throw new TPEGDecodeException("Decoding Error", null, this.getClass(), null, e);
//		} catch (IllegalAccessException e) {
//			throw new TPEGDecodeException("Decoding Error", null, this.getClass(), null, e);
//		} catch (IOException e) {
//			throw new TPEGDecodeException("Decoding Error", null, this.getClass(), null, e);
//		}
//	}
//	
//	/**
//	 * 解码对象
//	 * 
//	 * @param id
//	 *            : field ID
//	 * @param stream
//	 *            : Encoded Stream
//	 * @return
//	 * @throws IllegalArgumentException
//	 * @throws IllegalAccessException
//	 * @throws IOException
//	 */
//	private boolean doDecompile(String id, InputStream stream, List<Field> selectors) throws IllegalArgumentException, IllegalAccessException, IOException {
//		Field field = encodingField.get(this.getClass().getName()).get(id);
//		CoderItem ei = encodingEncodeItem.get(this.getClass().getName()).get(id);
//		if (ei.length() <= CoderItem.ITEMLENGTH_NOMAXLENGTH) {
//			return false; // 暂时不处理小于CoderItem.ITEMLENGTH_NOMAXLENGTH的对象
//		}
//		if (ei.type() == CoderItemType.LIST) {
//			return false; // 已经进行配置 可以不需要
//		} else {
//			boolean thisIsFlag = false;
//			if (ei.type() == CoderItemType.FLAGITEM_BITSWITCH || ei.type() == CoderItemType.FLAGITEM_SECLECTOR) {
//				selectors.add(field);
//				thisIsFlag = true;
//			}
//			int bflag = -3;
//			if (selectors.size() > 0 && !thisIsFlag) {
//				bflag = this.isItemInByteFlag(selectors, field);
//			}
//
//			boolean doDecompile = false;
//			if (bflag == -3 || bflag == -1) {
//				doDecompile = true;
//			} else if (bflag == 0) {
//				doDecompile = false;
//			} else if (bflag == 1) {
//				doDecompile = true;
//			} else if (bflag == -2) {
//				throw new TPEGDecodeException("ByteFlag is not instance.", null, this.getClass(), ei);
//			}
//			if (doDecompile) {
//				decompileItem(field, ei, stream);
//			}
//			return true;
//		}
//	}
//
//	/**
//	 * 判断某个字段是否被ByteFlag所影射
//	 * 
//	 * @param selectors
//	 * @param fieldid
//	 * @return 0:有存储字段的selector，但该对没有被存储 1:有存储字段的selector,该字段被存储
//	 *         -1:有selector，但没有影射字段 -2:有selector，但该对象没有被实例化 -3:无selector
//	 * @throws IllegalArgumentException
//	 * @throws IllegalAccessException
//	 */
//	private int isItemInByteFlag(List<Field> selectors, Field field) throws IllegalArgumentException, IllegalAccessException {
//		int i = -3;
//		for (Field selector : selectors) {
//			ByteFlag bf = (ByteFlag) selector.get(this);
//			if (bf == null) {
//				i = -2;
//				continue;
//			}
//			CoderItem ci = selector.getAnnotation(CoderItem.class);
//			CoderItem fci = field.getAnnotation(CoderItem.class);
//			String s[] = ci.dependent().dependentField();
//			int index = StrHelper.searchString(s, fci.id());
//			if (index == -1) {
//				i = -1;
//				continue;
//			}
//			int location = ci.dependent().dependentFieldDigit()[index];
//			i = bf.getLocationValue(location) ? 1 : 0;
//			break;
//		}
//		return i;
//	}
//	
//}
