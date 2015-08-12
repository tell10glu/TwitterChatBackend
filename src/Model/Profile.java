package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import Databases.Connections;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Profile {
	// okunmamış bildirim sayısını tutar.
	public static String USER_PROFILE_UNREADED_NOTIFICATIONS = "unReadedNotificationCount";
	// sadece profil fotograf pathini dondurur.
	public static String USER_PROFILE_IMAGE_PATH = "userProfileImagePath";
	//kullanıcının resimlerini dondurur. Bir ';!;'stringe göre split yapilacak.
	public static String USER_PROFILE_ALL_USER_IMAGES = "userImages";
	
	public static String USER_PROFILE_IMAGE_COUNT = "imageCount";
	
	public static String USER_PROFILE_IMAGE = "image%d";
	
	public static String USER_FIRST_NAME = "FirstName";
	
	public static String USER_LAST_NAME = "LastName";
	
	public static String getUserNameAndSurname(int userId){
		return getProfileValue(USER_FIRST_NAME, userId)+" "+getProfileValue(USER_LAST_NAME, userId);
		
	}
	private static String getProfileValue(String profileName,int userId){
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "Select UserProfile.value from Profile,UserProfile where Profile.Name = ? AND UserProfile.profileId=Profile.id AND UserProfile.userId = ?";
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, profileName);
			st.setInt(2, userId);
			ResultSet set = st.executeQuery();
			if(set.next()){
				return set.getString(1);
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				con.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		};
		return null;
	}
	public static void createProfile(int userId){
		
		createProfile(userId,USER_PROFILE_UNREADED_NOTIFICATIONS,USER_PROFILE_IMAGE_PATH,USER_PROFILE_ALL_USER_IMAGES,USER_PROFILE_IMAGE_COUNT);
	}
	public static void createProfile(int userId,String...profileNames){
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "Insert into UserProfile(profileId,userId,value) VALUES ((Select Profile.id from Profile where Profile.Name = ?) ,?, ?)";
			for(int i =0;i<profileNames.length;i++){
				PreparedStatement st = con.prepareStatement(query);
				st.setString(1, profileNames[i]);
				st.setInt(2, userId);
				st.setString(3, "0");
				st.clearBatch();
				
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				con.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		};
	}
	private static void updateProfileValue(int userId,String profileName,String value){
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "Update Profile,UserProfile SET Profile.value = ? where Profile.Name = ? AND UserProfile.profileId=Profile.id AND UserProfile.userId = ?";
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, value);
			st.setString(2, profileName);
			st.setInt(3, userId);
		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				con.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		};
	}
	public static int getUserNotificationCount(int userId){
		return Integer.parseInt(getProfileValue(USER_PROFILE_UNREADED_NOTIFICATIONS, userId));
	}
	public static String[] getUserImages(int userId,HttpServletRequest request){
		ArrayList<String> listOfImages = new ArrayList<String>();
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "Select ImagePath from UserImages where userId = ? ";
			PreparedStatement st = con.prepareStatement(query);
			
			st.setInt(1, userId);
			ResultSet set = st.executeQuery();
			if(set.next()){
				BufferedReader reader = new BufferedReader(new FileReader(set.getString(1)));
				StringBuilder imgData  = new StringBuilder();
				String tmp =null; 
				while((tmp=reader.readLine())!=null){
					imgData.append(tmp);
				}
				reader.close();
				listOfImages.add(imgData.toString());
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
		String[] strArray =new String[listOfImages.size()];
		
		return (String[])listOfImages.toArray(strArray);
	}
	public static String getUserImage(int index,int userId){
		return getProfileValue(String.format(USER_PROFILE_IMAGE, index).toString(),userId);
	}
	public static void markNotifications(int userId) {
		updateProfileValue(userId,USER_PROFILE_UNREADED_NOTIFICATIONS, "0");
	}
	public static void uploadNewImage(JSONObject js, HttpServletRequest request, HttpServletResponse response) throws JSONException{
		String imageString = js.getString("image");
		int userId = js.getInt("userid");
		
		String fileName = String.valueOf(new Random().nextInt(10000))+".png";
		String realPath = "/users/"+userId+"/Images/"+fileName;
		File targetFile = new File(request.getRealPath(realPath));
		System.out.println(targetFile.getPath());
		File parentFile = targetFile.getParentFile();
		if(!parentFile.exists() && !parentFile.mkdirs()){
			parentFile.mkdirs();
		}
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(targetFile);
			saveSelectedImageToDb(targetFile.getPath(),userId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.write(imageString);
		writer.close();
	}
	private static void saveSelectedImageToDb(String imagePath,int userId){
		Connection con = null;
		try {
			Date systemDate = new Date(new java.util.Date().getTime());
			Class.forName("com.mysql.jdbc.Driver");
			con = Connections.getDatabaseConnectionPath();
			String query = "insert into UserImages(ImagePath,userId) VALUES (?,?)";
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, imagePath);
			st.setInt(2, userId);
			st.execute();
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
	}
	
}
