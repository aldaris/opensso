package c1democlient.parser;

import java.io.InputStream;
import javafx.data.pull.Event;
import javafx.data.pull.PullParser;
import c1democlient.model.Account;

public class AccountsPullParser {
    var accounts: Account[];
    var account: Account;
    
    // Completion callback that also delivers parsed account
    public var onDone: function(data : Account[]) = null;

    def parseEventCallback = function(event: Event) {
        if (event.type == PullParser.START_ELEMENT) {
            processStartEvent(event)
        } else if (event.type == PullParser.END_ELEMENT) {
            processEndEvent(event)
        }else if (event.type == PullParser.END_DOCUMENT) {
            if (onDone != null) {
                onDone(accounts);
            }
        }
    }
    function processStartEvent(event: Event) {
        if(event.qname.name == "account" and event.level == 1) {
            account = Account {};
        }
    }
    function processEndEvent(event: Event) {
        if(event.qname.name == "account" and event.level == 1) {
            insert account into accounts;
        } else if(event.qname.name == "accountNumber" and event.level == 2) {
            account.accountNumber = event.text;
        } else if(event.qname.name == "billToAddressLine1" and event.level == 2) {
            account.addressLine1 = event.text;
        } else if(event.qname.name == "billToAddressLine2" and event.level == 2) {
            account.addressLine2 = event.text;
        } else if(event.qname.name == "billToCity" and event.level == 2) {
            account.addressCity = event.text;
        } else if(event.qname.name == "billToState" and event.level == 2) {
            account.addressState = event.text;
        } else if(event.qname.name == "billToZip" and event.level == 2) {
            account.addressZip = event.text;
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
