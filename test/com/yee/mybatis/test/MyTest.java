package com.yee.mybatis.test;

import java.util.List;

import org.junit.Test;

import com.yee.mybatis.MyBatisConfig;
import com.yee.mybatis.SqlSession;
import com.yee.mybatis.SqlSessionFactory;
import com.yee.mybatis.bean.Dept;

public class MyTest {
	@Test
	public void test(){
		// 配置文件放在src目录下
		MyBatisConfig myBatisConfig=new MyBatisConfig("mybatis-config.xml");
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactory(myBatisConfig);
		SqlSession session=new SqlSession(sqlSessionFactory);
		List<Dept> depts=session.selectList("findAll");
		for(Dept dept:depts){
			System.out.println(dept);
		}
	}
	
	@Test
	public void test1(){
		// 配置文件放在src目录下
		MyBatisConfig myBatisConfig=new MyBatisConfig("mybatis-config.xml");
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactory(myBatisConfig);
		SqlSession session=new SqlSession(sqlSessionFactory);
		int result=session.update("addDept",new Dept(60,"财务部","8605"));
		System.out.println(result);
	}
}
