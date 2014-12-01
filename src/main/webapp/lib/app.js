var techRadarApp = angular.module('techRadar', [
'ngRoute',
'techRadarControllers'
]);

techRadarApp.config(['$routeProvider',function($routeProvider) {
	$routeProvider
	
	// Radar
      .when('/radar', {
        templateUrl: 'partials/radar.jsp',
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
      
    // Technology
      .when('/technology', {
        templateUrl: 'partials/technology.jsp',
        controller: 'TechnologyCtrl'
      })
      .when('/technology/:technologyid', {
        templateUrl: 'partials/viewtechnology.jsp',
        controller: 'TechnologyCtrl'
      })
      
      // Default
      .otherwise({
		templateUrl: 'partials/radar.jsp',
		controller: 'RadarCtrl'
	});
}]);