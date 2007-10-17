/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: am.js,v 1.2 2007-10-17 23:00:50 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

var origFrmAction = '';
var tblBtnCounter = new Array();

function openNewWindow() {
    var feature = 'directories=no,location=0,menubar=0,status=0,titlebar=yes,toolbar=no,scrollbars=yes,width=800,height=600,resizable=yes';
    var openwin = window.open('', 'newwindow', feature);
    openwin.focus();
}

function submitButton(btn, val) {
    var frm = document.forms[0];
    frm.target = 'newwindow';
    origFrmAction = frm.action;
    frm.action += '?attrname=' + val;
    setTimeout("resetForm()", 1000);
}

function resetForm() {
    var frm = document.forms[0];
    frm.target = '';
    frm.action = origFrmAction;
}

/**
 * Enables and Disables the Delete button of a table.
 *
 * @param formName Name of form.
 * @param tblName Name of table.
 * @param counterName Name of counter.
 * @param btn Button object.
 * @param trigger Object that triggers this event.
 */
function toggleTblButtonState(formName, tblName, counterName, btn, trigger) {
    toggleTblButtonStateEx(formName, tblName, counterName, btn, trigger, false);
}

/**
 * Enables and Disables the Delete button of a table.
 *
 * @param formName Name of form.
 * @param tblName Name of table.
 * @param counterName Name of counter.
 * @param btn Button object.
 * @param trigger Object that triggers this event.
 * @param singleCheckbox true if button is enable is only one checkbox iss
 * selected.
 */
function toggleTblButtonStateEx(formName, tblName, counterName, btn, trigger,
    singleCheckbox) {
    if (tblBtnCounter[counterName] == undefined) {
	tblBtnCounter[counterName] = 0;
    }
    var prevState = (singleCheckbox) ? (tblBtnCounter[counterName] != 1) :
        (tblBtnCounter[counterName] <= 0);

    if (trigger.name.indexOf('DeselectAllHref') != -1) {
	tblBtnCounter[counterName] = 0;
    } else if (trigger.name.indexOf('SelectAllHref') != -1) {
	tblBtnCounter[counterName] = countCheckboxesInTable(formName, tblName);
    } else {
	if (trigger.checked) {
	    tblBtnCounter[counterName]++;
	} else {
	    tblBtnCounter[counterName]--;
	}
    }

    var currState = (singleCheckbox) ? (tblBtnCounter[counterName] != 1) :
        (tblBtnCounter[counterName] <= 0);

    if (btn) {
	if (prevState != currState) {
	    ccSetButtonDisabled(btn, formName, currState);
	}
    }
}

function countCheckboxesInTable(formName, tblName) {
    var frm = document.forms[formName];
    var cbCount = 0;
                                                                                
    for (var i = 0; i < frm.elements.length; i++) {
	var e = frm.elements[i];
        if ((e.type == 'checkbox') &&
	    (e.name.indexOf(tblName + '.SelectionCheckbox') != -1)
        ) {
	    cbCount++;
	}
    }
                                                                                
    return cbCount;
}


