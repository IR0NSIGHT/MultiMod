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
    private Vector2f size;
    //first position in array is ALWAYS used as startsize.
    private Vector3f[] sizes = new Vector3f[]{new Vector3f(1,1,0)};
    private Vector4f[] colors = new Vector4f[]{new Vector4f(0,1,1,0.5f), new Vector4f(0,1,1,0f)};
    public GodParticle(Vector3f velocity) {
        this.velocity = velocity;
    }

    @Override
    public void spawn() {
        float baseSize = 7.5f;
        sizes = new Vector3f[]{
                new Vector3f(baseSize,baseSize,0),
                new Vector3f((float) (baseSize + Math.random() *5),(float) (baseSize + Math.random() *5),0.75f),
        //       new Vector3f(20,20,1),
        };
        this.sizeX = sizes[0].x;
        this.sizeY = sizes[0].y;    //startsize
        lastSizeSnap = sizes[0];
        nextSizeSnap = sizes[0];
        rotate(this,(float) Math.random()*360f);
    }

    int sizeIterator = 0;
    Vector3f lastSizeSnap;
    Vector3f nextSizeSnap;
    @Override
    public void update(long currentTime) {
        //interpolate between current size position and next based on lifetime
        float lifePassed = getLifetimePercent(currentTime);
        if (nextSizeSnap.z < lifePassed) {
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
            size = GetSizeAt(lastSizeSnap,nextSizeSnap,lifePassed);
        }
        sizeX = size.x; sizeY = size.y;

        SetColor(GetColorAt(colors[0],colors[1],lifePassed));
       // fadeOverTime(this,currentTime);

    //    rotate(this,(float)Math.random());
    }

    /**
     * interpolates between last size snapshot and next sizesnapshot, based on lifetime percent
     * @param lastSizeSnap vector3f of last size snapshot: x,y, lifetimepercent
     * @param nextSizeSnap
     * @param lifepercent percent of particle lifetime [0..1]
     * @return interpolated size between last and next snap, based on lifetimepercent
     */
    private Vector2f GetSizeAt(Vector3f lastSizeSnap, Vector3f nextSizeSnap, float lifepercent) {
        if (nextSizeSnap == null || lifepercent > 1) {
            return new Vector2f(lastSizeSnap.x,lastSizeSnap.y);
        }
        //percent between lastsnap and next snap (= lifetime of this snapshot)
        float percentSnap = ExtraEffects.extrapolate(lastSizeSnap.z,nextSizeSnap.z,lifepercent); //50% on scale lastPoint .. nextPoint => perecent on that scale

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

    private Vector4f GetColorAt(Vector4f colorA, Vector4f colorB,float percent) {
        //interpolate between colors
        return new Vector4f(
                ExtraEffects.interpolate(colorA.x,colorB.x,percent),
                ExtraEffects.interpolate(colorA.y,colorB.y,percent),
                ExtraEffects.interpolate(colorA.z,colorB.z,percent),
                ExtraEffects.interpolate(colorA.w,colorB.w,percent)
        );
    }
}
