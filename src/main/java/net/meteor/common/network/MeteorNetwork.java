package net.meteor.common.network;

import net.meteor.MeteorsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/** The "METEORS" SimpleNetworkWrapper of 1.7.10. */
public class MeteorNetwork {

	private static final String PROTOCOL = "1";

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MeteorsMod.MOD_ID, "main"),
			() -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

	public static void register() {
		int id = 0;
		CHANNEL.messageBuilder(PacketBlockedMeteor.class, id++, NetworkDirection.PLAY_TO_CLIENT)
			.encoder(PacketBlockedMeteor::toBytes).decoder(PacketBlockedMeteor::new).consumerMainThread(PacketBlockedMeteor::handle).add();
		CHANNEL.messageBuilder(PacketButtonPress.class, id++, NetworkDirection.PLAY_TO_SERVER)
			.encoder(PacketButtonPress::toBytes).decoder(PacketButtonPress::new).consumerMainThread(PacketButtonPress::handle).add();
		CHANNEL.messageBuilder(PacketGhostMeteor.class, id++, NetworkDirection.PLAY_TO_CLIENT)
			.encoder(PacketGhostMeteor::toBytes).decoder(PacketGhostMeteor::new).consumerMainThread(PacketGhostMeteor::handle).add();
		CHANNEL.messageBuilder(PacketLastCrash.class, id++, NetworkDirection.PLAY_TO_CLIENT)
			.encoder(PacketLastCrash::toBytes).decoder(PacketLastCrash::new).consumerMainThread(PacketLastCrash::handle).add();
		CHANNEL.messageBuilder(PacketSoonestMeteor.class, id++, NetworkDirection.PLAY_TO_CLIENT)
			.encoder(PacketSoonestMeteor::toBytes).decoder(PacketSoonestMeteor::new).consumerMainThread(PacketSoonestMeteor::handle).add();
	}

	public static void sendTo(Object message, ServerPlayer player) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public static void sendToDimension(Object message, ServerLevel world) {
		CHANNEL.send(PacketDistributor.DIMENSION.with(world::dimension), message);
	}

	public static void sendToAllAround(Object message, ServerLevel world, double x, double y, double z, double range) {
		CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, range, world.dimension())), message);
	}

	public static void sendToServer(Object message) {
		CHANNEL.sendToServer(message);
	}

}
