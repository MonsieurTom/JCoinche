import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PlayerTest
{
    private static Player _player;

    @BeforeClass
    public static void beforeAll()
    {
        _player = new Player(0);
    }

    @Test
    public final void haveCardTest()
    {
        Card card = new Card(coincheProto.CARD_COLOR.HEARTH, coincheProto.CARD_NAME.TEN);
        Card card2 = new Card(coincheProto.CARD_COLOR.CLUB, coincheProto.CARD_NAME.TEN);

        _player.drawCard(card);
        assertTrue("should find card", _player.haveCard(card));
        assertFalse("should not find card", _player.haveCard(card2));
    }

    @Test
    public final void deleteCardTest()
    {
        Card card = new Card(coincheProto.CARD_COLOR.HEARTH, coincheProto.CARD_NAME.TEN);

        _player.drawCard(card);
        if (_player.haveCard(card))
        {
            _player.removeCard(card);
            assertFalse("should be false", _player.haveCard(card));
        }
        else
            fail("problem with haveCard function.");
    }
}
