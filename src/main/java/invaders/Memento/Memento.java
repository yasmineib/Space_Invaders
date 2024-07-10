package invaders.Memento;

import invaders.entities.Player;
import invaders.factory.EnemyProjectile;
import invaders.factory.PlayerProjectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Memento {
    private  long time;
    private long score;
    private HashMap<Renderable, Vector2D> allPos = null;


    public Memento(long time, long score, HashMap<Renderable, Vector2D> allPos){
        this.time = time;
        this.score = score;
        this.allPos = allPos;

    }
    public List<Renderable> getSavedRenderables(){
        List<Renderable> savedRenderables = new ArrayList<>();

        for(Renderable r: allPos.keySet()){
            Renderable clone;
            if (r.getRenderableObjectName().equals("Enemy")) {
                clone = (Enemy) r.copy();
            }else if(r.getRenderableObjectName().equals("Player")){
                clone = (Player) r.copy();
            }else if(r.getRenderableObjectName().equals("EnemyProjectile")){
                clone = (EnemyProjectile) r.copy();
            }else if(r.getRenderableObjectName().equals("PlayerProjectile")){
                clone = (PlayerProjectile) r.copy();
            }else if(r.getRenderableObjectName().equals("Bunker")){
                clone = (Bunker) r.copy();
            }else{
                clone = r.copy();
            }

            // Now we set the clone's position to the respective position saved in the Hashmap
            Vector2D savedPos = allPos.get(r);
            clone.setPosition(savedPos);
            savedRenderables.add(clone);
        }
        return savedRenderables;
    }

    public long getTime() {
        return time;
    }
    public long getScore() {
        return score;
    }
}