package c1democlient.parser;

import java.io.InputStream;
import javafx.data.pull.Event;
import javafx.data.pull.PullParser;

public class OtpPullParser {
    var otp: String;
    
    // Completion callback that also delivers parsed OTP
    public var onDone: function(data : String) = null;

    def parseEventCallback = function(event: Event) {
        if (event.type == PullParser.START_ELEMENT) {
            processStartEvent(event)
        } else if (event.type == PullParser.END_ELEMENT) {
            processEndEvent(event)
        }else if (event.type == PullParser.END_DOCUMENT) {
            if (onDone != null) {
                onDone(otp);
            }
        }
    }
    function processStartEvent(event: Event) {
        if(event.qname.name == "otp" and event.level == 0) {
            // Do nothing
        }
    }
    function processEndEvent(event: Event) {
        if(event.qname.name == "otp" and event.level == 0) {
            otp = event.text;
        }
    }
    public function parse(input: InputStream) {
        def parser = PullParser {
            input: input
            onEvent: parseEventCallback
        }
        parser.parse();        
    }
    
}
