# What is Plus Challenge ?
Plus challenge or *PC* is a fantastic game or addictive Alexa skill running in Amazon Echo device. It lets you calculate the result and if you answer correctly, you get score. You can select a number of challenges you need to play or play endlessly until you give wrong answer. The difficulty can be 'easy' , 'medium' or 'hard'

# Mode
There are two modes
* Endless : PC will keep asking you until you fail.
* Limit a number of question : You can specify a number of questions you want.

# Level or difficulty
Level easy : the possible operands are 0 - 9
Level medium : the possible operands are 0 - 99
Level hard : the possible operands are 0 - 999

# Developer
This skill presents how to handle same data type (Slot) in different question.
For example, Please consider these 2 question, we expect users to response with number
> Q1 : How many a number of questions would you like ?
> Q2 : What is 10+5 ?

How does our skill process answer with correct question ? Yes, we have 2 approach

## Separate Intents with key word
For example,
>NoofQuestionResIntent {numofqn} questions
>SumAnswerIntent the answer is {ans}

This approch will unnaturally ask user to provide key word or Users need to answer with the pattern we need. Surely we need to guide the pattern in our question's speech.

## In-App State
This approach routes numberic answer to same Intent, said
> NumberAnswerIntent {ans}

In our skill, we control our workflow. For this case, user need to specify a number of question before the game begins. So, we can check if "a number of question" stil be blank, the number users said is for Q1.

# Custom Slot
PS_LEVEL	easy | medium | hard
PS_ENDLESS	yes | no | of course | exactly | nope