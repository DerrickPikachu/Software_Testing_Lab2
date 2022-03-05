import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StrangeGameTest {
    static class PrisonStub extends Prison {
        @Override
        public void imprisonment(Player player) throws InterruptedException {}
    }

    @Test
    public void enterGameInWrongTime() {
        /*
        Test case a:
        If a notorious player enter the game on 0:00 - 11:59,
        verify that prison doesn’t do any imprisonment.
         */
        StrangeGame game = new StrangeGame();
        game.hour = mock(Hour.class);
        when(game.hour.getHour()).thenReturn(10);
        Prison prisonSpy = spy(game.prison);
        try {
            Player testPlayer = new Player();
            String result = game.enterGame(testPlayer);
            assertEquals("The game is not yet open!", result);
            verify(prisonSpy, never()).imprisonment(testPlayer);
        } catch(Exception e) {
            System.out.println(e.toString());
            fail();
        }
    }

    @Test
    public void enterGameInRightTime() {
        /*
        Test case b:
        If a notorious player enter the game on 12:00 - 23:59,
        assert the output correct.
         */
        StrangeGame game = new StrangeGame();
        game.hour = mock(Hour.class);
        when(game.hour.getHour()).thenReturn(14);
        Player player = new Player();
        game.prison = new PrisonStub();
        try {
            String result = game.enterGame(player);
            assertEquals(
                    "After a long period of punishment, the player can leave! :)",
                    result);
        } catch(Exception e) {
            System.out.println(e.toString());
            fail();
        }
    }

    @Test
    public void prisonerLogTest() {
        /*
        Test case c:
        Suppose 3 players go to the prison.
        Verify prisonerLog in prison will record prisoner’s playerid with spy method.
        Don’t stub getLog function.
         */
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new Player("8787", -1));
        players.add(new Player("8788", -1));
        players.add(new Player("8789", -1));
        ArrayList<String> answer = new ArrayList<String>();
        StrangeGame game = new StrangeGame();
        game.hour = mock(Hour.class);
        when(game.hour.getHour()).thenReturn(14);
        try {
            PrisonStub prisonSpy = spy(new PrisonStub());
            game.prison = prisonSpy;
            for (Player player : players) {
                answer.add(player.getPlayerId());
                game.enterGame(player);
            }
            for (Player player : players) {
                verify(prisonSpy).crime(player);
            }
            assertEquals(answer, game.prison.getLog());
        } catch(Exception e) {
            System.out.println(e.toString());
            fail();
        }
    }

    @Test
    public void getScoreTest() {
        /*
        Test case d:
        Use stub method to test getScore function (PlayerID = your StudentID)
        to avoid connection to outer database.
         */
        class DbStub implements GAMEDb {
            @Override
            public int getScore(String playrid) {
                return 1000000;
            }
        }

        DbStub dbStub = new DbStub();
        StrangeGame game = new StrangeGame();
        game.db = dbStub;
        assertEquals(1000000, game.getScore("310553034"));
    }

    @Test
    public void donateTest() {
        /*
        Test case e:
        Implement paypalService interface as a fake object to test donate function.
         */
        class SucceedPaypal implements paypalService {
            @Override
            public String doDonate() {
                System.out.println("Thanks for your donation!");
                return "Success";
            }
        }

        class FailedPaypal implements paypalService {
            @Override
            public String doDonate() {
                System.out.println("Sorry, The amount of your donation is not enough ><");
                return "Fail";
            }
        }

        SucceedPaypal succeedPaypal = new SucceedPaypal();
        FailedPaypal failedPaypal = new FailedPaypal();
        StrangeGame game = new StrangeGame();
        String succeedResult = game.donate(succeedPaypal);
        String failedResult = game.donate(failedPaypal);
        assertEquals("Thank you", succeedResult);
        assertEquals("Some errors occurred", failedResult);
    }
}