package engine.rendering;

import org.joml.Vector4f;

@FunctionalInterface
public interface PlaneRender {
	
	public void renderPlane(Vector4f plane, boolean sendViewMatrix);

}
