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
        IIcon left, right;
        
        if (!connectionDown)
        {
            left = glass.getConnectedBlockTexture(world, x, y, z, 0, true, meta);
            right = glass.getConnectedBlockTexture(world, x, y, z, 0, false, meta);
            
            halfU = left.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x, y, z, left.getMinU(), left.getMaxV());
            tessellator.addVertexWithUV(x + HALF, y, z, halfU, left.getMaxV());
            tessellator.addVertexWithUV(x + HALF, y, z + 1, halfU, left.getMinV());
            tessellator.addVertexWithUV(x, y, z + 1, left.getMinU(), left.getMinV());
            
            halfU = right.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x + HALF, y, z, halfU, right.getMaxV());
            tessellator.addVertexWithUV(x + 1, y, z, right.getMaxU(), right.getMaxV());
            tessellator.addVertexWithUV(x + 1, y, z + 1, right.getMaxU(), right.getMinV());
            tessellator.addVertexWithUV(x + HALF, y, z + 1, halfU, right.getMinV());
        }
        
        if (!connectionUp)
        {
            left = glass.getConnectedBlockTexture(world, x, y, z, 1, true, meta);
            right = glass.getConnectedBlockTexture(world, x, y, z, 1, false, meta);
            
            halfU = left.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x, y + 1, z + 1, left.getMinU(), left.getMinV());
            tessellator.addVertexWithUV(x + HALF, y + 1, z + 1, halfU, left.getMinV());
            tessellator.addVertexWithUV(x + HALF, y + 1, z, halfU, left.getMaxV());
            tessellator.addVertexWithUV(x, y + 1, z, left.getMinU(), left.getMaxV());
            
            halfU = right.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x + HALF, y + 1, z + 1, halfU, right.getMinV());
            tessellator.addVertexWithUV(x + 1, y + 1, z + 1, right.getMaxU(), right.getMinV());
            tessellator.addVertexWithUV(x + 1, y + 1, z, right.getMaxU(), right.getMaxV());
            tessellator.addVertexWithUV(x + HALF, y + 1, z, halfU, right.getMaxV());
        }
        
        if (!connectionNorth)
        {
            left = glass.getConnectedBlockTexture(world, x, y, z, 2, true, meta);
            right = glass.getConnectedBlockTexture(world, x, y, z, 2, false, meta);
            
            halfU = left.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x + HALF, y + 1, z, halfU, left.getMinV());
            tessellator.addVertexWithUV(x + 1, y + 1, z, left.getMinU(), left.getMinV());
            tessellator.addVertexWithUV(x + 1, y, z, left.getMinU(), left.getMaxV());
            tessellator.addVertexWithUV(x + HALF, y, z, halfU, left.getMaxV());
            
            halfU = right.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x, y + 1, z, right.getMaxU(), right.getMinV());
            tessellator.addVertexWithUV(x + HALF, y + 1, z, halfU, right.getMinV());
            tessellator.addVertexWithUV(x + HALF, y, z, halfU, right.getMaxV());
            tessellator.addVertexWithUV(x, y, z, right.getMaxU(), right.getMaxV());
        }
        
        if (!connectionSouth)
        {
            left = glass.getConnectedBlockTexture(world, x, y, z, 3, true, meta);
            right = glass.getConnectedBlockTexture(world, x, y, z, 3, false, meta);
            
            halfU = left.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x, y, z + 1, left.getMinU(), left.getMaxV());
            tessellator.addVertexWithUV(x + HALF, y, z + 1, halfU, left.getMaxV());
            tessellator.addVertexWithUV(x + HALF, y + 1, z + 1, halfU, left.getMinV());
            tessellator.addVertexWithUV(x, y + 1, z + 1, left.getMinU(), left.getMinV());
            
            halfU = right.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x + HALF, y, z + 1, halfU, right.getMaxV());
            tessellator.addVertexWithUV(x + 1, y, z + 1, right.getMaxU(), right.getMaxV());
            tessellator.addVertexWithUV(x + 1, y + 1, z + 1, right.getMaxU(), right.getMinV());
            tessellator.addVertexWithUV(x + HALF, y + 1, z + 1, halfU, right.getMinV());
        }
        
        if (!connectionWest)
        {
            left = glass.getConnectedBlockTexture(world, x, y, z, 4, true, meta);
            right = glass.getConnectedBlockTexture(world, x, y, z, 4, false, meta);
            
            halfU = left.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x, y + 1, z + HALF, halfU, left.getMinV());
            tessellator.addVertexWithUV(x, y + 1, z, left.getMinU(), left.getMinV());
            tessellator.addVertexWithUV(x, y, z, left.getMinU(), left.getMaxV());
            tessellator.addVertexWithUV(x, y, z + HALF, halfU, left.getMaxV());
            
            halfU = right.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x, y + 1, z + 1, right.getMaxU(), right.getMinV());
            tessellator.addVertexWithUV(x, y + 1, z + HALF, halfU, right.getMinV());
            tessellator.addVertexWithUV(x, y, z + HALF, halfU, right.getMaxV());
            tessellator.addVertexWithUV(x, y, z + 1, right.getMaxU(), right.getMaxV());
        }
        
        if (!connectionEast)
        {
            left = glass.getConnectedBlockTexture(world, x, y, z, 5, true, meta);
            right = glass.getConnectedBlockTexture(world, x, y, z, 5, false, meta);
            
            halfU = left.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x + 1, y, z + 1, left.getMinU(), left.getMaxV());
            tessellator.addVertexWithUV(x + 1, y, z + HALF, halfU, left.getMaxV());
            tessellator.addVertexWithUV(x + 1, y + 1, z + HALF, halfU, left.getMinV());
            tessellator.addVertexWithUV(x + 1, y + 1, z + 1, left.getMinU(), left.getMinV());
            
            halfU = right.getInterpolatedU(8.0D);
            tessellator.addVertexWithUV(x + 1, y, z + HALF, halfU, right.getMaxV());
            tessellator.addVertexWithUV(x + 1, y, z, right.getMaxU(), right.getMaxV());
            tessellator.addVertexWithUV(x + 1, y + 1, z, right.getMaxU(), right.getMinV());
            tessellator.addVertexWithUV(x + 1, y + 1, z + HALF, halfU, right.getMinV());
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
