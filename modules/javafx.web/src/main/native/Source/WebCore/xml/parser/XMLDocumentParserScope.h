/*
 * Copyright (C) 2009-2024 Apple Inc. All rights reserved.
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

#pragma once

#include <libxml/parser.h>
#include <wtf/Noncopyable.h>
#include <wtf/WeakPtr.h>

#if ENABLE(XSLT)
#include <libxml/xmlerror.h>
#endif

namespace WebCore {

class CachedResourceLoader;

class XMLDocumentParserScope {
        WTF_MAKE_NONCOPYABLE(XMLDocumentParserScope);
public:
    XMLDocumentParserScope() = delete;
        explicit XMLDocumentParserScope(CachedResourceLoader*);
        ~XMLDocumentParserScope();

    static WeakPtr<CachedResourceLoader>& currentCachedResourceLoader();

#if ENABLE(XSLT)
    XMLDocumentParserScope(CachedResourceLoader*, xmlGenericErrorFunc, xmlStructuredErrorFunc = nullptr, void* genericErrorContext = nullptr, void* structuredErrorContext = nullptr);
#endif

private:
    WeakPtr<CachedResourceLoader> m_oldCachedResourceLoader;
    xmlExternalEntityLoader m_oldEntityLoader { nullptr };

#if ENABLE(XSLT)
    xmlGenericErrorFunc m_oldGenericErrorFunc { nullptr };
    xmlStructuredErrorFunc m_oldStructuredErrorFunc { nullptr };
    void* m_oldGenericErrorContext { nullptr };
    void* m_oldStructuredErrorContext { nullptr };
#endif
};

} // namespace WebCore
