import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.function.Consumer;

public class CoincheGameTest {
    private static CoincheGame coincheGame;
    private static Player P0;
    private static Player P1;
    private static Player P2;
    private static Player P3;
    private static coincheProto.TRUMP[] _trumps = {coincheProto.TRUMP.TCLUB, coincheProto.TRUMP.TDIAMOND, coincheProto.TRUMP.THEARTH, coincheProto.TRUMP.TSPADE, coincheProto.TRUMP.TALL, coincheProto.TRUMP.TNONE};
    private final coincheProto.CARD_COLOR[] _cardColor = {coincheProto.CARD_COLOR.CLUB, coincheProto.CARD_COLOR.DIAMOND, coincheProto.CARD_COLOR.HEARTH, coincheProto.CARD_COLOR.SPADE};
    private final coincheProto.CARD_NAME[] _cardName = {coincheProto.CARD_NAME.ACE, coincheProto.CARD_NAME.HEIGHT, coincheProto.CARD_NAME.NINE,
                                                            coincheProto.CARD_NAME.TEN, coincheProto.CARD_NAME.JACK, coincheProto.CARD_NAME.QUEEN, coincheProto.CARD_NAME.KING};

    @BeforeClass
    public static void beforeAll() {
        P0 = new Player(0);
        P1 = new Player(1);
        P2 = new Player(2);
        P3 = new Player(3);

        coincheGame = new CoincheGame(P0, P1, P2, P3, new Consumer<MessageRequest>() {
            @Override
            public void accept(MessageRequest messageRequest) {
            }
        });
    }

    @Test
    public final void decodePlayedCardTest()
    {
        coincheProto.PlayCard.Builder body = coincheProto.PlayCard.newBuilder();

        for (coincheProto.CARD_NAME name: _cardName)
        {
            for (coincheProto.CARD_COLOR color: _cardColor)
            {
                body.setColor(color);
                body.setName(name);

                Card card = coincheGame.decodePlayedCard(body.build());
                assertEquals("card are the same", new Card(color, name).color, card.color);
            }
        }
    }

    @Test
    public final void isInGameTest()
    {
        assertTrue("player0 is in game", coincheGame.isInGame(P0.getClientId()));
        assertTrue("player1 is in game", coincheGame.isInGame(P1.getClientId()));
        assertTrue("player2 is in game", coincheGame.isInGame(P2.getClientId()));
        assertTrue("player3 is in game", coincheGame.isInGame(P3.getClientId()));
    }

    @Test
    public final void sameColorTest()
    {
        for (coincheProto.CARD_COLOR color : _cardColor)
        {
            for (coincheProto.TRUMP trump : _trumps)
            {
                if (trump.getNumber() == color.getNumber() + 1)
                    assertTrue("same color = true", coincheGame.sameColor(trump, color));
                else
                    assertFalse("same color = false", coincheGame.sameColor(trump, color));
            }
        }
    }

    @Test
    public final void getCardValueTest()
    {
        coincheProto.PlayCard.Builder body = coincheProto.PlayCard.newBuilder();

        for (coincheProto.CARD_NAME name: _cardName)
        {
            for (coincheProto.CARD_COLOR color: _cardColor)
            {
                body.setColor(color);
                body.setName(name);

                Card card = coincheGame.decodePlayedCard(body.build());

                for (coincheProto.TRUMP trump : _trumps)
                {
                    coincheGame.setTrump(trump);
                    //System.out.println("color: " + card.color + " name: " + card.name + " trump: " + trump);
                    if (trump == coincheProto.TRUMP.TALL)
                    {
                        switch (card.name)
                        {
                            case ACE:
                                assertEquals("ace tall", 7, coincheGame.getCardValue(card));
                                break;
                            case SEVEN:
                                assertEquals("seven tall", 0, coincheGame.getCardValue(card));
                                break;
                            case HEIGHT:
                                assertEquals("height tal", 0, coincheGame.getCardValue(card));
                                break;
                            case NINE:
                                assertEquals("nine tall", 9, coincheGame.getCardValue(card));
                                break;
                            case TEN:
                                assertEquals("ten tall", 5, coincheGame.getCardValue(card));
                                break;
                            case JACK:
                                assertEquals("jack tall", 14, coincheGame.getCardValue(card));
                                break;
                            case QUEEN:
                                assertEquals("queen tall", 2, coincheGame.getCardValue(card));
                                break;
                            case KING:
                                assertEquals("king tall", 3, coincheGame.getCardValue(card));
                                break;
                        }
                    }
                    else if (trump == coincheProto.TRUMP.TNONE)
                    {
                        switch (card.name)
                        {
                            case ACE:
                                assertEquals("ace tnone", 19, coincheGame.getCardValue(card));
                                break;
                            case SEVEN:
                                assertEquals("seven tnone", 0, coincheGame.getCardValue(card));
                                break;
                            case HEIGHT:
                                assertEquals("height tnone", 0, coincheGame.getCardValue(card));
                                break;
                            case NINE:
                                assertEquals("nine tnonr", 0, coincheGame.getCardValue(card));
                                break;
                            case TEN:
                                assertEquals("ten tnonr", 10, coincheGame.getCardValue(card));
                                break;
                            case JACK:
                                assertEquals("jack tnone", 2, coincheGame.getCardValue(card));
                                break;
                            case QUEEN:
                                assertEquals("queen tnone", 3, coincheGame.getCardValue(card));
                                break;
                            case KING:
                                assertEquals("king tnone", 4, coincheGame.getCardValue(card));
                                break;
                        }
                    }
                    else
                    {
                        switch (card.name)
                        {
                            case ACE:
                                assertEquals("ace normal", coincheGame.sameColor(trump, card.color) ? 11 : 11, coincheGame.getCardValue(card));
                                break;
                            case SEVEN:
                                assertEquals("seven normal", coincheGame.sameColor(trump, card.color) ? 0 : 0, coincheGame.getCardValue(card));
                                break;
                            case HEIGHT:
                                assertEquals("height normal", coincheGame.sameColor(trump, card.color) ? 0 : 0, coincheGame.getCardValue(card));
                                break;
                            case NINE:
                                assertEquals("nine normal", coincheGame.sameColor(trump, card.color) ? 14 : 0, coincheGame.getCardValue(card));
                                break;
                            case TEN:
                                assertEquals("ten normal", coincheGame.sameColor(trump, card.color) ? 10 : 10, coincheGame.getCardValue(card));
                                break;
                            case JACK:
                                assertEquals("jack normal", coincheGame.sameColor(trump, card.color) ? 20 : 2, coincheGame.getCardValue(card));
                                break;
                            case QUEEN:
                                assertEquals("queen normal", coincheGame.sameColor(trump, card.color) ? 3 : 3, coincheGame.getCardValue(card));
                                break;
                            case KING:
                                assertEquals("king normal", coincheGame.sameColor(trump, card.color) ? 4 : 4, coincheGame.getCardValue(card));
                                break;
                        }
                    }
                }
            }
        }
    }
}