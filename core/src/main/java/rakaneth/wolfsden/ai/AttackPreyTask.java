package rakaneth.wolfsden.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class AttackPreyTask extends LeafTask<Entity>
{

  @Override
  public Status execute()
  {
    // TODO Auto-generated method stub
    return Status.FAILED;
  }

  @Override
  protected Task<Entity> copyTo(Task<Entity> task)
  {
    return task;
  }

}
