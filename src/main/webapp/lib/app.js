var techRadarApp = angular.module('techRadar', [
'ngRoute',
'techRadarControllers'
]);

techRadarApp.config(['$routeProvider',function($routeProvider) {
	$routeProvider.otherwise({
		templateUrl: 'partials/radar.jsp',
		controller: 'RadarCtrl'
	});
}]);