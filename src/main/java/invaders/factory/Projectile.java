package invaders.factory;

import invaders.Observer.Observer;
import invaders.Observer.Subject;
import invaders.Prototype.Prototype;
import invaders.gameobject.GameObject;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import invaders.strategy.ProjectileStrategy;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public abstract class Projectile implements Renderable, GameObject, Prototype, Subject {
    private int lives = 1;
    private Vector2D position;
    private final Image image;
    private List<Observer> observers = new ArrayList<>();
    private ProjectileStrategy strategy;
    private boolean kill = false;


    public Projectile(Vector2D position, Image image) {
        this.position = position;
        this.image = image;
    }
    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public Layer getLayer() {
        return Layer.FOREGROUND;
    }

    @Override
    public void start() {}

    @Override
    public double getWidth() {
        return 10;
    }

    @Override
    public double getHeight() {
        return 10;
    }

    @Override
    public void takeDamage(double amount) {
        this.lives-=1;
    }

    @Override
    public double getHealth() {
        return this.lives;
    }

    @Override
    public boolean isAlive() {
        return this.lives>0;
    }
    public void setPosition(Vector2D position){
        this.position = position;
    }

    @Override
    public void kill(){
        kill = true;
    }
    public boolean getShouldKill(){return kill;}

    @Override
    public void attatchObserver(Observer o){
        observers.add(o);
    }
    @Override
    public void detatchObserver(Observer o){
        observers.remove(o);
    }
    @Override
    public void notifyObservers(){
        for(Observer o: observers){
            o.update();
        }
    }
    public void setLives(int lives){
        this.lives = lives;
    }
    public int cheat(String speed){ return 0;}
}
