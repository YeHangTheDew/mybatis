package com.yee.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

/*
 * 1.读取xml配置文件
 * 获取配置文件中的
 * DataSource对象
 * mappers下的映射文件路径
 */
public class MyBatisConfig {
	private DataSource dataSource;
	private List<String> mappers=new ArrayList<String>();
	
	public MyBatisConfig(String config){
		//读取并解析这个配置文件
		try {
			this.readXml(config);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void readXml(String config) throws DocumentException{
		SAXReader saxReader=new SAXReader();
//		URL url=this.getClass().getClassLoader().getResource(config);
//		Document doc=saxReader.read(url);
		Document doc=saxReader.read(this.getClass().getClassLoader().getResourceAsStream(config));
		
		// "//"当前的所有dataSource元素(不管位置)下的property 
		XPath xpath=doc.createXPath("//dataSource/property");
		List<Element> properties=xpath.selectNodes(doc);
		
		dataSource=new DataSource();
		String pname;
		//根据元素节点的属性值注入dataSource对象中
		for(Element el:properties){
			pname=el.attributeValue("name");
			if("driver".equals(pname)){
				dataSource.setDriver( el.attributeValue("value") );
			}else if("url".equals(pname)){
				dataSource.setUrl( el.attributeValue("value") );
			}else if("user".equals(pname)){
				dataSource.setUser( el.attributeValue("value") );
			}else if("password".equals(pname)){
				dataSource.setPassword( el.attributeValue("value") );
			}
		}
		
		xpath=doc.createXPath("//mappers/mapper");
		List<Element> list=xpath.selectNodes(doc);
		for(Element el:list){
			mappers.add(el.attributeValue("resource"));
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public List<String> getMappers() {
		return mappers;
	}
}
