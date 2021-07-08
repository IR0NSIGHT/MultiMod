package me.jakev.extraeffects.particles.advanced;

import api.ModPlayground;
import api.listener.Listener;
import api.listener.events.gui.PlayerGUIDrawEvent;
import api.mod.StarLoader;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.ExtraEffects;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.listeners.ExtraEffectsDrawUtil;
import me.jakev.extraeffects.particles.GodParticle;
import org.apache.commons.collections4.iterators.EntrySetMapIterator;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector;

import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 08.07.2021
 * TIME: 19:46
 */
public class PulsingExplosion extends BasicExplosion implements Runnable {


    /**
     * creates a particle explosion that
     *
     * @param pos        in sector position
     * @param duration   in millis on average
     * @param strength   strength in 1..1000
     * @param colorrange
     * @param color      color of explosion in V3 rgb
     * @param SectorID
     */
    public PulsingExplosion(Vector3f pos, int duration, int strength, float colorrange, Vector3f color, int SectorID) {
        super(pos, duration, strength, colorrange, color, SectorID);
    }

    int pulseTime;
    int pulseRadius;
    int amountParticles;

    public void setAttributes(int pulseRadius, int amountParticles) {
        pulseTime = duration;
        buildup = (long) (pulseTime*0.2f);
        remain = (long) (pulseTime*0.2f);
        collapse = (long) (pulseTime*0.6f);
        this.pulseRadius = pulseRadius;
        this.amountParticles = amountParticles;
    }

    @Override
    public void play() {

        this.stop = System.currentTimeMillis() + duration;
        this.start = System.currentTimeMillis();
        ExtraEffectsDrawUtil.subscribe(this);
        Vector3f[] arr = distributeOverSphere(amountParticles);
        //create a bunch of particles
        for (int i = 0; i < amountParticles; i++) {
            GodParticle p = new GodParticle(SpriteList.GLOWBALL.getSprite(), pos, duration, sectorID);
            p.setColors(new float[][]{
                    new float[]{1,1,0,0,0},
                    new float[]{0,1,0,1,0.2f},
                    new float[]{0,1,1,1,0.4f},
                    new float[]{0,1,0,0, (float) (1f-Math.random()*0.3)},
            });
            p.setSizes(new Vector3f[]{new Vector3f(5,5,0)});
            ModParticleUtil.playClientDirect(p);
            float offset = (float) (pulseRadius*5*(0.8*Math.random()+0.2));
            //arr[i].scale(offset);
            this.particles.put(p,arr[i]);
            this.offsets.put(p, offset);
        }
    }
    private HashMap<GodParticle,Vector3f> particles = new HashMap<>();
    private HashMap<GodParticle,Float> offsets = new HashMap<>();
    private long stop;
    private long start;
    private long buildup; //length
    private long remain; //length
    private long collapse; //length



    /**
     *
     * @param percent how far the process is done
     */
    private void buildup(float percent) {
        //build up the sparks around the ship
        for (Map.Entry<GodParticle,Float> e: offsets.entrySet()) {
            GodParticle particle = e.getKey();
            float offset = e.getValue();
            offset = ExtraEffects.interpolate(pulseRadius,offset,1-percent);
            offsetParticle(particle,pos,particles.get(particle),offset);
        }
    }

    private void remain(float percent) {
    //    for (Map.Entry<GodParticle,Float> e: offsets.entrySet()) {
    //        GodParticle particle = e.getKey();
    //    }
    }

    private void collapse(float percent) {
        for (Map.Entry<GodParticle,Float> e: offsets.entrySet()) {
             GodParticle particle = e.getKey();
             Vector3f vel = new Vector3f(-.5f +(float)  Math.random(),-.5f +(float)Math.random(),-.5f +(float)Math.random());
             vel.normalize(); vel.add(particles.get(particle));vel.normalize();
             vel.scale((float) Math.random()/4);
             particle.velocity = vel;

        }
        collapsed = true;
    }
    private boolean collapsed = false;
    @Override
    public void run() {
        //runs every draw update (probably frame)
        if (System.currentTimeMillis() > stop) {
            ExtraEffectsDrawUtil.unsubscribe(this);
            return;
        }
        float percent;
        if (System.currentTimeMillis() < start+buildup) {
            percent = (float)(System.currentTimeMillis() - start)/buildup;
            buildup(percent);
        } else if (System.currentTimeMillis() < start+buildup+remain) {
            percent = (float)(System.currentTimeMillis() - (start+buildup)/remain);
            remain(percent);
        } else if (!collapsed) {
            percent = (float)(System.currentTimeMillis() - (start+buildup+remain))/collapse;
            collapse(percent);
        }

    }
    private void offsetParticle(GodParticle p, Vector3f pos, Vector3f dir, float offset) {
        Vector3f particlePos = new Vector3f(pos);
        Vector3f direction = new Vector3f(dir);
        direction.scale(offset);
        particlePos.add(direction);
        p.position = particlePos;
    }
    //shamelessly stolen from stackoverflow https://stackoverflow.com/questions/9600801/evenly-distributing-n-points-on-a-sphere
    private static Vector3f[] distributeOverSphere(int n) {
        Vector3f[] arr = new Vector3f[n];

        double phi = Math.PI * (3 - Math.sqrt(5)); //golden angle in radians
        for (int i = 0; i < n; i++) {
            float y = 1 - (i/(float)(n-1)) * 2;
            float radius = (float) Math.sqrt(1-y * y);
            float theta = (float) (phi * i);
            float x = (float) (Math.cos(theta) * radius);
            float z = (float) (Math.sin(theta) * radius);
            arr[i] = new Vector3f(x,y,z);
            float length = arr[i].length();
            int uwu = 0;
        }
        return arr;
    }
}
