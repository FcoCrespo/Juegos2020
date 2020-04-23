var url = "ws://localhost:8600/juegos";
var ws = new WebSocket(url);

function ViewModel() {
    var self = this;
    self.usuarios = {};
    var idMatch = sessionStorage.idMatch;
    var started = JSON.parse(sessionStorage.started);
    self.mensaje = ko.observable("Esperando oponente para la partida " + idMatch);
    self.tablero = ko.observableArray([ko.observableArray([])]);
  
    
    var finished = false;

    buildTablero();


    self.doPlay = function(fila, columna, $data, event){
     //   console.log(event.target.id);
     //   document.getElementById(event.target.id).innerHTML = "O";
     //   console.log(fila + " " + columna);

        var msg = {
            type: "doPlayTer",
            idMatch: sessionStorage.idMatch,
            lugar_x: (fila-1),
            lugar_y: columna
        };
        
        if(!finished){
        	ws.send(JSON.stringify(msg));
        }

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
            var comb = ["O", "X"];

            for (var i = 0; i <= 1; i++) {
                var player = players[i];
                self.usuarios[player.userName] = comb[i];
            }
        }

        if(data.type == "matchChangeTurn") {
            self.mensaje("Turno de " + data.turn);
        }

        if(data.type == "matchIlegalPlay"){
            alert(data.result);
        }

        if(data.type == "matchFinished"){
            self.mensaje(data.result);
            finished = true;
        }

        if(data.type == "matchPlay"){
            document.getElementById("box" + (parseInt(data.play_x)+1) + data.play_y).innerHTML = self.usuarios[data.playName];
        }

    }
}

var vm = new ViewModel();
ko.applyBindings(vm);