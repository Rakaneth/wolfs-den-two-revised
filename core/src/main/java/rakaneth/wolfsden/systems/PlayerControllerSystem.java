package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.CommandTypes;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Player;
import rakaneth.wolfsden.components.Position;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;

public class PlayerControllerSystem extends IteratingSystem
{

	public PlayerControllerSystem()
	{
		super(Family.all(Player.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime)
	{
		Player playerCmd = Mapper.player.get(entity);
		Position pos = Mapper.position.get(entity);
		if (!playerCmd.cmds.empty()) {
			CommandTypes cmd = (CommandTypes)playerCmd.cmds.pop();
			switch (cmd) {
			case MOVE:
				Direction d = (Direction)playerCmd.cmds.pop();
				int newX = pos.current.x + d.deltaX;
				int newY = pos.current.y + d.deltaY;
				Coord newCoord = Coord.get(newX, newY);
				if (pos.map.isPassable(newCoord))
				{
					pos.dirty = true;
					pos.current = newCoord;
					playerCmd.tookTurn = true;
				}
				default: {}
			}
		}
	}
}
