techRadarControllers.controller('TechnologiesCtrl', ['$scope', '$http', '$location', '$routeParams', '$log', 'trBannerService', function ($scope, $http, $location, $routeParams, $log, trBannerService) {

	$scope.techGroupings = [];

	trBannerService.message = "This view is a representation of the technology hub.";
	trBannerService.contact = "contact@techradar.net";
	
	$http({method: 'GET', url: '/radar/rest/technology?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		$scope.techGroupings = [];
		var techGroupingToIndexMap = {};
		if(data.length > 0) {
			$scope.selectedTechnology = data[0];
			var currentIndex = -1;
			for(var i = 0; i < data.length; i++) {
				var techGrouping = {name:data[i].techGrouping,technologies:[]};
				var index = techGroupingToIndexMap[techGrouping.name];
				if(typeof index == 'undefined') {
					currentIndex++;
					techGroupingToIndexMap[techGrouping.name] = currentIndex;
					$scope.techGroupings.push(techGrouping);
					index = currentIndex;
					
				}
				$scope.techGroupings[index].technologies.push(data[i]);
			}
			
			var count = 0;
			for(var i = 0; i < $scope.techGroupings.length; i++) {
				for(var j = 0; j < $scope.techGroupings[i].technologies.length; j++) {
					$scope.techGroupings[i].technologies[j].ind = ++count;
				}
			}
		}
	}).
	error(function(data, status, headers, config) {
		$log.log('error getting technology list');
	});
	
	$scope.$watch('selectedTechnology', function (newVal, oldVal, scope) {
		if(typeof newVal != 'undefined') {
			var technology = newVal;
			if(typeof technology.ratings == 'undefined') {
				$http({method: 'GET', url: '/radar/rest/technology/' + technology.id + '/user?nocache=' + (new Date()).getTime()}).
				success(function(data, status, headers, config) {
					technology.ratings = [];
					for(var i = 0; i < data.length; i++) {
						technology.ratings.push(data[i]);
					}
				}).
				error(function(data, status, headers, config) {
					$log.log('failed to load user rating for technology ' + technology.name);
				});
				
				// Load technology radars
				$scope.otherRadars = [];
				$http({method: 'GET', url: '/radar/rest/technology/' + technology.id + '/radar?nocache=' + (new Date()).getTime()}).
				success(function(data, status, headers, config) {
					technology.otherRadars = [];
					for(var i = 0; i < data.length; i++) {
						technology.otherRadars.push(data[i]);
						$scope.otherRadars.push(data[i]);
					}
				}).
				error(function(data, status, headers, config) {
					$log.log('failed to load user radars for technology with ID ' + technology.id);
				});
			}
		}
	});
	
	$scope.technologySelected = function(technology) {
		$scope.selectedTechnology = technology;
	};

	$scope.$on('$destroy', function destroy() {
		trBannerService.message='';
	});

}]);
