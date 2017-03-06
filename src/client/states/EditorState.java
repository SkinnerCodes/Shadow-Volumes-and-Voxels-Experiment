package client.states;

import chunks.ChunkMap;
import chunks.NodeSearchResult;
import chunks.Voxel;
import client.EditorForm;
import client.World;
import optic.containers.Ray;
import optic.framework.Camera3D;
import optic.framework.GameState;
import optic.math.Vec2;
import optic.math.Vec3;
import optic.math.Vec4;
import optic.math.Vec4I;
import optic.misc.Timer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.glViewport;

/**
 * Created with IntelliJ IDEA.
 * User: Joseph
 * Date: 1/25/14
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditorState implements GameState {

    public enum BrushMode {
        Unknown,
        Select,
        AddSubtract,
        Paint,
        Smooth
    }

    private EditorForm form;

    private World world;
    private Camera3D camera;

    private BrushMode brushMode = BrushMode.Select;
    private int brushLevel = 0;


    public void setBrushMode(BrushMode mode) {
        brushMode = mode;
        System.out.println("Brush mode: " + mode.toString());
    }

    public void setBrushLevel(int level) {
        brushLevel = level;
        System.out.println("Brush level: " + brushLevel);
    }

    public void init() {
        form = new EditorForm(this);

        this.camera = new Camera3D();
        world = new World();
    }

    @Override
    public void update(float lastFrameDuration) {
        lastFrameDuration /= 100;
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            camera.Thrust(lastFrameDuration);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            camera.Thrust(-lastFrameDuration);

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            camera.StrafeHorz(-lastFrameDuration);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            camera.StrafeHorz(lastFrameDuration);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            camera.StrafeVert(lastFrameDuration);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            camera.StrafeVert(-lastFrameDuration);
        }
        lastFrameDuration *= 5;
        if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
            camera.Pitch(-lastFrameDuration * 150);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            camera.Pitch(lastFrameDuration * 150);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_J)) {
            camera.Yaw(lastFrameDuration * 150);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            camera.Yaw(-lastFrameDuration * 150);
        }

        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_ESCAPE:
                        //leaveMainLoop();
                        break;
                }
            }
        }

        while (Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                if (Mouse.getEventButton() == 0) {
                    if(Mouse.isButtonDown(0)) {
                        Ray mouseRay = camera.getRayFromScreen(new Vec2(Mouse.getX(), Mouse.getY()));

                        NodeSearchResult result = ChunkMap.getNearestVoxelInRay(mouseRay);

                        if (result == null)
                            return;
                        Vec3 intersectionPoint = result.node.getIntersectionPoint(mouseRay, result.rayResult);

                        Voxel v = (Voxel) result.node;
                        v.subtract(result.rayResult.facet, intersectionPoint, brushLevel);
                        ChunkMap.resetOcclusion();
                        ChunkMap.calculateOcclusion();
                    }
                }
                if (Mouse.getEventButton() == 1) {
                    if(Mouse.isButtonDown(1)) {
                        Ray mouseRay = camera.getRayFromScreen(new Vec2(Mouse.getX(), Mouse.getY()));

                        NodeSearchResult result = ChunkMap.getNearestVoxelInRay(mouseRay);

                        if (result == null)
                            return;
                        Vec3 intersectionPoint = result.node.getIntersectionPoint(mouseRay, result.rayResult);
                        Voxel v = (Voxel) result.node;
                        Voxel newVoxel = v.extrude(result.rayResult.facet, v.type, intersectionPoint, brushLevel);
                        ChunkMap.resetOcclusion();
                        ChunkMap.calculateOcclusion();
                    }
                }
            }
        }
    }

    Timer timer = new Timer(Timer.Type.LOOP, 100f);
    float elapsedTime = 0f;
    @Override
    public void display(float lastFrameDuration) {
        elapsedTime += 4 * lastFrameDuration;
        if(elapsedTime > 300000f) elapsedTime = 0f;
        timer.update(elapsedTime);
        world.testLight.WorldPosition = Vec3.add(camera.eyePosition, camera.getRayFromScreen(new Vec2(Mouse.getX(), Mouse.getY())).direction.scale(4f));
        world.testLight.Intensity = new Vec4(new Vec3((float)Math.sin(timer.getAlpha() * 20 * 3.14), (float)Math.cos(timer.getAlpha() * 10 * 3.14), (float)Math.cos(timer.getAlpha() * 30 * 3.14)).normalize().scale(2f), 1f);
//        world.lightSystem.SunDirection = (new Vec4((float)Math.sin(timer.getAlpha() * 2 * 3.14), 0.5f, (float)Math.cos(timer.getAlpha() * 2 * 3.14), 0f)).normalize();
        world.lightSystem.RenderScene(world, camera);
    }
    @Override
    public void reshapeWindow() {
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        camera.viewPort = new Vec4I(0,0,Display.getWidth(), Display.getHeight());
    }

}
