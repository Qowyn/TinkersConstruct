package tconstruct.smeltery.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import tconstruct.smeltery.blocks.GlassPaneConnected;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class PaneConnectedRender implements ISimpleBlockRenderingHandler
{

    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {

    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        GlassPaneConnected pane = (GlassPaneConnected) block;

        // Connection state
        boolean connectionDown = pane.canPaneConnectTo(world, x, y, z, DOWN);
        boolean connectionUp = pane.canPaneConnectTo(world, x, y, z, UP);
        boolean connectionNorth = pane.canPaneConnectTo(world, x, y, z, NORTH);
        boolean connectionSouth = pane.canPaneConnectTo(world, x, y, z, SOUTH);
        boolean connectionWest = pane.canPaneConnectTo(world, x, y, z, WEST);
        boolean connectionEast = pane.canPaneConnectTo(world, x, y, z, EAST);
        // Render all connections
        boolean noXZConnection = !connectionEast && !connectionWest && !connectionSouth && !connectionNorth;

        // Needed to dertmine if vertices with side texture should be rendered
        boolean paneDown = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y - 1, z), world.getBlockMetadata(x, y - 1, z));
        boolean paneUp = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z));
        boolean paneNorth = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y, z - 1), world.getBlockMetadata(x, y, z - 1));
        boolean paneSouth = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y, z + 1), world.getBlockMetadata(x, y, z + 1));
        boolean paneWest = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x - 1, y, z), world.getBlockMetadata(x - 1, y, z));
        boolean paneEast = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x + 1, y, z), world.getBlockMetadata(x + 1, y, z));

        // first nearly half
        final double FNH = 7.0D / 16.0D;
        // second nearly half
        final double SNH = 9.0D / 16.0D;
        // stop the Z-Fighting!
        final double AZF = 0.005D;

        IIcon iconDown = pane.getIcon(world, x, y, z, 0);
        IIcon iconUp = pane.getIcon(world, x, y, z, 1);
        IIcon iconNorth = pane.getIcon(world, x, y, z, 2);
        IIcon iconSouth = pane.getIcon(world, x, y, z, 3);
        IIcon iconWest = pane.getIcon(world, x, y, z, 4);
        IIcon iconEast = pane.getIcon(world, x, y, z, 5);
        IIcon iconSide = pane.getSideTextureIndex();

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));

        int color = block.colorMultiplier(world, x, y, z);
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float aR = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
            float aG = (r * 30.0F + g * 70.0F) / 100.0F;
            float aB = (r * 30.0F + b * 70.0F) / 100.0F;
            r = aR;
            g = aG;
            b = aB;
        }

        tessellator.setColorOpaque_F(r, g, b);

        double minU, half7U, half9U, maxU, minV, half7V, half9V, maxV;

        if (!paneDown)
        {
            // Down
            minU = iconDown.getMinU();
            half7U = iconDown.getInterpolatedU(7);
            half9U = iconDown.getInterpolatedU(9);
            maxU = iconDown.getMaxU();
            minV = iconDown.getMinV();
            half7V = iconDown.getInterpolatedV(7);
            half9V = iconDown.getInterpolatedV(9);
            maxV = iconDown.getMaxV();

            if (noXZConnection || connectionWest)
            {
                if (!connectionDown)
                {
                    tessellator.addVertexWithUV(x, y, z + SNH, minU, half9V);
                    tessellator.addVertexWithUV(x, y, z + FNH, minU, half7V);
                    tessellator.addVertexWithUV(x + FNH, y, z + FNH, half7U, half7V);
                    tessellator.addVertexWithUV(x + FNH, y, z + SNH, half7U, half9V);
                }

                tessellator.addVertexWithUV(x + FNH, y + AZF, z + SNH, half7U, half9V);
                tessellator.addVertexWithUV(x + FNH, y + AZF, z + FNH, half7U, half7V);
                tessellator.addVertexWithUV(x, y + AZF, z + FNH, minU, half7V);
                tessellator.addVertexWithUV(x, y + AZF, z + SNH, minU, half9V);
            }

            if (noXZConnection || connectionEast)
            {
                if (!connectionDown)
                {
                    tessellator.addVertexWithUV(x + SNH, y, z + SNH, half9U, half9V);
                    tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, half7V);
                    tessellator.addVertexWithUV(x + 1, y, z + FNH, maxU, half7V);
                    tessellator.addVertexWithUV(x + 1, y, z + SNH, maxU, half9V);
                }

                tessellator.addVertexWithUV(x + 1, y + AZF, z + SNH, maxU, half9V);
                tessellator.addVertexWithUV(x + 1, y + AZF, z + FNH, maxU, half7V);
                tessellator.addVertexWithUV(x + SNH, y + AZF, z + FNH, half9U, half7V);
                tessellator.addVertexWithUV(x + SNH, y + AZF, z + SNH, half9U, half9V);
            }

            if (noXZConnection || connectionNorth)
            {
                if (!connectionDown)
                {
                    tessellator.addVertexWithUV(x + FNH, y, z + FNH, half7U, half7V);
                    tessellator.addVertexWithUV(x + FNH, y, z, half7U, minV);
                    tessellator.addVertexWithUV(x + SNH, y, z, half9U, minV);
                    tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, half7V);
                }

                tessellator.addVertexWithUV(x + SNH, y + AZF, z + FNH, half9U, half7V);
                tessellator.addVertexWithUV(x + SNH, y + AZF, z, half9U, minV);
                tessellator.addVertexWithUV(x + FNH, y + AZF, z, half7U, minV);
                tessellator.addVertexWithUV(x + FNH, y + AZF, z + FNH, half7U, half7V);
            }

            if (noXZConnection || connectionSouth)
            {
                if (!connectionDown)
                {
                    tessellator.addVertexWithUV(x + FNH, y, z + 1, half7U, maxV);
                    tessellator.addVertexWithUV(x + FNH, y, z + SNH, half7U, half9V);
                    tessellator.addVertexWithUV(x + SNH, y, z + SNH, half9U, half9V);
                    tessellator.addVertexWithUV(x + SNH, y, z + 1, half9U, maxV);
                }

                tessellator.addVertexWithUV(x + SNH, y + AZF, z + 1, half9U, maxV);
                tessellator.addVertexWithUV(x + SNH, y + AZF, z + SNH, half9U, half9V);
                tessellator.addVertexWithUV(x + FNH, y + AZF, z + SNH, half7U, half9V);
                tessellator.addVertexWithUV(x + FNH, y + AZF, z + 1, half7U, maxV);
            }

            if (!connectionDown)
            {
                tessellator.addVertexWithUV(x + FNH, y, z + SNH, half7U, half9V);
                tessellator.addVertexWithUV(x + FNH, y, z + FNH, half7U, half7V);
                tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, half7V);
                tessellator.addVertexWithUV(x + SNH, y, z + SNH, half9U, half9V);
            }

            tessellator.addVertexWithUV(x + SNH, y + AZF, z + SNH, half9U, half9V);
            tessellator.addVertexWithUV(x + SNH, y + AZF, z + FNH, half9U, half7V);
            tessellator.addVertexWithUV(x + FNH, y + AZF, z + FNH, half7U, half7V);
            tessellator.addVertexWithUV(x + FNH, y + AZF, z + SNH, half7U, half9V);
        }

        if (!paneUp)
        {
            // Up
            minU = iconUp.getMinU();
            half7U = iconUp.getInterpolatedU(7);
            half9U = iconUp.getInterpolatedU(9);
            maxU = iconUp.getMaxU();
            minV = iconUp.getMinV();
            half7V = iconUp.getInterpolatedV(7);
            half9V = iconUp.getInterpolatedV(9);
            maxV = iconUp.getMaxV();

            if (noXZConnection || connectionWest)
            {
                if (!connectionUp)
                {
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half7U, half9V);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half7U, half7V);
                    tessellator.addVertexWithUV(x, y + 1, z + FNH, minU, half7V);
                    tessellator.addVertexWithUV(x, y + 1, z + SNH, minU, half9V);
                }

                tessellator.addVertexWithUV(x, y + 1 - AZF, z + SNH, minU, half9V);
                tessellator.addVertexWithUV(x, y + 1 - AZF, z + FNH, minU, half7V);
                tessellator.addVertexWithUV(x + FNH, y + 1 - AZF, z + FNH, half7U, half7V);
                tessellator.addVertexWithUV(x + FNH, y + 1 - AZF, z + SNH, half7U, half9V);
            }

            if (noXZConnection || connectionEast)
            {
                if (!connectionUp)
                {
                    tessellator.addVertexWithUV(x + 1, y + 1, z + SNH, maxU, half9V);
                    tessellator.addVertexWithUV(x + 1, y + 1, z + FNH, maxU, half7V);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, half7V);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half9U, half9V);
                }

                tessellator.addVertexWithUV(x + SNH, y + 1 - AZF, z + SNH, half9U, half9V);
                tessellator.addVertexWithUV(x + SNH, y + 1 - AZF, z + FNH, half9U, half7V);
                tessellator.addVertexWithUV(x + 1, y + 1 - AZF, z + FNH, maxU, half7V);
                tessellator.addVertexWithUV(x + 1, y + 1 - AZF, z + SNH, maxU, half9V);
            }

            if (noXZConnection || connectionNorth)
            {
                if (!connectionUp)
                {
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, half7V);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z, half9U, minV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z, half7U, minV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half7U, half7V);
                }

                tessellator.addVertexWithUV(x + FNH, y + 1 - AZF, z + FNH, half7U, half7V);
                tessellator.addVertexWithUV(x + FNH, y + 1 - AZF, z, half7U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1 - AZF, z, half9U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1 - AZF, z + FNH, half9U, half7V);
            }

            if (noXZConnection || connectionSouth)
            {
                if (!connectionUp)
                {
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + 1, half9U, maxV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half9U, half9V);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half7U, half9V);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + 1, half7U, maxV);
                }

                tessellator.addVertexWithUV(x + FNH, y + 1 - AZF, z + 1, half7U, maxV);
                tessellator.addVertexWithUV(x + FNH, y + 1 - AZF, z + SNH, half7U, half9V);
                tessellator.addVertexWithUV(x + SNH, y + 1 - AZF, z + SNH, half9U, half9V);
                tessellator.addVertexWithUV(x + SNH, y + 1 - AZF, z + 1, half9U, maxV);
            }

            if (!connectionUp)
            {
                tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half9U, half9V);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, half7V);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half7U, half7V);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half7U, half9V);
            }

            tessellator.addVertexWithUV(x + FNH, y + 1 - AZF, z + SNH, half7U, half9V);
            tessellator.addVertexWithUV(x + FNH, y + 1 - AZF, z + FNH, half7U, half7V);
            tessellator.addVertexWithUV(x + SNH, y + 1 - AZF, z + FNH, half9U, half7V);
            tessellator.addVertexWithUV(x + SNH, y + 1 - AZF, z + SNH, half9U, half9V);
        }

        // North

        minU = iconNorth.getMinU();
        half7U = iconNorth.getInterpolatedU(7);
        half9U = iconNorth.getInterpolatedU(9);
        maxU = iconNorth.getMaxU();
        minV = iconNorth.getMinV();
        maxV = iconNorth.getMaxV();

        if (connectionWest && connectionEast && !connectionNorth)
        {
            tessellator.addVertexWithUV(x, y + 1, z + FNH, maxU, minV);
            tessellator.addVertexWithUV(x + 1, y + 1, z + FNH, minU, minV);
            tessellator.addVertexWithUV(x + 1, y, z + FNH, minU, maxV);
            tessellator.addVertexWithUV(x, y, z + FNH, maxU, maxV);
            tessellator.addVertexWithUV(x, y, z + FNH, maxU, maxV);
            tessellator.addVertexWithUV(x + 1, y, z + FNH, minU, maxV);
            tessellator.addVertexWithUV(x + 1, y + 1, z + FNH, minU, minV);
            tessellator.addVertexWithUV(x, y + 1, z + FNH, maxU, minV);
        }
        else
        {
            if (noXZConnection || connectionWest)
            {
                tessellator.addVertexWithUV(x, y + 1, z + FNH, maxU, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half9U, minV);
                tessellator.addVertexWithUV(x + FNH, y, z + FNH, half9U, maxV);
                tessellator.addVertexWithUV(x, y, z + FNH, maxU, maxV);
                tessellator.addVertexWithUV(x, y, z + FNH, maxU, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + FNH, half9U, maxV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half9U, minV);
                tessellator.addVertexWithUV(x, y + 1, z + FNH, maxU, minV);
            }

            if (noXZConnection || connectionEast)
            {
                tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half7U, minV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + FNH, minU, minV);
                tessellator.addVertexWithUV(x + 1, y, z + FNH, minU, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + FNH, half7U, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + FNH, half7U, maxV);
                tessellator.addVertexWithUV(x + 1, y, z + FNH, minU, maxV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + FNH, minU, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half7U, minV);
            }

            if (!noXZConnection && !connectionNorth)
            {
                tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half9U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half7U, minV);
                tessellator.addVertexWithUV(x + SNH, y, z + FNH, half7U, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + FNH, half9U, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + FNH, half9U, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + FNH, half7U, maxV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half7U, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half9U, minV);
            }
        }

        // South

        minU = iconSouth.getMinU();
        half7U = iconSouth.getInterpolatedU(7);
        half9U = iconSouth.getInterpolatedU(9);
        maxU = iconSouth.getMaxU();
        minV = iconSouth.getMinV();
        maxV = iconSouth.getMaxV();

        if (connectionWest && connectionEast && !connectionSouth)
        {
            tessellator.addVertexWithUV(x + 1, y + 1, z + SNH, maxU, minV);
            tessellator.addVertexWithUV(x + 1, y, z + SNH, maxU, maxV);
            tessellator.addVertexWithUV(x, y, z + SNH, minU, maxV);
            tessellator.addVertexWithUV(x, y + 1, z + SNH, minU, minV);
            tessellator.addVertexWithUV(x, y + 1, z + SNH, minU, minV);
            tessellator.addVertexWithUV(x, y, z + SNH, minU, maxV);
            tessellator.addVertexWithUV(x + 1, y, z + SNH, maxU, maxV);
            tessellator.addVertexWithUV(x + 1, y + 1, z + SNH, maxU, minV);
        }
        else
        {
            if (noXZConnection || connectionWest)
            {
                tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half7U, minV);
                tessellator.addVertexWithUV(x + FNH, y, z + SNH, half7U, maxV);
                tessellator.addVertexWithUV(x, y, z + SNH, minU, maxV);
                tessellator.addVertexWithUV(x, y + 1, z + SNH, minU, minV);
                tessellator.addVertexWithUV(x, y + 1, z + SNH, minU, minV);
                tessellator.addVertexWithUV(x, y, z + SNH, minU, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + SNH, half7U, maxV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half7U, minV);
            }

            if (noXZConnection || connectionEast)
            {
                tessellator.addVertexWithUV(x + 1, y + 1, z + SNH, maxU, minV);
                tessellator.addVertexWithUV(x + 1, y, z + SNH, maxU, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + SNH, half9U, maxV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half9U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half9U, minV);
                tessellator.addVertexWithUV(x + SNH, y, z + SNH, half9U, maxV);
                tessellator.addVertexWithUV(x + 1, y, z + SNH, maxU, maxV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + SNH, maxU, minV);
            }

            if (!noXZConnection && !connectionSouth)
            {
                tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half9U, minV);
                tessellator.addVertexWithUV(x + SNH, y, z + SNH, half9U, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + SNH, half7U, maxV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half7U, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half7U, minV);
                tessellator.addVertexWithUV(x + FNH, y, z + SNH, half7U, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + SNH, half9U, maxV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half9U, minV);
            }
        }

        // West
        minU = iconWest.getMinU();
        half7U = iconWest.getInterpolatedU(7);
        half9U = iconWest.getInterpolatedU(9);
        maxU = iconWest.getMaxU();
        minV = iconWest.getMinV();
        maxV = iconWest.getMaxV();

        if (connectionNorth && connectionSouth && !connectionWest)
        {
            tessellator.addVertexWithUV(x + FNH, y, z + 1, maxU, maxV);
            tessellator.addVertexWithUV(x + FNH, y, z, minU, maxV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z, minU, minV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + 1, maxU, minV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + 1, maxU, minV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z, minU, minV);
            tessellator.addVertexWithUV(x + FNH, y, z, minU, maxV);
            tessellator.addVertexWithUV(x + FNH, y, z + 1, maxU, maxV);
        }
        else
        {
            if (noXZConnection || connectionNorth)
            {
                tessellator.addVertexWithUV(x + FNH, y, z + FNH, half7U, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z, minU, maxV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z, minU, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half7U, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half7U, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z, minU, minV);
                tessellator.addVertexWithUV(x + FNH, y, z, minU, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + FNH, half7U, maxV);
            }

            if (noXZConnection || connectionSouth)
            {
                tessellator.addVertexWithUV(x + FNH, y, z + 1, maxU, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + SNH, half9U, maxV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half9U, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + 1, maxU, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + 1, maxU, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half9U, minV);
                tessellator.addVertexWithUV(x + FNH, y, z + SNH, half9U, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + 1, maxU, maxV);
            }

            if (!noXZConnection && !connectionWest)
            {
                tessellator.addVertexWithUV(x + FNH, y, z + SNH, half9U, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + FNH, half7U, maxV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half7U, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half9U, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half9U, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half7U, minV);
                tessellator.addVertexWithUV(x + FNH, y, z + FNH, half7U, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + SNH, half9U, maxV);
            }
        }

        // East
        minU = iconEast.getMinU();
        half7U = iconEast.getInterpolatedU(7);
        half9U = iconEast.getInterpolatedU(9);
        maxU = iconEast.getMaxU();
        minV = iconEast.getMinV();
        maxV = iconEast.getMaxV();

        if (connectionNorth && connectionSouth && !connectionEast)
        {
            tessellator.addVertexWithUV(x + SNH, y, z + 1, minU, maxV);
            tessellator.addVertexWithUV(x + SNH, y, z, maxU, maxV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z, maxU, minV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + 1, minU, minV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + 1, minU, minV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z, maxU, minV);
            tessellator.addVertexWithUV(x + SNH, y, z, maxU, maxV);
            tessellator.addVertexWithUV(x + SNH, y, z + 1, minU, maxV);
        }
        else
        {
            if (noXZConnection || connectionNorth)
            {
                tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z, maxU, maxV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z, maxU, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z, maxU, minV);
                tessellator.addVertexWithUV(x + SNH, y, z, maxU, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, maxV);
            }

            if (noXZConnection || connectionSouth)
            {
                tessellator.addVertexWithUV(x + SNH, y, z + 1, minU, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + SNH, half7U, maxV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half7U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + 1, minU, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + 1, minU, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half7U, minV);
                tessellator.addVertexWithUV(x + SNH, y, z + SNH, half7U, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + 1, minU, maxV);
            }

            if (!noXZConnection && !connectionEast)
            {
                tessellator.addVertexWithUV(x + SNH, y, z + SNH, half7U, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, maxV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half7U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half7U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, minV);
                tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + SNH, half7U, maxV);
            }
        }

        //Sides
        minU = iconSide.getMinU();
        half7U = iconSide.getInterpolatedU(7);
        half9U = iconSide.getInterpolatedU(9);
        maxU = iconSide.getMaxU();
        minV = iconSide.getMinV();
        half7V = iconSide.getInterpolatedV(7);
        half9V = iconSide.getInterpolatedV(9);
        maxV = iconSide.getMaxV();

        if (noXZConnection || connectionNorth && !paneNorth)
        {
            if (noXZConnection)
            {
                tessellator.addVertexWithUV(x + FNH, y, z, half9U, maxV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z, half9U, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z, half7U, minV);
                tessellator.addVertexWithUV(x + SNH, y, z, half7U, maxV);
            }

            tessellator.addVertexWithUV(x + SNH, y, z + AZF, half7U, maxV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + AZF, half7U, minV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + AZF, half9U, minV);
            tessellator.addVertexWithUV(x + FNH, y, z + AZF, half9U, maxV);
        }
        
        if (connectionWest != connectionEast && connectionSouth && !connectionNorth)
        {
            tessellator.addVertexWithUV(x + FNH, y, z + FNH, half9U, maxV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half9U, minV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half7U, minV);
            tessellator.addVertexWithUV(x + SNH, y, z + FNH, half7U, maxV);
            
            tessellator.addVertexWithUV(x + SNH, y, z + FNH, half7U, maxV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half7U, minV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half9U, minV);
            tessellator.addVertexWithUV(x + FNH, y, z + FNH, half9U, maxV);
        }

        if (noXZConnection || connectionSouth && !paneSouth)
        {
            if (noXZConnection)
            {
                tessellator.addVertexWithUV(x + SNH, y, z + 1, half7U, maxV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + 1, half7U, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + 1, half9U, minV);
                tessellator.addVertexWithUV(x + FNH, y, z + 1, half9U, maxV);
            }

            tessellator.addVertexWithUV(x + FNH, y, z + 1 - AZF, half9U, maxV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + 1 - AZF, half9U, minV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + 1 - AZF, half7U, minV);
            tessellator.addVertexWithUV(x + SNH, y, z + 1 - AZF, half7U, maxV);
        }
        
        if (connectionWest != connectionEast && !connectionSouth && connectionNorth)
        {
            tessellator.addVertexWithUV(x + SNH, y, z + SNH, half7U, maxV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half7U, minV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half9U, minV);
            tessellator.addVertexWithUV(x + FNH, y, z + SNH, half9U, maxV);
            
            tessellator.addVertexWithUV(x + FNH, y, z + SNH, half9U, maxV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half9U, minV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half7U, minV);
            tessellator.addVertexWithUV(x + SNH, y, z + SNH, half7U, maxV);
        }

        if (noXZConnection || connectionWest && !paneWest)
        {
            if (noXZConnection)
            {
                tessellator.addVertexWithUV(x, y, z + SNH, half7U, maxV);
                tessellator.addVertexWithUV(x, y + 1, z + SNH, half7U, minV);
                tessellator.addVertexWithUV(x, y + 1, z + FNH, half9U, minV);
                tessellator.addVertexWithUV(x, y, z + FNH, half9U, maxV);
            }

            tessellator.addVertexWithUV(x + AZF, y, z + FNH, half9U, maxV);
            tessellator.addVertexWithUV(x + AZF, y + 1, z + FNH, half9U, minV);
            tessellator.addVertexWithUV(x + AZF, y + 1, z + SNH, half7U, minV);
            tessellator.addVertexWithUV(x + AZF, y, z + SNH, half7U, maxV);
        }
        
        if (connectionNorth != connectionSouth && connectionEast && !connectionWest)
        {
            tessellator.addVertexWithUV(x + FNH, y, z + SNH, half7U, maxV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half7U, minV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half9U, minV);
            tessellator.addVertexWithUV(x + FNH, y, z + FNH, half9U, maxV);
            
            tessellator.addVertexWithUV(x + FNH, y, z + FNH, half9U, maxV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, half9U, minV);
            tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, half7U, minV);
            tessellator.addVertexWithUV(x + FNH, y, z + SNH, half7U, maxV);
        }

        if (noXZConnection || connectionEast && !paneEast)
        {
            if (noXZConnection)
            {
                tessellator.addVertexWithUV(x + 1, y, z + FNH, half9U, maxV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + FNH, half9U, minV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + SNH, half7U, minV);
                tessellator.addVertexWithUV(x + 1, y, z + SNH, half7U, maxV);
            }

            tessellator.addVertexWithUV(x + 1 - AZF, y, z + SNH, half7U, maxV);
            tessellator.addVertexWithUV(x + 1 - AZF, y + 1, z + SNH, half7U, minV);
            tessellator.addVertexWithUV(x + 1 - AZF, y + 1, z + FNH, half9U, minV);
            tessellator.addVertexWithUV(x + 1 - AZF, y, z + FNH, half9U, maxV);
        }
        
        if (connectionNorth != connectionSouth && !connectionEast && connectionWest)
        {
            tessellator.addVertexWithUV(x + SNH, y, z + SNH, half7U, maxV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half7U, minV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, minV);
            tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, maxV);
            
            tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, maxV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, minV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half7U, minV);
            tessellator.addVertexWithUV(x + SNH, y, z + SNH, half7U, maxV);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelID)
    {
        return false;
    }

    @Override
    public int getRenderId ()
    {
        return model;
    }

}
