#version 330 core

in vec3 in_Position;

out vec4 pass_ClipSpace;
out vec2 pass_TexCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

const float tiling = 4.0;

void main(void) {
	vec4 worldPosition = modelMatrix * vec4(in_Position.x, 0.0, in_Position.y, 1.0);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	pass_ClipSpace = projectionMatrix * positionRelativeToCamera;
	gl_Position = pass_ClipSpace;
	pass_TexCoord = vec2(in_Position.x / 2.0 + 0.5, in_Position.y / 2.0 + 0.5);
	pass_TexCoord = pass_TexCoord * tiling;
}