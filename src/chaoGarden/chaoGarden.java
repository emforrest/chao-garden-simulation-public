package chaoGarden;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Arrays;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class chaoGarden {
        private static ArrayList<Chao> chaoList = new ArrayList<Chao>();
	private static Shop shop;
	private static Bag bag = new Bag();
	
        private static String bytesToHex(byte[] hash) {
            //Convert a hash from a set of bytes into a hexadecimal string
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        
	private static String[] register(Scanner scanner, Connection connection) {
		//Allow the user to create a new account
                String success = "Failure";
                String shopSeed = "";
                System.out.println("Enter a username:");
                String chosenUsername = scanner.next();
                if(chosenUsername.equals("null")){
                    System.out.println("You cannot choose that username.");
                    String[] result= {success, chosenUsername, shopSeed};
                    return result;
                }
                if (chosenUsername.length()<256){ //the max length it can be in the database
                    if(!(chosenUsername.contains(" "))){
                        //See if this username is already taken
                        boolean uniqueUsername = true;
                        try{
                            Statement statement1 = connection.createStatement();
                            ResultSet results1 = statement1.executeQuery("SELECT Username FROM Users");
                            
                            while (results1.next()){
                                if (results1.getString(1).equals(chosenUsername)){
                                    uniqueUsername = false;
                                }
                            }
                            
                            if (uniqueUsername){
                                //Confirm a password
                                System.out.println("Enter a password:");
                                String password = scanner.next();
                                System.out.println("Confirm password:");
                                String confirmPassword = scanner.next();
                                if (password.equals(confirmPassword)){
                                    
                                    //Hash the password
                                    MessageDigest digest;
                                    String encodedHashHex = "";
                                    try {
                                        digest = MessageDigest.getInstance("SHA-256");
                                        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                                        encodedHashHex = bytesToHex(encodedHash);
                                    } 
                                    catch (NoSuchAlgorithmException e) {
                                        System.out.println("Hashing error.");
                                    }
                                    
                                    //Generate a random shop seed for this user
                                    Random rand = new Random();
                                    shopSeed = Integer.toString(rand.nextInt(3));
                                    
                                    //Save this account to the database
                                    try{
                                        Statement statement2 = connection.createStatement();
                                        statement2.execute(String.format("INSERT INTO Users (Username, Password, ShopSeed) VALUES (\"%s\", \"%s\", \"%s\")", chosenUsername, encodedHashHex, shopSeed));
                            
                                        success = "Success";
                                        System.out.println("Account successfully added.");
                                    }
                                    catch (SQLException e) {
                                        System.out.println("SQL error.");
                                    }
                                }
                                else{
                                    System.out.println("Passwords do not match.");
                                }
                            }
                            else{
                                System.out.println("That username is already in use.");
                            }
                        }
                        catch (SQLException e) {
                            System.out.println("SQL error.");
                        }
                    }
                    else{
                        System.out.println("The username must not contain spaces.");
                    }
                }
                else{
                    System.out.println("The username is too long.");
                }
                
                String[] result= {success, chosenUsername, shopSeed};
		return result;
                
	}
	
	private static String[] login(Scanner scanner, Connection connection) {
		//Allow a user to log in and retreive their shop seed
                String success = "Failure";
                String shopSeed = "";
                System.out.println("Enter a username:");
                String inputUsername = scanner.next();
                //Check if the username is in use
                try{
                    boolean foundUsername = false;
                    Statement statement1 = connection.createStatement();
                    ResultSet results1 = statement1.executeQuery("SELECT Username FROM Users;");
                    while(results1.next()){
                        if (inputUsername.equals(results1.getString("Username"))){
                            foundUsername = true;
                            break;
                        }
                    }
                    if (foundUsername){
                        System.out.println("Enter password:");
                        String inputPassword = scanner.next();
                        
                        //Hash the input password
                        MessageDigest digest;
                        String newHash = "";
                        try {
                            digest = MessageDigest.getInstance("SHA-256");
                            byte[] encodedHash = digest.digest(inputPassword.getBytes(StandardCharsets.UTF_8));
                            String encodedHashHex = bytesToHex(encodedHash);
                            newHash = encodedHashHex.toString();
                        } 
                        catch (NoSuchAlgorithmException e) {
                            System.out.println("Hashing error.");
                        }
                        
                        //Retreive the existing password
                        Statement statement2 = connection.createStatement();
                        ResultSet results2 = statement2.executeQuery(String.format("SELECT Password FROM Users WHERE Username = \"%s\";", inputUsername));
                        String oldHash = "";
                        while(results2.next()){
                            oldHash = results2.getString("Password");
                        }
                        
                        //Check the hashes match
                        if (oldHash.equals(newHash)){
                            //Load the user's shop seed
                            Statement statement3 = connection.createStatement();
                            ResultSet results3 = statement3.executeQuery(String.format("SELECT ShopSeed FROM Users WHERE Username = \"%s\";", inputUsername));
                            while(results3.next()){
                                int shopSeedInt = results3.getInt("ShopSeed");
                                shopSeed = Integer.toString(shopSeedInt); 
                            }   
                            success = "Success";
                        }
                        else{
                            System.out.println("Incorrect password.");
                        }
                    }
                    else{
                        System.out.println("That username wasn't found.");
                    }
                }
                catch (SQLException e){
                    System.out.println("SQL Error.");
                }
                
		String[] result= {success, inputUsername, shopSeed};
		return result;
	}
        
        private static ObjectContainer loadGame(String username){
            //Load the game from a saved file
            String url = "jdbc:mysql://132.145.50.130:3306/chaoGardenDatabase?enabledTLSProtocols=TLSv1.2";
            String user = "java";
            String pass = "password"; 
            Connection connection;
            try {
                connection = DriverManager.getConnection(url, user, pass);
                //Retrieve the user ID
                Statement statement1 = connection.createStatement();
                ResultSet results1 = statement1.executeQuery(String.format("SELECT UserID, ShopSeed FROM Users WHERE Users.Username = \"%s\"", username));
                int userID = -1;
                while (results1.next()){
                    userID = results1.getInt("UserID");
                }
                
                //Load the chao garden
                ArrayList<Chao> chaoList = new ArrayList<Chao>();
                Statement statement2 = connection.createStatement();
                ResultSet results2 = statement2.executeQuery(String.format("SELECT * FROM Chaos WHERE UserID = \"%d\";", userID));
                int chaoID;
                String nickname;
                String colour;
                String tone;
                String shiny;
                String jewel;
                String originalOwner;
                String giftedFrom;
                while (results2.next()){
                    Chao chao;
                    chaoID = results2.getInt("ChaoID");
                    nickname = results2.getString("Nickname");
                    colour = results2.getString("Colour");
                    tone = results2.getString("Tone");
                    shiny = results2.getString("Shiny");
                    jewel = results2.getString("Jewel");
                    originalOwner = results2.getString("OriginalOwner");
                    giftedFrom = results2.getString("GiftedFrom");
                    if (giftedFrom != null){
                        System.out.println(String.format("You received a gift Chao, %s, from %s!", nickname, giftedFrom));
                        //Set the Chao's GiftedFrom column to null
                        Statement statement3 = connection.createStatement();
                        statement3.execute(String.format("UPDATE Chaos SET GiftedFrom = null WHERE ChaoID = \"%d\";", chaoID));
                    
                    }
                    
                    String[] appearanceAttributes = {colour, tone, shiny, jewel};
                    chao = new Chao(appearanceAttributes, nickname, originalOwner, chaoID);
                    chaoList.add(chao);
                }
                
                //Load the bag
                Bag bag = new Bag();
                Statement statement3 = connection.createStatement();
                ResultSet results3 = statement3.executeQuery(String.format("SELECT ItemID, ItemType FROM BagItems WHERE UserID = \"%d\";", userID));
                int itemID;
                String itemType;
                while (results3.next()){
                    itemID = results3.getInt("ItemID");
                    itemType = results3.getString("ItemType");
                    //get any related attributes and add the item
                    if (itemType.contains("Egg")){
                        Statement statement4 = connection.createStatement();
                        ResultSet results4 = statement4.executeQuery(String.format("SELECT EggColour, EggTone, EggShiny, EggJewel FROM BagItems WHERE ItemID = \"%d\";", itemID));
                        String eggColour = "";
                        String eggTone = "";
                        String eggShiny = "";
                        String eggJewel = "";
                        while(results4.next()){
                            eggColour = results4.getString("EggColour");
                            eggTone = results4.getString("EggTone");
                            eggShiny = results4.getString("EggShiny");
                            eggJewel = results4.getString("EggJewel");
                        }
                        ArrayList<String> parentAttributes = new ArrayList<String>(Arrays.asList(eggColour, eggTone, eggShiny, eggJewel));
                        Egg egg = new Egg(parentAttributes, parentAttributes, itemID);
                        bag.addItem(egg);
                    }
                }
                
                //Return the game state
                ObjectContainer obj = new ObjectContainer(chaoList, bag);
                return obj;
                
            }
            catch (SQLException e){
                System.out.println("Loading data failed.");
            }
            return null;
        }
        
        private static ObjectContainer saveGame(String username){
            //Save the user's progress to the database
            String url = "jdbc:mysql://132.145.50.130:3306/chaoGardenDatabase?enabledTLSProtocols=TLSv1.2";
            String user = "java";
            String pass = "password"; 
            Connection connection;
            try {
                connection = DriverManager.getConnection(url, user, pass);
                //Retrieve the user ID
                Statement statement1 = connection.createStatement();
                ResultSet results1 = statement1.executeQuery(String.format("SELECT UserID FROM Users WHERE Users.Username = \"%s\"", username));
                int userID = -1;
                while (results1.next()){
                    userID = results1.getInt("UserID");
                }
                
                //Save Chaos
                for(Chao chao:chaoList){
                    Statement statement2 = connection.createStatement();
                    ResultSet results2 = statement2.executeQuery(String.format("SELECT * FROM Chaos WHERE Chaos.UserID = %s", userID));
                    boolean foundEntry = false;
                    while (results2.next()){
                        if (results2.getInt("chaoID") == chao.getChaoID()){
                            foundEntry = true;
                            //Update the entry
                            Statement statement3 = connection.createStatement();
                            statement3.execute(String.format("UPDATE Chaos SET UserID = \"%d\", Nickname = \"%s\", Colour = \"%s\", Tone = \"%s\", Shiny = \"%s\", Jewel = \"%s\", OriginalOwner = \"%s\" WHERE chaoID = \"%s\" AND originalOwner = \"%s\";", userID, chao.getNickname(), chao.getColour(), chao.getTone(), chao.getShiny(), chao.getJewel(), chao.getOriginalOwner(), chao.getChaoID(), chao.getOriginalOwner()));
                        }
                    }
                    if (!foundEntry){
                        //Create a new entry
                        Statement statement4 = connection.createStatement();
                        statement4.execute(String.format("INSERT INTO Chaos (UserID, Nickname, Colour, Tone, Shiny, Jewel, OriginalOwner) VALUES (\"%d\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");", userID, chao.getNickname(), chao.getColour(), chao.getTone(), chao.getShiny(), chao.getJewel(), chao.getOriginalOwner()));                      
                    }
                }
            
                //Save Bag contents
                
                //Clear all items associated with this user in the bag
                Statement statement5 = connection.createStatement();
                statement5.execute(String.format("DELETE FROM BagItems WHERE UserID = \"%s\";", userID));
               
                //Save all items in the bag
                for (Item item:bag.getItems()){
                    String itemString = item.getItemType();
                    String itemType = ""; //in the database the item class is used but this may not match the getItemType() result
                    if (itemString.contains("Egg")){
                        itemType = "Egg";
                        Egg egg = (Egg)item;
                        Statement statement7 = connection.createStatement();
                        statement7.execute(String.format("INSERT INTO BagItems (ItemType, UserID, EggColour, EggTone, EggShiny, EggJewel) VALUES (\"%s\", \"%d\", \"%s\", \"%s\", \"%s\", \"%s\");", itemType, userID, egg.getColour(), egg.getTone(), egg.getShiny(), egg.getJewel()));
                    }
                    ///similar statements for other items - itemtype and userid must be added
                      
                }
                
                //Reload the game state (updates values such as the chao IDs)
                ObjectContainer obj = loadGame(username);
                
                System.out.println("Successfully saved.");
                return obj;
            } catch (SQLException e) {
                System.out.println("Saving failed.");
            } 
            return null;
        }
	
	private static boolean chaoGardenArea(Scanner scanner, ArrayList<Chao> chaoList) {
		//The control flow for the chao garden segment
                boolean reloadRequired = false;
		String input1 = "";
		while (!input1.equals("B")) {
			System.out.println("Chao Garden");
			
			StringBuilder gardenContents = new StringBuilder();
			if (chaoList.isEmpty()) {
				System.out.println("The garden is empty.");
			}
			else {
				ListIterator<Chao> iterator = chaoList.listIterator();
				while(iterator.hasNext()) {
					gardenContents.append((iterator.nextIndex()+1) + ". " + iterator.next().getNickname()+"\n");
				}
			}

			System.out.print(gardenContents);
			System.out.print("""
B. Back
>>>""");
			input1 = scanner.next().toUpperCase();
			
			try {
				int input1Int = Integer.valueOf(input1);
				if (input1Int > 0 && input1Int <= chaoList.size()) {
					Chao chosenChao = chaoList.get(input1Int - 1);
					reloadRequired = chosenChao.interact();
                                        return reloadRequired;
				}
			}
			catch (NumberFormatException e) {			
			}
		}
                return reloadRequired;
	}
	
	private static void bag(Scanner scanner, String username) {
            //The control flow for the bag segment
            String input1 = "";
            while(!input1.equals("B")){
                System.out.println("Bag");
                StringBuilder contents = new StringBuilder();
                if (bag.getItemsAsStrings().isEmpty()) {
                    System.out.println("The bag is empty.");
                }
                else {
                    ListIterator<String> iterator = bag.getItemsAsStrings().listIterator();
                    while(iterator.hasNext()) {
                        contents.append((iterator.nextIndex()+1) + ". " + iterator.next()+"\n");
                    }
                }

                System.out.print(contents);
                System.out.print("""
B. Back
>>>""");
                input1=scanner.next().toUpperCase();
                
                try{
                    int input1Int = Integer.valueOf(input1);
                    if (input1Int > 0 && input1Int <= bag.getItemsAsStrings().size()) {
                        //Get the selected item and choose what to do with it
                        Item chosenItem = bag.getItems().get(input1Int-1);
                        String chosenAction = chosenItem.interact();
                        handleItemAction(chosenItem, chosenAction, username);
                    }
                }
                catch (NumberFormatException e){
                }
            }
        }
            	
	private static void shop(Scanner scanner, String shopSeed) {
		//The control flow for the shop segment
		ArrayList<String> itemsList = shop.returnItems();
		
		String input1 = "";
		while (!input1.equals("B")) {
			System.out.println("Welcome to the Shop! What would you like to buy?");
			StringBuilder shopContents = new StringBuilder();
			ListIterator<String> iterator = itemsList.listIterator();
			while (iterator.hasNext()) {
				shopContents.append((iterator.nextIndex()+1) + ". " + iterator.next()+"\n");
			}
			System.out.print(shopContents);
			System.out.print("""
B. Back
>>>""");
			input1 = scanner.next().toUpperCase();
								
			try {
				int input1Int = Integer.valueOf(input1);
				if (input1Int > 0 && input1Int <= itemsList.size()) {
					
					//View the item's info
					Item chosenItem = createItem(itemsList.get(input1Int -1));
                                        System.out.println(chosenItem.viewInfo());
                                       
                                        //Choose whether to buy the item
                                        String input2 = "";
                                        String[] options2 = {"Y","N"};
                                        while (!(Arrays.asList(options2).contains(input2))) {
                                            System.out.println("Buy this item?");
                                            System.out.print("""
Y. Yes
N. No
>>>""");
                                            input2 = scanner.next().toUpperCase();
                                        }
                                        
                                        if (input2.equals("Y")){
                                            bag.addItem(chosenItem);
                                            System.out.println(String.format("You bought one %s!", chosenItem.getItemType()));
                                        }
                                }
			}
			catch (NumberFormatException e) {	
			}
		}
	}

        private static ObjectContainer giftChao(Scanner scanner,  String currentUser, ArrayList<Chao> chaoList){
            //Allow the user to send a chao to another user of their choice
            String url = "jdbc:mysql://132.145.50.130:3306/chaoGardenDatabase?enabledTLSProtocols=TLSv1.2";
            String user = "java";
            String pass = "password"; 
            Connection connection;
            try {
                connection = DriverManager.getConnection(url, user, pass);
          
                if (chaoList.isEmpty()){
                    System.out.println("You have no Chao to gift.");
                    return null;
                }
                System.out.print("Enter the username of the user you wish to gift a Chao to:");
                String inputUsername = scanner.next();
                //Check if the username is in use
                boolean foundUsername = false;
                Statement statement4 = connection.createStatement();
                ResultSet results4 = statement4.executeQuery("SELECT Username FROM Users;");
                while(results4.next()){
                    if (inputUsername.equals(results4.getString("Username"))){
                        foundUsername = true;
                        break;
                    }
                }
                
                if (foundUsername){
                    //Check the user didn't input their own username
                    if(!inputUsername.equals(currentUser)){
                        //Select a Chao to be sent
                        String input1 = "";
                        StringBuilder gardenContents = new StringBuilder();
			ListIterator<Chao> iterator = chaoList.listIterator();
			while(iterator.hasNext()) {
				gardenContents.append((iterator.nextIndex()+1) + ". " + iterator.next().getNickname()+"\n");
			}
                        while (!input1.equals("B")){
                            System.out.println("Select a Chao to gift:");
                            System.out.print(gardenContents);
                            System.out.print("""
B. Back
>>>""");
                            input1 = scanner.next().toUpperCase();
			
                            try {
                                int input1Int = Integer.valueOf(input1);
                                if (input1Int > 0 && input1Int <= chaoList.size()) {
                                    Chao chosenChao = chaoList.get(input1Int - 1);
                                    
                                    //Confirm the user's selections
                                    String input2 = "";
                                    while (!input2.equals("Y") && !input2.equals("N")) {
                                        System.out.println(String.format("Send %s to %s? (You will no longer see %s in your chao garden.)", chosenChao.getNickname(), inputUsername, chosenChao.getNickname()));
                                        System.out.print("""
Y. Yes
N. No
>>>""");
                                        input2 = scanner.next().toUpperCase();
                                    }
			
                                    if (input2.equals("Y")) {
                                        //Change the chao's user ID in the database
                                        Statement statement5 = connection.createStatement();
                                        ResultSet results5 = statement5.executeQuery(String.format("SELECT UserID from Users WHERE Username = \"%s\";", inputUsername));
                                        int chosenUserID = -1;
                                        while (results5.next()){
                                            chosenUserID = results5.getInt("UserID");
                                        }
                                        Statement statement6 = connection.createStatement();
                                        statement6.execute(String.format("UPDATE Chaos SET UserID = \"%d\", GiftedFrom = \"%s\" WHERE ChaoID = \"%d\";", chosenUserID, currentUser, chosenChao.getChaoID()));
                                        System.out.println(String.format("Successfully gifted %s to %s. Farewell, %s!", chosenChao.getNickname(), inputUsername, chosenChao.getNickname()));
                                        
                                        //Reload the current user's save to remove the chao
                                        ObjectContainer obj = loadGame(currentUser);
                                        return obj;
                                    }
       
				}
                            }
                            catch (NumberFormatException e) {			
                            }
                        }
                    }
                    else{
                        System.out.println("You cannot gift a Chao to yourself.");
                    }
                }
                else{
                    System.out.println(String.format("No such user: %s.", inputUsername));
                }
                connection.close();
            }
            catch (SQLException e){
                System.out.println("SQL Error.");
            }
            return null;
        }
        
	private static Item createItem(String itemString) {
		// Create and return an item given it as a string
		if (itemString.contains(" Egg")){
                    //Work out the appearance attributes
                    String colour = "White";
                    String tone = "Mono-tone";
                    String shiny = "Non-shiny";
                    String jewel = "Non-jewel";
                    if (itemString.contains("Jewel")){
                        jewel = itemString.replace(" Jewel Egg", "");
                        colour = "Jewel";
                    }
                    if (itemString.contains("Two-tone")){
                        tone = "Two-tone";
                    }
                    if (itemString.contains("Shiny")){
                        shiny = "Shiny";
                    }
                    if (!colour.equals("Jewel")){
                        String colour1 = itemString.replace(" Egg", "");
                        String colour2 = colour1.replace("Shiny ", "");
                        colour = colour2.replace("Two-tone ", "");
                    }
                    //Eggs created in this way are those bought in the shop so they do not require two sets of attributes
                    ArrayList<String> parentAttributes = new ArrayList(Arrays.asList(colour, tone, shiny, jewel));
                    Egg egg = new Egg(parentAttributes, parentAttributes, -1);
                    return egg;
                }
                ///etc
	
            return null;
	}
        
        private static void handleItemAction(Item item, String chosenAction, String username){
            //Perform an action on the given item
            if (chosenAction.equals("Info")){
                System.out.println(item.viewInfo());
            }
            if (chosenAction.equals("Hatch")){
                //Hatch an egg
                Egg egg = (Egg) item; //downcast the item as we know it is an egg
                Chao hatchedChao = egg.hatch(username);
                chaoList.add(hatchedChao);
                bag.removeItem(egg);
                System.out.println("Your egg hatched into a Chao!");
            }
            if (chosenAction.equals("Delete")){
                bag.removeItem(item);
                String url = "jdbc:mysql://132.145.50.130:3306/chaoGardenDatabase?enabledTLSProtocols=TLSv1.2";
                String user = "java";
                String pass = "password"; 
                Connection connection;
                try {
                    connection = DriverManager.getConnection(url, user, pass);
                    Statement statement1 = connection.createStatement();
                    statement1.execute(String.format("DELETE FROM BagItems WHERE ItemID = \"%d\";", item.itemID));                
                } 
                catch (SQLException e){
                    System.out.println("SQL Error.");
                }
            }
        }

	public static void main(String[] args) {
            //The main control flow for the game
            
            //Connect to the database
            String url = "jdbc:mysql://132.145.50.130:3306/chaoGardenDatabase?enabledTLSProtocols=TLSv1.2";
            String user = "java";
            String pass = "password"; 
            Connection connection;
            try {
                connection = DriverManager.getConnection(url, user, pass);
            
                Scanner scanner = new Scanner(System.in); //may need to pass into all methods using scanner
		
                System.out.println("Welcome to the Chao Garden!");
		
                while (true){
                
                    String input1 = "";
                    String[] options1 = {"R", "L"};
                    while (!(Arrays.asList(options1).contains(input1))) {
                        System.out.println("Please register or log in to your account.");
                        System.out.print("""
R. Register
L. Login
>>>""");
                        input1 = scanner.next().toUpperCase();
                    }
		
                    String[] userInfo;
                    String success = "Failure";
                    String username = "";
                    String shopSeed = "-1";
                    boolean newUser = false;
                    System.out.println(input1);
                    if ((input1.toUpperCase()).equals("R")) {
                        userInfo = register(scanner, connection);
                        success = userInfo[0];
                        username = userInfo[1];
                        shopSeed = userInfo[2];
                        newUser = true;
                    }
                    else if ((input1.toUpperCase()).equals("L")) {
                        userInfo = login(scanner, connection);
                        success = userInfo[0];
                        username = userInfo[1];
                        shopSeed = userInfo[2];
                    }
                
                    if(success.equals("Success")){
                        //Close the database connection
                        try {
                            connection.close();
                        } 
                        catch (SQLException e) {
                            System.out.println("Connection close error.");
                        }
                    
                        System.out.println(String.format("Welcome %s.", username));
		
                        if (newUser) {
                            //Instantiate the chao garden, shop and bag
                            chaoList = new ArrayList<Chao>();
                            bag = new Bag();
                            System.out.println("Please buy an egg from the Shop to get started.");
                        }
                        else{
                            //Load the user's saved data
                            ObjectContainer obj = loadGame(username);
                            chaoList = obj.chaoList;
                            bag = obj.bag;
                            System.out.println("Successfully loaded data.");
                        }
                        shop = new Shop(shopSeed);
                        
                        while(true) {
                            String input2 = "";
                            String[] options2 = {"C", "B", "S", "G", "X", "Q"};
                            while (!(Arrays.asList(options2).contains(input2))) {
                                System.out.println("Please select a destination:");
                                System.out.print("""
C. Chao Garden
B. Bag
S. Shop
G. Gift a Chao
X. Save Game
Q. Quit
>>>""");
                                input2 = scanner.next().toUpperCase();
                            }
		
                            if (input2.equals("C")) {
                                boolean reloadRequired = chaoGardenArea(scanner, chaoList);
                                if (reloadRequired){
                                    ObjectContainer obj = loadGame(username);
                                    chaoList = obj.chaoList;
                                    bag = obj.bag;
                                }
                            }
                            else if (input2.equals("B")) {
                                bag(scanner, username);
                            }
                            else if (input2.equals("S")) {
                                shop(scanner, shopSeed);
                            }
                            else if (input2.equals("G")){
                                ObjectContainer obj = giftChao(scanner, username, chaoList);
                                chaoList = obj.chaoList;
                                bag = obj.bag;
                            }
                            else if (input2.equals("X")){
                                ObjectContainer obj = saveGame(username);
                                chaoList = obj.chaoList;
                                bag = obj.bag;
                            }
                            else if (input2.equals("Q")){
                                System.out.println("Goodbye.");
                                System.exit(0);
                            }
                        }  
                    }
                }
            } 
            catch (SQLException e) {
                System.out.println("Cannot connect to the database!");
            }
	}



}

