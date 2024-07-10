package invaders.Levels;
public class EasyLevel implements Levels{
    private static EasyLevel instance;
    private String configPath = "src/main/resources/config_easy.json";

    private EasyLevel(String configPath) {
        this.configPath = configPath;
        // Read and store the configuration using ConfigReader
    }
    public String getConfigPath(){return configPath;}

    public static Levels getInstance() {
        String configPath = "src/main/resources/config_easy.json";
        if (instance == null) {
            instance = new EasyLevel(configPath);
        }
        return instance;
    }

}
