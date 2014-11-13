var techRadarApp = angular.module('techRadar', []);

techRadarApp.directive('ngRadar', function () {
	return {
		restrict: 'A',
		scope: {
			radar: '=',
			selectedBlip: '='
		},
		link: function ($scope, element, attrs) {
			var el = element[0];
			var doDraw = function(r) {
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

techRadarApp.controller('RadarCtrl', function ($scope, $http, $log) {

	$scope.selectedId = document.radar.radarId;

	var quadrantColours = ['#3DB5BE', '#83AD78', '#E88744', '#8D2145'];
	var arcColours = ['#BFC0BF', '#CBCCCB', '#D7D8D6', '#E4E5E4'];
	var arcWidths = [150, 125, 75, 50];

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

	$http({method: 'GET', url: '/radar/rest/service?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		$scope.radars = data;
		if($scope.selectedId == null) {
			if(data.length > 0) {
				$scope.selectedId = data[0].id;
				loadRadar();
			}
		}
	}).
	error(function(data, status, headers, config) {
		$log.log('error');
	});
	
	var loadRadar = function() {
		$http({method: 'GET', url: '/radar/rest/service/' + $scope.selectedId + '?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			var theRadar = data;
			theRadar.arcMap = {};
			theRadar.quadrantMap = {};
			theRadar.radar = {
					arcs: [],
					quadrants: []
			};
			
			for(var i = 0; i < theRadar.xs.length; i++) {
				(function(row){
					var arc = theRadar.arcMap[row.name];
					if(arc == null || typeof arc == 'undefined') {
						arc = {
								id: row.name,
								name: row.name,
								r: arcWidths[theRadar.radar.arcs.length],
								color: arcColours[theRadar.radar.arcs.length]
						};
						theRadar.arcMap[row.name] = arc;
						theRadar.radar.arcs.push(arc);
					}
				})(theRadar.xs[i].arc);
			}
			
			for(var i = 0; i < theRadar.ys.length; i++) {
				(function(row){
					var quadrant = theRadar.quadrantMap[row.name];
					if(quadrant == null || typeof quadrant == 'undefined') {
						quadrant = {
								id: row.name,
								name: row.name,
								color: quadrantColours[theRadar.radar.quadrants.length],
								items: []
						};
						theRadar.quadrantMap[row.name] = quadrant;
						theRadar.radar.quadrants.push(quadrant);
					}
				})(theRadar.ys[i].quadrant);
			}

			for(var i = 0; i < theRadar.zs.length; i++) {
				(function(row){
					var customerStrategic = row.technology.customerStrategic;
					var newItem = {
							id: i+1,
							name: row.technology.name,
							show: false,
							arc: row.x.arc.name,
							pc: {
								r: row.radius,
								t: Math.floor((Math.random() * 90) + 1)
							},
							movement: row.movement,
							description: row.technology.description,
							detailUrl: row.technology.detailUrl,
							customerStrategic: customerStrategic,
							url: row.technology.url
					};
					theRadar.quadrantMap[row.y.quadrant.name].items.push(newItem);
				})(theRadar.zs[i]);
			}
			$scope.selectedRadar = theRadar;
		}).
		error(function(data, status, headers, config) {
			$log.log('error');
		});
	};

	if($scope.selectedId != null) {
		loadRadar();
	}

});