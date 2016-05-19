class User {
	private String username;
	private String usercolor;
	private Boolean turnvalue;
	
	public User (String name, String color, Boolean turn){
		this.username = name;
		this.usercolor = color;
		this.turnvalue = turn;
		System.out.println(toString());
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public Boolean getTurnValue(){
		return this.turnvalue;
	}
	
	public String getUserColor() {
		return this.usercolor;
	}
	
	@Override
	public String toString() {
		return ("Username:\t" + this.username + "\nColor:\t" + this.usercolor);
	}
}