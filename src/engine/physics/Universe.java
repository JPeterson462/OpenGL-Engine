package engine.physics;

import java.util.ArrayList;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import engine.terrain.Terrain;

public class Universe {
	
	private CollisionListener listener;
	
	private Vector3f gravity;
	
	private ArrayList<Body> bodies = new ArrayList<>();
	
	private Terrain terrain;
	
	public Universe(CollisionListener listener, Vector3f gravity, Terrain terrain) {
		this.listener = listener;
		this.gravity = gravity;
		this.terrain = terrain;
	}
	
	public void addBody(Body body) {
		bodies.add(body);
	}
	
	private void zeroOutComponents(Vector3f dest, Vector3f components) {
		if (components.x != 0)
			dest.x = 0;
		if (components.y != 0)
			dest.y = 0;
		if (components.z != 0)
			dest.z = 0;
	}

	public void update(float delta) {
		Quaternionf unit = new Quaternionf(), tmp = new Quaternionf();
		// Move objects
		bodies.forEach(body -> {
			body.getLinearVelocity().fma(delta, gravity);
			body.getPosition().fma(delta, body.getLinearVelocity());
			unit.slerp(body.getAngularVelocity(), delta, tmp);
			body.getOrientation().mul(tmp);
		});
		// Check and resolve collision
		for (int i = 0; i < bodies.size(); i++) {
			Body body = bodies.get(i);
			CollisionBounds bounds = body.getBounds();
			for (int j = 0; j < i; j++) {
				Body other = bodies.get(j);
				Vector3f overlap = bounds.overlaps(other, body);
				if (overlap.dot(overlap) > 0) {
					// Overlap is relative to body, so body.sub and other.add
					float bodyToOther = body.getMass() / (body.getMass() + other.getMass());
					float otherToBody = 1f - bodyToOther;
					if (body.getMass() == Float.MAX_VALUE && other.getMass() == Float.MAX_VALUE)
						continue;
					if (body.getMass() == Float.MAX_VALUE) {
						listener.onCollision(other, body);
						other.getPosition().sub(overlap);
						zeroOutComponents(other.getLinearVelocity(), overlap);
					}
					else if (body.getMass() == Float.MAX_VALUE) {
						listener.onCollision(body, other);
						other.getPosition().add(overlap);
						zeroOutComponents(other.getLinearVelocity(), overlap);
					}
					else {
						if (body.getLinearVelocity().lengthSquared() > other.getLinearVelocity().lengthSquared())
							listener.onCollision(other, body);
						else
							listener.onCollision(body, other);
						other.getPosition().fma(bodyToOther, overlap);
						zeroOutComponents(other.getLinearVelocity(), overlap);
						overlap.negate();
						body.getPosition().fma(otherToBody, overlap);
						zeroOutComponents(body.getLinearVelocity(), overlap);
					}
				}
			}
		}
		for (int i = 0; i < bodies.size(); i++) {
			Body body = bodies.get(i);
			CollisionBounds bounds = body.getBounds();
			float underTerrain = bounds.underneath(body, terrain);
			if (underTerrain > 0) {
				listener.onTerrainCollision(body);
				body.getPosition().add(0, underTerrain, 0);
				body.getLinearVelocity().y = 0;
			}
		}
	}
	
}
