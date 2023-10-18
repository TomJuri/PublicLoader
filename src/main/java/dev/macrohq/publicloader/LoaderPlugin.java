package dev.macrohq.publicloader;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoaderPlugin implements IFMLLoadingPlugin {

    static {
        String modId = LoaderPlugin.class.getTypeName().split("\\.")[2].replace("loader", "");
        String mhqFileName = "/" + modId + ".mhq";
        URL modInfoURL;
        if(LoaderPlugin.class.getResource(mhqFileName) == null) {
            throw new RuntimeException("Unable to find mhq file for mod " + modId + ".", e);
        }
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(LoaderPlugin.class.getResourceAsStream(mhqFileName))))) {
            modInfoURL = new URL(br.readLine());
        } catch (Exception e) {
            throw new RuntimeException("Unable to read mhq file for mod " + modId + ".", e);
        }
        try {


            URL url = new File("/home/tom/Downloads/SkySkipped-3.6.1.jar").toURI().toURL();
            Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            m.setAccessible(true);
            m.invoke(Launch.classLoader.getClass().getClassLoader(), url);
            Launch.classLoader.addURL(url);
            @SuppressWarnings("unchecked")
            List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses");
            tweakClasses.add("me.cephetir.bladecore.loader.BladeCoreTweaker");
        } catch (Exception e) {
            throw new RuntimeException("Unable to load mod.", e);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
