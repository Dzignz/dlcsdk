Ext.ns('Ext.ux.plugins');

/**
 * allow custom value in combobox
 * if the entered value is not found in the store the value is used as the combos value
 * 
 * @author Gustav Rek
 * @date 21.01.2010
 * @version 1
 * 
 */

Ext.ux.plugins.CustomFilterCombo = function(config) {
    Ext.apply(this, config);
    
    Ext.ux.plugins.CustomFilterCombo.superclass.constructor.call(this);
};

Ext.extend(Ext.ux.plugins.CustomFilterCombo, Object, {
    
    /**
     * @cfg {Boolean} anyMatch true to match any part not just the beginning (default=false)
     */
    anyMatch : false,
    
    /**
     * @cfg {Boolean} caseSensitive true for case sensitive comparison (default=false)
     */
    caseSensitive : false,
    
    /**
     * @cfg {Function} filterFn Filter by a function. This function will be called for each
     * Record in this Store. If the function returns true the Record is included,
     * otherwise it is filtered out (default=undefined).
     * When using this parameter anyMathch and caseSensitive are ignored!
     * @param {Ext.data.Record} record  The {@link Ext.data.Record record}
     * to test for filtering. Access field values using {@link Ext.data.Record#get}.
     * @param {Object} id The ID of the Record passed.
     * @param {String} field The field filtered in (normally the displayField of the combo).
     * Use this with {@link Ext.data.Record#get} to fetch the String to match against.
     * @param {String} value The value typed in by the user.
     */
    filterFn : undefined,
    
    init : function(combo) {
        this.combo = combo;
        
        if(Ext.isFunction(this.filterFn)) {
            combo.store.filter = this.filterBy.createDelegate(this);
        } else {
            // save this funtcion for later use, before we overwrite it
            combo.store.originalFilter = combo.store.filter;
            combo.store.filter = this.filter.createDelegate(this);
        }
    },
    
    // scope: this
    filterBy : function(field, value) {
        this.combo.store.filterBy(this.filterFn.createDelegate(this.combo, [field, value], true));        
    },
    
    // scope: this
    filter : function(field, value) {
        this.combo.store.originalFilter(field, value, this.anyMatch, this.caseSensitive);
    }
});

Ext.preg('customfiltercombo', Ext.ux.plugins.CustomFilterCombo);