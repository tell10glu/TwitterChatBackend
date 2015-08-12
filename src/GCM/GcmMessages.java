package GCM;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import Databases.Connections;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

/**
 * Servlet implementation class GcmMessages
 */
@WebServlet("/GcmMessages")
public class GcmMessages extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//google apiden gelecek
    private static final String SENDER_ID = "AIzaSyARTItW4lty2CQompkWqTJiqMzH2P7SDTo";
    private ArrayList<Model.Messages> listMessages;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GcmMessages() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		JSONObject jMessage = new JSONObject();
		try {
			jMessage.put("senderId", "BEN");
			jMessage.put("messageData", "asdf");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.getWriter().write(jMessage.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userIdStr = request.getParameter("userId");
		int userId = 0;
		if(userIdStr==null)
			return;
		try {
			userId = Integer.parseInt(userIdStr);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Connection con = null;
		String deviceId = null;
		Integer messageCount = 0;
		try {
			con = Connections.getDatabaseConnectionPath();
			String query = "Select COUNT(*),deviceId from Messages,User where receiverId = ?";
			PreparedStatement st = con.prepareStatement(query);
			st.setInt(1, userId);
			ResultSet set = st.executeQuery();
			if(set.next()){
				deviceId = set.getString(2);
				messageCount = set.getInt(1);
			}
			if(messageCount!=0){
				Sender sender = new Sender(SENDER_ID);
				//.collapseKey("MESSAGEFROMTCHATTER") eğer birden fazla bildirimi üst üste göndermeyeceksek olması gereken kısım.
				Message message = new Message.Builder()
				.delayWhileIdle(true)
				.addData("You have "+messageCount+"messages","")
				.build();
				//Result işlemini yap
				 sender.send(message,deviceId, 1);
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			try {
				con.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		};
		
//		try {
//			Sender sender = new Sender(SENDER_ID);
//			//.collapseKey("MESSAGEFROMTCHATTER") eğer birden fazla bildirimi üst üste göndermeyeceksek olması gereken kısım.
//			Message message = new Message.Builder()
//			.delayWhileIdle(true)
//			.addData("messageContent", "Sistemden "+new Random().nextInt(100)+"mesajiniz var")
//			.addData("userName", "Faruk Rahmet")
//			.addData("userId", "-1")
//			.addData("date", new Date().toString())
//		
//			.build();
//			
//			Result result = sender.send(message,"APA91bGFHBaJA8zIAiIQ_Oq05Vy8J4RBnfrH58ssIDd1wGKvRt1P6CQoYvO7S87_t35pjG1kybhwfoPyan2iKmZ9TpLKszJtRCFWI9PMyG-or0yz7A1mBjLgvJvKkRy7nOQwsXDvBMuT", 1);
//			response.getWriter().write(result.toString());
//			System.out.println(result.toString());
//		} catch (Exception e) {
//			response.getWriter().write(e.getMessage());
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//		
//		request.getRequestDispatcher("index.jsp").forward(request, response);
		
	}

}
