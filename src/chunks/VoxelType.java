package chunks;

import client.rendering.TextureManager;
import optic.containers.AxialDirection;
import optic.image.Texture;

public enum VoxelType {
    // Constructor Calls
    Null(0, null),
    Test(1, TextureManager.TestTexture),
    Dino(2, TextureManager.DinoTexture);

    // Internal Variables
    private final int value;
    private final Texture[] faceTextures = new Texture[6];

    // Internal Constructors
    private VoxelType(int value, Texture texture) {
        this.value = value;
        faceTextures[0] = texture;
        faceTextures[1] = texture;
        faceTextures[2] = texture;
        faceTextures[3] = texture;
        faceTextures[4] = texture;
        faceTextures[5] = texture;
    }

    private VoxelType(int value, Texture tex0, Texture tex1, Texture tex2, Texture tex3, Texture tex4, Texture tex5) {
        this.value = value;
        faceTextures[0] = tex0;
        faceTextures[1] = tex1;
        faceTextures[2] = tex2;
        faceTextures[3] = tex3;
        faceTextures[4] = tex4;
        faceTextures[5] = tex5;
    }


    // Public Properties
    public int getValue() {
        return value;
    }

    public Texture getFaceTexture(int faceIndex) {
        return faceTextures[faceIndex];
    }

    public Texture getFaceTexture(AxialDirection face) {
        return faceTextures[face.getValue()];
    }

}
