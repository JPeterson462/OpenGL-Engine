package engine.rendering;

import java.util.ArrayList;

import engine.Assets;
import engine.Engine;
import engine.postprocessing.PostProcessor;

public class PostProcessingRenderer {
	
	private ArrayList<PostProcessor> postProcessors = new ArrayList<>();
	
//	private Framebuffer framebuffer;
	
//	private Geometry fullscreenQuad;
	
	public PostProcessingRenderer() {
//		framebuffer = Assets.newFullscreenFramebuffer(1, true);
//		fullscreenQuad = Assets.newFullscreenQuad();
	}
	
	public void addPostProcessor(PostProcessor postProcessor) {
		postProcessors.add(postProcessor);
	}
	
	public void render(Engine engine, FullSceneRender sceneRenderCall) {
//		framebuffer.bind();
		sceneRenderCall.renderScene();
//		framebuffer.unbind();
//		fullscreenQuad.bind();
//		Texture texture = framebuffer.getColorTexture(0);
//		for (int i = 0; i < postProcessors.size(); i++) {
//			PostProcessor postProcessor = postProcessors.get(i);
//			texture.bind(0);
//			postProcessor.bind();
//			fullscreenQuad.renderGeometry();
//			postProcessor.unbind();
//			texture = postProcessor.getColorTexture();
//		}
//		texture.bind(0);
//		fullscreenQuad.renderGeometry();
//		texture.unbind();
//		fullscreenQuad.unbind();
	}

}
