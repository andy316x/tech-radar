techRadarDirectives.directive('ngAddTech', function ($http) {
	return {
		restrict: 'A',
		scope: {
			visible: '=',
			selectedRadar: '=',
			onSave: '&',
			onCancel: '&'
		},
		templateUrl: 'templates/add-tech.html',
		link: function ($scope, element, attrs) {
			$scope.stage = 1;
			
			function getTechFromRadar(techName){
				if ($scope.selectedRadar.technologies == undefined){
					return undefined;
				}
				
				for(var i = 0; i < $scope.selectedRadar.technologies.length; i++) {
					if ($scope.selectedRadar.technologies[i].technology === techName){
						return $scope.selectedRadar.technologies[i];
					}
				}
				return undefined;
			};
			
			function isInRadar(techName){
				return getTechFromRadar(techName) != undefined;
			};
			
			function setTechnologySelection() {
				$scope.technologies.forEach(function(t){
					var techInRadar = getTechFromRadar(t.name);
					if(typeof techInRadar !== 'undefined') {
						t.selected = true;
						t.maturity = techInRadar.maturity;
						t.quadrant = techInRadar.quadrant;
					} else {
						t.selected = false;
						if(typeof $scope.maturityOptions != 'undefined' && $scope.maturityOptions.length > 0) {
							t.maturity = $scope.maturityOptions[0].value;
						}
						for(var j = 0; j < $scope.quadrantOptions.length; j++) {
							if(t.techGrouping == $scope.quadrantOptions[j].value) {
								t.quadrant = t.techGrouping;
							}
						}
					}
				})
			};
			
			$scope.technologies = [];
						
			$scope.technologySelected = function(technology) {
				technology.selected = !(technology.selected);
			};
			
			$scope.countSelected = function(technologies) {
                return technologies.filter(function(curr){
                    return curr.selected;
                }).length;
			};
			
			function updateOptions() {
				if($scope.selectedRadar){
					if($scope.selectedRadar.maturities) {
						$scope.maturityOptions = $scope.selectedRadar.maturities.map(function(m){
                            return {label:m.name, value:m.name};
                        });
					}
					
					if($scope.selectedRadar.quadrants) {
						$scope.quadrantOptions = $scope.selectedRadar.quadrants.map(function(quadrant){
                            return {label:quadrant.name, value:quadrant.name};
                        });
					}
				}
			};
			
			$scope.$watch('selectedRadar', function (newVal, oldVal, scope) {
				updateOptions();
				setTechnologySelection();
			}, false);

			$scope.$watch('visible', function (newVal, oldVal, scope) {
				if(newVal != oldVal) {
					if(newVal) {
						element.children(":first").modal('show');
						if(!$scope.fetched){
							$http({method: 'GET', url: '/radar/rest/technology?nocache=' + (new Date()).getTime()}).
							success(function(data) {
								$scope.fetched = true;
				                $scope.technologies = data;
								setTechnologySelection();
							}).
							error(function(data) {
								console.log('error getting technology list');
							});
						}
					} else {
						element.children(":first").modal('hide');
					}
				}
			}, false);

			element.children(":first").on('hide.bs.modal', function(e) {
				$scope.visible = false;
			});
			
			$scope.doSave = function() {
				$scope.errors = [];
				var newTechs = [];
                $scope.technologies.forEach(function(tech){
                    if(tech.selected){
                        var valid = true;
						if(!(tech.maturity)) {
							valid = false;
							$scope.errors.push({text:tech.name + ' has no maturity set'});
						}
						if(!(tech.quadrant)) {
							valid = false;
							$scope.errors.push({text:tech.name + ' has no tech grouping set'});
						}
						if(valid) {
							newTechs.push({
								technology:tech.name,
								maturity:tech.maturity,
								quadrant:tech.quadrant,
								techGrouping:tech.techGrouping
							});
						}
                    }
                });
				if(!($scope.errors.length)) {
					$scope.onSave({techs:newTechs});
				}
			};
			
			$scope.doCancel = function() {
				$scope.onCancel();
			};

		}
	};
});
