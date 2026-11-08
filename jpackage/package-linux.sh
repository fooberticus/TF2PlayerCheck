#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

# jpackage refuses to overwrite an existing app image
rm -rf TF2PlayerCheck

jpackage @opts/jpackage-linux-image.opts
jpackage @opts/jpackage-deb.opts
