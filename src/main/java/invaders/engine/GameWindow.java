package invaders.engine;

import java.util.List;
import java.util.ArrayList;
import invaders.Observer.Observer;
import invaders.Score;
import invaders.Time;
import invaders.entities.EntityViewImpl;
import invaders.entities.SpaceBackground;
import javafx.util.Duration;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import invaders.entities.EntityView;
import invaders.rendering.Renderable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GameWindow implements Observer{
	private final int width;
    private final int height;
	private Scene scene;
    private Pane pane;
    private GameEngine model;
    private List<EntityView> entityViews =  new ArrayList<EntityView>();
    private SpaceBackground background;

    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;
    private Time time;
    private Score score;

	public GameWindow(GameEngine model){
        this.model = model;
		this.width =  model.getGameWidth();
        this.height = model.getGameHeight();

        pane = new Pane();
        scene = new Scene(pane, width, height);
        this.background = new SpaceBackground(pane);

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(this.model);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

        time = model.getTime();
        score = model.getScore();
        time.attatchObserver(this);
        score.attatchObserver(this);
    }

	public void run() {
         Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> this.draw()));

         timeline.setCycleCount(Timeline.INDEFINITE);
         timeline.play();
    }
    @Override
    public void update(){
        showClock();
        showScore();
    }


    private void draw(){
        model.update();

        List<Renderable> renderables = model.getRenderables();
        for (Renderable entity : renderables) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (Renderable entity : renderables){
            if (!entity.isAlive()){
                for (EntityView entityView : entityViews){
                    if (entityView.matchesEntity(entity)){
                        entityView.markForDelete();
                    }
                }
            }
        }
        for(Renderable r: model.getPendingToRemoveRenderable()){
            for (EntityView entityView : entityViews){
                if (entityView.matchesEntity(r)){
                    entityView.markForDelete();
                }
            }
        }


        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }


        model.getGameObjects().removeAll(model.getPendingToRemoveGameObject());
        model.getGameObjects().addAll(model.getPendingToAddGameObject());
        model.getRenderables().removeAll(model.getPendingToRemoveRenderable());
        model.getRenderables().addAll(model.getPendingToAddRenderable());

        model.getPendingToAddGameObject().clear();
        model.getPendingToRemoveGameObject().clear();
        model.getPendingToAddRenderable().clear();
        model.getPendingToRemoveRenderable().clear();

        entityViews.removeIf(EntityView::isMarkedForDelete);

        showClock();
        showScore();

        if(model.isOver()){
            showGameOver();
        }

    }

    public void showClock(){
        String timerText = String.format(time.getTimeElapsed());

        Text timer = new Text(timerText);
        timer.setFont(Font.font("Serif", FontWeight.BOLD, FontPosture.REGULAR, 24));
        timer.setFill(Color.WHITE);
        timer.setLayoutX(model.getGameX() - 150);
        timer.setLayoutY(20);

        //remove old, add new
        pane.getChildren().removeIf(node -> node instanceof Text && ((Text) node).getText().startsWith("TIME:"));
        pane.getChildren().add(timer);

    }
    public void showScore(){

        Text scoreText = new Text("SCORE: " + score.getScore());
        scoreText.setFont(Font.font("Serif", FontWeight.BOLD, FontPosture.REGULAR, 24));
        scoreText.setFill(Color.WHITE);
        scoreText.setLayoutX(20);
        scoreText.setLayoutY(20);

        //remove old score
        pane.getChildren().removeIf(node -> node instanceof Text && ((Text) node).getText().startsWith("SCORE:"));

        //add new score
        pane.getChildren().add(scoreText);
    }

	public Scene getScene() {
        return scene;
    }

    public void showGameOver(){
        time.stop();
        boolean didWin = model.gameOver();
        Text winText;
        if(didWin){
            //say YOU WIN
            winText = new Text("YOU WIN!");
        }else{
            //say GAME OVER: you lose.
            winText = new Text("GAME OVER:\n YOU LOSE");
        }
        winText.setFont(Font.font("Serif", FontWeight.BOLD, FontPosture.REGULAR, 50));
        winText.setFill(Color.WHITE);
        winText.setLayoutX((double) (model.getGameWidth() / 4));//i want it centered
        winText.setLayoutY((double) (model.getGameHeight() / 2));
        pane.getChildren().add(winText);
    }
}
