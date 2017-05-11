#version 330 core

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;
uniform sampler2D shadowMap;
uniform vec3 lightColor;
uniform vec2 viewPlane;

uniform float moveFactor;

uniform float shadowMapSize;

in vec4 pass_ClipSpace;
in vec2 pass_TexCoord;
in vec3 pass_ToCameraVector;
in vec3 pass_FromLightVector;
in vec4 pass_ShadowCoords;

out vec4 out_Color;

const float waveStrength = 0.04;
const float shineDamper = 20.0;
const float reflectivity = 0.5;
const float depthFactor = 5.0;

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
	float lightFactor = 1.0 - (total * pass_ShadowCoords.w * 0.25); // Only render the shadow partially (not a solid object)
	
	vec2 ndc = (pass_ClipSpace.xy / pass_ClipSpace.w) / 2.0 + 0.5;
	vec2 refractTexCoord = vec2(ndc.x, ndc.y);
	vec2 reflectTexCoord = vec2(ndc.x, -ndc.y);
	float depth = texture2D(depthMap, refractTexCoord).r;
	float near = viewPlane.x;
	float far = viewPlane.y;
	float floorDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
	depth = gl_FragCoord.z;
	float waterDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
	float waterDepth = floorDistance - waterDistance;
	vec2 distortedTexCoord = texture2D(dudvMap, vec2(pass_TexCoord.x + moveFactor, pass_TexCoord.y)).rg * 0.1;
	distortedTexCoord = pass_TexCoord + vec2(distortedTexCoord.x, distortedTexCoord.y + moveFactor);
	vec2 distortion = (texture2D(dudvMap, distortedTexCoord).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth / 5.0, 0.0, 1.0);
	refractTexCoord += distortion;
	refractTexCoord = clamp(refractTexCoord, 0.001, 0.999);
	reflectTexCoord += distortion;
	reflectTexCoord.x = clamp(reflectTexCoord.x, 0.001, 0.999);
	reflectTexCoord.y = clamp(reflectTexCoord.y, -0.999, -0.001);
	vec4 reflectColor = texture2D(reflectionTexture, reflectTexCoord);
	vec4 refractColor = texture2D(refractionTexture, refractTexCoord);
	vec4 normalMapColor = texture2D(normalMap, distortedTexCoord);
	vec3 normal = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b * 3.0, normalMapColor.g * 2.0 - 1.0);
	normal = normalize(normal);
	vec3 viewVector = normalize(pass_ToCameraVector);
	float refractiveFactor = dot(viewVector, normal);
	refractiveFactor = pow(refractiveFactor, 0.5);
	vec3 reflectedLight = reflect(normalize(pass_FromLightVector), normal);
	float specular = max(dot(reflectedLight, viewVector), 0.0);
	specular = pow(specular, shineDamper);
	vec3 specularHighlights = lightColor * specular * reflectivity * clamp(waterDepth / 5.0, 0.0, 1.0);
	out_Color = mix(reflectColor, refractColor, refractiveFactor);
	out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.2) + vec4(specularHighlights, 0.0);
	//out_Color.a = clamp(waterDepth / depthFactor, 0.0, 1.0);
	out_Color.a = clamp(waterDepth / depthFactor, -0.01, 0.99) + 0.01;
	out_Color = out_Color * vec4(vec3(lightFactor), 1.0);
}