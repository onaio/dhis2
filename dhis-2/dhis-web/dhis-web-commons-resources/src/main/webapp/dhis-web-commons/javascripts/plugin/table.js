Ext.onReady( function() {

	// ext config
	Ext.Ajax.method = 'GET';

	// pt
	PT = {
		core: {
			instances: []
		},
		i18n: {},
		isDebug: false,
		isSessionStorage: 'sessionStorage' in window && window['sessionStorage'] !== null
	};

	// CORE

	PT.core.getInstance = function(init) {
        var conf = {},
            util = {},
            api = {},
            service = {},
            engine = {},
            dimConf;

		// conf
		(function() {
			conf.finals = {
				url: {
					path_module: '/dhis-web-pivot/',
					path_api: '/api/',
					path_commons: '/dhis-web-commons-ajax-json/',
					data_get: 'chartValues.json',
					indicator_get: 'indicatorGroups/',
					indicator_getall: 'indicators.json?paging=false&links=false',
					indicatorgroup_get: 'indicatorGroups.json?paging=false&links=false',
					dataelement_get: 'dataElementGroups/',
					dataelement_getall: 'dataElements.json?paging=false&links=false',
					dataelementgroup_get: 'dataElementGroups.json?paging=false&links=false',
					dataset_get: 'dataSets.json?paging=false&links=false',
					organisationunit_getbygroup: 'getOrganisationUnitPathsByGroup.action',
					organisationunit_getbylevel: 'getOrganisationUnitPathsByLevel.action',
					organisationunit_getbyids: 'getOrganisationUnitPaths.action',
					organisationunitgroup_getall: 'organisationUnitGroups.json?paging=false&links=false',
					organisationunitgroupset_get: 'getOrganisationUnitGroupSetsMinified.action',
					organisationunitlevel_getall: 'organisationUnitLevels.json?paging=false&links=false&viewClass=detailed',
					organisationunitchildren_get: 'getOrganisationUnitChildren.action',
					favorite_addorupdate: 'addOrUpdateChart.action',
					favorite_addorupdatesystem: 'addOrUpdateSystemChart.action',
					favorite_updatename: 'updateChartName.action',
					favorite_get: 'charts/',
					favorite_getall: 'getSystemAndCurrentUserCharts.action',
					favorite_delete: 'deleteCharts.action'
				},
				dimension: {
					data: {
						value: 'data',
						name: PT.i18n.data,
						dimensionName: 'dx',
						objectName: 'dx',
						warning: {
							filter: '...'//PT.i18n.wm_multiple_filter_ind_de
						}
					},
					category: {
						name: PT.i18n.categories,
						dimensionName: 'co',
						objectName: 'co',
					},
					indicator: {
						value: 'indicators',
						name: PT.i18n.indicators,
						dimensionName: 'dx',
						objectName: 'in'
					},
					dataElement: {
						value: 'dataElements',
						name: PT.i18n.data_elements,
						dimensionName: 'dx',
						objectName: 'de'
					},
					operand: {
						value: 'operand',
						name: 'Operand',
						dimensionName: 'dx',
						objectName: 'dc'
					},
					dataSet: {
						value: 'dataSets',
						name: PT.i18n.data_sets,
						dimensionName: 'dx',
						objectName: 'ds'
					},
					period: {
						value: 'period',
						name: PT.i18n.periods,
						dimensionName: 'pe',
						objectName: 'pe'
					},
					fixedPeriod: {
						value: 'periods'
					},
					relativePeriod: {
						value: 'relativePeriods'
					},
					organisationUnit: {
						value: 'organisationUnits',
						name: PT.i18n.organisation_units,
						dimensionName: 'ou',
						objectName: 'ou'
					},
					dimension: {
						value: 'dimension'
						//objectName: 'di'
					},
					value: {
						value: 'value'
					}
				},
				root: {
					id: 'root'
				}
			};

			dimConf = conf.finals.dimension;

			dimConf.objectNameMap = {};
			dimConf.objectNameMap[dimConf.data.objectName] = dimConf.data;
			dimConf.objectNameMap[dimConf.indicator.objectName] = dimConf.indicator;
			dimConf.objectNameMap[dimConf.dataElement.objectName] = dimConf.dataElement;
			dimConf.objectNameMap[dimConf.operand.objectName] = dimConf.operand;
			dimConf.objectNameMap[dimConf.dataSet.objectName] = dimConf.dataSet;
			dimConf.objectNameMap[dimConf.category.objectName] = dimConf.category;
			dimConf.objectNameMap[dimConf.period.objectName] = dimConf.period;
			dimConf.objectNameMap[dimConf.organisationUnit.objectName] = dimConf.organisationUnit;
			dimConf.objectNameMap[dimConf.dimension.objectName] = dimConf.dimension;

			conf.period = {
				relativePeriods: {
					'LAST_WEEK': 1,
					'LAST_4_WEEKS': 4,
					'LAST_12_WEEKS': 12,
					'LAST_MONTH': 1,
					'LAST_3_MONTHS': 3,
					'LAST_BIMONTH': 1,
					'LAST_6_BIMONTHS': 6,
					'LAST_12_MONTHS': 12,
					'LAST_QUARTER': 1,
					'LAST_4_QUARTERS': 4,
					'LAST_SIX_MONTH': 1,
					'LAST_2_SIXMONTHS': 2,
					'LAST_FINANCIAL_YEAR': 1,
					'LAST_5_FINANCIAL_YEARS': 6,
					'THIS_YEAR': 1,
					'LAST_YEAR': 1,
					'LAST_5_YEARS': 5
				},
				relativePeriodValueKeys: {
					'LAST_WEEK': 'lastWeek',
					'LAST_4_WEEKS': 'last4Weeks',
					'LAST_12_WEEKS': 'last12Weeks',
					'LAST_MONTH': 'lastMonth',
					'LAST_3_MONTHS': 'last3Months',
					'LAST_12_MONTHS': 'last12Months',
					'LAST_BIMONTH': 'lastBimonth',
					'LAST_6_BIMONTHS': 'last6BiMonths',
					'LAST_QUARTER': 'lastQuarter',
					'LAST_4_QUARTERS': 'last4Quarters',
					'LAST_SIX_MONTH': 'lastSixMonth',
					'LAST_2_SIXMONTHS': 'last2SixMonths',
					'LAST_FINANCIAL_YEAR': 'lastFinancialYear',
					'LAST_5_FINANCIAL_YEARS': 'last5FinancialYears',
					'THIS_YEAR': 'thisYear',
					'LAST_YEAR': 'lastYear',
					'LAST_5_YEARS': 'last5Years'
				},
				relativePeriodParamKeys: {
					'lastWeek': 'LAST_WEEK',
					'last4Weeks': 'LAST_4_WEEKS',
					'last12Weeks': 'LAST_12_WEEKS',
					'lastMonth': 'LAST_MONTH',
					'last3Months': 'LAST_3_MONTHS',
					'last12Months': 'LAST_12_MONTHS',
					'lastBimonth': 'LAST_BIMONTH',
					'last6BiMonths': 'LAST_6_BIMONTHS',
					'lastQuarter': 'LAST_QUARTER',
					'last4Quarters': 'LAST_4_QUARTERS',
					'lastSixMonth': 'LAST_SIX_MONTH',
					'last2SixMonths': 'LAST_2_SIXMONTHS',
					'lastFinancialYear': 'LAST_FINANCIAL_YEAR',
					'last5FinancialYears': 'LAST_5_FINANCIAL_YEARS',
					'thisYear': 'THIS_YEAR',
					'lastYear': 'LAST_YEAR',
					'last5Years': 'LAST_5_YEARS'
				},
				periodTypes: [
					{id: 'Daily', name: 'Daily'},
					{id: 'Weekly', name: 'Weekly'},
					{id: 'Monthly', name: 'Monthly'},
					{id: 'BiMonthly', name: 'BiMonthly'},
					{id: 'Quarterly', name: 'Quarterly'},
					{id: 'SixMonthly', name: 'SixMonthly'},
					{id: 'Yearly', name: 'Yearly'},
					{id: 'FinancialOct', name: 'FinancialOct'},
					{id: 'FinancialJuly', name: 'FinancialJuly'},
					{id: 'FinancialApril', name: 'FinancialApril'}
				]
			};

			conf.layout = {
				west_width: 424,
				west_fieldset_width: 416,
				west_width_padding: 4,
				west_fill: 2,
				west_fill_accordion_indicator: 59,
				west_fill_accordion_dataelement: 59,
				west_fill_accordion_dataset: 33,
				west_fill_accordion_period: 296,
				west_fill_accordion_organisationunit: 62,
				west_maxheight_accordion_indicator: 400,
				west_maxheight_accordion_dataelement: 400,
				west_maxheight_accordion_dataset: 400,
				west_maxheight_accordion_period: 513,
				west_maxheight_accordion_organisationunit: 900,
				west_maxheight_accordion_group: 340,
				west_maxheight_accordion_options: 449,
				west_scrollbarheight_accordion_indicator: 300,
				west_scrollbarheight_accordion_dataelement: 300,
				west_scrollbarheight_accordion_dataset: 300,
				west_scrollbarheight_accordion_period: 450,
				west_scrollbarheight_accordion_organisationunit: 450,
				west_scrollbarheight_accordion_group: 300,
				east_tbar_height: 31,
				east_gridcolumn_height: 30,
				form_label_width: 55,
				window_favorite_ypos: 100,
				window_confirm_width: 250,
				window_share_width: 500,
				grid_favorite_width: 420,
				grid_row_height: 27,
				treepanel_minheight: 135,
				treepanel_maxheight: 400,
				treepanel_fill_default: 310,
				treepanel_toolbar_menu_width_group: 140,
				treepanel_toolbar_menu_width_level: 120,
				multiselect_minheight: 100,
				multiselect_maxheight: 250,
				multiselect_fill_default: 345,
				multiselect_fill_reportingrates: 315
			};

			conf.pivot = {
				digitGroupSeparator: {
					'comma': ',',
					'space': ' '
				},
				displayDensity: {
					'compact': '3px',
					'normal': '5px',
					'comfortable': '10px',
				},
				fontSize: {
					'small': '10px',
					'normal': '11px',
					'large': '13px'
				}
			};
		}());

		// util
		(function() {
			util.object = {
				getLength: function(object) {
					var size = 0;

					for (var key in object) {
						if (object.hasOwnProperty(key)) {
							size++;
						}
					}

					return size;
				}
			};

			util.mask = {
                showMask: function(cmp, msg) {
                    msg = msg || 'Loading..';

                    if (cmp.mask && cmp.mask.destroy) {
                        cmp.mask.destroy();
                    }
                    cmp.mask = new Ext.LoadMask(cmp, {msg: msg});
                    cmp.mask.show();
                },
                hideMask: function(cmp) {
                    if (cmp.mask && cmp.mask.hide) {
                        cmp.mask.hide();
                    }
                }
            };

			util.store = {
				addToStorage: function(s, records) {
					s.each( function(r) {
						if (!s.storage[r.data.id]) {
							s.storage[r.data.id] = {id: r.data.id, name: r.data.name, parent: s.parent};
						}
					});
					if (records) {
						Ext.Array.each(records, function(r) {
							if (!s.storage[r.data.id]) {
								s.storage[r.data.id] = {id: r.data.id, name: r.data.name, parent: s.parent};
							}
						});
					}
				},
				loadFromStorage: function(s) {
					var items = [];
					s.removeAll();
					for (var obj in s.storage) {
						if (s.storage[obj].parent === s.parent) {
							items.push(s.storage[obj]);
						}
					}
					s.add(items);
					s.sort('name', 'ASC');
				},
				containsParent: function(s) {
					for (var obj in s.storage) {
						if (s.storage[obj].parent === s.parent) {
							return true;
						}
					}
					return false;
				}
			};

			util.array = {
				sortDimensions: function(dimensions, key) {
					key = key || 'dimensionName';

					// Sort object order
					Ext.Array.sort(dimensions, function(a,b) {
						if (a[key] < b[key]) {
							return -1;
						}
						if (a[key] > b[key]) {
							return 1;
						}
						return 0;
					});

					// Sort object items, ids
					for (var i = 0, dim; i < dimensions.length; i++) {
						dim = dimensions[i];

						if (dim.items) {
							dimensions[i].items = util.array.sortDimensions(dim.items, 'id');
						}

						if (dim.ids) {
							dimensions[i].ids = dim.ids.sort();
						}
					}

					return dimensions;
				},

				sortObjectsByString: function(array, key) {
					key = key || 'name';
					array.sort( function(a, b) {
						var nameA = a[key].toLowerCase(),
							nameB = b[key].toLowerCase();

						if (nameA < nameB) {
							return -1;
						}
						if (nameA > nameB) {
							return 1;
						}
						return 0;
					});
					return array;
				}
			};

			util.number = {
				getNumberOfDecimals: function(x) {
					var tmp = new String(x);
					return (tmp.indexOf('.') > -1) ? (tmp.length - tmp.indexOf('.') - 1) : 0;
				},

				roundIf: function(x, prec) {
					x = parseFloat(x);
					prec = parseFloat(prec);

					if (Ext.isNumber(x) && Ext.isNumber(prec)) {
						var dec = util.number.getNumberOfDecimals(x);
						return dec > prec ? Ext.Number.toFixed(x, prec) : x;
					}
					return x;
				},

				pp: function(x, nf) {
					nf = nf || 'space';

					if (nf === 'none') {
						return x;
					}

					return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, conf.pivot.digitGroupSeparator[nf]);
				}
			};

			util.str = {
				replaceAll: function(str, find, replace) {
					return str.replace(new RegExp(find, 'g'), replace);
				}
			};

			util.color = {
				hexToRgb: function(hex) {
					var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
					hex = hex.replace(shorthandRegex, function(m, r, g, b) {
						return r + r + g + g + b + b;
					});

					var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
					return result ? {
						r: parseInt(result[1], 16),
						g: parseInt(result[2], 16),
						b: parseInt(result[3], 16)
					} : null;
				},
				getContrast: function(hex) {
					if (Ext.isString(hex)) {
						var rgb = util.color.hexToRgb(hex),
							factor = (rgb.r + rgb.g + rgb.b);

						if (factor < 210) {
							return '#ffffff';
						}
					}

					return '#000000';
				}
			};

			util.message = {
				alert: function(message) {
                    alert(message);
				}
			};
		}());

		// init
		(function() {
			// sort and extend dynamic dimensions
			init.dimensions = util.array.sortObjectsByString(init.dimensions);

			for (var i = 0, dim; i < init.dimensions.length; i++) {
				dim = init.dimensions[i];
				dim.dimensionName = dim.id;
				dim.objectName = conf.finals.dimension.dimension.objectName;
				conf.finals.dimension.objectNameMap[dim.id] = dim;
			}

			// legend set map
			init.idLegendSetMap = {};

			for (var i = 0, set; i < init.legendSets.length; i++) {
				set = init.legendSets[i];
				init.idLegendSetMap[set.id] = set;
			}
		}());

		// api
		(function() {
			api.layout = {};

			api.layout.Record = function(config) {
				var record = {};

				// id: string

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Record config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.id)) {
						alert('Record id is not text: ' + config);
						return;
					}

					record.id = config.id.replace('.', '-');

					if (Ext.isString(config.name)) {
						record.name = config.name;
					}

					return Ext.clone(record);
				}();
			};

			api.layout.Dimension = function(config) {
				var dimension = {};

				// dimension: string

				// items: [Record]

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Dimension config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.dimension)) {
						console.log('Dimension name is not text: ' + config);
						return;
					}

					if (config.dimension !== conf.finals.dimension.category.objectName) {
						var records = [];

						if (!Ext.isArray(config.items)) {
							console.log('Dimension items is not an array: ' + config);
							return;
						}

						for (var i = 0; i < config.items.length; i++) {
							record = api.layout.Record(config.items[i]);

							if (record) {
								records.push(record);
							}
						}

						config.items = records;

						if (!config.items.length) {
							console.log('Dimension has no valid items: ' + config);
							return;
						}
					}

					dimension.dimension = config.dimension;
					dimension.items = config.items;

					return Ext.clone(dimension);
				}();
			};

			api.layout.Layout = function(config) {
				var layout = {},
					getValidatedDimensionArray,
					validateSpecialCases;

				// columns: [Dimension]

				// rows: [Dimension]

				// filters: [Dimension]

				// showTotals: boolean (true)

				// showSubTotals: boolean (true)

				// hideEmptyRows: boolean (false)

				// displayDensity: string ('normal') - 'compact', 'normal', 'comfortable'

				// fontSize: string ('normal') - 'small', 'normal', 'large'

				// digitGroupSeparator: string ('space') - 'none', 'comma', 'space'

				// legendSet: object

				// userOrganisationUnit: boolean (false)

				// userOrganisationUnitChildren: boolean (false)

				// parentGraphMap: object

				// reportingPeriod: boolean (false) //report tables only

				// organisationUnit: boolean (false) //report tables only

				// parentOrganisationUnit: boolean (false) //report tables only

				// regression: boolean (false)

				// cumulative: boolean (false)

				// sortOrder: integer (0) //-1, 0, 1

				// topLimit: integer (100) //5, 10, 20, 50, 100

				getValidatedDimensionArray = function(dimensionArray) {
					var dimensions = [];

					if (!(dimensionArray && Ext.isArray(dimensionArray) && dimensionArray.length)) {
						return;
					}

					for (var i = 0, dimension; i < dimensionArray.length; i++) {
						dimension = api.layout.Dimension(dimensionArray[i]);

						if (dimension) {
							dimensions.push(dimension);
						}
					}

					dimensionArray = dimensions;

					return dimensionArray.length ? dimensionArray : null;
				};

				validateSpecialCases = function() {
					var dimConf = conf.finals.dimension,
						dimensions,
						objectNameDimensionMap = {};

					if (!layout) {
						return;
					}

					dimensions = Ext.Array.clean([].concat(layout.columns, layout.rows, layout.filters));

					for (var i = 0; i < dimensions.length; i++) {
						objectNameDimensionMap[dimensions[i].dimension] = dimensions[i];
					}

					if (layout.filters && layout.filters.length) {
						for (var i = 0; i < layout.filters.length; i++) {

							// Indicators as filter
							if (layout.filters[i].dimension === dimConf.indicator.objectName) {
								util.message.alert(PT.i18n.indicators_cannot_be_specified_as_filter || 'Indicators cannot be specified as filter');
								return;
							}

							// Categories as filter
							if (layout.filters[i].dimension === dimConf.category.objectName) {
								util.message.alert(PT.i18n.categories_cannot_be_specified_as_filter || 'Categories cannot be specified as filter');
								return;
							}

							// Data sets as filter
							if (layout.filters[i].dimension === dimConf.dataSet.objectName) {
								util.message.alert(PT.i18n.data_sets_cannot_be_specified_as_filter || 'Data sets cannot be specified as filter');
								return;
							}
						}
					}

					// dc and in
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.indicator.objectName]) {
						util.message.alert('Indicators and detailed data elements cannot be specified together');
						return;
					}

					// dc and de
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.dataElement.objectName]) {
						util.message.alert('Detailed data elements and totals cannot be specified together');
						return;
					}

					// dc and ds
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.dataSet.objectName]) {
						util.message.alert('Data sets and detailed data elements cannot be specified together');
						return;
					}

					// dc and co
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.category.objectName]) {
						util.message.alert('Categories and detailed data elements cannot be specified together');
						return;
					}

					// Degs and datasets in the same query
					//if (Ext.Array.contains(dimensionNames, dimConf.data.dimensionName) && store.dataSetSelected.data.length) {
						//for (var i = 0; i < init.degs.length; i++) {
							//if (Ext.Array.contains(dimensionNames, init.degs[i].id)) {
								//alert(PT.i18n.data_element_group_sets_cannot_be_specified_together_with_data_sets);
								//return;
							//}
						//}
					//}

					return true;
				};

				return function() {
					var a = [],
						objectNames = [],
						dimConf = conf.finals.dimension,
						dims,
						isOu = false,
						isOuc = false,
						isOugc = false;

					config.columns = getValidatedDimensionArray(config.columns);
					config.rows = getValidatedDimensionArray(config.rows);
					config.filters = getValidatedDimensionArray(config.filters);

					// Config must be an object
					if (!(config && Ext.isObject(config))) {
						alert(init.el + ': Layout config is not an object');
						return;
					}

					// At least one dimension specified as column or row
					if (!(config.columns || config.rows)) {
						alert(PT.i18n.at_least_one_dimension_must_be_specified_as_row_or_column);
						return;
					}

					// Get object names and user orgunits
					for (var i = 0, dim, dims = [].concat(config.columns || [], config.rows || [], config.filters || []); i < dims.length; i++) {
						dim = dims[i];

						if (dim) {

							// Object names
							if (Ext.isString(dim.dimension)) {
								objectNames.push(dim.dimension);
							}

							// user orgunits
							if (dim.dimension === dimConf.organisationUnit.objectName && Ext.isArray(dim.items)) {
								for (var j = 0; j < dim.items.length; j++) {
									if (dim.items[j].id === 'USER_ORGUNIT') {
										isOu = true;
									}
									else if (dim.items[j].id === 'USER_ORGUNIT_CHILDREN') {
										isOuc = true;
									}
									else if (dim.items[j].id === 'USER_ORGUNIT_GRANDCHILDREN') {
										isOugc = true;
									}
								}
							}
						}
					}

					// At least one period
					if (!Ext.Array.contains(objectNames, dimConf.period.objectName)) {
						alert(PT.i18n.at_least_one_period_must_be_specified_as_column_row_or_filter);
						return;
					}

					// Layout
					layout.columns = config.columns;
					layout.rows = config.rows;
					layout.filters = config.filters;

					// Properties
					layout.showTotals = Ext.isBoolean(config.totals) ? config.totals : (Ext.isBoolean(config.showTotals) ? config.showTotals : true);
					layout.showSubTotals = Ext.isBoolean(config.subtotals) ? config.subtotals : (Ext.isBoolean(config.showSubTotals) ? config.showSubTotals : true);
					layout.hideEmptyRows = Ext.isBoolean(config.hideEmptyRows) ? config.hideEmptyRows : false;

					layout.showHierarchy = Ext.isBoolean(config.showHierarchy) ? config.showHierarchy : false;

					layout.displayDensity = Ext.isString(config.displayDensity) && !Ext.isEmpty(config.displayDensity) ? config.displayDensity : 'normal';
					layout.fontSize = Ext.isString(config.fontSize) && !Ext.isEmpty(config.fontSize) ? config.fontSize : 'normal';
					layout.digitGroupSeparator = Ext.isString(config.digitGroupSeparator) && !Ext.isEmpty(config.digitGroupSeparator) ? config.digitGroupSeparator : 'space';
					layout.legendSet = Ext.isObject(config.legendSet) && Ext.isString(config.legendSet.id) ? config.legendSet : null;

					layout.userOrganisationUnit = isOu;
					layout.userOrganisationUnitChildren = isOuc;
					layout.userOrganisationUnitGrandChildren = isOugc;

					layout.parentGraphMap = Ext.isObject(config.parentGraphMap) ? config.parentGraphMap : null;

					layout.reportingPeriod = Ext.isObject(config.reportParams) && Ext.isBoolean(config.reportParams.paramReportingPeriod) ? config.reportParams.paramReportingPeriod : (Ext.isBoolean(config.reportingPeriod) ? config.reportingPeriod : false);
					layout.organisationUnit =  Ext.isObject(config.reportParams) && Ext.isBoolean(config.reportParams.paramOrganisationUnit) ? config.reportParams.paramOrganisationUnit : (Ext.isBoolean(config.organisationUnit) ? config.organisationUnit : false);
					layout.parentOrganisationUnit =  Ext.isObject(config.reportParams) && Ext.isBoolean(config.reportParams.paramParentOrganisationUnit) ? config.reportParams.paramParentOrganisationUnit : (Ext.isBoolean(config.parentOrganisationUnit) ? config.parentOrganisationUnit : false);

					layout.regression = Ext.isBoolean(config.regression) ? config.regression : false;
					layout.cumulative = Ext.isBoolean(config.cumulative) ? config.cumulative : false;
					layout.sortOrder = Ext.isNumber(config.sortOrder) ? config.sortOrder : 0;
					layout.topLimit = Ext.isNumber(config.topLimit) ? config.topLimit : 0;

					if (!validateSpecialCases()) {
						return;
					}

					return Ext.clone(layout);
				}();
			};

			api.response = {};

			api.response.Header = function(config) {
				var header = {};

				// name: string

				// meta: boolean

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Header is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.name)) {
						console.log('Header name is not text: ' + config);
						return;
					}

					if (!Ext.isBoolean(config.meta)) {
						console.log('Header meta is not boolean: ' + config);
						return;
					}

					header.name = config.name;
					header.meta = config.meta;

					return Ext.clone(header);
				}();
			};

			api.response.Response = function(config) {
				var response = {};

				// headers: [Header]

				return function() {
					var headers = [];

					if (!(config && Ext.isObject(config))) {
						alert('Data response invalid');
						return false;
					}

					if (!(config.headers && Ext.isArray(config.headers))) {
						alert('Data response invalid');
						return false;
					}

					for (var i = 0, header; i < config.headers.length; i++) {
						header = api.response.Header(config.headers[i]);

						if (header) {
							headers.push(header);
						}
					}

					config.headers = headers;

					if (!config.headers.length) {
						alert('No valid response headers');
						return;
					}

					if (!(Ext.isArray(config.rows) && config.rows.length > 0)) {
						alert('No values found');
						return false;
					}

					if (config.headers.length !== config.rows[0].length) {
						alert('Data invalid');
						return false;
					}

					response.headers = config.headers;
					response.metaData = config.metaData;
					response.width = config.width;
					response.height = config.height;
					response.rows = config.rows;

					return response;
				}();
			};
		}());

		// service
		(function() {
			service.layout = {};

			service.layout.getObjectNameDimensionMap = function(dimensionArray) {
				var map = {};

				if (Ext.isArray(dimensionArray) && dimensionArray.length) {
					for (var i = 0, dim; i < dimensionArray.length; i++) {
						dim = api.layout.Dimension(dimensionArray[i]);

						if (dim) {
							map[dim.dimension] = dim;
						}
					}
				}

				return map;
			};

			service.layout.getObjectNameDimensionItemsMap = function(dimensionArray) {
				var map = {};

				if (Ext.isArray(dimensionArray) && dimensionArray.length) {
					for (var i = 0, dim; i < dimensionArray.length; i++) {
						dim = api.layout.Dimension(dimensionArray[i]);

						if (dim) {
							map[dim.dimension] = dim.items;
						}
					}
				}

				return map;
			};

			service.response = {};
		}());

		// engine
		(function() {
			engine.getExtendedLayout = function(layout) {
				var layout = Ext.clone(layout),
					xLayout = {
						columns: [],
						rows: [],
						filters: [],

						columnObjectNames: [],
						columnDimensionNames: [],
						rowObjectNames: [],
						rowDimensionNames: [],

						// Axis
						axisDimensions: [],
						axisObjectNames: [],
						axisDimensionNames: [],

							// For param string
						sortedAxisDimensionNames: [],

						// Filter
						filterDimensions: [],
						filterObjectNames: [],
						filterDimensionNames: [],

							// For param string
						sortedFilterDimensions: [],

						// All
						dimensions: [],
						objectNames: [],
						dimensionNames: [],

						// Object name maps
						objectNameDimensionsMap: {},
						objectNameItemsMap: {},
						objectNameIdsMap: {},

						// Dimension name maps
						dimensionNameDimensionsMap: {},
						dimensionNameItemsMap: {},
						dimensionNameIdsMap: {},

							// For param string
						dimensionNameSortedIdsMap: {}
					};

				Ext.applyIf(xLayout, layout);

				// Columns, rows, filters
				if (layout.columns) {
					for (var i = 0, dim, items, xDim; i < layout.columns.length; i++) {
						dim = layout.columns[i];
						items = dim.items;
						xDim = {};

						xDim.dimension = dim.dimension;
						xDim.objectName = dim.dimension;
						xDim.dimensionName = dimConf.objectNameMap[dim.dimension].dimensionName;

						if (items) {
							xDim.items = items;
							xDim.ids = [];

							for (var j = 0; j < items.length; j++) {
								xDim.ids.push(items[j].id);
							}
						}

						xLayout.columns.push(xDim);

						xLayout.columnObjectNames.push(xDim.objectName);
						xLayout.columnDimensionNames.push(xDim.dimensionName);

						xLayout.axisDimensions.push(xDim);
						xLayout.axisObjectNames.push(xDim.objectName);
						xLayout.axisDimensionNames.push(dimConf.objectNameMap[xDim.objectName].dimensionName);

						xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
						xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
						xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
					}
				}

				if (layout.rows) {
					for (var i = 0, dim, items, xDim; i < layout.rows.length; i++) {
						dim = Ext.clone(layout.rows[i]);
						items = dim.items;
						xDim = {};

						xDim.dimension = dim.dimension;
						xDim.objectName = dim.dimension;
						xDim.dimensionName = dimConf.objectNameMap[dim.dimension].dimensionName;

						if (items) {
							xDim.items = items;
							xDim.ids = [];

							for (var j = 0; j < items.length; j++) {
								xDim.ids.push(items[j].id);
							}
						}

						xLayout.rows.push(xDim);

						xLayout.rowObjectNames.push(xDim.objectName);
						xLayout.rowDimensionNames.push(xDim.dimensionName);

						xLayout.axisDimensions.push(xDim);
						xLayout.axisObjectNames.push(xDim.objectName);
						xLayout.axisDimensionNames.push(dimConf.objectNameMap[xDim.objectName].dimensionName);

						xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
						xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
						xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
					}
				}

				if (layout.filters) {
					for (var i = 0, dim, items, xDim; i < layout.filters.length; i++) {
						dim = layout.filters[i];
						items = dim.items;
						xDim = {};

						xDim.dimension = dim.dimension;
						xDim.objectName = dim.dimension;
						xDim.dimensionName = dimConf.objectNameMap[dim.dimension].dimensionName;

						if (items) {
							xDim.items = items;
							xDim.ids = [];

							for (var j = 0; j < items.length; j++) {
								xDim.ids.push(items[j].id);
							}
						}

						xLayout.filters.push(xDim);

						xLayout.filterDimensions.push(xDim);
						xLayout.filterObjectNames.push(xDim.objectName);
						xLayout.filterDimensionNames.push(dimConf.objectNameMap[xDim.objectName].dimensionName);

						xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
						xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
						xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
					}
				}

				// Unique dimension names
				xLayout.axisDimensionNames = Ext.Array.unique(xLayout.axisDimensionNames);
				xLayout.filterDimensionNames = Ext.Array.unique(xLayout.filterDimensionNames);

				xLayout.columnDimensionNames = Ext.Array.unique(xLayout.columnDimensionNames);
				xLayout.rowDimensionNames = Ext.Array.unique(xLayout.rowDimensionNames);
				xLayout.filterDimensionNames = Ext.Array.unique(xLayout.filterDimensionNames);

					// For param string
				xLayout.sortedAxisDimensionNames = Ext.clone(xLayout.axisDimensionNames).sort();
				xLayout.sortedFilterDimensions = util.array.sortDimensions(Ext.clone(xLayout.filterDimensions));

				// All
				xLayout.dimensions = [].concat(xLayout.axisDimensions, xLayout.filterDimensions);
				xLayout.objectNames = [].concat(xLayout.axisObjectNames, xLayout.filterObjectNames);
				xLayout.dimensionNames = [].concat(xLayout.axisDimensionNames, xLayout.filterDimensionNames);

				// Dimension name maps
				for (var i = 0, dimName; i < xLayout.dimensionNames.length; i++) {
					dimName = xLayout.dimensionNames[i];

					xLayout.dimensionNameDimensionsMap[dimName] = [];
					xLayout.dimensionNameItemsMap[dimName] = [];
					xLayout.dimensionNameIdsMap[dimName] = [];
				}

				for (var i = 0, xDim; i < xLayout.dimensions.length; i++) {
					xDim = xLayout.dimensions[i];

					xLayout.dimensionNameDimensionsMap[xDim.dimensionName].push(xDim);
					xLayout.dimensionNameItemsMap[xDim.dimensionName] = xLayout.dimensionNameItemsMap[xDim.dimensionName].concat(xDim.items);
					xLayout.dimensionNameIdsMap[xDim.dimensionName] = xLayout.dimensionNameIdsMap[xDim.dimensionName].concat(xDim.ids);
				}

					// For param string
				for (var key in xLayout.dimensionNameIdsMap) {
					if (xLayout.dimensionNameIdsMap.hasOwnProperty(key)) {
						xLayout.dimensionNameSortedIdsMap[key] = Ext.clone(xLayout.dimensionNameIdsMap[key]).sort();
					}
				}

				return xLayout;
			};

			engine.getParamString = function(xLayout, isSorted) {
				var axisDimensionNames = isSorted ? xLayout.sortedAxisDimensionNames : xLayout.axisDimensionNames,
					filterDimensions = isSorted ? xLayout.sortedFilterDimensions : xLayout.filterDimensions,
					dimensionNameIdsMap = isSorted ? xLayout.dimensionNameSortedIdsMap : xLayout.dimensionNameIdsMap,
					paramString = '?',
					dimConf = conf.finals.dimension,
					addCategoryDimension = false,
					map = xLayout.dimensionNameItemsMap,
					dx = dimConf.indicator.dimensionName,
					co = dimConf.category.dimensionName;

				for (var i = 0, dimName, items; i < axisDimensionNames.length; i++) {
					dimName = axisDimensionNames[i];

					paramString += 'dimension=' + dimName;

					items = Ext.clone(dimensionNameIdsMap[dimName]);

					if (dimName === dx) {
						for (var j = 0, index; j < items.length; j++) {
							index = items[j].indexOf('-');

							if (index > 0) {
								addCategoryDimension = true;
								items[j] = items[j].substr(0, index);
							}
						}

						items = Ext.Array.unique(items);
					}

					if (dimName !== co) {
						paramString += ':' + items.join(';');
					}

					if (i < (axisDimensionNames.length - 1)) {
						paramString += '&';
					}
				}

				if (addCategoryDimension) {
					paramString += '&dimension=' + conf.finals.dimension.category.dimensionName;
				}

				if (Ext.isArray(filterDimensions) && filterDimensions.length) {
					for (var i = 0, dim; i < filterDimensions.length; i++) {
						dim = filterDimensions[i];

						paramString += '&filter=' + dim.dimensionName + ':' + dim.ids.join(';');
					}
				}

				if (xLayout.showHierarchy) {
					paramString += '&hierarchyMeta=true';
				}

				return paramString;
			};

			engine.setSessionStorage = function(session, obj, url) {
				if (PT.isSessionStorage) {
					var dhis2 = JSON.parse(sessionStorage.getItem('dhis2')) || {};
					dhis2[session] = obj;
					sessionStorage.setItem('dhis2', JSON.stringify(dhis2));

					if (Ext.isString(url)) {
						window.location.href = url;
					}
				}
			};

			engine.createTable = function(layout, pt, updateGui, isFavorite) {
				var legendSet = layout.legendSet ? pt.init.idLegendSetMap[layout.legendSet.id] : null,
					isHierarchy,
					getItemName,
					getSyncronizedXLayout,
					getExtendedResponse,
					getExtendedAxis,
					validateUrl,
					setMouseHandlers,
					getTableHtml,
					initialize,
					afterLoad,
					tableUuid = pt.init.el + '_' + Ext.data.IdGenerator.get('uuid').generate(),
					uuidDimUuidsMap = {},
					uuidObjectMap = {};

				isHierarchy = function(id, response) {
					return layout.showHierarchy && Ext.isObject(response.metaData.ouHierarchy) && response.metaData.ouHierarchy.hasOwnProperty(id);
				};

				getItemName = function(id, response, isHtml) {
					var metaData = response.metaData,
						name = '';

					if (isHierarchy(id, response)) {
						var a = Ext.clean(metaData.ouHierarchy[id].split('/'));
						a.shift();

						for (var i = 0; i < a.length; i++) {
							name += (isHtml ? '<span class="text-weak">' : '') + metaData.names[a[i]] + (isHtml ? '</span>' : '') + ' / ';
						}
					}

					name += metaData.names[id];

					return name;
				};

				getSyncronizedXLayout = function(xLayout, response) {
					var removeDimensionFromXLayout,
						getHeaderNames,
						dimensions = [].concat(xLayout.columns || [], xLayout.rows || [], xLayout.filters || []);

					removeDimensionFromXLayout = function(objectName) {
						var getUpdatedAxis;

						getUpdatedAxis = function(axis) {
							var dimension;
							axis = Ext.clone(axis);

							for (var i = 0; i < axis.length; i++) {
								if (axis[i].dimension === objectName) {
									dimension = axis[i];
								}
							}

							if (dimension) {
								Ext.Array.remove(axis, dimension);
							}

							return axis;
						};

						if (xLayout.columns) {
							xLayout.columns = getUpdatedAxis(xLayout.columns);
						}
						if (xLayout.rows) {
							xLayout.rows = getUpdatedAxis(xLayout.rows);
						}
						if (xLayout.filters) {
							xLayout.filters = getUpdatedAxis(xLayout.filters);
						}
					};

					getHeaderNames = function() {
						var headerNames = [];

						for (var i = 0; i < response.headers.length; i++) {
							headerNames.push(response.headers[i].name);
						}

						return headerNames;
					};

					return function() {
						var headerNames = getHeaderNames(),
							xOuDimension = xLayout.objectNameDimensionsMap[dimConf.organisationUnit.objectName],
							isUserOrgunit = xOuDimension && Ext.Array.contains(xOuDimension.ids, 'USER_ORGUNIT'),
							isUserOrgunitChildren = xOuDimension && Ext.Array.contains(xOuDimension.ids, 'USER_ORGUNIT_CHILDREN'),
							isUserOrgunitGrandChildren = xOuDimension && Ext.Array.contains(xOuDimension.ids, 'USER_ORGUNIT_GRANDCHILDREN'),
							isLevel = function() {
								if (xOuDimension && Ext.isArray(xOuDimension.ids)) {
									for (var i = 0; i < xOuDimension.ids.length; i++) {
										if (xOuDimension.ids[i].substr(0,5) === 'LEVEL') {
											return true;
										}
									}
								}

								return false;
							}(),
							isGroup = function() {
								if (xOuDimension && Ext.isArray(xOuDimension.ids)) {
									for (var i = 0; i < xOuDimension.ids.length; i++) {
										if (xOuDimension.ids[i].substr(0,8) === 'OU_GROUP') {
											return true;
										}
									}
								}

								return false;
							}(),
							co = dimConf.category.objectName,
							ou = dimConf.organisationUnit.objectName,
							layout;

						// Set items from init/metaData/xLayout
						for (var i = 0, dim, metaDataDim, items; i < dimensions.length; i++) {
							dim = dimensions[i];
							dim.items = [];
							metaDataDim = response.metaData[dim.objectName];

							// If ou and children
							if (dim.dimensionName === ou) {
								if (isUserOrgunit || isUserOrgunitChildren || isUserOrgunitGrandChildren) {
									var userOu,
										userOuc,
										userOugc;

									if (isUserOrgunit) {
										userOu = [{
											id: pt.init.user.ou,
											name: getItemName(pt.init.user.ou, response)
										}];
									}
									if (isUserOrgunitChildren) {
										userOuc = [];

										for (var j = 0; j < pt.init.user.ouc.length; j++) {
											userOuc.push({
												id: pt.init.user.ouc[j],
												name: getItemName(pt.init.user.ouc[j], response)
											});
										}

										userOuc = pt.util.array.sortObjectsByString(userOuc);
									}
									if (isUserOrgunitGrandChildren) {
										var userOuOuc = [].concat(pt.init.user.ou, pt.init.user.ouc),
											responseOu = response.metaData[ou];

										userOugc = [];

										for (var j = 0, id; j < responseOu.length; j++) {
											id = responseOu[j];

											if (!Ext.Array.contains(userOuOuc, id)) {
												userOugc.push({
													id: id,
													name: getItemName(id, response)
												});
											}
										}

										userOugc = pt.util.array.sortObjectsByString(userOugc);
									}

									dim.items = [].concat(userOu || [], userOuc || [], userOugc || []);
								}
								else if (isLevel || isGroup) {
									for (var j = 0, responseOu = response.metaData[ou], id; j < responseOu.length; j++) {
										id = responseOu[j];

										dim.items.push({
											id: id,
											name: getItemName(id, response)
										});
									}

									dim.items = pt.util.array.sortObjectsByString(dim.items);
								}
								else {
									dim.items = Ext.clone(xLayout.dimensionNameItemsMap[dim.dimensionName]);
								}
							}
							else {
								// Items: get ids from metadata -> items
								if (Ext.isArray(metaDataDim) && metaDataDim.length) {
									var ids = Ext.clone(response.metaData[dim.dimensionName]);
									for (var j = 0; j < ids.length; j++) {
										dim.items.push({
											id: ids[j],
											name: response.metaData.names[ids[j]]
										});
									}
								}
								// Items: get items from xLayout
								else {
									dim.items = Ext.clone(xLayout.objectNameItemsMap[dim.objectName]);
								}
							}
						}

						// Remove dimensions from layout that do not exist in response
						for (var i = 0, dimensionName; i < xLayout.axisDimensionNames.length; i++) {
							dimensionName = xLayout.axisDimensionNames[i];
							if (!Ext.Array.contains(headerNames, dimensionName)) {
								removeDimensionFromXLayout(dimensionName);
							}
						}

						// Re-layout
						layout = pt.api.layout.Layout(xLayout);

						if (layout) {
							dimensions = [].concat(layout.columns || [], layout.rows || [], layout.filters || []);

							for (var i = 0, idNameMap = response.metaData.names, dimItems; i < dimensions.length; i++) {
								dimItems = dimensions[i].items;

								if (Ext.isArray(dimItems) && dimItems.length) {
									for (var j = 0, item; j < dimItems.length; j++) {
										item = dimItems[j];

										if (Ext.isObject(item) && Ext.isString(idNameMap[item.id]) && !Ext.isString(item.name)) {
											item.name = idNameMap[item.id] || '';
										}
									}
								}
							}

							return engine.getExtendedLayout(layout);
						}

						return null;
					}();
				};

				getExtendedResponse = function(response, xLayout) {
					response.nameHeaderMap = {};
					response.idValueMap = {};
					ids = [];

					var extendHeaders = function() {

						// Extend headers: index, items, size
						for (var i = 0, header; i < response.headers.length; i++) {
							header = response.headers[i];

							// Index
							header.index = i;

							if (header.meta) {

								// Items
								header.items = Ext.clone(xLayout.dimensionNameIdsMap[header.name]) || [];

								// Size
								header.size = header.items.length;

								// Collect ids, used by extendMetaData
								ids = ids.concat(header.items);
							}
						}

						// nameHeaderMap (headerName: header)
						for (var i = 0, header; i < response.headers.length; i++) {
							header = response.headers[i];

							response.nameHeaderMap[header.name] = header;
						}
					}();

					var extendMetaData = function() {
						for (var i = 0, id, splitId ; i < ids.length; i++) {
							id = ids[i];

							if (id.indexOf('-') !== -1) {
								splitId = id.split('-');
								response.metaData.names[id] = response.metaData.names[splitId[0]] + ' ' + response.metaData.names[splitId[1]];
							}
						}
					}();

					var createValueIdMap = function() {
						var valueHeaderIndex = response.nameHeaderMap[conf.finals.dimension.value.value].index,
							coHeader = response.nameHeaderMap[conf.finals.dimension.category.dimensionName],
							dx = dimConf.data.dimensionName,
							co = dimConf.category.dimensionName,
							axisDimensionNames = xLayout.axisDimensionNames,
							idIndexOrder = [];

						// idIndexOrder
						for (var i = 0; i < axisDimensionNames.length; i++) {
							idIndexOrder.push(response.nameHeaderMap[axisDimensionNames[i]].index);

							// If co exists in response and is not added in layout, add co after dx
							if (coHeader && !Ext.Array.contains(axisDimensionNames, co) && axisDimensionNames[i] === dx) {
								idIndexOrder.push(coHeader.index);
							}
						}

						// idValueMap
						for (var i = 0, row, id; i < response.rows.length; i++) {
							row = response.rows[i];
							id = '';

							for (var j = 0; j < idIndexOrder.length; j++) {
								id += row[idIndexOrder[j]];
							}

							response.idValueMap[id] = row[valueHeaderIndex];
						}
					}();

					return response;
				};

				getExtendedAxis = function(type, dimensionNames, xResponse) {
					if (!dimensionNames || (Ext.isArray(dimensionNames) && !dimensionNames.length)) {
						return;
					}

					var dimensionNames = Ext.clone(dimensionNames),
						mDimensions = [],
						spanType = type === 'col' ? 'colSpan' : 'rowSpan',
						nCols = 1,
						aNumCols = [],
						aAccNumCols = [],
						aSpan = [],
						aGuiItems = [],
						aAllItems = [],
						aColIds = [],
						aAllObjects = [],
						aUniqueIds;

					for (var i = 0; i < dimensionNames.length; i++) {
						mDimensions.push({
							dimensionName: dimensionNames[i]
						});
					}

					aUniqueIds = function() {
						var a = [];

						for (var i = 0, dim; i < mDimensions.length; i++) {
							dim = mDimensions[i];

							a.push(xResponse.nameHeaderMap[dim.dimensionName].items);
						}

						return a;
					}();
	//aUniqueIds	= [ [de1, de2, de3],
	//					[p1],
	//					[ou1, ou2, ou3, ou4] ]


					for (var i = 0, dim; i < aUniqueIds.length; i++) {
						nNumCols = aUniqueIds[i].length;

						aNumCols.push(nNumCols);
						nCols = nCols * nNumCols;
						aAccNumCols.push(nCols);
					}
		//aNumCols		= [3, 1, 4]
		//nCols			= (12) [3, 3, 12] (3 * 1 * 4)
		//aAccNumCols	= [3, 3, 12]

		//nCols			= 12

					for (var i = 0; i < aUniqueIds.length; i++) {
						if (aNumCols[i] === 1) {
							if (i === 0) {
								aSpan.push(nCols); //if just one item and top level, span all
							}
							else {
								if (layout.hideEmptyRows && type === 'row') {
									aSpan.push(nCols / aAccNumCols[i]);
								}
								else {
									aSpan.push(aSpan[0]); //if just one item and not top level, span same as top level
								}
							}
						}
						else {
							aSpan.push(nCols / aAccNumCols[i]);
						}
					}
		//aSpan			= [4, 12, 1]


					aGuiItems.push(aUniqueIds[0]);

					if (aUniqueIds.length > 1) {
						for (var i = 1, a, n; i < aUniqueIds.length; i++) {
							a = [];
							n = aNumCols[i] === 1 ? aNumCols[0] : aAccNumCols[i-1];

							for (var j = 0; j < n; j++) {
								a = a.concat(aUniqueIds[i]);
							}

							aGuiItems.push(a);
						}
					}
		//aGuiItems	= [ [d1, d2, d3], (3)
		//				[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (15)
		//				[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2...] (30)
		//		  	  ]

					for (var i = 0, aAllRow, aUniqueRow, span, factor; i < aUniqueIds.length; i++) {
						aAllRow = [];
						aUniqueRow = aUniqueIds[i];
						span = aSpan[i];
						factor = nCols / (span * aUniqueRow.length);

						for (var j = 0; j < factor; j++) {
							for (var k = 0; k < aUniqueRow.length; k++) {
								for (var l = 0; l < span; l++) {
									aAllRow.push(aUniqueRow[k]);
								}
							}
						}

						aAllItems.push(aAllRow);
					}
		//aAllItems	= [ [d1, d1, d1, d1, d1, d1, d1, d1, d1, d1, d2, d2, d2, d2, d2, d2, d2, d2, d2, d2, d3, d3, d3, d3, d3, d3, d3, d3, d3, d3], (30)
		//				[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (30)
		//				[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2] (30)
		//		  	  ]


					for (var i = 0, id; i < nCols; i++) {
						id = '';

						for (var j = 0; j < aAllItems.length; j++) {
							id += aAllItems[j][i];
						}

						aColIds.push(id);
					}
		//aColIds	= [ abc, bcd, ... ]


					// allObjects

					for (var i = 0, allRow; i < aAllItems.length; i++) {
						allRow = [];

						for (var j = 0; j < aAllItems[i].length; j++) {
							allRow.push({
								id: aAllItems[i][j],
								uuid: Ext.data.IdGenerator.get('uuid').generate(),
								dim: i,
								axis: type
							});
						}

						aAllObjects.push(allRow);
					}

					// add span and children
					for (var i = 0; i < aAllObjects.length; i++) {
						for (var j = 0, obj; j < aAllObjects[i].length; j += aSpan[i]) {
							obj = aAllObjects[i][j];

							// span
							obj[spanType] = aSpan[i];

							// children
							obj.children = Ext.isDefined(aSpan[i + 1]) ? aSpan[i] / aSpan[i + 1] : 0;

							if (i === 0) {
								obj.root = true;
							}
						}
					}

					// add parents
					if (aAllObjects.length > 1) {
						for (var i = 1, allRow; i < aAllObjects.length; i++) {
							allRow = aAllObjects[i];

							for (var j = 0, obj, sizeCount = 0, span = aSpan[i - 1], parentObj = aAllObjects[i - 1][0]; j < allRow.length; j++) {
								obj = allRow[j];
								obj.parent = parentObj;

								sizeCount++;

								if (sizeCount === span) {
									parentObj = aAllObjects[i - 1][j + 1];
									sizeCount = 0;
								}
							}
						}
					}

					// add uuids array to leaves
					if (aAllObjects.length) {
						for (var i = 0, leaf, parentUuids, obj, span = aAllObjects.length > 1 ? aSpan[aAllObjects.length - 2] : 1, leafUuids = []; i < aAllObjects[aAllObjects.length - 1].length; i++) {
							leaf = aAllObjects[aAllObjects.length - 1][i];
							leafUuids.push(leaf.uuid);
							parentUuids = [];
							obj = leaf;

							// get parent uuids
							while (obj.parent) {
								obj = obj.parent;
								parentUuids.push(obj.uuid);
							}

							// add parent uuids
							leaf.uuids = Ext.clone(parentUuids);

							// add uuid for all leaves
							if (leafUuids.length === span) {
								for (var j = i - span + 1, leaf; j <= i; j++) {
									leaf = aAllObjects[aAllObjects.length - 1][j];
									leaf.uuids = leaf.uuids.concat(Ext.clone(leafUuids));
								}

								leafUuids = [];
							}
						}
					}

					// populate uuid-object map
					for (var i = 0; i < aAllObjects.length; i++) {
						for (var j = 0, object; j < aAllObjects[i].length; j++) {
							object = aAllObjects[i][j];
	//console.log(object.uuid, object);
							uuidObjectMap[object.uuid] = object;
						}
					}

	//console.log("aAllObjects", aAllObjects);

					return {
						type: type,
						items: mDimensions,
						xItems: {
							unique: aUniqueIds,
							gui: aGuiItems,
							all: aAllItems
						},
						objects: {
							all: aAllObjects
						},
						ids: aColIds,
						span: aSpan,
						dims: aUniqueIds.length,
						size: nCols
					};
				};

				validateUrl = function(url) {
					if (!Ext.isString(url) || url.length > 2000) {
						var percent = ((url.length - 2000) / url.length) * 100;
						alert('Too many parameters selected. Please reduce the number of parameters by at least ' + Ext.Number.toFixed(percent, 0) + '%.');
						return;
					}

					return true;
				};

				setMouseHandlers = function() {
					var valueElement;

					for (var key in uuidDimUuidsMap) {
						if (uuidDimUuidsMap.hasOwnProperty(key)) {
							valueElement = Ext.get(key);

							if (parseFloat(valueElement.dom.textContent)) {
								valueElement.dom.pt = pt;
								valueElement.dom.setAttribute('onclick', 'this.pt.engine.onMouseClick(this.id, this.pt);');
							}
						}
					}
				};

				getTableHtml = function(xColAxis, xRowAxis, xResponse) {
					var getRoundedHtmlValue,
						getTdHtml,
						doSubTotals,
						doTotals,
						getColAxisHtmlArray,
						getRowHtmlArray,
						rowAxisHtmlArray,
						getColTotalHtmlArray,
						getGrandTotalHtmlArray,
						getTotalHtmlArray,
						getHtml,
						getUniqueFactor = function(xAxis) {
							if (!xAxis) {
								return null;
							}

							var unique = xAxis.xItems.unique;

							if (unique) {
								if (unique.length < 2) {
									return 1;
								}
								else {
									return xAxis.size / unique[0].length;
								}
							}

							return null;
						},
						colUniqueFactor = getUniqueFactor(xColAxis),
						rowUniqueFactor = getUniqueFactor(xRowAxis),
						valueItems = [],
						valueObjects = [],
						totalColObjects = [],
						htmlArray;

					getRoundedHtmlValue = function(value, dec) {
						dec = dec || 2;
						return parseFloat(pt.util.number.roundIf(value, 2)).toString();
					};

					getTdHtml = function(config) {
						var bgColor,
							mapLegends,
							colSpan,
							rowSpan,
							htmlValue,
							displayDensity,
							fontSize,
							isLegendSet = Ext.isObject(legendSet) && Ext.isArray(legendSet.mapLegends) && legendSet.mapLegends.length,
							isNumeric = Ext.isObject(config) && Ext.isString(config.type) && config.type.substr(0,5) === 'value' && !config.empty,
							isValue = Ext.isObject(config) && Ext.isString(config.type) && config.type === 'value' && !config.empty,
							cls = '',
							html = '';

						if (!Ext.isObject(config)) {
							return '';
						}

						// Background color from legend set
						if (isNumeric && isLegendSet) {
							mapLegends = legendSet.mapLegends;

							for (var i = 0, value; i < mapLegends.length; i++) {
								value = parseFloat(config.value);

								if (Ext.Number.constrain(value, mapLegends[i].sv, mapLegends[i].ev) === value) {
									bgColor = mapLegends[i].color;
								}
							}
						}

						colSpan = config.colSpan ? 'colspan="' + config.colSpan + '" ' : '';
						rowSpan = config.rowSpan ? 'rowspan="' + config.rowSpan + '" ' : '';
						htmlValue = config.collapsed ? '' : config.htmlValue || config.value || '';
						htmlValue = config.type !== 'dimension' ? pt.util.number.pp(htmlValue, layout.digitGroupSeparator) : htmlValue;
						displayDensity = conf.pivot.displayDensity[config.displayDensity] || conf.pivot.displayDensity[layout.displayDensity];
						fontSize = conf.pivot.fontSize[config.fontSize] || conf.pivot.fontSize[layout.fontSize];

						cls += config.hidden ? ' td-hidden' : '';
						cls += config.collapsed ? ' td-collapsed' : '';
						cls += isValue ? ' pointer' : '';
						cls += bgColor ? ' legend' : (config.cls ? ' ' + config.cls : '');

						html += '<td ' + (config.uuid ? ('id="' + config.uuid + '" ') : '') + ' class="' + cls + '" ' + colSpan + rowSpan;

						if (bgColor) {
							html += '>';
							html += '<div class="legendCt">';
							html += '<div class="number ' + config.cls + '" style="padding:' + displayDensity + '; padding-right:3px; font-size:' + fontSize + '">' + htmlValue + '</div>';
							html += '<div class="arrowCt ' + config.cls + '">';
							html += '<div class="arrow" style="border-bottom:8px solid transparent; border-right:8px solid ' + bgColor + '">&nbsp;</div>';
							html += '</div></div></div></td>';

							//cls = 'legend';
							//cls += config.hidden ? ' td-hidden' : '';
							//cls += config.collapsed ? ' td-collapsed' : '';

							//html += '<td class="' + cls + '" ';
							//html += colSpan + rowSpan + '>';
							//html += '<div class="legendCt">';
							//html += '<div style="display:table-cell; padding:' + displayDensity + '; font-size:' + fontSize + '"';
							//html += config.cls ? ' class="' + config.cls + '">' : '';
							//html += htmlValue + '</div>';
							//html += '<div class="legendColor" style="background-color:' + bgColor + '">&nbsp;</div>';
							//html += '</div></td>';
						}
						else {
							html += 'style="padding:' + displayDensity + '; font-size:' + fontSize + ';"' + '>' + htmlValue + '</td>';
						}

						return html;
					};

					doSubTotals = function(xAxis) {
						return !!layout.showSubTotals && xAxis && xAxis.dims > 1;

						//var multiItemDimension = 0,
							//unique;

						//if (!(layout.showSubTotals && xAxis && xAxis.dims > 1)) {
							//return false;
						//}

						//unique = xAxis.xItems.unique;

						//for (var i = 0; i < unique.length; i++) {
							//if (unique[i].length > 1) {
								//multiItemDimension++;
							//}
						//}

						//return (multiItemDimension > 1);
					};

					doTotals = function() {
						return !!layout.showTotals;
					};

					getColAxisHtmlArray = function() {
						var a = [],
							getEmptyHtmlArray;

						getEmptyHtmlArray = function() {
							return (xColAxis && xRowAxis) ? getTdHtml({
								cls: 'pivot-dim-empty',
								colSpan: xRowAxis.dims,
								rowSpan: xColAxis.dims
							}) : '';
						};

						if (!(xColAxis && Ext.isObject(xColAxis))) {
							return a;
						}

						for (var i = 0, dimHtml; i < xColAxis.dims; i++) {
							dimHtml = [];

							if (i === 0) {
								dimHtml.push(getEmptyHtmlArray());
							}

							for (var j = 0, obj, spanCount = 0; j < xColAxis.size; j++) {
								spanCount++;

								obj = xColAxis.objects.all[i][j];
								obj.type = 'dimension';
								obj.cls = 'pivot-dim';
								obj.noBreak = false;
								obj.hidden = !(obj.rowSpan || obj.colSpan);
								obj.htmlValue = getItemName(obj.id, xResponse, true);

								dimHtml.push(getTdHtml(obj));

								if (i === 0 && spanCount === xColAxis.span[i] && doSubTotals(xColAxis) ) {
									dimHtml.push(getTdHtml({
										type: 'dimensionSubtotal',
										cls: 'pivot-dim-subtotal',
										rowSpan: xColAxis.dims
									}));

									spanCount = 0;
								}

								if (i === 0 && (j === xColAxis.size - 1) && doTotals()) {
									dimHtml.push(getTdHtml({
										type: 'dimensionTotal',
										cls: 'pivot-dim-total',
										rowSpan: xColAxis.dims,
										htmlValue: 'Total'
									}));
								}
							}

							a.push(dimHtml);
						}

						return a;
					};

					getRowHtmlArray = function() {
						var a = [],
							axisObjects = [],
							xValueObjects,
							totalValueObjects = [],
							mergedObjects = [],
							valueItemsCopy,
							colSize = xColAxis ? xColAxis.size : 1,
							rowSize = xRowAxis ? xRowAxis.size : 1,
							recursiveReduce;

						recursiveReduce = function(obj) {
							if (!obj.children) {
								obj.collapsed = true;

								if (obj.parent) {
									obj.parent.children = obj.parent.children - 1;
								}
							}

							if (obj.parent) {
								recursiveReduce(obj.parent);
							}
						};

						// Populate dim objects
						if (xRowAxis) {
							for (var i = 0, row; i < xRowAxis.size; i++) {
								row = [];

								for (var j = 0, obj, newObj; j < xRowAxis.dims; j++) {
									obj = xRowAxis.objects.all[j][i];
									obj.type = 'dimension';
									obj.cls = 'pivot-dim td-nobreak' + (isHierarchy(obj.id, xResponse) ? ' align-left' : '');
									obj.noBreak = true;
									obj.hidden = !(obj.rowSpan || obj.colSpan);
									obj.htmlValue = getItemName(obj.id, xResponse, true);

									row.push(obj);
								}

								axisObjects.push(row);
							}
						}

						// Value objects
						for (var i = 0, valueItemsRow, valueObjectsRow, idValueMap = Ext.clone(xResponse.idValueMap); i < rowSize; i++) {
							valueItemsRow = [];
							valueObjectsRow = [];

							for (var j = 0, id, value, htmlValue, empty, uuid, uuids; j < colSize; j++) {
								empty = false;
								uuids = [];

								// meta data uid
								id = (xColAxis ? pt.util.str.replaceAll(xColAxis.ids[j], '-', '') : '') + (xRowAxis ? pt.util.str.replaceAll(xRowAxis.ids[i], '-', '') : '');

								// value html element id
								uuid = Ext.data.IdGenerator.get('uuid').generate();

								// col and row dim element ids
								if (xColAxis) {
									uuids = uuids.concat(xColAxis.objects.all[xColAxis.dims - 1][j].uuids);
								}
								if (xRowAxis) {
									uuids = uuids.concat(xRowAxis.objects.all[xRowAxis.dims - 1][i].uuids);
								}

								if (idValueMap[id]) {
									value = parseFloat(idValueMap[id]);
									htmlValue = value.toString();
								}
								else {
									value = 0;
									htmlValue = '';
									empty = true;
								}

								valueItemsRow.push(value);
								valueObjectsRow.push({
									uuid: uuid,
									type: 'value',
									cls: 'pivot-value',
									value: value,
									htmlValue: htmlValue,
									empty: empty,
									uuids: uuids
								});

								// Map element id to dim element ids
								uuidDimUuidsMap[uuid] = uuids;
							}

							valueItems.push(valueItemsRow);
							valueObjects.push(valueObjectsRow);
						}

						// Value total objects
						if (xColAxis && doTotals()) {
							for (var i = 0, empty = [], total = 0; i < valueObjects.length; i++) {
								for (j = 0, obj; j < valueObjects[i].length; j++) {
									obj = valueObjects[i][j];

									empty.push(obj.empty);
									total += obj.value;
								}

								totalValueObjects.push({
									type: 'valueTotal',
									cls: 'pivot-value-total',
									value: total,
									htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(total) : '',
									empty: !Ext.Array.contains(empty, false)
								});

								empty = [];
								total = 0;
							}
						}

						// Hide empty rows (dims/values/totals)
						if (xColAxis && xRowAxis) {
							if (layout.hideEmptyRows) {
								for (var i = 0, valueRow, empty, parent; i < valueObjects.length; i++) {
									valueRow = valueObjects[i];
									empty = [];

									for (var j = 0; j < valueRow.length; j++) {
										empty.push(!!valueRow[j].empty);
									}

									if (!Ext.Array.contains(empty, false) && xRowAxis) {

										// Hide values
										for (var j = 0; j < valueRow.length; j++) {
											valueRow[j].collapsed = true;
										}

										// Hide total
										if (doTotals()) {
											totalValueObjects[i].collapsed = true;
										}

										// Hide/reduce parent dim span
										parent = axisObjects[i][xRowAxis.dims-1];
										recursiveReduce(parent);
									}
								}
							}
						}

						xValueObjects = Ext.clone(valueObjects);

						// Col subtotals
						if (doSubTotals(xColAxis)) {
							var tmpValueObjects = [];

							for (var i = 0, row, rowSubTotal, colCount; i < xValueObjects.length; i++) {
								row = [];
								rowSubTotal = 0;
								colCount = 0;

								for (var j = 0, item, collapsed = [], empty = []; j < xValueObjects[i].length; j++) {
									item = xValueObjects[i][j];
									rowSubTotal += item.value;
									empty.push(!!item.empty);
									collapsed.push(!!item.collapsed);
									colCount++;

									row.push(item);

									if (colCount === colUniqueFactor) {
										row.push({
											type: 'valueSubtotal',
											cls: 'pivot-value-subtotal',
											value: rowSubTotal,
											htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(rowSubTotal) : '',
											empty: !Ext.Array.contains(empty, false),
											collapsed: !Ext.Array.contains(collapsed, false)
										});

										colCount = 0;
										rowSubTotal = 0;
										empty = [];
										collapsed = [];
									}
								}

								tmpValueObjects.push(row);
							}

							xValueObjects = tmpValueObjects;
						}

						// Row subtotals
						if (doSubTotals(xRowAxis)) {
							var tmpAxisObjects = [],
								tmpValueObjects = [],
								tmpTotalValueObjects = [],
								getAxisSubTotalRow;

							getAxisSubTotalRow = function(collapsed) {
								var row = [];

								for (var i = 0, obj; i < xRowAxis.dims; i++) {
									obj = {};
									obj.type = 'dimensionSubtotal';
									obj.cls = 'pivot-dim-subtotal';
									obj.collapsed = Ext.Array.contains(collapsed, true);

									if (i === 0) {
										obj.htmlValue = '';
										obj.colSpan = xRowAxis.dims;
									}
									else {
										obj.hidden = true;
									}

									row.push(obj);
								}

								return row;
							};

							// tmpAxisObjects
							for (var i = 0, row, collapsed = []; i < axisObjects.length; i++) {
								tmpAxisObjects.push(axisObjects[i]);
								collapsed.push(!!axisObjects[i][0].collapsed);

								// Insert subtotal after last objects
								if (!Ext.isArray(axisObjects[i+1]) || !!axisObjects[i+1][0].root) {
									tmpAxisObjects.push(getAxisSubTotalRow(collapsed));

									collapsed = [];
								}
							}

							// tmpValueObjects
							for (var i = 0; i < tmpAxisObjects.length; i++) {
								tmpValueObjects.push([]);
							}

							for (var i = 0; i < xValueObjects[0].length; i++) {
								for (var j = 0, rowCount = 0, tmpCount = 0, subTotal = 0, empty = [], collapsed, item; j < xValueObjects.length; j++) {
									item = xValueObjects[j][i];
									tmpValueObjects[tmpCount++].push(item);
									subTotal += item.value;
									empty.push(!!item.empty);
									rowCount++;

									if (axisObjects[j][0].root) {
										collapsed = !!axisObjects[j][0].collapsed;
									}

									if (!Ext.isArray(axisObjects[j+1]) || axisObjects[j+1][0].root) {
										tmpValueObjects[tmpCount++].push({
											type: item.type === 'value' ? 'valueSubtotal' : 'valueSubtotalTotal',
											value: subTotal,
											htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(subTotal) : '',
											collapsed: collapsed,
											cls: item.type === 'value' ? 'pivot-value-subtotal' : 'pivot-value-subtotal-total'
										});
										rowCount = 0;
										subTotal = 0;
										empty = [];
									}
								}
							}

							// tmpTotalValueObjects
							for (var i = 0, obj, collapsed = [], empty = [], subTotal = 0, count = 0; i < totalValueObjects.length; i++) {
								obj = totalValueObjects[i];
								tmpTotalValueObjects.push(obj);

								collapsed.push(!!obj.collapsed);
								empty.push(!!obj.empty);
								subTotal += obj.value;
								count++;

								if (count === xRowAxis.span[0]) {
									tmpTotalValueObjects.push({
										type: 'valueTotalSubgrandtotal',
										cls: 'pivot-value-total-subgrandtotal',
										value: subTotal,
										htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(subTotal) : '',
										empty: !Ext.Array.contains(empty, false),
										collapsed: !Ext.Array.contains(collapsed, false)
									});

									collapsed = [];
									empty = [];
									subTotal = 0;
									count = 0;
								}
							}

							axisObjects = tmpAxisObjects;
							xValueObjects = tmpValueObjects;
							totalValueObjects = tmpTotalValueObjects;
						}

						// Merge dim, value, total
						for (var i = 0, row; i < xValueObjects.length; i++) {
							row = [];

							if (xRowAxis) {
								row = row.concat(axisObjects[i]);
							}

							row = row.concat(xValueObjects[i]);

							if (xColAxis) {
								row = row.concat(totalValueObjects[i]);
							}

							mergedObjects.push(row);
						}

						// Create html items
						for (var i = 0, row; i < mergedObjects.length; i++) {
							row = [];

							for (var j = 0; j < mergedObjects[i].length; j++) {
								row.push(getTdHtml(mergedObjects[i][j]));
							}

							a.push(row);
						}

						return a;
					};

					getColTotalHtmlArray = function() {
						var a = [];

						if (xRowAxis && doTotals()) {
							var xTotalColObjects;

							// Total col items
							for (var i = 0, total = 0, empty = []; i < valueObjects[0].length; i++) {
								for (var j = 0, obj; j < valueObjects.length; j++) {
									obj = valueObjects[j][i];

									total += obj.value;
									empty.push(!!obj.empty);
								}

								totalColObjects.push({
									type: 'valueTotal',
									value: total,
									htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(total) : '',
									empty: !Ext.Array.contains(empty, false),
									cls: 'pivot-value-total'
								});

								total = 0;
								empty = [];
							}

							xTotalColObjects = Ext.clone(totalColObjects);

							if (xColAxis && doSubTotals(xColAxis)) {
								var tmp = [];

								for (var i = 0, item, subTotal = 0, empty = [], colCount = 0; i < xTotalColObjects.length; i++) {
									item = xTotalColObjects[i];
									tmp.push(item);
									subTotal += item.value;
									empty.push(!!item.empty);
									colCount++;

									if (colCount === colUniqueFactor) {
										tmp.push({
											type: 'valueTotalSubgrandtotal',
											value: subTotal,
											htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(subTotal) : '',
											empty: !Ext.Array.contains(empty, false),
											cls: 'pivot-value-total-subgrandtotal'
										});

										subTotal = 0;
										colCount = 0;
									}
								}

								xTotalColObjects = tmp;
							}

							// Total col html items
							for (var i = 0; i < xTotalColObjects.length; i++) {
								a.push(getTdHtml(xTotalColObjects[i]));
							}
						}

						return a;
					};

					getGrandTotalHtmlArray = function() {
						var total = 0,
							empty = [],
							a = [];

						if (doTotals()) {
							for (var i = 0, obj; i < totalColObjects.length; i++) {
								obj = totalColObjects[i];

								total += obj.value;
								empty.push(obj.empty);
							}

							if (xColAxis && xRowAxis) {
								a.push(getTdHtml({
									type: 'valueGrandTotal',
									cls: 'pivot-value-grandtotal',
									htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(total) : '',
									empty: !Ext.Array.contains(empty, false)
								}));
							}
						}

						return a;
					};

					getTotalHtmlArray = function() {
						var dimTotalArray,
							colTotal = getColTotalHtmlArray(),
							grandTotal = getGrandTotalHtmlArray(),
							row,
							a = [];

						if (doTotals()) {
							if (xRowAxis) {
								dimTotalArray = [getTdHtml({
									type: 'dimensionSubtotal',
									cls: 'pivot-dim-total',
									colSpan: xRowAxis.dims,
									htmlValue: 'Total'
								})];
							}

							row = [].concat(dimTotalArray || [], Ext.clone(colTotal) || [], Ext.clone(grandTotal) || []);

							a.push(row);
						}

						return a;
					};

					getHtml = function() {
						var s = '<table id="' + tableUuid + '" class="pivot">';

						for (var i = 0; i < htmlArray.length; i++) {
							s += '<tr>' + htmlArray[i].join('') + '</tr>';
						}

						return s += '</table>';
					};

					htmlArray = [].concat(getColAxisHtmlArray(), getRowHtmlArray(), getTotalHtmlArray());
					htmlArray = Ext.Array.clean(htmlArray);

					return getHtml(htmlArray);
				};

				afterLoad = function(layout, xLayout, xResponse) {

					if (pt.isPlugin) {

						// Resize render elements
						var baseEl = Ext.get(pt.init.el),
							baseElBorderW = parseInt(baseEl.getStyle('border-left-width')) + parseInt(baseEl.getStyle('border-right-width')),
							baseElBorderH = parseInt(baseEl.getStyle('border-top-width')) + parseInt(baseEl.getStyle('border-bottom-width')),
							baseElPaddingW = parseInt(baseEl.getStyle('padding-left')) + parseInt(baseEl.getStyle('padding-right')),
							baseElPaddingH = parseInt(baseEl.getStyle('padding-top')) + parseInt(baseEl.getStyle('padding-bottom')),
							el = Ext.get(tableUuid);

						pt.viewport.centerRegion.setWidth(el.getWidth());
						pt.viewport.centerRegion.setHeight(el.getHeight());
						baseEl.setWidth(el.getWidth() + baseElBorderW + baseElPaddingW);
						baseEl.setHeight(el.getHeight() + baseElBorderH + baseElPaddingH);
					}
					else {
						if (PT.isSessionStorage) {
							setMouseHandlers();
							engine.setSessionStorage('table', layout);
						}

						if (updateGui) {
							pt.viewport.setGui(layout, xLayout, updateGui, isFavorite);
						}
					}

					// Hide mask
					util.mask.hideMask(pt.viewport.centerRegion);

					// Add uuid maps to instance
					pt.uuidDimUuidsMap = uuidDimUuidsMap;
					pt.uuidObjectMap = uuidObjectMap;

					// Add objects to instance
					pt.layout = layout;
					pt.xLayout = xLayout;
					pt.xResponse = xResponse;

					if (PT.isDebug) {
						console.log("xResponse", xResponse);
						console.log("xLayout", xLayout);
					}
				};

                initialize = function() {
                    var xLayout = engine.getExtendedLayout(layout),
                        paramString = engine.getParamString(xLayout, true),
						method = 'GET',
						success;

					success = function(response) {
						var xResponse,
							chart,
							html,
							response = pt.api.response.Response(response);

						if (!response) {
							pt.util.mask.hideMask(pt.viewport.centerRegion);
							return;
						}

						// Synchronize xLayout
						xLayout = getSyncronizedXLayout(xLayout, response);

						if (!xLayout) {
							pt.util.mask.hideMask(pt.viewport.centerRegion);
							return;
						}

						// Extended response
						xResponse = getExtendedResponse(response, xLayout);

						// Extended axes
						xColAxis = getExtendedAxis('col', xLayout.columnDimensionNames, xResponse);
						xRowAxis = getExtendedAxis('row', xLayout.rowDimensionNames, xResponse);

						// Create html
						html = getTableHtml(xColAxis, xRowAxis, xResponse);

						// Update viewport
						//pt.viewport.centerRegion.removeAll(true);
						pt.viewport.centerRegion.update(html);

						pt.paramString = paramString;

						afterLoad(layout, xLayout, xResponse);
					};

					// Validate request size
                    if (!validateUrl(init.contextPath + '/api/analytics.jsonp' + paramString)) {
                        return;
                    }

					// Show load mask
                    util.mask.showMask(pt.viewport.centerRegion);

                    if (pt.isPlugin) {
						Ext.data.JsonP.request({
							method: method,
							url: init.contextPath + '/api/analytics.jsonp' + paramString,
							disableCaching: false,
							success: success
						});
					}
					else {
						Ext.Ajax.request({
							method: method,
							url: init.contextPath + '/api/analytics.json' + paramString,
							timeout: 60000,
							headers: {
								'Content-Type': 'application/json',
								'Accept': 'application/json'
							},
							disableCaching: false,
							failure: function(r) {
								util.mask.hideMask(pt.viewport.centerRegion);
								alert(r.responseText);
							},
							success: function(r) {
								success(Ext.decode(r.responseText));
							}
						});
					}
                }();
			};

			engine.loadTable = function(id, pt, updateGui, isFavorite) {
				var url = init.contextPath + '/api/reportTables/' + id,
					params = '?viewClass=dimensional&links=false',
					method = 'GET',
					success,
					failure;

				if (!Ext.isString(id)) {
					alert('Invalid uid');
					return;
				}

				success = function(layoutConfig) {
					var layout = api.layout.Layout(layoutConfig);

					if (layout) {
						pt.favorite = Ext.clone(layout);
						pt.favorite.id = layoutConfig.id;
						pt.favorite.name = layoutConfig.name;

						engine.createTable(layout, pt, updateGui, isFavorite);
					}
				};

				failure = function(responseText) {
					util.mask.hideMask(pt.viewport.centerRegion);
					alert(responseText);
				};

				if (pt.isPlugin) {
					Ext.data.JsonP.request({
						url: url + '.jsonp' + params,
						method: method,
						failure: function(r) {
							failure(r);
						},
						success: function(r) {
							success(r);
						}
					});
				}
				else {
					Ext.Ajax.request({
						url: url + '.json' + params,
						method: method,
						failure: function(r) {
							failure(r.responseText);
						},
						success: function(r) {
							success(Ext.decode(r.responseText));
						}
					});
				}
			};

			engine.onMouseHover = function(uuid, event, param, pt) {
				var dimUuids;

				if (param === 'chart') {
					if (Ext.isString(uuid) && Ext.isArray(pt.uuidDimUuidsMap[uuid])) {
						dimUuids = pt.uuidDimUuidsMap[uuid];

						for (var i = 0, el; i < dimUuids.length; i++) {
							el = Ext.get(dimUuids[i]);

							if (el) {
								if (event === 'mouseover') {
									el.addCls('highlighted');
								}
								else if (event === 'mouseout') {
									el.removeCls('highlighted');
								}
							}
						}
					}
				}
			};

			engine.onMouseClick = function(uuid, pt) {
				var that = this,
					uuids = pt.uuidDimUuidsMap[uuid],
					layoutConfig = Ext.clone(pt.layout),
					objects = [],
					menu;

				// modify layout dimension items based on uuid objects

				// get objects
				for (var i = 0; i < uuids.length; i++) {
					objects.push(pt.uuidObjectMap[uuids[i]]);
				}

				// clear layoutConfig dimension items
				for (var i = 0, a = [].concat(layoutConfig.columns, layoutConfig.rows); i < a.length; i++) {
					a[i].items = [];
				}

				// add new items
				for (var i = 0, obj, axis; i < objects.length; i++) {
					obj = objects[i];
					axis = obj.axis === 'col' ? layoutConfig.columns || [] : layoutConfig.rows || [];

					if (axis.length) {
						axis[obj.dim].items.push({
							id: obj.id,
							name: pt.xResponse.metaData.names[obj.id]
						});
					}
				}

				// menu

				menu = Ext.create('Ext.menu.Menu', {
					shadow: true,
					showSeparator: false,
					items: [
						{
							text: 'Open selection as chart' + '&nbsp;&nbsp;', //i18n
							iconCls: 'pt-button-icon-chart',
							param: 'chart',
							handler: function() {
								that.setSessionStorage('analytical', layoutConfig, pt.init.contextPath + '/dhis-web-visualizer/app/index.html?s=analytical');
							},
							listeners: {
								render: function() {
									this.getEl().on('mouseover', function() {
										that.onMouseHover(uuid, 'mouseover', 'chart', pt);
									});

									this.getEl().on('mouseout', function() {
										that.onMouseHover(uuid, 'mouseout', 'chart', pt);
									});
								}
							}
						},
						{
							text: 'Open selection as map' + '&nbsp;&nbsp;', //i18n
							iconCls: 'pt-button-icon-map',
							param: 'map',
							disabled: true,
							handler: function() {
								that.setSessionStorage('analytical', layoutConfig, pt.init.contextPath + '/dhis-web-mapping/app/index.html?s=analytical');
							},
							listeners: {
								render: function() {
									this.getEl().on('mouseover', function() {
										that.onMouseHover(uuid, 'mouseover', 'map', pt);
									});

									this.getEl().on('mouseout', function() {
										that.onMouseHover(uuid, 'mouseout', 'map', pt);
									});
								}
							}
						}
					]
				});

				menu.showAt(function() {
					var el = Ext.get(uuid),
						xy = el.getXY();

					xy[0] += el.getWidth() - 5;
					xy[1] += el.getHeight() - 5;

					return xy;
				}());
			};
		}());

		// instance
		PT.core.instances.push({
			conf: conf,
			util: util,
			init: init,
			api: api,
			service: service,
			engine: engine
		});

        return PT.core.instances[PT.core.instances.length - 1];
	};

	// PLUGIN

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

	var init = {
			user: {}
		},
		configs = [],
		isInitStarted = false,
		isInitComplete = false,
		getInit,
		execute;

	getInit = function(url) {
		var isInit = false,
			requests = [],
			callbacks = 0,
			fn;

		fn = function() {
			if (++callbacks === requests.length) {
				isInitComplete = true;
				
				for (var i = 0; i < configs.length; i++) {
					execute(configs[i]);
				}

				configs = [];
			}
		};

		requests.push({
			url: url + '/api/system/context.jsonp',
			success: function(r) {
				init.contextPath = r.contextPath;
				fn();
			}
		});

		requests.push({
			url: url + '/api/organisationUnits.jsonp?userOnly=true&viewClass=detailed&links=false',
			success: function(r) {
				var ou = r.organisationUnits[0];
				init.user.ou = ou.id;
				init.user.ouc = Ext.Array.pluck(ou.children, 'id');
				fn();
			}
		});

		requests.push({
			url: url + '/api/mapLegendSets.jsonp?viewClass=detailed&links=false&paging=false',
			success: function(r) {
				init.legendSets = r.mapLegendSets;
				fn();
			}
		});

		requests.push({
			url: url + '/api/dimensions.jsonp?links=false&paging=false',
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

			config.id = config.id || config.uid;

			return true;
		};

        extendInstance = function(pt) {
            var util = pt.util || {},
                init = pt.init || {};

            init.el = config.el;
		};

		createViewport = function() {
			var setFavorite,
				centerRegion;

			setFavorite = function(layout) {
				pt.engine.createTable(layout, pt);
			};

			return {
				setFavorite: setFavorite,
				centerRegion: Ext.get(config.el)
			};
		};

		initialize = function() {
			if (!validateConfig(config)) {
				return;
			}

			pt = PT.core.getInstance(Ext.clone(init));
			extendInstance(pt);

			pt.isPlugin = true;
			pt.viewport = createViewport();

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

		if (isInitComplete) {
			execute(config);
		}
		else {
			configs.push(config);
			
			if (!isInitStarted) {
				isInitStarted = true;
				getInit(config.url);
			}
		}
	};

	DHIS = Ext.isObject(window['DHIS']) ? DHIS : {};
	DHIS.getTable = PT.plugin.getTable;
});
