package engine.terrain;

import com.esotericsoftware.minlog.Log;

import engine.rendering.Material;
import engine.rendering.Shader;
import engine.rendering.Texture;

public class TerrainTexturePack {
	
	private Texture[] textures;
	
	private Material baseMaterial;
	
	public TerrainTexturePack(float reflectivity, Texture...textureSet) {
		baseMaterial = new Material(textureSet[0]);
		baseMaterial.setReflectivity(reflectivity);
		textures = new Texture[textureSet.length - 1];
		if (textures.length != 4) {
			Log.warn("No handling for texture packs of size " + textureSet.length + ". Behaviour undefined.");
		}
		System.arraycopy(textureSet, 1, textures, 0, textures.length);
	}
	
	public void bind(Shader shader) {
		shader.uploadFloat("materialShineDamper", baseMaterial.getShineDamper());
		shader.uploadFloat("materialReflectivity", baseMaterial.getReflectivity());
		shader.uploadInt("backgroundTexture", 0);
		shader.uploadInt("rTexture", 1);
		shader.uploadInt("gTexture", 2);
		shader.uploadInt("bTexture", 3);
		shader.uploadInt("blendMapTexture", 4);
		baseMaterial.getDiffuseTexture().bind(0);
		for (int i = 0; i < textures.length; i++) {
			textures[i].bind(i + 1);
		}
	}
	
	public void unbind() {
		baseMaterial.getDiffuseTexture().unbind();
		for (int i = 0; i < textures.length; i++) {
			textures[i].unbind();
		}
	}

}
