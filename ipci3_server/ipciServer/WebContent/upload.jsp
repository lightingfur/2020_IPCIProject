<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oreilly.servlet.*" %>
<%@ page import="com.oreilly.servlet.multipart.*" %>
<%@ page import="java.sql.*" %>
<%
	request.setCharacterEncoding("utf-8");

	//upload 폴더까지의 경로를 얻어온다
	String path = getServletContext().getRealPath("upload");
	System.out.println(path);
	//최대파일 용량 
	int max = 1024*1024*100;
	//이름 변경 정책 
	DefaultFileRenamePolicy policy = new DefaultFileRenamePolicy();
	//업로드처리 
	MultipartRequest mr = new MultipartRequest(request, path, max, "utf-8", policy);
	
	
	String mobile_str1 = mr.getParameter("mobile_str1");
	String mobile_str2 = mr.getParameter("mobile_str2");
	String mobile_str3 = mr.getParameter("mobile_str3");
	String mobile_image = mr.getParameter("mobile_image");
	int mobile_see = Integer.parseInt(mr.getParameter("mobile_see"));
	float mobile_right =  Float.parseFloat(mr.getParameter("mobile_right"));
	float mobile_rate =  Float.parseFloat(mr.getParameter("mobile_rate"));
	
	
	System.out.println("mobile_str1 : " + mobile_str1);
	System.out.println("mobile_str2 : " + mobile_str2);
	System.out.println("mobile_str3 : " + mobile_str3);
	System.out.println("mobile_see : " + mobile_see);
	System.out.println("mobile_right : " + mobile_right);
	System.out.println("mobile_rate : " + mobile_rate);	
	System.out.println("mobile_image : " + mobile_image);	
	
	//JDBC 드라이버로딩 
	Class.forName("oracle.jdbc.OracleDriver");
	String url ="jdbc:oracle:thin:@222.119.13.193:1521:xe";
	String id = "sys as sysdba";
	String pw ="1234";
	
	//데이터베이스 접속 
	Connection db = DriverManager.getConnection(url, id, pw);
	//쿼리문 
	String sql = "insert into mobile_table"
				+"(mobile_idx,mobile_image,mobile_str1,mobile_str2,mobile_str3,mobile_see,mobile_right,mobile_rate) "
				+"values (mobile_seq.nextval, ?, ?, ?, ?, ?, ?, ?)";
	
	PreparedStatement pstmt = db.prepareStatement(sql);
	pstmt.setString(1, mobile_image);
	pstmt.setString(2, mobile_str1);
	pstmt.setString(3, mobile_str2);
	pstmt.setString(4, mobile_str3);
	pstmt.setInt(5,mobile_see);
	pstmt.setFloat(6,mobile_right);
	pstmt.setFloat(7, mobile_rate);
	
	pstmt.execute();
	db.close();

%>
OK


