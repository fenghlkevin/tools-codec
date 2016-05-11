package cn.com.cennavi.codec.core.base;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.cennavi.codec.Item;
import cn.com.cennavi.codec.core.annotation.CoderItem;
import cn.com.cennavi.codec.core.annotation.CoderLinks;
import cn.com.cennavi.codec.core.annotation.IMapperConverter;
import cn.com.cennavi.codec.core.annotation.CoderItem.CoderItemType;
import cn.com.cennavi.codec.exception.CodecBaseException;
import cn.com.cennavi.codec.exception.CodecDecodeException;
import cn.com.cennavi.codec.exception.CodecEncodeException;
import cn.com.cennavi.codec.exception.CodecOutOfRangeException;
import cn.com.cennavi.codec.util.NTransUtil;
import cn.com.cennavi.codec.util.ReflectUtil;
import cn.com.cennavi.codec.util.StrHelper;

/**
 * 该方法为所有ITEM对象（非base对象）需要继承的对象。
 * 继承该对象并在实现类中使用annotation，对实现类和成员变量进行标示，即可进行编译，无需手动编译对象。 成员变量类型支持如下
 * List<Item>,Item，两种 example对象：
 * 
 * @author 冯贺亮
 * 
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCoderItem implements Item {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2261925320952681432L;

	/**
	 * log日志对象
	 */
	private Logger logger = LoggerFactory.getLogger(AbstractCoderItem.class);

	/**
	 * 存储类对象信息
	 */
	private static Map<String, Map<String, Field>> encodingField;

	/**
	 * 存储类对象的FIELD的配置信息
	 */
	private static Map<String, Map<String, CoderItem>> encodingEncodeItem;

	/**
	 * 解码时的对象
	 */
	private InputStream stream;

	/**
	 * 编码后得到对象
	 */
	private byte[] encodedArray = new byte[0];

	/**
	 * 对象是否已经编码
	 */
	private boolean isEncoded = false;

	private static Map<String, Class<?>> coderBeanMap;

	{
		if(intanceObjMap==null){
			intanceObjMap=new HashMap<String, Object>();
		}
		if (encodingField == null || encodingEncodeItem == null) {
			encodingField = new HashMap<String, Map<String, Field>>();
			encodingEncodeItem = new HashMap<String, Map<String, CoderItem>>();
		}

		if (coderBeanMap == null) {
			coderBeanMap = new HashMap<String, Class<?>>();
		}

		coderBeanMap.put(this.getClass().getSimpleName().toLowerCase(), this.getClass());

		String key = this.getClass().getName();
		if (!encodingField.containsKey(key)) {

			if (!ReflectUtil.isInstanceOf(this.getClass(), Item.class)) {
				throw new CodecBaseException("Class is not instanceof {0}", new Object[]{Item.class.getName()}, this.getClass(), null);
			}

			Map<String, Field> fields = new HashMap<String, Field>();
			Map<String, CoderItem> encodeItems = new HashMap<String, CoderItem>();

			// 如果配置了EncoderItem字段，但是该字段不是Item的实现类的话，不可以通过实例化
			Class<?> thiz = this.getClass();
			for (; !thiz.equals(Object.class); thiz = thiz.getSuperclass()) {
				for (Field field : thiz.getDeclaredFields()) {
					if (field.isAnnotationPresent(CoderItem.class)) {
						CoderItem ei = field.getAnnotation(CoderItem.class);
						if (ei.type() != CoderItemType.LIST && !ReflectUtil.isInstanceOf(field, Item.class)) {
							throw new CodecBaseException("Configuration of different types of annotation types and field. CoderItem Name : {0}, Field Name : {1}", new Object[]{
									ei.type(), field.getName()}, this.getClass(), null);
						}
						if (fields.containsKey(ei.id())) {
							continue;
						}
						field.setAccessible(true);
						validate(field, ei);
						fields.put(ei.id(), field);
						encodeItems.put(ei.id(), ei);
					}
				}
			}

			encodingField.put(key, fields);
			encodingEncodeItem.put(key, encodeItems);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.com.cennavi.tpeg.core.Item#setEncodedStream(java.io.ByteArrayInputStream
	 * )
	 */
	@Override
	public void setEncodedStream(InputStream stream) {
		this.stream = stream;
	}

	/**
	 * @return
	 */
	protected InputStream getEncodedByteStream() {
		return this.stream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.cennavi.tpeg.core.Item#getEncodedMaxSize()
	 */
	public int getEncodedMaxSize() {
		CoderLinks el = this.getClass().getAnnotation(CoderLinks.class);
		String[] build = el.buildOrder().order();

		int tempMaxSize = 0;
		String key = this.getClass().getName();
		for (String id : build) {
			CoderItem ei = null;
			try {
				ei = encodingEncodeItem.get(key).get(id);
				Field field = encodingField.get(key).get(id);
				if (ei.type() == CoderItemType.LIST) {
					List<Item> its = (List<Item>) field.get(this);
					if (its == null) {
						continue;
					}
					for (Item it : its) {
						tempMaxSize += it.getEncodedMaxSize();
					}
				} else {
					Item item = (Item) field.get(this);
					if (item == null) {
						continue;
					}
					tempMaxSize += item.getEncodedMaxSize();
				}
			} catch (IllegalArgumentException e) {
				throw new CodecEncodeException("Illegal Argument error. ", new Object[]{}, this.getClass(), ei, e);
			} catch (IllegalAccessException e) {
				throw new CodecEncodeException("Illegal Access error. ", new Object[]{}, this.getClass(), ei, e);
			}
		}

		return tempMaxSize;
	}

	/**
	 * @return
	 */
	public boolean isEncoded() {
		return isEncoded;
	}

	/**
	 * @param isEncoded
	 */
	// protected void setEncoded(boolean isEncoded) {
	// this.isEncoded = isEncoded;
	// }

	public void setEncoded(boolean isEncoded) {
		this.isEncoded = isEncoded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.cennavi.tpeg.core.Item#getEncodedSize()
	 */
	@Override
	public int getEncodedSize() {
		if (this.isEncoded()) {
			return this.encodedArray.length;
		} else {
			return -1;
		}
	}

	public void setEncodedStream(byte[] bs) {
		this.encodedArray = bs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.cennavi.tpeg.core.Item#getEncodedStream()
	 */
	@Override
	public byte[] getEncodedStream() {
		if (this.isEncoded()) {
			return this.encodedArray;
		} else {
			throw new CodecEncodeException("Object has not encoded. Please Call cn.com.cennavi.tpeg.core.encoding()", null, this.getClass(), null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.cennavi.tpeg.core.Item#reEncoding()
	 */
	@Override
	public void reEncoding() {
		this.setEncoded(false);
		encoding();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.cennavi.tpeg.core.Item#encoding()
	 */
	@Override
	public void encoding() {
		if (this.isEncoded) {
			return;
		}

		CoderLinks el = this.getClass().getAnnotation(CoderLinks.class);
		if (el == null) {
			throw new CodecEncodeException("Object do not  Exist annotation CoderLinks. ", null, this.getClass(), null);
		}
		String[] compileOrder = el.compileOrder().order();
		String[] build = el.buildOrder().order();
		if (compileOrder == null || compileOrder.length == 0 || build == null || build.length == 0) {
			throw new CodecEncodeException("Object do not Exist annotation right CompileOrder or BuildOrder.", null, this.getClass(), null);
		}

		Map<String, byte[]> compileMap = new HashMap<String, byte[]>();
		String id = null;
		try {
			for (String fieldID : compileOrder) {
				id = fieldID;
				doCompile(fieldID, compileMap);
			}
			byte[] tempAll = new byte[0];
			for (String fieldID : build) {
				byte[] temp = compileMap.get(fieldID);
				if (temp == null || temp.length == 0) {
					logger.debug(StrHelper.getContent("Configuration Filed is null,so do not build the Field..Class Name : {0}, Field Name : {1}", new Object[]{
							this.getClass().getName(), fieldID}));
				}
				tempAll = StrHelper.integrateByteArray(tempAll, temp);
			}
			this.encodedArray = tempAll;
			this.setEncoded(true);
		} catch (IllegalArgumentException e) {
			this.setEncoded(false);
			throw new CodecEncodeException("Illegal Argument error. ITEM ID : {0}", new Object[]{id}, this.getClass(), null, e);
		} catch (IllegalAccessException e) {
			this.setEncoded(false);
			throw new CodecEncodeException("Illegal Access error. ITEM ID : {0}", new Object[]{id}, this.getClass(), null, e);
		} catch (Exception e) {
			this.setEncoded(false);
			if (e instanceof CodecBaseException) {
				throw (CodecBaseException) e;
			}
			throw new CodecEncodeException("UnKnown error. ITEM ID : {0}", new Object[]{id}, this.getClass(), null, e);
		}
	}

	/**
	 * 校验Filed的配置是否符合规定，在初始化时会确定
	 * 
	 * @param field
	 * @param coderItem
	 */
	private void validate(Field field, CoderItem coderItem) {
		try {
			Item item = (Item) field.get(this);
			if (coderItem.length() == 0 && item instanceof BaseItem) {
				throw new CodecBaseException(" if CoderItemType length is Zero, the Item can not extends BaseItem", null, this.getClass(), coderItem);
			} else if ((coderItem.length() > 0 || (coderItem.length() < 0 && coderItem.length() > CoderItem.ITEMLENGTH_NOMAXLENGTH))
					&& !(ReflectUtil.isInstanceOf(field, BaseItem.class))) {
				throw new CodecBaseException(" if CoderItemType length is not Zero, the Item muse be extends BaseItem", null, this.getClass(), coderItem);
			}
		} catch (IllegalArgumentException e) {
			throw new CodecBaseException("get Item Object IllegalArgumentException", null, this.getClass(), coderItem, e);
		} catch (IllegalAccessException e) {
			throw new CodecBaseException("get Item Object IllegalAccessException", null, this.getClass(), coderItem, e);
		}
		if (coderItem.type() == CoderItemType.ITEM) {
			// if (CoderItem.dependent() != null) {
			// throw new
			// TPEGEncodeException("EncoderItemType item cannot Exist Dependent");
			// }
			// if (coderItem.length() < 0 && coderItem.length() > -99999) {
			// throw new TPEGBaseException(
			// "EncoderItemType item length must Zero, Positive integer or less than -99999");
			// }
		} else if (coderItem.type() == CoderItemType.LIST) {
			// if (CoderItem.dependent() != null) {
			// throw new
			// TPEGBaseException("EncoderItemType List cannot Exist Dependent");
			// }
		} else if (coderItem.type() == CoderItemType.CRC) {
			if (coderItem.dependent() == null) {
				throw new CodecBaseException("EncoderItemType CRC must Exist Dependent", null, this.getClass(), coderItem);
			}
			if (coderItem.dependent().dependentFieldDigit() == null || coderItem.dependent().dependentFieldDigit().length == 0) {
				throw new CodecBaseException("EncoderItemType CRC, must Exist dependentFieldDigit", null, this.getClass(), coderItem);
			}
			if (coderItem.dependent().dependentField() == null || coderItem.dependent().dependentField().length == 0) {
				throw new CodecBaseException("EncoderItemType CRC, must Exist dependentField", null, this.getClass(), coderItem);
			}
			if (coderItem.dependent().dependentField().length != coderItem.dependent().dependentFieldDigit().length) {
				throw new CodecBaseException("EncoderItemType CRC, dependentField and dependentFieldDigit  must Exist same count item.", null, this.getClass(), coderItem);
			}
			if (coderItem.length() <= 0) {
				throw new CodecBaseException("EncoderItemType CRC length must Positive integer", null, this.getClass(), coderItem);
			}
		} else if (coderItem.type() == CoderItemType.LENGTH) {
			if (coderItem.dependent() == null) {
				throw new CodecBaseException("EncoderItemType LENGTH must Exist Dependent", null, this.getClass(), coderItem);
			}
			if (coderItem.dependent().dependentField() == null || coderItem.dependent().dependentField().length == 0) {
				throw new CodecBaseException("EncoderItemType LENGTH, must Exist dependentField", null, this.getClass(), coderItem);
			}
			if (coderItem.length() == 0 || coderItem.length() <= -99999) {
				throw new CodecBaseException("EncoderItemType LENGTH length must Positive integer or Negative integer", null, this.getClass(), coderItem);
			}
		} else if (coderItem.type() == CoderItemType.FLAGITEM_BITSWITCH || coderItem.type() == CoderItemType.FLAGITEM_SECLECTOR) {
			if (coderItem.dependent() == null) {
				throw new CodecBaseException("EncoderItemType FLAGITEM must Exist Dependent", null, this.getClass(), coderItem);
			}
			if (coderItem.dependent().dependentField() == null || coderItem.dependent().dependentField().length == 0) {
				throw new CodecBaseException("EncoderItemType FLAGITEM, must Exist dependentField", null, this.getClass(), coderItem);
			}
			if (coderItem.dependent().dependentFieldDigit() == null || coderItem.dependent().dependentFieldDigit().length == 0) {
				throw new CodecBaseException("EncoderItemType FLAGITEM, must Exist dependentFieldDigit", null, this.getClass(), coderItem);
			}
			if (coderItem.dependent().dependentFieldDigit().length != coderItem.dependent().dependentField().length) {
				throw new CodecBaseException("EncoderItemType FLAGITEM, dependentField and dependentFieldDigit must Exist same count item.", null, this.getClass(), coderItem);
			}
		}
	}

	/**
	 * 编译基础对象
	 * 
	 * @param field
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private byte[] compileItem(Field field) throws IllegalArgumentException, IllegalAccessException {
		Item it = (Item) field.get(this);
		if (it == null) {
			return null;
		}
		try {
			it.encoding();
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + "    " + field.getName());
			throw (CodecBaseException) e;

		}

		return it.getEncodedStream();
	}

	/**
	 * 编译数组
	 * 
	 * @param field
	 * @param ei
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private byte[] compileList(Field field, CoderItem ei) throws IllegalArgumentException, IllegalAccessException {
		if (ei.type().getClass().getName().equalsIgnoreCase(List.class.getName())) {
			throw new CodecEncodeException("Configuration of different types of annotation types and field. NOt a {0}. ", new Object[]{List.class.getName(),}, this.getClass(), ei);
		}
		List<Item> items = (List<Item>) field.get(this);
		if (items == null || items.size() == 0) {
			return null;
		}
		byte[] tempEncodeArray = new byte[0];
		Item item = null;
		for (int i = 0; i < items.size(); i++) {
			item = items.get(i);
			if (item == null) {
				logger.debug(StrHelper.getContent("LIst<Filed> index [{0}] has null item,so do not compile the List item. Class Name : {1}, Field Name : {2}", new Object[]{i,
						this.getClass().getName(), ei.id()}));
				continue;
			}
			item.encoding();
			byte itemArray[] = item.getEncodedStream();
			tempEncodeArray = StrHelper.integrateByteArray(tempEncodeArray, itemArray);
			item = null;
		}
		return tempEncodeArray;
	}

	/**
	 * 编译长度类型
	 * 
	 * @param field
	 * @param ei
	 * @param compile
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private byte[] compileLength(Field field, CoderItem ei, Map<String, byte[]> compile) throws IllegalArgumentException, IllegalAccessException {
		if (ei.type().getClass().getName().equalsIgnoreCase(NumberField.class.getName())) {
			throw new CodecEncodeException("Configuration of different types of annotation types and field. NOt a {0}.", new Object[]{NumberField.class.getName(),},
					this.getClass(), ei);
		}
		NumberField numberField = (NumberField) field.get(this);
		if (numberField == null) {
			return null;
		}

		String fieldIds[] = ei.dependent().dependentField();
		byte[] fieldByte = null;
		int length = 0;
		for (String fieldID : fieldIds) {
			fieldByte = compile.get(fieldID);
			if (fieldByte == null) {
				throw new CodecEncodeException("Configuration LENGTH Dependent Field do not Compile.", null, this.getClass(), ei);
			}
			length += fieldByte.length;
			fieldByte = null;
		}

		numberField.setNumber(length);
		numberField.encoding();
		return numberField.getEncodedStream();
	}

	/**
	 * 编译CRC
	 * 
	 * @param field
	 * @param ei
	 * @param compile
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private byte[] compileCRC(Field field, CoderItem ei, Map<String, byte[]> compile) throws IllegalArgumentException, IllegalAccessException {
		int digits[] = ei.dependent().dependentFieldDigit();
		String fields[] = ei.dependent().dependentField();
		int d;
		String fieldID;
		byte[] crcByte = new byte[0];
		byte[] fieldByte = null;
		/**
		 * 从前到后取出依赖的字段ID 根据字段ID取得相应byte数组
		 * 判断配置的字段长度与数组中是是否相同，如果数组长度小于配置长度，则取出数组中的所有内容 把数组内容放入临时CRC数组
		 * 循环结束后，对临时CRC数组中内容进行编译
		 */
		for (int i = 0; i < fields.length; i++) {
			d = digits[i];
			if (d == 0) {
				logger.debug(StrHelper.getContent("Configuration CRC ItemLength equals 0,so do not compile the Field. Class Name : {0}, Field Name : {1}", new Object[]{
						this.getClass().getName(), field.getName()}));
			}
			fieldID = fields[i];
			fieldByte = compile.get(fieldID);
			if (fieldByte == null) {
				throw new CodecEncodeException("Configuration CRC Dependent Field do not Compile.", null, this.getClass(), ei);
			}

			d = Math.abs(d);
			if (d > fieldByte.length) {
				d = fieldByte.length;
			}
			byte[] tempcrc = new byte[d];
			if (digits[i] < 0) {
				// 从该字段 从后往前取
				System.arraycopy(fieldByte, fieldByte.length - d, tempcrc, 0, d);
			} else {
				// 从该字段 从前往后取
				System.arraycopy(fieldByte, 0, tempcrc, 0, d);
			}
			crcByte = StrHelper.integrateByteArray(crcByte, tempcrc);

			d = 0;
			fieldID = null;
			fieldByte = null;
		}
		CRC it = (CRC) field.get(this);
		if (it == null) {
			return null;
		}

		it.setNeedCrc(crcByte);
		it.encoding();

		return it.getEncodedStream();
	}

	/**
	 * 编译长度类型
	 * 
	 * @param field
	 * @param ei
	 * @param compile
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private byte[] compileFlagItem(Field field, CoderItem ei, Map<String, byte[]> compile) throws IllegalArgumentException, IllegalAccessException {
		ByteFlag byteFlag = (ByteFlag) field.get(this);
		if (byteFlag == null) {
			return null;
		}

		String fieldIds[] = ei.dependent().dependentField();
		int[] locations = ei.dependent().dependentFieldDigit();
		byte[] fieldByte = null;
		String fieldID;
		for (int i = 0; i < fieldIds.length; i++) {
			fieldID = fieldIds[i];
			fieldByte = compile.get(fieldID);
			byteFlag.changeLocationValue(!(fieldByte == null || fieldByte.length == 0), locations[i]);
			fieldByte = null;
		}
		byteFlag.encoding();
		return byteFlag.getEncodedStream();
	}

	/**
	 * 从某个字段开始编译，不包括该字段
	 * 
	 * @param startfield
	 *            :从字段开始，不包括该字段
	 */
	public void decoding(Field startfield) {
		decoding(startfield, null);
	}

	// @Override
	public void decoding(Field startfield, Field endfield) {
		CoderLinks el = this.getClass().getAnnotation(CoderLinks.class);
		if (el == null) {
			throw new CodecDecodeException("Object do not  Exist annotation CoderLinks.", null, this.getClass(), null);
		}
		String[] build = el.buildOrder().order();
		if (build == null || build.length == 0) {
			throw new CodecDecodeException("Object do not Exist annotation right CompileOrder or BuildOrder. ", null, this.getClass(), null);
		}
		String id = null;
		try {
			// 读取解析起始的位置
			int startIndex = 0;
			String startfieldID = "";
			if (startfield != null) {
				CoderItem ci = startfield.getAnnotation(CoderItem.class);
				if (ci == null) {
					throw new CodecDecodeException("startfield {0} is not exist a Annotation typeof CoderItem.", new Object[]{startfield.getName()}, this.getClass(), ci);
				}
				startfieldID = ci.id();
			}
			if ("".equalsIgnoreCase(startfieldID)) {
				logger.debug("startfield is null,so from the first element do decode");
				startIndex = -1;
			} else {
				startIndex = StrHelper.searchString(build, startfieldID);
			}

			// 读取解析到达位置
			int endIndex = build.length - 1;
			String endfieldID = "";
			if (endfield != null) {
				CoderItem ci = endfield.getAnnotation(CoderItem.class);
				if (ci == null) {
					throw new CodecDecodeException("endfield {0} is not exist a Annotation typeof CoderItem.", new Object[]{startfield.getName()}, this.getClass(), ci);
				}
				endfieldID = ci.id();
			}
			if ("".equalsIgnoreCase(endfieldID)) {
				logger.debug("endField is null,so decoding element to the last one");
				endIndex = build.length - 1;
			} else {
				endIndex = StrHelper.searchString(build, endfieldID);
			}

			// 对startIndex，endIndex进行校验

			List<Field> selectors = new ArrayList<Field>(2);
			for (int i = startIndex + 1; i <= endIndex; i++) {
				id = build[i];
				if (!doDecompile(id, stream, selectors)) {
					break;
				}
			}
		} catch (IllegalArgumentException e) {
			throw new CodecDecodeException("Decoding Error", null, this.getClass(), null, e);
		} catch (IllegalAccessException e) {
			throw new CodecDecodeException("Decoding Error", null, this.getClass(), null, e);
		} catch (IOException e) {
			throw new CodecDecodeException("Decoding Error", null, this.getClass(), null, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.cennavi.tpeg.core.Item#decoding()
	 */
	@Override
	public void decoding() {
		CoderLinks el = this.getClass().getAnnotation(CoderLinks.class);
		this.decoding(null, null);
	}

	/**
	 * 进行编码tpeg对象
	 * 
	 * @param id
	 * @param compile
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void doCompile(String id, Map<String, byte[]> compile) throws IllegalArgumentException, IllegalAccessException {
		Field field = encodingField.get(this.getClass().getName()).get(id);
		CoderItem ei = encodingEncodeItem.get(this.getClass().getName()).get(id);
		byte[] temp = null;
		if (ei.type() == CoderItemType.ITEM) {
			temp = compileItem(field);
		} else if (ei.type() == CoderItemType.LIST) {
			temp = compileList(field, ei);
		} else if (ei.type() == CoderItemType.LENGTH) {
			temp = compileLength(field, ei, compile);
		} else if (ei.type() == CoderItemType.CRC) {
			temp = compileCRC(field, ei, compile);
		} else if (ei.type() == CoderItemType.FLAGITEM_BITSWITCH || ei.type() == CoderItemType.FLAGITEM_SECLECTOR) {
			temp = compileFlagItem(field, ei, compile);
		}
		if (temp == null || temp.length <= 0) {
			logger.debug(StrHelper.getContent("Configuration Field is null,so do not compile the Field. Class Name : {0}, Field Name : {1}", new Object[]{
					this.getClass().getName(), field.getName()}));
			temp = new byte[0];
		}
		compile.put(id, temp);
	}

	/**
	 * new一个新的ITEM对象，包括baseItem和复合ITEM
	 * 
	 * @param fieldID
	 * @return
	 */
	protected Item newInstanceItem(String fieldID, boolean reNew) {
		String key = this.getClass().getName();
		Map<String, Field> fieldMap = encodingField.get(key);
		if (!fieldMap.containsKey(fieldID)) {
			throw new CodecBaseException("Class have not instanceof Field {0}", new Object[]{fieldID}, this.getClass(), null);
		}
		Field field = fieldMap.get(fieldID);
		Item item = null;
		try {
			item = (Item) field.get(this);
		} catch (IllegalArgumentException e1) {
			throw new CodecBaseException("set Class object to Field error. fieldID :  {0}", new Object[]{fieldID}, this.getClass(), null, e1);
		} catch (IllegalAccessException e1) {
			throw new CodecBaseException("set Class object to Field error. fieldID :  {0}", new Object[]{fieldID}, this.getClass(), null, e1);
		}
		if (item == null || reNew) {
			item = newInstanceItem(field);
			try {
				field.set(this, item);
			} catch (IllegalArgumentException e) {
				throw new CodecBaseException("set Class object to Field error. fieldID :  {0}", new Object[]{fieldID}, this.getClass(), null, e);
			} catch (IllegalAccessException e) {
				throw new CodecBaseException("set Class object to Field error. fieldID :  {0}", new Object[]{fieldID}, this.getClass(), null, e);
			}
		}

		return item;
	}

	/**
	 * new一个新的ITEM对象，包括baseItem和复合ITEM
	 * 
	 * @param field
	 * @return
	 */
	private Item newInstanceItem(Field field) {
		CoderItem ci = field.getAnnotation(CoderItem.class);
		Item item = null;
		if (ReflectUtil.isInstanceOf(field, NumberField.class)) {
			item = new NumberField(0, ci.length());
		} else if (ReflectUtil.isInstanceOf(field, NumberLongField.class)) {
			item = new NumberLongField(0L, ci.length());
		} else if (ReflectUtil.isInstanceOf(field, CRC.class)) {
			item = new CRC(ci.length());
		} else if (ReflectUtil.isInstanceOf(field, StringItem.class)) {
			item = new StringItem("", ci.length());
		} else if (ReflectUtil.isInstanceOf(field, ByteFlag.class)) {
			if (ci.type() == CoderItemType.FLAGITEM_BITSWITCH) {
				item = new ByteFlag(ci.length(), ByteFlag.TYPE_BITSWITCH);
			} else if (ci.type() == CoderItemType.FLAGITEM_SECLECTOR) {
				item = new ByteFlag(ci.length(), ByteFlag.TYPE_SELECTOR);
			}
		} else {
			// 直接实例化，并且对象不能有带参数的构造函数
			try {
				item = (Item) ReflectUtil.createObject(field.getType());
			} catch (ClassNotFoundException e) {
				throw new CodecBaseException("createObject error", null, this.getClass(), null, e);
			} catch (InstantiationException e) {
				throw new CodecBaseException("createObject error", null, this.getClass(), null, e);
			} catch (IllegalAccessException e) {
				throw new CodecBaseException("createObject error", null, this.getClass(), null, e);
			}
		}
		return item;
	}

	/**
	 * decode item对象 length==0时候，认为是子对象，需要set stream进一步处理
	 * length==CoderItem.ITEMLENGTH_NOMAXLENGTH时候，不进行处理，需要单独处理
	 * length==正数，则认为本类中可以处理
	 * length==负数并大于CoderItem.ITEMLENGTH_NOMAXLENGTH，不进行处理；
	 * 并抛出异常，item对象不会有负数出现（只有Item类型为length情况才会有负数）
	 * 
	 * @param id
	 * @param ei
	 * @param byteStream
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	private Item decompileItem(Field field, CoderItem ei, InputStream stream) throws IllegalArgumentException, IllegalAccessException, IOException {
		Item item = (Item) field.get(this);
		if (item == null) {
			item = newInstanceItem(field);
			field.set(this, item);
		}
		if (ReflectUtil.isInstanceOf(field, BaseItem.class)) {
			byte[] temp = new byte[0];
			validateStream(field, ei, stream);
			if (((item instanceof NumberField) || (item instanceof NumberLongField) || item instanceof StringItem || item instanceof ByteFlag) && ei.length() < 0) {// TODO
				String binstr = "";
				do {
					byte[] onebyte = new byte[1];
					stream.read(onebyte);
					binstr = NTransUtil.hexByteToLengthBinStr(onebyte);
					temp = StrHelper.integrateByteArray(temp, onebyte);
				} while (binstr.startsWith("1"));
			} else {
				temp = new byte[ei.length()];
				stream.read(temp);
			}
			((BaseItem) item).setEncodedArray(temp);
			temp = null;
		} else {
			item.setEncodedStream(stream);
		}
		item.decoding();
		return item;
	}

	/**
	 * 校验EncodedStream 是否已经超过限制
	 * 
	 * @param field
	 * @param ei
	 * @param stream
	 * @throws IOException
	 */
	private void validateStream(Field field, CoderItem ei, InputStream stream) throws IOException {
		if (ei.length() > stream.available()) {
			throw new CodecOutOfRangeException("Item length large then available length of Stream. Class Name : {0}, Field Name : {1}, Available Length : {2}", new Object[]{
					this.getClass().getName(), field.getName(), new Integer(stream.available())});
		}
	}

	/**
	 * 解码对象
	 * 
	 * @param id
	 *            : field ID
	 * @param stream
	 *            : Encoded Stream
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	private boolean doDecompile(String id, InputStream stream, List<Field> selectors) throws IllegalArgumentException, IllegalAccessException, IOException {
		Field field = encodingField.get(this.getClass().getName()).get(id);
		CoderItem ei = encodingEncodeItem.get(this.getClass().getName()).get(id);
		if (ei.length() <= CoderItem.ITEMLENGTH_NOMAXLENGTH) {
			return false; // 暂时不处理小于CoderItem.ITEMLENGTH_NOMAXLENGTH的对象
		}
		if (ei.type() == CoderItemType.LIST) {
			return false; // 已经进行配置 可以不需要
		} else {
			boolean thisIsFlag = false;
			if (ei.type() == CoderItemType.FLAGITEM_BITSWITCH || ei.type() == CoderItemType.FLAGITEM_SECLECTOR) {
				selectors.add(field);
				thisIsFlag = true;
			}
			int bflag = -3;
			if (selectors.size() > 0 && !thisIsFlag) {
				bflag = this.isItemInByteFlag(selectors, field);
			}

			boolean doDecompile = false;
			if (bflag == -3 || bflag == -1) {
				doDecompile = true;
			} else if (bflag == 0) {
				doDecompile = false;
			} else if (bflag == 1) {
				doDecompile = true;
			} else if (bflag == -2) {
				throw new CodecDecodeException("ByteFlag is not instance.", null, this.getClass(), ei);
			}
			if (doDecompile) {
				decompileItem(field, ei, stream);
			}
			return true;
		}
	}

	/**
	 * 判断某个字段是否被ByteFlag所影射
	 * 
	 * @param selectors
	 * @param fieldid
	 * @return 0:有存储字段的selector，但该对没有被存储 1:有存储字段的selector,该字段被存储
	 *         -1:有selector，但没有影射字段 -2:有selector，但该对象没有被实例化 -3:无selector
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private int isItemInByteFlag(List<Field> selectors, Field field) throws IllegalArgumentException, IllegalAccessException {
		int i = -3;
		for (Field selector : selectors) {
			ByteFlag bf = (ByteFlag) selector.get(this);
			if (bf == null) {
				i = -2;
				continue;
			}
			CoderItem ci = selector.getAnnotation(CoderItem.class);
			CoderItem fci = field.getAnnotation(CoderItem.class);
			String s[] = ci.dependent().dependentField();
			int index = StrHelper.searchString(s, fci.id());
			if (index == -1) {
				i = -1;
				continue;
			}
			int location = ci.dependent().dependentFieldDigit()[index];
			i = bf.getLocationValue(location) ? 1 : 0;
			break;
		}
		return i;
	}

	public static Map<String, Map<String, Field>> getEncodingField() {
		return encodingField;
	}

	public static Map<String, Class<?>> getCoderBeanMap() {
		return coderBeanMap;
	}

	public static Map<String, Map<String, CoderItem>> getEncodingEncodeItem() {
		return encodingEncodeItem;
	}

	private String itemName;

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	@Override
	public String toString() {
		CoderLinks el = this.getClass().getAnnotation(CoderLinks.class);
		if (el == null) {
			throw new CodecDecodeException("Object do not  Exist annotation CoderLinks.", null, this.getClass(), null);
		}
		String[] build = el.buildOrder().order();
		if (build == null || build.length == 0) {
			throw new CodecDecodeException("Object do not Exist annotation right CompileOrder or BuildOrder. ", null, this.getClass(), null);
		}
		StringBuffer str = new StringBuffer();
		str.append("{");
		try {
			for (String id : build) {
				Field field = encodingField.get(this.getClass().getName()).get(id);
				CoderItem ei = encodingEncodeItem.get(this.getClass().getName()).get(id);
				Object itemobj = field.get(this);
				if (itemobj == null) {
					str.append("\"" + id + "\"").append(":").append("\"\"").append(",");
					continue;
				}
				if (ei.type() == CoderItemType.ITEM || ei.type() == CoderItemType.LENGTH || ei.type() == CoderItemType.CRC || ei.type() == CoderItemType.FLAGITEM_BITSWITCH
						|| ei.type() == CoderItemType.FLAGITEM_SECLECTOR) {
					Item it = (Item) itemobj;
					String temp = it.toString();
					str.append("\"" + id + "\"").append(":");
					if (it instanceof BaseItem) {
						str.append(temp == null ? "\"\"" : "\"" + temp + "\"");
					} else {
						str.append(temp == null ? "\"\"" : temp);
					}
					str.append(",");

					// str.append("\""+id+"\"").append(":").append(temp==null?"''":temp).append(",");
				} else if (ei.type() == CoderItemType.LIST) {
					List<?> list = (List<?>) itemobj;
					str.append("\"" + id + "\"").append(":").append("[");
					for (Object obj : list) {
						str.append(obj.toString()).append(",");
					}
					str.append("]").append(",");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		str.append("}");
		return str.toString();

	}
	
	/**
	 * 把对象转换成可以被json或xml解析的对象
	 * </p>
	 * json可以直接使用
	 * xml需要配合使用TPEGConverter，调用xstream使用
	 * @return
	 */
	public Map<String,Object> toMapper(boolean useAnnotation) {
		CoderLinks el = this.getClass().getAnnotation(CoderLinks.class);
		if (el == null) {
			throw new CodecDecodeException("Object do not  Exist annotation CoderLinks.", null, this.getClass(), null);
		}
		String[] build = el.buildOrder().order();
		if (build == null || build.length == 0) {
			throw new CodecDecodeException("Object do not Exist annotation right CompileOrder or BuildOrder. ", null, this.getClass(), null);
		}
		Map<String,Object> res=new LinkedHashMap<String, Object>(); 
		try {
			for (String id : build) {
				Field field = encodingField.get(this.getClass().getName()).get(id);
				CoderItem ei = encodingEncodeItem.get(this.getClass().getName()).get(id);
				if(!ei.toMapper()&&useAnnotation){
					continue;
				}
				Object itemobj = field.get(this);
				if (itemobj == null) {
					res.put(id, "");
					continue;
				}
				if (ei.type() == CoderItemType.ITEM || ei.type() == CoderItemType.LENGTH || ei.type() == CoderItemType.CRC || ei.type() == CoderItemType.FLAGITEM_BITSWITCH
						|| ei.type() == CoderItemType.FLAGITEM_SECLECTOR) {
					
					if(itemobj instanceof BaseItem){
						BaseItem o=(BaseItem)itemobj;
						String temp = o.toString();
						if(!ei.toMapperClass().getName().equalsIgnoreCase(IMapperConverter.class.getName())){
							IMapperConverter converter=(IMapperConverter) intanceObjMap.get(ei.toMapperClass().getName());
							if(converter==null){
								converter=ei.toMapperClass().newInstance();
								intanceObjMap.put(ei.toMapperClass().getName(), converter);
							}
							try{
								temp=converter.converter(temp);
							}catch(Exception e){
							}
						}
						res.put(id, temp==null?"":temp);
					}else if(itemobj instanceof AbstractCoderItem){
						AbstractCoderItem o=(AbstractCoderItem)itemobj;
						res.put(id, o.toMapper(useAnnotation));
					}
				} else if (ei.type() == CoderItemType.LIST) {
					List<?> list = (List<?>) itemobj;
					List<Object> listobj=new ArrayList<Object>();
					res.put(id, listobj);
					for (Object obj : list) {
						if(obj instanceof BaseItem){
							BaseItem o=(BaseItem)obj;
							listobj.add(o.toString());
						}else if(obj instanceof AbstractCoderItem){
							AbstractCoderItem o=(AbstractCoderItem)obj;
							listobj.add(o.toMapper(useAnnotation));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return res;
		}
		return res;
	}
	
	private static Map<String,Object> intanceObjMap;

}
