Ext.namespace("Mogan.template");
if (!Ext.grid.GridView.prototype.templates) {
	Ext.grid.GridView.prototype.templates = {};
}
Ext.grid.GridView.prototype.templates.body = new Ext.Template(
		'<tr class="x-grid3-col x-grid3-cell x-grid3-td-{id} mogan-selectable {css}" style="{style}" tabIndex="0" {cellAttr}>',
		'<div class="x-grid3-cell-inner x-grid3-col-{id}" {attr}>{value}</div>',
		'</tr>');
		
		 