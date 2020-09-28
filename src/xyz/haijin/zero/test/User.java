package xyz.haijin.zero.test;

import xyz.haijin.zero.Column;
import xyz.haijin.zero.ID;
import xyz.haijin.zero.NoWhere;
import xyz.haijin.zero.Table;
import xyz.haijin.zero.Where;

@Table("article_user")
@Where("f_user_username = ?")
@NoWhere("limit ?")
public class User {
	@ID("f_user_username")
	private String userName;
	private String password;
	@Column("f_user_realname")
	private String realName;
	@Column("f_user_sex")

	private String sex;
	@Column("f_user_register_time")
	private String registerTime;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	@Override
	public String toString() {
		return "User [userName=" + userName + ", password=" + password + ", realName=" + realName + ", sex=" + sex
				+ ", registerTime=" + registerTime + "]";
	}
}
