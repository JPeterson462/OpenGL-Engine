#version 330

uniform sampler2D fontAtlas;

in vec2 pass_TexCoord;
in vec4 pass_Color;

out vec4 out_Color;

uniform vec4 effectSharpness;

const vec2 offset = vec2(0.05, 0.05);

const vec3 outlineColor = vec3(0.2, 0.2, 0.2);

void main(void) {
	float width = effectSharpness.x;
	float edge = effectSharpness.y;
	float borderWidth = effectSharpness.z;
	float borderEdge = effectSharpness.w;

	vec3 color = pass_Color.rgb;

	float distance = 1.0 - texture2D(fontAtlas, pass_TexCoord).a;
	float alpha = 1.0 - smoothstep(width, width + edge, distance);
	
	float distance2 = 1.0 - texture2D(fontAtlas, pass_TexCoord + offset).a;
	float outlineAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);
	
	float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
	vec3 overallColor = mix(outlineColor, color, alpha / overallAlpha);

	out_Color = vec4(overallColor, overallAlpha);
}