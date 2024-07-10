package invaders.engine;

import java.util.*;
import java.util.List;
import invaders.ConfigReader;
import invaders.Levels.EasyLevel;
import invaders.Levels.Levels;
import invaders.Levels.MediumLevel;
import invaders.Memento.*;
import invaders.Observer.Observer;
import invaders.Score;
import invaders.Time;
import invaders.builder.BunkerBuilder;
import invaders.builder.Director;
import invaders.builder.EnemyBuilder;
import invaders.factory.EnemyProjectile;
import invaders.factory.Projectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.entities.Player;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import org.json.simple.JSONObject;
import invaders.Levels.HardLevel;

/**
 * This class manages the main loop and logic of the game
 */
public class GameEngine implements Observer {
	private List<GameObject> gameObjects = new ArrayList<>(); // A list of game objects that gets updated each frame
	private List<GameObject> pendingToAddGameObject = new ArrayList<>();
	private List<GameObject> pendingToRemoveGameObject = new ArrayList<>();

	private List<Renderable> pendingToAddRenderable = new ArrayList<>();
	private List<Renderable> pendingToRemoveRenderable = new ArrayList<>();

	private List<Renderable> renderables =  new ArrayList<>();
	private List<Projectile> enemyProjectile = new ArrayList<>();
	private List<Projectile> pendingToDeleteEnemyProjectile = new ArrayList<>();
	private List<Enemy> enemies = new ArrayList<>();
	private Random random = new Random();
	private JSONObject eachBunkerInfo;
	private JSONObject eachEnemyInfo;
	private List<Enemy> fastEnemies = new ArrayList<>();
	private List<Enemy> slowEnemies = new ArrayList<>();
	private List<Bunker> bunkers = new ArrayList<>();
	private Player player = null;
	private Caretaker caretaker = new Caretaker();
	private ArrayList<Renderable> clonedRenderables = new ArrayList<>();
	private HashMap<Renderable, Vector2D> allpos = new HashMap<>();
	private Score score = new Score();
	private Time time = new Time();

	private boolean left;
	private boolean right;
	private int gameWidth;
	private int gameHeight;
	private int timer = 45;
	private Levels level;
	private boolean didWin = false;
	private boolean isOver = false;
	private int initialAlienSize = 0;

	// Constructor
	public GameEngine(String difficultyLevel) {
		// Initialize game level based on difficulty
		if (difficultyLevel.equals("medium")) {
			level = MediumLevel.getInstance();
		} else if (difficultyLevel.equals("hard")) {
			level = HardLevel.getInstance();
		} else {
			level = EasyLevel.getInstance();
		}

		// Read configuration
		readConfig();

		// Attach observers
		time.attatchObserver(this);
		score.attatchObserver(this);
	}

	// Read game configuration
	public void readConfig() {
		// Prepare for resetting game state
		pendingToRemoveGameObject.addAll(gameObjects);
		pendingToRemoveRenderable.addAll(renderables);
		gameObjects.clear();
		allpos.clear();
		enemies.clear();
		enemyProjectile.clear();
		score.resetScore();
		time.resetTime();

		// Remove existing renderables
		for (Renderable r : renderables) {
			r.kill();
		}

		// Parse configuration
		String config = level.getConfigPath();
		ConfigReader.parse(config);

		// Get game width and height from configuration
		gameWidth = ((Long) ((JSONObject) ConfigReader.getGameInfo().get("size")).get("x")).intValue();
		gameHeight = ((Long) ((JSONObject) ConfigReader.getGameInfo().get("size")).get("y")).intValue();

		// Instantiate player
		if (player != null) {
			player = null;
		}
		this.player = new Player(ConfigReader.getPlayerInfo());
		renderables.add(player);
		gameObjects.add(player);

		// Build bunkers
		Director director = new Director();
		BunkerBuilder bunkerBuilder = new BunkerBuilder();
		for (Object eachBunkerInfo : ConfigReader.getBunkersInfo()) {
			this.eachBunkerInfo = (JSONObject) eachBunkerInfo;
			Bunker bunker = director.constructBunker(bunkerBuilder, this.eachBunkerInfo);
			gameObjects.add(bunker);
			renderables.add(bunker);
			bunkers.add(bunker);
		}

		// Build enemies
		EnemyBuilder enemyBuilder = new EnemyBuilder();
		for (Object eachEnemyInfo : ConfigReader.getEnemiesInfo()) {
			this.eachEnemyInfo = (JSONObject) eachEnemyInfo;
			Enemy enemy = director.constructEnemy(enemyBuilder, this.eachEnemyInfo);
			gameObjects.add(enemy);
			renderables.add(enemy);
			enemies.add(enemy);
			initialAlienSize++;
		}
	}


	/**
	 * Updates the game/simulation
	 */
	public void update() {
		timer += 1;

		movePlayer();

		// Update all game objects
		for (GameObject go : gameObjects) {
			go.update(this);
		}

		// Check if player was killed
		if (player.wasKilled()) {
			didWin = false;
			isOver = true;
			gameOver();
		}

		// Collision logic
		for (int i = 0; i < renderables.size(); i++) {
			Renderable renderableA = renderables.get(i);
			for (int j = i + 1; j < renderables.size(); j++) {
				Renderable renderableB = renderables.get(j);

				// Skip collision check if both renderables are enemies or enemy projectiles
				if ((renderableA.getRenderableObjectName().equals("Enemy") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))
						|| (renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("Enemy"))
						|| (renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))) {
					continue;
				}

				// Perform collision check and update score and health accordingly
				if (renderableA.isColliding(renderableB) && (renderableA.getHealth() > 0 && renderableB.getHealth() > 0)) {
					calculateScore(renderableA, renderableB);
					renderableA.takeDamage(1);
					renderableB.takeDamage(1);

					// Check if player or enemy is killed
					if (renderableA.getRenderableObjectName().equals("Player") || renderableA.getRenderableObjectName().equals("Player")) {
						if (!player.isAlive()) {
							player.setWasKilled(true);
						}
					}

					if (renderableA.getRenderableObjectName().equals("Enemy") || renderableA.getRenderableObjectName().equals("Enemy")) {
						boolean doesHave = false;
						for (Enemy enemy : enemies) {
							if (enemy.isAlive()) {
								doesHave = true;
								break;
							}
						}
						if (!doesHave) {
							didWin = true;
							gameOver();
						}
					}
				}
			}
		}

		// Load all positions
		allpos.clear();
		for (Renderable r : renderables) {
			allpos.put(r, r.getPosition());
		}

		// Ensure that renderable foreground objects don't go off-screen
		int offset = 1;
		for (Renderable ro : renderables) {
			if (!ro.getLayer().equals(Renderable.Layer.FOREGROUND)) {
				continue;
			}
			if (ro.getPosition() == null) {
				System.out.println(ro.getRenderableObjectName());
			} else {
				if (ro.getPosition().getX() + ro.getWidth() >= gameWidth) {
					ro.getPosition().setX((gameWidth - offset) - ro.getWidth());
				}

				if (ro.getPosition().getX() <= 0) {
					ro.getPosition().setX(offset);
				}

				if (ro.getPosition().getY() + ro.getHeight() >= gameHeight) {
					ro.getPosition().setY((gameHeight - offset) - ro.getHeight());
				}

				if (ro.getPosition().getY() <= 0) {
					ro.getPosition().setY(offset);
				}
			}
		}

		// Enemy Projectile Logic
		for (Enemy enemy : enemies) {
			// Adding enemy projectiles
			if (enemyProjectile.size() < 3) {
				if (enemy.isAlive() && random.nextInt(120) == 10 && !isOver) {
					Projectile p = enemy.getProjectileFactory().createProjectile(new Vector2D(enemy.getPosition().getX() + enemy.getImage().getWidth() / 2, enemy.getPosition().getY() + enemy.getImage().getHeight() + 2), enemy.getProjectileStrategy(), enemy.getImage());
					enemyProjectile.add(p);
					getPendingToAddGameObject().add(p);
					getPendingToAddRenderable().add(p);
				}
			}

			// Removing enemy projectiles
			pendingToDeleteEnemyProjectile.clear();
			for (Projectile p : enemyProjectile) {
				if (!p.isAlive() || p.getShouldKill()) {
					getPendingToRemoveGameObject().add(p);
					getPendingToRemoveRenderable().add(p);
					pendingToDeleteEnemyProjectile.add(p);
				}
			}

			for (Projectile p : pendingToDeleteEnemyProjectile) {
				enemyProjectile.remove(p);
			}

			// Changing the enemy speed and shifting it down
			if (enemy.getPosition().getX() <= enemy.getImage().getWidth() || enemy.getPosition().getX() >= (this.getGameWidth() - enemy.getImage().getWidth() - 1)) {
				enemy.getPosition().setY(enemy.getPosition().getY() + 25);
				enemy.multiplyxVel(-1);
			}

			enemy.getPosition().setX(enemy.getPosition().getX() + enemy.getxVel());

			// If enemy reached the bottom of the screen, kill player
			if ((enemy.getPosition().getY() + enemy.getImage().getHeight()) >= this.getPlayer().getPosition().getY()) {
				this.getPlayer().takeDamage(Integer.MAX_VALUE);
				// You lose!
				didWin = false;
			}
		}
	}
	public void calculateScore(Renderable renderableA, Renderable renderableB) {
		// Incrementing Score based on collision type

		// Check if renderableA is an Enemy
		if (renderableA.getRenderableObjectName().equals("Enemy")) {
			Enemy enemyA = (Enemy) renderableA;
			String s = enemyA.getProjectileStrategy().getStrategy();
			// Increment score based on projectile strategy
			if (s.equals("slow")) {
				score.incrementScore(3);
			} else {
				score.incrementScore(4);
			}
		}

		// Check if renderableB is an Enemy
		if (renderableB.getRenderableObjectName().equals("Enemy")) {
			Enemy enemyB = (Enemy) renderableB;
			String s = enemyB.getProjectileStrategy().getStrategy();
			// Increment score based on projectile strategy
			if (s.equals("slow")) {
				score.incrementScore(3);
			} else {
				score.incrementScore(4);
			}
		}

		// Check if renderableA is an EnemyProjectile and renderableB is a PlayerProjectile
		if (renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("PlayerProjectile")) {
			EnemyProjectile projA = (EnemyProjectile) renderableA;
			String s = projA.getStrategy();
			// Increment score based on projectile strategy
			if (s.equals("slow")) {
				score.incrementScore(1);
			} else {
				score.incrementScore(2);
			}
		}

		// Check if renderableB is an EnemyProjectile and renderableA is a PlayerProjectile
		if (renderableB.getRenderableObjectName().equals("EnemyProjectile") && renderableA.getRenderableObjectName().equals("PlayerProjectile")) {
			EnemyProjectile projB = (EnemyProjectile) renderableB;
			String s = projB.getStrategy();
			// Increment score based on projectile strategy
			if (s.equals("slow")) {
				score.incrementScore(1);
			} else {
				score.incrementScore(2);
			}
		}
	}

	// Getters for various lists and objects
	public List<Renderable> getRenderables() {
		return renderables;
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}

	public List<GameObject> getPendingToAddGameObject() {
		return pendingToAddGameObject;
	}

	public List<GameObject> getPendingToRemoveGameObject() {
		return pendingToRemoveGameObject;
	}

	public List<Renderable> getPendingToAddRenderable() {
		return pendingToAddRenderable;
	}

	public List<Renderable> getPendingToRemoveRenderable() {
		return pendingToRemoveRenderable;
	}

	// Methods for handling key presses and releases
	public void leftReleased() {
		this.left = false;
	}

	public void rightReleased() {
		this.right = false;
	}

	public void leftPressed() {
		this.left = true;
	}

	public void rightPressed() {
		this.right = true;
	}

	// Getter for the score object
	public Score getScore() {
		return score;
	}

	// Method for handling shooting action
	public boolean shootPressed() {
		// Check if enough time has passed and the player is alive
		if (timer > 45 && player.isAlive()) {
			// Create a projectile and add it to the game
			Projectile projectile = player.shoot();
			gameObjects.add(projectile);
			renderables.add(projectile);
			timer = 0;
			return true;
		}
		return false;
	}

	private void movePlayer(){
		if(left){
			player.left();
		}
		if(right){
			player.right();
		}
	}

	public void save(){
		// Get the positions per renderable to save in the memento
		HashMap<Renderable, Vector2D> positions = new HashMap<>();
		for(Renderable r: renderables){
			Vector2D pos = r.getPosition();
			positions.put(r, pos);
		}

		Memento memento = new Memento(time.getElapsedtime(), score.getScore(), positions);
		caretaker.addMemento(memento);
		clonedRenderables.clear();
	}

	public void revert(){
		// Revert to the previous state by killing existing renderables and replacing them with the
		// saved renderables

		Memento memento = caretaker.getMemento();
		if(memento == null){
			return;
		}

		for(Renderable clone: memento.getSavedRenderables()){
			// Clone
			clonedRenderables.add(clone); // This method sets the clone's posistion to the respective position in the hashmap
			if(clone.getRenderableObjectName().equals("Player")){
				this.player = (Player) clone;
			}
			if(clone.getRenderableObjectName().equals("Enemy")){
				enemies.add((Enemy) clone);
			}
			if(clone.getRenderableObjectName().equals("EnemyProjectile")){
				enemyProjectile.add((Projectile) clone);
			}if(clone.getRenderableObjectName().equals("Bunker")){
				bunkers.add((Bunker) clone);
			}
		}

		for(Renderable r: renderables){
			// Kill old renderables
			r.kill();
			pendingToRemoveRenderable.add(r);
		}
		for(Projectile p: enemyProjectile){
			p.kill();
		}
		gameObjects.clear();
		renderables.clear();
		renderables.addAll(clonedRenderables);
		time.restoreTimeFromMemento(memento.getTime());
		score.restroreScoreFromMemento(memento.getScore());


	}

	public void changeConfig(String newLevel){
		if (newLevel.equals("medium")){
			level = MediumLevel.getInstance();
		}else if(newLevel.equals("hard")){
			level = HardLevel.getInstance();
		}else{
			level = EasyLevel.getInstance();
		}
		readConfig();
	}
	public void alienCheat(String speed){
		for(Enemy enemy: enemies){
			score.incrementScore(enemy.cheat(speed));
		}
	}
	public void projectileCheat(String speed){
		Iterator<Projectile> iterator = enemyProjectile.iterator();
		while(iterator.hasNext()){
			score.incrementScore(iterator.next().cheat(speed));
		}
	}
	public int getGameX(){return gameWidth;}
	public Time getTime(){return this.time; }

	public int getGameWidth() {
		return gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean gameOver(){
		isOver = true;
		for(int i = 0; i < renderables.size(); i++ ){
			renderables.get(i).kill();
		}
		renderables.clear();
		enemyProjectile.clear();
		return didWin;
	}

	public boolean isOver() {
		return isOver;
	}

}
