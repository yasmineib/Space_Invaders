package invaders.Levels;
public class HardLevel implements Levels{
    private static HardLevel instance;
    private String configPath = "src/main/resources/config_hard.json";

    private HardLevel(String configPath) {
        this.configPath = configPath;
    }
    public String getConfigPath(){return configPath;}

    public static HardLevel getInstance() {
        String configPath = "src/main/resources/config_hard.json";
        if (instance == null) {
            instance = new HardLevel(configPath);
        }
        return instance;
    }
}
