package dev.royalcore.api.consumer;

import dev.royalcore.api.errors.Result;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ResourcepackConsumer {

    List<ResourcePackInfo> resourcePacks = new ArrayList<>();

    public Result add(ResourcePackInfo resourcePackInfo) {
        if (resourcePackInfo == null)
            return Result.Err(Component.text("A resource pack returned null! It has been excluded."), false);
        if (resourcePacks.contains(resourcePackInfo))
            return Result.Err(Component.text("Duplicate resource pack detected."), false);

        resourcePacks.add(resourcePackInfo);
        return Result.Ok();

    }

    public void sendPack(final @NotNull Audience target) {
        for (ResourcePackInfo resourcePack : resourcePacks) {
            final ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                    .packs(resourcePack)
                    .prompt(Component.text("You are required to download resource packs!").color(NamedTextColor.BLUE))
                    .required(true)
                    .build();

            target.sendResourcePacks(request);
        }
    }

    public void sendAsOptional(final @NotNull Audience target) {
        for (ResourcePackInfo resourcePack : resourcePacks) {
            final ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                    .packs(resourcePack)
                    .prompt(Component.text("Please download these optional packs!").color(NamedTextColor.BLUE))
                    .required(false)
                    .build();

            target.sendResourcePacks(request);
        }
    }

}
