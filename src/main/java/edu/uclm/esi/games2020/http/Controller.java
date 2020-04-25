package edu.uclm.esi.games2020.http;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.games2020.model.Manager;
import edu.uclm.esi.games2020.model.Match;
import edu.uclm.esi.games2020.model.User;


@RestController
public class Controller {
	
	private static final String MATCHTYPE = "match";
	private final Logger log = Logger.getLogger(Controller.class.getName());
	
	@PostMapping("/email")
    public void emailPassRequest(@RequestBody Map<String, Object> email) {
        JSONObject jso = new JSONObject(email);
        String emailReq = jso.getString("email");
        boolean result;
		try {
			result = Manager.get().emailPassReq(emailReq);
			if(!result) {
	        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email does not exist in the system.");
	        }
		} catch (Exception e) {
			log.info("\nError al enviar el correo");
		}
        
        
    }
	
	@PostMapping("newpassword")
    public void getToken(@RequestBody Map<String, Object> passwords) {
		JSONObject jso = new JSONObject(passwords);
        String newpwd1 = jso.getString("newpwd1");
        String newpwd2 = jso.getString("newpwd2");
        String token = jso.getString("token");
        if (newpwd1.equals(newpwd2)) {
            int result = Manager.get().changePass(token, newpwd1);
            if(result == -1) {
            	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unexpected token");
            }
            if(result == -2) {
            	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your token has expired. You have to request a password change again.");
            }
        }
        else {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords are not the same");
        }

        
    }
	
    @PostMapping("/login")
    public void login(HttpSession session, @RequestBody Map<String, Object> credenciales){
        JSONObject jso = new JSONObject(credenciales);
        String userName = jso.getString("userName");
        String pwd = jso.getString("pwd");
        User user;
		try {
			user = Manager.get().login(session, userName, pwd);
			session.setAttribute("user", user);
	        log.info("\nLa sesión actual del usuario es: "+session.getId());
		} catch (Exception e) {
			log.info("\nError en login");
		}
        
    }

    @PostMapping("/register")
    public void register(@RequestBody Map<String, Object> credenciales){
        JSONObject jso = new JSONObject(credenciales);
        String userName = jso.getString("userName");
        String email = jso.getString("email");
        String pwd1 = jso.getString("pwd1");
        String pwd2 = jso.getString("pwd2");
        String cuenta = jso.getString("cuenta");
        if (pwd1.equals(pwd2)) {
            try {
				Manager.get().register(email, userName, pwd1, cuenta);
			} catch (Exception e) {
				log.info("\nError en registro");
			}
        }
        else {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords are not the same");
        }
    }

    @GetMapping("/getGames")
    public JSONArray getGames(){
        return Manager.get().getGames();
    }
    
    @GetMapping("/getUser")
    public JSONArray getUser(HttpSession session){
    	log.info("\nLa sesión actual del usuario tras conectarse es: "+session.getId());
        return Manager.get().getUser(session);
    }
    
    @GetMapping("/getRankedUsers")
    public JSONArray getRankedUsers(HttpSession session){
    	log.info("\nLa sesión actual del usuario tras conectarse es: "+session.getId());
        return Manager.get().getRankedUsers(session);
    }
    

    @PostMapping(value = "/joinToMatchConMap", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> joinToMatchConMap(HttpSession session, @RequestBody Map<String, Object> info) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Identíficate antes de jugar");
        } else {
            JSONObject jso = new JSONObject(info);
            String game = jso.getString("game");
            Match match = Manager.get().joinToMatch(user, game);
            HashMap<String, Object> resultado = new HashMap<>();
            
            resultado.put("type", MATCHTYPE);
            resultado.put(MATCHTYPE, match);
            return resultado;
        }
    }

    @PostMapping(value = "/joinToMatch", produces = MediaType.APPLICATION_JSON_VALUE)
    public String joinToMatch(HttpSession session, @RequestBody Map<String, Object> info) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Identíficate antes de jugar");
        } else {
            JSONObject jso = new JSONObject(info);
            String game = jso.getString("game");
            Match match = Manager.get().joinToMatch(user, game);
            JSONObject resultado = new JSONObject();
            resultado.put("type", MATCHTYPE);
            resultado.put(MATCHTYPE, match.toJSON());
            return resultado.toString();
        }
    }
    
}
