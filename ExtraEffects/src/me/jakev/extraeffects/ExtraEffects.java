package me.jakev.extraeffects;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.weapon.CannonProjectileAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import api.utils.textures.StarLoaderTexture;
import com.bulletphysics.linearmath.Transform;
import me.jakev.extraeffects.particles.FireParticle;
import me.jakev.extraeffects.particles.SmokeParticle;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.forms.Sprite;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import java.io.IOException;

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
    ElementInformation imp;
    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        imp = config.newElement(this, "Factory assistant", new short[]{124});
        imp.setBuildIconNum(ElementKeyMap.WEAPON_CONTROLLER_ID);


        imp.setMaxHitPointsE(100);
        imp.setArmorValue(1);
        imp.setCanActivate(false);

        for (int id : pullers) BlockConfig.setBlocksConnectable(ElementKeyMap.getInfo(id), imp);
        for (int id : factories) BlockConfig.setBlocksConnectable(imp, ElementKeyMap.getInfo(id));
        config.add(imp);
        BlockConfig.addRecipe(imp, 5, 5, new FactoryResource(1, ElementKeyMap.AI_ELEMENT));
    }

    public static ExtraEffects inst;
    private Sprite spark;
    private Sprite smoke;
    @Override
    public void onEnable() {
        StarLoaderTexture.runOnGraphicsThread(() -> {
            synchronized (ExtraEffects.class) {
                try {
                    spark = StarLoaderTexture.newSprite(ImageIO.read(ExtraEffects.class.getResourceAsStream("res/spark.png")), ExtraEffects.this, "extraeffects_spark");
                    smoke = StarLoaderTexture.newSprite(ImageIO.read(ExtraEffects.class.getResourceAsStream("res/smoke.png")), ExtraEffects.this, "extraeffects_smoke");
                    StarLoaderTexture.newSprite(ImageIO.read(ExtraEffects.class.getResourceAsStream("res/m.png")), ExtraEffects.this, "extraeffects_monke");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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

                ModParticleUtil.play(pos, spark, 20, 1000, new Vector3f(0, 0F, 0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new FireParticle(dir, 1);
                    }
                });
                dir.scale(0.5F);
                ModParticleUtil.play(pos, smoke, 10, 500, new Vector3f(0, 0F, 0), new ModParticleFactory() {
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
                    Transform var1 = new Transform();
                    GameClient.getClientPlayerState().getWordTransform(var1);
                    Vector3f dir = GlUtil.getForwardVector(new Vector3f(), var1);
                    dir.scale(3);
                    ModParticleUtil.play(transform.origin, spark, 100, 5000, new Vector3f(0, 0F, 0), new ModParticleFactory() {
                        @Override
                        public ModParticle newParticle() {
                            return new FireParticle(dir, 1);
                        }
                    });
                }
            }
        }, this);
    }
}