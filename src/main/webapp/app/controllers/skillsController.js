techRadarControllers.controller('SkillsCtrl', ['$scope', '$http', '$location', '$routeParams', '$modal', '$log', 'trBannerService', function ($scope, $http, $location, $routeParams, $modal, $log, trBannerService) {

	trBannerService.message = 'This view is a representation of the skills profile.';
	trBannerService.contact = "contact@techradar.net";
	
	$scope.skillLevels = [];
	$scope.activeGrouping = null;
	
	$http({method: 'GET', url: '/radar/rest/techgrouping?nocache=' + (new Date()).getTime()}).
	success(function(data) {
		$scope.techGroupings = data;
	}).
	error(function(data, status, headers, config) {
		$log.error('Failed to load technology groupings');
	});
	
	$http({method: 'GET', url: '/radar/rest/me/skillLevel?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		for(var i = 0; i < data.length; i++) {
			$scope.skillLevels.push(data[i]);
		}
	}).
	error(function(data, status, headers, config) {
		$log.error('Failed to load user technologies');
	});
	
	$scope.$on('$destroy', function destroy() {
		trBannerService.message='';
	});

}]);
