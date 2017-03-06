#version 330

layout(std140) uniform;

layout(location = 0) in vec3 position;
layout(location = 2) in vec3 normal;
layout(location = 5) in vec2 texCoord;
layout(location = 6) in vec4 worldCubePosition; // w component is scale of face
layout(location = 7) in int faceIndex;
layout(location = 8) in vec4 tint;
layout(location = 9) in float glow;

out vec2 colorCoord;
out vec3 cameraSpacePosition;
out vec3 cameraSpaceNormal;
out vec4 fragTint;
out float fragGlow;

uniform Projection
{
	mat4 view;
	mat4 projection;
	ivec4 viewPort;
};

uniform mat3[6] faceMatrix;
void main()
{
    vec3 newPos = faceMatrix[faceIndex] * position;
    newPos = newPos * worldCubePosition.w;
    newPos = newPos + worldCubePosition.xyz + (vec3(.5,.5,.5) * worldCubePosition.w);
    vec4 newPos2 = view * vec4(newPos, 1.0);
	gl_Position = projection * newPos2;

	cameraSpaceNormal = (view * vec4((faceMatrix[faceIndex] * vec3(0.0, 0.0, 1.0)), 0.0)).xyz;
	cameraSpacePosition = newPos2.xyz;
	colorCoord = texCoord * worldCubePosition.w;
	fragTint = tint;
	fragGlow = glow;
}
