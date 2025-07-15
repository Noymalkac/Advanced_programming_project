
package configs;

/**
 * Config.java
 * This file is part of the project_biu configuration interface.
 * It defines the contract for configuration classes in the project.
 */
public interface Config {
    /**
     * Creates the configuration.
     * This method is responsible for initializing the configuration settings.
     */
    void create();
    /**
     * Gets the name of the configuration.
     * @return The name of the configuration.
     */
    String getName();
    /**
     * Gets the version of the configuration.
     * @return The version of the configuration.
     */
    int getVersion();
    /**
     * Closes the configuration.
     * This method is called to clean up resources used by the configuration.
     */
    void close();
}
