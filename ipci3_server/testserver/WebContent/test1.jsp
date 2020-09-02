<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oreilly.servlet.*" %>
<%@ page import="com.oreilly.servlet.multipart.*" %>
<%
	request.setCharacterEncoding("utf-8");

	String mobile_str1 = request.getParameter("mobile_str1");
	String mobile_str2 = request.getParameter("mobile_str2");
	int mobile_see = Integer.parseInt(request.getParameter("mobile_see"));
	
	System.out.println("mobile_str1 : " + mobile_str1);
	System.out.println("mobile_str2 : " + mobile_str2);
	System.out.println("mobile_see : " + mobile_see);
	
	
	
/*
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
	String mobile_image = mr.getParameter("mobile_image");
*/
%>