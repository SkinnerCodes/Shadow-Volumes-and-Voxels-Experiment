package client;

import chunks.*;
import client.rendering.LightSystem;
import client.rendering.ShaderPrograms;
import client.rendering.SimplePointLight;
import optic.containers.Color;
import optic.framework.Camera3D;
import optic.math.Vec3;
import optic.math.Vec3I;
import optic.math.Vec4;

import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * Created by Thomas on 2/1/14.
 */
public class World {

    public LightSystem lightSystem;


    public static void initWorldShaders() {

    }

    public World() {

        ArrayList<Voxel> splitQueue = new ArrayList<Voxel>();
        RootChunk root = new RootChunk(new Vec3I(0));
        Random r = new Random();
        for (int i = 0; i < 8; i++) {
            splitQueue.add(root.createChildVoxel(Octant.fromValue(i), VoxelType.Test, false));
        }
        while (splitQueue.size() > 0) {
            Voxel voxel = splitQueue.get(0);
            if (voxel.level > 0) {
                Chunk chunk = voxel.split();
                for (int i = 0; i < 8; i++) {
                    ((Voxel) chunk.children[i]).tint = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255) ,r.nextInt(255));
                    splitQueue.add((Voxel) chunk.children[i]);
                }
            }
            splitQueue.remove(voxel);
        }
        ChunkMap.putRootChunk(root);


        ChunkMap.calculateOcclusion();

        lightSystem = new LightSystem();
        lightSystem.showPointLight(testLight);


    }

    public SimplePointLight testLight = new SimplePointLight(new Vec3(1f), new Vec4(1f,0.5f,0.5f,1f));



    public void update() {
        //physics go here

    }

    public void initDraw(Camera3D camera) {
        //submit ViewProjection matrix to shared block / shader programs

//        //bind a texture to the location of a uniform sampler
//        glActiveTexture(GL_TEXTURE0 + ShaderPrograms.alreadyClipData.Cache.colorTextureBindingIndex);
//        glBindTexture(GL_TEXTURE_2D, TextureManager.TestTexture.TextureID);
//        glBindSampler(ShaderPrograms.alreadyClipData.Cache.colorTextureBindingIndex, TextureManager.AnisotropicSampler);
//        //

        //draw quads
        glUseProgram(ShaderPrograms.QuadData.theProgram);
        //glUniform1i(ShaderPrograms.QuadData.UniformLocations.numberOfLightsUnif, lightSystem.getNumLights());
    }

    public void draw(Camera3D camera) {
        glUseProgram(ShaderPrograms.QuadShadowData.theProgram);
        ChunkMap.drawShadowVolumes();
        glUseProgram(ShaderPrograms.QuadData.theProgram);
        ChunkMap.draw();
    }
    public void Render(Camera3D camera) {

    }

}
