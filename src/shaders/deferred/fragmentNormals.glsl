#version 330

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;

uniform float materialShineDamper;
uniform float materialReflectivity;

in vec2 pass_TexCoord;
in vec3 pass_SurfaceNormal;

layout (location = 0) out vec4 out_Color0;
layout (location = 0) out vec4 out_Color1;
layout (location = 0) out vec4 out_Color2;

vec3 computeNormal() {
	vec3 mappedNormal = texture2D(normalTexture, pass_TexCoord).rgb * 2.0 - 1.0;
	vec3 totalNormal = pass_SurfaceNormal + mappedNormal;
	return normalize(totalNormal);
}

void main(void) {
	out_Color0 = texture2D(diffuseTexture, pass_TexCoord);
	out_Color1 = vec4(computeNormal() * 0.5 + 0.5, 1.0);
	out_Color2 = vec4(materialShineDamper, materialReflectivity, 0.0, 0.0);
}