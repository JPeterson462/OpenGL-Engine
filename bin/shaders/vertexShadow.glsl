#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

in vec3 in_Position;

out vec2 pass_TexCoord;

void main(void) {
	pass_TexCoord = in_Position.xy * 0.5 + 0.5;
	gl_Position = vec4(in_Position, 1.0);
}