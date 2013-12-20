Ext.onReady(function() {

	// chart tips css
	var css = '.dv-chart-tips { border-radius: 2px; padding: 0px 3px 1px; border: 2px solid #777; background-color: #f1f1f1; } \n';
	css += '.dv-chart-tips .x-tip-body { background-color: #f1f1f1; font-size: 13px; font-weight: normal; color: #444; -webkit-text-stroke: 0; } \n';
	css += '.dv-chart-tips .x-tip-body div { font-family: arial,sans-serif,ubuntu,consolas !important; } \n';

	// load mask css
	css += '.x-mask-msg { padding: 0; \n	border: 0 none; background-image: none; background-color: transparent; } \n';
	css += '.x-mask-msg div { background-position: 11px center; } \n';
	css += '.x-mask-msg .x-mask-loading { border: 0 none; \n background-color: #000; color: #fff; border-radius: 2px; padding: 12px 14px 12px 30px; opacity: 0.65; } \n';

	Ext.util.CSS.createStyleSheet(css);

	// i18n
	DV.i18n = {
		target: 'Target',
		base: 'Base',
		trend: 'Trend'
	};

    // plugin
    DV.plugin = {};

	var init = {},
		configs = [],
		isInitialized = false,
		getInit,
		execute;

	getInit = function(config) {
		var requests = [],
			callbacks = 0,
			fn;

		init.user = {};

		fn = function() {
			if (++callbacks === requests.length) {
				for (var i = 0; i < configs.length; i++) {
					execute(configs[i]);
				}
			}
		};

		requests.push({
			url: config.url + '/api/system/context.jsonp',
			success: function(r) {
				init.contextPath = r.contextPath;
				fn();
			}
		});

		requests.push({
			url: config.url + '/api/organisationUnits.jsonp?userOnly=true&viewClass=detailed&links=false',
			success: function(r) {
				var ou = r.organisationUnits[0];
				init.user.ou = ou.id;
				init.user.ouc = Ext.Array.pluck(ou.children, 'id');
				fn();
			}
		});

		requests.push({
			url: config.url + '/api/mapLegendSets.jsonp?viewClass=detailed&links=false&paging=false',
			success: function(r) {
				init.legendSets = r.mapLegendSets;
				fn();
			}
		});

		requests.push({
			url: config.url + '/api/dimensions.jsonp?links=false&paging=false',
			success: function(r) {
				init.dimensions = r.dimensions;
				fn();
			}
		});

		for (var i = 0; i < requests.length; i++) {
			Ext.data.JsonP.request(requests[i]);
		}
	};

	execute = function(config) {
		var validateConfig,
            extendInstance,
			createViewport,
			initialize,
			dv;

		validateConfig = function(config) {
			if (!Ext.isObject(config)) {
				console.log('Chart configuration is not an object');
				return;
			}

			if (!Ext.isString(config.el)) {
				console.log('No element id provided');
				return;
			}

			config.id = config.id || config.uid;

			return true;
		};

        extendInstance = function(dv) {
            var util = dv.util || {},
                init = dv.init || {};

            init.el = config.el;
		};

		createViewport = function() {
			var el = Ext.get(dv.init.el),
				setFavorite,
				centerRegion,
				elBorderW = parseInt(el.getStyle('border-left-width')) + parseInt(el.getStyle('border-right-width')),
				elBorderH = parseInt(el.getStyle('border-top-width')) + parseInt(el.getStyle('border-bottom-width')),
				elPaddingW = parseInt(el.getStyle('padding-left')) + parseInt(el.getStyle('padding-right')),
				elPaddingH = parseInt(el.getStyle('padding-top')) + parseInt(el.getStyle('padding-bottom')),
				width = el.getWidth() - elBorderW - elPaddingW,
				height = el.getHeight() - elBorderH - elPaddingH;

			setFavorite = function(layout) {
				dv.engine.createChart(layout, dv);
			};

			centerRegion = Ext.create('Ext.panel.Panel', {
				renderTo: el,
				bodyStyle: 'border: 0 none',
				width: config.width || width,
				height: config.height || height,
				layout: 'fit'
			});

			return {
				setFavorite: setFavorite,
				centerRegion: centerRegion
			};
		};

		initialize = function() {
			if (!validateConfig(config)) {
				return;
			}

			dv = DV.core.getInstance(Ext.clone(init));
			extendInstance(dv);

			dv.isPlugin = true;
			dv.viewport = createViewport();

			if (config.id) {
				dv.engine.loadChart(config.id, dv);
			}
			else {
				layout = dv.api.layout.Layout(config);

				if (!layout) {
					return;
				}

				dv.engine.createChart(layout, dv);
			}
		}();
	};

	DV.plugin.getChart = function(config) {
		if (Ext.isString(config.url) && config.url.split('').pop() === '/') {
			config.url = config.url.substr(0, config.url.length - 1);
		}

		configs.push(config);

		if (!isInitialized) {
			isInitialized = true;
			getInit(config);
		}
	};
});
