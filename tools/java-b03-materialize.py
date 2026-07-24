#!/usr/bin/env python3
from __future__ import annotations

import base64
import hashlib
import io
import json
import os
import pathlib
import urllib.request
import zipfile

REPOSITORY = "alimbetov/obsidian"
EXPECTED_SHA256 = "21a18e3e0788332de775caaf36a0365a43bff629a3de3635132e61ff6e39fda3"
BLOB_SHAS = [
    "03ab08b8f5f517b64fa4014ec300a63a99be7545",
    "5ead20e17f770231c8ac0e02a763f76f2694e18c",
    "a6c1e51088027bef8c0d021372364ba36f81fb80",
    "714f2c0a60dc0621f00e25fbc0c29b5ef635da80",
    "af1c9fc5b827a67a5db89d57390d3fa204f85bf3",
    "fbb3511724e1ec263f47d38b338d96b7e6e7eeb8",
    "b30a3633fe17219ce264d04488c60e8c8413a92c",
    "488b77bf09f70af1478435c635de4a8e6c3d8f9a",
]


def fetch_blob(sha: str, token: str) -> bytes:
    request = urllib.request.Request(
        f"https://api.github.com/repos/{REPOSITORY}/git/blobs/{sha}",
        headers={
            "Accept": "application/vnd.github+json",
            "Authorization": f"Bearer {token}",
            "X-GitHub-Api-Version": "2022-11-28",
            "User-Agent": "java-b03-materializer",
        },
    )
    with urllib.request.urlopen(request, timeout=30) as response:
        payload = json.load(response)
    if payload.get("encoding") != "base64":
        raise RuntimeError(f"Unexpected blob encoding for {sha}: {payload.get('encoding')}")
    return base64.b64decode(payload["content"].replace("\n", ""), validate=True)


def safe_extract(archive_bytes: bytes, destination: pathlib.Path) -> None:
    root = destination.resolve()
    with zipfile.ZipFile(io.BytesIO(archive_bytes)) as archive:
        for member in archive.infolist():
            target = (destination / member.filename).resolve()
            if root not in target.parents and target != root:
                raise RuntimeError(f"Unsafe ZIP member: {member.filename}")
        archive.extractall(destination)


def main() -> None:
    token = os.environ.get("GITHUB_TOKEN")
    if not token:
        raise RuntimeError("GITHUB_TOKEN is required")

    archive_bytes = b"".join(fetch_blob(sha, token) for sha in BLOB_SHAS)
    actual = hashlib.sha256(archive_bytes).hexdigest()
    if actual != EXPECTED_SHA256:
        raise RuntimeError(f"Payload checksum mismatch: {actual}")

    safe_extract(archive_bytes, pathlib.Path.cwd())
    print(f"Materialized JAVA-B03 payload: {len(archive_bytes)} bytes, sha256={actual}")


if __name__ == "__main__":
    main()
