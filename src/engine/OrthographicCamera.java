package engine;

import java.util.Stack;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;

public class OrthographicCamera extends Camera {
	
	private float width, height;
	
	private Stack<Matrix4f> viewMatrixStack = new Stack<>();
	
	public OrthographicCamera(float width, float height) {
		this.width = width;
		this.height = height;
		update();
	}
	
	public void pushMatrix() {
		viewMatrixStack.push(getViewMatrix());
	}
	
	public void popMatrix() {
		if (!viewMatrixStack.isEmpty())
			getViewMatrix().set(viewMatrixStack.pop());
	}
	
	public void translate(Vector2f offset) {
		getViewMatrix().translate(offset.x, offset.y, 0);
	}

	public void rotate(Quaternionf rotation) {
		getViewMatrix().rotate(rotation);
	}
	
	public void scale(Vector2f scale) {
		getViewMatrix().scale(scale.x, scale.y, 1);
	}
	
	@Override
	public void newProjectionMatrix() {
		getProjectionMatrix().identity().ortho2D(0, width, height, 0);
	}

	@Override
	public void newViewMatrix() {
		getViewMatrix().identity();
	}

}
