<div class="container-fluid main-content" ng-controller="RadarCtrl">
	
	<div class="row">
	
		<div class="col-md-12 clearfix" style="margin-bottom: 0;border-bottom: 1px solid #CCC;padding-bottom: 20px;">
			<div class="col-md-6">
				<ul class="nav nav-pills">
					<li  class="active"><a href="#">All</a></li>
					<li><a href="#">UK Services</a></li>
					<li><a href="#">Cyber</a></li>
					<li><a href="#">Communications</a></li>
					<li><a href="#">My Radars</a></li>
				</ul>
			</div>
			<div class="col-md-3">
				<div class="left-inner-addon">
					<i class="glyphicon glyphicon-search"></i>
					<input type="text" class="form-control" placeholder="Search"></input>
				</div>
			</div>
			<div class="col-md-3 text-right">
				<button class="btn btn-success" ng-click="newRadarVisible=true">New Radar</button>
			</div>
		</div>
		
		<div ng-new-radar="" visible="newRadarVisible" radar-created="onRadarCreated()"></div>
		
		<div class="col-md-12" style="background-color:#EFEFEF;padding:25px 20px;">
	
			<div ng-repeat="radar in radars" class="radar-card">
				<div style="margin-bottom: 20px;">
					<div class="col-xs-6">
						{{radar.name}}
					</div>
					<div class="col-xs-6 text-right">
						<i class="glyphicon glyphicon-ok" style="color:green;"></i>
					</div>
				</div>
				<div style="text-align:center;">
					<img ng-src="/radar/preview/{{radar.id}}?w=175"></img>
				</div>
				<div>
					<img class="img img-rounded" src="/radar/img/128.jpg">
					<div style="padding-left:60px;">
						<div><strong>Ricky Winterbourne</strong></div>
						<div><small>(UK Services)</small></div>
						<div><small>Published <strong>{{radar.dateUploaded | date:'dd MMM yy'}}</strong></small></div>
					</div>
				</div>
				<div>
					<button class="btn btn-primary btn-block" ng-click="go('/radar/' + radar.id)">View</button>
				</div>
			</div>
			
		</div>
		
	</div>
</div>