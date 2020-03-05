<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*, edu.uclm.esi.web.Manager, edu.uclm.esi.games.model.*" %>

<%
String p=request.getParameter("p");
JSONObject resultado=new JSONObject();
try {
	if (!request.getMethod().equals("POST"))
		throw new Exception("Método no soportado");
	JSONObject jso=new JSONObject(p);
	
	if (!jso.getString("type").equals("ResetPwd")) {
		resultado.put("type", "error");
		resultado.put("message", "Mensaje inesperado");
	} else {
		String userName=jso.getString("userName");
		String pwd1=jso.getString("pwd1");
		String pwd2=jso.getString("pwd2");
		int token=jso.getInt("token");
		AbstractPlayer jsoRespuesta=Manager.get().resetPwd(userName, pwd1, token);
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
