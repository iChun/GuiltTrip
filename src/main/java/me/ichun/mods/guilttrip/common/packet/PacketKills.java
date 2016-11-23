package me.ichun.mods.guilttrip.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.guilttrip.common.GuiltTrip;
import me.ichun.mods.guilttrip.common.core.KillInfo;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

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
    public void writeTo(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, killer);
        buffer.writeInt(kills.size());
        for(int i = 0; i < kills.size(); i++)
        {
            ByteBufUtils.writeTag(buffer, kills.get(i).getTag());
        }
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        killer = ByteBufUtils.readUTF8String(buffer);
        kills = new ArrayList<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++)
        {
            kills.add(KillInfo.createKillInfoFromTag(ByteBufUtils.readTag(buffer)));
        }
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
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
        return null;
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }
}
