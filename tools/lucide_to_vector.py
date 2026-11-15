"""Convert the Lucide SVGs we use into Android vector drawables.

Lucide is ISC licensed; the license text is downloaded alongside the icons.
Run from the repo root: python3 tools/lucide_to_vector.py
"""

import os
import re
import urllib.request

ICONS = [
    "house",
    "layout-grid",
    "film",
    "globe",
    "settings",
    "check",
    "chevron-right",
    "shield-check",
    "moon",
    "sun",
    "clock",
    "bell",
    "plus",
    "search",
    "info",
    "refresh-cw",
    "more-horizontal",
    "x",
    "sprout",
    "users",
    "shield-ban",
    "spade",
]

BASE = "https://raw.githubusercontent.com/lucide-icons/lucide/main/icons/{}.svg"

# Lucide renames icons now and then; keep our file names stable.
FETCH_ALIASES = {"more-horizontal": "ellipsis"}
OUT_DIR = "app/src/main/res/drawable"
STROKE = 1.75


def fetch(name):
    for candidate in (name, name.replace("-", "")):
        try:
            with urllib.request.urlopen(BASE.format(candidate), timeout=20) as response:
                return response.read().decode("utf-8")
        except Exception:
            continue
    raise SystemExit("could not fetch " + name)


def number(text, key):
    match = re.search(r'\b%s="([-\d.]+)"' % key, text)
    if not match:
        raise SystemExit("missing %s in %s" % (key, text))
    return float(match.group(1))


def tidy(value):
    return ("%.3f" % value).rstrip("0").rstrip(".")


def elements(svg):
    paths = []

    for match in re.finditer(r"<path[^>]*>", svg):
        tag = match.group(0)
        d = re.search(r'\bd="([^"]+)"', tag).group(1)
        paths.append(d.strip())

    for match in re.finditer(r"<line[^>]*>", svg):
        tag = match.group(0)
        x1, y1 = number(tag, "x1"), number(tag, "y1")
        x2, y2 = number(tag, "x2"), number(tag, "y2")
        paths.append("M%s,%s L%s,%s" % (tidy(x1), tidy(y1), tidy(x2), tidy(y2)))

    for match in re.finditer(r"<circle[^>]*>", svg):
        tag = match.group(0)
        cx, cy, r = number(tag, "cx"), number(tag, "cy"), number(tag, "r")
        paths.append(
            "M%s,%s a%s,%s 0 1,0 %s,0 a%s,%s 0 1,0 -%s,0"
            % (
                tidy(cx - r),
                tidy(cy),
                tidy(r),
                tidy(r),
                tidy(2 * r),
                tidy(r),
                tidy(r),
                tidy(2 * r),
            )
        )

    for match in re.finditer(r"<rect[^>]*>", svg):
        tag = match.group(0)
        x, y = number(tag, "x"), number(tag, "y")
        w, h = number(tag, "width"), number(tag, "height")
        rx_match = re.search(r'\brx="([-\d.]+)"', tag)
        rx = float(rx_match.group(1)) if rx_match else 0.0
        if rx <= 0:
            paths.append(
                "M%s,%s h%s v%s h-%s z"
                % (tidy(x), tidy(y), tidy(w), tidy(h), tidy(w))
            )
        else:
            paths.append(
                "M%s,%s h%s a%s,%s 0 0,1 %s,%s v%s a%s,%s 0 0,1 -%s,%s h-%s a%s,%s 0 0,1 -%s,-%s v-%s a%s,%s 0 0,1 %s,-%s z"
                % (
                    tidy(x + rx),
                    tidy(y),
                    tidy(w - 2 * rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(h - 2 * rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(w - 2 * rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(h - 2 * rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(rx),
                    tidy(rx),
                )
            )

    for kind in ("polyline", "polygon"):
        for match in re.finditer(r"<%s[^>]*>" % kind, svg):
            tag = match.group(0)
            points = re.search(r'\bpoints="([^"]+)"', tag).group(1)
            coords = re.findall(r"[-\d.]+", points)
            pairs = list(zip(coords[0::2], coords[1::2]))
            d = "M" + " L".join("%s,%s" % (p[0], p[1]) for p in pairs)
            if kind == "polygon":
                d += " z"
            paths.append(d)

    return paths


def vector(name, paths):
    body = "\n".join(
        '    <path\n'
        '        android:pathData="%s"\n'
        '        android:strokeColor="#FF000000"\n'
        '        android:strokeLineCap="round"\n'
        '        android:strokeLineJoin="round"\n'
        '        android:strokeWidth="%s" />' % (d, STROKE)
        for d in paths
    )
    return (
        '<?xml version="1.0" encoding="utf-8"?>\n'
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
        '    android:width="24dp"\n'
        '    android:height="24dp"\n'
        '    android:viewportWidth="24"\n'
        '    android:viewportHeight="24">\n'
        "%s\n"
        "</vector>\n" % body
    )


def main():
    os.makedirs(OUT_DIR, exist_ok=True)
    for icon in ICONS:
        svg = fetch(FETCH_ALIASES.get(icon, icon))
        paths = elements(svg)
        if not paths:
            raise SystemExit("no shapes in " + icon)
        target = os.path.join(OUT_DIR, "ic_" + icon.replace("-", "_") + ".xml")
        with open(target, "w", encoding="utf-8") as handle:
            handle.write(vector(icon, paths))
        print("%-18s %d shapes -> %s" % (icon, len(paths), target))

    license_path = "docs/licenses/Lucide-ISC.txt"
    with urllib.request.urlopen(
        "https://raw.githubusercontent.com/lucide-icons/lucide/main/LICENSE", timeout=20
    ) as response:
        with open(license_path, "wb") as handle:
            handle.write(response.read())
    print("license -> " + license_path)


if __name__ == "__main__":
    main()
