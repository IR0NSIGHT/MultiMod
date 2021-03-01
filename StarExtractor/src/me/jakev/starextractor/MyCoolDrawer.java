package me.jakev.starextractor;

import api.utils.draw.Updatable;
import org.lwjgl.opengl.GL11;
import org.schema.schine.graphicsengine.core.*;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;

/**
 * Created by Jake on 2/26/2021.
 * <insert description here>
 */
public class MyCoolDrawer implements Shaderable, Drawable, Updatable {
    float time = 0;

//    Dodecahedron h;
    Mesh mesh;

    @Override
    public void onExit() {
    }

    @Override
    public void updateShader(DrawableScene scene) {
        GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    @Override
    public void updateShaderParameters(Shader shader) {
        GlUtil.updateShaderFloat(shader, "time", time);
        GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, StarExtractorNew.tex.getMaterial().getTexture().getTextureId());
//        GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, GameResourceLoader.lavaTexture.getTextureId());
        GlUtil.updateShaderInt(shader, "lavaTex", 0);
    }


    @Override
    public void cleanUp() {
        if(mesh != null){
            mesh.cleanUp();
        }
    }
    int rad = 700;
    @Override
    public void draw() {
        if (mesh != null) {
            GlUtil.glDisable(GL11.GL_CULL_FACE);

            ShaderLibrary.lavaShader.setShaderInterface(this);
            ShaderLibrary.lavaShader.load();
                GlUtil.scaleModelview(rad, rad, rad);
                mesh.loadVBO(true);
                mesh.renderVBO();
                mesh.unloadVBO(true);
            ShaderLibrary.lavaShader.unload();

            GlUtil.glEnable(GL11.GL_CULL_FACE);
        }
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void onInit() {
        mesh = (Mesh) Controller.getResLoader().getMeshLoader().getModMesh(StarExtractorNew.inst, "Sphere").getChilds().iterator().next();
//        mesh = (Mesh) Controller.getResLoader().getMeshLoader().getModMesh(StarExtractorNew.inst, "planet_sphere").getChilds().iterator().next();
//        mesh = (Mesh) Controller.getResLoader().getMesh("GeoSphere").getChilds().iterator().next();
//        h = new Dodecahedron(500);
//        h.create();
    }

    @Override
    public void update(Timer timer) {
        time += timer.getDelta()*10;
    }
}
