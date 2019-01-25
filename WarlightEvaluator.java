package conquest.bot.playground.expectiminimax;

import conquest.bot.state.ContinentState;
import conquest.bot.state.GameState;
import conquest.bot.state.RegionState;

public class WarlightEvaluator implements Evaluator<GameState>
{
	int player;
	
	public WarlightEvaluator(int pl)
	{
		player = pl;
	}
	
	@Override
	public double evaluate(GameState state) 
	{
		double buffer = 0.0;
		
		for (RegionState reg : state.regions)
		{
			if (reg == null) continue;
			if (reg.owned(player))
			{
				buffer += reg.armies;
				buffer += 1;
			}
		}
		
		for (ContinentState con : state.continents)
		{
			if (con == null) continue;
			if (con.ownedBy(player))
			{
				buffer += 25;
			}			
		}		
		return buffer;
	}

}
