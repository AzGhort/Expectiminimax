package conquest.bot.playground.expectiminimax;

public class Possibility<S> 
{
	 public double prob; // probability from 0..1
	 public S state;
	 
	 public Possibility(double p, S s)
	 {
		 prob = p;
		 state = s;
	 }
}
