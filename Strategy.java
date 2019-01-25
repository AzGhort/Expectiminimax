package conquest.bot.playground.expectiminimax;

public interface Strategy<S, A>
{
	  A action(S state);
}
