if ( typeof APP == "undefined" ) {
    var APP = {
        lang: {},
        util: {}
    };
}

APP.callDelayed = function callDelayed( element, func, delay ) {
    var el = YAHOO.util.Dom.get(element);
    if ( el.zid ) {
        clearTimeout(el.zid);
    }
    if ( delay == null ) {
        delay = 600;
    }
    el.zid = setTimeout(func, delay);
}

APP.onCallReturn = function( jsonEncodedYUIResponse ) {
    var args = jsonEncodedYUIResponse.argument;
    var successCallback = ( args != null ? args[0] : null );
    var failureCallback = ( args != null ? args[1] : null );

    var jsonResponse = jsonEncodedYUIResponse.responseText.parseJSON();

    if ( jsonResponse.valid ) {
        if ( successCallback != null ) {
            successCallback(jsonResponse.body);
        }
    } else {
        if ( failureCallback != null ) {
            failureCallback(jsonResponse.body);
        }

    }
}

APP.call = function call( url, methodName, params, successCallback, failureCallback ) {
    var callUrl = url + "?actionLink=" + methodName + "&" + params;
    var args = new Array( successCallback, failureCallback );
    AjaxUtils.call( callUrl, APP.onCallReturn, args );
}

APP.util.PaginatedTable = function(container, urlRequest, initialRequestValues, columnDefs, responseSchema, ddGroup) {
    this.container = container;
    this.urlRequest = urlRequest;
    this.initialRequestValues = initialRequestValues;
    this.columnDefs = columnDefs;
    this.responseSchema = responseSchema;
    this.ddGroup = ddGroup;
    this.init();
};

APP.util.PaginatedTable.prototype = {

    container: null,
    urlRequest: null,
    initialRequestValues: null,
    columnDefs: null,
    responseSchema: null,
    dataStore: null,
    dataTable: null,
    totalRecords: 0,
    rowsPerPage: 50,
    ddGroup: null,


    init: function() {
        var dataTableConfigs = {
            paginated: true,
            paginator: {
                rowsPerPage: this.rowsPerPage,
                pageLinks: -1
            },
            initialRequest: this.initialRequestValues,
            scrollable: true
        };
        this.dataSource = new YAHOO.util.DataSource(this.urlRequest);
        this.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
        this.dataSource.responseSchema = this.responseSchema;
        this.dataSource.doBeforeCallback = this._parseData;
        this.dataTable = new YAHOO.widget.DataTable(this.container, this.columnDefs, this.dataSource, dataTableConfigs);
        this.dataTable.subscribe("refreshEvent", this.destroyDragDrops, this, true);
        this.dataTable.subscribe("refreshEvent", this.createDragDrops, this, true);
    },

    _parseData: function(pRequest, pRawResponse, pParsedResponse) {
        // Get Paginator values
        var rawResponse = pRawResponse.parseJSON();
        var totalRecords = rawResponse.totalRecords;
        var startIndex = 1;
        var endIndex = (totalRecords < 50) ? totalRecords : 50;
        
        YAHOO.util.Dom.get("startIndex").innerHTML = startIndex;
        YAHOO.util.Dom.get("endIndex").innerHTML = endIndex;
        YAHOO.util.Dom.get("ofTotal").innerHTML = totalRecords;

        // Let the DataSource parse the rest of the response
        return pParsedResponse;
    },

    refreshPage: function() {
        var dataTable = this.dataTable;
        dataTable.refreshView();
    },

    getPreviousPage: function( e ) {
        YAHOO.util.Event.stopEvent(e);
        var dataTable = this.dataTable;
        var paginator = dataTable.get("paginator");
        // Already at first page
        if ( paginator.currentPage - 1 < 1 ) {
            return;
        }
        paginator.currentPage--;    // Move to previous page
        this.refreshPage();
    },

    getNextPage: function( e ) {
        YAHOO.util.Event.stopEvent(e);
        var dataTable = this.dataTable;
        var paginator = dataTable.get("paginator");
        // Already at last page
        if ( paginator.currentPage + 1 > paginator.totalPages ) {
            return;
        }
        paginator.currentPage++;    // Move to next page
        this.refreshPage();
    },

    createDragDrops: function() {
        var nextTrEl = this.dataTable.getTrEl(0);
        while (nextTrEl) {
            new APP.util.CustomDDProxy(nextTrEl.id, this.ddGroup, {resizeFrame:true});
            nextTrEl = nextTrEl.nextSibling;
        }
    },

    destroyDragDrops: function() {
        var DDM = YAHOO.util.DragDropMgr;
        var ids = DDM.ids[this.ddGroup];
        if ( ids ) {
            var nextTrEl = this.dataTable.getTrEl(0);
            while ( nextTrEl ) {
                var currentDD = ids[nextTrEl.id];
                nextTrEl = nextTrEl.nextSibling;
                if ( ! DDM.isTypeOfDD(currentDD) ) {
                    continue;
                }
                currentDD.removeFromGroup(this.ddGroup);
            }
        }
    }

};


APP.util.CustomDDProxy = function(id, sGroup, config) {
    APP.util.CustomDDProxy.superclass.constructor.call(this, id, sGroup, config);
    this.isTarget = false;
};

YAHOO.extend(APP.util.CustomDDProxy, YAHOO.util.DDProxy, {

    startDrag: function( x, y ) {
        var DOM = YAHOO.util.Dom;
        var sourceEl = this.getEl();
        var proxyEl = this.getDragEl();

        proxyEl.innerHTML = sourceEl.innerHTML;
        DOM.setStyle(proxyEl, "color", DOM.getStyle(sourceEl, "color"));
        DOM.setStyle(proxyEl, "backgroundColor", DOM.getStyle(sourceEl, "backgroundColor"));
        DOM.setStyle(proxyEl, "border", "2px solid gray");
        DOM.setStyle(proxyEl, "opacity", 0.7);
        DOM.setStyle(sourceEl, "opacity", 0.1);
    },

    endDrag: function(e) {
        var DOM = YAHOO.util.Dom;
        var sourceEl = this.getEl();
        var proxyEl = this.getDragEl();
        var endDragMotion = new YAHOO.util.Motion(
                proxyEl, {
                    points: {
                        to: DOM.getXY(sourceEl)
                    }
                },
                0.2,
                YAHOO.util.Easing.easeOut);

        // Hide the proxy and show the source element when finished with the animation
        endDragMotion.onComplete.subscribe(function() {
            DOM.setStyle(proxyEl, "visibility", "hidden");
            DOM.setStyle(sourceEl, "opacity", 1);
        });
        // Show the proxy element and animate it to the src element's location
        DOM.setStyle(proxyEl, "visibility", "");
        endDragMotion.animate();
    },

    onDragDrop: function(e, id) {
        var DOM = YAHOO.util.Dom;
        var sourceEl = this.getEl();
        var targetEl = DOM.get(id);
        var parentEl = targetEl.getElementsByTagName("tbody")[1];
        var firstRowEl = parentEl.getElementsByTagName("tr")[0];

        parentEl.insertBefore(sourceEl, firstRowEl);
        DOM.setStyle(targetEl, "opacity", 1);

        if (this.groups["G1"]) {
            this.addToGroup("G2");
            this.removeFromGroup("G1");
        }
        else {
            this.addToGroup("G1");
            this.removeFromGroup("G2");            
        }
    },

    onDrag: function(e) {
    },

    onDragOver: function(e, id) {
    },

    onDragEnter: function(e, id) {
        var DOM = YAHOO.util.Dom;
        DOM.setStyle(DOM.get(id), "opacity", 0.50);
    },

    onDragOut: function(e, id) {
        var DOM = YAHOO.util.Dom;
        DOM.setStyle(DOM.get(id), "opacity", 1);
    }

});



APP.util.SimpleTable = function( container, urlRequest, initialRequestValues, columnDefs, responseSchema, enableSelection ) {
    this.container = container;
    this.urlRequest = urlRequest;
    this.initialRequestValues = initialRequestValues;
    this.columnDefs = columnDefs;
    this.responseSchema = responseSchema;
    this.selectionEnabled = enableSelection;
    this.init();
};

APP.util.SimpleTable.prototype = {

    container: null,
    urlRequest: null,
    initialRequestValues: null,
    columnDefs: null,
    responseSchema: null,
    selectionEnabled: false,
    dataStore: null,
    dataTable: null,

    init: function() {
        var dataTableConfigs = {
            paginated: true,
            paginator: {
                rowsPerPage: 50,
                pageLinks: -1
            },
            initialRequest: this.initialRequestValues,
            scrollable: true
        };
        this.dataSource = new YAHOO.util.DataSource(this.urlRequest);
        this.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
        this.dataSource.responseSchema = this.responseSchema;
        this.dataTable = new YAHOO.widget.DataTable(this.container, this.columnDefs, this.dataSource, dataTableConfigs);

        if (this.selectionEnabled) {
            this.dataTable.set("selectionMode","single");
            this.dataTable.subscribe("rowMouseoverEvent", this.dataTable.onEventHighlightRow);
	        this.dataTable.subscribe("rowMouseoutEvent", this.dataTable.onEventUnhighlightRow);
	        this.dataTable.subscribe("rowClickEvent", this.dataTable.onEventSelectRow);
        }
    }

};




