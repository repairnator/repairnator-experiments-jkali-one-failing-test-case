/*
* Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are
* permitted provided that the following conditions are met:
*
*    1. Redistributions of source code must retain the above copyright notice, this list of
*       conditions and the following disclaimer.
*
*    2. Redistributions in binary form must reproduce the above copyright notice, this list
*       of conditions and the following disclaimer in the documentation and/or other materials
*       provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
* FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation are those of the
* authors and should not be interpreted as representing official policies, either expressed
* or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 *
 * @author fireshoes
 */
public final class PlanechaseAnthology extends ExpansionSet {

    private static final PlanechaseAnthology instance = new PlanechaseAnthology();

    public static PlanechaseAnthology getInstance() {
        return instance;
    }

    private PlanechaseAnthology() {
        super("Planechase Anthology", "PCA", ExpansionSet.buildDate(2016, 11, 25), SetType.SUPPLEMENTAL);
        this.blockName = "Command Zone";

        cards.add(new SetCardInfo("Arc Trail", 39, Rarity.UNCOMMON, mage.cards.a.ArcTrail.class));
        cards.add(new SetCardInfo("Armillary Sphere", 108, Rarity.COMMON, mage.cards.a.ArmillarySphere.class));
        cards.add(new SetCardInfo("Armored Griffin", 1, Rarity.UNCOMMON, mage.cards.a.ArmoredGriffin.class));
        cards.add(new SetCardInfo("Assassinate", 30, Rarity.COMMON, mage.cards.a.Assassinate.class));
        cards.add(new SetCardInfo("Augury Owl", 14, Rarity.COMMON, mage.cards.a.AuguryOwl.class));
        cards.add(new SetCardInfo("Aura Gnarlid", 55, Rarity.COMMON, mage.cards.a.AuraGnarlid.class));
        cards.add(new SetCardInfo("Auramancer", 2, Rarity.COMMON, mage.cards.a.Auramancer.class));
        cards.add(new SetCardInfo("Auratouched Mage", 3, Rarity.UNCOMMON, mage.cards.a.AuratouchedMage.class));
        cards.add(new SetCardInfo("Awakening Zone", 56, Rarity.RARE, mage.cards.a.AwakeningZone.class));
        cards.add(new SetCardInfo("Baleful Strix", 82, Rarity.UNCOMMON, mage.cards.b.BalefulStrix.class));
        cards.add(new SetCardInfo("Beast Within", 57, Rarity.UNCOMMON, mage.cards.b.BeastWithin.class));
        cards.add(new SetCardInfo("Beetleback Chief", 40, Rarity.UNCOMMON, mage.cards.b.BeetlebackChief.class));
        cards.add(new SetCardInfo("Bituminous Blast", 83, Rarity.UNCOMMON, mage.cards.b.BituminousBlast.class));
        cards.add(new SetCardInfo("Bloodbraid Elf", 84, Rarity.UNCOMMON, mage.cards.b.BloodbraidElf.class));
        cards.add(new SetCardInfo("Boar Umbra", 58, Rarity.UNCOMMON, mage.cards.b.BoarUmbra.class));
        cards.add(new SetCardInfo("Bramble Elemental", 59, Rarity.COMMON, mage.cards.b.BrambleElemental.class));
        cards.add(new SetCardInfo("Brindle Shoat", 60, Rarity.UNCOMMON, mage.cards.b.BrindleShoat.class));
        cards.add(new SetCardInfo("Brutalizer Exarch", 61, Rarity.UNCOMMON, mage.cards.b.BrutalizerExarch.class));
        cards.add(new SetCardInfo("Cadaver Imp", 31, Rarity.COMMON, mage.cards.c.CadaverImp.class));
        cards.add(new SetCardInfo("Cage of Hands", 4, Rarity.COMMON, mage.cards.c.CageOfHands.class));
        cards.add(new SetCardInfo("Cancel", 15, Rarity.COMMON, mage.cards.c.Cancel.class));
        cards.add(new SetCardInfo("Celestial Ancient", 5, Rarity.RARE, mage.cards.c.CelestialAncient.class));
        cards.add(new SetCardInfo("Concentrate", 16, Rarity.UNCOMMON, mage.cards.c.Concentrate.class));
        cards.add(new SetCardInfo("Cultivate", 62, Rarity.COMMON, mage.cards.c.Cultivate.class));
        cards.add(new SetCardInfo("Dark Hatchling", 32, Rarity.RARE, mage.cards.d.DarkHatchling.class));
        cards.add(new SetCardInfo("Deny Reality", 85, Rarity.COMMON, mage.cards.d.DenyReality.class));
        cards.add(new SetCardInfo("Dimir Aqueduct", 116, Rarity.COMMON, mage.cards.d.DimirAqueduct.class));
        cards.add(new SetCardInfo("Dimir Infiltrator", 86, Rarity.COMMON, mage.cards.d.DimirInfiltrator.class));
        cards.add(new SetCardInfo("Dowsing Shaman", 63, Rarity.UNCOMMON, mage.cards.d.DowsingShaman.class));
        cards.add(new SetCardInfo("Dragonlair Spider", 87, Rarity.RARE, mage.cards.d.DragonlairSpider.class));
        cards.add(new SetCardInfo("Dreampod Druid", 64, Rarity.UNCOMMON, mage.cards.d.DreampodDruid.class));
        cards.add(new SetCardInfo("Elderwood Scion", 88, Rarity.RARE, mage.cards.e.ElderwoodScion.class));
        cards.add(new SetCardInfo("Enigma Sphinx", 89, Rarity.RARE, mage.cards.e.EnigmaSphinx.class));
        cards.add(new SetCardInfo("Enlisted Wurm", 90, Rarity.UNCOMMON, mage.cards.e.EnlistedWurm.class));
        cards.add(new SetCardInfo("Erratic Explosion", 41, Rarity.COMMON, mage.cards.e.ErraticExplosion.class));
        cards.add(new SetCardInfo("Etherium-Horn Sorcerer", 91, Rarity.RARE, mage.cards.e.EtheriumHornSorcerer.class));
        cards.add(new SetCardInfo("Exotic Orchard", 117, Rarity.RARE, mage.cards.e.ExoticOrchard.class));
        cards.add(new SetCardInfo("Farsight Mask", 109, Rarity.UNCOMMON, mage.cards.f.FarsightMask.class));
        cards.add(new SetCardInfo("Felidar Umbra", 6, Rarity.UNCOMMON, mage.cards.f.FelidarUmbra.class));
        cards.add(new SetCardInfo("Fiery Conclusion", 42, Rarity.COMMON, mage.cards.f.FieryConclusion.class));
        cards.add(new SetCardInfo("Fiery Fall", 43, Rarity.COMMON, mage.cards.f.FieryFall.class));
        cards.add(new SetCardInfo("Fires of Yavimaya", 92, Rarity.UNCOMMON, mage.cards.f.FiresOfYavimaya.class));
        cards.add(new SetCardInfo("Flayer Husk", 110, Rarity.COMMON, mage.cards.f.FlayerHusk.class));
        cards.add(new SetCardInfo("Fling", 44, Rarity.COMMON, mage.cards.f.Fling.class));
        cards.add(new SetCardInfo("Forest", 151, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 152, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 153, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 154, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 155, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 156, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Fusion Elemental", 93, Rarity.UNCOMMON, mage.cards.f.FusionElemental.class));
        cards.add(new SetCardInfo("Ghostly Prison", 7, Rarity.UNCOMMON, mage.cards.g.GhostlyPrison.class));
        cards.add(new SetCardInfo("Glen Elendra Liege", 94, Rarity.RARE, mage.cards.g.GlenElendraLiege.class));
        cards.add(new SetCardInfo("Gluttonous Slime", 65, Rarity.UNCOMMON, mage.cards.g.GluttonousSlime.class));
        cards.add(new SetCardInfo("Graypelt Refuge", 118, Rarity.UNCOMMON, mage.cards.g.GraypeltRefuge.class));
        cards.add(new SetCardInfo("Gruul Turf", 119, Rarity.COMMON, mage.cards.g.GruulTurf.class));
        cards.add(new SetCardInfo("Guard Gomazoa", 17, Rarity.UNCOMMON, mage.cards.g.GuardGomazoa.class));
        cards.add(new SetCardInfo("Hellion Eruption", 45, Rarity.RARE, mage.cards.h.HellionEruption.class));
        cards.add(new SetCardInfo("Hellkite Hatchling", 95, Rarity.UNCOMMON, mage.cards.h.HellkiteHatchling.class));
        cards.add(new SetCardInfo("Higure, the Still Wind", 18, Rarity.RARE, mage.cards.h.HigureTheStillWind.class));
        cards.add(new SetCardInfo("Hissing Iguanar", 46, Rarity.COMMON, mage.cards.h.HissingIguanar.class));
        cards.add(new SetCardInfo("Hyena Umbra", 8, Rarity.COMMON, mage.cards.h.HyenaUmbra.class));
        cards.add(new SetCardInfo("Illusory Angel", 19, Rarity.UNCOMMON, mage.cards.i.IllusoryAngel.class));
        cards.add(new SetCardInfo("Indrik Umbra", 96, Rarity.RARE, mage.cards.i.IndrikUmbra.class));
        cards.add(new SetCardInfo("Ink-Eyes, Servant of Oni", 33, Rarity.RARE, mage.cards.i.InkEyesServantOfOni.class));
        cards.add(new SetCardInfo("Inkfathom Witch", 97, Rarity.UNCOMMON, mage.cards.i.InkfathomWitch.class));
        cards.add(new SetCardInfo("Island", 137, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Island", 138, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Island", 139, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Island", 140, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Island", 141, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Jwar Isle Refuge", 120, Rarity.UNCOMMON, mage.cards.j.JwarIsleRefuge.class));
        cards.add(new SetCardInfo("Kathari Remnant", 98, Rarity.UNCOMMON, mage.cards.k.KathariRemnant.class));
        cards.add(new SetCardInfo("Kazandu Refuge", 121, Rarity.UNCOMMON, mage.cards.k.KazanduRefuge.class));
        cards.add(new SetCardInfo("Khalni Garden", 122, Rarity.COMMON, mage.cards.k.KhalniGarden.class));
        cards.add(new SetCardInfo("Kor Spiritdancer", 9, Rarity.RARE, mage.cards.k.KorSpiritdancer.class));
        cards.add(new SetCardInfo("Krond the Dawn-Clad", 99, Rarity.MYTHIC, mage.cards.k.KrondTheDawnClad.class));
        cards.add(new SetCardInfo("Krosan Verge", 123, Rarity.UNCOMMON, mage.cards.k.KrosanVerge.class));
        cards.add(new SetCardInfo("Last Stand", 100, Rarity.RARE, mage.cards.l.LastStand.class));
        cards.add(new SetCardInfo("Liliana's Specter", 34, Rarity.COMMON, mage.cards.l.LilianasSpecter.class));
        cards.add(new SetCardInfo("Lumberknot", 66, Rarity.UNCOMMON, mage.cards.l.Lumberknot.class));
        cards.add(new SetCardInfo("Maelstrom Wanderer", 101, Rarity.MYTHIC, mage.cards.m.MaelstromWanderer.class));
        cards.add(new SetCardInfo("Mammoth Umbra", 10, Rarity.UNCOMMON, mage.cards.m.MammothUmbra.class));
        cards.add(new SetCardInfo("Mark of Mutiny", 47, Rarity.UNCOMMON, mage.cards.m.MarkOfMutiny.class));
        cards.add(new SetCardInfo("Mass Mutiny", 48, Rarity.RARE, mage.cards.m.MassMutiny.class));
        cards.add(new SetCardInfo("Mistblade Shinobi", 20, Rarity.COMMON, mage.cards.m.MistbladeShinobi.class));
        cards.add(new SetCardInfo("Mitotic Slime", 67, Rarity.RARE, mage.cards.m.MitoticSlime.class));
        cards.add(new SetCardInfo("Mountain", 147, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mountain", 148, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mountain", 149, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mountain", 150, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mudbutton Torchrunner", 49, Rarity.COMMON, mage.cards.m.MudbuttonTorchrunner.class));
        cards.add(new SetCardInfo("Mycoloth", 68, Rarity.RARE, mage.cards.m.Mycoloth.class));
        cards.add(new SetCardInfo("Nest Invader", 69, Rarity.COMMON, mage.cards.n.NestInvader.class));
        cards.add(new SetCardInfo("Ninja of the Deep Hours", 21, Rarity.COMMON, mage.cards.n.NinjaOfTheDeepHours.class));
        cards.add(new SetCardInfo("Noggle Ransacker", 102, Rarity.UNCOMMON, mage.cards.n.NoggleRansacker.class));
        cards.add(new SetCardInfo("Nullmage Advocate", 70, Rarity.COMMON, mage.cards.n.NullmageAdvocate.class));
        cards.add(new SetCardInfo("Okiba-Gang Shinobi", 35, Rarity.COMMON, mage.cards.o.OkibaGangShinobi.class));
        cards.add(new SetCardInfo("Ondu Giant", 71, Rarity.COMMON, mage.cards.o.OnduGiant.class));
        cards.add(new SetCardInfo("Overrun", 72, Rarity.UNCOMMON, mage.cards.o.Overrun.class));
        cards.add(new SetCardInfo("Penumbra Spider", 73, Rarity.COMMON, mage.cards.p.PenumbraSpider.class));
        cards.add(new SetCardInfo("Peregrine Drake", 22, Rarity.UNCOMMON, mage.cards.p.PeregrineDrake.class));
        cards.add(new SetCardInfo("Plains", 132, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Plains", 133, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Plains", 134, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Plains", 135, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Plains", 136, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Pollenbright Wings", 103, Rarity.UNCOMMON, mage.cards.p.PollenbrightWings.class));
        cards.add(new SetCardInfo("Predatory Urge", 74, Rarity.RARE, mage.cards.p.PredatoryUrge.class));
        cards.add(new SetCardInfo("Preyseizer Dragon", 50, Rarity.RARE, mage.cards.p.PreyseizerDragon.class));
        cards.add(new SetCardInfo("Primal Plasma", 23, Rarity.COMMON, mage.cards.p.PrimalPlasma.class));
        cards.add(new SetCardInfo("Quiet Disrepair", 75, Rarity.COMMON, mage.cards.q.QuietDisrepair.class));
        cards.add(new SetCardInfo("Quietus Spike", 112, Rarity.RARE, mage.cards.q.QuietusSpike.class));
        cards.add(new SetCardInfo("Rancor", 76, Rarity.COMMON, mage.cards.r.Rancor.class));
        cards.add(new SetCardInfo("Rivals' Duel", 51, Rarity.UNCOMMON, mage.cards.r.RivalsDuel.class));
        cards.add(new SetCardInfo("Rupture Spire", 124, Rarity.COMMON, mage.cards.r.RuptureSpire.class));
        cards.add(new SetCardInfo("Sai of the Shinobi", 113, Rarity.UNCOMMON, mage.cards.s.SaiOfTheShinobi.class));
        cards.add(new SetCardInfo("Sakashima's Student", 24, Rarity.RARE, mage.cards.s.SakashimasStudent.class));
        cards.add(new SetCardInfo("See Beyond", 25, Rarity.COMMON, mage.cards.s.SeeBeyond.class));
        cards.add(new SetCardInfo("Selesnya Sanctuary", 125, Rarity.COMMON, mage.cards.s.SelesnyaSanctuary.class));
        cards.add(new SetCardInfo("Shardless Agent", 104, Rarity.UNCOMMON, mage.cards.s.ShardlessAgent.class));
        cards.add(new SetCardInfo("Shimmering Grotto", 126, Rarity.COMMON, mage.cards.s.ShimmeringGrotto.class));
        cards.add(new SetCardInfo("Sigil of the Empty Throne", 11, Rarity.RARE, mage.cards.s.SigilOfTheEmptyThrone.class));
        cards.add(new SetCardInfo("Silent-Blade Oni", 105, Rarity.RARE, mage.cards.s.SilentBladeOni.class));
        cards.add(new SetCardInfo("Silhana Ledgewalker", 77, Rarity.COMMON, mage.cards.s.SilhanaLedgewalker.class));
        cards.add(new SetCardInfo("Skarrg, the Rage Pits", 127, Rarity.UNCOMMON, mage.cards.s.SkarrgTheRagePits.class));
        cards.add(new SetCardInfo("Skullsnatcher", 36, Rarity.COMMON, mage.cards.s.Skullsnatcher.class));
        cards.add(new SetCardInfo("Snake Umbra", 78, Rarity.COMMON, mage.cards.s.SnakeUmbra.class));
        cards.add(new SetCardInfo("Spirit Mantle", 12, Rarity.UNCOMMON, mage.cards.s.SpiritMantle.class));
        cards.add(new SetCardInfo("Swamp", 142, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swamp", 143, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swamp", 144, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swamp", 145, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swamp", 146, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Sunken Hope", 26, Rarity.RARE, mage.cards.s.SunkenHope.class));
        cards.add(new SetCardInfo("Tainted Isle", 128, Rarity.UNCOMMON, mage.cards.t.TaintedIsle.class));
        cards.add(new SetCardInfo("Terramorphic Expanse", 129, Rarity.COMMON, mage.cards.t.TerramorphicExpanse.class));
        cards.add(new SetCardInfo("Thorn-Thrash Viashino", 52, Rarity.COMMON, mage.cards.t.ThornThrashViashino.class));
        cards.add(new SetCardInfo("Thran Golem", 114, Rarity.UNCOMMON, mage.cards.t.ThranGolem.class));
        cards.add(new SetCardInfo("Three Dreams", 13, Rarity.RARE, mage.cards.t.ThreeDreams.class));
        cards.add(new SetCardInfo("Throat Slitter", 37, Rarity.UNCOMMON, mage.cards.t.ThroatSlitter.class));
        cards.add(new SetCardInfo("Thromok the Insatiable", 106, Rarity.MYTHIC, mage.cards.t.ThromokTheInsatiable.class));
        cards.add(new SetCardInfo("Thunder-Thrash Elder", 53, Rarity.UNCOMMON, mage.cards.t.ThunderThrashElder.class));
        cards.add(new SetCardInfo("Tormented Soul", 38, Rarity.COMMON, mage.cards.t.TormentedSoul.class));
        cards.add(new SetCardInfo("Tukatongue Thallid", 79, Rarity.COMMON, mage.cards.t.TukatongueThallid.class));
        cards.add(new SetCardInfo("Vela the Night-Clad", 107, Rarity.MYTHIC, mage.cards.v.VelaTheNightClad.class));
        cards.add(new SetCardInfo("Viridian Emissary", 80, Rarity.COMMON, mage.cards.v.ViridianEmissary.class));
        cards.add(new SetCardInfo("Vitu-Ghazi, the City-Tree", 130, Rarity.UNCOMMON, mage.cards.v.VituGhaziTheCityTree.class));
        cards.add(new SetCardInfo("Vivid Creek", 131, Rarity.UNCOMMON, mage.cards.v.VividCreek.class));
        cards.add(new SetCardInfo("Walker of Secret Ways", 27, Rarity.UNCOMMON, mage.cards.w.WalkerOfSecretWays.class));
        cards.add(new SetCardInfo("Wall of Blossoms", 81, Rarity.UNCOMMON, mage.cards.w.WallOfBlossoms.class));
        cards.add(new SetCardInfo("Wall of Frost", 28, Rarity.UNCOMMON, mage.cards.w.WallOfFrost.class));
        cards.add(new SetCardInfo("Warstorm Surge", 54, Rarity.RARE, mage.cards.w.WarstormSurge.class));
        cards.add(new SetCardInfo("Whirlpool Warrior", 29, Rarity.RARE, mage.cards.w.WhirlpoolWarrior.class));
        cards.add(new SetCardInfo("Whispersilk Cloak", 115, Rarity.UNCOMMON, mage.cards.w.WhispersilkCloak.class));
    }

}
