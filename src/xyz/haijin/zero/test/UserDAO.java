package xyz.haijin.zero.test;

import java.sql.SQLException;

import xyz.haijin.zero.BaseDAO;

public class UserDAO extends BaseDAO<User>{
	
	public UserDAO() {
		super(User.class, "d_weblog");
	}
	
	public static void main(String[] args) throws SQLException, IllegalAccessException {
		BaseDAO<User> dao = new UserDAO();
//		User user = new User();
//		user.setUserName("我爱海鲸");
//		user.setPassword("123456");
//		user.setRealName("刘德华");
//		user.setRegisterTime("202009130123");
//		user.setSex("男");
//		dao.save(user);
		System.out.println(dao.findWhereSql("我爱海鲸",10));
//		System.out.println(dao.findCountSql("我爱海鲸",10));
	}

}
