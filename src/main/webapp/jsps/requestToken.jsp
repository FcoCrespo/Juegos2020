<%@page import="edu.uclm.esi.jsoner.JSONer"%>
<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*, edu.uclm.esi.web.Manager, edu.uclm.esi.games.model.Token" %>

<%
String p=request.getParameter("p");
JSONObject resultado=new JSONObject();
try {
	if (!request.getMethod().equals("POST"))
		throw new Exception("Método no soportado");
	JSONObject jso=new JSONObject(p);
	
	if (!jso.getString("type").equals("RequestToken")) {
		resultado.put("type", "error");
		resultado.put("message", "Mensaje inesperado");
	} else {
		String userName=jso.getString("userName");
		Token token=Manager.get().requestToken(userName);
		JSONObject jsoRespuesta=JSONer.toJSON(token);
		resultado.put("resultado", jsoRespuesta);
		resultado.put("type", "OK");
	}
}
catch (Exception e) {
	resultado.put("type", "error");
	resultado.put("message", e.getMessage());
}
%>

<%= resultado.toString() %>
