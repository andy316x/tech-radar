techRadarDirectives.directive('ngSkillLevel', function () {
	return {
		restrict: 'E',
		scope: {
			skillLevels: '='
		},
		templateUrl: 'templates/skill-level.html',
		link: function ($scope, element, attrs) {
			$scope.skills = [
			                    {name:'LEADER',   fill:'#5CB85C',stroke:'#5CB85C',textFill:'#FFFFFF',technologies:[]},
			                    {name:'EXPERT',   fill:'#F0AD4E',stroke:'#F0AD4E',textFill:'#FFFFFF',technologies:[]},
			                    {name:'COMPETENT',fill:'#5BC0DE',stroke:'#5BC0DE',textFill:'#FFFFFF',technologies:[]},
			                    {name:'LEARNING', fill:'#428BCA',stroke:'#428BCA',textFill:'#FFFFFF',technologies:[]},
			                    {name:'WATCHING', fill:'#FFFFFF',stroke:'#CCCCCC',textFill:'#333333',technologies:[]}
			                      ];
			var indexes = {
					'Leader':0,
					'Expert':1,
					'Competent':2,
					'Learning':3,
					'Watching':4,
			};

			$scope.getWidth = function () {
				return element[0].offsetWidth;
			};

			$scope.$watch('skillLevels', function (newval, oldval) {
				var theMax = 0;
				if(newval){
					newval.forEach(function(skill){
						var idx = indexes[skill.skillLevel];
						$scope.skills[idx].technologies.push({name:skill.technology});
					});
					$scope.skills.forEach(function(skill){
						theMax = Math.max(theMax, skill.technologies.length);
					});
				}
				$scope.highest = theMax;
				element.css('padding-top', (theMax * 40 * 10/100)+'%');
			}, true);
			
		}
	};
});
