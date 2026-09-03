package cx.tfe.fennec.data

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB

enum class Biomes(val game_name: String, val corner1: BlockPos, val corner2: BlockPos) {
    CAVERN("Cavern", BlockPos(-51, 20, 2), BlockPos(-180, 120, 105)),
    FOREST("Forest", BlockPos(-48, 20, 2), BlockPos(50, 120, 105)),
    HAUNTED("Haunted", BlockPos(-49, 20, -1), BlockPos(50, 120, -120)),
    ICY("Icy", BlockPos(-51, 20, -1), BlockPos(-180, 120, -120));

    val box: AABB = AABB(
        corner1.x.toDouble(), corner1.y.toDouble(), corner1.z.toDouble(),
        corner2.x.toDouble(), corner2.y.toDouble(), corner2.z.toDouble(),
    )
}