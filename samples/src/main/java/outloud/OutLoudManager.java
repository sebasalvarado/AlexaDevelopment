package outloud;

import java.util.*;

import outloud.Question;

public class OutLoudManager {

  /**
   * The BuzzPopManager receives answers and shoots questions to the users and manages the flow of executionof the game.
   */
  
  /**
   * HashMap that contains the questions with their possible answers and Alexa's responses for each
   */
  private HashMap<Integer, Question> questionMap;
  /* String indicating who is the current player */
  private String currentPlayer;
  private String currentQuestion;
  
  private static final String[] repromptMessages = {"Did I stutter?",
                                                    "Are you not entertained?",
                                                    };
 
  /**
   * Game Manager that should initialize the interaction between Alexa and the users
   * Initialize the Question hashmap
   */
  public OutLoudManager() {
    //Initialize the questions and answers
    initializeQuestions();
  }
  
  public String getCurrentPlayer(){
    return currentPlayer;
  }
  
  public void setCurrentPlayer(String newName){
    this.currentPlayer = newName;
  }
  
  /**
   * Returns a random question from the HashMap of possible questions
   * @return
   */
  public String getRandomQuestion(){
    Random random = new Random();
    List<String> keys = new ArrayList<String>(questionMap.keySet());
    String randomQuestion = keys.get(random.nextInt(keys.size()));
    currentQuestion = randomQuestion;
    return randomQuestion;
  }
  
  /**
   * Returns true or false depending on the answer given by the user
   * @param userAnswer
   * @return
   */
  public boolean getResult(String userAnswer){
    // Check the answer against the first item in the arraylist that it maps to
    String expectedAnswer = questionMap.get(currentQuestion);
    return (expectedAnswer.equals(userAnswer));
  }
  
  /**
   * Returns a random reprompt message from a list of messages for the user
   * @return reprompt
   */
  public String getRandomReprompt(){
    Random random = new Random();
    int number = random.nextInt(repromptMessages.length - 1);
    return repromptMessages[number];
    
  }

  public ArrayList<String> getOptions(){
    // go to the question itself and return the arraylist of all the possible answers
  }
  public String getCurrentQuestion() {
    return currentQuestion;
  }

  public void setCurrentQuestion(String currentQuestion) {
    this.currentQuestion = currentQuestion;
  }

  private HashMap<String, Integer> scores = new HashMap<String, Integer>();

  public void addScoreForPlayer(String playerName){
    if(!scores.containsKey(playerName)){
      scores.put(playerName, 1);
    }else{
      int currentscore = scores.get(playerName);
      scores.put(playerName, currentscore + 1);
    }
  }

  public String getEveryOneScore(){
    List<Map.Entry<String,Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(sortByValue(scores).entrySet());
    Map.Entry<String, Integer> lastEntry = entryList.get(entryList.size()-1);
    String playerName = lastEntry.getKey();
    int value = lastEntry.getValue();
    return playerName + " " + value;
  }

  private Map sortByValue(Map map) {
    List list = new LinkedList(map.entrySet());
    Collections.sort(list, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue());
      }
    });

    Map result = new LinkedHashMap();
    for (Iterator it = list.iterator(); it.hasNext();) {
      Map.Entry entry = (Map.Entry)it.next();
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }


}
