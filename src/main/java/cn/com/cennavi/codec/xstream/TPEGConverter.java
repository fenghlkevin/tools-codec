/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2012 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package cn.com.cennavi.codec.xstream;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 配合AbstractCoderItem类的toMapper方法使用
 * </p>
 * 现存在bug:</p>
 * 1.unmarshal 方法没有实现</p>
 * 2.外层的map标签不能去掉，需要手动去掉或者增加一层标签，如：</p>
 * 
 * private static class TPEG{</p>
 *		Map<String,Object> tpegframe; </p>
 *	} </p>
 *</p>
 * how to use:</p>
 * </p>
 *	    Map<String,Object> mapper=tpeg.toJsonMapper();</p>
 *		TPEG t=new TPEG();//上面定义好的类</p>
 *		 t.tpegframe=mapper;</p>
 *		XStream xstream=new XStream();</p>
 *		xstream.registerConverter(new MyMapConverter(new DefaultMapper(TPEG.class.getClassLoader())));</p>
 *		xstream.alias("tpeg", TPEGA.class);</p>
 *		String xml=xstream.toXML(t);</p>
 *
 * @author fengheliang
 *
 */
public class TPEGConverter extends AbstractCollectionConverter {

	public TPEGConverter(Mapper mapper) {
		super(mapper);
	}

	public boolean canConvert(Class type) {
		return type.equals(HashMap.class) || type.equals(Hashtable.class) || type.getName().equals("java.util.LinkedHashMap")
				|| type.getName().equals("java.util.concurrent.ConcurrentHashMap") || type.getName().equals("sun.font.AttributeMap");
	}

	@SuppressWarnings("rawtypes")
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Map map = (Map) source;
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String key = String.valueOf(entry.getKey());
			Object obj = entry.getValue();

			if (obj instanceof String) {
				writer.startNode(key);
				writer.setValue(String.valueOf(obj));
				writer.endNode();
			} else {
				if (obj instanceof List) {
					writer.startNode(key);
					List tempList = (List) obj;
					for (int i = 0; i < tempList.size(); i++) {
						Object tempObj = tempList.get(i);
						String name=key.substring(0,key.length()-1);
						//ExtendedHierarchicalStreamWriterHelper.startNode(writer, "array"+"_"+i, String.class);
						ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, String.class);
						context.convertAnother(tempObj);
						writer.endNode();
					}
					writer.endNode();
				} else {
					//writer.startNode(key);
					ExtendedHierarchicalStreamWriterHelper.startNode(writer, key, String.class);
					context.convertAnother(obj);
					writer.endNode();
				}

			}

		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		// TODO Auto-generated method stub
		return null;
	}

}
