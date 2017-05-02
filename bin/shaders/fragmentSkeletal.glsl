#version 150

const vec2 lightBias = vec2(0.7, 0.6);

uniform sampler2D diffuseMap;
uniform vec3 lightDirection;

in vec2 pass_TexCoord;
in vec3 pass_Normal;

out vec4 out_Color;

void main(void) {
	vec4 diffuseColor = texture2D(diffuseMap, pass_TexCoord);		
	vec3 unitNormal = normalize(pass_Normal);
	float diffuseLight = max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y;
	out_Color = diffuseColor * diffuseLight;	
}