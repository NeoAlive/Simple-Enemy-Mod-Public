package net.nekoyuni.SimpleEnemyMod;

import net.nekoyuni.SimpleEnemyMod.compat.cloth.ClothConfigCompat;
import net.nekoyuni.SimpleEnemyMod.compat.cloth.ClothConfigScreenHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = SimpleEnemyMod.MODID, dist = Dist.CLIENT)
public class SimpleEnemyModClient {

    public SimpleEnemyModClient(ModContainer modContainer) {
        if (ClothConfigCompat.LOADED) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                    (IConfigScreenFactory) (minecraft, parentScreen) -> ClothConfigScreenHelper.createConfigScreen(parentScreen));
        }
    }
}
