package helloworld;

import java.util.HashSet;
import java.util.Set;

//import org.eclipse.jetty.util.log.Log;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import outloud.OutLoudSpeechlet;

/**
 * This class will be the handler for the OutLoud
 * @author sebastianalvarado
 *
 */
public final class HelloWorldSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
  private static final Set<String> supportedApplicationIds = new HashSet<String>();
  
  static {
    /*
     * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
     * Alexa Skill and put the relevant Application Ids in this Set.
     */
     supportedApplicationIds.add("amzn1.echo-sdk-ams.app.372974a0-976c-487c-8bf3-307e437cd39a");
}

public HelloWorldSpeechletRequestStreamHandler() {
    super(new OutLoudSpeechlet(), supportedApplicationIds);
}

}
