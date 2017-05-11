#version 330

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMapTexture;

uniform float materialShineDamper;
uniform float materialReflectivity;

in vec2 pass_TexCoord;
in vec3 pass_SurfaceNormal;

layout (location = 0) out vec4 out_Color0;
layout (location = 0) out vec4 out_Color1;
layout (location = 0) out vec4 out_Color2;

vec4 computeTerrainColor() {
	vec4 blendMapColor = texture2D(blendMapTexture, pass_TexCoord);
	float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
	vec2 tiledCoords = pass_TexCoord * 75.0;
	vec4 backgroundTextureColor = texture2D(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColor = texture2D(rTexture, tiledCoords) * blendMapColor.r;
	vec4 gTextureColor = texture2D(gTexture, tiledCoords) * blendMapColor.g;
	vec4 bTextureColor = texture2D(bTexture, tiledCoords) * blendMapColor.b;
	vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;
	return totalColor;
}

void main(void) {
	out_Color0 = computeTerrainColor();
	out_Color1 = vec4(normalize(pass_SurfaceNormal) * 0.5 + 0.5, 1.0);
	out_Color2 = vec4(materialShineDamper, materialReflectivity, 0.0, 0.0);
}