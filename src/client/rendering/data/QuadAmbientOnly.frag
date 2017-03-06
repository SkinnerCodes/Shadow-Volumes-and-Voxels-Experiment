#version 330

in vec2 colorCoord;
in vec3 cameraSpacePosition;
in vec3 cameraSpaceNormal;
in vec4 fragTint;
in float fragGlow;

layout(location = 0) out vec4 outputColor;


layout(std140) uniform;

struct PerLight
{
	vec4 cameraSpaceLightPos;
	vec4 lightIntensity;
};

uniform Light
{
	vec4 ambientIntensity;
	float lightAttenuation;
	float maxIntensity;
	PerLight lights[4];
} Lgt;

uniform int numberOfLights;
uniform vec2 screenDims;



uniform sampler2D diffuseColorTex;
uniform sampler2D lightFilter;

void main()
{
	vec4 diffuseColor = fragTint * texture(diffuseColorTex, colorCoord);

	vec4 accumLighting = diffuseColor * Lgt.ambientIntensity;
	
	outputColor = accumLighting / Lgt.maxIntensity;

}
