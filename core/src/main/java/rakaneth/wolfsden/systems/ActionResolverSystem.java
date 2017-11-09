package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.ActionStack;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;

public class ActionResolverSystem extends IteratingSystem
{

	public ActionResolverSystem()
	{
		super(Family.all(ActionStack.class)
								.get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime)
	{
		ActionStack playerCmd = Mapper.actions.get(entity);
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
				}
				break;
			case STAIRS:
				WolfMap.Stairs stair = pos.map.getStair(pos.current);
				switch (stair) {
				case UP:
				case OUT:
				case DOWN:
					PlayScreen.instance.followConnection(pos.current, stair);
					break;
				default:
					PlayScreen.addMessage("No stairs here.");
				}
				break;
			default: {
			}
			}
		}
	}
}
