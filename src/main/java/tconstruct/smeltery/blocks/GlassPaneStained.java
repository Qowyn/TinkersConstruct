package tconstruct.smeltery.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import tconstruct.library.TConstructRegistry;
import tconstruct.util.config.PHConstruct;

public class GlassPaneStained extends GlassPaneConnected
{
    public String[] textures;
    public IIcon[][] icons;
    boolean ignoreMetaForConnectedGlass = PHConstruct.connectedTexturesMode == 2;

    public GlassPaneStained(String location, boolean hasAlpha, String... textures)
    {
        super(location, hasAlpha);
        this.textures = textures;
        this.icons = new IIcon[textures.length][10];
        this.setHardness(0.3F);
        this.stepSound = soundTypeGlass;
        this.setBlockName("tconstruct.glasspanestained");
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    public boolean shouldConnectToBlock (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, Block par5, int par6)
    {
        return par5 == this && (ignoreMetaForConnectedGlass || par6 == par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    @Override
    public int getRenderBlockPass ()
    {
        return 1;
    }

    @Override
    public int damageDropped (int par1)
    {
        return par1;
    }
    
    @Override
    public IIcon[] getTextures (int meta)
    {
        return icons[meta];
    }
    
    @Override
    public void getSubBlocks (Item b, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < textures.length; i++)
        {
            par3List.add(new ItemStack(b, 1, i));
        }
    }
    
    public IIcon getSideTextureIndex (int meta)
    {
        return this.icons[meta][9];
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int par1, int par2)
    {
        return par1 > 3 ? icons[par2][0] : icons[par2][9];
    }
    
    @Override
    public void registerBlockIcons (IIconRegister par1IconRegister)
    {
        for (int i = 0; i < textures.length; i++)
        {
            icons[i][0] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_uds");
            icons[i][1] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_u");
            icons[i][2] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_s");
            icons[i][3] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_d");
            icons[i][4] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_os");
            icons[i][5] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_ud");
            icons[i][6] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_n");
            icons[i][7] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_us");
            icons[i][8] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_ds");
            icons[i][9] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/" + textures[i] + "/glass_side");
        }
    }
    
    
}
