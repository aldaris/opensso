package c1democlient.parser;

import java.io.InputStream;
import javafx.data.pull.Event;
import javafx.data.pull.PullParser;
import c1democlient.model.Challenge;

public class ChallengePullParser {
    var challenge: Challenge;
    
    // Completion callback that also delivers parsed phone
    public var onDone: function(data : Challenge) = null;

    def parseEventCallback = function(event: Event) {
        if (event.type == PullParser.START_ELEMENT) {
            processStartEvent(event)
        } else if (event.type == PullParser.END_ELEMENT) {
            processEndEvent(event)
        }else if (event.type == PullParser.END_DOCUMENT) {
            if (onDone != null) {
                onDone(challenge);
            }
        }
    }
    function processStartEvent(event: Event) {
        if(event.qname.name == "challenge" and event.level == 0) {
            challenge = Challenge {};
        }
    }
    function processEndEvent(event: Event) {
        if(event.qname.name == "challengeQuestion" and event.level == 1) {
            insert event.text into challenge.challengeQuestions;
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
