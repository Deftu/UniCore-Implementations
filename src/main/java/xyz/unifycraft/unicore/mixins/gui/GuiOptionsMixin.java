package xyz.unifycraft.unicore.mixins.gui;

//#if MC<=10809
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.unifycraft.unicore.api.UniCore;

@Mixin({GuiOptions.class})
public class GuiOptionsMixin extends GuiScreen {
    private static int BROADCAST_BUTTON_ID = 107;

    @Inject(method = "initGui", at = @At("RETURN"))
    public void initGui(CallbackInfo callbackInfo) {
        GuiButton button = buttonList.stream().filter(btn -> btn.id == BROADCAST_BUTTON_ID).findFirst().orElse(null);
        if (button == null) {
            UniCore.getLogger().warn("Could not find the broadcast button in the options menu! The UniCore menu will not be accessible through here.");
            return;
        }
        button.displayString = UniCore.getName() + "...";
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id != BROADCAST_BUTTON_ID) return; // The "Broadcast Settings" button.
        ci.cancel();
        // UniCore.getGuiHelper().showScreen(new UniCoreMenu());
    }

}
//#endif
