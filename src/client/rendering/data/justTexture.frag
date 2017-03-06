#version 330

in vec2 colorCoord;

out vec4 outputColor;

uniform sampler2D diffuseColorTex;

void main()
{

    //float t= texture(diffuseColorTex, colorCoord).r;
   // if(t * 999 > 0) t = 1;
    //outputColor = vec4(t,t,t,1);
	outputColor =  texture(diffuseColorTex, colorCoord);
	//outputColor =  vec4(1,1,1,1);
}
