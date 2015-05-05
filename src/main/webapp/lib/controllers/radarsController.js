techRadarControllers.controller('RadarsCtrl', function ($scope, $http, $location, $routeParams, $modal, $log) {

	$scope.radars = [];
	
	$scope.filter = 'All';

	$scope.newRadarVisible = false;

	$scope.onRadarCreated = function(radar) {
		$scope.newRadarVisible = false;
		$scope.radars.push(radar);
	};
	
	var filterPredicates = [{
		doFilter: function(radar) {
			if($scope.filter == 'All') {
				return true;
			} else if($scope.filter == 'My Radars') {
				return $scope.uid === radar.createdBy;
			} else {
				if($scope.filter == radar.businessUnit) {
					return true;
				} else {
					return false;
				}
			}
		}
	},{
		doFilter: function(radar) {
			if(radar.published === true) {
				return true;
			} else {
				return $scope.uid === radar.createdBy;
			}
		}
	}];
	
	$scope.filterRadar = function(criteria) {
		return function(radar) {
			for(var i = 0; i < filterPredicates.length; i++) {
				if(!filterPredicates[i].doFilter(radar)) {
					return false;
				}
			}
			return true;
		};
	};

	$http({method: 'GET', url: '/radar/rest/radar?nocache=' + (new Date()).getTime()}).
	success(function(data, status, headers, config) {
		$scope.radars = data;
	}).
	error(function(data, status, headers, config) {
		$log.error('Error getting radars');
	});
	
	$http({method: 'GET', url: '/radar/rest/businessunit'}).
	success(function(data, status, headers, config) {
		$scope.businessUnits = data;
	}).
	error(function(data, status, headers, config) {
		$log.error('Error getting business units');
	});

});
