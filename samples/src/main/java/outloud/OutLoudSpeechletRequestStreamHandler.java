package outloud;

import java.util.HashSet;
import java.util.Set;



import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import outloud.OutLoudSpeechlet;

/**
 * This class will be the handler for the OutLoud
 * @author sebastianalvarado
 *
 */
public final class OutLoudSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
  private static final Set<String> supportedApplicationIds = new HashSet<String>();
  
  static {
    /*
     * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
     * Alexa Skill and put the relevant Application Ids in this Set.
     */
     supportedApplicationIds.add("amzn1.echo-sdk-ams.app.75dfc98a-c882-4f5d-80f0-23e70cabd1c9");
}

public OutLoudSpeechletRequestStreamHandler() {
    super(new OutLoudSpeechlet(), supportedApplicationIds);
}

}
