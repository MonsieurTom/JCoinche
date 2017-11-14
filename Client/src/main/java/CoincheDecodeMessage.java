import java.util.ArrayList;

public class CoincheDecodeMessage
{
    private final String[]                  _cardName = {"ace", "seven", "height", "nine", "ten", "jack", "queen", "king"};
    private final String[]                  _trumps = {"none", "spade", "hearth", "diamond", "club", "all"};
    private final String[]                  _cardColor = {"spade", "hearth", "diamond", "club"};
    private static CoincheDecodeMessage     instance;

    private CoincheDecodeMessage()
    {}

    public static CoincheDecodeMessage  getInstance()
    {
        if (instance == null)
            instance = new CoincheDecodeMessage();
        return (instance);
    }

    public void     decodeMessage(coincheProto.ServerMsg message, Runnable runnable)
    {
        switch (message.getTypeMessage())
        {
            case DRAWCARD:
                System.out.println("You just draw a card :");
                System.out.println(_cardName[message.getDrawCard().getName().getNumber()] + " of " +
                        _cardColor[message.getDrawCard().getColor().getNumber()]);
                Hand.getInstance().addCard(_cardName[message.getDrawCard().getName().getNumber()],
                        _cardColor[message.getDrawCard().getColor().getNumber()]);
                break;
            case WINNER:
                System.out.println("---   END   ---");
                System.out.println("Team : " + message.getWinner().getDealerTeam() + " has taken the deal.");
                if (message.getWinner().getWinDeal())
                    System.out.println("They did manage to complete the deal.");
                else
                    System.out.println("They didn't manage to complete the deal.");
                System.out.println("Stats : ");
                System.out.println("Team 1 : " + message.getWinner().getTeam1Score() + " pts.");
                System.out.println("Team 2 : " + message.getWinner().getTeam2Score() + " pts.");
                System.exit(0);
                break;
            case DEALWINNER:
                System.out.println("Player : " + message.getDealWinner().getPlayer() + " from team : " + message.getDealWinner().getTeam()
                        + " Won the deal with : " + message.getDealWinner().getDeal() + " and trump : " +
                        _trumps[message.getDealWinner().getTrump().getNumber()] + ".");
                break;
            case STACKWINNER:
                System.out.println("Player : " + message.getStackWinner().getPlayer() + " from team : " + message.getStackWinner().getTeam()
                + " Won the stack.");
                System.out.println("He won " + + message.getStackWinner().getScore() + " points.");
                break;
            case PLAYEDCARD:
                System.out.println("Player : " + message.getPlayedCard().getPlayer() + " from team : " + message.getPlayedCard().getTeam()
                        + " played a " + message.getPlayedCard().getName() + " of " + message.getPlayedCard().getColor());
                break;
            case PLAYERDEALED:
                if (message.getPlayerDealed().getDeal() == -1)
                    System.out.println("Player : " + message.getPlayerDealed().getPlayer() + " from team : " + message.getPlayerDealed().getTeam()
                    + " did pass the deal.");
                else
                    System.out.println("Player : " + message.getPlayerDealed().getPlayer() + " from team : " + message.getPlayerDealed().getTeam()
                    + " has deal " + message.getPlayerDealed().getDeal() + " points on " + message.getPlayerDealed().getTrump());
                break;
            case ACTIONVALIDATION:
                if (message.getActionValidation().getAccepted())
                {
                    Hand.getInstance().deletePlayedCard();
                    System.out.println("Action accepted.");
                }
                else if (!message.getActionValidation().getAccepted())
                    System.out.println("Action refused.");
                break;
            case PLAYERSTATE:
                switch (message.getPlayerState().getState())
                {
                    case PLAYING:
                        System.out.println("You just entered a game's table.");
                        System.out.println("Your ID is : " + message.getPlayerState().getPlayer() + ".");
                        System.out.println("Your team is : " + message.getPlayerState().getTeam() + ".");
                        break;
                    case YOURTURN:
                        System.out.println("It is your time to play.");
                        break;
                    case WAITINGGAME:
                        System.out.println("No game's table are available.");
                        System.out.println("Entering the queue to find other to play with and form a game's table.");
                        break;
                    case LEAVE:
                        System.out.println("Someone has leave the game.");
                        System.out.println("everybody on this game's table is being kick.");
                        System.exit(0);
                        break;
                    case DEAL:
                        System.out.println("It's your time to deal.");
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}
