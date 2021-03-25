package me.jakev.extraeffects.particles;

import api.utils.particle.ModParticle;
import me.jakev.extraeffects.ExtraEffects;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 17.03.2021
 * TIME: 18:47
 */
public class GodParticle extends ModParticle {
    //TODO get set sizes
    //TODO color snapshots
    //TODO rotation over time: angles, boolean: perSecondOrAbsolute
    private Vector2f size;

    /**
     * will set the size snapshots for the particle. format: x,y,% lifetime.
     * MUST be in ascending order of lifetime. Particle will interpolate its size between snapshots, unlimited amount allowed.
     * @param sizes
     */
    public void setSizes(Vector3f[] sizes) {
        this.sizes = sizes;
        this.sizeX = sizes[0].x;
        this.sizeY = sizes[0].y;    //startsize
        lastSizeSnap = sizes[0];
        nextSizeSnap = sizes[0];
    }

    //first position in array is ALWAYS used as startsize.
    private Vector3f[] sizes = new Vector3f[]{new Vector3f(1,1,0)};

    /**
     * set color snapshots. first entry = startcolor.
     * Array of float[5] {r,g,b,a,% lifetime}.
     * MUST be in ascending life order. Default is bright blue, fading after 0.5 life
     * @param colors
     */
    public void setColors(float[][] colors) {
    //    for (float[] color: colors) {
    //        if (color.length != 5) {
    //            System.err.println("GodParticle received wrong format of colors: " + color);
    //            return;
    //        }
    //    }
        this.colors = colors;
        lastColorSnap = colors[0];
        nextColorSnap = colors[0];
    }

    //TODO allow color snapshots with time info (Vector5)
    private float[][] colors = new float[][]{
            new float[]{0, 1, 1, 0.5f, 0.5f},
            new float[]{0, 1, 1, 0, 1}
    };
    public GodParticle(int spriteID, Vector3f pos, int lifetime ) {
        super();
        this.particleSpriteId = spriteID;
        this.lifetimeMs = lifetime;
        this.startTime = System.currentTimeMillis();
        this.position.set(pos);
        this.updateCameraDistance();
        this.spawn();

    }
    @Override
    public void spawn() {
        lastSizeSnap = sizes[0];
        nextSizeSnap = sizes[0];

        lastColorSnap = colors[0];
        nextColorSnap = colors[0];

        //TODO rotate around camera direction.
        rotate(this,(float)Math.random() * 360);
    }

    int sizeIterator = 0;
    Vector3f lastSizeSnap;
    Vector3f nextSizeSnap;

    int colorIterator = 0;
    float[] lastColorSnap;
    float[] nextColorSnap;

    @Override
    public void update(long currentTime) {
        //interpolate between current size position and next based on lifetime
        float percentLife = getLifetimePercent(currentTime);
        if (nextSizeSnap.z < percentLife) {
            sizeIterator ++;
            lastSizeSnap = nextSizeSnap; //move one forward
            if (sizes.length > sizeIterator) { //only if a next snap exists.
                nextSizeSnap = sizes[sizeIterator];
            };
        };

        //get size at current lifetime percent
        if (sizeIterator >= sizes.length) {
            //last snapshot's deathtime reached.
            size = new Vector2f(lastSizeSnap.x,lastSizeSnap.y);
            if (size.x == 0 || size.y == 0) {
                markForDelete();    //last snapshot was reached, and particle is not visible.
            }
        } else {
            size = GetSizeAt(lastSizeSnap,nextSizeSnap,percentLife);
        }
        sizeX = size.x; sizeY = size.y;

        UpdateColor(percentLife);
    }

    /**
     * interpolates between last size snapshot and next sizesnapshot, based on lifetime percent
     * @param lastSizeSnap vector3f of last size snapshot: x,y, lifetimepercent
     * @param nextSizeSnap
     * @param percentLife percent of particle lifetime [0..1]
     * @return interpolated size between last and next snap, based on lifetimepercent
     */
    private Vector2f GetSizeAt(Vector3f lastSizeSnap, Vector3f nextSizeSnap, float percentLife) {
        if (nextSizeSnap == null || percentLife > 1) {
            return new Vector2f(lastSizeSnap.x,lastSizeSnap.y);
        }
        //percent between lastsnap and next snap (= lifetime of this snapshot)
        float percentSnap = ExtraEffects.extrapolate(lastSizeSnap.z,nextSizeSnap.z,percentLife); //50% on scale lastPoint .. nextPoint => perecent on that scale

        float x = ExtraEffects.interpolate(lastSizeSnap.x,nextSizeSnap.x,percentSnap);  //[10,20,0.5] => 15, 50% between 10 oldSize and 20 newSize
        float y= ExtraEffects.interpolate(lastSizeSnap.y,nextSizeSnap.y,percentSnap);  //[10,20,0.5] => 15, 50% between 10 oldSize and 20 newSize
        return new Vector2f(x,y);
    }

    private void SetColor(Vector4f color) {
        colorR = (byte) (color.x * 127);
        colorG = (byte) (color.y * 127);
        colorB = (byte) (color.z * 127);
        colorA = (byte) (color.w * 127);
    }

    private Vector4f GetColorAt(float[] colorA, float[] colorB,float percentLife) {
        //interpolate between colors
        float percent = ExtraEffects.extrapolate(colorA[4],colorB[4],percentLife);
        return new Vector4f(
                ExtraEffects.interpolate(colorA[0],colorB[0],percent),
                ExtraEffects.interpolate(colorA[1],colorB[1],percent),
                ExtraEffects.interpolate(colorA[2],colorB[2],percent),
                ExtraEffects.interpolate(colorA[3],colorB[3],percent)
        );
    }

    private void UpdateColor(float percentLife) {
        //decide what snapshot to use
        if (nextColorSnap[4] < percentLife) {
            colorIterator ++;
            lastColorSnap = nextColorSnap; //move one forward
            if (colors.length > colorIterator) { //only if a next snap exists.
                nextColorSnap = colors[colorIterator];
            };
        };

        //update color
        SetColor(GetColorAt(lastColorSnap,nextColorSnap,percentLife));
    }
}
