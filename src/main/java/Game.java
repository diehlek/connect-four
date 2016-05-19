
import org.eclipse.jetty.websocket.api.*;
import org.json.*;

import java.text.*;
import java.util.*;

import static j2html.TagCreator.*;
import static spark.Spark.*;


public class Game {

	static Board MYGAMEBOARD = new Board();

	static Map<Session, User> USERNAMEMAP = new HashMap<>();
	static int nextUserNumber = 1;	//Used for creating the next username
	static int nextObserverNumber = 1;


	public static void main(String[] args) {

		staticFileLocation("public");	//index.html is served at localhost: 4567 (default port)
		webSocket("/chat", GameWebSocketHandler.class);
		init();

	}

	//Sends a message from one user to all users, along with a list of current usernames
	public static void broadcastMessage(String sender, String message, boolean disable) {

		String type;
		if (disable){
			type = "disable";
		}else {
			type = "chat";
		}
		//create set of usernames for the userlist
		Set<String> usernames = new HashSet<String>();
		USERNAMEMAP.values().stream().forEach(user-> {
			String line = "";
			line = (user.getUsername() + ": " + user.getUserColor());
			usernames.add(line);
		});

		//updates all the clients chat sessions
		//code inspired by java spark tutorial for chat
		USERNAMEMAP.keySet().stream().filter(Session::isOpen).forEach(session -> {
			try {
				session.getRemote().sendString(String.valueOf(new JSONObject()
				.put("messageType", type)
				.put("userMessage", createHtmlMessageFromSender(sender, message))
				.put("userlist", usernames)
						));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	//sends a message from one user to all users to update the board
	public static void broadcastMessage(String sender, String message, String place, String color) {

		//updates all the clients boards
		USERNAMEMAP.keySet().stream().filter(Session::isOpen).forEach(session -> {
			try {
				session.getRemote().sendString(String.valueOf(new JSONObject()
				.put("messageType", "board")
				.put("place", place).put("color", color)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}


	//Builds a HTML element w/sender-name, a message, and a timestamp
	//code taken from the java spark tutorial for a websocket chat.
	private static String createHtmlMessageFromSender(String sender, String message){
		return article().with(
				b(sender + " says: "),
				p(message),
				span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
				).render();
	}

	//returns whether or not the user was able to properly use their turn.
	//upon failure, they get another turn
	public static boolean update(String sender, String msg, User us){
		String prefix = "\"col";

		//check to see if the message is to update chat or board
		if (msg.startsWith(prefix)){	//update board
			String place = MYGAMEBOARD.getNextOpenPlace(Character.getNumericValue(
					msg.charAt(4)), us.getUserColor());

			//is there an open place in the selected column for the user?
			if(!place.equals("")){
				broadcastMessage(sender, msg, place, us.getUserColor());
				return true;
			} else {
				broadcastMessage("Server",(msg + " is full. "+ sender +" choose another column."),false);
				return false;
			}

		}else{	//update chat
			broadcastMessage(sender, msg, false);
			return false;
		}
	}

	//Method to check if there is a winner on the board
	//Both users cannot win
	public static boolean win() {
		String user1 = "red";
		String user2 = "blue";

		//check user1 winner
		if(MYGAMEBOARD.checkWin(user1)){
			String message = "User1 Wins! Congrats!!!!";
			broadcastMessage("Server",message,true);
			return true;
		}

		//check user2 winner
		if(MYGAMEBOARD.checkWin(user2)){
			String message = "User2 Wins! Congrats!!!!";
			broadcastMessage("Server",message,true);
			return true;
		}

		if (MYGAMEBOARD.fullBoard()){
			broadcastMessage("Server",("Full Board. No winner. Tie."),false);
		}

		return false;
	}


}
