//TODO - don't use global
techRadarDirectives.directive('trTechOverlay', function () {
	return {
		restrict: 'E',
        templateUrl: 'templates/tech-overlay.html',
		scope: {
            side: "@",
            quads: "=",
            focus: "&",
            selectedTechId: "="
		},
		link: function ($scope, element, attrs) {
			$scope.expandedTech = {id: null};
			$scope.expandTech = function(techId){
				if($scope.expandedTech.id == techId){
					$scope.expandedTech.id = null;
				}else{
					$scope.expandedTech.id = techId;	
				}
			};
			$scope.doFocus = function(tech) {
				$scope.focus({tech:tech});
			};
		}
	};
});
