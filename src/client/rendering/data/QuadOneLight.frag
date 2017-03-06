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

uniform int lightIndex;
uniform vec2 screenDims;

uniform Projection
{
	mat4 view;
	mat4 projection;
	ivec4 viewPort;
};

float CalcAttenuation(in vec3 cameraSpacePosition, in vec3 cameraSpaceLightPos, out vec3 lightDirection)
{
	vec3 lightDifference =  cameraSpaceLightPos - cameraSpacePosition;
	float lightDistanceSqr = dot(lightDifference, lightDifference);
	lightDirection = lightDifference * inversesqrt(lightDistanceSqr);
	
	return (1 / ( 1.0 + Lgt.lightAttenuation * lightDistanceSqr));
}

vec4 ComputeLighting(in vec4 diffuseColor, in PerLight lightData)
{
	vec3 lightDir;
	vec4 lightIntensity;
	if(lightData.cameraSpaceLightPos.w == 0.0)
	{
		lightDir = vec3(lightData.cameraSpaceLightPos);
		lightIntensity = lightData.lightIntensity;
	    lightDir = normalize(-lightDir);
	}
	else
	{
		float atten = CalcAttenuation(cameraSpacePosition, lightData.cameraSpaceLightPos.xyz, lightDir);
		lightIntensity = atten * lightData.lightIntensity;
     }

	vec3 surfaceNormal = normalize(cameraSpaceNormal);
	vec3 halfVec = normalize((lightDir + normalize(-cameraSpacePosition)));
	float cosAngIncidence = dot(surfaceNormal, halfVec);
	cosAngIncidence = cosAngIncidence < 0.0001 ? 0.0 : cosAngIncidence;
	cosAngIncidence = dot(surfaceNormal, lightDir) > 0 ? cosAngIncidence : 0.0;

	vec4 lighting = diffuseColor * lightIntensity * pow(cosAngIncidence, 2);

	
	return lighting;
}

uniform sampler2D diffuseColorTex;
uniform sampler2D lightFilter;

void main()
{
	vec4 diffuseColor = fragTint * texture(diffuseColorTex, colorCoord);
	diffuseColor = vec4(diffuseColor.xyz * texture(lightFilter, (gl_FragCoord.xy - viewPort.xy) / viewPort.zw).xyz, diffuseColor.w); //put this back for colored shadows

	vec4 accumLighting = ComputeLighting(diffuseColor, Lgt.lights[lightIndex]);

	outputColor = accumLighting / Lgt.maxIntensity;

    //float t = 5;
    //if(((gl_FragCoord.xy - viewPort.xy) / viewPort.zw).x  > t && (gl_FragCoord.xy - viewPort.xy / viewPort.zw).y  > t)  outputColor = vec4(0,1,0,1);
    //if(viewPort.z < 0)  outputColor = vec4(0,1,0,1);

    //vec3 pixLoc;
    //pixLoc.xy = ((2.0 * gl_FragCoord.xy) - (2.0 * viewPort.xy)) / (viewPort.zw) - 1;
    //pixLoc.z = (2.0 *  texture(lightFilter, gl_FragCoord.xy - viewPort.xy / viewPort.zw).r - gl_DepthRange.near - gl_DepthRange.far) / (gl_DepthRange.far - gl_DepthRange.near);
    //now in ndc
    //pixLoc.z = projection[3].z / ((projection[2].z / pixLoc.z) + 1);
    //pixLoc.xy = pixLoc.xy * ( (projection[3].z - pixLoc.z) / projection[2].z);
	//if(abs((projection * vec4(cameraSpacePosition, 1)).z - pixLoc.z) > 5)  outputColor = vec4(0,1,0,1);

//outputColor = vec4(-1.0,-1.0,-1.0, 1.0);
	//outputColor = vec4(texture(lightFilter, gl_FragCoord).rgb - vec3(1.0, 1.0, 1.0), 1.0);
   // outputColor = texture(lightFilter, gl_FragCoord.xy / screenDims);
    //outputColor = vec4(1.0,1.0,1.0,1.0);
}
