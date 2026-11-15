"""Print the vertical rhythm of a screenshot: card runs and the gaps between them.

Usage: python3 tools/measure_rhythm.py shot.png [x]
Reads a PNG, walks one column, and reports runs of canvas versus content in dp.
"""

import struct
import sys
import zlib

DENSITY = 2.75
CANVAS = (246, 244, 239)


def read_png(path):
    data = open(path, "rb").read()
    assert data[:8] == b"\x89PNG\r\n\x1a\n"
    pos = 8
    idat = b""
    width = height = depth = color = 0
    while pos < len(data):
        length = struct.unpack(">I", data[pos : pos + 4])[0]
        kind = data[pos + 4 : pos + 8]
        body = data[pos + 8 : pos + 8 + length]
        pos += 12 + length
        if kind == b"IHDR":
            width, height, depth, color = struct.unpack(">IIBB", body[:10])
        elif kind == b"IDAT":
            idat += body
        elif kind == b"IEND":
            break
    assert depth == 8 and color in (2, 6), "expected 8-bit RGB or RGBA"
    channels = 3 if color == 2 else 4
    raw = zlib.decompress(idat)
    stride = width * channels
    rows = []
    prev = bytearray(stride)
    index = 0
    for _ in range(height):
        kind = raw[index]
        index += 1
        line = bytearray(raw[index : index + stride])
        index += stride
        if kind == 1:
            for i in range(channels, stride):
                line[i] = (line[i] + line[i - channels]) & 0xFF
        elif kind == 2:
            for i in range(stride):
                line[i] = (line[i] + prev[i]) & 0xFF
        elif kind == 3:
            for i in range(stride):
                left = line[i - channels] if i >= channels else 0
                line[i] = (line[i] + ((left + prev[i]) >> 1)) & 0xFF
        elif kind == 4:
            for i in range(stride):
                a = line[i - channels] if i >= channels else 0
                b = prev[i]
                c = prev[i - channels] if i >= channels else 0
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                predictor = a if pa <= pb and pa <= pc else (b if pb <= pc else c)
                line[i] = (line[i] + predictor) & 0xFF
        rows.append(line)
        prev = line
    return width, height, channels, rows


def main():
    path = sys.argv[1]
    width, height, channels, rows = read_png(path)
    x = int(sys.argv[2]) if len(sys.argv) > 2 else width // 2

    def is_canvas(y):
        line = rows[y]
        i = x * channels
        r, g, b = line[i], line[i + 1], line[i + 2]
        return abs(r - CANVAS[0]) <= 3 and abs(g - CANVAS[1]) <= 3 and abs(b - CANVAS[2]) <= 3

    runs = []
    current = is_canvas(0)
    start = 0
    for y in range(1, height):
        value = is_canvas(y)
        if value != current:
            runs.append((current, start, y - 1))
            current = value
            start = y
    runs.append((current, start, height - 1))

    print("%s  %dx%d  column x=%d" % (path, width, height, x))
    for canvas, start, end in runs:
        length = end - start + 1
        if canvas and length >= 20:
            print("  gap   %4d-%4d  %5.1f dp" % (start, end, length / DENSITY))
        elif not canvas and length >= 20:
            print("  block %4d-%4d  %5.1f dp" % (start, end, length / DENSITY))


if __name__ == "__main__":
    main()
