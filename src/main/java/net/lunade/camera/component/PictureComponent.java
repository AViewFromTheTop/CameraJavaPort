package net.lunade.camera.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record PictureComponent(ResourceLocation location) {
// public record PictureComponent(ResourceLocation location, int day, int month, int year, int hour, int minute) {
    public static final Codec<PictureComponent> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    ResourceLocation.CODEC.fieldOf("location").forGetter(component -> component.location)
                    /*
                    Codec.INT.fieldOf("day").forGetter(component -> component.day),
                    Codec.INT.fieldOf("month").forGetter(component -> component.month),
                    Codec.INT.fieldOf("year").forGetter(component -> component.year),
                    Codec.INT.fieldOf("hour").forGetter(component -> component.hour),
                    Codec.INT.fieldOf("minute").forGetter(component -> component.minute)
                     */
            )
            .apply(instance, PictureComponent::new));

    public static final StreamCodec<ByteBuf, PictureComponent> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, PictureComponent::location,
            /*
            ByteBufCodecs.VAR_INT, PictureComponent::day,
            ByteBufCodecs.VAR_INT, PictureComponent::month,
            ByteBufCodecs.VAR_INT, PictureComponent::year,
            ByteBufCodecs.VAR_INT, PictureComponent::hour,
            ByteBufCodecs.VAR_INT, PictureComponent::minute,
             */
            PictureComponent::new
    );
}
