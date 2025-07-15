// package configs;
// import graph.ParallelAgent;
// import graph.Agent;

// import java.io.*;
// import java.util.*;

// public class GenericConfig implements Config {
//     private final List<ParallelAgent> agents = new ArrayList<>();
//     private String confFile;

//     @Override
//     public void create() {
//         if (confFile == null) throw new RuntimeException("Configuration file not set.");

//         try {
//             List<String> lines = new ArrayList<>();
//             try (BufferedReader br = new BufferedReader(new FileReader(confFile))) {
//                 String line;
//                 while ((line = br.readLine()) != null) {
//                     lines.add(line.trim());
//                 }
//             }

//             if (lines.size() % 3 != 0) {
//                 throw new IllegalArgumentException("Invalid configuration file: line count not divisible by 3");
//             }

//             for (int i = 0; i < lines.size(); i += 3) {
//                 String className = lines.get(i);
//                 String[] subs = lines.get(i + 1).split(",");
//                 String[] pubs = lines.get(i + 2).split(",");

//                 Class<?> cls = Class.forName(className);
//                 Object instance = cls.getConstructor(String[].class, String[].class)
//                                      .newInstance((Object) subs, (Object) pubs);

//                 if (!(instance instanceof Agent)) {
//                     throw new RuntimeException("Class does not implement Agent: " + className);
//                 }

//                 ParallelAgent pa = new ParallelAgent((Agent) instance, 10);
//                 agents.add(pa);
//             }
//         } catch (Exception e) {
//             throw new RuntimeException("Failed to create configuration: " + e.getMessage(), e);
//         }
//     }

//     @Override
//     public String getName() {
//         return "Generic Config";
//     }

//     @Override
//     public int getVersion() {
//         return 1;
//     }

//     @Override
//     public void close() {
//         for (ParallelAgent pa : agents) {
//             pa.close();
//         }
//     }
// }


package configs;

import graph.ParallelAgent;
import graph.Agent;

import java.io.*;
import java.util.*;

public class GenericConfig implements Config {
    private final List<ParallelAgent> agents = new ArrayList<>();
    private String confFile;
    

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

    public void setConfFile(String path) {
        this.confFile = path;
    }

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

    @Override
    public String getName() {
        return "Generic Config";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {
        for (ParallelAgent pa : agents) {
            pa.close();
        }
    }
}
