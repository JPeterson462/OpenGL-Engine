#version 330

uniform sampler2D texture;

in vec2 pass_TexCoord;

out vec4 out_Color;

void main(void) {
	out_Color = texture2D(texture, pass_TexCoord);
}