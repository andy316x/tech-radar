techRadarDirectives.directive('ngRadar', function ($routeParams) {
	return {
		restrict: 'E',
		scope: {
			radar: '=',
			selectedBlip: '=',
			centreIndex: '=',
			editable: '=',
			onBlipClicked: '&',
			onBlipMoved: '&'
		},
		link: function ($scope, element, attrs) {
            var el = element[0];
            var interactions = {
                onblipmove: function(blip) {
                    $scope.$apply(function(){
                        $scope.onBlipMoved({blip:blip});
                    });
                },
                onblipclick: function(blip) {
                    $scope.$apply(function(){
                        $scope.onBlipClicked({blip:blip});
                    });
                },
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
            };

			var doDraw = function(r) {
				if(!$scope.theRadar || !$scope.theRadar.blips.length){
					$scope.theRadar = new Radar(el, r, $scope.editable, interactions);
				}
                $scope.theRadar.draw();
            };

			if($scope.radar) {
				doDraw($scope.radar);
			}

			$scope.$watch('radar', function (newVal, oldVal, scope) {
				if(newVal) {
					doDraw(newVal);
				}
			}, true);

			$scope.$watch('selectedBlip', function (newVal, oldVal, scope) {
                //TODO - oldVal/scope parameters redundant?
				if($scope.theRadar) {
					if(newVal) {
						$scope.theRadar.unselectBlip(oldVal);
						$scope.theRadar.selectBlip(newVal);
					}
				}
			}, true);
			
			$scope.$watch('centreIndex', function (newVal, oldVal, scope) {
				if($scope.theRadar) {
					$scope.theRadar.zoom(newVal);
				}
			}, false);

			window.addEventListener('resize', function() {
				doDraw($scope.radar);
			}, true);
		}
	};
});