function ViewModel() {
	var self = this;
	self.usuarios = ko.observableArray([]);
	self.cartas = ko.observableArray([]);
	self.cartasEnMesa = ko.observableArray([]);
	
	var idMatch = sessionStorage.idMatch;
	var started = JSON.parse(sessionStorage.started);
	self.mensaje = ko.observable("");
	if (started) {
		self.mensaje("La partida " + idMatch + " ha comenzado");
	} else {
		self.mensaje("Esperando oponente para la partida " + idMatch);
	}
	
	self.ponerEnMesa = function(carta) {
		var msg = {
			type : "carta a la mesa",
			carta : carta
		};
		ws.send(JSON.stringify(msg));
	}
	
	var url = "ws://localhost:8600/juegos";
	var ws = new WebSocket(url);

	ws.onopen = function(event) {
		var msg = {
			type : "ready",
			idMatch : sessionStorage.idMatch
		};
		ws.send(JSON.stringify(msg));
	}

	ws.onmessage = function(event) {
		var data = event.data;
		data = JSON.parse(data);
		if (data.type == "matchStarted") {
			self.mensaje("La partida ha empezado");
			var players = data.players;
			for (var i=0; i<players.length; i++) {
				var player = players[i];
				self.usuarios.push(player.userName);
			}
			var table = data.startData.table;
			for (var i=0; i<table.length; i++) {
				self.cartasEnMesa.push(table[i]);
			}
			var cartas = data.startData.data;
			for (var i=0; i<cartas.length; i++)
				self.cartas.push(cartas[i]);
			console.log(data);
		}
	}
}

var vm = new ViewModel();
ko.applyBindings(vm);