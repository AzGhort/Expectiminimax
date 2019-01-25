package conquest.bot.playground.expectiminimax;

import java.util.List;

public class Expectiminimax<S, A> implements Strategy<S, A> 
{
	Game<S, A> Game;
	Generator<S, A> Generator;
	Evaluator<S> Evaluator;
	int MaxDepth;
	A bestAction;
	
	public Expectiminimax(Game<S, A> game, Generator<S, A> generator, Evaluator<S> evaluator, int maxDepth) 
	{
		Game = game;
		Generator = generator;
		Evaluator = evaluator;
		MaxDepth = maxDepth;
	}

	private double expectiminimax(S state, int depth, double alpha, double beta)
	{
		if (Game.isDone(state) || depth < 1)
		{
			return Game.outcome(state);
		}
		// adversary
		if (Game.player(state) == 2)
		{
			double value = Double.MAX_VALUE;
			List<A> actions = Generator.actions(state);
			for (A a : actions)
			{
				double newvalue = 0;
				double newbeta = 0;
				List<Possibility<S>> outcomes = Generator.possibleResults(state, a);
				for (Possibility<S> pos : outcomes)
				{
					newvalue += (pos.prob)*expectiminimax(pos.state, depth - 1, alpha, beta);
					newbeta += (pos.prob)*Evaluator.evaluate(pos.state);
				}
				if (newvalue < value)
				{
					value = newvalue;
				}
				//beta = Math.min(beta, newbeta);
				//if (alpha >= beta)
				//{
					//break;
				//}
			}
			return value;
		}
		// our turn
		else //if (Game.player(state) == 1)
		{
			double value = Double.MIN_VALUE;
			List<A> actions = Generator.actions(state);
			for (A a : actions)
			{
				double newvalue = 0;
				double newalpha = 0;
				List<Possibility<S>> outcomes = Generator.possibleResults(state, a);
				for (Possibility<S> pos : outcomes)
				{
					newvalue += (pos.prob)*expectiminimax(pos.state, depth - 1, alpha, beta);
					newalpha += (pos.prob)*Evaluator.evaluate(pos.state);
				}
				if (newvalue >= value)
				{
					value = newvalue;
					//top level - pick the best action
					if (depth == MaxDepth)
					{
						bestAction = a;
					}
				}
				//bestAction = a;
				//alpha = Math.max(alpha, newalpha);
				//if (alpha >= beta) 
				//{ 
					//break; 
				//}
			}
			return value;
		}
	}
	
	@Override
	public A action(S state)
	{
		double exp = expectiminimax(state, MaxDepth, Double.MIN_VALUE, Double.MAX_VALUE);
		return bestAction;
	}     
}
