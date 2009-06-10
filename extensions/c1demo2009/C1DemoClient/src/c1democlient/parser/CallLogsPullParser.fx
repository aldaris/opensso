/*
 * CallLogsPullParser.fx
 *
 * Created on May 18, 2009, 10:11:26 PM
 */

package c1democlient.parser;

import java.io.InputStream;
import javafx.data.pull.Event;
import javafx.data.pull.PullParser;
import c1democlient.model.CallLog;

public class CallLogsPullParser {
    var calls: CallLog[];
    var call: CallLog;

    // Completion callback that also delivers parsed phone
    public var onDone: function(data : CallLog[]) = null;

    def parseEventCallback = function(event: Event) {
        if (event.type == PullParser.START_ELEMENT) {
            processStartEvent(event)
        } else if (event.type == PullParser.END_ELEMENT) {
            processEndEvent(event)
        }else if (event.type == PullParser.END_DOCUMENT) {
            if (onDone != null) {
                onDone(calls);
            }
        }
    }
    function processStartEvent(event: Event) {
        if(event.qname.name == "callLog" and event.level == 1) {
            call = CallLog {};
        }
    }
    function processEndEvent(event: Event) {
        if(event.qname.name == "callLog" and event.level == 1) {
            insert call into calls;
        } else if(event.qname.name == "phoneNumberTo" and event.level == 2) {
            call.phoneNumberTo = event.text;
        } else if(event.qname.name == "callTime" and event.level == 2) {
            call.callTime = event.text;
        } else if(event.qname.name == "callDurationSecs" and event.level == 2) {
            call.callDurationSecs = Number.parseFloat(event.text);
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
