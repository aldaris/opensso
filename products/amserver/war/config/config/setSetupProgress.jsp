<html>
<head>
<link rel="stylesheet" type="text/css" href="../com_sun_web_ui/css/css_ns6up.css">
<script language="Javascript">
    function addProgressText(str) 
    { 
        var obj = document.getElementById("progressText"); 
        obj.innerHTML += str;
        var obj = document.getElementById("progressP");
        obj.scrollTop = obj.scrollHeight; 
    }

</script>
</head>
<body>
<p id="progressP" style="height:200px; overflow:auto; border:1px solid grey;"> 
    <span id="progressText"></span> 
</p> 
<%
com.sun.identity.setup.SetupProgress.setWriter(out);
out.flush();
Thread.sleep(600000);
com.sun.identity.setup.SetupProgress.reportStart("", null);
%>
</body>
</html>
