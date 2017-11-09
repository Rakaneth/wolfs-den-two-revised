package rakaneth.wolfsden.systems;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.ActionStack;
import rakaneth.wolfsden.components.ChangeLevel;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.SecondaryStats;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;

public class ActionResolverSystem extends IteratingSystem
{
	private static final Logger logger = Logger.getLogger(ActionResolverSystem.class.getName());
	private boolean paused;

	public ActionResolverSystem()
	{
		super(Family.all(ActionStack.class, SecondaryStats.class)
								.get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime)
	{
		ActionStack playerCmd = Mapper.actions.get(entity);
		SecondaryStats sStats = Mapper.secondaries.get(entity);
		if (!paused) 
			playerCmd.delay -= 1;
		
		if (playerCmd.delay <= 0)
		{
			if (Mapper.isPlayer(entity))
				paused = true;
			Position pos = Mapper.position.get(entity);
			if (!playerCmd.cmds.empty())
			{
				CommandTypes cmd = (CommandTypes) playerCmd.cmds.pop();
				switch (cmd) {
				case MOVE:
					Direction d = (Direction) playerCmd.cmds.pop();
					int newX = pos.current.x + d.deltaX;
					int newY = pos.current.y + d.deltaY;
					Coord newCoord = Coord.get(newX, newY);
					if (pos.map.isPassable(newCoord))
					{
						pos.dirty = true;
						pos.current = newCoord;
						playerCmd.tookTurn = true;
						playerCmd.delay = sStats.moveDelay;
					}
					break;
				case STAIRS:
					WolfMap.Stairs stair = pos.map.getStair(pos.current);
					switch (stair) {
					case UP:
					case OUT:
					case DOWN:
						entity.add(new ChangeLevel(pos.current, stair));
						break;
					default:
						PlayScreen.addMessage("No stairs here.");
					}
					break;
				default:
				}
				paused = false;
			}
			else if (!paused)
			{
				logger.log(Level.WARNING, "{0} took no action due to empty stack", entity.getComponent(Identity.class).id);
			}
		}
	}
}
