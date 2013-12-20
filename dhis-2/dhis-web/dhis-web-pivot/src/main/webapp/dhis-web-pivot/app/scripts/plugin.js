Ext.onReady(function() {

	// css
	css = 'table.pivot { \n font-family: arial,sans-serif,ubuntu,consolas; \n } \n';
	css += '.td-nobreak { \n white-space: nowrap; \n } \n';
	css += '.td-hidden { \n display: none; \n } \n';
	css += '.td-collapsed { \n display: none; \n } \n';
	css += 'table.pivot { \n border-collapse: collapse; \n border-spacing: 0px; \n border: 0 none; \n } \n';
	css += '.pivot td { \n padding: 5px; \n border: \n 1px solid #b2b2b2; \n } \n';
	css += '.pivot-dim { \n background-color: #dae6f8; \n text-align: center; \n } \n';
	css += '.pivot-dim.highlighted { \n	background-color: #c5d8f6; \n } \n';
	css += '.pivot-dim-subtotal { \n background-color: #cad6e8; \n text-align: center; \n } \n';
	css += '.pivot-dim-total { \n background-color: #bac6d8; \n text-align: center; \n } \n';
	css += '.pivot-dim-empty { \n background-color: #dae6f8; \n text-align: center; \n } \n';
	css += '.pivot-value { \n background-color: #fff; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-subtotal { \n background-color: #f4f4f4; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-subtotal-total { \n background-color: #e7e7e7; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-total { \n background-color: #e4e4e4; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-total-subgrandtotal { \n background-color: #d8d8d8; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-grandtotal { \n background-color: #c8c8c8; \n white-space: nowrap; \n text-align: right; \n } \n';

	css += '.x-mask-msg { \n padding: 0; \n	border: 0 none; \n background-image: none; \n background-color: transparent; \n } \n';
	css += '.x-mask-msg div { \n background-position: 11px center; \n } \n';
	css += '.x-mask-msg .x-mask-loading { \n border: 0 none; \n	background-color: #000; \n color: #fff; \n border-radius: 2px; \n padding: 12px 14px 12px 30px; \n opacity: 0.65; \n } \n';

	css += '.pivot td.legend { \n padding: 0; \n } \n';
	css += '.pivot div.legendCt { \n display: table; \n float: right; \n width: 100%; \n } \n';
	css += '.pivot div.arrowCt { \n display: table-cell; \n vertical-align: top; \n width: 8px; \n } \n';
	css += '.pivot div.arrow { \n width: 0; \n height: 0; \n } \n';
	css += '.pivot div.number { \n display: table-cell; \n } \n',
	css += '.pivot div.legendColor { \n display: table-cell; \n width: 2px; \n } \n';

	Ext.util.CSS.createStyleSheet(css);

	// plugin
	PT.plugin = {};

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
			pt;

		validateConfig = function(config) {
			if (!Ext.isObject(config)) {
				console.log('Report table configuration is not an object');
				return;
			}

			if (!Ext.isString(config.el)) {
				console.log('No valid element id provided');
				return;
			}

			if (!Ext.isString(config.url)) {
				console.log('No valid url provided');
				return;
			}

			config.id = config.id || config.uid;

			return true;
		};

        extendInstance = function(pt) {
            var util = pt.util || {},
                init = pt.init || {};

            init.el = config.el;

            util.message = {
                alert: function() {
                    console.log(pt.init.el + ': ' + message);
                }
            };
        };

		createViewport = function() {
			var setFavorite,
				centerRegion;

			setFavorite = function(layout) {
				pt.engine.createTable(layout, pt);
			};

			centerRegion = Ext.create('Ext.panel.Panel', {
				renderTo: Ext.get(pt.init.el),
				bodyStyle: 'border: 0 none',
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

			pt = PT.core.getInstance(Ext.clone(init));
			extendInstance(pt);

			pt.viewport = createViewport();
			pt.isPlugin = true;

			if (config.id) {
				pt.engine.loadTable(config.id, pt);
			}
			else {
				layout = pt.api.layout.Layout(config);

				if (!layout) {
					return;
				}

				pt.engine.createTable(layout, pt);
			}
		}();
	};

	PT.plugin.getTable = function(config) {
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
