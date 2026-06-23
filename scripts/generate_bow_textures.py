#!/usr/bin/env python3
"""
Script to download vanilla Minecraft bow and crossbow textures and create color-shifted variants.
"""

import os
import urllib.request
from PIL import Image
import colorsys

# Output directory for textures
OUTPUT_DIR = "../common/src/main/resources/assets/sagittary/textures/item"

# Vanilla texture URLs (from Minecraft Wiki or similar source)
# Using raw GitHub URLs from the Minecraft assets repository
BASE_URL = "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21/assets/minecraft/textures/item"

BOW_TEXTURES = [
    "bow.png",
    "bow_pulling_0.png",
    "bow_pulling_1.png",
    "bow_pulling_2.png"
]

CROSSBOW_TEXTURES = [
    "crossbow_standby.png",
    "crossbow_pulling_0.png",
    "crossbow_pulling_1.png",
    "crossbow_pulling_2.png",
    "crossbow_arrow.png",
    "crossbow_firework.png"
]

def ensure_dir(path):
    os.makedirs(path, exist_ok=True)

def download_texture(url, local_path):
    """Download a texture from URL to local path."""
    try:
        print(f"Downloading: {url}")
        urllib.request.urlretrieve(url, local_path)
        return True
    except Exception as e:
        print(f"Failed to download {url}: {e}")
        return False

def color_shift_image(image, hue_shift, saturation_mult=1.0, value_mult=1.0):
    """
    Shift the hue of an image while preserving alpha.
    hue_shift: amount to shift hue (0-1 range, wraps around)
    saturation_mult: multiplier for saturation
    value_mult: multiplier for value/brightness
    """
    if image.mode != 'RGBA':
        image = image.convert('RGBA')

    pixels = image.load()
    width, height = image.size

    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]

            if a == 0:
                continue

            # Convert to HSV
            h, s, v = colorsys.rgb_to_hsv(r/255.0, g/255.0, b/255.0)

            # Apply transformations
            h = (h + hue_shift) % 1.0
            s = min(1.0, s * saturation_mult)
            v = min(1.0, v * value_mult)

            # Convert back to RGB
            r_new, g_new, b_new = colorsys.hsv_to_rgb(h, s, v)

            pixels[x, y] = (int(r_new * 255), int(g_new * 255), int(b_new * 255), a)

    return image

def create_iron_variant(image):
    """Create iron/steel colored variant (desaturate and shift toward gray)."""
    return color_shift_image(image, hue_shift=0.0, saturation_mult=0.3, value_mult=0.9)

def create_compound_variant(image):
    """Create compound bow variant (orange/blaze rod color)."""
    return color_shift_image(image, hue_shift=0.08, saturation_mult=1.2, value_mult=1.0)

def create_repeater_variant(image):
    """Create repeater crossbow variant (red/redstone tint)."""
    return color_shift_image(image, hue_shift=-0.05, saturation_mult=1.3, value_mult=0.95)

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    output_dir = os.path.join(script_dir, OUTPUT_DIR)
    ensure_dir(output_dir)

    temp_dir = os.path.join(script_dir, "temp_textures")
    ensure_dir(temp_dir)

    print("=== Downloading and processing bow textures ===\n")

    # Process bow textures
    for tex_name in BOW_TEXTURES:
        url = f"{BASE_URL}/{tex_name}"
        temp_path = os.path.join(temp_dir, tex_name)

        if download_texture(url, temp_path):
            img = Image.open(temp_path)

            # Create iron bow
            iron_name = tex_name.replace("bow", "iron_bow")
            iron_img = create_iron_variant(img.copy())
            iron_path = os.path.join(output_dir, iron_name)
            iron_img.save(iron_path)
            print(f"Created: {iron_name}")

            # Create compound bow
            compound_name = tex_name.replace("bow", "compound_bow")
            compound_img = create_compound_variant(img.copy())
            compound_path = os.path.join(output_dir, compound_name)
            compound_img.save(compound_path)
            print(f"Created: {compound_name}")

    print("\n=== Downloading and processing crossbow textures ===\n")

    # Process crossbow textures
    for tex_name in CROSSBOW_TEXTURES:
        url = f"{BASE_URL}/{tex_name}"
        temp_path = os.path.join(temp_dir, tex_name)

        if download_texture(url, temp_path):
            img = Image.open(temp_path)

            # Create iron crossbow
            iron_name = tex_name.replace("crossbow", "iron_crossbow")
            iron_img = create_iron_variant(img.copy())
            iron_path = os.path.join(output_dir, iron_name)
            iron_img.save(iron_path)
            print(f"Created: {iron_name}")

            # Create repeater crossbow
            repeater_name = tex_name.replace("crossbow", "repeater_crossbow")
            repeater_img = create_repeater_variant(img.copy())
            repeater_path = os.path.join(output_dir, repeater_name)
            repeater_img.save(repeater_path)
            print(f"Created: {repeater_name}")

    # Cleanup temp directory
    import shutil
    shutil.rmtree(temp_dir)

    print("\n=== Done! ===")
    print(f"Textures saved to: {output_dir}")

if __name__ == "__main__":
    main()
