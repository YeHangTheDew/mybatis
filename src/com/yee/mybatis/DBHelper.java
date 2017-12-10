package com.yee.mybatis;

import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vera
 */
public class DBHelper {
	private DataSource dataSource;
	
	public DBHelper(DataSource dataSource){
		this.dataSource=dataSource;
		try {
			Class.forName( dataSource.getDriver() );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 获取数据库连接的方法
	 * @return
	 */
	private Connection getConnection(){
		Connection con=null;
		try {
			con=DriverManager.getConnection(dataSource.getUrl(),dataSource.getUser(),dataSource.getPassword() );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * 关闭资源
	 * 
	 * @param con
	 * @param pstmt
	 * @param rs
	 */
	private void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 给预编译语句中的占位符赋值
	 * 
	 * @param pstmt
	 * @param params
	 */
	public void setParams(PreparedStatement pstmt, Object... params) {
		if (params != null && params.length > 0) {
			for (int i = 0, len = params.length; i < len; i++) {
				try {
					pstmt.setObject(i + 1, params[i]);
				} catch (SQLException e) {
					System.out.println("第" + (i + 1) + "个注值失败..." + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 给预编译语句中的占位符赋值
	 * 
	 * @param pstmt
	 * @param params
	 */
	public void setParams(PreparedStatement pstmt, List<Object> params) {
		if (params != null && params.size() > 0) {
			for (int i = 0, len = params.size(); i < len; i++) {
				try {
					pstmt.setObject(i + 1, params.get(i));
				} catch (SQLException e) {
					System.out.println("第" + (i + 1) + "个注值失败..." + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取结果集中的列名
	 * 
	 * @param rsmd
	 * @param count
	 * @return
	 */
	public String[] getColumnNames(ResultSetMetaData rsmd, int count) {
		String[] colNames = null;
		if (rsmd != null) {
			colNames = new String[count];
			try {
				for (int i = 0; i < count; i++) {
					colNames[i] = rsmd.getColumnName(i + 1).toLowerCase();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return colNames;
	}

	/**
	 * 获取结果集中的列名
	 * 
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	public String[] getColumnNames(ResultSet rs) throws Exception {
		ResultSetMetaData rsmd = rs.getMetaData();
		int len = rsmd.getColumnCount(); // 获取结果集中列 的数量

		String[] colNames = new String[len];

		// 循环取出每个列的列名存放到colNames数组中
		for (int i = 0; i < len; i++) {
			colNames[i] = rsmd.getColumnName(i + 1).toLowerCase(); // 将每个列名全部转化为小写字母
		}

		return colNames;
	}

	/**
	 * 添加操作
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public int update(String sql, Object... params) {
		int result = 0;
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 获取连接
			con = this.getConnection();

			// 执行预编译语句
			pstmt = con.prepareStatement(sql);

			// 给预编译的SQL语句赋值
			this.setParams(pstmt, params);

			// 执行语句
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.close(con, pstmt, null);
		}
		return result;
	}

	/**
	 * 添加操作
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public int update(String sql, List<Object> params) {
		int result = 0;
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 获取连接
			con = this.getConnection();

			// 执行预编译语句
			pstmt = con.prepareStatement(sql);

			// 给预编译的SQL语句赋值
			this.setParams(pstmt, params);

			// 执行语句
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.close(con, pstmt, null);
		}
		return result;
	}

	/**
	 * 查询
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> find(String sql, Object... params) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 获取连接
			con = this.getConnection();

			// 执行预编译语句
			pstmt = con.prepareStatement(sql);

			// 给预编译语句中的占位符赋值
			this.setParams(pstmt, params);

			// 获取结果集
			rs = pstmt.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();

			String[] colNames = this.getColumnNames(rsmd, colCount);

			Map<String, Object> map = null;

			Object obj = null;
			String colType = null;

			while (rs.next()) { //// 每循环一次就是一行记录，我们需要将这行记录中的值存到一个map中，以列名为键，以当前列的值为值
				map = new HashMap<String, Object>();
				for (String col : colNames) {
					obj = rs.getObject(col); // 判断当前这个列的值的类型

					if (obj != null) {
						colType = obj.getClass().getSimpleName();

						if ("BLOB".equals(colType)) {
							Blob blob = rs.getBlob(col);

							// 将Blob变成一个字节数组
							byte[] bt = blob.getBytes(1, (int) blob.length());

							map.put(col, bt);
						} else {
							map.put(col, obj);
						}
					} else {
						map.put(col, obj);
					}
				}
				// 当for循环结束，说明这一行数据已经读完并存到了map中，接下来我们需要将这行数据存到list中
				list.add(map);
				map = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(con, pstmt, rs);
		}
		return list;
	}

	/**
	 * 查询
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String, String>> findStr(String sql, Object... params) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 获取连接
			con = this.getConnection();

			// 执行预编译语句
			pstmt = con.prepareStatement(sql);

			// 给预编译语句中的占位符赋值
			this.setParams(pstmt, params);

			// 获取结果集
			rs = pstmt.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();

			String[] colNames = this.getColumnNames(rsmd, colCount);

			Map<String, String> map = null;

			while (rs.next()) { //// 每循环一次就是一行记录，我们需要将这行记录中的值存到一个map中，以列名为键，以当前列的值为值
				map = new HashMap<String, String>();

				for (String col : colNames) {
					map.put(col, rs.getString(col));
				}
				// 当for循环结束，说明这一行数据已经读完并存到了map中，接下来我们需要将这行数据存到list中
				list.add(map);
				map = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.close(con, pstmt, rs);
		}
		return list;
	}

	/**
	 * 查询
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> find(String sql, List<Object> params) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 获取连接
			con = this.getConnection();

			// 执行预编译语句
			pstmt = con.prepareStatement(sql);

			// 给预编译语句中的占位符赋值
			this.setParams(pstmt, params);

			// 获取结果集
			rs = pstmt.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();

			String[] colNames = this.getColumnNames(rsmd, colCount);

			Map<String, Object> map = null;

			Object obj = null;
			String colType = null;

			while (rs.next()) { //// 每循环一次就是一行记录，我们需要将这行记录中的值存到一个map中，以列名为键，以当前列的值为值
				map = new HashMap<String, Object>();
				for (String col : colNames) {
					obj = rs.getObject(col); // 判断当前这个列的值的类型

					if (obj != null) {
						colType = obj.getClass().getSimpleName();

						if ("BLOB".equals(colType)) {
							Blob blob = rs.getBlob(col);

							// 将Blob变成一个字节数组
							byte[] bt = blob.getBytes(1, (int) blob.length());

							map.put(col, bt);
						} else {
							map.put(col, obj);
						}
					} else {
						map.put(col, obj);
					}
				}
				// 当for循环结束，说明这一行数据已经读完并存到了map中，接下来我们需要将这行数据存到list中
				list.add(map);
				map = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(con, pstmt, rs);
		}
		return list;
	}

	/**
	 * 查询单行数据
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public Map<String, Object> findSingle(String sql, Object... params) {
		Map<String, Object> map = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 获取连接
			con = this.getConnection();

			// 执行预编译语句
			pstmt = con.prepareStatement(sql);

			// 给预编译语句中的占位符赋值
			this.setParams(pstmt, params);

			// 获取结果集
			rs = pstmt.executeQuery();

			if (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int colCount = rsmd.getColumnCount();

				String[] colNames = this.getColumnNames(rsmd, colCount);

				map = new HashMap<String, Object>();

				Object obj = null;
				String colType = null;

				for (String col : colNames) {
					obj = rs.getObject(col); // 判断当前这个列的值的类型

					if (obj != null) {
						colType = obj.getClass().getSimpleName();

						if ("BLOB".equals(colType)) {
							Blob blob = rs.getBlob(col);

							// 将Blob变成一个字节数组
							byte[] bt = blob.getBytes(1, (int) blob.length());

							map.put(col, bt);
						} else {
							map.put(col, obj);
						}
					} else {
						map.put(col, obj);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(con, pstmt, rs);
		}
		return map;
	}

	/**
	 * 查询单行数据,用字符串存储
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public Map<String, String> findSingleByStr(String sql, Object... params) {
		Map<String, String> map = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 获取连接
			con = this.getConnection();

			// 执行预编译语句
			pstmt = con.prepareStatement(sql);

			// 给预编译语句中的占位符赋值
			this.setParams(pstmt, params);

			// 获取结果集
			rs = pstmt.executeQuery();

			if (rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int colCount = rsmd.getColumnCount();

				String[] colNames = this.getColumnNames(rsmd, colCount);

				map = new HashMap<String, String>();

				String obj = null;
				String colType = null;

				for (String col : colNames) {
					obj = rs.getString(col); // 判断当前这个列的值的类型

					if (obj != null) {
						colType = obj.getClass().getSimpleName();

						if ("BLOB".equals(colType)) {
							Blob blob = rs.getBlob(col);

							// 将Blob变成一个字节数组
							byte[] bt = blob.getBytes(1, (int) blob.length());

							map.put(col, String.valueOf(bt));
						} else {
							map.put(col, obj);
						}
					} else {
						map.put(col, obj);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(con, pstmt, rs);
		}
		return map;
	}

	/**
	 * 查询
	 * @param sql
	 * @param c
	 * @param params
	 * @return
	 */
	public <T> List<T> find(String sql, Class<T> c, Object ...params){
		List<T> list=new ArrayList<T>();
		Connection con=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		try {
			con=this.getConnection();
			pstmt=con.prepareStatement(sql);
			this.setParams(pstmt,params);
			rs=pstmt.executeQuery();
			//获取结果集中的列名
			String[] colNames = this.getColumnNames(rs);

			//取出给定类所有settet方法
			List<Method> methods=this.getSetter(c);

			T t=null;
			Object obj;
			String colName=null;  //数据库中的列名
			String mname=null;  //方法名
			String typeName=null; //类型名

			while(rs.next()){
				t=c.newInstance();  //new UserInfo();
				for(String col:colNames){
					colName="set"+col;  //setusid
					for(Method m:methods){
						mname=m.getName();  //setUsid
						if(mname.equalsIgnoreCase(colName)){  //说明找到了对应的方法
							obj=rs.getObject(col);
							if(obj!=null){  //如果不为空，则获取这个对象的类型，如果为空则说明这个属性为空，不需要管
								typeName=obj.getClass().getSimpleName();
								if("BigDecimal".equals(typeName)){   //number(8)  number(10,2)
									try{
										//激活这个方法注值
										m.invoke(t, rs.getInt(col));
									}catch(Exception e){
										m.invoke(t, rs.getDouble(col));
									}
								}else if("Integer".equals(typeName)){
									m.invoke(t, rs.getInt(col));
								}else{
									m.invoke(t, String.valueOf(obj));
								}
							}
							break;
						}
					}
				}
				list.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			this.close(con, pstmt, rs);
		}

		return list;		

	}

	/**
	 * 获取指定类所有setter方法
	 * @param c
	 * @return
	 */
	public List<Method> getSetter(Class<?> c){
		Method[] methods=c.getMethods();
		List<Method> list=new ArrayList<Method>();
		for(Method method:methods){
			if(method.getName().startsWith("set")){
				list.add(method);
			}
		}
		return list;
	}

	/**
	 * 获取记录数
	 * @param sql
	 * @param params
	 * @return
	 */
	public int getTotal(String sql,Object ...params){
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result=0;

		try {
			// 获取连接
			con = this.getConnection();

			// 执行预编译语句
			pstmt = con.prepareStatement(sql);

			// 给预编译语句中的占位符赋值
			this.setParams(pstmt, params);

			// 获取结果集
			rs = pstmt.executeQuery();

			if (rs.next()) {
				result=rs.getInt("total");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(con, pstmt, rs);
		}
		return result;
	}

	public boolean updates(List<String> sqls,List<List<Object>> params){
		boolean bl=false;
		Connection con=null;
		PreparedStatement pstmt=null;
		try {
			if(sqls!=null && sqls.size()>0){
				con=this.getConnection();
				//关闭事务的自动提交
				con.setAutoCommit(false);
				
				for(int i=0,len=sqls.size();i<len;i++){
					pstmt=con.prepareStatement(sqls.get(i));
					this.setParams(pstmt, params.get(i));
					pstmt.executeUpdate();
				}
				
				//如果所有语句执行完成都没有错误，则提交事务
				con.commit();
				return true;
			}
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally{
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.close(con, pstmt,null);
		}
		return bl;
	}
}
