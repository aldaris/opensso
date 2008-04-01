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
    var isProgressShow = false;
    function toggleProgressDiv()
    {
        var obj = document.getElementById("progressControl"); 
        var obj1 = document.getElementById("progressDiv"); 
        if (isProgressShow == true ) {
            obj.innerHTML = "Show Progress";
            obj1.style.display ="none";
            isProgressShow = false;
        } else {
            obj.innerHTML = "Hide progress log";
            obj1.style.display="block";
            isProgressShow = true;
        }
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
com.sun.identity.setup.SetupProgress.reportStart("setupprogress.timeout", null);
%>
</body>
</html>
