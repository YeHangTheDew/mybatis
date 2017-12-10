package com.yee.mybatis;

import java.util.List;

/**
 * 3.
 * 1)通过SqlSessionFactory工厂 获取
 * DataSource 和 DBHelper
 * 
 * 2)外界调用的接口
 *  session.selectList("findAll",params);
 *  session.uplate("addDept",new Dept());
 */
public class SqlSession {
	private SqlSessionFactory factory;
	private DBHelper db;

	public SqlSession(SqlSessionFactory factory){
		this.factory=factory;
		db=new DBHelper(factory.getConfig().getDataSource());
	}

	/**
	 * 按要求返回查找的数据
	 * @param sqlId
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T>List<T> selectList(String sqlId,Object ... params){
		//获取sql 
		//获取参数列表
		MapperInfo mapperInfo=factory.getMapperInfos().get(sqlId); //键值对存储可以快速获取 addDept findAll 和 对应的属性 parameterType resultMap

		if(mapperInfo!=null){
			try {
				String sql=mapperInfo.getSql();
				if(mapperInfo.isUpdate()){//如果是更新
					return null; //selectList 不匹配(事实上不会出现) 最好抛出一个异常
				}else{  //为查询
					//根据类路径加载类  com.yee.mybatis.bean.Dept
					String className=mapperInfo.getResultType();
					Class c=Class.forName(className);
					//调用数据层返回结果
					return db.find(sql, c, params);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 更新
	 * @param sqlId
	 * @param obj
	 * @return
	 */
	public int update(String sqlId,Object obj){
		int result=0;
		//获取<update></update> 里边的属性值
		MapperInfo mapperInfo=factory.getMapperInfos().get("sqlId"); //addDept
		
		//获取sql语句  重！！ #{usid} #{pwd}
		if(mapperInfo!=null){
			String sql=mapperInfo.getSql();
			System.out.println(sql);
			System.out.println(obj);
		}
		return result;
	}
}
