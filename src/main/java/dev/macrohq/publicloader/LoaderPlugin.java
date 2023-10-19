package dev.macrohq.publicloader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.ModListHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class LoaderPlugin implements IFMLLoadingPlugin {

    static {
        try {
            JsonObject modInfo = getModInfo();
            String modId = modInfo.get("modId").getAsString();
            String md5 = modInfo.get("md5").getAsString();
            String url = modInfo.get("url").getAsString();
            File file = new File(System.getProperty("java.io.tmpdir") + "/MacroHQ_PublicLoader_Mod_" + modId + ".jar");
            if (!file.exists() || doesNotMatchMD5(file, md5)) {
                if (!downloadFile(url, file)) throw new RuntimeException("Failed to download mod!");
                if (doesNotMatchMD5(file, md5))
                    throw new RuntimeException("MD5 hash of downloaded mod does not match!");
            }
            loadMod(file);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load mod.", e);
        }
    }

    private static JsonObject getModInfo() throws IOException {
        try(InputStream is = LoaderPlugin.class.getProtectionDomain().getCodeSource().getLocation().openStream()) {
            URL modInfoURL = new URL(new Manifest(is).getMainAttributes().get("ModInfoURL").toString());
            return new Gson().fromJson(IOUtils.toString(modInfoURL), JsonObject.class);
        }
    }

    private static boolean doesNotMatchMD5(File file, String hash) throws IOException {
        return !hash.equals(DigestUtils.md5Hex(new BufferedInputStream(Files.newInputStream(file.toPath()))));
    }

    private static boolean downloadFile(String url, File destination) throws IOException {
        if (!destination.getParentFile().mkdirs()) return false;
        Files.copy(new URL(url).openStream(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

    private static void loadMod(File file) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        URL url = file.toURI().toURL();
        Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        m.setAccessible(true);
        m.invoke(Launch.classLoader.getClass().getClassLoader(), url);
        Launch.classLoader.addURL(url);
        String tweaker;
        try (JarFile jar = new JarFile(file)) {
            tweaker = jar.getManifest().getMainAttributes().getValue("TweakClass");
        }
        if (tweaker != null) {
            @SuppressWarnings("unchecked")
            List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses");
            tweakClasses.add(tweaker);
        }
        ModListHelper.additionalMods.put(file.getName(), file);
    }

    @Override public String[] getASMTransformerClass() {return new String[0];}
    @Override public String getModContainerClass() {return null;}
    @Override public String getSetupClass() {return null;}
    @Override public void injectData(Map<String, Object> map) {}
    @Override public String getAccessTransformerClass() {return null;}
}