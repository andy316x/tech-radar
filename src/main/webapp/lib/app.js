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
        templateUrl: 'partials/createradar.jsp',
        controller: 'RadarCtrl'
      })
      .when('/radar/:radarid', {
        templateUrl: 'partials/viewradar.jsp',
        controller: 'RadarCtrl'
      })
      .when('/radar/:radarid/edit', {
        templateUrl: 'partials/editradar.jsp',
        controller: 'RadarCtrl'
      })
      .otherwise({
		templateUrl: 'partials/radar.jsp',
		controller: 'RadarCtrl'
	});
}]);