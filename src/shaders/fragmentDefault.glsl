#version 330

uniform sampler2D diffuseTexture;

uniform float materialShineDamper;
uniform float materialReflectivity;

in vec2 pass_TexCoord;
in vec3 pass_SurfaceNormal;

layout (location = 0) out vec4 out_Color0;
layout (location = 1) out vec4 out_Color1;
layout (location = 2) out vec4 out_Color2;

void main(void) {
	vec4 diffusePixel = texture2D(diffuseTexture, pass_TexCoord);
	if (diffusePixel.a < 0.5) {
		discard;
	}
	out_Color0 = vec4(normalize(pass_SurfaceNormal) * 0.5 + 0.5, 0.0);
	out_Color1 = diffusePixel;
	out_Color2 = vec4(materialShineDamper, materialReflectivity, 0.0, 0.0);
}