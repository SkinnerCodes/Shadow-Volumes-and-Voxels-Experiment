package chunks;

import client.rendering.VoxelDrawer;
import optic.containers.AxialDirection;
import optic.containers.Color;
import optic.math.Vec3;
import optic.math.Vec3I;

import java.util.Random;


public class Voxel extends Node {

    //region Public Variables
    public VoxelType type;
    public Color tint = Color.white;
    public byte glow = (byte) 0;
    public int[] occlusionAmount = new int[6];
    //endregion

    //region Constructors
    public Voxel(Chunk parent, Octant relativePosition, VoxelType type){
        super(parent, relativePosition, parent.level - 1);
        this.type = type;
        Random r = new Random();
        //this.tint = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255), 255);
    }
    //endregion

    //region Public Methods
    public Chunk split() {
        Chunk chunk = parent.createChildChunk(octant);
        chunk.createChildVoxel(Octant.TOP_FRONT_RIGHT, type, false);
        chunk.createChildVoxel(Octant.TOP_BACK_RIGHT, type, false);
        chunk.createChildVoxel(Octant.TOP_BACK_LEFT, type, false);
        chunk.createChildVoxel(Octant.TOP_FRONT_LEFT, type, false);
        chunk.createChildVoxel(Octant.BOTTOM_FRONT_RIGHT, type, false);
        chunk.createChildVoxel(Octant.BOTTOM_BACK_RIGHT, type, false);
        chunk.createChildVoxel(Octant.BOTTOM_BACK_LEFT, type, false);
        chunk.createChildVoxel(Octant.BOTTOM_FRONT_LEFT, type, false);
        return chunk;
    }

    public Voxel extrude(AxialDirection direction) {
        return extrude(direction, type);
    }

    public Voxel extrude(AxialDirection direction, VoxelType type) {
        return createVoxel(getAdjacentNodeLoc(direction), level, type, false);
    }

    public Voxel extrude(final AxialDirection direction, final VoxelType type, final Vec3 startFrom, final int level)
    {
        Vec3 searchPoint = new Vec3(startFrom);     // Create new reference so we don't affect original.
        searchPoint.x += direction.getX() * 0.5f;
        searchPoint.y += direction.getY() * 0.5f;
        searchPoint.z += direction.getZ() * 0.5f;

        Vec3I searchPointI = new Vec3I((int) Math.floor(searchPoint.x), (int) Math.floor(searchPoint.y), (int) Math.floor(searchPoint.z));
        NodePosition creationPoint = new NodePosition(new Vec3I(0), searchPointI);

        creationPoint.clampRelPosition();

        return createVoxel(creationPoint, level, type, true);
    }

    public boolean subtract(final AxialDirection direction, final Vec3 startFrom, final int level) {
        System.out.println("start subtract");
        Vec3 searchPoint = new Vec3(startFrom);     // Create new reference so we don't affect original.
        searchPoint.x -= direction.getX() * 0.5f;
        searchPoint.y -= direction.getY() * 0.5f;
        searchPoint.z -= direction.getZ() * 0.5f;

        Vec3I searchPointI = new Vec3I((int) Math.floor(searchPoint.x), (int) Math.floor(searchPoint.y), (int) Math.floor(searchPoint.z));
        NodePosition deletionPoint = new NodePosition(new Vec3I(0), searchPointI);

        deletionPoint.clampRelPosition();

        return deleteVoxel(deletionPoint, level, true);
    }

    //endregion

    @Override
    public void draw() {
        VoxelDrawer.drawVoxel(this);
    }

    @Override
    public void resetOcclusion() {
        for (int i = 0; i < 6; i++)
            occlusionAmount[i] = 0;
    }

    @Override
    public void calculateOcclusion() {
        for (int i = 0; i < 6; i++) {
            AxialDirection searchDirection = AxialDirection.fromInteger(i);
            NodePosition searchPosition = getAdjacentNodeLoc(searchDirection);
            Node searchResult = startNodeSearch(searchPosition, level);
            if (searchResult == null)
                continue;
            if (searchResult instanceof Chunk)
                continue;
            Voxel foundVoxel = (Voxel) searchResult;
            if (foundVoxel.level >= level)
            {
                occlusionAmount[searchDirection.getValue()] += foundVoxel.diameter * foundVoxel.diameter;
                foundVoxel.occlusionAmount[AxialDirection.getOppositeFace(searchDirection).getValue()] += diameter * diameter;
            }
       }
    }

    @Override
    public void testOcclusion() {
        if (isSideOccluded(0)) {
            this.tint = Color.green;
            invalidateBuffer();
        } else {
            this.tint = Color.white;
            invalidateBuffer();
        }
    }

    public boolean isSideOccluded(int sideIndex){
        return (occlusionAmount[sideIndex] >= diameter*diameter);
    }

    @Override
    public void debugWriteout() {
        for (int i = 0; i < ChunkMap.OT_ROOT_LEVEL - level; i++)
            System.out.print("#");
        System.out.print(level + " VOXEL " + octant.toString());
        System.out.println();
    }

}
