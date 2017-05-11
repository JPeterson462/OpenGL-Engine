#version 330

const int MAX_LIGHTS = 4;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMapTexture;
uniform sampler2D shadowMap;

uniform vec3 lightColor[MAX_LIGHTS];

uniform float materialShineDamper;
uniform float materialReflectivity;

uniform float ambientLightFactor;

uniform vec3 attenuation[MAX_LIGHTS];

uniform vec3 skyColor;

uniform float shadowMapSize;

in vec2 pass_TexCoord;
in vec3 pass_SurfaceNormal;
in vec3 pass_ToLightVector[MAX_LIGHTS];
in vec3 pass_ToCameraVector;
in float pass_Visibility;
in vec4 pass_ShadowCoords;

out vec4 out_Color;

#define SAMPLES 8
const float angle = 0;
const vec2 offsets[SAMPLES] = vec2[SAMPLES](
	vec2(sin(angle + 0 * 180 / SAMPLES), cos(angle + 0 * 180 / SAMPLES)),
	vec2(sin(angle + 1 * 180 / SAMPLES), cos(angle + 1 * 180 / SAMPLES)),
	vec2(sin(angle + 2 * 180 / SAMPLES), cos(angle + 2 * 180 / SAMPLES)),
	vec2(sin(angle + 3 * 180 / SAMPLES), cos(angle + 3 * 180 / SAMPLES)),
	vec2(sin(angle + 4 * 180 / SAMPLES), cos(angle + 4 * 180 / SAMPLES)),
	vec2(sin(angle + 5 * 180 / SAMPLES), cos(angle + 5 * 180 / SAMPLES)),
	vec2(sin(angle + 6 * 180 / SAMPLES), cos(angle + 6 * 180 / SAMPLES)),
	vec2(sin(angle + 7 * 180 / SAMPLES), cos(angle + 7 * 180 / SAMPLES))
);

float rand(vec2 co){
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

float shadow(vec3 coords) {
	float filterRadius = 1 / shadowMapSize;
	float passed = 0;
	float normalized = rand(coords.xy);
	for (int i = 0; i < SAMPLES; i++) {
		passed += texture2D(shadowMap, coords.xy + offsets[i] * filterRadius * normalized).r;
		passed += texture2D(shadowMap, coords.xy - offsets[i] * filterRadius * normalized).r;
	}
	return passed * (0.5 / SAMPLES);
}

void main(void) {	
	float lightFactor = shadow(pass_ShadowCoords.xyz) * pass_ShadowCoords.w;
	
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
	totalDiffuse = max(totalDiffuse * lightFactor, ambientLightFactor);
	out_Color = vec4(totalDiffuse, 1.0) * totalColor + vec4(totalSpecular, 1.0);
	out_Color = mix(vec4(skyColor,1.0), out_Color, pass_Visibility);
}