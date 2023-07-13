package chaoGarden;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Shop {
	private ArrayList<String> fixedItems = new ArrayList<String>();
	private ArrayList<String> todaysItems = new ArrayList<String>();
	
	public Shop(String shopSeed) {
		//Initialise the Shop with its fixed items
		
		//shopSeed is randomly picked for each user and determines which colour eggs are on sale
		ArrayList<String> fixedEggs = new ArrayList<String>();
		switch (shopSeed) {
		default:
			fixedEggs.addAll(Arrays.asList("White Egg", "Blue Egg", "Orange Egg", "Green Egg", "Grey Egg", "Light Green Egg", "Red Egg", "Aqua Egg", "Brown Egg", "Black Egg"));
			break;
		case "1":
			fixedEggs.addAll(Arrays.asList("White Egg", "Red Egg", "Aqua Egg", "Brown Egg", "Grey Egg", "Light Green Egg", "Yellow Egg", "Pink Egg", "Purple Egg", "Black Egg"));
			break;
		case "2":
			fixedEggs.addAll(Arrays.asList("White Egg", "Yellow Egg", "Pink Egg", "Purple Egg", "Grey Egg", "Light Green Egg", "Blue Egg", "Orange Egg", "Green Egg", "Black Egg"));
			break;
		}
		this.fixedItems.addAll(fixedEggs);
		
		this.fixedItems.add("Hero Fruit");
                this.fixedItems.add("Dark Fruit");
                this.fixedItems.add("Heart Fruit");
		
	}

	public ArrayList<String> returnItems() {
		//Return a list of items on sale.
		ArrayList<String> items = new ArrayList<String>();
		this.todaysItems = rerollItems();
		items.addAll(todaysItems);
		items.addAll(this.fixedItems);
		return items;
		
	}
	
	private ArrayList<String> rerollItems() {
            //Randomly select variable items for the day
            Random random = new Random();
            while(todaysItems.size()<5){
                String selectedItemType = "";
                int randomInt = random.nextInt(1, 214);
                if (randomInt == 1){
                    selectedItemType = "Jewel Egg";
                }
                else if (1< randomInt && randomInt<=3){
                    selectedItemType = "Shiny Two-tone Egg";
                }
                else if (3< randomInt && randomInt<=13){
                    selectedItemType = "Two-tone Egg";
                }
                else if (13< randomInt && randomInt<=23){
                    selectedItemType = "Tasty Mushroom";
                }
                else if (23< randomInt && randomInt<=43){
                    selectedItemType = "Shiny Egg";
                }
                else if (43< randomInt && randomInt<=63){
                    selectedItemType = "Mushroom";
                }
                else if (63< randomInt && randomInt<=93){
                    selectedItemType = "Chao Fruit";
                }
                else if (93< randomInt && randomInt<=133){
                    selectedItemType = "Square Fruit";
                }
                else if (133< randomInt && randomInt<=173){
                    selectedItemType = "Triangle Fruit";
                }
                else {
                    selectedItemType = "Round Fruit";
                }
                if (!todaysItems.contains(selectedItemType)){
                    todaysItems.add(selectedItemType);
                }
            }
            //If the selected item is an egg, choose its colour
            for(String item : todaysItems){
                if (item.contains("Egg")){
                    String editedItem = "";
                    if (item.contains("Jewel")){
                        String[] allJewelEggs = {"Silver Jewel Egg", "Gold Jewel Egg", "Ruby Jewel Egg", "Sapphire Jewel Egg", "Amethyst Jewel Egg", "Emerald Jewel Egg", "Garnet Jewel Egg", "Aquamarine Jewel Egg", "Peridot Jewel Egg", "Topaz Jewel Egg", "Onyx Jewel Egg", "Pearl Jewel Egg", "Metal Jewel Egg", "Glass Jewel Egg"};
                        editedItem = allJewelEggs[random.nextInt(allJewelEggs.length)];
                    }
                    else{
                        String prefix = item.substring(0, item.indexOf("Egg"));
                        String[] allColourEggs = {"White Egg", "Blue Egg", "Orange Egg", "Green Egg", "Grey Egg", "Light Green Egg", "Red Egg", "Aqua Egg", "Brown Egg", "Black Egg", "Yellow Egg", "Pink Egg", "Purple Egg"};
                        String chosenColourEgg = allColourEggs[random.nextInt(allColourEggs.length)];
                        editedItem = prefix + chosenColourEgg;
                    }
                    todaysItems.set(todaysItems.indexOf(item), editedItem);
                }
            }
            
            return this.todaysItems;
	}
}
