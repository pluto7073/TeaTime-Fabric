package ml.pluto7073.teatime.utils;

import ml.pluto7073.teatime.TeaTime;
import ml.pluto7073.teatime.gui.VersionWarningScreen;
import net.minecraft.client.MinecraftClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class VersionChecker {

    private static final String VERSION_LINK = "https://pluto7073.github.io/files/tea-version-number.txt";
    private static boolean outdated;
    private static boolean screenShown = false;

    public static void checkOutdated() {
        try (BufferedInputStream bis = new BufferedInputStream(new URL(VERSION_LINK).openStream())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(bis));
            int recentVersion = Integer.parseInt(reader.readLine());
            reader.close();
            bis.close();
            outdated = recentVersion > TeaTime.MOD_VERSION;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isOutdated() {
        return outdated;
    }

    public static void showWarningScreen() {
        if (screenShown) return;
        screenShown = true;
        if (!isOutdated()) return;
        MinecraftClient.getInstance().setScreen(new VersionWarningScreen());
    }

}
