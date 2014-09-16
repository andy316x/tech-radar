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

			doDraw($scope.radar);

			$scope.$watch('radar', function (newVal, oldVal, scope) {
				if(newVal) {
					doDraw(newVal);
				}
			}, true);

			$scope.$watch('selectedBlip', function (newVal, oldVal, scope) {
				console.log(newVal);
				if(newVal == null || typeof newVal == 'undefined') {
					$scope.theRadar.unselectBlip(newVal);
				} else {
					$scope.theRadar.selectBlip(newVal);
				}
			}, true);

			window.addEventListener('resize', function() {
				doDraw($scope.radar);
			}, true);
		}
	};
});

techRadarApp.controller('RadarCtrl', function ($scope) {

	var quadrantColours = ['#3DB5BE', '#83AD78', '#E88744', '#8D2145'];
	var arcColours = ['#BFC0BF', '#CBCCCB', '#D7D8D6', '#E4E5E4'];
	var arcWidths = [150, 125, 75, 50];

	$scope.mouseOver = function(item) {
		$scope.selectedItem = angular.copy(item);
	};

	$scope.mouseOut = function(item) {
		$scope.selectedItem = null;
	};


	$('#theFile').on('change', function(evt){



		var f = evt.target.files[0];

		if (f) {
			var r = new FileReader();
			r.onload = function(e) { 

				$scope.$apply(function(){
					var arcMap = {};
					var quadrantMap = {};
					$scope.radar.arcs = [];
					$scope.radar.quadrants = [];

					var contents = e.target.result;
					var lines = contents.split('\n');
					for(var i = 0; i < lines.length; i++) {
						var readLine = (function(line) {
							if(line.trim() != '') {
								var cells = line.split(',');
								if(cells.length == 8) {
									var technologyName = cells[0].trim().substring(1, cells[0].trim().length-1);
									var quadrantName = cells[1].trim().substring(1, cells[1].trim().length-1);
									var arcName = cells[2].trim().substring(1, cells[2].trim().length-1);
									var radius = parseInt(cells[3].trim().substring(1, cells[3].trim().length-1));
									var theta = parseInt(cells[4].trim().substring(1, cells[4].trim().length-1));
									var movement = cells[5].trim().substring(1, cells[5].trim().length-1);
									var blipSize = parseInt(cells[6].trim().substring(1, cells[6].trim().length-1));
									var url = cells[7].trim().substring(1, cells[7].trim().length-1);

									var arc = arcMap[arcName];
									if(arc == null || typeof arc == 'undefined') {
										arc = {
												id: arcName,
												name: arcName,
												r: arcWidths[$scope.radar.arcs.length],
												color: arcColours[$scope.radar.arcs.length]
										};
										arcMap[arcName] = arc;
										$scope.radar.arcs.push(arc);
									}

									var quadrant = quadrantMap[quadrantName];
									if(quadrant == null || typeof quadrant == 'undefined') {
										quadrant = {
												id: quadrantName,
												name: quadrantName,
												color: quadrantColours[$scope.radar.quadrants.length],
												items: []
										};
										quadrantMap[quadrantName] = quadrant;
										console.log(quadrant);
										console.log($scope.radar.quadrants);
										$scope.radar.quadrants.push(quadrant);
									}

									quadrant.items.push({
										id: i+1,
										name: technologyName,
										arc: arcName,
										pc: {
											r: radius,
											t: Math.floor((Math.random() * 90) + 1)
										},
										movement: movement,
										url: url
									});

								} else {
									console.log('invalid row');
								}
							}
						})(lines[i]);

					}
				});
			};
			r.readAsText(f);
		} else { 
			alert("Failed to load file");
		}
	});

	$scope.radar = {
			arcs: [],
			quadrants: []
	};

});