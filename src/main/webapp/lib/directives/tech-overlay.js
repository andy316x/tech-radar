//TODO - don't use global
techRadarDirectives.directive('trTechOverlay', function () {
	return {
		restrict: 'E',
        templateUrl: 'templates/tech-overlay.html',
		scope: {
            side: "@",
            quads: "=",
		},
		link: function ($scope, element, attrs) {
		}
	};
});
