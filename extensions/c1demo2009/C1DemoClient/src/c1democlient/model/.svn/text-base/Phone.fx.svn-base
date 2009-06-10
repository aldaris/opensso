/*
 * Phone.fx
 *
 * Created on May 18, 2009, 5:10:22 PM
 */

package c1democlient.model;

import c1democlient.Constants;

/**
 * @author pat
 */

public class Phone {
    public var phoneNumber: String;
    public var accountNumber: String;
    public var userName: String;
    public var headOfHousehold: Boolean;
    public var allocatedMinutes: String;
    public var canDownloadRingtones: Boolean;
    public var canDownloadMusic: Boolean;
    public var canDownloadVideo: Boolean;

    public function marshall(): String {
        return "<phone uri=\"http://{Constants.host}:{Constants.port}/{Constants.contextRoot}/resources/phones/{phoneNumber}/\">"
            "<allocatedMinutes>{allocatedMinutes}</allocatedMinutes>"
            "<canDownloadMusic>{canDownloadMusic}</canDownloadMusic>"
            "<canDownloadRingtones>{canDownloadRingtones}</canDownloadRingtones>"
            "<canDownloadVideo>{canDownloadVideo}</canDownloadVideo>"
            "<headOfHousehold>{headOfHousehold}</headOfHousehold>"
            "<phoneNumber>{phoneNumber}</phoneNumber>"
            "<userName>{userName}</userName>"
        "</phone>";
    }
}
