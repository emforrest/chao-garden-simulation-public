package chaoGarden;

import java.util.ArrayList;

public class ObjectContainer {
    //Used to pass the global variables between methods
    public static ArrayList<Chao> chaoList;
    public static Bag bag;
    
    public ObjectContainer(ArrayList<Chao> chaoList, Bag bag){
        this.chaoList = chaoList;
        this.bag = bag;
    }
}
