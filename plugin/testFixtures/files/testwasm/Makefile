#
# SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
# SPDX-License-Identifier: Apache-2.0
#

.PHONY: all clean

all: helloworld.wasm clock.wasm

clock.wasm: clock.cpp
	emcc -O3 -mbulk-memory $^ -o $@; chmod 644 $@

helloworld.wasm: helloworld.cpp
	emcc -O3 -mbulk-memory $^ -o $@; chmod 644 $@

clean:
	@rm -f *.wasm *.mjs

