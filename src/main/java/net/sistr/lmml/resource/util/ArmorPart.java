package net.sistr.lmml.resource.util;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.sistr.lmml.entity.compound.IHasMultiModel;
import net.sistr.lmml.maidmodel.ModelMultiBase;
import net.sistr.lmml.resource.holder.TextureHolder;

import java.lang.ref.WeakReference;
import java.util.*;

//防具の1部位のデータ保持用クラス
public class ArmorPart {
    private final TexturePair innerTex;
    private final TexturePair outerTex;
    private final ModelMultiBase innerModel;
    private final ModelMultiBase outerModel;

    public ArmorPart(ResourceLocation innerTex, ResourceLocation innerTexLight,
                     ResourceLocation outerTex, ResourceLocation outerTexLight,
                     ModelMultiBase innerModel, ModelMultiBase outerModel) {
        this.innerTex = new TexturePair(innerTex, innerTexLight);
        this.outerTex = new TexturePair(outerTex, outerTexLight);
        this.innerModel = innerModel;
        this.outerModel = outerModel;
    }

    public ResourceLocation getTexture(IHasMultiModel.Layer layer, boolean isLight) {
        if (!layer.isArmor()) {
            throw new IllegalArgumentException("取得できません。");
        }
        if (layer == IHasMultiModel.Layer.INNER) {
            return innerTex.getTexture(isLight);
        } else {
            return outerTex.getTexture(isLight);
        }
    }

    public ModelMultiBase getModel(IHasMultiModel.Layer layer) {
        if (!layer.isArmor()) {
            throw new IllegalArgumentException("取得できません。");
        }
        if (layer == IHasMultiModel.Layer.INNER) {
            return innerModel;
        } else {
            return outerModel;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmorPart that = (ArmorPart) o;
        return Objects.equals(innerTex, that.innerTex) &&
                Objects.equals(outerTex, that.outerTex) &&
                Objects.equals(innerModel, that.innerModel) &&
                Objects.equals(outerModel, that.outerModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerTex, outerTex, innerModel, outerModel);
    }

    public static final class Builder {
        private static final Map<TextureHolder, List<WeakReference<ArmorPart>>> REFERENCES = new HashMap<>();
        private ResourceLocation innerTex;
        private ResourceLocation innerTexLight;
        private ResourceLocation outerTex;
        private ResourceLocation outerTexLight;
        private ModelMultiBase innerModel;
        private ModelMultiBase outerModel;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder innerTex(ResourceLocation innerTex) {
            this.innerTex = innerTex;
            return this;
        }

        public Builder innerTexLight(ResourceLocation innerTexLight) {
            this.innerTexLight = innerTexLight;
            return this;
        }

        public Builder outerTex(ResourceLocation outerTex) {
            this.outerTex = outerTex;
            return this;
        }

        public Builder outerTexLight(ResourceLocation outerTexLight) {
            this.outerTexLight = outerTexLight;
            return this;
        }

        public Builder innerModel(ModelMultiBase innerModel) {
            this.innerModel = innerModel;
            return this;
        }

        public Builder outerModel(ModelMultiBase outerModel) {
            this.outerModel = outerModel;
            return this;
        }

        public ArmorPart build() {
            return new ArmorPart(innerTex, innerTexLight, outerTex, outerTexLight, innerModel, outerModel);
            //getNewDataAndCache(textureHolder, innerTex, innerTexLight, outerTex, outerTexLight, innerModel, outerModel);
        }

        //参照されてる=他のメイドが使用してる防具パーツモデルは使いまわす
        //メモリを削減できたとしても負荷的にはどうなのか…早すぎる最適化な気もする
        private static ArmorPart getNewDataAndCache(TextureHolder textureHolder,
                                                    ResourceLocation innerTex, ResourceLocation innerTexLight,
                                                    ResourceLocation outerTex, ResourceLocation outerTexLight,
                                                    ModelMultiBase innerModel, ModelMultiBase outerModel) {
            refreshReferences();
            List<WeakReference<ArmorPart>> references = REFERENCES.get(textureHolder);
            if (references != null) {
                Iterator<WeakReference<ArmorPart>> iterator = references.iterator();
                while (iterator.hasNext()) {
                    WeakReference<ArmorPart> reference = iterator.next();
                    ArmorPart weakData = reference.get();
                    if (weakData == null) {
                        iterator.remove();
                    } else if (weakData.getTexture(IHasMultiModel.Layer.INNER, false) == innerTex) {
                        return weakData;
                    }
                }
            }
            ArmorPart data = new ArmorPart(
                    innerTex, innerTexLight, outerTex, outerTexLight, innerModel, outerModel);
            REFERENCES.put(textureHolder, Lists.newArrayList(new WeakReference<>(data)));
            return data;
        }

        private static void refreshReferences() {
            REFERENCES.forEach((texturePackage, references) -> {
                references.removeIf(reference -> reference.get() == null);
                if (references.size() <= 0) {
                    REFERENCES.remove(texturePackage);
                }
            });

        }

    }
}
