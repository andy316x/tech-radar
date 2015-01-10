var techRadarControllers = angular.module('techRadarControllers', ['ui.bootstrap']);

techRadarControllers.controller('CommonViewCtrl', function ($scope, $http, $location, $routeParams, $modal, $log) {

	// Stuff that all views will need


	$scope.items = ['item1', 'item2', 'item3'];

	$scope.open = function (size) {

		var modalInstance = $modal.open({
			templateUrl: 'myModalContent.html',
			controller: 'ModalInstanceCtrl',
			size: size,
			resolve: {
				items: function () {
					return $scope.items;
				}
			}
		});

		modalInstance.result.then(function (selectedItem) {
			$scope.selected = selectedItem;
		}, function () {
			$log.info('Modal dismissed at: ' + new Date());
		});
	};




	// Login
	$scope.loggedin = false;


	var getUserInfo = function() {
		// User info
		$http({method: 'GET', url: '/radar/rest/me?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			$scope.loggedin = true;
			$scope.uid = data.uid;
			$scope.name = data.name;
		}).
		error(function(data, status, headers, config) {
			if(status==403) {
				// The user is not authorised
				$scope.loggedin = false;
				$log.log('User is not logged in');
				var checkLogin = function() {
					var loginFrame = document.getElementById('loginframe');
					if(loginFrame != null) {
						var loginWindow = loginFrame.contentWindow;
						if(typeof loginWindow.techRadarData !== 'undefined') {
							// User has now logged in so get their details
							getUserInfo();
						} else {
							// User is still not logged in, try again in a bit
							window.setTimeout(checkLogin, 1000);
						}
					} else {
						// The login frame is not on the page, try again in a bit
						window.setTimeout(checkLogin, 1000);
					}
				};
				checkLogin();
			} else {
				// TODO error, we have an unexpected error case
				$log.error('Unexpected error occurred while verifying user identity');
			}
		});
	};
	getUserInfo();



	// Navigation
	$scope.go = function ( path ) {
		$location.path( path );
	};
	$scope.goApply = function ( path ) {
		$scope.$apply(function() {
			$location.path( path );
		});
	};

});

techRadarControllers.controller('TechnologiesCtrl', function ($scope, $http, $location, $routeParams, $log) {

	$scope.technologies = [];

	$http({method: 'GET', url: '/radar/rest/technology?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		if(data.length > 0) {
			$scope.selectedTechnology = data[0];
			for(var i = 0; i < data.length; i++) {
				$scope.technologies.push(data[i]);
			}
		}
	}).
	error(function(data, status, headers, config) {
		$log.log('error getting technology list');
	});
	
	$scope.$watch('selectedTechnology', function (newVal, oldVal, scope) {
		if(typeof newVal != 'undefined') {
			var technology = newVal;
			if(typeof technology.ratings == 'undefined') {
				$http({method: 'GET', url: '/radar/rest/technology/' + technology.id + '/user?nocache=' + (new Date()).getTime()}).
				success(function(data, status, headers, config) {
					technology.ratings = [];
					for(var i = 0; i < data.length; i++) {
						technology.ratings.push(data[i]);
					}
				}).
				error(function(data, status, headers, config) {
					$log.log('failed to load user rating for technology ' + technology.name);
				});
				
				// Load technology radars
				$scope.otherRadars = [];
				$http({method: 'GET', url: '/radar/rest/technology/' + technology.id + '/radar?nocache=' + (new Date()).getTime()}).
				success(function(data, status, headers, config) {
					technology.otherRadars = [];
					for(var i = 0; i < data.length; i++) {
						technology.otherRadars.push(data[i]);
					}
				}).
				error(function(data, status, headers, config) {
					$log.log('failed to load user radars for technology with ID ' + technology.id);
				});
			}
		}
	});
	
	$scope.technologySelected = function(technology) {
		$scope.selectedTechnology = technology;
	};

});

techRadarControllers.controller('TechnologyCtrl', function ($scope, $http, $location, $routeParams, $log) {

	$scope.technology = null;
	$scope.ratings = [];
	$scope.otherRadars = [];

	if(typeof $routeParams.technologyid != 'undefined') {
		$http({method: 'GET', url: '/radar/rest/technology/' + $routeParams.technologyid + '?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			$scope.technology = data;
		}).
		error(function(data, status, headers, config) {
			$log.log('error getting technology with ID ' + $routeParams.technologyid);
		});

		$http({method: 'GET', url: '/radar/rest/technology/' + $routeParams.technologyid + '/user?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			for(var i = 0; i < data.length; i++) {
				$scope.ratings.push(data[i]);
				if(data[i].user === $scope.uid) {
					$scope.currentSkillLevel = data[i].skillLevel;
				}
			}
		}).
		error(function(data, status, headers, config) {
			$log.log('failed to load user rating for technology ' + newval.name);
		});

		// Load technology radars
		$http({method: 'GET', url: '/radar/rest/technology/' + $routeParams.technologyid + '/radar?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			for(var i = 0; i < data.length; i++) {
				$scope.otherRadars.push(data[i]);
			}
		}).
		error(function(data, status, headers, config) {
			$log.log('failed to load radars for technology with ID ' + techId);
		});
	}

	$scope.selectSkillLevel = function(skillLevel) {
		$scope.currentSkillLevel = skillLevel;
		var toBeRemoved = -1;
		for(var i = 0; i < $scope.ratings.length; i++) {
			if($scope.ratings[i].user === $scope.uid) {
				toBeRemoved = i;
			}
		}
		if(toBeRemoved != -1) {
			$scope.ratings.splice(toBeRemoved, 1);
		}
		if(skillLevel != null) {
			$scope.ratings.push({user:$scope.uid,skillLevel:skillLevel});
		}

		$http.post('/radar/rest/technology/' + $scope.technology.id + '/user', {skillLevel:skillLevel}).
		success(function(data, status, headers, config) {
			$log.log('Successfully set technology \'' + $scope.technology.name + '\' (ID: ' + $scope.technology.id + ') to skill level \'' + skillLevel + '\'');
		}).
		error(function(data, status, headers, config) {
			$log.error('Failed to add technologies to radar');
			$log.error(data);
		});
	};

});

techRadarControllers.controller('RadarsCtrl', function ($scope, $http, $location, $routeParams, $modal, $log) {

	$scope.radars = [];

	$scope.newRadarVisible = false;

	$scope.onRadarCreated = function(radar) {
		$scope.newRadarVisible = false;
		$scope.radars.push(radar);
	};

	$http({method: 'GET', url: '/radar/rest/radar?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		$scope.radars = data;
	}).
	error(function(data, status, headers, config) {
		$log.log('error');
	});

});

techRadarControllers.controller('RadarCtrl', function ($scope, $http, $location, $routeParams, $log) {

	$scope.uploadingTechnologies = false;
	$scope.errors = [];
	$scope.msgs = [];

	$scope.selectedQuad = $routeParams.quadrant;

	if($scope.selectedQuad == null || typeof $scope.selectedQuad === "undefined"){
		$scope.selectedQuad = "";
	}

	$('#fileinput').on('change', function(ev){
		$scope.$apply(function(){
			$scope.uploadingTechnologies = true;
			$scope.errors = [];
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
					} else {
						for(var i = 0; i < frameWindow.techRadarData.errors.length; i++) {
							$scope.errors.push(frameWindow.techRadarData.errors[i]);
						}
					}
				});
			} else {
				window.setTimeout(checkFrame, 2000);
			}
		};

		window.setTimeout(checkFrame, 2000);
	});

	$scope.exportSvg = function(id) {
		var form = document.getElementById('theForm');
		form['id'].value = id;
		form.submit();
	};

	$scope.exportCsv = function(id) {
		var form = document.getElementById('csvExportForm');
		form['id'].value = id;
		form.submit();
	};

	$scope.doCsvExport = function ( radarId ) {
		$http({method: 'GET', url: '/radar/export/csv?id=' + radarId + '&nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			$log.log(done);
		}).
		error(function(data, status, headers, config) {
			$log.log('error');
		});
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

	var quadrantColours = ['#3DB5BE', '#83AD78', '#E88744', '#8D2145'];
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

	$scope.skillLevelSelected = function(technology, skillLevel) {
		$http.post('/radar/rest/technology/' + technology.id + '/user', {skillLevel:skillLevel}).
		success(function(data, status, headers, config) {
			$log.log('Successfully set technology \'' + technology.name + '\' (ID: ' + technology.id + ') to skill level \'' + skillLevel + '\'');
		}).
		error(function(data, status, headers, config) {
			$log.error('Failed to add technologies to radar');
			$log.error(data);
		});
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
						name: row.name,
						r: arcWidths[theRadar.radar.arcs.length],
						color: arcColours[theRadar.radar.arcs.length]
				};
				theRadar.arcMap[row.name] = arc;
				theRadar.radar.arcs.push(arc);
			})(theRadar.maturities[i]);
		}

		for(var i = 0; i < theRadar.techGroupings.length; i++) {
			(function(row){
				quadrant = {
						id: row.name,
						name: row.name,
						color: quadrantColours[theRadar.radar.quadrants.length],
						items: []
				};
				theRadar.quadrantMap[row.name] = quadrant;
				theRadar.radar.quadrants.push(quadrant);
			})(theRadar.techGroupings[i]);
		}

		if(theRadar.technologies!=null && typeof theRadar.technologies!='undefined') {
			for(var i = 0; i < theRadar.technologies.length; i++) {
				(function(row){
					var customerStrategic = row.customerStrategic;
					var newItem = {
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
					theRadar.quadrantMap[row.techGrouping].items.push(newItem);
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

techRadarControllers.controller('ModalInstanceCtrl', function ($scope, $modalInstance, items) {

	$scope.items = items;
	
	$scope.selected = {
			item: $scope.items[0]
	};

	$scope.ok = function () {
		$modalInstance.close($scope.selected.item);
	};

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	
});

