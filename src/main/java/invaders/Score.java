package invaders;

import invaders.Observer.Observer;
import invaders.Observer.Subject;

import java.util.ArrayList;
import java.util.List;

public class Score implements Subject {
    private long score;
    private List<Observer> observers = new ArrayList<>();

    public Score() {
        this.score = 0;
    }

    public void incrementScore(int points) {
        this.score += points;
    }

    public long getScore() {
        return score;
    }
    public void restroreScoreFromMemento(long score){
        this.score = score;
        notifyObservers();
    }
    public void resetScore(){
        score = 0;
    }

    @Override
    public void attatchObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detatchObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer o: observers){
            o.update();
        }
    }
}
