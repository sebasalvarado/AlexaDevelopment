package outloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.util.json.JSONObject;


public class OutLoudSpeechlet implements Speechlet {

  private static final Logger log = LoggerFactory.getLogger(OutLoudSpeechlet.class);
  private static final String RESET_URL = "https://amazonechohackathon.herokuapp.com/reset";
  private static String WEB_SERVER_URL = "https://amazonechohackathon.herokuapp.com/whobuzzfirst";
  private final String welcome = "For this game, please open our web link (blank) and enter your name.  Once everyone is done adding their names and the round begins, the first person to press the button may speak and receive points for every correct answer.  When you're ready to play say begin";
  private final String initial = "The next question for you is: ";
  private OutLoudManager manager = new OutLoudManager();
  @Override
  /**
   * @param
   * @param
   * @return
   */
  public void onSessionStarted(final SessionStartedRequest request,final Session session) throws SpeechletException {
    log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
        session.getSessionId());
    // Initialize the game manager that populates the Session with questions and aswers
  }

  @Override
  public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
    
    // First part should be a welcome second should be the details of the game
    log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
        session.getSessionId());
    String reprompt = manager.getRandomReprompt();
    //Adding the welcome message to the first question
    return getSpeechletResponse(welcome,reprompt,true);
  }

  @Override
  public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
    log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
        session.getSessionId());
    //Getting the intent that has the information of the response
    Intent intent = request.getIntent();
    String intentName = (intent != null)?intent.getName():null;
    // Note: If the session is started with an intent, no welcome message will be rendered;
    // rather, the intent specific response will be returned.
    if ("OutLoudIntent".equals(intentName)) {
        // get the answer from the user and check the answer
        connectToServer(WEB_SERVER_URL);
        resetServer(RESET_URL);
        return checkUserAnswer(intent,session,manager.getCurrentPlayer());
    }
    else if("InitialIntent".equals(intentName)){
        String question = manager.getRandomQuestion();
        manager.setCurrentQuestion(question);
        String reprompt = manager.getRandomReprompt();
        return getSpeechletResponse(initial + question,reprompt,true);
    }
    else if("ChangeCategoryIntent".equals(intentName)){
      // set the category of the game
      
    }
    else if("RepeatIntent".equals(intentName)){
      String reprompt = manager.getRandomReprompt();
      return getSpeechletResponse(manager.getCurrentQuestion(),reprompt, true);
    }
    else if ("AMAZON.HelpIntent".equals(intentName)) {
      // Create a text output
      //TODO Add the string we have to say when we dont get anything
      String help = "Okay I will go now, Bye!";
      return getSpeechletResponse(help,"",false);
    } else {
        throw new SpeechletException("Invalid Intent");
    }
  }

  @Override
  public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
    // TODO Auto-generated method stub
    log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
        session.getSessionId());
    
  }
  

  /**
   * 
   * @param intent
   * @param session
   * @return
   */
  private SpeechletResponse checkUserAnswer(Intent intent, Session session, String name){
    // Ask the manager to check the answer if it was correct 
    String finalResult;
    String nextQuestion = manager.getRandomQuestion();
    String userAnswer = intent.getSlot("Answer").getValue();
    boolean result = manager.getResult(userAnswer);
    if(result){
      finalResult = String.format("Great Job %s, That is the correct answer! One point for you", name);
    }
    else{
      finalResult = "That is incorrect, maybe next time. ";
    }
    manager.setCurrentQuestion(nextQuestion);
    nextQuestion = finalResult + initial+ nextQuestion;
    String reprompt = manager.getRandomReprompt();
    return getSpeechletResponse(nextQuestion,reprompt,true);
  }
  
  /**
   * 
   * @param speechText
   * @param repromtText
   * @param isask
   * @return
   */
  private SpeechletResponse getSpeechletResponse(String speechText, String repromptText, boolean isAsk){
    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("Session");
    card.setContent(speechText);
    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);
    
    if(isAsk){
      // Create reprompt
      PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
      repromptSpeech.setText(repromptText);
      Reprompt reprompt = new Reprompt();
      reprompt.setOutputSpeech(repromptSpeech);
      return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
    else{
      // When it is a Tell response 
      return SpeechletResponse.newTellResponse(speech, card);
    }
  }
  
  /**
   * 
   * @param stringUrl
   */
  private void connectToServer(String stringUrl){
    HttpURLConnection connection = null;
    try{
      // SETTING UP THE CONNECTION
      URL url = new URL(stringUrl);
      connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("GET");
      // Getting the response back
      //Parsing the Json string in order to get the name
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();
      
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
       }
       in.close();
       //Convert it into a JSON file and extract the name
       JSONObject obj = new JSONObject(response);
       String userName = obj.getString("name");
       manager.setCurrentPlayer(userName);
       connection.disconnect();
    }catch (Exception e){
      e.printStackTrace();
    }
  }
  
  private void resetServer(String stringUrl){
    HttpURLConnection connection = null;
    try{
      // SETTING UP THE CONNECTION
      URL url = new URL(stringUrl);
      connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("POST");
      connection.disconnect();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

}
