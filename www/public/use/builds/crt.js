var crttemplate='<!DOCTYPE html PUBLIC "-//w3c//dtd html 4.0 transitional//en"><html><div id="ENTIREHTML"><head><meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"><title>OpenSSO CRT</title>  <link rel="stylesheet" href="https://opensso.dev.java.net/public/use/builds/crt.css"> <script src="https://opensso.dev.java.net/public/use/builds/crt.js" type="text/javascript"></script> </head> <body onload="getUser()" bgcolor="#ffffff" text="#000000"> <table cellpadding="2" width="100%"> <tbody><tr> <td align="center" valign="top" width="99%"> <h2>OpenSSO Change/Patch Request Template (Draft v0.1)</h2> </td>  <!-- <td align="right" bgcolor="#eeeeee" nowrap="nowrap" width="1%"> View<br> <a href="http://policy.red.iplanet.com/cgi-bin/crtviewer7_1.cgi">all CRTs</a><br> <a href="http://policy.red.iplanet.com/cgi-bin/crtviewer_installer7_1.cgi">installer related CRTs</a><br> <a href="http://policy.red.iplanet.com/cgi-bin/crtviewer_migration7_1.cgi">migration related CRTs</a><br> </td> --> </tr> </tbody></table> <div id="collabnet"> <div id="login"> <div> Logged in: <strong class="username">rajeevangal</strong> |    <a href="https://www.dev.java.net/servlets/Logout">Logout</a>   </div> </div> <a href="http://www.collab.net/special/clickpbc0502.html" id="cnlogo"> <span class="alt">CollabNet Enterprise Edition</span></a> </div> <hr color="#cccccc" noshade="noshade" size="1">  <form name="CRT" onSubmit="aggSubmit();return false;" >  <div class="secHdrTxt">General</div> <br>  <table> <tbody> <tr> <td>&nbsp;</td> <td>Summitter :</td> <td><input size="17" name="submitter" type="text" value="CRTsubmitter"></td> </tr> <tr> <td>&nbsp;</td> <td>Issue number:</td> <td><input size="17" name="id" type="text" value="CRTid"></td> </tr> <tr> <td valign="top"><font color="red">*</font></td> <td colspan="2" valign="top">Short description of the problem and the solution:</td> </tr> <tr> <td>&nbsp;</td> <td colspan="2"><textarea name="descr" cols="80" rows="5">CRTdescr</textarea></td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Engineering</div> <br>  <table> <tbody><tr> <td><input name="otherparts" type="checkbox" CRTotherparts></td> <td>Affects other parts of the product</td> </tr> <tr> <td>&nbsp;</td> <td> <table> <tbody><tr> <td>** Were the dependencies rebuilt and tested? <input name="otherpartstested" type="checkbox" CRTotherpartstested > </td> </tr> </tbody></table> </td> </tr> <tr> <td><input name="apichange" type="checkbox" CRTapichange></td> <td>API Changes</td> </tr>  <tr> <td><input name="uichange" type="checkbox" CRTuichange></td> <td>User Interface Modified</td> </tr> <tr> <td>&nbsp;</td> <td>UI reviewer :</td> <td><input size="17" name="uireviewer" type="text" value="CRTuireviewer"> <a href="https://opensso.dev.java.net/nonav/servlets/AssignableUsers" onclick="return launch(this.href, 3, false, document.getElementById(\'CRT\').uireviewer)">Lookup users</a> </td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Documentation</div> <br>  <table> <tbody> <tr> <td valign="top"><font color="red">*</font></td> <td>User documentation :<br>Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/OpenSSO\')">Wiki</a> pointer :</td> <td><input name="userdocs" type="text" size="50" value="CRTuserdocs"></td> </tr> <tr> <td valign="top"><font color="red">*</font></td> <td>Diagnostics/Troubleshooting documentation :<br>Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/OpenSSO\')">Wiki</a> pointer :</td> <td><input name="tddocs" type="text" value="CRTtddocs" size="50"></td> </tr>  <tr> <td valign="top"><font color="red">*</font></td> <td>Online Help Changes :<br>Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/OpenSSO\')">Wiki</a> pointer :</td> <td><input name="online" type="text" value="CRTonline" size="50"></td> </tr> </tbody></table>  <blockquote> <font size="-1"><i>NOTE: UI, programmatic interfaces, command line parameter, configuration file and new components typically require at least one of the above. Diagnostic and troubleshooting documentation should include detection of misconfigurations and tips on correcting them. <br> Wiki instructions : (1) Create a anchor eg :</i>#CrtDoc&lt;issueid&gt;<i> (2) Append </i>CrtDoc&lt;issueid&gt; <i> to the URLs above when you are done.  </i></font> </blockquote> <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Migration/Upgrading</div> <br> <table> <tbody><tr> <td align="top"><font color="red">*</font></td> <td> Does this affect migration/upgrading from previous release?  <font color="#6666cc"><a href="javascript:popUpWindow(\'migration_help.html\')"> <i>(more information...)</i> </a></font> </td> </tr> <tr> <td>&nbsp;</td> <td>If yes, please describe. <br><textarea name="desc_migration_updating" cols="80" rows="5">CRTdesc_migration_updating</textarea> </td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Installer</div> <br> <table> <tbody><tr> <td align="top"><font color="red">*</font></td> <td>  Does this affect the installer?<font color="#6666cc"><a href="javascript:popUpWindow(\'installer_help.html\')"> <i>(more information...)</i> </a></font> </td> </tr> <tr> <td>&nbsp;</td> <td> If yes, please describe. <br><textarea name="desc_installer_change" cols="80" rows="5">CRTdesc_installer_change</textarea> </td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Test/Quality</div> <br> <table> <tbody><tr> <td>&nbsp;</td> <td>&nbsp;</td> <td> Component packaging tested? </td> </tr> <tr> <td>&nbsp;</td> <td>&nbsp;</td> <td> Unit test developed? </td> </tr> <tr> <td colspan="2">&nbsp;</td> <td valign="top">Location of the unit test files.</td> </tr> <tr> <td colspan="2">&nbsp;</td> <td><textarea name="unittestloc" cols="80" rows="5">CRTunittestloc</textarea></td> </tr>  <tr><td colspan="3">&nbsp;</td></tr> <tr> <td><font color="red">*</font></td> <td colspan="2" valign="top">Description of testing performed.</td> </tr> <tr> <td>&nbsp;</td> <td colspan="2"><textarea name="testing" cols="80" rows="5">CRTtesting</textarea></td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Review/Diffs</div> <br>  <table> <tbody><tr> <td align="top"><font color="red">*</font></td> <td>  Code following coding standards? </td> </tr> </tbody></table>  <table> <tbody><tr> <td align="top"><font color="red">*</font></td> <td>  All reviewer comments completed/addressed? </td> </tr> <tr> <td>&nbsp;</td> <td> If not, why. <br><textarea name="desc_comments_addressed" cols="80" rows="5">CRTdesc_comments_addressed</textarea> </td> </tr> </tbody></table>  <table> <tbody><tr> <td><font color="red">*</font></td> <td>Path to local workspace: <input size="30" name="localworkspace" type="text" value="CRTlocalworkspace"></td> </tr> <tr> <td><font color="red">*</font></td> <td> Listing of the files created/deleted/updated. <i>(use \'cvs stat\')</i> </td> </tr> <tr> <td>&nbsp;</td> <td><textarea name="files" cols="80" rows="10">CRTfiles</textarea></td> </tr> </tbody></table>   <p> <table> <tbody><tr> <td><font color="red">*</font></td> <td>Change Reviewer(s):&nbsp;<input size="30" name="codereviewer" type="text" value="CRTcodereviewer"> <a href="https://opensso.dev.java.net/nonav/servlets/AssignableUsers" onclick="return launch(this.href, 3, false, document.getElementById(\'CRT\').codereviewer)">Lookup users</a> </td>  </tr> <tr> <td><font color="red">*</font></td> <td>Context specific diffs (or pointer to the diffs) of the added/modified/removed files</td> </tr> <tr> <td>&nbsp;</td> <td><textarea name="diffs" cols="80" rows="10">CRTdiffs</textarea></td> </tr> </tbody></table>  </p><hr color="#cccccc" noshade="noshade" size="1">   <table> <tbody><tr> <td><input name="submitN" value="Submit (Update)" type="submit" onclick="return setBtn(\'1\');"></td> <td><input name="submitC" value="Submit (Attach)" type="submit" onclick="return setBtn(\'2\');"></td> <td><input name="submitN" value="Submit (New)" type="submit" onclick="return setBtn(\'3\');"></td> <td><input name="reset" value="Reset" type="reset"></td> </tr> </tbody></table>  </form> <br><b>Notes: </b> <br><b>Submit (Update)</b> will update an existing issue (specified by Issue# parameter) with a CRT instance in the description field.  <br><b>Submit (Attach)</b> will update an existing issue (Issue#) with a CRT instance as an attachment to the issue.  <br><b>Submit (New)</b> will create a new issue with Issue# in short summary and CRT instance in the description field.  <br><br>******** Please ignore stuff below : its for debugging **********<br><br> <FORM name="IT" action="https://opensso.dev.java.net/issues/process_bug.cgi" method="post"> <INPUT type="hidden" name="id"><BR> <INPUT type="hidden" name="component" value="opensso"> <INPUT type="hidden" name="subcomponent" value="Web site"> <INPUT type="hidden" name="version" value="current"> <INPUT type="hidden" name="rep_platform" value="All"> <INPUT type="hidden" name="op_sys" value="All"> <INPUT type="hidden" name="priority" value="P3"> <INPUT type="hidden" name="issue_type" value=PATCH> <INPUT type="hidden" name="knob" value="reassignbysubcomponent"> <INPUT type="hidden" name="assigned_to"> <INPUT type="hidden" name="newcc" > <INPUT type="hidden" name="issue_file_loc" value="http://"> <INPUT type="hidden" name="short_desc" value="TEST, IGNORE"> <textarea name="comment" rows="10" cols="80" >CRTcomment</textarea> <INPUT type="hidden" name="longdesclength" value="8000"> <input label="hidden" name="IDButton" value="submit" type="hidden"> </P> </FORM> <FORM name="ITU" action="https://opensso.dev.java.net/issues/createattachment.cgi" method="post" enctype="multipart/form-data"> <input name="id" type="hidden"> <input name="data" type="hidden" value="C:\Sun\arch\CRTstuff\1.txt"> <input name="description" type="hidden"> <input name="type" type="hidden" value="text/html"> <input name="othertype" type="hidden"> <input name="IDButton" value="submit" type="hidden"> </FORM> <FORM> <input name="id" id="id" type="hidden"> <textarea name="data" id="data"></textarea> <!-- Data:<input name="data" type="file" value="C:\Sun\arch\CRTstuff\1.txt"><br> --> <input name="description" id="description" type="hidden"> <input name="type" id="type" type="hidden" value="text/html"> <input name="othertype" id="othertype"  type="hidden"> <input type="hidden" name="IDButton" id="ajaxbutton" value="submit" onclick="upload();"> </FORM> <div id="sizespan"></div> <hr> <div id="myspan"></div> <FORM name="NEWPATCH" action="https://opensso.dev.java.net/issues/post_bug.cgi" method="post"> <P> <INPUT type="hidden" name="reporter" value="rajeevangal"> <INPUT type="hidden" name="component" value="opensso"> <INPUT type="hidden" name="subcomponent" value="Web site"> <INPUT type="hidden" name="version" value="current"> <INPUT type="hidden" name="rep_platform" value="All"> <INPUT type="hidden" name="op_sys" value="All"> <INPUT type="hidden" name="priority" value="P3"> <INPUT type="hidden" name="issue_type" value=PATCH> <INPUT type="hidden" name="assigned_to"> <INPUT type="hidden" name="cc"> <INPUT type="hidden" name="issue_file_loc" value="http://"> <INPUT type="hidden" name="short_desc" value="TEST, IGNORE"> <textarea name="comment"></textarea> <input type="hidden" label="Submit" name="IDButton" value="submit"> </P> </FORM> </body> </div> <div id=TEST></div> </html>';
var clickedBtn = '0';
var url = "https://opensso.dev.java.net/issues/createattachment.cgi";
var mydata;
var myid;
var mydescription;
var mytype;
var myothertype;
var userid = "unknown";

if (typeof(launch) == "undefined")
{
   alert("launch NOT defined");
  document.write( "\<SCRIPT SRC='https://opensso.dev.java.net/branding/scripts/tigris.js'\>\</SCRIPT\>" );
} else
   alert("launch already defined");

function escapeHTML(str)
{
    var div = document.createElement('div');
    var text = document.createTextNode(str);
    div.appendChild(text);
    return div.innerHTML;
}
function getUser()
{
    var var1 = document.getElementById('login');
    var userid = var1.childNodes[1].childNodes[1].innerHTML;
    var frms = document.forms;
    var crtFrm = frms['CRT'];
    crtFrm.elements['submitter'].value = userid;
}
function setBtn(val)
{
     clickedBtn = val;
     return true;
}
function aggSubmit() {
var crtn=new Array(37);
crtn['submitter'] ='Submitter';
crtn['id'] ='Issue#';
crtn['component'] ='Component';
crtn['project'] ='Project';
crtn['workspaces'] ='Workspaces Affected';
crtn['descr'] ='Short description of the problem and the solution';
crtn['apichange'] ='API Changed';
crtn['otherparts'] ='Affects other parts of the product';
crtn['otherpartstested'] ='Were the dependencies rebuilt and tested'
crtn['uichange'] ='UI Changed';
crtn['uireviewer'] ='UI Reviewer';
crtn['userdocs'] ='User Docs Changed';
crtn['tddocs'] ='Diagnostics/Troubleshooting Docs Changed';
crtn['online'] ='Online Help Changes Required';
crtn['migration_updating'] ='Migration/Upgrading Affected';
crtn['desc_migration_updating'] ='Migration Description';
crtn['installer_change']  ='Installer Affected';
crtn['desc_installer_change'] = 'Installer Description';
crtn['comprisk'] ='Component Risk Level';
crtn['stabrisk'] ='Product Stability Risk';
crtn['pkgtested'] ='Packaging tested';
crtn['unittested'] ='Unit test Developed';
crtn['unittestloc'] ='Unit Test Location(s)';
crtn['testing'] ='Description of testing';
crtn['code_standards'] ='Coding standards followed';
crtn['desc_code_standards'] ='If not why?';
crtn['comments_addressed'] ='All comments addressed';
crtn['desc_comments_addressed'] ='Why comments were not addressed';
crtn['localworkspace'] ='Local workspace path';
crtn['files'] ='Listing of files affected';
crtn['codereviewer'] ='Code reviewer';
crtn['diffs'] ='File Diffs';
crtn['submit'] ='Submit';
crtn['reset'] ='Reset';
crtn['id'] ='Issue#';
crtn['component'] ='Component';

    var frms = document.forms;
    var crtFrm = frms['CRT'];
    var data = '';

    if ( crtFrm == null) 
       alert ("Fatal Error : crtFrom not found.");
    var crtt = crttemplate;
    for (var j = 0; j < 2; j++ ) {
      for (var i = 0; i < crtFrm.elements.length; i++) {
	var elm = crtFrm.elements[i];
        var eval = '';
        if (elm.type == 'submit' || elm.type == 'reset')
           continue;
        if ( j == 0) {
            if (elm.type == 'textarea') {
                 continue;
            }
            if (elm.type == 'radio') {
	      if (elm.checked) {
	        eval =  elm.value;
	      } else {
                continue; 
              }
            } else if (elm.type == 'checkbox') {
              if (elm.checked) {
                eval = 'yes';
                crtt = crtt.replace('CRT'+elm.name, 'CHECKED');
              }
              else {
                eval = 'no';
              }
            } else { // Assume text
                eval = elm.value;
                crtt = crtt.replace('CRT'+elm.name, escapeHTML(eval));
            }
            
            var prt = crtn[elm.name];
            if (prt == null)
               prt = elm.name;
            var str = padleft(prt, ' ', 50) + '=' + eval + '\n';
            data = data + str;
        } else {
            if (elm.type != 'textarea')
                continue;
            var prt = crtn[elm.name];
            if (prt == null)
               prt = elm.name;
            var val = elm.value;
            if (val == '')
                val = '*Not Specified*'
            data = data + '\n' + prt + '\n--------------------\n'+val + '\n';
            crtt = crtt.replace('CRT'+elm.name, escapeHTML(val));
        }

       }
   }
   var hiddenFrm = document.forms['IT'];
   var hiddenFrmU = document.forms['ITU'];
   var hiddenFrmN = document.forms['NEWPATCH'];

    if (clickedBtn == '1' && hiddenFrm != null) {
        hiddenFrm.elements['id'].value = crtFrm.elements['id'].value;
        hiddenFrm.elements['comment'].value = data;
        /* alert(data); */
        hiddenFrm.elements['IDButton'].value = 'Submit';
        hiddenFrm.submit();
    }
    if (clickedBtn == '2' && hiddenFrmU != null) {
       hiddenFrmU.elements['id'].value = crtFrm.elements['id'].value;
        hiddenFrmN.elements['comment'].value = data;
	//mydata = data;
	mydata = crtt;
	myid = crtFrm.elements['id'].value;
	mydescription = "CRT:"+myid;
	mytype = "text/html"
	myothertype = '';
	//document.getElementById('ajaxbutton').disabled = true;
        hiddenFrm.elements['comment'].value = crtt;
        hiddenFrmU.elements['data'].value = crtt;
        ajax_upload();
    }
    if (clickedBtn == '3' && hiddenFrmN != null)
    {
       hiddenFrmU.elements['id'].value = crtFrm.elements['id'].value;
        hiddenFrmN.elements['comment'].value = data;
	mydata = data;
	myid = crtFrm.elements['id'].value;
	mydescription = "CRT:"+myid;
	mytype = "text/html"
	myothertype = '';
	//document.getElementById('ajaxbutton').disabled = true;
        hiddenFrm.elements['comment'].value = crtt;
        hiddenFrmU.elements['data'].value = crtt;
        //ajax_upload();
    }
    
}
function padleft(val, ch, num) {
   var ll = val.length;
   var ret = val;
   if (ll < num) {
      ll =  num - ll;
      for (var i =0; i < ll ; i++) {
         ret = ch + ret;
      }
   }
   return ret;
}



function upload() {
	mydata = crtt;// document.getElementById('data').value;
	myid = document.getElementById('id').value;
	mydescription = document.getElementById('description').value;
	mytype = document.getElementById('type').value;
	myothertype = document.getElementById('othertype').value;
	//document.getElementById('ajaxbutton').disabled = true;

	// start AJAX file upload in 1 second
	window.setTimeout("ajax_upload()", 1000);
}

function ajax_upload() {
  // Try IE stuff first
  try {
    http_request = new ActiveXObject("Msxml2.XMLHTTP");
  } catch (e) {
     try {
        http_request = new ActiveXObject("Microsoft.XMLHTTP");
     } catch (E) {
        http_request = false;
     }
  }
   
  // Not IE - assume Netscape/Mozilla/Firefox
  if (!http_request && typeof XMLHttpRequest != 'undefined') {
	// request more permissions
	try {
		netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
	} catch (e) {
	//	alert("Permission open connection was denied." + e);
	}
        try {
	   http_request = new XMLHttpRequest();
        } catch(e) {
	   alert('Cannot create XMLHttpRequest instance' + e );
        }
  }
  if (!http_request) {
	alert('Cannot create XMLHttpRequest instance');
	return false;
  }

	// prepare the MIME POST data
	var boundaryString = 'somerandomstrxxyyujjj';
	var boundary = '--' + boundaryString;
	var requestbody = boundary + '\r\n' 
	+ 'Content-Disposition: form-data; name="id"' + '\r\n' 
	+ '\r\n' 
	+ myid 
	+ '\r\n' + boundary + '\r\n' 
	+ 'Content-Disposition: form-data; name="data"; filename="' 
		+ mydescription + '"' + '\r\n' 
	+ 'Content-Type: application/octet-stream' + '\r\n' 
	+ '\r\n'
	+ mydata
	+ '\r\n' + boundary + '\r\n'
	+ 'Content-Disposition: form-data; name="description"' + '\r\n' 
	+ '\r\n' 
	+ mydescription 
	+ '\r\n' + boundary + '\r\n' 
	+ 'Content-Disposition: form-data; name="type"' + '\r\n' 
	+ '\r\n' 
	+ mytype
	+ '\r\n' + boundary + '\r\n' 
	+ 'Content-Disposition: form-data; name="othertype"' + '\r\n' 
	+ '\r\n' 
	+ myothertype
	+ '\r\n' + boundary + '--\r\n';
        

	document.getElementById('sizespan').innerHTML = 
		"requestbody.length=" + requestbody.length;
	
	// do the AJAX request
        try {
	    http_request.onreadystatechange = requestdone;
	    http_request.open('POST', url, true);
	    http_request.setRequestHeader("Content-type", "multipart/form-data; boundary=" + boundaryString);
	    http_request.setRequestHeader("Connection", "close");
	    http_request.setRequestHeader("Content-length", requestbody.length);
	    http_request.send(requestbody);
        } catch(e) {
            alert("Problem creating CRT:"+e);
        }

}

function requestdone() {
	if (http_request.readyState == 4) {
		if (http_request.status == 200) {
			result = http_request.responseText;
		        //document.getElementById('myspan').innerHTML = result;            
                        document.write(result);
                        document.close();
		} else {
			alert('There was a problem with the request.');
		}
		//document.getElementById('ajaxbutton').disabled = false;
	}
}
function popUpWindow(url) {
    var bars = 'directories=no,location=0,menubar=0,status=0,titlebar=yes,toolbar=no';
    var options = 'scrollbars=yes,width=400,height=600,resizable=yes';
    var feature = bars + ',' + options;
    var openwin = window.open(url, 'help', feature);
    openwin.focus();
}
