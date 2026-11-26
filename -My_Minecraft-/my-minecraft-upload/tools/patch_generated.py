#!/usr/bin/env python3
"""Apply Minecraft 26.3 source compatibility fixes to generated AnimalSpecs.java.

The Minecraft sound-event constants are not stable across target versions. Resolve their
registry identifiers instead so generated animal data does not depend on constant names.
"""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
source = ROOT / "src/main/java/com/myminecraft/animal/AnimalSpecs.java"
text = source.read_text(encoding="utf-8")

text = re.sub(
    r"SoundEvents\.([A-Z0-9_]+)_(AMBIENT|HURT|DEATH)",
    lambda match: 'sound("entity.' + match.group(1).lower() + '.' + match.group(2).lower() + '")',
    text,
)
text = text.replace("import net.minecraft.sounds.SoundEvents;", """import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;""")
helper = '''    private static SoundEvent sound(String id) {
        return BuiltInRegistries.SOUND_EVENT.getValue(ResourceLocation.withDefaultNamespace(id));
    }

'''
marker = "    private AnimalSpecs() {}"
if helper.strip() not in text:
    if marker not in text:
        raise SystemExit(f"Could not find insertion point in {source}")
    text = text.replace(marker, helper + marker)
source.write_text(text, encoding="utf-8")
