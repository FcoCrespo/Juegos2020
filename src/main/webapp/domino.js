var url = "ws://localhost:8600/juegos";
var ws = new WebSocket(url);
var MiUsuario = ""

function getFichaSelected(){
    var e = document.getElementById("fichas").value;
    var fichaN1 = e.substring(0, 1)
    var fichaN2 = e.substring(4, 5)
    return [fichaN1, fichaN2]
}

function deleteFichaPuesta(){
    var ops = document.getElementById("fichas");
    if(ops.selectedIndex != -1){
        ops.remove(ops.selectedIndex)
    }
}

function ViewModel() {
    var self = this;
    self.usuarios = {};
    var idMatch = sessionStorage.idMatch;
    var started = JSON.parse(sessionStorage.started);
    self.mensaje = ko.observable("Esperando oponente para la partida " + idMatch);
    self.tablero = ko.observableArray([ko.observableArray([])]);
    self.fichas = ko.observableArray([]);
    self.contAfter = 35; // Centro
    self.contBefore = 35;


    var finished = false;

    buildTablero();

    self.doPlay = function(posicion, $data, event){

        var fichaN1 = getFichaSelected()[0]
        var fichaN2 = getFichaSelected()[1]


        if(fichaN1 === "" || fichaN2 === ""){
            alert("Accion no permitida "+fichaN1 + " " + fichaN2)
        }else{
            var msg = {
                type: "doPlayDO",
                idMatch: sessionStorage.idMatch,
                posicion: posicion, // true -> izquierda, false -> derecha
                number_1: fichaN1,
                number_2: fichaN2
            };
            if(!finished){
                ws.send(JSON.stringify(msg));
            }
        }
    }

    self.rob = function ($data) {
        var msg = {
            type: "robCard",
            idMatch: sessionStorage.idMatch
        };
        if(!finished){
            ws.send(JSON.stringify(msg))
        }

    }

    self.pass = function ($data) {
        var msg = {
            type: "passTurn",
            idMatch: sessionStorage.idMatch
        };
        if(!finished){
            ws.send(JSON.stringify(msg))
        }
    }

    function buildTablero() {
        var n1 = 5;
        var n2 = 11;

        for(var i = 0; i<n1; i++){

            var row =  ko.observableArray([]);

            for(var j = 0; j<n2; j++){
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
            var fichas = data.startData.data;
            for (var i = 0; i < fichas.length; i++)
                self.fichas.push(fichas[i]);
        }

        if(data.type == "matchChangeTurn") {
            turno = data.turn;
            self.mensaje("Turno de " + data.turn);
        }

        if(data.type == "matchIlegalPlay"){
            jugadaHecha = false
            alert(data.result);
        }

        if(data.type == "matchFinished"){
            self.mensaje(data.result);
            finished = true;
        }

        if(data.type == "cardRobbed"){
            var option = document.createElement("option");
            option.value = self.fichaN1 + ' | ' + self.fichaN2;
            option.text = self.fichaN1 + ' | ' + self.fichaN2;
            var fichasOps = document.getElementById("fichas");
            fichasOps.add(option)
        }

        if(data.type == "matchPlay"){

            if (data.posicion) {
                var posicionString = self.contBefore.toString()
                var fila = posicionString.substring(0, 1)
                if(self.contBefore == 35){
                    self.contAfter++
                }
                var contN1 = parseInt(self.contBefore.toString().substring(0,1))
                var contN2 = parseInt(self.contBefore.toString().substring(1,3))
                if(contN1%2){
                    if(contN2 == 0){
                        contN1++
                    }else {
                        contN2--
                    }
                }else{
                    if(contN2 == 10){
                        contN1++
                    }else{
                        contN2++
                    }
                }
                self.contBefore = contN1.toString() + contN2.toString()
            }else{
                var posicionString = self.contAfter.toString()
                var fila = posicionString.substring(0, 1)
                if(self.contAfter == 35){
                    self.contBefore--
                }
                var contN1 = parseInt(self.contAfter.toString().substring(0,1))
                var contN2 = parseInt(self.contAfter.toString().substring(1,3))
                if(contN1%2){
                    if (contN2 == 10) {
                        contN1--
                    } else {
                        contN2++
                    }
                }else{
                    if(contN2 == 0){
                        contN1--
                    }else{
                        contN2--
                    }
                }
                self.contAfter = contN1.toString() + contN2.toString()
            }

            if (!(parseInt(fila)%2)) {
                var aux = data.fichaN1
                data.fichaN1 = data.fichaN2
                data.fichaN2 = aux
            }
            document.getElementById("box" + posicionString).innerHTML = data.fichaN1 + ' | ' + data.fichaN2
            if (data.playName == MiUsuario){
                deleteFichaPuesta();
            }
        }
    }
}

var vm = new ViewModel();
ko.applyBindings(vm);