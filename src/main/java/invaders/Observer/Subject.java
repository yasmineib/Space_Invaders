package invaders.Observer;

public interface Subject {
    public void attatchObserver(Observer observer);
    public void detatchObserver(Observer observer);
    public void notifyObservers();
}