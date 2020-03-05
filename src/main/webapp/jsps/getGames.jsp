<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*, edu.uclm.esi.games2020.model.Manager" %>

<%
JSONObject resultado=new JSONObject();
try {
	if (!request.getMethod().equals("GET"))
		throw new Exception("Método no soportado");
	resultado.put("resultado", Manager.get().getGames());
	resultado.put("type", "OK");
}
catch (Exception e) {
	resultado.put("type", "error");
	resultado.put("message", e.getMessage());
}
%>

<%= resultado.toString() %>
