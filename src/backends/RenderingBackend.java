package backends;

import java.io.InputStream;
import java.util.ArrayList;

import engine.Engine;
import engine.Settings;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.rendering.Framebuffer;
import engine.rendering.Geometry;
import engine.rendering.InstancedGeometry;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import engine.rendering.VertexTemplate;

public interface RenderingBackend {
	
	public void checkError();
	
	public void setCulling(boolean enabled);
	
	public void setDepth(boolean enabled);
	
	public void setDepthBuffer(boolean enabled);
	
	public void setBlending(boolean enabled);
	
	public void setAdditiveBlending(boolean enabled);
	
	public void setBackgroundColor(float red, float green, float blue);
	
	public void createDisplay(Settings settings, Keyboard keyboard, Mouse mouse, Engine engine);
	
	public void showDisplay();
	
	public boolean isOpen();
	
	public void prepareContext();
	
	public Geometry createGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indexList, boolean isStatic);
	
	public InstancedGeometry createInstancedGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indexList, int instancedDataLength, int dimensions);
	
	public void updateGeometry(Geometry geometry, ArrayList<Vertex> vertices, ArrayList<Integer> indexList);
	
	public Shader createShader(InputStream fragment, InputStream vertex, VertexTemplate vertices);

	public Shader createInstancedShader(InputStream fragment, InputStream vertex, int[] attributes, String[] names);
	
	public Texture createTexture(InputStream stream, boolean mipmapAlways, boolean clampEdges);
	
	public Texture createCubemap(InputStream[] stream);
	
	public Framebuffer createFramebuffer(int width, int height, int colorAttachments, boolean hasDepthBuffer);
	
	public void updateContext();
	
	public void hideDisplay();
	
	public void destroyDisplay();

}
