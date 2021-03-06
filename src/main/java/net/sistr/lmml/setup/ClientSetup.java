package net.sistr.lmml.setup;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.sistr.lmml.LittleMaidModelLoader;
import net.sistr.lmml.client.renderer.MultiModelRenderer;

@Mod.EventBusSubscriber(modid = LittleMaidModelLoader.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(Registration.MULTI_MODEL_ENTITY.get(), MultiModelRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.DUMMY_MODEL_ENTITY.get(), MultiModelRenderer::new);
    }

}
