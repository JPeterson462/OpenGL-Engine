#version 330

uniform sampler2D texture;

in vec2 pass_TexCoord;
in vec4 pass_Color;

out vec4 out_Color;

uniform vec4 textEffect;
uniform vec3 effectColor;

void main(void) {
	float lineWidth = textEffect.x;
	float sharpness = textEffect.y;
	float borderWidth = textEffect.z;
	vec2 offset = vec2(textEffect.w, textEffect.w);

	float textureAlpha = texture2D(texture, pass_TexCoord).a;
	float distance = 1.0 - textureAlpha;
	float fontAlpha = 1.0 - smoothstep(lineWidth, lineWidth + sharpness, distance);
	float textureAlpha2 = texture2D(texture, pass_TexCoord + offset).a;
	float distance2 = 1.0 - textureAlpha2;
	float outlineAlpha = 1.0 - smoothstep(borderWidth, borderWidth + sharpness, distance2);
	float overallAlpha = fontAlpha + (1.0 - fontAlpha) * outlineAlpha;
	vec3 overallColor = mix(effectColor, pass_Color.rgb, fontAlpha / overallAlpha);
	out_Color = vec4(overallColor, overallAlpha);
}