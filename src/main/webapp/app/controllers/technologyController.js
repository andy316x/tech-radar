techRadarControllers.controller('TechnologyCtrl', function ($scope, $http, $location, $routeParams, $log) {

	$scope.technology = null;
	$scope.ratings = [];
	$scope.otherRadars = [];

	if(typeof $routeParams.technologyid != 'undefined') {
		$http({method: 'GET', url: '/radar/rest/technology/' + $routeParams.technologyid + '?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			$scope.technology = data;
		}).
		error(function(data, status, headers, config) {
			$log.log('error getting technology with ID ' + $routeParams.technologyid);
		});

		$http({method: 'GET', url: '/radar/rest/technology/' + $routeParams.technologyid + '/user?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			for(var i = 0; i < data.length; i++) {
				$scope.ratings.push(data[i]);
				if(data[i].user === $scope.uid) {
					$scope.currentSkillLevel = data[i].skillLevel;
				}
			}
		}).
		error(function(data, status, headers, config) {
			$log.log('failed to load user rating for technology ' + newval.name);
		});

		// Load technology radars
		$http({method: 'GET', url: '/radar/rest/technology/' + $routeParams.technologyid + '/radar?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			for(var i = 0; i < data.length; i++) {
				$scope.otherRadars.push(data[i]);
			}
		}).
		error(function(data, status, headers, config) {
			$log.log('failed to load radars for technology with ID ' + techId);
		});
	}

	$scope.selectSkillLevel = function(skillLevel) {
		$scope.currentSkillLevel = skillLevel;
		var toBeRemoved = -1;
		for(var i = 0; i < $scope.ratings.length; i++) {
			if($scope.ratings[i].user === $scope.uid) {
				toBeRemoved = i;
			}
		}
		if(toBeRemoved != -1) {
			$scope.ratings.splice(toBeRemoved, 1);
		}
		if(skillLevel != null) {
			$scope.ratings.push({user:$scope.uid,skillLevel:skillLevel});
		}

		$http.post('/radar/rest/technology/' + $scope.technology.id + '/user', {skillLevel:skillLevel}).
		success(function(data, status, headers, config) {
			$log.log('Successfully set technology \'' + $scope.technology.name + '\' (ID: ' + $scope.technology.id + ') to skill level \'' + skillLevel + '\'');
		}).
		error(function(data, status, headers, config) {
			$log.error('Failed to add technologies to radar');
			$log.error(data);
		});
	};

});
