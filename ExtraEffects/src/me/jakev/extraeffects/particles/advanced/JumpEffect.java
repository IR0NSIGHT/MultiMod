package me.jakev.extraeffects.particles.advanced;

import api.DebugFile;
import api.common.GameClient;
import api.common.GameServer;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.ExtraEffects;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.listeners.ExtraEffectsDrawUtil;
import me.jakev.extraeffects.listeners.Playable;
import me.jakev.extraeffects.particles.GodParticle;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.SegmentController;


import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.Map;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 08.07.2021
 * TIME: 19:46
 */
public class JumpEffect implements Runnable, Playable {
     /**
     * creates a particle effect that collects particles around a ship, forms them into a glowing ball,
     * and after some time pops like a bubble
     * @param pos        in sector position
     * @param duration   in millis on average
     */
    public JumpEffect(Vector3f pos, int duration, int sectorID) {
       this.pos = pos; this.duration = duration; this.sectorID= sectorID;
    }

    private transient HashMap<GodParticle,Vector3f> particles;
    private transient HashMap<GodParticle,Float> offsets;
    private transient SegmentController ship;

    private int pulseRadius;
    private int amountParticles;
    private long stop; //systemtime
    private long start; //systemtime
    private long buildup; //length
    private long remain; //length
    private long collapse; //length
    private int duration;
    private int sectorID;
    private Vector3f pos;
    private boolean collapsed = false;
    private float pSize; //particle size, depends on radius
    private String shipUID;

    public void setAttributes(int pulseRadius, int amountParticles, String shipUID) {
        buildup  = (long) (duration * 0.05f);
        remain   = (long) (duration * 0.2f);
        collapse = (long) (duration * 0.75f);
        this.pulseRadius = pulseRadius;
        this.amountParticles = amountParticles;
        this.shipUID = shipUID;
        pSize = (float) (2*pulseRadius/Math.sqrt(amountParticles))*6;
    }

    /**
     * plays explosion on local machine (needs to be run on a client. use RemotePlay.class to play from server)
     */
    @Override
    public void play() {
        try {
            ship = (SegmentController) GameClientState.instance.getLocalAndRemoteObjectContainer().getUidObjectMap().get(shipUID);
        } catch (Exception e) {
            e.printStackTrace();
            DebugFile.log("FAILED TO GET SC ON CLIENT");
            return;
        }
        offsets  = new HashMap<>();
        particles  = new HashMap<>();
        this.stop = System.currentTimeMillis() + duration;
        this.start = System.currentTimeMillis();
        ExtraEffectsDrawUtil.subscribe(this);
        Vector3f[] arr = distributeOverSphere(amountParticles);
        //create a bunch of particles
        for (int i = 0; i < amountParticles; i++) {
            GodParticle p = new GodParticle(SpriteList.GLOWBALL.getSprite(), pos, duration, sectorID);
            float vanish = (float) (((buildup+remain+0.5*collapse)+Math.random()*0.5*collapse)/duration);
            p.setColors(new float[][]{
                    new float[]{1,1,0,0,0},
                    new float[]{0,1,0,1,((float)buildup/duration)}, //end of buildup
                    new float[]{0,1,1,1,(((float)remain+(float)buildup)/duration)}, //end of remain
                    new float[]{0,1,0,0,vanish} //collapse
            });
            p.setSizes(new Vector3f[]{
                    new Vector3f(0,0,0),
                    new Vector3f(0.1f*pSize,0.1f*pSize,  (0.5f*buildup)/duration),
                    new Vector3f(pSize,pSize,  ((float)buildup/duration)), //jump grpahic effect starts
                    new Vector3f(pSize,pSize,(((float)remain+(float)buildup)/duration)), //end of remain
                    new Vector3f(0,0, vanish)//collapse
            });
            ModParticleUtil.playClientDirect(p);
            float offset = (float) (pulseRadius*5*(0.8*Math.random()+0.2));
            //arr[i].scale(offset);
            this.particles.put(p,arr[i]);
            this.offsets.put(p, offset);
        }
    }

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
       for (Map.Entry<GodParticle,Float> e: offsets.entrySet()) {
            GodParticle particle = e.getKey();
            if (ship != null) this.pos = ship.getWorldTransform().origin;
            offsetParticle(particle,pos,particles.get(particle),pulseRadius);
       }
    }

    private void collapse(float percent) {
    //    remain(percent);
       for (Map.Entry<GodParticle,Float> e: offsets.entrySet()) {
            GodParticle particle = e.getKey();
            Vector3f vel = new Vector3f(-.5f +(float)  Math.random(),-.5f +(float)Math.random(),-.5f +(float)Math.random());
            vel.normalize(); vel.add(particles.get(particle));vel.normalize();
            vel.scale((float) Math.random()/4);
       //     if (ship != null) vel.add(ship.getLinearVelocity(new Vector3f()));
            particle.velocity = vel;

       }
       collapsed = true;
    }

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

    @Override
    public String toString() {
        return "JumpEffect{" +
                "particles=" + particles +
                ", offsets=" + offsets +
                ", pulseRadius=" + pulseRadius +
                ", amountParticles=" + amountParticles +
                ", stop=" + stop +
                ", start=" + start +
                ", buildup=" + buildup +
                ", remain=" + remain +
                ", collapse=" + collapse +
                ", duration=" + duration +
                ", sectorID=" + sectorID +
                ", pos=" + pos +
                ", collapsed=" + collapsed +
                ", pSize=" + pSize +
                ", shipUID='" + shipUID + '\'' +
                ", ship=" + ship +
                '}';
    }
}