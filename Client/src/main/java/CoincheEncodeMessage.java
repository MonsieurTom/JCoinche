public class CoincheEncodeMessage
{
    public static CoincheEncodeMessage      instance = null;
    private final String[]                  _cardName = {"ace", "seven", "height", "nine", "ten", "jack", "queen", "king"};
    private final coincheProto.CARD_NAME[]  card_names = {coincheProto.CARD_NAME.ACE, coincheProto.CARD_NAME.SEVEN,
                            coincheProto.CARD_NAME.HEIGHT, coincheProto.CARD_NAME.NINE, coincheProto.CARD_NAME.TEN,
                            coincheProto.CARD_NAME.JACK, coincheProto.CARD_NAME.QUEEN, coincheProto.CARD_NAME.KING};
    private final String[]                  _cardColor = {"spade", "hearth", "diamond", "club"};
    private final coincheProto.CARD_COLOR[] card_colors = {coincheProto.CARD_COLOR.SPADE, coincheProto.CARD_COLOR.HEARTH,
                                                        coincheProto.CARD_COLOR.DIAMOND, coincheProto.CARD_COLOR.CLUB};
    private final String[]                  _trumps = {"none", "spade", "hearth", "diamond", "club", "all"};
    private final coincheProto.TRUMP[]      _trumpsCard = {coincheProto.TRUMP.TNONE, coincheProto.TRUMP.TSPADE,
            coincheProto.TRUMP.THEARTH, coincheProto.TRUMP.TDIAMOND, coincheProto.TRUMP.TCLUB, coincheProto.TRUMP.TALL};
    private Runnable                        _runnable = null;
    private String[]                        _wordTab;

    private CoincheEncodeMessage(Runnable runnable)
    {
        _runnable = runnable;
    }

    public static CoincheEncodeMessage  getInstance(Runnable runnable)
    {
        if (instance == null)
            instance = new CoincheEncodeMessage(runnable);
        return (instance);
    }

    public coincheProto.PlayerMsg encode(String message)
    {
        switch (message.toLowerCase()) {
            case "quit":
                _runnable.run();
                break;
            case "help":
                System.out.println("---   Help   ---");
                System.out.println("- differents helps menues exists -");
                System.out.println("- Commands are not case sensitives -");
                System.out.println("help commands");
                System.out.println("help CARD_COLOR");
                System.out.println("help CARD_NAME");
                System.out.println("help TRUMPS");
                break;
            case "help commands":
                System.out.println("---   Available commands   ---");
                System.out.println("PLAYCARD $(CARD_NAME) $(CARD_COLOR)");
                System.out.println("DEAL $(your deal) $(TRUMP)  ----- Your deal can be \"PASS\"");
                break;
            case "help card_color":
                System.out.println("---   Differents card's colors   ---");
                System.out.println("- SPADE");
                System.out.println("- HEARTH");
                System.out.println("- DIAMOND");
                System.out.println("- CLUB");
                break;
            case "help card_name":
                System.out.println("---  Differents card's names   ---");
                System.out.println("- ACE");
                System.out.println("- KING");
                System.out.println("- QUEEN");
                System.out.println("- JACK");
                System.out.println("- TEN");
                System.out.println("- NINE");
                System.out.println("- SEVEN");
                break;
            case "help trumps":
                System.out.println("---   Differents Trumps   ---");
                System.out.println("- SPADE");
                System.out.println("- HEARTH");
                System.out.println("- DIAMOND");
                System.out.println("- CLUB");
                System.out.println("- NONE");
                System.out.println("- ALL");
                break;
            case "hand":
                handCommand();
                break;
            default:
                return (this.getCoincheProto(message));
        }
            return (null);
    }

    private void                        handCommand()
    {
        int                             idx = 0;

        if (Hand.getInstance().getCardName(idx) == null)
            System.out.println("---   You don't own any card   ---");
        else
        {
            System.out.println("---   Card(s) you own   ---");
            while (Hand.getInstance().getCardName(idx) != null)
            {
                System.out.println("- " + Hand.getInstance().getCardName(idx) + " of "
                                            + Hand.getInstance().getCardColor(idx));
                idx++;
            }
        }
    }

    private coincheProto.PlayerMsg      getCoincheProto(String message)
    {
        this._wordTab = message.split(" ");
        switch (this._wordTab[0].toLowerCase()) {
            case "playcard":
                return (computePlayCard());
            case "deal":
                return (computeDeal());
            default:
                System.out.println("Command Doesn't exist, try the 'help' command");
                break;
        }
        return (null);
    }

    private coincheProto.PlayerMsg      computePlayCard()
    {
        int                             i = 0;
        int                             j = 0;

        try {
            coincheProto.PlayerMsg.Builder msg = coincheProto.PlayerMsg.newBuilder();
            coincheProto.PlayCard.Builder body = coincheProto.PlayCard.newBuilder();

            msg.setTypeMessage(coincheProto.PlayerMsg.TypeMessage.PLAYCARD);
            if (_wordTab.length != 3) {
                return (null);
            }
            while (i <= 8)
            {
                if (i == 8)
                {
                    System.out.println("Invalid command. try the \"help\" command.");
                    return (null);
                }
                if (_wordTab[1].equalsIgnoreCase(_cardName[i])) {
                    body.setName(card_names[i]);
                    break;
                }
                i++;
            }
            while (j <= 4)
            {
                if (j == 4)
                {
                    System.out.println("Invalid command. try the \"help\" command.");
                    return (null);
                }
                if (_wordTab[2].equalsIgnoreCase(_cardColor[j]))
                {
                    body.setColor(card_colors[j]);
                    if(!Hand.getInstance().playCard(_cardName[i], _cardColor[j]))
                    {
                        System.out.println("You don't own this card.");
                        System.out.println("Enter \"HAND\" command to see which cards you own");
                        return (null);
                    }
                    return (msg.setPlayCard(body.build()).build());
                }
                j++;
            }
        }
        catch (Exception e) {
            System.out.println("Something went wrong, try again.");
        }
        return (null);
    }

    private coincheProto.PlayerMsg      computeDeal()
    {
        try {
            coincheProto.PlayerMsg.Builder msg = coincheProto.PlayerMsg.newBuilder();
            coincheProto.Deal.Builder body = coincheProto.Deal.newBuilder();

            if (_wordTab.length == 2) {
                if (_wordTab[1].equalsIgnoreCase("pass")) {
                    msg.setTypeMessage(coincheProto.PlayerMsg.TypeMessage.DEAL);
                    body.setTrump(coincheProto.TRUMP.TNONE);
                    body.setDeal(-1);
                    return (msg.setDeal(body.build()).build());
                } else {
                    System.out.println("Invalid command. try the \"help\" command.");
                    return (null);
                }
            } else {
                if (_wordTab.length != 3) {
                    System.out.println("Invalid command. try the \"help\" command.");
                    return (null);
                }
                msg.setTypeMessage(coincheProto.PlayerMsg.TypeMessage.DEAL);
                body.setDeal(Integer.parseInt(_wordTab[1]));
                for (int i = 0; i <= 6; i++) {
                    if (i == 6) {
                        System.out.println("Invalid command. try the \"help\" command.");
                        return (null);
                    }
                    if (_wordTab[2].equalsIgnoreCase(_trumps[i])) {
                        body.setTrump(_trumpsCard[i]);
                        return (msg.setDeal(body.build()).build());
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("Something went wrong, maybe you didn't enter an integer as first parameter.");
            return (null);
        }
        return (null);
    }
}
