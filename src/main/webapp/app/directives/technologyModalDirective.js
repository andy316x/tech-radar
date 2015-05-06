techRadarDirectives.directive('ngTechnologyModal', function ($http) {
	return {
		restrict: 'E',
		scope: {
			visible: '=',
			technology: '=',
			loggedInUser: '=',
			onSkillLevelSelected: '&'
		},
		templateUrl: 'templates/technology-modal.html',
		link: function ($scope, element, attrs) {

			$scope.$watch('technology', function (newVal, oldVal, scope) {
				if(newVal != oldVal) {
					reloadTechnology(newVal.techId);
				}
			}, false);

			var reloadTechnology = function(techId) {
				// Load technology ratings
				//TODO - Caching?
				$scope.ratings = [];
				$http({method: 'GET', url: '/radar/rest/technology/' + techId + '/user?nocache=' + (new Date()).getTime()}).
				success(function(data, status, headers, config) {
					$scope.currentSkillLevel = null;
					data.forEach(function(skill){
						$scope.ratings.push(skill);
						if(skill.user === $scope.loggedInUser) {
							$scope.currentSkillLevel =  skill.skillLevel;
						}
					});
				}).
				error(function(data, status, headers, config) {
					console.log('failed to load user ratings for technology with ID ' + techId);
				});

				// Load technology radars
				// TODO omit current radar
				$scope.otherRadars = [];
				$http({method: 'GET', url: '/radar/rest/technology/' + techId + '/radar?nocache=' + (new Date()).getTime()}).
				success(function(data) {
                    $scope.otherRadars = $scope.otherRadars.concat(data);
				}).
				error(function() {
					console.log('failed to load user radars for technology with ID ' + techId);
				});
			};

			$scope.$watch('visible', function (newVal, oldVal, scope) {
				if(newVal != oldVal) {
					if(newVal) {
						element.children(":first").modal('show');
					} else {
						element.children(":first").modal('hide');
					}
				}
			}, false);
			
			element.children(":first").on('hide.bs.modal', function(e) {
				$scope.visible = false;
			});

			$scope.selectSkillLevel = function(skillLevel) {
				$scope.currentSkillLevel = skillLevel;
				var toBeRemoved = -1;
				for(var i = 0; i < $scope.ratings.length; i++) {
					if($scope.ratings[i].user === $scope.loggedInUser) {
						toBeRemoved = i;
						break;
					}
				}
				if(toBeRemoved != -1) {
					$scope.ratings.splice(toBeRemoved, 1);
				}
				if(skillLevel != null) {
					$scope.ratings.push({user:$scope.loggedInUser,skillLevel:skillLevel});
				}
				$scope.onSkillLevelSelected({skillLevel:skillLevel});
			}
		}
	}
});