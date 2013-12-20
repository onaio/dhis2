Ext.onReady( function() {
	var init = {},
		configs = [],
		isInitialized = false,
		getInit,
		applyCss,
		execute;

	GIS.i18n = {
		facility_layer_legend: 'Facility layer legend',
		thematic_layer_1_legend: 'Thematic layer 1 legend',
		thematic_layer_2_legend: 'Thematic layer 2 legend',
		thematic_layer_3_legend: 'Thematic layer 3 legend',
		thematic_layer_4_legend: 'Thematic layer 4 legend',
		measure_distance: 'Measure distance'
	};

	GIS.plugin = {};

	getInit = function(config) {
		var requests = [],
			callbacks = 0,
			fn;

		init.user = {};

		fn = function() {
			applyCss();

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

	applyCss = function() {
		var css = '.gis-plugin, .gis-plugin * { font-family: arial, sans-serif, liberation sans, consolas; } \n';
		css += '.x-panel-body { font-size: 11px; } \n';
		css += '.x-panel-header { height: 30px; padding: 7px 4px 4px 7px; border: 0 none; } \n';
		css += '.gis-container-default .x-window-body { padding: 5px; background: #fff; } \n';
		css += '.olControlPanel { position: absolute; top: 0; right: 0; border: 0 none; } \n';
		css += '.olControlButtonItemActive { background: #556; color: #fff; width: 24px; height: 24px; opacity: 0.75; filter: alpha(opacity=75); -ms-filter: "alpha(opacity=75)"; cursor: pointer; cursor: hand; text-align: center; font-size: 21px !important; text-shadow: 0 0 1px #ddd; } \n';
		css += '.olControlPanel.zoomIn { right: 72px; } \n';
		css += '.olControlPanel.zoomIn .olControlButtonItemActive { border-bottom-left-radius: 2px; } \n';
		css += '.olControlPanel.zoomOut { right: 48px; } \n';
		css += '.olControlPanel.zoomVisible { right: 24px; } \n';
		css += '.olControlPermalink { display: none !important; } \n';
		css += '.olControlMousePosition { background: #fff !important; opacity: 0.8 !important; filter: alpha(opacity=80) !important; -ms-filter: "alpha(opacity=80)" !important; right: 0 !important; bottom: 0 !important; border-top-left-radius: 2px !important; padding: 2px 2px 2px 5px !important; color: #000 !important; -webkit-text-stroke-width: 0.2px; -webkit-text-stroke-color: #555; } \n';
		css += '.olControlMousePosition * { font-size: 10px !important; } \n';
		css += '.text-mouseposition-lonlat { color: #555; } \n';
		css += '.olLayerGoogleCopyright, .olLayerGoogleV3.olLayerGooglePoweredBy { display: none; } \n';
		css += '#google-logo { background: url("' + init.contextPath + '/dhis-web-mapping/app/images/google-logo.png") no-repeat; width: 40px; height: 13px; margin-left: 6px; display: inline-block; vertical-align: bottom; cursor: pointer; cursor: hand; } \n';
		css += '.olControlScaleLine { left: 5px !important; bottom: 5px !important; } \n';
		css += '.olControlScaleLineBottom { display: none; } \n';
		css += '.olControlScaleLineTop { font-weight: bold; } \n';
		css += '.x-mask-msg { padding: 0; border: 0 none; background-image: none; background-color: transparent; } \n';
		css += '.x-mask-msg div { background-position: 11px center; } \n';
		css += '.x-mask-msg .x-mask-loading { border: 0 none; background-color: #000; color: #fff; border-radius: 2px; padding: 12px 14px 12px 30px; opacity: 0.65; } \n';
		css += '.gis-window-widget-feature { padding: 0; border: 0 none; border-radius: 0; background: transparent; box-shadow: none; } \n';
		css += '.gis-window-widget-feature .x-window-body-default { border: 0 none; background: transparent; } \n';
		css += '.gis-window-widget-feature .x-window-body-default .x-panel-body-default { border: 0 none; background: #556; opacity: 0.92; filter: alpha(opacity=92); -ms-filter: "alpha(opacity=92)"; padding: 5px 8px 5px 8px; border-bottom-left-radius: 2px; border-bottom-right-radius: 2px; color: #fff; font-weight: bold; letter-spacing: 1px; } \n';
		css += '.x-menu-body { border:1px solid #bbb; border-radius: 2px; padding: 0; background-color: #fff !important; } \n';
		css += '.x-menu-item-active .x-menu-item-link {	border-radius: 0; border-color: #e1e1e1; background-color: #e1e1e1; background-image: none; } \n';
		css += '.x-menu-item-link { padding: 4px 5px 4px 26px; } \n';
		css += '.x-menu-item-text { color: #111; } \n';
		css += '.disabled { opacity: 0.4; cursor: default !important; } \n';
		css += '.el-opacity-1 { opacity: 1 !important; } \n';
		css += '.el-border-0, .el-border-0 .x-panel-body { border: 0 none !important; } \n';
		css += '.el-fontsize-10 { font-size: 10px !important; } \n';
		css += '.gis-grid-row-icon-disabled * { cursor: default !important; } \n';
		css += '.gis-toolbar-btn-menu { margin-top: 4px; } \n';
		css += '.gis-toolbar-btn-menu .x-panel-body-default { border-radius: 2px; border-color: #999; } \n';
		css += '.gis-grid .link, .gis-grid .link * { cursor: pointer; cursor: hand; color: blue; text-decoration: underline; } \n';
		css += '.gis-menu-item-icon-drill, .gis-menu-item-icon-float { left: 6px; } \n';
		css += '.gis-menu-item-first.x-menu-item-active .x-menu-item-link {	border-radius: 0; border-top-left-radius: 2px; border-top-right-radius: 2px; } \n';
		css += '.gis-menu-item-last.x-menu-item-active .x-menu-item-link { border-radius: 0; border-bottom-left-radius: 2px; border-bottom-right-radius: 2px; } \n';
		css += '.gis-menu-item-icon-drill { \n background: url("' + init.contextPath + '/dhis-web-mapping/app/images/drill_16.png") no-repeat; } \n';
		css += '.gis-menu-item-icon-float { background: url("' + init.contextPath + '/dhis-web-mapping/app/images/float_16.png") no-repeat; } \n';
		css += '.x-color-picker a { padding: 0; } \n';
		css += '.x-color-picker em span { width: 14px; height: 14px; } \n';

		Ext.util.CSS.createStyleSheet(css);
	};

	execute = function(config) {
		var validateConfig,
			createViewport,
			afterRender,
			initialize,
			gis;

		validateConfig = function() {
			if (!Ext.isString(config.url)) {
				alert('Invalid url (' + config.el + ')');
				return;
			}

			if (config.url.split('').pop() === '/') {
				config.url = config.url.substr(0, config.url.length - 1);
			}

			if (!Ext.isString(config.el)) {
				alert('Invalid html element id (' + config.el + ')');
				return;
			}

			config.id = config.id || config.uid;

			if (config.id && !Ext.isString(config.id)) {
				alert('Invalid map id (' + config.el + ')');
				return;
			}

			return true;
		};

		createViewport = function() {
			var viewport,
				eastRegion,
				centerRegion,
				el = Ext.get(gis.el);

			viewport = Ext.create('Ext.panel.Panel', {
				renderTo: el,
				width: el.getWidth(),
				height: el.getHeight(),
				cls: 'gis-plugin',
				layout: {
					type: 'hbox',
					align: 'stretch'
				},
				items: [
					{
						xtype: 'gx_mappanel',
						map: gis.olmap,
						bodyStyle: 'border:0 none',
						width: el.getWidth() - 200,
						height: el.getHeight(),
						listeners: {
							added: function() {
								centerRegion = this;
							}
						}
					},
					{
						xtype: 'panel',
						layout: 'anchor',
						bodyStyle: 'border-top:0 none; border-bottom:0 none',
						width: 200,
						preventHeader: true,
						defaults: {
							bodyStyle: 'padding: 6px; border: 0 none',
							collapsible: true,
							collapsed: true,
							animCollapse: false
						},
						items: [
							{
								title: GIS.i18n.thematic_layer_1_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic1.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.thematic_layer_2_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic2.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.thematic_layer_3_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic3.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.thematic_layer_4_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic4.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.facility_layer_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.facility.legendPanel = this;
									}
								}
							}
						],
						listeners: {
							added: function() {
								eastRegion = this;
							}
						}
					}
				],
				listeners: {
					afterrender: function() {
						afterRender();
					}
				}
			});

			viewport.centerRegion = centerRegion;
			viewport.eastRegion = eastRegion;

			return viewport;
		};

		afterRender = function(vp) {
			var len = Ext.query('.zoomInButton').length;

			for (var i = 0; i < len; i++) {
				Ext.query('.zoomInButton')[i].innerHTML = '<img src="' + gis.init.contextPath + '/dhis-web-mapping/app/images/zoomin_24.png" />';
				Ext.query('.zoomOutButton')[i].innerHTML = '<img src="' + gis.init.contextPath + '/dhis-web-mapping/app/images/zoomout_24.png" />';
				Ext.query('.zoomVisibleButton')[i].innerHTML = '<img src="' + gis.init.contextPath + '/dhis-web-mapping/app/images/zoomvisible_24.png" />';
				Ext.query('.measureButton')[i].innerHTML = '<img src="' + gis.init.contextPath + '/dhis-web-mapping/app/images/measure_24.png" />';
			}
		};

		initialize = function() {
			if (!validateConfig()) {
				return;
			}

			gis = GIS.core.getInstance(init);
			gis.el = config.el;

			GIS.core.createSelectHandlers(gis, gis.layer.boundary);
			GIS.core.createSelectHandlers(gis, gis.layer.thematic1);
			GIS.core.createSelectHandlers(gis, gis.layer.thematic2);
			GIS.core.createSelectHandlers(gis, gis.layer.thematic3);
			GIS.core.createSelectHandlers(gis, gis.layer.thematic4);
			GIS.core.createSelectHandlers(gis, gis.layer.facility);

			gis.map = config;

			gis.viewport = createViewport();

			gis.olmap.mask = Ext.create('Ext.LoadMask', gis.viewport.centerRegion.getEl(), {
				msg: 'Loading'
			});

			GIS.core.MapLoader(gis).load();
		}();
	};

	GIS.plugin.getMap = function(config) {
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
