Ext.onReady( function() {
	var createViewport,
		initialize,
		pt;

	PT.app = {};

	PT.app.extendInstance = function(pt) {
        var init = pt.init,
            conf = pt.conf,
            util = pt.util,
            api = pt.api,
            engine = pt.engine,
            store = {},
            cmp = {},
            dimConf = conf.finals.dimension;

        pt.init.el = 'app';

		// util
		(function() {
			util.dimension = {
				panel: {
					setHeight: function(mx) {
						var panelHeight = pt.cmp.dimension.panels.length * 28,
							height;

						if (pt.viewport.westRegion.hasScrollbar) {
							height = panelHeight + mx;
							pt.viewport.accordion.setHeight(pt.viewport.getHeight() - 2);
							pt.viewport.accordionBody.setHeight(height - 2);
						}
						else {
							height = pt.viewport.westRegion.getHeight() - conf.layout.west_fill;
							mx += panelHeight;
							pt.viewport.accordion.setHeight((height > mx ? mx : height) - 2);
							pt.viewport.accordionBody.setHeight((height > mx ? mx : height) - 2);
						}
					},

					getExpanded: function() {
						for (var i = 0, panel; i < pt.cmp.dimension.panels.length; i++) {
							panel = pt.cmp.dimension.panels[i];

							if (!panel.collapsed) {
								return panel;
							}
						}

						return null;
					}
				}
			};

			util.checkbox = {
				setAllFalse: function() {
					var a = cmp.dimension.relativePeriod.checkbox;
					for (var i = 0; i < a.length; i++) {
						a[i].setValue(false);
					}
				},
				isAllFalse: function() {
					var a = cmp.dimension.relativePeriod.checkbox;
					for (var i = 0; i < a.length; i++) {
						if (a[i].getValue()) {
							return false;
						}
					}
					return true;
				}
			};

			util.multiselect = {
				select: function(a, s) {
					var selected = a.getValue();
					if (selected.length) {
						var array = [];
						Ext.Array.each(selected, function(item) {
							array.push({id: item, name: a.store.getAt(a.store.findExact('id', item)).data.name});
						});
						s.store.add(array);
					}
					this.filterAvailable(a, s);
				},
				selectAll: function(a, s, doReverse) {
					var array = [];
					a.store.each( function(r) {
						array.push({id: r.data.id, name: r.data.name});
					});
					if (doReverse) {
						array.reverse();
					}
					s.store.add(array);
					this.filterAvailable(a, s);
				},
				unselect: function(a, s) {
					var selected = s.getValue();
					if (selected.length) {
						Ext.Array.each(selected, function(item) {
							s.store.remove(s.store.getAt(s.store.findExact('id', item)));
						});
						this.filterAvailable(a, s);
					}
				},
				unselectAll: function(a, s) {
					s.store.removeAll();
					a.store.clearFilter();
					this.filterAvailable(a, s);
				},
				filterAvailable: function(a, s) {
					a.store.filterBy( function(r) {
						var keep = true;
						s.store.each( function(r2) {
							if (r.data.id == r2.data.id) {
								keep = false;
							}

						});
						return keep;
					});
					a.store.sortStore();
				},
				setHeight: function(ms, panel, fill) {
					for (var i = 0; i < ms.length; i++) {
						ms[i].setHeight(panel.getHeight() - fill);
					}
				}
			};

			util.url = {
				getUrlParam: function(s) {
					var output = '';
					var href = window.location.href;
					if (href.indexOf('?') > -1 ) {
						var query = href.substr(href.indexOf('?') + 1);
						var query = query.split('&');
						for (var i = 0; i < query.length; i++) {
							if (query[i].indexOf('=') > -1) {
								var a = query[i].split('=');
								if (a[0].toLowerCase() === s) {
									output = a[1];
									break;
								}
							}
						}
					}
					return unescape(output);
				}
			};

			util.window = util.window || {};

			util.window.setAnchorPosition = function(w, target) {
				var vpw = pt.viewport.getWidth(),
					targetx = target ? target.getPosition()[0] : 4,
					winw = w.getWidth(),
					y = target ? target.getPosition()[1] + target.getHeight() + 4 : 33;

				if ((targetx + winw) > vpw) {
					w.setPosition((vpw - winw - 2), y);
				}
				else {
					w.setPosition(targetx, y);
				}
			};

			util.window.addHideOnBlurHandler = function(w) {
				var el = Ext.get(Ext.query('.x-mask')[0]);

				el.on('click', function() {
					if (w.hideOnBlur) {
						w.hide();
					}
				});

				w.hasHideOnBlurHandler = true;
			};

			util.window.addDestroyOnBlurHandler = function(w) {
				var el = Ext.get(Ext.query('.x-mask')[0]);

				el.on('click', function() {
					if (w.destroyOnBlur) {
						w.destroy();
					}
				});

				w.hasDestroyOnBlurHandler = true;
			};

			util.message = {
				alert: function(message) {
					alert(message);
				}
			}
		}());

        // init
        (function() {

			// root nodes
			for (var i = 0; i < init.rootNodes.length; i++) {
				init.rootNodes[i].path = '/' + conf.finals.root.id + '/' + init.rootNodes[i].id;
			}

			// viewport afterrender
			init.afterRender = function() {

				// Resize event handler
				pt.viewport.westRegion.on('resize', function() {
					var panel = util.dimension.panel.getExpanded();

					if (panel) {
						panel.onExpand();
					}
				});

				// Left gui
				var viewportHeight = pt.viewport.westRegion.getHeight(),
					numberOfTabs = init.dimensions.length + 5,
					tabHeight = 28,
					minPeriodHeight = 380;

				if (viewportHeight > numberOfTabs * tabHeight + minPeriodHeight) {
					if (!Ext.isIE) {
						pt.viewport.accordion.setAutoScroll(false);
						pt.viewport.westRegion.setWidth(conf.layout.west_width);
						pt.viewport.accordion.doLayout();
					}
				}
				else {
					pt.viewport.westRegion.hasScrollbar = true;
				}

                // Expand first panel
				cmp.dimension.panels[0].expand();

                // Look for url params
				var id = util.url.getUrlParam('id'),
					session = util.url.getUrlParam('s'),
					layout;

				if (id) {
					engine.loadTable(id, pt, true, true);
				}
				else if (Ext.isString(session) && PT.isSessionStorage && Ext.isObject(JSON.parse(sessionStorage.getItem('dhis2'))) && session in JSON.parse(sessionStorage.getItem('dhis2'))) {
					layout = api.layout.Layout(JSON.parse(sessionStorage.getItem('dhis2'))[session]);

					if (layout) {
						pt.engine.createTable(layout, pt, true);
					}
				}

				// Fade in
				Ext.defer( function() {
					Ext.getBody().fadeIn({
						duration: 300
					});
				}, 400 );
			};
		}());

		// store
		(function() {
			store.indicatorAvailable = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				proxy: {
					type: 'ajax',
					reader: {
						type: 'json',
						root: 'indicators'
					}
				},
				storage: {},
				sortStore: function() {
					this.sort('name', 'ASC');
				},
				listeners: {
					load: function(s) {
						util.store.addToStorage(s);
						util.multiselect.filterAvailable({store: s}, {store: store.indicatorSelected});
					}
				}
			});

			store.indicatorSelected = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: []
			});

			store.dataElementAvailable = Ext.create('Ext.data.Store', {
				fields: ['id', 'name', 'dataElementId', 'optionComboId', 'operandName'],
				proxy: {
					type: 'ajax',
					reader: {
						type: 'json',
						root: 'dataElements'
					}
				},
				storage: {},
				sortStore: function() {
					this.sort('name', 'ASC');
				},
				setTotalsProxy: function(uid) {
					var path;

					if (Ext.isString(uid)) {
						path = conf.finals.url.dataelement_get + uid + '.json?links=false&paging=false';
					}
					else if (uid === 0) {
						path = conf.finals.url.dataelement_getall;
					}

					if (!path) {
						alert('Invalid parameter');
						return;
					}

					this.setProxy({
						type: 'ajax',
						url: init.contextPath + conf.finals.url.path_api + path,
						reader: {
							type: 'json',
							root: 'dataElements'
						}
					});

					this.load({
						scope: this,
						callback: function() {
							util.multiselect.filterAvailable({store: this}, {store: store.dataElementSelected});
						}
					});
				},
				setDetailsProxy: function(uid) {
					if (Ext.isString(uid)) {
						this.setProxy({
							type: 'ajax',
							url: init.contextPath + conf.finals.url.path_commons + 'getOperands.action?uid=' + uid,
							reader: {
								type: 'json',
								root: 'operands'
							}
						});

						this.load({
							scope: this,
							callback: function() {
								this.each(function(r) {
									r.set('id', r.data.dataElementId + '-' + r.data.optionComboId);
									r.set('name', r.data.operandName);
								});

								util.multiselect.filterAvailable({store: this}, {store: store.dataElementSelected});
							}
						});
					}
					else {
						alert('Invalid parameter');
					}
				},
				listeners: {
					load: function(s) {
						util.store.addToStorage(s);
						util.multiselect.filterAvailable({store: s}, {store: store.dataElementSelected});
					}
				}
			});

			store.dataElementSelected = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: []
			});

			store.dataSetAvailable = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				proxy: {
					type: 'ajax',
					url: init.contextPath + conf.finals.url.path_api + conf.finals.url.dataset_get,
					reader: {
						type: 'json',
						root: 'dataSets'
					}
				},
				storage: {},
				sortStore: function() {
					this.sort('name', 'ASC');
				},
				isLoaded: false,
				listeners: {
					load: function(s) {
						this.isLoaded = true;

						util.store.addToStorage(s);
						util.multiselect.filterAvailable({store: s}, {store: store.dataSetSelected});
					}
				}
			});

			store.dataSetSelected = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: []
			});

			store.periodType = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: conf.period.periodTypes
			});

			store.fixedPeriodAvailable = Ext.create('Ext.data.Store', {
				fields: ['id', 'name', 'index'],
				data: [],
				setIndex: function(periods) {
					for (var i = 0; i < periods.length; i++) {
						periods[i].index = i;
					}
				},
				sortStore: function() {
					this.sort('index', 'ASC');
				}
			});

			store.fixedPeriodSelected = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: []
			});

			store.reportTable = Ext.create('Ext.data.Store', {
				fields: ['id', 'name', 'lastUpdated', 'access'],
				proxy: {
					type: 'ajax',
					reader: {
						type: 'json',
						root: 'reportTables'
					}
				},
				isLoaded: false,
				pageSize: 10,
				page: 1,
				defaultUrl: init.contextPath + '/api/reportTables.json?viewClass=sharing&links=false',
				loadStore: function(url) {
					this.proxy.url = url || this.defaultUrl;

					this.load({
						params: {
							pageSize: this.pageSize,
							page: this.page
						}
					});
				},
				loadFn: function(fn) {
					if (this.isLoaded) {
						fn.call();
					}
					else {
						this.load(fn);
					}
				},
				listeners: {
					load: function(s) {
						if (!this.isLoaded) {
							this.isLoaded = true;
						}

						this.sort('name', 'ASC');
					}
				}
			});

			store.organisationUnitGroup = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				proxy: {
					type: 'ajax',
					url: init.contextPath + conf.finals.url.path_api + conf.finals.url.organisationunitgroup_getall,
					reader: {
						type: 'json',
						root: 'organisationUnitGroups'
					}
				}
			});

			store.legendSet = Ext.create('Ext.data.Store', {
				fields: ['id', 'name', 'index'],
				data: function() {
					var data = init.legendSets;
					data.unshift({id: 0, name: PT.i18n.none, index: -1});
					return data;
				}(),
				sorters: [
					{property: 'index', direction: 'ASC'},
					{property: 'name', direction: 'ASC'}
				]
			});

			pt.store = store;
		}());

		// cmp
		(function() {
			cmp = {
				dimension: {
					panels: [],

					indicator: {},
					dataElement: {},
					dataSet: {},
					relativePeriod: {
						checkbox: []
					},
					fixedPeriod: {},
					organisationUnit: {}
				},
				favorite: {}
			};

			pt.cmp = cmp;
		}());

		// engine
		(function() {
			engine.getLayoutConfig = function() {
				var panels = pt.cmp.dimension.panels,
					columnDimNames = pt.viewport.colStore.getDimensionNames(),
					rowDimNames = pt.viewport.rowStore.getDimensionNames(),
					filterDimNames = pt.viewport.filterStore.getDimensionNames(),
					config = pt.viewport.optionsWindow.getOptions(),
					dx = dimConf.data.dimensionName,
					co = dimConf.category.dimensionName,
					nameDimArrayMap = {};

				config.columns = [];
				config.rows = [];
				config.filters = [];

				// Panel data
				for (var i = 0, dim, dimName; i < panels.length; i++) {
					dim = panels[i].getDimension();

					if (dim) {
						nameDimArrayMap[dim.dimension] = [dim];
					}
				}

				nameDimArrayMap[dx] = Ext.Array.clean([].concat(
					nameDimArrayMap[dimConf.indicator.objectName],
					nameDimArrayMap[dimConf.dataElement.objectName],
					nameDimArrayMap[dimConf.operand.objectName],
					nameDimArrayMap[dimConf.dataSet.objectName]
				));

				// Columns, rows, filters
				for (var i = 0, nameArrays = [columnDimNames, rowDimNames, filterDimNames], axes = [config.columns, config.rows, config.filters], dimNames; i < nameArrays.length; i++) {
					dimNames = nameArrays[i];

					for (var j = 0, dimName, dim; j < dimNames.length; j++) {
						dimName = dimNames[j];

						if (dimName === co) {
							axes[i].push({
								dimension: co,
								items: []
							});
						}
						else if (dimName === dx && nameDimArrayMap.hasOwnProperty(dimName) && nameDimArrayMap[dimName]) {
							for (var k = 0; k < nameDimArrayMap[dx].length; k++) {
								axes[i].push(Ext.clone(nameDimArrayMap[dx][k]));
							}
						}
						else if (nameDimArrayMap.hasOwnProperty(dimName) && nameDimArrayMap[dimName]) {
							for (var k = 0; k < nameDimArrayMap[dimName].length; k++) {
								axes[i].push(Ext.clone(nameDimArrayMap[dimName][k]));
							}
						}
					}
				}

				config.userOrganisationUnit = pt.viewport.userOrganisationUnit.getValue();
				config.userOrganisationUnitChildren = pt.viewport.userOrganisationUnitChildren.getValue();

				return config;
			};
		}());
	};

	PT.app.LayoutWindow = function() {
		var dimension,
			dimensionStore,
			row,
			rowStore,
			col,
			colStore,
			filter,
			filterStore,
			value,

			getData,
			getStore,
			getStoreKeys,
			getCmpHeight,
			getSetup,

			dimensionPanel,
			selectPanel,
			window,

			margin = 2,
			defaultWidth = 160,
			defaultHeight = 158,
			maxHeight = (pt.viewport.getHeight() - 100) / 2,

			dimConf = pt.conf.finals.dimension;

		getData = function(all) {
			var data = [];

			if (all) {
				data.push({id: dimConf.data.dimensionName, name: dimConf.data.name});
			}

			data.push({id: dimConf.category.dimensionName, name: dimConf.category.name});

			if (all) {
				data.push({id: dimConf.period.dimensionName, name: dimConf.period.name});
				data.push({id: dimConf.organisationUnit.dimensionName, name: dimConf.organisationUnit.name});
			}

			return data.concat(Ext.clone(pt.init.dimensions));
		};

		getStore = function(data) {
			var config = {};

			config.fields = ['id', 'name'];

			if (data) {
				config.data = data;
			}

			config.getDimensionNames = function() {
				var dimensionNames = [];

				this.each(function(r) {
					dimensionNames.push(r.data.id);
				});

				return Ext.clone(dimensionNames);
			};

			return Ext.create('Ext.data.Store', config);
		};

		getStoreKeys = function(store) {
			var keys = [],
				items = store.data.items;

			if (items) {
				for (var i = 0; i < items.length; i++) {
					keys.push(items[i].data.id);
				}
			}

			return keys;
		};

		dimensionStore = getStore(getData());
		dimensionStore.reset = function(all) {
			dimensionStore.removeAll();
			dimensionStore.add(getData(all));
		};
		pt.viewport.dimensionStore = dimensionStore;

		rowStore = getStore();
		pt.viewport.rowStore = rowStore;
		rowStore.add({id: dimConf.period.dimensionName, name: dimConf.period.name});

		colStore = getStore();
		pt.viewport.colStore = colStore;
		colStore.add({id: dimConf.data.dimensionName, name: dimConf.data.name});

		filterStore = getStore();
		pt.viewport.filterStore = filterStore;
		filterStore.add({id: dimConf.organisationUnit.dimensionName, name: dimConf.organisationUnit.name});

		getCmpHeight = function() {
			var size = dimensionStore.totalCount,
				expansion = 10,
				height = defaultHeight,
				diff;

			if (size > 10) {
				diff = size - 10;
				height += (diff * expansion);
			}

			height = height > maxHeight ? maxHeight : height;

			return height;
		};

		dimension = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: (getCmpHeight() * 2) + margin,
			style: 'margin-right:' + margin + 'px; margin-bottom:0px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			ddReorder: false,
			store: dimensionStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: PT.i18n.dimensions,
					cls: 'pt-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		row = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-bottom:0px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			store: rowStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: PT.i18n.row,
					cls: 'pt-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		col = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-bottom:' + margin + 'px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			store: colStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: PT.i18n.column,
					cls: 'pt-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		filter = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-right:' + margin + 'px; margin-bottom:' + margin + 'px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			store: filterStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: PT.i18n.filter,
					cls: 'pt-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		selectPanel = Ext.create('Ext.panel.Panel', {
			bodyStyle: 'border:0 none',
			items: [
				{
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						filter,
						col
					]
				},
				{
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						row
					]
				}
			]
		});

		getSetup = function() {
			return {
				col: getStoreKeys(colStore),
				row: getStoreKeys(rowStore),
				filter: getStoreKeys(filterStore)
			};
		};

		window = Ext.create('Ext.window.Window', {
			title: PT.i18n.table_layout,
			bodyStyle: 'background-color:#fff; padding:2px',
			closeAction: 'hide',
			autoShow: true,
			modal: true,
			resizable: false,
			getSetup: getSetup,
			dimensionStore: dimensionStore,
			rowStore: rowStore,
			colStore: colStore,
			filterStore: filterStore,
			hideOnBlur: true,
			items: {
				layout: 'column',
				bodyStyle: 'border:0 none',
				items: [
					dimension,
					selectPanel
				]
			},
			bbar: [
				'->',
				{
					text: PT.i18n.hide,
					listeners: {
						added: function(b) {
							b.on('click', function() {
								window.hide();
							});
						}
					}
				},
				{
					text: '<b>' + PT.i18n.update + '</b>',
					listeners: {
						added: function(b) {
							b.on('click', function() {
								pt.viewport.updateViewport();
								window.hide();
							});
						}
					}
				}
			],
			listeners: {
				show: function(w) {
					if (pt.viewport.layoutButton.rendered) {
						pt.util.window.setAnchorPosition(w, pt.viewport.layoutButton);

						if (!w.hasHideOnBlurHandler) {
							pt.util.window.addHideOnBlurHandler(w);
						}
					}
				}
			}
		});

		return window;
	};

	PT.app.OptionsWindow = function() {
		var showTotals,
			showSubTotals,
			hideEmptyRows,
			showHierarchy,
			digitGroupSeparator,
			displayDensity,
			fontSize,
			reportingPeriod,
			organisationUnit,
			parentOrganisationUnit,

			data,
			style,
			parameters,

			comboboxWidth = 262,
			window;

		showTotals = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: PT.i18n.show_totals,
			style: 'margin-bottom:4px',
			checked: true
		});
		pt.viewport.showTotals = showTotals;

		showSubTotals = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: PT.i18n.show_subtotals,
			style: 'margin-bottom:4px',
			checked: true
		});
		pt.viewport.showSubTotals = showSubTotals;

		hideEmptyRows = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: PT.i18n.hide_empty_rows,
			style: 'margin-bottom:4px'
		});
		pt.viewport.hideEmptyRows = hideEmptyRows;

		showHierarchy = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: PT.i18n.show_hierarchy,
			style: 'margin-bottom:4px'
		});
		pt.viewport.showHierarchy = showHierarchy;

		displayDensity = Ext.create('Ext.form.field.ComboBox', {
			cls: 'pt-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: PT.i18n.display_density,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 'normal',
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 'comfortable', text: PT.i18n.comfortable},
					{id: 'normal', text: PT.i18n.normal},
					{id: 'compact', text: PT.i18n.compact}
				]
			})
		});
		pt.viewport.displayDensity = displayDensity;

		fontSize = Ext.create('Ext.form.field.ComboBox', {
			cls: 'pt-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: PT.i18n.font_size,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 'normal',
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 'large', text: PT.i18n.large},
					{id: 'normal', text: PT.i18n.normal},
					{id: 'small', text: PT.i18n.small_}
				]
			})
		});
		pt.viewport.fontSize = fontSize;

		digitGroupSeparator = Ext.create('Ext.form.field.ComboBox', {
			labelStyle: 'color:#333',
			cls: 'pt-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: PT.i18n.digit_group_separator,
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 'space',
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 'comma', text: 'Comma'},
					{id: 'space', text: 'Space'},
					{id: 'none', text: 'None'}
				]
			})
		});
		pt.viewport.digitGroupSeparator = digitGroupSeparator;

		legendSet = Ext.create('Ext.form.field.ComboBox', {
			cls: 'pt-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: PT.i18n.legend_set,
			valueField: 'id',
			displayField: 'name',
			editable: false,
			value: 0,
			store: pt.store.legendSet
		});
		pt.viewport.legendSet = legendSet;

		reportingPeriod = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: PT.i18n.reporting_period,
			style: 'margin-bottom:4px',
		});
		pt.viewport.reportingPeriod = reportingPeriod;

		organisationUnit = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: PT.i18n.organisation_unit,
			style: 'margin-bottom:4px',
		});
		pt.viewport.organisationUnit = organisationUnit;

		parentOrganisationUnit = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: PT.i18n.parent_organisation_unit,
			style: 'margin-bottom:4px',
		});
		pt.viewport.parentOrganisationUnit = parentOrganisationUnit;

		regression = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: PT.i18n.include_regression,
			style: 'margin-bottom:4px',
		});
		pt.viewport.regression = regression;

		cumulative = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: PT.i18n.include_cumulative,
			style: 'margin-bottom:6px',
		});
		pt.viewport.cumulative = cumulative;

		sortOrder = Ext.create('Ext.form.field.ComboBox', {
			cls: 'pt-combo',
			style: 'margin-bottom:3px',
			width: 250,
			labelWidth: 130,
			fieldLabel: PT.i18n.sort_order,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 0,
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 0, text: PT.i18n.none},
					{id: 1, text: PT.i18n.low_to_high},
					{id: 2, text: PT.i18n.high_to_low}
				]
			})
		});
		pt.viewport.sortOrder = sortOrder;

		topLimit = Ext.create('Ext.form.field.ComboBox', {
			cls: 'pt-combo',
			style: 'margin-bottom:0',
			width: 250,
			labelWidth: 130,
			fieldLabel: PT.i18n.top_limit,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 0,
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 0, text: PT.i18n.none},
					{id: 5, text: 5},
					{id: 10, text: 10},
					{id: 20, text: 20},
					{id: 50, text: 50},
					{id: 100, text: 100}
				]
			})
		});
		pt.viewport.topLimit = topLimit;

		data = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				showTotals,
				showSubTotals,
				hideEmptyRows
			]
		};

		organisationUnits = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				showHierarchy
			]
		};

		style = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				displayDensity,
				fontSize,
				digitGroupSeparator,
				legendSet
			]
		};

		parameters = {
			bodyStyle: 'border:0 none; background:transparent',
			style: 'margin-left:14px',
			items: [
				reportingPeriod,
				organisationUnit,
				parentOrganisationUnit,
				regression,
				cumulative,
				sortOrder,
				topLimit
			]
		};

		window = Ext.create('Ext.window.Window', {
			title: PT.i18n.table_options,
			bodyStyle: 'background-color:#fff; padding:5px',
			closeAction: 'hide',
			autoShow: true,
			modal: true,
			resizable: false,
			hideOnBlur: true,
			getOptions: function() {
				return {
					showTotals: showTotals.getValue(),
					showSubTotals: showSubTotals.getValue(),
					hideEmptyRows: hideEmptyRows.getValue(),
					showHierarchy: showHierarchy.getValue(),
					displayDensity: displayDensity.getValue(),
					fontSize: fontSize.getValue(),
					digitGroupSeparator: digitGroupSeparator.getValue(),
					legendSet: {id: legendSet.getValue()},
					reportingPeriod: reportingPeriod.getValue(),
					organisationUnit: organisationUnit.getValue(),
					parentOrganisationUnit: parentOrganisationUnit.getValue(),
					regression: regression.getValue(),
					cumulative: cumulative.getValue(),
					sortOrder: sortOrder.getValue(),
					topLimit: topLimit.getValue()
				};
			},
			setOptions: function(layout) {
				showTotals.setValue(Ext.isBoolean(layout.showTotals) ? layout.showTotals : true);
				showSubTotals.setValue(Ext.isBoolean(layout.showSubTotals) ? layout.showSubTotals : true);
				hideEmptyRows.setValue(Ext.isBoolean(layout.hideEmptyRows) ? layout.hideEmptyRows : false);
				showHierarchy.setValue(Ext.isBoolean(layout.showHierarchy) ? layout.showHierarchy : false);
				displayDensity.setValue(Ext.isString(layout.displayDensity) ? layout.displayDensity : 'normal');
				fontSize.setValue(Ext.isString(layout.fontSize) ? layout.fontSize : 'normal');
				digitGroupSeparator.setValue(Ext.isString(layout.digitGroupSeparator) ? layout.digitGroupSeparator : 'space');
				legendSet.setValue(Ext.isObject(layout.legendSet) && Ext.isString(layout.legendSet.id) ? layout.legendSet.id : 0);
				reportingPeriod.setValue(Ext.isBoolean(layout.reportingPeriod) ? layout.reportingPeriod : false);
				organisationUnit.setValue(Ext.isBoolean(layout.organisationUnit) ? layout.organisationUnit : false);
				parentOrganisationUnit.setValue(Ext.isBoolean(layout.parentOrganisationUnit) ? layout.parentOrganisationUnit : false);
				regression.setValue(Ext.isBoolean(layout.regression) ? layout.regression : false);
				cumulative.setValue(Ext.isBoolean(layout.cumulative) ? layout.cumulative : false);
				sortOrder.setValue(Ext.isNumber(layout.sortOrder) ? layout.sortOrder : 0);
				topLimit.setValue(Ext.isNumber(layout.topLimit) ? layout.topLimit : 0);
			},
			items: [
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px; margin-left:2px',
					html: PT.i18n.data
				},
				data,
				{
					bodyStyle: 'border:0 none; padding:7px'
				},
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px; margin-left:2px',
					html: PT.i18n.organisation_units
				},
				organisationUnits,
				{
					bodyStyle: 'border:0 none; padding:7px'
				},
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px; margin-left:2px',
					html: PT.i18n.style
				},
				style,
				{
					bodyStyle: 'border:0 none; padding:4px'
				},
				{
					bodyStyle: 'border:1px solid #d5d5d5; padding:5px; background-color:#f0f0f0',
					items: [
						{
							bodyStyle: 'border:0 none; padding:0 5px 6px 2px; background-color:transparent; color:#222; font-size:12px',
							html: '<b>' + PT.i18n.parameters + '</b> <span style="font-size:11px"> (' + PT.i18n.for_standard_reports_only + ')</span>'
						},
						parameters
					]
				}
			],
			bbar: [
				'->',
				{
					text: PT.i18n.hide,
					handler: function() {
						window.hide();
					}
				},
				{
					text: '<b>' + PT.i18n.update + '</b>',
					handler: function() {
						pt.viewport.updateViewport();
						window.hide();
					}
				}
			],
			listeners: {
				show: function(w) {
					if (pt.viewport.optionsButton.rendered) {
						pt.util.window.setAnchorPosition(w, pt.viewport.optionsButton);

						if (!w.hasHideOnBlurHandler) {
							pt.util.window.addHideOnBlurHandler(w);
						}
					}

					if (!legendSet.store.isLoaded) {
						legendSet.store.load();
					}
				}
			}
		});

		return window;
	};

	PT.app.FavoriteWindow = function() {

		// Objects
		var NameWindow,

		// Instances
			nameWindow,

		// Functions
			getBody,

		// Components
			addButton,
			searchTextfield,
			grid,
			prevButton,
			nextButton,
			tbar,
			bbar,
			info,
			nameTextfield,
			createButton,
			updateButton,
			cancelButton,
			favoriteWindow,

		// Vars
			windowWidth = 500,
			windowCmpWidth = windowWidth - 22;

		pt.store.reportTable.on('load', function(store, records) {
			var pager = store.proxy.reader.jsonData.pager;

			info.setText('Page ' + pager.page + ' of ' + pager.pageCount);

			prevButton.enable();
			nextButton.enable();

			if (pager.page === 1) {
				prevButton.disable();
			}

			if (pager.page === pager.pageCount) {
				nextButton.disable();
			}
		});

		getBody = function() {
			var favorite,
				dimensions;

			if (pt.layout) {
				favorite = Ext.clone(pt.layout);
				dimensions = [].concat(favorite.columns || [], favorite.rows || [], favorite.filters || []);

				// Server sync
				favorite.totals = favorite.showTotals;
				delete favorite.showTotals;

				favorite.subtotals = favorite.showSubTotals;
				delete favorite.showSubTotals;

				favorite.reportParams = {
					paramReportingPeriod: favorite.reportingPeriod,
					paramOrganisationUnit: favorite.organisationUnit,
					paramParentOrganisationUnit: favorite.parentOrganisationUnit
				};
				delete favorite.reportingPeriod;
				delete favorite.organisationUnit;
				delete favorite.parentOrganisationUnit;

				delete favorite.parentGraphMap;

				// Replace operand id characters
				for (var i = 0; i < dimensions.length; i++) {
					if (dimensions[i].dimension === pt.conf.finals.dimension.operand.objectName) {
						for (var j = 0; j < dimensions[i].items.length; j++) {
							dimensions[i].items[j].id = dimensions[i].items[j].id.replace('-', '.');
						}
					}
				}
			}

			return favorite;
		};

		NameWindow = function(id) {
			var window,
				record = pt.store.reportTable.getById(id);

			nameTextfield = Ext.create('Ext.form.field.Text', {
				height: 26,
				width: 371,
				fieldStyle: 'padding-left: 5px; border-radius: 1px; border-color: #bbb; font-size:11px',
				style: 'margin-bottom:0',
				emptyText: 'Favorite name',
				value: id ? record.data.name : '',
				listeners: {
					afterrender: function() {
						this.focus();
					}
				}
			});

			createButton = Ext.create('Ext.button.Button', {
				text: PT.i18n.create,
				handler: function() {
					var favorite = getBody();
					favorite.name = nameTextfield.getValue();

					//tmp
					//delete favorite.legendSet;

					if (favorite && favorite.name) {
						Ext.Ajax.request({
							url: pt.init.contextPath + '/api/reportTables/',
							method: 'POST',
							headers: {'Content-Type': 'application/json'},
							params: Ext.encode(favorite),
							failure: function(r) {
								pt.viewport.mask.show();
								alert(r.responseText);
							},
							success: function(r) {
								var id = r.getAllResponseHeaders().location.split('/').pop();

								pt.favorite = favorite;

								pt.store.reportTable.loadStore();

								pt.viewport.interpretationButton.enable();

								window.destroy();
							}
						});
					}
				}
			});

			updateButton = Ext.create('Ext.button.Button', {
				text: PT.i18n.update,
				handler: function() {
					var name = nameTextfield.getValue(),
						reportTable;

					if (id && name) {
						Ext.Ajax.request({
							url: pt.init.contextPath + '/api/reportTables/' + id + '.json?viewClass=dimensional&links=false',
							method: 'GET',
							failure: function(r) {
								pt.viewport.mask.show();
								alert(r.responseText);
							},
							success: function(r) {
								reportTable = Ext.decode(r.responseText);
								reportTable.name = name;

								//tmp
								//delete reportTable.legendSet;

								Ext.Ajax.request({
									url: pt.init.contextPath + '/api/reportTables/' + reportTable.id,
									method: 'PUT',
									headers: {'Content-Type': 'application/json'},
									params: Ext.encode(reportTable),
									failure: function(r) {
										pt.viewport.mask.show();
										alert(r.responseText);
									},
									success: function(r) {
										pt.store.reportTable.loadStore();
										window.destroy();
									}
								});
							}
						});
					}
				}
			});

			cancelButton = Ext.create('Ext.button.Button', {
				text: PT.i18n.cancel,
				handler: function() {
					window.destroy();
				}
			});

			window = Ext.create('Ext.window.Window', {
				title: id ? 'Rename favorite' : 'Create new favorite',
				//iconCls: 'pt-window-title-icon-favorite',
				bodyStyle: 'padding:2px; background:#fff',
				resizable: false,
				modal: true,
				items: nameTextfield,
				destroyOnBlur: true,
				bbar: [
					cancelButton,
					'->',
					id ? updateButton : createButton
				],
				listeners: {
					show: function(w) {
						pt.util.window.setAnchorPosition(w, addButton);

						if (!w.hasDestroyBlurHandler) {
							pt.util.window.addDestroyOnBlurHandler(w);
						}

						pt.viewport.favoriteWindow.destroyOnBlur = false;

						nameTextfield.focus(false, 500);
					},
					destroy: function() {
						pt.viewport.favoriteWindow.destroyOnBlur = true;
					}
				}
			});

			return window;
		};

		addButton = Ext.create('Ext.button.Button', {
			text: PT.i18n.add_new,
			width: 67,
			height: 26,
			style: 'border-radius: 1px;',
			menu: {},
			disabled: !Ext.isObject(pt.xLayout),
			handler: function() {
				nameWindow = new NameWindow(null, 'create');
				nameWindow.show();
			}
		});

		searchTextfield = Ext.create('Ext.form.field.Text', {
			width: windowCmpWidth - addButton.width - 11,
			height: 26,
			fieldStyle: 'padding-right: 0; padding-left: 5px; border-radius: 1px; border-color: #bbb; font-size:11px',
			emptyText: PT.i18n.search_for_favorites,
			enableKeyEvents: true,
			currentValue: '',
			listeners: {
				keyup: {
					fn: function() {
						if (this.getValue() !== this.currentValue) {
							this.currentValue = this.getValue();

							var value = this.getValue(),
								url = value ? pt.init.contextPath + '/api/reportTables/query/' + value + '.json?viewClass=sharing&links=false' : null,
								store = pt.store.reportTable;

							store.page = 1;
							store.loadStore(url);
						}
					},
					buffer: 100
				}
			}
		});

		prevButton = Ext.create('Ext.button.Button', {
			text: PT.i18n.prev,
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? pt.init.contextPath + '/api/reportTables/query/' + value + '.json?viewClass=sharing&links=false' : null,
					store = pt.store.reportTable;

				store.page = store.page <= 1 ? 1 : store.page - 1;
				store.loadStore(url);
			}
		});

		nextButton = Ext.create('Ext.button.Button', {
			text: PT.i18n.next,
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? pt.init.contextPath + '/api/reportTables/query/' + value + '.json?viewClass=sharing&links=false' : null,
					store = pt.store.reportTable;

				store.page = store.page + 1;
				store.loadStore(url);
			}
		});

		info = Ext.create('Ext.form.Label', {
			cls: 'pt-label-info',
			width: 300,
			height: 22
		});

		grid = Ext.create('Ext.grid.Panel', {
			cls: 'pt-grid',
			scroll: false,
			hideHeaders: true,
			columns: [
				{
					dataIndex: 'name',
					sortable: false,
					width: windowCmpWidth - 88,
					renderer: function(value, metaData, record) {
						var fn = function() {
							var element = Ext.get(record.data.id);

							if (element) {
								element = element.parent('td');
								element.addClsOnOver('link');
								element.load = function() {
									favoriteWindow.hide();
									pt.engine.loadTable(record.data.id, pt, true, true);
								};
								element.dom.setAttribute('onclick', 'Ext.get(this).load();');
							}
						};

						Ext.defer(fn, 100);

						return '<div id="' + record.data.id + '">' + value + '</div>';
					}
				},
				{
					xtype: 'actioncolumn',
					sortable: false,
					width: 80,
					items: [
						{
							iconCls: 'pt-grid-row-icon-edit',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-edit' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex);

								if (record.data.access.update) {
									nameWindow = new NameWindow(record.data.id);
									nameWindow.show();
								}
							}
						},
						{
							iconCls: 'pt-grid-row-icon-overwrite',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-overwrite' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									message,
									favorite;

								if (record.data.access.update) {
									message = PT.i18n.overwrite_favorite + '?\n\n' + record.data.name;
									favorite = getBody();

									if (favorite) {
										favorite.name = record.data.name;

										if (confirm(message)) {
											Ext.Ajax.request({
												url: pt.init.contextPath + '/api/reportTables/' + record.data.id,
												method: 'PUT',
												headers: {'Content-Type': 'application/json'},
												params: Ext.encode(favorite),
												success: function() {
													pt.favorite = favorite;
													pt.viewport.interpretationButton.enable();
													pt.store.reportTable.loadStore();
												}
											});
										}
									}
									else {
										alert(PT.i18n.please_create_a_table_first);
									}
								}
							}
						},
						{
							iconCls: 'pt-grid-row-icon-sharing',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-sharing' + (!record.data.access.manage ? ' disabled' : '');
							},
							handler: function(grid, rowIndex) {
								var record = this.up('grid').store.getAt(rowIndex);

								if (record.data.access.manage) {
									Ext.Ajax.request({
										url: pt.init.contextPath + '/api/sharing?type=reportTable&id=' + record.data.id,
										method: 'GET',
										failure: function(r) {
											pt.viewport.mask.hide();
											alert(r.responseText);
										},
										success: function(r) {
											var sharing = Ext.decode(r.responseText),
												window = PT.app.SharingWindow(sharing);
											window.show();
										}
									});
								}
							}
						},
						{
							iconCls: 'pt-grid-row-icon-delete',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-delete' + (!record.data.access['delete'] ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									message;

								if (record.data.access['delete']) {
									message = PT.i18n.delete_favorite + '?\n\n' + record.data.name;

									if (confirm(message)) {
										Ext.Ajax.request({
											url: pt.init.contextPath + '/api/reportTables/' + record.data.id,
											method: 'DELETE',
											success: function() {
												pt.store.reportTable.loadStore();
											}
										});
									}
								}
							}
						}
					]
				},
				{
					sortable: false,
					width: 6
				}
			],
			store: pt.store.reportTable,
			bbar: [
				info,
				'->',
				prevButton,
				nextButton
			],
			listeners: {
				added: function() {
					pt.viewport.favoriteGrid = this;
				},
				render: function() {
					var size = Math.floor((pt.viewport.centerRegion.getHeight() - 155) / pt.conf.layout.grid_row_height);
					this.store.pageSize = size;
					this.store.page = 1;
					this.store.loadStore();

					pt.store.reportTable.on('load', function() {
						if (this.isVisible()) {
							this.fireEvent('afterrender');
						}
					}, this);
				},
				afterrender: function() {
					var fn = function() {
						var editArray = Ext.query('.tooltip-favorite-edit'),
							overwriteArray = Ext.query('.tooltip-favorite-overwrite'),
							//dashboardArray = Ext.query('.tooltip-favorite-dashboard'),
							sharingArray = Ext.query('.tooltip-favorite-sharing'),
							deleteArray = Ext.query('.tooltip-favorite-delete'),
							el;

						for (var i = 0; i < editArray.length; i++) {
							var el = editArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: PT.i18n.rename,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < overwriteArray.length; i++) {
							el = overwriteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: PT.i18n.overwrite,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < sharingArray.length; i++) {
							el = sharingArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: PT.i18n.share_with_other_people,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < deleteArray.length; i++) {
							el = deleteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: PT.i18n.delete_,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}
					};

					Ext.defer(fn, 100);
				},
				itemmouseenter: function(grid, record, item) {
					this.currentItem = Ext.get(item);
					this.currentItem.removeCls('x-grid-row-over');
				},
				select: function() {
					this.currentItem.removeCls('x-grid-row-selected');
				},
				selectionchange: function() {
					this.currentItem.removeCls('x-grid-row-focused');
				}
			}
		});

		favoriteWindow = Ext.create('Ext.window.Window', {
			title: PT.i18n.manage_favorites,
			//iconCls: 'pt-window-title-icon-favorite',
			bodyStyle: 'padding:5px; background-color:#fff',
			resizable: false,
			modal: true,
			width: windowWidth,
			destroyOnBlur: true,
			items: [
				{
					xtype: 'panel',
					layout: 'hbox',
					bodyStyle: 'border:0 none',
					items: [
						addButton,
						{
							height: 24,
							width: 1,
							style: 'width:1px; margin-left:5px; margin-right:5px; margin-top:1px',
							bodyStyle: 'border-left: 1px solid #aaa'
						},
						searchTextfield
					]
				},
				grid
			],
			listeners: {
				show: function(w) {
					pt.util.window.setAnchorPosition(w, pt.viewport.favoriteButton);

					if (!w.hasDestroyOnBlurHandler) {
						pt.util.window.addDestroyOnBlurHandler(w);
					}

					searchTextfield.focus(false, 500);
				}
			}
		});

		return favoriteWindow;
	};

	PT.app.SharingWindow = function(sharing) {

		// Objects
		var UserGroupRow,

		// Functions
			getBody,

		// Components
			userGroupStore,
			userGroupField,
			userGroupButton,
			userGroupRowContainer,
			externalAccess,
			publicGroup,
			window;

		UserGroupRow = function(obj, isPublicAccess, disallowPublicAccess) {
			var getData,
				store,
				getItems,
				combo,
				getAccess,
				panel;

			getData = function() {
				var data = [
					{id: 'r-------', name: PT.i18n.can_view},
					{id: 'rw------', name: PT.i18n.can_edit_and_view}
				];

				if (isPublicAccess) {
					data.unshift({id: '-------', name: PT.i18n.none});
				}

				return data;
			}

			store = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: getData()
			});

			getItems = function() {
				var items = [];

				combo = Ext.create('Ext.form.field.ComboBox', {
					fieldLabel: isPublicAccess ? PT.i18n.public_access : obj.name,
					labelStyle: 'color:#333',
					cls: 'pt-combo',
					width: 380,
					labelWidth: 250,
					queryMode: 'local',
					valueField: 'id',
					displayField: 'name',
					labelSeparator: null,
					editable: false,
					disabled: !!disallowPublicAccess,
					value: obj.access || 'rw------',
					store: store
				});

				items.push(combo);

				if (!isPublicAccess) {
					items.push(Ext.create('Ext.Img', {
						src: 'images/grid-delete_16.png',
						style: 'margin-top:2px; margin-left:7px',
						overCls: 'pointer',
						width: 16,
						height: 16,
						listeners: {
							render: function(i) {
								i.getEl().on('click', function(e) {
									i.up('panel').destroy();
									window.doLayout();
								});
							}
						}
					}));
				}

				return items;
			};

			getAccess = function() {
				return {
					id: obj.id,
					name: obj.name,
					access: combo.getValue()
				};
			};

			panel = Ext.create('Ext.panel.Panel', {
				layout: 'column',
				bodyStyle: 'border:0 none',
				getAccess: getAccess,
				items: getItems()
			});

			return panel;
		};

		getBody = function() {
			var body = {
				object: {
					id: sharing.object.id,
					name: sharing.object.name,
					publicAccess: publicGroup.down('combobox').getValue(),
					externalAccess: externalAccess ? externalAccess.getValue() : false,
					user: {
						id: pt.init.user.id,
						name: pt.init.user.name
					}
				}
			};

			if (userGroupRowContainer.items.items.length > 1) {
				body.object.userGroupAccesses = [];
				for (var i = 1, item; i < userGroupRowContainer.items.items.length; i++) {
					item = userGroupRowContainer.items.items[i];
					body.object.userGroupAccesses.push(item.getAccess());
				}
			}

			return body;
		};

		// Initialize
		userGroupStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: pt.init.contextPath + '/api/sharing/search',
				reader: {
					type: 'json',
					root: 'userGroups'
				}
			}
		});

		userGroupField = Ext.create('Ext.form.field.ComboBox', {
			valueField: 'id',
			displayField: 'name',
			emptyText: PT.i18n.search_for_user_groups,
			queryParam: 'key',
			queryDelay: 200,
			minChars: 1,
			hideTrigger: true,
			fieldStyle: 'height:26px; padding-left:6px; border-radius:1px; font-size:11px',
			style: 'margin-bottom:5px',
			width: 380,
			store: userGroupStore,
			listeners: {
				beforeselect: function(cb) { // beforeselect instead of select, fires regardless of currently selected item
					userGroupButton.enable();
				},
				afterrender: function(cb) {
					cb.inputEl.on('keyup', function() {
						userGroupButton.disable();
					});
				}
			}
		});

		userGroupButton = Ext.create('Ext.button.Button', {
			text: '+',
			style: 'margin-left:2px; padding-right:4px; padding-left:4px; border-radius:1px',
			disabled: true,
			height: 26,
			handler: function(b) {
				userGroupRowContainer.add(UserGroupRow({
					id: userGroupField.getValue(),
					name: userGroupField.getRawValue(),
					access: 'r-------'
				}));

				userGroupField.clearValue();
				b.disable();
			}
		});

		userGroupRowContainer = Ext.create('Ext.container.Container', {
			bodyStyle: 'border:0 none'
		});

		if (sharing.meta.allowExternalAccess) {
			externalAccess = userGroupRowContainer.add({
				xtype: 'checkbox',
				fieldLabel: PT.i18n.allow_external_access,
				labelSeparator: '',
				labelWidth: 250,
				checked: !!sharing.object.externalAccess
			});
		}

		publicGroup = userGroupRowContainer.add(UserGroupRow({
			id: sharing.object.id,
			name: sharing.object.name,
			access: sharing.object.publicAccess
		}, true, !sharing.meta.allowPublicAccess));

		if (Ext.isArray(sharing.object.userGroupAccesses)) {
			for (var i = 0, userGroupRow; i < sharing.object.userGroupAccesses.length; i++) {
				userGroupRow = UserGroupRow(sharing.object.userGroupAccesses[i]);
				userGroupRowContainer.add(userGroupRow);
			}
		}

		window = Ext.create('Ext.window.Window', {
			title: PT.i18n.sharing_settings,
			bodyStyle: 'padding:6px 6px 0px; background-color:#fff',
			resizable: false,
			modal: true,
			destroyOnBlur: true,
			items: [
				{
					html: sharing.object.name,
					bodyStyle: 'border:0 none; font-weight:bold; color:#333',
					style: 'margin-bottom:8px'
				},
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						userGroupField,
						userGroupButton
					]
				},
				userGroupRowContainer
			],
			bbar: [
				'->',
				{
					text: PT.i18n.save,
					handler: function() {
						Ext.Ajax.request({
							url: pt.init.contextPath + '/api/sharing?type=reportTable&id=' + sharing.object.id,
							method: 'POST',
							headers: {
								'Content-Type': 'application/json'
							},
							params: Ext.encode(getBody())
						});

						window.destroy();
					}
				}
			],
			listeners: {
				show: function(w) {
					var pos = pt.viewport.favoriteWindow.getPosition();
					w.setPosition(pos[0] + 5, pos[1] + 5);

					if (!w.hasDestroyOnBlurHandler) {
						pt.util.window.addDestroyOnBlurHandler(w);
					}

					pt.viewport.favoriteWindow.destroyOnBlur = false;
				},
				destroy: function() {
					pt.viewport.favoriteWindow.destroyOnBlur = true;
				}
			}
		});

		return window;
	};

	PT.app.InterpretationWindow = function() {
		var textArea,
			linkPanel,
			shareButton,
			window;

		if (Ext.isObject(pt.favorite) && Ext.isString(pt.favorite.id)) {
			textArea = Ext.create('Ext.form.field.TextArea', {
				cls: 'pt-textarea',
				height: 130,
				fieldStyle: 'padding-left: 4px; padding-top: 3px',
				emptyText: PT.i18n.write_your_interpretation,
				enableKeyEvents: true,
				listeners: {
					keyup: function() {
						shareButton.xable();
					}
				}
			});

			linkPanel = Ext.create('Ext.panel.Panel', {
				html: function() {
					var reportTableUrl = pt.init.contextPath + '/dhis-web-pivot/app/index.html?id=' + pt.favorite.id,
						apiUrl = pt.init.contextPath + '/api/reportTables/' + pt.favorite.id + '/data.html',
						html = '';

					html += '<div><b>Pivot link: </b><span class="user-select"><a href="' + reportTableUrl + '" target="_blank">' + reportTableUrl + '</a></span></div>';
					html += '<div style="padding-top:3px"><b>API link: </b><span class="user-select"><a href="' + apiUrl + '" target="_blank">' + apiUrl + '</a></span></div>';
					return html;
				}(),
				style: 'padding-top: 8px; padding-bottom: 5px',
				bodyStyle: 'border: 0 none'
			});

			shareButton = Ext.create('Ext.button.Button', {
				text: PT.i18n.share,
				disabled: true,
				xable: function() {
					this.setDisabled(!textArea.getValue());
				},
				handler: function() {
					if (textArea.getValue()) {
						Ext.Ajax.request({
							url: pt.init.contextPath + pt.conf.finals.url.path_api + 'interpretations/reportTable/' + pt.favorite.id,
							method: 'POST',
							params: textArea.getValue(),
							headers: {'Content-Type': 'text/html'},
							success: function() {
								textArea.reset();
								pt.viewport.interpretationButton.disable();
								window.hide();
								//PT.util.notification.interpretation(PT.i18n.interpretation_was_shared + '.');
							}
						});
					}
				}
			});

			window = Ext.create('Ext.window.Window', {
				title: pt.favorite.name,
				layout: 'fit',
				//iconCls: 'pt-window-title-interpretation',
				width: 500,
				bodyStyle: 'padding:5px 5px 3px; background-color:#fff',
				resizable: true,
				destroyOnBlur: true,
				modal: true,
				items: [
					textArea,
					linkPanel
				],
				bbar: {
					cls: 'pt-toolbar-bbar',
					defaults: {
						height: 24
					},
					items: [
						'->',
						shareButton
					]
				},
				listeners: {
					show: function(w) {
						pt.util.window.setAnchorPosition(w, pt.viewport.interpretationButton);

						document.body.oncontextmenu = true;

						if (!w.hasDestroyOnBlurHandler) {
							pt.util.window.addDestroyOnBlurHandler(w);
						}
					},
					hide: function() {
						document.body.oncontextmenu = function(){return false;};
					},
					destroy: function() {
						pt.viewport.interpretationWindow = null;
					}
				}
			});

			return window;
		}

		return;
	};

	createViewport = function() {
        var dimConf = pt.conf.finals.dimension,

			indicatorAvailable,
			indicatorSelected,
			indicator,
			dataElementAvailable,
			dataElementSelected,
			dataElement,
			dataSetAvailable,
			dataSetSelected,
			dataSet,
			rewind,
			relativePeriod,
			fixedPeriodAvailable,
			fixedPeriodSelected,
			period,
			treePanel,
			userOrganisationUnit,
			userOrganisationUnitChildren,
			userOrganisationUnitGrandChildren,
			userOrganisationUnitPanel,
			organisationUnitLevel,
			tool,
			toolPanel,
			organisationUnit,
			dimensionIdAvailableStoreMap = {},
			dimensionIdSelectedStoreMap = {},
			getGroupSetPanels,
			update,

			layoutButton,
			optionsButton,
			favoriteButton,
			openTableLayoutTab,
			downloadButton,

			accordionBody,
			accordion,
			westRegion,
			centerRegion,

			setGui,

			viewport,
			addListeners;

		indicatorAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-left',
			width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			store: pt.store.indicatorAvailable,
			tbar: [
				{
					xtype: 'label',
					text: PT.i18n.available,
					cls: 'pt-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.select(indicatorAvailable, indicatorSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.selectAll(indicatorAvailable, indicatorSelected);
					}
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						pt.util.multiselect.select(this, indicatorSelected);
					}, this);
				}
			}
		});

		indicatorSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-right',
			width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			ddReorder: true,
			store: pt.store.indicatorSelected,
			tbar: [
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.unselectAll(indicatorAvailable, indicatorSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.unselect(indicatorAvailable, indicatorSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: PT.i18n.selected,
					cls: 'pt-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						pt.util.multiselect.unselect(indicatorAvailable, this);
					}, this);
				}
			}
		});

		indicator = {
			xtype: 'panel',
			title: '<div class="pt-panel-title-data">' + PT.i18n.indicators + '</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
					dimension: pt.conf.finals.dimension.indicator.objectName,
					items: []
				};

				pt.store.indicatorSelected.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = pt.viewport.westRegion.hasScrollbar ?
					pt.conf.layout.west_scrollbarheight_accordion_indicator : pt.conf.layout.west_maxheight_accordion_indicator;
				pt.util.dimension.panel.setHeight(h);
				pt.util.multiselect.setHeight(
					[indicatorAvailable, indicatorSelected],
					this,
					pt.conf.layout.west_fill_accordion_indicator
				);
			},
			items: [
				{
					xtype: 'combobox',
					cls: 'pt-combo',
					style: 'margin-bottom:2px; margin-top:0px',
					width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding,
					valueField: 'id',
					displayField: 'name',
					emptyText: PT.i18n.select_indicator_group,
					editable: false,
					store: {
						xtype: 'store',
						fields: ['id', 'name', 'index'],
						proxy: {
							type: 'ajax',
							url: pt.init.contextPath + pt.conf.finals.url.path_api + pt.conf.finals.url.indicatorgroup_get,
							reader: {
								type: 'json',
								root: 'indicatorGroups'
							}
						},
						listeners: {
							load: function(s) {
								s.add({
									id: 0,
									name: PT.i18n.all_indicator_groups,
									index: -1
								});
								s.sort([
									{
										property: 'index',
										direction: 'ASC'
									},
									{
										property: 'name',
										direction: 'ASC'
									}
								]);
							}
						}
					},
					listeners: {
						select: function(cb) {
							var store = pt.store.indicatorAvailable;
							store.parent = cb.getValue();

							if (pt.util.store.containsParent(store)) {
								pt.util.store.loadFromStorage(store);
								pt.util.multiselect.filterAvailable(indicatorAvailable, indicatorSelected);
							}
							else {
								if (cb.getValue() === 0) {
									store.proxy.url = pt.init.contextPath + pt.conf.finals.url.path_api + pt.conf.finals.url.indicator_getall;
									store.load();
								}
								else {
									store.proxy.url = pt.init.contextPath + pt.conf.finals.url.path_api + pt.conf.finals.url.indicator_get + cb.getValue() + '.json';
									store.load();
								}
							}
						}
					}
				},
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						indicatorAvailable,
						indicatorSelected
					]
				}
			],
			listeners: {
				added: function() {
					pt.cmp.dimension.panels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		dataElementAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-left',
			width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			store: pt.store.dataElementAvailable,
			tbar: [
				{
					xtype: 'label',
					text: PT.i18n.available,
					cls: 'pt-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.select(dataElementAvailable, dataElementSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.selectAll(dataElementAvailable, dataElementSelected);
					}
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						pt.util.multiselect.select(this, dataElementSelected);
					}, this);
				}
			}
		});

		dataElementSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-right',
			width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			ddReorder: true,
			store: pt.store.dataElementSelected,
			tbar: [
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.unselectAll(dataElementAvailable, dataElementSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.unselect(dataElementAvailable, dataElementSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: PT.i18n.selected,
					cls: 'pt-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						pt.util.multiselect.unselect(dataElementAvailable, this);
					}, this);
				}
			}
		});

		dataElementGroupStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'index'],
			proxy: {
				type: 'ajax',
				url: pt.init.contextPath + pt.conf.finals.url.path_api + pt.conf.finals.url.dataelementgroup_get,
				reader: {
					type: 'json',
					root: 'dataElementGroups'
				}
			},
			listeners: {
				load: function(s) {
					if (dataElementDetailLevel.getValue() === pt.conf.finals.dimension.dataElement.objectName) {
						s.add({
							id: 0,
							name: '[ ' + PT.i18n.all_data_element_groups + ' ]',
							index: -1
						});
					}

					s.sort([
						{property: 'index', direction: 'ASC'},
						{property: 'name', direction: 'ASC'}
					]);
				}
			}
		});

		dataElementGroupComboBox = Ext.create('Ext.form.field.ComboBox', {
			cls: 'pt-combo',
			style: 'margin:0 2px 2px 0',
			width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding - 90,
			valueField: 'id',
			displayField: 'name',
			emptyText: PT.i18n.select_data_element_group,
			editable: false,
			store: dataElementGroupStore,
			loadAvailable: function() {
				var store = pt.store.dataElementAvailable,
					detailLevel = dataElementDetailLevel.getValue(),
					value = this.getValue();

				if (value !== null) {
					if (detailLevel === pt.conf.finals.dimension.dataElement.objectName) {
						store.setTotalsProxy(value);
					}
					else {
						store.setDetailsProxy(value);
					}
				}
			},
			listeners: {
				select: function(cb) {
					cb.loadAvailable();
				}
			}
		});

		dataElementDetailLevel = Ext.create('Ext.form.field.ComboBox', {
			cls: 'pt-combo',
			style: 'margin-bottom:2px',
			baseBodyCls: 'small',
			queryMode: 'local',
			editable: false,
			valueField: 'id',
			displayField: 'text',
			width: 90 - 2,
			value: pt.conf.finals.dimension.dataElement.objectName,
			store: {
				fields: ['id', 'text'],
				data: [
					{id: pt.conf.finals.dimension.dataElement.objectName, text: PT.i18n.totals},
					{id: pt.conf.finals.dimension.operand.objectName, text: PT.i18n.details}
				]
			},
			listeners: {
				select: function(cb) {
					var record = dataElementGroupStore.getById(0);

					if (cb.getValue() === pt.conf.finals.dimension.operand.objectName && record) {
						dataElementGroupStore.remove(record);
					}

					if (cb.getValue() === pt.conf.finals.dimension.dataElement.objectName && !record) {
						dataElementGroupStore.insert(0, {
							id: 0,
							name: '[ ' + PT.i18n.all_data_element_groups + ' ]',
							index: -1
						});
					}

					dataElementGroupComboBox.loadAvailable();
					pt.store.dataElementSelected.removeAll();
				}
			}
		});

		dataElement = {
			xtype: 'panel',
			title: '<div class="pt-panel-title-data">' + PT.i18n.data_elements + '</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
					dimension: dataElementDetailLevel.getValue(),
					items: []
				};

				pt.store.dataElementSelected.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = pt.viewport.westRegion.hasScrollbar ?
					pt.conf.layout.west_scrollbarheight_accordion_dataelement : pt.conf.layout.west_maxheight_accordion_dataelement;
				pt.util.dimension.panel.setHeight(h);
				pt.util.multiselect.setHeight(
					[dataElementAvailable, dataElementSelected],
					this,
					pt.conf.layout.west_fill_accordion_indicator
				);
			},
			items: [
				{
					xtype: 'container',
					layout: 'column',
					items: [
						dataElementGroupComboBox,
						dataElementDetailLevel
					]
				},
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						dataElementAvailable,
						dataElementSelected
					]
				}
			],
			listeners: {
				added: function() {
					pt.cmp.dimension.panels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		dataSetAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-left',
			width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			store: pt.store.dataSetAvailable,
			tbar: [
				{
					xtype: 'label',
					text: PT.i18n.available,
					cls: 'pt-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.select(dataSetAvailable, dataSetSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.selectAll(dataSetAvailable, dataSetSelected);
					}
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						pt.util.multiselect.select(this, dataSetSelected);
					}, this);
				}
			}
		});

		dataSetSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-right',
			width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			ddReorder: true,
			store: pt.store.dataSetSelected,
			tbar: [
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.unselectAll(dataSetAvailable, dataSetSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.unselect(dataSetAvailable, dataSetSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: PT.i18n.selected,
					cls: 'pt-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						pt.util.multiselect.unselect(dataSetAvailable, this);
					}, this);
				}
			}
		});

		dataSet = {
			xtype: 'panel',
			title: '<div class="pt-panel-title-data">' + PT.i18n.reporting_rates + '</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
					dimension: pt.conf.finals.dimension.dataSet.objectName,
					items: []
				};

				pt.store.dataSetSelected.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = pt.viewport.westRegion.hasScrollbar ?
					pt.conf.layout.west_scrollbarheight_accordion_dataset : pt.conf.layout.west_maxheight_accordion_dataset;
				pt.util.dimension.panel.setHeight(h);
				pt.util.multiselect.setHeight(
					[dataSetAvailable, dataSetSelected],
					this,
					pt.conf.layout.west_fill_accordion_dataset
				);

				if (!pt.store.dataSetAvailable.isLoaded) {
					pt.store.dataSetAvailable.load();
				}
			},
			items: [
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						dataSetAvailable,
						dataSetSelected
					]
				}
			],
			listeners: {
				added: function() {
					pt.cmp.dimension.panels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		rewind = Ext.create('Ext.form.field.Checkbox', {
			relativePeriodId: 'rewind',
			boxLabel: 'Rewind one period',
			xable: function() {
				this.setDisabled(pt.util.checkbox.isAllFalse());
			}
		});

		relativePeriod = {
			xtype: 'panel',
			hideCollapseTool: true,
			autoScroll: true,
			bodyStyle: 'border:0 none',
			valueComponentMap: {},
			items: [
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						{
							xtype: 'panel',
							columnWidth: 0.34,
							bodyStyle: 'border-style:none; padding:0 0 0 8px',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: PT.i18n.weeks,
									cls: 'pt-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_WEEK',
									boxLabel: PT.i18n.last_week
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_4_WEEKS',
									boxLabel: PT.i18n.last_4_weeks
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_12_WEEKS',
									boxLabel: PT.i18n.last_12_weeks
								}
							]
						},
						{
							xtype: 'panel',
							columnWidth: 0.33,
							bodyStyle: 'border-style:none',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: PT.i18n.months,
									cls: 'pt-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_MONTH',
									boxLabel: PT.i18n.last_month
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_3_MONTHS',
									boxLabel: PT.i18n.last_3_months
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_12_MONTHS',
									boxLabel: PT.i18n.last_12_months,
									checked: true
								}
							]
						},
						{
							xtype: 'panel',
							columnWidth: 0.33,
							bodyStyle: 'border-style:none',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: PT.i18n.bimonths,
									cls: 'pt-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_BIMONTH',
									boxLabel: PT.i18n.last_bimonth
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_6_BIMONTHS',
									boxLabel: PT.i18n.last_6_bimonths
								}
							]
						}
					]
				},
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						{
							xtype: 'panel',
							columnWidth: 0.34,
							bodyStyle: 'border-style:none; padding:5px 0 0 10px',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: PT.i18n.quarters,
									cls: 'pt-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_QUARTER',
									boxLabel: PT.i18n.last_quarter
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_4_QUARTERS',
									boxLabel: PT.i18n.last_4_quarters
								}
							]
						},
						{
							xtype: 'panel',
							columnWidth: 0.33,
							bodyStyle: 'border-style:none; padding:5px 0 0',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: PT.i18n.sixmonths,
									cls: 'pt-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_SIX_MONTH',
									boxLabel: PT.i18n.last_sixmonth
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_2_SIXMONTHS',
									boxLabel: PT.i18n.last_2_sixmonths
								}
							]
						},
						{
							xtype: 'panel',
							columnWidth: 0.33,
							bodyStyle: 'border-style:none; padding:5px 0 0',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: PT.i18n.financial_years,
									cls: 'pt-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_FINANCIAL_YEAR',
									boxLabel: PT.i18n.last_financial_year
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_5_FINANCIAL_YEARS',
									boxLabel: PT.i18n.last_5_financial_years
								}
							]
						}

						//{
							//xtype: 'panel',
							//layout: 'anchor',
							//bodyStyle: 'border-style:none; padding:5px 0 0 46px',
							//defaults: {
								//labelSeparator: '',
								//style: 'margin-bottom:2px',
							//},
							//items: [
								//{
									//xtype: 'label',
									//text: 'Options',
									//cls: 'pt-label-period-heading-options'
								//},
								//rewind
							//]
						//}
					]
				},
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						{
							xtype: 'panel',
							columnWidth: 0.35,
							bodyStyle: 'border-style:none; padding:5px 0 0 10px',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: PT.i18n.years,
									cls: 'pt-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'THIS_YEAR',
									boxLabel: PT.i18n.this_year
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_YEAR',
									boxLabel: PT.i18n.last_year
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_5_YEARS',
									boxLabel: PT.i18n.last_5_years
								}
							]
						}
					]
				}
			]
		};

		fixedPeriodAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-left',
			width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
			height: 180,
			valueField: 'id',
			displayField: 'name',
			store: pt.store.fixedPeriodAvailable,
			tbar: [
				{
					xtype: 'label',
					text: PT.i18n.available,
					cls: 'pt-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.select(fixedPeriodAvailable, fixedPeriodSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.selectAll(fixedPeriodAvailable, fixedPeriodSelected, true);
					}
				},
				' '
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						pt.util.multiselect.select(fixedPeriodAvailable, fixedPeriodSelected);
					}, this);
				}
			}
		});

		fixedPeriodSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-right',
			width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
			height: 180,
			valueField: 'id',
			displayField: 'name',
			ddReorder: false,
			store: pt.store.fixedPeriodSelected,
			tbar: [
				' ',
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.unselectAll(fixedPeriodAvailable, fixedPeriodSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						pt.util.multiselect.unselect(fixedPeriodAvailable, fixedPeriodSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: PT.i18n.selected,
					cls: 'pt-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						pt.util.multiselect.unselect(fixedPeriodAvailable, fixedPeriodSelected);
					}, this);
				}
			}
		});

		period = {
			xtype: 'panel',
			title: '<div class="pt-panel-title-period">Periods</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
						dimension: pt.conf.finals.dimension.period.objectName,
						items: []
					},
					chb = pt.cmp.dimension.relativePeriod.checkbox;

				pt.store.fixedPeriodSelected.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				for (var i = 0; i < chb.length; i++) {
					if (chb[i].getValue()) {
						config.items.push({
							id: chb[i].relativePeriodId,
							name: ''
						});
					}
				}

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = pt.viewport.westRegion.hasScrollbar ?
					pt.conf.layout.west_scrollbarheight_accordion_period : pt.conf.layout.west_maxheight_accordion_period;
				pt.util.dimension.panel.setHeight(h);
				pt.util.multiselect.setHeight(
					[fixedPeriodAvailable, fixedPeriodSelected],
					this,
					pt.conf.layout.west_fill_accordion_period
				);
			},
			items: [
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					style: 'margin-top:0px',
					items: [
						{
							xtype: 'combobox',
							cls: 'pt-combo',
							style: 'margin-bottom:2px',
							width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding - 62 - 62 - 4,
							valueField: 'id',
							displayField: 'name',
							emptyText: PT.i18n.select_period_type,
							editable: false,
							queryMode: 'remote',
							store: pt.store.periodType,
							periodOffset: 0,
							listeners: {
								select: function() {
									var ptype = new PeriodType(),
										periodType = this.getValue();

									var periods = ptype.get(periodType).generatePeriods({
										offset: this.periodOffset,
										filterFuturePeriods: true,
										reversePeriods: true
									});

									pt.store.fixedPeriodAvailable.setIndex(periods);
									pt.store.fixedPeriodAvailable.loadData(periods);
									pt.util.multiselect.filterAvailable(fixedPeriodAvailable, fixedPeriodSelected);
								}
							}
						},
						{
							xtype: 'button',
							text: PT.i18n.prev_year,
							style: 'margin-left:2px; border-radius:2px',
							height: 24,
							handler: function() {
								var cb = this.up('panel').down('combobox');
								if (cb.getValue()) {
									cb.periodOffset--;
									cb.fireEvent('select');
								}
							}
						},
						{
							xtype: 'button',
							text: PT.i18n.next_year,
							style: 'margin-left:2px; border-radius:2px',
							height: 24,
							handler: function() {
								var cb = this.up('panel').down('combobox');
								if (cb.getValue() && cb.periodOffset < 0) {
									cb.periodOffset++;
									cb.fireEvent('select');
								}
							}
						}
					]
				},
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none; padding-bottom:2px',
					items: [
						fixedPeriodAvailable,
						fixedPeriodSelected
					]
				},
				relativePeriod
			],
			listeners: {
				added: function() {
					pt.cmp.dimension.panels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		treePanel = Ext.create('Ext.tree.Panel', {
			cls: 'pt-tree',
			style: 'border-top: 1px solid #ddd; padding-top: 1px',
			width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding,
			rootVisible: false,
			autoScroll: true,
			multiSelect: true,
			rendered: false,
			reset: function() {
				var rootNode = this.getRootNode().findChild('id', pt.init.rootNodes[0].id);
				this.collapseAll();
				this.expandPath(rootNode.getPath());
				this.getSelectionModel().select(rootNode);
			},
			selectRootIf: function() {
				if (this.getSelectionModel().getSelection().length < 1) {
					var node = this.getRootNode().findChild('id', pt.init.rootNodes[0].id);
					if (this.rendered) {
						this.getSelectionModel().select(node);
					}
					return node;
				}
			},
			isPending: false,
			recordsToSelect: [],
			recordsToRestore: [],
			multipleSelectIf: function(map, doUpdate) {
				if (this.recordsToSelect.length === pt.util.object.getLength(map)) {
					this.getSelectionModel().select(this.recordsToSelect);
					this.recordsToSelect = [];
					this.isPending = false;

					if (doUpdate) {
						update();
					}
				}
			},
			multipleExpand: function(id, map, doUpdate) {
				var that = this,
					rootId = pt.conf.finals.root.id,
					path = map[id];

				if (path.substr(0, rootId.length + 1) !== ('/' + rootId)) {
					path = '/' + rootId + path;
				}

				that.expandPath(path, 'id', '/', function() {
					record = Ext.clone(that.getRootNode().findChild('id', id, true));
					that.recordsToSelect.push(record);
					that.multipleSelectIf(map, doUpdate);
				});
			},
            select: function(url, params) {
                if (!params) {
                    params = {};
                }
                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    params: params,
                    scope: this,
                    success: function(r) {
                        var a = Ext.decode(r.responseText).organisationUnits;
                        this.numberOfRecords = a.length;
                        for (var i = 0; i < a.length; i++) {
                            this.multipleExpand(a[i].id, a[i].path);
                        }
                    }
                });
            },
			getParentGraphMap: function() {
				var selection = this.getSelectionModel().getSelection(),
					map = {};

				if (Ext.isArray(selection) && selection.length) {
					for (var i = 0, pathArray, key; i < selection.length; i++) {
						pathArray = selection[i].getPath().split('/');
						map[pathArray.pop()] = pathArray.join('/');
					}
				}

				return map;
			},
			selectGraphMap: function(map, update) {
				if (!pt.util.object.getLength(map)) {
					return;
				}
				
				this.isPending = true;

				for (var key in map) {
					if (map.hasOwnProperty(key)) {
						treePanel.multipleExpand(key, map, update);
					}
				}
			},
			store: Ext.create('Ext.data.TreeStore', {
				proxy: {
					type: 'ajax',
					url: pt.init.contextPath + pt.conf.finals.url.path_module + pt.conf.finals.url.organisationunitchildren_get
				},
				root: {
					id: pt.conf.finals.root.id,
					expanded: true,
					children: pt.init.rootNodes
				}
			}),
			xable: function(values) {
				for (var i = 0; i < values.length; i++) {
					if (!!values[i]) {
						this.disable();
						return;
					}
				}

				this.enable();
			},
			listeners: {
				beforeitemexpand: function() {
					var rts = treePanel.recordsToSelect;
					
					if (!treePanel.isPending) {
						treePanel.recordsToRestore = treePanel.getSelectionModel().getSelection();
					}
				},
				itemexpand: function() {						
					if (!treePanel.isPending && treePanel.recordsToRestore.length) {
						treePanel.getSelectionModel().select(treePanel.recordsToRestore);
						treePanel.recordsToRestore = [];
					}
				},					
				added: function() {
					pt.cmp.dimension.organisationUnit.treepanel = this;
				},
				render: function() {
					this.rendered = true;
				},
				afterrender: function() {
					this.getSelectionModel().select(0);
				},
				itemcontextmenu: function(v, r, h, i, e) {
					v.getSelectionModel().select(r, false);

					if (v.menu) {
						v.menu.destroy();
					}
					v.menu = Ext.create('Ext.menu.Menu', {
						id: 'treepanel-contextmenu',
						showSeparator: false,
						shadow: false
					});
					if (!r.data.leaf) {
						v.menu.add({
							id: 'treepanel-contextmenu-item',
							text: PT.i18n.select_all_children,
							icon: 'images/node-select-child.png',
							handler: function() {
								r.expand(false, function() {
									v.getSelectionModel().select(r.childNodes, true);
									v.getSelectionModel().deselect(r);
								});
							}
						});
					}
					else {
						return;
					}

					v.menu.showAt(e.xy);
				}
			}
		});

		userOrganisationUnit = Ext.create('Ext.form.field.Checkbox', {
			columnWidth: 0.28,
			style: 'padding-top:2px; padding-left:3px; margin-bottom:0',
			boxLabel: PT.i18n.user_organisation_unit,
			labelWidth: pt.conf.layout.form_label_width,
			handler: function(chb, checked) {
				treePanel.xable([checked, userOrganisationUnitChildren.getValue(), userOrganisationUnitGrandChildren.getValue()]);
			}
		});

		userOrganisationUnitChildren = Ext.create('Ext.form.field.Checkbox', {
			columnWidth: 0.31,
			style: 'padding-top:2px; margin-bottom:0',
			boxLabel: PT.i18n.user_organisation_unit_children,
			labelWidth: pt.conf.layout.form_label_width,
			handler: function(chb, checked) {
				treePanel.xable([checked, userOrganisationUnit.getValue(), userOrganisationUnitGrandChildren.getValue()]);
			}
		});

		userOrganisationUnitGrandChildren = Ext.create('Ext.form.field.Checkbox', {
			columnWidth: 0.41,
			style: 'padding-top:2px; margin-bottom:0',
			boxLabel: PT.i18n.user_organisation_unit_grandchildren,
			labelWidth: pt.conf.layout.form_label_width,
			handler: function(chb, checked) {
				treePanel.xable([checked, userOrganisationUnit.getValue(), userOrganisationUnitChildren.getValue()]);
			}
		});

		organisationUnitLevel = Ext.create('Ext.form.field.ComboBox', {
			cls: 'pt-combo',
			multiSelect: true,
			style: 'margin-bottom:0',
			width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding - 38,
			valueField: 'level',
			displayField: 'name',
			emptyText: PT.i18n.select_organisation_unit_levels,
			editable: false,
			hidden: true,
			store: {
				fields: ['id', 'name', 'level'],
				data: pt.init.organisationUnitLevels
			}
		});

		organisationUnitGroup = Ext.create('Ext.form.field.ComboBox', {
			cls: 'pt-combo',
			multiSelect: true,
			style: 'margin-bottom:0',
			width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding - 38,
			valueField: 'id',
			displayField: 'name',
			emptyText: PT.i18n.select_organisation_unit_groups,
			editable: false,
			hidden: true,
			store: pt.store.organisationUnitGroup
		});

		toolMenu = Ext.create('Ext.menu.Menu', {
			shadow: false,
			showSeparator: false,
			menuValue: 'orgunit',
			clickHandler: function(param) {
				if (!param) {
					return;
				}

				var items = this.items.items;
				this.menuValue = param;

				// Menu item icon cls
				for (var i = 0; i < items.length; i++) {
					if (items[i].setIconCls) {
						if (items[i].param === param) {
							items[i].setIconCls('pt-menu-item-selected');
						}
						else {
							items[i].setIconCls('pt-menu-item-unselected');
						}
					}
				}

				// Gui
				if (param === 'orgunit') {
					userOrganisationUnit.show();
					userOrganisationUnitChildren.show();
					userOrganisationUnitGrandChildren.show();
					organisationUnitLevel.hide();
					organisationUnitGroup.hide();

					if (userOrganisationUnit.getValue() || userOrganisationUnitChildren.getValue()) {
						treePanel.disable();
					}
				}
				else if (param === 'level') {
					userOrganisationUnit.hide();
					userOrganisationUnitChildren.hide();
					userOrganisationUnitGrandChildren.hide();
					organisationUnitLevel.show();
					organisationUnitGroup.hide();
					treePanel.enable();
				}
				else if (param === 'group') {
					userOrganisationUnit.hide();
					userOrganisationUnitChildren.hide();
					userOrganisationUnitGrandChildren.hide();
					organisationUnitLevel.hide();
					organisationUnitGroup.show();
					treePanel.enable();
				}
			},
			items: [
				{
					xtype: 'label',
					text: 'Selection mode',
					style: 'padding:7px 5px 5px 7px; font-weight:bold; border:0 none'
				},
				{
					text: PT.i18n.select_organisation_units + '&nbsp;&nbsp;',
					param: 'orgunit',
					iconCls: 'pt-menu-item-selected'
				},
				{
					text: 'Select levels' + '&nbsp;&nbsp;',
					param: 'level',
					iconCls: 'pt-menu-item-unselected'
				},
				{
					text: 'Select groups' + '&nbsp;&nbsp;',
					param: 'group',
					iconCls: 'pt-menu-item-unselected'
				}
			],
			listeners: {
				afterrender: function() {
					this.getEl().addCls('pt-btn-menu');
				},
				click: function(menu, item) {
					this.clickHandler(item.param);
				}
			}
		});

		tool = Ext.create('Ext.button.Button', {
			cls: 'pt-button-organisationunitselection',
			iconCls: 'pt-button-icon-gear',
			width: 36,
			height: 24,
			menu: toolMenu
		});

		toolPanel = Ext.create('Ext.panel.Panel', {
			width: 36,
			bodyStyle: 'border:0 none; text-align:right',
			style: 'margin-right:2px',
			items: tool
		});

		organisationUnit = {
			xtype: 'panel',
			title: '<div class="pt-panel-title-organisationunit">' + PT.i18n.organisation_units + '</div>',
			bodyStyle: 'padding:2px',
			hideCollapseTool: true,
			collapsed: false,
			getDimension: function() {
				var r = treePanel.getSelectionModel().getSelection(),
					config = {
						dimension: pt.conf.finals.dimension.organisationUnit.objectName,
						items: []
					};

				if (toolMenu.menuValue === 'orgunit') {
					if (userOrganisationUnit.getValue() || userOrganisationUnitChildren.getValue() || userOrganisationUnitGrandChildren.getValue()) {
						if (userOrganisationUnit.getValue()) {
							config.items.push({
								id: 'USER_ORGUNIT',
								name: ''
							});
						}
						if (userOrganisationUnitChildren.getValue()) {
							config.items.push({
								id: 'USER_ORGUNIT_CHILDREN',
								name: ''
							});
						}
						if (userOrganisationUnitGrandChildren.getValue()) {
							config.items.push({
								id: 'USER_ORGUNIT_GRANDCHILDREN',
								name: ''
							});
						}
					}
					else {
						for (var i = 0; i < r.length; i++) {
							config.items.push({id: r[i].data.id});
						}
					}
				}
				else if (toolMenu.menuValue === 'level') {
					var levels = organisationUnitLevel.getValue();

					for (var i = 0; i < levels.length; i++) {
						config.items.push({
							id: 'LEVEL-' + levels[i],
							name: ''
						});
					}

					for (var i = 0; i < r.length; i++) {
						config.items.push({
							id: r[i].data.id,
							name: ''
						});
					}
				}
				else if (toolMenu.menuValue === 'group') {
					var groupIds = organisationUnitGroup.getValue();

					for (var i = 0; i < groupIds.length; i++) {
						config.items.push({
							id: 'OU_GROUP-' + groupIds[i],
							name: ''
						});
					}

					for (var i = 0; i < r.length; i++) {
						config.items.push({
							id: r[i].data.id,
							name: ''
						});
					}
				}

				return config.items.length ? config : null;
			},
            onExpand: function() {
                var h = pt.viewport.westRegion.hasScrollbar ?
                    pt.conf.layout.west_scrollbarheight_accordion_organisationunit : pt.conf.layout.west_maxheight_accordion_organisationunit;
                pt.util.dimension.panel.setHeight(h);
                treePanel.setHeight(this.getHeight() - pt.conf.layout.west_fill_accordion_organisationunit);
            },
            items: [
                {
                    layout: 'column',
                    bodyStyle: 'border:0 none',
                    style: 'padding-bottom:2px',
                    items: [
                        toolPanel,
                        {
                            width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding - 38,
                            layout: 'column',
                            bodyStyle: 'border:0 none',
                            items: [
                                userOrganisationUnit,
                                userOrganisationUnitChildren,
                                userOrganisationUnitGrandChildren,
                                organisationUnitLevel,
                                organisationUnitGroup
                            ]
                        }
                    ]
                },
                treePanel
            ],
            listeners: {
                added: function() {
                    pt.cmp.dimension.panels.push(this);
                },
                expand: function(p) {
                    p.onExpand();
                }
            }
        };

		getDimensionPanels = function(dimensions, iconCls) {
			var	getAvailableStore,
				getSelectedStore,

				createPanel,
				getPanels;

			getAvailableStore = function(dimension) {
				return Ext.create('Ext.data.Store', {
					fields: ['id', 'name'],
					proxy: {
						type: 'ajax',
						url: pt.init.contextPath + '/api/dimensions/' + dimension.id + '.json',
						reader: {
							type: 'json',
							root: 'items'
						}
					},
					isLoaded: false,
					storage: {},
					sortStore: function() {
						this.sort('name', 'ASC');
					},
					reset: function() {
						if (this.isLoaded) {
							this.removeAll();
							pt.util.store.loadFromStorage(this);
							this.sortStore();
						}
					},
					listeners: {
						load: function(s) {
							s.isLoaded = true;
							pt.util.store.addToStorage(s);
						}
					}
				});
			};

			getSelectedStore = function() {
				return Ext.create('Ext.data.Store', {
					fields: ['id', 'name'],
					data: []
				});
			};

			createPanel = function(dimension) {
				var getAvailable,
					getSelected,

					availableStore,
					selectedStore,
					available,
					selected,

					panel;

				getAvailable = function(availableStore) {
					return Ext.create('Ext.ux.form.MultiSelect', {
						cls: 'pt-toolbar-multiselect-left',
						width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
						valueField: 'id',
						displayField: 'name',
						store: availableStore,
						tbar: [
							{
								xtype: 'label',
								text: PT.i18n.available,
								cls: 'pt-toolbar-multiselect-left-label'
							},
							'->',
							{
								xtype: 'button',
								icon: 'images/arrowright.png',
								width: 22,
								handler: function() {
									pt.util.multiselect.select(available, selected);
								}
							},
							{
								xtype: 'button',
								icon: 'images/arrowrightdouble.png',
								width: 22,
								handler: function() {
									pt.util.multiselect.selectAll(available, selected);
								}
							}
						],
						listeners: {
							afterrender: function() {
								this.boundList.on('itemdblclick', function() {
									pt.util.multiselect.select(available, selected);
								}, this);
							}
						}
					});
				};

				getSelected = function(selectedStore) {
					return Ext.create('Ext.ux.form.MultiSelect', {
						cls: 'pt-toolbar-multiselect-right',
						width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
						valueField: 'id',
						displayField: 'name',
						ddReorder: true,
						store: selectedStore,
						tbar: [
							{
								xtype: 'button',
								icon: 'images/arrowleftdouble.png',
								width: 22,
								handler: function() {
									pt.util.multiselect.unselectAll(available, selected);
								}
							},
							{
								xtype: 'button',
								icon: 'images/arrowleft.png',
								width: 22,
								handler: function() {
									pt.util.multiselect.unselect(available, selected);
								}
							},
							'->',
							{
								xtype: 'label',
								text: PT.i18n.selected,
								cls: 'pt-toolbar-multiselect-right-label'
							}
						],
						listeners: {
							afterrender: function() {
								this.boundList.on('itemdblclick', function() {
									pt.util.multiselect.unselect(available, selected);
								}, this);
							}
						}
					});
				};

				availableStore = getAvailableStore(dimension);
				selectedStore = getSelectedStore();

				dimensionIdAvailableStoreMap[dimension.id] = availableStore;
				dimensionIdSelectedStoreMap[dimension.id] = selectedStore;

				available = getAvailable(availableStore);
				selected = getSelected(selectedStore);

				availableStore.on('load', function() {
					pt.util.multiselect.filterAvailable(available, selected);
				});

				panel = {
					xtype: 'panel',
					title: '<div class="' + iconCls + '">' + dimension.name + '</div>',
					hideCollapseTool: true,
					availableStore: availableStore,
					selectedStore: selectedStore,
					getDimension: function() {
						var config = {
							dimension: dimension.id,
							items: []
						};

						selectedStore.each( function(r) {
							config.items.push({id: r.data.id});
						});

						return config.items.length ? config : null;
					},
					onExpand: function() {
						if (!availableStore.isLoaded) {
							availableStore.load();
						}

						var h = pt.viewport.westRegion.hasScrollbar ?
							pt.conf.layout.west_scrollbarheight_accordion_group : pt.conf.layout.west_maxheight_accordion_group;
						pt.util.dimension.panel.setHeight(h);

						pt.util.multiselect.setHeight(
							[available, selected],
							this,
							pt.conf.layout.west_fill_accordion_dataset
						);
					},
					items: [
						{
							xtype: 'panel',
							layout: 'column',
							bodyStyle: 'border-style:none',
							items: [
								available,
								selected
							]
						}
					],
					listeners: {
						added: function() {
							pt.cmp.dimension.panels.push(this);
						},
						expand: function(p) {
							p.onExpand();
						}
					}
				};

				return panel;
			};

			getPanels = function() {
				var panels = [];

				for (var i = 0, panel; i < dimensions.length; i++) {
					panel = createPanel(dimensions[i]);

					panels.push(panel);
				}

				return panels;
			};

			return getPanels();
		};

		update = function() {
			var config = pt.engine.getLayoutConfig(),
				layout = pt.api.layout.Layout(config);

			if (!layout) {
				return;
			}

			pt.engine.createTable(layout, pt);
		};

		accordionBody = Ext.create('Ext.panel.Panel', {
			layout: 'accordion',
			activeOnTop: true,
			cls: 'pt-accordion',
			bodyStyle: 'border:0 none; margin-bottom:2px',
			height: 700,
			items: function() {
				var panels = [
					indicator,
					dataElement,
					dataSet,
					period,
					organisationUnit
				],
				dims = Ext.clone(pt.init.dimensions);

				pt.util.array.sortObjectsByString(dims);

				panels = panels.concat(getDimensionPanels(dims, 'pt-panel-title-dimension'));

				last = panels[panels.length - 1];
				last.cls = 'pt-accordion-last';

				return panels;
			}()
		});

		accordion = Ext.create('Ext.panel.Panel', {
			bodyStyle: 'border-style:none; padding:2px; padding-bottom:0; overflow-y:scroll;',
			items: accordionBody,
			listeners: {
				added: function() {
					pt.cmp.dimension.accordion = this;
				}
			}
		});

		westRegion = Ext.create('Ext.panel.Panel', {
			region: 'west',
			preventHeader: true,
			collapsible: true,
			collapseMode: 'mini',
			width: function() {
				if (Ext.isWebKit) {
					return pt.conf.layout.west_width + 8;
				}
				else {
					if (Ext.isLinux && Ext.isGecko) {
						return pt.conf.layout.west_width + 13;
					}
					return pt.conf.layout.west_width + 17;
				}
			}(),
			items: accordion
		});

		layoutButton = Ext.create('Ext.button.Button', {
			text: 'Layout',
			menu: {},
			handler: function() {
				if (!pt.viewport.layoutWindow) {
					pt.viewport.layoutWindow = PT.app.LayoutWindow(pt);
				}

				pt.viewport.layoutWindow.show();
			}
		});

		optionsButton = Ext.create('Ext.button.Button', {
			text: 'Options',
			menu: {},
			handler: function() {
				if (!pt.viewport.optionsWindow) {
					pt.viewport.optionsWindow = PT.app.OptionsWindow();
				}

				pt.viewport.optionsWindow.show();
			}
		});

		favoriteButton = Ext.create('Ext.button.Button', {
			text: 'Favorites',
			menu: {},
			handler: function() {
				if (pt.viewport.favoriteWindow) {
					pt.viewport.favoriteWindow.destroy();
				}

				pt.viewport.favoriteWindow = PT.app.FavoriteWindow();
				pt.viewport.favoriteWindow.show();
			}
		});

		openTableLayoutTab = function(type, isNewTab) {
			if (pt.init.contextPath && pt.paramString) {
				var colDimNames = Ext.clone(pt.xLayout.columnDimensionNames),
					colObjNames = pt.xLayout.columnObjectNames,
					rowDimNames = Ext.clone(pt.xLayout.rowDimensionNames),
					rowObjNames = pt.xLayout.rowObjectNames,
					dc = pt.conf.finals.dimension.operand.objectName,
					co = pt.conf.finals.dimension.category.dimensionName,
					columnNames = Ext.Array.clean([].concat(colDimNames, (Ext.Array.contains(colObjNames, dc) ? co : []))),
					rowNames = Ext.Array.clean([].concat(rowDimNames, (Ext.Array.contains(rowObjNames, dc) ? co : []))),
					url = '';

				url += pt.init.contextPath + '/api/analytics.' + type;
				url += pt.engine.getParamString(pt.xLayout);
				url += '&tableLayout=true';
				url += '&columns=' + columnNames.join(';');
				url += '&rows=' + rowNames.join(';');

				window.open(url, isNewTab ? '_blank' : '_top');
			}
		};

		downloadButton = Ext.create('Ext.button.Button', {
			text: 'Download',
			disabled: true,
			menu: {
				cls: 'pt-menu',
				shadow: false,
				showSeparator: false,
				items: [
					{
						xtype: 'label',
						text: PT.i18n.table_layout,
						style: 'padding:7px 5px 5px 7px; font-weight:bold; border:0 none'
					},
					{
						text: 'Microsoft Excel (.xls)',
						iconCls: 'pt-menu-item-tablelayout',
						handler: function() {
							openTableLayoutTab('xls');
						}
					},
					{
						text: 'CSV (.csv)',
						iconCls: 'pt-menu-item-tablelayout',
						handler: function() {
							openTableLayoutTab('csv');
						}
					},
					{
						text: 'HTML (.html)',
						iconCls: 'pt-menu-item-tablelayout',
						handler: function() {
							openTableLayoutTab('html', true);
						}
					},
					{
						xtype: 'label',
						text: PT.i18n.plain_data_sources,
						style: 'padding:7px 5px 5px 7px; font-weight:bold'
					},
					{
						text: 'JSON',
						iconCls: 'pt-menu-item-datasource',
						handler: function() {
							if (pt.init.contextPath && pt.paramString) {
								window.open(pt.init.contextPath + '/api/analytics.json' + pt.paramString, '_blank');
							}
						}
					},
					{
						text: 'XML',
						iconCls: 'pt-menu-item-datasource',
						handler: function() {
							if (pt.init.contextPath && pt.paramString) {
								window.open(pt.init.contextPath + '/api/analytics.xml' + pt.paramString, '_blank');
							}
						}
					},
					{
						text: 'Microsoft Excel',
						iconCls: 'pt-menu-item-datasource',
						handler: function() {
							if (pt.init.contextPath && pt.paramString) {
								window.location.href = pt.init.contextPath + '/api/analytics.xls' + pt.paramString;
							}
						}
					},
					{
						text: 'CSV',
						iconCls: 'pt-menu-item-datasource',
						handler: function() {
							if (pt.init.contextPath && pt.paramString) {
								window.location.href = pt.init.contextPath + '/api/analytics.csv' + pt.paramString;
							}
						}
					},
					{
						text: 'JRXML',
						iconCls: 'pt-menu-item-datasource',
						handler: function() {
							if (pt.init.contextPath && pt.paramString) {
								window.open(pt.init.contextPath + '/api/analytics.jrxml' + pt.paramString, '_blank');
							}
						}
					}
				],
				listeners: {
					afterrender: function() {
						this.getEl().addCls('pt-toolbar-btn-menu');
					}
				}
			}
		});

		interpretationButton = Ext.create('Ext.button.Button', {
			text: PT.i18n.share,
			menu: {},
			disabled: true,
			xable: function() {
				if (pt.favorite) {
					this.enable();
					this.disabledTooltip.destroy();
				}
				else {
					if (pt.xLayout) {
						this.disable();
						this.createTooltip();
					}
				}
			},
			disabledTooltip: null,
			createTooltip: function() {
				this.disabledTooltip = Ext.create('Ext.tip.ToolTip', {
					target: this.getEl(),
					html: PT.i18n.save_load_favorite_before_sharing,
					'anchor': 'bottom'
				});
			},
			handler: function() {
				if (pt.viewport.interpretationWindow) {
					pt.viewport.interpretationWindow.destroy();
				}

				pt.viewport.interpretationWindow = PT.app.InterpretationWindow();

				if (pt.viewport.interpretationWindow) {
					pt.viewport.interpretationWindow.show();
				}
			}
		});

		defaultButton = Ext.create('Ext.button.Button', {
			text: PT.i18n.table,
			iconCls: 'pt-button-icon-table',
			toggleGroup: 'module',
			pressed: true,
			handler: function() {
				if (!this.pressed) {
					this.toggle();
				}
			}
		});

		centerRegion = Ext.create('Ext.panel.Panel', {
			region: 'center',
			bodyStyle: 'padding:1px',
			autoScroll: true,
			tbar: {
				defaults: {
					height: 26
				},
				items: [
					{
						text: '<<<',
						handler: function(b) {
							var text = b.getText();
							text = text === '<<<' ? '>>>' : '<<<';
							b.setText(text);

							westRegion.toggleCollapse();
						}
					},
					{
						text: '<b>' + PT.i18n.update + '</b>',
						handler: function() {
							update();
						}
					},
					layoutButton,
					optionsButton,
					{
						xtype: 'tbseparator',
						height: 18,
						style: 'border-color:transparent; border-right-color:#d1d1d1; margin-right:4px',
					},
					favoriteButton,
					downloadButton,
					interpretationButton,
					'->',
					defaultButton,
					{
						text: PT.i18n.chart,
						iconCls: 'pt-button-icon-chart',
						toggleGroup: 'module',
						menu: {},
						handler: function(b) {
							b.menu = Ext.create('Ext.menu.Menu', {
								closeAction: 'destroy',
								shadow: false,
								showSeparator: false,
								items: [
									{
										text: 'Go to charts' + '&nbsp;&nbsp;', //i18n
										cls: 'pt-menu-item-noicon',
										handler: function() {
											window.location.href = pt.init.contextPath + '/dhis-web-visualizer/app/index.html';
										}
									},
									'-',
									{
										text: 'Open this table as chart' + '&nbsp;&nbsp;', //i18n
										cls: 'pt-menu-item-noicon',
										disabled: !(PT.isSessionStorage && pt.layout),
										handler: function() {
											if (PT.isSessionStorage) {
												pt.layout.parentGraphMap = treePanel.getParentGraphMap();
												pt.engine.setSessionStorage('analytical', pt.layout, pt.init.contextPath + '/dhis-web-visualizer/app/index.html?s=analytical');
											}
										}
									},
									{
										text: 'Open last chart' + '&nbsp;&nbsp;', //i18n
										cls: 'pt-menu-item-noicon',
										disabled: !(PT.isSessionStorage && JSON.parse(sessionStorage.getItem('dhis2')) && JSON.parse(sessionStorage.getItem('dhis2'))['chart']),
										handler: function() {
											window.location.href = pt.init.contextPath + '/dhis-web-visualizer/app/index.html?s=chart';
										}
									}
								],
								listeners: {
									show: function() {
										pt.util.window.setAnchorPosition(b.menu, b);
									},
									hide: function() {
										b.menu.destroy();
										defaultButton.toggle();
									},
									destroy: function(m) {
										b.menu = null;
									}
								}
							});

							b.menu.show();
						}
					},
					{
						text: PT.i18n.map,
						iconCls: 'pt-button-icon-map',
						toggleGroup: 'module',
						menu: {},
						handler: function(b) {
							b.menu = Ext.create('Ext.menu.Menu', {
								closeAction: 'destroy',
								shadow: false,
								showSeparator: false,
								items: [
									{
										text: 'Go to maps' + '&nbsp;&nbsp;', //i18n
										cls: 'pt-menu-item-noicon',
										handler: function() {
											window.location.href = pt.init.contextPath + '/dhis-web-mapping/app/index.html';
										}
									},
									'-',
									{
										text: 'Open this table as map' + '&nbsp;&nbsp;', //i18n
										cls: 'pt-menu-item-noicon',
										disabled: !(PT.isSessionStorage && pt.layout),
										handler: function() {
											if (PT.isSessionStorage) {
												pt.layout.parentGraphMap = treePanel.getParentGraphMap();
												pt.engine.setSessionStorage('analytical', pt.layout, pt.init.contextPath + '/dhis-web-mapping/app/index.html?s=analytical');
											}
										}
									},
									{
										text: 'Open last map' + '&nbsp;&nbsp;', //i18n
										cls: 'pt-menu-item-noicon',
										disabled: !(PT.isSessionStorage && JSON.parse(sessionStorage.getItem('dhis2')) && JSON.parse(sessionStorage.getItem('dhis2'))['map']),
										handler: function() {
											window.location.href = pt.init.contextPath + '/dhis-web-mapping/app/index.html?s=map';
										}
									}
								],
								listeners: {
									show: function() {
										pt.util.window.setAnchorPosition(b.menu, b);
									},
									hide: function() {
										b.menu.destroy();
										defaultButton.toggle();
									},
									destroy: function(m) {
										b.menu = null;
									}
								}
							});

							b.menu.show();
						}
					},
					{
						xtype: 'tbseparator',
						height: 18,
						style: 'border-color:transparent; border-right-color:#d1d1d1; margin-right:4px',
					},
					{
						xtype: 'button',
						text: PT.i18n.home,
						handler: function() {
							window.location.href = pt.init.contextPath + '/dhis-web-commons-about/redirect.action';
						}
					}
				]
			},
			listeners: {
				afterrender: function(p) {
					var liStyle = 'padding:3px 10px; color:#333',
						html = '';

					html += '<div style="padding:20px">';
					html += '<div style="font-size:14px; padding-bottom:8px">' + PT.i18n.example1 + '</div>';
					html += '<div style="' + liStyle + '">- ' + PT.i18n.example2 + '</div>';
					html += '<div style="' + liStyle + '">- ' + PT.i18n.example3 + '</div>';
					html += '<div style="' + liStyle + '">- ' + PT.i18n.example4 + '</div>';
					html += '<div style="font-size:14px; padding-top:20px; padding-bottom:8px">' + PT.i18n.example5 + '</div>';
					html += '<div style="' + liStyle + '">- ' + PT.i18n.example6 + '</div>';
					html += '<div style="' + liStyle + '">- ' + PT.i18n.example7 + '</div>';
					html += '<div style="' + liStyle + '">- ' + PT.i18n.example8 + '</div>';
					html += '</div>';

					p.update(html);
				}
			}
		});

		setGui = function(layout, xLayout, updateGui, isFavorite) {
			var dimensions = [].concat(layout.columns || [], layout.rows || [], layout.filters || []),
				dimMap = pt.service.layout.getObjectNameDimensionMap(dimensions),
				recMap = pt.service.layout.getObjectNameDimensionItemsMap(dimensions),
				graphMap = layout.parentGraphMap,
				objectName,
				periodRecords,
				fixedPeriodRecords = [],
				dimNames = [],
				isOu = false,
				isOuc = false,
				isOugc = false,
				levels = [],
				groups = [],
				orgunits = [];

			// State
			downloadButton.enable();

			if (isFavorite) {
				interpretationButton.enable();
			}

			// Set gui
			if (!updateGui) {
				return;
			}

			// Indicators
			pt.store.indicatorSelected.removeAll();
			objectName = dimConf.indicator.objectName;
			if (dimMap[objectName]) {
				pt.store.indicatorSelected.add(Ext.clone(recMap[objectName]));
				pt.util.multiselect.filterAvailable({store: pt.store.indicatorAvailable}, {store: pt.store.indicatorSelected});
			}

			// Data elements
			pt.store.dataElementSelected.removeAll();
			objectName = dimConf.dataElement.objectName;
			if (dimMap[objectName]) {
				pt.store.dataElementSelected.add(Ext.clone(recMap[objectName]));
				pt.util.multiselect.filterAvailable({store: pt.store.dataElementAvailable}, {store: pt.store.dataElementSelected});
				dataElementDetailLevel.setValue(objectName);
			}

			// Operands
			objectName = dimConf.operand.objectName;
			if (dimMap[objectName]) {
				pt.store.dataElementSelected.add(Ext.clone(recMap[objectName]));
				pt.util.multiselect.filterAvailable({store: pt.store.dataElementAvailable}, {store: pt.store.dataElementSelected});
				dataElementDetailLevel.setValue(objectName);
			}

			// Data sets
			pt.store.dataSetSelected.removeAll();
			objectName = dimConf.dataSet.objectName;
			if (dimMap[objectName]) {
				pt.store.dataSetSelected.add(Ext.clone(recMap[objectName]));
				pt.util.multiselect.filterAvailable({store: pt.store.dataSetAvailable}, {store: pt.store.dataSetSelected});
			}

			// Periods
			pt.store.fixedPeriodSelected.removeAll();
			pt.util.checkbox.setAllFalse();
			periodRecords = recMap[dimConf.period.objectName] || [];
			for (var i = 0, peroid, checkbox; i < periodRecords.length; i++) {
				period = periodRecords[i];
				checkbox = relativePeriod.valueComponentMap[period.id];
				if (checkbox) {
					checkbox.setValue(true);
				}
				else {
					fixedPeriodRecords.push(period);
				}
			}
			pt.store.fixedPeriodSelected.add(fixedPeriodRecords);
			pt.util.multiselect.filterAvailable({store: pt.store.fixedPeriodAvailable}, {store: pt.store.fixedPeriodSelected});

			// Group sets
			for (var key in dimensionIdSelectedStoreMap) {
				if (dimensionIdSelectedStoreMap.hasOwnProperty(key)) {
					var a = dimensionIdAvailableStoreMap[key],
						s = dimensionIdSelectedStoreMap[key];

					if (s.getCount() > 0) {
						a.reset();
						s.removeAll();
					}

					if (recMap[key]) {
						s.add(recMap[key]);
						pt.util.multiselect.filterAvailable({store: a}, {store: s});
					}
				}
			}

			// Layout
			pt.viewport.dimensionStore.reset(true);
			pt.viewport.colStore.removeAll();
			pt.viewport.rowStore.removeAll();
			pt.viewport.filterStore.removeAll();

			if (layout.columns) {
				dimNames = [];

				for (var i = 0, dim; i < layout.columns.length; i++) {
					dim = dimConf.objectNameMap[layout.columns[i].dimension];

					if (!Ext.Array.contains(dimNames, dim.dimensionName)) {
						pt.viewport.colStore.add({
							id: dim.dimensionName,
							name: dimConf.objectNameMap[dim.dimensionName].name
						});

						dimNames.push(dim.dimensionName);
					}

					pt.viewport.dimensionStore.remove(pt.viewport.dimensionStore.getById(dim.dimensionName));
				}
			}

			if (layout.rows) {
				dimNames = [];

				for (var i = 0, dim; i < layout.rows.length; i++) {
					dim = dimConf.objectNameMap[layout.rows[i].dimension];

					if (!Ext.Array.contains(dimNames, dim.dimensionName)) {
						pt.viewport.rowStore.add({
							id: dim.dimensionName,
							name: dimConf.objectNameMap[dim.dimensionName].name
						});

						dimNames.push(dim.dimensionName);
					}

					pt.viewport.dimensionStore.remove(pt.viewport.dimensionStore.getById(dim.dimensionName));
				}
			}

			if (layout.filters) {
				dimNames = [];

				for (var i = 0, dim; i < layout.filters.length; i++) {
					dim = dimConf.objectNameMap[layout.filters[i].dimension];

					if (!Ext.Array.contains(dimNames, dim.dimensionName)) {
						pt.viewport.filterStore.add({
							id: dim.dimensionName,
							name: dimConf.objectNameMap[dim.dimensionName].name
						});

						dimNames.push(dim.dimensionName);
					}

					pt.viewport.dimensionStore.remove(pt.viewport.dimensionStore.getById(dim.dimensionName));
				}
			}

			// Options
			if (pt.viewport.optionsWindow) {
				pt.viewport.optionsWindow.setOptions(layout);
			}

			// Organisation units
			if (recMap[dimConf.organisationUnit.objectName]) {
				for (var i = 0, ouRecords = recMap[dimConf.organisationUnit.objectName]; i < ouRecords.length; i++) {
					if (ouRecords[i].id === 'USER_ORGUNIT') {
						isOu = true;
					}
					else if (ouRecords[i].id === 'USER_ORGUNIT_CHILDREN') {
						isOuc = true;
					}
					else if (ouRecords[i].id === 'USER_ORGUNIT_GRANDCHILDREN') {
						isOugc = true;
					}
					else if (ouRecords[i].id.substr(0,5) === 'LEVEL') {
						levels.push(parseInt(ouRecords[i].id.split('-')[1]));
					}
					else if (ouRecords[i].id.substr(0,8) === 'OU_GROUP') {
						groups.push(ouRecords[i].id.split('-')[1]);
					}
					else {
						orgunits.push(ouRecords[i].id);
					}
				}

				if (levels.length) {
					toolMenu.clickHandler('level');
					organisationUnitLevel.setValue(levels);
				}
				else if (groups.length) {
					toolMenu.clickHandler('group');
					organisationUnitGroup.setValue(groups);
				}
				else {
					toolMenu.clickHandler('orgunit');
					userOrganisationUnit.setValue(isOu);
					userOrganisationUnitChildren.setValue(isOuc);
					userOrganisationUnitGrandChildren.setValue(isOugc);
				}

				if (!(isOu || isOuc || isOugc)) {
					if (Ext.isObject(graphMap)) {
						treePanel.selectGraphMap(graphMap);
					}
				}
			}
			else {
				treePanel.reset();
			}
		};

		viewport = Ext.create('Ext.container.Viewport', {
			layout: 'border',
			accordion: accordion,
			accordionBody: accordionBody,
			westRegion: westRegion,
			centerRegion: centerRegion,
			updateViewport: update,
			layoutButton: layoutButton,
			optionsButton: optionsButton,
			favoriteButton: favoriteButton,
			downloadButton: downloadButton,
			interpretationButton: interpretationButton,
			userOrganisationUnit: userOrganisationUnit,
			userOrganisationUnitChildren: userOrganisationUnitChildren,
			dataElementDetailLevel: dataElementDetailLevel,
			treePanel: treePanel,
			setGui: setGui,
			items: [
				westRegion,
				centerRegion
			],
			listeners: {
				render: function(vp) {
					pt.viewport = vp;

					pt.viewport.layoutWindow = PT.app.LayoutWindow();
					pt.viewport.layoutWindow.hide();
					pt.viewport.optionsWindow = PT.app.OptionsWindow();
					pt.viewport.optionsWindow.hide();
				},
				afterrender: function() {
					pt.init.afterRender();
				}
			}
		});

		addListeners = function() {
			pt.store.indicatorAvailable.on('load', function() {
				pt.util.multiselect.filterAvailable(indicatorAvailable, indicatorSelected);
			});

			pt.store.dataElementAvailable.on('load', function() {
				pt.util.multiselect.filterAvailable(dataElementAvailable, dataElementSelected);
			});

			pt.store.dataSetAvailable.on('load', function(s) {
				pt.util.multiselect.filterAvailable(dataSetAvailable, dataSetSelected);
				s.sort('name', 'ASC');
			});
		}();

		return viewport;
	};

	initialize = function() {

		// ext configuration
		Ext.QuickTips.init();

		Ext.override(Ext.LoadMask, {
			onHide: function() {
				this.callParent();
			}
		});

		// right click handler
		document.body.oncontextmenu = function() {
			return false;
		};

		Ext.Ajax.request({
			url: '../initialize.action',
			success: function(r) {
				var init = Ext.decode(r.responseText);

				PT.i18n = init.i18n;

				pt = PT.core.getInstance(init);

				PT.app.extendInstance(pt);

				pt.viewport = createViewport();
			}
		});
	}();
});
