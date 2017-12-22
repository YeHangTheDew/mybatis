package com.yee.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

/**
 * 2.读取并解析.xml batis xml文件 
 * SqlSessionFactory实体类
 * 1)获取MyBatisConfig对象 获取配置文件中的
 *  DataSource(DataSource [driver=oracle.jdbc.driver.OracleDriver, url=jdbc:oracle:thin:@127.0.0.1:1521:orcl, user=vera, password=a])
 *  和
 *  .xml文件路径(com/yee/mybatis/bean/DeptMapper.xml)
 * 2)mapperInfo 对应一个操作 <select></select>
 * 	用键值对存储  方便后续操作 根据id名处理 addUser findAll
 */
public class SqlSessionFactory {
	private MyBatisConfig config;
	private Map<String,MapperInfo> mapperInfos=new HashMap<String,MapperInfo>();
	
	public SqlSessionFactory(MyBatisConfig config){
		this.config=config;
		
		try {
			parseXml();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void parseXml() throws DocumentException{
		List<String> mappers=config.getMappers();
		//new过不会为null 判断时先判空 再看内容是否为空
		if(mappers!=null && mappers.size()>0){
			SAXReader saxReader=new SAXReader();
			for(String mapper:mappers){
				Document doc=saxReader.read(this.getClass().getClassLoader().getResourceAsStream(mapper));
				
				XPath xpath=doc.createXPath("//mapper/*");
				List<Element> ops=xpath.selectNodes(doc);
				MapperInfo info=null;
				String opname=null;
				for(Element el:ops){
					info=new MapperInfo();
					opname=el.getName();   //获取操作类型 即元素名 insert 
					if("select".equals(opname)){
						info.setUpdate(false); //不为更新操作
					}
					info.setParameterType(el.attributeValue("parameterType"));
					info.setResultType(el.attributeValue("resultType"));
					info.setSql(el.getTextTrim());
					mapperInfos.put(el.attributeValue("id"),info); //addDept findAll
//					System.out.println(el.getName());   //insert
//					System.out.println(el.attributeValue("id")); //addDept
//					System.out.println(el.attributeValue("parameterType"));  //com.yee.mybatis.bean.Dept
//					System.out.println(el.attributeValue("resultType"));  //null
//					System.out.println(el.getTextTrim()); //insert into dept values(#{deptno},#{dname},#{loc})
				}
				System.out.println(mapperInfos);
			}
		}
	}

	public MyBatisConfig getConfig() {
		return config;
	}

	public Map<String, MapperInfo> getMapperInfos() {
		return mapperInfos;
	}
}
