package project.clientside;

import java.text.NumberFormat;

public abstract class BattlePrinter {

    static void battlePrint(ServerConnection serverConnection){
        int diamonds,assistSpeed,level,stage;
        long damage,coins,clickPreview,clickPrice,assistDamage,assistPreview,assistPrice,maxHP,currentHP,loot;
        boolean hasAssist;
        damage = serverConnection.getNextLong();
        assistDamage = serverConnection.getNextLong();
        assistSpeed = serverConnection.getNextInt();
        clickPreview = serverConnection.getNextLong();
        assistPreview = serverConnection.getNextLong();
        String monsterName = serverConnection.getNextString();
        maxHP = serverConnection.getNextLong();
        currentHP = serverConnection.getNextLong();
        loot = serverConnection.getNextLong();
        coins = serverConnection.getNextLong();
        diamonds = serverConnection.getNextInt();
        hasAssist = serverConnection.getNextAction() == FinalActions.GOT_ASSISTANT;
        clickPrice = serverConnection.getNextLong();
        assistPrice = serverConnection.getNextLong();
        level = serverConnection.getNextInt();
        stage = serverConnection.getNextInt();
        //reads all battle information from server in order to print to the user
        heroPrint(diamonds,coins,hasAssist,assistSpeed,damage,clickPreview,clickPrice,assistDamage,assistPreview,assistPrice);
        monsterPrint(stage,level,loot,monsterName,currentHP,maxHP);
        menuPrint(clickPrice,coins,hasAssist,diamonds,assistPrice);
    }

    private static void heroPrint(int diamonds,long coins,boolean hasAssist,int assistSpeed,long damage,long clickPreview,long clickPrice,long assistDamage,long assistPreview,long assistPrice){
        NumberFormat numForm = NumberFormat.getInstance();
        System.out.println("Diamonds: " + diamonds + "   Coins: " + numForm.format(coins) + (hasAssist ? "     Assist Damage/sec: " + numForm.format((long)(assistDamage*(double)(2000-assistSpeed)/1000)) + "  Attack Speed: " + (double)(assistSpeed/1000) : ""));
        System.out.println("Click damage: " + numForm.format(damage) + "      Upgrade: +" + numForm.format(clickPreview) + "     Cost: " + numForm.format(clickPrice));
        System.out.println(hasAssist ? "Assist damage: " + numForm.format(assistDamage) + "    Upgrade: +" + numForm.format(assistPreview) + "     Cost: " + numForm.format(assistPrice) : "");
    }

    private static void monsterPrint(int stage,int level,long loot,String name,long currentHP,long maxHP){
        NumberFormat numForm = NumberFormat.getInstance();
        System.out.println(stage == 5 ? "       B O S S   F I G H T !!     +1 Diamonds" : "");
        System.out.println("Level: " + level +"     Monster: " + stage + "/5" + "       Loot: " + numForm.format(loot) + " Coins.");
        System.out.println("Name: " + name + "          HP: " + numForm.format(currentHP) + "/" + numForm.format(maxHP));
    }

    private static void menuPrint(long clickPrice,long coins,boolean hasAssist,int diamonds,long assistPrice){
        NumberFormat numForm = NumberFormat.getInstance();
        System.out.println("1. Click upgrade.    (Cost: "+ numForm.format(coins) +  " / " + numForm.format(clickPrice) + ")");
        if (hasAssist) {
            System.out.println("2. Assist upgrade.   (Cost: " + numForm.format(coins) +  " / " + numForm.format(assistPrice) + ")");
            System.out.println("3. Assistant attack speed +110% (" + diamonds +"/4 Diamonds)");
        }
        else
            System.out.println("2. Buy an assistant : (" + diamonds + "/2 Diamonds)\n");
        System.out.println("Press ENTER to attack! (or anything else)");
        System.out.println("9. Main menu");
    }

    static void printError(ServerConnection serverConnection){      //prints error from server
        long coins,clickPrice,assistPrice;
        int diamonds;
        boolean hasAssist;
        NumberFormat numForm = NumberFormat.getInstance();
        String message = serverConnection.getNextString();
        coins = serverConnection.getNextLong();
        diamonds = serverConnection.getNextInt();
        hasAssist = serverConnection.getNextAction() == FinalActions.GOT_ASSISTANT;
        clickPrice = serverConnection.getNextLong();
        assistPrice = serverConnection.getNextLong();
        System.out.println(message + "\nDiamonds: " + diamonds + "      Coins: " + numForm.format(coins));
        menuPrint(clickPrice,coins,hasAssist,diamonds,assistPrice);
    }

    public static void mainMenuPrint(){
        System.out.println("1. Resume game.");
        System.out.println("2. Toggle Assistant (on/off)");
        System.out.println("7. Start a new game.");
        System.out.println("9. Save and Exit.");
        System.out.println("0. Exit (without saving)");
    }
    public static void newGame(){
        System.out.println("Are you sure you want to start a new game?");
        System.out.println("1. No.");
        System.out.println("9. Yes.");
    }
}
