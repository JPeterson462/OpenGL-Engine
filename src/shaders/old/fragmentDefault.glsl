#version 330

const int MAX_LIGHTS = 4;

uniform sampler2D texture;
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

const int pcfCount = 2;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

void main(void) {
	float texelSize = 1.0 / shadowMapSize;
	float total = 0.0;
	for (int x = -pcfCount; x <= pcfCount; x++) {
		for (int y = -pcfCount; y <= pcfCount; y++) {
			float objectNearestLight = texture2D(shadowMap, pass_ShadowCoords.xy + vec2(x, y) * texelSize).r;
			if (pass_ShadowCoords.z > objectNearestLight + 0.002) {
				total += 1.0;
			}
		}
	}
	total /= totalTexels;
	float lightFactor = 1.0 - (total * pass_ShadowCoords.w);
	
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
	vec4 textureColor = texture2D(texture, pass_TexCoord, -1.0);
	if (textureColor.a < 0.5) discard;
	out_Color = (vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0));
	out_Color = mix(vec4(skyColor,1.0), out_Color, pass_Visibility);
}