package tconstruct.library.tools;

import net.minecraft.util.StatCollector;

/*
 * Dynamic substitute for an enum. It carries a lot of information
 */
public class ToolMaterial
{
    public final String materialName;
    public final int harvestLevel;
    public final int durability;
    public final int miningspeed; // <-- divided by 100
    public final int attack;
    public final float handleModifier;
    public final int reinforced;
    public final float stonebound;
    public final String tipStyle;


    @Deprecated
    public String displayName;
    @Deprecated
    public String ability;
    @Deprecated
    public ToolMaterial(String name, String displayName, int level, int durability, int speed, int damage, float handle, int reinforced, float stonebound, String style, String ability)
    {
        this(name, level, durability, speed, damage, handle, reinforced, stonebound, style);
        this.displayName = prefixName();
        this.ability = ability();
    }

    @Deprecated
    public ToolMaterial(String name, int level, int durability, int speed, int damage, float handle, int reinforced, float stonebound, String style, String ability)
    {
        this(name, level, durability, speed, damage, handle, reinforced, stonebound, style);
        this.displayName = prefixName();
        this.ability = ability();
    }

    public ToolMaterial(String name, int level, int durability, int speed, int damage, float handle, int reinforced, float stonebound, String style)
    {
        this.materialName = name;
        this.harvestLevel = level;
        this.durability = durability;
        this.miningspeed = speed;
        this.attack = damage;
        this.handleModifier = handle;
        this.reinforced = reinforced;
        this.stonebound = stonebound;
        this.tipStyle = style;
    }

    public String name ()
    {
        return materialName;
    }

    public String localizedName() { return StatCollector.translateToLocal("material." + materialName.toLowerCase()); }

    public String prefixName()
    {
        // check if there's a special name, otherwise use the regular one
        if(StatCollector.canTranslate(String.format("material.%s.display", materialName.toLowerCase())))
            return StatCollector.translateToLocal(String.format("material.%s.display", materialName.toLowerCase()));
        return localizedName();
    }

    public int durability ()
    {
        return this.durability;
    }

    public int toolSpeed ()
    {
        return this.miningspeed;
    }

    public int attack ()
    {
        return this.attack;
    }

    public int harvestLevel ()
    {
        return this.harvestLevel;
    }

    public float handleDurability ()
    {
        return this.handleModifier;
    }

    public int reinforced ()
    {
        return this.reinforced;
    }

    public float shoddy ()
    {
        return this.stonebound;
    }

    public String style ()
    {
        return this.tipStyle;
    }

    /**
     * Returns the ability of the tool to display.
     * ONLY USE THIS FOR DISPLAY PURPOSES. It is not data you can rely on. Use the material-ids for that.
     */
    public String ability ()
    {
        if(StatCollector.canTranslate(String.format("material.%s.ability", materialName.toLowerCase())))
            return StatCollector.translateToLocal(String.format("material.%s.ability", materialName.toLowerCase()));
        return "";
    }
}
