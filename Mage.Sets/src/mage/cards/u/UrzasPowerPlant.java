
package mage.cards.u;

import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.dynamicvalue.common.UrzaTerrainValue;
import mage.abilities.mana.DynamicManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;

import java.util.UUID;

/**
 *
 * @author Melkhior
 */
public final class UrzasPowerPlant extends CardImpl {
    public UrzasPowerPlant(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.LAND},"");
        this.subtype.add(SubType.URZAS, SubType.POWER_PLANT);

        // {T}: Add {C}. If you control an Urza's Mine and an Urza's Tower, add {C}{C} instead.
        Ability urzaManaAbility = new DynamicManaAbility(Mana.ColorlessMana(1), new UrzaTerrainValue(2),
                "Add {C}. If you control an Urza's Mine and an Urza's Tower, add {C}{C} instead");
        this.addAbility(urzaManaAbility);
    }

    public UrzasPowerPlant(final UrzasPowerPlant card) {
        super(card);
    }

    @Override
    public UrzasPowerPlant copy() {
        return new UrzasPowerPlant(this);
    }
}
