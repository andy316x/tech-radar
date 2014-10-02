var rad=function(deg){
	return deg*Math.PI/180;
};

var Radar = {
		
	draw: function(element, radar, callback) {
		
		var w = element.offsetWidth;
		var h = w;
		
		var scaleFactor = w/1000;
		
		var totalArc = 0;
		for(var i = 0; i < radar.arcs.length; i++) {
			totalArc = totalArc + radar.arcs[i].r;
		}
		
		while (element.firstChild) {
			element.removeChild(element.firstChild);
		}
		
		var svg=d3.select(element)
			.insert('svg',':first-child')
			.attr('xmlns:xmlns:xlink','http://www.w3.org/1999/xlink')
			.attr('width',w)
			.attr('height',h);
		svg.selectAll('*').remove();
		
		var cumulativeArc = 0;
		var arcMap = {};
		for(var i = 0; i < radar.arcs.length; i++) {
			var r = (radar.arcs[i].r / totalArc)*(w/2);
			
			this._drawArc(svg, cumulativeArc, cumulativeArc + r, w/2, h/2, radar.arcs[i].color);
			this._drawArcAxisText(svg, cumulativeArc, cumulativeArc + r, w, h, radar.arcs[i].name, scaleFactor);
			
			arcMap[radar.arcs[i].id] = {
				innerRadius: cumulativeArc,
				outerRadius: cumulativeArc + r
			};
			
			cumulativeArc = cumulativeArc + r;
		}
		
		var axisWidth = scaleFactor*15;
		svg.append('rect')
			.attr('x',0)
			.attr('y',(h/2)-(axisWidth/2))
			.attr('width',w)
			.attr('height',10)
			.attr('fill','white')
			.attr('opacity',0.5);
		
		svg.append('rect')
			.attr('x',(w/2)-(axisWidth/2))
			.attr('y',0)
			.attr('width',10)
			.attr('height',h)
			.attr('fill','white')
			.attr('opacity',0.5);
		
		this._drawKey(svg,w,h,scaleFactor);
		
		var translation = [[1, 1], [-1, 1], [-1, -1], [1, -1]];
		
		
		var quadrantMap = {};
		var angle = 0;
		for(var i = 0; i < radar.quadrants.length; i++) {
			var quadrant = radar.quadrants[i];
			quadrantMap[quadrant.id] = quadrant;
			quadrant.startAngle = angle;
			
			var labelx = (w/2) + translation[i][0]*0.75*(w/2);
			var labely = (h/2) + translation[i][1]*0.95*(h/2);
			svg.append('text')
				.attr({'x':labelx,'y':labely,'font-size':scaleFactor*14,'font-weight':'bold'})
				.style({'text-anchor':'middle'})
				.text(quadrant.name);
			
			for(var j = 0; j < quadrant.items.length; j++) {
				var arc = arcMap[quadrant.items[j].arc];
				var r = (quadrant.items[j].pc.r/100)*(arc.outerRadius-arc.innerRadius)+arc.innerRadius;
				
				var x = (w/2) + r*Math.cos(rad(quadrant.items[j].pc.t + angle));
				var y = (h/2) + r*Math.sin(rad(quadrant.items[j].pc.t + angle));
				
				var link=svg.append('svg:a')
					.attr({'id':'blip-'+quadrant.items[j].id,'xlink:href':quadrant.items[j].url})
					.style({'text-decoration':'none','cursor':'pointer'});
				
				this._drawBlip(link, x, y, scaleFactor*30, quadrant.items[j].movement, quadrant.color);
				
				var textY = quadrant.items[j].movement=='c'?y+(scaleFactor*6):y+(scaleFactor*4);
				link.append('text')
					.attr({'x':x,'y':textY,'font-size':scaleFactor*14,'font-style':'italic','font-weight':'bold','fill':'white'})
					.text(quadrant.items[j].id)
					.style({'text-anchor':'middle'})
					.append("svg:title")
					.text(quadrant.items[j].name);
				
				var onmouseenter = (function(blip){
					return function() {
						d3.selectAll('a circle, a path').attr('opacity',0.3);
						d3.select('#blip-'+blip.id).selectAll('circle, path').attr('opacity',1.0);
						callback.onbliphover(blip);
					};
				})(quadrant.items[j]);
				var onmouseleave = (function(blip){
					return function() {
						d3.selectAll('a circle, a path').attr('opacity',1.0);
						callback.onblipleave(blip);
					};
				})(quadrant.items[j]);
				link.on('mouseenter',onmouseenter);
				link.on('mouseleave',onmouseleave);
			}
			
			angle = angle + 90;
			quadrant.endAngle = angle;
		}
		
		return {
			selectBlip: function(blip) {
				d3.selectAll('a circle, a path').attr('opacity',0.3);
				d3.select('#blip-'+blip.id).selectAll('circle, path').attr('opacity',1.0);
			},
			unselectBlip: function(blip) {
				d3.selectAll('a circle, a path').attr('opacity',1.0);
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
	
	_drawArcAxisText: function(svg, innerRadius, outerRadius, totalWidth, totalHeight, text, sf) {
		var x = (totalWidth/2)+innerRadius+((outerRadius-innerRadius)/2);
		var y = totalHeight/2;
		svg.append('text')
			.attr({'x':x,'y':y+sf*2,'text-anchor':'middle','fill':'#000'})
			.style({'font-size':(sf*10) + 'px','font-weight':900})
			.text(text.toUpperCase());
	},
	
	_drawBlip: function(svg, x, y, w, movement, color) {
		if(movement == 'c') {
			svg.append('path')
				.attr('d','M412.201,311.406c0.021,0,0.042,0,0.063,0c0.067,0,0.135,0,0.201,0c4.052,0,6.106-0.051,8.168-0.102c2.053-0.051,4.115-0.102,8.176-0.102h0.103c6.976-0.183,10.227-5.306,6.306-11.53c-3.988-6.121-4.97-5.407-8.598-11.224c-1.631-3.008-3.872-4.577-6.179-4.577c-2.276,0-4.613,1.528-6.48,4.699c-3.578,6.077-3.26,6.014-7.306,11.723C402.598,306.067,405.426,311.406,412.201,311.406')
				.attr('stroke','white')
				.attr('stroke-width',2)
				.attr('fill',color)
				.attr('transform','scale('+(w/34)+') translate('+(-404+x*(34/w)-17)+', '+(-282+y*(34/w)-17)+')');
		} else {
			return svg.append('path')
				.attr('d',"M420.084,282.092c-1.073,0-2.16,0.103-3.243,0.313c-6.912,1.345-13.188,8.587-11.423,16.874c1.732,8.141,8.632,13.711,17.806,13.711c0.025,0,0.052,0,0.074-0.003c0.551-0.025,1.395-0.011,2.225-0.109c4.404-0.534,8.148-2.218,10.069-6.487c1.747-3.886,2.114-7.993,0.913-12.118C434.379,286.944,427.494,282.092,420.084,282.092")
				.attr("stroke","white")
				.attr("stroke-width",2)
				.attr('fill',color)
				.attr('transform','scale('+(w/34)+') translate('+(-404+x*(34/w)-17)+', '+(-282+y*(34/w)-17)+')');
		}
	},
	
	_drawKey: function(svg,w,h,sf){
		var x=w-(sf*100);
		var y=h-(sf*100);
		var triangleKey="New or moved";
		var circleKey="No change";
		
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
	}
	
};
