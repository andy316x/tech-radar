techRadarControllers.controller('CreateRadarCtrl', function ($scope, $http, $location, $routeParams, $modal, $log) {
	
	$scope.maturityOptions = [
	                             {name: 'phase out'},
	                             {name: 'maintain'},
	                             {name: 'invest'},
	                             {name: 'assess'},
	                             {name: 'watch'}
	                             ];
	$scope.techGroupingOptions = [];
	$scope.businessUnitOptions = [];
	
	$scope.selectGrouping = function(index, grouping) {
		if(index===1) {
			$scope.techGrouping1 = grouping;
		} else if(index===2) {
			$scope.techGrouping2 = grouping;
		} else if(index===3) {
			$scope.techGrouping3 = grouping;
		} else if(index===4) {
			$scope.techGrouping4 = grouping;
		}
	};

	$scope.techGrouping1 = 'Dev Tool';
	$scope.techGrouping2 = 'Dev Language';
	$scope.techGrouping3 = 'Platform';
	$scope.techGrouping4 = 'Solution Technology';
	$http.get('/radar/rest/quadrant').
	success(function(data, status, headers, config) {
		for(var i = 0; i < data.length; i++) {
			$scope.techGroupingOptions.push({label:data[i].name, value:data[i].name});
		}
	}).
	error(function(data, status, headers, config) {
		console.log('failed to load quadrants');
	});
	
//	$http({method: 'GET', url: '/radar/rest/maturity'}).
//	success(function(data, status, headers, config) {
//		var maturityOptions = [];
//		for(var i = 0; i < data.length; i++) {
//			maturityOptions.push({name:data[i].name});
//		}
//		$scope.maturityOptions = maturityOptions;
//	}).
//	error(function(data, status, headers, config) {
//		$log.error('Failed to load maturities');
//	});
	
	$http.get('/radar/rest/businessunit').
	success(function(data, status, headers, config) {
		for(var i = 0; i < data.length; i++) {
			if(i == 0) {
				$scope.newRadarBusinessUnit = data[i].name;
			}
			$scope.businessUnitOptions.push({label:data[i].name, value:data[i].name});
		}
	}).
	error(function(data, status, headers, config) {
		console.log('failed to load business units');
	});

	$scope.create = function() {
		var radar = {
				name: $scope.newRadarName,
				description: $scope.newRadarDescription,
				businessUnit: $scope.newRadarBusinessUnit,
				approved: false,
				published: false,
				majorVersion: 0,
				minorVersion: 0,
				dateCreated: new Date().getTime(),
				quadrants: [
				                {name: $scope.techGrouping1},
				                {name: $scope.techGrouping2},
				                {name: $scope.techGrouping3},
				                {name: $scope.techGrouping4}
				                ],
				                maturities: [
				                             {name: 'phase out'},
				                             {name: 'maintain'},
				                             {name: 'invest'},
				                             {name: 'assess'},
				                             {name: 'watch'}
				                             ]
		};
		$http.post('/radar/rest/radar', radar).
		success(function(data, status, headers, config) {
			$scope.go('/radar/' + data.id);
		}).
		error(function(data, status, headers, config) {
			$scope.errors = data;
		});
	};
	
});

