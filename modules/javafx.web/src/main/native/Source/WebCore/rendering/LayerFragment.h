/*
 * Copyright (C) 2015 Apple Inc. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY APPLE INC. AND ITS CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL APPLE INC. OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

#ifndef LayerFragment_h
#define LayerFragment_h

#include "ClipRect.h"

namespace WebCore {

class LayerFragment {
public:
    LayerFragment() = default;

    void setRects(const LayoutRect& bounds, const ClipRect& background, const ClipRect& foreground, const LayoutRect* bbox)
    {
        layerBounds = bounds;
        backgroundRect = background;
        foregroundRect = foreground;
        if (bbox) {
            boundingBox = *bbox;
            hasBoundingBox = true;
        }
    }

    void moveBy(const LayoutPoint& offset)
    {
        layerBounds.moveBy(offset);
        backgroundRect.moveBy(offset);
        foregroundRect.moveBy(offset);
        paginationClip.moveBy(offset);
        boundingBox.moveBy(offset);
    }

    void intersect(const LayoutRect& rect)
    {
        backgroundRect.intersect(rect);
        foregroundRect.intersect(rect);
        boundingBox.intersect(rect);
    }

    void intersect(const ClipRect& clipRect)
    {
        backgroundRect.intersect(clipRect);
        foregroundRect.intersect(clipRect);
    }

    bool shouldPaintContent = false;
    bool hasBoundingBox = false;
    LayoutRect layerBounds;
    ClipRect backgroundRect;
    ClipRect foregroundRect;
    LayoutRect boundingBox;

    // Unique to paginated fragments. The physical translation to apply to shift the layer when painting/hit-testing.
    LayoutSize paginationOffset;

    // Also unique to paginated fragments. An additional clip that applies to the layer. It is in layer-local
    // (physical) coordinates.
    LayoutRect paginationClip;
};

typedef Vector<LayerFragment, 1> LayerFragments;

}

#endif
