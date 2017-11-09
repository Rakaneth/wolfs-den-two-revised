package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.ChangeLevel;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.screens.PlayScreen;

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
		WolfMap.Connection headedTo = pos.map.getConnection(lv.from);
		pos.map = headedTo.getMap();
		pos.current = headedTo.toC;
		entity.remove(ChangeLevel.class);
		if (Mapper.isPlayer(entity))
			PlayScreen.instance.changeMap(pos.map);
	}

}
