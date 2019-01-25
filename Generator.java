package conquest.bot.playground.expectiminimax;

import java.util.List;

public interface Generator<S, A> 
{
	List<A> actions(S state);  // actions to try in this state
	List<Possibility<S>> possibleResults(S state, A action); // some possible results of an action
}
