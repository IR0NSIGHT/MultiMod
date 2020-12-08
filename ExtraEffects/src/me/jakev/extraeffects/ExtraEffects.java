package me.jakev.extraeffects;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.weapon.CannonProjectileAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import com.bulletphysics.linearmath.Transform;
import me.jakev.extraeffects.particles.FireParticle;
import me.jakev.extraeffects.particles.FlashParticle;
import me.jakev.extraeffects.particles.SmokeParticle;
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

                ModParticleUtil.play(pos, SpriteList.SPARK.getSprite(), 20, 1000, new Vector3f(0, 0F, 0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new FireParticle(dir, 1);
                    }
                });
                dir.scale(0.5F);
                ModParticleUtil.play(pos, SpriteList.SPARK.getSprite(), 10, 500, new Vector3f(0, 0F, 0), new ModParticleFactory() {
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
                    vector3f.scale(50);
                    transform.origin.add(vector3f);
                    ModParticleUtil.play(transform.origin, SpriteList.FLASH.getSprite(), 1, 500, new Vector3f(0, 0F, 0), new ModParticleFactory() {
                        @Override
                        public ModParticle newParticle() {
                            return new FlashParticle();
                        }
                    });
                }
            }
        }, this);
    }
}