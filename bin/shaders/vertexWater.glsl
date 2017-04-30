#version 330 core

in vec3 in_Position;

out vec4 pass_ClipSpace;
out vec2 pass_TexCoord;
out vec3 pass_ToCameraVector;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;

const float tiling = 6.0;

void main(void) {
	vec4 worldPosition = modelMatrix * vec4(in_Position.x, 0.0, in_Position.y, 1.0);
	pass_ClipSpace = projectionMatrix * viewMatrix * worldPosition;
	gl_Position = pass_ClipSpace;
	pass_TexCoord = vec2(in_Position.x / 2.0 + 0.5, in_Position.y / 2.0 + 0.5);
	pass_TexCoord = pass_TexCoord * tiling;
	pass_ToCameraVector = cameraPosition - worldPosition.xyz;
}