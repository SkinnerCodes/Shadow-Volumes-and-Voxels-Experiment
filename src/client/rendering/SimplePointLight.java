package client.rendering;

import optic.math.Vec3;
import optic.math.Vec4;

/**
 * Created by Thomas on 1/4/14.
 */
public class SimplePointLight implements PointLight {

    public SimplePointLight(Vec3 initialPos, Vec4 intensity){
        WorldPosition = initialPos;
        Intensity = intensity;
    }
    public Vec3 WorldPosition;
    public Vec4 Intensity;
    @Override
    public Vec3 getWorldPosition() {
        return WorldPosition;
    }

    @Override
    public Vec4 getLightIntensity() {
        return Intensity;
    }
}
