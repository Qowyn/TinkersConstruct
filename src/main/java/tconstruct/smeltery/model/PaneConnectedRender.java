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

    // first nearly half
    private static final double FNH = 7.0D / 16.0D;
    // second nearly half
    private static final double SNH = 9.0D / 16.0D;
    // half
    private static final double HALF = 8.0D / 16.0D;
    // stop the Z-Fighting!
    private static final double AZF = 0.005D;

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

        // Needed to determine if vertices with side texture should be rendered
        boolean paneDown = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y - 1, z), world.getBlockMetadata(x, y - 1, z));
        boolean paneUp = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z));
        boolean paneNorth = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y, z - 1), world.getBlockMetadata(x, y, z - 1));
        boolean paneSouth = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y, z + 1), world.getBlockMetadata(x, y, z + 1));
        boolean paneWest = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x - 1, y, z), world.getBlockMetadata(x - 1, y, z));
        boolean paneEast = pane.shouldConnectToBlock(world, x, y, z, world.getBlock(x + 1, y, z), world.getBlockMetadata(x + 1, y, z));

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

        if (connectionWest || connectionEast || noXZConnection)
        {
            // North

            IIcon northWest = pane.getConnectedPaneTexture(world, x, y, z, 2, false);
            IIcon northEast = pane.getConnectedPaneTexture(world, x, y, z, 2, true);

            if (!noXZConnection && !connectionNorth)
            {
                // West half
                minU = northWest.getInterpolatedU(8.0D);
                maxU = northWest.getMaxU();
                minV = northWest.getMinV();
                maxV = northWest.getMaxV();
                tessellator.addVertexWithUV(x, y + 1, z + FNH, maxU, minV);
                tessellator.addVertexWithUV(x + HALF, y + 1, z + FNH, minU, minV);
                tessellator.addVertexWithUV(x + HALF, y, z + FNH, minU, maxV);
                tessellator.addVertexWithUV(x, y, z + FNH, maxU, maxV);
                tessellator.addVertexWithUV(x, y, z + FNH, maxU, maxV);
                tessellator.addVertexWithUV(x + HALF, y, z + FNH, minU, maxV);
                tessellator.addVertexWithUV(x + HALF, y + 1, z + FNH, minU, minV);
                tessellator.addVertexWithUV(x, y + 1, z + FNH, maxU, minV);

                // East half
                minU = northEast.getMinU();
                maxU = northEast.getInterpolatedU(8.0D);
                minV = northEast.getMinV();
                maxV = northEast.getMaxV();
                tessellator.addVertexWithUV(x + HALF, y + 1, z + FNH, maxU, minV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + FNH, minU, minV);
                tessellator.addVertexWithUV(x + 1, y, z + FNH, minU, maxV);
                tessellator.addVertexWithUV(x + HALF, y, z + FNH, maxU, maxV);
                tessellator.addVertexWithUV(x + HALF, y, z + FNH, maxU, maxV);
                tessellator.addVertexWithUV(x + 1, y, z + FNH, minU, maxV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + FNH, minU, minV);
                tessellator.addVertexWithUV(x + HALF, y + 1, z + FNH, maxU, minV);
            }
            else
            {
                if (noXZConnection || connectionWest)
                {
                    minU = northWest.getInterpolatedU(9.0D);
                    maxU = northWest.getMaxU();
                    minV = northWest.getMinV();
                    maxV = northWest.getMaxV();
                    tessellator.addVertexWithUV(x, y + 1, z + FNH, maxU, minV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, minU, minV);
                    tessellator.addVertexWithUV(x + FNH, y, z + FNH, minU, maxV);
                    tessellator.addVertexWithUV(x, y, z + FNH, maxU, maxV);
                    tessellator.addVertexWithUV(x, y, z + FNH, maxU, maxV);
                    tessellator.addVertexWithUV(x + FNH, y, z + FNH, minU, maxV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, minU, minV);
                    tessellator.addVertexWithUV(x, y + 1, z + FNH, maxU, minV);
                }

                if (noXZConnection || connectionEast)
                {
                    minU = northEast.getMinU();
                    maxU = northEast.getInterpolatedU(7.0D);
                    minV = northEast.getMinV();
                    maxV = northEast.getMaxV();
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, maxU, minV);
                    tessellator.addVertexWithUV(x + 1, y + 1, z + FNH, minU, minV);
                    tessellator.addVertexWithUV(x + 1, y, z + FNH, minU, maxV);
                    tessellator.addVertexWithUV(x + SNH, y, z + FNH, maxU, maxV);
                    tessellator.addVertexWithUV(x + SNH, y, z + FNH, maxU, maxV);
                    tessellator.addVertexWithUV(x + 1, y, z + FNH, minU, maxV);
                    tessellator.addVertexWithUV(x + 1, y + 1, z + FNH, minU, minV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, maxU, minV);
                }
            }

            // South

            IIcon southWest = pane.getConnectedPaneTexture(world, x, y, z, 3, true);
            IIcon southEast = pane.getConnectedPaneTexture(world, x, y, z, 3, false);

            if (!noXZConnection && !connectionSouth)
            {
                minU = southWest.getMinU();
                maxU = southWest.getInterpolatedU(8.0D);
                minV = southWest.getMinV();
                maxV = southWest.getMaxV();
                tessellator.addVertexWithUV(x + HALF, y + 1, z + SNH, maxU, minV);
                tessellator.addVertexWithUV(x + HALF, y, z + SNH, maxU, maxV);
                tessellator.addVertexWithUV(x, y, z + SNH, minU, maxV);
                tessellator.addVertexWithUV(x, y + 1, z + SNH, minU, minV);
                tessellator.addVertexWithUV(x, y + 1, z + SNH, minU, minV);
                tessellator.addVertexWithUV(x, y, z + SNH, minU, maxV);
                tessellator.addVertexWithUV(x + HALF, y, z + SNH, maxU, maxV);
                tessellator.addVertexWithUV(x + HALF, y + 1, z + SNH, maxU, minV);

                minU = southEast.getInterpolatedU(8.0D);
                maxU = southEast.getMaxU();
                minV = southEast.getMinV();
                maxV = southEast.getMaxV();
                tessellator.addVertexWithUV(x + 1, y + 1, z + SNH, maxU, minV);
                tessellator.addVertexWithUV(x + 1, y, z + SNH, maxU, maxV);
                tessellator.addVertexWithUV(x + HALF, y, z + SNH, minU, maxV);
                tessellator.addVertexWithUV(x + HALF, y + 1, z + SNH, minU, minV);
                tessellator.addVertexWithUV(x + HALF, y + 1, z + SNH, minU, minV);
                tessellator.addVertexWithUV(x + HALF, y, z + SNH, minU, maxV);
                tessellator.addVertexWithUV(x + 1, y, z + SNH, maxU, maxV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + SNH, maxU, minV);
            }
            else
            {
                if (noXZConnection || connectionWest)
                {
                    minU = southWest.getMinU();
                    maxU = southWest.getInterpolatedU(7.0D);
                    minV = southWest.getMinV();
                    maxV = southWest.getMaxV();
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, maxU, minV);
                    tessellator.addVertexWithUV(x + FNH, y, z + SNH, maxU, maxV);
                    tessellator.addVertexWithUV(x, y, z + SNH, minU, maxV);
                    tessellator.addVertexWithUV(x, y + 1, z + SNH, minU, minV);
                    tessellator.addVertexWithUV(x, y + 1, z + SNH, minU, minV);
                    tessellator.addVertexWithUV(x, y, z + SNH, minU, maxV);
                    tessellator.addVertexWithUV(x + FNH, y, z + SNH, maxU, maxV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, maxU, minV);
                }

                if (noXZConnection || connectionEast)
                {
                    minU = southEast.getInterpolatedU(9.0D);
                    maxU = southEast.getMaxU();
                    minV = southEast.getMinV();
                    maxV = southEast.getMaxV();
                    tessellator.addVertexWithUV(x + 1, y + 1, z + SNH, maxU, minV);
                    tessellator.addVertexWithUV(x + 1, y, z + SNH, maxU, maxV);
                    tessellator.addVertexWithUV(x + SNH, y, z + SNH, minU, maxV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, minU, minV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, minU, minV);
                    tessellator.addVertexWithUV(x + SNH, y, z + SNH, minU, maxV);
                    tessellator.addVertexWithUV(x + 1, y, z + SNH, maxU, maxV);
                    tessellator.addVertexWithUV(x + 1, y + 1, z + SNH, maxU, minV);
                }
            }
        }

        if (connectionNorth || connectionSouth || noXZConnection)
        {
            // West
            IIcon westNorth = pane.getConnectedPaneTexture(world, x, y, z, 4, true);
            IIcon westSouth = pane.getConnectedPaneTexture(world, x, y, z, 4, false);

            if (!noXZConnection && !connectionWest)
            {
                minU = westNorth.getMinU();
                maxU = westNorth.getInterpolatedU(8.0D);
                minV = westNorth.getMinV();
                maxV = westNorth.getMaxV();
                tessellator.addVertexWithUV(x + FNH, y, z + HALF, maxU, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z, minU, maxV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z, minU, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + HALF, maxU, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + HALF, maxU, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z, minU, minV);
                tessellator.addVertexWithUV(x + FNH, y, z, minU, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + HALF, maxU, maxV);

                minU = westSouth.getInterpolatedU(8.0D);
                maxU = westSouth.getMaxU();
                minV = westSouth.getMinV();
                maxV = westSouth.getMaxV();
                tessellator.addVertexWithUV(x + FNH, y, z + 1, maxU, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + HALF, minU, maxV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + HALF, minU, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + 1, maxU, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + 1, maxU, minV);
                tessellator.addVertexWithUV(x + FNH, y + 1, z + HALF, minU, minV);
                tessellator.addVertexWithUV(x + FNH, y, z + HALF, minU, maxV);
                tessellator.addVertexWithUV(x + FNH, y, z + 1, maxU, maxV);
            }
            else
            {
                if (noXZConnection || connectionNorth)
                {
                    minU = westNorth.getMinU();
                    maxU = westNorth.getInterpolatedU(7.0D);
                    minV = westNorth.getMinV();
                    maxV = westNorth.getMaxV();
                    tessellator.addVertexWithUV(x + FNH, y, z + FNH, maxU, maxV);
                    tessellator.addVertexWithUV(x + FNH, y, z, minU, maxV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z, minU, minV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, maxU, minV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + FNH, maxU, minV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z, minU, minV);
                    tessellator.addVertexWithUV(x + FNH, y, z, minU, maxV);
                    tessellator.addVertexWithUV(x + FNH, y, z + FNH, maxU, maxV);
                }

                if (noXZConnection || connectionSouth)
                {
                    minU = westSouth.getInterpolatedU(9.0D);
                    maxU = westSouth.getMaxU();
                    minV = westSouth.getMinV();
                    maxV = westSouth.getMaxV();
                    tessellator.addVertexWithUV(x + FNH, y, z + 1, maxU, maxV);
                    tessellator.addVertexWithUV(x + FNH, y, z + SNH, minU, maxV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, minU, minV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + 1, maxU, minV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + 1, maxU, minV);
                    tessellator.addVertexWithUV(x + FNH, y + 1, z + SNH, minU, minV);
                    tessellator.addVertexWithUV(x + FNH, y, z + SNH, minU, maxV);
                    tessellator.addVertexWithUV(x + FNH, y, z + 1, maxU, maxV);
                }
            }

            // East
            IIcon eastNorth = pane.getConnectedPaneTexture(world, x, y, z, 5, false);
            IIcon eastSouth = pane.getConnectedPaneTexture(world, x, y, z, 5, true);

            if (!noXZConnection && !connectionEast)
            {
                minU = eastNorth.getInterpolatedU(8.0D);
                maxU = eastNorth.getMaxU();
                minV = eastNorth.getMinV();
                maxV = eastNorth.getMaxV();
                tessellator.addVertexWithUV(x + SNH, y, z + HALF, minU, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z, maxU, maxV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z, maxU, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + HALF, minU, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + HALF, minU, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z, maxU, minV);
                tessellator.addVertexWithUV(x + SNH, y, z, maxU, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + HALF, minU, maxV);

                minU = eastSouth.getMinU();
                maxU = eastSouth.getInterpolatedU(8.0D);
                minV = eastSouth.getMinV();
                maxV = eastSouth.getMaxV();
                tessellator.addVertexWithUV(x + SNH, y, z + 1, minU, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + HALF, maxU, maxV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + HALF, maxU, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + 1, minU, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + 1, minU, minV);
                tessellator.addVertexWithUV(x + SNH, y + 1, z + HALF, maxU, minV);
                tessellator.addVertexWithUV(x + SNH, y, z + HALF, maxU, maxV);
                tessellator.addVertexWithUV(x + SNH, y, z + 1, minU, maxV);
            }
            else
            {
                if (noXZConnection || connectionNorth)
                {
                    minU = eastNorth.getInterpolatedU(9.0D);
                    maxU = eastNorth.getMaxU();
                    minV = eastNorth.getMinV();
                    maxV = eastNorth.getMaxV();
                    tessellator.addVertexWithUV(x + SNH, y, z + FNH, minU, maxV);
                    tessellator.addVertexWithUV(x + SNH, y, z, maxU, maxV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z, maxU, minV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, minU, minV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, minU, minV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z, maxU, minV);
                    tessellator.addVertexWithUV(x + SNH, y, z, maxU, maxV);
                    tessellator.addVertexWithUV(x + SNH, y, z + FNH, minU, maxV);
                }

                if (noXZConnection || connectionSouth)
                {
                    minU = eastSouth.getMinU();
                    maxU = eastSouth.getInterpolatedU(7.0D);
                    minV = eastSouth.getMinV();
                    maxV = eastSouth.getMaxV();
                    tessellator.addVertexWithUV(x + SNH, y, z + 1, minU, maxV);
                    tessellator.addVertexWithUV(x + SNH, y, z + SNH, maxU, maxV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, maxU, minV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + 1, minU, minV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + 1, minU, minV);
                    tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, maxU, minV);
                    tessellator.addVertexWithUV(x + SNH, y, z + SNH, maxU, maxV);
                    tessellator.addVertexWithUV(x + SNH, y, z + 1, minU, maxV);
                }
            }
        }
        
        minU = iconSide.getMinU();
        half7U = iconSide.getInterpolatedU(7);
        half9U = iconSide.getInterpolatedU(9);
        maxU = iconSide.getMaxU();
        minV = iconSide.getMinV();
        half7V = iconSide.getInterpolatedV(7);
        half9V = iconSide.getInterpolatedV(9);
        maxV = iconSide.getMaxV();
        
        // Down

        if ((noXZConnection || connectionWest) && (!paneDown || !pane.canPaneConnectTo(world, x, y - 1, z, WEST)))
        {
            if (!connectionDown && paneDown)
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

        if ((noXZConnection || connectionEast) && (!paneDown || !pane.canPaneConnectTo(world, x, y - 1, z, EAST)))
        {
            if (!connectionDown && paneDown)
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

        if ((noXZConnection || connectionNorth) && (!paneDown || !pane.canPaneConnectTo(world, x, y - 1, z, NORTH)))
        {
            if (!connectionDown && paneDown)
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

        if ((noXZConnection || connectionSouth) && (!paneDown || !pane.canPaneConnectTo(world, x, y - 1, z, SOUTH)))
        {
            if (!connectionDown && paneDown)
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

        if (!paneDown)
        {
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

        // Up

        if ((noXZConnection || connectionWest) && (!paneUp || !pane.canPaneConnectTo(world, x, y + 1, z, WEST)))
        {
            if (!connectionUp || paneUp)
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

        if ((noXZConnection || connectionEast) && (!paneUp || !pane.canPaneConnectTo(world, x, y + 1, z, EAST)))
        {
            if (!connectionUp || paneUp)
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

        if ((noXZConnection || connectionNorth) && (!paneUp || !pane.canPaneConnectTo(world, x, y + 1, z, NORTH)))
        {
            if (!connectionUp || paneUp)
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

        if ((noXZConnection || connectionSouth) && (!paneUp || !pane.canPaneConnectTo(world, x, y + 1, z, SOUTH)))
        {
            if (!connectionUp || paneUp)
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

        if (!paneUp)
        {
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

        //Sides

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

        if (!connectionNorth && connectionSouth && !connectionWest && !connectionEast)
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

        if (connectionNorth && !connectionSouth && !connectionWest && !connectionEast)
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

        if (!connectionNorth && !connectionSouth && !connectionWest && connectionEast)
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

        if (!connectionNorth && !connectionSouth && connectionWest && !connectionEast)
        {
            tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, maxV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, minV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half7U, minV);
            tessellator.addVertexWithUV(x + SNH, y, z + SNH, half7U, maxV);

            tessellator.addVertexWithUV(x + SNH, y, z + SNH, half7U, maxV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + SNH, half7U, minV);
            tessellator.addVertexWithUV(x + SNH, y + 1, z + FNH, half9U, minV);
            tessellator.addVertexWithUV(x + SNH, y, z + FNH, half9U, maxV);
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
