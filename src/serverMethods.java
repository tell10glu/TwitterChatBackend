import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.apple.eawt.Application;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

import responses.ErrorTypes;
import responses.SuccessType;
import Databases.Connections;
import GCM.GcmTypeFlags;
import Model.NotificationTypes;
import Model.Profile;
import Model.User;
import exceptions.HackerException;

/**
 * Servlet implementation class serverMethods
 */
@WebServlet("/serverMethods")
public class serverMethods extends HttpServlet {
	// Serverin çalışıp çalışmadığını döndüren method
	private static final String METHOD_IS_SERVER_AVAILABLE = "isServerAvailable";
	// Yeni arkadaşlar bulmaya yarayan method
	private static final String METHOD_USER_FIND_FRIENDS = "findFriends";
	// Kullanıcı kaydı
	private static final String METHOD_NEW_USER = "newUser";
	// Bu method kayıt işleminden sonra gerçekleşecek. Kullanıcı kayıt olması
	// sırasında çağırılırsa kullanıcı kaydı yanlış olsa bile hata vermeden
	// kayıt edecektir.
	private static final String METHOD_NEW_REGISTRY_FOR_GCM = "registerGCM";
	// Twitter datalarını çekicek olan method.NOT twitter dataları mobile
	// tarafında verir ise yani kullanıcının hesabı gizli ise buraya twitler
	// gelicek.
	private static final String METHOD_NEW_REGISTRY_FOR_TWITTER = "registerTwitter";
	// Instagram datalarını çekicek olan method.Resim çekilmek için şimdi
	// kullanılabilir.(ilerde hashtaglere göre bir kullancıı belirleme
	// olabilir.)
	private static final String METHOD_NEW_REGISTRY_FOR_INSTAGRAM = "registerInstagram";

	private static final String METHOD_IS_USER_A_MEMBER = "isUserAMember";
	private static final long serialVersionUID = 1L;
	private static final String METHOD_REMOVE_GCM = "removeGcm";

	private static final String METHOD_CHANGE_PASSWORD = "changepassword";

	private static final String METHOD_SEND_MESSAGE = "METHOD_SEND_MESSAGE";

	private static final String METHOD_REQUEST_NEW_FRIEND = "METHOD_REQUEST_NEW_FRIEND";

	private static final String METHOD_ACCEPT_NEW_FRIEND_REQUEST = "METHOD_ACCEPT_NEW_FRIEND_REQUEST";

	private static final String METHOD_MARK_NOTIFICATION_AS_SEEN = "METHOD_MARK_NOTIFICATION_AS_SEEN";

	private static final String METHOD_CHECK_NOTIFICATION = "METHOD_CHECK_NOTIFICATION";

	private static final String METHOD_GET_UNREADED_MESSAGES = "METHOD_GET_UNREADED_MESSAGES";

	private static final String METHOD_UPLOAD_NEW_IMAGE = "METHOD_UPLOAD_NEW_IMAGE";

	private static final String METHOD_GET_USER_IMAGES = "METHOD_GET_USER_IMAGES";

	private static final String METHOD_FETCH_FRIENDS = "METHOD_FETCH_FRIENDS";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public serverMethods() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	private static int getUserId(JSONObject object) throws HackerException, JSONException {
		Integer userID = object.getInt("userid");
		if (userID == null || userID <= 0)
			throw new HackerException();
		return userID;

	}

	/**
	 * Butun islerin yapilacagi sinif burasi.
	 * Gelen method ismine gore gerekli methodu calistirir.
	 * Eger methodName bos ise return eder.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String methodName = null;
		String requestJson = request.getReader().readLine();
		try {
			JSONObject js = new JSONObject(requestJson);
			methodName = js.getString("method");
			if (methodName == null) {
				return;
			}
			// gelen ve giden istekleri loglamak için kullanılır.
			Log.request(requestJson);
			if (methodName.equals(METHOD_IS_SERVER_AVAILABLE)) {
				isServerAvailable(response);
			} else if (methodName.equals(METHOD_USER_FIND_FRIENDS)) {
				findFriends(js, response);
			} else if (methodName.equals(METHOD_NEW_USER)) {
				newUser(js, response);
			} else if (methodName.equals(METHOD_IS_USER_A_MEMBER)) {
				isUserAMember(js, response);
			} else if (methodName.equals(METHOD_NEW_REGISTRY_FOR_GCM)) {
				registerforgcm(js, response);
			} else if (methodName.equals(METHOD_REMOVE_GCM)) {
				removeGcm(requestJson, response);
			} else if (methodName.equals(METHOD_CHANGE_PASSWORD)) {
				changePassword(requestJson, response);
			} else if (methodName.equals(METHOD_SEND_MESSAGE)) {
				sendMessage(js, response);
			} else if (methodName.equals(METHOD_REQUEST_NEW_FRIEND)) {
				requestNewFriend(js, response);
			} else if (methodName.equals(METHOD_ACCEPT_NEW_FRIEND_REQUEST)) {
				acceptRequest(js, response);
			} else if (methodName.equals(METHOD_CHECK_NOTIFICATION)) {
				getNotificationCount(js, response);
			} else if (methodName.equals(METHOD_MARK_NOTIFICATION_AS_SEEN)) {
				markNotifications(js);
			} else if (methodName.equals(METHOD_GET_UNREADED_MESSAGES)) {
				getUnreadedMessages(js, response);
			} else if (methodName.equals(METHOD_UPLOAD_NEW_IMAGE)) {
				Profile.uploadNewImage(js, request, response);
			} else if (methodName.equals(METHOD_GET_USER_IMAGES)) {
				String[] imagePaths = Profile.getUserImages(getUserId(js), request);
				JSONObject object = new JSONObject();
				object.put("image", imagePaths);
				response.getWriter().write(object.toString());
				System.out.println(object.toString());
				Log.response(object.toString());
			} else if (methodName.equals(METHOD_FETCH_FRIENDS)) {
				fetchFriends(js, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Kullanicinin okunmamis mesajlarini dondurmek icin kullanilir. Kullanimin
	 * ardindan mesajları okunmuş olarak işaretler.
	 * 
	 * @param js
	 *            [{senderid,context,date},..] şeklinde bir jsonArray
	 * @param response
	 * @throws HackerException
	 *             userid null gelir ise
	 * @throws JSONException
	 *             json hatasi
	 */
	private void getUnreadedMessages(JSONObject js, HttpServletResponse response) throws HackerException, JSONException {
		int userid = getUserId(js);
		Connection con = null;
		ArrayList<Integer> listOfUnreadedMessages = new ArrayList<Integer>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "Select * from Messages where receiverId = ? AND isReceived = ?";
			PreparedStatement st = con.prepareStatement(query);
			st.setInt(1, userid);
			st.setBoolean(2, false);

			ResultSet set = st.executeQuery();
			JSONArray jsonArray = new JSONArray();
			while (set.next()) {
				JSONObject object = new JSONObject();
				object.put("senderid", set.getInt("senderId"));
				object.put("context", set.getString("messageText"));
				object.put("date", set.getDate("messageDate"));
				listOfUnreadedMessages.add(set.getInt(1));
				jsonArray.put(object);
			}
			response.getWriter().write(new JSONObject().put("messages", jsonArray).toString());
			getExecutorService().execute(new setMessagesAsReaded(listOfUnreadedMessages));
		} catch (Exception e) {
			Log.systemError(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Bildirimleri okundu olarak isaretle
	 * @param js userid gerekli
	 * @throws HackerException
	 * @throws JSONException
	 */
	private void markNotifications(JSONObject js) throws HackerException, JSONException {
		int userId = getUserId(js);
		Profile.markNotifications(userId);
	}

	/**
	 * Kullanicinin bildirim sayisini dondurur.(Cok gerekli mi bilmiyorum)
	 * @param js userid
	 * @param response userid,notificationCount
	 * @throws JSONException
	 * @throws HackerException
	 * @throws IOException
	 */
	private void getNotificationCount(JSONObject js, HttpServletResponse response) throws JSONException, HackerException, IOException {
		Integer userId = js.getInt("userid");
		if (userId == null) {
			throw new HackerException();
		}
		int notificationCount = Profile.getUserNotificationCount(userId);
		JSONObject object = new JSONObject();
		object.put("userid", userId);
		object.put("notificationCount", notificationCount);
		response.getWriter().write(object.toString());

	}

	private void acceptRequest(JSONObject js, HttpServletResponse response) throws JSONException {

	}

	/**
	 * Yeni bir arkadaslik istegi yapilir.
	 * Diger kullaniciya gcm ile bildirim gonderilir.
	 * @param js userid,friendid
	 * @param response id,userid,friendid,receiverAccepted,requestDate (receiverAccepted = false) , (requestDate = sistem saati)
	 * @throws HackerException yanlis bir data gelir ise HackerException dondur
	 * @throws JSONException
	 */
	private void requestNewFriend(JSONObject js, HttpServletResponse response) throws HackerException, JSONException {
		Log.request(js.toString());
		Integer senderId = js.getInt("userid");
		Integer receiverId = js.getInt("friendid");
		if (senderId == null || receiverId == null) {
			throw new HackerException();
		}
		if (checkUsersIfFriends(senderId.intValue(), receiverId.intValue())) {
			// hata dondur.
			return;
		}
		Connection con = null;
		try {
			Date systemDate = new Date(new java.util.Date().getTime());
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "insert into UserRequests(senderid,receiverid,isRequestDeclined,date) VALUES (?,?,?,?) ";
			PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, senderId);
			st.setInt(2, receiverId);
			st.setBoolean(3, false);
			st.setDate(4, systemDate);
			long num = st.executeUpdate();// burasi hatali
			if (num != 0) {
				ResultSet generatedKeys = st.getGeneratedKeys();
				if (generatedKeys.next()) {
					num = generatedKeys.getLong(1);
				}
				System.out.println(num);
				JSONObject responseObject = new JSONObject();
				responseObject.put("id", num);
				responseObject.put("userid", senderId);
				responseObject.put("friendid", receiverId);
				responseObject.put("receiverAccepted", false);
				responseObject.put("requestDate", systemDate);
				Log.response(responseObject.toString());
				response.getWriter().write(responseObject.toString());
			}
			String notificationText = Profile.getUserNameAndSurname(senderId);
			if (notificationText == null)
				notificationText = "selam";
			long notificationId = User.saveNotification(receiverId, NotificationTypes.REQUEST_NEW_FRIEND, notificationText, new Date(new java.util.Date().getTime()), "user:" + senderId);
			getExecutorService().execute(new GcmNotificationRunnable(receiverId, (int) notificationId, NotificationTypes.REQUEST_NEW_FRIEND, notificationText, DateUtils.convertDateToString(new java.util.Date()), "user:" + senderId));
		} catch (Exception e) {
			Log.systemError(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Kullanicinin baska bir kullanici ile arkadas olup olmamasina bakilir.
	 * Burada sanirsam fazla bir kontrol var
	 * @param senderId  
	 * @param receiverId 
	 * @return
	 */
	private boolean checkUsersIfFriends(int senderId, int receiverId) {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			// ikinci kontrol gerekli mi ?
			String query = "select * from UserFriends where (userId=? AND friendId=?) OR (friendId=? AND userId=?) ";
			PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, senderId);
			st.setInt(2, receiverId);
			st.setInt(3, receiverId);
			st.setInt(4, senderId);
			ResultSet set = st.executeQuery();
			return set.next();

		} catch (Exception e) {
			Log.systemError(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Kullanicinin mesaj gonderecegi method.
	 * 
	 * @param requestJson senderid,receiverid,text gerekli
	 * @param response messageid,senderid,receiverid,messagetext,messagedate,isReceived (isReceiver false doner burada)
	 * @throws JSONException
	 * @throws IOException
	 */
	private void sendMessage(JSONObject requestJson, HttpServletResponse response) throws JSONException, IOException {
		Integer senderId = requestJson.getInt("senderid");
		Integer receiverId = requestJson.getInt("receiverid");
		String messageText = requestJson.getString("messagetext");
		if (senderId == null || receiverId == null || messageText == null) {
			response.getWriter().write(ErrorTypes.MESSAGE_SEND_FAIL);
			return;
		}
		if (senderId.intValue() == receiverId.intValue()) {
			response.getWriter().write("Fuck you");
			// session engellemeye calis.

			return;
		}
		Connection con = null;
		try {
			java.util.Date localDate = new java.util.Date();
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "insert into Messages(senderId,receiverId,messageText,messageDate,isReceived) VALUES (?,?,?,?,?) ";
			PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, senderId);
			st.setInt(2, receiverId);
			st.setString(3, messageText);
			st.setDate(3, new Date(localDate.getTime()));
			st.setBoolean(4, false);
			long num = st.executeUpdate();
			if (num == 0) {
			} else {
				// returns last id !!
				ResultSet generatedKeys = st.getGeneratedKeys();
				if (generatedKeys.next()) {
					num = generatedKeys.getLong(1);
				}
				JSONObject responseObject = new JSONObject();
				responseObject.put("messageid", num);
				responseObject.put("senderid", senderId);
				responseObject.put("receiverid", receiverId);
				responseObject.put("messagetext", messageText);
				responseObject.put("messagedate", localDate.getTime());
				responseObject.put("isReceived", false);
				Log.response(responseObject.toString());
				response.getWriter().write(responseObject.toString());
				getExecutorService().execute(new GcmMessagesRunnable(receiverId, messageText));
			}
		} catch (Exception e) {
			Log.systemError(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	/**
	 * Gereksiz method simdilik
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private boolean isServerAvailable(HttpServletResponse response) throws IOException {
		Log.request(METHOD_IS_SERVER_AVAILABLE);
		response.getWriter().write("{}");
		Log.response("available");
		return true;
	}

	/**
	 * Yeni kullanici kaydi yapilir.
	 * @param req username,password,email
	 * @param response dogru ise username,password,email,id 
	 * @throws IOException
	 * @throws JSONException
	 */
	private void newUser(JSONObject req, HttpServletResponse response) throws IOException, JSONException {
		String userName = req.getString("username");
		String password = req.getString("password");
		String email = req.getString("email");
		PrintWriter writer = response.getWriter();
		if (userName == null) {
			writer.write(ErrorTypes.EMPTY_USER_NAME);
			return;
		}
		if (password == null) {
			writer.write(ErrorTypes.EMPTY_PASSWORD);
			return;
		}
		if (email == null) {
			writer.write(ErrorTypes.EMPTY_EMAIL_ADDRESS);
			return;
		}
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "insert into User(userName,password,email) VALUES (?,?,?) ";
			PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, userName);
			st.setString(2, password);
			st.setString(3, email);
			long num = st.executeUpdate();
			if (num == 0) {
			} else {
				// returns last id !!
				ResultSet generatedKeys = st.getGeneratedKeys();
				if (generatedKeys.next()) {
					num = generatedKeys.getLong(1);
				}
				Profile.createProfile(((Long) num).intValue());
				JSONObject responseObject = new JSONObject();
				responseObject.put("username", userName);
				responseObject.put("password", password);
				responseObject.put("email", email);
				responseObject.put("userid", num);
				Log.response(responseObject.toString());
				response.getWriter().write(responseObject.toString());
			}

		} catch (Exception e) {
			Log.systemError(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/***
	 * Kullanicinin arkadaslarini bulmasina yarayan method.
	 * Simdilik sadece kendi id si olmayan kullanicilari donduruyor.
	 * @param request userid 
	 * @param response {["userid"=1,"userid"=2]} 
	 * @throws IOException
	 * @throws JSONException
	 * @throws HackerException yanlis bir userid gelir ise.
	 */
	private void findFriends(JSONObject request, HttpServletResponse response) throws IOException, JSONException, HackerException {

		JSONObject object = new JSONObject();

		int userId = getUserId(request);
		try {
			Connection con = null;
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			// Engelli kullanıcılar da çekiliyor çekilmemesi gerek .
			// Burada ilerde twitter değerlerine göre datalar çekilcek
			/*
			 * String query =
			 * "Select * from User,UserFriends where User.id!= UserFriends.friendId AND User.id = ?"
			 * ; PreparedStatement st = con.prepareStatement(query);
			 * st.setInt(1, userId); ResultSet rs = st.executeQuery();
			 * st.clearBatch(); while (rs.next()) { JSONObject userData = new
			 * JSONObject(); userData.put("id", rs.getInt(1));
			 * userData.put("twitterName", rs.getString(2));
			 * userData.put("userName", rs.getString(3)); JSONArray userImages =
			 * new JSONArray(); String imageQuery =
			 * "Select * from UserImages where userId = ?"; st =
			 * con.prepareStatement(imageQuery); ResultSet rs2 =
			 * st.executeQuery(); while (rs2.next()) {
			 * userImages.put(rs2.getBlob("Image")); } userData.put("images",
			 * userImages); jUsers.put(userData); }
			 */
			JSONArray jUsers = new JSONArray();
			String query = "Select * from user where id != ?";
			PreparedStatement st = con.prepareStatement(query);
			st.setInt(1, userId);
			ResultSet set = st.executeQuery();
			while (set.next()) {
				JSONObject userData = new JSONObject();
				userData.put("userid", set.getInt(1));
				jUsers.put(userData);
			}
			object.put("users", jUsers);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.getWriter().write(object.toString());
	}
	/**
	 * Kullanicinin arkadaslarinin verilerini cekmek icin kullanilacaktir.
	 * 
	 * @param js userid
	 * @param response userid,firstname,lastname,profil resmi
	 * @throws HackerException
	 * @throws JSONException
	 */
	private void fetchFriends(JSONObject js, HttpServletResponse response) throws HackerException, JSONException {
		int userId = getUserId(js);
		Connection con = null;
		try {
			JSONObject userData = null;
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			// Engelli kullanıcılar da çekiliyor çekilmemesi gerek .
			// Burada ilerde twitter değerlerine göre datalar çekilcek
			// bu şekilde çekilecek.
			String query = "Select User.id,userName from User,UserFriends where User.id=? AND User.id = UserFriends.friendId ";
			PreparedStatement st = con.prepareStatement(query);
			//st.setInt(1, jsonRequest.getInt("userid"));
			ResultSet rs = st.executeQuery();
			st.clearBatch();
			if (rs.next()) {
				userData = new JSONObject();
				userData.put("userid", rs.getInt(1));
				userData.put("username", rs.getString(2));
				userData.put("password", rs.getString(3));
				userData.put("email", rs.getString(4));
				System.out.print("id den geldim :");
				userData.put("deviceId", rs.getString(5));
				System.out.println(userData.toString());
				System.out.println("cevabim : " + userData.toString());
				response.getWriter().write(userData.toString());
				return;
			} else {
				JSONObject errorJSON = new JSONObject();
				errorJSON.put("error", ErrorTypes.WRONG_USER_ID);
				response.getWriter().write(errorJSON.toString());

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
		
		
	}

	/**
	 * Kullanici uye mi degil mi kontrol edilir.
	 * @param jsonRequest userid veya username,password ile giris yapilabilir.
	 * @param response userid,username,password,email,deviceId doner
	 * @throws IOException
	 */
	private void isUserAMember(JSONObject jsonRequest, HttpServletResponse response) throws IOException {

		System.out.println(jsonRequest.toString());
		try {
			if (jsonRequest.getInt("userid") >= 0) {
				Connection con = null;
				try {
					JSONObject userData = null;
					Class.forName("com.mysql.jdbc.Driver");
					con = Connections.getDatabaseConnectionPath();
					// Engelli kullanıcılar da çekiliyor çekilmemesi gerek .
					// Burada ilerde twitter değerlerine göre datalar çekilcek
					String query = "Select * from User where id=? ";
					PreparedStatement st = con.prepareStatement(query);
					st.setInt(1, jsonRequest.getInt("userid"));
					ResultSet rs = st.executeQuery();
					st.clearBatch();
					if (rs.next()) {
						userData = new JSONObject();
						userData.put("userid", rs.getInt(1));
						userData.put("username", rs.getString(2));
						userData.put("password", rs.getString(3));
						userData.put("email", rs.getString(4));
						userData.put("deviceId", rs.getString(5));
						response.getWriter().write(userData.toString());
						return;
					} else {
						JSONObject errorJSON = new JSONObject();
						errorJSON.put("error", ErrorTypes.WRONG_USER_ID);
						response.getWriter().write(errorJSON.toString());

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
			}

		} catch (JSONException e1) {
			e1.printStackTrace();

		}
		Connection con = null;
		try {
			System.out.println(jsonRequest.toString());
			JSONObject userData = null;
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			// Engelli kullanıcılar da çekiliyor çekilmemesi gerek .
			// Burada ilerde twitter değerlerine göre datalar çekilcek
			String query = "Select * from User where userName = ? AND password = ? ";
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, jsonRequest.getString("username"));
			st.setString(2, jsonRequest.getString("password"));
			ResultSet rs = st.executeQuery();
			st.clearBatch();
			if (rs.next()) {
				userData = new JSONObject();
				userData.put("id", rs.getInt(1));
				userData.put("username", rs.getString(2));
				userData.put("password", rs.getString(3));
				userData.put("email", rs.getString(4));
				userData.put("deviceId", rs.getString(5));
				System.out.println(userData.toString());
				response.getWriter().write(userData.toString());
			} else {
				JSONObject errorJSON = new JSONObject();
				errorJSON.put("hata", "yanlis");
				response.getWriter().write(errorJSON.toString());
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

	}

	/**
	 * Kullanicinin telefonunu kaydeden method.
	 * @param requestObject kullaniciid ve deviceid gerekli
	 * @param response dogru ise deviceid ve userid dondurulur.
	 * @throws IOException
	 * @throws JSONException
	 */
	private void registerforgcm(JSONObject requestObject, HttpServletResponse response) throws IOException, JSONException {
		Log.request(requestObject.toString());

		String deviceId = requestObject.getString("gcmid");
		if (deviceId == null) {
			response.getWriter().write(ErrorTypes.WRONG_DEVICE_ID);
			return;
		}
		int userId = requestObject.getInt("userid");
		/*
		 * try { userId = Integer.parseInt(request.getParameter("userid")); }
		 * catch (Exception e) {
		 * response.getWriter().write(ErrorTypes.WRONG_USER_ID); return; }
		 */
		// /****ÖNEMLİ*****
		// veritabanından user hesabı var mı diye kontrol et .
		// /****ÖNEMLİ*****
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "Update User SET deviceId = ? where id = ? ";
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, deviceId);
			st.setInt(2, userId);
			st.executeUpdate();
			JSONObject j = new JSONObject();
			j.put("deviceid", deviceId);
			j.put("userid", userId);
			response.getWriter().write(j.toString());
			Log.response(j.toString());
		} catch (Exception e) {
			e.printStackTrace();
			Log.response(ErrorTypes.UNKNOWN_ERROR + ":" + e.getMessage());
			Log.systemError(e.getMessage());
			response.getWriter().write(ErrorTypes.UNKNOWN_ERROR + ":" + e.getMessage());
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * Kullanicinin sifresini degistirmeye yarayacak olan sinif
	 * @param requestJson kullaniciadi,sifre gerekli
	 * @param response true veya false dondurulmeli (simdilik onemsiz)
	 * @throws JSONException
	 * @throws IOException
	 */
	private void changePassword(String requestJson, HttpServletResponse response) throws JSONException, IOException {
		JSONObject object = new JSONObject(requestJson);
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "Update User SET password = ? where username = ? ";
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, object.getString("password"));
			st.setString(2, object.getString("username"));
			st.executeUpdate();
			response.getWriter().write(SuccessType.SUCCESS_REGISTER_GCM);
			Log.response(SuccessType.SUCCESS_REGISTER_GCM);
		} catch (Exception e) {
			e.printStackTrace();
			Log.response(ErrorTypes.UNKNOWN_ERROR + ":" + e.getMessage());
			Log.systemError(e.getMessage());
			response.getWriter().write(ErrorTypes.UNKNOWN_ERROR + ":" + e.getMessage());
		} finally {
			try {
				object = null;
				con.close();
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * Kullanicinin gcm adresini siler. Kullanici logout oldugunda yapilmasi gerek
	 * @param requestJson userid gerekli
	 * @param response hata veya ok seklinde dondurulmeli (cok onemli degil)
	 * @throws JSONException 
	 * @throws IOException
	 */
	private void removeGcm(String requestJson, HttpServletResponse response) throws JSONException, IOException {
		int userId = new JSONObject(requestJson).getInt(("userid"));
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "Update User SET deviceId = ? where id = ? ";
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, null);
			st.setInt(2, userId);
			st.executeUpdate();
			response.getWriter().write(SuccessType.SUCCESS_REGISTER_GCM);
			Log.response(SuccessType.SUCCESS_REGISTER_GCM);
		} catch (Exception e) {
			e.printStackTrace();
			Log.response(ErrorTypes.UNKNOWN_ERROR + ":" + e.getMessage());
			Log.systemError(e.getMessage());
			response.getWriter().write(ErrorTypes.UNKNOWN_ERROR + ":" + e.getMessage());
		} finally {
			try {

				con.close();
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * GCM Bildirim gonderen method. Backgroundda calisir
	 * type ,notificationCount,
	 * @author abdullahtellioglu
	 *
	 */
	public class GcmNotificationRunnable implements Runnable {
		Integer userId;
		int nid;
		int ntype;
		String ntext;
		String ndate;
		String nLink;

		public GcmNotificationRunnable(Integer userId, int nid, int ntype, String ntext, String ndate, String nLink) {
			super();
			this.userId = userId;
			this.nid = nid;
			this.ntype = ntype;
			this.ntext = ntext;
			this.ndate = ndate;
			this.nLink = nLink;
		}

		@Override
		public void run() {
			Sender sender = new Sender(SENDER_ID);
			if (userId == null) {
				System.out.println("userId null geldi gcmNotification");
				return;
			}
			int notificationCount = 1;
			try {
				notificationCount = Profile.getUserNotificationCount(userId);
			} catch (Exception e) {
				e.printStackTrace();
				notificationCount = 1;
			}
			String deviceId = User.getUserDeviceId(userId);
			System.out.println(deviceId);

			Message message = new Message.Builder().delayWhileIdle(true).addData("type", String.valueOf(GcmTypeFlags.GCM_NOTIFICATION_FLAG)).addData("notificationCount", String.valueOf(notificationCount)).addData("notificationType", String.valueOf(ntype)).addData("notificationDate", String.valueOf(ndate)).addData("notificationLink", String.valueOf(nLink))
					.addData("notificationText", String.valueOf(ntext))

					.addData("id", String.valueOf(nid)).build();
			System.out.println(nid);
			try {
				sender.send(message, deviceId, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	/**
	 * Mesajlari gcm'e gonderecek olan sinif. Backgroundda calisir
	 * Type,senderid,context şeklinde gönderir
	 * @author abdullahtellioglu
	 *
	 */
	public class GcmMessagesRunnable implements Runnable {
		private Integer userid;
		private String messageText;

		public GcmMessagesRunnable(Integer userid, String messageText) {
			this.userid = userid;
			this.messageText = messageText;
		}

		@Override
		public void run() {
			Sender sender = new Sender(SENDER_ID);
			if (userid == null) {
				System.out.println("userId null geldi gcmNotification");
				return;
			}
			String deviceId = User.getUserDeviceId(userid);

			Message message = new Message.Builder().delayWhileIdle(true).addData("type", String.valueOf(GcmTypeFlags.GCM_MESSAGES_FLAG)).addData("senderid", String.valueOf(userid)).addData("context", messageText).build();
			try {
				sender.send(message, deviceId, 1);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

	private ExecutorService getExecutorService() {
		ServletContext context = getServletContext();
		ExecutorService service = (ExecutorService) context.getAttribute(ExecutorContextListener.EXECUTOR_SERVICE);
		return service;
	}

	/**
	 *  backgroundda mesajlari isaretlendi olarak atayacak olan method.
	 * @author abdullahtellioglu
	 *
	 */
	private class setMessagesAsReaded implements Runnable {
		private ArrayList<Integer> listOfMessages = null;

		public setMessagesAsReaded(ArrayList<Integer> listOfMessages) {
			this.listOfMessages = listOfMessages;

		}

		@Override
		public void run() {

			Connection con = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = Connections.getDatabaseConnectionPath();
				String query = "Update Messages SET isMessageReceived = ? where id = ?";
				PreparedStatement st = con.prepareStatement(query);
				for (int i = 0; i < listOfMessages.size(); i++) {
					st.setBoolean(1, true);
					st.setInt(2, listOfMessages.get(i));
					st.execute();
				}

			} catch (Exception e) {
				Log.systemError(e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					listOfMessages.clear();
					con.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

		}

	}

	private static final String SENDER_ID = "AIzaSyBfSTOAth2J_IR3hI6_mzTo9qDeCyo53QQ";

}
