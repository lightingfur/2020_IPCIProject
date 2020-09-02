<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.*" %>
<%	

	//클라이언트가 전달한 글번호를 추출
	String str1 = request.getParameter("mobile_idx");
	int mobile_idx = Integer.parseInt(str1);
	
	//JDBC 드라이버로딩 
	Class.forName("oracle.jdbc.OracleDriver");
	String url ="jdbc:oracle:thin:@222.119.13.193:1521:xe";
	String id = "sys as sysdba";
	String pw ="1234";
	
	//데이터베이스 접속 
	Connection db = DriverManager.getConnection(url, id, pw);
		
	String sql = "select mobile_image, mobile_str1, mobile_str2, mobile_str3, mobile_see, mobile_right,mobile_rate from mobile_table "
			+ "where mobile_idx=?";
	PreparedStatement pstmt = db.prepareStatement(sql);
	pstmt.setInt(1, mobile_idx);
	
	ResultSet rs = pstmt.executeQuery();
	rs.next();
	
	String mobile_image = rs.getString("mobile_image");	
	String mobile_str1 = rs.getString("mobile_str1");
	String mobile_str2 = rs.getString("mobile_str2");
	String mobile_str3 = rs.getString("mobile_str3");
	int mobile_see = rs.getInt("mobile_see");
	float mobile_right = rs.getFloat("mobile_right");
	float mobile_rate = rs.getFloat("mobile_rate");
		
		
	JSONObject obj = new JSONObject();
	obj.put("mobile_idx", mobile_idx);
	obj.put("mobile_image", mobile_image);
	obj.put("mobile_str1", mobile_str1);
	obj.put("mobile_str2", mobile_str2);
	obj.put("mobile_str3", mobile_str3);
	obj.put("mobile_see", mobile_see );
	obj.put("mobile_right", mobile_right);
	obj.put("mobile_rate", mobile_rate);
		
	db.close();
%>
<%= obj.toJSONString() %>


