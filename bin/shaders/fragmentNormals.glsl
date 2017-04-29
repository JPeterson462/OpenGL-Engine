#version 330

const int MAX_LIGHTS = 4;

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;

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
	vec4 normalMapValue = 2.0 * texture2D(normalTexture, pass_TexCoord, -1.0) - 1.0;
	vec3 unitNormal = normalize(normalMapValue.rgb);
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
	vec4 textureColor = texture2D(diffuseTexture, pass_TexCoord, -1.0);
	if (textureColor.a < 0.5) discard;
	out_Color = (vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0));
	out_Color = mix(vec4(skyColor,1.0), out_Color, pass_Visibility);
}