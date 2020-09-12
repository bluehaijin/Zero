package xyz.haijin.zero.test;

import xyz.haijin.zero.Column;
import xyz.haijin.zero.ID;
import xyz.haijin.zero.NoWhere;
import xyz.haijin.zero.Table;
import xyz.haijin.zero.Where;

@Table("article_user")
@Where("f_user_realname = ?")
@NoWhere("limit ?")
public class User {
	@ID("f_user_username")
	private String userName;
	@Column("f_user_password")
	private String password;
	@Column("f_user_realname")
	private String realName;
	@Column("f_user_sex")
	private String sex;
	@Column("f_user_register_time")
	private String registerTime;

	@Override
	public String toString() {
		return "User [userName=" + userName + ", password=" + password + ", realName=" + realName + ", sex=" + sex
				+ ", registerTime=" + registerTime + "]";
	}
}
