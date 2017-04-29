#version 330 core

in vec3 in_Position;

out vec2 pass_TexCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(in_Position.x, 0.0, in_Position.y, 1.0);
	pass_TexCoord = vec2(in_Position.x / 2.0 + 0.5, in_Position.y / 2.0 + 0.5);
}