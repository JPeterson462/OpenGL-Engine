package engine.rendering;

public class Material {
	
	private Texture diffuseTexture, normalTexture;
	
	private float shineDamper, reflectivity;
	
	public static final float MAX_REFLECTIVITY = 1, NO_REFLECTIVITY = 0;

	public Material(Texture diffuseTexture, float shineDamper, float reflectivity) {
		this.diffuseTexture = diffuseTexture;
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
	}

	public Material(Texture diffuseTexture, Texture normalTexture, float shineDamper, float reflectivity) {
		this.diffuseTexture = diffuseTexture;
		this.normalTexture = normalTexture;
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
	}

	public Material(Texture diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
		this.shineDamper = 10;
		this.reflectivity = 1;
	}

	public Material(Texture diffuseTexture, Texture normalTexture) {
		this.diffuseTexture = diffuseTexture;
		this.normalTexture = normalTexture;
		this.shineDamper = 10;
		this.reflectivity = 1;
	}

	public Texture getDiffuseTexture() {
		return diffuseTexture;
	}
	
	public Texture getNormalTexture() {
		return normalTexture;
	}
	
	public boolean hasNormalTexture() {
		return normalTexture != null;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

}
