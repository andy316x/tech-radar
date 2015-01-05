var techRadarDirectives = angular.module('techRadarDirectives', []);

techRadarDirectives.directive('ngRadar', function ($routeParams) {
	return {
		restrict: 'A',
		scope: {
			radar: '=',
			selectedBlip: '=',
			onBlipClicked: '&'
		},
		link: function ($scope, element, attrs) {
			var el = element[0];

			var quadrantName = $routeParams.quadrant;

			var doDraw;
			if(quadrantName != null && typeof quadrantName !== "undefined"){
				doDraw = function(r) {
					$scope.theRadar = Radar.draw_Quadrant(el, r, quadrantName, {
						onblipclick: function(blip) {
							$scope.$apply(function(){
								$scope.onBlipClicked({blip:blip});
							});
						},
						onbliphover: function(blip) {
							$scope.$apply(function(){
								$scope.selectedBlip = blip;
							});
						},
						onblipleave: function(blip) {
							$scope.$apply(function(){
								$scope.selectedBlip = null;
							});
						}
					});
				};
			}else{
				doDraw = function(r) {
					$scope.theRadar = Radar.draw(el, r, {
						onblipclick: function(blip) {
							$scope.$apply(function(){
								$scope.onBlipClicked({blip:blip});
							});
						},
						onbliphover: function(blip) {
							$scope.$apply(function(){
								$scope.selectedBlip = blip;
							});
						},
						onblipleave: function(blip) {
							$scope.$apply(function(){
								$scope.selectedBlip = null;
							});
						}
					});
				};
			}

			if($scope.radar != null && typeof $scope.radar != 'undefined') {
				doDraw($scope.radar);
			}

			$scope.$watch('radar', function (newVal, oldVal, scope) {
				if(newVal != null && typeof newVal != 'undefined') {
					doDraw(newVal);
				}
			}, true);

			$scope.$watch('selectedBlip', function (newVal, oldVal, scope) {
				if($scope.theRadar != null && typeof $scope.theRadar != 'undefined') {
					if(newVal == null || typeof newVal == 'undefined') {
						$scope.theRadar.unselectBlip(newVal);
					} else {
						$scope.theRadar.selectBlip(newVal);
					}
				}
			}, true);

			window.addEventListener('resize', function() {
				doDraw($scope.radar);
			}, true);
		}
	};
});

techRadarDirectives.directive('ngNewRadar', function ($http) {
	return {
		restrict: 'A',
		scope: {
			visible: '=',
			radarCreated: '&'
		},
		template: '<div class="modal fade">' +
		'  <div class="modal-dialog">' +
		'    <div class="modal-content">' +
		'      <div class="modal-header">' +
		'        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>' +
		'        <h4 class="modal-title">New Radar</h4>' +
		'      </div>' +
		'      <div class="modal-body">' +
		'        <div class="form-group">' +
		'          <label>Name</label>' +
		'          <input type="text" class="form-control" ng-model="name" placeholder="Enter name"></input>' +
		'        </div>' +
		'        <div class="form-group">' +
		'          <label>Tech Grouping 1</label>' +
		'          <select class="form-control" ng-model="techGrouping1" ng-options="techGroupingOption.value as techGroupingOption.label for techGroupingOption in techGroupingOptions"></select>' +
		'          </select>' +
		'        </div>' +
		'        <div class="form-group">' +
		'          <label>Tech Grouping 2</label>' +
		'          <select class="form-control" ng-model="techGrouping2" ng-options="techGroupingOption.value as techGroupingOption.label for techGroupingOption in techGroupingOptions"></select>' +
		'          </select>' +
		'        </div>' +
		'        <div class="form-group">' +
		'          <label>Tech Grouping 3</label>' +
		'          <select class="form-control" ng-model="techGrouping3" ng-options="techGroupingOption.value as techGroupingOption.label for techGroupingOption in techGroupingOptions"></select>' +
		'          </select>' +
		'        </div>' +
		'        <div class="form-group">' +
		'          <label>Tech Grouping 4</label>' +
		'          <select class="form-control" ng-model="techGrouping4" ng-options="techGroupingOption.value as techGroupingOption.label for techGroupingOption in techGroupingOptions"></select>' +
		'          </select>' +
		'        </div>' +
		'        <div ng-repeat="error in errors"><span class="text-danger">{{error}}</span></div>' +
		'      </div>' +
		'      <div class="modal-footer">' +
		'        <button type="button" ng-click="save()" class="btn btn-success">Create</button>' +
		'      </div>' +
		'    </div>' +
		'  </div>' +
		'</div>',
		link: function ($scope, element, attrs) {

			$scope.techGroupingOptions = [];
			$scope.techGrouping1 = 'Dev Tool';
			$scope.techGrouping2 = 'Dev Language';
			$scope.techGrouping3 = 'Platform';
			$scope.techGrouping4 = 'Solution Technology';
			$http.get('/radar/rest/techgrouping').
			success(function(data, status, headers, config) {
				for(var i = 0; i < data.length; i++) {
					$scope.techGroupingOptions.push({label:data[i].name, value:data[i].name});
				}
			}).
			error(function(data, status, headers, config) {
				$log.log('failed to load tech groupings');
			});

			$scope.$watch('visible', function (newVal, oldVal, scope) {
				if(newVal != oldVal) {
					if(newVal == true) {
						element.children(":first").modal('show');
					} else {
						element.children(":first").modal('hide');
					}
				}
			}, false);

			element.children(":first").on('hide.bs.modal', function(e) {
				$scope.visible = false;
			});

			$scope.save = function() {
				var radar = {
						name: $scope.name,
						techGroupings: [
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
					$scope.radarCreated({radar: data});
				}).
				error(function(data, status, headers, config) {
					$scope.errors = data;
				});
			};

		}
	};
});

techRadarDirectives.directive('ngTechnologyModal', function ($http) {
	return {
		restrict: 'A',
		scope: {
			visible: '=',
			technology: '=',
			loggedInUser: '=',
			onSkillLevelSelected: '&'
		},
		template: '<div class="modal fade">' +
		'  <div class="modal-dialog">' +
		'    <div class="modal-content">' +
		'      <div class="modal-header">' +
		'        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>' +
		'        <h4 class="modal-title">{{technology.name}}</h4>' +
		'      </div>' +
		'      <div class="modal-body clearfix">' +
		'        <div class="col-sm-6">' + 
		'          <blockquote>' + 
		'            <p ng-show="technology.description!=null">{{technology.description}}</p>' + 
		'            <p ng-show="technology.description==null">No description</p>' + 
		'            <footer><strong>{{technology.name}}</strong> is advocated by <cite title="Andy Wilson">Andy Wilson</cite></footer>' + 
		'          </blockquote>' + 
		'          <div class="btn-group btn-group-justified" role="group" aria-label="...">' +
		'            <div class="btn-group" role="group">' +
		'              <button type="button" class="btn btn-xs btn-danger {{currentSkillLevel==null?\'active\':\'\'}}" ng-click="selectSkillLevel(null)">None</button>' +
		'            </div>' +
		'            <div class="btn-group" role="group">' +
		'              <button type="button" class="btn btn-xs btn-default {{currentSkillLevel==\'Watching\'?\'active\':\'\'}}" ng-click="selectSkillLevel(\'Watching\')">Watching</button>' +
		'            </div>' +
		'            <div class="btn-group" role="group">' +
		'              <button type="button" class="btn btn-xs btn-primary {{currentSkillLevel==\'Learning\'?\'active\':\'\'}}" ng-click="selectSkillLevel(\'Learning\')">Learning</button>' +
		'            </div>' +
		'            <div class="btn-group" role="group">' +
		'              <button type="button" class="btn btn-xs btn-info {{currentSkillLevel==\'Competent\'?\'active\':\'\'}}" ng-click="selectSkillLevel(\'Competent\')">Competent</button>' +
		'            </div>' +
		'            <div class="btn-group" role="group">' +
		'              <button type="button" class="btn btn-xs btn-warning {{currentSkillLevel==\'Expert\'?\'active\':\'\'}}" ng-click="selectSkillLevel(\'Expert\')">Expert</button>' +
		'            </div>' +
		'            <div class="btn-group" role="group">' +
		'              <button type="button" class="btn btn-xs btn-success {{currentSkillLevel==\'Leader\'?\'active\':\'\'}}" ng-click="selectSkillLevel(\'Leader\')">Leader</button>' +
		'            </div>' +
		'          </div>' +
		'        </div>' +
		'        <div class="col-sm-6">' + 
		'          <div ng-show="ratings.length > 0" ng-tech-ratings="" ratings="ratings"></div>' + 
		'          <div ng-show="ratings.length == 0" style="margin-bottom:10px">Be the first to rate your skill level for this technology</div>' + 
		'          <div style="margin-bottom:10px">' + 
		'            <img ng-repeat="rating in ratings" src="/radar/img/icon_8204.png" title="{{rating.user + \' - \' + rating.skillLevel}}" style="width: 50px;border-radius: 25px;background-color: #DEDEDE;margin: 5px;padding: 1px;box-shadow: 1px 1px 1px #333;"></img>' + 
		'          </div>' + 
		'          <div ng-repeat="otherRadar in otherRadars">' + 
		'            <strong>{{otherRadar.addedByUid}}</strong> added <a href="/radar/#/technology/{{technology.id}}">{{technology.name}}</a> to <a href="/radar/#/radar/{{otherRadar.radarId}}">{{otherRadar.radarName}}</a> {{otherRadar.addedDate | prettydate}}' + 
		'          </div>' + 
		'        </div>' + 
		'      </div>' +
		'    </div>' +
		'  </div>' +
		'</div>',
		link: function ($scope, element, attrs) {
			
			$scope.$watch('technology', function (newVal, oldVal, scope) {
				if(newVal != oldVal) {
					reloadTechnology(newVal.id);
				}
			}, false);
			
			var reloadTechnology = function(techId) {
				// Load technology ratings
				$scope.ratings = [];
				$http({method: 'GET', url: '/radar/rest/technology/' + techId + '/user?nocache=' + (new Date()).getTime()}).
				success(function(data, status, headers, config) {
					$scope.currentSkillLevel = null;
					for(var i = 0; i < data.length; i++) {
						$scope.ratings.push(data[i]);
						if(data[i].user === $scope.loggedInUser) {
							$scope.currentSkillLevel =  data[i].skillLevel;
						}
					}
				}).
				error(function(data, status, headers, config) {
					$log.log('failed to load user ratings for technology with ID ' + techId);
				});
				
				// Load technology radars
				// TODO omit current radar
				$scope.otherRadars = [];
				$http({method: 'GET', url: '/radar/rest/technology/' + techId + '/radar?nocache=' + (new Date()).getTime()}).
				success(function(data, status, headers, config) {
					for(var i = 0; i < data.length; i++) {
						$scope.otherRadars.push(data[i]);
					}
				}).
				error(function(data, status, headers, config) {
					$log.log('failed to load user radars for technology with ID ' + techId);
				});
			};

			$scope.$watch('visible', function (newVal, oldVal, scope) {
				if(newVal != oldVal) {
					if(newVal == true) {
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
					}
				}
				if(toBeRemoved != -1) {
					$scope.ratings.splice(toBeRemoved, 1);
				}
				if(skillLevel != null) {
					$scope.ratings.push({user:$scope.loggedInUser,skillLevel:skillLevel});
				}
				$scope.onSkillLevelSelected({skillLevel:skillLevel});
			};

		}
	};
});

techRadarDirectives.directive('ngTechRatings', function ($http) {
	return {
		restrict: 'A',
		scope: {
			ratings: '='
		},
		template: '<svg viewBox="0 0 1000 400" preserveAspectRatio="none" version="1.1" xmlns="http://www.w3.org/2000/svg">' +
		'  <rect x="5"   y="{{10+(300-300*(watching.length/max))}}"  rx="20" ry="20" width="190" height="{{300*(watching.length/max)}}"  style="fill:#FFFFFF;stroke-width:1;stroke:#CCCCCC"></rect>' +
		'  <rect x="205" y="{{10+(300-300*(learning.length/max))}}"  rx="20" ry="20" width="190" height="{{300*(learning.length/max)}}"  style="fill:#428BCA;stroke-width:1;stroke:#428BCA"></rect>' +
		'  <rect x="405" y="{{10+(300-300*(competent.length/max))}}" rx="20" ry="20" width="190" height="{{300*(competent.length/max)}}" style="fill:#5BC0DE;stroke-width:1;stroke:#5BC0DE"></rect>' +
		'  <rect x="605" y="{{10+(300-300*(expert.length/max))}}"    rx="20" ry="20" width="190" height="{{300*(expert.length/max)}}"    style="fill:#F0AD4E;stroke-width:1;stroke:#F0AD4E"></rect>' +
		'  <rect x="805" y="{{10+(300-300*(leader.length/max))}}"    rx="20" ry="20" width="190" height="{{300*(leader.length/max)}}"    style="fill:#5CB85C;stroke-width:1;stroke:#5CB85C"></rect>' +
		'  <text x="100" y="350" text-anchor="middle" fill="#333333" style="font-size: 20px; font-weight: 900;">Watching</text>' +
		'  <text x="300" y="350" text-anchor="middle" fill="#333333" style="font-size: 20px; font-weight: 900;">Learning</text>' +
		'  <text x="500" y="350" text-anchor="middle" fill="#333333" style="font-size: 20px; font-weight: 900;">Competent</text>' +
		'  <text x="700" y="350" text-anchor="middle" fill="#333333" style="font-size: 20px; font-weight: 900;">Expert</text>' +
		'  <text x="900" y="350" text-anchor="middle" fill="#333333" style="font-size: 20px; font-weight: 900;">Leader</text>' +
		'</svg>',
		link: function ($scope, element, attrs) {
			
			$scope.$watch('ratings', function (newVal, oldVal, scope) {
				repopulateRatings(newVal);
			}, true);
			
			var repopulateRatings = function(ratings) {
				
				$scope.watching = [];
				$scope.learning = [];
				$scope.competent= [];
				$scope.expert = [];
				$scope.leader = [];
				
				if(ratings != null && typeof ratings != 'undefined') {
					for(var i = 0; i < ratings.length; i++) {
						if(ratings[i].skillLevel === 'Watching') {
							$scope.watching.push(ratings[i]);
						} else if(ratings[i].skillLevel === 'Learning') {
							$scope.learning.push(ratings[i]);
						} else if(ratings[i].skillLevel === 'Competent') {
							$scope.competent.push(ratings[i]);
						} else if(ratings[i].skillLevel === 'Expert') {
							$scope.expert.push(ratings[i]);
						} else if(ratings[i].skillLevel === 'Leader') {
							$scope.leader.push(ratings[i]);
						}
					}
				}
				
				var max = Math.max(
					$scope.watching.length,
					$scope.learning.length,
					$scope.competent.length,
					$scope.expert.length,
					$scope.leader.length
				);
				
				$scope.max = max===0 ? 1 : max;
			};
			
			repopulateRatings($scope.ratings);
		}
	};
});

techRadarDirectives.directive('ngSearch', function () {
	return {
		restrict: 'A',
		scope: {
			onRadarSelected: '&',
			onTechnologySelected: '&'
		},
		link: function ($scope, element, attrs) {

			// Search bar
			var technologySuggestions = new Bloodhound({
				datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
				queryTokenizer: Bloodhound.tokenizers.whitespace,
				prefetch: '/radar/rest/technology?nocache=' + new Date().getTime()
			});
			technologySuggestions.clearPrefetchCache();
			technologySuggestions.initialize();

			var radarSuggestions = new Bloodhound({
				datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
				queryTokenizer: Bloodhound.tokenizers.whitespace,
				prefetch: '/radar/rest/radar?nocache=' + new Date().getTime()
			});
			radarSuggestions.clearPrefetchCache();
			radarSuggestions.initialize();

			element.typeahead({
				hint: true,
				highlight: true,
				minLength: 1
			},
			{
				name: 'technologies',
				displayKey: 'name',
				source: technologySuggestions.ttAdapter(),
				templates: {
					header: '<h3 class="search-title">Technologies</h3>'
				}
			},
			{
				name: 'radars',
				displayKey: 'name',
				source: radarSuggestions.ttAdapter(),
				templates: {
					header: '<h3 class="search-title">Radars</h3>'
				}
			})
			.bind('typeahead:selected', function (obj, datum) {
				if(typeof datum.maturities != 'undefined') {
					$scope.onRadarSelected({id:datum.id});
				} else {
					$scope.onTechnologySelected({id:datum.id});
				}
			});

		}
	};
});

techRadarDirectives.filter('prettydate', function() {
	return function(input) {
		if(input == null || typeof input == 'undefined') {
			return '';
		}
		
		var aSecond = 1000;
		var aMinute = 60*aSecond;
		var anHour = 60*aMinute;
		var anDay = 24*anHour;
		
		var nowMillis = (new Date()).getTime();
		var difference = nowMillis - input;
		
		if(difference < 20*aSecond) {
			return 'just now';
		}
		
		if(difference < aMinute) {
			var seconds = Math.floor(difference/aSecond);
			if(seconds==1) {
				return 'a second ago';
			} else {
				return seconds + ' seconds ago';
			}
		}
		
		if(difference < anHour) {
			var minutes = Math.floor(difference/aMinute);
			if(minutes==1) {
				return 'a minute ago';
			} else {
				return minutes + ' minutes ago';
			}
		}
		
		if(difference < anDay) {
			var hours = Math.floor(difference/anHour);
			if(hours==1) {
				return 'an hour ago';
			} else {
				return hours + ' hours ago';
			}
		}
		
		var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
		var then = new Date(input);
		
		return 'on ' + then.getDate() + ' ' + months[then.getMonth()] + ' \'' + ('' + then.getFullYear()).substring(2, 4);
	};
});
