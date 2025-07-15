/**
 * GenericConfig.java
 * This file is part of the project_biu configuration management system.
 * It implements a generic configuration loader for agents defined in a configuration file.
 */
package configs;

import graph.ParallelAgent;
import graph.Agent;

import java.io.*;
import java.util.*;

/**
 * GenericConfig is a configuration class that loads agents from a specified configuration file.
 * It reads the file, instantiates agents based on the defined classes, and manages their subscriptions and publications.
 */
public class GenericConfig implements Config {
    private final List<ParallelAgent> agents = new ArrayList<>();
    private String confFile;
    /**
     * Loads the configuration from an InputStream.
     *
     * @param is The InputStream to read the configuration from.
     * @return A GenericConfig instance populated with agents defined in the configuration.
     */
    public static GenericConfig load(InputStream is) {
        GenericConfig config = new GenericConfig();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }

            if (lines.size() % 3 != 0) {
                throw new IllegalArgumentException("Invalid configuration file: line count not divisible by 3");
            }

            for (int i = 0; i < lines.size(); i += 3) {
                String className = lines.get(i);
                String[] subs = lines.get(i + 1).split(",");
                String[] pubs = lines.get(i + 2).split(",");

                for (int j = 0; j < subs.length; j++) subs[j] = subs[j].trim();
                for (int j = 0; j < pubs.length; j++) pubs[j] = pubs[j].trim();

                Class<?> cls = Class.forName(className);
                Object instance = cls.getConstructor(String[].class, String[].class)
                                     .newInstance((Object) subs, (Object) pubs);

                if (!(instance instanceof Agent)) {
                    throw new RuntimeException("Class does not implement Agent: " + className);
                }

                ParallelAgent pa = new ParallelAgent((Agent) instance, 10);
                config.agents.add(pa);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration: " + e.getMessage(), e);
        }
        return config;
    }
    /**
     * Sets the path to the configuration file.
     *
     * @param path The path to the configuration file.
     */
    public void setConfFile(String path) {
        this.confFile = path;
    }
    /**
     * Creates the configuration by loading agents from the specified configuration file.
     * This method reads the configuration file, instantiates agents, and adds them to the configuration.
     * If the configuration file is not set, it throws a RuntimeException.
     */
    @Override
    public void create() {
        if (confFile == null) throw new RuntimeException("Configuration file not set.");
        try (InputStream is = new FileInputStream(confFile)) {
            GenericConfig loaded = load(is);
            this.agents.addAll(loaded.agents);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create configuration: " + e.getMessage(), e);
        }
    }
    /**
     * Returns the name of the configuration.
     *
     * @return The name of the configuration.
     */
    @Override
    public String getName() {
        return "Generic Config";
    }
    /**
     * Returns the version of the configuration.
     *
     * @return The version number of the configuration.
     */
    @Override
    public int getVersion() {
        return 1;
    }
    /**
     * Closes all agents in the configuration.
     * This method is called to clean up resources used by the agents.
     */
    @Override
    public void close() {
        for (ParallelAgent pa : agents) {
            pa.close();
        }
    }
}
