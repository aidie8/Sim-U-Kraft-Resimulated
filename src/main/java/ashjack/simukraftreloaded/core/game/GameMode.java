package ashjack.simukraftreloaded.core.game;

public class GameMode 
{
	public static enum GAMEMODES
    {
        DONOTRUN, NORMAL, CREATIVE, HARDCORE;
    }
    public static GAMEMODES gameMode = null;

    public static int getGameModeNumber()
    {
        if (gameMode == GAMEMODES.DONOTRUN)
        {
            return -1;
        }
        else if (gameMode == GAMEMODES.NORMAL)
        {
            return 0;
        }
        else if (gameMode == GAMEMODES.CREATIVE)
        {
            return 1;
        }
        else if (gameMode == GAMEMODES.HARDCORE)
        {
            return 2;
        }

        return 0;
    }
    public static void setGameModeFromNumber(int gm)
    {
        if (gm == -1)
        {
            gameMode = GAMEMODES.DONOTRUN;
        }
        else if (gm == 0)
        {
            gameMode = GAMEMODES.NORMAL;
        }
        else if (gm == 1)
        {
            gameMode = GAMEMODES.CREATIVE;
        }
        else if (gm == 2)
        {
            gameMode = GAMEMODES.HARDCORE;
        }
    }
}
