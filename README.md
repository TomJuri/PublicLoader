# PublicLoader
PublicLoader is a free and open source mod to help you always load the latest version of your mod into the user's client.
[![Release](https://jitpack.io/v/Macro-HQ/PublicLoader.svg)](https://jitpack.io/#Macro-HQ/PublicLoader)
## How to set up?
1. Add the ShadowJar plugin to your plugins if you haven't already.
```kotlin
plugins {
    [...]
    id("com.github.johnrengelman.shadow") version "8.1.1"
}
```

2. Add the jitpack repository to your repositories if you haven't already.
```kotlin
repositories {
    [...]
    maven("https://jitpack.io")
}
```

3. Create a new configuration called loader
```kotlin
[...]
val loader: Configuration by configurations.creating
[...]
```

4. Add the latest version of the loader to your dependencies
```kotlin
dependencies {
    [...]
    loader("com.github.Macro-HQ:PublicLoader:<VERSION>")
}
```

5. Create a new task called "loaderJar" like this:
```kotlin
tasks {
    [...]
    register<ShadowJar>("loaderJar") {
        archiveClassifier.set("loader")
        configurations = listOf(loader)
        manifest.attributes(mapOf(
            "ModInfoURL" to "<RAW URL TO YOUR MOD INFO FILE>",
            "FMLCorePlugin" to "dev.macrohq.publicloader.<YOURMODNAME>.LoaderPlugin"
        ))
        relocate("dev.macrohq.publicloader", "dev.macrohq.publicloader.<YOURMODNAME>")
    }
    build.get().dependsOn(named("loaderJar"))
}
```

5. Create a mod info somewhere in your repository and add the following:
```json
{
  "modId": "<YOUR MOD ID>",
  "md5": "<MD5 OF YOUR MOD JAR>",
  "url": "<RAW URL TO YOUR MOD JAR>"
}
```

And done. When you run the gradle build task a modname-loader.jar will be created in your build/libs folder. This is the file you can share with your users.
Make sure that you update the mod info file every time you update your mod and that you always provide raw urls to the files, so that if you were to open them in your browser 
it would show the text without any html rendering or download the file directly.