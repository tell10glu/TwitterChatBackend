import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;

import Databases.Connections;


public class Log {
	private static final  String ACTION_INFO = "info";
	private static final String ACTION_ERROR = "error";
	private static final String ACTION_SYSTEM_ERROR = "systemerror";
	private static final String ACTION_REQUEST = "request";
	private static final String ACTION_RESPONSE = "response";
	private static final String ACTION_REQUEST_AND_RESPONSE = "requestandresponse";
	private static void wDb(String cikti,String ACTION){
		Connection con = null;
		try{
			Class.forName("com.mysql.jdbc.Driver"); 
			con = Connections.getDatabaseConnectionPath();
			String query = "insert into Log(date,hour,type,text) VALUES (?,?,?,?)";
			PreparedStatement st = con.prepareStatement(query);
			st.setDate(1, new Date(new java.util.Date().getTime()));
			st.setTime(2, new Time(new java.util.Date().getTime()));
			st.setString(3,ACTION);
			st.setString(4, cikti);
			st.executeUpdate();
		}catch(ClassNotFoundException ex){
			ex.printStackTrace();
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void systemError(String cikti){
		wDb(cikti,ACTION_SYSTEM_ERROR);
		
	}
	public static void request(String cikti){
		if(cikti.length()>250){
			cikti = cikti.substring(0, 250);
		}
		wDb(cikti,ACTION_REQUEST);
	}
	public static void response(String cikti){
		if(cikti.length()>250){
			cikti = cikti.substring(0, 250);
		}
		wDb(cikti,ACTION_RESPONSE);
	}
	public static void requestandresponse(String cikti){
		wDb(cikti,ACTION_REQUEST_AND_RESPONSE);
	}
	public static void i(String cikti){
		wDb(cikti, ACTION_INFO);
		
	}
	public static void e(String cikti){
		wDb(cikti,ACTION_ERROR);
	}
}