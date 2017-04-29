#version 330

const int MAX_LIGHTS = 4;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMapTexture;

uniform vec3 lightColor[MAX_LIGHTS];

uniform float materialShineDamper;
uniform float materialReflectivity;

uniform vec3 attenuation[MAX_LIGHTS];

uniform vec3 skyColor;

in vec2 pass_TexCoord;
in vec3 pass_SurfaceNormal;
in vec3 pass_ToLightVector[MAX_LIGHTS];
in vec3 pass_ToCameraVector;
in float pass_Visibility;

out vec4 out_Color;

void main(void) {
	vec4 blendMapColor = texture2D(blendMapTexture, pass_TexCoord);
	float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
	vec2 tiledCoords = pass_TexCoord * 75.0;
	vec4 backgroundTextureColor = texture2D(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColor = texture2D(rTexture, tiledCoords) * blendMapColor.r;
	vec4 gTextureColor = texture2D(gTexture, tiledCoords) * blendMapColor.g;
	vec4 bTextureColor = texture2D(bTexture, tiledCoords) * blendMapColor.b;
	vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;
	
	vec3 unitNormal = normalize(pass_SurfaceNormal);
	vec3 unitCameraVector = normalize(pass_ToCameraVector);
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	for (int i = 0; i < MAX_LIGHTS; i++) {
		float distance = length(pass_ToLightVector[i]);
		float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		vec3 unitLightVector = normalize(pass_ToLightVector[i]);
		float nDotl = dot(unitNormal, unitLightVector);
		float brightness = max(nDotl, 0.0);
		vec3 diffuse = brightness * lightColor[i];
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitCameraVector);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, materialShineDamper);
		vec3 specular = dampedFactor * materialReflectivity * lightColor[i];
		totalDiffuse += diffuse / attenuationFactor;
		totalSpecular += specular / attenuationFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.2);
	out_Color = vec4(totalDiffuse, 1.0) * totalColor + vec4(totalSpecular, 1.0);
	out_Color = mix(vec4(skyColor,1.0), out_Color, pass_Visibility);
}