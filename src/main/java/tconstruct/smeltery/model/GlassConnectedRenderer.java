package tconstruct.smeltery.model;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import tconstruct.smeltery.blocks.GlassBlockConnected;
import tconstruct.smeltery.blocks.GlassPaneConnected;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class GlassConnectedRenderer implements ISimpleBlockRenderingHandler
{

    public static int model = RenderingRegistry.getNextAvailableRenderId();

    // half
    private static final double HALF = 8.0D / 16.0D;

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
        GlassBlockConnected glass = (GlassBlockConnected) block;
        
        int meta = world.getBlockMetadata(x, y, z);

        // Connection state
        boolean connectionDown = glass.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y - 1, z), world.getBlockMetadata(x, y - 1, z));
        boolean connectionUp = glass.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z));
        boolean connectionNorth = glass.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y, z - 1), world.getBlockMetadata(x, y, z - 1));
        boolean connectionSouth = glass.shouldConnectToBlock(world, x, y, z, world.getBlock(x, y, z + 1), world.getBlockMetadata(x, y, z + 1));
        boolean connectionWest = glass.shouldConnectToBlock(world, x, y, z, world.getBlock(x - 1, y, z), world.getBlockMetadata(x - 1, y, z));
        boolean connectionEast = glass.shouldConnectToBlock(world, x, y, z, world.getBlock(x + 1, y, z), world.getBlockMetadata(x + 1, y, z));

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
        
        double halfU;
        
        if (!connectionNorth)
        {
            IIcon northWest = glass.getConnectedBlockTexture(world, x, y, z, 2, false, meta);
            IIcon northEast = glass.getConnectedBlockTexture(world, x, y, z, 2, true, meta);
            
            halfU = northWest.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x, y + 1, z, northWest.getMaxU(), northWest.getMinV());
            tessellator.addVertexWithUV(x + HALF, y + 1, z, halfU, northWest.getMinV());
            tessellator.addVertexWithUV(x + HALF, y, z, halfU, northWest.getMaxV());
            tessellator.addVertexWithUV(x, y, z, northWest.getMaxU(), northWest.getMaxV());
            
            halfU = northEast.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x + HALF, y + 1, z, halfU, northEast.getMinV());
            tessellator.addVertexWithUV(x + 1, y + 1, z, northEast.getMinU(), northEast.getMinV());
            tessellator.addVertexWithUV(x + 1, y, z, northEast.getMinU(), northEast.getMaxV());
            tessellator.addVertexWithUV(x + HALF, y, z, halfU, northEast.getMaxV());
        }
        
        if (!connectionSouth)
        {
            IIcon southWest = glass.getConnectedBlockTexture(world, x, y, z, 3, true, meta);
            IIcon southEast = glass.getConnectedBlockTexture(world, x, y, z, 3, false, meta);
            
            halfU = southWest.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x, y, z + 1, southWest.getMinU(), southWest.getMaxV());
            tessellator.addVertexWithUV(x + HALF, y, z + 1, halfU, southWest.getMaxV());
            tessellator.addVertexWithUV(x + HALF, y + 1, z + 1, halfU, southWest.getMinV());
            tessellator.addVertexWithUV(x, y + 1, z + 1, southWest.getMinU(), southWest.getMinV());
            
            halfU = southEast.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x + HALF, y, z + 1, halfU, southEast.getMaxV());
            tessellator.addVertexWithUV(x + 1, y, z + 1, southEast.getMaxU(), southEast.getMaxV());
            tessellator.addVertexWithUV(x + 1, y + 1, z + 1, southEast.getMaxU(), southEast.getMinV());
            tessellator.addVertexWithUV(x + HALF, y + 1, z + 1, halfU, southEast.getMinV());
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
