package Mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

public class Mysql {

	private static String DBDRIVER = "com.mysql.jdbc.Driver"; // ����������
	private static String DBURL = null; // URLָ��Ҫ���ʵ����ݿ���mydata

	private static String DBUSER = null; // MySQL����ʱ���û���
	private static String DBPASSWORD = null;
	private Connection conn = null;
	private Statement stmt = null;

	/**
	 * 
	 * @param host
	 *            ���ݿ��ַ
	 * @param port
	 *            ���ݿ��ַ
	 * @param name
	 *            ���ݿ�����
	 * @param user
	 *            ���ݿ��˺�
	 * @param password
	 *            ���ݿ�����
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
			System.err.println("���ݿ�������Ϣ����!");
			return false;
		}

	}

	/**
	 * ��/��/ɾ�����
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
	 * �ǵ����closeһ��
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