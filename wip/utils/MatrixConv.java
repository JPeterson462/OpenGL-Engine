package utils;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class MatrixConv {
	
	private static FloatBuffer convBuf = BufferUtils.createFloatBuffer(16);
	
	public static org.lwjgl.util.vector.Matrix4f convert(org.joml.Matrix4f matrix) {
		convBuf.position(0);
		matrix.get(convBuf);
		return (org.lwjgl.util.vector.Matrix4f) new org.lwjgl.util.vector.Matrix4f().load(convBuf);
	}

}
