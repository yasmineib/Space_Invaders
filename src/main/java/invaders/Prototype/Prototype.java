package invaders.Prototype;

import invaders.gameobject.GameObject;
import invaders.rendering.Renderable;

public interface Prototype {
    public Renderable copy();
}