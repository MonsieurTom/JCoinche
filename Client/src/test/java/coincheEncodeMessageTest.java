import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class coincheEncodeMessageTest
{
    private static CoincheEncodeMessage    encoder;

    @BeforeClass
    public static void  beforeAll()
    {
        encoder = CoincheEncodeMessage.getInstance(null);
    }

    @Test
    public final void    encodeTestWithHand()
    {
        coincheProto.PlayerMsg  msg;

        Hand.getInstance().addCard("ten", "hearth");
        Hand.getInstance().addCard("nine", "hearth");

        msg = encoder.encode("playcard ten hearth");

        assertEquals(msg.getTypeMessage(), coincheProto.PlayerMsg.TypeMessage.PLAYCARD);
        assertEquals(msg.getPlayCard().getColor(), coincheProto.CARD_COLOR.HEARTH);
        assertEquals(msg.getPlayCard().getName(), coincheProto.CARD_NAME.TEN);
    }

    @Test
    public final void     encodeTestDealTest()
    {
        coincheProto.PlayerMsg  msg;

        msg = encoder.encode("deal 10 none");

        assertEquals(coincheProto.PlayerMsg.TypeMessage.DEAL, msg.getTypeMessage());
        assertEquals(10, msg.getDeal().getDeal());
        assertEquals(coincheProto.TRUMP.TNONE,msg.getDeal().getTrump());
    }
}
