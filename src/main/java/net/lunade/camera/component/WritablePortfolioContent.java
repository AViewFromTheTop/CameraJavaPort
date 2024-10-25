package net.lunade.camera.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lunade.camera.component.impl.PortfolioContent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WritablePortfolioContent implements PortfolioContent<ItemStack, WritablePortfolioContent> {
    public static final WritablePortfolioContent EMPTY = new WritablePortfolioContent(List.of());
    public static final int MAX_PAGES = 64;
    private static final Codec<ItemStack> PAGE_CODEC = ItemStack.CODEC;
    public static final Codec<List<ItemStack>> PAGES_CODEC = PAGE_CODEC.sizeLimitedListOf(MAX_PAGES);
    public static final Codec<WritablePortfolioContent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WritablePortfolioContent::pages))
                    .apply(instance, WritablePortfolioContent::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, WritablePortfolioContent> STREAM_CODEC = ItemStack.STREAM_CODEC
            .apply(ByteBufCodecs.list(MAX_PAGES))
            .map(WritablePortfolioContent::new, WritablePortfolioContent::pages);

    private final List<ItemStack> pages;

    public WritablePortfolioContent(List<ItemStack> pages) {
        this.pages = pages;
    }

    @Override
    public List<ItemStack> pages() {
        return this.pages;
    }

    @Override
    public WritablePortfolioContent withReplacedPages(List<ItemStack> pages) {
        return new WritablePortfolioContent(pages);
    }
}
