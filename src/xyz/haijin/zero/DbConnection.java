package xyz.haijin.zero;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.exceptions.CJCommunicationsException;



public class DbConnection {

	private String jdbc_driver = "com.mysql.cj.jdbc.Driver";

	private String url;
	private String username;
	private String password;

	private Connection connection;
	private Statement statement;
	
	DbConnection(String url, String username, String password,String driver) {
		this.url = url;
		this.username = username;
		this.password = password;
		this.jdbc_driver = driver;
	}

	private Connection getConnection() {
		try {
			if(connection == null || connection.isClosed()) {
				Class.forName(jdbc_driver);
				connection = DriverManager.getConnection(url, username, password);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	protected Statement returnStatement() {
		Statement s = null;
		try {
			s = getConnection().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return s;
	}

	public Statement getStatement() {
		try {
			if(statement == null || statement.isClosed()) {
				statement = getConnection().createStatement();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return statement;
	}

	public PreparedStatement getStatement(String sql) {
		System.out.println("执行的sql语句:"+sql);
		PreparedStatement statement = null;
		try {
			statement = getConnection().prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return statement;
	}

	public int queryCount(String sql) throws SQLException {
		ResultSet resultSet = query(sql);
		resultSet.next();
		int count = resultSet.getInt(1);
		resultSet.close();
		return count;
	}

	public ResultSet query(String sql) {
//		if(!sql.contains("limit") && !sql.contains(" count") 
//				&& !sql.contains(" sum") && !sql.contains("group by") && !sql.contains("LAST_INSERT_ID")) {
//			sql += " limit 100";
//			System.out.println(sql);
//		}
		System.out.println("执行的sql语句:"+sql);
        ResultSet rs = null;
		try {
			rs = getStatement().executeQuery(sql);
		} catch (CJCommunicationsException e) { 
			close();
			System.out.println("retry");
			try {
				rs = getStatement().executeQuery(sql);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return rs;
	}

	public boolean execute(String sql) {
		System.out.println(sql);
        boolean result = false;
		try {
			result = getStatement().execute(sql);
		} catch (CJCommunicationsException e) {
			close();
			try {
				result = getStatement().execute(sql);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return result;
	}

	public boolean dropTable(String table) {
		String sql = "drop table if exists " + table;
        return execute(sql);
	}

	public void close() {
		if(statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			statement = null;
		}
		if(connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			connection = null;
		}
	}
	
}
