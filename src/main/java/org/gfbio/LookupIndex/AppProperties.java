package org.gfbio.LookupIndex;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads required program parameters from a supplied parameters file
 *
 */
public final class AppProperties {

  private static AppProperties instance;

  private static Properties properties;

  private AppProperties() {
    properties = new Properties();
  }

  public static AppProperties getInstance() {
    if (instance == null) {
      instance = new AppProperties();
    }
    return instance;
  }

  /**
   * 
   * @param propertiesFile
   */
  public void initProperties(String propertiesFile) {

    if (propertiesFile == null) {
      throw new RuntimeException("no .properties path supplied!");
    }

    try {
      properties.load(new FileReader(propertiesFile));
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("no valid .properties supplied!");
    }

  }

  public String getProperty(String p) {
    return properties.getProperty(p);
  }
}
