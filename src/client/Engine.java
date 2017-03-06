package client;

import client.rendering.Quad;
import client.rendering.Quad2D;
import client.rendering.ShaderPrograms;
import client.rendering.TextureManager;
import client.states.EditorState;
import optic.framework.Framework;
import optic.framework.GameEngine;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class Engine extends GameEngine {



    public static void main(String[] args) {
        Framework.CURRENT_TUTORIAL_DATAPATH = "/client/rendering/data/";
        new Engine().start(700, 700);
    }
	
	
	
	GameStateSwitcher gameStateSwitcher;

    @Override
    protected void init() {
//        try {
//            ShaderPrograms.initializePrograms();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            System.exit(-1);
//        }

        ShaderPrograms.initializePrograms();
        gameStateSwitcher = new GameStateSwitcher();
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDepthFunc(GL_LEQUAL);
        glDepthRange(0, 1);
        glEnable(GL_DEPTH_CLAMP);

        TextureManager.initialize();
        Quad.initialize(); Quad2D.initialize();

        gameStateSwitcher.activeState = new EditorState();
        gameStateSwitcher.initState();

    }

    float totalFrameTime = 0f;
    int framesCounted = 0;

    @Override
    protected void update() {

        //Display.sync(60);
        totalFrameTime += getLastFrameDuration();
        framesCounted++;

        if (totalFrameTime >= 250f)
        {
            float averageFrameTime = totalFrameTime / framesCounted;
            float averageFramesPerSecond = 1000f / averageFrameTime;
            totalFrameTime = 0f;
            framesCounted = 0;
            Display.setTitle("FPS: " + averageFramesPerSecond);
        }

        gameStateSwitcher.updateState(getLastFrameDuration());
    }


    @Override
    protected void display() {
        if (useGammaDisplay) {
            glEnable(GL_FRAMEBUFFER_SRGB);
        } else {
            glDisable(GL_FRAMEBUFFER_SRGB);
        }


        glClearColor(.4f, .5f, .6f, 1f);
        glClearDepth(1.0f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        gameStateSwitcher.displayState(getLastFrameDuration());
//
//        String e = glGetShaderInfoLog(ShaderPrograms.QuadShadowData.theGeoShader,20000);
//        System.out.println(e);

    }


    @Override
    protected void reshape(int width, int height) {
        gameStateSwitcher.reshapeWindow();

    }

    private boolean useGammaDisplay = true;


}