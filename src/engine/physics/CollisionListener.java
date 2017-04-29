package engine.physics;

public interface CollisionListener {
	
	public void onCollision(Body b1, Body b2);
	
	public void onTerrainCollision(Body b1);

}
