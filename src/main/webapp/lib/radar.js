var rad=function(deg){
	return deg*Math.PI/180;
};

var global_radar;
var global_totalArc;

var maxSample = 30;
var maxRadius = 100;

var Radar = {
		
	draw: function(element, radar, callback) {
		
		var w = element.offsetWidth;
		var h = w;
		
		global_radar = radar;

		var scaleFactor = w/1000;
		var allRails = [];
		var maxBlips = new Array(radar.arcs.length);
		for(var i = 0; i < radar.quadrants.length; i++) {
			var quadrant = radar.quadrants[i];
			allRails.push([]);
			for(var j = 0; j < radar.arcs.length; j++) {
				allRails[i].push([]);
			}
		
			for(var j = 0; j < quadrant.items.length; j++) {
				var arcIndex = function(arcName){
					for(var k = 0; k < radar.arcs.length; k++){
						if(radar.arcs[k].name == arcName){
							return k;
						}
					}
					return 0;
				}(quadrant.items[j].arc);
				allRails[i][arcIndex].push(quadrant.items[j]);
			}
			
			for(var j = 0; j < radar.arcs.length;j++){
				if(typeof maxBlips[j] === "undefined" || maxBlips[j] < allRails[i][j].length){
					maxBlips[j] = allRails[i][j].length;
				}
			}
		}
		
		var totalMaxBlips = 0;
		for(var i = 0; i < radar.arcs.length; i++){
			if(maxBlips[i] == 0){
				maxBlips[i] = 1;
			}
			totalMaxBlips += maxBlips[i];
		}
		
		for(var i = 0; i < radar.arcs.length; i++){
			maxBlips[i] = Math.sqrt((maxBlips[i]/totalMaxBlips)/(1/(radar.arcs.length - i)));
			if(totalMaxBlips === 0){
				maxBlips[i] = 1;
			}
		}
		
		var totalArc = 0;
		for(var i = 0; i < radar.arcs.length; i++) {
			totalArc = totalArc + radar.arcs[i].r*maxBlips[i];
		}
		global_totalArc = totalArc;
		
		while (element.firstChild) {
			element.removeChild(element.firstChild);
		}
		
		var centres = [
		               [w/2, w/2, w],
		               [w - scaleFactor*6, w - scaleFactor*6, w + scaleFactor*6],
		               [scaleFactor*6, w - scaleFactor*6, w + scaleFactor*6],
		               [scaleFactor*6, w/2 - scaleFactor*6, w + scaleFactor*6],
		               [w - scaleFactor*6, w/2 - scaleFactor*6, w + scaleFactor*6],
		               ];
		
		var canvas=d3.select(element)
			.insert('svg',':first-child')
			.attr('xmlns:xmlns:xlink','http://www.w3.org/1999/xlink')
			.attr('width',w)
			.attr('height',h)
		canvas.selectAll('*').remove();
		
		var svg = canvas.append('g');
		
		function transition(svg, start, end) {
			var center = [w / 2, h / 2],
			i = d3.interpolateZoom(start, end);
			
			svg
			.attr("transform", transform(start))
			.transition()
			.delay(250)
			.duration(i.duration * 2)
			.attrTween("transform", function() { return function(t) { return transform(i(t)); }; });

			function transform(p) {
				var k = h / p[2];
				return "translate(" + (center[0] - p[0] * k) + "," + (center[1] - p[1] * k) + ")scale(" + k + ")";
			}
		}
		
		// filters go in defs element
		var defs = svg.append("defs");

		// create filter with id #drop-shadow
		// height=130% so that the shadow is not clipped
		var filter = defs.append("filter")
		    .attr("id", "drop-shadow")
		    .attr("height", "150%");

		// SourceAlpha refers to opacity of graphic that this filter will be applied to
		// convolve that with a Gaussian with standard deviation 3 and store result
		// in blur
		filter.append("feGaussianBlur")
		    .attr("in", "SourceAlpha")
		    .attr("stdDeviation", 2)
		    .attr("result", "blur");

		// translate output of Gaussian blur to the right and downwards with 2px
		// store result in offsetBlur
		filter.append("feOffset")
		    .attr("in", "blur")
		    .attr("dx", 1)
		    .attr("dy", 1)
		    .attr("result", "offsetBlur");

		// overlay original SourceGraphic over translated blurred opacity by using
		// feMerge filter. Order of specifying inputs is important!
		var feMerge = filter.append("feMerge");

		feMerge.append("feMergeNode")
		    .attr("in", "offsetBlur")
		feMerge.append("feMergeNode")
		    .attr("in", "SourceGraphic");
		
		var cumulativeArc = 0;
		var arcMap = {};
		var arcs = [];
		for(var i = 0; i < radar.arcs.length; i++) {
			
			var r = (radar.arcs[i].r*maxBlips[i] / totalArc)*(w/2);
			
			this._drawArc(svg, cumulativeArc, cumulativeArc + r, w/2, h/2, radar.arcs[i].color);
			
			var arc = {
				innerRadius: cumulativeArc,
				outerRadius: cumulativeArc + r,
				index: i
			};
			arcMap[radar.arcs[i].id] = arc;
			arcs.push(arc);
			
			cumulativeArc = cumulativeArc + r;
		}
		
		var axisWidth = scaleFactor*25;
		svg.append('rect')
			.attr('x',0)
			.attr('y',(h/2)-(axisWidth/2))
			.attr('width',w)
			.attr('height',scaleFactor*axisWidth)
			.attr('fill','rgb(236,236,236)');
		
		svg.append('rect')
			.attr('x',(w/2)-(axisWidth/2))
			.attr('y',0)
			.attr('width',scaleFactor*axisWidth)
			.attr('height',h)
			.attr('fill','rgb(236,236,236)');
		
		for(var i = 0; i < radar.arcs.length; i++) {
			
			var arc = arcMap[radar.arcs[i].id];
			
			this._drawArcAxisText(svg, arc.innerRadius, arc.outerRadius, w, h, radar.arcs[i].name, scaleFactor);
		}
		
		this._drawKey(svg,w,h,scaleFactor);
		
		var translation = [[1, 1, 'end', 1, 0], [-1, 1, 'start', 0, 1], [-1, -1, 'start', 0, 1], [1, -1, 'end', 1, 0]];
		
		var blips = [];
		var quadrantMap = {};
		var angle = 0;
		for(var i = 0; i < radar.quadrants.length; i++) {
			var quadrant = radar.quadrants[i];
			quadrantMap[quadrant.id] = quadrant;
			quadrant.startAngle = angle;
			
			var labelx = (w/2) + translation[i][0]*(w/2);
			var labely = (h/2) + translation[i][1]*0.95*(h/2);
			var textElement = svg.append('text')
				.attr({'x':labelx+translation[i][4]*scaleFactor*20,'y':labely,'font-size':scaleFactor*14,'font-weight':'bold'})
				.style({'text-anchor':translation[i][2]})
				.text(quadrant.name);
			
			svg.append('circle')
			.attr('r',scaleFactor*7)
			.attr('fill',quadrant.color)
			.attr('cx',labelx+scaleFactor*10-translation[i][3]*(scaleFactor*20+textElement.node().getComputedTextLength()))
			.attr('cy',labely-scaleFactor*5);
			
			var arcRails = allRails[i];
			
			for(var j = 0; j < arcRails.length; j++) {
				var rails = arcRails[j];
				blips.push([]);
				
				var poisson_dist = [];
				var queue = [];
				var queueSize = 0;
				var radius = maxRadius;
				var radius2 = radius*radius;
				var R = 3*radius;
				var quadNo = i;
				var arcNo = j;
				
				while(poisson_dist.length < rails.length){
					if(poisson_dist.length > 0 && queueSize == 0){
						poisson_dist = [];
						queue = [];
						queueSize = 0;
						radius -= 2;
						radius2 = radius*radius;
						R = 3*radius;
					}
					
					while(queueSize == 0){
						var innerRadius = arcs[arcNo].innerRadius;
						var outerRadius = arcs[arcNo].outerRadius;
						
						var r = ((outerRadius-innerRadius)/2)+innerRadius;
						var x = (w/2) + r*Math.cos(rad(90/2 + angle));
						var y = (h/2) + r*Math.sin(rad(90/2 + angle));
						
						if(!outside(x,y,quadNo,arcNo, 0)){
							acceptCandidate(x,y);
						}else{
							//return;
						}
					}
					sample();
					
					function sample(){
						var a = Math.random() * queueSize | 0;
						var s = queue[a];
						var b = 0;
						genCandidate(s);
						
						function genCandidate(s){
							if (++b > maxSample){
								return rejectActive();
							}
							
							var c = 2 * Math.PI * Math.random();
							var r = Math.sqrt(Math.random() * R + radius2);
							var x = s[0] + r * Math.cos(c);
							var y = s[1] + r * Math.sin(c);
							
							if(outside(x,y,quadNo,arcNo, radius)) return genCandidate(s);
							
							if (far(x,y)){
								return acceptCandidate(x,y);
							}else{
								return genCandidate(s);
							}
							
							function rejectActive(){
								queue[a] = queue[--queueSize];
								queue.length = queueSize;
							}
						}
					}

					function far(x,y){
						for(var i = 0; i < poisson_dist.length; i++){
							var dx = poisson_dist[i][0] - x;
							var dy = poisson_dist[i][1] - y;
							if(dx*dx + dy*dy < radius2){
								return false;
							}
						}
						return true;
					}

					function acceptCandidate(x,y){
						queue.push([x,y]);
						++queueSize;
						poisson_dist.push([x,y]);
						return [x,y];
					}
					
					function outside(x,y,quad,arc,radius){
						var w = canvas.attr("width")/2;
						var h = canvas.attr("height")/2;
						
						var innerRadius = arcs[arcNo].innerRadius + radius/2;
						var outerRadius = arcs[arcNo].outerRadius - radius/2;
						
						var innerx = w + (x - w)/Math.sqrt(Math.pow(x - w,2) + Math.pow(y - h,2))*innerRadius;
						var outerx = w + (x - w)/Math.sqrt(Math.pow(x - w,2) + Math.pow(y - h,2))*outerRadius;
						var innery = h + (y - h)/Math.sqrt(Math.pow(x - w,2) + Math.pow(y - h,2))*innerRadius;
						var outery = h + (y - h)/Math.sqrt(Math.pow(x - w,2) + Math.pow(y - h,2))*outerRadius;
						
						if(Math.abs(x - w) < Math.abs(innerx - w) || Math.abs(x - w) > Math.abs(outerx - w)){
							return true;
						}
						
						if(Math.abs(y - h) < Math.abs(innery - h) || Math.abs(y - h) > Math.abs(outery - h)){
							return true;
						}
						
						if((quad == 0 || quad == 3) && x < w + radius/2 ){
							return true;
						}else if((quad == 1 || quad == 2) && x > w - radius/2){
							return true;
						}
						
						if((quad == 2 || quad == 3) && y > h - radius/2){
							return true;
						}else if((quad == 0 || quad == 1) && y < h + radius/2){
							return true;
						}
						
						return false;
					}
				}
				
				for(var k = 0; k < rails.length; k++) {
					var x = poisson_dist[k][0];
					var y = poisson_dist[k][1];
					
					blips[j+i*arcRails.length].push({'item':arcRails[j][k],'x':x,'y':y, 'color':quadrant.color, 'scaleFactor': scaleFactor, 'arc': j, 'quad' : i});
				}
			}
			
			angle = angle + 90;
			quadrant.endAngle = angle;
		}
		this._drawBlips(svg,blips,callback);
		
		var context = {
			oldZoom: 0	
		};
		
		return {
			selectBlip: function(blip) {
				d3.selectAll('a circle, a path').attr('opacity',0.3);
				d3.select('#blip-'+blip.id).selectAll('circle, path').attr('opacity',1.0);
			},
			unselectBlip: function(blip) {
				d3.selectAll('a circle, a path').attr('opacity',1.0);
			},
			zoom: function(index) {
				svg.call(transition, centres[context.oldZoom], centres[index]);
				context.oldZoom = index;
			}
		};
	},
	
	_drawArc: function(svg, innerRadius, outerRadius, x, y, color) {
		var arc=d3.svg.arc()
			.innerRadius(innerRadius)
			.outerRadius(outerRadius)
			.startAngle(rad(0))
			.endAngle(rad(360));

		svg.append('path')
			.attr('d',arc)
			.attr('fill',color)
			.attr('transform','translate('+x+', '+y+')');
	},
	
	_drawArcQuad: function(svg, innerRadius, outerRadius, x, y, color, quadrantNo) {
		var startAngle;
		var endAngle;
		
		switch (quadrantNo){
			case 0:
				startAngle = 0;
				endAngle = 90;
				x = 0;
				y = 0;
				break;
			case 1:
				startAngle = 90;
				endAngle = 180;
				y = 0;
				break;
			case 2:
				startAngle = 180;
				endAngle = 270;
				y = y;
				break;
			case 3:
				startAngle = 270;
				endAngle = 0;
				x = 0;
				y = y;
				break;
		}
		var arc=d3.svg.arc()
			.innerRadius(innerRadius)
			.outerRadius(outerRadius)
			.startAngle(rad(0))
			.endAngle(rad(360));

		svg.append('path')
			.attr('d',arc)
			.attr('fill',color)
			.attr('transform','translate('+x+', '+y+')');
	},
	
	_drawArcAxisText: function(svg, innerRadius, outerRadius, totalWidth, totalHeight, text, sf) {
		var x = (totalWidth/2)+innerRadius+((outerRadius-innerRadius)/2);
		var y = totalHeight/2;
		svg.append('text')
			.attr({'x':x,'y':y+sf*1,'text-anchor':'middle','fill':'#000'})
			.style({'font-size':(sf*10) + 'px','font-weight':900})
			.text(text.toUpperCase());
	},
	
	_drawArcAxisTextQuad: function(svg, innerRadius, outerRadius, totalWidth, totalHeight, text, sf, quadrantNo, hMin) {
		var x;
		var y;
		
		switch (quadrantNo){
		case 0:
			y = hMin/2;
			x = innerRadius+((outerRadius-innerRadius)/2);
			break;
		case 1:
			x = totalWidth - outerRadius+((outerRadius-innerRadius)/2);;
			y = hMin/2;
			break;
		case 2:
			x = totalWidth - outerRadius+((outerRadius-innerRadius)/2);;;
			y = totalHeight - hMin/4;
			break;
		case 3:
			x = innerRadius+((outerRadius-innerRadius)/2);;
			y = totalHeight - hMin/4;
			break;
	}
		
		svg.append('text')
			.attr({'x':x,'y':y+sf*3,'text-anchor':'middle','fill':'#000'})
			.style({'font-size':(sf*12) + 'px','font-weight':900})
			.text(text);
		
		var x2 = (totalWidth/2)-(innerRadius+((outerRadius-innerRadius)/2));
		svg.append('text')
		.attr({'x':x,'y':y+sf*3,'text-anchor':'middle','fill':'#000'})
		.style({'font-size':(sf*12) + 'px','font-weight':900})
		.text(text);
	},
	
	_drawBlips: function(svg,blips,callback) {
		var group = svg.selectAll('a').data(blips).enter().append('g');
		group.attr("class", function(d, i) { return 'group' + i;});
		
		for(var i = 0; i < blips.length; i++) {
			(function(theBlip){
				
				var link = svg.selectAll('g').data(blips).selectAll('a').data(function (d) {return d;}).enter().append("svg:a")
				.attr('id', function(d){ return 'blip-'+d.item.id;})
				.attr('xlink:href', 'javascript:void(0)')
				.style({'text-decoration':'none','cursor':'pointer'})
				.on('click', function(d) {
					callback.onblipclick(d.item);
				})
				.on('mouseover', function(d) {
					d3.selectAll('a circle, a path').attr('opacity',0.3);
					d3.select('#blip-'+d.item.id).selectAll('circle, path').attr('opacity',1.0);
					callback.onbliphover(d.item);
				})
				.on('mouseleave', function(d) {
					d3.selectAll('a circle, a path').attr('opacity',1.0);
					callback.onblipleave(d.item);
				});
			
				var blip = link.append('path');
			
				blip.attr('d', function(d){
					if (d.item.movement == 'c'){
						return "M5,82.9422876 C32.8460969,99.0192375 67.1539031,99.0192375 95,82.9422876 C95.0000034,50.7883845 77.8460999,21.0769515 50,5 C22.1539001,21.0769515 4.99999658,50.7883845 5,82.9422876 L5,82.9422876 Z";
					}else{
						return 'M420.084,282.092c-1.073,0-2.16,0.103-3.243,0.313c-6.912,1.345-13.188,8.587-11.423,16.874c1.732,8.141,8.632,13.711,17.806,13.711c0.025,0,0.052,0,0.074-0.003c0.551-0.025,1.395-0.011,2.225-0.109c4.404-0.534,8.148-2.218,10.069-6.487c1.747-3.886,2.114-7.993,0.913-12.118C434.379,286.944,427.494,282.092,420.084,282.092';
					}
				});
			
				blip.attr('fill',function(d){ return d.color;})
					.attr('transform',function(d){
						if(d.item.movement == 'c') {
							return 'scale('+d.scaleFactor*0.32+') translate(' + (d.x - 15)/(d.scaleFactor*0.32) + ', ' + (d.y - 17)/(d.scaleFactor*0.32) + ')';
						} else {
							return 'scale('+((d.scaleFactor*30)/34)+') translate('+(-404+d.x*(34/(d.scaleFactor*30))-17)+', '+(-282+d.y*(34/(d.scaleFactor*30))-17)+')';
						}
					});
				
				blip.style("filter", "url(#drop-shadow)");
			
				link.append('text')
					.attr('x',function(d){ return d.x;})
					.attr('y',function(d){ return d.item.movement=='c'?d.y+(d.scaleFactor*6):d.y+(d.scaleFactor*4);})
					.attr('font-size',function(d){ return d.scaleFactor*14;})
					.attr({'font-style':'italic','font-weight':'bold','fill':'white'})
					.text(function(d){return d.item.id;})
					.style({'text-anchor':'middle'})
					.append("svg:title")
					.text(function(d){return d.item.name;});
				
			})(blips[i]);
		}
		
	},
	
	_drawKey: function(svg,w,h,sf){
		var x=w-(sf*125);
		var y=sf*60;
		var triangleKey="New or moved";
		var circleKey="Unchanged";
		
		var scale=sf*5;
		var colour = 'black';
		svg.append('path')
			.attr('d','M5,82.9422876 C32.8460969,99.0192375 67.1539031,99.0192375 95,82.9422876 C95.0000034,50.7883845 77.8460999,21.0769515 50,5 C22.1539001,21.0769515 4.99999658,50.7883845 5,82.9422876 L5,82.9422876 Z')
			.attr('fill',colour)
			.attr('transform','scale('+(scale/34)+') translate('+x*(34/scale)+', '+(y-sf*10)*(34/scale)+')');
		svg.append('text')
			.attr({'x':x+sf*20,'y':y+sf*1,'fill':colour,'font-size':(sf*0.8)+'em'})
			.text(triangleKey);
		svg.append('circle')
			.attr('r',sf*7)
			.attr('fill',colour)
			.attr('cx',x+sf*7)
			.attr('cy',y+sf*22);
		svg.append('text')
			.attr({'x':x+sf*20,'y':y+sf*26,'fill':colour,'font-size':(sf*0.8)+'em'})
			.text(circleKey);
	},
	
	_drawKeyQuad: function(svg,w,h,sf, quadrantNo){
		var x;
		var y;
		
		switch (quadrantNo){
			case 0:
				x = w/2-(sf*100);
				y = h-(sf*100);
				break;
			case 1:
				x = w/2+(sf*100);
				y = h-(sf*100);
				break;
			case 2:
				x = w/2+(sf*100);
				y = (sf*100);
				break;
			case 3:
				x = w/2-(sf*100);
				y = (sf*100);
				break;
		}
		
		var triangleKey="New or moved";
		var circleKey="Unchanged";
		
		var scale=sf*10;
		var colour = 'black';
		svg.append('path')
			.attr('d','M412.201,311.406c0.021,0,0.042,0,0.063,0c0.067,0,0.135,0,0.201,0c4.052,0,6.106-0.051,8.168-0.102c2.053-0.051,4.115-0.102,8.176-0.102h0.103c6.976-0.183,10.227-5.306,6.306-11.53c-3.988-6.121-4.97-5.407-8.598-11.224c-1.631-3.008-3.872-4.577-6.179-4.577c-2.276,0-4.613,1.528-6.48,4.699c-3.578,6.077-3.26,6.014-7.306,11.723C402.598,306.067,405.426,311.406,412.201,311.406')
			.attr('fill',colour)
			.attr('transform','scale('+(scale/34)+') translate('+(-404+x*(34/scale)-17)+', '+(-282+(y-sf*10)*(34/scale)-17)+')');
		svg.append('text')
			.attr({'x':x+sf*10,'y':y-sf*5,'fill':colour,'font-size':(sf*0.8)+'em'})
			.text(triangleKey);
		svg.append('path')
			.attr('d',"M420.084,282.092c-1.073,0-2.16,0.103-3.243,0.313c-6.912,1.345-13.188,8.587-11.423,16.874c1.732,8.141,8.632,13.711,17.806,13.711c0.025,0,0.052,0,0.074-0.003c0.551-0.025,1.395-0.011,2.225-0.109c4.404-0.534,8.148-2.218,10.069-6.487c1.747-3.886,2.114-7.993,0.913-12.118C434.379,286.944,427.494,282.092,420.084,282.092")
			.attr('fill',colour)
			.attr('transform','scale('+(scale/34)+') translate('+(-404+x*(34/scale)-17)+', '+(-282+(y+sf*10)*(34/scale)-17)+')');
		svg.append('text')
			.attr({'x':x+sf*10,'y':y+sf*15,'fill':colour,'font-size':(sf*0.8)+'em'})
			.text(circleKey);
	},
	
};
