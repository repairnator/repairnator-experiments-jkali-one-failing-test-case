/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.v;

import java.util.UUID;
import mage.MageInt;
import mage.ObjectColor;
import mage.abilities.common.EntersBattlefieldAbility;
import mage.abilities.common.SpellCastControllerTriggeredAbility;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.effects.common.counter.RemoveCounterSourceEffect;
import mage.abilities.keyword.LifelinkAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.counters.CounterType;
import mage.filter.FilterSpell;
import mage.filter.predicate.mageobject.ColorPredicate;

/**
 * @author Loki
 */
public final class VoraciousHatchling extends CardImpl {

    private static final FilterSpell filterWhiteSpell = new FilterSpell("a white spell");
    private static final FilterSpell filterBlackSpell = new FilterSpell("a black spell");

    static {
        filterWhiteSpell.add(new ColorPredicate(ObjectColor.WHITE));
        filterBlackSpell.add(new ColorPredicate(ObjectColor.BLACK));
    }

    public VoraciousHatchling(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{3}{W/B}");
        this.subtype.add(SubType.ELEMENTAL);


        this.power = new MageInt(6);
        this.toughness = new MageInt(6);

        // Lifelink
        this.addAbility(LifelinkAbility.getInstance());
        // Voracious Hatchling enters the battlefield with four -1/-1 counters on it.
        this.addAbility(new EntersBattlefieldAbility(new AddCountersSourceEffect(CounterType.M1M1.createInstance(4)),"with four -1/-1 counters on it"));
        // Whenever you cast a white spell, remove a -1/-1 counter from Voracious Hatchling.
        this.addAbility(new SpellCastControllerTriggeredAbility(new RemoveCounterSourceEffect(CounterType.M1M1.createInstance(1)), filterWhiteSpell, false));
        // Whenever you cast a black spell, remove a -1/-1 counter from Voracious Hatchling.
        this.addAbility(new SpellCastControllerTriggeredAbility(new RemoveCounterSourceEffect(CounterType.M1M1.createInstance(1)), filterBlackSpell, false));
    }

    public VoraciousHatchling(final VoraciousHatchling card) {
        super(card);
    }

    @Override
    public VoraciousHatchling copy() {
        return new VoraciousHatchling(this);
    }
}
