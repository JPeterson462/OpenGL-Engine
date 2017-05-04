#version 330

uniform sampler2D sceneDepthMap;
uniform sampler2D normalMap;
uniform sampler2D diffuseMap;
uniform sampler2D materialMap;

uniform sampler2D lightDiffuseMap;
uniform sampler2D lightDirectionMap0;
uniform sampler2D lightDirectionMap1;

in vec2 pass_TexCoord;

layout (location = 0) out vec4 out_Color;

void main(void) {
	float sceneDepth = texture2D(sceneDepthMap, pass_TexCoord).r;
	vec3 normal = texture2D(normalMap, pass_TexCoord).rgb * 2.0 - 1.0;
	vec4 sceneDiffuse = texture2D(diffuseMap, pass_TexCoord);
	vec4 material = texture2D(materialMap, pass_TexCoord);
	float materialShineDamper = material.r;
	float materialReflectivity = material.g;
	vec4 lightDiffuse = texture2D(lightDiffuseMap, pass_TexCoord);
	vec4 toCamera = texture2D(lightDirectionMap0, pass_TexCoord);
	vec4 toLight = texture2D(lightDirectionMap1, pass_TexCoord);
	
	vec3 unitNormal = normalize(normal);
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	float distance = toLight.w;
	float attenuation = lightDiffuse.w;
	vec3 unitLightVector = toLight.xyz;
	float brightness = max(dot(unitNormal, unitLightVector), 0.0);
	vec3 diffuse = brightness * lightDiffuse.xyz;
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	vec3 unitCameraVector = toCamera.xyz;
	float specularFactor = max(dot(reflectedLightDirection, unitCameraVector), 0.0);
	float dampedFactor = pow(specularFactor, materialShineDamper);
	vec3 specular = dampedFactor * materialReflectivity * lightDiffuse.xyz;
	totalDiffuse = diffuse * attenuation;
	totalSpecular = specular * attenuation;
	
	out_Color = texture2D(diffuseMap, pass_TexCoord) * vec4(totalDiffuse + totalSpecular, 1.0);
}