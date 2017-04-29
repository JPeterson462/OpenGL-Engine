#version 330 core

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;

in vec2 pass_TexCoord;

out vec4 out_Color;

void main(void) {
	vec4 reflectColor = texture2D(reflectionTexture, pass_TexCoord);
	vec4 refractColor = texture2D(refractionTexture, pass_TexCoord);
	out_Color = mix(reflectColor, refractColor, 0.5);
}