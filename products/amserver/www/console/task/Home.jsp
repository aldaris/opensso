<%--
   The contents of this file are subject to the terms
   of the Common Development and Distribution License
   (the License). You may not use this file except in
   compliance with the License.

   You can obtain a copy of the License at
   https://opensso.dev.java.net/public/CDDLv1.0.html or
   opensso/legal/CDDLv1.0.txt
   See the License for the specific language governing
   permission and limitations under the License.

   When distributing Covered Code, include this CDDL
   Header Notice in each file and include the License file
   at opensso/legal/CDDLv1.0.txt.
   If applicable, add the following below the CDDL Header,
   with the fields enclosed by brackets [] replaced by
   your own identifying information:
   "Portions Copyrighted [year] [name of copyright owner]"

   $Id: Home.jsp,v 1.3 2008-01-31 04:08:06 veiming Exp $

   Copyright 2008 Sun Microsystems Inc. All Rights Reserved
--%>



<%@ page info="Home" language="java" %>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato" %>
<%@taglib uri="/WEB-INF/cc.tld" prefix="cc" %>
<jato:useViewBean
    className="com.sun.identity.console.task.HomeViewBean"
    fireChildDisplayEvents="true" >

<cc:i18nbundle baseName="amConsole" id="amConsole"
    locale="<%=((com.sun.identity.console.base.AMViewBeanBase)viewBean).getUserLocale()%>"/>

<cc:header name="hdrCommon" pageTitle="webconsole.title" bundleID="amConsole" copyrightYear="2008" fireDisplayEvents="true">

<link id="styleSheet" href="../console/css/commontask.css" type="text/css" rel="stylesheet" />
<link id="styleSheet" href="../console/css/css_master.css" type="text/css" rel="stylesheet" />
<script language="javascript" src="../com_sun_web_ui/js/browserVersion.js"></script>
<script language="javascript" src="../console/js/am.js"></script>
<script language="javascript" src="../console/js/tasksPage.js"></script>

<script language="javascript">
    function confirmLogout() {
        return confirm("<cc:text name="txtLogout" defaultValue="masthead.logoutMessage" bundleID="amConsole"/>");
    }
</script>
<cc:primarymasthead name="mhCommon" bundleID="amConsole"  logoutOnClick="return confirmLogout();"/>
<cc:tabs name="tabCommon" bundleID="amConsole" submitFormData="false" />

<div id="info1" onclick="showDiv(); event.cancelBubble = true;" class="TskPgeInfPnl">
<div><a href="#" id="close1" onclick="closeAll(1); event.cancelBubble = true;return false;"><img alt="" src="../console/images/tasks/close.gif" border="0" /></a></div><p><span class="TskPgeHdr"><cc:text name="txtHelpCreateHostedIDP" defaultValue="commontask.create.hosted.idp" bundleID="amConsole" /></span></p></div>
<div id="info2" onclick="showDiv(); event.cancelBubble = true;" class="TskPgeInfPnl">
<div><a href="#" id="close2" onclick="closeAll(2); event.cancelBubble = true;return false;"><img alt="" src="../console/images/tasks/close.gif" border="0" /></a></div><p><span class="TskPgeHdr"><cc:text name="txtHelpCreateHostedSP" defaultValue="commontask.create.hosted.sp" bundleID="amConsole" /></span></p></div>

<div id="info3" onclick="showDiv(); event.cancelBubble = true;" class="TskPgeInfPnl">
<div><a href="#" id="close3" onclick="closeAll(3); event.cancelBubble = true;return false;"><img alt="" src="../console/images/tasks/close.gif" border="0" /></a></div><p><span class="TskPgeHdr"><cc:text name="txtHelpCreateRemoteIDP" defaultValue="commontask.create.remote.idp" bundleID="amConsole" /></span></p></div>
<div id="info4" onclick="showDiv(); event.cancelBubble = true;" class="TskPgeInfPnl">
<div><a href="#" id="close4" onclick="closeAll(4); event.cancelBubble = true;return false;"><img alt="" src="../console/images/tasks/close.gif" border="0" /></a></div><p><span class="TskPgeHdr"><cc:text name="txtHelpCreateRemoteSP" defaultValue="commontask.create.remote.sp" bundleID="amConsole" /></span></p></div>

<div id="info11" onclick="showDiv(); event.cancelBubble = true;" class="TskPgeInfPnl">
<div><a href="#" id="close11" onclick="closeAll(11); event.cancelBubble = true;return false;"><img alt="" src="../console/images/tasks/close.gif" border="0" /></a></div><p><span class="TskPgeHdr"><cc:text name="txtHelpDoc" defaultValue="commontask.doc" bundleID="amConsole" /></span></p></div>

<div class="TskPgeFllPge" id="TskPge" onclick="hideAllMenus()">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top"><td>&nbsp;</td><td colspan="4"></td></tr>
  <tr>
    <td>&nbsp;</td>
    <td width="40%"><img alt="" src="../console/images/tasks/spacer.gif" width="220" height="1" /></td>
    <td width="9%">&nbsp;</td>
    <td width="39%"><img alt="" src="../console/images/tasks/spacer.gif" width="220" height="1" /></td>
    <td width="7%">&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td valign="top">

    <span class="TskPgeSbHdr">SAMLv2</span>

    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TskPgeBtmSpc">
    <tr><td class="TskPgeBckgrTd">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
        <td width="2%" valign="bottom" class="TskPgeTskLftTd"><img alt="" id="gif1" src="../console/images/tasks/spacer.gif" width="12" height="8" /></td>
        <td width="100%;" class="TskPgeTskCntrTd"><a href="CreateHostedIDP" class="TskPgeTxtBg" onmouseover="this.className='TskPgeTxtBgOvr'" onfocus="this.className='TskPgeTxtBgOvr'" onmouseout="this.className='TskPgeTxtBg'" onblur="this.className='TskPgeTxtBg'">
         <span class="TskPgeTskLftBtm"></span><span class="TskPgeTskLftTp"></span><span class="TskPgeTskRghtBtm"></span><span class="TskPgeTskRghtTp"></span>  
         <span class="TskPgeTskRghtBrdr"></span><span class="TskPgeTskPdng"><cc:text name="txtCreateHostedIDP" defaultValue="commontask.label.create.hosted.idp" bundleID="amConsole" /></span>         
         </a></td>
         <td width="3%" class="TskPgeTskRghtTd" align="right" valign="top">
         <a href="#" onclick="test(1); event.cancelBubble = true; return false;"  onmouseover="hoverImg(1); event.cancelBubble = true;" onmouseout="outImg(1); event.cancelBubble = true;" onfocus="hoverImg(1); event.cancelBubble = true;" onblur="outImg(1); event.cancelBubble = true;" id="i1"><img alt="" src="../console/images/tasks/rightToggle.gif" width="29" height="21" border="0" id="togImg1" /></a></td>
          </tr>
          </table>
    </td></tr>

    <tr><td class="TskPgeBckgrTd">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
        <td width="2%" valign="bottom" class="TskPgeTskLftTd"><img alt="" id="gif1" src="../console/images/tasks/spacer.gif" width="12" height="8" /></td>
        <td width="100%;" class="TskPgeTskCntrTd"><a href="CreateHostedSP" class="TskPgeTxtBg" onmouseover="this.className='TskPgeTxtBgOvr'" onfocus="this.className='TskPgeTxtBgOvr'" onmouseout="this.className='TskPgeTxtBg'" onblur="this.className='TskPgeTxtBg'">
         <span class="TskPgeTskLftBtm"></span><span class="TskPgeTskLftTp"></span><span class="TskPgeTskRghtBtm"></span><span class="TskPgeTskRghtTp"></span>
         <span class="TskPgeTskRghtBrdr"></span><span class="TskPgeTskPdng"><cc:text name="txtCreateHostedSP" defaultValue="commontask.label.create.hosted.sp" bundleID="amConsole" /></span>
         </a></td>
         <td width="3%" class="TskPgeTskRghtTd" align="right" valign="top">
         <a href="#" onclick="test(2); event.cancelBubble = true; return false;"  onmouseover="hoverImg(2); event.cancelBubble = true;" onmouseout="outImg(2); event.cancelBubble = true;" onfocus="hoverImg(2); event.cancelBubble = true;" onblur="outImg(2); event.cancelBubble = true;" id="i1"><img alt="" src="../console/images/tasks/rightToggle.gif" width="29" height="21" border="0" id="togImg2" /></a></td>
          </tr>
          </table>
    </td></tr>

    <tr><td class="TskPgeBckgrTd">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
        <td width="2%" valign="bottom" class="TskPgeTskLftTd"><img alt="" id="gif1" src="../console/images/tasks/spacer.gif" width="12" height="8" /></td>
        <td width="100%;" class="TskPgeTskCntrTd"><a href="CreateRemoteIDP" class="TskPgeTxtBg" onmouseover="this.className='TskPgeTxtBgOvr'" onfocus="this.className='TskPgeTxtBgOvr'" onmouseout="this.className='TskPgeTxtBg'" onblur="this.className='TskPgeTxtBg'">
         <span class="TskPgeTskLftBtm"></span><span class="TskPgeTskLftTp"></span><span class="TskPgeTskRghtBtm"></span><span class="TskPgeTskRghtTp"></span>
         <span class="TskPgeTskRghtBrdr"></span><span class="TskPgeTskPdng"><cc:text name="txtCreateRemoteIDP" defaultValue="commontask.label.create.remote.idp" bundleID="amConsole" /></span>
         </a></td>
         <td width="3%" class="TskPgeTskRghtTd" align="right" valign="top">
         <a href="#" onclick="test(3); event.cancelBubble = true; return false;"  onmouseover="hoverImg(3); event.cancelBubble = true;" onmouseout="outImg(3); event.cancelBubble = true;" onfocus="hoverImg(3); event.cancelBubble = true;" onblur="outImg(3); event.cancelBubble = true;" id="i1"><img alt="" src="../console/images/tasks/rightToggle.gif" width="29" height="21" border="0" id="togImg3" /></a></td>
          </tr>
          </table>
    </td></tr>

    <tr><td class="TskPgeBckgrTd">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
        <td width="2%" valign="bottom" class="TskPgeTskLftTd"><img alt="" id="gif1" src="../console/images/tasks/spacer.gif" width="12" height="8" /></td>
        <td width="100%;" class="TskPgeTskCntrTd"><a href="CreateRemoteSP" class="TskPgeTxtBg" onmouseover="this.className='TskPgeTxtBgOvr'" onfocus="this.className='TskPgeTxtBgOvr'" onmouseout="this.className='TskPgeTxtBg'" onblur="this.className='TskPgeTxtBg'">
         <span class="TskPgeTskLftBtm"></span><span class="TskPgeTskLftTp"></span><span class="TskPgeTskRghtBtm"></span><span class="TskPgeTskRghtTp"></span>
         <span class="TskPgeTskRghtBrdr"></span><span class="TskPgeTskPdng"><cc:text name="txtCreateRemoteSP" defaultValue="commontask.label.create.remote.sp" bundleID="amConsole" /></span>
         </a></td>
         <td width="3%" class="TskPgeTskRghtTd" align="right" valign="top">
         <a href="#" onclick="test(4); event.cancelBubble = true; return false;"  onmouseover="hoverImg(4); event.cancelBubble = true;" onmouseout="outImg(4); event.cancelBubble = true;" onfocus="hoverImg(4); event.cancelBubble = true;" onblur="outImg(4); event.cancelBubble = true;" id="i1"><img alt="" src="../console/images/tasks/rightToggle.gif" width="29" height="21" border="0" id="togImg4" /></a></td>
          </tr>
          </table>
    </td></tr>

    </table>


    <td>&nbsp;</td>
    <td valign="top">
    <span class="TskPgeSbHdr">Documentation</span>
    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TskPgeBtmSpc">
    <tr><td class="TskPgeBckgrTd">
        <table id="button2" width="100%"  border="0" cellspacing="0" cellpadding="0">
        <tr>
        <td width="2%" valign="bottom" class="TskPgeTskLftTd"><img alt="" id="gif11" src="../console/images/tasks/spacer.gif" width="12" height="8" /></td>
        <td width="100%" class="TskPgeTskCntrTd"><a href="http://docs.sun.com/app/docs/coll/1292.1?l=en&q=Access+Manager+7" class="TskPgeTxtBg" onmouseover="this.className='TskPgeTxtBgOvr'" onfocus="this.className='TskPgeTxtBgOvr'" onmouseout="this.className='TskPgeTxtBg'" onblur="this.className='TskPgeTxtBg'">
        <span class="TskPgeTskLftBtm"></span><span class="TskPgeTskLftTp"></span><span class="TskPgeTskRghtBtm"></span><span class="TskPgeTskRghtTp"></span>
        <span class="TskPgeTskRghtBrdr"></span><span class="TskPgeTskPdng"><cc:text name="txtLblDoc" defaultValue="commontask.label.doc" bundleID="amConsole"/></span></a></td>
        <td width="3%" align="right" valign="top" class="TskPgeTskRghtTd" >
        <a href="#" onclick="test(11); event.cancelBubble = true; return false;" onmouseover="hoverImg(11); event.cancelBubble = true;" onmouseout="outImg(11); event.cancelBubble = true;" onfocus="hoverImg(11); event.cancelBubble = true;" onblur="outImg(11); event.cancelBubble = true;" id="i11"><img alt="" id="togImg11" src="../console/images/tasks/rightToggle.gif" width="29" height="21"  border="0" /></a></td>
        </tr>
        </table></td>
      </tr>
    </table>       

    <td>&nbsp;</td>
  </tr>  
  <tr class="TskPgeBtmTr">
    <td colspan="5">&nbsp;</td>
  </tr>
</table>
</div>



</cc:header>
</jato:useViewBean>
