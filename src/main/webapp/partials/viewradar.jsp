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
					<a ng-click="loginclick=!loginclick">Sign in  <img class="img img-rounded" src="/radar/img/icon_8204.png"></a>
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
	
	<form id="theForm" action="/radar/upload" method="post">
		<input hidden="true" id="id" name="id" value=""></input>
	</form>	
	
	<form id="csvExportForm" action="/radar/export/csv" method="post">
		<input hidden="true" id="id" name="id" value=""></input>
	</form>	
	
	<div class="col-md-12 clearfix" style="margin-bottom: 0;border-bottom: 1px solid #CCC;padding-bottom: 20px;">
		<div class="col-md-6">
			<h1><i class="glyphicon glyphicon-ok" style="color:green;"></i> {{selectedRadar.name}}</h1>
		</div>
		<div class="col-md-6 text-right">
			<button class="btn btn-default" ng-click="exportCsv(selectedRadar.id)">Download Data</button>
			<button class="btn btn-default" ng-click="exportSvg(selectedRadar.id)">Export PDF</button>
			<button class="btn btn-default">Share</button>
			<button class="btn btn-danger" ng-click="go('/radar/'+selectedRadar.id+'/edit')" ng-show="loggedin==true">Edit</button>
		</div>
	</div>

	<div class="col-md-12" style="background-color:rgb(236,236,236);padding:25px 20px;">
	
		<div class="col-md-12">
			<div class="col-md-6">
				<ul class="nav nav-pills">
					<li ng-class="selectedQuad == '' ? 'active' : ''"><a href ng-click="go('/radar/'+selectedRadar.id)">All</a></li>
					<li ng-repeat="quadrant in selectedRadar.radar.quadrants" ng-class="selectedQuad == quadrant.name ? 'active' : ''">
						<a href ng-click="go('/radar/'+selectedRadar.id+'/'+quadrant.name)">{{quadrant.name}}</a>
					</li>
				</ul>
			</div>
			<div class="col-md-6 text-right" style="padding:10px;">
				<div class="col-xs-6" style="padding:8px 0;">
					<span>Highest Skills</span>
				</div>
				<div class="col-xs-6">
					<select class="form-control">
						<option value="None">None</option>
					</select>
				</div>
			</div>
		</div>
		
		<div class="col-md-12" style="padding:50px 10%;">
			<div id="radar" ng-radar="" radar="selectedRadar.radar" selected-blip="selectedItem"></div>
		</div>
		
	</div>
		
	</div>
</div>
