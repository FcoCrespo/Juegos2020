package edu.uclm.esi.games2020.ws;

import java.util.List;

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
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Se ha conectado " + session.getId());
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
        System.out.println("La sesión " + session.getId() + " dice " + message.getPayload());
        JSONObject jso = new JSONObject(message.getPayload().toString());
        if (jso.getString("type").equals("ready")) {
            Manager.get().playerReady(jso.getString("idMatch"), session);
        }

        if(jso.getString("type").equals("doPlayTer")){
        	Match match = Manager.get().findMatch(jso.getString("idMatch"));
        	match.play(jso, session);
            //Manager.get().doPlayTer(jso.getString("idMatch"), jso.getInt("lugar_x"), jso.getInt("lugar_y"), session);
        }

    }
  
}
