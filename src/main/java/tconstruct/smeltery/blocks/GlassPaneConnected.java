package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.smeltery.model.PaneConnectedRender;
import tconstruct.util.config.PHConstruct;

public class GlassPaneConnected extends GlassBlockConnected
{

    private IIcon theIcon;

    public GlassPaneConnected(String location, boolean hasAlpha)
    {
        super(location, hasAlpha);
    }

    @Override
    public int getRenderType ()
    {
        return PaneConnectedRender.model;
        // return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (IBlockAccess world, int x, int y, int z, int side)
    {
        if (side == 0 || side == 1)
        {
            return world.getBlock(x, y - 1, z) == this && side == 0 ? icons[15] : world.getBlock(x, y + 1, z) == this && side == 1 ? icons[15] : getSideTextureIndex();
        }
        else
        {
            return super.getIcon(world, x, y, z, side);
        }
    }

    public IIcon getConnectedPaneTexture (IBlockAccess world, int x, int y, int z, int side, boolean leftSide)
    {
        if (side == 0 || side == 1)
        {
            return getSideTextureIndex();
        }

        if (PHConstruct.connectedTexturesMode == 0)
        {
            return icons[0];
        }

        boolean hasDown, hasUp, hasNorth, hasSouth, hasWest, hasEast;
        boolean hasSide, hasOtherSide, hasFront, hasBack;
        ForgeDirection checkDirection;
        
        hasDown = shouldConnectToBlock(world, x, y, z, world.getBlock(x, y - 1, z), world.getBlockMetadata(x, y - 1, z));
        hasUp = shouldConnectToBlock(world, x, y, z, world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z));
        hasNorth = shouldConnectToBlock(world, x, y, z, world.getBlock(x, y, z - 1), world.getBlockMetadata(x, y, z - 1));
        hasSouth = shouldConnectToBlock(world, x, y, z, world.getBlock(x, y, z + 1), world.getBlockMetadata(x, y, z + 1));
        hasWest = shouldConnectToBlock(world, x, y, z, world.getBlock(x - 1, y, z), world.getBlockMetadata(x - 1, y, z));
        hasEast = shouldConnectToBlock(world, x, y, z, world.getBlock(x + 1, y, z), world.getBlockMetadata(x + 1, y, z));

        switch (side)
        {
        case 2: // North
            hasFront = hasNorth;
            hasBack = hasSouth;

            if (leftSide)
            {
                hasSide = hasEast;
                hasOtherSide = hasWest;
                checkDirection = ForgeDirection.EAST;
            }
            else
            {
                hasSide = hasWest;
                hasOtherSide = hasEast;
                checkDirection = ForgeDirection.WEST;
            }

            break;
        case 3: // South
            hasFront = hasSouth;
            hasBack = hasNorth;

            if (leftSide)
            {
                hasSide = hasWest;
                hasOtherSide = hasEast;
                checkDirection = ForgeDirection.WEST;
            }
            else
            {
                hasSide = hasEast;
                hasOtherSide = hasWest;
                checkDirection = ForgeDirection.EAST;
            }

            break;
        case 4: // West
            hasFront = hasWest;
            hasBack = hasEast;

            if (leftSide)
            {
                hasSide = hasNorth;
                hasOtherSide = hasSouth;
                checkDirection = ForgeDirection.NORTH;
            }
            else
            {
                hasSide = hasSouth;
                hasOtherSide = hasNorth;
                checkDirection = ForgeDirection.SOUTH;
            }

            break;
        case 5: // East
            hasFront = hasEast;
            hasBack = hasWest;

            if (leftSide)
            {
                hasSide = hasSouth;
                hasOtherSide = hasNorth;
                checkDirection = ForgeDirection.SOUTH;
            }
            else
            {
                hasSide = hasNorth;
                hasOtherSide = hasSouth;
                checkDirection = ForgeDirection.NORTH;
            }

            break;
        default:
            return icons[0];
        }

        if (hasOtherSide && !hasFront && !hasSide && !canPaneConnectTo(world, x, y, z, checkDirection))
        {
            return icons[4];
        }
        
        if (hasDown)
        {
            hasDown = canPaneConnectTo(world, x, y - 1, z, checkDirection);
        }
        if (hasUp)
        {
            hasUp = canPaneConnectTo(world, x, y + 1, z, checkDirection);
        }
        
        if (hasUp && hasSide && hasDown)
        {
            return icons[6];
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
            return icons[5];
        }
        
        if (hasDown)
        {
            return icons[7];
        }
        
        if (hasUp)
        {
            return icons[8];
        }

        return icons[0];
    }

    @Override
    public void addCollisionBoxesToList (World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
        boolean flag = this.canPaneConnectTo(par1World, par2, par3, par4, ForgeDirection.NORTH);
        boolean flag1 = this.canPaneConnectTo(par1World, par2, par3, par4, ForgeDirection.SOUTH);
        boolean flag2 = this.canPaneConnectTo(par1World, par2, par3, par4, ForgeDirection.WEST);
        boolean flag3 = this.canPaneConnectTo(par1World, par2, par3, par4, ForgeDirection.EAST);

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1))
        {
            if (flag2 && !flag3)
            {
                this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
            }
            else if (!flag2 && flag3)
            {
                this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
            }
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1))
        {
            if (flag && !flag1)
            {
                this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
            }
            else if (!flag && flag1)
            {
                this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
            }
        }
        else
        {
            this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        }
    }

    @Override
    public void setBlockBoundsForItemRender ()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        float f = 0.4375F;
        float f1 = 0.5625F;
        float f2 = 0.4375F;
        float f3 = 0.5625F;
        boolean flag = this.canPaneConnectTo(par1IBlockAccess, par2, par3, par4, ForgeDirection.NORTH);
        boolean flag1 = this.canPaneConnectTo(par1IBlockAccess, par2, par3, par4, ForgeDirection.SOUTH);
        boolean flag2 = this.canPaneConnectTo(par1IBlockAccess, par2, par3, par4, ForgeDirection.WEST);
        boolean flag3 = this.canPaneConnectTo(par1IBlockAccess, par2, par3, par4, ForgeDirection.EAST);

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1))
        {
            if (flag2 && !flag3)
            {
                f = 0.0F;
            }
            else if (!flag2 && flag3)
            {
                f1 = 1.0F;
            }
        }
        else
        {
            f = 0.0F;
            f1 = 1.0F;
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1))
        {
            if (flag && !flag1)
            {
                f2 = 0.0F;
            }
            else if (!flag && flag1)
            {
                f3 = 1.0F;
            }
        }
        else
        {
            f2 = 0.0F;
            f3 = 1.0F;
        }

        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
    }

    public IIcon getSideTextureIndex ()
    {
        return this.theIcon;
    }

    public final boolean canThisPaneConnectToThisBlock (Block b)
    {
        return b.isOpaqueCube() || b == (Block) this || b == Blocks.glass;
    }

    @Override
    public void registerBlockIcons (IIconRegister par1IconRegister)
    {
        icons[0] = par1IconRegister.registerIcon("tinker:glass/" + folder + "test/glass_udl");
        icons[1] = par1IconRegister.registerIcon("tinker:glass/" + folder + "test/glass_u");
        icons[2] = par1IconRegister.registerIcon("tinker:glass/" + folder + "test/glass_l");
        icons[3] = par1IconRegister.registerIcon("tinker:glass/" + folder + "test/glass_d");
        icons[4] = par1IconRegister.registerIcon("tinker:glass/" + folder + "test/glass_r");
        icons[5] = par1IconRegister.registerIcon("tinker:glass/" + folder + "test/glass_ud");
        icons[6] = par1IconRegister.registerIcon("tinker:glass/" + folder + "test/glass_n");
        icons[7] = par1IconRegister.registerIcon("tinker:glass/" + folder + "test/glass_ul");
        icons[8] = par1IconRegister.registerIcon("tinker:glass/" + folder + "test/glass_dl");
        
        theIcon = par1IconRegister.registerIcon("tinker:glass/" + folder + "test/glass_side");
    }

    public boolean canPaneConnectTo (IBlockAccess access, int x, int y, int z, ForgeDirection dir)
    {
        return canThisPaneConnectToThisBlock(access.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) || access.isSideSolid(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite(), false);
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }
}
