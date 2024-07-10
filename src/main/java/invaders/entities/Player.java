package invaders.entities;

import invaders.Prototype.Prototype;
import invaders.engine.GameEngine;
import invaders.factory.PlayerProjectileFactory;
import invaders.factory.Projectile;
import invaders.factory.ProjectileFactory;
import invaders.gameobject.GameObject;
import invaders.physics.Moveable;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;

import invaders.strategy.NormalProjectileStrategy;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.io.File;

public class Player implements Moveable, Renderable, Prototype, GameObject {

    private Vector2D position;
    private double health;
    private double velocity;
    private boolean wasKilled = false;

    private final double width = 20;
    private final double height = 20;
    private final Image image;
    private ProjectileFactory playerProjectileFactory = new PlayerProjectileFactory();
    JSONObject playerInfo;


    public Player(JSONObject playerInfo){
        this.playerInfo = playerInfo;
        int x = ((Long)((JSONObject)(playerInfo.get("position"))).get("x")).intValue();
        int y = ((Long)((JSONObject)(playerInfo.get("position"))).get("y")).intValue();

        this.image = new Image(new File("src/main/resources/player.png").toURI().toString(), width, height, true, true);
        this.position = new Vector2D(x,y);
        this.health = ((Long) playerInfo.get("lives")).intValue();
        this.velocity = ((Long) playerInfo.get("speed")).intValue();

    }
    @Override
    public void takeDamage(double amount) {
        this.health -= amount;
    }
    @Override
    public void kill(){
        this.health = -1;
    }

    @Override
    public double getHealth() {
        return this.health;
    }

    @Override
    public boolean isAlive() {
        return this.health > 0;
    }

    @Override
    public void up() {
        return;
    }

    @Override
    public void down() {
        return;
    }

    @Override
    public void left() {
        this.position.setX(this.position.getX() - this.velocity);
    }

    @Override
    public void right() {
        this.position.setX(this.position.getX() + this.velocity);
    }

    public Projectile shoot(){
        return playerProjectileFactory.createProjectile(new Vector2D(this.position.getX() + 5 ,this.position.getY() - 10),new NormalProjectileStrategy(),null);
    }
    @Override
    public void setPosition(Vector2D position){
        this.position = position;
    }
    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public Layer getLayer() {
        return Layer.FOREGROUND;
    }

    @Override
    public String getRenderableObjectName() {
        return "Player";
    }

    @Override
    public void start() {

    }

    public void setHealth(double health) {
        this.health = health;
    }

    @Override
    public void update(GameEngine model) {

    }

    @Override
    public Renderable copy(){
        Player p = new Player(this.playerInfo);
        Vector2D savedPos = this.getPosition();
        p.setPosition(savedPos);
        p.setHealth(this.health);
        return p;
    }

    public void setWasKilled(boolean wasKilled){
        this.wasKilled = wasKilled;
    }

    public boolean wasKilled() {
        return wasKilled;
    }
}
