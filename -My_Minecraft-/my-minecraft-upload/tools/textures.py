"""Procedural texture painters (Pillow). Every UV rectangle here matches the cuboid layout in AnimalModel.java."""
import os
import random
from PIL import Image, ImageDraw


def _save(img, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path, optimize=True)


def hx(s):
    s = s.lstrip('#')
    return tuple(int(s[i:i + 2], 16) for i in (0, 2, 4)) + (255,)


def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + (255,)


def noisy(img, rect, c, amp=7, rnd=None):
    rnd = rnd or random
    x, y, w, h = rect
    px = img.load()
    for j in range(y, y + h):
        for i in range(x, x + w):
            if 0 <= i < img.width and 0 <= j < img.height:
                px[i, j] = shade(c, 1 + rnd.uniform(-amp, amp) / 100.0)


def faces(u, v, w, h, d):
    """UV rectangles of a Minecraft cuboid: returns dict face -> (x, y, w, h)."""
    return {
        "top": (u + d, v, w, d), "bottom": (u + d + w, v, w, d),
        "west": (u, v + d, d, h), "north": (u + d, v + d, w, h),
        "east": (u + d + w, v + d, d, h), "south": (u + 2 * d + w, v + d, w, h),
    }


# ---- slots: must stay identical to AnimalModel.java --------------------------------------------
SLOT = dict(head=(0, 0), snout=(0, 32), ear=(64, 0), horn=(96, 0), tusk=(64, 24), antler=(96, 24), mane=(64, 48),
            body=(0, 64), leg=(0, 128), arm=(40, 128), neck=(128, 64), tail=(128, 96), trunk=(160, 96), wing=(128, 128),
            eye=(248, 0))


def paint_animal(a, path):
    rnd = random.Random(a["name"])
    p = a["pal"]
    base, dark, light, acc = hx(p["base"]), hx(p["dark"]), hx(p["light"]), hx(p["accent"])
    img = Image.new("RGBA", (256, 256), base)
    dr = ImageDraw.Draw(img)
    noisy(img, (0, 0, 256, 256), base, 6, rnd)

    bw, bh, bl = a["body"]
    hw, hh, hl = a["head"]
    legH, legT = a["legs"]
    pat = p["pattern"]

    def rect(r, col):
        dr.rectangle([r[0], r[1], r[0] + r[2] - 1, r[1] + r[3] - 1], fill=col)

    def fur(r, col, amp=6):
        noisy(img, r, col, amp, rnd)

    bf = faces(*SLOT["body"], bw, bh, bl)
    fur(bf["top"], shade(base, 0.88)); fur(bf["bottom"], light)
    for k in ("west", "east", "north", "south"):
        fur(bf[k], base)
        x, y, w, h = bf[k]
        for j in range(h):
            if j > h * 0.66:
                t = (j - h * 0.66) / (h * 0.34)
                for i in range(w):
                    px = img.getpixel((x + i, y + j))
                    img.putpixel((x + i, y + j), tuple(int(px[c] * (1 - 0.55 * t) + light[c] * 0.55 * t) for c in range(3)) + (255,))
    hf = faces(*SLOT["head"], hw, hh, hl)
    for k, r in hf.items():
        fur(r, base)
    x, y, w, h = hf["north"]
    rect((x + w // 4, y + h // 2, max(2, w // 2), h // 2), light)
    fur((x + w // 4, y + h // 2, max(2, w // 2), h // 2), light)
    rect((x + w // 2 - 1, y + h // 2, 2, 1), dark)
    arms = a.get("arms", (6, 2))
    for slot, dims in (("leg", (legT, legH, legT)), ("arm", (arms[1], arms[0], arms[1]))):
        for k, r in faces(*SLOT[slot], *dims).items():
            fur(r, base)
            if k in ("west", "north", "east", "south"):
                rect((r[0], r[1] + int(r[3] * 0.72), r[2], r[3] - int(r[3] * 0.72)), dark)
    for slot in ("snout", "ear", "horn", "tusk", "antler", "mane", "neck", "tail", "trunk", "wing"):
        x, y = SLOT[slot]
        col = base
        if slot in ("horn", "antler", "tusk"):
            col = hx("d9cdb0")
        if slot == "ear":
            col = dark
        if slot == "snout":
            col = acc if a.get("beak") else light
        if slot == "mane":
            col = dark if a.get("mane") != "ring" else shade(base, 0.7)
        if slot == "wing":
            col = base if a["name"] == "flamingo" else dark
        fur((x, y, 64, 32), col)

    def stripes(r, thick=(1, 3), gap=(2, 4), vertical=True):
        x, y, w, h = r
        pos, n = 0, (w if vertical else h)
        while pos < n:
            t = rnd.randint(*thick)
            for q in range(t):
                if pos + q >= n:
                    break
                if vertical:
                    dr.line([x + pos + q, y, x + pos + q, y + h - 1], fill=dark)
                else:
                    dr.line([x, y + pos + q, x + w - 1, y + pos + q], fill=dark)
            pos += t + rnd.randint(*gap)

    def spots(r, col, n, rad=(1, 2)):
        x, y, w, h = r
        for _ in range(n):
            cx, cy, rr = x + rnd.randrange(w), y + rnd.randrange(h), rnd.randint(*rad)
            dr.ellipse([cx - rr, cy - rr, cx + rr, cy + rr], fill=col)

    body_rects = [bf["west"], bf["east"], bf["top"], bf["north"], bf["south"]]
    if pat == "stripes":
        for r in body_rects:
            stripes(r)
        for k in ("north", "west", "east"):
            stripes(hf[k], (1, 1), (2, 3))
        for r in faces(*SLOT["leg"], legT, legH, legT).values():
            stripes(r, (1, 1), (3, 4), False)
        stripes((*SLOT["tail"], 64, 32), (1, 2), (2, 3), False)
    elif pat == "spots":
        for r in body_rects:
            spots(r, light, max(3, r[2] * r[3] // 22))
    elif pat == "dots":
        for r in body_rects + [(*SLOT["tail"], 64, 32)]:
            spots(r, dark, max(3, r[2] * r[3] // 18), (1, 1))
        spots(hf["west"], dark, 3, (1, 1)); spots(hf["east"], dark, 3, (1, 1))
    elif pat == "patches":
        for r in body_rects + [(*SLOT["neck"], 64, 32)]:
            spots(r, dark, max(3, r[2] * r[3] // 38), (2, 4))
        spots(hf["west"], dark, 2); spots(hf["east"], dark, 2)
    elif pat == "rings":
        x, y = SLOT["tail"]
        for j in range(y, y + 32, 6):
            dr.rectangle([x, j, x + 63, j + 2], fill=dark)
        if a["name"] == "raccoon":
            x, y, w, h = hf["north"]
            dr.rectangle([x, y + h // 4, x + w - 1, y + h // 4 + max(1, h // 4)], fill=dark)
    ex, ey = SLOT["eye"]
    eye = hx("f2b400") if a["name"] in ("owl", "eagle") else hx("0c0c0c")
    dr.rectangle([ex, ey, ex + 5, ey + 2], fill=eye)
    _save(img, path)


def paint_player_skin(path, name, skin, hair, top, legs, shoes):
    """64x64 player-layout skin: 8x8 face at (8,8)."""
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    rnd = random.Random(name)

    def box(x, y, w, h, c):
        d.rectangle([x, y, x + w - 1, y + h - 1], fill=c)
        noisy(img, (x, y, w, h), c, 5, rnd)

    s, hr, t, lg, sh = hx(skin), hx(hair), hx(top), hx(legs), hx(shoes)
    box(0, 0, 32, 16, hr)
    box(8, 8, 8, 8, s)
    d.rectangle([10, 11, 10, 11], fill=hx("1a1a1a")); d.rectangle([13, 11, 13, 11], fill=hx("1a1a1a"))
    d.rectangle([11, 14, 12, 14], fill=shade(s, 0.7))
    box(16, 16, 24, 16, t); box(40, 16, 16, 16, t); box(32, 48, 16, 16, t)
    box(0, 16, 16, 16, lg); box(16, 48, 16, 16, lg)
    for (x, y) in ((0, 28), (16, 60)):
        box(x, y, 16, 4, sh)
    for (x, y) in ((40, 30), (32, 62)):
        box(x, y, 16, 2, s)
    _save(img, path)


def paint_iceologer(path):
    """Illager-layout 64x64 skin (evoker UV): icy robe, pale-blue skin, dark hood trim."""
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    rnd = random.Random("iceologer")
    robe, trim, skin, beard = hx("8fd0ee"), hx("2c5d7a"), hx("c9d8e0"), hx("f4fbff")

    def box(r, c, amp=6):
        d.rectangle([r[0], r[1], r[0] + r[2] - 1, r[1] + r[3] - 1], fill=c)
        noisy(img, r, c, amp, rnd)
    box((0, 0, 32, 18), shade(skin, 0.95)); box((8, 8, 8, 10), skin)
    box((32, 0, 32, 18), trim); box((24, 0, 8, 6), shade(skin, 0.85))
    box((16, 20, 28, 18), robe); box((0, 38, 28, 26), shade(robe, 0.9))
    box((44, 22, 16, 16), trim); box((40, 46, 16, 16), robe); box((0, 22, 16, 16), shade(trim, 1.2))
    box((10, 15, 4, 3), beard)
    d.rectangle([10, 11, 10, 11], fill=hx("1c3a50")); d.rectangle([13, 11, 13, 11], fill=hx("1c3a50"))
    d.rectangle([9, 10, 14, 10], fill=hx("e8f6ff"))
    _save(img, path)


def paint_vehicle(path, kind):
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    rnd = random.Random(kind)
    body = hx("c8281e") if kind == "car" else hx("2a6fd6")
    for rect, c in (((0, 0, 64, 40), body), ((0, 40, 64, 24), hx("1a1a1a"))):
        d.rectangle([rect[0], rect[1], rect[0] + rect[2] - 1, rect[1] + rect[3] - 1], fill=c)
        noisy(img, rect, c, 4, rnd)
    d.rectangle([0, 20, 63, 27], fill=hx("a9d6f5") if kind == "car" else hx("d8d8d8"))
    _save(img, path)


def paint_pool_tile(path, base="9fd9f0", grout="e9f6fb"):
    img = Image.new("RGBA", (16, 16), hx(base))
    noisy(img, (0, 0, 16, 16), hx(base), 4, random.Random("pool"))
    d = ImageDraw.Draw(img)
    for i in (0, 8):
        d.line([i, 0, i, 15], fill=hx(grout)); d.line([0, i, 15, i], fill=hx(grout))
    _save(img, path)
