package chunks;

import optic.containers.Ray;
import optic.math.Vec3I;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class ChunkMap {
    public static final int OT_N_LEVELS = 6;       // Number of possible levels in the octree.
    public static final int OT_ROOT_LEVEL = OT_N_LEVELS - 1;
    public static final int OT_ROOT_DIAMETER = (2 << OT_ROOT_LEVEL) / 2;
    public static final int OT_MAX_VOXELS = OT_ROOT_DIAMETER * OT_ROOT_DIAMETER * OT_ROOT_DIAMETER;

    public static Vec3I worldCenterIndex = new Vec3I(0);      // The index of the root chunk that represents the center of the world.

    public static HashMap<Vec3I, RootChunk> chunks = new HashMap<>();

    public static NodeSearchResult getNearestVoxelInRay(Ray ray) {
        Comparator<NodeSearchResult> comparator = new NodeSearchResult.NodeDistanceComparator();
        PriorityQueue<NodeSearchResult> queue = new PriorityQueue<>(10, comparator);


        for (RootChunk child : chunks.values()) {
            if (child == null)
                continue;
            Ray.RayResult out = new Ray.RayResult();
            if (child.isIntersectingRay(ray, out)) {
                NodeSearchResult searchResult = new NodeSearchResult();
                searchResult.node = child;
                searchResult.rayResult = out;
                queue.add(searchResult);
            }
        }

        while (queue.size() != 0) {
            NodeSearchResult result = queue.remove();

            RootChunk childChunk = (RootChunk) result.node;
            NodeSearchResult childTestResult = childChunk.getNearestVoxelInRay(ray);
            if (childTestResult != null)
                return childTestResult;
        }
        return null;
    }

    public static void putRootChunk(RootChunk chunk) {
        chunks.put(chunk.position.rootIndex, chunk);
    }

    public static RootChunk getRootChunk(Vec3I index) {
        return chunks.get(index);
    }

    public static RootChunk getRootChunk(int x, int y, int z) {
        return getRootChunk(new Vec3I(x, y, z));
    }

    public static void invalidateWorld() {
        for (RootChunk chunk : chunks.values()) {
            if (chunk != null)
                chunk.invalidateBuffer();
        }
    }

    public static void draw() {
        for (RootChunk chunk : chunks.values()) {
            if (chunk != null)
                chunk.draw();
        }
    }

    public static void resetOcclusion() {
        for (RootChunk chunk : chunks.values()) {
            if (chunk != null)
                chunk.resetOcclusion();
        }
    }

    public static void calculateOcclusion() {
        for (RootChunk chunk : chunks.values()) {
            if (chunk != null)
                chunk.calculateOcclusion();
        }
    }

    public static void testOcclusion() {
        for (RootChunk chunk : chunks.values()) {
            if (chunk != null)
                chunk.testOcclusion();
        }
    }

    public static void drawShadowVolumes() {
        for (RootChunk chunk : chunks.values()) {
            if (chunk != null)
                chunk.drawShadowVolumes();
        }
    }

}
