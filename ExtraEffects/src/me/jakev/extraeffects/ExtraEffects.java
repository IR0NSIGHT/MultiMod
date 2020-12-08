package me.jakev.extraeffects;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.weapon.CannonProjectileAddEvent;
import api.listener.events.weapon.ExplosionEvent;
import api.listener.events.weapon.MissilePostAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import com.bulletphysics.linearmath.Transform;
import me.jakev.extraeffects.particles.*;
import org.schema.game.common.data.element.ElementKeyMap;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 12/3/2020.
 * <insert description here>
 */
public class ExtraEffects extends StarMod {
    public static void main(String[] args) {

    }

    @Override
    public void onGameStart() {
        setModName("ExtraEffects");
        setModVersion("0.1");
        setModAuthor("JakeV");
        setModDescription("Various particles and sounds");
        inst = this;
        setSMDResourceId(44);
    }

    public static final int[] pullers = new int[]{
            211, //basic
            217, //standard
            259, //advanced
            677, //shipyard
            ElementKeyMap.STASH_ELEMENT
    };
    public static final int[] factories = new int[]{
            211, //basic
            217, //standard
            259, //advanced
            ElementKeyMap.STASH_ELEMENT
    };
    @Override
    public void onBlockConfigLoad(BlockConfig config) {
    }

    public static ExtraEffects inst;


    @Override
    public void onClientCreated(ClientInitializeEvent event) {
    }
    @Override
    public void onEnable() {
        SpriteList.init(this);
        StarLoader.registerListener(MissilePostAddEvent.class, new Listener<MissilePostAddEvent>() {
            @Override
            public void onEvent(MissilePostAddEvent event) {
                Vector3f dir = new Vector3f();
                event.getMissile().getDirection(dir);
                dir.normalize();
                dir.scale(0.3F);
                Vector3f origin = event.getMissile().getWorldTransform().origin;
                System.err.println("origin: " + origin);
                System.err.println("Direction: " + dir);
                ModParticleUtil.playClient(origin, SpriteList.BALL.getSprite(), 40, 1000, new Vector3f(0,0,0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new MissileShootParticle(dir, 0.15F, 1F);
                    }
                });
                new StarRunnable(true){
                    @Override
                    public void run() {
                        if(event.getMissile().isAlive()){
                            if(ticksRan > 2) {
                                ModParticleUtil.playClient(origin, SpriteList.FIRE.getSprite(), 1, 1200, new Vector3f(0, 0, 0), new ModParticleFactory() {
                                    @Override
                                    public ModParticle newParticle() {
                                        return new SimpleFireParticle(3F, 20);
                                    }
                                });
                            }
                        }else{
                            cancel();
                        }
                    }
                }.runTimer(inst, 1);
            }
        }, this);
        StarLoader.registerListener(ExplosionEvent.class, new Listener<ExplosionEvent>() {
            @Override
            public void onEvent(ExplosionEvent event) {
                Vector3f toPos = event.getExplosion().fromPos;
                System.err.println("ThePos: " + toPos.toString());
                ModParticleUtil.playClient(toPos, SpriteList.FLASH.getSprite(), 1, 5000, new Vector3f(0,0,0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new FlashParticle(10);
                    }
                });
                ModParticleUtil.playClient(toPos, SpriteList.SMOKE.getSprite(), 200, 500, 0.4F,0,0,0,new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new FadeParticle();
                    }
                });
            }
        }, this);

        StarLoader.registerListener(CannonProjectileAddEvent.class, new Listener<CannonProjectileAddEvent>() {
            @Override
            public void onEvent(CannonProjectileAddEvent event) {
                Vector3f dir = new Vector3f();
                Vector3f pos = new Vector3f();
                event.getContainer().getVelocity(event.getIndex(), dir);
                event.getContainer().getPos(event.getIndex(), pos);
                /* Transform
                Origin: [x, y, z]
                Basis:
                [rightX, upX, forwardX]
                [rightY, upY, forwardY]
                [rightZ, upZ, forwardZ]
                 */
                Transform world = new Transform();
                world.origin.set(pos);
                dir.normalize();
                dir.scale(10);

                ModParticleUtil.playClient(pos, SpriteList.SPARK.getSprite(), 20, 1000, new Vector3f(0, 0F, 0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new FireParticle(dir, 1);
                    }
                });
                dir.scale(0.5F);
                ModParticleUtil.playClient(pos, SpriteList.SPARK.getSprite(), 10, 500, new Vector3f(0, 0F, 0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new SmokeParticle(dir, 3);
                    }
                });
            }
        }, this);
        StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
            @Override
            public void onEvent(KeyPressEvent event) {
                if(event.getChar() == 'l') {
                    Transform transform = GameClient.getClientState().getCurrentPosition();
                    Vector3f vector3f = new Vector3f();
                    transform.basis.getColumn(2, vector3f);
                    vector3f.scale(5);
                    transform.origin.add(vector3f);
                    ModParticleUtil.playClient(transform.origin, SpriteList.CLOUD.getSprite(), 1000, 1000, 0F, 100,100,100, new ModParticleFactory() {
                        @Override
                        public ModParticle newParticle() {
                            return new FadeParticle();
                        }
                    });
//                    ModParticleUtil.playClient(transform.origin, SpriteList.ENERGY.getSprite(), 400, 400, 2F, 0,0,0, new ModParticleFactory() {
//                        @Override
//                        public ModParticle newParticle() {
//                            return new EnergyParticle();
//                        }
//                    });
//                    ModParticleUtil.play(transform.origin, SpriteList.FLASH.getSprite(), 1, 500, new Vector3f(0, 0F, 0), new ModParticleFactory() {
//                        @Override
//                        public ModParticle newParticle() {
//                            return new FlashParticle();
//                        }
//                    });
                }
            }
        }, this);
    }
}