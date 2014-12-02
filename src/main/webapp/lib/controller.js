var techRadarControllers = angular.module('techRadarControllers', []);

techRadarControllers.directive('ngRadar', function ($routeParams) {
	return {
		restrict: 'A',
		scope: {
			radar: '=',
			selectedBlip: '='
		},
		link: function ($scope, element, attrs) {
			var el = element[0];
			
			var quadrantName = $routeParams.quadrant;
		
			var doDraw;
			if(quadrantName != null && typeof quadrantName !== "undefined"){
				doDraw = function(r) {
					$scope.theRadar = Radar.draw_Quadrant(el, r, quadrantName, {
						onbliphover: function(blip) {
							$scope.$apply(function(){
								$scope.selectedBlip = blip;
							});
						},
						onblipleave: function(blip) {
							$scope.$apply(function(){
								$scope.selectedBlip = null;
							});
						}
					});
				};
			}else{
				doDraw = function(r) {
					$scope.theRadar = Radar.draw(el, r, {
						onbliphover: function(blip) {
							$scope.$apply(function(){
								$scope.selectedBlip = blip;
							});
						},
						onblipleave: function(blip) {
							$scope.$apply(function(){
								$scope.selectedBlip = null;
							});
						}
					});
				};
			}

			if($scope.radar != null && typeof $scope.radar != 'undefined') {
				doDraw($scope.radar);
			}

			$scope.$watch('radar', function (newVal, oldVal, scope) {
				if(newVal != null && typeof newVal != 'undefined') {
					doDraw(newVal);
				}
			}, true);

			$scope.$watch('selectedBlip', function (newVal, oldVal, scope) {
				if($scope.theRadar != null && typeof $scope.theRadar != 'undefined') {
					if(newVal == null || typeof newVal == 'undefined') {
						$scope.theRadar.unselectBlip(newVal);
					} else {
						$scope.theRadar.selectBlip(newVal);
					}
				}
			}, true);

			window.addEventListener('resize', function() {
				doDraw($scope.radar);
			}, true);
		}
	};
});

techRadarControllers.directive('ngNewRadar', function ($http) {
	return {
		restrict: 'A',
		scope: {
			visible: '=',
			radarCreated: '&'
		},
		template: '<div class="modal fade">' +
		'  <div class="modal-dialog">' +
		'    <div class="modal-content">' +
		'      <div class="modal-header">' +
		'        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>' +
		'        <h4 class="modal-title">New Radar</h4>' +
		'      </div>' +
		'      <div class="modal-body">' +
		'        <div class="form-group">' +
		'          <label>Name</label>' +
		'          <input type="text" class="form-control" ng-model="name" placeholder="Enter name"></input>' +
		'        </div>' +
		'        <div class="form-group">' +
		'          <label>Tech Grouping 1</label>' +
		'          <select class="form-control" ng-model="techGrouping1" ng-options="techGroupingOption.value as techGroupingOption.label for techGroupingOption in techGroupingOptions"></select>' +
		'          </select>' +
		'        </div>' +
		'        <div class="form-group">' +
		'          <label>Tech Grouping 2</label>' +
		'          <select class="form-control" ng-model="techGrouping2" ng-options="techGroupingOption.value as techGroupingOption.label for techGroupingOption in techGroupingOptions"></select>' +
		'          </select>' +
		'        </div>' +
		'        <div class="form-group">' +
		'          <label>Tech Grouping 3</label>' +
		'          <select class="form-control" ng-model="techGrouping3" ng-options="techGroupingOption.value as techGroupingOption.label for techGroupingOption in techGroupingOptions"></select>' +
		'          </select>' +
		'        </div>' +
		'        <div class="form-group">' +
		'          <label>Tech Grouping 4</label>' +
		'          <select class="form-control" ng-model="techGrouping4" ng-options="techGroupingOption.value as techGroupingOption.label for techGroupingOption in techGroupingOptions"></select>' +
		'          </select>' +
		'        </div>' +
		'        <div ng-repeat="error in errors"><span class="text-danger">{{error}}</span></div>' +
		'      </div>' +
		'      <div class="modal-footer">' +
		'        <button type="button" ng-click="save()" class="btn btn-success">Create</button>' +
		'      </div>' +
		'    </div>' +
		'  </div>' +
		'</div>',
		link: function ($scope, element, attrs) {

			$scope.techGroupingOptions = [];
			$scope.techGrouping1 = 'Dev Tool';
			$scope.techGrouping2 = 'Dev Language';
			$scope.techGrouping3 = 'Platform';
			$scope.techGrouping4 = 'Solution Technology';
			$http.get('/radar/rest/techgrouping').
			success(function(data, status, headers, config) {
				for(var i = 0; i < data.length; i++) {
					$scope.techGroupingOptions.push({label:data[i].name, value:data[i].name});
				}
			}).
			error(function(data, status, headers, config) {
				$log.log('failed to load tech groupings');
			});

			$scope.$watch('visible', function (newVal, oldVal, scope) {
				if(newVal != oldVal) {
					if(newVal == true) {
						element.children(":first").modal('show');
					} else {
						element.children(":first").modal('hide');
					}
				}
			}, false);

			element.children(":first").on('hide.bs.modal', function(e) {
				$scope.visible = false;
			});

			$scope.save = function() {
				var radar = {
						name: $scope.name,
						techGroupings: [
						                {name: $scope.techGrouping1},
						                {name: $scope.techGrouping2},
						                {name: $scope.techGrouping3},
						                {name: $scope.techGrouping4}
						                ],
						                maturities: [
						                             {name: 'phase out'},
						                             {name: 'maintain'},
						                             {name: 'invest'},
						                             {name: 'assess'},
						                             {name: 'watch'}
						                             ]
				};
				$http.post('/radar/rest/radar', radar).
				success(function(data, status, headers, config) {
					$scope.radarCreated({radar: data});
				}).
				error(function(data, status, headers, config) {
					$scope.errors = data;
				});
			};

		}
	};
});

techRadarControllers.controller('TechnologyCtrl', function ($scope, $http, $location, $routeParams, $log) {

	// TODO I am being lazy, we need this in a directive
	var technologySuggestions = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: '/radar/rest/technology'
	});
	technologySuggestions.clearPrefetchCache();
	technologySuggestions.initialize();
	
	var radarSuggestions = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: '/radar/rest/radar'
	});
	radarSuggestions.clearPrefetchCache();
	radarSuggestions.initialize();

	$('.typeahead').typeahead({
		hint: true,
		highlight: true,
		minLength: 1
	},
	{
		name: 'technologies',
		displayKey: 'name',
		source: technologySuggestions.ttAdapter(),
		templates: {
			header: '<h3 class="search-title">Technologies</h3>'
		}
	},
	{
		name: 'radars',
		displayKey: 'name',
		source: radarSuggestions.ttAdapter(),
		templates: {
			header: '<h3 class="search-title">Radars</h3>'
		}
	})
	.bind('typeahead:selected', function (obj, datum) {
		$scope.$apply(function(){
			if(typeof datum.maturities != 'undefined') {
				$location.path('/radar/' + datum.id);
			} else {
				$location.path('/technology/' + datum.id);
			}
		});
    });
	
	$scope.technologies = [];
	$scope.technology = null;

	$http({method: 'GET', url: '/radar/rest/technology?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		for(var i = 0; i < data.length; i++) {
			$scope.technologies.push(data[i]);
		}
	}).
	error(function(data, status, headers, config) {
		$log.log('error getting technology list');
	});

	if(typeof $routeParams.technologyid != 'undefined') {
		$http({method: 'GET', url: '/radar/rest/technology/' + $routeParams.technologyid + '?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			$scope.technology = data;
		}).
		error(function(data, status, headers, config) {
			$log.log('error getting technology with ID ' + $routeParams.technologyid);
		});
	}

});

techRadarControllers.controller('RadarCtrl', function ($scope, $http, $location, $routeParams, $log) {

	// TODO I am being lazy, we need this in a directive
	var technologySuggestions = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: '/radar/rest/technology'
	});
	technologySuggestions.clearPrefetchCache();
	technologySuggestions.initialize();
	
	$scope.selectedQuad = $routeParams.quadrant;
	
	if($scope.selectedQuad == null || typeof $scope.selectedQuad === "undefined"){
		$scope.selectedQuad = "";
	}
	
	var radarSuggestions = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: '/radar/rest/radar'
	});
	radarSuggestions.clearPrefetchCache();
	radarSuggestions.initialize();

	$('.typeahead').typeahead({
		hint: true,
		highlight: true,
		minLength: 1
	},
	{
		name: 'technologies',
		displayKey: 'name',
		source: technologySuggestions.ttAdapter(),
		templates: {
			header: '<h3 class="search-title">Technologies</h3>'
		}
	},
	{
		name: 'radars',
		displayKey: 'name',
		source: radarSuggestions.ttAdapter(),
		templates: {
			header: '<h3 class="search-title">Radars</h3>'
		}
	})
	.bind('typeahead:selected', function (obj, datum) {
		$scope.$apply(function(){
			if(typeof datum.maturities != 'undefined') {
				$scope.go('/radar/' + datum.id);
			} else {
				$scope.go('/technology/' + datum.id);
			}
		});
    });



	$('#fileinput').on('change', function(ev){
		var form = document.getElementById('uploadform');
		form['id'].value = $scope.selectedRadar.id;
		form.submit();

		var checkFrame = function() {
			var frameWindow = document.getElementById('theframe').contentWindow;
			console.log(frameWindow);
			if(typeof frameWindow.techRadarData != 'undefined') {
				console.log(frameWindow.techRadarData);
				$scope.$apply(function(){
					mapRadar(frameWindow.techRadarData);
				});
				console.log('done');
			} else {
				console.log('No data, try again in a bit');
				window.setTimeout(checkFrame, 500);
			}
		};

		window.setTimeout(checkFrame, 500);
	});

	$scope.newRadarVisible = false;

	$scope.onRadarCreated = function(radar) {
		$scope.newRadarVisible = false;
		$scope.go('/radar/' + radar.id);
	};

	$scope.go = function ( path ) {
		$location.path( path );
	};

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
			$scope.go('/radar/' + $scope.selectedRadar.id);
		}).
		error(function(data, status, headers, config) {
			$log.error('Failed to add technologies to radar');
			$log.error(data);
		});
	};

	$scope.doDelete = function ( radarId ) {
		$http({method:'DELETE', url:'/radar/rest/radar/' + radarId}).
		success(function(data, status, headers, config) {
			$scope.go('/radar');
		}).
		error(function(data, status, headers, config) {
			$log.error('Failed to delete radar');
			$log.error(data);
		});
	};

	$scope.selectedId = document.radar.radarId;

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

	$http({method: 'GET', url: '/radar/rest/radar?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		$scope.radars = data;
	}).
	error(function(data, status, headers, config) {
		$log.log('error');
	});

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

	if(!($routeParams.radarid == null || typeof $routeParams.radarid === "undefined")) {
		loadRadar($routeParams.radarid);
	}

});
