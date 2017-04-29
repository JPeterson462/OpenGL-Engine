package backends.opengl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.lwjgl.glfw.GLFW;

import com.esotericsoftware.minlog.Log;

import engine.input.Key;
import engine.input.MouseButton;

public class GLFWInput {
	
	public static void attachKeys() {
		Field[] fields = Key.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (!Modifier.isStatic(field.getModifiers())) continue;
			field.setAccessible(true);
			String glfwFieldName = "GLFW_KEY_" + field.getName();
			if (field.getName().contains("NUM_") && !field.getName().equals("NUM_LOCK")) {
				glfwFieldName = "GLFW_KEY_" + field.getName().substring(4);
			}
			try {
				field.set(null, new Key(GLFW.class.getDeclaredField(glfwFieldName).getInt(null)));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				Log.error("Failed to create GLFW Key", e);
			}
		}
	}
	
	public static void attachMouseButtons() {
		Field[] fields = MouseButton.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (!Modifier.isStatic(field.getModifiers())) continue;
			field.setAccessible(true);
			String glfwFieldName = "GLFW_MOUSE_BUTTON_" + field.getName();
			if (field.getName().contains("BUTTON_")) {
				glfwFieldName = "GLFW_MOUSE_" + field.getName();
			}
			try {
				field.set(null, new MouseButton(GLFW.class.getDeclaredField(glfwFieldName).getInt(null)));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				Log.error("Failed to create GLFW mouse button", e);
			}
		}
	}

}
