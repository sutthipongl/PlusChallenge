package sutthipong.pluschallenge;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	 private static final String SESSSTATE = "STATE";
	 private static final String INTENT_START = "PCSessionStart";
	 private static final String INTENT_LEVELCHANGE = "PCSessionLevelChange";
	// private static final String INTENT_LIMITCHANGE = "PCSessionTimeChange";
	 private static final String INTENT_MODECHANGE = "PCSessionModeChange";
	 private static final String INTENT_ANSWER = "PCNumberAnswer";
	 private static final String INTENT_UNCLEAR = "PCUnclear";
	 private static final String ENDLESS = "endless";
	// private static final String LIMIT = "limit";
	 private static final String LEVEL = "level";
	 private static final String NoOfQns = "noOfQuestions";
	 private static final String QN_NO = "qn_no";
	 private static final String QN_ANS = "qn_answer";
	 private static final String SCORE = "score";
	 private static final String QUESTION = "question";
	 private static final String STOPFLAG = "stop";
	 
	 private static final Integer QN_NO_DEFAULT = 5;
	
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
		        return getNextQuestion("",session);
		}

		public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
			// TODO Auto-generated method stub
			log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
	                session.getSessionId());

	        Intent intent = request.getIntent();
	        String intentName = (intent != null) ? intent.getName() : null;
	        log.info("Intent name :"+intentName);
	        // Session start
	        if (intentName.equals(INTENT_START)) 
	        {
	        	return processSessionStart(request,session);
	        	
	        }else if (intentName.equals(INTENT_LEVELCHANGE))
	        {
	        	return processLevelChange(request,session);
	        	
	        }else if (intentName.equals(INTENT_MODECHANGE))
	        {
	        	return processModeChange(request,session);
	        	
	        }else if (intentName.equals(INTENT_ANSWER))
	        { // process both a no of question users want and answer for each challenge
	        	return processAnswer(request,session);
	        	
	        } else if (intentName.equals(INTENT_UNCLEAR))
	        {
	        	return processUnclearResponse(request,session);
	        }
	        else if (intentName.equals("AMAZON.HelpIntent"))
	        {
	        	return getHelpResponse(session);
	        	
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
			log.info("processSessionStart called : Current state is "+ getCurrentState(s) );
			
			return getNextQuestion("",s);
			
		}
		
		private SpeechletResponse processLevelChange(IntentRequest r, Session s)
		{
			log.info("processLevelChange called : Current state is "+ getCurrentState(s) );
			
			if(getCurrentState(s) ==PSstate.LEVEL)
			{
				try{
					// Get and set data from Intent if have		
					String level = String.valueOf(r.getIntent().getSlots().get("l").getValue());
					log.info("processLevelChange called : user said = "+level);
					
					if (level.indexOf("easy") != -1) 
						setAttr(LEVEL,"easy",s);
					else if (level.indexOf("medium") != -1) 
						setAttr(LEVEL,"medium",s);
					else if (level.indexOf("hard") != -1) 
						setAttr(LEVEL,"hard",s);
					else
						setAttr(LEVEL,"easy",s);
						
					setAttr(SESSSTATE,PSstate.START,s);
					log.info("processLevelChange called : set state to START");
					
					return getNextQuestion("",s);
					
				}catch (Exception e)
				{
					// Not sure what user said, ask for answer again
					log.error("processLevelChange Exception : " + e.getMessage());
					return askAgain(getStrAttr(QUESTION, s));
				}
				
				
			}else
			{
				log.error("processLevelChange : NOT LEVEL state");
				
				if(getCurrentState(s) == PSstate.START)
					return askAgain("Question number "+getAttr(QN_NO,s) + ", " +getStrAttr(QUESTION, s));
				else
					return askAgain(getStrAttr(QUESTION, s));
				
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
			log.info("processModeChange called : Current state is "+ getCurrentState(s) );
			
			if (getCurrentState(s) == PSstate.MODE)
			{
				try
				{
					// Get and set data from Intent if have		
					Boolean endless;
					String response = String.valueOf(r.getIntent().getSlots().get("endless").getValue());
					if (response.indexOf("yes") !=  -1 | response.indexOf("yep") !=  -1 | 
							response.indexOf("exactly") !=  -1 | response.indexOf("of course") !=  -1) 
					{	
						endless=false;		
						setAttr(SESSSTATE,PSstate.NOOFQN,s);	
					}
					else
					{
						endless=true;
						setAttr(SESSSTATE,PSstate.LEVEL,s);
					}
					
					setAttr(ENDLESS,endless,s);
					log.info("processModeChange called : endless="+endless);	
					
					return getNextQuestion("",s);
					
				}catch (Exception e)
				{
					// Not sure what user said, ask for answer again
					log.error("processModeChange exception : " +e.getMessage());
					return askAgain(getStrAttr(QUESTION, s));
				}
			
			}else // sometime , users say nine but Alex hears No and route to this intent
			{
				log.error("processModeChange : not MODE state");
				
				if(getCurrentState(s) == PSstate.START)
					return askAgain("Question number "+getAttr(QN_NO,s) + ", " +getStrAttr(QUESTION, s));
				else
					return askAgain(getStrAttr(QUESTION, s));
				
			}
			
			
		}
		
		private SpeechletResponse processAnswer(IntentRequest r, Session s)
		{	
			log.info("processAnswer called : Current state is "+ getCurrentState(s) );
			
			if (getCurrentState(s) == PSstate.NOOFQN)
			{
				try{
					// Get and set data from Intent if have		
					Integer qn = Integer.valueOf(r.getIntent().getSlots().get("ans").getValue());
					setAttr(NoOfQns,qn,s);
					
					log.info("processAnswer called : No of Questions="+qn);
					setAttr(SESSSTATE, PSstate.LEVEL, s);
					return getNextQuestion("",s);
					
				}catch (Exception e)
				{
					// Not sure what user said, ask for answer again
					log.error("processAnswer exception :" + e.getMessage());
					return askAgain("Sorry, " +getStrAttr(QUESTION, s));
				}
				
			}else if (getCurrentState(s)  == PSstate.START)
			{
				// Get and set data from Intent if have		
				String result = "";
				Integer curQnNo = getAttr(QN_NO,s);
				
				try{
					Integer user_ans = Integer.valueOf(r.getIntent().getSlots().get("ans").getValue());
					log.info("processAnswer called : given answer="+user_ans +" for question "+ curQnNo);
					
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
					log.info("processAnswer called : Update QN_NO to "+getAttr(QN_NO,s));
					
					// comment out as score=QN_NO in mode 2
					//if (getAttr(MODE,s).equals(2))
					//	setAttr(NoOfQns, ++curQnNo, s);
					
					// Go to next answer
					return getNextQuestion("",s);
					
				}catch (Exception e)
				{
					// if player can't see number correctly, imply wrong answer
					log.info("processAnswer Exception : wrong answer and get next question");
					
					result="wrong";
					if (getBoolAttr(ENDLESS,s))
					{
						log.info("set STOPFLAG to true");
						setAttr(STOPFLAG,true,s);
					}
					
					// Update Card
					cardQuestionAnswer = new SimpleCard();
					cardQuestionAnswer.setTitle("Question number "+ curQnNo);
					cardQuestionAnswer.setContent(getStrAttr(QUESTION,s) + " = 0 ("+ result +")\nScore : " +getAttr(SCORE,s));	
					
					setAttr(QN_NO, ++curQnNo, s);
					log.info("processAnswer called : Update QN_NO to "+getAttr(QN_NO,s));
					
					return getNextQuestion("",s);
				}
				
			}else 
			{
				log.error("processAnswer : wrong state");
				return askAgain(getStrAttr(QUESTION, s));
			}
				
			
		}
		
		private SpeechletResponse processUnclearResponse(IntentRequest r, Session s)
		{	
			log.info("processAnswer called : Current state is "+ getCurrentState(s) );
			
			if (getCurrentState(s)  == PSstate.MODE)
			{
				log.info("processUnclearResponse : MODE");
				//skip asking a no of question, use default
				setAttr(SESSSTATE,PSstate.LEVEL,s);
				setAttr(ENDLESS,true,s);
			
				return getNextQuestion("No worry , let I question you until you fail," ,s);
				
			}else if (getCurrentState(s) == PSstate.NOOFQN)
			{
				log.info("processUnclearResponse : NOOFQN");
				//skip asking a no of question, use default
				setAttr(SESSSTATE,PSstate.LEVEL,s);

				return getNextQuestion("let start with "+QN_NO_DEFAULT+" questions" ,s);
				
			}else if (getCurrentState(s)  == PSstate.LEVEL)
			{
				log.info("processUnclearResponse : LEVEL");
				setAttr(SESSSTATE,PSstate.START,s);
				setAttr(LEVEL,"easy",s);
				
				return getNextQuestion("let start with the easy level, " ,s);
				
			}else if (getCurrentState(s)  == PSstate.START)
			{   log.info("processUnclearResponse : START");
				// process as usual, we handle this case in processAnswer method
				return processAnswer(r, s);
			}else
				{
					log.error("processUnclearResponse : Unknown Mode");
				
					if(getCurrentState(s) == PSstate.START)
						return askAgain("Question number "+getAttr(QN_NO,s) + ", " +getStrAttr(QUESTION, s));
					else
						return askAgain(getStrAttr(QUESTION, s));
				}
			
		}
		
		private SpeechletResponse askMode(Session s)
		{
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText("Welcome to plus challenge, Before we begin, Would you like to set a number of challenge");
	        
	        // keep latest question in QUESTION attribute, just in case user ask for help, so , we can repeat latest question again
	        setAttr(QUESTION,"Would you like to set a number of challenge",s);
	        // Create reprompt
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);

			return SpeechletResponse.newAskResponse(speech, reprompt);

		}
		
		
		 private SpeechletResponse askLevel(String initSpeech,Session s)
		{
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText(initSpeech+"Could I ask you what level you prefer, easy , medium or hard");
	        
	     // keep latest question in QUESTION attribute, just in case user ask for help, so , we can repeat latest question again
	        setAttr(QUESTION,"Could I ask you what level you prefer, easy , medium or hard",s);
	        
	        // Create reprompt
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);
	    
			return SpeechletResponse.newAskResponse(speech, reprompt);
		}

		private SpeechletResponse askNoOfQuestion(String initSpeech, Session s)
		{
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText(initSpeech + ", How many challenges you would like");
	        
	        // keep latest question in QUESTION attribute, just in case user ask for help, so , we can repeat latest question again
	        setAttr(QUESTION,"How many challenges you would like",s);
	        
	        // Create reprompt
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);

			return SpeechletResponse.newAskResponse(speech, reprompt);
		}
		
		private SpeechletResponse askAgain(String s)
		{
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        if (s==null)
	        	speech.setText("Could you please answer again");
	        else
	        	speech.setText(s);

	        
	        // Create reprompt
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);
	        // card is not required when re-ask

			return SpeechletResponse.newAskResponse(speech, reprompt);
		}
		
		private SpeechletResponse askQuestion(String initSpeech, Session s)
		{
			Integer a = getRandom(s);
			Integer b = getRandom(s);
			Integer answer = a+b;
			setAttr(QN_ANS, answer,s); 
			String speedText  = initSpeech + "Question number "+getAttr(QN_NO,s) + ", " + a + " + " + b;
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
		private SpeechletResponse getNextQuestion(String initSpeech,Session s)
		{
			// Check all required parameters
			//if( getBoolAttr(ENDLESS,s)==null)
			if (getCurrentState(s) == PSstate.MODE)
			{
				log.info("getNextQuestion called : State MODE");
				return askMode(s);
			}
			
			if (getCurrentState(s) == PSstate.NOOFQN)
			{
				log.info("getNextQuestion called : State NOOFQN");
				return askNoOfQuestion(initSpeech,s);
			}	
			
			
			if (getCurrentState(s) == PSstate.LEVEL)
			{
				log.info("getNextQuestion called : State LEVEL");
				return askLevel(initSpeech,s);
			}
			/*
			if( getAttr(LIMIT,s)==null)
			{
				log.info("getNextQuestion called : LIMIT parameter is NULL");
				return askLimit();
			}
			*/
			
			if (getCurrentState(s) == PSstate.START)
			{
				if (!getBoolAttr(ENDLESS,s) )
				{
					Integer curQnNo = getAttr(QN_NO,s);
					log.info("getNextQuestion called : QN_NO is "+curQnNo);
					
					if(curQnNo<= getAttr(NoOfQns,s))
					{
						return askQuestion(initSpeech,s);
						
					}else{
						// No more questions left, finish and tell the score
						return getFinalScore(s);
					}
					
				}else if (getBoolAttr(ENDLESS,s))
				{
					while(!getBoolAttr(STOPFLAG, s))
					{
						return askQuestion(initSpeech,s);
					}
						// Users give wrong answer or time out
						return getFinalScore(s);
					
				}
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
		
		private Integer getCurrentState (Session s)
		{
			return (Integer) s.getAttribute(SESSSTATE);
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
			 session.setAttribute(SESSSTATE, PSstate.MODE);
			 session.setAttribute(ENDLESS, true);
			 session.setAttribute(LEVEL, "easy");
			 session.setAttribute(NoOfQns, QN_NO_DEFAULT);
			 session.setAttribute(QN_NO, 1);
			 session.setAttribute(QN_ANS, "");
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
	        if (cardQuestionAnswer==null)
	        	cardQuestionAnswer = new SimpleCard();
	        
	        String content = cardQuestionAnswer.getContent();
	        
	        if (content == null)
	        	content = "";
	        else
	        	content += "\n";
	        
	        if (getBoolAttr(ENDLESS, s))
	        	cardQuestionAnswer.setContent(content + "Your score is " + getAttr(SCORE,s) + ", thanks for playing");
	        else
	        	cardQuestionAnswer.setContent(content + "Your score is " + getAttr(SCORE,s) + " from " + getAttr(NoOfQns,s) + " questions, thanks for playing");	
	        	
	        resetAttr(s);
			return SpeechletResponse.newTellResponse(speech, cardQuestionAnswer);
		}
		
		private SpeechletResponse getHelpResponse(Session s)
		{
			// Create the plain text output.
	        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
	        speech.setText("Plus challenge game let you calculate the result and if you answer correctly, "
	        		+ "you get score. You can select a number of challenges you need to play "
	        		+ "or play endlessly until you give wrong answer. "
	        		+ "The difficulty can be 'easy' , 'medium' or 'hard' , In easy level, the possible operands can be 0 to 9,"
	        		+ "in meduim level , the possible operands are 0 to 99 and in hard level , the possible operands are 0 to 999,"
	        		+ getStrAttr(QUESTION, s));
	        
	        // Create the Simple card content.
	        SimpleCard card = new SimpleCard();
	        card.setTitle("Help");
	        card.setContent("Plus challenge game let you calculate the result and if you answer correctly, you get score. You can select a number of challenges you need to play or play endlessly until you give wrong answer. The difficulty can be 'easy' , 'medium' or 'hard'\n"+
"Level easy : the possible operands are 0 - 9\n"+
"Level medium : the possible operands are 0 - 99\n"+
"Level hard : the possible operands are 0 - 999");	
	   
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(speech);
	        
			return SpeechletResponse.newAskResponse(speech, reprompt,card);
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
