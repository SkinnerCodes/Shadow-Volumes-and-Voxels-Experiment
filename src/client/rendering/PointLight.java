package client.rendering;

import optic.math.Vec3;
import optic.math.Vec4;

/**
 * Created by Thomas on 1/4/14.
 */
public interface PointLight {
    public Vec3 getWorldPosition();
    public Vec4 getLightIntensity();

}
