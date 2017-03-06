package optic.framework;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

/**
 * Created by Thomas on 1/7/14.
 */
public class GPUBuffer {
    public int bufferID;
    public int size;
    int usage;
    public int target;
    public GPUBuffer(int usage) {
        bufferID = glGenBuffers();
        this.usage = usage;
    }
    public void bind(int target) {
        this.target = target;
        glBindBuffer(target, bufferID);
    }
    public void allocate(int size)
    {
        glBufferData(target, size, usage);
        this.size = size;
    }
    public void submitData(int offsetGPU, ByteBuffer data) {
        if(size >= (data.remaining() + offsetGPU))
        {
            glBufferSubData(target, offsetGPU, data);
        }
        else
        {
            if(offsetGPU > 0) throw new RuntimeException("you may be deleting data at the beginning of the buffer");
            this.size = data.remaining();
            glBufferData(target, data, usage);
        }

    }
    public void submitData(int offsetGPU, FloatBuffer data) {
        if(size >= (data.remaining() + offsetGPU))
        {
            glBufferSubData(target, offsetGPU, data);
        }
        else
        {
            if(offsetGPU > 0) throw new RuntimeException("you may be deleting data at the beginning of the buffer");
            this.size = data.remaining();
            glBufferData(target, data, usage);
        }

    }
    public void submitData(int offsetGPU, IntBuffer data) {
        if(size >= (data.remaining() + offsetGPU))
        {
            glBufferSubData(target, offsetGPU, data);
        }
        else
        {
            if(offsetGPU > 0) throw new RuntimeException("you may be deleting data at the beginning of the buffer");
            this.size = data.remaining();
            glBufferData(target, data, usage);
        }

    }
    public void Delete() {
        glDeleteBuffers(bufferID);
    }
}
