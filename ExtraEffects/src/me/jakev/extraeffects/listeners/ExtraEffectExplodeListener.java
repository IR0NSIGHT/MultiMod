package me.jakev.extraeffects.listeners;

import api.listener.Listener;
import api.listener.events.entity.SegmentControllerOverheatEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import com.bulletphysics.linearmath.Transform;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.FadeParticle;
import me.jakev.extraeffects.particles.shipexplode.ColorFlashParticle;
import me.jakev.extraeffects.particles.shipexplode.DebrisFlairParticle;
import me.jakev.extraeffects.particles.shipexplode.InvisibleEmitterParticle;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/9/2020.
 * <insert description here>
 */
public class ExtraEffectExplodeListener {
    public static void init(StarMod inst){
        StarLoader.registerListener(SegmentControllerOverheatEvent.class, new Listener<SegmentControllerOverheatEvent>() {
            @Override
            public void onEvent(SegmentControllerOverheatEvent event) {
                Vector3f pos = event.getEntity().getWorldTransformCenterOfMass(new Transform()).origin;
                new StarRunnable(){

                    @Override
                    public void run() {
                        ModParticleUtil.playClient(pos, SpriteList.BIGSMOKE.getSprite(), 70, 3000, 0.3F,false, new ModParticleFactory() {
                            @Override
                            public ModParticle newParticle() {
                                return new FadeParticle(10, 500);
                            }
                        });
                    }
                }.runLater(inst, 2);
                //130
                ModParticleUtil.playClient(pos, SpriteList.FLASH.getSprite(), 1, 180, new Vector3f(0, 0, 0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {//15
                        return new ColorFlashParticle(25, new Vector4f(1,1,0,1), new Vector4f(1F,0,0,1F));
                    }
                });

                //30, 1.6
                ModParticleUtil.playClient(pos, SpriteList.NOTHING.getSprite(), 40, 5000, 1.8F,0,0,0, new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new InvisibleEmitterParticle(SpriteList.FIREFLASH.getSprite(), 1, 5000, new Vector3f(0, 0, 0), 1000, new ModParticleFactory() {
                            @Override
                            public ModParticle newParticle() {
                                return new DebrisFlairParticle();
                            }
                        });
                    }
                });
            }
        }, inst);
    }
}
