package com.open.capacity.db.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.open.capacity.db.utils.UnicodeCryptUtil;

/**
 * 自定义typehandler
 * @author xh
 */
@MappedTypes(CryptType.class)
public class CryptTypeHandler extends BaseTypeHandler<String> {

	 
	@Override
	public void setNonNullParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
		try {
			String encryptStr = this.encode(s);
			preparedStatement.setString(i, encryptStr);
		} catch (Exception e) {
			preparedStatement.setString(i, s);
		}

	}
 
	@Override
	public String getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
		return decrypt(resultSet.getString(columnName));
	}

	/**
	 * 判断字符串是否是密文
	 * @param param
	 * @return
	 */
	private boolean isEncrypt(String param) {
		// 可以使用字符串长度，是否包含中文判断是否是密文
		return true;
	}

	@Override
	public String getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
		return decrypt(resultSet.getString(columnIndex));
	}

	@Override
	public String getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
		return decrypt(callableStatement.getString(columnIndex));
	}
	
	
	
	/**
	 * Unicode 编码
	 */
	private String encode(String param) {
		return UnicodeCryptUtil.encodeUicode(param);
	}

	/**
	 * Unicode 解码
	 */
	private String decrypt(String param) {
		return UnicodeCryptUtil.decodeUnicode(param);
	}
}
