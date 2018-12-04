package project.serverside.the_game;

import project.serverside.FinalActions;
import project.serverside.ClientConnection;

abstract class BattleSender {

    static void sendBattle(boolean print, ClientConnection clientConnection, GameInitializer gm){
        if (print)
            clientConnection.sendBytes(FinalActions.BATTLE_PRINT);
        gm.hero.sendInfo(clientConnection, gm.heroCount, gm.assistCount,print);
        gm.monster.sendInfo(clientConnection,print);
        sendShop(print,clientConnection,gm);
        clientConnection.sendInt(gm.level);
        clientConnection.sendInt(gm.stage);
        if (!print)
            sendCount(clientConnection,gm);
    }

    private static void sendShop(boolean print,ClientConnection clientConnection,GameInitializer gm) {
        gm.hero.sendInventory(clientConnection,print);
        clientConnection.sendLong(gm.clickPrice);
        clientConnection.sendLong(gm.assistPrice);
    }

    private static void sendCount(ClientConnection clientConnection, GameInitializer gm) {
        clientConnection.sendLong(gm.incrementalHP);
        clientConnection.sendLong(gm.incrementalLoot);
        clientConnection.sendInt(gm.heroCount);
        clientConnection.sendInt(gm.assistCount);
    }

    static void printError(boolean diamonds, ClientConnection clientConnection, GameInitializer gm){
        clientConnection.sendBytes(FinalActions.ERROR_MESSAGE);
        clientConnection.sendBytes("Not enough " + (diamonds ? "Diamonds" : "Coins") +" to do that!");
        sendShop(true,clientConnection,gm);
    }
}
