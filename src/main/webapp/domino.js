var url = "ws://localhost:8600/juegos";
var ws = new WebSocket(url);

function ViewModel() {
    var self = this;
    self.usuarios = {};
    var idMatch = sessionStorage.idMatch;
    var started = JSON.parse(sessionStorage.started);
    self.mensaje = ko.observable("Esperando oponente para la partida " + idMatch);
    self.tablero = ko.observableArray([ko.observableArray([])]);
    self.contAfter = 22;
    self.contBefore = 22;
  
    
    var finished = false;

    buildTablero();


    self.doPlay = function(posicion, fichaN1, fichaN2, $data, event){

        var msg = {
            type: "doPlayDO",
            idMatch: sessionStorage.idMatch,
            posicion: posicion, // 0 -> delante, 1 -> atras
            number_1: fichaN1,
            number_2: fichaN2
        };
        
        if(!finished){
        	ws.send(JSON.stringify(msg));
        }

    }


    function buildTablero() {
        var n = 5;

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