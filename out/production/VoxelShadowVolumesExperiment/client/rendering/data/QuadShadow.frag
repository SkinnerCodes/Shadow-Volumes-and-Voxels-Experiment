#version 330

layout(std140) uniform;
layout(location = 0) out vec3 outputColor;

flat in vec4[5] facePlanes;
in vec3 tint;

uniform Projection
{
	mat4 view;
	mat4 projection;
	ivec4 viewPort;
};

uniform sampler2D diffuseColorTex;
uniform sampler2D depthTex;

void main()
{
    vec3 pixLoc;
    pixLoc.xy = ((2.0 * gl_FragCoord.xy) - (2.0 * viewPort.xy)) / (viewPort.zw) - 1;
    pixLoc.z = (2.0 *  texture(depthTex, (gl_FragCoord.xy - viewPort.xy) / viewPort.zw).r - gl_DepthRange.near - gl_DepthRange.far) / (gl_DepthRange.far - gl_DepthRange.near);
    //now in ndc
    //if(pixLoc.z > .98) discard; //test to remove light filtering at points at infinity or zfar or ww/e

     pixLoc.z = -projection[3].z / (projection[2].z +  pixLoc.z);
     pixLoc.x =  (-pixLoc.z * (projection[2].x + pixLoc.x) ) / projection[0].x;
     pixLoc.y = (-pixLoc.z * (projection[2].y + pixLoc.y) ) / projection[1].y;

    //now in eye space


    for(int i=0; i<5; ++i)
    {
        if(dot(facePlanes[i].xyz, pixLoc.xyz) + facePlanes[i].w >= 0) discard;
    }

	outputColor = vec3(0,0,0);


}
