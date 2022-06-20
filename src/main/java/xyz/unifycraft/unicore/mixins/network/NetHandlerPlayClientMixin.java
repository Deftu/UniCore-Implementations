package xyz.unifycraft.unicore.mixins.network;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.unifycraft.unicore.api.UniCore;
import xyz.unifycraft.unicore.api.events.chat.ChatActionReceivedEvent;
import xyz.unifycraft.unicore.api.events.chat.ChatMessageReceivedEvent;
import xyz.unifycraft.unicore.api.events.chat.ChatReceivedEvent;
import xyz.unifycraft.unicore.api.events.chat.ChatSystemReceivedEvent;

//#if MC<=11404
import net.minecraft.client.network.NetHandlerPlayClient;
//#endif

@Mixin({NetHandlerPlayClient.class})
public class NetHandlerPlayClientMixin {
    @Unique private IChatComponent modifiedReceivedMessage;

    @Inject(method = "handleChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;printChatMessage(Lnet/minecraft/util/IChatComponent;)V", shift = At.Shift.BEFORE))
    private void onChatReceived(S02PacketChat packet, CallbackInfo ci) {
        IChatComponent component = packet.getChatComponent();
        ChatReceivedEvent event = null;
        switch (packet.getType()) {
            case 0:
                event = new ChatMessageReceivedEvent(component, UniCore.getChatHelper().retrievePlayer(component));
                break;
            case 1:
                event = new ChatSystemReceivedEvent(component);
                break;
            case 2:
                event = new ChatActionReceivedEvent(component);
        }
        if (event != null) {
            UniCore.getEventBus().post(event);
            if (event.getCancelled())
                ci.cancel();
            modifiedReceivedMessage = event.getText();
        } else modifiedReceivedMessage = component;
    }

    @ModifyArg(method = "handleChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;printChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0))
    private IChatComponent modifyChatMessage(IChatComponent component) {
        return modifiedReceivedMessage;
    }
}
