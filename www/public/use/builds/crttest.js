// crt.js : All javascript support for the CRT tool.
//
// Actors and Usecase :
// (1) New CRT Flow :
//    Prerequisite : CRT filer logged in and has write access to issue tracker. 
//    - CRT filer loads crt URL (crtfinal.html)
//    - Fills in all mandatory fields - including the issueid.
//    - Submit results in creation of an attachment to the the issue with 
//      a "prefilled" form as the content.
//
// (2) CRT Update :
//    Prerequisite : CRT filer logged in and has write access to issue tracker. 
//    - CRT filer visits issue and clicks on an existing CRT attachment.
//    - Updates the values in the form.
//    - Submit results in creation of a attachment to the the issue with 
//      that is a form with values prefilled for the next update.
//
// (3) CRT Approval
//    Prerequisite : CRT filer logged in and has write access to issue tracker. 
//    - CRT  approver visits issue and clicks on an existing CRT atachment.
//    - Fills in a "Approver" form at the top - approves or diapproves the
//      request.
//    - Submit results in a new attachment to the issue with contents of the
//      approval.
//
// (4) CRT Query :
//     User queries for CRTs.
//
// UCs 1, 2 and 3 are satisfied by this js file and assiciated crtfinal.html.
// UC 4 is covered via Issur tracker query tool and email filtering facilities.
//   
// crttemplate :
// This variable contains a "tagged" version of the CRT html page.
// Tags are replaced when a CRT form is submitted with values contained 
// in the form. This stringified html page is then added as a new 
// attachment to the specified issue.
// Special Note : Any changes to crtfinal.html *must* be reflected here. Also
//   existing and new form components must have the appropriate "CRT" tag.
//   New form component must have an "id" and the crtn array in aggSubmit()
//  function updated.
var ttt = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> <html> <head> <style type="text/css"> /* <![CDATA[ */ @import "https://opensso.dev.java.net/branding/css/tigris.css"; @import "https://opensso.dev.java.net/branding/css/inst.css"; /* ]]> */</style> <link rel="stylesheet" type="text/css" href="/branding/css/print.css" media="print" /> <script src="/branding/scripts/tigris.js" type="text/javascript"></script> <title> opensso:        Create attachment </title> <!-- <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> --> <meta http-equiv="Content-Style-Type" content="text/css" /> <meta name="version" content="3.5.1.19.9" /> </head> <body  class="composite"> <div id="collabnet"> <div id="login"> <div> Logged in: <strong class="username">rajeevangal</strong> |    <a href="https://www.dev.java.net/servlets/Logout">Logout</a>   </div> </div> <a href="http://www.collab.net/special/clickpbc0502.html" id="cnlogo"> <span class="alt">CollabNet Enterprise Edition</span></a> </div> <div class="topline"> <div class="topbar"> <div id="banner"> <table border="0" cellspacing="0" cellpadding="0" width="100%"> <tr> <td> <div><a href="http://www.java.net"><img src="/branding/images/header_jnet_new.jpg" width="500" height="60" alt="The Source for Java Technology Collaboration" /></a></div> </td> <td class="right" style="vertical-align:middle"> <div id="domainlinks"> <div id="loginbox"> <div> Logged in: <strong class="username">rajeevangal</strong> |    <a href="https://www.dev.java.net/servlets/Logout">Logout</a>   </div> </div> </div> </td> </tr> </table> </div> </div> </div> <div id="toptabs"> <table cellpadding="4" cellspacing="0" border="0" class="tabs"><tr> <td>   <a href="https://www.dev.java.net/servlets/StartPage">My pages</a> </td> <th><a href="http://community.java.net/projects/">Projects</a></th> <td><a href="http://community.java.net/">Communities</a></td> <td><a href="http://www.java.net">java.net</a></td> </tr></table> </div> <div id="breadcrumbs"> <a href="https://www.dev.java.net/servlets/ProjectList">Projects</a>     > <a href="https://general.dev.java.net/" >general</a> > <strong>   <a href="https://opensso.dev.java.net/" >opensso</a> </strong>  </div> <table border="0" cellspacing="0" cellpadding="4" width="100%" id="main"> <tr> <td id="navcol"> <dl class="navgroup"> <dt>Get Involved</dt> <dd> <ul> <li><a href="https://java-net.dev.java.net/">java-net Project</a></li> <li><a href="http://www.java.net/request_project.csp">Request a Project</a></li> <li><a href="http://community.java.net/help_wanted/">Project Help Wanted Ads</a></li> <li><a href="http://community.java.net/projects/publicize.csp">Publicize your Project</a></li> <li><a href="http://today.java.net/cs/user/create/bl?x-layout=content">Submit Content</a></li> </ul> </dd> </dl> <dl id="projecttools" class="navgroup"> <dt>Project tools</dt> <dd> <ul> <li>   <a href="https://opensso.dev.java.net/servlets/ProjectHome">Project home</a> </li> <li>   <a href="https://opensso.dev.java.net/servlets/ProjectMemberList">Membership</a> </li> </ul> </dd> <dd> <ul>   <li>   <a href="https://opensso.dev.java.net/servlets/ProjectNewsList">Announcements</a> </li> <li>   <a href="https://opensso.dev.java.net/servlets/ProjectForumView">Discussion forums</a> </li> <li>   <a href="https://opensso.dev.java.net/servlets/ProjectMailingListList">Mailing lists</a> </li> </ul></dd>  <dd> <ul>  <li>   <a href="https://opensso.dev.java.net/servlets/ProjectDocumentList">Documents &amp; files</a> </li> <li><a href="https://opensso.dev.java.net/source/browse/opensso/">Version control - CVS</a> </li> </ul> </dd> <dd><ul><li>   <a href="https://opensso.dev.java.net/servlets/ProjectIssues">Issue tracker</a> </li> </ul> </dd> <dd> </dd> <script id="eraser"> // hide all default navigation bars // get to the block var dd = document.getElementById("eraser").previousSibling; for( n=dd; n!=null; n=n.previousSibling ) { if(n.nodeType==1) { // if element if(n.tagName=="DD") { // kill all the link items n.style.display = "none"; } if(n.tagName=="DT") { // change the title n.innerHTML = "<strong>The OpenSSO Project</strong>"; } } } // delete the "get involved" block for( n=dd.parentNode.previousSibling; n!=null; n=n.previousSibling ) { if(n.nodeType==1) { // if element if(n.tagName=="DL") { n.style.display = "none"; } } } </script> <dd> <ul> <li> <a href="https://opensso.dev.java.net/servlets/ProjectHome">Home</a> </li> </ul> <dd> <ul> <li> <b><a href="https://opensso.dev.java.net/public/about/index.html">Information</a></b> </li> <li> <a href="https://opensso.dev.java.net/public/about/faqcenter/index.html">FAQs</a><br> </li> <li> <a href="https://opensso.dev.java.net/public/about/governance/index.html">Governance</a> </li> <li> <a href="https://opensso.dev.java.net/servlets/ProjectMembershipRequest">Join</a> </li> </ul> <dd> <ul> <li> <b><a href="https://opensso.dev.java.net/public/use/index.html">Downloads</a></b> </li> <li> <a href="http://download.java.net/general/opensso/nightly/">Nightly Builds</a><br> </li> <li> <a href="https://opensso.dev.java.net/public/use/builds/instructions.html">Build Instructions</a> </li> <li> <a href="http://opensso.dev.java.net/public/use/docs/index.html">Documentation</a> </li> </ul> <dd> <ul> <li> <b><a href="https://opensso.dev.java.net/public/improve/index.html">Source Code</a></b> </li> <li> <a href="https://opensso.dev.java.net/source/browse/opensso">Browse CVS</a><br> </li> <li> <a href="https://opensso.dev.java.net/public/improve/codingguidelines/paper.html">Coding Guidelines</a> </li> <li> <a href="https://opensso.dev.java.net/public/about/faqcenter/faqgetstart.html#changes">Commit Procedure</a> </li> </ul> <dd> <ul> <li> <b><a href="https://opensso.dev.java.net/public/improve/discussions.html">Discussions</a></b> </li> <li> <a href="irc://irc.freenode.net/opensso">IRC Channel</a> </li> <li> <a href="https://opensso.dev.java.net/servlets/ProjectMailingListList">Mailing Lists</a> </li> <li> <a href="http://wiki.java.net/bin/view/Projects/OpenSSO">Wiki</a> </li> <li> <a href="https://opensso.dev.java.net/servlets/ProjectForumView">Forums</a> </li> </ul> <dd> <ul>    <li> <b><a href="https://opensso.dev.java.net/servlets/ProjectIssues">Issue Tracker</a></b><br> </li> <li> <a href="https://opensso.dev.java.net/public/about/faqcenter/faqbugs.html">Reporting Issues</a> </li> <li> <a href="https://www.dev.java.net/nonav/scdocs/issuewritinghelp.html">Writing Issues</a><br> </li> <li> <form method="get" action="https://opensso.dev.java.net/issues/show_bug.cgi"> <input type="submit" value="Go to"/>Issue #<input name="id" size="3"/> </form> </li> </ul> <dd> <ul> <li> <b>Subprojects</b> </li> <li> <b><a href="https://lightbulb.dev.java.net/">lightbulb</a></b> </li> </ul> <!-- The following code was generated by statcounter.com to track usage statistics on the opensso web site. If you are modifying this page for your own purposes please REMOVE this code. --> <!-- Start of StatCounter Code --> <script type="text/javascript" language="javascript"> <!-- var sc_project=1783597; var sc_invisible=1; var sc_partition=16; var sc_security="814713cc"; //--> </script> <script type="text/javascript" language="javascript" src="http://www.statcounter.com/counter/counter.js"></script><noscript><a href="http://www.statcounter.com/" target="_blank"><img  src="http://c17.statcounter.com/counter.php?sc_project=1783597&amp;amp;java=0&amp;amp;security=814713cc&amp;amp;invisible=1" alt="web statistics" border="0"></a> </noscript> <!-- End of StatCounter Code --> <!-- The following code was generated by googleanalytics.com to track usage statistics on the opensso web site --> <script src="https://ssl.google-analytics.com/urchin.js" type="text/javascript"> </script> <script type="text/javascript"> _uacct = "UA-606541-1"; urchinTracker(); </script> <!-- End of google analytics code --> </dd> </dl> <form action="https://opensso.dev.java.net/servlets/Search" method="get"> <dl id="searchbox" class="navgroup"> <dt>Search</dt> <dd> <div> <select name="scope"> <option value="project" selected="selected">This project</option> <option value="myprojects">My projects</option> <option value="domain" >All projects</option> </select> </div> <div> <input type="hidden" name="resultsPerPage" value="40" /> <input type="text" name="query" size="10" /> <input type="submit" name="Button" value="Go" /> </div> <div> <a href="https://opensso.dev.java.net/servlets/Search?mode=advanced">Advanced search</a>    </div> </dd> </dl> </form> <dl id="helptext" class="navgroup"> <dt>How do I...</dt> <dd> <ul> <li>  <a href="https://www.dev.java.net/scdocs/ddIssues_Query" onclick="return launch(this.href, 1)" title="Note: link may open in new window" class="helplink"> Query the issue database?</a> </li> <li>  <a href="https://www.dev.java.net/scdocs/ddIssues_Reports" onclick="return launch(this.href, 1)" title="Note: link may open in new window" class="helplink"> Get a report of issues in the database?</a> </li> <li>  <a href="https://www.dev.java.net/scdocs/ddIssues_EnterModify#vote" onclick="return launch(this.href, 1)" title="Note: link may open in new window" class="helplink"> Learn about voting for issues?</a> </li> <li>  <a href="https://www.dev.java.net/scdocs/ddIssues_EnterModify" onclick="return launch(this.href, 1)" title="Note: link may open in new window" class="helplink"> Enter an issue?</a> </li> <li> <a href="http://www.collab.net/rn/" onclick="return launch(this.href, 1)" title="Note: link may open in new window" class="helplink"> Get release notes for CollabNet 3.5.1?</a> </li> <li><a href="https://opensso.dev.java.net/nonav/servlets/HelpTOC" onclick="return launch(this.href, 1)" title="Note: link may open in new window" class="helplink">Get help?</a></li> </ul> </dd> </dl> <div class="strut">&nbsp;</div> </td> <td id="midcol"> <div id="topmodule"> <table> <tr> <td id="issueid"> <form method="get" action="https://opensso.dev.java.net/issues/show_bug.cgi"> <div> <input name="id" size="6" tabindex="1" /> <input type="submit" value="Jump to issue" tabindex="2" /> </div> </form> </td> </tr> </table> </div> <div id="bodycol"> <div id="apphead"> <h1><small>opensso</small><br />         Create attachment </h1> </div> <p class="tasknav"> <a href="enter_bug.cgi">New</a> | <a href="query.cgi">Query</a> | <a href="reports.cgi">Reports</a> | <a href="showvotes.cgi">My votes</a> | <a href="buglist.cgi?cmdtype=runuserdefault">My issues</a> <br /> Edit <a href="userprefs.cgi">Prefs</a> </p> <!--BEGINNING OF APP CLASS--> <div id="issuezilla" class="application"> <!--BEGINNING OF H2 CLASS--> <div class="h2"> <h2>Create attachment: Issue 38</h2> <table><tr><td>Attachment to issue 38 created (attachment <a title="CRT_submittal:38" href="https://opensso.dev.java.net/nonav/issues/showattachment.cgi/120/CRT_submittal:38">120</a>)<div> <strong>Email sent to:</strong> issues@opensso, rajeevangal, rmisra </div></td><td><a href="https://opensso.dev.java.net/issues/show_bug.cgi?id=38">Go back to ISSUE# 38</a></td></tr></table> <p><a href="createattachment.cgi?id=38">Attach a file to this issue</a></p> <!--ENDING OF CLASS H2--> </div> <p>Queries: <a href=\'buglist.cgi?cmdtype=runuserdefault\'>My issues</a></p><!--ENDING OF CLASS APP--> </div> </div> </td> </tr> </table> <div id="footer"> <p><a href="http://today.java.net/pub/q/java_net_rss"><img src="/branding/images/xml.gif" width="36" height="14" alt="XML" style="vertical-align:middle"/></a>&nbsp;<b>java.net RSS</b></p> <table border="0" cellspacing="0" cellpadding="4" width="100%"> <tr> <td><a href="http://www.collab.net/special/clickpbc0502.html" onclick="return launch(this.href, 1)"><img src="/branding/images/poweredby_036.gif" alt="This site is Powered by CollabNet." id="poweredby" width="102" height="38" /></a></td> <td><a href="http://java-net.dev.java.net/feedback.html"  onclick="return launch(this.href, 1)" title="Note: link may open in new window" class="helplink">Feedback</a>   | <a href="http://java.net/faq.csp">FAQ</a> | <a href="http://java.net/presscenter/">Press</a> | <a href="http://www.collab.net/developers/tools/" onclick="return launch(this.href, 1)" title="Note: link may open in new window">Developer tools</a> 	   <div> <a href="http://java.net/terms.csp">Terms of Use</a> | <a href="http://www.sun.com/privacy">Privacy</a> | <a href="http://www.sun.com/suntrademarks/">Trademarks</a> | <a href="https://java-net.dev.java.net/sitemap.html">Site Map</a> </div> <p>&#169; 1995 - 2006 CollabNet. CollabNet is a registered trademark of CollabNet, Inc.</p> </td> <td><a href="http://www.sun.com" onclick="return launch(this.href, 1)"><img src="/branding/images/logo_sun_small.gif" id="logosun" width="61" height="29" alt="Sun" /></a> <a href="http://www.oreilly.com" onclick="return launch(this.href, 1)"><img src="/branding/images/montague_logo_oreilly.gif" id="logooreilly" width="74" height="19" alt="Oreilly" /></a></td> </tr></table> <!-- Begin SiteCatalyst code--> <script language="JavaScript" src="https://www.dev.java.net/branding/images/s_code_remote.txt "></script> <!-- End SiteCatalyst code --> </body> </html> <img name="s_i_sunjnetdev" height=1 width=1 border=0 alt="">';
var crttemplate='<!DOCTYPE html PUBLIC "-//w3c//dtd html 4.0 transitional//en"> <html> <head> <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"> <META HTTP-EQUIV="Pragma" CONTENT="no-cache"> <META HTTP-EQUIV="Expires" CONTENT="-1"> <title>OpenSSO CRT</title> <script src="https://opensso.dev.java.net/public/use/builds/crttest.js" type="text/javascript"></script> </head> <body bgcolor="#ffffff" text="#000000"> <table cellpadding="2" width="100%"> <tbody> <tr> <td align="center" valign="top" width="99%"> <h2>OpenSSO Change/Patch Request Template (v1.0)</h2> </td> </tr> </tbody> </table> <hr color="#cccccc" noshade="noshade" size="1"> <div CRTApproveForm style="display:none"> <fieldset> <legend>Approval</legend> <form name="approve" id="approve" onSubmit="approveSubmit();return false;"><a href="https://opensso.dev.java.net/nonav/servlets/AssignableUsers" onclick="return launch(this.href, 3, false, document.getElementById(\'approve\').userid)">UserId:</a><input name="userid" type="text">Check if Approved:<input name="approval" type="checkbox">Brief comment :<input name="comment" type="text"><input name="Submit" type="Submit" value="Submit" > </form> </fieldset> <p><a>*Form View</a>&nbsp;|&nbsp; <a onClick="showStuff(\'crttext\');hideStuff(\'crtform\');" href="#">Text View</a> </div> <div id="crtform" style="display:inline"> <form name="CRT" id="CRT" onSubmit="aggSubmit();return false;" > <div class="secHdrTxt"><b>General</b></div> <br>  <table> <tbody> <tr> <td valign="top"><font color="red">*</font></td><td>Submitter :</td> <td><input size="17" name="submitter" type="text" valuel="CRTsubmitter" value=""></td> </tr> <tr> <td valign="top"><font color="red">*</font></td> <td>Issue number:</td> <td><input size="17" name="id" type="text" valuel="CRTid" value="">&nbsp;<a href="#" onClick="isValidIssueId(getElementById(\'CRT\').id.value, true)">Check</a></td> </tr> <tr> <td valign="top"><font color="red">*</font></td> <td colspan="2" valign="top">Short description of the problem :</td> </tr> <tr> <td>&nbsp;</td> <td colspan="2"><textarea name="descr" cols="80" rows="5" CRTdescr></textarea></td> </tr> <tr> <td style="vertical-align: top; font-weight: bold;"><font color="red">*</font></td> <td style="vertical-align: top;" colspan="2">Solution:<br></td> </tr> <tr> <td style="vertical-align: top;"><br></td> <td style="vertical-align: top;" colspan="2"><textarea name="solution" cols="80" rows="5" CRTsolution></textarea> <br></td> </tr> </tbody> </table> <table> <tbody> <tr> <td style="vertical-align: top;"><span style="color: rgb(255, 0, 0); font-weight: bold;">*</span><br></td><td style="vertical-align: top;" colspan="2">Select all areas that have been changed or will be affected:<br></td> </tr> <tr> <td style="vertical-align: top;"><br></td> <td style="vertical-align: top;" colspan="1"><input name="changeadminconsole" type="checkbox" CRTchangeadminconsole> Admin Console<br></td> <td style="vertical-align: top;"><input name="changecli" type="checkbox" CRTchangecli> CLI<br></td> <td style="vertical-align: top;"><input name="changepolicy" type="checkbox" CRTchangepolicy> Policy<br></td><td style="vertical-align: top;"><input name="changeidrepo" type="checkbox" CRTchangeidrepo> ID Repo<br></td> </tr> <tr> <td style="vertical-align: top;"><br></td> <td style="vertical-align: top;"><input name="changeauthn" type="checkbox" CRTchangeauthn> Authentication<br></td> <td style="vertical-align: top;"><input name="changefed" type="checkbox" CRTchangefed> Federation<br></td> <td style="vertical-align: top;"><input name="changeinstall" type="checkbox" CRTchangeinstall> Installation<br></td> <td style="vertical-align: top;"><input name="changesession" type="checkbox" CRTchangesession> Session<br></td> </tr> <tr> <td style="vertical-align: top;"><br></td> <td style="vertical-align: top;"><input name="changemigration" type="checkbox" CRTchangemigration> Migration<br></td> <td style="vertical-align: top;"><input name="changeupgrade" type="checkbox" CRTchangeupgrade> Upgrade<br></td> <td style="vertical-align: top;"><input name="changelogging" type="checkbox" CRTchangelogging> Logging<br></td> <td style="vertical-align: top;"><input name="changej2eeagent" type="checkbox" CRTchangej2eeagent> J2EE Agent<br> </td> </tr> <tr> <td style="vertical-align: top;"><br> </td> <td style="vertical-align: top;"><input name="changewebagent" type="checkbox" CRTchangewebagent> Web Agent<br> </td> </tr> </tbody> </table>  <p></p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt"><b>Documentation</b></div> <br> Any changes involving the user interface (command line or browser), programmatic interfaces, configuration files, or new components will typically require some documentation changes. <br> <br> Documentation update instructions:<br> <div style="margin-left: 40px;">1) Select the appropriate Wiki to add your documentation update.<br> 2) Place the anchor tag to your update in the text field. <br> 3) Select the View link to verify your tag is correct.<br> <br>  </div> <table> <tbody> <tr> <td valign="top"><font color="red">*</font></td> <td>User documentation :<br>(Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/CommunityDocumentation\')">Wiki</a> pointer)</td> <td><input name="userdocs" type="text" size="50" valuel="CRTuserdocs" value="">&nbsp;<a href="javascript:popUpWindow(document.getElementById(\'CRT\').userdocs.value)">View</a> </td> </tr> <tr> <td valign="top"><font color="red">*</font></td> <td>Diagnostics/Troubleshooting documentation :<br>(Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/TroubleshootingDebugging\')">Wiki</a> pointer)</td> <td><input name="tddocs" type="text" valuel="CRTtddocs" value="" size="50"> &nbsp;<a href="javascript:popUpWindow(document.getElementById(\'CRT\').tddocs.value)">View</a> </td> </tr> <tr> <td valign="top"><font color="red"></font></td> <td>Online Help Changes :<br>(Please provide <a href="javascript:popUpWindow(\'http://wiki.java.net/bin/view/Projects/OnlineHelp\')">Wiki</a> pointer)</td> <td><input name="online" type="text" valuel="CRTonline" value="" size="50"> &nbsp;<a href="javascript:popUpWindow(document.getElementById(\'CRT\').online.value)">View</a> </td> </tr> </tbody> </table> <blockquote> <font size="-1"> ** Diagnostic and troubleshooting documentation should include detection of misconfigurations and tips on correcting them.<br> <br> </font> </blockquote> <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt"><b>Migration/Upgrading</b></div> <br> <table> <tbody> <tr> <td align="top"><font color="red"></font></td> <td> If this change affects migration/upgrading from previous release <font color="#6666cc"><a href="javascript:popUpWindow(\'https://opensso.dev.java.net/public/use/builds/migration_help.html\')"> <i>(more information...)</i> </a></font>, please describe : <br><textarea name="desc_migration_updating" cols="80" rows="5" CRTdesc_migration_updating></textarea> </td> </tr> </tbody> </table>  <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt"><b>Installer</b></div> <br> <table> <tbody> <tr> <td align="top"><font color="red"></font></td> <td>  If this change affects the installer <font color="#6666cc"><a href="javascript:popUpWindow(\'https://opensso.dev.java.net/public/use/builds/installer_help.html\')"> <i>(more information...)</i> </a></font>, please describe : <br><textarea name="desc_installer_change" cols="80" rows="5" CRTdesc_installer_change></textarea> </td> </tr> </tbody> </table> <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt"><b>Test/Quality</b></div> <br> <table> <tbody> <tr> <td>&nbsp;</td> <td> Please make sure (i) Component packaging is tested (ii) Unit Tests are developed.</td> </tr> <tr> <td align="top"><font color="red">*</font></td> <td valign="top">Location of the unit test files : <br><textarea name="unittestloc" cols="80" rows="5" CRTunittestloc></textarea></td> </tr> <tr> <td colspan="3">&nbsp;</td> </tr> <tr> <td><font color="red">*</font></td> <td colspan="2" valign="top">Description of testing performed.</td> </tr> <tr> <td>&nbsp;</td> <td colspan="2"><textarea name="testing" cols="80" rows="5" CRTtesting></textarea></td> </tr> </tbody> </table> <p> </p><hr color="#cccccc" noshade="noshade" size="1"> <div class="secHdrTxt"><b>Review/Diffs</b></div> <br>  <table> <tbody> <tr> <td align="top"><font color="red">*</font></td> <td><a href="javascript:popUpWindow(\'https://opensso.dev.java.net/public/improve/codingguidelines/paper.html\')">Coding standards</a> must be followed. </td> </tr> </tbody> </table>  <table> <tbody> <tr> <td align="top"><font color="red">*</font></td> <td>  All reviewer comments are expected to be completed/addressed. </td> </tr> <tr> <td>&nbsp;</td> <td> If not, why. <br><textarea name="desc_comments_addressed" cols="80" rows="5" CRTdesc_comments_addressed></textarea> </td> </tr> </tbody> </table>  <table> <tbody> <tr> <td><font color="red">*</font></td> <td> Listing of the files created/deleted/updated. <i>(use \'cvs stat\')</i> </td> </tr> <tr> <td>&nbsp;</td> <td><textarea name="files" cols="80" rows="10" CRTfiles></textarea></td> </tr> </tbody> </table>   <p> <table> <tbody> <tr> <td><font color="red">*</font></td> <td>Change Reviewer(s):&nbsp;<input size="30" name="codereviewer" type="text" valuel="CRTcodereviewer" value=""> <a href="https://opensso.dev.java.net/nonav/servlets/AssignableUsers" onclick="return launch(this.href, 3, false, document.getElementById(\'CRT\').codereviewer)">Lookup users</a> </td> </tr> <tr> <td><font color="red">*</font></td> <td>Context specific diffs (or pointer to the diffs) of the added/modified/removed files</td> </tr> <tr> <td>&nbsp;</td> <td><textarea name="diffs" cols="80" rows="10" CRTdiffs></textarea></td> </tr> </tbody> </table>  </p><hr color="#cccccc" noshade="noshade" size="1">   <table> <tbody> <tr> <td><input name="submitC" value="Submit" type="submit" onclick="return setBtn(\'2\');"></td> <td><input name="reset" value="Reset" type="reset"></td> </tr> </tbody> </table>  </form> </div> <div id="crttext" style="display:none"> <p><a onClick="hideStuff(\'crttext\');showStuff(\'crtform\');" href="#">Form View</a>&nbsp;|&nbsp;*Text View <br><br><textarea id="crttexttb" cols=80 rows=100>CRTTEXTDATA</textarea> <div id="sizespan"></div> <hr> <div id="myspan"> </div> <script> getUser(true); if (userid == null || userid.length == 0 || userid == \'Login\') alert("You must Login first."); else { setupUser(\'CRT\', \'submitter\'); setupUser(\'approve\', \'userid\');} </script> </body> </html>';
var clickedBtn = '0';
var url = "https://opensso.dev.java.net/issues/createattachment.cgi";
var uurl = "https://opensso.dev.java.net";
var issueurl = "https://opensso.dev.java.net/issues/show_bug.cgi?id=";
var noissue = "There does not seem to be an issue numbered";
var nossue1 ="Issue ";
var fixissue1 = "show_bug.cgi?id=";
var fixissue2 = "https://opensso.dev.java.net/issues/show_bug.cgi?id=";
var uidstr = "Logged in: <STRONG class=username>";
var mydata;
var myid;
var mydescription;
var mytype;
var myothertype;
var userid = "Login";

// "static" javascript :
// Load java.net supplied stylesheet and javascript if not already loaded.
// This script depends on javascipt provided by java.net for "search
// for users" functionality

if (typeof(launch) == "undefined")
{
  document.write('<link rel="stylesheet" href="https://opensso.dev.java.net/branding/css/tigris.css">');
  document.write( "\<SCRIPT SRC='https://opensso.dev.java.net/branding/scripts/tigris.js'\>\</SCRIPT\>" );
}

// Controls visibility of components
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

// userid is obtained by screenscraping.
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
        var str = var1.innerHTML;
        //userid = var1.childNodes[1].childNodes[1].innerHTML;
        var st1 = str.indexOf('class="username"');
        if (st1 == -1) { // IE
           st1 = str.indexOf('class=username');
           if (st1 == -1) {
              return;
           }
        }
        var st2 = str.indexOf(">", st1);
        var st3 = str.indexOf("<", st2);
        userid = str.substring(st2+1,st3);
    } else {
        if (usereq) {
           uhttp_request = get_request_handle()
           try {
	      uhttp_request.open('GET', uurl, false);
	      uhttp_request.send(null);
              var result = uhttp_request.responseText;
              var st1 = result.indexOf('class="username"');
              if (st1 == -1) {
                 st1 = result.indexOf('class=username');
                 if (st1 == -1)
                     return;
              }
              var st2 = result.indexOf(">", st1);
              var st3 = result.indexOf("<", st2);
              userid = result.substring(st2+1,st3);
           } catch(e) {
                alert("Problem scraping for user:"+e);
            }
        }
    }
        
}

// Validates issueid.
function isValidIssueId(id, writemsg)
{
    uhttp_request = get_request_handle()
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

// Approve form processing.
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


// Aggregates all values provided into a single CRT.
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
    // 2 pass iteration of form elements for ascii CRT generation.
    //  - once for non-textarea elements (top) and later for textarea 
    // elements (bottom).
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
   // Current implementation uses a single submit button. Retaining
   // the if for future inclution of other buttons.

   if (clickedBtn == '2' ) {
        var approveFormStrFrom = 'CRTApproveForm style="display:none"';
        var approveFormStrTo = 'CRTApproveForm style="display:inline"';
        crtt = crtt.replace(approveFormStrFrom, approveFormStrTo);
	document.getElementById('crttexttb').value=escapeHTML(data);
        crtt = crtt.replace('CRTTEXTDATA', escapeHTML(data));
	mydata = crtt;
	myid = crtFrm.elements['id'].value;
        if (!isValidIssueId(myid, false)) {
            alert ("Error : Issue#"+myid+" does not exist.");
            return;
        }
	mydescription = "CRT_submittal:"+myid;
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


// This function is not used currently.
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

// Returns a XMLHttpRequest object based on browser kind.
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

// Post-processes the html returned from teh AJAX call. Relative paths
// are replaced with absolute paths.
function writeResponse( resp)
{
    //resp = resp.replace('/@import "/g','@import "https://opensso.dev.java.net');
    resp = resp.replace('@import "/','@import "https://opensso.dev.java.net/');
    resp = resp.replace('@import "/','@import "https://opensso.dev.java.net/');
    resp = resp.replace('UTF-8', 'iso-8859-1');
    //resp = resp.replace('UTF-8', 'iso-8859-1');
    resp = resp.replace(fixissue1, fixissue2);
alert("WR : "+resp);
    //document.write(resp);
    document.write(ttt);
    //document.write("<html><body> HELLO </body></html>");
    document.close();
}

// Callback for async ajax requests.
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

// Popup window processing for help and other windows.
function popUpWindow(url) {
    var bars = 'directories=no,location=0,menubar=0,status=0,titlebar=yes,toolbar=no';
    var options = 'scrollbars=yes,width=800,height=600,resizable=yes';
    var feature = bars + ',' + options;
    //var openwin = window.open(url, 'help', feature);
    var openwin = window.open(url);
    openwin.focus();
}
