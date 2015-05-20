techRadarControllers.controller('SkillsCtrl', function ($scope, $http, $location, $routeParams, $modal, $log) {

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
	

});
