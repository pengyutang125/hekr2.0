(function() {
	function v(a) {
		this.url = "http://" + a.host + ":" + (parseInt(a.port) + 1) + "/scriptSocket";
		this.readyState = 0;
		this.getSocketData();
		var b = this;
		window.onbeforeunload = function() {
			try {
				b.closeSocket()
			} catch (a) {}
		}
	}
	function x(a) {
		function b(a, b) {
			var c = null;
			if (a.matches(b.selectorText)) {
				for (var c = b.selectorText.split(","), h = b.selectorText, g = 0; g < c.length; g++) if (a.matches(c[g])) {
					h = c[g];
					break
				}
				c = {
					css: b.cssText,
					selectors: b.selectorText,
					selector: h
				}
			}
			return c
		}
		return {
			_class: function(a) {
				var c = document.styleSheets,
					e = [];
				a.matches = a.matches || a.webkitMatchesSelector || a.mozMatchesSelector || a.msMatchesSelector || a.oMatchesSelector;
				for (var h, g, l = 0, w = c.length; l < w; l++) if (h = c[l].cssRules || c[l].rules, (g = c[l].media && c[l].media.mediaText) && "all" != g || (g = ""), h && (!g || window.matchMedia(g).matches)) for (var p = c[l].href || location.pathname + location.search, s = {}, q, k = 0, n = h.length; k < n; k++) try {
					if (q = h[k], 1 == q.type) {
						if (s = b(a, q)) s.href = p, s.sheetIndex = l, s.cssRuleIndex = [k], g && (s.mediaText = 'media="' + g + '"'), e.push(s)
					} else if (4 == q.type) {
						var m = q.media && q.media.mediaText || "";
						if (m && window.matchMedia(m).matches) {
							var r = q.cssRules || q.rules;
							if (r) for (var t = 0, v = r.length; t < v; t++) if (s = b(a, r[t])) s.href = p, s.sheetIndex = l, s.cssRuleIndex = [k, t], s.mediaText = "@media " + m, e.push(s)
						}
					}
				} catch (y) {}
				return e
			}(a),
			_style: function(a) {
				return (a = a.getAttribute("style")) ? a : ""
			}(a)
		}
	}
	function m(a, b) {
		b ? c.socketBuffer.unshift(a) : c.socketBuffer.push(a);
		n.socketReady() && c.socketSendStop && (c.socketSendStop = 0, c._sendMessage())
	}
	debuggap = {
		version: "3.0.0"
	};
	var g = function(a, b) {
			b = b ? b : document;
			return b.querySelector(a)
		},
		k = function(a, b) {
			b = b ? b : document;
			return b.querySelectorAll(a)
		},
		c = debuggap;
	c.css3Prefix = "-webkit-";
	c.selfClosing = {
		img: 1,
		hr: 1,
		br: 1,
		area: 1,
		base: 1,
		basefont: 1,
		input: 1,
		link: 1,
		meta: 1,
		command: 1,
		embed: 1,
		keygen: 1,
		wbr: 1,
		param: 1,
		source: 1,
		track: 1,
		col: 1
	};
	c.browser = "webkit";
	/MSIE|\.NET|IEMobile/i.test(navigator.userAgent) && (c.browser = "ie");
	c.size = function() {
		return {
			width: document.documentElement.clientWidth,
			height: document.documentElement.clientHeight
		}
	};
	c.extend = function() {
		var a = arguments[0] || {},
			b = 1,
			d = arguments.length,
			c = !1,
			e;
		a.constructor == Boolean && (c = a, a = arguments[1] || {}, b = 2);
		"object" != typeof a && "function" != typeof a && (a = {});
		1 == d && (a = this, b = 0);
		for (; b < d; b++) if (null != (e = arguments[b])) for (var h in e) a !== e[h] && (c && e[h] && "object" == typeof e[h] && a[h] && !e[h].nodeType ? a[h] = psoft.extend(c, a[h], e[h]) : void 0 != e[h] && (a[h] = e[h]));
		return a
	};
	c.inherit = function(a) {
		for (var b in a) n[b] &&
		function(b) {
			var c = n[b];
			n[b] = function() {
				c.apply(this, arguments);
				a[b].apply(this, arguments)
			}
		}(b)
	};
	c.css = function(a, b, d, c) {
		if ("object" == typeof b) {
			a.length || (a = [a]);
			for (var e = 0; e < a.length; e++) {
				var h = a[e],
					g;
				for (g in b) g in h.style ? h.style[g] = b[g] : (str = ";" + g + ":" + b[g], h.style.cssText += str)
			}
			d && setTimeout(function() {
				d(a)
			}, c)
		} else return getComputedStyle(a, null).getPropertyValue(b)
	};
	c.classes = {
		add: function(a, b) {
			var d = a.className;
			this.have(a, b) || (d = d ? d + " " + b : b.toString(), a.setAttribute("class", d))
		},
		remove: function(a, b) {
			if (b) {
				var d = a.className,
					d = d.replace(b, "").replace(/^\s+|\s+$/g, "");
				a.setAttribute("class", d)
			} else a.className = ""
		},
		have: function(a, b) {
			return RegExp("\\b" + b + "\\b").exec(a.className)
		}
	};
	c.scale = function(a) {
		var b;
		(b = g("#debuggapScale")) && b.parentNode.removeChild(b);
		a ? t.scaleColor = a : a = t.scaleColor;
		a = [
			["top, transparent 4px, " + a + " 5px", "10px 5px", "100%", "10px"],
			["top, transparent 24px, " + a + " 25px", "20px 25px", "100%", "20px"],
			["left, transparent 4px, " + a + " 5px", "5px 10px", "10px", "100%"],
			["left, transparent 24px, " + a + " 25px", "25px 20px", "20px", "100%"]
		];
		b = document.createElement("div");
		b.id = "debuggapScale";
		c.classes.add(b, "dg-scale");
		for (var d = 0; 4 > d; d++) {
			var f = document.createElement("div"),
				e = a[d],
				e = "background:" + c.css3Prefix + "linear-gradient(" + e[0] + ");background-size:" + e[1] + ";height:" + e[2] + ";width:" + e[3],
				e = e + ";position:absolute;left:0px;top:0px;z-index:999;";
			f.setAttribute("style", e);
			b.appendChild(f)
		}
		g("#debuggapRoot").appendChild(b)
	};
	c.conf = {
		scaleColor: "#cccccc",
		lineColor: "#cc6600"
	};
	var t = {};
	c.draw = {
		drawLi: function(a) {
			var b = document.createElement("li");
			b.className = "dg-node";
			if (8 == a.nodeType) return a = a.nodeValue, a = a.replace(/\</g, "&lt;").replace(/\>/g, "&gt;"), b.innerHTML = '<pre class="pre"><span class="com">&lt;!--' + a + "--&gt;</span></pre>", b;
			if (3 == a.nodeType) return b.innerHTML = '<pre class="pre">' + a.nodeValue + "</pre>", b;
			if (10 == a.nodeType) return b.style.color = "#ccc", b.innerHTML = "&lt;!DOCTYPE " + a.name + " " + a.publicId + " " + a.systemId + "&gt;", b;
			for (var d = a.tagName.toLowerCase(), f = '<span class="tag">&lt;' + d + "</span>", e = a.attributes, h = null, g = 0; g < e.length; g++) f += ' <span class="attr">' + e[g].name + '=</span><span class="val">"' + e[g].value + '"</span>';
			if (c.selfClosing[d]) f += '<span class="tag">/&gt;</span>';
			else {
				f += '<span class="tag">&gt;</span>';
				if (a.childNodes.length) {
					f += "...";
					h = document.createElement("span");
					h.className = "dg-right";
					var l = document.createElement("span");
					l.className = "dg-tap"
				}
				f += '<span class="tag">&lt;/' + d + "&gt;</span>"
			}
			b.innerHTML = f;
			h && (b.appendChild(h), b.appendChild(l));
			return b
		},
		getRelation: function(a) {
			var b = a.parentNode,
				d = [];
			do {
				for (var f = [], e = c.filterChildNodes(b, [1, 3, 8]), h = 0; h < e.length; h++)"dg-child" != e[h].className && f.push(e[h]);
				for (e = 0; e < f.length && f[e] != a; e++);
				d.unshift(e);
				if ("ul" == b.tagName.toLowerCase() && "debuggapTree" == b.id) break;
				a = b.parentNode;
				do a = a.previousSibling;
				while (1 != a.nodeType);
				b = a.parentNode
			} while (1);
			return d
		},
		findRelation: function(a) {
			var b = c.filterChildNodes(document, [1, 3, 8, 10]),
				d;
			do {
				d = [];
				for (var f = 0; f < b.length; f++)"debuggapRoot" != b[f].id && d.push(b[f]);
				b = a.shift();
				d = d[b];
				b = c.filterChildNodes(d, [1, 3, 8, 10])
			} while (a.length);
			return d
		},
		doAction: function(a) {
			c.classes.have(a, "dg-rotate") ? this.del(a) : this.add(a.parentNode);
			delete a
		},
		add: function(a) {
			var b = a.innerHTML,
				b = b.replace(/\.\.\.(.*?)<\/span>/, "");
			a.innerHTML = b;
			var b = this.getRelation(a),
				b = this.findRelation(b),
				d = c.filterChildNodes(b, [1, 3, 8]),
				f = document.createElement("li");
			f.className = "dg-child";
			for (var e = document.createElement("ul"), h = 0; h < d.length; h++)"debuggapRoot" != d[h].id && e.appendChild(this.drawLi(d[h]));
			f.appendChild(e);
			d = document.createElement("li");
			d.className = "dg-child";
			d.innerHTML = '<span class="tag">&lt;/' + b.tagName.toLowerCase() + "&gt;</span>";
			a.parentNode.insertBefore(d, a.nextSibling);
			a.parentNode.insertBefore(f, d);
			b = g(".dg-right", a);
			c.classes.add(b, "dg-rotate");
			return a
		},
		del: function(a) {
			var b = a.parentNode;
			a = b.innerHTML;
			var d = a.match(/&lt;(.+?)<\/span>/)[1];
			a = a.replace(/&gt;<\/span>/, '&gt;</span>...<span class="tag">&lt;/' + d + "&gt;</span>");
			b.innerHTML = a;
			a = b.nextSibling;
			a.parentNode.removeChild(a);
			b.parentNode.removeChild(b.nextSibling);
			a = g(".dg-right", b);
			c.classes.remove(a, "dg-rotate");
			c.classes.have(b, "line-wh") && c.map.treeToEle(b);
			return b
		}
	};
	c.extend({
		indexArray: function(a, b) {
			for (var d = 0; d < b.length; d++) if (b[d] == a) return d;
			return -1
		},
		inArray: function(a, b) {
			return -1 != this.indexArray(a, b) ? !0 : !1
		},
		isArray: function(a) {
			return "[object Array]" === toString.call(a)
		},
		each: function(a, b, d) {
			if (void 0 == a.length) for (var c in a) {
				if (!1 === b.call(a[c], c, a[c], d)) break
			} else {
				c = 0;
				for (var e = a.length; c < e && !1 !== b.call(a[c], c, a[c], d); c++);
			}
		},
		position: function(a) {
			for (var b = 0, d = 0, c = a.clientWidth, e = a.clientHeight; a && a != document.body;) b += a.offsetLeft, d += a.offsetTop, a = a.offsetParent;
			return {
				left: b,
				top: d,
				width: c,
				height: e
			}
		},
		max: function(a, b) {
			return a > b ? a : b
		},
		min: function(a, b) {
			return a > b ? b : a
		},
		preName: function(a) {
			return c.css3Prefix + a
		},
		trim: function(a) {
			return a.replace(/^\s+|\s+$/g, "")
		},
		createEle: function(a, b, d) {
			a = document.createElement(a);
			for (var c in b) a.setAttribute(c, b[c]);
			d && (a.innerHTML = d);
			return a
		},
		isFunction: function(a) {
			return "function" == typeof a
		},
		filterChildNodes: function(a, b) {
			b = b ? b : [1, 3, 8];
			for (var d = [], f = a.childNodes, e = 0; e < f.length; e++)!c.inArray(f[e].nodeType, b) || 3 == f[e].nodeType && "" == c.trim(f[e].nodeValue) || d.push(f[e]);
			return d
		},
		ajax: function(a, b, d) {
			var c = new XMLHttpRequest;
			c.open(d ? "POST" : "GET", a, !0, "", "");
			c.setRequestHeader("Accept", "text/plain, */*");
			c.setRequestHeader("innerUse", !0);
			c.innerUse = !0;
			c.onreadystatechange = function() {
				c && 4 == c.readyState && (b(c), c = null, delete c)
			};
			c.send(d ? d : null)
		},
		bind: function(a, b) {
			"string" == typeof b && (b = a[b]);
			return function() {
				b.apply(a, arguments)
			}
		}
	});
	c.map = {
		treeToEle: function(a) {
			this.preShadowNode && this.removeMap(this.preShadowNode);
			this.preShadowNode = a;
			var b = c.draw.getRelation(a),
				b = c.draw.findRelation(b);
			this.drawShadow(b);
			c.classes.add(a, "line-wh");
			c.each(k("span", a), function() {
				c.classes.add(this, "font-wh")
			})
		},
		eleToTree: function(a) {
			var b = c.map.getRelation(a);
			if (n.socketReady()) n.doLeafStructure(b.join(",")), this.drawShadow(a), c.scale();
			else {
				c.doc.trigger(k("#debuggapBlock .dg-leaf")[0], "tap");
				var d = g("#debuggapTree");
				for (a = 0; a < b.length - 1; a++) {
					var f = b[a],
						d = k("li", d)[f];
					c.draw.add(d);
					d = d.nextSibling
				}
				d = k("li", d)[b[a]];
				c.map.treeToEle(d)
			}
		},
		getRelation: function(a) {
			var b = [],
				d = a;
			do {
				if (!d.parentNode) break;
				a = c.filterChildNodes(d.parentNode, [1, 3, 8, 10]);
				for (var f = 0; f < a.length && a[f] != d; f++);
				b.unshift(f);
				d = d.parentNode
			} while (d && 9 != d.nodeType);
			return b
		},
		removeMap: function(a) {
			c.classes.remove(a, "line-wh");
			c.each(k("span", a), function() {
				c.classes.remove(this, "font-wh")
			});
			(a = g("#debuggapShadow")) && debuggapNode.removeChild(a);
			c.each(k(".debuggapLine"), function() {
				debuggapNode.removeChild(this)
			});
			this.preShadowNode = null
		},
		drawShadow: function(a) {
			c.doc.bind(document, "taps", function(a) {
				c.each(k("#debuggapTree,#debuggapScale,#debuggapShadow,#debuggapConfig,.debuggapLine"), function() {
					debuggapNode.removeChild(this)
				});
				c.doc.unbind(document);
				a.preventDefault();
				a.stopPropagation()
			});
			var b = g("#debuggapShadow");
			b && debuggapNode.removeChild(b);
			for (var d = a.getBoundingClientRect(), b = ["padding", "border", "margin"], f = ["left", "right", "top", "bottom"], e = {}, h = 0; h < b.length; h++) {
				e[b[h]] = [];
				var u = "";
				"border" == b[h] && (u = "-width");
				for (var l = 0; l < f.length; l++) {
					var w = b[h] + "-" + f[l] + u;
					e[b[h]].push(parseInt(c.css(a, w)))
				}
			}
			a = d.left + document.body.scrollLeft;
			var u = d.top + document.body.scrollTop,
				p = d.width - e.border[0] - e.border[1],
				d = d.height - e.border[2] - e.border[3];
			a = Math.ceil(a - e.margin[0]);
			var u = Math.ceil(u - e.margin[2]),
				p = c.max(p - e.padding[0] - e.padding[1], 0),
				d = c.max(d - e.padding[2] - e.padding[3], 0),
				s = document.createElement("div");
			c.css(s, {
				width: p + "px",
				height: d + "px",
				opacity: 0.5,
				"background-color": "#3879d9"
			});
			for (h = 0; 4 > h; h++) e.margin[h] += e.border[h];
			b.splice(1, 1);
			for (l = 0; l < b.length; l++) {
				var q = b[l];
				if (0 != e[q][0] + e[q][1] + e[q][2] + e[q][3]) {
					for (var m = document.createElement("div"), n = {
						opacity: 0.8
					}, h = 0; h < f.length; h++) w = "border-" + f[h], n[w] = e[q][h] + "px solid " + this.borderColor[q];
					c.css(m, n);
					m.appendChild(s);
					s = m
				}
			}
			c.css(s, {
				position: "absolute",
				left: a + "px",
				top: u + "px"
			});
			s.id = "debuggapShadow";
			debuggapNode.insertBefore(s, debuggapNode.childNodes[0]);
			this.drawLine(a, u, p + e.padding[0] + e.padding[1] + e.margin[0] + e.margin[1], d + e.padding[2] + e.padding[3] + e.margin[2] + e.margin[3])
		},
		drawLine: function(a, b, d, f) {
			c.each(k(".debuggapLine"), function() {
				debuggapNode.removeChild(this)
			});
			if (0 != d && 0 != f) {
				var e = c.size().width,
					h = c.size().height;
				a = [
					[a, 0, 1, b],
					[a + d - 1, 0, 1, b],
					[a, b + f, 1, h - b - f],
					[a + d - 1, b + f - 1, 1, h - b - f],
					[0, b, a, 1],
					[a + d, b, e - a - d, 1],
					[0, b + f - 1, a, 1],
					[a + d, b + f - 1, e - a - d, 1]
				];
				b = document.createDocumentFragment();
				d = t.lineColor;
				for (f = 0; f < a.length; f++) e = a[f], h = document.createElement("div"), c.css(h, {
					left: e[0] + "px",
					top: e[1] + "px",
					width: e[2] + "px",
					height: e[3] + "px",
					position: "absolute",
					"background-color": d
				}), c.classes.add(h, "debuggapLine"), b.appendChild(h);
				debuggapNode.insertBefore(b, debuggapNode.childNodes[0])
			}
		},
		noMap: {
			html: 1,
			head: 1,
			script: 1,
			style: 1,
			meta: 1,
			title: 1,
			option: 1
		},
		borderColor: {
			padding: "#329406",
			border: "#dd903f",
			margin: "#c56c0e"
		},
		preShadowNode: null
	};
	c.console = {
		log: function() {
			var a = this.createLine();
			c.inArray(this.focus, ["all", "log"]) || c.css(a, {
				display: "none"
			});
			c.classes.add(a, "dg-l");
			k("td", a)[1].innerHTML = this.concatArg(arguments)
		},
		warn: function() {
			var a = this.createLine();
			c.inArray(this.focus, ["all", "warn"]) || c.css(a, {
				display: "none"
			});
			c.classes.add(a, "dg-w");
			k("td", a)[0].innerHTML = '<div class="dg-warn"></div><div class="dg-type-con">!</div>';
			k("td", a)[1].innerHTML = this.concatArg(arguments)
		},
		error: function() {
			var a = this.createLine();
			c.inArray(this.focus, ["all", "error"]) || c.css(a, {
				display: "none"
			});
			c.classes.add(a, "dg-e");
			k("td", a)[0].innerHTML = '<div class="dg-error"></div><div class="dg-type-con">x</div>';
			k("td", a)[1].innerHTML = "<span style='color:red'>" + this.concatArg(arguments) + "</span>"
		},
		concatArg: function(a) {
			for (var b = "", d = 0, c = a.length; d < c; d++) b += " " + a[d];
			return b
		},
		tryCatch: function(a) {
			this.history[0] != a && this.history.unshift(a);
			this.createLine(a);
			try {
				a = /(for|while)/.exec(a) ? 'return new Function("' + a + '")()' : "return " + a;
				var b = (new Function(a))();
				b ? "string" == typeof b ? b = '<span style="white-space:pre;color:#cb4416;">' + b.replace(/\>/g, "&gt;").replace(/\</g, "&lt;") + "</span>" : "function" == typeof b && (b = '<span style="white-space:pre">' + b + "</span>") : b += "";
				this.log(b)
			} catch (d) {
				this.error(d.name + ": " + d.message)
			}
		},
		createLine: function(a) {
			var b = document.createElement("tr");
			b.innerHTML = "<td></td><td></td>";
			c.each(k("td", b), function(b) {
				this.innerHTML = 1 == b && a ? '<span style="color:blue;">' + a + "</span>" : ""
			});
			c.classes.add(k("td", b)[0], "dg-type");
			c.classes.add(k("td", b)[1], "dg-con");
			g("table", g("#debuggapConsole .dg-console")).appendChild(b);
			return b
		},
		history: [],
		index: -1,
		up: function() {
			this.index++;
			this.index < this.history.length ? g("#debuggapInput").value = this.history[this.index] : this.index--
		},
		down: function() {
			this.index--;
			0 > this.index ? (g("#debuggapInput").value = "", this.index = -1) : g("#debuggapInput").value = this.history[this.index]
		},
		go: function() {
			var a = g("#debuggapInput");
			a.value && (this.tryCatch(a.value), this.index = -1, a.value = "")
		},
		clean: function() {
			var a = g(".dg-console", g("#debuggapConsole")),
				a = k("tr", a);
			c.each(a, function() {
				this.parentNode.removeChild(this)
			})
		},
		focus: "all",
		filter: function(a) {
			var b = a.innerHTML;
			if ("clean" == b.toLowerCase()) return this.clean(), !0;
			this.focus = b.toLowerCase();
			c.each(k("span", a.parentNode), function() {
				this == a ? c.classes.add(this, "dg-console-focus") : c.classes.remove(this, "dg-console-focus")
			});
			var b = b.toLowerCase()[0],
				d = g(".dg-console", g("#debuggapConsole")),
				f = "a" == b ? {
					display: "table-row"
				} : {
					display: "none"
				};
			c.each(k(".dg-l,.dg-e,.dg-w", d), function() {
				c.css(this, f)
			});
			"a" != b && c.each(k(".dg-" + b), function() {
				c.css(this, {
					display: "table-row"
				})
			})
		},
		overwrite: function() {
			for (var a = ["log", "warn", "error"], b = 0; b < a.length; b++) {
				var d = console[a[b]];
				(function(a, b) {
					console[b] = function() {
						a.apply(this, arguments);
						g("#debuggapConsole") && c.console[b].apply(c.console, arguments);
						m(b + "Cmd:" + n._transformCmd(arguments[0]))
					}
				})(d, a[b])
			}
			d = null;
			delete null;
			delete d
		}
	};
	c.event = {
		eventIndex: 1,
		inWrap: function(a, b) {
			var d = a.left + a.width,
				c = a.top + a.height,
				e = b.pageX,
				h = b.pageY;
			if (e > a.left && h > a.top && e < d && h < c) return !0
		},
		register: function(a) {
			if (!(this instanceof arguments.callee)) return !0;
			var b = a.parentNode,
				d = 0,
				f = 0,
				e = {};
			this.bind = function(a, b, d) {
				var f;
				"string" == typeof a ? (f = e[a]) ? f[b] = d : (f = {}, f[b] = d, e[a] = f) : a.dgEventIndex ? (f = e[a.dgEventIndex]) ? f[b] = d : (f = {}, f[b] = d, e[a.dgEventIndex] = f) : (a.dgEventIndex = c.event.eventIndex++, f = {}, f[b] = d, e[a.dgEventIndex] = f)
			};
			this.unbind = function(a) {
				a.dgEventIndex && e[a.dgEventIndex] && (e[a.dgEventIndex] = null, delete e[a.dgEventIndex])
			};
			this.trigger = function(a, b) {
				var d;
				(d = e[a.dgEventIndex]) && d[b].call(a, null)
			};
			this.destroy = function() {
				e = null;
				a.removeEventListener("touchmove", g, !1);
				a.removeEventListener("touchend", l, !1);
				a.removeEventListener("touchstart", h, !1);
				h = l = g = null
			};
			var h = function(a) {
					var h = a.touches && a.touches[0] || a,
						g = h.target;
					for (f = 0; g != b && g;) {
						var q = e[g.dgEventIndex];
						if (q && q.scroll) return a = q.scroll, d = 0, f = 1, a.dgOx = h.pageX, a.dgOy = h.pageY, c.css(a, {
							"-webkit-transition": ""
						}), h = (a.style.WebkitTransform ? a.style.WebkitTransform : "translate(0px,0px)").match(/translate\(([^\)]*)\)/)[1].split(","), a.dgX = parseInt(h[0]), a.dgY = parseInt(h[1]), !0;
						q && q.move && (d = 0);
						if (q && q.taps && q.taps.call(g, a)) return !0;
						g = g.parentNode
					}
				},
				g = function(a) {
					for (var c = a.touches[0], f = c.target; f != b && f;) {
						var h = e[f.dgEventIndex];
						if (h && h.scroll) return f = h.scroll, d = 1, c = Math.abs(c.pageY - f.dgOy) > Math.abs(c.pageX - f.dgOx) ? "translate(" + f.dgX + "px," + (c.pageY - f.dgOy + f.dgY) + "px) " : "translate(" + (c.pageX - f.dgOx + f.dgX) + "px," + f.dgY + "px) ", f.style.WebkitTransform = c, a.preventDefault(), !0;
						if (h && h.move && (d = 1, a.preventDefault(), a.stopPropagation(), h.move.call(f, a))) return !0;
						f = f.parentNode
					}
				},
				l = function(a) {
					for (var h = a.changedTouches[0], g = h.target; g != b && g;) {
						var l = g.dgEventIndex,
							u = (g.tagName || "").toLowerCase();
						if ((l = e[l] ? e[l] : e[u]) && l.tap && !d) {
							if (1 == g.nodeType) var k = c.position(g);
							else f = 1;
							if ((f || c.event.inWrap(k, h)) && l.tap.call(g, a)) return !0
						}
						if (l && l.scroll && d) return a = l.scroll, d = 0, h = (a.style.WebkitTransform ? a.style.WebkitTransform : "translate(0px,0px)").match(/translate\(([^\)]*)\)/)[1].split(","), a.dgX = parseInt(h[0]), a.dgY = parseInt(h[1]), h = c.max(a.scrollHeight - parseInt(c.css(a.parentNode, "height")), 0), g = c.max(a.scrollWidth - parseInt(c.css(a.parentNode, "width")), 0), l = k = "", u = 0, 0 < a.dgY ? (l = "0px", u = 1) : Math.abs(a.dgY) > h && (l = "-" + h + "px", u = 1), 0 < a.dgX ? (k = "0px", u = 1) : Math.abs(a.dgX) > g && (k = "-" + g + "px", u = 1), u && (k || (k = a.dgX + "px"), l || (l = a.dgY + "px"), c.css(a, {
							"-webkit-transition": "-webkit-transform 0.5s",
							"-webkit-transform": "translate(" + k + "," + l + ")"
						})), !0;
						g = g.parentNode
					}
				};
			a.addEventListener("touchmove", g, !1);
			a.addEventListener("touchend", l, !1);
			a.addEventListener("ie" == c.browser ? "mousedown" : "touchstart", h, !1)
		}
	};
	v.prototype = {
		tryMaxTimes: 1,
		currentTimes: 0,
		readyState: 0,
		timeout: 10,
		getSocketData: function() {
			var a = document.createElement("script");
			a.src = (0 == this.readyState ? this.url + "/init" : this.url) + "?_d=" + (new Date).getTime();
			a.id = "socket_script";
			a.onload = c.bind(this, "success");
			a.onerror = c.bind(this, "error");
			document.head.appendChild(a)
		},
		closeSocket: function() {
			var a = document.createElement("script");
			a.src = this.url + "/close?_d=" + (new Date).getTime();
			document.head.appendChild(a)
		},
		send: function(a) {
			c.ajax(this.url, function() {}, a)
		},
		success: function() {
			this._finish();
			setTimeout(c.bind(this, "getSocketData"), this.timeout)
		},
		error: function() {
			this._finish();
			this.currentTimes++ != this.tryMaxTimes ? setTimeout(c.bind(this, "getSocketData"), this.timeout) : (delete localStorage.scriptSocket, this.onclose && this.onclose())
		},
		_finish: function() {
			document.head.removeChild(document.getElementById("socket_script"))
		},
		close: function() {}
	};
	c.scriptSocket = {
		handShake: function() {
			r && (r.readyState = 1, r.onopen())
		},
		handle: function(a) {
			r && r.onmessage({
				data: a
			})
		}
	};
	(function() {
		var a = {},
			b = XMLHttpRequest.prototype.open;
		XMLHttpRequest.prototype.open = function(d, c, f) {
			try {
				var g = 1E3 * (new Date).getTime() + Math.floor(1E3 * Math.random()),
					k, p = c,
					m = p;
				"http" != p.slice(0, 4) && ("/" == p.slice(0, 1) ? m = location.protocol + "//" + location.host + p : "./" == p.slice(0, 2) && (m = (location.protocol + "//" + location.host + location.pathname).replace(/\/.[^\/]*$/, "/") + p.slice(2)));
				k = m;
				if ("http" == k.slice(0, 4) || "file" == k.slice(0, 4)) this.uniqueId = g, a[g] = {
					method: d,
					url: k,
					header: {}
				}
			} catch (q) {}
			b.apply(this, arguments)
		};
		var d = XMLHttpRequest.prototype.setRequestHeader;
		XMLHttpRequest.prototype.setRequestHeader = function(b, c) {
			this.uniqueId && (a[this.uniqueId].header[b] = c);
			d.apply(this, arguments)
		};
		var c = XMLHttpRequest.prototype.send;
		XMLHttpRequest.prototype.send = function(b) {
			this.setRequestHeader("XHR", !0);
			if (this.uniqueId) if (this.innerUse) a[this.uniqueId] = null, delete a[this.uniqueId], c.apply(this, arguments);
			else {
				a[this.uniqueId].body = b;
				if ("http" == a[this.uniqueId].url.slice(0, 4)) {
					var d = a[this.uniqueId].url.match(/([^:]+):\/\/([^\/\#\?]+)([^?#]*)([^#]*)(.*)/),
						g = d[2].split(":")[0],
						l = d[2].split(":")[1] ? d[2].split(":")[1] : "",
						d = {
							method: a[this.uniqueId].method,
							id: this.uniqueId,
							requestHeaders: a[this.uniqueId].header,
							httpVersion: "HTTP/1.1",
							location: {
								protocol: d[1],
								host: g,
								port: l,
								hostname: d[2],
								hash: d[5],
								search: d[4],
								pathname: d[3],
								href: d[0]
							}
						};
					b && (d.payload = b);
					m("initRequest:" + JSON.stringify(d))
				}
				var k = (new Date).getTime(),
					p = this,
					n = !1,
					q = function() {
						if (!n) {
							n = !0;
							var b, d;
							try {
								b = p.getAllResponseHeaders(), d = p.responseText
							} catch (c) {
								d = b = ""
							}
							if ("http" == a[p.uniqueId].url.slice(0, 4)) {
								var f = b.split("\r\n");
								b = {};
								for (var e = 0, g; e < f.length; e++) if (g = f[e]) g = g.split(":"), b[g[0]] = g[1];
								b = {
									host: location.host,
									times: (new Date).getTime() - k,
									size: d.length,
									responseHeaders: b,
									data: d,
									id: p.uniqueId,
									statusCode: p.status
								};
								m("resultRequest:" + JSON.stringify(b))
							}
						}
					},
					r = setInterval(function() {
						4 == p.readyState && (clearInterval(r), q(), a[p.uniqueId] = null, delete a[p.uniqueId])
					}, 5),
					t = function() {
						if (p.onreadystatechange) {
							clearInterval(v);
							var a = p.onreadystatechange;
							p.onreadystatechange = function() {
								4 == p.readyState && q();
								a && a()
							}
						}
					},
					v = setInterval(function() {
						t()
					}, 0);
				t();
				c.apply(p, arguments)
			}
		}
	})();
	c.init = {
		setting: function() {},
		addWrap: function() {
			if (!(debuggapNode = g("#debuggapRoot"))) {
				var a = document.createElement("div");
				a.id = "debuggapRoot";
				document.body.appendChild(a);
				debuggapNode = a;
				c.init.addStyle();
				c.init.addConsole();
				c.init.addBlock();
				"ie" == c.browser && c.css(g("#debuggapBlock"), {
					display: "none"
				})
			}
		},
		addStyle: function() {
			var a = document.createElement("style");
			a.innerHTML = "body{-webkit-text-size-adjust:100%}#debuggapRoot input{font-size:14px;-webkit-appearance:none;}#debuggapRoot .dg-block{white-space:nowrap;margin: 0px;padding: 20px;}#debuggapRoot td{font-family: arial,sans-serif;letter-spacing: 1px;}#debuggapRoot .dg-scale{}#debuggapRoot li{list-style:none;padding-left:15px;position:relative;font-size:15px;font-family:arial,sans-serif;line-height:18px;text-align:left;}#debuggapRoot ul{list-style:none;padding-left:0px;margin:0px;}span.dg-down{display:inline-block;border-left:5px solid transparent;border-right:5px solid transparent;border-top:10px solid #515151;width:0px;height:0px;position:absolute;left:0px;top:3px;}span.dg-right{-webkit-transition:-webkit-transform 0.5s;transition:transform 0.5s;display:inline-block;border-top:5px solid transparent;border-bottom:5px solid transparent;border-left:10px solid #515151;width:0px;height:0px;position:absolute;left:0px;top:3px;}span.dg-tap{height:18px;padding:0px 25px;left:-30px;position:absolute;}span.dg-rotate{-webkit-transform:rotate(90deg);transform:rotate(90deg);}#debuggapRoot .tag{color:#a5129f;}#debuggapRoot .attr{color:#994500}#debuggapRoot .val{color:#1a1a7e;}#debuggapRoot .com{color:#236e25;}#debuggapRoot .pre{margin:0px;padding:0px;}#debuggapRoot .font-wh{color:#fff;}#debuggapRoot .line-wh{color:#fff;background-color:#3879d9;}#debuggapTree {position:absolute;}.debuggapFull {background-color:rgba(255,255,255,0.5);position:absolute;left:0px;top:0px;right:0px;bottom:0px;z-index:999;overflow:hidden;}.debuggapFull0 {background-color:rgba(255,255,255,1);position:absolute;left:0px;top:0px;right:0px;bottom:0px;z-index:999;overflow:hidden;}#debuggapRoot .dg-out{background-color: transparent;position: absolute;z-index: 999;top: 20px;right: 20px;border: 2px solid #00abe3;border-radius: 30px;width: 30px;height: 30px;box-sizing: content-box;}#debuggapRoot .dg-inner{width:20px;height: 20px;background: #ccc;margin: 5px;border-radius: 20px;background-color: #00abe3;}#debuggapConsole{display:none;padding:10px;margin:0px;}#debuggapConsole .dg-console{overflow:hidden;border-top:1px solid #ccc;margin-top:2px;}#debuggapConsole .dg-console tr{display:table-row}#debuggapInput {width:100%;line-height:16px;padding:2px;margin:0px;border:1px solid #ccc;outline-style:none;}#debuggapConsole .dg-up{border-left:8px solid transparent;border-bottom:16px solid #515151;border-right:8px solid transparent;width:0px;height:0px;position:absolute;left:0px;top:7px;}#debuggapConsole .dg-go{border-top:8px solid transparent;border-bottom:8px solid transparent;border-left:16px solid #515151;width:0px;height:0px;position:absolute;right:0px;top:2px;}#debuggapConsole .dg-down{border-top:16px solid #515151;border-right:8px solid transparent;border-left:8px solid transparent;width:0px;height:0px;position:absolute;left:0px;top:7px;}#debuggapConsole .dg-upP{width:20px;height:25px;position:absolute;left:0px;top:0px;}#debuggapConsole .dg-downP{width:20px;height:25px;position:absolute;left:25px;top:0px;}#debuggapConsole .dg-goP{width:30px;height:20px;position:absolute;right:0px;top:0px;}#debuggapConsole .dg-type{width:20px;height:16px;text-align:center;position:relative;}#debuggapConsole .dg-con{border-bottom:1px solid #ccc;font-size:11px ! important;word-break:break-all;}#debuggapConsole .dg-error{border:6px solid #d80c15;border-radius:6px;width:0px;height:0px;position:absolute;left:0px;top:1px;}#debuggapConsole .dg-type-con{width:10px;height:10px;position:absolute;left:1px;top:1px;color:#fff;line-height:10px;font-size:14px;}#debuggapConsole .dg-warn{border-left:6px solid transparent;border-bottom:12px solid #f4bd00;border-right:6px solid transparent;width:0px;height:0px;position:absolute;left:0px;top:1px;}#debuggapConsole .dg-console-info{padding:0px 5px;color:#fff;background-color:#a8a8a8;border-radius:10px;margin-right:5px;font-size:14px;}#debuggapConsole .dg-console-focus{background-color:rgb(0,171,227);}#debuggapConfig {padding:10px;margin:0px;}#debuggapConfig .dg-conf-bts{height:30px;overflow:hidden;}#debuggapConfig .dg-conf-left{border-radius:5px;float:left;background-color:rgb(0,171,227);color:#fff;border:0px}#debuggapConfig .dg-conf-right{border-radius:5px;float:right;background-color:rgb(0,171,227);color:#fff;border:0px;}#debuggapBlock {}#debuggapBlock .dg-leaf{width:70px;height:70px;border-radius:30px;text-align:center;line-height:70px;color:#fff;margin:1px;float:left;background-color:rgba(0,171,227,0.7);}#debuggapBlock .dg-flower{width:144px;height:144px;position:absolute;z-index:999;left:50%;top:50%;margin-left:-72px;margin-top:-72px;opacity:0;display:none;-webkit-transition:opacity 0.5s;}#debuggapBlock .dg-center{width:50px;height:50px;position:absolute;left:47px;top:47px;border-radius:50px;text-align:center;line-height:50px;color:#fff;margin:1px;float:left;background-color:rgba(0,171,227,1);}";
			debuggapNode.appendChild(a)
		},
		addBlock: function() {
			var a = document.createElement("div");
			a.id = "debuggapBlock";
			a.innerHTML = '<div id="debuggapScrim" class="debuggapFull" style="display:none;"></div><div class="dg-flower" class="dg-flower"><div class="dg-leaf" style="border-top-left-radius:0px;">Nodes</div><div class="dg-leaf" style="border-top-right-radius:0px;">Inspect</div><div class="dg-leaf" style="border-bottom-left-radius:0px;">Config</div><div class="dg-leaf" style="border-bottom-right-radius:0px;" >Console</div><div class="dg-center">Close</div></div><div class="dg-out"><div class="dg-inner"></div></div>';
			debuggapNode.appendChild(a)
		},
		addConsole: function() {
			var a = document.createElement("div");
			a.id = "debuggapConsole";
			a.innerHTML = '<table border=0 cellpadding="0" cellspacing="0" width=100%><tr><td><input type="txt" id="debuggapInput"/></td><td> <div style="position:relative;width:25px;height:22px;"><div class="dg-goP"><div class="dg-go"></div></div></div></td></tr><tr><td colspan=2 ><div style="height:25px;width:100%;position:relative;"><div class="dg-upP"><div class="dg-up"></div></div><div class="dg-downP"><div class="dg-down"></div></div><div style="position:absolute;right:0px;top: 7px;"><span class="dg-console-info dg-console-focus">All</span><span class="dg-console-info">Error</span><span class="dg-console-info">Warn</span><span class="dg-console-info">Log</span><span class="dg-console-info">Clean</span> </div></div></td></tr></table><div class="dg-console"><table border=0 cellpadding="0" cellspacing="0" width=100%></table></div>';
			debuggapNode.appendChild(a)
		},
		showTree: function() {
			var a = document.createElement("ul");
			a.id = "debuggapTree";
			c.classes.add(a, "dg-block");
			for (var b = 0; b < document.childNodes.length; b++) {
				var d = debuggap.draw.drawLi(document.childNodes[b]);
				a.appendChild(d)
			}
			debuggapNode.appendChild(a);
			c.scale();
			c.classes.add(g("#debuggapRoot"), "debuggapFull");
			c.css(g("#debuggapTree"), {
				"min-width": debuggap.size.width + "px",
				"min-height": debuggap.size.height + "px"
			})
		},
		destroyTree: function() {
			debuggapNode.removeChild(g("#debuggapTree"));
			debuggapNode.removeChild(g("#debuggapScale"));
			c.classes.remove(debuggapNode, "debuggapFull")
		},
		showConfig: function() {
			if (!g("#debuggapConfig")) {
				var a = document.createElement("div");
				a.id = "debuggapConfig";
				a.innerHTML = '<table width="100%" border=0><caption>Config Setting</caption><tr><td>scale color:</td><td><input type="txt" id="scaleColor"/></td></tr><tr><td>line color:</td><td><input type="txt" id="lineColor"/></td></tr></table><div class="dg-conf-bts"><input class="dg-conf-left" type="button" value="reset"/><input class="dg-conf-right"  type="button" value="modify"/></div><hr/>click the following button to connect to remote DebugGap<br/><div class="dg-socket-bts"><b>Server</b> : <input type="txt" id="dgSocketHost" style="width:100px"> : <input type="txt" id="dgSocketPort" style="width:50px"> <input class="dg-conf-right" id="dgConnect" type="button" value="Connect"/></div>';
				debuggapNode.appendChild(a);
				for (var b in t) g("#" + b) && (g("#" + b).value = t[b])
			}
		},
		daemon: function() {
			var a = 0,
				b = setInterval(function() {
					g("#debuggapRoot") || c.init.addWrap();
					10 <= ++a && (c.start(), clearInterval(b))
				}, 200)
		},
		reconnect: function() {
			var a = c._getCurrentAddr();
			a && 2 == a.length && (localStorage.host = a[0], localStorage.port = a[1], localStorage.protocal = "websocket", localStorage.name = "debuggap_client", localStorage.expired = (new Date).getTime() + 36E5);
			"ie" == c.browser && 2 != a.length ? alert('Please include debuggap.js with remote address in IE.\nsuch as:\n<script src="debuggap.js?192.168.1.4:11111">\x3c/script>') : localStorage.expired && (new Date).getTime() < localStorage.expired && (c.extend(c.conf, {
				host: localStorage.host,
				port: localStorage.port,
				protocal: localStorage.protocal,
				name: localStorage.name
			}), c.initSocket(c.conf))
		}
	};
	c.ready = function() {
		debuggap.extend(t, c.conf);
		c.console.overwrite();
		c.init.addWrap();
		c.init.daemon();
		c.init.reconnect()
	};
	c.start = function() {
		var a = new c.event.register(document);
		c.doc = a;
		a.bind(g("#debuggapBlock .dg-out"), "tap", function() {
			a.unbind(document);
			var b = g("#debuggapBlock .dg-flower");
			0 == c.css(b, "opacity") ? (c.css(g("#debuggapScrim"), {
				display: "block"
			}), c.css(g(".dg-out"), {
				display: "none"
			}), c.css(b, {
				opacity: 1,
				display: "block"
			})) : c.css(b, {
				opacity: 0
			}, function(a) {
				c.css(a, {
					display: "none"
				})
			}, 500);
			return !0
		});
		a.bind(g("#debuggapBlock .dg-out"), "move", function(a) {
			var d = a.touches[0];
			a = d.pageX;
			var d = d.pageY,
				f = c.size().width - 40,
				e = c.size().height - 40;
			10 > a ? a = 10 : a > f && (a = f);
			10 > d ? d = 10 : d > e && (d = e);
			c.css(this, {
				top: d + "px",
				left: a + "px"
			});
			return !0
		});
		a.bind("span", "tap", function() {
			if (c.classes.have(this, "dg-tap")) {
				var a = g(".dg-right", this.parentNode);
				c.draw.doAction(a);
				return !0
			}
			if (c.classes.have(this, "dg-console-info")) return c.console.filter(this), !0
		});
		a.bind("li", "tap", function() {
			if (c.classes.have(this, "dg-node")) {
				var a = this.innerHTML.match(/&lt;(.*?)<\/span>/)[1];
				c.map.noMap[a] || (c.classes.have(this, "line-wh") ? c.map.removeMap(this) : c.map.treeToEle(this));
				return !0
			}
		});
		a.bind("input", "tap", function() {
			if (this.parentNode && "dg-conf-bts" == this.parentNode.className) {
				if ("reset" == this.value) for (var a in t) g("#" + a) && (t[a] = g("#" + a).value = c.conf[a]);
				else for (a in t) g("#" + a) && (t[a] = g("#" + a).value);
				return !0
			}
			this.parentNode && ("dg-socket-bts" == this.parentNode.className && "button" == this.type) && (c.extend(c.conf, {
				host: g("#dgSocketHost").value,
				port: g("#dgSocketPort").value,
				protocal: "websocket",
				name: "debuggap_client"
			}), localStorage.host = c.conf.host, localStorage.port = c.conf.port, localStorage.protocal = c.conf.protocal, localStorage.name = c.conf.name, localStorage.expired = (new Date).getTime() + 36E5, this.value = "Connecting", c.initSocket(c.conf))
		});
		a.bind(g("#debuggapScrim"), "tap", function(a) {
			c.css(this, {
				display: "none"
			});
			c.css(g("#debuggapBlock .dg-flower"), {
				opacity: 0
			}, function(a) {
				c.css(a, {
					display: "none"
				})
			}, 500);
			c.css(g(".dg-out"), {
				display: "block"
			});
			return !0
		});
		a.bind(k("#debuggapBlock .dg-leaf")[0], "tap", function(b) {
			a.trigger(g("#debuggapBlock .dg-center"), "tap");
			c.init.showTree();
			a.bind(g("#debuggapRoot"), "scroll", g("#debuggapTree"));
			return !0
		});
		a.bind(k("#debuggapBlock .dg-leaf")[1], "tap", function(b) {
			a.trigger(g("#debuggapBlock .dg-center"), "tap");
			a.bind(g("#debuggapRoot"), "scroll", null);
			a.bind(document, "taps", function(b) {
				a.unbind(document);
				var f = b.changedTouches[0].target;
				c.inArray(f.className, ["dg-inner", "dg-out"]) || c.map.eleToTree(f);
				b.preventDefault()
			});
			b && b.preventDefault();
			b && b.stopPropagation();
			return !0
		});
		a.bind(k("#debuggapBlock .dg-leaf")[2], "tap", function(b) {
			a.trigger(g("#debuggapBlock .dg-center"), "tap");
			c.classes.add(g("#debuggapRoot"), "debuggapFull0");
			c.init.showConfig();
			b = "";
			try {
				b = JSON.parse(b), b = b.split(":"), g("#dgSocketHost").value = b[0], g("#dgSocketPort").value = b[1]
			} catch (d) {
				localStorage.host && (g("#dgSocketHost").value = localStorage.host, g("#dgSocketPort").value = localStorage.port)
			}
			return !0
		});
		a.bind(k("#debuggapBlock .dg-leaf")[3], "tap", function(b) {
			a.trigger(g("#debuggapBlock .dg-center"), "tap");
			c.css(g("#debuggapConsole"), {
				display: "block"
			});
			c.classes.add(g("#debuggapRoot"), "debuggapFull0");
			a.bind(g("#debuggapRoot"), "scroll", g("#debuggapConsole .dg-console table"));
			c.css(g("#debuggapConsole .dg-console"), {
				height: c.size().height - 65 + "px"
			});
			return !0
		});
		a.bind(g("#debuggapBlock .dg-center"), "tap", function(b) {
			a.trigger(g("#debuggapScrim"), "tap");
			c.each(k("#debuggapTree,#debuggapScale,#debuggapShadow,#debuggapConfig,.debuggapLine"), function() {
				debuggapNode.removeChild(this)
			});
			c.css(g("#debuggapConsole"), {
				display: "none"
			});
			c.classes.remove(debuggapNode);
			return !0
		});
		g("#debuggapInput").addEventListener("keypress", function(a) {
			13 != a.which && 13 != a.keyCode || c.console.go()
		}, !1);
		a.bind(g("#debuggapConsole .dg-upP"), "tap", function(a) {
			c.console.up()
		});
		a.bind(g("#debuggapConsole .dg-goP"), "tap", function(a) {
			c.console.go()
		});
		a.bind(g("#debuggapConsole .dg-downP"), "tap", function(a) {
			c.console.down()
		})
	};
	var n = {
		remoteClientReady: !1,
		socketReady: function() {
			return r && 1 == r.readyState && n.remoteClientReady
		},
		doReady: function() {
			console.log("Socket connected successfully")
		},
		doInit: function() {
			this.remoteClientReady = !0;
			this.preCookie = this.preSessionStorage = this.preLocalStorage = "";
			var a;
			a = "" + (c.indexArray(document.body.parentNode, c.filterChildNodes(document, [1, 3, 8, 10])) + ",");
			a += c.indexArray(document.body, c.filterChildNodes(document.body.parentNode, [1, 3, 8, 10]));
			this.doAllStructure(a);
			m("deviceInfo:" + navigator.userAgent)
		},
		doAllStructure: function(a) {
			a = n._getStructure(a);
			m("allStructure:" + JSON.stringify(a))
		},
		doLeafStructure: function(a) {
			var b = a.lastIndexOf(","),
				d = a.substr(b + 1);
			a = a.substr(0, b);
			a = n._getStructure(a);
			m("leafStructure:" + d + ";" + JSON.stringify(a))
		},
		doGetChildren: function(a) {
			for (var b = a.split(","), d, c = n._getStructure(a); b.length;) d = b.shift(), c = c[d].c;
			m("addChildren:" + a + ";" + JSON.stringify(c))
		},
		doGetChildrenV2: function(a) {
			var b = a.slice(0, 13);
			a = a.slice(14);
			for (var d = "" == a ? [] : a.split(","), c, e = n._getStructure(a); d.length;) c = d.shift(), e = e[c].c;
			m("childrenList:" + b + ";" + a + ";" + JSON.stringify(e))
		},
		_getStructure: function(a) {
			a = "" == a ? [] : a.split(",");
			for (var b = document, d = [], f, e = d; a.length;) {
				f = c.filterChildNodes(b, [1, 3, 8, 10]);
				for (var g = parseInt(a.shift()), k = 0; k < f.length; k++) if ("debuggapRoot" == f[k].id) f.splice(k, 1), k--;
				else {
					var l = this._getTagAndAttr(f[k]);
					d.push(l);
					k == g && (b = f[g])
				}
				d = d[g].c
			}
			f = c.filterChildNodes(b, [1, 3, 8, 10]);
			for (k = 0; k < f.length; k++)"debuggapRoot" != f[k].id && (l = this._getTagAndAttr(f[k])) && d.push(l);
			return e
		},
		_getTagAndAttr: function(a) {
			var b = {
				t: a.nodeName.toLowerCase(),
				c: 0 < a.childNodes.length ? [] : !1
			};
			if (1 == a.nodeType) {
				b._dg_t = b.t;
				b.c && (1 == a.childNodes.length && 3 == a.childNodes[0].nodeType && 20 > a.childNodes[0].nodeValue.length) && (b.cs = a.childNodes[0].nodeValue);
				a = a.attributes;
				for (var d = 0; d < a.length; d++) b.a || (b.a = {}), b.a[a[d].name] = a[d].value
			} else 10 == a.nodeType ? (b.s = "<!DOCTYPE " + a.name + " " + a.publicId + " " + a.systemId + ">", delete b.t) : (b.s = a.nodeValue, b._dg_t = b.t);
			return b
		},
		doFile: function(a) {
			setTimeout(function() {
				c.ajax(a, function(b) {
					m("fileCon:" + a + "_dg_" + b.responseText)
				})
			}, 100)
		},
		doCmd: function(a) {
			try {
				var b = (new Function("return " + a))(),
					b = this._transformCmd(b);
				m("cmdResult:" + b)
			} catch (d) {
				b = d.name + ": " + d.message, console.error(b)
			}
		},
		_transformCmd: function(a) {
			if (a && c.inArray(a.nodeType, [1, 3, 8, 9])) {
				var b = c.map.getRelation(a);
				a = n._getTagAndAttr(a);
				a.relation = b;
				a = JSON.stringify(a)
			} else if ("[object Array]" == Object.prototype.toString.call(a)) try {
				a = JSON.stringify(a)
			} catch (d) {
				a = this._objectToString(a), a = JSON.stringify(a)
			} else "[object Object]" == Object.prototype.toString.call(a) && (a = this._objectToString(a), a = JSON.stringify(a));
			return a
		},
		_objectToString: function(a) {
			var b = Object.keys(a),
				d = {},
				f;
			a.length && (d = []);
			for (var e = 0, g = b.length; e < g; e++) if (f = b[e], a[f] && c.inArray(a[f].nodeType, [1, 3, 8, 9])) {
				var k = c.map.getRelation(a[f]),
					l = n._getTagAndAttr(a[f]);
				l.relation = k;
				d[f] = {
					v: a[f].toString(),
					element: l
				}
			} else a[f] && "object" == typeof a[f] && Object.keys(a[f]).length ? d[f] = arguments.callee(a[f]) : "function" == typeof a[f] ? d[f] = {
				v: a[f].toString().match(/[^\n{]+/)[0] + "{...}",
				tag: "func"
			} : "[object RegExp]" == Object.prototype.toString.call(a[f]) ? d[f] = {
				v: a[f].toString(),
				tag: "reg"
			} : d[f] = a[f];
			return d
		},
		doFileTree: function() {
			this._doFileStart(location.href.replace(location.hash, ""));
			for (var a = document.scripts, b = 0; b < a.length; b++) a[b].src && "chrome-extension" != a[b].src.substr(0, 16) && this._doFile(a[b].src);
			a = document.styleSheets;
			for (b = 0; b < a.length; b++) a[b].href && this._doFile(a[b].href);
			m("fileTree:" + JSON.stringify([this._sPre, this._sTitle, this._sFiles]))
		},
		_doFile: function(a) {
			a = a.replace(this._sPre, "");
			a = a.split("/");
			var b = this._sFiles;
			if (1 == a.length) b.push(a[0]);
			else {
				for (var d = 0; d < a.length - 1; d++) {
					for (var c = a[d], e = b.length, g = !1, k = 0; k < e; k++) if ("string" != typeof b[k] && b[k][c]) {
						b = b[k][c];
						g = !0;
						break
					}
					if (!g) {
						d = a.slice(d, -1);
						b = this._sCreateTree(b, d);
						break
					}
				}
				b.push(a[a.length - 1])
			}
			b.sort(function(a, b) {
				return a > b ? 1 : -1
			})
		},
		_sCreateTree: function(a, b) {
			for (var d = 0; d < b.length; d++) {
				var c = {};
				c[b[d]] = [];
				var c = a.push(c),
					e = a;
				a = a[c - 1][b[d]];
				e.sort(function(a, b) {
					var d = "string" == typeof a,
						c = "string" == typeof b;
					return d && c ? a > b ? -1 : 1 : d ? 1 : c ? -1 : Object.keys(a)[0] > Object.keys(b)[0] ? -1 : 1
				})
			}
			return a
		},
		_doFileStart: function(a) {
			this._sPre = a.substring(0, a.lastIndexOf("/") + 1);
			this._sFiles = [];
			this._sTitle = a.substring(a.lastIndexOf("/") + 1)
		},
		preLocalStorage: "",
		doLocalStorage: function() {
			var a = this._addDot(localStorage);
			a != this.preLocalStorage && (this.preLocalStorage = a, m("localStorage:" + a))
		},
		preSessionStorage: "",
		doSessionStorage: function() {
			var a = this._addDot(sessionStorage);
			a != this.preSessionStorage && (this.preSessionStorage = a, m("sessionStorage:" + a))
		},
		_addDot: function(a) {
			var b = {},
				c;
			for (c in a) b[c] = a[c].substr(0, 250), 250 < a[c].length && (b[c] += "...");
			return JSON.stringify(b)
		},
		preCookie: "",
		doCookie: function() {
			var a = document.cookie;
			a != this.preCookie && (this.preCookie = a, m("cookie:" + a))
		},
		doDelLocalStorage: function(a) {
			localStorage[a] = null;
			delete localStorage[a]
		},
		doDelSessionStorage: function(a) {
			sessionStorage[a] = null;
			delete sessionStorage[a]
		},
		doDelCookie: function(a) {
			var b = new Date;
			b.setTime(b.getTime() - 1E4);
			document.cookie = a + "=0; expires=" + b.toGMTString()
		},
		doCacheFile: function(a) {
			var b = a.indexOf("_dg_");
			if (!(1 > b)) {
				var c = a.substring(0, b);
				a = a.substring(b + 4);
				b = c.substring(c.lastIndexOf(".") + 1);
				"js" == b ? this._doCacheJs(c, a) : "css" == b && this._doCacheCss(c, a)
			}
		},
		_doCacheJs: function(a, b) {
			g('script[_src="' + a + '"]') && g('script[_src="' + a + '"]').remove();
			g("#debuggapRoot").appendChild(c.createEle("script", {
				_src: a
			}, b))
		},
		_doCacheCss: function(a, b) {
			var d = null,
				d = g('style[_href="' + a + '"]') ? g('style[_href="' + a + '"]') : n.deepFinder("link", a);
			d.parentNode.insertBefore(c.createEle("style", {
				_href: a
			}, b), d);
			d.remove()
		},
		doRelationToEle: function(a) {
			a = a.split(",");
			a = c.draw.findRelation(a);
			1 == a.nodeType && (c.doc.trigger(g("#debuggapBlock .dg-center"), "tap"), c.map.drawShadow(a), c.scale())
		},
		doGetCalculateCss: function(a) {
			a = a.split(",");
			a = c.draw.findRelation(a);
			this._getCalculateCss(a)
		},
		_getCalculateCss: function(a) {
			a = x(a);
			m("calculateCss:" + JSON.stringify(a))
		},
		_resetCssForElement: function(a) {
			c.map.drawShadow(a);
			this._getCalculateCss(a)
		},
		doAddCssForElement: function(a) {
			a = a.split(";");
			var b = c.draw.findRelation(a[0].split(",")),
				d = b.getAttribute("style"),
				d = d ? d.replace(/;*$/, ";") : "";
			b.setAttribute("style", d + a[1]);
			this._getCalculateCss(b)
		},
		doRemoveCssForElement: function(a) {
			a = a.split(";");
			var b = c.draw.findRelation(a[0].split(",")),
				d = c.trim(b.getAttribute("style")),
				d = d.replace(/^;+|;+$/g, ""),
				d = d.split(";");
			d.splice(parseInt(a[1]), 1);
			d = d.join(";");
			b.setAttribute("style", d);
			this._getCalculateCss(b)
		},
		doReplaceCssForElement: function(a) {
			a = a.split(";");
			var b = c.draw.findRelation(a[0].split(",")),
				d = c.trim(b.getAttribute("style")),
				d = d.replace(/^;+|;+$/g, ""),
				d = d.split(";");
			d[a[1]] = a[2];
			d = d.join(";");
			b.setAttribute("style", d);
			this._getCalculateCss(b)
		},
		styleCache: {},
		doActiveCssForElement: function(a) {
			a = a.split(";");
			var b = c.draw.findRelation(a[0].split(",")),
				d = c.trim(b.getAttribute("style")),
				d = d.replace(/^;+|;+$/g, "");
			(d = d.split(";")) && d[a[1]] && (d[a[1]] = "active" == a[2] ? d[a[1]].replace(/\/\*+([^*]+)\*+\//, "$1") : "/*" + d[a[1]] + "*/", d = d.join(";"), b.setAttribute("style", d));
			this._getCalculateCss(b)
		},
		doReplaceClassItem: function(a) {
			var b = a.split(";");
			a = c.draw.findRelation(b[0].split(","));
			var d = b[1].split(":"),
				b = decodeURIComponent(b[2]);
			if (d && "undefined" != typeof d[1]) {
				var f = document.styleSheets[d[0]],
					e = d[1];
				if (3 == d.length) {
					var g = this._getMediaCss(f.cssRules[e]);
					g[d[2]] = b;
					b = f.cssRules[e].cssText.match(/@\s*media\s+[^{]+/)[0] + "{\n" + g.join("\n") + "\n}"
				}
				"ie" == c.browser ? f.deleteRule(e) : f.removeRule(e);
				f.insertRule(b, e)
			}
			this._getCalculateCss(a)
		},
		_getMediaCss: function(a) {
			var b = [];
			if (a = a.cssRules) for (var c = 0, f = a.length; c < f; c++) b.push(a[c].cssText);
			return b
		},
		doGetPrompt: function(a) {
			var b = a.substr(0, a.indexOf(":"));
			a = a.substr(a.indexOf(":") + 1).split(".");
			for (var c = window, f = a.length, e = 0; e < f - 1; e++) try {
				c = c[a[e]]
			} catch (g) {
				m("prompt:" + b + ":" + JSON.stringify({
					msg: g.message
				}))
			}
			try {
				var k = [],
					l = RegExp("^" + a[f - 1]);
				for (e in c) l.test(e) && k.push(e);
				m("prompt:" + b + ":" + JSON.stringify(k))
			} catch (n) {}
		},
		doCleanInspect: function() {
			c.doc.trigger(g("#debuggapBlock .dg-center"), "tap")
		},
		doStartInspect: function() {
			this.doCleanInspect();
			c.doc.bind(document, "taps", function(a) {
				c.doc.unbind(document);
				m("closeInspect:", !0);
				var b = a.changedTouches && a.changedTouches[0].target || a.target;
				c.inArray(b.className, ["dg-inner", "dg-out"]) || c.map.eleToTree(b);
				a.preventDefault()
			})
		},
		doCloseInspect: function() {
			c.doc.unbind(document);
			this.doCleanInspect()
		}
	};
	c.extend(n, {
		deepFinder: function(a, b) {
			for (var c = "script" == a ? "src" : "href", f = k(a), e = 0; e < f.length; e++) if (f[e][c] == b) return f[e];
			return null
		}
	});
	var r;
	c.extend({
		socketBuffer: [],
		socketTimeout: 0,
		socketSendStop: 1,
		_sendMessage: function() {
			if (0 < c.socketBuffer.length) {
				var a = c.socketBuffer.shift(),
					a = encodeURIComponent(a);
				setTimeout(function() {
					r.send(a);
					var b = Math.ceil(a.length / 50);
					5E3 < b ? b = 5E3 : 50 > b && (b = 50);
					c.socketTimeout = b;
					0 == c.socketBuffer.length ? (c.socketSendStop = 1, 500 < b && (c.socketTimeout = b)) : c._sendMessage()
				}, c.socketTimeout)
			}
		},
		_getCurrentAddr: function() {
			for (var a = [], b = document.scripts, c, f = 0, e = b.length; f < e; f++) if ((c = b[f].src.match(/\?(.*)/)) && 2 == c.length) {
				a = c[1].split(":");
				break
			}
			return a
		},
		decodeMessage: function(a) {
			for (var b = [], c = 0; c < a.length; c += 2) b.push(parseInt(a.charCodeAt(c).toString(16).substr(1, 1) + a.charCodeAt(c + 1).toString(16).substr(1, 1), 16));
			a = b.splice(0, 4);
			for (var f = 0, e = "", c = 0; c < b.length; c++) e += String.fromCharCode(a[f++ % 4] ^ b[c]);
			return e
		},
		distributeMessage: function(a) {
			a = decodeURIComponent(a);
			var b = a.indexOf(":");
			if (!(1 > b)) {
				var c = a.substring(0, b);
				a = a.substring(b + 1);
				b = "do" + c[0].toUpperCase() + c.substring(1);
				try {
					n[b](a)
				} catch (f) {
					console.error(f.message)
				}
			}
		},
		initSocketMethod: function(a) {
			a.onopen = function(b) {
				a.send(encodeURIComponent("initClient:" + c.version + "_debuggap_" + navigator.userAgent + "_debuggap_" + location.href));
				g("#dgConnect") && (g("#dgConnect").value = "Connect")
			};
			a.onmessage = function(a) {
				c.distributeMessage(a.data)
			};
			a.onclose = function(a) {
				c.scriptSocketFlag && (g("#dgConnect") && (g("#dgConnect").value = "Connect"), console.error("Please check your network,client could not talk with server!"))
			}
		},
		scriptSocketFlag: 0,
		initSocket: function(a) {
			this.scriptSocketFlag = 0;
			var b = "ws://" + a.host + ":" + a.port;
			a.name && (b += "/" + a.name);
			var c = 0;
			try {
				1 == localStorage.scriptSocket ? c = 1 : r = new WebSocket(b, a.protocal)
			} catch (f) {
				console.log(f)
			}
			if (c || !r) r = new v(a), this.scriptSocketFlag = 1, 1 == localStorage.scriptSocket;
			this.initSocketMethod(r);
			if (1 != localStorage.scriptSocket) {
				var e = this;
				setTimeout(function() {
					e.scriptSocketFlag = 1;
					r && 1 != r.readyState && (localStorage.scriptSocket = 1, r = new v(a), e.initSocketMethod(r))
				}, 3E3)
			}
		}
	});
	(function() {
		/loaded|complete/.test(document.readyState) ? setTimeout(c.ready, 10) : setTimeout(arguments.callee, 10)
	})();
	c.inherit({});
	c.extend(n, {
		_doCacheJs: function(a, b) {
			var d = n.filterCon(b);
			d.length ? n.setPrototype(d) : (g('script[_src="' + a + '"]') && g('script[_src="' + a + '"]').remove(), g("#debuggapRoot").appendChild(c.createEle("script", {
				_src: a
			}, b)))
		},
		filterCon: function(a) {
			var b = 0,
				c = [];
			do {
				var f = a.indexOf("enyo.kind");
				if (-1 != f) {
					a = a.replace("enyo.kind", "");
					for (var e = [], g = b; g < a.length; g++) {
						var k = a.charCodeAt(g);
						if (123 == k) e.push(g);
						else if (125 == k) if (1 > e.length) {
							alert("remote debug error");
							break
						} else if (1 == e.length) {
							e.push(g);
							b = g + 1;
							break
						} else e.pop()
					}
					2 == e.length ? c.push(a.substring(e[0], e[1] + 1)) : alert("remote debug error")
				}
			} while (-1 != f);
			return c
		},
		setPrototype: function(a) {
			for (var b = 0; b < a.length; b++) {
				var d = (new Function("return " + a[b]))(),
					f = d.name ? d.name : d.kind ? d.kind : null;
				if (f) {
					var f = enyo.getPath(f),
						e;
					for (e in d) c.isFunction(d[e]) && (f.prototype[e] = d[e])
				}
			}
		}
	})
})();