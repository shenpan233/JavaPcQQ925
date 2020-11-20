package Mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

public class Mysql {

	private static String DBDRIVER = "com.mysql.jdbc.Driver"; // 驱动程序名
	private static String DBURL = null; // URL指向要访问的数据库名mydata

	private static String DBUSER = null; // MySQL配置时的用户名
	private static String DBPASSWORD = null;
	private Connection conn = null;
	private Statement stmt = null;

	/**
	 * 
	 * @param host
	 *            数据库地址
	 * @param port
	 *            数据库地址
	 * @param name
	 *            数据库名称
	 * @param user
	 *            数据库账号
	 * @param password
	 *            数据库密码
	 */
	public boolean Init(String host, String port, String name, String user,
			String password) {
		this.DBURL = "jdbc:mysql://" + host + ":" + port + "/" + name
				+ "?useSSL=false";
		this.DBUSER = user;
		this.DBPASSWORD = password;

		try {
			Class.forName(this.DBDRIVER);
			this.conn = DriverManager.getConnection(this.DBURL, this.DBUSER,
					this.DBPASSWORD);

			this.stmt = this.conn.createStatement();
			return true;
		} catch (Exception e) {
			System.err.println("数据库配置信息错误!");
			return false;
		}

	}

	/**
	 * 改/增/删用这个
	 * 
	 * @param s
	 * @return boolean
	 */
	public boolean query(String s) {
		try {
			PreparedStatement pre = (PreparedStatement) this.conn
					.prepareCall(s);
			pre.executeUpdate();
			pre.close();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * 记得最后close一下
	 * 
	 * @param sql
	 * @return
	 */
	public ResultSet select(String sql) {
		try {
			ResultSet rs = this.stmt.executeQuery(sql);
			rs.next();
			return rs;
		} catch (SQLException e) {
			return null;
		}

	}

	public boolean Close() {
		try {
			this.conn.close();
			this.stmt.close();
			return true;
		} catch (SQLException e) {
			return false;
		}

	}

}