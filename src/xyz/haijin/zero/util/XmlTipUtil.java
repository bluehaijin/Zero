package xyz.haijin.zero.util;

import java.util.ArrayList;
import java.util.List;


public class XmlTipUtil {
	private List<String> keyWordStrs = new ArrayList<String>();
	
	public List<String> getKeyWordStrs() {
		return keyWordStrs;
	}

	private  String keyWord = "div";
	
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	
	protected  String getkeyWordStr(String html) {
		return getkeyWordStr(html, "<"+keyWord+">", "</"+keyWord+">");
	}
	
	protected  String getkeyWordStr(String html,String end_html) {
		return getkeyWordStr(html, "<"+keyWord+">", end_html);
	}
	
	protected  String getkeyWordStr(String html,String start_html,String end_html) {
		String div = "";
		if(html.contains("<"+keyWord+" ")) {
			start_html = "<"+keyWord+" "+XmlUtils.getStringBetweenKeys(html, "<"+keyWord+" ", ">")+">";
			if(XmlUtils.getStringBeforeKey(html, start_html).contains("<"+keyWord+">")) {
				start_html = "<"+keyWord+">";
			}
		}
		div = XmlUtils.getStringBetweenKeys(html, start_html, end_html);
		if(div.contains("<"+keyWord+" ")||div.contains("<"+keyWord+">")) {
			div = getkeyWordStr(div,start_html,end_html);
		}
		if(!div.contains("<"+keyWord+"")) {
			div = start_html+div+"</"+keyWord+">";
		}
		return div;
	}
	
	protected String getkeyWordEntity(String minDiv) {
		String div = new String();
		String keyValue = "<"+keyWord+">";
		if(minDiv.contains("<"+keyWord+" ")) {
			keyValue = "<"+keyWord+" "+XmlUtils.getStringBetweenKeys(minDiv, "<"+keyWord+" ", ">")+">";
		}
		div = XmlUtils.getStringBetweenKeys(minDiv, keyValue, "</"+keyWord+">");
		return div;
	}
	
	private String getRemoveTrim(String html) {
		while (html.contains("\r")) {
			html = html.replaceAll("\r", "");
		}
		while (html.contains("\n")) {
			html = html.replaceAll("\n", "");
		}
		while (html.contains("\t")) {
			html = html.replaceAll("\t", "");
		}
		while (html.contains("  ")) {
			html = html.replace("  ", " ");
		}
		return html.trim();
	}
	
	public void returnKeyWordTipHtml(String html) {
		keyWordStrs.add(getkeyWordEntity(getkeyWordStr(html)));
		String newhtml = XmlUtils.getStringBeforeKey(html, getkeyWordStr(html))+XmlUtils.getStringAfterKey(html, getkeyWordStr(html));
		String keyValue = "<"+keyWord+">";
		if( html.contains("<"+keyWord+" ")) {
			keyValue = "<"+keyWord+" "+XmlUtils.getStringBetweenKeys(html, "<"+keyWord+" ", ">")+">";
				if(XmlUtils.getStringBeforeKey(html, keyValue).contains("<"+keyWord+">")) {
					keyValue = "<"+keyWord+">";
				}
		}
		String check = getRemoveTrim(newhtml);
		if(!"".equals(check)) {
			String lastDivContent = XmlUtils.getStringAfterKey(newhtml, keyValue);
			if(lastDivContent.contains("<"+keyWord+"")) {
				returnKeyWordTipHtml(newhtml);
			}else {
				keyWordStrs.add(getkeyWordEntity(getkeyWordStr(newhtml)));
			}
		}
	}
	
	public static void main(String[] args) {
		String html = "<site>\r\n" + 
				"	<name>weblog</name>\r\n" + 
				"	<driver>com.mysql.cj.jdbc.Driver</driver>\r\n" + 
				"	<url>jdbc:mysql://49.233.196.64:3307/d_weblog?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Hongkong</url>\r\n" + 
				"	<username>root</username>\r\n" + 
				"	<password>liuhaijin</password>\r\n" + 
				"	<use>true</use>\r\n" + 
				"</site>";
//		System.out.println(html);
		XmlTipUtil keyWordTipUtil = new XmlTipUtil();
		keyWordTipUtil.setKeyWord("site");
		keyWordTipUtil.returnKeyWordTipHtml(html);
		System.out.println(keyWordTipUtil.getKeyWordStrs().size());
		for(String str : keyWordTipUtil.getKeyWordStrs()) {
			System.out.println(str);
//			System.out.println(TextUtils.getStringBetweenKeys(str, "title=\"", "\">"));
		}
	}
	
}
