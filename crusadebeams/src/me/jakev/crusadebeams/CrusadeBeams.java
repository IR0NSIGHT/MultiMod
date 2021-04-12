package me.jakev.crusadebeams;

import api.listener.Listener;
import api.listener.events.register.MeshLoadEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.resource.MeshLoader;
import org.schema.schine.resource.ResourceLoader;

import java.io.IOException;

/**
 * Created by Jake on 4/12/2021.
 * <insert description here>
 */
public class CrusadeBeams extends StarMod {
    @Override
    public void onEnable() {
        System.err.println("CrusadeBeams Enabled");

        StarLoader.registerListener(MeshLoadEvent.class, this, new Listener<MeshLoadEvent>() {
            @Override
            public void onEvent(MeshLoadEvent event) {
                //Swap out the beam mesh on load
                if(event.getMesh().getName().equals("SimpleBeam")){
                    event.getMeshLoader().getMeshMap().put(event.getName(), beamMesh);
                }
            }
        });
        new StarRunnable(){

            @Override
            public void run() {
                MeshLoader loader = Controller.getResLoader().getMeshLoader();
            }
        }.runTimer(this, 10);
    }
    Mesh beamMesh = null;

    @Override
    public void onResourceLoad(ResourceLoader loader) {
        try {
            //Load our custom shader
            Shader beamShader = Shader.newModShader(getSkeleton(), "BeamShader",
                    getJarResource("me/jakev/crusadebeams/resource/simplebeam.vert.glsl"),
                    getJarResource("me/jakev/crusadebeams/resource/simplebeam.frag.glsl"));
            //Overwrite the vanilla shader
            ShaderLibrary.simpleBeamShader = beamShader;

            //Load our custom mesh
            loader.getMeshLoader().loadModMesh(this, "beam", getJarResource("me/jakev/crusadebeams/resource/simplebeam.zip"), null);
            beamMesh = loader.getMeshLoader().getModMesh(this, "beam");
            System.err.println("Done resource load");
        } catch (ResourceException | IOException e) {
            e.printStackTrace();
        }

    }
}
