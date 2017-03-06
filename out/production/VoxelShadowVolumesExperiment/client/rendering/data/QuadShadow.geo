#version 330

layout(std140) uniform;

layout(points) in;
layout(triangle_strip, max_vertices = 13) out; //change t o 36

out vec3 tint;
flat out vec4 facePlanes[5];
in vec3[] passTint;
flat in int[] passFaceIndex;

uniform Projection
{
	mat4 view;
	mat4 projection;
	ivec4 viewPort;
};

uniform mat3[6] faceMatrix;

uniform vec4 lightPos;

void main() //faceMatrix[passFaceIndex[0]]
{
    tint = passTint[0];

    vec3 newPos = faceMatrix[passFaceIndex[0]] * vec3(-0.5f, -0.5f, 0.5f);
    newPos = newPos * gl_in[0].gl_Position.w; //commented this out to test
    newPos = newPos + gl_in[0].gl_Position.xyz + (vec3(.5,.5,.5) * gl_in[0].gl_Position.w);
    vec4 bottomLeft = view * vec4(newPos, 1.0);

    newPos = faceMatrix[passFaceIndex[0]] * vec3(0.5f, -0.5f, 0.5f);
    newPos = newPos * gl_in[0].gl_Position.w;
    newPos = newPos + gl_in[0].gl_Position.xyz + (vec3(.5,.5,.5) * gl_in[0].gl_Position.w);
    vec4 bottomRight = view * vec4(newPos, 1.0);


    newPos = faceMatrix[passFaceIndex[0]] * vec3(-0.5f, 0.5f, 0.5f);
    newPos = newPos * gl_in[0].gl_Position.w;
    newPos = newPos + gl_in[0].gl_Position.xyz + (vec3(.5,.5,.5) * gl_in[0].gl_Position.w);
    vec4 topLeft = view * vec4(newPos, 1.0);

    newPos = faceMatrix[passFaceIndex[0]] * vec3(0.5f, 0.5f, 0.5f);
    newPos = newPos * gl_in[0].gl_Position.w;
    newPos = newPos + gl_in[0].gl_Position.xyz + (vec3(.5,.5,.5) * gl_in[0].gl_Position.w);
    vec4 topRight =  view * vec4(newPos, 1.0);

	//the bias to fix tesselation on some graphics cards
    float test = 0.05;
    topRight.z -= test;
    topLeft.z -= test;
    bottomRight.z -= test;
    bottomLeft.z -= test;

    newPos = faceMatrix[passFaceIndex[0]] * vec3(0.0f, 0.0f, 1.0f);
    newPos = (view * vec4(newPos, 0.0)).xyz;
    newPos = normalize(newPos); //incase view has scaling
    vec3 lightDir =  (1 - lightPos.w)*(normalize(-lightPos.xyz)) + (lightPos.w)*(normalize(lightPos.xyz - ((topRight.xyz + topLeft.xyz + bottomLeft.xyz + bottomRight.xyz)/4))); //normalize may not be needed
    float sameWinding = dot(newPos, lightDir);
    sameWinding = (1 + (sameWinding / abs(sameWinding)))/2; //gets 1 or 0, 1 for same winding and 0 for opposite winding

    //sameWinding = 0; //test
    vec4 temp = topLeft;
    topLeft = (sameWinding)*(topLeft) + (1 - sameWinding)*(topRight);
    topRight = (1 - sameWinding)*(temp) + (sameWinding)*(topRight);
    temp = bottomLeft;
    bottomLeft = (sameWinding)*(bottomLeft) + (1 - sameWinding)*(bottomRight);
    bottomRight = (1 - sameWinding)*(temp) + (sameWinding)*(bottomRight);

    vec4 projectedBottomLeft = projection * bottomLeft;
    vec4 projectedBottomRight = projection * bottomRight;
    vec4 projectedTopLeft = projection * topLeft;
    vec4 projectedTopRight = projection * topRight;

    float bigNumber = 200000.0;

    lightDir = (lightPos.w)*(topRight.xyz) +  (1 - (2 * lightPos.w))*(lightPos.xyz);
    vec4 topRightBack = vec4((((lightDir) * bigNumber) + topRight.xyz), 1);
    vec4 projectedTopRightBack = projection * topRightBack;

    lightDir = (lightPos.w)*(bottomRight.xyz) +  (1 - (2 * lightPos.w))*(lightPos.xyz);
    vec4 bottomRightBack = vec4((((lightDir) * bigNumber) + bottomRight.xyz), 1);
    vec4 projectedBottomRightBack = projection * bottomRightBack;

    lightDir = (lightPos.w)*(topLeft.xyz) +  (1 - (2 * lightPos.w))*(lightPos.xyz);
    vec4 topLeftBack = vec4((((lightDir) * bigNumber) + topLeft.xyz), 1);
    vec4 projectedTopLeftBack = projection * topLeftBack;

    lightDir = (lightPos.w)*(bottomLeft.xyz) +  (1 - (2 * lightPos.w))*(lightPos.xyz);
    vec4 bottomLeftBack = vec4((((lightDir) * bigNumber) + bottomLeft.xyz), 1);
    vec4 projectedBottomLeftBack = projection * bottomLeftBack;

    vec3 normal;
    normal = normalize(cross((topRight.xyz - topLeft.xyz),(topLeft.xyz - bottomLeft.xyz)));
    facePlanes[0] = vec4(normal.xyz, dot(-normal.xyz, topLeft.xyz));
    normal = normalize(cross((topRightBack.xyz - topRight.xyz),(topLeft.xyz - topRight.xyz)));
    facePlanes[1] = vec4(normal.xyz, dot(-normal.xyz, topLeft.xyz));
    normal = normalize(cross((topLeftBack.xyz - topLeft.xyz),(bottomLeft.xyz - topLeft.xyz)));
    facePlanes[2] = vec4(normal.xyz, dot(-normal.xyz, topLeft.xyz));
    normal = normalize(cross((bottomRightBack.xyz - bottomRight.xyz),(topRight.xyz - bottomRight.xyz)));
    facePlanes[3] = vec4(normal.xyz, dot(-normal.xyz, bottomRight.xyz));
    normal = normalize(cross((bottomLeft.xyz - bottomRight.xyz),(bottomRightBack.xyz - bottomRight.xyz)));
    facePlanes[4] = vec4(normal.xyz, dot(-normal.xyz, bottomRight.xyz));

    //gl_Position = vec4(-1,-1,-1,1); EmitVertex();
    //gl_Position = vec4(1,-1,-1,1); EmitVertex();
    //gl_Position = vec4(-1,1,-1,1); EmitVertex();
    //gl_Position = vec4(1,1,-1,1);  EmitVertex();
    //EndPrimitive();

    //front face
    //gl_Position = projectedBottomLeft;
    //EmitVertex();

    //gl_Position = projectedBottomRight;
    //EmitVertex();

    //gl_Position = projectedTopLeft;
    //EmitVertex();

    gl_Position = projectedBottomLeft;
    EmitVertex();

    gl_Position = projectedBottomRight;
    EmitVertex();

    gl_Position = projectedTopLeft;
    EmitVertex();

    gl_Position = projectedTopRight;
    EmitVertex();

    gl_Position = projectedTopRightBack;
    EmitVertex();

    gl_Position = projectedBottomRight;
    EmitVertex();

    gl_Position = projectedBottomRightBack;
    EmitVertex();

    gl_Position = projectedBottomLeft;
    EmitVertex();

    gl_Position = projectedBottomLeftBack;
    EmitVertex();

    gl_Position = projectedTopLeft;
    EmitVertex();

    gl_Position = projectedTopLeftBack;
    EmitVertex();

    gl_Position = projectedTopRight;
    EmitVertex();

    gl_Position = projectedTopRightBack;
    EmitVertex();

    //don't draw back cap
    EndPrimitive();

}
