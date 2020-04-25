package edu.uclm.esi.games2020.ws;

import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import edu.uclm.esi.games2020.model.Manager;
import edu.uclm.esi.games2020.model.Match;
import edu.uclm.esi.games2020.model.User;

@Component
public class SpringWebSocket extends TextWebSocketHandler {
	private final Logger log = Logger.getLogger(SpringWebSocket.class.getName());
	private static final String IDMATCH = "idMatch";
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	log.info("\nSe ha conectado " + session.getId());
        HttpHeaders headers = session.getHandshakeHeaders();
        List<String> cookies = headers.get("cookie");
        for (String cookie : cookies)
            if (cookie.startsWith("JSESSIONID=")) {
                String httpSessionId = cookie.substring("JSESSIONID=".length());
                User user = Manager.get().findUserByHttpSessionId(httpSessionId);
                user.setSession(session);
                break;
            }
    }
    
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    	log.info("\nLa sesión " + session.getId() + " dice " + message.getPayload());
        JSONObject jso = new JSONObject(message.getPayload().toString());
        if (jso.getString("type").equals("ready")) {
            Manager.get().playerReady(jso.getString(IDMATCH));
        }

        if(jso.getString("type").equals("doPlayTer")){
        	Match match = Manager.get().findMatch(jso.getString(IDMATCH));
        	match.play(jso, session);
        }
        
        if(jso.getString("type").equals("movimiento")){
        	Match match = Manager.get().findMatch(jso.getString(IDMATCH));
        	match.play(jso, session);
        }

        if(jso.getString("type").equals("doPlayDO")){
        	Match match = Manager.get().findMatch(jso.getString(IDMATCH));
        	String winner = match.play(jso, session);
        	if(winner!=null)
        		Manager.get().actualizarVictorias(winner);
        }
        if(jso.getString("type").equals("robCard")){
        	Match match = Manager.get().findMatch(jso.getString(IDMATCH));
        	match.play(jso, session);
        }
        if(jso.getString("type").equals("passTurn")){
        	Match match = Manager.get().findMatch(jso.getString(IDMATCH));
        	match.play(jso, session);
        }
    }
  
}
