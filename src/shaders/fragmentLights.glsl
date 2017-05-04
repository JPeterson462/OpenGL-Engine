#version 150

uniform vec4 lightColor;

in float pass_FadeFactor;
in float pass_Directional;
in vec3 pass_ToCameraVector;
in vec3 pass_ToLightVector;

out vec4 out_Color0;
out vec4 out_Color1;
out vec4 out_Color2;

void main(void) {
	if (pass_Directional > 0) {
		float distanceToCamera = length(pass_ToCameraVector);
		float distanceToLight = length(pass_ToLightVector);
		out_Color0 = lightColor * pass_FadeFactor;
		out_Color1 = vec4(normalize(pass_ToCameraVector) * 0.5 + 0.5, distanceToCamera);
		out_Color2 = vec4(normalize(pass_ToLightVector) * 0.5 + 0.5, distanceToLight);
	} else {
		out_Color0 = lightColor;
		out_Color1 = vec4(0.0);
		out_Color2 = vec4(0.0);
	}
}