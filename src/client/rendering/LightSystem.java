package client.rendering;

import chunks.ChunkMap;
import client.World;
import optic.debug.Benchmark;
import optic.framework.BufferableData;
import optic.framework.Camera3D;
import optic.framework.GPUBuffer;
import optic.math.Mat4;
import optic.math.Vec3;
import optic.math.Vec4;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 * Visit https://github.com/rosickteam/OpenGL for project info, updates and license terms.
 * 
 * @author integeruser, xire-
 */
public class LightSystem {

	public LightSystem() {
        { //the following needs to be managed more if there is to be mulitple worlds because the light / projection buffer needs to be swapped for each different draw call
            // Setup our Uniform Buffers
            viewProjectionBuffer = new GPUBuffer(GL_DYNAMIC_DRAW);
            viewProjectionBuffer.bind(GL_UNIFORM_BUFFER);
            viewProjectionBuffer.allocate(4*4*4*2 + 4*4);

            lightBuffer = new GPUBuffer(GL_DYNAMIC_DRAW);
            lightBuffer.bind(GL_UNIFORM_BUFFER);
            lightBuffer.allocate(LightSystem.LightBlock.SIZE);

            //bind viewprojection uniform buffer to a uniform binding point corresponding to the shader block
            ShaderPrograms.bindViewProjectionBlock(0);
            glBindBufferRange(GL_UNIFORM_BUFFER, ShaderPrograms.viewProjBindingPoint, viewProjectionBuffer.bufferID, 0, viewProjectionBuffer.size);

            ShaderPrograms.bindLightBlock(1);
            glBindBufferRange(GL_UNIFORM_BUFFER, ShaderPrograms.lightBlockBindingPoint, lightBuffer.bufferID, 0, LightSystem.LightBlock.SIZE);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);
        }
        //worldframe fbo
            // The framebuffer, which regroups 0, 1, or more textures, and 0 or 1 depth buffer.

            worldFrame = glGenFramebuffers();
            glBindFramebuffer(GL_FRAMEBUFFER, worldFrame);

            worldColor = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, worldColor);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Display.getWidth(), Display.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer)null);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);

            // The depth buffer
            worldDepth = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, worldDepth);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT16, Display.getWidth(), Display.getHeight(), 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, (ByteBuffer)null);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_NONE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
//            glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_LUMINANCE);
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);

            // bind attachments
            glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, worldColor, 0);
            glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, worldDepth, 0);
            // Set the list of draw buffers.
            glDrawBuffers(GL_COLOR_ATTACHMENT0); // "1" is the size of DrawBuffers

            // Always check that our framebuffer is ok
            if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
                throw new RuntimeException("yo frame buffer is broken bro");

        { //lightframe fbo
            // The framebuffer, which regroups 0, 1, or more textures, and 0 or 1 depth buffer.
            lightFrame = glGenFramebuffers();
            glBindFramebuffer(GL_FRAMEBUFFER, lightFrame);

            lightMap = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, lightMap);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Display.getWidth(), Display.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer)null); //this is stupid wrong
            // Poor filtering. Needed !
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);

            // The depth buffer
//            int depthrenderbuffer = glGenRenderbuffers();
//            glBindRenderbuffer(GL_RENDERBUFFER, depthrenderbuffer);
//            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, Display.getWidth(), Display.getHeight());
//            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthrenderbuffer);

            // Set "lightMap" as our colour attachement #0
            glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, lightMap, 0); //           glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, worldDepth, 0);
            glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, worldDepth, 0);
            // Set the list of draw buffers.
            glDrawBuffers(GL_COLOR_ATTACHMENT0); // "1" is the size of DrawBuffers
            // Always check that our framebuffer is ok
            if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
                throw new RuntimeException("yo frame buffer is broken bro");
        }


    }
	int lightFrame;
    int lightMap;
	int worldFrame;
    int worldColor;
    int worldDepth;
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private FloatBuffer mat4Buffer 	= BufferUtils.createFloatBuffer(Mat4.SIZE);
    private FloatBuffer lightBlockBuffer = BufferUtils.createFloatBuffer(LightSystem.LightBlock.SIZE);
    private IntBuffer intBuffer = BufferUtils.createIntBuffer(4);

    GPUBuffer viewProjectionBuffer;
    GPUBuffer lightBuffer;
    public void submitProjectionBlock(Camera3D camera) {
        glBindBuffer(GL_UNIFORM_BUFFER, viewProjectionBuffer.bufferID);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, camera.calcViewMatrix().fillAndFlipBuffer(mat4Buffer));
        glBufferSubData(GL_UNIFORM_BUFFER, 4 * 4 * 4, camera.calcProjectionMatrix().fillAndFlipBuffer(mat4Buffer));
        glBufferSubData(GL_UNIFORM_BUFFER, 4 * 4 * 4 * 2, camera.viewPort.fillAndFlipBuffer(intBuffer));
        //System.out.print(camera.viewPort.toString());
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

    }


//    public void RenderScene(World world, Camera3D camera)
//    {
//        glBindFramebuffer(GL_FRAMEBUFFER, 0) ;
//        submitProjectionBlock(camera);
//
//
//        //submit light data to shared uniform block
//        LightBlock lightData = getLightBlock(camera.calcViewMatrix());
//        glBindBuffer(GL_UNIFORM_BUFFER, lightBuffer.bufferID);
//        lightBuffer.submitData(0, lightData.fillAndFlipBuffer(lightBlockBuffer));
//        glBindBuffer(GL_UNIFORM_BUFFER, 0);
//
//        //initial set up/
//        glClear(GL_STENCIL_BUFFER_BIT); //clear stencil buffer
//        glEnable(GL_STENCIL_TEST); //enable stencil testing, but how much bitplanes does it have?
//        glStencilFunc(GL_ALWAYS, 0, ~0); //set stencil test function
//        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
//        glEnable(GL_DEPTH_TEST); //enable depth testing
//        glDepthFunc(GL_LESS); //set depth test function
//
//        // Render ambient scene
//        world.initDraw(camera);
//        //glDisable(GL_CULL_FACE); //test
//        glUseProgram(ShaderPrograms.QuadAmbientData.theProgram);
//        ChunkMap.draw();
//
//        //turn off writing to color / depth buffer
//        glColorMask(false, false, false, false);
//        glDepthMask(false);
//
//        //enable gl depth clamp
//        //set up stencil buffer
//        //set additive blending
//
//        //for each light
//        //accumulate shadow volumes
//        // draw back and front faces, z pass or zfail
//
//        for (int i = 0; i < numOfPointLights + 1; i++) {
//            glUseProgram(ShaderPrograms.QuadShadowData.theProgram);
//            glUniform4f(ShaderPrograms.QuadShadowData.UniformLocations.lightPos, lightData.lights[i].cameraSpaceLightPos.x, lightData.lights[i].cameraSpaceLightPos.y, lightData.lights[i].cameraSpaceLightPos.z, lightData.lights[i].cameraSpaceLightPos.w);
//            if(i == 1) Display.setTitle(lightData.lights[i].cameraSpaceLightPos.toString());
//            glColorMask(false, false, false, false);
//
//            glClear(GL_STENCIL_BUFFER_BIT); //clear stencil buffer
//            glEnable(GL_STENCIL_TEST); //enable stencil testing, but how much bitplanes does it have?
//            glStencilFunc(GL_ALWAYS, 0, ~0); //set stencil test function
//            glEnable(GL_DEPTH_TEST); //enable depth testing
//            glDepthFunc(GL_LESS); //set depth test function
//            glEnable(GL_CULL_FACE);
//            glCullFace(GL_BACK);
//            glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);
//            glDisable(GL_BLEND);
//            if( !Keyboard.isKeyDown(Keyboard.KEY_F)) ChunkMap.drawShadowVolumes();
//            glCullFace(GL_FRONT);
//            glStencilOp(GL_KEEP, GL_KEEP, GL_DECR);
//            if( !Keyboard.isKeyDown(Keyboard.KEY_F)) ChunkMap.drawShadowVolumes();
//
//
//            glCullFace(GL_BACK);
//            // draw lit scene with stencil mask for this lightz
//            glColorMask(true, true, true, true);
//            glDepthFunc(GL_LEQUAL); //equal or greater?
//            glStencilFunc(GL_EQUAL, 0, ~0);
//            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
//            glEnable(GL_BLEND); //can be moved up ?
//            glBlendFunc(GL_ONE, GL_ONE);
//
//            glUseProgram(ShaderPrograms.QuadData.theProgram);
//            glUniform1i(ShaderPrograms.QuadData.UniformLocations.lightIndexUnif, i);
//            ChunkMap.draw();
//
//        }
//
//        //clean up
//        glDepthMask(true);
//        glDepthFunc(GL_LEQUAL);
//        glStencilFunc(GL_ALWAYS, 0, ~0);
//        glDisable(GL_BLEND);
//
//    }
    public void RenderScene(World world, Camera3D camera)
    {
        Vec4 cam = new Vec4(4780,-4,4078,1);
        Vec4 proj = Mat4.mul(camera.calcProjectionMatrix(), cam);
        Vec3 ndc = new Vec3(Vec4.scale(proj, 1/proj.w));
        Vec3 window = new Vec3(0f);
        window.z = ((ndc.z + 1)/2);
        window.x = ((ndc.x + 1)/2) * camera.viewPort.z;
        window.y = ((ndc.y + 1)/2) * camera.viewPort.w;


        Vec3 pixLoc = new Vec3(window);
        pixLoc.x = (float)((2.0 * window.x) - (2.0 * camera.viewPort.x)) / (camera.viewPort.z) - 1;
        pixLoc.y = (float)((2.0 * window.y) - (2.0 * camera.viewPort.y)) / (camera.viewPort.w) - 1;
        pixLoc.z = (float)(2.0 *  window.z - 1);
        //now in ndc
        pixLoc.z = -camera.calcProjectionMatrix().getColumn(3).z / (camera.calcProjectionMatrix().getColumn(2).z +  pixLoc.z);
        pixLoc.x =  (-pixLoc.z * (camera.calcProjectionMatrix().getColumn(2).x + pixLoc.x) ) / camera.calcProjectionMatrix().getColumn(0).x;
        pixLoc.y = (-pixLoc.z * (camera.calcProjectionMatrix().getColumn(2).y + pixLoc.y) ) / camera.calcProjectionMatrix().getColumn(1).y;
        //now in camera

        //System.out.println("test: " + cam + " | " + pixLoc);

        Vec3 projectedTopRight, projectedTopLeft, projectedBottomLeft, projectedBottomRight, projectedTopRightBack, projectedTopLeftBack, projectedBottomLeftBack, projectedBottomRightBack;

        pixLoc = new Vec3(40,50,40); //test
        float x = 400;
        projectedTopRight = new Vec3(x,x,x);
        projectedBottomRight = new Vec3(x,-x,x);
        projectedBottomLeft = new Vec3(-x,-x,x);
        projectedTopLeft = new Vec3(-x,x,x);
        projectedTopRightBack = new Vec3(x,x,-x);
        projectedBottomRightBack = new Vec3(x,-x,-x);
        projectedBottomLeftBack = new Vec3(-x,-x,-x);
        projectedTopLeftBack = new Vec3(-x,x,-x);

        Vec4[] facePlanes = new Vec4[5];
        Vec3 normal;
        normal = Vec3.normalize(Vec3.cross(Vec3.sub(projectedTopRight, projectedTopLeft), Vec3.sub(projectedTopLeft, projectedBottomLeft)));
        facePlanes[0] = new Vec4(normal, Vec3.dot(Vec3.scale(normal,-1f), projectedTopLeft));
        normal = Vec3.normalize(Vec3.cross(Vec3.sub(projectedTopRightBack, projectedTopRight),Vec3.sub(projectedTopLeft, projectedTopRight)));
        facePlanes[1] = new Vec4(normal, Vec3.dot(Vec3.scale(normal, -1f), projectedTopLeft));
        normal = Vec3.normalize(Vec3.cross(Vec3.sub(projectedTopLeftBack, projectedTopLeft),Vec3.sub(projectedBottomLeft, projectedTopLeft)));
        facePlanes[2] = new Vec4(normal, Vec3.dot(Vec3.scale(normal, -1f), projectedTopLeft));
        normal = Vec3.normalize(Vec3.cross(Vec3.sub(projectedBottomRightBack, projectedBottomRight),Vec3.sub(projectedTopRight, projectedBottomRight)));
        facePlanes[3] = new Vec4(normal, Vec3.dot(Vec3.scale(normal, -1f), projectedBottomRight));
        normal = Vec3.normalize(Vec3.cross(Vec3.sub(projectedBottomLeft, projectedBottomRight),Vec3.sub(projectedBottomRightBack, projectedBottomRight)));
        facePlanes[4] = new Vec4(normal, Vec3.dot(Vec3.scale(normal, -1f), projectedBottomRight));
        for(int i=0; i<5; ++i)
        {
            if(Vec3.dot(new Vec3(facePlanes[i]), pixLoc) + facePlanes[i].w >= 0) System.out.println("NOT INSIDE BRO: " + i);
        }









        submitProjectionBlock(camera);

        //submit light data to shared uniform block
        LightBlock lightData = getLightBlock(camera.calcViewMatrix());
        glBindBuffer(GL_UNIFORM_BUFFER, lightBuffer.bufferID);
        lightBuffer.submitData(0, lightData.fillAndFlipBuffer(lightBlockBuffer));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        //initial set up/
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST); //enable depth testing
        glDepthFunc(GL_LESS); //set depth test function
        glColorMask(true, true, true, true); glDepthMask(true);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        // Render ambient scene
        glUseProgram(ShaderPrograms.QuadAmbientData.theProgram);
        glBindFramebuffer(GL_FRAMEBUFFER, worldFrame) ;
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        ChunkMap.draw();

        for (int i = 0; i < numOfPointLights + 1; i++) { //test: change i back to 0
            glUseProgram(ShaderPrograms.QuadShadowData.theProgram);
            glUniform4f(ShaderPrograms.QuadShadowData.UniformLocations.lightPos, lightData.lights[i].cameraSpaceLightPos.x, lightData.lights[i].cameraSpaceLightPos.y, lightData.lights[i].cameraSpaceLightPos.z, lightData.lights[i].cameraSpaceLightPos.w);


            glBindFramebuffer(GL_FRAMEBUFFER, lightFrame);
            glColorMask(true,true,true,true); //turn on writing to color buffer
            glClearColor(1f, 1f, 1f, 1f);
            //glClearColor(0f, 0f, 0f, 1f); //test
            //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClear(GL_COLOR_BUFFER_BIT);
            glUseProgram(ShaderPrograms.QuadShadowData.theProgram);
            //glDisable(GL_DEPTH_TEST); glDepthMask(false);// does same thing as  glDepthFunc(GL_ALWAYS); amd glDepthMask(false);
            glDepthMask(false);
            glEnable(GL_CULL_FACE);
            //glDisable(GL_CULL_FACE);//test
            glCullFace(GL_BACK);
            glEnable(GL_BLEND);
            glBlendFunc(GL_DST_COLOR, GL_ZERO);
            //glClampColor(GL_CLAMP_READ_COLOR, GL_FALSE); //turns off color clamping to 0=1 range, useful for float textures
            glActiveTexture(GL_TEXTURE0 + ShaderPrograms.QuadShadowData.Cache.depthTexBindingIndex);
            glBindTexture(GL_TEXTURE_2D, worldDepth);
            //TODO: have to make it bind diffuse textures and draw per texture here
            //glDisable(GL_DEPTH_TEST);
            //if(!Keyboard.isKeyDown(Keyboard.KEY_V))glEnable(GL_RASTERIZER_DISCARD);
            Benchmark.GPUstart();
            ChunkMap.drawShadowVolumes();
            Benchmark.GPUstop();
            //if(!Keyboard.isKeyDown(Keyboard.KEY_V))glDisable(GL_RASTERIZER_DISCARD);


            glBindFramebuffer(GL_FRAMEBUFFER, worldFrame) ;
            glCullFace(GL_BACK);
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LEQUAL);
            glDepthMask(false);
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE);
            glUseProgram(ShaderPrograms.QuadData.theProgram);
            glActiveTexture(GL_TEXTURE0 + ShaderPrograms.QuadData.Cache.lightFilerBindingIndex);
            //glBindTexture(GL_TEXTURE_2D, worldDepth); //test
            glBindTexture(GL_TEXTURE_2D, lightMap);
            glUniform1i(ShaderPrograms.QuadData.UniformLocations.lightIndexUnif, i);
            ChunkMap.draw();
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0) ;
        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        glUseProgram(ShaderPrograms.alreadyClipData.theProgram);
        glActiveTexture(GL_TEXTURE0 + ShaderPrograms.alreadyClipData.Cache.colorTextureBindingIndex);
        glBindTexture(GL_TEXTURE_2D, (!Keyboard.isKeyDown(Keyboard.KEY_F)) ? worldColor : lightMap);
        Quad2D.renderFullScreenNDCquad();

    }
	class PerLight extends BufferableData<FloatBuffer> {
		Vec4 cameraSpaceLightPos;
		Vec4 lightIntensity;
		
		static final int SIZE = Vec4.SIZE + Vec4.SIZE;
		
		@Override
		public FloatBuffer fillBuffer(FloatBuffer buffer) {
			cameraSpaceLightPos.fillBuffer(buffer);
			lightIntensity.fillBuffer(buffer);

			return buffer;
		}


	}

	
	class LightBlock extends BufferableData<FloatBuffer> {
		Vec4 ambientIntensity;
		float lightAttenuation;
		float maxIntensity;
		float padding[] = new float[2];
		PerLight lights[] = new PerLight[MAX_NUMBER_OF_LIGHTS];

		static final int SIZE = Vec4.SIZE + ((1 + 1 + 2) * (Float.SIZE / Byte.SIZE)) + PerLight.SIZE * MAX_NUMBER_OF_LIGHTS;

		@Override
		public FloatBuffer fillBuffer(FloatBuffer buffer) {			
			ambientIntensity.fillBuffer(buffer);
			buffer.put(lightAttenuation);
			buffer.put(maxIntensity);
			buffer.put(padding);
			
			for (PerLight light : lights) {
				if (light == null)
					break;
				
				light.fillBuffer(buffer);
			}
			
			return buffer;
		}
	}

	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	LightBlock getLightBlock(Mat4 worldToCameraMat) {
		LightBlock lightData = new LightBlock();
		lightData.ambientIntensity =  ambientIntensity ;
		lightData.lightAttenuation = lightAttenuation;
		lightData.maxIntensity = 1f; //also known as HDR i think

		lightData.lights[0] = new PerLight();
		lightData.lights[0].cameraSpaceLightPos = Mat4.mul(worldToCameraMat, SunDirection);
		lightData.lights[0].lightIntensity = SunIntensity;

		for (int lightIndex = 0; lightIndex < numOfPointLights; lightIndex++) {
			Vec4 worldLightPos = new Vec4(PointLights.get(lightIndex).getWorldPosition(), 1.0f);
			Vec4 lightPosCameraSpace = Mat4.mul(worldToCameraMat, worldLightPos);

			lightData.lights[lightIndex + 1] = new PerLight();
			lightData.lights[lightIndex + 1].cameraSpaceLightPos = lightPosCameraSpace; 
	    	lightData.lights[lightIndex + 1].lightIntensity = new Vec4(PointLights.get(lightIndex).getLightIntensity());
		}

		return lightData;
	}

	int getNumLights() {
		return 1 + numOfPointLights;
	}


	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	private final static int MAX_NUMBER_OF_LIGHTS = 4;
	
	private final float halfLightDistance = 5.0f;
	private final float lightAttenuation = 1.0f / (halfLightDistance * halfLightDistance);

    public Vec4 SunDirection = (new Vec4(800f,800f,800f, 0)).normalize();
    public Vec4 SunIntensity = new Vec4(new Vec3(0.2f), 1f);
    public Vec4 ambientIntensity = new Vec4(0f, 0f, 0f,  1f);

	public ArrayList<PointLight> PointLights = new ArrayList<>();
    private int numOfPointLights = 0;

    public void showPointLight(PointLight pointLight) {
        PointLights.add(pointLight);
        numOfPointLights += 1;
    }

    public void hidePointLight(PointLight pointLight) {
        PointLights.remove(pointLight);
        numOfPointLights -= 1;
    }
}