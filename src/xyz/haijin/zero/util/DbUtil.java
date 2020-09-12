package xyz.haijin.zero.util;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public final class DbUtil {
	public static final Map<String,Map<String,String>> CONTEXT_MAP = new HashMap<>();
	private DbUtil() {}
	static {
		init();
	}

	
	private static void init() {
		InputStream instream = DbUtil.class.getClassLoader().getResourceAsStream("conf/Conection.conf");   
		File file = new File("./conf/Conection.conf");
		String xmlString = "";
		if(!file.exists()) {
			xmlString = XmlUtils.readEntyFile(instream);
		}else {
			xmlString = XmlUtils.readEntyFile("./conf/Conection.conf");
		}
		XmlTipUtil keyWordTipUtil = new XmlTipUtil();
		keyWordTipUtil.setKeyWord("site");
		keyWordTipUtil.returnKeyWordTipHtml(xmlString);
		for(String str : keyWordTipUtil.getKeyWordStrs()) {
			Map<String,String> subMap = new HashMap<>();
			String name = XmlUtils.getStringBetweenKeys(str, "<name>", "</name>");
			String driver = XmlUtils.getStringBetweenKeys(str, "<driver>", "</driver>");
			String url = XmlUtils.getStringBetweenKeys(str, "<url>", "</url>");
			String username = XmlUtils.getStringBetweenKeys(str, "<username>", "</username>");
			String password = XmlUtils.getStringBetweenKeys(str, "<password>", "</password>");
			String use = XmlUtils.getStringBetweenKeys(str, "<use>", "</use>");
			subMap.put("name", name);
			subMap.put("driver", driver);
			subMap.put("url", url);
			subMap.put("username", username);
			subMap.put("password", password);
			subMap.put("use", use);
			if("true".equals(subMap.get("use"))) {
				CONTEXT_MAP.put(subMap.get("name"), subMap);
			}
		}
	}
	
	public static Set<String> getNameSet(){
		return CONTEXT_MAP.keySet();
	}
	
	/**
	 * 拿到对应的数据库链接数据
	 * @param novelSiteEnum
	 * @return
	 */
	public static Map<String,String> getContext(String name){
		return CONTEXT_MAP.get(name);
	}
	
	public static void main(String[] args) {
		System.out.println(DbUtil.getNameSet());
	}
}
