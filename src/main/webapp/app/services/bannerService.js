techRadarServices.factory('trBannerService', function bannerServiceFactory() {
	var f = function() {
		var self = this;
		this.message = '';
		this.setMessage = function(msg) {
			self.message = msg;
		};
	}
	return new f();
});