package invaders.gameobject;

import invaders.engine.GameEngine;
import invaders.physics.Vector2D;

// contains basic methods that all GameObjects must implement
public interface GameObject {

    public void start();
    public void update(GameEngine model);
//    public GameObject copy();
    public Vector2D getPosition();
    public void setPosition(Vector2D position);

}
