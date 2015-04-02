var techRadarControllers = angular.module('techRadarControllers', ['ui.bootstrap', 'ngAnimate']);

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
	
	
	// TODO create a modal service, this is hack here 
	// to remove the modal backdrop on navigation
	$scope.$on('$locationChangeStart', function(event, newUrl) {
		$('.modal-backdrop').hide();
		$('.modal-open').removeClass('modal-open'); 
	});

});

techRadarControllers.controller('TechnologiesCtrl', function ($scope, $http, $location, $routeParams, $log) {

	$scope.techGroupings = [];

	$http({method: 'GET', url: '/radar/rest/technology?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		$scope.techGroupings = [];
		var techGroupingToIndexMap = {};
		if(data.length > 0) {
			$scope.selectedTechnology = data[0];
			var currentIndex = -1;
			for(var i = 0; i < data.length; i++) {
				var techGrouping = {name:data[i].techGrouping,technologies:[]};
				var index = techGroupingToIndexMap[techGrouping.name];
				if(typeof index == 'undefined') {
					currentIndex++;
					techGroupingToIndexMap[techGrouping.name] = currentIndex;
					$scope.techGroupings.push(techGrouping);
					index = currentIndex;
					
				}
				$scope.techGroupings[index].technologies.push(data[i]);
			}
			
			var count = 0;
			for(var i = 0; i < $scope.techGroupings.length; i++) {
				for(var j = 0; j < $scope.techGroupings[i].technologies.length; j++) {
					$scope.techGroupings[i].technologies[j].ind = ++count;
				}
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
	
	$scope.filter = 'All';

	$scope.newRadarVisible = false;

	$scope.onRadarCreated = function(radar) {
		$scope.newRadarVisible = false;
		$scope.radars.push(radar);
	};
	
	var filterPredicates = [{
		doFilter: function(radar) {
			if($scope.filter == 'All') {
				return true;
			} else if($scope.filter == 'My Radars') {
				return $scope.uid === radar.createdBy;
			} else {
				if($scope.filter == radar.businessUnit) {
					return true;
				} else {
					return false;
				}
			}
		}
	},{
		doFilter: function(radar) {
			if(radar.published === true) {
				return true;
			} else {
				return $scope.uid === radar.createdBy;
			}
		}
	}];
	
	$scope.filterRadar = function(criteria) {
		return function(radar) {
			for(var i = 0; i < filterPredicates.length; i++) {
				if(!filterPredicates[i].doFilter(radar)) {
					return false;
				}
			}
			return true;
		};
	};

	$http({method: 'GET', url: '/radar/rest/radar?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		$scope.radars = data;
	}).
	error(function(data, status, headers, config) {
		$log.error('Error getting radars');
	});
	
	$http({method: 'GET', url: '/radar/rest/businessunit'}).
	success(function(data, status, headers, config) {
		$scope.businessUnits = data;
	}).
	error(function(data, status, headers, config) {
		$log.error('Error getting business units');
	});

});

techRadarControllers.controller('RadarCtrl', function ($scope, $http, $location, $routeParams, $modal, $log) {

	$scope.uploadingTechnologies = false;
	$scope.showShare = false;
	$scope.errors = [];
	$scope.warnings = [];
	$scope.msgs = [];
	
	$scope.addTechVisible = false;
	
	$scope.theUrl = window.location.href;

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
		
		console.log(techs);
		
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

techRadarControllers.controller('SkillsCtrl', function ($scope, $http, $location, $routeParams, $modal, $log) {

	$scope.skillLevels = [];
	
	$http({method: 'GET', url: '/radar/rest/me/skillLevel?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		for(var i = 0; i < data.length; i++) {
			$scope.skillLevels.push(data[i]);
		}
	}).
	error(function(data, status, headers, config) {
		$log.error('Failed to load user technologies');
	});
	

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

