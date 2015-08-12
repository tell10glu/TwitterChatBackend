<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    // retrieve our passed CollapseKey and Message parameters, if they exist.
    String collapseKey = "GCM_Message";
    String message  = "Generic Broadcast Message";
 
    Object collapseKeyObj =  request.getAttribute("CollapseKey");
     
    if (collapseKeyObj != null) {
        collapseKey = collapseKeyObj.toString();
    }
     
    Object msgObj =  request.getAttribute("Message");
     
    if (msgObj != null) {
        message = msgObj.toString();
    }
     
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
    <form action="GcmNotification" method="post">
        <label>Message </label>
        <input type="text" name="userid">
        <br/><textarea name="Message" rows="3" cols="60" ><%=message %> </textarea> 
        <br/><input type="submit" name="submit" value="Submit" />
        </form>   
</body>
</html>