<%@page import="edu.uclm.esi.web.ws.Sessions"%>
<%@page import="edu.uclm.esi.web.ws.WSServer"%>
<%@page import="edu.uclm.esi.web.ws.WSSession"%>
<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@ page import="edu.uclm.esi.web.*, edu.uclm.esi.games.model.*, java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Juegos en grupo</title>
</head>
<body>
	Welcome.
	
	<table>
		<tr>
			<td>
				<table border="1">
					<tr>
						<th>Jugadores conectados</th>
					</tr>
					<tr>
						<th>Nombre</th>
					</tr>
					<%
					Manager manager=Manager.get();
					Enumeration<AbstractPlayer> users=manager.getPlayers().elements();
					while (users.hasMoreElements()) {
						AbstractPlayer user=users.nextElement();
						%>
						<tr>
							<td><%= user.getUserName() %></td>
						</tr>
						<%
					}
					%>
				</table>			
			</td>
			<td>
				<table border="1">
					<tr>
						<th colspan="2">Sesiones activas</th>
					</tr>
					<tr>
						<th>Id</th>
						<th>Jugador</th>
					</tr>
					<%
					Enumeration<WSSession> sessions=WSServer.getSessions().getSessionsByUserName().elements();
					WSSession wsSession;
					while (sessions.hasMoreElements()) {
						wsSession=sessions.nextElement();
						%>
						<tr>
							<td><%= wsSession.getId() %></td>
							<td><%= wsSession.getPlayer().getUserName() %></td>
						</tr>
						<%
					}
					%>
				</table>
			</td>
		</tr>
	</table>
	
	<table>
		<tr>
			<td>
				<table border="1">
					<tr>
						<th colspan="4">Partidas en juego</th>
					</tr>
					<tr>
						<th>Id</th><th>Id juego</th><th>Jugador A</th><th>Jugador B</th>
					</tr>
					<%
					Enumeration<Match> matches=manager.getInPlayMatches().elements();
					Match match;
					while (matches.hasMoreElements()) {
						match=matches.nextElement();
						%>
						<tr>
							<td><%= match.getIdMatch() %></td>
							<td><%= match.getGameName() %></td>
							<td><%= match.getPlayerA().getUserName() %></td>
							<td><%= match.getPlayerB().getUserName() %></td>
						</tr>
						<%
					}
					%>
				</table>
			</td>
			<td>
				<table border="1">
					<tr>
						<th colspan="3">Partidas pendientes</th>
					</tr>
					<tr>
						<th>Id</th><th>Id juego</th><th>Jugador A</th>
					</tr>
					<%
					Vector<Match> vMatches=manager.getPendingMatches();
					for (int i=0; i<vMatches.size(); i++) {
						match=vMatches.get(i);
						%>
						<tr>
							<td><%= match.getIdMatch() %></td>
							<td><%= match.getGameName() %></td>
							<td><%= match.getPlayerA().getUserName() %></td>
						</tr>
						<%
					}
					%>
				</table>
			</td>
		</tr>
	</table>
	
</body>
</html>