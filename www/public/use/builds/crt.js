var crttemplate='<!DOCTYPE html PUBLIC "-//w3c//dtd html 4.0 transitional//en"><html><head><meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"><title>OpenSSO CRT</title><link rel="stylesheet" href="https://opensso.dev.java.net/public/use/builds/crt.css"> <script src="https://opensso.dev.java.net/public/use/builds/crt.js" type="text/javascript"></script> </head> <body bgcolor="#ffffff" text="#000000"> <table cellpadding="2" width="100%"> <tbody><tr> <td align="center" valign="top" width="99%"> <h2>OpenSSO Change/Patch Request Template (Draft v0.1)</h2> </td> </tr> </tbody></table> <hr color="#cccccc" noshade="noshade" size="1"><div style="display:inline"><fieldset><legend>Approval</legend><form name="approve" id="approve" onSubmit="approveSubmit();return false;"><a href="https://opensso.dev.java.net/nonav/servlets/AssignableUsers" onclick="return launch(this.href, 3, false, document.getElementById(\'approve\').userid)">UserId:</a><input name="userid" type="text">Check if Approved:<input name="approval" type="checkbox">Brief comment :<input name="comment" type="text"><input name="Submit" type="Submit" value="Submit" ></form></formset><p></div><div id="crtform" style="display:inline"><p><a>*Form View</a>&nbsp;|&nbsp;<a onClick="showStuff(\'crttext\');hideStuff(\'crtform\');" href="#">Text View</a> <form name="CRT" id="CRT" onSubmit="aggSubmit();return false;" >  <div class="secHdrTxt">General</div> <br>  <table> <tbody> <tr> <td>&nbsp;</td> <td>Submitter :</td> <td><input size="17" name="submitter" type="text" valuel="CRTsubmitter" value=""></td> </tr> <tr> <td>&nbsp;</td> <td>Issue number:</td> <td><input size="17" name="id" type="text" valuel="CRTid" value=""></td> </tr> <tr> <td valign="top"><font color="red">*</font></td> <td colspan="2" valign="top">Short description of the problem and the solution:</td> </tr> <tr> <td>&nbsp;</td> <td colspan="2"><textarea name="descr" cols="80" rows="5" CRTdescr></textarea></td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Engineering</div> <br>  <table> <tbody><tr> <td><input name="otherparts" type="checkbox" CRTotherparts></td> <td>Affects other parts of the product</td> </tr> <tr> <td>&nbsp;</td> <td> <table> <tbody><tr> <td>** Were the dependencies rebuilt and tested? <input name="otherpartstested" type="checkbox" CRTotherpartstested > </td> </tr> </tbody></table> </td> </tr> <tr> <td><input name="apichange" type="checkbox" CRTapichange></td> <td>API Changes</td> </tr>  <tr> <td><input name="uichange" type="checkbox" CRTuichange></td> <td>User Interface Modified</td> </tr> <tr> <td>&nbsp;</td> <td>UI reviewer :</td> <td><input size="17" name="uireviewer" type="text" valuel="CRTuireviewer" value=""><a href="https://opensso.dev.java.net/nonav/servlets/AssignableUsers" onclick="return launch(this.href, 3, false, document.getElementById(\'CRT\').uireviewer)">Lookup users</a></td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Documentation</div> <br>  <table> <tbody> <tr> <td valign="top"><font color="red">*</font></td> <td>User documentation :<br>Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/OpenSSO\')">Wiki</a> pointer :</td> <td><input name="userdocs" type="text" size="50" valuel="CRTuserdocs" value="">&nbsp;<a href="javascript:popUpWindow(document.getElementById(\'CRT\').userdocs.value)">View</a> </td> </tr> <tr> <td valign="top"><font color="red">*</font></td> <td>Diagnostics/Troubleshooting documentation :<br>Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/OpenSSO\')">Wiki</a> pointer :</td> <td><input name="tddocs" type="text" valuel="CRTtddocs" value="" size="50"> &nbsp;<a href="javascript:popUpWindow(document.getElementById(\'CRT\').tddocs.value)">View</a> </td> </tr>  <tr> <td valign="top"><font color="red">*</font></td> <td>Online Help Changes :<br>Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/OpenSSO\')">Wiki</a> pointer :</td> <td><input name="online" type="text" valuel="CRTonline" value="" size="50"> &nbsp;<a href="javascript:popUpWindow(document.getElementById(\'CRT\').online.value)">View</a> </td> </tr> </tbody></table>  <blockquote> <font size="-1"><i>NOTE: UI, programmatic interfaces, command line parameter, configuration file and new components typically require at least one of the above. Diagnostic and troubleshooting documentation should include detection of misconfigurations and tips on correcting them. <br> Wiki instructions : (1) Create a anchor eg :</i>#CrtDoc&lt;issueid&gt;<i> (2) Append </i>CrtDoc&lt;issueid&gt; <i> to the URLs above when you are done.  </i></font> </blockquote> <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Migration/Upgrading</div> <br> <table> <tbody><tr> <td align="top"><font color="red">*</font></td> <td> Does this affect migration/upgrading from previous release?  <font color="#6666cc"><a href="javascript:popUpWindow(\'https://opensso.dev.java.net/public/use/builds/migration_help.html\')"> <i>(more information...)</i> </a></font> </td> </tr> <tr> <td>&nbsp;</td> <td>If yes, please describe. <br><textarea name="desc_migration_updating" cols="80" rows="5" CRTdesc_migration_updating></textarea> </td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Installer</div> <br> <table> <tbody><tr> <td align="top"><font color="red">*</font></td> <td>  Does this affect the installer?<font color="#6666cc"><a href="javascript:popUpWindow(\'https://opensso.dev.java.net/public/use/builds/installer_help.html\')"> <i>(more information...)</i> </a></font> </td> </tr> <tr> <td>&nbsp;</td> <td> If yes, please describe. <br><textarea name="desc_installer_change" cols="80" rows="5" CRTdesc_installer_change></textarea> </td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Test/Quality</div> <br> <table> <tbody><tr> <td>&nbsp;</td> <td>&nbsp;</td> <td> Component packaging tested? </td> </tr> <tr> <td>&nbsp;</td> <td>&nbsp;</td> <td> Unit test developed? </td> </tr> <tr> <td colspan="2">&nbsp;</td> <td valign="top">Location of the unit test files.</td> </tr> <tr> <td colspan="2">&nbsp;</td> <td><textarea name="unittestloc" cols="80" rows="5" CRTunittestloc></textarea></td> </tr>  <tr><td colspan="3">&nbsp;</td></tr> <tr> <td><font color="red">*</font></td> <td colspan="2" valign="top">Description of testing performed.</td> </tr> <tr> <td>&nbsp;</td> <td colspan="2"><textarea name="testing" cols="80" rows="5" CRTtesting></textarea></td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Review/Diffs</div> <br>  <table> <tbody><tr> <td align="top"><font color="red">*</font></td> <td>  Code following coding standards? </td> </tr> </tbody></table>  <table> <tbody><tr> <td align="top"><font color="red">*</font></td> <td>  All reviewer comments completed/addressed? </td> </tr> <tr> <td>&nbsp;</td> <td> If not, why. <br><textarea name="desc_comments_addressed" cols="80" rows="5" CRTdesc_comments_addressed></textarea> </td> </tr> </tbody></table>  <table> <tbody><tr> <td><font color="red">*</font></td> <td>Path to local workspace: <input size="30" name="localworkspace" type="text" valuel="CRTlocalworkspace" value=""></td> </tr> <tr> <td><font color="red">*</font></td> <td> Listing of the files created/deleted/updated. <i>(use \'cvs stat\')</i> </td> </tr> <tr> <td>&nbsp;</td> <td><textarea name="files" cols="80" rows="10" CRTfiles></textarea></td> </tr> </tbody></table>   <p> <table> <tbody><tr> <td><font color="red">*</font></td> <td>Change Reviewer(s):&nbsp;<input size="30" name="codereviewer" type="text" valuel="CRTcodereviewer" value=""> <a href="https://opensso.dev.java.net/nonav/servlets/AssignableUsers" onclick="return launch(this.href, 3, false, document.getElementById(\'CRT\').codereviewer)">Lookup users</a> </td>  </tr> <tr> <td><font color="red">*</font></td> <td>Context specific diffs (or pointer to the diffs) of the added/modified/removed files</td> </tr> <tr> <td>&nbsp;</td> <td><textarea name="diffs" cols="80" rows="10" CRTdiffs></textarea></td> </tr> </tbody></table>  </p><hr color="#cccccc" noshade="noshade" size="1">   <table> <tbody><tr><td><input name="submitC" value="Submit (Attach)" type="submit" onclick="return setBtn(\'2\');"></td><td><input name="reset" value="Reset" type="reset"></td> </tr> </tbody></table>  </form></div><div id="crttext" style="display:none"> <p><a onClick="hideStuff(\'crttext\');showStuff(\'crtform\');" href="#">Form View</a>&nbsp;|&nbsp;*Text View <br><br><textarea id="crttexttb" cols=80 rows=100>CRTTEXTDATA</textarea><div><div id="sizespan"></div> <hr> <div id="myspan"></div><script>getUser(true); if (userid == null || userid.length == 0 || userid == \'Login\') alert("You must Login first."); else { setupUser(\'CRT\', \'submitter\'); setupUser(\'approve\', \'userid\');} </script> </body></html>';
var clickedBtn = '0';
var url = "https://opensso.dev.java.net/issues/createattachment.cgi";
var uurl = "https://opensso.dev.java.net";
var mydata;
var myid;
var mydescription;
var mytype;
var myothertype;
var userid = "Login";

if (typeof(launch) == "undefined")
{
  document.write( "\<SCRIPT SRC='https://opensso.dev.java.net/branding/scripts/tigris.js'\>\</SCRIPT\>" );
}
function hideStuff(id)
{
   var obj = document.getElementById(id);
   //obj.style.visibility = 'hidden';
   obj.style.display = 'none';
}
function showStuff(id)
{
   var obj = document.getElementById(id);
   //obj.style.visibility = 'visible';
   obj.style.display = 'inline';
}
function escapeHTML(str)
{
    var div = document.createElement('div');
    var text = document.createTextNode(str);
    div.appendChild(text);
    return div.innerHTML;
}
function setupUser(formn, elem)
{
   var frms = document.forms;
   var crtFrm = frms[formn];
   var val = crtFrm.elements[elem].value;
   if (val == null || val.length == 0)
      crtFrm.elements[elem].value = userid;
}

var uhttp_request;
function getUser(usereq)
{
    var var1 = document.getElementById('login');
    if (var1 != null) {
        userid = var1.childNodes[1].childNodes[1].innerHTML;
    } else {
        if (usereq) {
           uhttp_request = get_request_handle()
           try {
	      uhttp_request.open('GET', uurl, false);
	      uhttp_request.send(null);
              var result = uhttp_request.responseText;
              var st1 = result.indexOf('class="username"');
              if (st1 == -1)
                 return;
              var st2 = result.indexOf(">", st1);
              var st3 = result.indexOf("<", st2);
              userid = result.substring(st2+1,st3);
           } catch(e) {
                alert("Problem scraping for user:"+e);
            }
        }
    }
        
}

function setBtn(val)
{
     clickedBtn = val;
     return true;
}

function approveSubmit()
{
    var frms = document.forms;
    var crtFrm = frms['CRT'];
    myid = crtFrm.elements['id'].value;
    var approveFrm = frms['approve'];
    mydescription = "CRT_notapproved:"+myid;
    var approved = 'NOTAPPROVED';
    mydescription = "CRT_notapproved:"+myid;
    if (approveFrm.elements['approval'].checked) {
       approved = 'APPROVED';
       mydescription = "CRT_approved:"+myid;
    }
    var approver = approveFrm.elements['userid'].value;
    var comment = approveFrm.elements['comment'].value;

    mydata = "CRT="+window.document.location+"\nstatus="+approved+"\nBY="+approver+"\nComment="+comment; 
    mytype = "text/plain"
    myothertype = '';
    ajax_upload();
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
                crtt = crtt.replace('valuel="CRT'+elm.name+'" ', 'value="'+escapeHTML(eval)+'" o');
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
            crtt = crtt.replace('CRT'+elm.name+'>', '>'+escapeHTML(val));
        }

       }
   }
    if (clickedBtn == '2' ) {
	//mydata = data;
	document.getElementById('crttexttb').value=escapeHTML(data);
        crtt = crtt.replace('CRTTEXTDATA', escapeHTML(data));
	mydata = crtt;
	myid = crtFrm.elements['id'].value;
	mydescription = "CRT:"+myid;
	mytype = "text/html"
	myothertype = '';
	//document.getElementById('ajaxbutton').disabled = true;
        ajax_upload();
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

function get_request_handle()
{
  // Try IE stuff first
  var hr;
  try {
    hr = new ActiveXObject("Msxml2.XMLHTTP");
  } catch (e) {
     try {
        hr = new ActiveXObject("Microsoft.XMLHTTP");
     } catch (E) {
        hr = false;
     }
  }
   
  // Not IE - assume Netscape/Mozilla/Firefox
  if (!hr && typeof XMLHttpRequest != 'undefined') {
	// request more permissions
	try {
		netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
	} catch (e) {
	//	alert("Permission open connection was denied." + e);
	}
        try {
	   hr = new XMLHttpRequest();
        } catch(e) {
	   alert('Cannot create XMLHttpRequest instance' + e );
        }
  }
  if (!hr) {
	alert('Cannot create XMLHttpRequest instance');
	return false;
  }
  return hr;

}
var http_request;
function ajax_upload() {
        http_request = get_request_handle();
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
        

	//document.getElementById('sizespan').innerHTML = 
		//"requestbody.length=" + requestbody.length;
	
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
            result = result.replace('/@import "/g','@import "https://opensso.dev.java.net');
            result = result.replace('UTF-8', 'iso-8859-1');
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
