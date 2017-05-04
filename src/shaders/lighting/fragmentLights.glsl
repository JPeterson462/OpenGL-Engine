#version 150

uniform sampler2D sceneDepthMap;
uniform sampler2D normalMap;
uniform sampler2D diffuseMap;
uniform sampler2D materialMap;

uniform vec4 ambientLight;
uniform vec4 lightColor;

in float pass_FadeFactor;
in float pass_Directional;
in vec3 pass_ToCameraVector;
in vec3 pass_ToLightVector;
in vec2 pass_TexCoord;
in float pass_Depth;

out vec4 out_Color;

// http://sunandblackcat.com/tipFullView.php?l=eng&topicid=30&topic=Phong-Lighting

vec4 calculateAmbient(vec4 C, vec4 Ca) {
	return C * Ca;
}

vec4 calculateDiffuse(vec3 N, vec3 L, vec4 C) {
	float Ia = clamp(dot(N, L), 0.0, 1.0);
	return Ia * C;
}

vec4 calculateSpecular(vec3 N, vec3 L, vec3 V, float shininess, vec4 C) {
	vec3 H = normalize(L + V);
	float Fs = max(dot(N, L), 0.0);
	float Is = pow(clamp(dot(N, H), 0.0, 1.0), shininess);
	return Is * Fs * C;
	// TODO: shine damper
}

vec4 calculateLighting(vec3 N, vec3 L, vec3 V, float shininess, vec4 C, vec4 Ca) {
	return calculateDiffuse(N, L, C) + calculateSpecular(N, L, V, shininess, C) + calculateAmbient(C, Ca);
}

void main(void) {
	vec3 N = texture2D(normalMap, pass_TexCoord).rgb * 2.0 - 1.0;
	vec3 L = pass_ToLightVector;
	vec3 V = pass_ToCameraVector;
	vec4 C = texture2D(diffuseMap, pass_TexCoord);
	vec4 Ca = ambientLight;
	float shininess = texture2D(materialMap, pass_TexCoord).g;
	out_Color = calculateLighting(N, L, V, shininess, C, Ca) * pass_FadeFactor * lightColor;
	
	out_Color = calculateDiffuse(N, L, C) * lightColor;
/*
	float sceneDepth = texture2D(sceneDepthMap, pass_TexCoord).r;
	vec3 normal = texture2D(normalMap, pass_TexCoord).rgb * 2.0 - 1.0;
	vec4 sceneDiffuse = texture2D(diffuseMap, pass_TexCoord);
	vec4 material = texture2D(materialMap, pass_TexCoord);
	float materialShineDamper = material.r;
	float materialReflectivity = material.g;
	vec3 unitCameraVector = normalize(pass_ToCameraVector);
	vec3 unitLightVector = normalize(pass_ToLightVector);
	if (pass_Directional == 0) {
		out_Color = sceneDiffuse * lightColor;
	} else {
		vec3 unitNormal = normalize(normal);
		vec3 totalDiffuse = vec3(0.0);
		vec3 totalSpecular = vec3(0.0);
		float distance = length(pass_ToLightVector);
		float attenuation = pass_FadeFactor;
		float brightness = max(dot(unitNormal, unitLightVector), 0.0);
		vec3 diffuse = brightness * lightColor.xyz;
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = max(dot(reflectedLightDirection, unitCameraVector), 0.0);
		float dampedFactor = pow(specularFactor, materialShineDamper);
		vec3 specular = dampedFactor * materialReflectivity * lightColor.xyz;
		//out_Color = sceneDiffuse * vec4(diffuse + specular, attenuation);
		out_Color = sceneDiffuse * vec4(diffuse, attenuation);
	}
*/
}