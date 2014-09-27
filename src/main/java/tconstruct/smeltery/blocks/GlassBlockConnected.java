package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import mantle.blocks.MantleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.*;
import net.minecraft.world.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.smeltery.model.GlassConnectedRenderer;
import tconstruct.util.config.PHConstruct;

/**
 * @author fuj1n
 * 
 */

public class GlassBlockConnected extends MantleBlock
{
    protected IIcon[] icons = new IIcon[8];
    private boolean shouldRenderSelectionBox = true;
    protected String folder;
    private int renderPass;

    public GlassBlockConnected(String location, boolean hasAlpha)
    {
        super(Material.glass);
        this.stepSound = soundTypeGlass;
        folder = location;
        renderPass = hasAlpha ? 1 : 0;
        setHardness(0.3F);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    // For FMP support
    public IIcon[] getIcons ()
    {
        return icons;
    }

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    @Override
    public int getRenderBlockPass ()
    {
        return renderPass;
    }

    /**
     * This is checked to see if the texture should connect to this block
     * @param par2 x
     * @param par3 y
     * @param par4 z
     * @param par5 ID this block is asking to connect to (may be 0 if there is no block)
     * @param par6 Metadata of the block this block is trying to connect to
     * @return true if should connect
     */
    public boolean shouldConnectToBlock (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, Block par5, int par6)
    {
        return par5 == (Block) this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return icons[0];
    }
    
    public IIcon[] getTextures(int meta)
    {
        return icons;
    }
    
    public IIcon getConnectedBlockTexture (IBlockAccess world, int x, int y, int z, int side, boolean leftSide, int meta)
    {
        IIcon[] icons = getTextures(meta);
        
        if (PHConstruct.connectedTexturesMode == 0)
        {
            return icons[0];
        }

        boolean hasDown, hasUp, hasNorth, hasSouth, hasWest, hasEast;
        boolean hasSide;
        
        hasDown = shouldConnectToBlock(world, x, y, z, world.getBlock(x, y - 1, z), world.getBlockMetadata(x, y - 1, z));
        hasUp = shouldConnectToBlock(world, x, y, z, world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z));
        hasNorth = shouldConnectToBlock(world, x, y, z, world.getBlock(x, y, z - 1), world.getBlockMetadata(x, y, z - 1));
        hasSouth = shouldConnectToBlock(world, x, y, z, world.getBlock(x, y, z + 1), world.getBlockMetadata(x, y, z + 1));
        hasWest = shouldConnectToBlock(world, x, y, z, world.getBlock(x - 1, y, z), world.getBlockMetadata(x - 1, y, z));
        hasEast = shouldConnectToBlock(world, x, y, z, world.getBlock(x + 1, y, z), world.getBlockMetadata(x + 1, y, z));

        switch (side)
        {
        case 0: // Down
            hasSide = leftSide ? hasWest : hasEast;
            hasUp = hasSouth;
            hasDown = hasNorth;
            
            break;
        case 1: // Up
            hasSide = leftSide ? hasEast : hasWest;
            hasUp = hasSouth;
            hasDown = hasNorth;
            
            break;
        case 2: // North
            hasSide = leftSide ? hasEast : hasWest;

            break;
        case 3: // South
            hasSide = leftSide ? hasWest : hasEast;

            break;
        case 4: // West
            hasSide = leftSide ? hasNorth : hasSouth;

            break;
        case 5: // East
            hasSide = leftSide ? hasSouth : hasNorth;

            break;
        default:
            return icons[0];
        }
        
        if (hasUp && hasSide && hasDown)
        {
            return icons[5];
        }
        
        if (hasUp && hasSide)
        {
            return icons[3];
        }
        
        if (hasSide && hasDown)
        {
            return icons[1];
        }
        
        if (hasUp && hasDown)
        {
            return icons[2];
        }
        
        if (hasSide)
        {
            return icons[4];
        }
        
        if (hasDown)
        {
            return icons[6];
        }
        
        if (hasUp)
        {
            return icons[7];
        }

        return icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int par1, int par2)
    {
        return icons[0];
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool (World par1World, int par2, int par3, int par4)
    {
        if (shouldRenderSelectionBox)
        {
            return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
        }
        else
        {
            return AxisAlignedBB.getBoundingBox(0D, 0D, 0D, 0D, 0D, 0D);
        }
    }

    @Override
    public void registerBlockIcons (IIconRegister par1IconRegister)
    {
        icons[0] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_uds");
        icons[1] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_u");
        icons[2] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_s");
        icons[3] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_d");
        icons[4] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_ud");
        icons[5] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_n");
        icons[6] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_us");
        icons[7] = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_ds");
    }

    @Override
    public boolean canPlaceTorchOnTop (World world, int x, int y, int z)
    {
        return true;
    }
    
    @Override
    public int getRenderType ()
    {
        return GlassConnectedRenderer.model;
    }
}
