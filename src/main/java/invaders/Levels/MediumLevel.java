package invaders.Levels;
public class MediumLevel implements Levels{
    private static MediumLevel instance;
    private String configPath = "src/main/resources/config_medium.json";

    private MediumLevel(String configPath) {
        this.configPath = configPath;
        // Read and store the configuration using ConfigReader
    }
    public String getConfigPath(){return configPath;}

    public static MediumLevel getInstance() {
        String configPath = "src/main/resources/config_medium.json";
        if (instance == null) {
            instance = new MediumLevel(configPath);
        }
        return instance;
    }
}
