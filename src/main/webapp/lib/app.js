var techRadarApp = angular.module('techRadar', [
'ngRoute',
'techRadarControllers'
]);

techRadarApp.config(['$routeProvider',function($routeProvider) {
	$routeProvider
      .when('/radar', {
        templateUrl: 'partials/radar.jsp',
        controller: 'RadarCtrl'
      })
      .when('/radar/new', {
        templateUrl: 'partials/radar.jsp',
        controller: 'RadarCtrl'
      })
      .when('/radar/:radarid', {
        templateUrl: 'partials/radar.jsp',
        controller: 'RadarCtrl'
      })
      .when('/radar/:radarid/edit', {
        templateUrl: 'partials/radar.jsp',
        controller: 'RadarCtrl'
      })
      .otherwise({
		templateUrl: 'partials/radar.jsp',
		controller: 'RadarCtrl'
	});
}]);