var crttemplate='<!DOCTYPE html PUBLIC "-//w3c//dtd html 4.0 transitional//en"><html><head><meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"><title>OpenSSO CRT</title><link rel="stylesheet" href="https://opensso.dev.java.net/public/use/builds/crt.css"> <script src="https://opensso.dev.java.net/public/use/builds/crt.js" type="text/javascript"></script> </head> <body bgcolor="#ffffff" text="#000000"> <table cellpadding="2" width="100%"> <tbody><tr> <td align="center" valign="top" width="99%"> <h2>OpenSSO Change/Patch Request Template (v1.0)</h2> </td> </tr> </tbody></table> <hr color="#cccccc" noshade="noshade" size="1"><div style="display:inline"><fieldset><legend>Approval</legend><form name="approve" id="approve" onSubmit="approveSubmit();return false;"><a href="https://opensso.dev.java.net/nonav/servlets/AssignableUsers" onclick="return launch(this.href, 3, false, document.getElementById(\'approve\').userid)">UserId:</a><input name="userid" type="text">Check if Approved:<input name="approval" type="checkbox">Brief comment :<input name="comment" type="text"><input name="Submit" type="Submit" value="Submit" ></form></fieldset><p><a>*Form View</a>&nbsp;|&nbsp;<a onClick="showStuff(\'crttext\');hideStuff(\'crtform\');" href="#">Text View</a></div><div id="crtform" style="display:inline"><form name="CRT" id="CRT" onSubmit="aggSubmit();return false;" >  <div class="secHdrTxt">General</div> <br>  <table> <tbody> <tr> <td valign="top"><font color="red">*</font></td><td>Submitter :</td> <td><input size="17" name="submitter" type="text" valuel="CRTsubmitter" value=""></td> </tr> <tr> <td valign="top"><font color="red">*</font></td><td>Issue number:</td> <td><input size="17" name="id" type="text" valuel="CRTid" value="">&nbsp;<a href="#" onClick="isValidIssueId(getElementById(\'CRT\').id.value, true)">Check</a></td> </tr> <tr> <td valign="top"><font color="red">*</font></td> <td colspan="2" valign="top">Short description of the problem :</td> </tr> <tr> <td>&nbsp;</td> <td colspan="2"><textarea name="descr" cols="80" rows="5" CRTdescr></textarea></td> </tr><tr><td style="vertical-align: top; font-weight: bold;"><font color="red">*</font></td><td style="vertical-align: top;" colspan="2">Solution:<br></td></tr><tr><td style="vertical-align: top;"><br></td><td style="vertical-align: top;" colspan="2"><textarea name="solution" cols="80" rows="5" CRTsolution></textarea> <br></td></tr></tbody></table><table><tbody><tr><td style="vertical-align: top;"><span style="color: rgb(255, 0, 0); font-weight: bold;">*</span><br></td><td style="vertical-align: top;" colspan="2">Select all areas that have been changed or will be affected:<br></td></tr><tr><td style="vertical-align: top;"><br></td><td style="vertical-align: top;" colspan="1"><input name="changeadminconsole" type="checkbox" CRTchangeadminconsole> Admin Console<br></td><td style="vertical-align: top;"><input name="changecli" type="checkbox" CRTchangecli> CLI<br></td><td style="vertical-align: top;"><input name="changepolicy" type="checkbox" CRTchangepolicy> Policy<br></td><td style="vertical-align: top;"><input name="changeidrepo" type="checkbox" CRTchangeidrepo> ID Repo<br></td></tr><tr><td style="vertical-align: top;"><br></td><td style="vertical-align: top;"><input name="changeauthn" type="checkbox" CRTchangeauthn> Authentication<br></td><td style="vertical-align: top;"><input name="changefed" type="checkbox" CRTchangefed> Federation<br></td><td style="vertical-align: top;"><input name="changeinstall" type="checkbox" CRTchangeinstall> Installation<br></td><td style="vertical-align: top;"><input name="changesession" type="checkbox" CRTchangesession> Session<br></td></tr><tr><td style="vertical-align: top;"><br></td><td style="vertical-align: top;"><input name="changemigration" type="checkbox" CRTchangemigration> Migration<br></td><td style="vertical-align: top;"><input name="changeupgrade" type="checkbox" CRTchangeupgrade> Upgrade<br></td><td style="vertical-align: top;"><input name="changelogging" type="checkbox" CRTchangelogging> Logging<br></td><td style="vertical-align: top;"><input name="changej2eeagent" type="checkbox" CRTchangej2eeagent> J2EE Agent<br> </td> </tr> <tr> <td style="vertical-align: top;"><br> </td> <td style="vertical-align: top;"><input name="changewebagent" type="checkbox" CRTchangewebagent> Web Agent<br> </td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Documentation</div><br> Any changes involving the user interface (command line or browser), programmatic interfaces, configuration files, or new components will typically require some documentation changes. <br> <br> Documentation update instructions:<br> <div style="margin-left: 40px;">1) Select the appropriate Wiki to add your documentation update.<br> 2) Place the anchor tag to your update in the text field. <br> 3) Select the View link to verify your tag is correct.<br> <br>  </div> <table> <tbody> <tr> <td valign="top"><font color="red">*</font></td> <td>User documentation :<br>(Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/CommunityDocumentation\')">Wiki</a> pointer)</td> <td><input name="userdocs" type="text" size="50" valuel="CRTuserdocs" value="">&nbsp;<a href="javascript:popUpWindow(document.getElementById(\'CRT\').userdocs.value)">View</a> </td> </tr> <tr> <td valign="top"><font color="red">*</font></td> <td>Diagnostics/Troubleshooting documentation :<br>(Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/TroubleshootingDebugging\')">Wiki</a> pointer)</td> <td><input name="tddocs" type="text" valuel="CRTtddocs" value="" size="50"> &nbsp;<a href="javascript:popUpWindow(document.getElementById(\'CRT\').tddocs.value)">View</a> </td> </tr> <tr> <td valign="top"><font color="red"></font></td> <td>Online Help Changes :<br>(Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/OnlineHelp\')">Wiki</a> pointer)</td> <td><input name="online" type="text" valuel="CRTonline" value="" size="50"> &nbsp;<a href="javascript:popUpWindow(document.getElementById(\'CRT\').online.value)">View</a> </td> </tr> </tbody></table>  <blockquote> <font size="-1"> ** Diagnostic and troubleshooting documentation should include detection of misconfigurations and tips on correcting them.<br> <br> </font> </blockquote> <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Migration/Upgrading</div> <br> <table> <tbody><tr> <td align="top"><font color="red"></font></td> <td> If this change affects migration/upgrading from previous release <font color="#6666cc"><a href="javascript:popUpWindow(\'https://opensso.dev.java.net/public/use/builds/migration_help.html\')"> <i>(more information...)</i> </a></font>, please describe : <br><textarea name="desc_migration_updating" cols="80" rows="5" CRTdesc_migration_updating></textarea> </td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Installer</div> <br> <table> <tbody><tr> <td align="top"><font color="red"></font></td> <td>  If this change affects the installer <font color="#6666cc"><a href="javascript:popUpWindow(\'https://opensso.dev.java.net/public/use/builds/installer_help.html\')"> <i>(more information...)</i> </a></font>, please describe : <br><textarea name="desc_installer_change" cols="80" rows="5" CRTdesc_installer_change></textarea> </td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Test/Quality</div> <br> <table> <tbody><tr> <td>&nbsp;</td> <td> Please make sure (i) Component packaging is tested (ii) Unit Tests are developed.</td> </tr><tr> <td align="top"><font color="red">*</font></td> <td valign="top">Location of the unit test files : <br><textarea name="unittestloc" cols="80" rows="5" CRTunittestloc></textarea></td> </tr>  <tr><td colspan="3">&nbsp;</td></tr> <tr> <td><font color="red">*</font></td> <td colspan="2" valign="top">Description of testing performed.</td> </tr> <tr> <td>&nbsp;</td> <td colspan="2"><textarea name="testing" cols="80" rows="5" CRTtesting></textarea></td> </tr> </tbody></table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt">Review/Diffs</div> <br>  <table> <tbody><tr> <td align="top"><font color="red">*</font></td> <td><a href="javascript:popUpWindow(\'https://opensso.dev.java.net/public/improve/codingguidelines/paper.html\')">Coding standards</a> must be followed. </td> </tr> </tbody></table>  <table> <tbody><tr> <td align="top"><font color="red">*</font></td> <td>  All reviewer comments are expected to be completed/addressed. </td> </tr> <tr> <td>&nbsp;</td> <td> If not, why. <br><textarea name="desc_comments_addressed" cols="80" rows="5" CRTdesc_comments_addressed></textarea> </td> </tr> </tbody></table>  <table> <tbody><tr> <td><font color="red">*</font></td> <td> Listing of the files created/deleted/updated. <i>(use \'cvs stat\')</i> </td> </tr> <tr> <td>&nbsp;</td> <td><textarea name="files" cols="80" rows="10" CRTfiles></textarea></td> </tr> </tbody></table>   <p> <table> <tbody><tr> <td><font color="red">*</font></td> <td>Change Reviewer(s):&nbsp;<input size="30" name="codereviewer" type="text" valuel="CRTcodereviewer" value=""> <a href="https://opensso.dev.java.net/nonav/servlets/AssignableUsers" onclick="return launch(this.href, 3, false, document.getElementById(\'CRT\').codereviewer)">Lookup users</a> </td>  </tr> <tr> <td><font color="red">*</font></td> <td>Context specific diffs (or pointer to the diffs) of the added/modified/removed files</td> </tr> <tr> <td>&nbsp;</td> <td><textarea name="diffs" cols="80" rows="10" CRTdiffs></textarea></td> </tr> </tbody></table>  </p><hr color="#cccccc" noshade="noshade" size="1">   <table> <tbody><tr><td><input name="submitC" value="Submit" type="submit" onclick="return setBtn(\'2\');"></td><td><input name="reset" value="Reset" type="reset"></td> </tr> </tbody></table>  </form></div><div id="crttext" style="display:none"> <p><a onClick="hideStuff(\'crttext\');showStuff(\'crtform\');" href="#">Form View</a>&nbsp;|&nbsp;*Text View <br><br><textarea id="crttexttb" cols=80 rows=100>CRTTEXTDATA</textarea><div><div id="sizespan"></div> <hr> <div id="myspan"></div><script> alert("END getUSER"); getUser(true); if (userid == null || userid.length == 0 || userid == \'Login\') alert("You must Login first."); else { setupUser(\'CRT\', \'submitter\'); setupUser(\'approve\', \'userid\');} </script> </body></html>';
var clickedBtn = '0';
var url = "https://opensso.dev.java.net/issues/createattachment.cgi";
var uurl = "https://opensso.dev.java.net";
var issueurl = "https://opensso.dev.java.net/issues/show_bug.cgi?id=";
var noissue = "There does not seem to be an issue numbered";
var nossue1 ="Issue ";
var fixissue1 = "show_bug.cgi?id=";
var fixissue2 = "https://opensso.dev.java.net/issues/show_bug.cgi?id=";
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
alert("IN GETUSER");
    var var1 = document.getElementById('login');
alert("get login element os : "+var1);
    if (var1 != null) {
alert("userid aa"+var1.childNodes[1].innerHTML);
alert("userid bb"+var1.childNodes[1].childNodes[1]);
        userid = var1.childNodes[1].childNodes[1].innerHTML;
alert("userid1111 : "+userid);
    } else {
alert("userid2222 : "+usserid);
        if (usereq) {
alert("userid333 start : "+usserid);
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
alert("userid333 end : "+usserid);
           } catch(e) {
                alert("Problem scraping for user:"+e);
            }
        }
    }
        
}
function isValidIssueId(id, writemsg)
{
    uhttp_request = get_request_handle()
alert("Check..."+id);
    try {
        if (id == null || id.length == 0) {
           if (writemsg)
               alert("invalid issue id");
           return false;
        }
        uhttp_request.open('GET', issueurl+id, false);
        uhttp_request.send(null);
        var result = uhttp_request.responseText;
        var st1 = result.indexOf(noissue);
        if (st1 == -1)
             return true;
        else {
             if (writemsg)
                 alert("Issue does not exist :"+id);
             return false;
        }
    } catch(e) {
        alert("Problem in isValid Issue");
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
crtn['descr'] ='Short description of the problem';
crtn['solution'] ='Short description of the solution';
crtn['apichange'] ='API Changed';
crtn['otherparts'] ='Affects other parts of the product';
crtn['otherpartstested'] ='Were the dependencies rebuilt and tested'
crtn['uichange'] ='UI Changed';
crtn['changeadminconsole'] ='Admin Console Changed';
crtn['changecli'] ='CLI Changed';
crtn['changepolicy'] ='Policy Changed';
crtn['changeidrepo'] ='IDRepo Changed';
crtn['changeauthn'] ='Authentication Changed';
crtn['changefed'] ='Federation Changed';
crtn['changesession'] ='Session Changed';
crtn['changemigration'] ='Migration Changed';
crtn['changeupgrade'] ='Upgrade Changed';
crtn['changelogging'] ='Logging Changed';
crtn['changej2eeagent'] ='J2EE Agent Changed';
crtn['changewebagent'] ='Web Agent Changed';
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
ret = isValidIssueId(myid, false);
alert ("ISVALID RETURNS:"+ret);
        if (!isValidIssueId(myid, false)) {
            alert ("Error : Issue#"+myid+" does not exist.");
            return;
        }
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

function writeResponse( resp)
{
    resp = resp.replace('/@import "/g','@import "https://opensso.dev.java.net');
    resp = resp.replace('UTF-8', 'iso-8859-1');
    resp = resp.replace('UTF-8', 'iso-8859-1');
    resp = resp.replace(fixissue1, fixissue2);
    document.write(resp);
    document.close();
}
function requestdone() {
    if (http_request.readyState == 4) {
        if (http_request.status == 200) {
            result = http_request.responseText;
            writeResponse(result);
        } else {
           alert('There was a problem with the request.');
        }
       //document.getElementById('ajaxbutton').disabled = false;
    }
}
function popUpWindow(url) {
    var bars = 'directories=no,location=0,menubar=0,status=0,titlebar=yes,toolbar=no';
    var options = 'scrollbars=yes,width=800,height=600,resizable=yes';
    var feature = bars + ',' + options;
    //var openwin = window.open(url, 'help', feature);
    var openwin = window.open(url);
    openwin.focus();
}
