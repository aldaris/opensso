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
 * $Id: tasksPage.js,v 1.1 2008-01-17 06:36:24 veiming Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

// JavaScript Document

var hide  = true;
var tleft;
var ttop; 
var ileft;
var colnum = 17; // number of tasks

function getElementPosition(elemID) {
    var offsetTrail = document.getElementById(elemID);
    var offsetLeft = 0;
    var offsetTop = 0;


    while (offsetTrail) {
        offsetLeft += offsetTrail.offsetLeft;
        offsetTop += offsetTrail.offsetTop;
        offsetTrail = offsetTrail.offsetParent;
    }
    if (navigator.userAgent.indexOf("Mac") != -1 && 
        typeof document.body.leftMargin != "undefined") {
        offsetLeft += document.body.leftMargin;
        offsetTop += document.body.topMargin;
    }
   tleft=offsetLeft;
   ttop=offsetTop;

   //return {left:offsetLeft, top:offsetTop}; 

}
function getElementPosition2(elemID) {
    var offsetTrail = document.getElementById(elemID);
    var offsetLeft = 0;
    var offsetTop = 0;


    while (offsetTrail) {
        offsetLeft += offsetTrail.offsetLeft;
        offsetTop += offsetTrail.offsetTop;
        offsetTrail = offsetTrail.offsetParent;
    }
    if (navigator.userAgent.indexOf("Mac") != -1 && 
        typeof document.body.leftMargin != "undefined") {
        offsetLeft += document.body.leftMargin;
        offsetTop += document.body.topMargin;
    }
   ileft=offsetLeft;

   //return {left:offsetLeft, top:offsetTop}; 

}


function closeAll(num) {
  for(i=1;i<=colnum;i++) {
    if(document.getElementById("info"+i) && document.getElementById("togImg"+i)) {
      document.getElementById("info"+i).style.display = "none";   
      document.getElementById("togImg"+i).src = "../console/images/tasks/rightToggle.gif";
    }
  }
  document.getElementById("i"+num).focus();
}
function showDiv(num) {
document.getElementById("info"+num).style.display = "block";
}

function hideAllMenus() {
  for(i=1;i<=colnum;i++) {
    if(document.getElementById("info"+i) && document.getElementById("togImg"+i)) {
      document.getElementById("info"+i).style.display = "none";
      document.getElementById("togImg"+i).src = "../console/images/tasks/rightToggle.gif";  
    }
  }
}


// Toggle functions

function test(num) {
  getElementPosition2("togImg"+num);
  if (document.getElementById("info"+num).style.display != "block") {
    for(i=1;i<=colnum;i++) {
      if(i!=num && document.getElementById("togImg"+i) && document.getElementById("info"+i)) {
        document.getElementById("togImg"+i).src = "../console/images/tasks/rightToggle.gif";
        document.getElementById("info"+i).style.display = "none";
      }
    }
    document.getElementById("togImg"+num).src = "../console/images/tasks/rightToggle-selected.gif";

    getElementPosition("gif"+num);



    document.getElementById("info"+num).style.display = "block";
    document.getElementById("info"+num).style.top = (ttop + 10) + 'px';
    document.getElementById("info"+num).style.left = (tleft -1) + 'px';
    document.getElementById("info"+num).style.width = (ileft - tleft) + 29 + 'px';
    
    document.getElementById("close"+num).focus();
  }
  else if (document.getElementById("info"+num).style.display = "block"){
    for(i=1;i<=colnum;i++) {
      if(document.getElementById("togImg"+i)) {
        document.getElementById("togImg"+i).src = "../console/images/tasks/rightToggle.gif";
      }
    }
    document.getElementById("info"+num).style.display = "none";
  }
}


// Hover Functions

function hoverImg(num) {
  if (document.getElementById("info"+num).style.display != "block") {
    document.getElementById("togImg"+num).src = "../console/images/tasks/rightToggle-rollover.gif"
  }
  else { 
    document.getElementById("togImg"+num).src = "../console/images/tasks/rightToggle-selected.gif";
  }
}
function outImg(num) {
  if (document.getElementById("info"+num).style.display != "block") {
    document.getElementById("togImg"+num).src = "../console/images/tasks/rightToggle.gif";
  }
  else {
    document.getElementById("togImg"+num).src = "../console/images/tasks/rightToggle-selected.gif";
  }
}

