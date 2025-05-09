/*
 * Copyright (C) 2014-2021 Apple Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY APPLE INC. ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL APPLE INC. OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include "config.h"
#include "StructureSet.h"

#include "HeapInlines.h"
#include <wtf/CommaPrinter.h>

namespace JSC {

template<typename Visitor>
void StructureSet::markIfCheap(Visitor& visitor) const
{
    for (Structure* structure : *this)
        structure->markIfCheap(visitor);
}

template void StructureSet::markIfCheap(AbstractSlotVisitor&) const;
template void StructureSet::markIfCheap(SlotVisitor&) const;

bool StructureSet::isStillAlive(VM& vm) const
{
    for (Structure* structure : *this) {
        if (!vm.heap.isMarked(structure))
            return false;
    }
    return true;
}

void StructureSet::dumpInContext(PrintStream& out, DumpContext* context) const
{
    CommaPrinter comma;
    out.print("["_s);
    forEach([&] (Structure* structure) { out.print(comma, inContext(*structure, context)); });
    out.print("]"_s);
}

void StructureSet::dump(PrintStream& out) const
{
    dumpInContext(out, nullptr);
}

} // namespace JSC

