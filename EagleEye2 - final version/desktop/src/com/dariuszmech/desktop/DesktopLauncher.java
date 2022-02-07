package com.dariuszmech.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dariuszmech.EagleEyeCG;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.resizable = false;
		config.width = EagleEyeCG.WINDOW_WIDTH;
		config.height = EagleEyeCG.WINDOW_HEIGHT;
		config.title = "Eagle Eye - Sokoli Wzrok";

		new LwjglApplication(new EagleEyeCG(), config);
	}
}
