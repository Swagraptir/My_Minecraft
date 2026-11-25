package com.myminecraft.registry;

import com.myminecraft.Ids;
import com.myminecraft.worldgen.ProceduralPiece;
import com.myminecraft.worldgen.ProceduralStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public final class ModStructures {
    public static StructureType<ProceduralStructure> PROCEDURAL;
    public static StructurePieceType PIECE_TYPE;

    public static void register() {
        PIECE_TYPE = Registry.register(BuiltInRegistries.STRUCTURE_PIECE, Ids.of("procedural"),
                (StructurePieceType) ProceduralPiece::new);
        PROCEDURAL = Registry.register(BuiltInRegistries.STRUCTURE_TYPE, Ids.of("procedural"),
                () -> ProceduralStructure.CODEC);
    }

    private ModStructures() {}
}
