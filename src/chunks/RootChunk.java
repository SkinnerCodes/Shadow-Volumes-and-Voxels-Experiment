package chunks;

import client.rendering.VoxelDrawer;
import optic.framework.GPUBuffer;
import optic.image.Texture;
import optic.math.Vec3I;

import java.util.Map;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;


public class RootChunk extends Chunk {

    public boolean bufferValid = false;
    public GPUBuffer buffer = new GPUBuffer(GL_DYNAMIC_DRAW);
    public Map<Texture, VoxelDrawer.OffsetAndInstances> bufferMap;

    public RootChunk(Vec3I rootChunkIndex) {
        super(null, Octant.PARENT, ChunkMap.OT_ROOT_LEVEL);
        position.rootIndex = rootChunkIndex;
    }

    @Override
    public void invalidateBuffer() {
        bufferValid = false;
    }

    @Override
    public void draw() {
        if (!bufferValid) {
            System.out.println("validating buffer");
            VoxelDrawer.reset();
            for (int index = 0; index < 8; index++) {
                if(children[index] != null)
                    children[index].draw();
            }
            bufferMap = VoxelDrawer.flushToGPU(buffer);
            bufferValid = true;
        }

        VoxelDrawer.drawChunk(bufferMap, buffer);
    }

    @Override
    public void calculateOcclusion() {
        for (int index = 0; index < 8; index++) {
            if(children[index] != null)
                children[index].calculateOcclusion();
        }
        invalidateBuffer();
    }

    public void drawShadowVolumes() {
        if (!bufferValid) {
            VoxelDrawer.reset();
            for (int index = 0; index < 8; index++) {
                if(children[index] != null)
                    children[index].draw();
            }
            bufferMap = VoxelDrawer.flushToGPU(buffer);
            bufferValid = true;
        }

        VoxelDrawer.drawChunkShadowVolumes(bufferMap, buffer);
    }

}
