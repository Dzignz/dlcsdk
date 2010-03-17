Ext.namespace("Mogan.model");
Mogan.model.createModelGrid = function() {
	var grid = new Ext.grid.GridPanel({
				id : 'gridPanelModel',
				store : jStore,
				columns : [{
							header : 'Category',
							width : 60,
							renderer : Mogan.model.rendererCategory,
							sortable : true,
							dataIndex : 'category'
						}, {
							id : 'modelName',
							header : 'Model Name',
							width : 100,
							sortable : true,
							dataIndex : 'modelName'
						}, {
							header : 'Model Class Path',
							width : 200,
							sortable : true,
							dataIndex : 'modelClass'
						}, {
							header : 'Model Description',
							width : 200,
							sortable : true,
							dataIndex : 'modelDescription'
						}, {
							header : 'Start Schedule Date',
							width : 150,
							sortable : true,
							renderer: Ext.util.Format.dateRenderer('Y/m/d H:i:s'),
							dataIndex : 'start_schedule_date'
						},{
							header : 'Last Exe. Date',
							width : 150,
							sortable : true,
							renderer: Ext.util.Format.dateRenderer('Y/m/d H:i:s'),
							dataIndex : 'last_exe_date'
						},{
							header : 'Next Exe. Date',
							width : 150,
							sortable : true,
							renderer: Ext.util.Format.dateRenderer('Y/m/d H:i:s'),
							dataIndex : 'next_exe_date'
						},
							{
							header : 'Time Spec',
							width : 150,
							sortable : true,
							dataIndex : 'run_time_spec'
						},
						{
							header : 'Remain Time',
							width : 150,
							sortable : true,
							dataIndex : 'remain_time'
						},{
							header : 'Interval (Min.)',
							width : 150,
							sortable : true,
							dataIndex : 'interval'
						},
						
						{
							header : 'Status',
							width : 85,
							renderer : Mogan.model.rendererStatus,
							sortable : true,
							dataIndex : 'status'
						}, {
							header : 'Action',
							width : 85,
							renderer : Mogan.model.rendererAction,
							sortable : true,
							dataIndex : ''
						}, {
							header : 'Creator',
							width : 75,
							sortable : true,
							dataIndex : 'Creator'
						}, {
							header : 'Create Date',
							width : 85,
							sortable : true,
							dataIndex : 'create_Date'
						}],
				stripeRows : true,
				// autoExpandColumn : 'modelName',
				height : 350,
				width : 600,
				title : 'Models',
				// config options for stateful behavior
				stateful : true,
				stateId : 'grid'
			});
	return grid;
};

var jStore = new Ext.data.JsonStore({
			autoDestroy : true,
			root : 'responseData',
			storeId : 'jStore',
			fields : [{
						name : 'modelName'
					}, {
						name : 'modelClass'
					}, {
						name : 'modelDescription'
					}, {
						name : 'creator'
					}, {
						name : 'create_Date'
					}, {
						name : 'execute_date'
					}, {
						name : 'category'
					}, {
						name : 'status'
					}, {
						name : 'start_schedule_date'
					}, {
						name : 'next_exe_date'
					}, {
						name : 'last_exe_date'
					},{name:'run_time_spec'}, {
						name : 'remain_time'
					}, {
						name : 'interval'
					}]
		});

/**
 * 修正操作欄位 0=未執行 1=執行中 2=執行中(排程) 3=執行結束
 * 
 * @param {}
 *            value
 */
Mogan.model.rendererStatus = function(value, metaData, record, rowIndex,
		colIndex, store) {
	var actionBtn = '';
	
	switch (value) {
		case 0 :
			actionBtn="-";
			break;
		case 1 :
			actionBtn="執行中";
			break;
	}
	return actionBtn;
}
		
/**
 * 修正操作欄位 0=未執行 1=執行中 2=執行中(排程) 3=執行結束
 * 
 * @param {}
 *            value
 */
Mogan.model.rendererAction = function(value, metaData, record, rowIndex,
		colIndex, store) {
	var actionBtn = '';
	
	switch (record['data']['status']) {
		case 0 :
			actionBtn="<input type='button' value='執行' onclick=Mogan.model.startSchedule('"+record['data']['modelName']+"') />";
			break;
		case 1 :
			actionBtn="<input type='button' value='中斷' onclick=Mogan.model.stopSchedule('"+record['data']['modelName']+"') />";
			break;
	}
	return actionBtn;
}

/**
 * 修正類型欄位
 * 
 * @param {}
 *            value
 */
Mogan.model.rendererCategory = function(value, metaData, record, rowIndex,
		colIndex, store) {
	var category = '';
	if (value == 'SCHEDULE') {
		category = "程排";
	} else if (value == 'MODLE') {
		category = "模組";
	}
	return category;
}

function saveModelData() {

}
function delModelData() {

}
function newModelData() {

}