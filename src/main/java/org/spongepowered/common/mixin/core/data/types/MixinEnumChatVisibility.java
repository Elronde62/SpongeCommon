/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.data.types;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.text.translation.SpongeTranslation;

import java.util.Set;

@Mixin(EntityPlayer.EnumChatVisibility.class)
public abstract class MixinEnumChatVisibility implements ChatVisibility {

    @Shadow private String resourceKey;
    private final String id = ((Enum) (Object) this).name().toLowerCase();
    private final Translation translation = new SpongeTranslation(this.resourceKey);
    private Set<ChatType> visibleChatTypes = Sets.newHashSet();

    @Inject(method = "<init>(ILjava/lang/String;)V", at = @At("RETURN"))
    public void construct(int id, String resourceKey, CallbackInfo ci) {
        switch ((EntityPlayer.EnumChatVisibility) (Object) this) {
            case FULL:
                this.visibleChatTypes.addAll(Sponge.getRegistry().getAllOf(ChatType.class));
                break;
            case SYSTEM:
                this.visibleChatTypes.add(ChatTypes.SYSTEM);
                this.visibleChatTypes.add(ChatTypes.ACTION_BAR);
            break;
            case HIDDEN:
                // You can't see me.
                break;
        }
    }

    @Override
    public boolean isVisible(ChatType type) {
        checkNotNull(type, "type");
        return this.visibleChatTypes.contains(type);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.translation.get();
    }

    @Override
    public Translation getTranslation() {
        return this.translation;
    }

}
