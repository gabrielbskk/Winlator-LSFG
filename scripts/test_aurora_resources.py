#!/usr/bin/env python3
"""Static resource regressions; does not replace rendering on Android devices."""
from pathlib import Path
import json
import unittest
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/app/src/main/res"
ANDROID = "{http://schemas.android.com/apk/res/android}"

def luminance(color):
    rgb = [int(color[i:i+2], 16) / 255 for i in (1, 3, 5)]
    rgb = [c / 12.92 if c <= .04045 else ((c + .055) / 1.055) ** 2.4 for c in rgb]
    return sum(a * b for a, b in zip(rgb, (.2126, .7152, .0722)))

def contrast(a, b):
    low, high = sorted((luminance(a), luminance(b)))
    return (high + .05) / (low + .05)

class AuroraResourcesTest(unittest.TestCase):
    def test_text_contrast_in_both_themes(self):
        colors = {e.attrib['name']: e.text for e in ET.parse(RES / 'values/colors.xml').getroot()}
        styles = ET.parse(RES / 'values/styles.xml').getroot()
        for name in ('AppThemeLight', 'AppThemeDark'):
            theme = styles.find(f"style[@name='{name}']")
            palette = {e.attrib['name']: colors[e.text[7:]] for e in theme if e.text.startswith('@color/')}
            for text in ('colorPrimaryText', 'colorSecondaryText'):
                for surface in ('colorPrimarySurface', 'colorSecondarySurface'):
                    self.assertGreaterEqual(contrast(palette[text], palette[surface]), 4.5, (name, text, surface))
            self.assertGreaterEqual(contrast('#FFFFFF', palette['colorActionSurface']), 4.5)

    def test_rpc_preview_and_table_use_themed_surfaces(self):
        table = ET.parse(RES / 'drawable/table_head_background.xml').getroot()
        self.assertEqual('?attr/colorActionSurface', table.find('solid').attrib[ANDROID + 'color'])
        layout = ET.parse(RES / 'layout/shortcut_settings_dialog.xml').getroot()
        for parent in layout.iter():
            if any(c.attrib.get(ANDROID + 'id') == '@+id/IVDiscordRpcPreviewArtwork' for c in parent):
                self.assertEqual('?attr/colorSecondarySurface', parent.attrib[ANDROID + 'background'])
                break
        else:
            self.fail('RPC preview missing')

    def test_lsfg_can_scroll_and_keeps_multiplier_and_presets(self):
        root = ET.parse(RES / 'layout/lsfg_vk_config_dialog.xml').getroot()
        self.assertEqual('ScrollView', root.tag)
        ids = {e.attrib.get(ANDROID + 'id') for e in root.iter()}
        self.assertTrue({'@+id/SLSFGMultiplier', '@+id/SLSFGPreset', '@+id/SBLSFGFlowScale'} <= ids)

    def test_turnip_requires_explicit_driver_and_version(self):
        layout = ET.parse(RES / 'layout/turnip_config_dialog.xml').getroot()
        ids = {node.attrib.get(ANDROID + 'id') for node in layout.iter()}
        self.assertIn('@+id/SVersion', ids)
        self.assertNotIn('@+id/CBTurnipAutomatic', ids)
        self.assertTrue(any(node.attrib.get(ANDROID + 'text') == '@string/turnip_manual_note'
                            for node in layout.iter()))
        base = ROOT / 'app/app/src/main/java/com/winlator'
        drivers = (base / 'container/GraphicsDrivers.java').read_text(encoding='utf-8')
        self.assertNotIn('TurnipPolicy', drivers)
        self.assertNotIn('isTurnipCandidate', drivers)
        dialog = (base / 'contentdialog/TurnipConfigDialog.java').read_text(encoding='utf-8')
        self.assertNotIn('CBTurnipAutomatic', dialog)
        self.assertIn('newConfig.put("version"', dialog)

    def test_electron_preset_is_opt_in_and_exposes_cef_controls(self):
        base = ROOT / "app/app/src/main"
        manager = (base / "java/com/winlator/box64/Box64PresetManager.java").read_text(encoding="utf-8")
        self.assertIn("id.equals(Box64Preset.ELECTRON)", manager)
        self.assertIn("envVars.put(\"BOX64_LIBCEF\", \"1\")", manager)
        self.assertIn("envVars.put(\"BOX64_MALLOC_HACK\", \"2\")", manager)
        intermediate = manager[manager.index("id.equals(Box64Preset.INTERMEDIATE)"):manager.index("id.equals(Box64Preset.PERFORMANCE)")]
        self.assertNotIn("BOX64_LIBCEF", intermediate)
        variables = {item["name"] for item in json.loads((base / "assets/box64/env_vars.json").read_text(encoding="utf-8"))}
        self.assertTrue({"BOX64_LIBCEF", "BOX64_MALLOC_HACK"} <= variables)
        titles = {node.attrib.get(ANDROID + "title") for node in ET.parse(RES / "menu/extra_args_popup_menu.xml").getroot()}
        self.assertIn("--no-sandbox --disable-gpu-sandbox", titles)

if __name__ == '__main__':
    unittest.main()
