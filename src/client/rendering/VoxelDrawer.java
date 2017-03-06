package client.rendering;

import chunks.ChunkMap;
import chunks.Voxel;
import optic.framework.GPUBuffer;
import optic.image.Texture;
import optic.math.Vec3;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL33.glBindSampler;

/**
 * Created by Thomas on 1/7/14.
 */
public class VoxelDrawer {
    static ByteBuffer quadBuffer = BufferUtils.createByteBuffer(ChunkMap.OT_MAX_VOXELS * Quad.quadInstanceSize * 6);
    //use a dictionary bro
    static ArrayList<Voxel> voxels = new ArrayList<Voxel>(ChunkMap.OT_MAX_VOXELS /2);
    static int lastVoxel = -1;

    public static class OffsetAndInstances {
        public int offset = 0 ;
        public int instances = 0;
        public OffsetAndInstances(int offset, int instances) {this.offset = offset; this.instances = instances;}
    }
    static Map<Texture,OffsetAndInstances> bufferMap = new HashMap<Texture, OffsetAndInstances>();
    public static void drawVoxel(Voxel voxel)
    {
        voxels.add(voxel);
        lastVoxel++;
    }

    public static Map<Texture,OffsetAndInstances> flushToGPU(GPUBuffer buffer) {
        quadBuffer.clear();

        int quadsInBuffer = 0;
        for(int i = 0; i <= lastVoxel; i++)
        {
            //collect texture amounts
            for(int j = 0; j < 6; j++) {
                if (voxels.get(i).isSideOccluded(j)) //TODO: calling get twice is bad
                    continue;
                quadsInBuffer++;
                Texture text = voxels.get(i).type.getFaceTexture(j);
                OffsetAndInstances oai = bufferMap.get(text);
                if(oai == null)
                {
                    bufferMap.put(text, new OffsetAndInstances(0,1));
                }
                else {
                    oai.instances++;
                }
            }

        }
        System.out.println("QUADS IN BUFFER: " + quadsInBuffer);

        int offset = 0;
        int temp = 0;
        for(Texture text : bufferMap.keySet())
        {
            OffsetAndInstances i = bufferMap.get(text);
            temp = i.instances * Quad.quadInstanceSize;
            i.offset = offset;
            offset = offset + temp;
        }
        HashMap<Texture, Integer> instanceIncrements = new HashMap<Texture, Integer>();

        for(int i = 0; i <= lastVoxel; i++)
        {
            //put voxel data in buffer
            Voxel voxel = voxels.get(i);
            for(int j = 0; j < 6; j++) {
                if (voxel.isSideOccluded(j))
                    continue;
                Texture text = voxel.type.getFaceTexture(j);
                OffsetAndInstances oai = bufferMap.get(text);
                Integer increment = instanceIncrements.get(text);
                if (increment == null){
                    increment = 0;
                    instanceIncrements.put(text, increment);
                }
                Vec3 position = voxel.position.getRenderPosition();
                quadBuffer.putFloat(oai.offset + increment * Quad.quadInstanceSize, position.x);
                quadBuffer.putFloat(oai.offset + 4 + increment * Quad.quadInstanceSize, position.y);
                quadBuffer.putFloat(oai.offset + 8 + increment * Quad.quadInstanceSize, position.z);
                quadBuffer.putFloat(oai.offset + 12 + increment * Quad.quadInstanceSize, voxel.diameter);
                quadBuffer.putInt(oai.offset + 16 + increment * Quad.quadInstanceSize, j);
                quadBuffer.put(oai.offset + 20 + increment * Quad.quadInstanceSize, voxel.tint.r);
                quadBuffer.put(oai.offset + 21 + increment * Quad.quadInstanceSize, voxel.tint.g);
                quadBuffer.put(oai.offset + 22 + increment * Quad.quadInstanceSize, voxel.tint.b);
                quadBuffer.put(oai.offset + 23 + increment * Quad.quadInstanceSize, voxel.tint.a);
                quadBuffer.put(oai.offset + 24 + increment * Quad.quadInstanceSize, voxel.glow);
                instanceIncrements.put(text, increment +1);
            }

        }
        buffer.bind(GL_ARRAY_BUFFER);
        buffer.submitData(0, quadBuffer);

        return bufferMap;

    }
    public static void reset() {
        bufferMap = new HashMap<Texture, OffsetAndInstances>();
        lastVoxel = -1;
        quadBuffer.clear();
        voxels = new ArrayList<Voxel>(ChunkMap.OT_MAX_VOXELS /2);
    }
    public static void drawChunk(Map<Texture, OffsetAndInstances> map, GPUBuffer buffer)
    {
        for(Texture text : map.keySet()){

            glActiveTexture(GL_TEXTURE0 + ShaderPrograms.QuadData.Cache.colorTextureBindingIndex);
            glBindTexture(GL_TEXTURE_2D, text.TextureID);
            glBindSampler(ShaderPrograms.QuadData.Cache.colorTextureBindingIndex, TextureManager.AnisotropicSampler);
            OffsetAndInstances oai = map.get(text);
            Quad.render(buffer.bufferID, oai.offset, oai.instances);
        }
    }
    public static void drawChunkShadowVolumes(Map<Texture, OffsetAndInstances> map, GPUBuffer buffer)
    {
        //this can be optimized i think
        for(Texture text : map.keySet()){
            OffsetAndInstances oai = map.get(text);
            Quad.renderShadowVolume(buffer.bufferID, oai.offset, oai.instances);
        }
    }

}
