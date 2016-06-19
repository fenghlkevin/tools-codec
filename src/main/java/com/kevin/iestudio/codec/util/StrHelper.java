package com.kevin.iestudio.codec.util;

import java.text.MessageFormat;

/**
 * @author fenghl
 * 
 *         <p>
 *         Title: 异常帮助类
 *         </p>
 *         <p>
 *         Description:
 *         </p>
 *         为异常模块提供一些基础支持函数<br>
 */
public class StrHelper {
	
	 public static String toUnicode(String str){
         char[]arChar=str.toCharArray();
         int iValue=0;
         String uStr="";
         for(int i=0;i<arChar.length;i++){
             iValue=(int)str.charAt(i);           
             if(iValue<=256){
               // uStr+="& "+Integer.toHexString(iValue)+";";
                 uStr+="\\"+Integer.toHexString(iValue);
             }else{
               // uStr+="&#x"+Integer.toHexString(iValue)+";";
                 uStr+="\\u"+Integer.toHexString(iValue);
             }
         }
         return uStr;
     }
	
	public static String getContent(String message, Object[] args) {
		if (message == null) {
			return null;
		}

		if (message.indexOf("{") == -1) {
			return message;
		}
		if(args==null){
			return message;
		}

		MessageFormat format = new MessageFormat(message);
		return (format.format(args));
	}
	
	/**
	 * 合并多个byte数组到一个数组中
	 * 
	 * @param array
	 * @return
	 */
	public static byte[] integrateByteArray(byte[]... array) {
		byte[] reArray = new byte[0];
		for (byte[] itbyte : array) {
			if(itbyte==null){
				continue;
			}
			byte[] tempList = new byte[reArray.length + itbyte.length];
			System.arraycopy(reArray, 0, tempList, 0, reArray.length);
			System.arraycopy(itbyte, 0, tempList, reArray.length, itbyte.length);
			reArray = null;
			reArray = tempList;
		}
		return reArray;
	}
	
	public static boolean isNumber(String args) {
		boolean isnumber = true;
		if (args==null||args.isEmpty()) {
			return false;
		}
		for (int i = 0; i < args.length(); i++) {
			if (Character.isDigit(args.charAt(i)) || args.charAt(i) == '.' || args.charAt(i) == '-') {
				continue;
			} else {
				isnumber = false;
			}
		}
		return isnumber;
	}
	
	public static String fillString(String args, int length, String value) {
		if (args.length() >= length) {
			return args.substring(args.length() - length, args.length());
		}

		StringBuffer str = new StringBuffer();
		for (int i = 0; i < length - args.length(); i++) {
			str.append(value);
		}
		str.append(args);
		return str.toString();
	}
	
	public static String fillString(String args, String value, int divisor) {
		int count=args.length()/8;
		if(args.length()%8!=0){
			count++;
		}
		return fillString(args, count*divisor, value);
	}
	
	public static int searchString(String[] args,String arg){
		if(arg==null||"".equalsIgnoreCase(arg)||args==null||args.length==0){
			return -1;
		}
		int index=-1;
		for(String temp:args){
			index++;
			if(arg.equalsIgnoreCase(temp)){
				return index;
			}
		}
		return -1;
	}
	
	public static void main(String[] args){
		System.out.println(toUnicode("我来厕所的"));
	}
	
	public static int getWordCount(String s)  
    {  
        int length = 0;  
        for(int i = 0; i < s.length(); i++)  
        {  
            int ascii = Character.codePointAt(s, i);  
            if(ascii >= 0 && ascii <=255)  
                length++;  
            else  
                length += 2;  
                  
        }  
        return length;  
          
    }
}
