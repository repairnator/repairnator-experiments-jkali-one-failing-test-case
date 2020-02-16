package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 *
 * @author TheElk801
 */
public final class Commander2018 extends ExpansionSet {

    private static final Commander2018 instance = new Commander2018();

    public static Commander2018 getInstance() {
        return instance;
    }

    private Commander2018() {
        super("Commander 2018 Edition", "C18", ExpansionSet.buildDate(2018, 8, 10), SetType.SUPPLEMENTAL);
        this.blockName = "Command Zone";

        cards.add(new SetCardInfo("Acidic Slime", 127, Rarity.UNCOMMON, mage.cards.a.AcidicSlime.class));
        cards.add(new SetCardInfo("Adarkar Valkyrie", 60, Rarity.RARE, mage.cards.a.AdarkarValkyrie.class));
        cards.add(new SetCardInfo("Aether Gale", 80, Rarity.RARE, mage.cards.a.AetherGale.class));
        cards.add(new SetCardInfo("Aethermage's Touch", 168, Rarity.RARE, mage.cards.a.AethermagesTouch.class));
        cards.add(new SetCardInfo("Ajani's Chosen", 61, Rarity.RARE, mage.cards.a.AjanisChosen.class));
        cards.add(new SetCardInfo("Akoum Refuge", 231, Rarity.UNCOMMON, mage.cards.a.AkoumRefuge.class));
        cards.add(new SetCardInfo("Akroma's Vengeance", 62, Rarity.RARE, mage.cards.a.AkromasVengeance.class));
        cards.add(new SetCardInfo("Aminatou's Augury", 6, Rarity.RARE, mage.cards.a.AminatousAugury.class));
        cards.add(new SetCardInfo("Aminatou, the Fateshifter", 37, Rarity.MYTHIC, mage.cards.a.AminatouTheFateShifter.class));
        cards.add(new SetCardInfo("Ancient Stone Idol", 53, Rarity.RARE, mage.cards.a.AncientStoneIdol.class));
        cards.add(new SetCardInfo("Arcane Sanctum", 232, Rarity.UNCOMMON, mage.cards.a.ArcaneSanctum.class));
        cards.add(new SetCardInfo("Archetype of Imagination", 81, Rarity.UNCOMMON, mage.cards.a.ArchetypeOfImagination.class));
        cards.add(new SetCardInfo("Arixmethes, Slumbering Isle", 38, Rarity.RARE, mage.cards.a.ArixmethesSlumberingIsle.class));
        cards.add(new SetCardInfo("Army of the Damned", 113, Rarity.MYTHIC, mage.cards.a.ArmyOfTheDamned.class));
        cards.add(new SetCardInfo("Aura Gnarlid", 128, Rarity.COMMON, mage.cards.a.AuraGnarlid.class));
        cards.add(new SetCardInfo("Avenger of Zendikar", 129, Rarity.MYTHIC, mage.cards.a.AvengerOfZendikar.class));
        cards.add(new SetCardInfo("Azorius Chancery", 233, Rarity.UNCOMMON, mage.cards.a.AzoriusChancery.class));
        cards.add(new SetCardInfo("Azorius Guildgate", 234, Rarity.COMMON, mage.cards.a.AzoriusGuildgate.class));
        cards.add(new SetCardInfo("Azorius Signet", 196, Rarity.COMMON, mage.cards.a.AzoriusSignet.class));
        cards.add(new SetCardInfo("Baloth Woodcrasher", 130, Rarity.UNCOMMON, mage.cards.b.BalothWoodcrasher.class));
        cards.add(new SetCardInfo("Banishing Stroke", 63, Rarity.UNCOMMON, mage.cards.b.BanishingStroke.class));
        cards.add(new SetCardInfo("Bant Charm", 169, Rarity.UNCOMMON, mage.cards.b.BantCharm.class));
        cards.add(new SetCardInfo("Barren Moor", 235, Rarity.COMMON, mage.cards.b.BarrenMoor.class));
        cards.add(new SetCardInfo("Bear Umbra", 131, Rarity.RARE, mage.cards.b.BearUmbra.class));
        cards.add(new SetCardInfo("Blasphemous Act", 120, Rarity.RARE, mage.cards.b.BlasphemousAct.class));
        cards.add(new SetCardInfo("Blighted Woodland", 236, Rarity.UNCOMMON, mage.cards.b.BlightedWoodland.class));
        cards.add(new SetCardInfo("Blinkmoth Urn", 197, Rarity.RARE, mage.cards.b.BlinkmothUrn.class));
        cards.add(new SetCardInfo("Bloodtracker", 14, Rarity.RARE, mage.cards.b.Bloodtracker.class));
        cards.add(new SetCardInfo("Blossoming Sands", 237, Rarity.COMMON, mage.cards.b.BlossomingSands.class));
        cards.add(new SetCardInfo("Bojuka Bog", 238, Rarity.COMMON, mage.cards.b.BojukaBog.class));
        cards.add(new SetCardInfo("Boon Satyr", 132, Rarity.RARE, mage.cards.b.BoonSatyr.class));
        cards.add(new SetCardInfo("Borderland Explorer", 133, Rarity.COMMON, mage.cards.b.BorderlandExplorer.class));
        cards.add(new SetCardInfo("Boreas Charger", 1, Rarity.RARE, mage.cards.b.BoreasCharger.class));
        cards.add(new SetCardInfo("Bosh, Iron Golem", 198, Rarity.RARE, mage.cards.b.BoshIronGolem.class));
        cards.add(new SetCardInfo("Brainstorm", 82, Rarity.UNCOMMON, mage.cards.b.Brainstorm.class));
        cards.add(new SetCardInfo("Brudiclad, Telchor Engineer", 39, Rarity.MYTHIC, mage.cards.b.BrudicladTelchorEngineer.class));
        cards.add(new SetCardInfo("Bruna, Light of Alabaster", 170, Rarity.MYTHIC, mage.cards.b.BrunaLightOfAlabaster.class));
        cards.add(new SetCardInfo("Budoka Gardener", 134, Rarity.RARE, mage.cards.b.BudokaGardener.class));
        cards.add(new SetCardInfo("Buried Ruin", 239, Rarity.UNCOMMON, mage.cards.b.BuriedRuin.class));
        cards.add(new SetCardInfo("Celestial Archon", 64, Rarity.RARE, mage.cards.c.CelestialArchon.class));
        cards.add(new SetCardInfo("Centaur Vinecrasher", 135, Rarity.RARE, mage.cards.c.CentaurVinecrasher.class));
        cards.add(new SetCardInfo("Chain Reaction", 121, Rarity.RARE, mage.cards.c.ChainReaction.class));
        cards.add(new SetCardInfo("Chaos Warp", 122, Rarity.RARE, mage.cards.c.ChaosWarp.class));
        cards.add(new SetCardInfo("Charnelhoard Wurm", 171, Rarity.RARE, mage.cards.c.CharnelhoardWurm.class));
        cards.add(new SetCardInfo("Chief of the Foundry", 199, Rarity.UNCOMMON, mage.cards.c.ChiefOfTheFoundry.class));
        cards.add(new SetCardInfo("Cloudform", 83, Rarity.UNCOMMON, mage.cards.c.Cloudform.class));
        cards.add(new SetCardInfo("Cold-Eyed Selkie", 172, Rarity.RARE, mage.cards.c.ColdEyedSelkie.class));
        cards.add(new SetCardInfo("Command Tower", 240, Rarity.COMMON, mage.cards.c.CommandTower.class));
        cards.add(new SetCardInfo("Commander's Sphere", 200, Rarity.COMMON, mage.cards.c.CommandersSphere.class));
        cards.add(new SetCardInfo("Consign to Dust", 136, Rarity.UNCOMMON, mage.cards.c.ConsignToDust.class));
        cards.add(new SetCardInfo("Conundrum Sphinx", 84, Rarity.RARE, mage.cards.c.ConundrumSphinx.class));
        cards.add(new SetCardInfo("Coveted Jewel", 54, Rarity.RARE, mage.cards.c.CovetedJewel.class));
        cards.add(new SetCardInfo("Crash of Rhino Beetles", 29, Rarity.RARE, mage.cards.c.CrashOfRhinoBeetles.class));
        cards.add(new SetCardInfo("Creeping Renaissance", 137, Rarity.RARE, mage.cards.c.CreepingRenaissance.class));
        cards.add(new SetCardInfo("Crib Swap", 65, Rarity.UNCOMMON, mage.cards.c.CribSwap.class));
        cards.add(new SetCardInfo("Crystal Ball", 201, Rarity.UNCOMMON, mage.cards.c.CrystalBall.class));
        cards.add(new SetCardInfo("Cultivate", 138, Rarity.COMMON, mage.cards.c.Cultivate.class));
        cards.add(new SetCardInfo("Darksteel Citadel", 241, Rarity.UNCOMMON, mage.cards.d.DarksteelCitadel.class));
        cards.add(new SetCardInfo("Darksteel Juggernaut", 202, Rarity.RARE, mage.cards.d.DarksteelJuggernaut.class));
        cards.add(new SetCardInfo("Dawn's Reflection", 139, Rarity.COMMON, mage.cards.d.DawnsReflection.class));
        cards.add(new SetCardInfo("Daxos of Meletis", 173, Rarity.RARE, mage.cards.d.DaxosOfMeletis.class));
        cards.add(new SetCardInfo("Deathreap Ritual", 174, Rarity.UNCOMMON, mage.cards.d.DeathreapRitual.class));
        cards.add(new SetCardInfo("Decimate", 175, Rarity.RARE, mage.cards.d.Decimate.class));
        cards.add(new SetCardInfo("Devastation Tide", 85, Rarity.RARE, mage.cards.d.DevastationTide.class));
        cards.add(new SetCardInfo("Dictate of Kruphix", 86, Rarity.RARE, mage.cards.d.DictateOfKruphix.class));
        cards.add(new SetCardInfo("Dimir Aqueduct", 242, Rarity.UNCOMMON, mage.cards.d.DimirAqueduct.class));
        cards.add(new SetCardInfo("Dimir Guildgate", 243, Rarity.COMMON, mage.cards.d.DimirGuildgate.class));
        cards.add(new SetCardInfo("Dimir Signet", 203, Rarity.COMMON, mage.cards.d.DimirSignet.class));
        cards.add(new SetCardInfo("Dismal Backwater", 244, Rarity.COMMON, mage.cards.d.DismalBackwater.class));
        cards.add(new SetCardInfo("Dismantling Blow", 66, Rarity.COMMON, mage.cards.d.DismantlingBlow.class));
        cards.add(new SetCardInfo("Djinn of Wishes", 87, Rarity.RARE, mage.cards.d.DjinnOfWishes.class));
        cards.add(new SetCardInfo("Dream Cache", 88, Rarity.COMMON, mage.cards.d.DreamCache.class));
        cards.add(new SetCardInfo("Dreamstone Hedron", 204, Rarity.UNCOMMON, mage.cards.d.DreamstoneHedron.class));
        cards.add(new SetCardInfo("Duplicant", 205, Rarity.RARE, mage.cards.d.Duplicant.class));
        cards.add(new SetCardInfo("Duskmantle Seer", 176, Rarity.RARE, mage.cards.d.DuskmantleSeer.class));
        cards.add(new SetCardInfo("Echo Storm", 7, Rarity.RARE, mage.cards.e.EchoStorm.class));
        cards.add(new SetCardInfo("Eel Umbra", 89, Rarity.COMMON, mage.cards.e.EelUmbra.class));
        cards.add(new SetCardInfo("Eidolon of Blossoms", 140, Rarity.RARE, mage.cards.e.EidolonOfBlossoms.class));
        cards.add(new SetCardInfo("Elderwood Scion", 177, Rarity.RARE, mage.cards.e.ElderwoodScion.class));
        cards.add(new SetCardInfo("Emissary of Grudges", 20, Rarity.RARE, mage.cards.e.EmissaryOfGrudges.class));
        cards.add(new SetCardInfo("Empyrial Storm", 2, Rarity.RARE, mage.cards.e.EmpyrialStorm.class));
        cards.add(new SetCardInfo("Enchanter's Bane", 21, Rarity.RARE, mage.cards.e.EnchantersBane.class));
        cards.add(new SetCardInfo("Enchantress's Presence", 141, Rarity.RARE, mage.cards.e.EnchantresssPresence.class));
        cards.add(new SetCardInfo("Endless Atlas", 55, Rarity.RARE, mage.cards.e.EndlessAtlas.class));
        cards.add(new SetCardInfo("Enigma Sphinx", 178, Rarity.RARE, mage.cards.e.EnigmaSphinx.class));
        cards.add(new SetCardInfo("Entreat the Angels", 67, Rarity.MYTHIC, mage.cards.e.EntreatTheAngels.class));
        cards.add(new SetCardInfo("Entreat the Dead", 15, Rarity.RARE, mage.cards.e.EntreatTheDead.class));
        cards.add(new SetCardInfo("Epic Proportions", 142, Rarity.RARE, mage.cards.e.EpicProportions.class));
        cards.add(new SetCardInfo("Esper Charm", 179, Rarity.UNCOMMON, mage.cards.e.EsperCharm.class));
        cards.add(new SetCardInfo("Estrid's Invocation", 8, Rarity.RARE, mage.cards.e.EstridsInvocation.class));
        cards.add(new SetCardInfo("Estrid, the Masked", 40, Rarity.MYTHIC, mage.cards.e.EstridTheMasked.class));
        cards.add(new SetCardInfo("Etherium Sculptor", 90, Rarity.COMMON, mage.cards.e.EtheriumSculptor.class));
        cards.add(new SetCardInfo("Ever-Watching Threshold", 9, Rarity.RARE, mage.cards.e.EverWatchingThreshold.class));
        cards.add(new SetCardInfo("Evolving Wilds", 245, Rarity.COMMON, mage.cards.e.EvolvingWilds.class));
        cards.add(new SetCardInfo("Explore", 143, Rarity.COMMON, mage.cards.e.Explore.class));
        cards.add(new SetCardInfo("Explosive Vegetation", 144, Rarity.UNCOMMON, mage.cards.e.ExplosiveVegetation.class));
        cards.add(new SetCardInfo("Far Wanderings", 145, Rarity.COMMON, mage.cards.f.FarWanderings.class));
        cards.add(new SetCardInfo("Farhaven Elf", 146, Rarity.COMMON, mage.cards.f.FarhavenElf.class));
        cards.add(new SetCardInfo("Fertile Ground", 147, Rarity.COMMON, mage.cards.f.FertileGround.class));
        cards.add(new SetCardInfo("Finest Hour", 180, Rarity.RARE, mage.cards.f.FinestHour.class));
        cards.add(new SetCardInfo("Flameblast Dragon", 123, Rarity.RARE, mage.cards.f.FlameblastDragon.class));
        cards.add(new SetCardInfo("Forest", 305, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 306, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 307, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forge of Heroes", 58, Rarity.COMMON, mage.cards.f.ForgeOfHeroes.class));
        cards.add(new SetCardInfo("Forgotten Cave", 246, Rarity.COMMON, mage.cards.f.ForgottenCave.class));
        cards.add(new SetCardInfo("Forsaken Sanctuary", 247, Rarity.UNCOMMON, mage.cards.f.ForsakenSanctuary.class));
        cards.add(new SetCardInfo("Foundry of the Consuls", 248, Rarity.UNCOMMON, mage.cards.f.FoundryOfTheConsuls.class));
        cards.add(new SetCardInfo("Fury Storm", 22, Rarity.RARE, mage.cards.f.FuryStorm.class));
        cards.add(new SetCardInfo("Gaze of Granite", 181, Rarity.RARE, mage.cards.g.GazeOfGranite.class));
        cards.add(new SetCardInfo("Genesis Storm", 30, Rarity.RARE, mage.cards.g.GenesisStorm.class));
        cards.add(new SetCardInfo("Geode Golem", 56, Rarity.UNCOMMON, mage.cards.g.GeodeGolem.class));
        cards.add(new SetCardInfo("Golgari Rot Farm", 249, Rarity.UNCOMMON, mage.cards.g.GolgariRotFarm.class));
        cards.add(new SetCardInfo("Grapple with the Past", 148, Rarity.COMMON, mage.cards.g.GrappleWithThePast.class));
        cards.add(new SetCardInfo("Great Furnace", 250, Rarity.COMMON, mage.cards.g.GreatFurnace.class));
        cards.add(new SetCardInfo("Grim Backwoods", 251, Rarity.RARE, mage.cards.g.GrimBackwoods.class));
        cards.add(new SetCardInfo("Grisly Salvage", 182, Rarity.COMMON, mage.cards.g.GrislySalvage.class));
        cards.add(new SetCardInfo("Ground Seal", 149, Rarity.RARE, mage.cards.g.GroundSeal.class));
        cards.add(new SetCardInfo("Gruul Turf", 252, Rarity.UNCOMMON, mage.cards.g.GruulTurf.class));
        cards.add(new SetCardInfo("Gyrus, Waker of Corpses", 41, Rarity.MYTHIC, mage.cards.g.GyrusWakerOfCorpses.class));
        cards.add(new SetCardInfo("Halimar Depths", 253, Rarity.COMMON, mage.cards.h.HalimarDepths.class));
        cards.add(new SetCardInfo("Harrow", 150, Rarity.COMMON, mage.cards.h.Harrow.class));
        cards.add(new SetCardInfo("Haunted Fengraf", 254, Rarity.COMMON, mage.cards.h.HauntedFengraf.class));
        cards.add(new SetCardInfo("Heavenly Blademaster", 3, Rarity.RARE, mage.cards.h.HeavenlyBlademaster.class));
        cards.add(new SetCardInfo("Hedron Archive", 206, Rarity.UNCOMMON, mage.cards.h.HedronArchive.class));
        cards.add(new SetCardInfo("Hellkite Igniter", 124, Rarity.RARE, mage.cards.h.HellkiteIgniter.class));
        cards.add(new SetCardInfo("Herald of the Pantheon", 151, Rarity.RARE, mage.cards.h.HeraldOfThePantheon.class));
        cards.add(new SetCardInfo("High Priest of Penance", 183, Rarity.RARE, mage.cards.h.HighPriestOfPenance.class));
        cards.add(new SetCardInfo("Highland Lake", 255, Rarity.UNCOMMON, mage.cards.h.HighlandLake.class));
        cards.add(new SetCardInfo("Hunting Wilds", 152, Rarity.UNCOMMON, mage.cards.h.HuntingWilds.class));
        cards.add(new SetCardInfo("Hydra Omnivore", 153, Rarity.MYTHIC, mage.cards.h.HydraOmnivore.class));
        cards.add(new SetCardInfo("Inkwell Leviathan", 91, Rarity.RARE, mage.cards.i.InkwellLeviathan.class));
        cards.add(new SetCardInfo("Into the Roil", 92, Rarity.COMMON, mage.cards.i.IntoTheRoil.class));
        cards.add(new SetCardInfo("Island", 296, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Island", 297, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Island", 298, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Isolated Watchtower", 59, Rarity.RARE, mage.cards.i.IsolatedWatchtower.class));
        cards.add(new SetCardInfo("Izzet Boilerworks", 256, Rarity.UNCOMMON, mage.cards.i.IzzetBoilerworks.class));
        cards.add(new SetCardInfo("Izzet Guildgate", 257, Rarity.COMMON, mage.cards.i.IzzetGuildgate.class));
        cards.add(new SetCardInfo("Izzet Signet", 207, Rarity.COMMON, mage.cards.i.IzzetSignet.class));
        cards.add(new SetCardInfo("Jeskai Infiltrator", 93, Rarity.RARE, mage.cards.j.JeskaiInfiltrator.class));
        cards.add(new SetCardInfo("Jund Panorama", 258, Rarity.COMMON, mage.cards.j.JundPanorama.class));
        cards.add(new SetCardInfo("Jungle Hollow", 259, Rarity.COMMON, mage.cards.j.JungleHollow.class));
        cards.add(new SetCardInfo("Jwar Isle Refuge", 260, Rarity.UNCOMMON, mage.cards.j.JwarIsleRefuge.class));
        cards.add(new SetCardInfo("Kazandu Refuge", 261, Rarity.UNCOMMON, mage.cards.k.KazanduRefuge.class));
        cards.add(new SetCardInfo("Kestia, the Cultivator", 42, Rarity.MYTHIC, mage.cards.k.KestiaTheCultivator.class));
        cards.add(new SetCardInfo("Khalni Garden", 262, Rarity.COMMON, mage.cards.k.KhalniGarden.class));
        cards.add(new SetCardInfo("Khalni Heart Expedition", 154, Rarity.COMMON, mage.cards.k.KhalniHeartExpedition.class));
        cards.add(new SetCardInfo("Krosan Verge", 263, Rarity.UNCOMMON, mage.cards.k.KrosanVerge.class));
        cards.add(new SetCardInfo("Kruphix's Insight", 155, Rarity.COMMON, mage.cards.k.KruphixsInsight.class));
        cards.add(new SetCardInfo("Lavalanche", 184, Rarity.RARE, mage.cards.l.Lavalanche.class));
        cards.add(new SetCardInfo("Lightform", 68, Rarity.UNCOMMON, mage.cards.l.Lightform.class));
        cards.add(new SetCardInfo("Lonely Sandbar", 264, Rarity.COMMON, mage.cards.l.LonelySandbar.class));
        cards.add(new SetCardInfo("Lord Windgrace", 43, Rarity.MYTHIC, mage.cards.l.LordWindgrace.class));
        cards.add(new SetCardInfo("Loyal Apprentice", 23, Rarity.UNCOMMON, mage.cards.l.LoyalApprentice.class));
        cards.add(new SetCardInfo("Loyal Drake", 10, Rarity.UNCOMMON, mage.cards.l.LoyalDrake.class));
        cards.add(new SetCardInfo("Loyal Guardian", 31, Rarity.UNCOMMON, mage.cards.l.LoyalGuardian.class));
        cards.add(new SetCardInfo("Loyal Subordinate", 16, Rarity.UNCOMMON, mage.cards.l.LoyalSubordinate.class));
        cards.add(new SetCardInfo("Loyal Unicorn", 4, Rarity.UNCOMMON, mage.cards.l.LoyalUnicorn.class));
        cards.add(new SetCardInfo("Magmaquake", 125, Rarity.RARE, mage.cards.m.Magmaquake.class));
        cards.add(new SetCardInfo("Magnifying Glass", 208, Rarity.UNCOMMON, mage.cards.m.MagnifyingGlass.class));
        cards.add(new SetCardInfo("Magus of the Balance", 5, Rarity.RARE, mage.cards.m.MagusOfTheBalance.class));
        cards.add(new SetCardInfo("Martial Coup", 69, Rarity.RARE, mage.cards.m.MartialCoup.class));
        cards.add(new SetCardInfo("Maverick Thopterist", 185, Rarity.UNCOMMON, mage.cards.m.MaverickThopterist.class));
        cards.add(new SetCardInfo("Meandering River", 265, Rarity.UNCOMMON, mage.cards.m.MeanderingRiver.class));
        cards.add(new SetCardInfo("Mimic Vat", 209, Rarity.RARE, mage.cards.m.MimicVat.class));
        cards.add(new SetCardInfo("Mind Stone", 210, Rarity.COMMON, mage.cards.m.MindStone.class));
        cards.add(new SetCardInfo("Mirrorworks", 211, Rarity.RARE, mage.cards.m.Mirrorworks.class));
        cards.add(new SetCardInfo("Moldgraf Monstrosity", 156, Rarity.RARE, mage.cards.m.MoldgrafMonstrosity.class));
        cards.add(new SetCardInfo("Moonlight Bargain", 114, Rarity.RARE, mage.cards.m.MoonlightBargain.class));
        cards.add(new SetCardInfo("Mortify", 186, Rarity.UNCOMMON, mage.cards.m.Mortify.class));
        cards.add(new SetCardInfo("Mortuary Mire", 266, Rarity.COMMON, mage.cards.m.MortuaryMire.class));
        cards.add(new SetCardInfo("Mosswort Bridge", 267, Rarity.RARE, mage.cards.m.MosswortBridge.class));
        cards.add(new SetCardInfo("Mountain Valley", 268, Rarity.UNCOMMON, mage.cards.m.MountainValley.class));
        cards.add(new SetCardInfo("Mountain", 302, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mountain", 303, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mountain", 304, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mulldrifter", 94, Rarity.UNCOMMON, mage.cards.m.Mulldrifter.class));
        cards.add(new SetCardInfo("Myr Battlesphere", 212, Rarity.RARE, mage.cards.m.MyrBattlesphere.class));
        cards.add(new SetCardInfo("Myriad Landscape", 269, Rarity.UNCOMMON, mage.cards.m.MyriadLandscape.class));
        cards.add(new SetCardInfo("Myth Unbound", 32, Rarity.RARE, mage.cards.m.MythUnbound.class));
        cards.add(new SetCardInfo("Nesting Dragon", 24, Rarity.RARE, mage.cards.n.NestingDragon.class));
        cards.add(new SetCardInfo("New Benalia", 270, Rarity.UNCOMMON, mage.cards.n.NewBenalia.class));
        cards.add(new SetCardInfo("Night Incarnate", 17, Rarity.RARE, mage.cards.n.NightIncarnate.class));
        cards.add(new SetCardInfo("Ninja of the Deep Hours", 95, Rarity.COMMON, mage.cards.n.NinjaOfTheDeepHours.class));
        cards.add(new SetCardInfo("Nylea's Colossus", 33, Rarity.RARE, mage.cards.n.NyleasColossus.class));
        cards.add(new SetCardInfo("Octopus Umbra", 11, Rarity.RARE, mage.cards.o.OctopusUmbra.class));
        cards.add(new SetCardInfo("Orzhov Basilica", 271, Rarity.UNCOMMON, mage.cards.o.OrzhovBasilica.class));
        cards.add(new SetCardInfo("Orzhov Guildgate", 272, Rarity.COMMON, mage.cards.o.OrzhovGuildgate.class));
        cards.add(new SetCardInfo("Orzhov Signet", 213, Rarity.COMMON, mage.cards.o.OrzhovSignet.class));
        cards.add(new SetCardInfo("Overgrowth", 157, Rarity.COMMON, mage.cards.o.Overgrowth.class));
        cards.add(new SetCardInfo("Phyrexian Delver", 115, Rarity.RARE, mage.cards.p.PhyrexianDelver.class));
        cards.add(new SetCardInfo("Phyrexian Rebirth", 70, Rarity.RARE, mage.cards.p.PhyrexianRebirth.class));
        cards.add(new SetCardInfo("Pilgrim's Eye", 214, Rarity.COMMON, mage.cards.p.PilgrimsEye.class));
        cards.add(new SetCardInfo("Plains", 293, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Plains", 294, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Plains", 295, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Ponder", 96, Rarity.COMMON, mage.cards.p.Ponder.class));
        cards.add(new SetCardInfo("Portent", 97, Rarity.COMMON, mage.cards.p.Portent.class));
        cards.add(new SetCardInfo("Predict", 98, Rarity.UNCOMMON, mage.cards.p.Predict.class));
        cards.add(new SetCardInfo("Primordial Mist", 12, Rarity.RARE, mage.cards.p.PrimordialMist.class));
        cards.add(new SetCardInfo("Prismatic Lens", 215, Rarity.UNCOMMON, mage.cards.p.PrismaticLens.class));
        cards.add(new SetCardInfo("Prototype Portal", 216, Rarity.RARE, mage.cards.p.PrototypePortal.class));
        cards.add(new SetCardInfo("Psychosis Crawler", 217, Rarity.RARE, mage.cards.p.PsychosisCrawler.class));
        cards.add(new SetCardInfo("Putrefy", 187, Rarity.UNCOMMON, mage.cards.p.Putrefy.class));
        cards.add(new SetCardInfo("Rakdos Carnarium", 273, Rarity.COMMON, mage.cards.r.RakdosCarnarium.class));
        cards.add(new SetCardInfo("Rampaging Baloths", 158, Rarity.RARE, mage.cards.r.RampagingBaloths.class));
        cards.add(new SetCardInfo("Ravenous Slime", 34, Rarity.RARE, mage.cards.r.RavenousSlime.class));
        cards.add(new SetCardInfo("Reality Scramble", 25, Rarity.RARE, mage.cards.r.RealityScramble.class));
        cards.add(new SetCardInfo("Reclamation Sage", 159, Rarity.UNCOMMON, mage.cards.r.ReclamationSage.class));
        cards.add(new SetCardInfo("Retreat to Hagra", 116, Rarity.UNCOMMON, mage.cards.r.RetreatToHagra.class));
        cards.add(new SetCardInfo("Retrofitter Foundry", 57, Rarity.RARE, mage.cards.r.RetrofitterFoundry.class));
        cards.add(new SetCardInfo("Return to Dust", 71, Rarity.UNCOMMON, mage.cards.r.ReturnToDust.class));
        cards.add(new SetCardInfo("Reverse Engineer", 99, Rarity.UNCOMMON, mage.cards.r.ReverseEngineer.class));
        cards.add(new SetCardInfo("Righteous Authority", 188, Rarity.RARE, mage.cards.r.RighteousAuthority.class));
        cards.add(new SetCardInfo("Rocky Tar Pit", 274, Rarity.UNCOMMON, mage.cards.r.RockyTarPit.class));
        cards.add(new SetCardInfo("Rubblehulk", 189, Rarity.RARE, mage.cards.r.Rubblehulk.class));
        cards.add(new SetCardInfo("Ruinous Path", 117, Rarity.RARE, mage.cards.r.RuinousPath.class));
        cards.add(new SetCardInfo("Sage's Reverie", 72, Rarity.UNCOMMON, mage.cards.s.SagesReverie.class));
        cards.add(new SetCardInfo("Saheeli's Artistry", 100, Rarity.RARE, mage.cards.s.SaheelisArtistry.class));
        cards.add(new SetCardInfo("Saheeli's Directive", 26, Rarity.RARE, mage.cards.s.SaheelisDirective.class));
        cards.add(new SetCardInfo("Saheeli, the Gifted", 44, Rarity.MYTHIC, mage.cards.s.SaheeliTheGifted.class));
        cards.add(new SetCardInfo("Sakura-Tribe Elder", 160, Rarity.COMMON, mage.cards.s.SakuraTribeElder.class));
        cards.add(new SetCardInfo("Savage Lands", 275, Rarity.UNCOMMON, mage.cards.s.SavageLands.class));
        cards.add(new SetCardInfo("Savage Twister", 190, Rarity.UNCOMMON, mage.cards.s.SavageTwister.class));
        cards.add(new SetCardInfo("Scoured Barrens", 276, Rarity.COMMON, mage.cards.s.ScouredBarrens.class));
        cards.add(new SetCardInfo("Scrabbling Claws", 218, Rarity.UNCOMMON, mage.cards.s.ScrabblingClaws.class));
        cards.add(new SetCardInfo("Scute Mob", 161, Rarity.RARE, mage.cards.s.ScuteMob.class));
        cards.add(new SetCardInfo("Scuttling Doom Engine", 219, Rarity.RARE, mage.cards.s.ScuttlingDoomEngine.class));
        cards.add(new SetCardInfo("Seaside Citadel", 277, Rarity.UNCOMMON, mage.cards.s.SeasideCitadel.class));
        cards.add(new SetCardInfo("Seat of the Synod", 278, Rarity.COMMON, mage.cards.s.SeatOfTheSynod.class));
        cards.add(new SetCardInfo("Secluded Steppe", 279, Rarity.COMMON, mage.cards.s.SecludedSteppe.class));
        cards.add(new SetCardInfo("Seer's Lantern", 220, Rarity.COMMON, mage.cards.s.SeersLantern.class));
        cards.add(new SetCardInfo("Seer's Sundial", 221, Rarity.RARE, mage.cards.s.SeersSundial.class));
        cards.add(new SetCardInfo("Sejiri Refuge", 280, Rarity.UNCOMMON, mage.cards.s.SejiriRefuge.class));
        cards.add(new SetCardInfo("Selesnya Sanctuary", 281, Rarity.COMMON, mage.cards.s.SelesnyaSanctuary.class));
        cards.add(new SetCardInfo("Serra Avatar", 73, Rarity.MYTHIC, mage.cards.s.SerraAvatar.class));
        cards.add(new SetCardInfo("Sharding Sphinx", 101, Rarity.RARE, mage.cards.s.ShardingSphinx.class));
        cards.add(new SetCardInfo("Sigil of the Empty Throne", 74, Rarity.RARE, mage.cards.s.SigilOfTheEmptyThrone.class));
        cards.add(new SetCardInfo("Sigiled Starfish", 102, Rarity.COMMON, mage.cards.s.SigiledStarfish.class));
        cards.add(new SetCardInfo("Silent Sentinel", 75, Rarity.RARE, mage.cards.s.SilentSentinel.class));
        cards.add(new SetCardInfo("Silent-Blade Oni", 191, Rarity.RARE, mage.cards.s.SilentBladeOni.class));
        cards.add(new SetCardInfo("Simic Growth Chamber", 282, Rarity.UNCOMMON, mage.cards.s.SimicGrowthChamber.class));
        cards.add(new SetCardInfo("Skull Storm", 18, Rarity.RARE, mage.cards.s.SkullStorm.class));
        cards.add(new SetCardInfo("Snake Umbra", 162, Rarity.COMMON, mage.cards.s.SnakeUmbra.class));
        cards.add(new SetCardInfo("Sol Ring", 222, Rarity.UNCOMMON, mage.cards.s.SolRing.class));
        cards.add(new SetCardInfo("Soul Snare", 76, Rarity.UNCOMMON, mage.cards.s.SoulSnare.class));
        cards.add(new SetCardInfo("Soul of Innistrad", 118, Rarity.MYTHIC, mage.cards.s.SoulOfInnistrad.class));
        cards.add(new SetCardInfo("Soul of New Phyrexia", 223, Rarity.MYTHIC, mage.cards.s.SoulOfNewPhyrexia.class));
        cards.add(new SetCardInfo("Sower of Discord", 19, Rarity.RARE, mage.cards.s.SowerOfDiscord.class));
        cards.add(new SetCardInfo("Spawning Grounds", 163, Rarity.RARE, mage.cards.s.SpawningGrounds.class));
        cards.add(new SetCardInfo("Sphinx of Jwar Isle", 103, Rarity.RARE, mage.cards.s.SphinxOfJwarIsle.class));
        cards.add(new SetCardInfo("Sphinx of Uthuun", 104, Rarity.RARE, mage.cards.s.SphinxOfUthuun.class));
        cards.add(new SetCardInfo("Steel Hellkite", 224, Rarity.RARE, mage.cards.s.SteelHellkite.class));
        cards.add(new SetCardInfo("Stitch Together", 119, Rarity.UNCOMMON, mage.cards.s.StitchTogether.class));
        cards.add(new SetCardInfo("Submerged Boneyard", 283, Rarity.UNCOMMON, mage.cards.s.SubmergedBoneyard.class));
        cards.add(new SetCardInfo("Swamp", 299, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swamp", 300, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swamp", 301, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swiftfoot Boots", 225, Rarity.UNCOMMON, mage.cards.s.SwiftfootBoots.class));
        cards.add(new SetCardInfo("Swiftwater Cliffs", 284, Rarity.COMMON, mage.cards.s.SwiftwaterCliffs.class));
        cards.add(new SetCardInfo("Tawnos, Urza's Apprentice", 45, Rarity.MYTHIC, mage.cards.t.TawnosUrzasApprentice.class));
        cards.add(new SetCardInfo("Telling Time", 105, Rarity.COMMON, mage.cards.t.TellingTime.class));
        cards.add(new SetCardInfo("Temple of the False God", 285, Rarity.UNCOMMON, mage.cards.t.TempleOfTheFalseGod.class));
        cards.add(new SetCardInfo("Terminus", 77, Rarity.RARE, mage.cards.t.Terminus.class));
        cards.add(new SetCardInfo("Terramorphic Expanse", 286, Rarity.COMMON, mage.cards.t.TerramorphicExpanse.class));
        cards.add(new SetCardInfo("Thantis, the Warweaver", 46, Rarity.MYTHIC, mage.cards.t.ThantisTheWarweaver.class));
        cards.add(new SetCardInfo("Thirst for Knowledge", 106, Rarity.UNCOMMON, mage.cards.t.ThirstForKnowledge.class));
        cards.add(new SetCardInfo("Thopter Assembly", 226, Rarity.RARE, mage.cards.t.ThopterAssembly.class));
        cards.add(new SetCardInfo("Thopter Engineer", 126, Rarity.UNCOMMON, mage.cards.t.ThopterEngineer.class));
        cards.add(new SetCardInfo("Thopter Spy Network", 107, Rarity.RARE, mage.cards.t.ThopterSpyNetwork.class));
        cards.add(new SetCardInfo("Thornwood Falls", 287, Rarity.COMMON, mage.cards.t.ThornwoodFalls.class));
        cards.add(new SetCardInfo("Tidings", 108, Rarity.UNCOMMON, mage.cards.t.Tidings.class));
        cards.add(new SetCardInfo("Tranquil Cove", 288, Rarity.COMMON, mage.cards.t.TranquilCove.class));
        cards.add(new SetCardInfo("Tranquil Expanse", 289, Rarity.UNCOMMON, mage.cards.t.TranquilExpanse.class));
        cards.add(new SetCardInfo("Tranquil Thicket", 290, Rarity.COMMON, mage.cards.t.TranquilThicket.class));
        cards.add(new SetCardInfo("Treasure Hunt", 109, Rarity.COMMON, mage.cards.t.TreasureHunt.class));
        cards.add(new SetCardInfo("Treasure Nabber", 27, Rarity.RARE, mage.cards.t.TreasureNabber.class));
        cards.add(new SetCardInfo("Turntimber Sower", 35, Rarity.RARE, mage.cards.t.TurntimberSower.class));
        cards.add(new SetCardInfo("Tuvasa the Sunlit", 47, Rarity.MYTHIC, mage.cards.t.TuvasaTheSunlit.class));
        cards.add(new SetCardInfo("Unflinching Courage", 192, Rarity.UNCOMMON, mage.cards.u.UnflinchingCourage.class));
        cards.add(new SetCardInfo("Unquestioned Authority", 78, Rarity.UNCOMMON, mage.cards.u.UnquestionedAuthority.class));
        cards.add(new SetCardInfo("Unstable Obelisk", 227, Rarity.UNCOMMON, mage.cards.u.UnstableObelisk.class));
        cards.add(new SetCardInfo("Unwinding Clock", 228, Rarity.RARE, mage.cards.u.UnwindingClock.class));
        cards.add(new SetCardInfo("Utter End", 193, Rarity.RARE, mage.cards.u.UtterEnd.class));
        cards.add(new SetCardInfo("Varchild, Betrayer of Kjeldor", 28, Rarity.RARE, mage.cards.v.VarchildBetrayerOfKjeldor.class));
        cards.add(new SetCardInfo("Varina, Lich Queen", 48, Rarity.MYTHIC, mage.cards.v.VarinaLichQueen.class));
        cards.add(new SetCardInfo("Vedalken Humiliator", 13, Rarity.RARE, mage.cards.v.VedalkenHumiliator.class));
        cards.add(new SetCardInfo("Vessel of Endless Rest", 229, Rarity.UNCOMMON, mage.cards.v.VesselOfEndlessRest.class));
        cards.add(new SetCardInfo("Vow of Flight", 110, Rarity.UNCOMMON, mage.cards.v.VowOfFlight.class));
        cards.add(new SetCardInfo("Vow of Wildness", 164, Rarity.UNCOMMON, mage.cards.v.VowOfWildness.class));
        cards.add(new SetCardInfo("Warped Landscape", 291, Rarity.COMMON, mage.cards.w.WarpedLandscape.class));
        cards.add(new SetCardInfo("Whiptongue Hydra", 36, Rarity.RARE, mage.cards.w.WhiptongueHydra.class));
        cards.add(new SetCardInfo("Whirler Rogue", 111, Rarity.UNCOMMON, mage.cards.w.WhirlerRogue.class));
        cards.add(new SetCardInfo("Whitewater Naiads", 112, Rarity.UNCOMMON, mage.cards.w.WhitewaterNaiads.class));
        cards.add(new SetCardInfo("Wild Growth", 165, Rarity.COMMON, mage.cards.w.WildGrowth.class));
        cards.add(new SetCardInfo("Windgrace's Judgment", 49, Rarity.RARE, mage.cards.w.WindgracesJudgment.class));
        cards.add(new SetCardInfo("Winds of Rath", 79, Rarity.RARE, mage.cards.w.WindsOfRath.class));
        cards.add(new SetCardInfo("Woodland Stream", 292, Rarity.COMMON, mage.cards.w.WoodlandStream.class));
        cards.add(new SetCardInfo("Worm Harvest", 194, Rarity.RARE, mage.cards.w.WormHarvest.class));
        cards.add(new SetCardInfo("Worn Powerstone", 230, Rarity.UNCOMMON, mage.cards.w.WornPowerstone.class));
        cards.add(new SetCardInfo("Xantcha, Sleeper Agent", 50, Rarity.RARE, mage.cards.x.XantchaSleeperAgent.class));
        cards.add(new SetCardInfo("Yavimaya Elder", 166, Rarity.COMMON, mage.cards.y.YavimayaElder.class));
        cards.add(new SetCardInfo("Yavimaya Enchantress", 167, Rarity.COMMON, mage.cards.y.YavimayaEnchantress.class));
        cards.add(new SetCardInfo("Yennett, Cryptic Sovereign", 51, Rarity.MYTHIC, mage.cards.y.YennetCryptSovereign.class));
        cards.add(new SetCardInfo("Yuriko, the Tiger's Shadow", 52, Rarity.RARE, mage.cards.y.YurikoTheTigersShadow.class));
        cards.add(new SetCardInfo("Zendikar Incarnate", 195, Rarity.UNCOMMON, mage.cards.z.ZendikarIncarnate.class));
    }
}
