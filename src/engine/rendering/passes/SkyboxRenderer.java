package engine.rendering.passes;

import engine.Camera;
import engine.Engine;
import engine.rendering.Shader;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SkyboxRenderer {

	private Shader shader;

	private float rotation = 0;
	
	private float blendFactor = 0;
	
	private float time;
	
	private static final float DAY_NIGHT_CYCLE = 60;
	
	private static Vector3f sky0000 = new Vector3f(0, 0, 0);
	
	private static Vector3f sky1200 = new Vector3f();

	public SkyboxRenderer(Shader shader, Engine engine) {
		this.shader = shader;
	}
	
	public void update(float delta) {
		rotation += Math.toRadians(1) * delta;
		time += delta;
		
	}

	public void render(Camera camera) {
		//TODO
	}

}
