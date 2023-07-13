package chaoGarden;

public abstract class Item {
	protected String itemType;

	public String getItemType() {
		return itemType;
	}

	public abstract String viewInfo(); //Display info about the item when viewed in the Shop or bag
	public abstract String interact();
		
	
}
