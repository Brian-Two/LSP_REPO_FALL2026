#!/usr/bin/env python3
"""Create and verify a deterministic source-only capstone ZIP using the standard library."""

from pathlib import Path
from tempfile import NamedTemporaryFile
from zipfile import ZIP_DEFLATED, ZipFile, ZipInfo


def main():
    """Package only project Java sources and tests, then verify their paths and bytes."""
    root = Path(__file__).resolve().parent.parent
    folders = [root / kind / 'org/howard/edu/lsp/project' for kind in ('src', 'test')]
    files = sorted(path for folder in folders for path in folder.rglob('*.java'))
    for folder in folders:
        if not any(path.is_relative_to(folder) for path in files):
            raise SystemExit(f'Missing Java source files under {folder}')
    output = root / 'project/LSP_Project.zip'
    with NamedTemporaryFile(dir=output.parent, suffix='.zip', delete=False) as temporary:
        temp_path = Path(temporary.name)
    try:
        with ZipFile(temp_path, 'w', compression=ZIP_DEFLATED) as archive:
            for path in files:
                info = ZipInfo(path.relative_to(root).as_posix(), date_time=(2026, 11, 18, 0, 0, 0))
                info.compress_type = ZIP_DEFLATED
                info.external_attr = 0o100644 << 16
                archive.writestr(info, path.read_bytes())
        with ZipFile(temp_path) as archive:
            expected = {path.relative_to(root).as_posix() for path in files}
            if set(archive.namelist()) != expected or len(archive.namelist()) != len(files):
                raise SystemExit('Archive entry list does not exactly match project Java files.')
            if archive.testzip() is not None:
                raise SystemExit('Archive CRC verification failed.')
            for path in files:
                name = path.relative_to(root).as_posix()
                if archive.read(name) != path.read_bytes():
                    raise SystemExit(f'Archive content differs from source: {name}')
        temp_path.replace(output)
    finally:
        temp_path.unlink(missing_ok=True)
    print(f'Verified {output.name}: {len(files)} Java files, project source and tests only.')


if __name__ == '__main__':
    main()
