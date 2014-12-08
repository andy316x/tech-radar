<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target=".navbar-collapse">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="#">The Radar</a>
		</div>
		<div class="navbar-collapse collapse">
			<ul class="nav navbar-nav navbar-right">
				<li class="active"><a href="#/radar">Radars</a></li>
				<li><a href="#/technology">Technologies</a></li>
				<li><a href="#">Skills Profile</a></li>
				<li ng-show="loggedin==true"><a>{{name}}  <img ng-click="loginclick=!loginclick" class="img img-rounded" src="/radar/img/128.jpg"></a></li>
				<li ng-show="loggedin==false">
					<a  ng-click="loginclick=!loginclick">Sign in  <img class="img img-rounded" src="/radar/img/icon_8204.png"></a>
					<div ng-show="loginclick==true" class="popover fade bottom in" style="position: absolute;z-index: 100;top: 38px;left: -185px;display: block;width: 300px;padding: 16px 0px 0px 0px;">
      					<div class="arrow" style="margin-left:87px"></div>
      					<h3 class="popover-title" style="background-color: #FFF;color: #333;padding-top: 0;">Sign in</h3>
      					<div class="popover-content form-group">
      						<p>Sign in to Tech Radar using your Black network username and password</p>
      						<p><strong>Username</strong></p>
    						<p><input class="form-control" type="text" name="j_username" size="25" ng-model="username"></p>
    						<p><strong>Password</strong></p>
    						<p><input class="form-control" type="password" size="15" name="j_password" ng-model="password"></p>
    						<p class="text-danger" ng-show="wrongpassword==true">* Incorrect username/password</p>
    						<p class="text-right"><button class="btn btn-success" ng-click="login(username, password)">Sign in</button></p>
      					</div>
    				</div>
				</li>
			</ul>
		</div>
	</div>
</div>

<div class="container-fluid main-content">
	
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
				<div class="search-wrapper">
					<input ng-search="" on-radar-selected="goApply('/radar/' + id)" on-technology-selected="goApply('/technology/' + id)" type="text" class="form-control typeahead" placeholder="Search"></input>
					<i class="glyphicon glyphicon-search"></i>
				</div>
			</div>
			<div class="col-md-3 text-right">
				<button class="btn btn-success" ng-click="newRadarVisible=true" ng-show="loggedin==true">New Radar</button>
			</div>
		</div>
		
		<div ng-new-radar="" visible="newRadarVisible" radar-created="onRadarCreated(radar)"></div>
		
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
					<img src="/radar/img/radar_175.svg"></img>
				</div>
				<div>
					<img class="img img-rounded" src="/radar/img/128.jpg">
					<div style="padding-left:60px;">
						<div><strong>{{radar.createdBy}}</strong></div>
						<div><small>(UK Services)</small></div>
						<div><small>Published <strong>{{radar.dateCreated | date:'dd MMM yy'}}</strong></small></div>
					</div>
				</div>
				<div>
					<button class="btn btn-primary btn-block" ng-click="go('/radar/' + radar.id)">View</button>
				</div>
			</div>
			
			<form method=post>
    			
			</form>
			
		</div>

		
	</div>
</div>