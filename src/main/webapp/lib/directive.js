var techRadarDirectives = angular.module('techRadarDirectives', []);



techRadarDirectives.directive('ngRadar', function ($routeParams) {
	return {
		restrict: 'A',
		scope: {
			radar: '=',
			selectedBlip: '=',
			centreIndex: '=',
			editable: '=',
			onBlipClicked: '&',
			onBlipMoved: '&'
		},
		link: function ($scope, element, attrs) {
            var el = element[0];
			var quadrantName = $routeParams.quadrant;
            var interactions = {
                onblipmove: function(blip) {
                    $scope.$apply(function(){
                        $scope.onBlipMoved({blip:blip});
                    });
                },
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
            };

			var doDraw;
			if(quadrantName){
				doDraw = function(r) {
					$scope.theRadar = Radar.draw_Quadrant(el, r, quadrantName, $scope.editable, interactions);
				};
			}else{
				doDraw = function(r) {
					$scope.theRadar = Radar.draw(el, r, $scope.editable, interactions);
				};
			}

			if($scope.radar) {
				doDraw($scope.radar);
			}

			$scope.$watch('radar', function (newVal, oldVal, scope) {
				if(newVal) {
					doDraw(newVal);
				}
			}, true);

			$scope.$watch('selectedBlip', function (newVal, oldVal, scope) {
                //TODO - oldVal/scope parameters redundant?
				if($scope.theRadar) {
					if(newVal) {
						$scope.theRadar.unselectBlip(newVal);
					} else {
						$scope.theRadar.selectBlip(newVal);
					}
				}
			}, true);
			
			$scope.$watch('centreIndex', function (newVal, oldVal, scope) {
				if($scope.theRadar) {
					$scope.theRadar.zoom(newVal);
				}
			}, false);

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
		templateUrl: 'templates/new-radar.html',
		link: function ($scope, element, attrs) {
			
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

			$scope.techGroupingOptions = [];
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
				console.log('failed to load tech groupings');
			});
			
			$scope.businessUnitOptions = [];
			$http.get('/radar/rest/businessunit').
			success(function(data, status, headers, config) {
				for(var i = 0; i < data.length; i++) {
					if(i == 0) {
						$scope.businessUnit = data[i].name;
					}
					$scope.businessUnitOptions.push({label:data[i].name, value:data[i].name});
				}
			}).
			error(function(data, status, headers, config) {
				console.log('failed to load business units');
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
				$scope.$apply(function(){
					$scope.visible = false;
				});
			});

			$scope.save = function() {
				var radar = {
						name: $scope.name,
						description: $scope.description,
						businessUnit: $scope.businessUnit,
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
					$scope.radarCreated({radar: data});
				}).
				error(function(data, status, headers, config) {
					$scope.errors = data;
				});
			};

		}
	};
});


techRadarDirectives.directive('ngAddTech', function ($http) {
	return {
		restrict: 'A',
		scope: {
			visible: '=',
			selectedRadar: '=',
			onSave: '&',
			onCancel: '&'
		},
		template: '<div class="modal" >' +
		'  <div class="modal-dialog">' +
		'    <div class="modal-content">' +
		'      <div class="modal-header">' +
		'        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>' +
		'        <h4 class="modal-title">Select Technologies</h4>' +
		'      </div>' +
		'      <div class="modal-body">' +
		
		'		<div class="container-fluid main-content">' +
		'		 <div ng-show="stage==\'1\'" class="row">' +
		'		  <div class="col-md-12">' +
		
		'			 <div class="search-wrapper">' +
		'				<input ng-model="searchText" type="text" class="form-control typeahead" placeholder="Filter technologies..."></input>' +
		'				<i class="glyphicon glyphicon-search"></i>' +
		'			 </div>' +

		'			 <div class="technology-wrapper">' +
		'				<div ng-repeat="technology in technologies | filter:{name:searchText}" class="technology-card{{technology.selected==true?\' active\':\'\'}}" ng-click="technologySelected(technology)">' +
		'					<span>{{technology.name}}</span>' +
		'				</div>' +
		'			 </div>' +
		
		'		    <div class="text-right" style="margin-top: 10px;">' +
		'		      <span><strong>{{countSelected(technologies)}}</strong> of <strong>{{technologies.length}}</strong> selected</span>' +
		'		    </div>' +
		
		'		   </div>' +
		'		  </div>' +
		'		 </div>' +
		
		'		 <div ng-show="stage==\'2\'" class="row">' +
		'		  <div class="col-md-12">' +
		
		'			<div class="clearfix" style="margin-bottom:10px;">' +
		'				<div class="col-sm-4"><strong>Technology</strong></div>' +
		'				<div class="col-sm-4"><strong>Maturity</strong></div>' +
		'				<div class="col-sm-4"><strong>Tech Grouping</strong></div>' +
		'			</div>' +
		'			 <div style="overflow-y:scroll;overflow-x:hidden;height:340px;">' +
		'				<div ng-repeat="technology in technologies | filter:{name:searchText}" class="clearfix" ng-show="technology.selected==true">' +
		'					<div class="col-sm-4"><span>{{technology.name}}</span></div>' +
		'					<div class="col-sm-4"><select class="form-control" ng-model="technology.maturity" ng-options="maturity.label as maturity.value for maturity in $parent.maturityOptions"></select></div>' +
		'					<div class="col-sm-4"><select class="form-control" ng-model="technology.quadrant" ng-options="quadrant.label as quadrant.value for quadrant in $parent.quadrantOptions"></select></div>' +
		'				</div>' +
		'			 </div>' +
		
		'		   </div>' +
		'		  </div>' +
		'		 </div>' +
		
		'       <div class="error-panel">'+
		'         <div ng-repeat="error in errors" class="alert alert-danger" style="margin:5px;">{{error.text}}</div>'+
		'       </div>'+

		'      <div class="modal-footer clearfix">' +
		'        <div class="col-sm-6 text-left">' +
		'          <button ng-show="stage==\'2\'" ng-click="stage=\'1\'" class="btn btn-default">Back</button>' +
		'        </div>' +
		'        <div class="col-sm-6 text-right">' +
		'          <button ng-click="doCancel()" class="btn btn-default">Cancel</button>' +
		'          <button ng-show="stage==\'1\'" ng-click="stage=\'2\'" class="btn btn-success">Next</button>' +
		'          <button ng-show="stage==\'2\'" ng-click="doSave()" class="btn btn-success">Save</button>' +
		'        </div>' +
		'      </div>' +
		'    </div>' +
		'  </div>' +
		'</div>',
		link: function ($scope, element, attrs) {
			
			$scope.stage = '1';
			
			var getTechFromRadar = function (techName){
				if ($scope.selectedRadar.technologies == undefined){
					return undefined;
				}
				
				for(var i = 0; i < $scope.selectedRadar.technologies.length; i++) {
					if ($scope.selectedRadar.technologies[i].technology == techName){
						return $scope.selectedRadar.technologies[i];
					}
				}
				return undefined;
			};
			
			var isInRadar = function (techName){
				return getTechFromRadar(techName) != undefined;
			};
			
			var setTechnologySelection = function() {
				for(var i = 0; i < $scope.technologies.length; i++) {
					var techInRadar = getTechFromRadar($scope.technologies[i].name);
					if(typeof techInRadar !== 'undefined') {
						$scope.technologies[i].selected = true;
						$scope.technologies[i].maturity = techInRadar.maturity;
						$scope.technologies[i].quadrant = techInRadar.quadrant;
					} else {
						$scope.technologies[i].selected = false;
						if(typeof $scope.maturityOptions != 'undefined' && $scope.maturityOptions.length > 0) {
							$scope.technologies[i].maturity = $scope.maturityOptions[0].value;
						}
						for(var j = 0; j < $scope.quadrantOptions.length; j++) {
							if($scope.technologies[i].techGrouping == $scope.quadrantOptions[j].value) {
								$scope.technologies[i].quadrant = $scope.technologies[i].techGrouping;
							}
						}
					}
				}
			};
			
			$scope.technologies = [];
			$http({method: 'GET', url: '/radar/rest/technology?nocache=' + (new Date()).getTime()}).
			success(function(data, status, headers, config) {
				for(var i = 0; i < data.length; i++) {
					$scope.technologies.push(data[i]);
				}
				setTechnologySelection();
			}).
			error(function(data, status, headers, config) {
				console.log('error getting technology list');
			});
			
			$scope.technologySelected = function(technology) {
				if(technology.selected == true) {
					technology.selected = false;
				} else {
					technology.selected = true;
				}
			};
			
			$scope.countSelected = function(technologies) {
				var count = 0;
				for(var i = 0; i < technologies.length; i++) {
					if(technologies[i].selected==true) {
						count++;
					}
				}
				return count;
			};
			
			var updateOptions = function() {
				if(typeof $scope.selectedRadar !== 'undefined'){
					if(typeof $scope.selectedRadar.maturities !== 'undefined') {
						$scope.maturityOptions = [];
						for(var i = 0; i < $scope.selectedRadar.maturities.length; i++) {
							var maturity = $scope.selectedRadar.maturities[i];
							if(typeof maturity !== 'undefined') {
								$scope.maturityOptions.push({label:maturity.name, value:maturity.name});
							}
						}
					}
					
					if(typeof $scope.selectedRadar.quadrants !== 'undefined') {
						$scope.quadrantOptions = [];
						for(var i = 0; i < $scope.selectedRadar.quadrants.length; i++) {
							var quadrant = $scope.selectedRadar.quadrants[i];
							if(typeof quadrant !== 'undefined') {
								$scope.quadrantOptions.push({label:quadrant.name, value:quadrant.name});
							}
						}
					}
				}
			};
			
			$scope.$watch('selectedRadar', function (newVal, oldVal, scope) {
				updateOptions();
				setTechnologySelection();
			}, false);

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
			
			$scope.doSave = function() {
				$scope.errors = [];
				var newTechs = [];
				for(var i = 0; i < $scope.technologies.length; i++) {
					if($scope.technologies[i].selected==true) {
						var valid = true;
						if(typeof $scope.technologies[i].maturity == 'undefined') {
							valid = false;
							$scope.errors.push({text:$scope.technologies[i].name + ' has no maturity set'});
						}
						if(typeof $scope.technologies[i].quadrant == 'undefined') {
							valid = false;
							$scope.errors.push({text:$scope.technologies[i].name + ' has no tech grouping set'});
						}
						if(valid == true) {
							newTechs.push({
								technology:$scope.technologies[i].name,
								maturity:$scope.technologies[i].maturity,
								quadrant:$scope.technologies[i].quadrant,
								techGrouping:$scope.technologies[i].techGrouping
							});
						}
					}
				}
				if($scope.errors.length == 0) {
					$scope.onSave({techs:newTechs});
				}
			};
			
			$scope.doCancel = function() {
				$scope.onCancel();
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
		template: '<div class="modal modal-xl fade">' +
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
		'            <img ng-repeat="rating in ratings" src="/radar/img/icon_8204.png" title="{{rating.user + \' - \' + rating.skillLevel}}" data-toggle="tooltip" data-placement="bottom" tooltip=\"\"" style="width: 50px;border-radius: 25px;background-color: #DEDEDE;margin: 5px;padding: 1px;box-shadow: 1px 1px 1px #333;"></img>' + 
		'          </div>' + 
		'          <div ng-repeat="otherRadar in otherRadars">' + 
		'            <strong>{{otherRadar.addedByUid}}</strong> added <a href="/radar/#/technology/{{technology.techId}}">{{technology.name}}</a> to <a href="/radar/#/radar/{{otherRadar.radarId}}">{{otherRadar.radarName}}</a> {{otherRadar.addedDate | prettydate}}' + 
		'          </div>' + 
		'        </div>' + 
		'      </div>' +
		'    </div>' +
		'  </div>' +
		'</div>',
		link: function ($scope, element, attrs) {

			$scope.$watch('technology', function (newVal, oldVal, scope) {
				if(newVal != oldVal) {
					reloadTechnology(newVal.techId);
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
					console.log('failed to load user ratings for technology with ID ' + techId);
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
					console.log('failed to load user radars for technology with ID ' + techId);
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
		template: '<svg svg-width="{{scaleFactor*1000}}" svg-height="{{scaleFactor*400}}" version="1.1" xmlns="http://www.w3.org/2000/svg">' +
		'  <rect svg-x="{{scaleFactor*5}}"   svg-y="{{scaleFactor*(10+(300-300*(watching.length/max)))}}"  svg-rx="{{scaleFactor*20}}" svg-ry="{{scaleFactor*20}}" svg-width="{{scaleFactor*(190)}}" svg-height="{{scaleFactor*(300*(watching.length/max))}}"  svg-fill="#FFFFFF" stroke-width="1" svg-stroke="#CCCCCC"></rect>' +
		'  <rect svg-x="{{scaleFactor*205}}" svg-y="{{scaleFactor*(10+(300-300*(learning.length/max)))}}"  svg-rx="{{scaleFactor*20}}" svg-ry="{{scaleFactor*20}}" svg-width="{{scaleFactor*190}}"   svg-height="{{scaleFactor*(300*(learning.length/max))}}"  svg-fill="#428BCA" stroke-width="1" svg-stroke="#428BCA"></rect>' +
		'  <rect svg-x="{{scaleFactor*405}}" svg-y="{{scaleFactor*(10+(300-300*(competent.length/max)))}}" svg-rx="{{scaleFactor*20}}" svg-ry="{{scaleFactor*20}}" svg-width="{{scaleFactor*190}}"   svg-height="{{scaleFactor*(300*(competent.length/max))}}" svg-fill="#5BC0DE" stroke-width="1" svg-stroke="#5BC0DE"></rect>' +
		'  <rect svg-x="{{scaleFactor*605}}" svg-y="{{scaleFactor*(10+(300-300*(expert.length/max)))}}"    svg-rx="{{scaleFactor*20}}" svg-ry="{{scaleFactor*20}}" svg-width="{{scaleFactor*190}}"   svg-height="{{scaleFactor*(300*(expert.length/max))}}"    svg-fill="#F0AD4E" stroke-width="1" svg-stroke="#F0AD4E"></rect>' +
		'  <rect svg-x="{{scaleFactor*805}}" svg-y="{{scaleFactor*(10+(300-300*(leader.length/max)))}}"    svg-rx="{{scaleFactor*20}}" svg-ry="{{scaleFactor*20}}" svg-width="{{scaleFactor*190}}"   svg-height="{{scaleFactor*(300*(leader.length/max))}}"    svg-fill="#5CB85C" stroke-width="1" svg-stroke="#5CB85C"></rect>' +
		'  <text svg-x="{{scaleFactor*100}}" svg-y="{{scaleFactor*350}}" text-anchor="middle" fill="#333333" style="{{\'font-size: \' + scaleFactor*20 + \'px; font-weight: 900;\'">Watching</text>' +
		'  <text svg-x="{{scaleFactor*300}}" svg-y="{{scaleFactor*350}}" text-anchor="middle" fill="#333333" style="{{\'font-size: \' + scaleFactor*20 + \'px; font-weight: 900;\'">Learning</text>' +
		'  <text svg-x="{{scaleFactor*500}}" svg-y="{{scaleFactor*350}}" text-anchor="middle" fill="#333333" style="{{\'font-size: \' + scaleFactor*20 + \'px; font-weight: 900;\'">Competent</text>' +
		'  <text svg-x="{{scaleFactor*700}}" svg-y="{{scaleFactor*350}}" text-anchor="middle" fill="#333333" style="{{\'font-size: \' + scaleFactor*20 + \'px; font-weight: 900;\'">Expert</text>' +
		'  <text svg-x="{{scaleFactor*900}}" svg-y="{{scaleFactor*350}}" text-anchor="middle" fill="#333333" style="{{\'font-size: \' + scaleFactor*20 + \'px; font-weight: 900;\'">Leader</text>' +
		'</svg>',
		link: function ($scope, element, attrs) {

			$scope.scaleFactor = 1;

			$scope.getWidth = function () {
				return element[0].offsetWidth;
			};

			$scope.$watch($scope.getWidth, function (width) {
				$scope.scaleFactor = width/1000;
			});

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

techRadarDirectives.directive('ngTechMaturities', function () {
	return {
		restrict: 'A',
		template: '<svg width="{{scaleFactor*1000}}" height="{{scaleFactor*200}}" version="1.1" xmlns="http://www.w3.org/2000/svg">' +
		'  <circle r="{{scaleFactor*1000}}" cx="{{scaleFactor*1050}}" cy="{{scaleFactor*200}}" fill="rgb(223,223,223)"></circle>' +
		'  <circle r="{{scaleFactor*800}}" cx="{{scaleFactor*1050}}" cy="{{scaleFactor*200}}" fill="rgb(209,209,209)"></circle>' +
		'  <circle r="{{scaleFactor*600}}" cx="{{scaleFactor*1050}}" cy="{{scaleFactor*200}}" fill="rgb(190,191,193)"></circle>' +
		'  <circle r="{{scaleFactor*400}}" cx="{{scaleFactor*1050}}" cy="{{scaleFactor*200}}" fill="rgb(166,167,169)"></circle>' +
		'  <circle r="{{scaleFactor*200}}" cx="{{scaleFactor*1050}}" cy="{{scaleFactor*200}}" fill="rgb(223,223,223)"></circle>' +
		
		'  <circle r="{{scaleFactor*800}}" cx="{{scaleFactor*1050}}" cy="{{scaleFactor*200}}" fill="none" style="stroke-width:2;stroke:rgb(255,255,255)"></circle>' +
		'  <circle r="{{scaleFactor*600}}" cx="{{scaleFactor*1050}}" cy="{{scaleFactor*200}}" fill="none" style="stroke-width:2;stroke:rgb(255,255,255)"></circle>' +
		'  <circle r="{{scaleFactor*400}}" cx="{{scaleFactor*1050}}" cy="{{scaleFactor*200}}" fill="none" style="stroke-width:2;stroke:rgb(255,255,255)"></circle>' +
		'  <circle r="{{scaleFactor*200}}" cx="{{scaleFactor*1050}}" cy="{{scaleFactor*200}}" fill="none" style="stroke-width:2;stroke:rgb(255,255,255)"></circle>' +
		'</svg>',
		link: function ($scope, element, attrs) {

			$scope.scaleFactor = 1;

			$scope.getWidth = function () {
				return element[0].offsetWidth;
			};

			$scope.$watch($scope.getWidth, function (width) {
				$scope.scaleFactor = width/1000;
			});
		}
	};
});

techRadarDirectives.directive('ngSkillLevel', function () {
	return {
		restrict: 'A',
		scope: {
			skillLevels: '='
		},
		template: '<svg svg-width="{{scaleFactor*1000}}" svg-height="{{scaleFactor*(highest*40 + 100)}}" version="1.1" xmlns="http://www.w3.org/2000/svg">' +
		'  <g ng-repeat="skillLevel in skills">' +
		'    <text svg-x="{{scaleFactor*(100+$index*200)}}" svg-y="{{scaleFactor*(highest*40 + 10)}}" text-anchor="middle">{{skillLevel.name}}</text>' +
		'    <g ng-repeat="sl in skillLevel.technologies">' +
		'      <rect svg-x="{{scaleFactor*(10+$parent.$index*200)}}" svg-y="{{scaleFactor*((highest*40 - 40)-($index*35))}}" svg-rx="{{scaleFactor*5}}" svg-ry="{{scaleFactor*5}}" svg-width="{{scaleFactor*190}}" svg-height="{{scaleFactor*30}}" svg-fill="{{skillLevel.fill}}" svg-stroke="{{skillLevel.stroke}}" stroke-width="1"></rect>' +
		'      <text svg-x="{{scaleFactor*(100+$parent.$index*200)}}" svg-y="{{scaleFactor*((highest*40 - 20)-($index*35))}}" text-anchor="middle" svg-fill="{{skillLevel.textFill}}">{{sl.name}}</text>' +
		'    </g>' +
		'  </g>' +
		'</svg>',
		link: function ($scope, element, attrs) {
			
			$scope.skills = [
			                    {name:'LEADER',   fill:'#5CB85C',stroke:'#5CB85C',textFill:'#FFFFFF',technologies:[]},
			                    {name:'EXPERT',   fill:'#F0AD4E',stroke:'#F0AD4E',textFill:'#FFFFFF',technologies:[]},
			                    {name:'COMPETENT',fill:'#5BC0DE',stroke:'#5BC0DE',textFill:'#FFFFFF',technologies:[]},
			                    {name:'LEARNING', fill:'#428BCA',stroke:'#428BCA',textFill:'#FFFFFF',technologies:[]},
			                    {name:'WATCHING', fill:'#FFFFFF',stroke:'#CCCCCC',textFill:'#333333',technologies:[]}
			                      ];

			$scope.scaleFactor = 1;

			$scope.getWidth = function () {
				return element[0].offsetWidth;
			};

			$scope.$watch('skillLevels', function (newval, oldval) {
				var theMax = 0;
				for(var i = 0; i < newval.length; i++) {
					var skill = newval[i];
					if(skill.skillLevel == 'Leader') {
						$scope.skills[0].technologies.push({name:skill.technology});
						if($scope.skills[0].technologies.length > theMax) {
							theMax = $scope.skills[0].technologies.length;
						}
					} else if(skill.skillLevel == 'Expert') {
						$scope.skills[1].technologies.push({name:skill.technology});
						if($scope.skills[1].technologies.length > theMax) {
							theMax = $scope.skills[1].technologies.length;
						}
					} else if(skill.skillLevel == 'Competent') {
						$scope.skills[2].technologies.push({name:skill.technology});
						if($scope.skills[2].technologies.length > theMax) {
							theMax = $scope.skills[2].technologies.length;
						}
					} else if(skill.skillLevel == 'Learning') {
						$scope.skills[3].technologies.push({name:skill.technology});
						if($scope.skills[3].technologies.length > theMax) {
							theMax = $scope.skills[3].technologies.length;
						}
					} else if(skill.skillLevel == 'Watching') {
						$scope.skills[4].technologies.push({name:skill.technology});
						if($scope.skills[4].technologies.length > theMax) {
							theMax = $scope.skills[4].technologies.length;
						}
					}
				}
				$scope.highest = theMax;
			}, true);
			
			$scope.$watch($scope.getWidth, function (width) {
				$scope.scaleFactor = width/1000;
			});
		}
	};
});

techRadarDirectives.directive('svgWidth', function () {
	return {
		restrict: 'A',
		link: function ($scope, element, attrs) {
			attrs.$observe('svgWidth', function(value) {
				element.attr('width', Math.floor(value));
		    });
		}
	};
});

techRadarDirectives.directive('svgHeight', function () {
	return {
		restrict: 'A',
		link: function ($scope, element, attrs) {
			attrs.$observe('svgHeight', function(value) {
				element.attr('height', Math.floor(value));
		    });
		}
	};
});

techRadarDirectives.directive('svgX', function () {
	return {
		restrict: 'A',
		link: function ($scope, element, attrs) {
			attrs.$observe('svgX', function(value) {
				element.attr('x', Math.floor(value));
		    });
		}
	};
});

techRadarDirectives.directive('svgY', function () {
	return {
		restrict: 'A',
		link: function ($scope, element, attrs) {
			attrs.$observe('svgY', function(value) {
				element.attr('y', Math.floor(value));
		    });
		}
	};
});

techRadarDirectives.directive('svgRx', function () {
	return {
		restrict: 'A',
		link: function ($scope, element, attrs) {
			attrs.$observe('svgRx', function(value) {
				element.attr('rx', Math.floor(value));
		    });
		}
	};
});

techRadarDirectives.directive('svgRy', function () {
	return {
		restrict: 'A',
		link: function ($scope, element, attrs) {
			attrs.$observe('svgRy', function(value) {
				element.attr('ry', Math.floor(value));
		    });
		}
	};
});

techRadarDirectives.directive('svgFill', function () {
	return {
		restrict: 'A',
		link: function ($scope, element, attrs) {
			attrs.$observe('svgFill', function(value) {
				element.attr('fill', value);
		    });
		}
	};
});

techRadarDirectives.directive('svgStroke', function () {
	return {
		restrict: 'A',
		link: function ($scope, element, attrs) {
			attrs.$observe('svgStroke', function(value) {
				element.attr('stroke', value);
		    });
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

techRadarDirectives.directive('tooltip', function(){
	return {
		restrict: 'A',
		link: function(scope, element, attrs){
			$(element).hover(function(){
				// on mouseenter
				$(element).tooltip('show');
			}, function(){
				// on mouseleave
				$(element).tooltip('hide');
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
