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
				<li><a href="#">Ricky Winterbourne  <img class="img img-rounded" src="/radar/img/128.jpg"></a></li>
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
			<button class="btn btn-danger" ng-click="go('/radar/'+selectedRadar.id+'/edit')">Edit</button>
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
