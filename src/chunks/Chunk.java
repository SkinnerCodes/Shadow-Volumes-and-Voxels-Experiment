package chunks;

import optic.containers.Ray;
import optic.math.Vec3I;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Chunk extends Node {
    public Node[] children = new Node[8];

    public Chunk(Chunk parent, Octant octant, int level){
        super(parent, octant, level);
    }


    @Override
    public Node startNodeSearch(NodePosition searchPosition, int minLevel) {
        if (!position.rootIndex.equals(searchPosition.rootIndex)) { // If target chunk is in a different root
            RootChunk root = ChunkMap.getRootChunk(searchPosition.rootIndex);
            if (root == null)
                return null;
            if (minLevel == root.level)
                return root;
            return root.searchForNode(searchPosition, minLevel, false);
        }
        return searchForNode(searchPosition, minLevel, true);
    }

    @Override
    public Node searchForNode(NodePosition searchPosition, int minLevel, boolean maySearchUp) {

        // Check if this is the node that the function is searching for.
        // If it is, we're done!
        if (searchPosition.relPosition.equals(position.relPosition)) {
            return this;
        }

        // Check if the bounding box of this node contains the target point.
        if (searchPosition.relPosition.isInBounds(position.relPosition, Vec3I.add(position.relPosition, diameter))) {
            if (minLevel == this.level)
                return this;
            // Search position is within this chunk!
            // Let's check each child.
            for (Node child : children) {
                if (child != null) {
                    Node result = child.searchForNode(searchPosition, minLevel, false);
                    if (result != null)
                        return result;
                }
            }
            return this; // We couldn't find the exact node, so we're going to return the closest parent.
        } else {
            // Search position is not within this chunk, so we move up a level
            if (maySearchUp)
                return parent.searchForNode(searchPosition, minLevel, true);
            else
                return null;
        }
    }

    public Voxel tryMerge(boolean recursive)
    {
        // If this chunk is already a root, it can't get any larger.
        if (level >= ChunkMap.OT_ROOT_LEVEL)
            return null;

        // Check if all children exist and are the same type.
        VoxelType lastType = VoxelType.Null;
        for (int i = 0; i < 8; i++)
        {
            if (children[i] == null)
                return null;
            if (children[i] instanceof Chunk)
                return null;
            Voxel v = (Voxel) children[i];
            if (i!=0 && v.type != lastType)
                return null;
            lastType = v.type;
        }

        // Cleanup
        for (int i = 0; i < 8; i++) {
            children[i] = null;
        }

        // Merge
        Voxel mergedVoxel = parent.createChildVoxel(octant, lastType, false);

        // Recurse
        if (recursive)
        {
            Voxel parentResult = parent.tryMerge(true);
            if (parentResult == null)
                return mergedVoxel; // Parent didn't merge into higher level voxel, so return this level.
            else
                return parent.tryMerge(recursive); // Parent merged this new voxel into a higher level voxel, so return that.
        }
        return mergedVoxel;
    }

    //-------------------------------------------------------------------------------
    //  Creates a new child chunk for this chunk in the specified octant.
    //-------------------------------------------------------------------------------
    public Chunk createChildChunk(Octant octant) {
        Chunk child = new Chunk(this, octant, level - 1);
        children[octant.getValue()] = child;
        return child;
    }

    //-------------------------------------------------------------------------------
    //  Creates a new voxel in this chunk in the specified octant.
    //-------------------------------------------------------------------------------
    public Voxel createChildVoxel(Octant octant, VoxelType voxelType, boolean shouldTryMerge) {
        invalidateBuffer();
        Voxel child = new Voxel(this, octant, voxelType);
        children[octant.getValue()] = child;
        if (shouldTryMerge)
            tryMerge(true);
        return child;
    }

    public boolean deleteChild(Octant octant) {
        invalidateBuffer();
        if (children[octant.getValue()] == null)
            return false;
        children[octant.getValue()] = null;
        return true;
    }

    public NodeSearchResult getNearestVoxelInRay(Ray ray) {
        Comparator<NodeSearchResult> comparator = new NodeSearchResult.NodeDistanceComparator();
        PriorityQueue<NodeSearchResult> queue = new PriorityQueue<>(10, comparator);


        for (Node child : children) {
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

            if (result.node instanceof Voxel) {
                if(result.rayResult.distance >= 0)
            {
                if (result.rayResult.distance >=0)
                    return result;
                else
                    continue;
            }
            }
            else {
                Chunk childChunk = (Chunk) result.node;
                NodeSearchResult childTestResult = childChunk.getNearestVoxelInRay(ray);
                if (childTestResult != null)
                    return childTestResult;
            }
        }
        return null;
    }

    @Override
    public void draw() {
        for (int index = 0; index < 8; index++) {
            if (children[index] != null)
                children[index].draw();
        }
    }

    @Override
    public void resetOcclusion() {
        for (int index = 0; index < 8; index++) {
            if(children[index] != null)
                children[index].resetOcclusion();
        }
    }

    @Override
    public void calculateOcclusion() {
        for (int index = 0; index < 8; index++) {
            if(children[index] != null)
                children[index].calculateOcclusion();
        }
    }

    @Override
    public void testOcclusion() {
        for (int index = 0; index < 8; index++) {
            if(children[index] != null)
                children[index].testOcclusion();
        }
    }

    @Override
    public void debugWriteout() {
        for (int i = 0; i < ChunkMap.OT_ROOT_LEVEL - level; i++)
            System.out.print("#");
        System.out.print(level + " OCTANT " + octant.toString() + " SZ " + diameter + " POS " + position.relPosition.x + ", " + position.relPosition.y + ", " + position.relPosition.z);
        System.out.println();
        for (int index = 0; index < 8; index++)
            if (children[index] != null)
                children[index].debugWriteout();
    }
}
