package engine.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Body {
	
	private Vector3f position, linearVelocity;
	
	private Quaternionf orientation, angularVelocity;
	
	private CollisionBounds bounds;
	
	private float mass;
	
	public Body(CollisionBounds bounds, Vector3f position, Quaternionf orientation, float mass) {
		this.position = new Vector3f(position);
		linearVelocity = new Vector3f();
		this.orientation = new Quaternionf(orientation);
		angularVelocity = new Quaternionf();
		this.bounds = bounds;
		this.mass = mass;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getLinearVelocity() {
		return linearVelocity;
	}

	public Quaternionf getOrientation() {
		return orientation;
	}

	public Quaternionf getAngularVelocity() {
		return angularVelocity;
	}

	public CollisionBounds getBounds() {
		return bounds;
	}
	
	public float getMass() {
		return mass;
	}

}
