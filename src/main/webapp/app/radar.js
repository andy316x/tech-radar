var rad=function(deg){
	return deg*Math.PI/180;
};

var deg=function(rad){
	return rad*180/Math.PI;
};

var maxSample = 30;
var maxRadius = 100;

function findNamed(name, list, notFoundValue){
	for(var k = 0; k < list.length; k++){
		if(list[k].name == name){
			return k;
		}
	}
	return notFoundValue;
}

function longestChild(prev, list){
	return Math.max(prev, list.length);
}

var Radar = function(element, radar, editable, callback){
	var tip = d3.tip().attr('class', 'd3-tip').html(function(d) { 
		return d.item.name; 
		});
	var containerGroup;
    //TODO - fit into parent (i.e. min of parent width/height)?
	var w = 1000;
	var h = 1000;
	var canvas, maskGroup;
    this.blips = [];
    
    function _getAllRails(){
        var allRails = [];
		for(var i = 0; i < radar.quadrants.length; i++) {
			var quadrant = radar.quadrants[i];
			allRails.push([]);
			for(var j = 0; j < radar.arcs.length; j++) {
				allRails[i].push([]);
			}
		
			for(var j = 0; j < quadrant.items.length; j++) {
				var arcIndex = findNamed(quadrant.items[j].arc, radar.arcs, 0);
				allRails[i][arcIndex].push(quadrant.items[j]);
			}
		}
        return allRails;
    };
    
	function transition(container, target) {
		container
		.transition()
		.duration(350)
		.attr("transform", "translate("+target[0]+","+target[1]+")");
	};
	
    var oldZoom = 0;
	var arcs = [];
	var quadrants = radar.quadrants;
	var allRails = _getAllRails();
	var totalArc = radar.arcs.reduce(function(prev, curr){
		return prev + curr.r;
	},0);
	
	var centres = [
	               [0,],//Centre of radar
   	               [0, -w/2 + 7], //BR
   	               [w/2, -w/2 + 7], //BL
   	               [w/2, 0],//TL
   	               [0, 0],//TR
               ];
    
	while (element.firstChild) {
		element.removeChild(element.firstChild);
	}
    
	initSvg();

	var self = this;
	this.draw = function() {
		if(self.blips.length){
			return;
		}
		var cumulativeArc = 0;
		var arcMap = {};
		arcs = [];
		radar.arcs.forEach(function(_arc){
			var r = (_arc.r / totalArc)*(w/2);
			_drawArc(containerGroup, cumulativeArc, cumulativeArc + r, w/2, h/2, _arc.color);
			
			var arc = {
				name: _arc.name,
				innerRadius: cumulativeArc,
				outerRadius: cumulativeArc + r,
				index: i
			};
			arcMap[_arc.id] = arc;
			arcs.push(arc);
			
			cumulativeArc = cumulativeArc + r;
		});
		
		_drawAxes();
		
		radar.arcs.forEach(function(_arc){
			var arc = arcMap[_arc.id];
			_drawArcAxisText(containerGroup, arc.innerRadius, arc.outerRadius, w, h, _arc.name, 1);
		});
		
		var translation = [[1, 1, 'end', 1, 0], [-1, 1, 'start', 0, 1], [-1, -1, 'start', 0, 1], [1, -1, 'end', 1, 0]];
		
		var quadrantMap = {};
		var angle = 0;
        self.blips = [];
        for(var i = 0; i < radar.quadrants.length; i++) {
                var quadrant = radar.quadrants[i];
                quadrantMap[quadrant.id] = quadrant;
                quadrant.startAngle = angle;

                var labelx = (w/2) + translation[i][0]*(w/2);
                var labely = (h/2) + translation[i][1]*0.95*(h/2);
                var textElement = containerGroup.append('text')
                    .attr({'x':labelx+translation[i][4]*20,'y':labely,'font-size':18,'font-weight':'bold','fill':'#333'})
                    .style({'text-anchor':translation[i][2]})
                    .text(quadrant.name);

                //label colour marker
                containerGroup.append('circle')
                .attr('r',6)
                .attr('fill',quadrant.color)
                .attr('cx',labelx+10-translation[i][3]*(20+textElement.node().getComputedTextLength()))
                .attr('cy',labely-6);

                var arcRails = allRails[i];

                for(var j = 0; j < arcRails.length; j++) {
                    var rails = arcRails[j];
                    self.blips.push([]);

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
                        var w = 500;
                        var h = 500;

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
                            }else {
                            	
                            }
                        }
                        sample();
                    }

                    for(var k = 0; k < rails.length; k++) {
                        var x = poisson_dist[k][0];
                        var y = poisson_dist[k][1];

                        self.blips[j+i*arcRails.length].push({'item':arcRails[j][k],'x':x,'y':y, 'color':quadrant.color, 'arc': j, 'quad' : i});
                    }
                }

                angle = angle + 90;
                quadrant.endAngle = angle;
            }
		_drawBlips(containerGroup,self.blips,radar.quadrants,arcs,w,h,editable,callback);
		_drawMasks();
	};
	
	this.selectBlip = function(blip) {
		d3.selectAll('circle.blip').attr('opacity',0.3);
		d3.select('#blip-'+blip.id).selectAll('circle, path').attr('opacity',1.0);
	};
	this.unselectBlip = function(blip) {
		d3.selectAll('circle.blip').attr('opacity',1.0);
	},
	this.zoom = function(index) {
		if(index){
			maskGroup.style('display', 'block')
		}else{
			maskGroup.style('display', 'none')
		}
		containerGroup.call(transition, centres[index]);
 		oldZoom = index;
	};
    
	function initSvg(){
		canvas=d3.select(element)
		.insert('svg',':first-child')
        .style('position', 'absolute')
        .style('top',0)
        .style('left',0)
		.attr('xmlns:xmlns:xlink','http://www.w3.org/1999/xlink')
		.attr('viewBox', '0 0 1000 1000')
		.attr('preserveAspectRatio', 'xMinYMin meet');
	
		containerGroup = canvas.append('g').attr('class', 'containerGroup').attr("transform", "translate(0,0)");
		maskGroup = canvas.append('g').attr('class','mask').style('display','none');
	    canvas.call(tip);
		// filters go in defs element
		var defs = containerGroup.append("defs");
	
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
	}
   
    function _drawAxes(){
        var axisWidth = 25;
		containerGroup.append('rect')
			.attr('x',0)
			.attr('y',(h/2)-(axisWidth/2))
			.attr('width',w)
			.attr('height',axisWidth)
			.attr('fill','rgb(236,236,236)');
		
		containerGroup.append('rect')
			.attr('x',(w/2)-(axisWidth/2))
			.attr('y',0)
			.attr('width',axisWidth)
			.attr('height',h)
			.attr('fill','rgb(236,236,236)');
    };
    
	function _drawArc(svg, innerRadius, outerRadius, x, y, color) {
		var arc=d3.svg.arc()
			.innerRadius(innerRadius)
			.outerRadius(outerRadius)
			.startAngle(rad(0))
			.endAngle(rad(360));

		svg.append('path')
			.attr('d',arc)
			.attr('fill',color)
			.attr('transform','translate('+x+', '+y+')');
	};
	
	function _drawArcAxisText(svg, innerRadius, outerRadius, totalWidth, totalHeight, text, sf) {
		var x = (totalWidth/2)+innerRadius+((outerRadius-innerRadius)/2);
		var y = totalHeight/2;
		svg.append('text')
			.attr({'x':x,'y':y+sf*3,'text-anchor':'middle','fill':'#000'})
			.style({'font-size':(sf*13) + 'px','font-weight':900})
			.text(text.charAt(0).toUpperCase() + text.slice(1));
		
		var x2 = (totalWidth/2)-innerRadius-((outerRadius-innerRadius)/2);
		svg.append('text')
			.attr({'x':x2,'y':y+sf*3,'text-anchor':'middle','fill':'#000'})
			.style({'font-size':(sf*13) + 'px','font-weight':900})
			.text(text.charAt(0).toUpperCase() + text.slice(1));
	};

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
              var x = ((parseInt(text.attr('x')) + d3.transform(g.attr("transform")).translate[0]) - (w/2));
              var y = -1*((parseInt(text.attr('y')) + d3.transform(g.attr("transform")).translate[1]) - (h/2));

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
                  if(arcs[i].innerRadius < r && r < arcs[i].outerRadius) {
                      callback.onblipmove({name:d.item.name,techGrouping:quadrants[index].name,arc:arcs[i].name});
                  }
              }

              d3.selectAll('.blip').attr('opacity',1.0);
          });
	
	function _drawMasks(){
		var offsets = [
		               [0,0,500,510],
		               [0,506,1000,500]
		               ];
		offsets.forEach(function(o){
			maskGroup.append('rect')
			.attr('x',o[0])
			.attr('y',o[1])
			.attr('width',o[2])
			.attr('height',o[3]);
		});
	}
    
	function _drawBlips(svg,blipGroups,quadrants,arcs,w,h,editable,callback) {
		var blipsList = blipGroups.reduce(function(prev, curr){
			return prev.concat(curr);
		},[]);
		
		var selection = svg.selectAll('g').data(blipsList);
		
		var link = selection.enter().append("g")
		.attr('id', function(d){ return 'blip-'+d.item.id;})
		.attr('class', 'blip')
		.style({'text-decoration':'none','cursor':'pointer'})
		.on('dblclick', function(d) {
			callback.onblipclick(d.item);
		})
		.on('mouseover', function(d) {
			callback.onbliphover(d.item);
			svg.selectAll('circle.blip').attr('opacity',0.3);
			svg.select('#blip-'+d.item.id).selectAll('circle, path').attr('opacity',1.0);
			tip.show(d);
		})
		.on('mouseout', function(d) {
			callback.onblipleave(d.item);
			svg.selectAll('circle.blip').attr('opacity',1.0);
			tip.hide(d);
		});

		//Update
		selection.classed('hovered', function(d){
			return d.hovered;
		});
		
		if(editable) {
			link.call(dragGroup);
		}
	
		var blip = link.append('circle');
		//TODO - what's the appropriate radius?
		blip.attr('r', 13)
		.attr('cx',function(d){return d.x;})
		.attr('cy', function(d){return d.y})
		.attr('fill',function(d){ return d.color;})
		.attr('class',function(d){ return 'blip';});
		
		blip.style("filter", "url(#drop-shadow)");
	
		link.append('text')
			.attr('x',function(d){ return d.x;})
			.attr('y',function(d){ return d.y+4;})
			.attr('font-size',12)
			.attr({'font-style':'italic','font-weight':'bold','fill':'white'})
			.text(function(d){return d.item.id;})
			.style({'text-anchor':'middle', 'pointer-events': 'none'})
		
	};
	
	function _drawKey(svg,w,h,sf){
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
	};
	
};
