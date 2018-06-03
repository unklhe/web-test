package cloudtp.com.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

import cloudtp.com.Constants;
import cloudtp.com.LoggerFactory;
import cloudtp.com.PropertiesUtil;

import oracle.jdbc.driver.OracleConnection;

public class DataAccessObject {
	public static Connection getConnection() {
		try {
			@SuppressWarnings("rawtypes")
			Class dbDriverClass;
			dbDriverClass = Class.forName(PropertiesUtil.DBProperties.getProperty(Constants.DBDriverClassName));
			Driver driver = (Driver) dbDriverClass.newInstance();
			DriverManager.registerDriver(driver);
			Connection conn = DriverManager.getConnection(
					PropertiesUtil.DBProperties.getProperty(Constants.DBUrl),
					PropertiesUtil.DBProperties.getProperty(Constants.DBUser),
					PropertiesUtil.DBProperties.getProperty(Constants.DBPassword));
			
			return conn;
		} catch (Exception e) {			
			LoggerFactory.Write(e);
		}
		return null;
	}

	public static String[][] GetArray(String sql){
		ArrayList<String[]> list = new ArrayList<String[]>();
		int count = 0;
		Connection conn = DataAccessObject.getConnection();
		try {
			conn.setAutoCommit(false);
			Statement sm = conn.createStatement();
			ResultSet rs = sm.executeQuery(sql);

			while (rs.next()) {
				count++;
				int colCount = rs.getMetaData().getColumnCount();
				String[] row = new String[colCount];
				for (int i = 0; i < colCount; i++) {
					if(null!=rs.getObject(i+1))
						row[i] = rs.getObject(i+1).toString();
					else
						row[i] = "";
				}
				list.add(row);
			}
			rs.close();
			sm.close();
		} 
		catch(SQLException ex)
		{
			LoggerFactory.Write(ex);
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
		if(count<1)
			return null;
		String[][] result = new String[count][];
		for (int i = 0; i < count; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	public static String GetString(String sql) {
		Connection conn = DataAccessObject.getConnection();
		try {
			conn.setAutoCommit(false);
			Statement sm = conn.createStatement();
			ResultSet rs = sm.executeQuery(sql);
			String result = null;
			if (rs.next()) {				
				result = rs.getString(1);
			}
			rs.close();
			sm.close();
			return result;
		}
		catch(SQLException ex)
		{
			LoggerFactory.Write(ex);
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
		return null;
	}

	public static byte[] GetBlob(String sql) {
		Connection conn = DataAccessObject.getConnection();
		byte[] data = null;
		try {
			conn.setAutoCommit(false);
			Statement sm = conn.createStatement();	    	
	    	ResultSet rs = sm.executeQuery(sql);
	    	rs.next();
    	    //使用oracle.sql.BLOB类，没办法了，变成专用的了
    	    oracle.sql.BLOB blob = (oracle.sql.BLOB) rs.getBlob(1);
    	    //到数据库的输出流
    	    InputStream stream = blob.getBinaryStream();
    	    data = new byte[blob.getBufferSize()];
    	    //data = blob.getBytes();
    	    stream.read(data, 0 ,data.length);
    	    stream.close();
	    	sm.close();
    	} 
		catch(SQLException ex)
		{
			LoggerFactory.Write(ex);
		}
		catch(IOException ex)
		{
			LoggerFactory.Write(ex);
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
		return data;
	}
	
	
	public static String[] GetSingleRowValue(String sql){
		Connection conn = DataAccessObject.getConnection();
		try {

			conn.setAutoCommit(false);
			Statement sm = conn.createStatement();
			ResultSet rs = sm.executeQuery(sql);
			int colCount = rs.getMetaData().getColumnCount();
			String[] result = null;
			if (rs.next()) {
				result = new String[colCount];
				for (int i = 0; i < colCount; i++) {
					Object o = rs.getObject(i+1);
					if(o!=null)
						result[i] = rs.getObject(i+1).toString();
					else
						result[i] = "";
				}
			}
			rs.close();
			sm.close();
			return result;
		}
		catch(SQLException ex)
		{
			LoggerFactory.Write(ex);
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
		return null;
	}

	public static String[] GetList(String sql) {
		ArrayList<String> list = new ArrayList<String>();
		Connection conn = DataAccessObject.getConnection();
		try {
			conn.setAutoCommit(false);
			Statement sm = conn.createStatement();
			ResultSet rs = sm.executeQuery(sql);

			while (rs.next()) {
				list.add(rs.getObject(1).toString());
			}
			rs.close();
			sm.close();
		}
		catch(SQLException ex)
		{
			LoggerFactory.Write(ex);
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
		if(list.size()==0)
			return null;
		String[] result = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	public static int GetCount(String sql) {
		Connection conn = DataAccessObject.getConnection();
		try {
			conn.setAutoCommit(false);
			Statement sm = conn.createStatement();
			ResultSet rs = sm.executeQuery(sql);
			int count = 0;
			while (rs.next())
				count++;
			rs.close();
			sm.close();
			return count;
		} 
		catch(SQLException ex)
		{
			LoggerFactory.Write(ex);
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
		return -1;
	}

	public static void ExecuteSQL(String sql) throws SQLException {
		Connection conn = DataAccessObject.getConnection();
		try {
			conn.setAutoCommit(true);
			Statement sm = conn.createStatement();
			sm.execute(sql);			
			sm.close();			
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	public static void ExecuteSQL(String[] sqlList) throws SQLException {
		Connection conn = DataAccessObject.getConnection();
		try {
			conn.setAutoCommit(false);
			Statement sm = conn.createStatement();
			for (int i = 0; i < sqlList.length; i++) {
				if (sqlList[i].length() < 1)
					continue;
				sm.execute(sqlList[i]);				
			}
			conn.commit();
			sm.close();
		} catch (SQLException e) {
			try {
				conn.rollback();				
			} catch (SQLException e1) {
			}
			throw e;
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}		
	}

	
	
	public static void ExecuteProcedure(String procedureName, String[][] parameters) throws SQLException {
		OracleConnection conn = (OracleConnection) DataAccessObject
				.getConnection();

		try {

			String params = "";
			if(parameters!=null)
			{
				params = "(?";
				for(int i=1;i<parameters.length;i++)
					params += ",?";
				params += ")";
			}
			CallableStatement call = conn.prepareCall(String.format(
					"{ call %s%s }", procedureName, params));

			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					if ("out".equals(parameters[i][0]) || "inout".equals(parameters[i][0]))
						call.registerOutParameter(parameters[i][2], "num".equals(parameters[i][1]) ? Types.VARCHAR
								: Types.INTEGER);
					if (!"out".equals(parameters[i][0])) {
						if (!"num".equals(parameters[i][1]))
							call.setString(parameters[i][2], parameters[i][3]);
						else
							call.setLong(parameters[i][2], Integer.parseInt(parameters[i][3]));
					}
				}
			}
			call.execute();

			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					if ("out".equals(parameters[i][0]) || "inout".equals(parameters[i][0])) {
						if ("num".equals(parameters[i][1]))
							parameters[i][3] = call.getString(parameters[i][2]);
						else
							parameters[i][3] = String.valueOf(call.getLong(parameters[i][2]));
					}
				}
			}
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
}
