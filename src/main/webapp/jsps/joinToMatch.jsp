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
    if (!jso.getString("type").equals("JoinToMatch")) {
		resultado.put("type", "error");
		resultado.put("message", "Mensaje inesperado");
	} else {
		String game=jso.getString("game");
		User user = (User) session.getAttribute("user");
		if (user==null) 
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Identíficate antes de jugar");
		else {
			JSONObject jsoMatch = Manager.get().joinToMatch(user, game);
			resultado.put("type", "match");
			resultado.put("match", jsoMatch);
		}
	}
}
catch (Exception e) {
	resultado.put("type", "error");
	resultado.put("message", e.getMessage());
}
%>

<%= resultado.toString() %>
