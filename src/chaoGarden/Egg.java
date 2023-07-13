package chaoGarden;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Egg extends Item {
	private ArrayList<String> parent1Attributes;
	private ArrayList<String> parent2Attributes;
	private String colour;
	private String tone;
	private String shiny;
	private String jewel;
        
        private Scanner scanner = new Scanner(System.in);
	
	public Egg(ArrayList<String> parent1Attributes, ArrayList<String> parent2Attributes, int itemID) {
		this.itemID = itemID;
                this.parent1Attributes = parent1Attributes;
		this.parent2Attributes = parent2Attributes;
		generateColour();
		generateTone();
		generateShiny();
		generateJewel();
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
            Chao chao = new Chao(appearanceAttributes, null, user, -1);
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
                    System.out.println(String.format("Discard this %s?", this.itemType));
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
