/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the wasm2class-gradle-plugin project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

// hello.cpp
#include <stdio.h>
#include <time.h>

int main() {
  long long ts = (long long)time(NULL);
  printf("Time: %lli\n", ts);
  return 0;
}
