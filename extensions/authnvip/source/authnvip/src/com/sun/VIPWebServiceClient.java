/**
 * VIPWebServiceClient
 *
 * Contributors: Jeff Bounds
 */
package com.sun;

import com.verisign._2006._08.vipservice.TokenIdType;
import com.verisign._2006._08.vipservice.ValidateType;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.ws.BindingProvider;

public class VIPWebServiceClient {

    /**
     * creates an instance from username, password, and success value
     * <br>
     * @param username         username to transmit
     * @param password         password to transmit
     * @param success          success value to transmit
     */
    private String username;
    private String tokenId;
    private String otp;
    private boolean ok = false;
    private Logger logger;

    public VIPWebServiceClient(String _username, String _tokenId, String _otp, boolean success) {
        this.username = _username;    //username is not really needed for validation
        this.otp = _otp;              //one time password
        this.tokenId = _tokenId;
        this.ok = success;
    }

    /**
     * creates an instance from username, password, and false success
     * <br>
     * @param username         username to transmit
     * @param password         password to transmit
     */
    public VIPWebServiceClient(String _username, String _tokenId, String _otp) {
        this(_username, _tokenId, _otp, false);
    }

    /**
     * @return         the success/failure of the transaction
     */
    public boolean isOK() {
        return ok;
    }

    public String validateToken() {

        //Using Web Service client created by Netbeans 6.1. (JAX-WS)
        try {

            //Constructors with initial values didn't seem to be created
            //Thats why I use the methods below rather than creating the ValidateType
            //with values set in constructor.
            ValidateType vipVT = new com.verisign._2006._08.vipservice.ValidateType();

            //ID should be a unique value, for this sample code I just hardcode it
            vipVT.setId("abcd1234");
            vipVT.setVersion("2.0");

            TokenIdType vipTIT = new TokenIdType();

            //TokenID is an input parameter on the login page,  you would most likely lookup the users
            //tokenid based upon their username and not have them input it on the form
            vipTIT.setValue(tokenId);
            //tit.setType(EVENT);

            vipVT.setTokenId(vipTIT);
            vipVT.setOTP(otp);

            com.verisign._2006._08.vipservice.VipSoapInterfaceService service1 = new com.verisign._2006._08.vipservice.VipSoapInterfaceService();
            com.verisign._2006._08.vipservice.VipSoapInterface port1 = service1.getVipServiceAPI();
            // TODO process result here

            //The VIP WSDL doesn't fully define the enpoint, instead the VIPDemo
            //plugs in the URI for each method.   
            //We only use validate, so the URI ending is /val/soap
            //Wasn't sure how to do this or if this is the proper technique, but it works
            BindingProvider bp = (BindingProvider) port1;
            Map<String, Object> rc = bp.getRequestContext();
            rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://pilot-vipservices-auth.verisign.com/val/soap");


            com.verisign._2006._08.vipservice.ValidateResponseType resp = port1.validate(vipVT);

            BigInteger reason = new BigInteger(resp.getStatus().getReasonCode());

            if (reason.intValue() != 0) {
                return resp.getStatus().getStatusMessage();
            } else {
                ok = true;
                return "Success";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
