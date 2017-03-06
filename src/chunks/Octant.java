package chunks;


public enum Octant {
    TOP_FRONT_RIGHT(0, 1, 1, 1),        // + + +
    TOP_BACK_RIGHT(1, -1, 1, 1),        // - + +
    TOP_BACK_LEFT(2, -1, -1, 1),        // - - +
    TOP_FRONT_LEFT(3, 1, -1, 1),        // + - +
    BOTTOM_FRONT_RIGHT(4, 1, 1, -1),    // + + -
    BOTTOM_BACK_RIGHT(5, -1, 1, -1),    // - + -
    BOTTOM_BACK_LEFT(6, -1, -1, -1),    // - - -
    BOTTOM_FRONT_LEFT(7, 1, -1, -1),    // + - -
    PARENT(-1, 0, 0, 0);

    private final int value;
    private final int x;
    private final int y;
    private final int z;
    private Octant(int value, int x, int y, int z) {
        this.value = value;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getValue() {
        return value;
    }

    public int getXSign() {
        return x;
    }

    public int getYSign() {
        return y;
    }

    public int getZSign() {
        return z;
    }

    public static Octant fromSign(int x, int y, int z) {
        if (x >= 0)
            if (y >= 0)
                if (z >= 0)
                    return TOP_FRONT_RIGHT;
                else
                    return BOTTOM_FRONT_RIGHT;
            else
                if (z >= 0)
                    return TOP_FRONT_LEFT;
                else
                    return BOTTOM_FRONT_LEFT;
        else
            if (y >= 0)
                if (z >= 0)
                    return TOP_BACK_RIGHT;
                else
                    return BOTTOM_BACK_RIGHT;
            else
                if (z >= 0)
                    return TOP_BACK_LEFT;
                else
                    return BOTTOM_BACK_LEFT;
    }

    public static Octant fromValue(int value) {
        switch (value) {
            case 0:
                return TOP_FRONT_RIGHT;
            case 1:
                return TOP_BACK_RIGHT;
            case 2:
                return TOP_BACK_LEFT;
            case 3:
                return TOP_FRONT_LEFT;
            case 4:
                return BOTTOM_FRONT_RIGHT;
            case 5:
                return BOTTOM_BACK_RIGHT;
            case 6:
                return BOTTOM_BACK_LEFT;
            case 7:
                return BOTTOM_FRONT_LEFT;
            case -1:
                return PARENT;
        }
        return null;
    }
}
