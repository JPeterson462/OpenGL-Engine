#version 330

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;

uniform float materialShineDamper;
uniform float materialReflectivity;

in vec2 pass_TexCoord;
in vec3 pass_SurfaceNormal;

layout (location = 0) out vec4 out_Color0;
layout (location = 1) out vec4 out_Color1;
layout (location = 2) out vec4 out_Color2;

void main(void) {
	out_Color0 = vec4(normalize(pass_SurfaceNormal + texture2D(normalTexture, pass_TexCoord).rgb) * 0.5 + 0.5, 0.0);
	out_Color1 = vec4(texture2D(diffuseTexture, pass_TexCoord));
	out_Color2 = vec4(materialShineDamper, materialReflectivity, 0.0, 0.0);
}