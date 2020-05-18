package me.ichun.mods.guilttrip.client.core;

import me.ichun.mods.guilttrip.client.layer.LayerGuiltTrip;
import me.ichun.mods.guilttrip.common.core.KillInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

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
            if(Minecraft.getInstance().world == null)
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
                    else if(!Minecraft.getInstance().isGamePaused())
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
    public void addGuiltTripLayer(FMLLoadCompleteEvent event)
    {
        for(Map.Entry<String, PlayerRenderer> e : Minecraft.getInstance().getRenderManager().skinMap.entrySet())
        {
            e.getValue().addLayer(new LayerGuiltTrip(e.getValue()));
        }
    }
}
