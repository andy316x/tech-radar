var techRadarApp = angular.module('techRadar', [
'ngRoute',
'techRadarControllers',
'techRadarDirectives'
]);

techRadarApp.config(['$routeProvider',function($routeProvider) {
	$routeProvider

	// Radar
	.when('/radar', {
		templateUrl: 'partials/radar.html',
		controller: 'RadarsCtrl'
	})
	.when('/radar/:radarid', {
		templateUrl: 'partials/viewradar.html',
		controller: 'RadarCtrl'
	})
	.when('/radar/:radarid/edit', {
		templateUrl: 'partials/editradar.html',
		controller: 'RadarCtrl'
	})

	// Technology
	.when('/technology', {
		templateUrl: 'partials/technology.html',
		controller: 'TechnologiesCtrl'
	})
	.when('/technology/:technologyid', {
		templateUrl: 'partials/viewtechnology.html',
		controller: 'TechnologyCtrl'
	})
	
	// Skills
	.when('/skill', {
		templateUrl: 'partials/skill.html',
		controller: 'SkillsCtrl'
	})

	// Default
	.otherwise({redirectTo: "/radar"});
	
}]);
