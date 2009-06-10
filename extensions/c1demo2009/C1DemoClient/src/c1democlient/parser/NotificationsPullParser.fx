/*
 * NotificationsPullParser.fx
 *
 * Created on May 18, 2009, 10:11:26 PM
 */

package c1democlient.parser;

import java.io.InputStream;
import javafx.data.pull.Event;
import javafx.data.pull.PullParser;
import c1democlient.model.Notification;

public class NotificationsPullParser {
    var notifications: Notification[];
    var notification: Notification;

    // Completion callback that also delivers parsed phone
    public var onDone: function(data : Notification[]) = null;

    def parseEventCallback = function(event: Event) {
        if (event.type == PullParser.START_ELEMENT) {
            processStartEvent(event)
        } else if (event.type == PullParser.END_ELEMENT) {
            processEndEvent(event)
        }else if (event.type == PullParser.END_DOCUMENT) {
            if (onDone != null) {
                onDone(notifications);
            }
        }
    }
    function processStartEvent(event: Event) {
        if(event.qname.name == "notification" and event.level == 1) {
            notification = Notification {};
        }
    }
    function processEndEvent(event: Event) {
        if(event.qname.name == "notification" and event.level == 1) {
            insert notification into notifications;
        } else if(event.qname.name == "notificationTime" and event.level == 2) {
            notification.notificationTime = event.text;
        } else if(event.qname.name == "messageText" and event.level == 2) {
            notification.messageText = event.text;
        }
    }
    public function parse(input: InputStream) {
        // Parse the input data (Photo Metadata) and construct Photo instance
        def parser = PullParser {
            input: input
            onEvent: parseEventCallback
        }
        parser.parse();
    }

}
