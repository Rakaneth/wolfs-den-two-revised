package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.SecondaryStats;
import rakaneth.wolfsden.components.Stats;

public class CalcSecondariesSystem extends IteratingSystem
{

	public CalcSecondariesSystem()
	{
		super(Family.all(Stats.class, SecondaryStats.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime)
	{
		//TODO: adjust when equipment is in
		Stats stats = Mapper.stats.get(entity);
		SecondaryStats secStats = Mapper.secondaries.get(entity);
		
		secStats.moveDelay = Math.max(1, 11 - stats.spd);
		secStats.atkDelay = Math.max(1, 11 - stats.skl);
		secStats.atk.set(stats.skl, stats.skl);
		secStats.def = stats.spd;
		secStats.dmg.set(stats.str, stats.str);
	}

}
