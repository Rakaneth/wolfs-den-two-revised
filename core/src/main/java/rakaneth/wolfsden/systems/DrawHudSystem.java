package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.EntitySystem;

import rakaneth.wolfsden.GameInfo;
import rakaneth.wolfsden.screens.PlayScreen;

public class DrawHudSystem extends EntitySystem
{
  public DrawHudSystem()
  {
  }

  @Override
  public boolean checkProcessing()
  {
    return GameInfo.hudDirty;
  }

  @Override
  public void update(float dt)
  {
    PlayScreen.instance.clearHUD();
    PlayScreen.instance.drawHUD();
    GameInfo.hudDirty = false;
  }
}
