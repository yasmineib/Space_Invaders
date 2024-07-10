package invaders.engine;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class KeyboardInputHandler {
    private final GameEngine model;
    private boolean left = false;
    private boolean right = false;
    private boolean save = false;
    private boolean revert = false;
    private boolean configEasy = false;
    private boolean configMed = false;
    private boolean configHard = false;
    private boolean slowAlienCheat = false;
    private boolean fastAlienCheat = false;
    private boolean slowProjCheat = false;
    private boolean fastProjCheat = false;




    private Set<KeyCode> pressedKeys = new HashSet<>();

    private Map<String, MediaPlayer> sounds = new HashMap<>();

    KeyboardInputHandler(GameEngine model) {
        this.model = model;

        // TODO (longGoneUser): Is there a better place for this code?
        URL mediaUrl = getClass().getResource("/shoot.wav");
        String jumpURL = mediaUrl.toExternalForm();

        Media sound = new Media(jumpURL);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        sounds.put("shoot", mediaPlayer);
    }

    void handlePressed(KeyEvent keyEvent) {
        if (pressedKeys.contains(keyEvent.getCode())) {
            return;
        }
        pressedKeys.add(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.SPACE)) {
            if (model.shootPressed()) {
                MediaPlayer shoot = sounds.get("shoot");
                shoot.stop();
                shoot.play();
            }
        }

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = true;
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            right = true;
        }
        if (keyEvent.getCode().equals(KeyCode.S)) {
            //make a memento
            save = true;
        }
        if (keyEvent.getCode().equals(KeyCode.D)) {
            //revert
            revert = true;
        }
        if (keyEvent.getCode().equals(KeyCode.E)) {
            configEasy = true;
        }if (keyEvent.getCode().equals(KeyCode.M)) {
            configMed = true;
        }if (keyEvent.getCode().equals(KeyCode.H)) {
            configHard = true;
        }

        if (keyEvent.getCode().equals(KeyCode.P)) {
            slowAlienCheat = true;
        }if (keyEvent.getCode().equals(KeyCode.O)) {
            fastAlienCheat = true;
        }if (keyEvent.getCode().equals(KeyCode.L)) {
            slowProjCheat = true;
        }if (keyEvent.getCode().equals(KeyCode.K)) {
            fastProjCheat = true;
        }


        if (left) {
            model.leftPressed();
        }

        if(right){
            model.rightPressed();
        }
        if(save){
            model.save();
        }
        if(revert){
            model.revert();
        }if(configEasy){
            model.changeConfig("easy");
        }if(configMed){
            model.changeConfig("medium");
        }if(configHard){
            model.changeConfig("hard");
        }

        if(slowProjCheat){
            model.projectileCheat("slow");
        }if(fastProjCheat){
            model.projectileCheat("fast");
        }if(slowAlienCheat){
            model.alienCheat("slow");
        }if(fastAlienCheat){
            model.alienCheat("fast");
        }
    }

    void handleReleased(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = false;
            model.leftReleased();
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            model.rightReleased();
            right = false;
        }
        if (keyEvent.getCode().equals(KeyCode.S)) {
            model.rightReleased();
            save = false;
        }
        if (keyEvent.getCode().equals(KeyCode.D)) {
            model.rightReleased();
            revert = false;
        }
        if (keyEvent.getCode().equals(KeyCode.E)) {
            model.rightReleased();
            configEasy = false;
        }if (keyEvent.getCode().equals(KeyCode.M)) {
            model.rightReleased();
            configMed = false;
        }if (keyEvent.getCode().equals(KeyCode.H)) {
            model.rightReleased();
            configHard = false;
        }if (keyEvent.getCode().equals(KeyCode.P)) {
            model.rightReleased();
            slowAlienCheat = false;
        }if (keyEvent.getCode().equals(KeyCode.O)) {
            model.rightReleased();
            fastAlienCheat = false;
        }if (keyEvent.getCode().equals(KeyCode.L)) {
            model.rightReleased();
            slowProjCheat = false;
        }if (keyEvent.getCode().equals(KeyCode.K)) {
            model.rightReleased();
            fastProjCheat = false;
        }
    }
}
