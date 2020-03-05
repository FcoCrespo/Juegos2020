<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*, java.io.BufferedReader, edu.uclm.esi.games2020.model.Manager, edu.uclm.esi.games2020.model.User" %>

<%
BufferedReader br = request.getReader();
JSONObject resultado=new JSONObject();
try {
	if (!request.getMethod().equals("POST"))
		throw new Exception("Método no soportado");
	
	char[] charBuffer = new char[128];
	int bytesRead = -1;
	StringBuilder sb = new StringBuilder();
    while ((bytesRead = br.read(charBuffer)) > 0) {
        sb.append(charBuffer, 0, bytesRead);
    }
    String p = sb.toString();
	JSONObject jso=new JSONObject(p);
	
	if (!jso.getString("type").equals("Register")) {
		resultado.put("type", "error");
		resultado.put("message", "Mensaje inesperado");
	} else {
		String email=jso.getString("email");
		String userName=jso.getString("userName");
		String pwd1=jso.getString("pwd1");
		String pwd2=jso.getString("pwd2");
		if (!pwd1.equals(pwd2)) {
			response.sendError(HttpServletResponse.SC_CONFLICT, "Las passwords no coinciden");
		} else {
			Manager.get().register(email, userName, pwd1);
			resultado.put("type", "OK");
		}
	}
}
catch (Exception e) {
	resultado.put("type", "error");
	resultado.put("message", e.getMessage());
}
%>

<%= resultado.toString() %>
