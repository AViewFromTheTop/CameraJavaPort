package net.lunade.camera.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class PictureComponent {
    public static final Codec<String> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.STRING.fieldOf("picture").forGetter(s -> s))
            .apply(instance, String::new));

    public static final StreamCodec<ByteBuf, String> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, s -> s,
            String::new
    );
}
