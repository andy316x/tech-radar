var techRadarDirectives = angular.module('techRadarDirectives', []);

techRadarDirectives.directive('ngRadar', function ($routeParams) {
	return {
		restrict: 'A',
		scope: {
			radar: '=',
			selectedBlip: '=',
			centreIndex: '=',
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
			
			$scope.$watch('centreIndex', function (newVal, oldVal, scope) {
				if($scope.theRadar != null && typeof $scope.theRadar != 'undefined') {
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
		'        <div style="position:relative;">' +
		'          <img src="/radar/img/radar_175.svg" style="width: 100%;">' +
		'          <span style="position:absolute;right:110px;bottom:150px;">' + 
		'            <div class="dropdown">' + 
		'              <button class="btn btn-link" id="dropdownMenu1" data-toggle="dropdown" aria-expanded="true">{{techGrouping1}} <span class="caret"></button></span>' +
		'              <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">' +
		'                <li ng-repeat="techGroupingOption in techGroupingOptions"role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)" ng-click="selectGrouping(1, techGroupingOption.label)">{{techGroupingOption.label}}</a></li>' +
	    '              </ul>' +
	    '            </div>' +
	    '          </span>' + 
	    '          <span style="position:absolute;left:110px;bottom:150px;">' + 
		'            <div class="dropdown">' + 
		'              <button class="btn btn-link" id="dropdownMenu1" data-toggle="dropdown" aria-expanded="true">{{techGrouping2}} <span class="caret"></button></span>' +
		'              <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">' +
		'                <li ng-repeat="techGroupingOption in techGroupingOptions"role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)" ng-click="selectGrouping(2, techGroupingOption.label)">{{techGroupingOption.label}}</a></li>' +
	    '              </ul>' +
	    '            </div>' +
	    '          </span>' + 
	    '          <span style="position:absolute;left:110px;top:150px;">' + 
		'            <div class="dropdown">' + 
		'              <button class="btn btn-link" id="dropdownMenu1" data-toggle="dropdown" aria-expanded="true">{{techGrouping3}} <span class="caret"></button></span>' +
		'              <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">' +
		'                <li ng-repeat="techGroupingOption in techGroupingOptions"role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)" ng-click="selectGrouping(3, techGroupingOption.label)">{{techGroupingOption.label}}</a></li>' +
	    '              </ul>' +
	    '            </div>' +
	    '          </span>' + 
	    '          <span style="position:absolute;right:110px;top:150px;">' + 
		'            <div class="dropdown">' + 
		'              <button class="btn btn-link" id="dropdownMenu1" data-toggle="dropdown" aria-expanded="true">{{techGrouping4}} <span class="caret"></button></span>' +
		'              <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">' +
	    '                <li ng-repeat="techGroupingOption in techGroupingOptions" role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0)" ng-click="selectGrouping(4, techGroupingOption.label)">{{techGroupingOption.label}}</a></li>' +
	    '              </ul>' +
	    '            </div>' +
	    '          </span>' + 
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
						dateCreated: new Date().getTime(),
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


techRadarDirectives.directive('ngAddTech', function ($http) {
	return {
		restrict: 'A',
		scope: {
			visible: '=',
			selectedRadar: '=',
			doSave: '&'
		},
		template: '<div class="modal" >' +
		'  <div class="modal-dialog">' +
		'    <div class="modal-content">' +
		'      <div class="modal-header">' +
		'        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>' +
		'        <h4 class="modal-title">Add Technologies</h4>' +
		'      </div>' +
		'      <div class="modal-body">' +
		
		'		<div class="container-fluid main-content">' +
		'		 <div class="row">' +
		'		  <div class="col-md-12" style="background-color:#EFEFEF;padding:25px 20px;">' +
		'		    <div class="col-md-6">' +
		
		'			 <div class="search-wrapper">' +
		'				<input ng-model="searchText" type="text" class="form-control typeahead" placeholder="Filter technologies..."></input>' +
		'				<i class="glyphicon glyphicon-search"></i>' +
		'			 </div>' +

		'			 <div class="technology-wrapper">' +
		'				<div ng-repeat="technology in technologies | filter:{name:searchText}" class="technology-card{{selectedTechnology.id==technology.id?\' active\':\'\'}}" ng-click="technologySelected(technology)">' +
		'					<span>{{$index+1}}. {{technology.name}}<i ng-show="isInRadar(technology.name)" class="glyphicon glyphicon-screenshot"></i></span>' +
		'				</div>' +
		'			 </div>' +
		
		'		    </div>' +
		
		'		    <div class="col-md-6"> ' +
		'		    <div class="technology-wrapper" style="margin:0" ng-show="selectedTechnology!=undefined"> ' +
					
		'				<div class="clearfix" style="padding: 20px 15px 20px 5px;border-bottom: 1px solid #CCCCCC;">' +
		'					<div class="col-xs-6">' +
		'						<h1>{{selectedTechnology.name}}</h1>' +
		'					</div>' +
		'				</div>' +
						
		'				<div style="padding: 10px; border-bottom: 1px solid #CCCCCC">' +

		'		         <div class="container-fluid main-content">' +
		'		          <div class="row">' +
		'					<div class="col-xs-6 text-left">' +
		'					 <h4>Maturity</h4>' +
		'					 <div class="btn-group-vertical" role="group" aria-label="Maturity">' +
		'					   <button ng-repeat="maturityOption in maturityOptions" ng-click="setMaturity(maturityOption.value)" ng-class="[btnStyle,btnDefaultStyle,{{maturityOption.value.replace(\' \',\'\')}}Style]">{{maturityOption.label}}</button>' +
		'				     </div>' +
		'				    </div>' +
		
		'					<div class="col-xs-6 text-right">' +
		'					 <h4>Tech Group</h4>' +
		'					 <div class="btn-group-vertical" role="group" aria-label="Tech Group">' +
		'					  <button ng-repeat="techGroupingOption in techGroupingOptions" ng-click="setTechGroup(techGroupingOption.value)" ng-class="[btnStyle,btnDefaultStyle,{{techGroupingOption.value.replace(\' \',\'\')}}Style]">{{techGroupingOption.label}}</button>' +
		'					 </div>' +
		'				    </div>' +
		'		         </div>' +
		'				</div>' +

		'				</div>' +
		'				<div class="text-right" style="padding: 10px; border-bottom: 1px solid #CCCCCC">' +
		'                <div>{{validationMessage}}</div>' +
		'                <button type="button" ng-hide="isInRadar(selectedTechnology.name)" ng-click="add()" class="btn btn-success">Add</button>' +
		'                <button type="button" ng-show="isInRadar(selectedTechnology.name)" ng-click="remove()" class="btn btn-success">Remove</button>' +
		'				</div>' +
		'		    </div>' +
		
		'		   </div>' +
		'		  </div>' +
		'		 </div>' +

		'      <div class="modal-footer">' +
		'        <button type="button" ng-click="doSave()" class="btn btn-success">Save</button>' +
		'      </div>' +
		'    </div>' +
		'  </div>' +
		'</div>',
		link: function ($scope, element, attrs) {
			
			$scope.btnStyle = "btn";
			$scope.btnDefaultStyle = "btn-default";
			
			$scope.maturity = "invest";
			$scope.maturityOptions = [];
			
			$http.get('/radar/rest/maturity').
			success(function(data, status, headers, config) {
				for(var i = data.length-1; i >= 0; i--) {
					$scope.maturityOptions.push({label:$scope.capitaliseFirstLetter(data[i].name), value:data[i].name});
				}
			}).
			error(function(data, status, headers, config) {
				$log.log('failed to load maturity levels');
			});
			
			$scope.techGroupingOptions = [];
			$http.get('/radar/rest/techgrouping').
			success(function(data, status, headers, config) {
				for(var i = 0; i < data.length; i++) {
					$scope.techGroupingOptions.push({label:data[i].name, value:data[i].name});
				}
			}).
			error(function(data, status, headers, config) {
				$log.log('failed to load tech groupings');
			});
			
			$scope.capitaliseFirstLetter = function (string)	{
			    return string.charAt(0).toUpperCase() + string.slice(1);
			}
			
			$scope.technologies = [];

			$http({method: 'GET', url: '/radar/rest/technology?nocache=' + (new Date()).getTime()}).
			success(function(data, status, headers, config) {
				if(data.length > 0) {
					$scope.selectedTechnology = undefined;
					for(var i = 0; i < data.length; i++) {
						$scope.technologies.push(data[i]);
					}
				}
			}).
			error(function(data, status, headers, config) {
				$log.log('error getting technology list');
			});
			
			$scope.setMaturity = function(maturity) {
				$scope.maturity = maturity;
				$scope.setMaturityStyle(maturity);
				
				if ($scope.techFromRadar != undefined){
					$scope.techFromRadar.maturity = maturity;
				}
			}
			
			$scope.setTechGroup = function(techGroup) {
				$scope.techGroup = techGroup;
				$scope.setTechGroupStyle(techGroup);
				
				if ($scope.techFromRadar != undefined){
					$scope.techFromRadar.techGrouping = techGroup;
				}
			}
			
			$scope.technologySelected = function(technology) {
				$scope.selectedTechnology = technology;
				
				$scope.searchText = $scope.selectedTechnology.name;
				
				if (($scope.techFromRadar = $scope.getTechFromRadar($scope.selectedTechnology.name)) != undefined){
					$scope.maturity = $scope.techFromRadar.maturity;
					$scope.techGroup = $scope.techFromRadar.techGrouping;
				} else {
					//  both of these attributes are currently undefined in selectedTechnology object
					$scope.maturity = $scope.selectedTechnology.maturity;
					$scope.techGroup = $scope.selectedTechnology.techGrouping;
				}
				
				$scope.setMaturityStyle($scope.maturity);
				$scope.setTechGroupStyle($scope.techGroup);
			};
			
			$scope.setMaturityStyle = function (maturity){
				for(var i = 0; i < $scope.maturityOptions.length; i++) {
					$scope[$scope.maturityOptions[i].value.replace(' ','') + 'Style'] = ''; 
				}
				if (maturity != undefined){
					$scope[maturity.replace(' ','') + 'Style'] = 'active';
				}
			}
			
			$scope.setTechGroupStyle = function (techGroup){
				for(var i = 0; i < $scope.techGroupingOptions.length; i++) {
					$scope[$scope.techGroupingOptions[i].value.replace(' ','') + 'Style'] = ''; 
				}
				if (techGroup != undefined){
					$scope[techGroup.replace(' ','') + 'Style'] = 'active';
				}
			}
			
			$scope.isInRadar = function (techName){
				return $scope.getTechFromRadar(techName) != undefined;
			}
			
			$scope.getTechFromRadar = function (techName){
				if ($scope.selectedRadar.technologies == undefined){
					return undefined;
				}
				
				for(var i = 0; i < $scope.selectedRadar.technologies.length; i++) {
					if ($scope.selectedRadar.technologies[i].technology == techName){
						return $scope.selectedRadar.technologies[i];
					}
				}
				return undefined;
			}

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

			$scope.add = function() {
				
				if ($scope.maturity == undefined){
					$scope.validationMessage = "Select maturity.";
					return;
				}
				
				if ($scope.techGroup == undefined){
					$scope.validationMessage = "Select technology group.";
					return;
				}
				$scope.validationMessage = "";
				
				newTech = {id: 1,
						techGrouping: $scope.techGroup,
						maturity: $scope.maturity,
						technology: $scope.selectedTechnology.name};
				
				$scope.selectedRadar.technologies.push(newTech);
			};

			$scope.remove = function() {
				for(var i = 0; i < $scope.selectedRadar.technologies.length; i++) {
					if ($scope.selectedRadar.technologies[i].technology == $scope.selectedTechnology.name){
						$scope.selectedRadar.technologies.splice(i,1);
						return;
					}
				}
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
		template: '<svg width="{{scaleFactor*1000}}" height="{{scaleFactor*400}}" version="1.1" xmlns="http://www.w3.org/2000/svg">' +
		'  <rect x="{{scaleFactor*5}}"   y="{{scaleFactor*(10+(300-300*(watching.length/max)))}}"  rx="{{scaleFactor*20}}" ry="{{scaleFactor*20}}" width="{{scaleFactor*(190)}}" height="{{scaleFactor*(300*(watching.length/max))}}"  style="fill:#FFFFFF;stroke-width:1;stroke:#CCCCCC"></rect>' +
		'  <rect x="{{scaleFactor*205}}" y="{{scaleFactor*(10+(300-300*(learning.length/max)))}}"  rx="{{scaleFactor*20}}" ry="{{scaleFactor*20}}" width="{{scaleFactor*190}}"   height="{{scaleFactor*(300*(learning.length/max))}}"  style="fill:#428BCA;stroke-width:1;stroke:#428BCA"></rect>' +
		'  <rect x="{{scaleFactor*405}}" y="{{scaleFactor*(10+(300-300*(competent.length/max)))}}" rx="{{scaleFactor*20}}" ry="{{scaleFactor*20}}" width="{{scaleFactor*190}}"   height="{{scaleFactor*(300*(competent.length/max))}}" style="fill:#5BC0DE;stroke-width:1;stroke:#5BC0DE"></rect>' +
		'  <rect x="{{scaleFactor*605}}" y="{{scaleFactor*(10+(300-300*(expert.length/max)))}}"    rx="{{scaleFactor*20}}" ry="{{scaleFactor*20}}" width="{{scaleFactor*190}}"   height="{{scaleFactor*(300*(expert.length/max))}}"    style="fill:#F0AD4E;stroke-width:1;stroke:#F0AD4E"></rect>' +
		'  <rect x="{{scaleFactor*805}}" y="{{scaleFactor*(10+(300-300*(leader.length/max)))}}"    rx="{{scaleFactor*20}}" ry="{{scaleFactor*20}}" width="{{scaleFactor*190}}"   height="{{scaleFactor*(300*(leader.length/max))}}"    style="fill:#5CB85C;stroke-width:1;stroke:#5CB85C"></rect>' +
		'  <text x="{{scaleFactor*100}}" y="{{scaleFactor*350}}" text-anchor="middle" fill="#333333" style="{{\'font-size: \' + scaleFactor*20 + \'px; font-weight: 900;\'">Watching</text>' +
		'  <text x="{{scaleFactor*300}}" y="{{scaleFactor*350}}" text-anchor="middle" fill="#333333" style="{{\'font-size: \' + scaleFactor*20 + \'px; font-weight: 900;\'">Learning</text>' +
		'  <text x="{{scaleFactor*500}}" y="{{scaleFactor*350}}" text-anchor="middle" fill="#333333" style="{{\'font-size: \' + scaleFactor*20 + \'px; font-weight: 900;\'">Competent</text>' +
		'  <text x="{{scaleFactor*700}}" y="{{scaleFactor*350}}" text-anchor="middle" fill="#333333" style="{{\'font-size: \' + scaleFactor*20 + \'px; font-weight: 900;\'">Expert</text>' +
		'  <text x="{{scaleFactor*900}}" y="{{scaleFactor*350}}" text-anchor="middle" fill="#333333" style="{{\'font-size: \' + scaleFactor*20 + \'px; font-weight: 900;\'">Leader</text>' +
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
		template: '<svg width="{{scaleFactor*1000}}" height="{{scaleFactor*200}}" version="1.1" xmlns="http://www.w3.org/2000/svg">' +
		'  <g ng-repeat="skillLevel in skillLevels">' +
		'    <text x="{{scaleFactor*(100+$index*200)}}" y="{{scaleFactor*190}}" style="text-anchor:middle">{{skillLevel.name}}</text>' +
		'    <g ng-repeat="sl in skillLevel.technologies">' +
		'      <rect x="{{scaleFactor*(10+$parent.$index*200)}}" y="{{scaleFactor*(140-($index*35))}}" rx="{{scaleFactor*5}}" ry="{{scaleFactor*5}}" width="{{scaleFactor*190}}" height="{{scaleFactor*30}}" style="{{\'fill:\'+skillLevel.fill+\';stroke:\'+skillLevel.stroke+\'stroke-width:1\'}}"></rect>' +
		'      <text x="{{scaleFactor*(100+$parent.$index*200)}}" y="{{scaleFactor*(160-($index*35))}}" style="text-anchor:middle" fill="{{skillLevel.textFill}}">{{sl.name}}</text>' +
		'    </g>' +
		'  </g>' +
		'</svg>',
		link: function ($scope, element, attrs) {
			
			$scope.skillLevels = [
			                    {name:'LEADER',   fill:'#5CB85C',stroke:'#5CB85C',textFill:'#FFFFFF',technologies:[{name:'Google Web Toolkit'},{name:'Bamboo'}]},
			                    {name:'EXPERT',   fill:'#F0AD4E',stroke:'#F0AD4E',textFill:'#FFFFFF',technologies:[{name:'C++'},{name:'.Net'},{name:'C#'}]},
			                    {name:'COMPETENT',fill:'#5BC0DE',stroke:'#5BC0DE',textFill:'#FFFFFF',technologies:[{name:'Java EE'},{name:'Java SE'},{name:'Python'},{name:'Shell Script'},{name:'SQL'}]},
			                    {name:'LEARNING', fill:'#428BCA',stroke:'#428BCA',textFill:'#FFFFFF',technologies:[{name:'XPath'},{name:'XSD'}]},
			                    {name:'WATCHING', fill:'#FFFFFF',stroke:'#CCCCCC',textFill:'#333333',technologies:[{name:'XSLT'},{name:'COM/ActiveX'},{name:'EMITE'}]}
			                      ];

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
