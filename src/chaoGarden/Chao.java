package chaoGarden;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Scanner;

public class Chao {
	//A Chao. Users breed and collect them
        private int chaoID;
	private String nickname;
        private String originalOwner;
	private String colour;
	private String tone;
	private String shiny;
	private String jewel;
        private char swimIV;
        private char flyIV;
        private char runIV;
        private char powerIV;
        private char staminaIV;
        private int swimEV;
        private int flyEV;
        private int runEV;
        private int powerEV;
        private int staminaEV;
	
	private Scanner scanner = new Scanner(System.in);
	
	public Chao(String[] appearanceAttributes, char[] ivs, int[] evs, String nickname, String originalOwner, int chaoID) {
                this.chaoID = chaoID;
		this.colour = appearanceAttributes[0];
		this.tone = appearanceAttributes[1];
		this.shiny = appearanceAttributes[2];
		this.jewel = appearanceAttributes[3];
                this.swimIV = ivs[0];
                this.flyIV = ivs[1];
                this.runIV = ivs[2];
                this.powerIV = ivs[3];
                this.staminaIV = ivs[4];
                this.swimEV = evs[0];
                this.flyEV = evs[1];
                this.runEV = evs[2];
                this.powerEV = evs[3];
                this.staminaEV = evs[4];
                if (nickname == null){
                    this.nickname = this.toString();
                }
                else{
                    this.nickname = nickname;
                }
                this.originalOwner = originalOwner;
	}
	
	public String toString() {
            //Red Chao, Shiny Yellow Chao etc
            StringBuilder chaoString = new StringBuilder();
            
            if (this.colour.equals("Jewel")){
                chaoString.append(this.jewel);
                chaoString.append(" Jewel");
            }
            else{
                if (this.shiny.equals("Shiny")){
                    chaoString.append("Shiny ");
                }
                if(this.tone.equals("Two-tone")){
                    chaoString.append("Two-tone ");
                }
            }
            
            chaoString.append(this.colour);
            chaoString.append(" Chao");
            
            return chaoString.toString();
	}
	
	public String getNickname() {
		return this.nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
        public int getChaoID(){
            return this.chaoID;
        }
        
        public String getOriginalOwner(){
            return this.originalOwner;
        }
        
        public String getColour(){
            return this.colour;
        }
        
        public String getTone(){
            return this.tone;
        }
        
        public String getShiny(){
            return this.shiny;
        }
        
        public String getJewel(){
            return this.jewel;
        }
        
        public char getSwimIV(){
            return this.swimIV;
        }
        
        public int getSwimEV(){
            return this.swimEV;
        }
        
        public char getFlyIV(){
            return this.flyIV;
        }
        
        public int getFlyEV(){
            return this.flyEV;
        }
        
        public char getRunIV(){
            return this.runIV;
        }
        
        public int getRunEV(){
            return this.runEV;
        }
        
        public char getPowerIV(){
            return this.powerIV;
        }
        
        public int getPowerEV(){
            return this.powerEV;
        }
        
        public char getStaminaIV(){
            return this.staminaIV;
        }
        
        public int getStaminaEV(){
            return this.staminaEV;
        }
        
	public boolean interact() {
		//What to do when a Chao is selected in the Chao garden
		String input1 = "";
		String[] options1 = { "1", "2", "3", "4", "B"};
		while (!Arrays.asList(options1).contains(input1)) {
			System.out.println(this.nickname);
			System.out.print("""
1. Set Nickname
2. View Appearance Attributes
3. View Stats
4. View Original Owner
5. Say Goodbye
B. Back
>>>""");
			input1 = scanner.next().toUpperCase();
		}
		
		if (input1.equals("1")) {
			this.editNickname();			
		}
		else if (input1.equals("2")) {
			//Display the Chao's appearance
			System.out.println(String.format("""
Colour: %s
Tone: %s
Shiny: %s
Jewel: %s""", this.colour, this.tone, this.shiny, this.jewel));
			System.out.println("Input to continue:");
			scanner.next();
		}
                else if (input1.equals("3")) {
			//Display the Chao's stat IVs and EVs
			System.out.println(String.format("""
Swim: %c +%d
Fly: %c +%d
Run: %c +%d
Power: %c +%d
Stamina: %c +%d""", this.swimIV, this.swimEV, this.flyIV, this.flyEV, this.runIV, this.runEV, this.powerIV, this.powerEV, this.staminaIV, this.staminaEV));
			System.out.println("Input to continue:");
			scanner.next();
		}
                else if (input1.equals("4")) {
                    System.out.println(String.format("Original Owner: %s", this.originalOwner));
                }
                else if (input1.equals("5")){
                    String confirmed1 = "";
                    while (!(confirmed1.equals("Y")) && !(confirmed1.equals("N"))){
                        System.out.println(String.format("Say Goodbye to %s?", this.nickname));
                        System.out.print("""
Y. Yes
N. No
>>>""");
                        confirmed1 = scanner.next().toUpperCase();
                    }
                    if (confirmed1.equals("Y")){
                        String confirmed2 = "";
                        while (!(confirmed2.equals("Y")) && !(confirmed2.equals("N"))){
                            System.out.println(String.format("Are you sure you want to release this Chao? Your game will be saved and you will not see it again.", this.nickname));
                            System.out.print("""
Y. Yes
N. No
>>>""");
                            confirmed2 = scanner.next().toUpperCase();
                        }
                        if (confirmed2.equals("Y")){
                            System.out.println(String.format("Farewell, %s!", this.nickname));
                            this.deleteChao();
                            return true;
                        }
                    }
                }
                return false;
	}
	
	public void editNickname() {
		//Contains the I/O to set a new nickname
		String newNickname = "";
		boolean confirmed = false;
		while(!confirmed) {
			System.out.println("Enter a new nickname for this Chao:");
			System.out.print(">>>");
			newNickname = scanner.next();
			if (newNickname.isBlank()) {
				newNickname = this.toString();
			}
			String input2 = "";
			while (!input2.equals("Y") && !input2.equals("N")) {
				System.out.println(String.format("Name this Chao \"%s\"?", newNickname));
				System.out.print("""
Y. Yes
N. No
>>>""");
				input2 = scanner.next().toUpperCase();
			}
			
			if (input2.equals("Y")) {
				confirmed = true;
			}
		}
		this.setNickname(newNickname);
		System.out.println("Nickname set.");
	}
        
        public void deleteChao(){
            //Delete this chao
            String url = "jdbc:mysql://132.145.50.130:3306/chaoGardenDatabase?enabledTLSProtocols=TLSv1.2";
            String user = "java";
            String pass = "password"; 
            Connection connection;
            try {
                connection = DriverManager.getConnection(url, user, pass);
                Statement statement1 = connection.createStatement();
                statement1.execute(String.format("DELETE FROM Chaos WHERE ChaoID = \"%d\";", this.chaoID));
                
            } 
            catch (SQLException e){
                System.out.println("SQL Error.");
            }
            
        }
}
