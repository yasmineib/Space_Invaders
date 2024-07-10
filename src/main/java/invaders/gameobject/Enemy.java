package invaders.gameobject;

import invaders.Observer.Observer;
import invaders.Observer.Subject;
import invaders.Prototype.Prototype;
import invaders.builder.Director;
import invaders.builder.EnemyBuilder;
import invaders.engine.GameEngine;
import invaders.factory.EnemyProjectileFactory;
import invaders.factory.Projectile;
import invaders.factory.ProjectileFactory;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import invaders.strategy.ProjectileStrategy;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy implements GameObject, Renderable, Prototype, Subject {
    private Vector2D position;
    private int lives = 1;
    private Image image;
    private int xVel = -1;

    private ArrayList<Projectile> enemyProjectile;
    private ArrayList<Projectile> pendingToDeleteEnemyProjectile;
    private ProjectileStrategy projectileStrategy;
    private ProjectileFactory projectileFactory;
    private Image projectileImage;
    private Random random = new Random();
    private List<Observer> observers = new ArrayList<>();


    public Enemy(Vector2D position) {
        this.position = position;
        this.projectileFactory = new EnemyProjectileFactory();
        this.enemyProjectile = new ArrayList<>();
        this.pendingToDeleteEnemyProjectile = new ArrayList<>();
    }
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
    public int cheat(String speed){
        int score = 0;
        if(speed.equals("fast")){
            // Remove fast aliens
            if(projectileStrategy.getStrategy().equals("fast")){
                this.kill();
                score += 4;
            }
        }
        if(speed.equals("slow")){
            // Remove slow aliens
            if(projectileStrategy.getStrategy().equals("slow")){
                this.kill();
                score += 3;
            }
        }
        return score;
    }
    public void multiplyxVel(int mult){
        this.xVel *= mult;
    }
    @Override
    public void start() {}
    @Override
    public void kill(){
        this.lives = -1;
    }

    @Override
    public void update(GameEngine engine) {}
    public ProjectileFactory getProjectileFactory(){
        return projectileFactory;
    }
    public ProjectileStrategy getProjectileStrategy(){
        return projectileStrategy;
    }
    public int getxVel(){return this.xVel; }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public double getWidth() {
        return this.image.getWidth();
    }

    @Override
    public double getHeight() {
       return this.image.getHeight();
    }

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    @Override
    public Layer getLayer() {
        return Layer.FOREGROUND;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setProjectileImage(Image projectileImage) {
        this.projectileImage = projectileImage;
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
    public String getRenderableObjectName() {
        return "Enemy";
    }

    @Override
    public boolean isAlive() {
        return this.lives>0;
    }

    public void setProjectileStrategy(ProjectileStrategy projectileStrategy) {
        this.projectileStrategy = projectileStrategy;
    }
    public void setxVel(int xVel){this.xVel = xVel; }
    @Override
    public Renderable copy() {
        Enemy clone = new Enemy(this.position);
        clone.setImage(this.image);
        clone.setProjectileStrategy(this.projectileStrategy);
        Vector2D savedPos = this.getPosition();
        clone.setPosition(savedPos);
        clone.setxVel(this.xVel);
        clone.setLives(this.lives);
        return clone;
    }
}
