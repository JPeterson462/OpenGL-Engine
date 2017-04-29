package engine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface UniformSetter {
	
	public void uploadMatrix(String uniform, Matrix4f matrix);
	
	public void uploadVector(String uniform, Vector3f vector);
	
	public void uploadFloat(String uniform, float f);
	
	public void uploadInt(String uniform, int i);
	
	public void uploadVector(String uniform, Vector2f vector);
	
	public void uploadVector(String uniform, Vector4f vector);

}
