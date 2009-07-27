<%@page contentType="text/html"%> 
<%@page pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head> 
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Stock Quote Client Sample</title>
    </head>
    <body>

    <h1>Stock Quote Client Sample</h1>

    <form name="GetQuote" action="GetQuote" method="GET"> 
        Stock Symbol: <input type="text" name="symbol" value="JAVA" size="12"/>
        <p> <input type="submit" value="GetQuote" name="quote" /> 
    </form>

    </body>
</html>
