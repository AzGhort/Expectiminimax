package conquest.bot.playground.expectiminimax;

import conquest.bot.state.Action;
import conquest.bot.state.GameState;
import conquest.game.Phase;

public class WarlightGame implements Game<GameState, Action> 
{
	GameState initial;
		
	public WarlightGame(GameState state)
	{
		initial = state;
	}	
	
	@Override
	public GameState initialState() 
	{
		return initial;
	}

	@Override
	public int player(GameState state) 
	{
		return state.me;
	}	

	@Override
	public boolean isDone(GameState state) 
	{
		return state.isDone();
	}

	@Override
	public double outcome(GameState state) 
	{
		double res = 0.5;
		if (state.isDone())
		{
			if (state.winningPlayer() == 1)
			{
				res = 1.0;
			}
			else 
			{
				res = 0.0;
			}
		}
		return res;
	}

	@Override
	public GameState clone(GameState state) 
	{
		return state.clone();
	}

	@Override
	public void apply(GameState state, Action action) 
	{
		action.apply(state);
	}
}
