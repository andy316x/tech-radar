var rad=function(deg){
	return deg*Math.PI/180;
};

var deg=function(rad){
	return rad*180/Math.PI;
};

var global_radar;
var global_totalArc;

var maxSample = 30;
var maxRadius = 100;

var Radar = {
		
	draw: function(element, radar, editable, callback) {
		
		var w = 1000;
		var h = w;
		
		global_radar = radar;

		var scaleFactor = 1;
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
			totalArc = totalArc + radar.arcs[i].r;
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
		
		var parentWidth = element.offsetWidth;
		var canvas=d3.select(element)
			.insert('svg',':first-child')
			.attr('xmlns:xmlns:xlink','http://www.w3.org/1999/xlink')
			.attr('width','100%')
			.attr('height','100%')
			.attr('style','position:absolute;top:0;left:0;')
			.attr('viewBox', '0 0 1000 1000')
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
			
			var r = (radar.arcs[i].r / totalArc)*(w/2);
			
			this._drawArc(svg, cumulativeArc, cumulativeArc + r, w/2, h/2, radar.arcs[i].color);
			
			var arc = {
				name: radar.arcs[i].name,
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
		
		//this._drawKey(svg,w,h,scaleFactor);
		
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
				.attr({'x':labelx+translation[i][4]*scaleFactor*20,'y':labely,'font-size':scaleFactor*18,'font-weight':'bold','fill':'#333'})
				.style({'text-anchor':translation[i][2]})
				.text(quadrant.name);
			
			svg.append('circle')
				.attr('r',scaleFactor*6)
				.attr('fill',quadrant.color)
				.attr('cx',labelx+scaleFactor*10-translation[i][3]*(scaleFactor*20+textElement.node().getComputedTextLength()))
				.attr('cy',labely-scaleFactor*6);
			
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
					var w = 1000/2;
					var h = w;
					
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
		this._drawBlips(svg,blips,radar.quadrants,arcs,w,h,editable,callback);
		
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
			.attr({'x':x,'y':y+sf*4,'text-anchor':'middle','fill':'#000'})
			.style({'font-size':(sf*13) + 'px','font-weight':900})
			.text(text.charAt(0).toUpperCase() + text.slice(1));
		
		var x2 = (totalWidth/2)-innerRadius-((outerRadius-innerRadius)/2);
		svg.append('text')
			.attr({'x':x2,'y':y+sf*4,'text-anchor':'middle','fill':'#000'})
			.style({'font-size':(sf*13) + 'px','font-weight':900})
			.text(text.charAt(0).toUpperCase() + text.slice(1));
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
	
	_drawBlips: function(svg,blipGroups,quadrants,arcs,w,h,editable,callback) {
		var blipsList = [];
		
		for(var i = 0; i < blipGroups.length; i++) {
			(function(blips){
				for(var j = 0; j < blips.length; j++) {
					(function(b){
						blipsList.push(b);
					})(blips[j]);
				}
			})(blipGroups[i]);
		}
		
		if(editable === true) {
			var dragGroup = d3.behavior.drag()
			.origin(function(d,i) { 
				var t = d3.select(this);
			    return {
			        x: t.attr("x") + d3.transform(t.attr("transform")).translate[0],
			        y: t.attr("y") + d3.transform(t.attr("transform")).translate[1]
			    };
			})
			.on('dragstart', function(d, i) {
			    d3.selectAll('.blip').attr('opacity',0.3);
				d3.select('#blip-'+d.item.id).attr('opacity',1.0);
			  }).on('drag', function(d, i) {
			    d.x = d3.event.x;
			    d.y = d3.event.y;
			    d3.select(this).attr("transform", "translate("+d3.event.x+","+d3.event.y+")");
			  }).on('dragend', function(d, i) {
				  var g = d3.select(this);
				  var text = g.select('text');
				  var x = ((parseInt(text.attr('x')) + d3.transform(g.attr("transform")).translate[0]) - (w/2))/d.scaleFactor;
				  var y = -1*((parseInt(text.attr('y')) + d3.transform(g.attr("transform")).translate[1]) - (h/2))/d.scaleFactor;
				  
				  var r = Math.sqrt(x*x + y*y);
				  var theta = deg(Math.atan(y / x));
				  var offset = 0;
				  var index = 0;
				  if(x < 0 && y < 0) {
					  index = 1;
					  offset = 180;
				  } else if(x < 0 && y > 0) {
					  index = 2;
					  offset = 180;
				  } else if(x > 0 && y < 0) {
					  index = 0;
					  offset = 360;
				  } else if(x > 0 && y > 0) {
					  index = 3;
					  offset = 0;
				  }
				  
				  for(var i = 0; i < arcs.length; i++) {
					  if(arcs[i].innerRadius < r*d.scaleFactor && r*d.scaleFactor < arcs[i].outerRadius) {
						  callback.onblipmove({name:d.item.name,techGrouping:quadrants[index].name,arc:arcs[i].name});
					  }
				  }
				  
				  d3.selectAll('.blip').attr('opacity',1.0);
			  });
		}
		
		var link = svg.selectAll('g').data(blipsList).enter().append("g")
		.attr('id', function(d){ return 'blip-'+d.item.id;})
		.attr('class', 'blip')
		.style({'text-decoration':'none','cursor':'pointer'})
		.on('dblclick', function(d) {
			callback.onblipclick(d.item);
		})
		.on('mouseover', function(d) {
			callback.onbliphover(d.item);
		})
		.on('mouseleave', function(d) {
			callback.onblipleave(d.item);
		})
		
		if(editable === true) {
			link.call(dragGroup);
		}
	
		var blip = link.append('path');
	
		blip.attr('d', function(d){
			return 'M420.084,282.092c-1.073,0-2.16,0.103-3.243,0.313c-6.912,1.345-13.188,8.587-11.423,16.874c1.732,8.141,8.632,13.711,17.806,13.711c0.025,0,0.052,0,0.074-0.003c0.551-0.025,1.395-0.011,2.225-0.109c4.404-0.534,8.148-2.218,10.069-6.487c1.747-3.886,2.114-7.993,0.913-12.118C434.379,286.944,427.494,282.092,420.084,282.092';
		});
	
		blip.attr('fill',function(d){ return d.color;})
			.attr('transform',function(d){
				return 'scale('+((d.scaleFactor*30)/34)+') translate('+(-404+d.x*(34/(d.scaleFactor*30))-17)+', '+(-282+d.y*(34/(d.scaleFactor*30))-17)+')';
			});
		
		blip.style("filter", "url(#drop-shadow)");
	
		link.append('text')
			.attr('x',function(d){ return d.x;})
			.attr('y',function(d){ return d.y+(d.scaleFactor*4);})
			.attr('font-size',function(d){ return d.scaleFactor*12;})
			.attr({'font-style':'italic','font-weight':'bold','fill':'white'})
			.text(function(d){return d.item.id;})
			.style({'text-anchor':'middle'})
			.append("svg:title")
			.text(function(d){return d.item.name;});
		
	}
	
};

var CreateRadar = {
		
		draw: function(element, config, callback) {
			
			var colours = ['rgb(223,223,223)', 'rgb(166,167,169)', 'rgb(190,191,193)', 'rgb(209,209,209)', 'rgb(223,223,223)'];
			
			var w = 1000;
			var h = w;
			var axisWidth = 25;
			var minArcWidth = 50;
			var resizeHeight = 20;
			var resizeWidth = 4;
			
			var canvas=d3.select(element)
				.insert('svg',':first-child')
				.attr('xmlns:xmlns:xlink','http://www.w3.org/1999/xlink')
				.attr('width','100%')
				.attr('height','100%')
				.attr('style','position:absolute;top:0;left:0;')
				.attr('viewBox', '0 0 1000 1000')
			canvas.selectAll('*').remove();
		
			var group1 = canvas.append('g');
			var group2 = canvas.append('g');
			var group3 = canvas.append('g');
			var group4 = canvas.append('g');
			var group4 = canvas.append('g');
			
			var circles = [];
			
			for(var i = config.maturityOptions.length-1; i > -1; i--) {
				var arcWidth = (w/2)/config.maturityOptions.length;
				
				circles.push({
					name: config.maturityOptions[i].name,
					colour: colours[i%colours.length],
					min: arcWidth*i,
					max: arcWidth*(i+1)
				});
			}
			
			for(var i = circles.length - 1; i > -1; i--) {
				if(i > 0) {
					circles[i].next = circles[i-1];
				}
				if(i < circles.length - 1) {
					circles[i].previous = circles[i+1];
				}
			}
			
			var theCircs = group1.selectAll('circle').data(circles);
			
			theCircs.enter().append('circle')
				.attr('cx',function(d){return w/2;})
				.attr('cy',function(d){return h/2;})
				.attr('r',function(d){return d.max;})
				.attr('fill',function(d){return d.colour;})
				.attr('stroke',function(d){return 'black';})
				.attr('stroke-width',function(d){return 2;})
				.attr('stroke-dasharray',function(d){return '15, 10, 5, 10';});
			
			group2.append('rect')
				.attr('width',w)
				.attr('height',axisWidth)
				.attr('x',0)
				.attr('y',h/2-(axisWidth/2))
				.attr('fill','rgb(236,236,236)');
			
			group2.append('rect')
				.attr('width',axisWidth)
				.attr('height',h)
				.attr('x',w/2-(axisWidth/2))
				.attr('y',0)
				.attr('fill','rgb(236,236,236)');
			
			function dragstarted(d) {
				d3.event.sourceEvent.stopPropagation();
				d3.select(this).classed("dragging", true);
			}
			
			function dragged(d) {
				var newMax = d.max + d3.event.dx;
				if(typeof d.next != 'undefined') {
					if(d.next.max - newMax > minArcWidth && newMax > minArcWidth && (typeof d.previous == 'undefined' || newMax - d.previous.max > minArcWidth)) {
						d.max = newMax;
						d.next.min = newMax;
						d3.select(this).attr('x', (w/2)+d.max-(resizeWidth/2));
						theLabels.attr('x',function(d){
							if(typeof d.previous != 'undefined') {
								return (w/2)+d.min+(d.max-d.previous.max)/2;
							} else {
								return (w/2)+d.max/2;
							}
						});
					}
				}
			}
			
			function dragended(d) {
				d3.select(this).classed('dragging', false);
				theCircs.transition(1500).attr('r', function(d) {return d.max;}).ease('sin');
				
				var newData = [];
				for(var i = circles.length - 1; i > -1; i--) {
					newData.push({
						name: circles[i].name,
						percentage: circles[i].max/(w/2)*100
					});
				}
				console.log(newData);
			}
			
			var drag = d3.behavior.drag()
		    	.origin(function(d) { return d; })
		    	.on("dragstart", dragstarted)
		    	.on("drag", dragged)
		    	.on("dragend", dragended);
			
			group3.selectAll('rect').data(circles).enter().append('rect')
				.attr('x', function(d){return (w/2)+d.max-(resizeWidth/2);})
				.attr('y', function(d){return (h/2)-(resizeHeight/2);})
				.attr('width', function(d){return resizeWidth;})
				.attr('height', function(d){return resizeHeight;})
				.attr('rx', function(d){return 2;})
				.attr('ry', function(d){return 2;})
				.attr('cursor', function(d){return 'ew-resize';})
				.attr('fill', function(d){return '#AAA';})
				.attr('visibility', function(d){
					if(typeof d.next != 'undefined') {
						return 'visible';
					} else {
						return 'hidden';
					}
				})
				.call(drag);
			
			var theLabels = group4.selectAll('text').data(circles);
			
			theLabels.enter().append('text')
				.attr('x',function(d){
					if(typeof d.previous != 'undefined') {
						return (w/2)+d.min+(d.max-d.previous.max)/2;
					} else {
						return (w/2)+d.max/2;
					}
				})
				.attr('y',function(d){return (h/2)+3;})
				.attr('text-anchor',function(d){return 'middle';})
				.attr('font-size',function(d){return '13px';})
				.attr('font-weight',function(d){return '900';})
				.text(function(d){ return d.name; });
			
		}

};
