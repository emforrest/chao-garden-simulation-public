package chaoGarden;

import java.util.ArrayList;

public class Bag {
    private ArrayList<Item> items = new ArrayList<Item>();

    public void addItem(Item newItem) {
        //Add a new item to the bag
        items.add(newItem);
    }

    public void removeItem(Item item){
        if(items.contains(item)){
            items.remove(item);
        }
        else{
            System.out.println("Item not found in bag.");
        }
    }
    
    public ArrayList<String> getItemsAsStrings(){
        ArrayList<String> itemsStrings = new ArrayList<String>();
        for (int i=0; i<items.size(); i++){
            itemsStrings.add(items.get(i).getItemType());
        }
        return itemsStrings;
    }
    
    public ArrayList<Item> getItems(){
        return items;
    }
}
