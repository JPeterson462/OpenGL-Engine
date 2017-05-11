#version 330

uniform sampler2D diffuseTexture;

uniform float materialShineDamper;
uniform float materialReflectivity;

in vec2 pass_TexCoord;
in vec3 pass_SurfaceNormal;

layout (location = 0) out vec4 out_Color0;
layout (location = 0) out vec4 out_Color1;
layout (location = 0) out vec4 out_Color2;

void main(void) {
	out_Color0 = texture2D(diffuseTexture, pass_TexCoord);
	out_Color1 = vec4(normalize(pass_SurfaceNormal) * 0.5 + 0.5, 1.0);
	out_Color2 = vec4(materialShineDamper, materialReflectivity, 0.0, 0.0);
}