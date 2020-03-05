<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*, edu.uclm.esi.games2020.model.Manager, edu.uclm.esi.games2020.model.User" %>

<%
JSONObject resultado=new JSONObject();
try {
	if (!request.getMethod().equals("GET"))
		throw new Exception("Método no soportado");

	User user=(User) session.getAttribute("user");
	if (user!=null) {
		Manager.get().logout(user);
		session.invalidate();
	}
	resultado.put("type", "OK");
}
catch (Exception e) {
	resultado.put("type", "error");
	resultado.put("message", e.getMessage());
}
%>

<%= resultado.toString() %>

