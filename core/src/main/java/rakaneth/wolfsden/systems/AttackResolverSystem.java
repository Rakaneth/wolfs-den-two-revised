package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.RKDice;
import rakaneth.wolfsden.Swatch;
import rakaneth.wolfsden.components.Attack;
import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.Identity;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.SecondaryStats;
import rakaneth.wolfsden.components.Vitals;
import rakaneth.wolfsden.screens.PlayScreen;

public class AttackResolverSystem extends IteratingSystem
{
  public AttackResolverSystem()
  {
    super(Family.all(Attack.class)
                .get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    Attack at = Mapper.attackers.get(entity);
    SecondaryStats atkStats = Mapper.secondaries.get(entity);
    SecondaryStats defStats = Mapper.secondaries.get(at.target);
    Drawing atDrw = Mapper.drawing.get(entity);
    Drawing defDrw = Mapper.drawing.get(at.target);
    Identity atkID = Mapper.identity.get(entity);
    Identity defID = Mapper.identity.get(at.target);
    Vitals defVit = Mapper.vitals.get(at.target);

    int atkSux = atkStats.atk.roll(defStats.def);
    String atkColor = atDrw.color.getName();
    String defColor = defDrw.color.getName();
    if (atkSux >= 1)
    {
      int bonusRoll = atkSux - 1;
      int bonusKeep = bonusRoll / 2;
      RKDice bonusDice = new RKDice(bonusRoll, bonusKeep);
      int dmg = atkStats.dmg.add(bonusDice)
                            .roll();
      PlayScreen.addMessage("[%s]%s[] attacks [%s]%s[] for [%s]%d damage![]", atkColor, atkID.name, defColor,
                            defID.name, "Crimson", dmg);
      // TODO: account for armor
      defVit.takeDmg(dmg);
    } else
    {
      PlayScreen.addMessage("[%s]%s[] attacks [%s]%s[] and [%s]misses![]", atkColor, atkID.name, defColor, defID.name,
                            Swatch.WARNING);
    }
    entity.remove(Attack.class);
  }
}
