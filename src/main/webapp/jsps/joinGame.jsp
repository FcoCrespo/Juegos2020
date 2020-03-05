<%@page import="edu.uclm.esi.games.model.Match"%>
<%@page import="edu.uclm.esi.jsoner.JSONer"%>
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
	
	if (!jso.getString("type").equals("JoinGame")) {
		resultado.put("type", "error");
		resultado.put("message", "Mensaje inesperado");
	} else {
		AbstractPlayer player=(AbstractPlayer) session.getAttribute("player");
		String gameName=jso.getString("gameName");
		Match match=Manager.get().joinGame(player, gameName);
		JSONObject jsoRespuesta=JSONer.toJSON(match);
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
