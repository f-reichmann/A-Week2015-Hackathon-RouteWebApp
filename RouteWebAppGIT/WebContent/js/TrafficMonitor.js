var TrafficMonitor = (function(conf) {

	if (window.$ !== undefined) {
		conf = $.extend({
			map : "map",
			latitude : 48.128,
			longitude : 11.670,
			zoom : 14,
			zoomMinus : 0,
			zoomPlus : 0,
			controls : false
		}, conf);
	}

	var map;
	var cars = {};
	var ambulances = {};
	var order;
	var clickAction = function(lat, lng) {
		console.log([ lat, lng ]);
	}
	var icon = {
		car : L.MakiMarkers.icon({
			icon : "car",
			color : "#0098cc",
			size : "s"
		}),
		alarmed : L.MakiMarkers.icon({
			icon : "fire-station",
			color : "#691e7c",
			size : "m"
		}),
		ambulance : L.MakiMarkers.icon({
			icon : "hospital",
			color : "#b70132",
			size : "l"
		}),
		emergency : L.MakiMarkers.icon({
			icon : "danger",
			color : "#b70132",
			size : "m"
		})
	};

	function init() {

		var mapConf = (conf.controls) ? {} : {
			dragging : false,
			zoomControl : false,
			zoomAnimation : false,
			fadeAnimation : false
		};
		map = new L.Map(conf.map, mapConf);

		var osm = new L.TileLayer(
				'http://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png',
				{
					minZoom : conf.zoom - conf.zoomMinus,
					maxZoom : conf.zoom + conf.zoomPlus,
					attribution : '<a href="http://openstreetmap.org">OpenStreetMap</a>',
					detectRetina : true
				});

		map.setView(new L.LatLng(conf.latitude, conf.longitude), conf.zoom);
		map.addLayer(osm);
		map.on('click', function(e) {
			updateTarget(e.latlng.lat, e.latlng.lng);
			clickAction(e.latlng.lat, e.latlng.lng);
		});
		map.on('zoomend', function(e) {
			map.panTo([ conf.latitude, conf.longitude ], {
				animate : false
			})
		});
	}

	function refresh() {
		map._onResize();
	}

	function setClickAction(fn) {
		clickAction = fn;
	}

	function updateTarget(lat, lng) {
		if (order === undefined) {
			order = L.marker([ lat, lng ], {
				icon : icon.emergency
			});
			order.addTo(map);
		}
		order.setLatLng([ lat, lng ]);
	}

	function animate(marker, from, to) {
		var deltaLat = (to.lat - from.l) / 10;
		var deltaLng = (to.lng - from.lng) / 10;
	}

	function updateAmbulance(car) {
		console.log(car.isFree);
		
		

		
		var c = ambulances[car.vin];	
		if (c === undefined) {
			c = L.Marker.movingMarker([ [ car.latitude, car.longitude ] ], [],
					{
						icon : icon.ambulance
					});
			console.log("****************************")
						
			//c.bindPopup("").openPopup();
			
			c.ts = new Date();
			c.addTo(map);
			ambulances[car.vin] = c;
		}
		
				
		
		c.moveTo([ car.latitude, car.longitude ], (new Date() - c.ts));
		c.ts = new Date();
		
		if (car.isFree == "true") {
			c.setIcon(icon.ambulance);

		} else {
			var newAmbulanceIcon = L.MakiMarkers.icon({
				icon : "hospital",
				color : "#FFFF00",
				size : "l"
			});
			
			c.setIcon(newAmbulanceIcon);
			
		}
		
		
		
	/*	var latitude = car.latitude;
		var longitude = car.longitude;
		var vin = car.vin;
		vin = vin.substr(9, vin.length);
		var popupContent = '<div><h3>Ambulance ' + vin + '</h3></div> <hr>' +  
							'GPS:  (' + 
							c.getLatLng().lat + 
							',' + 
							c.getLatLng().lng + 
							')';
	
		var popup = c.getPopup();
		if (popup._isOpen){
			popup.setContent(popupContent);
		}
		c.on('mouseover', function(e) {
			c.bindPopup(popupContent).openPopup();
			});
		
		c.on('mouseout', function (e) {
            this.closePopup();
        });*/
	}

	function updateCar(car) {
		var c = cars[car.vin];
		if (c === undefined) {
			c = L.Marker.movingMarker([ [ car.latitude, car.longitude ] ], [],
					{
						icon : icon.car
					});
			
			//c.bindPopup("").openPopup();

			c.ts = new Date();
			c.addTo(map);
			cars[car.vin] = c;
		}
		c.moveTo([ car.latitude, car.longitude ], (new Date() - c.ts));
		c.ts = new Date();

		if (car.notified !== c.notified) {
			if (c.notified == "notified") {
				c.setIcon(icon.alarmed);
			} else {
				c.setIcon(icon.car);
			}
			c.notified = car.notified;
		}
		
		/*var latitude = car.latitude;
		var longitude = car.longitude;
		var vin = car.vin;
		vin = vin.substr(3, vin.length);
		var popupContent = '<div><h3>Car ' + vin + '</h3></div> <hr>' +  
							'GPS:  (' + 
							c.getLatLng().lat + 
							',' + 
							c.getLatLng().lng + 
							')';
	
		var popup = c.getPopup();
		if (popup._isOpen){
			popup.setContent(popupContent);
		}
		c.on('mouseover', function(e) {
			c.bindPopup(popupContent).openPopup();
			});
		
		c.on('mouseout', function (e) {
            this.closePopup();
        });
*/
	}

	

	function update(car) {
		if (car.vin.indexOf("ambulance") > -1) {
			updateAmbulance(car);
		} else {
			updateCar(car);
		}
	}

	return {
		init : init,
		setClickAction : setClickAction,
		update : update,
		updateTarget : updateTarget,
		order : order,
		refresh : refresh
	};

});
