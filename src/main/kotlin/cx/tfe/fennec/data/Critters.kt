package cx.tfe.fennec.data

enum class Critters(val gameName: String, val biome: Biomes, val instanceAmount: IntRange) {
    /**
     * Cavern Biome
     */
    CAVERNFISH("Cavernfish", Biomes.CAVERN, 4..8),
    FLITTER("Flitter", Biomes.CAVERN, 6..8),
    SHYWORM("Shyworm", Biomes.CAVERN, 4..8),
    DRIFTLING("Driftling", Biomes.CAVERN, 3..6),
    CHUCKWALLA("Chuckwalla", Biomes.CAVERN, 2..4),
    ROCKMITE("Rockmite", Biomes.CAVERN, 0..20),
    SCRAPPY("Scrappy", Biomes.CAVERN, 3..3),
    SNOOZLE("Snoozle", Biomes.CAVERN, 0..5),
    GEMZIE("Gemzie", Biomes.CAVERN, 3..3),

    /**
     * Forest Biome
     */
    FOXTROT("Foxtrot", Biomes.FOREST, 6..8),
    BLUEBIRD("Bluebird", Biomes.FOREST, 0..0),
    HONEYBUG("Honeybug", Biomes.FOREST, 3..6),
    TREEFROG("Treefrog", Biomes.FOREST, 3..6),
    WOODCHUCKER("Woodchucker", Biomes.FOREST, 3..6),
    FLUFFLING("Fluffling", Biomes.FOREST, 1..3),
    HIDEONFLOOR("Hideonfloor", Biomes.FOREST, 1..3),
    PARAKEET("Parakeet", Biomes.FOREST, 0..0),
    MACAW("Macaw", Biomes.FOREST, 0..0),

    /**
     * Haunted Biome
     */
    AREITA("Areita", Biomes.HAUNTED, 3..6),
    BLOODBAT("Bloodbat", Biomes.HAUNTED, 3..6),
    DUPLICO("Duplico", Biomes.HAUNTED, 2..4),
    GAZER("Gazer", Biomes.HAUNTED,2..4),
    LITTERBUG("Litterbug", Biomes.HAUNTED, 4..8),
    SOLSNATCHER("Solsnatcher", Biomes.HAUNTED, 4..8),
    GIMMIEGOLD("Gimmiegold", Biomes.HAUNTED, 0..0),
    HIDEONWALL("Hideonwall", Biomes.HAUNTED, 2..4),
    HIDEYHO("Hideyho", Biomes.HAUNTED, 1..1),
    DOOMSPIRAL("Doomspiral", Biomes.HAUNTED, 1..1),

    /**
     * Icy Biome
     */
    STRONGARM("Strongarm", Biomes.ICY, 4..8),
    TEPID("Tepid", Biomes.ICY, 6..8),
    POLARIS("Polaris", Biomes.ICY, 2..4),
    SHUDDERSQUID("Shuddersquid", Biomes.ICY, 3..6),
    BILLYGOAT("Billygoat", Biomes.ICY, 2..4),
    MANTIS_SHRIMP("Mantis Shrimp", Biomes.ICY, 3..6),
    NOZZLENOSE("Nozzlenose", Biomes.ICY, 2..4),
    TROODON("Troodon", Biomes.ICY, 3..3),
    WUMPA("Wumpa", Biomes.ICY, 1..1);
}