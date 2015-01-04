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
      						<iframe id="loginframe" src="loginsuccess.html" style="width: 100%;height: 265px;border: none;overflow: hidden;"></iframe>
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
				<h1><i class="glyphicon glyphicon-ok" style="color:green;"></i> {{technology.name}}</h1>
			</div>
			<div class="col-md-3">
				<div class="search-wrapper">
					<input ng-search="" on-radar-selected="goApply('/radar/' + id)" on-technology-selected="goApply('/technology/' + id)" type="text" class="form-control typeahead" placeholder="Search"></input>
					<i class="glyphicon glyphicon-search"></i>
				</div>
			</div>
		</div>
		
		<div class="col-md-12" style="background-color:#EFEFEF;padding:25px 20px;">
			<div class="col-sm-6">
				<blockquote>
					<p ng-show="technology.description!=null">{{technology.description}}</p>
					<p ng-show="technology.description==null">No description</p> 
					<footer><strong>{{technology.name}}</strong> is advocated by <cite title="Andy Wilson">Andy Wilson</cite></footer>
				</blockquote>
				<div class="btn-group btn-group-justified" role="group" aria-label="...">
					<div class="btn-group" role="group">
						<button type="button" class="btn btn-xs btn-danger {{currentSkillLevel==null?'active':''}}" ng-click="selectSkillLevel(null)">None</button>
					</div>
					<div class="btn-group" role="group">
						<button type="button" class="btn btn-xs btn-default {{currentSkillLevel=='Watching'?'active':''}}" ng-click="selectSkillLevel('Watching')">Watching</button>
					</div>
					<div class="btn-group" role="group">
						<button type="button" class="btn btn-xs btn-primary {{currentSkillLevel=='Learning'?'active':''}}" ng-click="selectSkillLevel('Learning')">Learning</button>
					</div>
					<div class="btn-group" role="group">
						<button type="button" class="btn btn-xs btn-info {{currentSkillLevel=='Competent'?'active':''}}" ng-click="selectSkillLevel('Competent')">Competent</button>
					</div>
					<div class="btn-group" role="group">
						<button type="button" class="btn btn-xs btn-warning {{currentSkillLevel=='Expert'?'active':''}}" ng-click="selectSkillLevel('Expert')">Expert</button>
					</div>
					<div class="btn-group" role="group">
						<button type="button" class="btn btn-xs btn-success {{currentSkillLevel=='Leader'?'active':''}}" ng-click="selectSkillLevel('Leader')">Leader</button>
					</div>
				</div>
			</div>
			<div class="col-sm-6">
				<div ng-repeat="rating in ratings">
					<span>{{rating.user}} - {{rating.skillLevel}}</span>
				</div>
				<div ng-repeat="otherRadar in otherRadars">
					<strong>{{otherRadar.addedByUid}}</strong> added <strong>{{technology.name}}</strong> to <a href="/radar/#/radar/{{otherRadar.radarId}}">{{otherRadar.radarName}}</a> {{otherRadar.addedDate | prettydate}}
				</div>
			</div>
			
		</div>
		
	</div>
</div>