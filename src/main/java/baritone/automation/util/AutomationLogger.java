/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.automation.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutomationLogger {
    private static final String LOG_FILE = "logs/automation.log";
    private static AutomationLogger INSTANCE;
    private final PrintWriter logWriter;
    private final SimpleDateFormat dateFormat;

    private AutomationLogger() {
        PrintWriter writer = null;
        try {
            File logFile = new File(LOG_FILE);
            logFile.getParentFile().mkdirs();
            writer = new PrintWriter(new FileWriter(logFile, true), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.logWriter = writer;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static AutomationLogger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutomationLogger();
        }
        return INSTANCE;
    }

    public void info(String message) {
        log("INFO", message, TextFormatting.WHITE);
    }

    public void success(String message) {
        log("SUCCESS", message, TextFormatting.GREEN);
    }

    public void warning(String message) {
        log("WARN", message, TextFormatting.YELLOW);
    }

    public void error(String message) {
        log("ERROR", message, TextFormatting.RED);
    }

    private void log(String level, String message, TextFormatting color) {
        String timestamp = dateFormat.format(new Date());
        String logMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        // Schreibe in Datei
        if (logWriter != null) {
            logWriter.println(logMessage);
        }

        // Zeige im Chat wenn im Spiel
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendMessage(
                new StringTextComponent(color + message)
            );
        }
    }

    public void close() {
        if (logWriter != null) {
            logWriter.close();
        }
    }
}
