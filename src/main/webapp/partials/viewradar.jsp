<div class="container-fluid main-content" ng-controller="RadarCtrl">
	
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

	<div class="col-md-12" style="background-color:#EFEFEF;padding:25px 20px;">
	
		<div class="col-md-12">
			<div class="col-md-6">
				<ul class="nav nav-pills">
					<li class="active"><a href="#">All</a></li>
					<li ng-repeat="quadrant in selectedRadar.radar.quadrants"><a href="#">{{quadrant.name}}</a></li>
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
		
		<div class="col-md-12" style="padding:50px 100px;">
			<div id="radar" ng-radar="" radar="selectedRadar.radar" selected-blip="selectedItem"></div>
		</div>
		
	</div>
		
	</div>
</div>