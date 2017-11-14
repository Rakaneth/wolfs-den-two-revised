package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class Mapper
{
  public static final ComponentMapper<Drawing>        drawing     = ComponentMapper.getFor(Drawing.class);
  public static final ComponentMapper<Identity>       identity    = ComponentMapper.getFor(Identity.class);
  public static final ComponentMapper<Position>       position    = ComponentMapper.getFor(Position.class);
  public static final ComponentMapper<ActionStack>    actions     = ComponentMapper.getFor(ActionStack.class);
  public static final ComponentMapper<Stats>          stats       = ComponentMapper.getFor(Stats.class);
  public static final ComponentMapper<ChangeLevel>    changeLvl   = ComponentMapper.getFor(ChangeLevel.class);
  public static final ComponentMapper<SecondaryStats> secondaries = ComponentMapper.getFor(SecondaryStats.class);
  public static final ComponentMapper<Armor>          armors      = ComponentMapper.getFor(Armor.class);
  public static final ComponentMapper<Mainhand>       mainhands   = ComponentMapper.getFor(Mainhand.class);
  public static final ComponentMapper<Offhand>        offhands    = ComponentMapper.getFor(Offhand.class);
  public static final ComponentMapper<Trinket>        trinkets    = ComponentMapper.getFor(Trinket.class);
  public static final ComponentMapper<Vitals>         vitals      = ComponentMapper.getFor(Vitals.class);

  public static final boolean isPlayer(Entity entity)
  {
    ComponentMapper<Player> pm = ComponentMapper.getFor(Player.class);
    return pm.get(entity) != null;
  }
}
