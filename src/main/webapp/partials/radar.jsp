<div class="container-fluid main-content" ng-controller="RadarCtrl">
	
	<div class="row">
		
	<div class="col-md-12 clearfix" style="height: 238px;margin-bottom: 15px;border-bottom: 1px solid #CCC;padding-bottom: 10px;white-space: nowrap;overflow-x: auto;">
		<div style="display:inline-block;margin: 10px;padding: 10px;" ng-repeat="radar in radars" class="chart">
			<a ng-href="/radar/view/{{radar.id}}">
				<div style="cursor: pointer;">
					<div class="text-center radar-preview{{radar.id==selectedId?' selected-radar':''}}">
						<img ng-src="/radar/preview/{{radar.id}}?w=150"></img>
					</div>
					<div class="text-center">
						{{radar.filename}}
					</div>
					<div class="text-center">
						<small>{{radar.dateUploaded | date:'dd MMM yy'}}</small>
					</div>
				</div>
			</a>
		</div>
	</div>
	
	<div class="col-md-12">
		<form method="post" encoding="multipart/form-data" enctype="multipart/form-data" action="/radar/rest/service/upload">
			<input id="file" name="file" type="file"></input>
			<button type="submit" class="btn btn-primary">upload</button>
		</form>
	</div>

	<div class="col-md-4 sidebar">
		<div ng-repeat="quadrant in selectedRadar.radar.quadrants">
			<h3>{{quadrant.name}}</h3>
			<ul>
				<li ng-repeat="item in quadrant.items">
					<div ng-click="item.show= !item.show" ng-mouseover="mouseOver(item)" ng-mouseout="mouseOut(item)" style="{{selectedItem.id==item.id||item.show==true ? 'cursor:pointer;color:white;background-color:'+quadrant.color : ''}}">{{item.id}}.{{item.name}}<span ng-show="item.movement=='c'" class="radar-movement">new</span></div>
					<div ng-show="item.show==true" class="more-detail">
						<p ng-show="item.description!=null">{{item.description}}</p>
						<div>
							<a ng-show="item.detailUrl!=null" ng-href="{{item.detailUrl}}">more...</a>
						</div>
					</div>
				</li>
			</ul>
		</div>
	</div>

	<div class="col-md-8">
		<div id="radar" ng-radar="" radar="selectedRadar.radar" selected-blip="selectedItem"></div>
			
		<form id="theForm" action="/radar/rest/service/upload" method="post">
			<input hidden="true" id="data" name="data" value=""></input>
		</form>
		<button ng-show="selectedRadar.technologies.length > 0" class="btn btn-primary" onclick="exportSvg();">export</button>
		<script>
			exportSvg = function() {
				var theSvg = document.getElementById('radar').firstChild;
				var s = new XMLSerializer();
				var str = s.serializeToString(theSvg);
				
				var form = document.getElementById('theForm');
				form['data'].value = str;
				form.submit();
			}
		</script>
	</div>
		
	</div>
</div>