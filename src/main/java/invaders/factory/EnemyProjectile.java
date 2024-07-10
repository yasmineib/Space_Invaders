package invaders.factory;
import invaders.engine.GameEngine;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import invaders.strategy.ProjectileStrategy;
import javafx.scene.image.Image;
import invaders.Observer.Observer;
import java.util.ArrayList;
import java.util.List;

public class EnemyProjectile extends Projectile {
    private ProjectileStrategy strategy;
    private List<Observer> observers = new ArrayList<>();

    public EnemyProjectile(Vector2D position, ProjectileStrategy strategy, Image image) {
        super(position,image);
        this.strategy = strategy;
    }
    @Override
    public int cheat(String speed){
        int score = 0;
        if(speed.equals("fast")){
            // Remove fast porjectile
            if(strategy.getStrategy().equals("fast")){
                this.takeDamage(10);
                score += 2;
            }
        }
        if(speed.equals("slow")){
            // Remove slow porjectile
            if(strategy.getStrategy().equals("slow")){
                this.takeDamage(10);
                score += 1;
            }
        }
        return score;
    }
    @Override
    public void update(GameEngine model) {
        strategy.update(this);

        if(this.getPosition().getY()>= model.getGameHeight() - this.getImage().getHeight()){
            this.takeDamage(1);
        }

    }
    public String getStrategy(){
        return strategy.getStrategy();
    }
    @Override
    public String getRenderableObjectName() {
        return "EnemyProjectile";
    }
    @Override
    public Renderable copy() {
        Projectile ep = new EnemyProjectile(getPosition(),this.strategy, getImage());
        Vector2D savedPos = this.getPosition();
        ep.setPosition(savedPos);
        ep.setLives(1);
        return ep;
    }

}
