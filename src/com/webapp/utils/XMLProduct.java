package com.webapp.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.webapp.model.AppMarketListInfo;


public class XMLProduct extends DefaultHandler {
	private List<AppMarketListInfo> information;
	private AppMarketListInfo appMarketListInfo;
	private StringBuffer buffer=new StringBuffer();
	
	public XMLProduct(List<AppMarketListInfo> information){
		this.information=information;
	}
	
	public List<AppMarketListInfo> getInformation() {
		return information;
	}
	
	
	public List<AppMarketListInfo> getInformation(InputStream is) throws Exception, SAXException{
		SAXParserFactory factory=SAXParserFactory.newInstance();
		SAXParser parser=factory.newSAXParser();
		//SaxParseService handler=new SaxParseService();
		XMLProduct handler=new XMLProduct(information);
		parser.parse(is,handler);
		return handler.getInformation();
	}
	
	@Override
	public void characters(char[] ch,int start,int length) throws SAXException{
		buffer.append(ch,start,length);
		super.characters(ch, start, length);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if(qName.equals("package")){
			information.add(appMarketListInfo);
			buffer.setLength(0);
		}else if(qName.equals("name")){
			appMarketListInfo.setAppName(buffer.toString().trim());
			buffer.setLength(0);
		}else if(qName.equals("imageurl")){
			appMarketListInfo.setImageurl(buffer.toString().trim());
			buffer.setLength(0);
		}else if(qName.equals("size")){
			appMarketListInfo.setSize(Integer.parseInt(buffer.toString().trim()));
			buffer.setLength(0);
		}else if(qName.equals("description")) {
			appMarketListInfo.setShortDescription(buffer.toString().trim());
			buffer.setLength(0);
		}else if(qName.equals("download")) {
			appMarketListInfo.setDownloadurl(buffer.toString().trim());
			buffer.setLength(0);
		}else if(qName.equals("version")) {
			appMarketListInfo.setVersion(buffer.toString().trim());
			buffer.setLength(0);
		}
		super.endElement(uri, localName, qName);
	}
	
	@Override
	public void startDocument() {
		//开始分析XML文档，创建List对象用于保存分析完的Product对象
		//information=new ArrayList<AppMarketListInfo>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equals("package")){
			appMarketListInfo=new AppMarketListInfo();
		}
		super.startElement(uri, localName, qName, attributes);
	}
}
