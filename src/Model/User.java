package Model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mysql.jdbc.Statement;

import Databases.Connections;

public class User {
	private int userId;
	private String userTwitterName;
	private String userName;
	private String password;
	private String deviceId;//is needed ? 
	public static String getUserDeviceId(int userId){
	
			Connection con = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = Connections.getDatabaseConnectionPath();
				// Engelli kullanıcılar da çekiliyor çekilmemesi gerek .
				// Burada ilerde twitter değerlerine göre datalar çekilcek
				String query = "Select deviceId from User where id=? ";
				PreparedStatement st = con.prepareStatement(query);
				st.setInt(1, userId);
				ResultSet rs = st.executeQuery();
				st.clearBatch();
				if (rs.next()) {
					return rs.getString(1);
				} else {
					return null;

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					con.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return null;
	}
	public static long saveNotification(int userId,int type,String text,Date date,String link){

		long num  = 0;
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			// Engelli kullanıcılar da çekiliyor çekilmemesi gerek .
			// Burada ilerde twitter değerlerine göre datalar çekilcek
			String query = "Insert into UserNotifications (userId,notificationType,notificationText,notificationDate,notificationLink) VALUES (?,?,?,?,?) ";
			PreparedStatement st = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, userId);
			st.setInt(2,type);
			st.setString(3, text);
			st.setDate(4, date);
			st.setString(5, link);
			num = st.executeUpdate();
			if(num!=0){
				ResultSet generatedKeys = st.getGeneratedKeys();
				if (generatedKeys.next()) {
					num = generatedKeys.getLong(1);
				}
			}
			
			st.clearBatch();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return num;
	}
	
}
