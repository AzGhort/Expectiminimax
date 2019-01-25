package conquest.bot.playground.expectiminimax;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import conquest.bot.fight.FightSimulation.FightAttackersResults;
import conquest.bot.fight.FightSimulation.FightDefendersResults;
import conquest.bot.state.Action;
import conquest.bot.state.ChooseCommand;
import conquest.bot.state.GameState;
import conquest.bot.state.MoveAction;
import conquest.bot.state.MoveCommand;
import conquest.bot.state.PlaceAction;
import conquest.bot.state.PlaceCommand;
import conquest.bot.state.PlayerState;
import conquest.bot.state.RegionState;
import conquest.game.Phase;
import conquest.game.world.Region;
import conquest.utils.Util;

public class WarlightGenerator implements Generator<GameState, Action> 
{
	FightAttackersResults aRes;
	FightDefendersResults dRes;
	static final Random r = new Random();
	int numTries = 5;
	
	public WarlightGenerator() 
	{
		aRes = FightAttackersResults.loadFromFile(Util.file("../Conquest-Bots/FightSimulation-Attackers-A200-D200.obj"));
		dRes = FightDefendersResults.loadFromFile(Util.file("../Conquest-Bots/FightSimulation-Defenders-A200-D200.obj"));
	}
	
	@Override
	public List<Action> actions(GameState state) 
	{
		Phase ph = state.getPhase();
		if (ph == Phase.ATTACK_TRANSFER)
		{
			return GetAttacks(state);
		}
		else if (ph == Phase.PLACE_ARMIES)
		{
			return GetPlaceArmies(state);
		}
		else
		// ph == Phase.STARTING_REGIONS
		{
			return GetStartingRegions(state);
		}
	}

	private List<Action> GetAttacks(GameState state)
	{
		List<Action> actions = new ArrayList<Action>();
		
		PlayerState me = state.players[state.me];
		List<RegionState> mine = new ArrayList<RegionState>(me.regions.values());
		
		for (int i = 0; i < numTries; i++)
		{
			List<MoveCommand> mc = new ArrayList<MoveCommand>();
			for (RegionState rs : mine)
			{
				int armsToMove = rs.armies - 1;
				for (RegionState neighb : rs.neighbours)
				{
					if (armsToMove < 1) { break; }
					int arm = r.nextInt(armsToMove);
					arm += 1;
					mc.add(new MoveCommand(rs.region, neighb.region, arm));
					armsToMove -= arm;
				}
			}
			MoveAction ma = new MoveAction(mc);
			actions.add(ma);
		}
		return actions;
	}
	
	private List<Action> GetPlaceArmies(GameState state)
	{
		List<Action> actions = new ArrayList<Action>();
		
		PlayerState me = state.players[state.me];
		List<RegionState> mine = new ArrayList<RegionState>(me.regions.values());
		// 10 random place commands
		for (int j = 0; j < numTries; j++)
		{
			List<PlaceCommand> pc = new ArrayList<PlaceCommand>();
			int armiesLeft = me.placeArmies;
			for (RegionState rs : mine)
			{
				if (armiesLeft < 1)
				{
					break;
				}
				int arm = r.nextInt(armiesLeft);
				arm += 1;
				pc.add(new PlaceCommand(rs.region, arm));
				armiesLeft -= arm;
			}	
			PlaceAction pa = new PlaceAction(pc);
			actions.add(pa);
		}
		return actions;
	}		
	
	private List<Action> GetStartingRegions(GameState state)
	{
		List<Action> actions = new ArrayList<Action>();
		for (Region rs : state.getPickableRegions())
		{
			ChooseCommand cc = new ChooseCommand(rs);
			actions.add(cc);
		}
		return actions;
	}
	
	@Override
	public List<Possibility<GameState>> possibleResults(GameState state, Action action) 
	{
		List<Possibility<GameState>> poss = new ArrayList<Possibility<GameState>>();
		Class<?> actionClass = action.getClass();
		String classname = actionClass.getSimpleName();
		if (classname.equals("ChooseCommand"))
		{
			// deterministic action
			GameState clone = state.clone();
			action.apply(clone);
			poss.add(new Possibility<GameState>(1.0, clone));
		}
		else if (classname.equals("PlaceAction"))
		{
			// deterministic action
			GameState clone = state.clone();			
			action.apply(clone);
			poss.add(new Possibility<GameState>(1.0, clone));
		}
		else if (classname.equals("MoveAction"))
		{
			for (int i = 0; i < numTries; i++)
			{
				MoveAction ma = (MoveAction) action;
				double totalProb = 0.0;
				GameState clone = state.clone();
				for (MoveCommand mc : ma.commands)
				{			
					RegionState fr = state.region(mc.from);
					RegionState to = state.region(mc.to);
					// moving to our region
					if (fr.owner == to.owner)
					{		
						action.apply(clone);
						totalProb += 1.0;
					}		
					// attacking!
					else
					{
						double attackChance = aRes.getAttackersWinChance(mc.armies, to.armies);
						double defendersChance = dRes.getDefendersWinChance(mc.armies, to.armies);		
						action.apply(clone);
						if (clone.region(mc.to).owner == fr.owner)
						{
							totalProb += attackChance;
						}
						else
						{
							totalProb += defendersChance;
						}
					}
				}
				poss.add(new Possibility<GameState>(totalProb * 1.0/ ma.commands.size(), clone));
			}			
		}		
		return poss;
	}

}
