
package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 *
 * @author fireshoes
 */
public final class FTVTransform extends ExpansionSet {

    private static final FTVTransform instance = new FTVTransform();

    public static FTVTransform getInstance() {
        return instance;
    }

    private FTVTransform() {
        super("From the Vault: Transform", "V17", ExpansionSet.buildDate(2017, 11, 24), SetType.SUPPLEMENTAL);
        this.hasBasicLands = false;
        cards.add(new SetCardInfo("Archangel Avacyn", 1, Rarity.MYTHIC, mage.cards.a.ArchangelAvacyn.class));
        cards.add(new SetCardInfo("Avacyn, the Purifier", 1001, Rarity.MYTHIC, mage.cards.a.AvacynThePurifier.class));
        cards.add(new SetCardInfo("Arguel's Blood Fast", 2, Rarity.MYTHIC, mage.cards.a.ArguelsBloodFast.class));
        cards.add(new SetCardInfo("Temple of Aclazotz", 1002, Rarity.MYTHIC, mage.cards.t.TempleOfAclazotz.class));
        cards.add(new SetCardInfo("Arlinn Kord", 3, Rarity.MYTHIC, mage.cards.a.ArlinnKord.class));
        cards.add(new SetCardInfo("Arlinn, Embraced by the Moon", 1003, Rarity.MYTHIC, mage.cards.a.ArlinnEmbracedByTheMoon.class));
        cards.add(new SetCardInfo("Bloodline Keeper", 4, Rarity.MYTHIC, mage.cards.b.BloodlineKeeper.class));
        cards.add(new SetCardInfo("Lord of Lineage", 1004, Rarity.MYTHIC, mage.cards.l.LordOfLineage.class));
        cards.add(new SetCardInfo("Bruna, the Fading Light", 5, Rarity.MYTHIC, mage.cards.b.BrunaTheFadingLight.class));
        cards.add(new SetCardInfo("Brisela, Voice of Nightmares", 1005, Rarity.MYTHIC, mage.cards.b.BriselaVoiceOfNightmares.class));
        cards.add(new SetCardInfo("Brisela, Voice of Nightmares", 1005, Rarity.MYTHIC, mage.cards.b.BriselaVoiceOfNightmares.class));
        cards.add(new SetCardInfo("Chandra, Fire of Kaladesh", 6, Rarity.MYTHIC, mage.cards.c.ChandraFireOfKaladesh.class));
        cards.add(new SetCardInfo("Chandra, Roaring Flame", 1006, Rarity.MYTHIC, mage.cards.c.ChandraRoaringFlame.class));
        cards.add(new SetCardInfo("Delver of Secrets", 7, Rarity.MYTHIC, mage.cards.d.DelverOfSecrets.class));
        cards.add(new SetCardInfo("Insectile Aberration", 1007, Rarity.MYTHIC, mage.cards.i.InsectileAberration.class));
        cards.add(new SetCardInfo("Elbrus, the Binding Blade", 8, Rarity.MYTHIC, mage.cards.e.ElbrusTheBindingBlade.class));
        cards.add(new SetCardInfo("Withengar Unbound", 1008, Rarity.MYTHIC, mage.cards.w.WithengarUnbound.class));
        cards.add(new SetCardInfo("Garruk Relentless", 9, Rarity.MYTHIC, mage.cards.g.GarrukRelentless.class));
        cards.add(new SetCardInfo("Garruk, the Veil-Cursed", 1009, Rarity.MYTHIC, mage.cards.g.GarrukTheVeilCursed.class));
        cards.add(new SetCardInfo("Gisela, the Broken Blade", 10, Rarity.MYTHIC, mage.cards.g.GiselaTheBrokenBlade.class));
        cards.add(new SetCardInfo("Huntmaster of the Fells", 11, Rarity.MYTHIC, mage.cards.h.HuntmasterOfTheFells.class));
        cards.add(new SetCardInfo("Ravager of the Fells", 1011, Rarity.MYTHIC, mage.cards.r.RavagerOfTheFells.class));
        cards.add(new SetCardInfo("Jace, Vryn's Prodigy", 12, Rarity.MYTHIC, mage.cards.j.JaceVrynsProdigy.class));
        cards.add(new SetCardInfo("Jace, Telepath Unbound", 1012, Rarity.MYTHIC, mage.cards.j.JaceTelepathUnbound.class));
        cards.add(new SetCardInfo("Kytheon, Hero of Akros", 13, Rarity.MYTHIC, mage.cards.k.KytheonHeroOfAkros.class));
        cards.add(new SetCardInfo("Gideon, Battle-Forged", 1013, Rarity.MYTHIC, mage.cards.g.GideonBattleForged.class));
        cards.add(new SetCardInfo("Liliana, Heretical Healer", 14, Rarity.MYTHIC, mage.cards.l.LilianaHereticalHealer.class));
        cards.add(new SetCardInfo("Liliana, Defiant Necromancer", 1014, Rarity.MYTHIC, mage.cards.l.LilianaDefiantNecromancer.class));
        cards.add(new SetCardInfo("Nissa, Vastwood Seer", 15, Rarity.MYTHIC, mage.cards.n.NissaVastwoodSeer.class));
        cards.add(new SetCardInfo("Nissa, Sage Animist", 1015, Rarity.MYTHIC, mage.cards.n.NissaSageAnimist.class));
    }
}
