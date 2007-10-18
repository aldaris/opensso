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

APP.util.PaginatedTable = function( container, urlRequest, initialRequestValues, columnDefs, responseSchema ) {
    this.container = container;
    this.urlRequest = urlRequest;
    this.initialRequestValues = initialRequestValues;
    this.columnDefs = columnDefs;
    this.responseSchema = responseSchema;
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
    rowPerPage: 0,
    startIndex: 0,
    endIndex: 0,
    recordsReturned: 0,
    totalRecords: 0,

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
        this.dataSource.doBeforeCallback = this._parseData;
        this.dataTable = new YAHOO.widget.DataTable(this.container, this.columnDefs, this.dataSource, dataTableConfigs);
        this.dataTable.subscribe("refreshEvent", this.destroyDragDrops, this, true);
        this.dataTable.subscribe("refreshEvent", this.createDragDrops, this, true);
    },

    _parseData: function( pRequest, pRawResponse, pParsedResponse ) {
        // Get Paginator values
        var rawResponse = pRawResponse.parseJSON(); //JSON.parse(oRawResponse); // Parse the JSON data
        this.recordsReturned = rawResponse.recordsReturned; // How many records this page
        this.startIndex = rawResponse.startIndex; // Start record index this page
        this.endIndex = this.startIndex + this.recordsReturned - 1; // End record index this page
        this.totalRecords = rawResponse.totalRecords; // Total records all pages

        // Update the links UI
        //YAHOO.util.Dom.get("prevLink").innerHTML = (oSelf.startIndex - oSelf.recordsReturned <= 0) ? "<" : "<a href=\"#previous\" alt=\"Show previous page\"><</a>";
        //YAHOO.util.Dom.get("nextLink").innerHTML = (oSelf.endIndex >= oSelf.totalRecords) ? ">" : "<a href=\"#next\" alt=\"Show next page\">></a>";
        //YAHOO.util.Dom.get("startIndex").innerHTML = oSelf.startIndex;
        //YAHOO.util.Dom.get("endIndex").innerHTML = oSelf.endIndex;
        //YAHOO.util.Dom.get("ofTotal").innerHTML = " of " + oSelf.totalRecords;

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
        while ( nextTrEl ) {
            new APP.util.CustomDDProxy(nextTrEl.id, "paginatedTableGroup", {resizeFrame:true});
            nextTrEl = nextTrEl.nextSibling;
        }
    },

    destroyDragDrops: function() {
        var DDM = YAHOO.util.DragDropMgr;
        var ids = DDM.ids["paginatedTableGroup"];
        if ( ids ) {
            var nextTrEl = this.dataTable.getTrEl(0);
            while ( nextTrEl ) {
                var currentDD = ids[nextTrEl.id];
                nextTrEl = nextTrEl.nextSibling;
                if ( ! DDM.isTypeOfDD(currentDD) ) {
                    continue;
                }
                currentDD.removeFromGroup("paginatedTableGroup");
            }
        }
    }

};


APP.util.CustomDDProxy = function( id, sGroup, config ) {
    APP.util.CustomDDProxy.superclass.constructor.call(this, id, sGroup, config);
    var pEl = this.getDragEl();
    YAHOO.util.Dom.setStyle(pEl, "opacity", 0.60); // The proxy is slightly transparent
};

YAHOO.extend(APP.util.CustomDDProxy, YAHOO.util.DDProxy, {

    startDrag: function( x, y ) {
        var DOM = YAHOO.util.Dom;
        var sEl = this.getEl();
        var pEl = this.getDragEl();

        // Makes the proxy look like the source element
        pEl.innerHTML = sEl.innerHTML;
        DOM.setStyle(sEl, "visibility", "hidden");
        DOM.setStyle(pEl, "color", DOM.getStyle(sEl, "color"));
        DOM.setStyle(pEl, "backgroundColor", DOM.getStyle(sEl, "backgroundColor"));
        DOM.setStyle(pEl, "border", "2px solid gray");
    },

    endDrag: function( e ) {
        var DOM = YAHOO.util.Dom;
        var sEl = this.getEl();
        var pEl = this.getDragEl();

        // Show the proxy element and animate it to the src element's location
        DOM.setStyle(pEl, "visibility", "");

        var endDragMotion = new YAHOO.util.Motion(
            pEl, {
            points: {
                to: DOM.getXY(sEl)
            }
        },
            0.2,
            YAHOO.util.Easing.easeOut
            )

        // Hide the proxy and show the source element when finished with the animation
        endDragMotion.onComplete.subscribe(function() {
            DOM.setStyle(pEl, "visibility", "hidden");
            DOM.setStyle(sEl, "visibility", "");
        });

        endDragMotion.animate();
    },

/*
onDragDrop: function(e, id) {


    // If there is one drop interaction, the li was dropped either on the list,
    // or it was dropped on the current location of the source element.
    if (DDM.interactionInfo.drop.length === 1) {

        // The position of the cursor at the time of the drop (YAHOO.util.Point)
        var pt = DDM.interactionInfo.point;

        // The region occupied by the source element at the time of the drop
        var region = DDM.interactionInfo.sourceRegion;

        /*
        // Check to see if we are over the source element's location.  We will
        // append to the bottom of the list once we are sure it was a drop in
        // the negative space (the area of the list without any list items)
        if (!region.intersect(pt)) {
            var destEl = Dom.get(id);
            var destDD = DDM.getDDById(id);
            destEl.appendChild(this.getEl());
            destDD.isEmpty = false;
            DDM.refreshCache();
        }
        /
    }

},
*/

    onDrag: function( e ) {
    },

    onDragOver: function( e, id ) {
        var DOM = YAHOO.util.Dom;
        var sEl = this.getEl(); // Source Element
        var dEl = DOM.get(id);   // Destination Element

        if ( dEl.nodeName.toLowerCase() == "tr" ) {
            //var sParent = sEl.parentNode;   // Original parent node
            var dParent = dEl.parentNode;   // Destination parent node

            dParent.insertBefore(sEl, dParent.lastChild.nextSibling);

            /*
            if (this.goingUp) {
                p.insertBefore(srcEl, destEl); // insert above
            } else {
                p.insertBefore(srcEl, destEl.nextSibling); // insert below
            }
            */

            //DDM.refreshCache();
        }
    }

});


