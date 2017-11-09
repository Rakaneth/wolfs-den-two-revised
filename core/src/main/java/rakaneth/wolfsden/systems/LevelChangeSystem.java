package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.ChangeLevel;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Player;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidmath.Coord;

public class LevelChangeSystem extends IteratingSystem
{
	public LevelChangeSystem()
	{
		super(Family.all(ChangeLevel.class, Position.class)
								.get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime)
	{
		Position pos = Mapper.position.get(entity);
		ChangeLevel lv = Mapper.changeLvl.get(entity);
		WolfMap headedTo = pos.map.getConnection(lv.from);
		Coord toC;
		switch (lv.egress) {
		case DOWN:
			toC = headedTo.stairsDown;
			break;
		case OUT:
			toC = headedTo.stairsOut;
			break;
		case UP:
			toC = headedTo.stairsUp;
			break;
		default:
			toC = headedTo.stairsOut;
		}
		pos.map = headedTo;
		pos.current = toC;
		entity.remove(ChangeLevel.class);
		if (Mapper.isPlayer(entity))
			PlayScreen.instance.changeMap(headedTo);
	}

}
