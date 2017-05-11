#version 330 core

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D depthMap;
uniform vec2 viewPlane;

uniform float moveFactor;

in vec4 pass_ClipSpace;
in vec2 pass_TexCoord;

out vec4 out_Color;

const float waveStrength = 0.04;
const float shineDamper = 20.0;
const float reflectivity = 0.5;
const float depthFactor = 5.0;

void main(void) {
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
	out_Color = mix(reflectColor, refractColor, 0.5);
	out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.2);
	//out_Color.a = clamp(waterDepth / depthFactor, 0.0, 1.0);
	out_Color.a = clamp(waterDepth / depthFactor, -0.01, 0.99) + 0.01;
}