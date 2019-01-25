package conquest.bot.playground.expectiminimax;

public interface Game<S, A> 
{
	  S initialState();
	  S clone(S state);
	  int player(S state); // which player moves next: 1 (maximizing) or 2 (minimizing)
	  void apply(S state, A action);  
	  boolean isDone(S state); // true if game has finished
	  double outcome(S state); // 1.0 = player 1 wins, 0.5 = draw, 0.0 = player 2 wins
}
