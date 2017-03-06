#version 330

layout(std140) uniform;


layout(location = 6) in vec4 worldCubePosition; // w component is scale of face
layout(location = 7) in int faceIndex;
layout(location = 8) in vec4 tint;

flat out int passFaceIndex;
out vec3 passTint;

uniform Projection
{
	mat4 view;
	mat4 projection;
	ivec4 viewPort;
};

uniform mat3[6] faceMatrix;
void main()
{

    gl_Position = worldCubePosition;
    passFaceIndex = faceIndex;
    passTint = tint.xyz;

    passTint = passTint * (1 - tint.a);

    if(passTint.r < 0.01) { passTint.r = -1.0; }
    if(passTint.g < 0.01) { passTint.g = -1.0;}
    if(passTint.b < 0.01) { passTint.b = -1.0;}

    passTint = vec3(-5,-1,-1); //test
    //passFaceIndex = int[](int(worldCubePosition.x + worldCubePosition.y + worldCubePosition.z) % 6);
}
