package com.gregorune.minecartannoucer.RoadSigns;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class SkullCreator {
    static boolean lookupInitalized = false;
    private static final HashMap<String, String> Signs = new HashMap<>();
    public static HashMap<String, String> getSigns()
    {
        if(!lookupInitalized)
        {
            InitalizeLookup();
            lookupInitalized = true;
        }
        return Signs;
    }

    private static void InitalizeLookup()
    {
        Signs.put("no_entry",             "69c632601dd050bb2ff3b7bb741f6e45571a798732132721257f4bd755307db0");
        Signs.put("detour_right",         "f3f337d697f9d91aa812eb2cca62e37c6f6b4a38b1031c1e62d35c683f59d192");
        Signs.put("detour_left",          "c978f35c63860d694fee4ab3c3694fde4046c966e4c1bf0665f88748f9f8025");
        Signs.put("detour_both",          "165b9aabf3db2551f7c6bc97de1acd6eaeafe56e94f3602d7629cc60570bd326");
        Signs.put("light_green",          "ff404c1b73a7cbfc860578ce044e271d13b79166ead80b364518cff234d61212");
        Signs.put("light_yellow",         "d9c394fd624e447d125ef3fb54d82a6fa4b0c6188e304d33352d13fbdb0c751b");
        Signs.put("light_red",            "e6015b480ac2ce4834c9d8f2ca5d15c6cac38a52147040a1c4c095a2319816f5");
        Signs.put("light_right_signal",   "291ac432aa40d7e7a687aa85041de636712d4f022632dd5356c880521af2723a");
        Signs.put("light_straight_signal","77334cddfab45d75ad28e1a47bf8cf5017d2f0982f6737da22d4972952510661");
        Signs.put("light_left_signal",    "7a2c12cb22918384e0a81c82a1ed99aebdce94b2ec2754800972319b57900afb");
        Signs.put("light_stop_signal",    "bb72ad8369eb6cd8990cec1f54d1778442a108b0186622c5918eb85159e2fb9e");
        Signs.put("light_back_signal",    "e7742034f59db890c8004156b727c77ca695c4399d8e0da5ce9227cf836bb8e2");
        Signs.put("traffic_cone",         "8dafe5f6afa634f0632ed0f4b161b3c733592e20bff54eaa6576a794c0261af2");
        Signs.put("no_vehicles",          "5ca21b5704d23726e0382119e25863b9c5394aca4ab93b5c0a371ad4f3d42d11");
        Signs.put("one_way",              "c2abe43288a6c8cd76d0228f39112d2520c289d7c15c6aafe0c532ad9f5db9ad");
        Signs.put("curve_right",          "87e21db6e428935c5b022904f3e443fa7591e1f068ecb38722e09701c5449ab2");
        Signs.put("curve_left",           "b42836e40f9c91f240902982c52067c7b17422364d791e6b6cf79c4ba44f64e3");
    }

    public static ItemStack getSign(String name) {
        if(!lookupInitalized)
        {
            InitalizeLookup();
            lookupInitalized = true;
        }

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(URI.create("http://textures.minecraft.net/texture/" + Signs.get(name)).toURL());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid texture URL", e);
        }

        profile.setTextures(textures);
        assert meta != null;
        meta.setOwnerProfile(profile);
        skull.setItemMeta(meta);
        return skull;
    }
}