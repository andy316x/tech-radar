techRadarDirectives.directive('ngTechMaturities', function ($window) {
	return {
		restrict: 'E',
		scope: {
			selectedTech: "=",
		},
		templateUrl: 'templates/tech-maturities.html',
		link: function ($scope, element, attrs) {
			var aggregateLimit = 4;
			$scope.imageDimension = 64;
			
			$scope.$watch("selectedTech", function (n, o) {
				layout();
			}, true);
			
			$scope.otherRefs = [];
			var maturityX = {
					"watch":15,
					"assess":33,
					"invest":54,
					"maintain":73,
					"phase out":95,
			}
			var maturityY;
			
			function nextY(maturity){
				return maturityY[maturity] += 7;
			}
			
			function currentMaturityCount(maturity){
				return $scope.otherRefs.reduce(function(prev, ref){
					if(ref.maturity === maturity){
						return prev + 1;
					}
				}, 0);
			}
			
			function addRef(newRef){
				var toAdd = newRef;
				if(currentMaturityCount(newRef.maturity) === aggregateLimit){
					var matching = $scope.otherRefs.filter(function(ref){
						return ref.maturity == newRef.maturity;
					});
					$scope.otherRefs = $scope.otherRefs.filter(function(ref){
						return ref.maturity !== newRef.maturity;
					})
					toAdd = {
							maturity: newRef.maturity,
							x: maturityX[newRef.maturity],
							y: 15,
							radars: []
					};
					matching.forEach(function(m){
						toAdd.radars.push(m.radar);
					});
					toAdd.radars.push(newRef.radar);
				}
				$scope.otherRefs.push(toAdd);
			}
			
			function layout(){
				maturityY =	{
						"watch":-3,
						"assess":-3,
						"invest":-3,
						"maintain":-3,
						"phase out":-3,
				};
				$scope.otherRefs = [];
				if($scope.selectedTech && $scope.selectedTech.otherRadars){
					$scope.selectedTech.otherRadars.forEach(function(other){
						addRef({
							maturity: other.maturity,
							x: maturityX[other.maturity],
							y: nextY(other.maturity),
							radar: other.radarName
						});
					});
				}
			};
			layout();
		}
	};
});
