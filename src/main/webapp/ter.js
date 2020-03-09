var url = "ws://localhost:8600/juegos";
var ws = new WebSocket(url);

function ViewModel() {
    var self = this;
    self.usuarios = ko.observableArray([]);
    var idMatch = sessionStorage.idMatch;
    var started = JSON.parse(sessionStorage.started);
    self.mensaje = ko.observable("");
    self.tablero = ko.observableArray([ko.observableArray([])]);
    self.mensaje("La partida " + idMatch + " ha comenzado");

    buildTablero();


    self.doPlay = function(data, event){
        console.log(event.target.id);
        document.getElementById(event.target.id).innerHTML = "O";



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
    }
}

var vm = new ViewModel();
ko.applyBindings(vm);