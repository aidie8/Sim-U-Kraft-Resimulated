package ashjack.simukraftreloaded.client.Gui.folk;

import java.util.ArrayList;
import java.util.logging.Level;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import ashjack.simukraftreloaded.common.PricesForBlocks;
import ashjack.simukraftreloaded.common.jobs.Job;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class GuiMerchant extends GuiScreen
{
    private int currentPage = 0;
    private static ArrayList<Integer> quantities = new ArrayList<Integer>(); //holds the buy quantities
    private static ArrayList<Integer> sellLimits = new ArrayList<Integer>(); //sell limits based on player's inventory
    private Float totalCost = 0f;
    private int mouseCount = 0;

    public GuiMerchant()
    {
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void updateScreen()
    {
        //theGuiTextField1.updateCursorCounter();
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        quantities.clear();
        sellLimits.clear();

        for (int i = 0; i < 9; i++)
        {
            quantities.add(0);
            sellLimits.add(0);
        }

        showPage();
    }

    @Override
    public void drawScreen(int i, int j, float f)
    {
        if (mouseCount < 10)
        {
            mouseCount++;
            Mouse.setGrabbed(false);
        }

        drawDefaultBackground();

        if (currentPage == 0)        // MAIN
        {
            drawCenteredString(fontRendererObj, "Hello, how can I help you today?", width / 2, 5, 0xffffff);
        }
        else if (currentPage == 1)    // BUY
        {
            drawCenteredString(fontRendererObj, "I've got some bargains for you...", width / 2, 5, 0xffffff);
            drawString(fontRendererObj, "Block pack", 2, 25, 0xffffa0);
            drawString(fontRendererObj, "Price per pack", 100, 25, 0xffffa0);
            drawString(fontRendererObj, "Quantity (packs of 64)", 200, 25, 0xffffa0);
            drawString(fontRendererObj, "Sub-total", 350, 25, 0xffffa0);
            String blockName = "";
            String price = "";
            Float fprice = 0f;
            String subtotal = "";
            float grandTotal = 0f;

            for (int b = 0; b < 9; b++)
            {
                if (b == 0)
                {
                    blockName = "Planks";
                    fprice = PricesForBlocks.getPrice(Blocks.planks, true);
                }
                else if (b == 1)
                {
                    blockName = "Logs";
                    fprice = PricesForBlocks.getPrice(Blocks.log, true);
                }
                else if (b == 2)
                {
                    blockName = "Cobblestone";
                    fprice = PricesForBlocks.getPrice(Blocks.cobblestone, true);
                }
                else if (b == 3)
                {
                    blockName = "Stone";
                    fprice = PricesForBlocks.getPrice(Blocks.stone, true);
                }
                else if (b == 4)
                {
                    blockName = "Glass";
                    fprice = PricesForBlocks.getPrice(Blocks.glass, true);
                }
                else if (b == 5)
                {
                    blockName = "Wool";
                    fprice = PricesForBlocks.getPrice(Blocks.wool, true);
                }
                else if (b == 6)
                {
                    blockName = "Bricks";
                    fprice = PricesForBlocks.getPrice(Blocks.brick_block, true);
                }
                else if (b == 7)
                {
                    blockName = "Stone Bricks";
                    fprice = PricesForBlocks.getPrice(Blocks.stonebrick, true);
                }
                else if (b == 8)
                {
                    blockName = "Fence";
                    fprice = PricesForBlocks.getPrice(Blocks.fence, true);
                }

                price = PricesForBlocks.formatPrice(fprice);
                subtotal = PricesForBlocks.formatPrice(quantities.get(b) * fprice);
                grandTotal +=  quantities.get(b) * fprice;
                drawString(fontRendererObj, blockName, 2, 40 + (b * 20), 0xffffff);
                drawString(fontRendererObj, price, 100, 40 + (b * 20), 0xffffff);
                drawString(fontRendererObj, quantities.get(b) + "", 200, 40 + (b * 20), 0xffffff);
                drawString(fontRendererObj, subtotal, 350, 40 + (b * 20), 0xffffff);
            }

            drawString(fontRendererObj, "Total: " + PricesForBlocks.formatPrice(grandTotal), 2, height - 15, 0xf0ffff);
            totalCost = grandTotal;
        }
        else if (currentPage == 2)    // SELL
        {
            ///REMOVED THIS - now uses the chest
        }

        super.drawScreen(i, j, f);
    }

    /** this is ran once on each page change */
    private void showPage()
    {
        buttonList.clear();
        buttonList.add(new GuiButton(0, 2, 2, 50, 20, "Goodbye!"));

        if (currentPage == 0)            // MAIN
        {
            buttonList.add(new GuiButton(1, width / 2 - 100, 70, "I want to buy building materials"));
            buttonList.add(new GuiButton(2, width / 2 - 100, 90, "I want to sell building materials"));
        }
        else if (currentPage == 1)       // BUY
        {
            for (int b = 0; b < 9; b++)
            {
                buttonList.add(new GuiButton((100 + b), 250, 35 + (b * 20), 20, 20, "<"));
                buttonList.add(new GuiButton((200 + b), 270, 35 + (b * 20), 20, 20, ">"));
            }

            buttonList.add(new GuiButton(2, width - 100, height - 20, 100, 20, "* Buy *"));
        }
        else if (currentPage == 2)       // SELL
        {
            for (int b = 0; b < 9; b++)
            {
                buttonList.add(new GuiButton((100 + b), 250, 35 + (b * 20), 20, 20, "<"));
                buttonList.add(new GuiButton((200 + b), 270, 35 + (b * 20), 20, 20, ">"));
            }

            buttonList.add(new GuiButton(2, width - 100, height - 20, 100, 20, "* Sell *"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
        if (!guibutton.enabled)
        {
            return;
        }

        if (guibutton.id == 0) ///cancel button
        {
            mc.currentScreen = null;
            mc.setIngameFocus();
        }

        if (currentPage == 0)      //   MAIN
        {
            if (guibutton.id == 1)
            {
                currentPage = 1;
            }
            else if (guibutton.id == 2)
            {
                this.sellStuff();
            }

            showPage();
        }
        else if (currentPage == 1)        //BUY page
        {
            if (guibutton.id == 1) //this button was removed
            {
            }
            else if (guibutton.id >= 100 && guibutton.id < 200)     //less quantity
            {
                int q = quantities.get(guibutton.id - 100);

                if (q > 0)
                {
                    q--;
                    quantities.set(guibutton.id - 100, q);
                }
            }
            else if (guibutton.id >= 200)                                       //more quantity
            {
                int q = quantities.get(guibutton.id - 200);
                q++;
                quantities.set(guibutton.id - 200, q);
            }
            else if (guibutton.id == 2)    /// BUY!
            {
                if (SimukraftReloaded.states.credits < totalCost)
                {
                    SimukraftReloaded.sendChat("Merchant: 'Sorry, your card has been declined, you could try buying less.'");
                    mc.currentScreen = null;
                    mc.setIngameFocus();
                }
                else
                {
                    buyStuff();
                }
            }
        }
        else if (currentPage == 2)        //SELL page
        {
            if (guibutton.id >= 100 && guibutton.id < 200)   //less quantity
            {
                int q = quantities.get(guibutton.id - 100);

                if (q > 0)
                {
                    q--;
                    quantities.set(guibutton.id - 100, q);
                }
            }
            else if (guibutton.id >= 200)                   //more quantity
            {
                int q = quantities.get(guibutton.id - 200);

                if (q < sellLimits.get(guibutton.id - 200))    //stop the player selling more than they have
                {
                    q++;
                    quantities.set(guibutton.id - 200, q);
                }
            }
            else if (guibutton.id == 2)    /// Sell!
            {
                sellStuff();
            }
        }
    }

    /** buys the stuff currently shown on the buy page */
    private void buyStuff()
    {
    	SimukraftReloaded.log.info("Preparing to buy stuff");
        ItemStack stack = null;
        int quant = 0;
        Block block = null;
        boolean ok = false;
        Float stackPrice = 0f;
        ArrayList<IInventory> chests = Job.inventoriesFindClosest(
                                           new V3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.dimension), 5);

        if (chests == null || chests.size() == 0)
        {
            SimukraftReloaded.sendChat("Merchant: Please place a chest down here, and I will place your items in there.");
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }

        for (int i = 0; i < 9; i++)
        {
            quant = quantities.get(i);
            SimukraftReloaded.log.info(String.valueOf(quant));

            if (quant > 0)
            {
                if (i == 0)
                {
                    block = Blocks.planks;
                }
                else if (i == 1)
                {
                    block = Blocks.log;
                }
                else if (i == 2)
                {
                    block = Blocks.cobblestone;
                }
                else if (i == 3)
                {
                    block = Blocks.stone;
                }
                else if (i == 4)
                {
                    block = Blocks.glass;
                }
                else if (i == 5)
                {
                    block = Blocks.wool;
                }
                else if (i == 6)
                {
                    block = Blocks.brick_block;
                }
                else if (i == 7)
                {
                    block = Blocks.stonebrick;
                }
                else if (i == 8)
                {
                    block = Blocks.fence;
                }

                for (int c = 1; c <= quant; c++)
                {
                    stack = new ItemStack(block, 64);
                    this.placeIntoChest(chests.get(0), stack, stack.getItemDamage(), 64);
                    stackPrice = PricesForBlocks.getPrice(block, true);


                    SimukraftReloaded.states.credits -= stackPrice; //64 * baseprice + 25% markup

                }

                PricesForBlocks.adjustPrice(block, true);
            }
        }

        mc.currentScreen = null;
        mc.setIngameFocus();
        mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "ashjacksimukraftreloaded:cash", 1f, 1f, false);
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(3000);
                }
                catch (Exception e) {}

                mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "ashjacksimukraftreloaded:merchm", 1f, 1f, false);
            }
        });
        t.start();
    }

    /** sell selected player inventory to merchant */
    private void sellStuff()
    {
        ItemStack stack = null;
        int quant = 0;
        Block block = null;
        boolean ok = false;
        Float stackPrice = 0f;
        int stackCount = 0;
        ArrayList<IInventory> chests = Job.inventoriesFindClosest(new V3(
                                           mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.dimension), 5);

        if (chests == null | chests.size() == 0)
        {
            SimukraftReloaded.sendChat("Merchant: Please place a chest down here, and place stacks of 64 blocks in there.");
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }

        float total = 0f;

        for (int g = 0; g < chests.get(0). getSizeInventory(); g++)
        {
            ItemStack is = chests.get(0).getStackInSlot(g);

            if (is != null)
            {
                if (is.stackSize == 64)
                {
                    stackPrice = PricesForBlocks.getPrice(Block.getBlockFromItem(is.getItem()), false) ;

                    if (stackPrice > 0)
                    {
                    	SimukraftReloaded.states.credits += stackPrice; //64 * baseprice
                        PricesForBlocks.adjustPrice(block, false);
                        total += stackPrice;
                        chests.get(0).setInventorySlotContents(g, null);
                    }
                }
            }
        }

        if (total == 0f)
        {
            SimukraftReloaded.sendChat("Merchant: There were no valid stacks I want to buy from you in the chest?!");
        }
        else
        {
            mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "ashjacksimukraftreloaded:cash", 1f, 1f, false);
            SimukraftReloaded.sendChat("Sold all valid stacks for a total of " + SimukraftReloaded.displayMoney(total));
        }

        mc.currentScreen = null;
        mc.setIngameFocus();
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        if (i == 1)    //escape and dont save
        {
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
    }

    @Override
    protected void mouseClicked(int i, int j, int k)
    {
        //theGuiTextField1.mouseClicked(i, j, k);
        super.mouseClicked(i, j, k);
    }

    public boolean placeIntoChest(IInventory chest, ItemStack stack, int idmeta, int quantity)
    {
        Minecraft mc = Minecraft.getMinecraft();
        Boolean placedOK = false;

        if (stack == null)
        {
            return true;
        }

        for (int q = 1; q <= quantity; q++)
        {
            for (int g = 0; g < chest.getSizeInventory(); g++)
            {
                ItemStack is = chest.getStackInSlot(g);

                if (is == null)
                {
                    is = new ItemStack(stack.getItem(), 1, idmeta);
                    chest.setInventorySlotContents(g, is);
                    placedOK = true;
                    break; //breaks back into quantity loop
                }
                else if (is.getItem() == stack.getItem() && is.getItemDamage() == idmeta && is.stackSize < 64)
                {
                    is.stackSize++;
                    chest.setInventorySlotContents(g, is);
                    placedOK = true;
                    break;
                }
            }
        }

        return placedOK;
    }
}
