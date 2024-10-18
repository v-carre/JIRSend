````bash
mvn compile
mvn -X compile #????

archlinux-java status
sudo archlinux-java set java-17-openjdk
```

```json
"java.configuration.runtimes": [
    {
        "name": "JavaSE-1.8",
        "path": "/usr/lib/jvm/java-8-openjdk",
        "default": false
    },
    {
        "name": "JavaSE-17",
        "path": "/usr/lib/jvm/java-17-openjdk",
        "default": true
    }
],
"java.home": "/usr/lib/jvm/java-17-openjdk/",
"jdk.jdkhome": "/usr/lib/jvm/java-17-openjdk",
"java.import.gradle.java.home": "/usr/lib/jvm/java-17-openjdk",
"[cpp]": {
    "editor.wordBasedSuggestions": "off",
    "editor.suggest.insertMode": "replace",
    "editor.semanticHighlighting.enabled": true
},
"extensions.ignoreRecommendations": true,
"cmake.pinnedCommands": [
    "workbench.action.tasks.configureTaskRunner",
    "workbench.action.tasks.runTask"
],
"java.import.gradle.home": "",
"java.import.gradle.user.home": "",
"java.jdt.ls.java.home": "/usr/lib/jvm/java-17-openjdk"
}
```