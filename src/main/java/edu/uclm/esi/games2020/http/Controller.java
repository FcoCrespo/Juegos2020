package edu.uclm.esi.games2020.http;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.games2020.model.Manager;
import edu.uclm.esi.games2020.model.Match;
import edu.uclm.esi.games2020.model.User;
import edu.uclm.esi.games2020.model.Token;

@RestController
public class Controller {
	
	
	@PostMapping("/email")
    public void emailPassRequest(HttpSession session, @RequestBody Map<String, Object> email) throws Exception {
        JSONObject jso = new JSONObject(email);
        String emailReq = jso.getString("email");
        boolean result = Manager.get().emailPassReq(emailReq);
        if(result == false) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unexpected email");
        }
        
    }
	
	@GetMapping("/user/{token}")
    public JSONArray getToken(@PathVariable("token") String token) {
		JSONArray jso = Manager.get().userToken(token);

        if(jso == null) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unexpected token");
        }
        return jso;
    }
	
    @PostMapping("/login")
    public void login(HttpSession session, @RequestBody Map<String, Object> credenciales) throws Exception {
        JSONObject jso = new JSONObject(credenciales);
        String userName = jso.getString("userName");
        String pwd = jso.getString("pwd");
        User user = Manager.get().login(session, userName, pwd);
        session.setAttribute("user", user);
        System.out.println("La sesión actual del usuario es: "+session.getId());
    }

    @PostMapping("/register")
    public void register(HttpSession session, @RequestBody Map<String, Object> credenciales) throws Exception {
        JSONObject jso = new JSONObject(credenciales);
        String userName = jso.getString("userName");
        String email = jso.getString("email");
        String pwd1 = jso.getString("pwd1");
        String pwd2 = jso.getString("pwd2");
        String cuenta = jso.getString("cuenta");
        if (pwd1.equals(pwd2)) {
            Manager.get().register(email, userName, pwd1, cuenta);
        }
        else {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords are not the same");
        }
    }

    @GetMapping("/getGames")
    public JSONArray getGames(HttpSession session) throws Exception {
        return Manager.get().getGames();
    }
    
    @GetMapping("/getUser")
    public JSONArray getUser(HttpSession session) throws Exception {
    	System.out.println("La sesión actual del usuario tras conectarse es: "+session.getId());
    	JSONArray jso = Manager.get().getUser(session);
        return jso;
    }
    
    

    @PostMapping(value = "/joinToMatchConMap", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> joinToMatchConMap(HttpSession session, HttpServletResponse response, @RequestBody Map<String, Object> info) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Identíficate antes de jugar");
        } else {
            JSONObject jso = new JSONObject(info);
            String game = jso.getString("game");
            Match match = Manager.get().joinToMatch(user, game);
            HashMap<String, Object> resultado = new HashMap<>();
            resultado.put("type", "match");
            resultado.put("match", match);
            return resultado;
        }
    }

    @PostMapping(value = "/joinToMatch", produces = MediaType.APPLICATION_JSON_VALUE)
    public String joinToMatch(HttpSession session, HttpServletResponse response, @RequestBody Map<String, Object> info) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Identíficate antes de jugar");
        } else {
            JSONObject jso = new JSONObject(info);
            String game = jso.getString("game");
            Match match = Manager.get().joinToMatch(user, game);
            JSONObject resultado = new JSONObject();
            resultado.put("type", "match");
            resultado.put("match", match.toJSON());
            return resultado.toString();
        }
    }
    
}
