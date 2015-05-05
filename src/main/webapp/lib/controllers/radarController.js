techRadarControllers.controller('RadarCtrl', function ($scope, $http, $location, $routeParams, $modal, $log) {

	$scope.uploadingTechnologies = false;
	$scope.showShare = false;
	$scope.errors = [];
	$scope.warnings = [];
	$scope.msgs = [];
	$scope.editing = $location.url().match('edit$');
	
	$scope.addTechVisible = false;
	$scope.selectedQuad = $routeParams.quadrant;

	if($scope.selectedQuad == null || typeof $scope.selectedQuad === "undefined"){
		$scope.selectedQuad = "";
	}

	$('#fileinput').on('change', function(ev){
		$scope.$apply(function(){
			$scope.uploadingTechnologies = true;
			$scope.errors = [];
			$scope.warnings = [];
			$scope.msgs = [];
		});

		var form = document.getElementById('uploadform');
		form['id'].value = $scope.selectedRadar.id;
		form.submit();

		var checkFrame = function() {
			var frameWindow = document.getElementById('theframe').contentWindow;
			if(typeof frameWindow.techRadarData != 'undefined') {
				$scope.$apply(function(){
					$scope.uploadingTechnologies = false;
					if(frameWindow.techRadarData.success===true) {
						mapRadar(frameWindow.techRadarData.radar);
						$scope.msgs.push('Successfully uploaded technologies to the radar, click \'save\' if you are happy');
					}
					for(var i = 0; i < frameWindow.techRadarData.errors.length; i++) {
						$scope.errors.push(frameWindow.techRadarData.errors[i]);
					}
					for(var i = 0; i < frameWindow.techRadarData.warnings.length; i++) {
						$scope.warnings.push(frameWindow.techRadarData.warnings[i]);
					}
				});
			} else {
				window.setTimeout(checkFrame, 2000);
			}
		};

		window.setTimeout(checkFrame, 2000);
	});

	$scope.exportPdf = function(id) {
		var form = document.getElementById('pdfExportForm');
		form['id'].value = id;
		form.submit();
	};

	$scope.exportCsv = function(id) {
		var form = document.getElementById('csvExportForm');
		form['id'].value = id;
		form.submit();
	};

	$scope.addTechs = function (techs) {
		$scope.addTechVisible = false;
		
		
		$scope.selectedRadar.technologies = techs;
		
		mapRadar($scope.selectedRadar);
	};
	
	$scope.doSave = function () {
		$http.post('/radar/rest/radar/addtech/' + $scope.selectedRadar.id, $scope.selectedRadar.technologies).
		success(function(data, status, headers, config) {
			$location.path('/radar/' + $scope.selectedRadar.id);
		}).
		error(function(data, status, headers, config) {
			$log.error('Failed to add technologies to radar');
			$log.error(data);
		});
	};

	$scope.doDelete = function ( radarId ) {
		$http({method:'DELETE', url:'/radar/rest/radar/' + radarId}).
		success(function(data, status, headers, config) {
			$location.path('/radar');
		}).
		error(function(data, status, headers, config) {
			$log.error('Failed to delete radar');
			$log.error(data);
		});
	};
	
	$scope.doPublish = function ( radarId ) {
		var updateRadar = {
			id: $scope.selectedRadar.id,
			name: $scope.selectedRadar.name,
			description: $scope.selectedRadar.description,
			businessUnit: $scope.selectedRadar.businessUnit,
			published: true,
			lastPublishedDate: new Date().getTime(),
			approved: $scope.selectedRadar.approved,
			majorVersion: $scope.selectedRadar.majorVersion+1,
			minorVersion: 0,
			dateCreated: $scope.selectedRadar.dateCreated
		};
		
		$http.put('/radar/rest/radar/' + radarId, updateRadar).
		success(function(data, status, headers, config) {
			$scope.selectedRadar.published = data.published;
			$scope.selectedRadar.lastPublishedDate = data.lastPublishedDate;
			$scope.selectedRadar.majorVersion = data.majorVersion;
			$scope.selectedRadar.minorVersion = data.minorVersion;
		}).
		error(function(data, status, headers, config) {
			$log.error('Failed to publish radar');
			$log.error(data);
		});
	};
	
	$scope.blipMoved = function(blip) {
		for(var i = 0; i < $scope.selectedRadar.technologies.length; i++) {
			if($scope.selectedRadar.technologies[i].technology === blip.name) {
				$scope.selectedRadar.technologies[i].quadrant = blip.techGrouping;
				$scope.selectedRadar.technologies[i].maturity = blip.arc;
				$log.log('Moving ' + blip.name + ' to tech grouping ' + blip.techGrouping + ' and arc ' + blip.arc);
			}
		}
	};

	//var quadrantColours = ['#3DB5BE', '#83AD78', '#E88744', '#8D2145'];
	var quadrantColours = ['#428bca', '#d9534f', '#5cb85c', '#f0ad4e'];
	var arcColours = ['rgb(223,223,223)', 'rgb(166,167,169)', 'rgb(190,191,193)', 'rgb(209,209,209)', 'rgb(223,223,223)'];
	var arcWidths = [150, 125, 75, 50, 50];

	$scope.selectedRadar = {
			arcs:[],
			quadrants:[]
	};

	$scope.mouseOver = function(item) {
		$scope.selectedItem = angular.copy(item);
	};

	$scope.mouseOut = function(item) {
		$scope.selectedItem = null;
	};

	$scope.blipClicked = function(blip) {
		$scope.clickedTechnology = blip;
		$scope.technologyModalVisible = true;
	};
    
    $scope.focus = function(technology){
		$scope.hoveredTechnology = technology;
    };

	$scope.skillLevelSelected = function(technology, skillLevel) {
		$http.post('/radar/rest/technology/' + technology.techId + '/user', {skillLevel:skillLevel}).
		success(function(data, status, headers, config) {
			$log.log('Successfully set technology \'' + technology.name + '\' (ID: ' + technology.techId + ') to skill level \'' + skillLevel + '\'');
		}).
		error(function(data, status, headers, config) {
			$log.error('Failed to add technologies to radar');
			$log.error(data);
		});
	};
	
	$scope.radarIndex = 0;
	$scope.selectedQuadrant = null;
	
	$scope.selectIndex = function(ind) {
		if(ind>0) {
			$scope.selectedQuadrant = $scope.quads[ind-1];
		} else {
			$scope.selectedQuadrant = null;
		}
		$scope.radarIndex = ind;
	};

	var mapRadar = function(data) {
		var theRadar = data;
		theRadar.arcMap = {};
		theRadar.quadrantMap = {};
		theRadar.radar = {
				arcs: [],
				quadrants: []
		};

		for(var i = 0; i < theRadar.maturities.length; i++) {
			(function(row){
				var arc = {
						id: row.name,
						ind: i,
						name: row.name,
						r: arcWidths[theRadar.radar.arcs.length],
						color: arcColours[theRadar.radar.arcs.length]
				};
				theRadar.arcMap[row.name] = arc;
				theRadar.radar.arcs.push(arc);
			})(theRadar.maturities[i]);
		}
		
		$scope.quads = [];

		for(var i = 0; i < theRadar.quadrants.length; i++) {
			(function(row){
				quadrant = {
						id: row.name,
						ind: i,
						name: row.name,
						color: quadrantColours[theRadar.radar.quadrants.length],
						items: []
				};
				var quad = {name:row.name, arcs:[]};
				for(var j = 0; j < theRadar.maturities.length; j++) {
					var arc = {name:theRadar.maturities[j].name, techs:[]};
					quad.arcs.push(arc);
				}
				$scope.quads.push(quad);
				theRadar.quadrantMap[row.name] = quadrant;
				theRadar.radar.quadrants.push(quadrant);
			})(theRadar.quadrants[i]);
		}

		if(theRadar.technologies!=null && typeof theRadar.technologies!='undefined') {
			for(var i = 0; i < theRadar.technologies.length; i++) {
				(function(row){
					var customerStrategic = row.customerStrategic;
					var newItem = {
							techId: row.id,
							id: i+1,
							name: row.technology,
							show: false,
							arc: row.maturity,
							pc: {
								r: row.radius,
								t: Math.floor((Math.random() * 90) + 1)
							},
							movement: row.movement,
							description: row.description,
							detailUrl: row.detailUrl,
							customerStrategic: customerStrategic,
							url: row.url
					};
					$scope.quads[theRadar.quadrantMap[row.quadrant].ind].arcs[theRadar.arcMap[row.maturity].ind].techs.push(newItem);
					theRadar.quadrantMap[row.quadrant].items.push(newItem);
				})(theRadar.technologies[i]);
			}
		}
		$scope.selectedRadar = theRadar;
	};

	var loadRadar = function(id) {
		$http({method: 'GET', url: '/radar/rest/radar/' + id + '?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			mapRadar(data);
		}).
		error(function(data, status, headers, config) {
			$log.log('error');
		});
	};

	if(!($routeParams.radarid == null || typeof $routeParams.radarid === 'undefined')) {
		loadRadar($routeParams.radarid);
	}

});
