var techRadarDirectives = angular.module('techRadarDirectives', []);

techRadarDirectives.filter('reverse', function() {
	  return function(items) {
		  if(items){
			  return items.slice().reverse();
		  }
		  return items;
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

techRadarDirectives.directive('ngVbox', function() {
	  return {
		restrict: 'A',
	    link: function(scope, element, attrs) {
	      attrs.$observe('ngVbox', function(value) {
              element.context.setAttribute('viewBox', value);
	      });
	    }
	  };
	});