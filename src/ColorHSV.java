public final class ColorHSV {
    // CONSTANTS
    public static final ColorHSV WHITE =  new ColorHSV(0, 0, 1);

    private static final float DELTA = 0.2f;
    private static final float MIN_DARKNESS = 0.4f;
    private static final float MAX_ANGLE = 360;
    private static final int INTERVAL = 6;

    private static final float ANGLE_CHANGE = MAX_ANGLE / INTERVAL;
    private static final int MAX_ALLOWED = (int)((1f- MIN_DARKNESS) / DELTA);

    private static final float INITIAL_H = 60;
    private static int colorCount = 0;

    // FIELDS
    private final float h;
    private final float s;
    private final float v;

    public ColorHSV() {
        this(INITIAL_H);
    }

    private ColorHSV(float h) {
        this(h, 1, 1);
    }

    private ColorHSV(float h, float s, float v) {
        Useful.require(0<= h && h < MAX_ANGLE);
        Useful.require(0<= s && s <= 1);
        Useful.require(0<= v && v <= 1);
        this.h = h;
        this.s = s;
        this.v = v;
    }

    public ColorHSV darker() {
        ++colorCount;
        if (colorCount == MAX_ALLOWED) {
            colorCount = 0;
            return new ColorHSV((h + ANGLE_CHANGE) % MAX_ANGLE);
        }
        float sPrime = Math.max(s - DELTA, 0);
        float vPrime = Math.max(v - DELTA, 0);
        return new ColorHSV(h, sPrime, vPrime);
    }

    public java.awt.Color toAwtColor() {
        float c = v * s;
        float x = c* ( 1 - Math.abs((h / 60) % 2 - 1));
        float m = v - c;

        float r = 0;
        float g = 0;
        float b = 0;

        if (h < 60) {
            r = c;
            g = x;
            b = 0;
        } else if (60 <= h && h < 120) {
            r = x;
            g = c;
            b = 0;
        } else if (120 <= h && h < 180) {
            r = 0;
            g = c;
            b = x;
        } else if (180 <= h && h < 240) {
            r = 0;
            g = x;
            b = c;
        } else if (240 <= h && h < 300) {
            r = x;
            g = 0;
            b = c;
        } else {
            r = c;
            g = 0;
            b = x;
        }
        r += m;
        g += m;
        b += m;
        return new java.awt.Color(r, g, b);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ColorHSV) {
            ColorHSV col = (ColorHSV) other;
            return h == col.h && s == col.s && v == col.v;
        }
        return false;
    }
}
