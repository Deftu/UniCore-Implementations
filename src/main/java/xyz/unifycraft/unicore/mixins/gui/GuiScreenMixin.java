package xyz.unifycraft.unicore.mixins.gui;

import xyz.unifycraft.unicore.api.UniCore;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.unifycraft.unicore.api.events.chat.ChatSendEvent;

//#if MC<=11202
@Mixin({GuiScreen.class})
public class GuiScreenMixin {
    @Unique private String modifiedSentMessage;

    @Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;sendChatMessage(Ljava/lang/String;)V"), cancellable = true)
    private void onChatMessageSent(String message, boolean addToChat, CallbackInfo ci) {
        ChatSendEvent event = new ChatSendEvent(message);
        UniCore.getEventBus().post(event);
        if (event.getCancelled())
            ci.cancel();
        modifiedSentMessage = event.getMessage();
    }

    @ModifyArg(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;sendChatMessage(Ljava/lang/String;)V", ordinal = 0))
    private String modifySentMessage(String original) {
        return modifiedSentMessage;
    }
}
//#endif
