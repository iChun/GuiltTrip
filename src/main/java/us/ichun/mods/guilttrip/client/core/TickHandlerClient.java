package us.ichun.mods.guilttrip.client.core;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import us.ichun.mods.guilttrip.common.core.KillInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class TickHandlerClient
{
    public HashMap<String, ArrayList<KillInfo>> playerKills = new HashMap<String, ArrayList<KillInfo>>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
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
}
