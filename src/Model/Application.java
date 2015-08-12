package Model;

import javax.servlet.http.HttpServletRequest;

public class Application {
	public static String getURLWithContextPath(HttpServletRequest request) {
		   return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		}
}
