package engine.postprocessing;

import engine.rendering.Texture;

public interface PostProcessor {
	
	public void bind();
	
	public void unbind();
	
	public Texture getColorTexture();

}
