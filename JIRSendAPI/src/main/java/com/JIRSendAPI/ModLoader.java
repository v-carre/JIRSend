package com.JIRSendAPI;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModLoader {
    private static final String MODS_DIR = "mods";

    public List<JIRSendMod> loadMods() {
        List<JIRSendMod> mods = new ArrayList<>();
        File modsDir = new File(MODS_DIR);

        if (modsDir.exists() && modsDir.isDirectory()) {
            File[] modFiles = modsDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (modFiles != null) {
                for (File modFile : modFiles) {
                    try {
                        URL[] urls = { modFile.toURI().toURL() };
                        try (URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader())) {
                            for (Class<?> clazz : findImplementingClasses(classLoader)) {
                                if (JIRSendMod.class.isAssignableFrom(clazz)) {
                                    mods.add((JIRSendMod) clazz.getDeclaredConstructor().newInstance());
                                }
                            }
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
                        System.err.println("Error while loading " + modFile.getName());
                    }
                }
            }
        }
        return mods;
    }

    private List<Class<?>> findImplementingClasses(URLClassLoader classLoader) throws IOException {
        // Implementation for discovering classes within a JAR file
        List<Class<?>> classes = new ArrayList<>();
        URL jarUrl = classLoader.getURLs()[0];
        try (JarFile jarFile = new JarFile(jarUrl.getFile())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace('/', '.')
                            .replace(".class", "");
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                        // Ignore classes that cannot be loaded
                    }
                }
            }
        }
        return classes;
    }
}
