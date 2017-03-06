package client.rendering;

import optic.framework.Framework;
import optic.image.DdsLoader;
import optic.image.PNGDecoder;
import optic.image.Texture;
import optic.misc.ImageSet;
import optic.misc.TextureGenerator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL21.GL_SRGB8_ALPHA8;
import static org.lwjgl.opengl.GL33.*;

/**
 * Created by Thomas on 1/4/14.
 */
public class TextureManager {
    public static void initialize()
    {
        TestTexture = new Texture(loadPNGTexture("brick.png"));
        //DinoTexture = new Texture(loadPNGTexture("Portfolio.png"));
        LinearSampler = createLinearSampler();
        AnisotropicSampler = createAnisotropicSampler();
    }
    public static Texture TestTexture;
    public static Texture DinoTexture;
    public static int LinearSampler;
    public static int AnisotropicSampler;

    public static int loadPNGTexture(String filePath) {
//        filePath = Framework.findFileOrThrow(filePath);
        ByteBuffer buf = null;
        int tWidth = 0;
        int tHeight = 0;

        try {
            // Open the PNG file as an InputStream
            InputStream in = new FileInputStream(filePath);
            // Link the PNG decoder to this stream
            PNGDecoder decoder = new PNGDecoder(in);

            // Get the width and height of the texture
            tWidth = decoder.getWidth();
            tHeight = decoder.getHeight();


            // Decode the PNG file in a ByteBuffer
            buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA); //stride is length in bytes from one pixel in the opengl texture to a vertically adjacent one, I don't know why it must be specified.
            buf.flip();

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Create a new texture object in memory and bind it
        int texId = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

        // All RGB bytes are aligned to each other and each component is 1 byte
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // Upload the texture data and generate mip maps (for scaling)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, tWidth, tHeight, 0, //This should be made to work for alpha
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        // Setup the ST coordinate system
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        // Setup what to do when the texture has to be scaled
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
//                GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
//                GL11.GL_LINEAR_MIPMAP_LINEAR);

        //some type of error checking on opengl side was removed here

        return texId;
    }

    public static int loadDDSTexture(String filePath) {
        int textureID = -1;
        try	{
            filePath = Framework.findFileOrThrow(filePath);
            ImageSet imageSet = DdsLoader.loadFromFile(filePath);


            textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);

            TextureGenerator.OpenGLPixelTransferParams xfer = TextureGenerator.getUploadFormatType(imageSet.getFormat(), 0);

            for (int mipmapLevel = 0; mipmapLevel < imageSet.getMipmapCount(); mipmapLevel++) {
                ImageSet.SingleImage image = imageSet.getImage(mipmapLevel, 0, 0);
                ImageSet.Dimensions imageDimensions = image.getDimensions();

                glTexImage2D(GL_TEXTURE_2D, mipmapLevel, GL_SRGB8_ALPHA8, imageDimensions.width, imageDimensions.height, 0,
                        xfer.format, xfer.type, image.getImageData());
            }

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, imageSet.getMipmapCount() - 1);

            glBindTexture(GL_TEXTURE_2D, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textureID;
    }

    public static int createLinearSampler() {
        int samplerID;
        samplerID = glGenSamplers();
        glSamplerParameteri(samplerID, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glSamplerParameteri(samplerID, GL_TEXTURE_WRAP_T, GL_REPEAT);


        // Linear mipmap linear
        glSamplerParameteri(samplerID, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glSamplerParameteri(samplerID, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        return samplerID;


    }

    public static int createAnisotropicSampler()
    {
        int samplerID;
        samplerID = glGenSamplers();
        glSamplerParameteri(samplerID, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glSamplerParameteri(samplerID, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Max anisotropic
        float maxAniso = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);

        glSamplerParameteri(samplerID, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glSamplerParameteri(samplerID, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glSamplerParameterf(samplerID, GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAniso);

        return samplerID;
    }


}
