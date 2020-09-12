package xyz.haijin.zero.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class XmlUtils {

	// 截取多个指定字符之间�??短的字符�??
	protected static String getShortStringBetweenKeys(String str, String first, String[] secondKeys) {
		return getShortStringBetweenKeys(str, first, secondKeys, true);
	}

	// 截取多个指定字符之间�??短的字符�??
	protected static String getShortStringBetweenLastKeys(String str, String first, String[] secondKeys) {
		return getShortStringBetweenKeys(str, first, secondKeys, false);
	}

	// 截取多个指定字符之间�??短的字符�??
	protected static String getShortStringBetweenKeys(String str, String first, String[] secondKeys, boolean asc) {
		if(str != null && first != null && secondKeys != null) { // 判断�??有参数不为空
			// 判断第一个查找串位置
			int firstIndex = -1;
			if(asc) {
				firstIndex = str.indexOf(first);
			} else {
				firstIndex = str.lastIndexOf(first);
			}
			if(firstIndex != -1) {
				// 遍历第二组查找串位置�??小的�??
				int secondIndex = -1;
				firstIndex += first.length();
				for(int i = 0; i < secondKeys.length; i++) {
					int index = str.indexOf(secondKeys[i], firstIndex);
					if(index != -1 && (secondIndex == -1 || index < secondIndex)) {
						secondIndex = index;
					}
				}
				if(secondIndex == -1) {
					secondIndex = str.length();
				}
				str = str.substring(firstIndex, secondIndex);
			}
		}
		return str;
	}

	// 截取指定字符之间字符�??
	protected static String getStringBetweenKeys(String str, String first, String second) {
		if(str != null && first != null && second != null) { // 判断�??有参数不为空
			// 判断第一个查找串位置
			int firstIndex = str.indexOf(first);
			if(firstIndex != -1) {
				firstIndex += first.length();
				int secondIndex = str.indexOf(second, firstIndex);
				if(secondIndex == -1) {
					secondIndex = str.length();
				}
				str = str.substring(firstIndex, secondIndex);
			} else {
				str = "";
			}
		}
		return str;
	}

	// 截取指定字符之间字符�??
	protected static String getStringBetweenLastKeys(String str, String first, String second) {
		if(str != null && first != null && second != null) { // 判断�??有参数不为空
			// 判断第一个查找串位置
			int firstIndex = str.lastIndexOf(first);
			if(firstIndex != -1 && first.equals(second)) {
				firstIndex = str.lastIndexOf(first, firstIndex - first.length());
			}
			if(firstIndex != -1) {
				firstIndex += first.length();
				int secondIndex = str.indexOf(second, firstIndex);
				if(secondIndex == -1) {
					secondIndex = str.length();
				}
				str = str.substring(firstIndex, secondIndex);
			}
		}
		return str;
	}

	// 截取指定字符后的字符�??
	protected static String getStringAfterKey(String str, String key) {
		return getStringAfterKey(str, key, 0);
	}

	// 截取指定字符后的字符�??
	protected static String getStringAfterKey(String str, String key, int fromIndex) {
		if(str != null && key != null) {
			str = str.substring(str.indexOf(key, fromIndex) + key.length());
		}
		return str;
	}

	// 截取指定�??后一个字符后的字符串
	protected static String getStringAfterLastKey(String str, String key) {
		if(str != null && key != null) {
			str = str.substring(str.lastIndexOf(key) + key.length());
		}
		return str;
	}

	// 截取指定字符前的字符�??
	protected static String getStringBeforeKey(String str, String key) {
		if(str != null && key != null && str.indexOf(key) != -1) {
			str = str.substring(0, str.indexOf(key));
		}
		return str;
	}
	protected static String getStringBeforeKeys(String str, String ... keys) {
		// 遍历查找串位置最小的�??
		int firstIndex = -1;
		for(int i = 0; i < keys.length; i++) {
			int index = str.indexOf(keys[i]);
			if(index != -1 && (firstIndex == -1 || index < firstIndex)) {
				firstIndex = index;
			}
		}
		if(firstIndex == -1) {
			firstIndex = str.length();
		}
		str = str.substring(0, firstIndex);
		return str;
	}

	protected static String getStringBeforeLastKey(String str, String key) {
		if(str != null && key != null && str.lastIndexOf(key) != -1) {
			str = str.substring(0, str.indexOf(key));
		}
		return str;
	}
	
	//读取整个文件的字符串
	protected static String readEntyFile(String fileName) {
		String childPath = null;
		try{
			StringBuffer buffer = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName),"utf-8");
			BufferedReader br = new BufferedReader(isr); 
			String str;
			while((str = br.readLine()) != null){
				buffer.append(str);
				buffer.append('\r');
			}
			childPath = buffer.toString();
			br.close();
			isr.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return childPath;
	}
	
	//读取整个文件的字符串
		protected static String readEntyFile(InputStream instream) {
			String childPath = null;
			try{
				StringBuffer buffer = new StringBuffer();
				InputStreamReader isr = new InputStreamReader(instream,"utf-8");
				BufferedReader br = new BufferedReader(isr); 
				String str;
				while((str = br.readLine()) != null){
					buffer.append(str);
					buffer.append('\r');
				}
				childPath = buffer.toString();
				br.close();
				isr.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			return childPath;
		}

}
