package tconstruct.smeltery.model;

import org.lwjgl.opengl.GL11;

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

    // 7 pixels at default resolution
    private static final double P7 = 7.0D / 16.0D;
    // 8 pixels at default resolution
    private static final double P8 = 8.0D / 16.0D;
    // 9 pixels at default resolution
    private static final double P9 = 9.0D / 16.0D;
    
    // stop the Z-Fighting!
    private static final double AZF = 0.0005D;

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;
        
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        GlassPaneConnected pane = (GlassPaneConnected) block;
        
        int meta = world.getBlockMetadata(x, y, z);

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

        IIcon iconSide = pane.getSideTexture(meta);

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

        double minU, maxU, minV, halfV, maxV;

        if (connectionWest || connectionEast || noXZConnection)
        {
            // North

            IIcon northWest = pane.getConnectedPaneTexture(world, x, y, z, 2, false, meta);
            IIcon northEast = pane.getConnectedPaneTexture(world, x, y, z, 2, true, meta);

            if (!noXZConnection && !connectionNorth)
            {
                // West half
                if (northWest != iconSide)
                {
                    minU = northWest.getInterpolatedU(8.0D);
                    maxU = northWest.getMaxU();
                    minV = northWest.getMinV();
                    maxV = northWest.getMaxV();
                    tessellator.addVertexWithUV(x, y + 1, z + P7, maxU, minV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P8, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P8, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x, y + 1, z + P7, maxU, minV);
                }
                else
                {
                    minU = iconSide.getInterpolatedU(7.0D);
                    maxU = iconSide.getInterpolatedU(8.0D);
                    minV = iconSide.getMinV();
                    maxV = iconSide.getMaxV();
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P7, maxU, minV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P8, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P8, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P7, maxU, minV);
                }

                // East half
                if (northEast != iconSide)
                {
                    minU = northEast.getMinU();
                    maxU = northEast.getInterpolatedU(8.0D);
                    minV = northEast.getMinV();
                    maxV = northEast.getMaxV();
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P7, maxU, minV);
                    tessellator.addVertexWithUV(x + 1, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + 1, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P8, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P8, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + 1, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + 1, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P7, maxU, minV);
                }
                else
                {
                    minU = iconSide.getInterpolatedU(8.0D);
                    maxU = iconSide.getInterpolatedU(9.0D);
                    minV = iconSide.getMinV();
                    maxV = iconSide.getMaxV();
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P7, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P8, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P8, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P7, maxU, minV);
                }
            }
            else
            {
                if (noXZConnection || connectionWest)
                {
                    minU = northWest.getInterpolatedU(9.0D);
                    maxU = northWest.getMaxU();
                    minV = northWest.getMinV();
                    maxV = northWest.getMaxV();
                    tessellator.addVertexWithUV(x, y + 1, z + P7, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x, y + 1, z + P7, maxU, minV);
                }

                if (noXZConnection || connectionEast)
                {
                    minU = northEast.getMinU();
                    maxU = northEast.getInterpolatedU(7.0D);
                    minV = northEast.getMinV();
                    maxV = northEast.getMaxV();
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P7, maxU, minV);
                    tessellator.addVertexWithUV(x + 1, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + 1, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + 1, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + 1, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P7, maxU, minV);
                }
            }

            // South

            IIcon southWest = pane.getConnectedPaneTexture(world, x, y, z, 3, true, meta);
            IIcon southEast = pane.getConnectedPaneTexture(world, x, y, z, 3, false, meta);

            if (!noXZConnection && !connectionSouth)
            {
                // West half
                if (southWest != iconSide)
                {
                    minU = southWest.getMinU();
                    maxU = southWest.getInterpolatedU(8.0D);
                    minV = southWest.getMinV();
                    maxV = southWest.getMaxV();
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P9, maxU, minV);
                    tessellator.addVertexWithUV(x + P8, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P8, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P9, maxU, minV);
                }
                else
                {
                    minU = iconSide.getInterpolatedU(8.0D);
                    maxU = iconSide.getInterpolatedU(9.0D);
                    minV = iconSide.getMinV();
                    maxV = iconSide.getMaxV();
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P9, maxU, minV);
                    tessellator.addVertexWithUV(x + P8, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P8, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P9, maxU, minV);
                }

                // East half
                if (southEast != iconSide)
                {
                    minU = southEast.getInterpolatedU(8.0D);
                    maxU = southEast.getMaxU();
                    minV = southEast.getMinV();
                    maxV = southEast.getMaxV();
                    tessellator.addVertexWithUV(x + 1, y + 1, z + P9, maxU, minV);
                    tessellator.addVertexWithUV(x + 1, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P8, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P8, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + 1, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + 1, y + 1, z + P9, maxU, minV);
                } 
                else
                {
                    minU = iconSide.getInterpolatedU(7.0D);
                    maxU = iconSide.getInterpolatedU(8.0D);
                    minV = iconSide.getMinV();
                    maxV = iconSide.getMaxV();
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P9, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P8, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P8, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P8, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P9, maxU, minV);
                }
            }
            else
            {
                if (noXZConnection || connectionWest)
                {
                    minU = southWest.getMinU();
                    maxU = southWest.getInterpolatedU(7.0D);
                    minV = southWest.getMinV();
                    maxV = southWest.getMaxV();
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P9, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P9, maxU, minV);
                }

                if (noXZConnection || connectionEast)
                {
                    minU = southEast.getInterpolatedU(9.0D);
                    maxU = southEast.getMaxU();
                    minV = southEast.getMinV();
                    maxV = southEast.getMaxV();
                    tessellator.addVertexWithUV(x + 1, y + 1, z + P9, maxU, minV);
                    tessellator.addVertexWithUV(x + 1, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + 1, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + 1, y + 1, z + P9, maxU, minV);
                }
            }
        }

        if (connectionNorth || connectionSouth || noXZConnection)
        {
            // West
            IIcon westNorth = pane.getConnectedPaneTexture(world, x, y, z, 4, true, meta);
            IIcon westSouth = pane.getConnectedPaneTexture(world, x, y, z, 4, false, meta);

            if (!noXZConnection && !connectionWest)
            {
                // North half
                if (westNorth != iconSide)
                {
                    minU = westNorth.getMinU();
                    maxU = westNorth.getInterpolatedU(8.0D);
                    minV = westNorth.getMinV();
                    maxV = westNorth.getMaxV();
                    tessellator.addVertexWithUV(x + P7, y, z + P8, maxU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P8, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P8, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y, z, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P8, maxU, maxV);
                }
                else
                {
                    minU = iconSide.getInterpolatedU(8.0D);
                    maxU = iconSide.getInterpolatedU(9.0D);
                    minV = iconSide.getMinV();
                    maxV = iconSide.getMaxV();
                    tessellator.addVertexWithUV(x + P7, y, z + P8, maxU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P8, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P8, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P8, maxU, maxV);
                }

                // South half
                if (westSouth != iconSide)
                {
                    minU = westSouth.getInterpolatedU(8.0D);
                    maxU = westSouth.getMaxU();
                    minV = westSouth.getMinV();
                    maxV = westSouth.getMaxV();
                    tessellator.addVertexWithUV(x + P7, y, z + 1, maxU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P8, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P8, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + 1, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + 1, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P8, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y, z + P8, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + 1, maxU, maxV);
                }
                else
                {
                    minU = iconSide.getInterpolatedU(7.0D);
                    maxU = iconSide.getInterpolatedU(8.0D);
                    minV = iconSide.getMinV();
                    maxV = iconSide.getMaxV();
                    tessellator.addVertexWithUV(x + P7, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P8, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P8, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P9, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P9, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P8, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y, z + P8, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P9, maxU, maxV);
                }
            }
            else
            {
                if (noXZConnection || connectionNorth)
                {
                    minU = westNorth.getMinU();
                    maxU = westNorth.getInterpolatedU(7.0D);
                    minV = westNorth.getMinV();
                    maxV = westNorth.getMaxV();
                    tessellator.addVertexWithUV(x + P7, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P7, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P7, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y, z, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P7, maxU, maxV);
                }

                if (noXZConnection || connectionSouth)
                {
                    minU = westSouth.getInterpolatedU(9.0D);
                    maxU = westSouth.getMaxU();
                    minV = westSouth.getMinV();
                    maxV = westSouth.getMaxV();
                    tessellator.addVertexWithUV(x + P7, y, z + 1, maxU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + 1, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + 1, maxU, minV);
                    tessellator.addVertexWithUV(x + P7, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P7, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P7, y, z + 1, maxU, maxV);
                }
            }

            // East
            IIcon eastNorth = pane.getConnectedPaneTexture(world, x, y, z, 5, false, meta);
            IIcon eastSouth = pane.getConnectedPaneTexture(world, x, y, z, 5, true, meta);

            if (!noXZConnection && !connectionEast)
            {
                // North half
                if (eastNorth != iconSide)
                {
                    minU = eastNorth.getInterpolatedU(8.0D);
                    maxU = eastNorth.getMaxU();
                    minV = eastNorth.getMinV();
                    maxV = eastNorth.getMaxV();
                    tessellator.addVertexWithUV(x + P9, y, z + P8, minU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P8, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P8, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y, z, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P8, minU, maxV);
                }
                else
                {
                    minU = iconSide.getInterpolatedU(7.0D);
                    maxU = iconSide.getInterpolatedU(8.0D);
                    minV = iconSide.getMinV();
                    maxV = iconSide.getMaxV();
                    tessellator.addVertexWithUV(x + P9, y, z + P8, minU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P7, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P8, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P8, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P7, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y, z + P7, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P8, minU, maxV);
                }

                // South half
                if (eastSouth != iconSide)
                {
                    minU = eastSouth.getMinU();
                    maxU = eastSouth.getInterpolatedU(8.0D);
                    minV = eastSouth.getMinV();
                    maxV = eastSouth.getMaxV();
                    tessellator.addVertexWithUV(x + P9, y, z + 1, minU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P8, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P8, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + 1, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + 1, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P8, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y, z + P8, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + 1, minU, maxV);
                }
                else
                {
                    minU = iconSide.getInterpolatedU(8.0D);
                    maxU = iconSide.getInterpolatedU(9.0D);
                    minV = iconSide.getMinV();
                    maxV = iconSide.getMaxV();
                    tessellator.addVertexWithUV(x + P9, y, z + P9, minU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P8, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P8, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P9, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P8, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y, z + P8, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P9, minU, maxV);
                }
            }
            else
            {
                if (noXZConnection || connectionNorth)
                {
                    minU = eastNorth.getInterpolatedU(9.0D);
                    maxU = eastNorth.getMaxU();
                    minV = eastNorth.getMinV();
                    maxV = eastNorth.getMaxV();
                    tessellator.addVertexWithUV(x + P9, y, z + P7, minU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P7, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y, z, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P7, minU, maxV);
                }

                if (noXZConnection || connectionSouth)
                {
                    minU = eastSouth.getMinU();
                    maxU = eastSouth.getInterpolatedU(7.0D);
                    minV = eastSouth.getMinV();
                    maxV = eastSouth.getMaxV();
                    tessellator.addVertexWithUV(x + P9, y, z + 1, minU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P9, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + 1, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + 1, minU, minV);
                    tessellator.addVertexWithUV(x + P9, y + 1, z + P9, maxU, minV);
                    tessellator.addVertexWithUV(x + P9, y, z + P9, maxU, maxV);
                    tessellator.addVertexWithUV(x + P9, y, z + 1, minU, maxV);
                }
            }
        }
        
        // Down
        
        // U for west and east
        minU = iconSide.getInterpolatedU(9);
        maxU = iconSide.getMaxU();
        
        minV = iconSide.getInterpolatedV(4);
        halfV = iconSide.getInterpolatedV(6);
        maxV = iconSide.getInterpolatedV(8);

        if ((noXZConnection || connectionWest) && (!paneDown || !pane.canPaneConnectTo(world, x, y - 1, z, WEST)))
        {
            if (!connectionDown || paneDown)
            {
                tessellator.addVertexWithUV(x, y, z + P9, minU, halfV);
                tessellator.addVertexWithUV(x, y, z + P7, minU, minV);
                tessellator.addVertexWithUV(x + P7, y, z + P7, maxU, minV);
                tessellator.addVertexWithUV(x + P7, y, z + P9, maxU, halfV);
            }

            tessellator.addVertexWithUV(x + P7, y + AZF, z + P9, maxU, halfV);
            tessellator.addVertexWithUV(x + P7, y + AZF, z + P7, maxU, minV);
            tessellator.addVertexWithUV(x, y + AZF, z + P7, minU, minV);
            tessellator.addVertexWithUV(x, y + AZF, z + P9, minU, halfV);
        }

        if ((noXZConnection || connectionEast) && (!paneDown || !pane.canPaneConnectTo(world, x, y - 1, z, EAST)))
        {
            if (!connectionDown || paneDown)
            {
                tessellator.addVertexWithUV(x + P9, y, z + P9, minU, maxV);
                tessellator.addVertexWithUV(x + P9, y, z + P7, minU, halfV);
                tessellator.addVertexWithUV(x + 1, y, z + P7, maxU, halfV);
                tessellator.addVertexWithUV(x + 1, y, z + P9, maxU, maxV);
            }

            tessellator.addVertexWithUV(x + 1, y + AZF, z + P9, maxU, maxV);
            tessellator.addVertexWithUV(x + 1, y + AZF, z + P7, maxU, halfV);
            tessellator.addVertexWithUV(x + P9, y + AZF, z + P7, minU, halfV);
            tessellator.addVertexWithUV(x + P9, y + AZF, z + P9, minU, maxV);
        }
        
        // U for north, south and center
        minU = iconSide.getInterpolatedU(2);
        maxU = iconSide.getInterpolatedU(4);

        if ((noXZConnection || connectionNorth) && (!paneDown || !pane.canPaneConnectTo(world, x, y - 1, z, NORTH)))
        {
            minV = iconSide.getMinV();
            maxV = iconSide.getInterpolatedV(7);
            
            if (!connectionDown || paneDown)
            {
                tessellator.addVertexWithUV(x + P7, y, z + P7, minU, maxV);
                tessellator.addVertexWithUV(x + P7, y, z, minU, minV);
                tessellator.addVertexWithUV(x + P9, y, z, maxU, minV);
                tessellator.addVertexWithUV(x + P9, y, z + P7, maxU, maxV);
            }

            tessellator.addVertexWithUV(x + P9, y + AZF, z + P7, maxU, maxV);
            tessellator.addVertexWithUV(x + P9, y + AZF, z, maxU, minV);
            tessellator.addVertexWithUV(x + P7, y + AZF, z, minU, minV);
            tessellator.addVertexWithUV(x + P7, y + AZF, z + P7, minU, maxV);
        }

        if ((noXZConnection || connectionSouth) && (!paneDown || !pane.canPaneConnectTo(world, x, y - 1, z, SOUTH)))
        {
            minV = iconSide.getInterpolatedV(9);
            maxV = iconSide.getMaxV();
            
            if (!connectionDown || paneDown)
            {
                tessellator.addVertexWithUV(x + P7, y, z + 1, minU, maxV);
                tessellator.addVertexWithUV(x + P7, y, z + P9, minU, minV);
                tessellator.addVertexWithUV(x + P9, y, z + P9, maxU, minV);
                tessellator.addVertexWithUV(x + P9, y, z + 1, maxU, maxV);
            }

            tessellator.addVertexWithUV(x + P9, y + AZF, z + 1, maxU, maxV);
            tessellator.addVertexWithUV(x + P9, y + AZF, z + P9, maxU, minV);
            tessellator.addVertexWithUV(x + P7, y + AZF, z + P9, minU, minV);
            tessellator.addVertexWithUV(x + P7, y + AZF, z + 1, minU, maxV);
        }

        if (!paneDown)
        {
            minV = iconSide.getInterpolatedV(7);
            maxV = iconSide.getInterpolatedV(9);
            
            if (!connectionDown)
            {
                tessellator.addVertexWithUV(x + P7, y, z + P9, minU, maxV);
                tessellator.addVertexWithUV(x + P7, y, z + P7, minU, minV);
                tessellator.addVertexWithUV(x + P9, y, z + P7, maxU, minV);
                tessellator.addVertexWithUV(x + P9, y, z + P9, maxU, maxV);
            }
    
            tessellator.addVertexWithUV(x + P9, y + AZF, z + P9, maxU, maxV);
            tessellator.addVertexWithUV(x + P9, y + AZF, z + P7, maxU, minV);
            tessellator.addVertexWithUV(x + P7, y + AZF, z + P7, minU, minV);
            tessellator.addVertexWithUV(x + P7, y + AZF, z + P9, minU, maxV);
        }

        // Up
        
        // U for west and east
        minU = iconSide.getInterpolatedU(9);
        maxU = iconSide.getMaxU();
        
        minV = iconSide.getMinV();
        halfV = iconSide.getInterpolatedV(2);
        maxV = iconSide.getInterpolatedV(4);
        
        if ((noXZConnection || connectionWest) && (!paneUp || !pane.canPaneConnectTo(world, x, y + 1, z, WEST)))
        {
            if (!connectionUp || paneUp)
            {
                tessellator.addVertexWithUV(x + P7, y + 1, z + P9, maxU, halfV);
                tessellator.addVertexWithUV(x + P7, y + 1, z + P7, maxU, minV);
                tessellator.addVertexWithUV(x, y + 1, z + P7, minU, minV);
                tessellator.addVertexWithUV(x, y + 1, z + P9, minU, halfV);
            }

            tessellator.addVertexWithUV(x, y + 1 - AZF, z + P9, minU, halfV);
            tessellator.addVertexWithUV(x, y + 1 - AZF, z + P7, minU, minV);
            tessellator.addVertexWithUV(x + P7, y + 1 - AZF, z + P7, maxU, minV);
            tessellator.addVertexWithUV(x + P7, y + 1 - AZF, z + P9, maxU, halfV);
        }

        if ((noXZConnection || connectionEast) && (!paneUp || !pane.canPaneConnectTo(world, x, y + 1, z, EAST)))
        {
            if (!connectionUp || paneUp)
            {
                tessellator.addVertexWithUV(x + 1, y + 1, z + P9, maxU, maxV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + P7, maxU, halfV);
                tessellator.addVertexWithUV(x + P9, y + 1, z + P7, minU, halfV);
                tessellator.addVertexWithUV(x + P9, y + 1, z + P9, minU, maxV);
            }

            tessellator.addVertexWithUV(x + P9, y + 1 - AZF, z + P9, minU, maxV);
            tessellator.addVertexWithUV(x + P9, y + 1 - AZF, z + P7, minU, halfV);
            tessellator.addVertexWithUV(x + 1, y + 1 - AZF, z + P7, maxU, halfV);
            tessellator.addVertexWithUV(x + 1, y + 1 - AZF, z + P9, maxU, maxV);
        }
        
        // U for north, south and center
        minU = iconSide.getMinU();
        maxU = iconSide.getInterpolatedU(2);

        if ((noXZConnection || connectionNorth) && (!paneUp || !pane.canPaneConnectTo(world, x, y + 1, z, NORTH)))
        {
            minV = iconSide.getMinV();
            maxV = iconSide.getInterpolatedV(7);

            if (!connectionUp || paneUp)
            {
                tessellator.addVertexWithUV(x + P9, y + 1, z + P7, maxU, maxV);
                tessellator.addVertexWithUV(x + P9, y + 1, z, maxU, minV);
                tessellator.addVertexWithUV(x + P7, y + 1, z, minU, minV);
                tessellator.addVertexWithUV(x + P7, y + 1, z + P7, minU, maxV);
            }

            tessellator.addVertexWithUV(x + P7, y + 1 - AZF, z + P7, minU, maxV);
            tessellator.addVertexWithUV(x + P7, y + 1 - AZF, z, minU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1 - AZF, z, maxU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1 - AZF, z + P7, maxU, maxV);
        }

        if ((noXZConnection || connectionSouth) && (!paneUp || !pane.canPaneConnectTo(world, x, y + 1, z, SOUTH)))
        {
            minV = iconSide.getInterpolatedV(9);
            maxV = iconSide.getMaxV();

            if (!connectionUp || paneUp)
            {
                tessellator.addVertexWithUV(x + P9, y + 1, z + 1, maxU, maxV);
                tessellator.addVertexWithUV(x + P9, y + 1, z + P9, maxU, minV);
                tessellator.addVertexWithUV(x + P7, y + 1, z + P9, minU, minV);
                tessellator.addVertexWithUV(x + P7, y + 1, z + 1, minU, maxV);
            }

            tessellator.addVertexWithUV(x + P7, y + 1 - AZF, z + 1, minU, maxV);
            tessellator.addVertexWithUV(x + P7, y + 1 - AZF, z + P9, minU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1 - AZF, z + P9, maxU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1 - AZF, z + 1, maxU, maxV);
        }

        if (!paneUp)
        {
            minV = iconSide.getInterpolatedV(7);
            maxV = iconSide.getInterpolatedV(9);

            if (!connectionUp)
            {
                tessellator.addVertexWithUV(x + P9, y + 1, z + P9, maxU, maxV);
                tessellator.addVertexWithUV(x + P9, y + 1, z + P7, maxU, minV);
                tessellator.addVertexWithUV(x + P7, y + 1, z + P7, minU, minV);
                tessellator.addVertexWithUV(x + P7, y + 1, z + P9, minU, maxV);
            }

            tessellator.addVertexWithUV(x + P7, y + 1 - AZF, z + P9, minU, maxV);
            tessellator.addVertexWithUV(x + P7, y + 1 - AZF, z + P7, minU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1 - AZF, z + P7, maxU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1 - AZF, z + P9, maxU, maxV);
        }

        //Sides
        minU = iconSide.getInterpolatedU(7);
        maxU = iconSide.getInterpolatedU(9);
        minV = iconSide.getMinV();
        maxV = iconSide.getMaxV();

        if (noXZConnection || connectionNorth && !paneNorth)
        {
            if (noXZConnection)
            {
                tessellator.addVertexWithUV(x + P7, y, z, maxU, maxV);
                tessellator.addVertexWithUV(x + P7, y + 1, z, maxU, minV);
                tessellator.addVertexWithUV(x + P9, y + 1, z, minU, minV);
                tessellator.addVertexWithUV(x + P9, y, z, minU, maxV);
            }

            tessellator.addVertexWithUV(x + P9, y, z + AZF, minU, maxV);
            tessellator.addVertexWithUV(x + P9, y + 1, z + AZF, minU, minV);
            tessellator.addVertexWithUV(x + P7, y + 1, z + AZF, maxU, minV);
            tessellator.addVertexWithUV(x + P7, y, z + AZF, maxU, maxV);
        }

        if (!connectionNorth && connectionSouth && !connectionWest && !connectionEast)
        {
            tessellator.addVertexWithUV(x + P7, y, z + P7, minU, maxV);
            tessellator.addVertexWithUV(x + P7, y + 1, z + P7, minU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1, z + P7, maxU, minV);
            tessellator.addVertexWithUV(x + P9, y, z + P7, maxU, maxV);

            tessellator.addVertexWithUV(x + P9, y, z + P7, maxU, maxV);
            tessellator.addVertexWithUV(x + P9, y + 1, z + P7, maxU, minV);
            tessellator.addVertexWithUV(x + P7, y + 1, z + P7, minU, minV);
            tessellator.addVertexWithUV(x + P7, y, z + P7, minU, maxV);
        }

        if (noXZConnection || connectionSouth && !paneSouth)
        {
            if (noXZConnection)
            {
                tessellator.addVertexWithUV(x + P9, y, z + 1, maxU, maxV);
                tessellator.addVertexWithUV(x + P9, y + 1, z + 1, maxU, minV);
                tessellator.addVertexWithUV(x + P7, y + 1, z + 1, minU, minV);
                tessellator.addVertexWithUV(x + P7, y, z + 1, minU, maxV);
            }

            tessellator.addVertexWithUV(x + P7, y, z + 1 - AZF, minU, maxV);
            tessellator.addVertexWithUV(x + P7, y + 1, z + 1 - AZF, minU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1, z + 1 - AZF, maxU, minV);
            tessellator.addVertexWithUV(x + P9, y, z + 1 - AZF, maxU, maxV);
        }

        if (connectionNorth && !connectionSouth && !connectionWest && !connectionEast)
        {
            tessellator.addVertexWithUV(x + P9, y, z + P9, maxU, maxV);
            tessellator.addVertexWithUV(x + P9, y + 1, z + P9, maxU, minV);
            tessellator.addVertexWithUV(x + P7, y + 1, z + P9, minU, minV);
            tessellator.addVertexWithUV(x + P7, y, z + P9, minU, maxV);

            tessellator.addVertexWithUV(x + P7, y, z + P9, minU, maxV);
            tessellator.addVertexWithUV(x + P7, y + 1, z + P9, minU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1, z + P9, maxU, minV);
            tessellator.addVertexWithUV(x + P9, y, z + P9, maxU, maxV);
        }

        if (noXZConnection || connectionWest && !paneWest)
        {
            if (noXZConnection)
            {
                tessellator.addVertexWithUV(x, y, z + P9, maxU, maxV);
                tessellator.addVertexWithUV(x, y + 1, z + P9, maxU, minV);
                tessellator.addVertexWithUV(x, y + 1, z + P7, minU, minV);
                tessellator.addVertexWithUV(x, y, z + P7, minU, maxV);
            }

            tessellator.addVertexWithUV(x + AZF, y, z + P7, minU, maxV);
            tessellator.addVertexWithUV(x + AZF, y + 1, z + P7, minU, minV);
            tessellator.addVertexWithUV(x + AZF, y + 1, z + P9, maxU, minV);
            tessellator.addVertexWithUV(x + AZF, y, z + P9, maxU, maxV);
        }

        if (!connectionNorth && !connectionSouth && !connectionWest && connectionEast)
        {
            tessellator.addVertexWithUV(x + P7, y, z + P9, maxU, maxV);
            tessellator.addVertexWithUV(x + P7, y + 1, z + P9, maxU, minV);
            tessellator.addVertexWithUV(x + P7, y + 1, z + P7, minU, minV);
            tessellator.addVertexWithUV(x + P7, y, z + P7, minU, maxV);

            tessellator.addVertexWithUV(x + P7, y, z + P7, minU, maxV);
            tessellator.addVertexWithUV(x + P7, y + 1, z + P7, minU, minV);
            tessellator.addVertexWithUV(x + P7, y + 1, z + P9, maxU, minV);
            tessellator.addVertexWithUV(x + P7, y, z + P9, maxU, maxV);
        }

        if (noXZConnection || connectionEast && !paneEast)
        {
            if (noXZConnection)
            {
                tessellator.addVertexWithUV(x + 1, y, z + P7, maxU, maxV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + P7, maxU, minV);
                tessellator.addVertexWithUV(x + 1, y + 1, z + P9, minU, minV);
                tessellator.addVertexWithUV(x + 1, y, z + P9, minU, maxV);
            }

            tessellator.addVertexWithUV(x + 1 - AZF, y, z + P9, minU, maxV);
            tessellator.addVertexWithUV(x + 1 - AZF, y + 1, z + P9, minU, minV);
            tessellator.addVertexWithUV(x + 1 - AZF, y + 1, z + P7, maxU, minV);
            tessellator.addVertexWithUV(x + 1 - AZF, y, z + P7, maxU, maxV);
        }

        if (!connectionNorth && !connectionSouth && connectionWest && !connectionEast)
        {
            tessellator.addVertexWithUV(x + P9, y, z + P7, minU, maxV);
            tessellator.addVertexWithUV(x + P9, y + 1, z + P7, minU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1, z + P9, maxU, minV);
            tessellator.addVertexWithUV(x + P9, y, z + P9, maxU, maxV);

            tessellator.addVertexWithUV(x + P9, y, z + P9, maxU, maxV);
            tessellator.addVertexWithUV(x + P9, y + 1, z + P9, maxU, minV);
            tessellator.addVertexWithUV(x + P9, y + 1, z + P7, minU, minV);
            tessellator.addVertexWithUV(x + P9, y, z + P7, minU, maxV);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelID)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return model;
    }

}
