package ashjack.simukraftreloaded.common;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class PricesForBlocks implements Serializable
{
    private static final long serialVersionUID = -2617939458756927761L;

    /** base price is how much per block the merchant will pay YOU for it
     *   when he sells you blocks the price will be base + 12% (his/her mark-up)
     */
    private static Float basePricePlanks = 0.0131f;
    private static Float basePriceLogs = 0.0131f * 4;
    private static Float basePriceCobblestone = 0.0032f;
    private static Float basePriceStone = 0.0141f;
    private static Float basePriceGlass = 0.0121f;
    private static Float basePriceWool = 0.0115f;
    private static Float basePriceBrick = 0.0251f;
    private static Float basePriceStonebrick = 0.0261f;
    private static Float basePriceFence = 0.0113f;

    public static Float bankPriceDiamond = 10.23f;
    public static Float bankPriceEmerald = 9.34f;
    public static Float bankPriceRedstone = 3.75f;
    public static Float bankPriceGlowstone = 2.48f;
    public static Float bankPriceGold = 5.12f;

    /** get the buy for price or the sell for price of the specified pack of 64 blocks */
    public static Float getPrice(Block block, boolean isBuying)
    {
        float base = 0f;

        if (block == Blocks.planks)
        {
            base = basePricePlanks;
        }
        else if (block == Blocks.log)
        {
            base = basePriceLogs;
        }
        else if (block == Blocks.cobblestone)
        {
            base = basePriceCobblestone;
        }
        else if (block == Blocks.stone)
        {
            base = basePriceStone;
        }
        else if (block == Blocks.glass)
        {
            base = basePriceGlass;
        }
        else if (block == Blocks.wool)
        {
            base = basePriceWool;
        }
        else if (block == Blocks.brick_block)
        {
            base = basePriceBrick;
        }
        else if (block == Blocks.stonebrick)
        {
            base = basePriceStonebrick;
        }
        else if (block == Blocks.fence)
        {
            base = basePriceFence;
        }

        base = (base * 64); //per pack price

        if (isBuying)
        {
            base += (base * 1.12); //merchant's 12% markup
        }

        return base;
    }
    
    
    /*public static Float getPrice(B blockId, boolean isBuying)
    {
        float base = 0f;

        if (blockId == Blocks.planks)
        {
            base = basePricePlanks;
        }
        else if (blockId == Blocks.log)
        {
            base = basePriceLogs;
        }
        else if (blockId == Blocks.cobblestone)
        {
            base = basePriceCobblestone;
        }
        else if (blockId == Blocks.stone)
        {
            base = basePriceStone;
        }
        else if (blockId == Blocks.glass)
        {
            base = basePriceGlass;
        }
        else if (blockId == Blocks.cloth)
        {
            base = basePriceWool;
        }
        else if (blockId == Blocks.brick)
        {
            base = basePriceBrick;
        }
        else if (blockId == Blocks.stoneBrick)
        {
            base = basePriceStonebrick;
        }
        else if (blockId == Blocks.fence)
        {
            base = basePriceFence;
        }

        base = (base * 64); //per pack price

        if (isBuying)
        {
            base += (base * 1.12); //merchant's 12% markup
        }

        return base;
    }*/

    /** set price PER BLOCK (not 64 blocks) */
    public static void setPrice(Block block, float newPrice)
    {
        if (block == Blocks.planks)
        {
            basePricePlanks = newPrice;
        }
        else if (block == Blocks.log)
        {
            basePriceLogs = newPrice;
        }
        else if (block == Blocks.cobblestone)
        {
            basePriceCobblestone = newPrice;
        }
        else if (block == Blocks.stone)
        {
            basePriceStone = newPrice;
        }
        else if (block == Blocks.glass)
        {
            basePriceGlass = newPrice;
        }
        else if (block == Blocks.wool)
        {
            basePriceWool = newPrice;
        }
        else if (block == Blocks.brick_block)
        {
            basePriceBrick = newPrice;
        }
        else if (block == Blocks.stonebrick)
        {
            basePriceStonebrick = newPrice;
        }
        else if (block == Blocks.fence)
        {
            basePriceFence = newPrice;
        }
    }

    /** adjust the price up or down for sell and buy price for the block passed in,
     *  afterBuying is an adjustment after buying this block
     *  this is also called once a day for each block to fluctuate the prices */
    public static void adjustPrice(Block block, boolean afterBuying)
    {
        /// after selling the price goes down, so each sucessive sell gets less money
        /// after buying the price goes up
        /// make sure logs are 4 x planks
        /// min= 0.012     max=0.990
        Random r = new Random();
        float cprice;

        if (afterBuying)
        {
            cprice = PricesForBlocks.getPrice(block, false) / 64;
            cprice += (r.nextFloat() / 100);

            if (cprice > 0.99)
            {
                cprice = 0.99f;
            }

            PricesForBlocks.setPrice(block, cprice);
        }
        else
        {
            cprice = PricesForBlocks.getPrice(block, false) / 64;
            cprice -= (r.nextFloat() / 100);

            if (cprice < 0.012)
            {
                cprice = 0.012f;
            }

            PricesForBlocks.setPrice(block, cprice);
        }

        if (block == Blocks.planks)
        {
            PricesForBlocks.setPrice(Blocks.log, cprice * 4);
        }

        if (block == Blocks.log)  // Logs
        {
            PricesForBlocks.setPrice(Blocks.planks, cprice / 4);
        }
    }

    public static String formatPrice(float price)
    {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(price);
    }
}
