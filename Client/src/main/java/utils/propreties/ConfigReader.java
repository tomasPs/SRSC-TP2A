package utils.propreties;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class ConfigReader {
    private static Config config = null;

    public static Config getConfig(){
        if (config !=null)
            return config;

        Yaml yaml = new Yaml();
        InputStream inputStream = ConfigReader.class
            .getClassLoader()
            .getResourceAsStream("application.yml");

        config = yaml.loadAs(inputStream,Config.class);
        System.out.println(config);
        return config;
    }
}
