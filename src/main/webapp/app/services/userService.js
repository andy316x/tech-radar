techRadarServices.factory('trUserService', function(){
	var f = function(){
		this.getUserImage = function(uid){
			return "/radar/img/128.jpg";
		};
	};
	return new f();
});