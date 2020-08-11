<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import=com.oreilly.servlet.* %>
<%@ page import=com.oreilly.servlet.multipart.* %>
<%
	request.setCharacterEncoding("utf-8");
	
	//upload폴더까지의 경로를 얻어온다 
	String path = getServletContext().getRealPath("upload");
	System.out.println(path);
	int max = 1024*1024*100;
	DefaultFileRenamePolicy policy = new DefaultFileRenamePolicy();
	MultipartRequest mr = new MultipartRequest(request, path, max, "utf-8",policy);
%>