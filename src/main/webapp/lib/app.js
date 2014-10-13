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
		for(var j = 0; j < data.length; j++) {
			if($scope.selectedId == null) {
				$scope.selectedId = data[j].id;
			}
			(function(theRadar) {
				theRadar.arcMap = {};
				theRadar.quadrantMap = {};
				theRadar.radar = {
						arcs: [],
						quadrants: []
				};

				for(var i = 0; i < theRadar.technologies.length; i++) {
					(function(row){
						var arc = theRadar.arcMap[row.arcName];
						if(arc == null || typeof arc == 'undefined') {
							arc = {
									id: row.arcName,
									name: row.arcName,
									r: arcWidths[theRadar.radar.arcs.length],
									color: arcColours[theRadar.radar.arcs.length]
							};
							theRadar.arcMap[row.arcName] = arc;
							theRadar.radar.arcs.push(arc);
						}

						var quadrant = theRadar.quadrantMap[row.quadrantName];
						if(quadrant == null || typeof quadrant == 'undefined') {
							quadrant = {
									id: row.quadrantName,
									name: row.quadrantName,
									color: quadrantColours[theRadar.radar.quadrants.length],
									items: []
							};
							theRadar.quadrantMap[row.quadrantName] = quadrant;
							theRadar.radar.quadrants.push(quadrant);
						}
						
						var customerStrategic = row.customerStrategic;
						var newItem = {
							id: i+1,
							name: row.technologyName,
							show: false,
							arc: row.arcName,
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
						quadrant.items.push(newItem);
					})(theRadar.technologies[i]);
				}
			})(data[j]);
		}
		$scope.selectedRadar = $scope.radars[$scope.selectedId-1];
	}).
	error(function(data, status, headers, config) {
		$log.log('error');
	});

	$scope.radarSelect = function(r) {
		$scope.selectedRadar = r;
	};
	
	$scope.radar = {
			arcs: [],
			quadrants: []
	};

});