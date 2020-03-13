*******************************
CS455: Distributed Systems
Project 1 Chat Server
Team 5
    Cody Palin
    Omar Gonzalez
Spring 2020
*******************************

** Files **
In folder Chat
* Chat Client.java: Contains the information and code regarding the client-based operations for chat server. This includes connecting to the server,      
                    doing commands to acquire information from the server or joining chatrooms in the server.
*   ChatServer.java: All the server based code for the chat server, this also creates a port for clients to join the server. It also has channels or    
                     different chatrooms, and information should the client request it.
*   Package.java: Holds the message, who sent the message via nickname, and the destination of the message or chatroom. This is the "package" that is sent 
                  to the server.
------------------------------------------------

** Build **
To build the project:
    1 Enter in the directory.
    2 javac ChatClient.java ChatServer.java Package.java
    2a: or do javac *.java
    3: To run the program itself. do java ChatServer.java
    4: then to see if the server functions, do ChatClient.java.
    5: in ChatClient.java terminal do "/connect localhost 5121"
    6: From there you can /join chatroom to test the chatroom functionality. or /help for a list of all commands that can be performed
    7: The server will disconnect after 5 minutes of inactivity.

** Testing **
We tested it in a relatively simple manner. Essentially whenever a major change was made to the code or We recompiled and ran on eclipse as it has multiple console support, and ran chatclient and chatserver to see if the two interacted with new changes. We tried doing different commands, or had different arguments in the chat.

** Observations **
The development process was an interesting one. As you know, when the project has been been announced it was described or perhaps presented in a way where it was simple. However, we can say now with full faith, it was not so simple. There were many moving parts in this simple concept of a chatroom. The first main challenge was to decide whether to go stateless or a stateful server. In the end we went stateful. We also had to think quite a bit on how to send a message to the server and how to send with the nickname. To solve that, we made a new called Package that we essentially send with the information of the user's nickname, the message itself and later the channel name. The main real challenge was introducing multiple "channels" as described in the project description or the chatrooms. As we were really lost on what exactly that meant. In the end we decided on an interesting by in a way filtering and choosing which messages went to clients in different chatrooms with creating blockingqueues. After we solved the different channel issue, making the userclient commands became a lot more clear. Then we had to backtrack a couple of steps due to the project specifying that it wants to connect using the /connect command rather than instantly and we had to account for that by using a while loop in the userclient so it will always wait for that /connect before it can do anything else.
Omar, worked on various ChatClient commmands while also tweaking the package.java or rather the makings of package.java.
