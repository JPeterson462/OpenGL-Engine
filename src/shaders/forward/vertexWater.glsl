#version 330 core

in vec3 in_Position;

out vec4 pass_ClipSpace;
out vec2 pass_TexCoord;
out vec3 pass_ToCameraVector;
out vec3 pass_FromLightVector;
out vec4 pass_ShadowCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;
uniform vec3 lightPosition;
uniform mat4 toShadowMapSpace;
uniform float shadowDistance;

const float tiling = 4.0;
const float transitionDistance = 10.0;

void main(void) {
	vec4 worldPosition = modelMatrix * vec4(in_Position.x, 0.0, in_Position.y, 1.0);
	pass_ShadowCoords = toShadowMapSpace * worldPosition;
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	pass_ClipSpace = projectionMatrix * positionRelativeToCamera;
	gl_Position = pass_ClipSpace;
	pass_TexCoord = vec2(in_Position.x / 2.0 + 0.5, in_Position.y / 2.0 + 0.5);
	pass_TexCoord = pass_TexCoord * tiling;
	pass_ToCameraVector = cameraPosition - worldPosition.xyz;
	pass_FromLightVector = worldPosition.xyz - lightPosition;
	float distance = length(positionRelativeToCamera.xyz);
	distance = distance - (shadowDistance - transitionDistance);
	distance = distance / transitionDistance;
	pass_ShadowCoords.w = clamp(1.0 - distance, 0.0, 1.0);
}