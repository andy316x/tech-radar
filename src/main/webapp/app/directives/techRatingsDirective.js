techRadarDirectives.directive('ngTechRatings', function ($http) {
	return {
		restrict: 'E',
		scope: {
			ratings: '='
		},
		templateUrl: 'templates/tech-ratings.html',
		link: function ($scope, element, attrs) {

			$scope.getWidth = function () {
				return element[0].offsetWidth;
			};

			$scope.$watch('ratings', function (newVal, oldVal, scope) {
				repopulateRatings(newVal);
			}, true);

			var repopulateRatings = function(ratings) {

				$scope.watching = [];
				$scope.learning = [];
				$scope.competent= [];
				$scope.expert = [];
				$scope.leader = [];

				if(ratings != null && typeof ratings != 'undefined') {
					ratings.forEach(function(rating){
						if(rating.skillLevel){
							$scope[rating.skillLevel.toLowerCase()].push(rating);
						}
					});
				}
				
				var max = Math.max(
						$scope.watching.length,
						$scope.learning.length,
						$scope.competent.length,
						$scope.expert.length,
						$scope.leader.length
				);

				$scope.max = max===0 ? 1 : max;
			};

			repopulateRatings($scope.ratings);
		}
	};
});