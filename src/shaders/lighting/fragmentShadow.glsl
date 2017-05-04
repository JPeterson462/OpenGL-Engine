#version 330

uniform sampler2D lightDepthMap;
uniform sampler2D lightDiffuseMap;

in vec2 pass_TexCoord;

layout (location = 0) out vec4 out_Color;

void main(void) {
	out_Color = texture2D(lightDiffuseMap, pass_TexCoord);
}