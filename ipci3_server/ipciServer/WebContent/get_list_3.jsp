<%@  page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.*" %>
<%

	//JDBC 드라이버로딩 
	Class.forName("oracle.jdbc.OracleDriver");
	String url ="jdbc:oracle:thin:@222.119.13.193:1521:xe";
	String id = "sys as sysdba";
	String pw ="1234";
	
	//데이터베이스 접속 
	Connection db = DriverManager.getConnection(url, id, pw);
		
	String sql = "select mobile_idx, mobile_str1, mobile_image "
				+"from mobile_table where mobile_rate <0.3 and  mobile_rate >=0.0";
		
	PreparedStatement pstmt = db.prepareStatement(sql);
	ResultSet rs = pstmt.executeQuery();
		
	JSONArray root = new JSONArray();
	
		
	while(rs.next()){
		int mobile_idx = rs.getInt("mobile_idx");
		String mobile_str1 = rs.getString("mobile_str1");
		String mobile_image = rs.getString("mobile_image");
			
		JSONObject obj = new JSONObject();
		obj.put("mobile_idx", mobile_idx);
		obj.put("mobile_str1", mobile_str1);
		obj.put("mobile_image", mobile_image);
			
		root.add(obj);
			
	}
	db.close();
%>
<%= root.toJSONString() %>