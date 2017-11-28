package rakaneth.wolfsden.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import rakaneth.wolfsden.WolfMap;
import rakaneth.wolfsden.components.Mapper;
import squidpony.squidai.DijkstraMap;

public class MoveTowardsPreyTask extends LeafTask<Entity>
{
	private DijkstraMap dMap;
	
	@Override
	public Status execute()
	{
		Entity subject = getObject();
		
		if (status == Status.FRESH)
			dMap = new DijkstraMap(Mapper.position.get(subject).map.baseMap);
		//TODO: finish execute for moveTowardsPrey
		
		return Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task)
	{
		return task;
	}
	
	public void changeMap(WolfMap map)
	{
		dMap.initialize(map.baseMap);
	}

}
