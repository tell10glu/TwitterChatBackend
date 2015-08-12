<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="Databases.Connections"%>
<%@page import="java.util.Date"%>
<%@page import="com.google.android.gcm.server.Sender"%>
<%@page import="com.google.android.gcm.server.Message"%>
<%@page import="java.io.IOException"%>
<%@page import="org.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String SENDER_ID = "AIzaSyBfSTOAth2J_IR3hI6_mzTo9qDeCyo53QQ";
	String dId = request.getParameter("deviceid");
	String type = request.getParameter("type");
	String id = request.getParameter("id");
	String text = request.getParameter("text");
	String link = request.getParameter("link");
	if(dId!=null && type!=null && id!=null && text!=null && !dId.equals("")){
		
		Sender sender = new Sender(SENDER_ID);
		
		int notificationCount = 1;
		String deviceId =dId;
		System.out.println(deviceId);
		String d;
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try{
            d = dateFormat.format(new Date());
        }catch (Exception ex){
            ex.printStackTrace();
            d = dateFormat.format(new Date());
        }
        
		Message message = new Message.Builder()
		.delayWhileIdle(true)
		.addData("type",type)
		.addData("notificationCount", String.valueOf(notificationCount))
		.addData("notificationType", String.valueOf(type))
		.addData("notificationDate", String.valueOf(d))
		.addData("notificationLink", String.valueOf(link))
		.addData("notificationText",String.valueOf(text))
		.addData("id",String.valueOf(id))
		.build();
		try {
			sender.send(message, deviceId, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
		
	
%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="//cdn.datatables.net/1.10.7/css/jquery.dataTables.css">
<script type="text/javascript" charset="utf8" src="//code.jquery.com/jquery-1.10.2.min.js"></script>
<script type="text/javascript" charset="utf8" src="//cdn.datatables.net/1.10.7/js/jquery.dataTables.js"></script>
<script>
$(document).ready(function(){
    $("#myTable").DataTable();
    $("#myTable").DataTable();
});
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Test Gcm Notification</title>
</head>
<body>
		
	<form action="TestGcmNotification.jsp" method="POST">
	Device Id : <input type="text" name="deviceid" value='<%out.print(dId); %>' /><br/>
	Notification Type : <input type="text" name="type" value='<%out.print(type); %>'/><br/>
	Notification Id : <input type ="text" name="id" value='<%out.print(id); %>'/><br/>
	Notification Text : <input type ="text" name="text" value='<%out.print(text); %>'/><br/>
	Notification Link : <input type ="text" name="link" value='<%out.print(link); %>'/><br/>
	<input type="submit" value="gonder"/>
	</form>
	USERS
	<table id="myTable">
					<thead>
						<tr>
							<th>userId</th>
							<th>deviceId</th>
						</tr>
					</thead>
					<tbody>
						<%
						
						Connection con = null;
						
						try {
							Class.forName("com.mysql.jdbc.Driver");
							con = Connections.getDatabaseConnectionPath();
							String query = "Select id,deviceId from User";
							Statement st = con.createStatement();
							ResultSet set = st.executeQuery(query);
							while(set.next()){
								out.print("<tr>"+
										"<td>"+set.getInt(1)+"</td>"+
										"<td>"+set.getString(2)+"</td></tr>");
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
						
						%>
					
					</tbody>
	</table>
</body>
</html>