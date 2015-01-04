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
		'          <div ng-repeat="rating in ratings">' + 
		'            <span>{{rating.user}} - {{rating.skillLevel}}</span>' + 
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
					$log.log('failed to load user rating for technology ' + newval.name);
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
				$scope.onSkillLevelSelected({skillLevel:skillLevel});
			};

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
