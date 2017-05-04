#version 330

uniform mat4 projectionViewMatrix;
uniform mat4 modelMatrix;

in vec3 in_Position;
in vec2 in_TexCoord;

out vec2 pass_TexCoord;

void main(void) {
	gl_Position = projectionViewMatrix * modelMatrix * vec4(in_Position, 1.0);
	pass_TexCoord = in_TexCoord;
}