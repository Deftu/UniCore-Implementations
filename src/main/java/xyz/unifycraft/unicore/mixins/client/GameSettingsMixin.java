package xyz.unifycraft.unicore.mixins.client;

//#if MC<=10809
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin({GameSettings.class})
public class GameSettingsMixin {
    @Shadow public KeyBinding[] keyBindings;

    @Inject(method = { "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V", "<init>()V" }, at = @At("RETURN"))
    private void modifyKeyBindings(CallbackInfo ci) {
        System.out.println("Keybindings: " + ArrayUtils.toString(keyBindings));
        List<KeyBinding> keyBindingsList = new ArrayList<>(Arrays.asList(keyBindings));
        System.out.println("Keybindings List 1: " + keyBindingsList);
        keyBindingsList.removeIf(keyBinding -> keyBinding.getKeyCategory().equalsIgnoreCase("key.categories.stream"));
        System.out.println("Keybindings List 2: " + keyBindingsList);
        keyBindings = keyBindingsList.toArray(new KeyBinding[0]);
    }

}
//#endif
