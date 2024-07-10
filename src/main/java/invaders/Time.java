package invaders;

import invaders.Observer.Observer;
import invaders.Observer.Subject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Time implements Subject {
    private long startTime = System.currentTimeMillis();
    private long elapsedTime;
    private List<Observer> observers = new ArrayList<>();
    private boolean isRunning = true;
    private String stoppedTime;


    public String getTimeElapsed() {
        if(!isRunning){
            return stoppedTime;
        }
        elapsedTime = System.currentTimeMillis() - startTime;
        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        String time = String.format("TIME: %02d:%02d", minutes, seconds);
        return time;
    }
    public long getElapsedtime(){getTimeElapsed(); return elapsedTime; }

    public void restoreTimeFromMemento(long savedTime){
        elapsedTime = savedTime;
        startTime = System.currentTimeMillis() - elapsedTime; // Adjust start time
        notifyObservers();
    }
    public void resetTime(){
        // Reset the start time
        startTime = System.currentTimeMillis();
        // Reset the elapsed time
        elapsedTime = 0;
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
    public void stop(){
        stoppedTime = getTimeElapsed();
        isRunning = false;
    }

}
