package client.rendering;

import optic.framework.Framework;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;
import static org.lwjgl.opengl.GL32.*;

/**
 * Created by Thomas on 12/28/13.
 */
public class ShaderPrograms {

    public static void initializePrograms() {
        //this code does some stuff
        QuadData.loadProgram("Quad.vert", "QuadOneLight.frag");
        QuadAmbientData.loadProgram("Quad.vert", "QuadAmbientOnly.frag");
        QuadShadowData.loadProgram("QuadShadow.vert", "QuadShadow.frag", "QuadShadow.geo");
        alreadyClipData.loadProgram("alreadyClip.vert", "justTexture.frag");

    }


    public static class alreadyClipData {
        public static int theProgram;
        public static class UniformLocations {
            public static int colorTextureUnif;
            public static int projectionBlock;
        }
        public static class Cache {
            public static int colorTextureBindingIndex;
        }

        public static void loadProgram(String vertexShaderFilename, String fragmentShaderFilename) {
            ArrayList<Integer> shaderList = new ArrayList<>();
            shaderList.add(Framework.loadShader(GL_VERTEX_SHADER, vertexShaderFilename));
            shaderList.add(Framework.loadShader(GL_FRAGMENT_SHADER,	fragmentShaderFilename));

            theProgram = Framework.createProgram(shaderList);
            UniformLocations.projectionBlock = glGetUniformBlockIndex(theProgram, "Projection");


            UniformLocations.colorTextureUnif = glGetUniformLocation(theProgram, "diffuseColorTex");
            glUseProgram(theProgram);
            glUniform1i(UniformLocations.colorTextureUnif, 1);
            Cache.colorTextureBindingIndex = 1;
            glUseProgram(0);

        }


    }

    public static class QuadData {
        public static int theProgram;

        public static class UniformLocations {
            public static int modelMatrix;
            public static int lightIndexUnif;
            public static int projectionBlock;
            public static int lightBlock;
            public static int colorTextureUnif;
            public static int faceMatrix;
            public static int lightFilter;
            public static int screenDims;
        }
        public static class Cache {
            public static int colorTextureBindingIndex;
            public static int lightFilerBindingIndex;

        }


        public static void loadProgram(String vertexShaderFilename, String fragmentShaderFilename) {
            ArrayList<Integer> shaderList = new ArrayList<>();

            ShaderDebugger.DebugCompile(GL_VERTEX_SHADER, vertexShaderFilename);
            ShaderDebugger.DebugCompile(GL_FRAGMENT_SHADER, fragmentShaderFilename);
            //
            shaderList.add(Framework.loadShader(GL_VERTEX_SHADER, vertexShaderFilename));
            shaderList.add(Framework.loadShader(GL_FRAGMENT_SHADER,	fragmentShaderFilename));

            theProgram = Framework.createProgram(shaderList);
            UniformLocations.lightIndexUnif = glGetUniformLocation(theProgram, "lightIndex");

            UniformLocations.projectionBlock = glGetUniformBlockIndex(theProgram, "Projection");

            UniformLocations.lightBlock = glGetUniformBlockIndex(theProgram, "Light");

            UniformLocations.colorTextureUnif = glGetUniformLocation(theProgram, "diffuseColorTex");

            UniformLocations.lightFilter = glGetUniformLocation(theProgram, "lightFilter");

            UniformLocations.faceMatrix = glGetUniformLocation(theProgram, "faceMatrix");

            UniformLocations.screenDims = glGetUniformLocation(theProgram, "screenDims");

            FloatBuffer f = BufferUtils.createFloatBuffer(9 * 6);

            f.put(new float[] {0f,0f,-1f,0f,1f,0f,1f,0f,0f});
            f.put(new float[] {0f,0f,1f,0f,1f,0f,-1f,0f,0f});
            f.put(new float[] {1f,0f,0f,0f,0f,-1f,0f,1f,0f});
            f.put(new float[] {1f,0f,0f,0f,0f,1f,0f,-1f,0f});
            f.put(new float[] {1f,0f,0f,0f,1f,0f,0f,0f,1f});
            f.put(new float[] {-1f,0f,0f,0f,1f,0f,0f,0f,-1f});

            f.flip();
            glUseProgram(theProgram);
            glUniform1i(UniformLocations.colorTextureUnif, 1);
            glUniform1i(UniformLocations.lightFilter, 2);
            Cache.colorTextureBindingIndex = 1;
            Cache.lightFilerBindingIndex = 2;

            glUniformMatrix3(UniformLocations.faceMatrix, false, f);
            glUniform2f(UniformLocations.screenDims, Display.getWidth(), Display.getHeight());

            glUseProgram(0);
        }
    }
    public static class QuadAmbientData {
        public static int theProgram;

        public static class UniformLocations {
            public static int modelMatrix;
            public static int lightIndexUnif;
            public static int projectionBlock;
            public static int lightBlock;
            public static int colorTextureUnif;
            public static int faceMatrix;
            public static int lightFilter;
            public static int screenDims;
        }
        public static class Cache {
            public static int colorTextureBindingIndex;
            public static int lightFilerBindingIndex;

        }


        public static void loadProgram(String vertexShaderFilename, String fragmentShaderFilename) {
            ArrayList<Integer> shaderList = new ArrayList<>();

            ShaderDebugger.DebugCompile(GL_VERTEX_SHADER, vertexShaderFilename);
            ShaderDebugger.DebugCompile(GL_FRAGMENT_SHADER, fragmentShaderFilename);
            //
            shaderList.add(Framework.loadShader(GL_VERTEX_SHADER, vertexShaderFilename));
            shaderList.add(Framework.loadShader(GL_FRAGMENT_SHADER,	fragmentShaderFilename));

            theProgram = Framework.createProgram(shaderList);
            UniformLocations.lightIndexUnif = glGetUniformLocation(theProgram, "lightIndex");

            UniformLocations.projectionBlock = glGetUniformBlockIndex(theProgram, "Projection");

            UniformLocations.lightBlock = glGetUniformBlockIndex(theProgram, "Light");

            UniformLocations.colorTextureUnif = glGetUniformLocation(theProgram, "diffuseColorTex");

            UniformLocations.lightFilter = glGetUniformLocation(theProgram, "lightFilter");

            UniformLocations.faceMatrix = glGetUniformLocation(theProgram, "faceMatrix");

            UniformLocations.screenDims = glGetUniformLocation(theProgram, "screenDims");

            FloatBuffer f = BufferUtils.createFloatBuffer(9 * 6);

            f.put(new float[] {0f,0f,-1f,0f,1f,0f,1f,0f,0f});
            f.put(new float[] {0f,0f,1f,0f,1f,0f,-1f,0f,0f});
            f.put(new float[] {1f,0f,0f,0f,0f,-1f,0f,1f,0f});
            f.put(new float[] {1f,0f,0f,0f,0f,1f,0f,-1f,0f});
            f.put(new float[] {1f,0f,0f,0f,1f,0f,0f,0f,1f});
            f.put(new float[] {-1f,0f,0f,0f,1f,0f,0f,0f,-1f});

            f.flip();
            glUseProgram(theProgram);
            glUniform1i(UniformLocations.colorTextureUnif, 1);
            glUniform1i(UniformLocations.lightFilter, 2);
            Cache.colorTextureBindingIndex = 1;
            Cache.lightFilerBindingIndex = 2;

            glUniformMatrix3(UniformLocations.faceMatrix, false, f);
            glUniform2f(UniformLocations.screenDims, Display.getWidth(), Display.getHeight());

            glUseProgram(0);
        }
    }
    public static class QuadShadowData {
        public static int theProgram;
        public static int theGeoShader;
        public static int vertexShader;
        public static class UniformLocations {
            public static int projectionBlock;
            public static int faceMatrix;
            public static int lightPos;
            public static int diffuseTex;
            public static int depthTex;
        }
        public static class Cache {
            public static int diffuseTexBindingIndex;
            public static int depthTexBindingIndex;
        }


        public static void loadProgram(String vertexShaderFilename, String fragmentShaderFilename, String geometryShaderFilename) {
            ArrayList<Integer> shaderList = new ArrayList<>();

            ShaderDebugger.DebugCompile(GL_VERTEX_SHADER, vertexShaderFilename);
            ShaderDebugger.DebugCompile(GL_FRAGMENT_SHADER, fragmentShaderFilename);
            ShaderDebugger.DebugCompile(GL_GEOMETRY_SHADER, geometryShaderFilename );
            //
            shaderList.add(Framework.loadShader(GL_VERTEX_SHADER, vertexShaderFilename));
            theGeoShader = Framework.loadShader(GL_GEOMETRY_SHADER, geometryShaderFilename);
            shaderList.add(theGeoShader);
            shaderList.add(Framework.loadShader(GL_FRAGMENT_SHADER,	fragmentShaderFilename));

            theProgram = Framework.createProgram(shaderList);

            UniformLocations.projectionBlock = glGetUniformBlockIndex(theProgram, "Projection");
            UniformLocations.faceMatrix = glGetUniformLocation(theProgram, "faceMatrix");
            UniformLocations.diffuseTex = glGetUniformLocation(theProgram, "diffuseColorTex");
            UniformLocations.depthTex = glGetUniformLocation(theProgram, "depthTex");
            UniformLocations.lightPos = glGetUniformLocation(theProgram, "lightPos");

            FloatBuffer f = BufferUtils.createFloatBuffer(9 * 6);

            f.put(new float[] {0f,0f,-1f,0f,1f,0f,1f,0f,0f});
            f.put(new float[] {0f,0f,1f,0f,1f,0f,-1f,0f,0f});
            f.put(new float[] {1f,0f,0f,0f,0f,-1f,0f,1f,0f});
            f.put(new float[] {1f,0f,0f,0f,0f,1f,0f,-1f,0f});
            f.put(new float[] {1f,0f,0f,0f,1f,0f,0f,0f,1f});
            f.put(new float[] {-1f,0f,0f,0f,1f,0f,0f,0f,-1f});

            f.flip();
            glUseProgram(theProgram);
            glUniform1i(UniformLocations.diffuseTex, 1);
            glUniform1i(UniformLocations.depthTex, 2);
            Cache.diffuseTexBindingIndex = 1;
            Cache.depthTexBindingIndex = 2;
            glUniformMatrix3(UniformLocations.faceMatrix, false, f);
            glUniform3f(UniformLocations.lightPos, -5f, -5f, -10f);
            glUseProgram(0);
        }
    }


    public static int viewProjBindingPoint;
    public static void bindViewProjectionBlock(int projectionBindingIndex) {
        viewProjBindingPoint = projectionBindingIndex;
        glUniformBlockBinding(QuadData.theProgram, QuadData.UniformLocations.projectionBlock, projectionBindingIndex);
        glUniformBlockBinding(QuadAmbientData.theProgram, QuadAmbientData.UniformLocations.projectionBlock, projectionBindingIndex);
        glUniformBlockBinding(QuadShadowData.theProgram, QuadShadowData.UniformLocations.projectionBlock, projectionBindingIndex);
        glUniformBlockBinding(alreadyClipData.theProgram, alreadyClipData.UniformLocations.projectionBlock, projectionBindingIndex);


    }
    public static int lightBlockBindingPoint;
    public static void bindLightBlock(int lightBindingIndex) {
        lightBlockBindingPoint = lightBindingIndex;
        glUniformBlockBinding(QuadData.theProgram, QuadData.UniformLocations.lightBlock, lightBindingIndex);

        glUniformBlockBinding(QuadAmbientData.theProgram, QuadAmbientData.UniformLocations.lightBlock, lightBindingIndex);

        System.out.print(glGetInteger64(GL_MAX_GEOMETRY_TOTAL_OUTPUT_COMPONENTS));


    }


}
