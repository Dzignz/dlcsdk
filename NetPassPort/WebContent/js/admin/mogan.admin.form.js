Ext.namespace("Mogan.admin");
Mogan.admin.createModelGrid = function() {
	var grid = new Ext.grid.GridPanel({
				id:'gridPanelModel',
				store : jStore,
				columns : [{
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
				title : 'Web Sites',
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
					}]
		});
		

function saveModelData(){
	
}
function delModelData(){
	
}
function newModelData(){
	
}