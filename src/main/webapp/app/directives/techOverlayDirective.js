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
			$scope.doFocus = function(tech) {
				$scope.focus({tech:tech});
			};
		}
	};
});
