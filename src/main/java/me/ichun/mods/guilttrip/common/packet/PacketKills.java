package me.ichun.mods.guilttrip.common.packet;

import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.guilttrip.common.core.KillInfo;
import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;

public class PacketKills extends AbstractPacket
{
    public String killer;
    public ArrayList<KillInfo> kills;

    public PacketKills() {}

    public PacketKills(String name, ArrayList<KillInfo> zKills)
    {
        killer = name;
        kills = zKills;
    }

    @Override
    public void writeTo(PacketBuffer buffer)
    {
        buffer.writeString(killer);
        buffer.writeInt(kills.size());
        for(int i = 0; i < kills.size(); i++)
        {
            buffer.writeCompoundTag(kills.get(i).getTag());
        }
    }

    @Override
    public void readFrom(PacketBuffer buffer)
    {
        killer = buffer.readString(32767);
        kills = new ArrayList<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++)
        {
            kills.add(KillInfo.createKillInfoFromTag(buffer.readCompoundTag()));
        }
    }

    @Override
    public void process(NetworkEvent.Context context) //receiving side, client
    {
        context.enqueueWork(() -> {
            if(GuiltTrip.eventHandlerClient.playerKills.containsKey(killer))
            {
                ArrayList<KillInfo> ori = new ArrayList<>();
                ArrayList<KillInfo> zKills = GuiltTrip.eventHandlerClient.playerKills.get(killer);
                for(int j = 0; j < zKills.size(); j++)
                {
                    KillInfo info = zKills.get(j);
                    for(int i = kills.size() - 1; i >= 0; i--)
                    {
                        if(kills.get(i).identifier.equals(info.identifier))
                        {
                            ori.add(info);
                            kills.remove(i);
                        }
                    }
                }
                ori.addAll(kills);
                GuiltTrip.eventHandlerClient.playerKills.put(killer, ori);
            }
            else
            {
                GuiltTrip.eventHandlerClient.playerKills.put(killer, kills);
            }

        });
    }
}
