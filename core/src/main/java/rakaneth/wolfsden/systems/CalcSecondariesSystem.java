package rakaneth.wolfsden.systems;

import java.util.Arrays;
import java.util.OptionalInt;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.RKDice;
import rakaneth.wolfsden.components.Armor;
import rakaneth.wolfsden.components.Mainhand;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Offhand;
import rakaneth.wolfsden.components.SecondaryStats;
import rakaneth.wolfsden.components.Stats;
import rakaneth.wolfsden.components.Trinket;
import rakaneth.wolfsden.components.Vitals;

public class CalcSecondariesSystem extends IteratingSystem
{

  public CalcSecondariesSystem()
  {
    super(Family.all(Stats.class, SecondaryStats.class)
                .get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime)
  {
    // TODO: adjust when equipment is in
    Stats stats = Mapper.stats.get(entity);
    SecondaryStats secStats = Mapper.secondaries.get(entity);
    Armor arm = Mapper.armors.get(entity);
    Trinket trink = Mapper.trinkets.get(entity);
    Mainhand mh = Mapper.mainhands.get(entity);
    Offhand oh = Mapper.offhands.get(entity);
    Vitals vitals = Mapper.vitals.get(entity);

    RKDice totalAtk = RKDice.add(arm.atk, trink.atk, mh.atk, oh.atk);
    RKDice totalDmg = RKDice.add(arm.dmg, trink.dmg, mh.dmg, oh.dmg);
    int totalDef = arm.def + trink.def + mh.def + oh.def;

    // Base mov is in armor, others provide penalties (positive) or bonuses
    int maxMov = arm.mov + trink.mov + mh.mov + oh.mov;

    // Base delay is in MH or OH; others provide penalites (positive) or bonuses
    int maxDelay = Math.max(mh.delay, oh.delay) + trink.delay + arm.delay;

    secStats.moveDelay = Math.max(1, maxMov - stats.spd);
    secStats.atkDelay = Math.max(1, maxDelay - stats.skl);
    secStats.atk = RKDice.add(new RKDice(stats.skl, stats.skl), totalAtk);
    secStats.def = (stats.spd * 4) + totalDef;
    secStats.dmg = RKDice.add(new RKDice(stats.str, 0), totalDmg);
    vitals.maxVit = stats.stam * 10;
    vitals.maxEnd = stats.stam * 15;
  }

}
