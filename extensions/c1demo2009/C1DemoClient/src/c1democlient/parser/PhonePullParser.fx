package c1democlient.parser;

import java.io.InputStream;
import javafx.data.pull.Event;
import javafx.data.pull.PullParser;
import c1democlient.model.Phone;

public class PhonePullParser {
    var phone: Phone;
    
    // Completion callback that also delivers parsed phone
    public var onDone: function(data : Phone) = null;

    def parseEventCallback = function(event: Event) {
        if (event.type == PullParser.START_ELEMENT) {
            processStartEvent(event)
        } else if (event.type == PullParser.END_ELEMENT) {
            processEndEvent(event)
        }else if (event.type == PullParser.END_DOCUMENT) {
            if (onDone != null) {
                onDone(phone);
            }
        }
    }
    function processStartEvent(event: Event) {
        if(event.qname.name == "phone" and event.level == 0) {
            phone = Phone {};
        }
    }
    function processEndEvent(event: Event) {
        if(event.qname.name == "phoneNumber" and event.level == 1) {
            phone.phoneNumber = event.text;
        } else if(event.qname.name == "headOfHousehold" and event.level == 1) {
            phone.headOfHousehold = event.text.equals("true");
        } else if(event.qname.name == "userName" and event.level == 1) {
            phone.userName = event.text;
        } else if(event.qname.name == "allocatedMinutes" and event.level == 1) {
            phone.allocatedMinutes = event.text;
        } else if(event.qname.name == "canDownloadRingtones" and event.level == 1) {
            phone.canDownloadRingtones = event.text.equals("true");
        } else if(event.qname.name == "canDownloadMusic" and event.level == 1) {
            phone.canDownloadMusic = event.text.equals("true");
        } else if(event.qname.name == "canDownloadVideo" and event.level == 1) {
            phone.canDownloadVideo = event.text.equals("true");
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
