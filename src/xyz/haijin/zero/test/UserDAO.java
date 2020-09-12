package xyz.haijin.zero.test;

import java.sql.SQLException;

import xyz.haijin.zero.BaseDAO;

public class UserDAO extends BaseDAO<User>{
	
	public UserDAO() {
		super(User.class, "d_weblog");
	}
	
	public static void main(String[] args) throws SQLException {
		BaseDAO<User> dao = new UserDAO();
		System.out.println(dao.findWhereSql("我爱海鲸",10));
	}

}
