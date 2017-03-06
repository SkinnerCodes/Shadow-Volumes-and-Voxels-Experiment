package chunks;

import optic.containers.Ray;

import java.util.Comparator;


public class NodeSearchResult {
    public Node node;
    public Ray.RayResult rayResult;

    public static class NodeDistanceComparator implements Comparator<NodeSearchResult>
    {
        @Override
        public int compare(NodeSearchResult x, NodeSearchResult y) {
            if (x.rayResult.distance < y.rayResult.distance)
                return -1;
            if (x.rayResult.distance > y.rayResult.distance)
                return 1;
            return 0;
        }
    }
}
