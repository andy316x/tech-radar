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
						$scope.theRadar.unselectBlip(oldVal);
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
					$scope['techGrouping'+index] = grouping;
			};

			$scope.techGroupingOptions = [];
            //TODO - get defaults from outside
			$scope.techGrouping1 = 'Dev Tool';
			$scope.techGrouping2 = 'Dev Language';
			$scope.techGrouping3 = 'Platform';
			$scope.techGrouping4 = 'Solution Technology';
			$http.get('/radar/rest/quadrant').
			success(function(data) {
				data.forEach(function(d){
					$scope.techGroupingOptions.push({label:d.name, value:d.name});
				});
			}).
			error(function(data, status, headers, config) {
				console.log('failed to load tech groupings');
			});
			
			$scope.businessUnitOptions = [];
			$http.get('/radar/rest/businessunit')
			.success(function(data) {
				data.forEach(function(d){
					$scope.businessUnitOptions.push({label:d.name, value:d.name});
				});
				$scope.businessUnit = $scope.businessUnitOptions[0].value;
			})
			.error(function(data, status, headers, config) {
				console.log('failed to load business units');
			});

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
                    //TODO - server side time
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
				success(function(data) {
					$scope.radarCreated({radar: data});
				}).
				error(function(data) {
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
		templateUrl: 'templates/add-tech.html',
		link: function ($scope, element, attrs) {
			$scope.stage = 1;
			
			function getTechFromRadar(techName){
				if ($scope.selectedRadar.technologies == undefined){
					return undefined;
				}
				
				for(var i = 0; i < $scope.selectedRadar.technologies.length; i++) {
					if ($scope.selectedRadar.technologies[i].technology === techName){
						return $scope.selectedRadar.technologies[i];
					}
				}
				return undefined;
			};
			
			function isInRadar(techName){
				return getTechFromRadar(techName) != undefined;
			};
			
			function setTechnologySelection() {
				$scope.technologies.forEach(function(t){
					var techInRadar = getTechFromRadar(t.name);
					if(typeof techInRadar !== 'undefined') {
						t.selected = true;
						t.maturity = techInRadar.maturity;
						t.quadrant = techInRadar.quadrant;
					} else {
						t.selected = false;
						if(typeof $scope.maturityOptions != 'undefined' && $scope.maturityOptions.length > 0) {
							t.maturity = $scope.maturityOptions[0].value;
						}
						for(var j = 0; j < $scope.quadrantOptions.length; j++) {
							if(t.techGrouping == $scope.quadrantOptions[j].value) {
								t.quadrant = t.techGrouping;
							}
						}
					}
				})
			};
			
			$scope.technologies = [];
			$http({method: 'GET', url: '/radar/rest/technology?nocache=' + (new Date()).getTime()}).
			success(function(data) {
                $scope.technologies = $scope.technologies.concat(data);
				setTechnologySelection();
			}).
			error(function(data) {
				console.log('error getting technology list');
			});
			
			$scope.technologySelected = function(technology) {
				technology.selected = !(technology.selected);
			};
			
			$scope.countSelected = function(technologies) {
                return technologies.filter(function(curr){
                    return curr.selected;
                }).length;
			};
			
			function updateOptions() {
				if($scope.selectedRadar){
					if($scope.selectedRadar.maturities) {
						$scope.maturityOptions = $scope.selectedRadar.maturities.map(function(m){
                            return {label:m.name, value:m.name};
                        });
					}
					
					if($scope.selectedRadar.quadrants) {
						$scope.quadrantOptions = $scope.selectedRadar.quadrants.map(function(quadrant){
                            return {label:quadrant.name, value:quadrant.name};
                        });
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
                $scope.technologies.forEach(function(tech){
                    if(tech.selected){
                        var valid = true;
						if(!(tech.maturity)) {
							valid = false;
							$scope.errors.push({text:tech.name + ' has no maturity set'});
						}
						if(!(tech.quadrant)) {
							valid = false;
							$scope.errors.push({text:tech.name + ' has no tech grouping set'});
						}
						if(valid) {
							newTechs.push({
								technology:tech.name,
								maturity:tech.maturity,
								quadrant:tech.quadrant,
								techGrouping:tech.techGrouping
							});
						}
                    }
                });
				if(!($scope.errors.length)) {
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
		templateUrl: 'templates/technology-modal.html',
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
		templateUrl: 'templates/skill-level',
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
