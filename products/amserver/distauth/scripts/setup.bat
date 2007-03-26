java -cp classes;war/WEB-INF/lib/amclientsdk.jar com.sun.identity.distauth.setup.Main
jar cvf distauth.war manifest -C war .
