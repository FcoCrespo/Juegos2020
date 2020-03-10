var url = "ws://localhost:8600/juegos";
var ws = new WebSocket(url);

function ViewModel() {
    var self = this;
    self.usuarios = ko.observableArray([]);
    var idMatch = sessionStorage.idMatch;
    var started = JSON.parse(sessionStorage.started);
    self.mensaje = ko.observable("");
    self.tablero = ko.observableArray([ko.observableArray([])]);
    var identity = "O";
    var turn = false;
    if(started){
    	self.mensaje("La partida " + idMatch + " ha comenzado");
    	identity = "X";
    }
    else{
    	self.mensaje("Esperando rival para la partida " + idMatch);
    }
    
    buildTablero();
    turn = getTurn();
    
    self.doPlay = function(data, event){
    	if(checkPlay()){
	        console.log(event.target.id);
	        document.getElementById(event.target.id).innerHTML = identity;
	        rotateTurn();
    	}
    }
	
	function getTurn() {
		$.get("getTurn");
	}
	
	function rotateTurn() {
		$.get("rotateTurn");
		getTurn();
	}
    
    function checkPlay(){
    	
    	if(!started)
    		return false;
    	if(!turn)
    		return false;
    	
    }

    function buildTablero() {
        var n = 3;

        for(var i = 0; i<n; i++){

            var row =  ko.observableArray([]);

            for(var j = 0; j<n; j++){
                row.push("");
            }

            self.tablero.push(row);
        }
    }

    ws.onopen = function (event) {
        var msg = {
            type: "ready",
            idMatch: sessionStorage.idMatch
        };
        ws.send(JSON.stringify(msg));
    };

    ws.onmessage = function (event) {
        var data = event.data;
        data = JSON.parse(data);
        if (data.type == "matchStarted") {

            self.mensaje("La partida ha empezado");

            var players = data.players;

            for (var i = 0; i < players.length; i++) {
                var player = players[i];
                self.usuarios.push(player.userName);
            }
        }
        
        if (data.type == "matchChangeTurn"){
    		var turnSession = JSON.parse(data.turn);
    		var idSession = '<%= Session["VALUE"] %>';
    		console.log(idSession);
    		if(session = idSession){
    			turn = true;
    		}
    	} 
    }
}

var vm = new ViewModel();
ko.applyBindings(vm);