package engine.physics;

import org.joml.Vector3f;

import engine.terrain.Terrain;
import utils.MathUtils;

public class CubeCollisionBounds implements CollisionBounds {
	
	private Vector3f half;
	
	public CubeCollisionBounds(Vector3f size) {
		half = new Vector3f(size).mul(0.5f);
	}

	@Override
	public Vector3f overlaps(Body other, Body current) {
		if (other.getBounds() instanceof CubeCollisionBounds) {
			CubeCollisionBounds otherBounds = (CubeCollisionBounds) other.getBounds();
			Vector3f thisMin = new Vector3f(), thisMax = new Vector3f();
			Vector3f otherMin = new Vector3f(), otherMax = new Vector3f();
			current.getPosition().sub(half, thisMin);
			current.getPosition().add(half, thisMax);
			other.getPosition().sub(otherBounds.half, otherMin);
			other.getPosition().add(otherBounds.half, otherMax);
			return new Vector3f(otherMax).sub(thisMin);
		}
		return null;
	}

	@Override
	public float underneath(Body current, Terrain terrain) {
		Vector3f bottom0 = new Vector3f(current.getPosition().x - half.x, current.getPosition().y - half.y, current.getPosition().z - half.z);
		Vector3f bottom1 = new Vector3f(current.getPosition().x + half.x, current.getPosition().y - half.y, current.getPosition().z - half.z);
		Vector3f bottom2 = new Vector3f(current.getPosition().x + half.x, current.getPosition().y - half.y, current.getPosition().z + half.z);
		Vector3f bottom3 = new Vector3f(current.getPosition().x - half.x, current.getPosition().y - half.y, current.getPosition().z + half.z);
		float bottom0dy = terrain.getHeightAt(bottom0.x, bottom0.z) - bottom0.y;
		float bottom1dy = terrain.getHeightAt(bottom1.x, bottom1.z) - bottom1.y;
		float bottom2dy = terrain.getHeightAt(bottom2.x, bottom2.z) - bottom2.y;
		float bottom3dy = terrain.getHeightAt(bottom3.x, bottom3.z) - bottom3.y;
		return MathUtils.max(bottom0dy, bottom1dy, bottom2dy, bottom3dy);
	}

}
