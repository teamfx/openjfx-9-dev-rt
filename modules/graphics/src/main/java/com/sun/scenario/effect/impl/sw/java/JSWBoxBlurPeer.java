/*
 * Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This file was originally generated by JSLC
 * and then hand edited for performance.
 */

package com.sun.scenario.effect.impl.sw.java;

import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.BoxBlur;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;

public class JSWBoxBlurPeer extends JSWEffectPeer {

    public JSWBoxBlurPeer(FilterContext fctx, Renderer r, String uniqueName) {
        super(fctx, r, uniqueName);
    }

    @Override
    protected final BoxBlur getEffect() {
        return (BoxBlur)super.getEffect();
    }

    @Override
    public ImageData filter(Effect effect,
                            BaseTransform transform,
                            Rectangle outputClip,
                            ImageData... inputs)
    {
        // NOTE: for now, all input images must be TYPE_INT_ARGB_PRE
        setEffect(effect);
        Rectangle dstBounds = getResultBounds(transform, outputClip, inputs);

        // Calculate the amount the image grows on each iteration (size-1)
        boolean horizontal = (getPass() == 0);
        int hinc = horizontal ? getEffect().getHorizontalSize() - 1 : 0;
        int vinc = horizontal ? 0 : getEffect().getVerticalSize() - 1;
        int iterations = getEffect().getPasses();
        if (iterations < 1 || (hinc < 1 && vinc < 1)) {
            inputs[0].addref();
            return inputs[0];
        }
        // Calculate the amount the image will grow through the full operation
        // Always upgrade to the next even amount of growth
        int growx = (hinc * iterations + 1) & (~0x1);
        int growy = (vinc * iterations + 1) & (~0x1);

        // Assert: ((FilterEffect) effect).operatesInUserSpace()...
        // NOTE: We could still have a transformed ImageData for other reasons...
        HeapImage src = (HeapImage)inputs[0].getUntransformedImage();
        Rectangle srcr = inputs[0].getUntransformedBounds();

        HeapImage cur = src;
        int curw = srcr.width;
        int curh = srcr.height;
        int curscan = cur.getScanlineStride();
        int[] curPixels = cur.getPixelArray();

        int finalw = curw + growx;
        int finalh = curh + growy;
        while (curw < finalw || curh < finalh) {
            int neww = curw + hinc;
            int newh = curh + vinc;
            if (neww > finalw) neww = finalw;
            if (newh > finalh) newh = finalh;
            HeapImage dst = (HeapImage)getRenderer().getCompatibleImage(neww, newh);
            int newscan = dst.getScanlineStride();
            int[] newPixels = dst.getPixelArray();
            if (horizontal) {
                filterHorizontal(newPixels, neww, newh, newscan,
                                 curPixels, curw, curh, curscan);
            } else {
                filterVertical(newPixels, neww, newh, newscan,
                               curPixels, curw, curh, curscan);
            }
            if (cur != src) {
                getRenderer().releaseCompatibleImage(cur);
            }
            cur = dst;
            curw = neww;
            curh = newh;
            curPixels = newPixels;
            curscan = newscan;
        }

        Rectangle resBounds =
            new Rectangle(srcr.x - growx/2, srcr.y - growy/2, curw, curh);
        return new ImageData(getFilterContext(), cur, resBounds);
    }

    protected void filterHorizontal(int dstPixels[], int dstw, int dsth, int dstscan,
                                    int srcPixels[], int srcw, int srch, int srcscan)
    {
        int hsize = dstw - srcw + 1;
        int kscale = 0x7fffffff / (hsize * 255);
        int srcoff = 0;
        int dstoff = 0;
        for (int y = 0; y < dsth; y++) {
            int suma = 0;
            int sumr = 0;
            int sumg = 0;
            int sumb = 0;
            for (int x = 0; x < dstw; x++) {
                int rgb;
                // Un-accumulate the data for col-hsize location into the sums.
                rgb = (x >= hsize) ? srcPixels[srcoff + x - hsize] : 0;
                suma -= (rgb >>> 24);
                sumr -= (rgb >>  16) & 0xff;
                sumg -= (rgb >>   8) & 0xff;
                sumb -= (rgb       ) & 0xff;
                // Accumulate the data for this col location into the sums.
                rgb = (x < srcw) ? srcPixels[srcoff + x] : 0;
                suma += (rgb >>> 24);
                sumr += (rgb >>  16) & 0xff;
                sumg += (rgb >>   8) & 0xff;
                sumb += (rgb       ) & 0xff;
                dstPixels[dstoff + x] =
                    (((suma * kscale) >> 23) << 24) +
                    (((sumr * kscale) >> 23) << 16) +
                    (((sumg * kscale) >> 23) <<  8) +
                    (((sumb * kscale) >> 23)      );
            }
            srcoff += srcscan;
            dstoff += dstscan;
        }
    }

    protected void filterVertical(int dstPixels[], int dstw, int dsth, int dstscan,
                                  int srcPixels[], int srcw, int srch, int srcscan)
    {
        int vsize = dsth - srch + 1;
        int kscale = 0x7fffffff / (vsize * 255);
        int voff = vsize * srcscan;
        for (int x = 0; x < dstw; x++) {
            int suma = 0;
            int sumr = 0;
            int sumg = 0;
            int sumb = 0;
            int srcoff = x;
            int dstoff = x;
            for (int y = 0; y < dsth; y++) {
                int rgb;
                // Un-accumulate the data for row-vsize location into the sums.
                rgb = (srcoff >= voff) ? srcPixels[srcoff - voff] : 0;
                suma -= (rgb >>> 24);
                sumr -= (rgb >>  16) & 0xff;
                sumg -= (rgb >>   8) & 0xff;
                sumb -= (rgb       ) & 0xff;
                // Accumulate the data for this row location into the sums.
                rgb = (y < srch) ? srcPixels[srcoff] : 0;
                suma += (rgb >>> 24);
                sumr += (rgb >>  16) & 0xff;
                sumg += (rgb >>   8) & 0xff;
                sumb += (rgb       ) & 0xff;
                dstPixels[dstoff] =
                    (((suma * kscale) >> 23) << 24) +
                    (((sumr * kscale) >> 23) << 16) +
                    (((sumg * kscale) >> 23) <<  8) +
                    (((sumb * kscale) >> 23)      );
                srcoff += srcscan;
                dstoff += dstscan;
            }
        }
    }

    /*
     * This is a useful routine for some uses - it goes faster than the
     * horizontal-only and vertical-only loops, but it is hard to use it
     * in the face of multi-pass box blurs and having to adjust for even
     * blur sizes, so it is commented out for now...
    private void filterTranspose(int dstPixels[], int dstw, int dsth, int dstscan,
                                 int srcPixels[], int srcw, int srch, int srcscan,
                                 int ksize)
    {
        int kscale = 0x7fffffff / (ksize * 255);
        int srcoff = 0;
        for (int y = 0; y < dstw; y++) {
            int suma = 0;
            int sumr = 0;
            int sumg = 0;
            int sumb = 0;
            int dstoff = y;
            for (int x = 0; x < dsth; x++) {
                int rgb;
                // Un-accumulate the data for col-ksize location into the sums.
                rgb = (x >= ksize) ? srcPixels[srcoff + x - ksize] : 0;
                suma -= (rgb >>> 24);
                sumr -= (rgb >>  16) & 0xff;
                sumg -= (rgb >>   8) & 0xff;
                sumb -= (rgb       ) & 0xff;
                // Accumulate the data for this col location into the sums.
                rgb = (x < srcw) ? srcPixels[srcoff + x] : 0;
                suma += (rgb >>> 24);
                sumr += (rgb >>  16) & 0xff;
                sumg += (rgb >>   8) & 0xff;
                sumb += (rgb       ) & 0xff;
                dstPixels[dstoff] =
                    (((suma * kscale) >> 23) << 24) +
                    (((sumr * kscale) >> 23) << 16) +
                    (((sumg * kscale) >> 23) <<  8) +
                    (((sumb * kscale) >> 23)      );
                dstoff += dstscan;
            }
            srcoff += srcscan;
        }
    }
     */
}