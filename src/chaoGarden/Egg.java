package chaoGarden;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Egg extends Item {
	private ArrayList<String> parent1Attributes;
	private ArrayList<String> parent2Attributes;
        private ArrayList<Character> parent1IVs;
	private ArrayList<Character> parent2IVs;
	private String colour;
	private String tone;
	private String shiny;
	private String jewel;
        private char swimIV;
        private char flyIV;
        private char runIV;
        private char powerIV;
        private char staminaIV;
        private boolean loaded;
        
        private Scanner scanner = new Scanner(System.in);
	
	public Egg(ArrayList<String> parent1Attributes, ArrayList<String> parent2Attributes, ArrayList<Character> parent1IVs, ArrayList<Character> parent2IVs, int itemID, boolean loaded) {
		this.itemID = itemID;
                this.parent1Attributes = parent1Attributes;
		this.parent2Attributes = parent2Attributes;
                this.parent1IVs = parent1IVs;
                this.parent2IVs = parent2IVs;
                this.loaded = loaded;
		generateColour();
		generateTone();
		generateShiny();
		generateJewel();
                if (!loaded){
                    generateIVs();
                }
                else{
                    //if the egg was already created its IVs should not be recalculated - players could abuse this by reloading the game to get the best stats.
                    this.swimIV = parent1IVs.get(0);
                    this.flyIV = parent1IVs.get(1);
                    this.runIV = parent1IVs.get(2);
                    this.powerIV = parent1IVs.get(3);
                    this.staminaIV = parent1IVs.get(4);
                }
                generateItemType();
		
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
        
        public char getFlyIV(){
            return this.flyIV;
        }
        
        public char getRunIV(){
            return this.runIV;
        }
        
        public char getPowerIV(){
            return this.powerIV;
        }
        
        public char getStaminaIV(){
            return this.staminaIV;
        }
        
	private void generateColour() {
            // Select this chao's colour using the dominant and recessive gene system
            String parent1Colour = this.parent1Attributes.get(0);
            String parent2Colour = this.parent2Attributes.get(0);
            if (parent1Colour.equals(parent2Colour)) {
                this.colour = parent1Colour;
            }
            else if (parent1Colour.equals("Regular")){
                this.colour = parent2Colour;
            }
            else if (parent2Colour.equals("Regular")) {
                this.colour = parent1Colour;
            }
            else{
                String[] colours = {parent1Colour, parent2Colour};
        	Random rand = new Random();
                this.colour = colours[rand.nextInt(2)];
            }
	}

	private void generateTone() {
            // Select whether the chao will be monotone or two-tone
            String parent1Tone = this.parent1Attributes.get(1);
            String parent2Tone = this.parent2Attributes.get(1);
            if (parent1Tone.equals(parent2Tone)) {
                this.tone = parent1Tone;
            }
            else {
        	String[] tones = {parent1Tone, parent2Tone};
        	Random rand = new Random();
            this.tone = tones[rand.nextInt(1)];
            }
	}

	private void generateShiny() {
            // Select whether the chao will be shiny or non-shiny
            String parent1Shiny = this.parent1Attributes.get(2);
            String parent2Shiny = this.parent2Attributes.get(2);
            if (parent1Shiny.equals("Shiny") || parent2Shiny.equals("Shiny")) {
        	this.shiny = "Shiny";
            }
            else {
        	this.shiny = "Non-shiny";
            }
	}

	private void generateJewel() {
            // Select whether the chao will be jewel or non-jewel
            String parent1Jewel = this.parent1Attributes.get(3);
            String parent2Jewel = this.parent2Attributes.get(3);
            if (parent1Jewel.equals(parent2Jewel)) {
                this.jewel = parent1Jewel;
            }
            else if (parent1Jewel.equals("Non-Jewel")){
                this.jewel = parent2Jewel;
            }
            else if (parent2Jewel.equals("Non-Jewel")) {
                this.jewel = parent1Jewel;
            }
            else{
        	String[] jewels = {parent1Jewel, parent2Jewel};
        	Random rand = new Random();
                this.jewel = jewels[rand.nextInt(2)];
            }
	}
        
        private void generateIVs(){
            //Determine the chao's IVs based on those of its parents
            String[] statNames = {"swim", "fly", "run", "power", "stamina"};
            for (String stat : statNames){
                char parent1Stat = parent1IVs.get(Arrays.asList(statNames).indexOf(stat));
                char parent2Stat = parent2IVs.get(Arrays.asList(statNames).indexOf(stat));
                char[] stats = {parent1Stat, parent2Stat};
        	Random rand = new Random();
                char tempStat = stats[rand.nextInt(2)];
                //The chosen stat has a 25%chance to go up or down by one
                int changeInt = rand.nextInt(4);
                if (changeInt == 0){
                    //Decrease the stat
                    switch (tempStat){
                        case 'S':
                            tempStat = 'A';
                            break;
                        case 'A':
                            tempStat = 'B';
                            break;
                        case 'B':
                            tempStat = 'C';
                            break;
                        case 'C':
                            tempStat = 'D';
                            break;
                        case 'D':
                            tempStat = 'E';
                            break;
                    }   
                }
                if (changeInt == 1){
                    //Increase the stat
                    switch (tempStat){
                        case 'E':
                            tempStat = 'D';
                            break;
                        case 'D':
                            tempStat = 'C';
                            break;
                        case 'C':
                            tempStat = 'B';
                            break;
                        case 'B':
                            tempStat = 'A';
                            break;
                        case 'A':
                            tempStat = 'S';
                            break;
                    }   
                }
                //Set the stat
                switch (stat){
                    case "swim":
                        this.swimIV = tempStat;
                        break;
                    case "fly":
                        this.flyIV = tempStat;
                        break;
                    case "run":
                        this.runIV = tempStat;
                        break;
                    case "power":
                        this.powerIV = tempStat;
                        break;
                    case "stamina":
                        this.staminaIV = tempStat;
                        break;

                }
            }
        }
        
        
        private void generateItemType(){
            //Decide how to display the egg
            this.itemType = this.colour + " Egg";
            if(this.tone.equals("Two-tone")){
                this.itemType = "Two-tone " + this.itemType;
            }
            if (this.shiny.equals("Shiny")){
                this.itemType = "Shiny " + this.itemType;
            }
            if (this.colour.equals("Jewel")){
                this.itemType = this.jewel + " Jewel Egg";
            }
        }
        
        public Chao hatch(String user){
            //Create a new Chao
            String[] appearanceAttributes = {this.colour, this.tone, this.shiny, this.jewel};
            char[] ivs = {this.swimIV, this.flyIV, this.runIV, this.powerIV, this.staminaIV};
            int[] evs = {0, 0, 0, 0, 0}; //newly hatched Chao have no evs
            Chao chao = new Chao(appearanceAttributes, ivs, evs, null, user, -1);
            return chao;
        }

	@Override
	public String viewInfo() {
		// Display info about this egg
                String n = ""; //A or An?
                String[] vowels = {"A", "E", "I", "O", "U"};
                if(Arrays.asList(vowels).contains(this.itemType.substring(0, 1))){
                    n = "n";
                }
                String info = String.format("A%s %s. A Chao may hatch from it!", n, this.itemType);
                return info;

	}

        @Override
        public String interact(){
            //What to do when an item is selected in the bag
            String input1 = "";
            String[] options1 = { "I", "H", "D", "B"};
            while (!Arrays.asList(options1).contains(input1)) {
		System.out.println(this.itemType);
		System.out.print("""
I. Info
H. Hatch
D. Discard
B. Back
>>>""");
		input1 = scanner.next().toUpperCase();
            }
            if (input1.equals("I")){
                return "Info";
            }
            else if (input1.equals("H")){
                return "Hatch";
            }
            else if (input1.equals("D")){
                String confirmed = "";
                while (!(confirmed.equals("Y")) && !(confirmed.equals("N"))){
                    System.out.println(String.format("Discard this %s? It cannot be recovered.", this.itemType));
                    System.out.print("""
Y. Yes
N. No
>>>""");
                    confirmed = scanner.next().toUpperCase();
                }
                if (confirmed.equals("Y")){
                    return "Delete";
                }
            }
            return "Back";
            
        }
}
