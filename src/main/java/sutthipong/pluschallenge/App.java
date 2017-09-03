package sutthipong.pluschallenge;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.json.SpeechletResponseEnvelope;
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



/**
 * Hello world!
 *
 */
public class App implements Speechlet
{
	 private static final Logger log = LoggerFactory.getLogger(App.class);

	 private static final String INTENT_START = "PCSessionStart";
	 private static final String INTENT_LEVELCHANGE = "PCSessionLevelChange";
	// private static final String INTENT_LIMITCHANGE = "PCSessionTimeChange";
	 private static final String INTENT_MODECHANGE = "PCSessionModeChange";
	 private static final String INTENT_NOOFQUESTION = "PCSessionNoOfQuestionChange";
	 private static final String INTENT_ANSWER = "PCAnswer";
	 private static final String ENDLESS = "endless";
	// private static final String LIMIT = "limit";
	 private static final String LEVEL = "level";
	 private static final String NoOfQns = "noOfQuestions";
	 private static final String QN_NO = "qn_no";
	 private static final String QN_ANS = "qn_answer";
	 private static final String SCORE = "score";
	 private static final String QUESTION = "question";
	 private static final String STOPFLAG = "stop";
	
	 SimpleCard cardQuestionAnswer = null;
	 public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
			// TODO Auto-generated method stub
		 log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
	                session.getSessionId());
		
	        // any initialization logic goes here
		 resetAttr(session);
		}

		public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
			// TODO Auto-generated method stub
			  log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
		                session.getSessionId());
		        return getNextQuestion(session);
		}

		public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
			// TODO Auto-generated method stub
			log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
	                session.getSessionId());

	        Intent intent = request.getIntent();
	        String intentName = (intent != null) ? intent.getName() : null;

	        // Session start
	        if (intentName.equals(INTENT_START)) 
	        {
	        	return processSessionStart(request,session);
	        	
	        }else if (intentName.equals(INTENT_LEVELCHANGE))
	        {
	        	return processLevelChange(request,session);
	        	
	        }/*else if (intentName.equals(INTENT_LIMITCHANGE))
	        {
	        	return processLimitChange(request,session);
	        	
	        }*/else if (intentName.equals(INTENT_MODECHANGE))
	        {
	        	return processModeChange(request,session);
	        	
	        }else if (intentName.equals(INTENT_NOOFQUESTION))
	        {
	        	return processNoOfQuestionChange(request,session);
	        	
	        }else if (intentName.equals(INTENT_ANSWER))
	        {
	        	return processAnswer(request,session);
	        	
	        }else if (intentName.equals("AMAZON.StopIntent") || intentName.equals("AMAZON.CancelIntent")) {
	        	return getFinalScore(session);
	        } 
	        
	        log.error("INVALID INTENT : "+ intentName);
	        return getFailResponse(session);
	        
	        
		}

		public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
			// TODO Auto-generated method stub
			 log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
		                session.getSessionId());
		        // any cleanup logic goes here
		}
		
		private SpeechletResponse processSessionStart(IntentRequest r, Session s)
		{
			log.info("processSessionStart called");	
			return getNextQuestion(s);
			
		}
		
		private SpeechletResponse processLevelChange(IntentRequest r, Session s)
		{
			try{
				// Get and set data from Intent if have		
				String level = String.valueOf(r.getIntent().getSlots().get("l").getValue());
				if (level.equals("easy") | level.equals("medium") | level.equals("hard")) 
					setAttr(LEVEL,level,s);
				else
					return askAgain();
				
				log.info("processLevelChange called : level="+level);
				
				return getNextQuestion(s);
				
			}catch (NumberFormatException e)
			{
				// Not sure what user said, ask for answer again
				return askAgain();
			}
			
			
		}
		
		/*private SpeechletResponse processLimitChange(IntentRequest r, Session s)
		{
			try{
				// Get and set data from Intent if have		
				Integer limit = Integer.valueOf(r.getIntent().getSlots().get("t").getValue());
				
				if(limit >10)
					limit=10;
				
				setAttr(LIMIT,limit,s);
				
				log.info("processLimitChange called : limit="+limit);
			
				return getNextQuestion(s);
				
			}catch (NumberFormatException e)
			{
				// Not sure what user said, ask for answer again
				return askAgain();
			}
			
			
		}*/
		
		private SpeechletResponse processModeChange(IntentRequest r, Session s)
		{
			try
			{
				// Get and set data from Intent if have		
				Boolean endless;
				String response = String.valueOf(r.getIntent().getSlots().get("endless").getValue());
				if (response.equals("yes")) 
					endless=false;
				else
					endless=true;
						
				log.info("processModeChange called : endless="+endless);
				setAttr(ENDLESS,endless,s);
				
				return getNextQuestion(s);
				
			}catch (Exception e)
			{
				// Not sure what user said, ask for answer again
				log.error(e.getMessage());
				return askAgain();
			}
			
			
		}
		
		private SpeechletResponse processNoOfQuestionChange(IntentRequest r, Session s)
		{
			try{
				// Get and set data from Intent if have		
				Integer qn = Integer.valueOf(r.getIntent().getSlots().get("q").getValue());
				setAttr(NoOfQns,qn,s);
				
				log.info("processModeChange called : No of Questions="+qn);
				
				return getNextQuestion(s);
				
			}catch (NumberFormatException e)
			{
				// Not sure what user said, ask for answer again
				return askAgain();
			}
			
			
		}
		private SpeechletResponse processAnswer(IntentRequest r, Session s)
		{
			// Get and set data from Intent if have		
			try{
				String result = "";
				Integer user_ans = Integer.valueOf(r.getIntent().getSlots().get("ans").getValue());
				log.info("processAnswer called : given answer="+user_ans);
				
				Integer curQnNo = getAttr(QN_NO,s);
				
				if(user_ans.equals(getAttr(QN_ANS,s)))
				{
					Integer score = getAttr(SCORE,s);
					setAttr(SCORE,++score,s);
					result="correct";
					log.info("The answer is correct, give one more score");
				}else
				{
					log.info("The answer is wrong");
					result="wrong";
					if (getBoolAttr(ENDLESS,s))
					{
						log.info("set STOPFLAG to true");
						setAttr(STOPFLAG,true,s);
					}
				}
				
				// Update Card
				cardQuestionAnswer = new SimpleCard();
				cardQuestionAnswer.setTitle("Question number "+ curQnNo);
				cardQuestionAnswer.setContent(getStrAttr(QUESTION,s) + " = " + user_ans +" ("+ result +")\nScore : " +getAttr(SCORE,s));	
				
				setAttr(QN_NO, ++curQnNo, s);
				
				// comment out as score=QN_NO in mode 2
				//if (getAttr(MODE,s).equals(2))
				//	setAttr(NoOfQns, ++curQnNo, s);
				
				// Go to next answer
				return getNextQuestion(s);
				
			}catch (NumberFormatException e)
			{
				// Not sure what user said, ask for answer again
				return askAgain();
			}
			
			
		}
		
		
		private SpeechletResponse askMode()
		{
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText("Before we begin, Would you like to set a number of challenge, yes or no");
	        
	        // Create reprompt
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);

			return SpeechletResponse.newAskResponse(speech, reprompt);

		}
		
		
		 private SpeechletResponse askLevel()
		{
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText("Next, Could I ask you how dificult you prefer, easy , medium or hard");
	        
	        // Create reprompt
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);
	        // Create the Simple card content.
	        SimpleCard card = new SimpleCard();
	        card.setTitle("Plus Chanllenge Level selection");
	        card.setContent("Level easy : the possible operands are 0 - 9\n"
					+ "Level medium : the possible operands are 0 - 99\n"
					+ "Level hard : the possible operands are 0 - 999\n");	

			return SpeechletResponse.newAskResponse(speech, reprompt, card);
		}
		
/*
		private SpeechletResponse askLimit()
		{
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText("Please say limit ,followed by a number of second you prefer. Maximum is 10 seconds");
	        
	        // Create reprompt
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);
	        // Create the Simple card content.
	        SimpleCard card = new SimpleCard();
	        card.setTitle("Plus Chanllenge Limit");
	        card.setContent("Time limit for each answer");	

			return SpeechletResponse.newAskResponse(speech, reprompt, card);
		}
*/	
		private SpeechletResponse askNoOfQuestion()
		{
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText("How many challenges you would like, Please say ,set challenge ,and followed by a number of challenges you want");
	        
	        // Create reprompt
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);

			return SpeechletResponse.newAskResponse(speech, reprompt);
		}
		
		private SpeechletResponse askAgain()
		{
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText("Sorry, I am not sure I understand what you said, please say again");
	        
	        // Create reprompt
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);
	        // card is not required when re-ask

			return SpeechletResponse.newAskResponse(speech, reprompt);
		}
		
		private SpeechletResponse askQuestion(Session s)
		{
			Integer a = getRandom(s);
			Integer b = getRandom(s);
			Integer answer = a+b;
			setAttr(QN_ANS, answer,s); 
			String speedText  = "Question number "+getAttr(QN_NO,s) + ", " + a + " plus " + b;
			setAttr(QUESTION,a + " + " + b,s);
			
			log.info(speedText + " should be " + answer);
			
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText(speedText);
	        
	        // Create reprompt
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);
   
	        //cardQuestionAnswer is for the previous question
	        //cardQuestionAnswer is updated when get answer
	       
	        if(cardQuestionAnswer==null)
	        	return SpeechletResponse.newAskResponse(speech, reprompt);
	        else
	        	return SpeechletResponse.newAskResponse(speech, reprompt,cardQuestionAnswer);
		}
		private SpeechletResponse getNextQuestion(Session s)
		{
			// Check all required parameters
			if( getBoolAttr(ENDLESS,s)==null)
			{
				log.info("getNextQuestion called : MODE parameter is NULL");
				return askMode();
			}
			
			if(!getBoolAttr(ENDLESS,s)  && getAttr(NoOfQns,s)==null)
				return askNoOfQuestion();
			
			
			if( getStrAttr(LEVEL,s)==null)
			{
				log.info("getNextQuestion called : LEVEL parameter is NULL");
				return askLevel();
			}
			/*
			if( getAttr(LIMIT,s)==null)
			{
				log.info("getNextQuestion called : LIMIT parameter is NULL");
				return askLimit();
			}
			*/
			if (!getBoolAttr(ENDLESS,s) )
			{
				Integer curQnNo = getAttr(QN_NO,s);
				if(curQnNo<= getAttr(NoOfQns,s))
				{
					return askQuestion(s);
					
				}else{
					// No more questions left, finish and tell the score
					return getFinalScore(s);
				}
				
			}else if (getBoolAttr(ENDLESS,s))
			{
				while(!getBoolAttr(STOPFLAG, s))
				{
					return askQuestion(s);
				}
					// Users give wrong answer or time out
					return getFinalScore(s);
				
			}
			
			return getFailResponse(s);
			
		}
		
		private Integer getAttr (String attr, Session s)
		{
			return (Integer) s.getAttribute(attr);
		}
		
		private Boolean getBoolAttr (String attr, Session s)
		{
			return (Boolean) s.getAttribute(attr);
		}
		
		private String getStrAttr (String attr, Session s)
		{
			return (String) s.getAttribute(attr);
		}
		
		private void setAttr (String attr, Object value, Session s)
		{
			 s.setAttribute(attr,value);
		}
		
		private Integer getRandom(Session s)
		{
			Random rand = new Random(); 
			int seed=0;
			if (getStrAttr(LEVEL,s).equals("easy")) seed = 1;
			if (getStrAttr(LEVEL,s).equals("medium")) seed = 2;
			if (getStrAttr(LEVEL,s).equals("hard")) seed = 3;
			return rand.nextInt((new Double(Math.pow(10,seed)).intValue())); 
		}
		
		private void resetAttr(Session session)
		{
			 session.setAttribute(ENDLESS, null);
			 session.setAttribute(LEVEL, null);
			// session.setAttribute(LIMIT, null);
			 session.setAttribute(NoOfQns, null);
			 session.setAttribute(QN_NO, 1);
			 session.setAttribute(QN_ANS, null);
			 session.setAttribute(SCORE, 0);
			 session.setAttribute(STOPFLAG, false);
		}
		
		private SpeechletResponse getFinalScore(Session s)
		{
			// Users give wrong answer or time out
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText("Your score is " + getAttr(SCORE,s) + ", thanks for playing");
	        
	        // Create the Simple card content.
	        SimpleCard card = new SimpleCard();
	        card.setTitle("Your score");
	        card.setContent("Your score is " + getAttr(SCORE,s) + " from " + getAttr(NoOfQns,s) + " questions, thanks for playing");	
	        
	        resetAttr(s);
			return SpeechletResponse.newTellResponse(speech, card);
		}
		
		private Boolean isModeValid(Integer m)
		{
			return m==1 || m==2;
		}
		
		private SpeechletResponse getFailResponse(Session s)
		{
			// Users give wrong answer or time out
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText("Sorry, something goes wrong, please start again");
	        
	        // Create the Simple card content.
	        SimpleCard card = new SimpleCard();
	        card.setTitle("Something goes wrong");
	        card.setContent("Please start again");	
	        
	        resetAttr(s);
			return SpeechletResponse.newTellResponse(speech, card);
		}
}
