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
