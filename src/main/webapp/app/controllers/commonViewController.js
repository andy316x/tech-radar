techRadarControllers.controller('CommonViewCtrl', ['$scope','$http','$location','$routeParams','$modal','$log','trBannerService',function ($scope, $http, $location, $routeParams, $modal, $log, trBannerService) {

	$scope.banner = trBannerService;
	
	// Stuff that all views will need
	$scope.activePage = function(page){
		return $location.url().match(page);
	}

	$scope.items = ['item1', 'item2', 'item3'];

	$scope.open = function (size) {

		var modalInstance = $modal.open({
			templateUrl: 'myModalContent.html',
			controller: 'ModalInstanceCtrl',
			size: size,
			resolve: {
				items: function () {
					return $scope.items;
				}
			}
		});

		modalInstance.result.then(function (selectedItem) {
			$scope.selected = selectedItem;
		}, function () {
			$log.info('Modal dismissed at: ' + new Date());
		});
	};



	// Login
	$scope.loggedin = false;


	var getUserInfo = function() {
		// User info
		$http({method: 'GET', url: '/radar/rest/me?nocache=' + (new Date()).getTime()}).
		success(function(data, status, headers, config) {
			$scope.loggedin = true;
			$scope.uid = data.uid;
			$scope.name = data.name;
		}).
		error(function(data, status, headers, config) {
			if(status==403) {
				// The user is not authorised
				$scope.loggedin = false;
				$log.log('User is not logged in');
				var checkLogin = function() {
					var loginFrame = document.getElementById('loginframe');
					if(loginFrame != null) {
						var loginWindow = loginFrame.contentWindow;
						if(typeof loginWindow.techRadarData !== 'undefined') {
							// User has now logged in so get their details
							getUserInfo();
						} else {
							// User is still not logged in, try again in a bit
							window.setTimeout(checkLogin, 1000);
						}
					} else {
						// The login frame is not on the page, try again in a bit
						window.setTimeout(checkLogin, 1000);
					}
				};
				checkLogin();
			} else {
				// TODO error, we have an unexpected error case
				$log.error('Unexpected error occurred while verifying user identity');
			}
		});
	};
	getUserInfo();

	// Navigation
	$scope.go = function ( path ) {
		$location.path( path );
	};
	$scope.goApply = function ( path ) {
		$scope.$apply(function() {
			$location.path( path );
		});
	};
	
	
	// TODO create a modal service, this is hack here 
	// to remove the modal backdrop on navigation
	$scope.$on('$locationChangeStart', function(event, newUrl) {
		$('.modal-backdrop').hide();
		$('.modal-open').removeClass('modal-open'); 
	});

}]);
