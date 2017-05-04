#version 330

uniform sampler2D modelTexture;

in vec2 pass_TexCoord;

out vec4 out_Color;

void main(void) {
	float alpha = texture2D(modelTexture, pass_TexCoord).a;
	if (alpha < 0.5) {
		discard;
	}
	out_Color = vec4(1.0);
}