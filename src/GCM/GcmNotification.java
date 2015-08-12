package GCM;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Model.Profile;
import Model.User;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

/**
 * Servlet implementation class GcmNotification
 */
@WebServlet("/GcmNotification")
public class GcmNotification extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final String SENDER_ID = "AIzaSyBfSTOAth2J_IR3hI6_mzTo9qDeCyo53QQ";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GcmNotification() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*Sender sender = new Sender(SENDER_ID);
		
		int notificationCount = Profile.getUserNotificationCount(userId);
		String deviceId = User.getUserDeviceId(userId);
		System.out.println(deviceId);
		//if(notificationCount==0)
		//	return;
		
		Message message = new Message.Builder()
		.delayWhileIdle(true)
		.addData("type",String.valueOf( GcmTypeFlags.GCM_NOTIFICATION_FLAG))
		.addData("notificationCount",String.valueOf(notificationCount))
		.build();
		 sender.send(message,deviceId, 1);*/
		 
	}

}
