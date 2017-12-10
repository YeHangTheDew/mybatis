package com.yee.mybatis;

/**
 *  *Mapper.xml
 *  映射文件里面的一个属性
 *  <update id="addUser" parameteType="" resultType="">
 *  	insert into ... values(#{usid},#{uname}...)
 *  </update>
 *  <select></select>
 */
public class MapperInfo {
	private String parameterType;
	private String resultType;
	private String sql;
	private boolean isUpdate=false;
	
	@Override
	public String toString() {
		return "MapperInfo [parameterType=" + parameterType + ", resultType=" + resultType + ", sql=" + sql
				+ ", isUpdate=" + isUpdate + "]";
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
}
