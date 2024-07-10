package invaders.Memento;
import java.util.*;
public class Caretaker {
    private ArrayList<Memento> mementos = new ArrayList<>();
    public void addMemento(Memento mem){
        mementos.add(mem);
    }
    public Memento getMemento(){
        if(mementos.size()<=0){
            return null;
        }
        int index = mementos.size() - 1;
        return mementos.get(index);
    }

}