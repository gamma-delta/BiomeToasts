package at.petrak.biometoasts.server;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.UUID;

public class ServerMovementTracker {
    private static Multimap<UUID, ResourceLocation> LAST_STRUCTURES_IN = MultimapBuilder.hashKeys()
        .hashSetValues()
        .build();

    @SubscribeEvent
    public static void onServerStop(ServerStoppingEvent evt) {
        LAST_STRUCTURES_IN.clear();
    }

    @SubscribeEvent
    public static void onTick(TickEvent.WorldTickEvent evt) {
        if (!(evt.world instanceof ServerLevel world)) {
            return;
        }

        var gen = world.structureFeatureManager();
        for (var player : world.players()) {
            var structsIn = LAST_STRUCTURES_IN.get(player.getUUID());
            var structsHere = gen.getAllStructuresAt(player.blockPosition()).keySet();
            var structIDsHere = new HashSet<ResourceLocation>();

            var anyChange = false;
            for (var struct : structsHere) {
                var id = struct.feature.getRegistryName();
                if (!structsIn.contains(id)) {
                    anyChange = true;
                }
                structIDsHere.add(id);
            }
            if (!anyChange) {
                for (var struct : structsIn) {
                    if (!structIDsHere.contains(struct)) {
                        anyChange = true;
                        break;
                    }
                }
            }

            if (anyChange) {
                structsIn.clear();
                structsIn.addAll(structIDsHere);

                // todo: send to client
                player.sendMessage(new TextComponent(structsIn.toString()), Util.NIL_UUID);
            }
        }
    }
}
