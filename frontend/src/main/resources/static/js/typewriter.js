//wallace 2015 7 14
Element.prototype.typewriter=function(a){
			var d = this,
				c = d.innerHTML,
                c = c.trim();
				b = 0;
			d.innerHTML="";
			var e = setInterval(function() {
				var f = c.substr(b, 1);
				if (f == "<") {
					b = c.indexOf(">", b) + 1
				} else {
					b++
				}
				d.innerHTML=c.substring(0, b) + (b & 1 ? "_" : "");
				if (b >= c.length) {
					clearInterval(e);
                    d.innerHTML = d.innerHTML.replace("_","");
				}
			}, 300);

		return this
	
	}