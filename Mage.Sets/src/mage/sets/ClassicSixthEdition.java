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
package mage.sets;

import mage.cards.ExpansionSet;
import mage.cards.d.DrySpell;
import mage.cards.f.FeastOfTheUnicorn;
import mage.cards.m.MesaFalcon;
import mage.cards.s.SoldeviSage;
import mage.cards.v.VodalianSoldiers;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 *
 * @author North
 */
public class ClassicSixthEdition extends ExpansionSet {

    private static final ClassicSixthEdition instance = new ClassicSixthEdition();

    public static ClassicSixthEdition getInstance() {
        return instance;
    }

    private ClassicSixthEdition() {
        super("Classic Sixth Edition", "6ED", ExpansionSet.buildDate(1999, 3, 28), SetType.CORE);
        this.hasBoosters = true;
        this.numBoosterLands = 0;
        this.numBoosterCommon = 11;
        this.numBoosterUncommon = 3;
        this.numBoosterRare = 1;
        this.ratioBoosterMythic = 0;
        cards.add(new SetCardInfo("Abduction", 55, Rarity.UNCOMMON, mage.cards.a.Abduction.class));
        cards.add(new SetCardInfo("Abyssal Hunter", 109, Rarity.RARE, mage.cards.a.AbyssalHunter.class));
        cards.add(new SetCardInfo("Abyssal Specter", 110, Rarity.UNCOMMON, mage.cards.a.AbyssalSpecter.class));
        cards.add(new SetCardInfo("Adarkar Wastes", 319, Rarity.RARE, mage.cards.a.AdarkarWastes.class));
        cards.add(new SetCardInfo("Aether Flash", 163, Rarity.UNCOMMON, mage.cards.a.AetherFlash.class));
        cards.add(new SetCardInfo("Agonizing Memories", 111, Rarity.UNCOMMON, mage.cards.a.AgonizingMemories.class));
        cards.add(new SetCardInfo("Air Elemental", 56, Rarity.UNCOMMON, mage.cards.a.AirElemental.class));
        cards.add(new SetCardInfo("Aladdin's Ring", 271, Rarity.RARE, mage.cards.a.AladdinsRing.class));
        cards.add(new SetCardInfo("Amber Prison", 272, Rarity.RARE, mage.cards.a.AmberPrison.class));
        cards.add(new SetCardInfo("Anaba Bodyguard", 164, Rarity.COMMON, mage.cards.a.AnabaBodyguard.class));
        cards.add(new SetCardInfo("Anaba Shaman", 165, Rarity.COMMON, mage.cards.a.AnabaShaman.class));
        cards.add(new SetCardInfo("Ancestral Memories", 57, Rarity.RARE, mage.cards.a.AncestralMemories.class));
        cards.add(new SetCardInfo("Animate Wall", 1, Rarity.RARE, mage.cards.a.AnimateWall.class));
        cards.add(new SetCardInfo("Ankh of Mishra", 273, Rarity.RARE, mage.cards.a.AnkhOfMishra.class));
        cards.add(new SetCardInfo("Archangel", 2, Rarity.RARE, mage.cards.a.Archangel.class));
        cards.add(new SetCardInfo("Ardent Militia", 3, Rarity.UNCOMMON, mage.cards.a.ArdentMilitia.class));
        cards.add(new SetCardInfo("Armageddon", 4, Rarity.RARE, mage.cards.a.Armageddon.class));
        cards.add(new SetCardInfo("Armored Pegasus", 5, Rarity.COMMON, mage.cards.a.ArmoredPegasus.class));
        cards.add(new SetCardInfo("Ashen Powder", 112, Rarity.RARE, mage.cards.a.AshenPowder.class));
        cards.add(new SetCardInfo("Ashnod's Altar", 274, Rarity.UNCOMMON, mage.cards.a.AshnodsAltar.class));
        cards.add(new SetCardInfo("Balduvian Barbarians", 166, Rarity.COMMON, mage.cards.b.BalduvianBarbarians.class));
        cards.add(new SetCardInfo("Balduvian Horde", 167, Rarity.RARE, mage.cards.b.BalduvianHorde.class));
        cards.add(new SetCardInfo("Birds of Paradise", 217, Rarity.RARE, mage.cards.b.BirdsOfParadise.class));
        cards.add(new SetCardInfo("Blaze", 168, Rarity.UNCOMMON, mage.cards.b.Blaze.class));
        cards.add(new SetCardInfo("Blight", 113, Rarity.UNCOMMON, mage.cards.b.Blight.class));
        cards.add(new SetCardInfo("Blighted Shaman", 114, Rarity.UNCOMMON, mage.cards.b.BlightedShaman.class));
        cards.add(new SetCardInfo("Blood Pet", 115, Rarity.COMMON, mage.cards.b.BloodPet.class));
        cards.add(new SetCardInfo("Bog Imp", 116, Rarity.COMMON, mage.cards.b.BogImp.class));
        cards.add(new SetCardInfo("Bog Rats", 117, Rarity.COMMON, mage.cards.b.BogRats.class));
        cards.add(new SetCardInfo("Bog Wraith", 118, Rarity.UNCOMMON, mage.cards.b.BogWraith.class));
        cards.add(new SetCardInfo("Boil", 169, Rarity.UNCOMMON, mage.cards.b.Boil.class));
        cards.add(new SetCardInfo("Boomerang", 58, Rarity.COMMON, mage.cards.b.Boomerang.class));
        cards.add(new SetCardInfo("Bottle of Suleiman", 275, Rarity.RARE, mage.cards.b.BottleOfSuleiman.class));
        cards.add(new SetCardInfo("Browse", 59, Rarity.UNCOMMON, mage.cards.b.Browse.class));
        cards.add(new SetCardInfo("Brushland", 320, Rarity.RARE, mage.cards.b.Brushland.class));
        cards.add(new SetCardInfo("Burrowing", 170, Rarity.UNCOMMON, mage.cards.b.Burrowing.class));
        cards.add(new SetCardInfo("Call of the Wild", 218, Rarity.RARE, mage.cards.c.CallOfTheWild.class));
        cards.add(new SetCardInfo("Castle", 6, Rarity.UNCOMMON, mage.cards.c.Castle.class));
        cards.add(new SetCardInfo("Cat Warriors", 219, Rarity.COMMON, mage.cards.c.CatWarriors.class));
        cards.add(new SetCardInfo("Celestial Dawn", 7, Rarity.RARE, mage.cards.c.CelestialDawn.class));
        cards.add(new SetCardInfo("Charcoal Diamond", 276, Rarity.UNCOMMON, mage.cards.c.CharcoalDiamond.class));
        cards.add(new SetCardInfo("Chill", 60, Rarity.UNCOMMON, mage.cards.c.Chill.class));
        cards.add(new SetCardInfo("Circle of Protection: Black", 8, Rarity.COMMON, mage.cards.c.CircleOfProtectionBlack.class));
        cards.add(new SetCardInfo("Circle of Protection: Blue", 9, Rarity.COMMON, mage.cards.c.CircleOfProtectionBlue.class));
        cards.add(new SetCardInfo("Circle of Protection: Green", 10, Rarity.COMMON, mage.cards.c.CircleOfProtectionGreen.class));
        cards.add(new SetCardInfo("Circle of Protection: Red", 11, Rarity.COMMON, mage.cards.c.CircleOfProtectionRed.class));
        cards.add(new SetCardInfo("Circle of Protection: White", 12, Rarity.COMMON, mage.cards.c.CircleOfProtectionWhite.class));
        cards.add(new SetCardInfo("City of Brass", 321, Rarity.RARE, mage.cards.c.CityOfBrass.class));
        cards.add(new SetCardInfo("Coercion", 119, Rarity.COMMON, mage.cards.c.Coercion.class));
        cards.add(new SetCardInfo("Conquer", 171, Rarity.UNCOMMON, mage.cards.c.Conquer.class));
        cards.add(new SetCardInfo("Counterspell", 61, Rarity.COMMON, mage.cards.c.Counterspell.class));
        cards.add(new SetCardInfo("Creeping Mold", 220, Rarity.UNCOMMON, mage.cards.c.CreepingMold.class));
        cards.add(new SetCardInfo("Crimson Hellkite", 172, Rarity.RARE, mage.cards.c.CrimsonHellkite.class));
        cards.add(new SetCardInfo("Crusade", 13, Rarity.RARE, mage.cards.c.Crusade.class));
        cards.add(new SetCardInfo("Crystal Rod", 277, Rarity.UNCOMMON, mage.cards.c.CrystalRod.class));
        cards.add(new SetCardInfo("Crystal Vein", 322, Rarity.UNCOMMON, mage.cards.c.CrystalVein.class));
        cards.add(new SetCardInfo("Cursed Totem", 278, Rarity.RARE, mage.cards.c.CursedTotem.class));
        cards.add(new SetCardInfo("Dancing Scimitar", 279, Rarity.RARE, mage.cards.d.DancingScimitar.class));
        cards.add(new SetCardInfo("Daraja Griffin", 14, Rarity.UNCOMMON, mage.cards.d.DarajaGriffin.class));
        cards.add(new SetCardInfo("Daring Apprentice", 62, Rarity.RARE, mage.cards.d.DaringApprentice.class));
        cards.add(new SetCardInfo("D'Avenant Archer", 15, Rarity.COMMON, mage.cards.d.DAvenantArcher.class));
        cards.add(new SetCardInfo("Deflection", 63, Rarity.RARE, mage.cards.d.Deflection.class));
        cards.add(new SetCardInfo("Dense Foliage", 221, Rarity.RARE, mage.cards.d.DenseFoliage.class));
        cards.add(new SetCardInfo("Derelor", 120, Rarity.RARE, mage.cards.d.Derelor.class));
        cards.add(new SetCardInfo("Desertion", 64, Rarity.RARE, mage.cards.d.Desertion.class));
        cards.add(new SetCardInfo("Diminishing Returns", 65, Rarity.RARE, mage.cards.d.DiminishingReturns.class));
        cards.add(new SetCardInfo("Dingus Egg", 280, Rarity.RARE, mage.cards.d.DingusEgg.class));
        cards.add(new SetCardInfo("Disenchant", 16, Rarity.COMMON, mage.cards.d.Disenchant.class));
        cards.add(new SetCardInfo("Disrupting Scepter", 281, Rarity.RARE, mage.cards.d.DisruptingScepter.class));
        cards.add(new SetCardInfo("Divine Transformation", 17, Rarity.UNCOMMON, mage.cards.d.DivineTransformation.class));
        cards.add(new SetCardInfo("Doomsday", 121, Rarity.RARE, mage.cards.d.Doomsday.class));
        cards.add(new SetCardInfo("Dragon Engine", 282, Rarity.RARE, mage.cards.d.DragonEngine.class));
        cards.add(new SetCardInfo("Dragon Mask", 283, Rarity.UNCOMMON, mage.cards.d.DragonMask.class));
        cards.add(new SetCardInfo("Dread of Night", 122, Rarity.UNCOMMON, mage.cards.d.DreadOfNight.class));
        cards.add(new SetCardInfo("Dream Cache", 66, Rarity.COMMON, mage.cards.d.DreamCache.class));
        cards.add(new SetCardInfo("Drudge Skeletons", 123, Rarity.COMMON, mage.cards.d.DrudgeSkeletons.class));
        cards.add(new SetCardInfo("Dry Spell", 124, Rarity.COMMON, DrySpell.class));
        cards.add(new SetCardInfo("Dwarven Ruins", 323, Rarity.UNCOMMON, mage.cards.d.DwarvenRuins.class));
        cards.add(new SetCardInfo("Early Harvest", 222, Rarity.RARE, mage.cards.e.EarlyHarvest.class));
        cards.add(new SetCardInfo("Earthquake", 173, Rarity.RARE, mage.cards.e.Earthquake.class));
        cards.add(new SetCardInfo("Ebon Stronghold", 324, Rarity.UNCOMMON, mage.cards.e.EbonStronghold.class));
        cards.add(new SetCardInfo("Ekundu Griffin", 18, Rarity.COMMON, mage.cards.e.EkunduGriffin.class));
        cards.add(new SetCardInfo("Elder Druid", 223, Rarity.RARE, mage.cards.e.ElderDruid.class));
        cards.add(new SetCardInfo("Elven Cache", 224, Rarity.COMMON, mage.cards.e.ElvenCache.class));
        cards.add(new SetCardInfo("Elven Riders", 225, Rarity.UNCOMMON, mage.cards.e.ElvenRiders.class));
        cards.add(new SetCardInfo("Elvish Archers", 226, Rarity.RARE, mage.cards.e.ElvishArchers.class));
        cards.add(new SetCardInfo("Enfeeblement", 125, Rarity.COMMON, mage.cards.e.Enfeeblement.class));
        cards.add(new SetCardInfo("Enlightened Tutor", 19, Rarity.UNCOMMON, mage.cards.e.EnlightenedTutor.class));
        cards.add(new SetCardInfo("Ethereal Champion", 20, Rarity.RARE, mage.cards.e.EtherealChampion.class));
        cards.add(new SetCardInfo("Evil Eye of Orms-by-Gore", 126, Rarity.UNCOMMON, mage.cards.e.EvilEyeOfOrmsByGore.class));
        cards.add(new SetCardInfo("Exile", 21, Rarity.RARE, mage.cards.e.Exile.class));
        cards.add(new SetCardInfo("Fallen Angel", 127, Rarity.RARE, mage.cards.f.FallenAngel.class));
        cards.add(new SetCardInfo("Fallow Earth", 227, Rarity.UNCOMMON, mage.cards.f.FallowEarth.class));
        cards.add(new SetCardInfo("Familiar Ground", 228, Rarity.UNCOMMON, mage.cards.f.FamiliarGround.class));
        cards.add(new SetCardInfo("Fatal Blow", 128, Rarity.COMMON, mage.cards.f.FatalBlow.class));
        cards.add(new SetCardInfo("Fear", 129, Rarity.COMMON, mage.cards.f.Fear.class));
        cards.add(new SetCardInfo("Feast of the Unicorn", 130, Rarity.COMMON, FeastOfTheUnicorn.class));
        cards.add(new SetCardInfo("Femeref Archers", 229, Rarity.UNCOMMON, mage.cards.f.FemerefArchers.class));
        cards.add(new SetCardInfo("Feral Shadow", 131, Rarity.COMMON, mage.cards.f.FeralShadow.class));
        cards.add(new SetCardInfo("Fervor", 174, Rarity.RARE, mage.cards.f.Fervor.class));
        cards.add(new SetCardInfo("Final Fortune", 175, Rarity.RARE, mage.cards.f.FinalFortune.class));
        cards.add(new SetCardInfo("Firebreathing", 177, Rarity.COMMON, mage.cards.f.Firebreathing.class));
        cards.add(new SetCardInfo("Fire Diamond", 284, Rarity.UNCOMMON, mage.cards.f.FireDiamond.class));
        cards.add(new SetCardInfo("Fire Elemental", 176, Rarity.UNCOMMON, mage.cards.f.FireElemental.class));
        cards.add(new SetCardInfo("Fit of Rage", 178, Rarity.COMMON, mage.cards.f.FitOfRage.class));
        cards.add(new SetCardInfo("Flame Spirit", 179, Rarity.COMMON, mage.cards.f.FlameSpirit.class));
        cards.add(new SetCardInfo("Flash", 67, Rarity.RARE, mage.cards.f.Flash.class));
        cards.add(new SetCardInfo("Flashfires", 180, Rarity.UNCOMMON, mage.cards.f.Flashfires.class));
        cards.add(new SetCardInfo("Flight", 68, Rarity.COMMON, mage.cards.f.Flight.class));
        cards.add(new SetCardInfo("Flying Carpet", 285, Rarity.RARE, mage.cards.f.FlyingCarpet.class));
        cards.add(new SetCardInfo("Fog", 230, Rarity.COMMON, mage.cards.f.Fog.class));
        cards.add(new SetCardInfo("Fog Elemental", 69, Rarity.COMMON, mage.cards.f.FogElemental.class));
        cards.add(new SetCardInfo("Forbidden Crypt", 132, Rarity.RARE, mage.cards.f.ForbiddenCrypt.class));
        cards.add(new SetCardInfo("Forest", 347, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 348, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 349, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 350, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forget", 70, Rarity.RARE, mage.cards.f.Forget.class));
        cards.add(new SetCardInfo("Fountain of Youth", 286, Rarity.UNCOMMON, mage.cards.f.FountainOfYouth.class));
        cards.add(new SetCardInfo("Fyndhorn Brownie", 231, Rarity.COMMON, mage.cards.f.FyndhornBrownie.class));
        cards.add(new SetCardInfo("Fyndhorn Elder", 232, Rarity.UNCOMMON, mage.cards.f.FyndhornElder.class));
        cards.add(new SetCardInfo("Gaseous Form", 71, Rarity.COMMON, mage.cards.g.GaseousForm.class));
        cards.add(new SetCardInfo("Giant Growth", 233, Rarity.COMMON, mage.cards.g.GiantGrowth.class));
        cards.add(new SetCardInfo("Giant Spider", 234, Rarity.COMMON, mage.cards.g.GiantSpider.class));
        cards.add(new SetCardInfo("Giant Strength", 181, Rarity.COMMON, mage.cards.g.GiantStrength.class));
        cards.add(new SetCardInfo("Glacial Wall", 72, Rarity.UNCOMMON, mage.cards.g.GlacialWall.class));
        cards.add(new SetCardInfo("Glasses of Urza", 287, Rarity.UNCOMMON, mage.cards.g.GlassesOfUrza.class));
        cards.add(new SetCardInfo("Goblin Digging Team", 182, Rarity.COMMON, mage.cards.g.GoblinDiggingTeam.class));
        cards.add(new SetCardInfo("Goblin Elite Infantry", 183, Rarity.COMMON, mage.cards.g.GoblinEliteInfantry.class));
        cards.add(new SetCardInfo("Goblin Hero", 184, Rarity.COMMON, mage.cards.g.GoblinHero.class));
        cards.add(new SetCardInfo("Goblin King", 185, Rarity.RARE, mage.cards.g.GoblinKing.class));
        cards.add(new SetCardInfo("Goblin Recruiter", 186, Rarity.UNCOMMON, mage.cards.g.GoblinRecruiter.class));
        cards.add(new SetCardInfo("Goblin Warrens", 187, Rarity.RARE, mage.cards.g.GoblinWarrens.class));
        cards.add(new SetCardInfo("Gorilla Chieftain", 235, Rarity.COMMON, mage.cards.g.GorillaChieftain.class));
        cards.add(new SetCardInfo("Gravebane Zombie", 133, Rarity.UNCOMMON, mage.cards.g.GravebaneZombie.class));
        cards.add(new SetCardInfo("Gravedigger", 134, Rarity.COMMON, mage.cards.g.Gravedigger.class));
        cards.add(new SetCardInfo("Greed", 135, Rarity.RARE, mage.cards.g.Greed.class));
        cards.add(new SetCardInfo("Grinning Totem", 288, Rarity.RARE, mage.cards.g.GrinningTotem.class));
        cards.add(new SetCardInfo("Grizzly Bears", 236, Rarity.COMMON, mage.cards.g.GrizzlyBears.class));
        cards.add(new SetCardInfo("Hammer of Bogardan", 188, Rarity.RARE, mage.cards.h.HammerOfBogardan.class));
        cards.add(new SetCardInfo("Harmattan Efreet", 73, Rarity.UNCOMMON, mage.cards.h.HarmattanEfreet.class));
        cards.add(new SetCardInfo("Havenwood Battleground", 325, Rarity.UNCOMMON, mage.cards.h.HavenwoodBattleground.class));
        cards.add(new SetCardInfo("Healing Salve", 22, Rarity.COMMON, mage.cards.h.HealingSalve.class));
        cards.add(new SetCardInfo("Heavy Ballista", 23, Rarity.UNCOMMON, mage.cards.h.HeavyBallista.class));
        cards.add(new SetCardInfo("Hecatomb", 136, Rarity.RARE, mage.cards.h.Hecatomb.class));
        cards.add(new SetCardInfo("Hero's Resolve", 24, Rarity.COMMON, mage.cards.h.HerosResolve.class));
        cards.add(new SetCardInfo("Hidden Horror", 137, Rarity.UNCOMMON, mage.cards.h.HiddenHorror.class));
        cards.add(new SetCardInfo("Horned Turtle", 74, Rarity.COMMON, mage.cards.h.HornedTurtle.class));
        cards.add(new SetCardInfo("Howl from Beyond", 138, Rarity.COMMON, mage.cards.h.HowlFromBeyond.class));
        cards.add(new SetCardInfo("Howling Mine", 290, Rarity.RARE, mage.cards.h.HowlingMine.class));
        cards.add(new SetCardInfo("Hulking Cyclops", 189, Rarity.UNCOMMON, mage.cards.h.HulkingCyclops.class));
        cards.add(new SetCardInfo("Hurricane", 237, Rarity.RARE, mage.cards.h.Hurricane.class));
        cards.add(new SetCardInfo("Icatian Town", 25, Rarity.RARE, mage.cards.i.IcatianTown.class));
        cards.add(new SetCardInfo("Illicit Auction", 190, Rarity.RARE, mage.cards.i.IllicitAuction.class));
        cards.add(new SetCardInfo("Infantry Veteran", 26, Rarity.COMMON, mage.cards.i.InfantryVeteran.class));
        cards.add(new SetCardInfo("Infernal Contract", 139, Rarity.RARE, mage.cards.i.InfernalContract.class));
        cards.add(new SetCardInfo("Inferno", 191, Rarity.RARE, mage.cards.i.Inferno.class));
        cards.add(new SetCardInfo("Insight", 75, Rarity.UNCOMMON, mage.cards.i.Insight.class));
        cards.add(new SetCardInfo("Inspiration", 76, Rarity.COMMON, mage.cards.i.Inspiration.class));
        cards.add(new SetCardInfo("Iron Star", 291, Rarity.UNCOMMON, mage.cards.i.IronStar.class));
        cards.add(new SetCardInfo("Island", 335, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Island", 336, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Island", 337, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Island", 338, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Ivory Cup", 292, Rarity.UNCOMMON, mage.cards.i.IvoryCup.class));
        cards.add(new SetCardInfo("Jade Monolith", 293, Rarity.RARE, mage.cards.j.JadeMonolith.class));
        cards.add(new SetCardInfo("Jalum Tome", 294, Rarity.RARE, mage.cards.j.JalumTome.class));
        cards.add(new SetCardInfo("Jayemdae Tome", 295, Rarity.RARE, mage.cards.j.JayemdaeTome.class));
        cards.add(new SetCardInfo("Jokulhaups", 192, Rarity.RARE, mage.cards.j.Jokulhaups.class));
        cards.add(new SetCardInfo("Juxtapose", 77, Rarity.RARE, mage.cards.j.Juxtapose.class));
        cards.add(new SetCardInfo("Karplusan Forest", 326, Rarity.RARE, mage.cards.k.KarplusanForest.class));
        cards.add(new SetCardInfo("Kismet", 27, Rarity.UNCOMMON, mage.cards.k.Kismet.class));
        cards.add(new SetCardInfo("Kjeldoran Dead", 140, Rarity.COMMON, mage.cards.k.KjeldoranDead.class));
        cards.add(new SetCardInfo("Kjeldoran Royal Guard", 28, Rarity.RARE, mage.cards.k.KjeldoranRoyalGuard.class));
        cards.add(new SetCardInfo("Lead Golem", 296, Rarity.UNCOMMON, mage.cards.l.LeadGolem.class));
        cards.add(new SetCardInfo("Leshrac's Rite", 141, Rarity.UNCOMMON, mage.cards.l.LeshracsRite.class));
        cards.add(new SetCardInfo("Library of Lat-Nam", 78, Rarity.RARE, mage.cards.l.LibraryOfLatNam.class));
        cards.add(new SetCardInfo("Lightning Blast", 193, Rarity.COMMON, mage.cards.l.LightningBlast.class));
        cards.add(new SetCardInfo("Light of Day", 29, Rarity.UNCOMMON, mage.cards.l.LightOfDay.class));
        cards.add(new SetCardInfo("Living Lands", 238, Rarity.RARE, mage.cards.l.LivingLands.class));
        cards.add(new SetCardInfo("Llanowar Elves", 239, Rarity.COMMON, mage.cards.l.LlanowarElves.class));
        cards.add(new SetCardInfo("Longbow Archer", 30, Rarity.UNCOMMON, mage.cards.l.LongbowArcher.class));
        cards.add(new SetCardInfo("Lord of Atlantis", 79, Rarity.RARE, mage.cards.l.LordOfAtlantis.class));
        cards.add(new SetCardInfo("Lost Soul", 142, Rarity.COMMON, mage.cards.l.LostSoul.class));
        cards.add(new SetCardInfo("Lure", 240, Rarity.UNCOMMON, mage.cards.l.Lure.class));
        cards.add(new SetCardInfo("Manabarbs", 194, Rarity.RARE, mage.cards.m.Manabarbs.class));
        cards.add(new SetCardInfo("Mana Prism", 297, Rarity.UNCOMMON, mage.cards.m.ManaPrism.class));
        cards.add(new SetCardInfo("Mana Short", 80, Rarity.RARE, mage.cards.m.ManaShort.class));
        cards.add(new SetCardInfo("Marble Diamond", 298, Rarity.UNCOMMON, mage.cards.m.MarbleDiamond.class));
        cards.add(new SetCardInfo("Maro", 241, Rarity.RARE, mage.cards.m.Maro.class));
        cards.add(new SetCardInfo("Meekstone", 299, Rarity.RARE, mage.cards.m.Meekstone.class));
        cards.add(new SetCardInfo("Memory Lapse", 81, Rarity.COMMON, mage.cards.m.MemoryLapse.class));
        cards.add(new SetCardInfo("Merfolk of the Pearl Trident", 82, Rarity.COMMON, mage.cards.m.MerfolkOfThePearlTrident.class));
        cards.add(new SetCardInfo("Mesa Falcon", 31, Rarity.COMMON, MesaFalcon.class));
        cards.add(new SetCardInfo("Millstone", 300, Rarity.RARE, mage.cards.m.Millstone.class));
        cards.add(new SetCardInfo("Mind Warp", 143, Rarity.UNCOMMON, mage.cards.m.MindWarp.class));
        cards.add(new SetCardInfo("Mischievous Poltergeist", 144, Rarity.UNCOMMON, mage.cards.m.MischievousPoltergeist.class));
        cards.add(new SetCardInfo("Moss Diamond", 301, Rarity.UNCOMMON, mage.cards.m.MossDiamond.class));
        cards.add(new SetCardInfo("Mountain", 343, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mountain", 344, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mountain", 345, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mountain", 346, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mountain Goat", 195, Rarity.COMMON, mage.cards.m.MountainGoat.class));
        cards.add(new SetCardInfo("Mystical Tutor", 83, Rarity.UNCOMMON, mage.cards.m.MysticalTutor.class));
        cards.add(new SetCardInfo("Mystic Compass", 302, Rarity.UNCOMMON, mage.cards.m.MysticCompass.class));
        cards.add(new SetCardInfo("Nature's Resurgence", 242, Rarity.RARE, mage.cards.n.NaturesResurgence.class));
        cards.add(new SetCardInfo("Necrosavant", 145, Rarity.RARE, mage.cards.n.Necrosavant.class));
        cards.add(new SetCardInfo("Nightmare", 146, Rarity.RARE, mage.cards.n.Nightmare.class));
        cards.add(new SetCardInfo("Obsianus Golem", 303, Rarity.UNCOMMON, mage.cards.o.ObsianusGolem.class));
        cards.add(new SetCardInfo("Orcish Artillery", 196, Rarity.UNCOMMON, mage.cards.o.OrcishArtillery.class));
        cards.add(new SetCardInfo("Orcish Oriflamme", 197, Rarity.UNCOMMON, mage.cards.o.OrcishOriflamme.class));
        cards.add(new SetCardInfo("Order of the Sacred Torch", 32, Rarity.RARE, mage.cards.o.OrderOfTheSacredTorch.class));
        cards.add(new SetCardInfo("Ornithopter", 304, Rarity.UNCOMMON, mage.cards.o.Ornithopter.class));
        cards.add(new SetCardInfo("Pacifism", 33, Rarity.COMMON, mage.cards.p.Pacifism.class));
        cards.add(new SetCardInfo("Painful Memories", 147, Rarity.COMMON, mage.cards.p.PainfulMemories.class));
        cards.add(new SetCardInfo("Panther Warriors", 243, Rarity.COMMON, mage.cards.p.PantherWarriors.class));
        cards.add(new SetCardInfo("Patagia Golem", 305, Rarity.UNCOMMON, mage.cards.p.PatagiaGolem.class));
        cards.add(new SetCardInfo("Pearl Dragon", 34, Rarity.RARE, mage.cards.p.PearlDragon.class));
        cards.add(new SetCardInfo("Pentagram of the Ages", 306, Rarity.RARE, mage.cards.p.PentagramOfTheAges.class));
        cards.add(new SetCardInfo("Perish", 148, Rarity.UNCOMMON, mage.cards.p.Perish.class));
        cards.add(new SetCardInfo("Pestilence", 149, Rarity.COMMON, mage.cards.p.Pestilence.class));
        cards.add(new SetCardInfo("Phantasmal Terrain", 84, Rarity.COMMON, mage.cards.p.PhantasmalTerrain.class));
        cards.add(new SetCardInfo("Phantom Warrior", 85, Rarity.UNCOMMON, mage.cards.p.PhantomWarrior.class));
        cards.add(new SetCardInfo("Phyrexian Vault", 307, Rarity.UNCOMMON, mage.cards.p.PhyrexianVault.class));
        cards.add(new SetCardInfo("Pillage", 198, Rarity.UNCOMMON, mage.cards.p.Pillage.class));
        cards.add(new SetCardInfo("Plains", 331, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Plains", 332, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Plains", 333, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Plains", 334, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Polymorph", 86, Rarity.RARE, mage.cards.p.Polymorph.class));
        cards.add(new SetCardInfo("Power Sink", 87, Rarity.UNCOMMON, mage.cards.p.PowerSink.class));
        cards.add(new SetCardInfo("Pradesh Gypsies", 244, Rarity.COMMON, mage.cards.p.PradeshGypsies.class));
        cards.add(new SetCardInfo("Primal Clay", 308, Rarity.RARE, mage.cards.p.PrimalClay.class));
        cards.add(new SetCardInfo("Prodigal Sorcerer", 88, Rarity.COMMON, mage.cards.p.ProdigalSorcerer.class));
        cards.add(new SetCardInfo("Prosperity", 89, Rarity.UNCOMMON, mage.cards.p.Prosperity.class));
        cards.add(new SetCardInfo("Psychic Transfer", 90, Rarity.RARE, mage.cards.p.PsychicTransfer.class));
        cards.add(new SetCardInfo("Psychic Venom", 91, Rarity.COMMON, mage.cards.p.PsychicVenom.class));
        cards.add(new SetCardInfo("Pyrotechnics", 199, Rarity.COMMON, mage.cards.p.Pyrotechnics.class));
        cards.add(new SetCardInfo("Python", 150, Rarity.COMMON, mage.cards.p.Python.class));
        cards.add(new SetCardInfo("Radjan Spirit", 245, Rarity.UNCOMMON, mage.cards.r.RadjanSpirit.class));
        cards.add(new SetCardInfo("Raging Goblin", 200, Rarity.COMMON, mage.cards.r.RagingGoblin.class));
        cards.add(new SetCardInfo("Rag Man", 151, Rarity.RARE, mage.cards.r.RagMan.class));
        cards.add(new SetCardInfo("Raise Dead", 152, Rarity.COMMON, mage.cards.r.RaiseDead.class));
        cards.add(new SetCardInfo("Rampant Growth", 246, Rarity.COMMON, mage.cards.r.RampantGrowth.class));
        cards.add(new SetCardInfo("Razortooth Rats", 153, Rarity.COMMON, mage.cards.r.RazortoothRats.class));
        cards.add(new SetCardInfo("Recall", 92, Rarity.RARE, mage.cards.r.Recall.class));
        cards.add(new SetCardInfo("Reckless Embermage", 201, Rarity.RARE, mage.cards.r.RecklessEmbermage.class));
        cards.add(new SetCardInfo("Redwood Treefolk", 247, Rarity.COMMON, mage.cards.r.RedwoodTreefolk.class));
        cards.add(new SetCardInfo("Regal Unicorn", 35, Rarity.COMMON, mage.cards.r.RegalUnicorn.class));
        cards.add(new SetCardInfo("Regeneration", 248, Rarity.COMMON, mage.cards.r.Regeneration.class));
        cards.add(new SetCardInfo("Relearn", 93, Rarity.UNCOMMON, mage.cards.r.Relearn.class));
        cards.add(new SetCardInfo("Relentless Assault", 202, Rarity.RARE, mage.cards.r.RelentlessAssault.class));
        cards.add(new SetCardInfo("Remedy", 36, Rarity.COMMON, mage.cards.r.Remedy.class));
        cards.add(new SetCardInfo("Remove Soul", 94, Rarity.COMMON, mage.cards.r.RemoveSoul.class));
        cards.add(new SetCardInfo("Reprisal", 37, Rarity.UNCOMMON, mage.cards.r.Reprisal.class));
        cards.add(new SetCardInfo("Resistance Fighter", 38, Rarity.COMMON, mage.cards.r.ResistanceFighter.class));
        cards.add(new SetCardInfo("Reverse Damage", 39, Rarity.RARE, mage.cards.r.ReverseDamage.class));
        cards.add(new SetCardInfo("River Boa", 249, Rarity.UNCOMMON, mage.cards.r.RiverBoa.class));
        cards.add(new SetCardInfo("Rod of Ruin", 309, Rarity.UNCOMMON, mage.cards.r.RodOfRuin.class));
        cards.add(new SetCardInfo("Rowen", 250, Rarity.RARE, mage.cards.r.Rowen.class));
        cards.add(new SetCardInfo("Ruins of Trokair", 327, Rarity.UNCOMMON, mage.cards.r.RuinsOfTrokair.class));
        cards.add(new SetCardInfo("Sabretooth Tiger", 203, Rarity.COMMON, mage.cards.s.SabretoothTiger.class));
        cards.add(new SetCardInfo("Sage Owl", 95, Rarity.COMMON, mage.cards.s.SageOwl.class));
        cards.add(new SetCardInfo("Samite Healer", 40, Rarity.COMMON, mage.cards.s.SamiteHealer.class));
        cards.add(new SetCardInfo("Scaled Wurm", 251, Rarity.COMMON, mage.cards.s.ScaledWurm.class));
        cards.add(new SetCardInfo("Scathe Zombies", 154, Rarity.COMMON, mage.cards.s.ScatheZombies.class));
        cards.add(new SetCardInfo("Sea Monster", 96, Rarity.COMMON, mage.cards.s.SeaMonster.class));
        cards.add(new SetCardInfo("Segovian Leviathan", 97, Rarity.UNCOMMON, mage.cards.s.SegovianLeviathan.class));
        cards.add(new SetCardInfo("Sengir Autocrat", 155, Rarity.RARE, mage.cards.s.SengirAutocrat.class));
        cards.add(new SetCardInfo("Serenity", 41, Rarity.RARE, mage.cards.s.Serenity.class));
        cards.add(new SetCardInfo("Serra's Blessing", 42, Rarity.UNCOMMON, mage.cards.s.SerrasBlessing.class));
        cards.add(new SetCardInfo("Shanodin Dryads", 252, Rarity.COMMON, mage.cards.s.ShanodinDryads.class));
        cards.add(new SetCardInfo("Shatter", 204, Rarity.COMMON, mage.cards.s.Shatter.class));
        cards.add(new SetCardInfo("Shatterstorm", 205, Rarity.RARE, mage.cards.s.Shatterstorm.class));
        cards.add(new SetCardInfo("Shock", 206, Rarity.COMMON, mage.cards.s.Shock.class));
        cards.add(new SetCardInfo("Sibilant Spirit", 98, Rarity.RARE, mage.cards.s.SibilantSpirit.class));
        cards.add(new SetCardInfo("Skull Catapult", 310, Rarity.UNCOMMON, mage.cards.s.SkullCatapult.class));
        cards.add(new SetCardInfo("Sky Diamond", 311, Rarity.UNCOMMON, mage.cards.s.SkyDiamond.class));
        cards.add(new SetCardInfo("Snake Basket", 312, Rarity.RARE, mage.cards.s.SnakeBasket.class));
        cards.add(new SetCardInfo("Soldevi Sage", 99, Rarity.UNCOMMON, SoldeviSage.class));
        cards.add(new SetCardInfo("Soul Net", 313, Rarity.UNCOMMON, mage.cards.s.SoulNet.class));
        cards.add(new SetCardInfo("Spell Blast", 100, Rarity.COMMON, mage.cards.s.SpellBlast.class));
        cards.add(new SetCardInfo("Spirit Link", 43, Rarity.UNCOMMON, mage.cards.s.SpiritLink.class));
        cards.add(new SetCardInfo("Spitting Drake", 207, Rarity.UNCOMMON, mage.cards.s.SpittingDrake.class));
        cards.add(new SetCardInfo("Spitting Earth", 208, Rarity.COMMON, mage.cards.s.SpittingEarth.class));
        cards.add(new SetCardInfo("Stalking Tiger", 253, Rarity.COMMON, mage.cards.s.StalkingTiger.class));
        cards.add(new SetCardInfo("Standing Troops", 44, Rarity.COMMON, mage.cards.s.StandingTroops.class));
        cards.add(new SetCardInfo("Staunch Defenders", 45, Rarity.UNCOMMON, mage.cards.s.StaunchDefenders.class));
        cards.add(new SetCardInfo("Stone Rain", 209, Rarity.COMMON, mage.cards.s.StoneRain.class));
        cards.add(new SetCardInfo("Storm Cauldron", 314, Rarity.RARE, mage.cards.s.StormCauldron.class));
        cards.add(new SetCardInfo("Storm Crow", 101, Rarity.COMMON, mage.cards.s.StormCrow.class));
        cards.add(new SetCardInfo("Strands of Night", 156, Rarity.UNCOMMON, mage.cards.s.StrandsOfNight.class));
        cards.add(new SetCardInfo("Stream of Life", 254, Rarity.COMMON, mage.cards.s.StreamOfLife.class));
        cards.add(new SetCardInfo("Stromgald Cabal", 157, Rarity.RARE, mage.cards.s.StromgaldCabal.class));
        cards.add(new SetCardInfo("Stupor", 158, Rarity.UNCOMMON, mage.cards.s.Stupor.class));
        cards.add(new SetCardInfo("Sulfurous Springs", 328, Rarity.RARE, mage.cards.s.SulfurousSprings.class));
        cards.add(new SetCardInfo("Summer Bloom", 255, Rarity.UNCOMMON, mage.cards.s.SummerBloom.class));
        cards.add(new SetCardInfo("Sunweb", 46, Rarity.RARE, mage.cards.s.Sunweb.class));
        cards.add(new SetCardInfo("Svyelunite Temple", 329, Rarity.UNCOMMON, mage.cards.s.SvyeluniteTemple.class));
        cards.add(new SetCardInfo("Swamp", 339, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swamp", 340, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swamp", 341, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swamp", 342, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Syphon Soul", 159, Rarity.COMMON, mage.cards.s.SyphonSoul.class));
        cards.add(new SetCardInfo("Talruum Minotaur", 210, Rarity.COMMON, mage.cards.t.TalruumMinotaur.class));
        cards.add(new SetCardInfo("Tariff", 47, Rarity.RARE, mage.cards.t.Tariff.class));
        cards.add(new SetCardInfo("Teferi's Puzzle Box", 315, Rarity.RARE, mage.cards.t.TeferisPuzzleBox.class));
        cards.add(new SetCardInfo("Terror", 160, Rarity.COMMON, mage.cards.t.Terror.class));
        cards.add(new SetCardInfo("The Hive", 289, Rarity.RARE, mage.cards.t.TheHive.class));
        cards.add(new SetCardInfo("Thicket Basilisk", 256, Rarity.UNCOMMON, mage.cards.t.ThicketBasilisk.class));
        cards.add(new SetCardInfo("Throne of Bone", 316, Rarity.UNCOMMON, mage.cards.t.ThroneOfBone.class));
        cards.add(new SetCardInfo("Tidal Surge", 102, Rarity.COMMON, mage.cards.t.TidalSurge.class));
        cards.add(new SetCardInfo("Trained Armodon", 257, Rarity.COMMON, mage.cards.t.TrainedArmodon.class));
        cards.add(new SetCardInfo("Tranquil Grove", 258, Rarity.RARE, mage.cards.t.TranquilGrove.class));
        cards.add(new SetCardInfo("Tranquility", 259, Rarity.COMMON, mage.cards.t.Tranquility.class));
        cards.add(new SetCardInfo("Tremor", 211, Rarity.COMMON, mage.cards.t.Tremor.class));
        cards.add(new SetCardInfo("Tundra Wolves", 48, Rarity.COMMON, mage.cards.t.TundraWolves.class));
        cards.add(new SetCardInfo("Uktabi Orangutan", 260, Rarity.UNCOMMON, mage.cards.u.UktabiOrangutan.class));
        cards.add(new SetCardInfo("Uktabi Wildcats", 261, Rarity.RARE, mage.cards.u.UktabiWildcats.class));
        cards.add(new SetCardInfo("Underground River", 330, Rarity.RARE, mage.cards.u.UndergroundRiver.class));
        cards.add(new SetCardInfo("Unseen Walker", 262, Rarity.UNCOMMON, mage.cards.u.UnseenWalker.class));
        cards.add(new SetCardInfo("Unsummon", 103, Rarity.COMMON, mage.cards.u.Unsummon.class));
        cards.add(new SetCardInfo("Untamed Wilds", 263, Rarity.UNCOMMON, mage.cards.u.UntamedWilds.class));
        cards.add(new SetCardInfo("Unyaro Griffin", 49, Rarity.UNCOMMON, mage.cards.u.UnyaroGriffin.class));
        cards.add(new SetCardInfo("Vampiric Tutor", 161, Rarity.RARE, mage.cards.v.VampiricTutor.class));
        cards.add(new SetCardInfo("Venerable Monk", 50, Rarity.COMMON, mage.cards.v.VenerableMonk.class));
        cards.add(new SetCardInfo("Verduran Enchantress", 264, Rarity.RARE, mage.cards.v.VerduranEnchantress.class));
        cards.add(new SetCardInfo("Vertigo", 212, Rarity.UNCOMMON, mage.cards.v.Vertigo.class));
        cards.add(new SetCardInfo("Viashino Warrior", 213, Rarity.COMMON, mage.cards.v.ViashinoWarrior.class));
        cards.add(new SetCardInfo("Vitalize", 265, Rarity.COMMON, mage.cards.v.Vitalize.class));
        cards.add(new SetCardInfo("Vodalian Soldiers", 104, Rarity.COMMON, VodalianSoldiers.class));
        cards.add(new SetCardInfo("Volcanic Dragon", 214, Rarity.RARE, mage.cards.v.VolcanicDragon.class));
        cards.add(new SetCardInfo("Volcanic Geyser", 215, Rarity.UNCOMMON, mage.cards.v.VolcanicGeyser.class));
        cards.add(new SetCardInfo("Waiting in the Weeds", 266, Rarity.RARE, mage.cards.w.WaitingInTheWeeds.class));
        cards.add(new SetCardInfo("Wall of Air", 105, Rarity.UNCOMMON, mage.cards.w.WallOfAir.class));
        cards.add(new SetCardInfo("Wall of Fire", 216, Rarity.UNCOMMON, mage.cards.w.WallOfFire.class));
        cards.add(new SetCardInfo("Wall of Swords", 51, Rarity.UNCOMMON, mage.cards.w.WallOfSwords.class));
        cards.add(new SetCardInfo("Wand of Denial", 317, Rarity.RARE, mage.cards.w.WandOfDenial.class));
        cards.add(new SetCardInfo("Warmth", 52, Rarity.UNCOMMON, mage.cards.w.Warmth.class));
        cards.add(new SetCardInfo("Warrior's Honor", 53, Rarity.COMMON, mage.cards.w.WarriorsHonor.class));
        cards.add(new SetCardInfo("Warthog", 267, Rarity.UNCOMMON, mage.cards.w.Warthog.class));
        cards.add(new SetCardInfo("Wild Growth", 268, Rarity.COMMON, mage.cards.w.WildGrowth.class));
        cards.add(new SetCardInfo("Wind Drake", 106, Rarity.COMMON, mage.cards.w.WindDrake.class));
        cards.add(new SetCardInfo("Wind Spirit", 107, Rarity.UNCOMMON, mage.cards.w.WindSpirit.class));
        cards.add(new SetCardInfo("Wooden Sphere", 318, Rarity.UNCOMMON, mage.cards.w.WoodenSphere.class));
        cards.add(new SetCardInfo("Worldly Tutor", 269, Rarity.UNCOMMON, mage.cards.w.WorldlyTutor.class));
        cards.add(new SetCardInfo("Wrath of God", 54, Rarity.RARE, mage.cards.w.WrathOfGod.class));
        cards.add(new SetCardInfo("Wyluli Wolf", 270, Rarity.RARE, mage.cards.w.WyluliWolf.class));
        cards.add(new SetCardInfo("Zombie Master", 162, Rarity.RARE, mage.cards.z.ZombieMaster.class));
        cards.add(new SetCardInfo("Zur's Weirding", 108, Rarity.RARE, mage.cards.z.ZursWeirding.class));
    }
}
