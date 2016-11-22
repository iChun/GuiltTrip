package me.ichun.mods.guilttrip.client.core;

import me.ichun.mods.guilttrip.client.layer.LayerGuiltTrip;
import me.ichun.mods.guilttrip.common.core.KillInfo;
import me.ichun.mods.ichunutil.client.core.event.RendererSafeCompatibilityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventHandlerClient
{
    public HashMap<String, ArrayList<KillInfo>> playerKills = new HashMap<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            if(Minecraft.getMinecraft().theWorld == null)
            {
                playerKills.clear();
            }

            for(ArrayList<KillInfo> kills : playerKills.values())
            {
                for(int i = kills.size() - 1; i >= 0; i--)
                {
                    KillInfo info = kills.get(i);
                    if(!info.validateInstance(false))
                    {
                        info.entInstance = null; // set to null so GC can collect it.
                    }
                    else if(!Minecraft.getMinecraft().isGamePaused())
                    {
                        info.update();
                        if(info.maxAge != 0 && info.age > info.maxAge + 100)
                        {
                            kills.remove(i);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRendererSafeCompatibility(RendererSafeCompatibilityEvent event)
    {
        LayerGuiltTrip layer = new LayerGuiltTrip();
        for(Map.Entry<String, RenderPlayer> e : Minecraft.getMinecraft().getRenderManager().skinMap.entrySet())
        {
            e.getValue().addLayer(layer);
        }
    }
}
