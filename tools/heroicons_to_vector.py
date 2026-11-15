"""Convert the Heroicons solid SVGs we use into Android vector drawables.

Heroicons is MIT licensed; the license text is downloaded alongside the icons.
Run from the repo root: python3 tools/heroicons_to_vector.py
"""

import os
import re
import urllib.request

ICONS = [
    "home",
    "squares-2x2",
    "film",
    "globe-alt",
    "cog-6-tooth",
    "check",
    "chevron-right",
    "shield-check",
    "shield-exclamation",
    "moon",
    "sun",
    "clock",
    "bell",
    "plus",
    "magnifying-glass",
    "information-circle",
    "arrow-path",
    "ellipsis-horizontal",
    "x-mark",
    "users",
    "banknotes",
]

BASE = "https://raw.githubusercontent.com/tailwindlabs/heroicons/master/src/24/solid/{}.svg"
OUT_DIR = "app/src/main/res/drawable"


def fetch(name):
    with urllib.request.urlopen(BASE.format(name), timeout=20) as response:
        return response.read().decode("utf-8")


def paths(svg):
    found = []
    for match in re.finditer(r"<path[^>]*>", svg):
        tag = match.group(0)
        d = re.search(r'\bd="([^"]+)"', tag)
        if not d:
            continue
        even_odd = 'fill-rule="evenodd"' in tag
        found.append((d.group(1).strip(), even_odd))
    return found


def vector(shapes):
    body = []
    for d, even_odd in shapes:
        fill_type = '\n        android:fillType="evenOdd"' if even_odd else ""
        body.append(
            '    <path\n'
            '        android:pathData="%s"\n'
            '        android:fillColor="#FF000000"%s />' % (d, fill_type)
        )
    return (
        '<?xml version="1.0" encoding="utf-8"?>\n'
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
        '    android:width="24dp"\n'
        '    android:height="24dp"\n'
        '    android:viewportWidth="24"\n'
        '    android:viewportHeight="24">\n'
        "%s\n"
        "</vector>\n" % "\n".join(body)
    )


def main():
    os.makedirs(OUT_DIR, exist_ok=True)
    for name in ICONS:
        shapes = paths(fetch(name))
        if not shapes:
            raise SystemExit("no paths in " + name)
        target = os.path.join(OUT_DIR, "ic_" + name.replace("-", "_") + ".xml")
        with open(target, "w", encoding="utf-8") as handle:
            handle.write(vector(shapes))
        print("%-22s %d path(s) -> %s" % (name, len(shapes), target))

    with urllib.request.urlopen(
        "https://raw.githubusercontent.com/tailwindlabs/heroicons/master/LICENSE",
        timeout=20,
    ) as response:
        with open("docs/licenses/Heroicons-MIT.txt", "wb") as handle:
            handle.write(response.read())
    print("license -> docs/licenses/Heroicons-MIT.txt")


if __name__ == "__main__":
    main()
