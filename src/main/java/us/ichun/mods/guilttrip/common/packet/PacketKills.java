package us.ichun.mods.guilttrip.common.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import us.ichun.mods.guilttrip.common.GuiltTrip;
import us.ichun.mods.guilttrip.common.core.KillInfo;
import us.ichun.mods.ichunutil.common.core.network.AbstractPacket;

import java.util.ArrayList;

public class PacketKills extends AbstractPacket
{
    public String killer;
    public ArrayList<KillInfo> kills;

    public PacketKills(){}

    public PacketKills(String name, ArrayList<KillInfo> zKills)
    {
        killer = name;
        kills = zKills;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        ByteBufUtils.writeUTF8String(buffer, killer);
        buffer.writeInt(kills.size());
        for(int i = 0; i < kills.size(); i++)
        {
            ByteBufUtils.writeTag(buffer, kills.get(i).getTag());
        }
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        killer = ByteBufUtils.readUTF8String(buffer);
        kills = new ArrayList<KillInfo>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++)
        {
            kills.add(KillInfo.createKillInfoFromTag(ByteBufUtils.readTag(buffer)));
        }
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        if(GuiltTrip.proxy.tickHandlerClient.playerKills.containsKey(killer))
        {
            ArrayList<KillInfo> zKills = GuiltTrip.proxy.tickHandlerClient.playerKills.get(killer);
            for(KillInfo info : zKills)
            {
                for(int i = kills.size() - 1; i >= 0; i--)
                {
                    if(kills.get(i).identifier.equals(info.identifier))
                    {
                        kills.remove(i);
                    }
                }
            }
            zKills.addAll(kills);
        }
        else
        {
            GuiltTrip.proxy.tickHandlerClient.playerKills.put(killer, kills);
        }
    }
}
