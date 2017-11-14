import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class HandTest
{
    private static Hand    _hand;

    @BeforeClass
    public static void beforeAll()
    {
        _hand = Hand.getInstance();
    }

    @Test
    public final void playCardTest()
    {
        assertFalse(_hand.playCard("ten", "hearth"));
        _hand.addCard("ten", "hearth");
        assertTrue(_hand.playCard("ten", "hearth"));
    }

    @Test
    public final void deletePlayedCardTest()
    {
        _hand.addCard("ten", "hearth");
        assertTrue(_hand.playCard("ten", "hearth"));
        assertTrue(_hand.deletePlayedCard());
    }
}
