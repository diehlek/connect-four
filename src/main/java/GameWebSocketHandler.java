
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;


@WebSocket
public class GameWebSocketHandler {
	private String sender, msg;
	private boolean turn= true;
	private static final String USER1 = "red";
	private static final String USER2 = "blue";
	private String color = USER1;
	private boolean WON = false;


	@OnWebSocketConnect
	public void onConnect(Session user) throws Exception {
		if(Game.nextUserNumber <= 2){
			String username = "User" + Game.nextUserNumber++;
			Game.USERNAMEMAP.put(user, new User(username, color, turn));
			sender = "Server";
			msg = (username + " joined the game. Welcome!");
			Game.broadcastMessage(sender, msg,false);

			//set the correct color for next user
			if (color == USER1){
				color = USER2;
			} else {
				color = USER1;
			}
			//make sure the turn is correct
			turn = !turn;
		} else {
			String name = "Observer" + Game.nextObserverNumber++;
			Game.USERNAMEMAP.put(user, new User(name, "none", null));
			sender = "Server";
			msg = (name + " Tried to join game.\nGame is full.");
			
			//obtain board for new observer to view
			Game.MYGAMEBOARD.updateAll();
			//broadcast new observer
			Game.broadcastMessage(sender, msg,false);
			
			
		}

	}

	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {
		String username = Game.USERNAMEMAP.get(user).getUsername();
		Game.USERNAMEMAP.remove(user);
		sender = "Server";
		msg = (username + " left the chat");
		Game.broadcastMessage(sender,msg,false);

		if(username.startsWith("User")){
			//User1 or User2?
			if(username.endsWith("1")){
				Game.nextUserNumber=1;	
				color = USER1;
			} else {
				Game.nextUserNumber=2;
				color = USER2;
			}

		} else {
			Game.nextObserverNumber--;
		}
	}


	@OnWebSocketMessage
	public void onMessage(Session user, String message) {
		User us = Game.USERNAMEMAP.get(user);
		sender = us.getUsername();

		//if game isn't won
		//makes sure that it's the correct users turn to make a move
		//and to make sure there are 2 users, so no one is playing by self.
		//if game is won, all users can broadcast messages
		if(!WON && sender.startsWith("U")){
			if (turn == us.getTurnValue() && Game.USERNAMEMAP.size() >=2){
				msg = message;

				//User takes turn
				if(Game.update(sender, msg, us)){
					turn = !turn;	//update who's turn it is
				}

				//Check for winner
				if(Game.win()){
					WON=true;
				}

			} else if (Game.USERNAMEMAP.size() ==1) {
				Game.broadcastMessage(sender, msg = "You need to have 2 Users to play the game.", false);
			}
			else {
				msg = ("Not " + us.getUsername() + "'s turn.");
				Game.broadcastMessage(sender = "Server",msg,false);
			}

		} else {
			if(message.startsWith("\"col")){
				message = "Observers can't play on the board"; 
			}
			Game.broadcastMessage(sender, message,false);
		}
	}
}
