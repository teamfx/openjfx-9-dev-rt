/* 
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
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

package javafx.scene.control;

/**
Builder class for javafx.scene.control.PopupControl
@see javafx.scene.control.PopupControl
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class PopupControlBuilder<B extends javafx.scene.control.PopupControlBuilder<B>> extends javafx.stage.PopupWindowBuilder<B> implements javafx.util.Builder<javafx.scene.control.PopupControl> {
    protected PopupControlBuilder() {
    }
    
    /** Creates a new instance of PopupControlBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.control.PopupControlBuilder<?> create() {
        return new javafx.scene.control.PopupControlBuilder();
    }
    
    private int __set;
    private void __set(int i) {
        __set |= 1 << i;
    }
    public void applyTo(javafx.scene.control.PopupControl x) {
        super.applyTo(x);
        int set = __set;
        while (set != 0) {
            int i = Integer.numberOfTrailingZeros(set);
            set &= ~(1 << i);
            switch (i) {
                case 0: x.setId(this.id); break;
                case 1: x.setMaxHeight(this.maxHeight); break;
                case 2: x.setMaxWidth(this.maxWidth); break;
                case 3: x.setMinHeight(this.minHeight); break;
                case 4: x.setMinWidth(this.minWidth); break;
                case 5: x.setPrefHeight(this.prefHeight); break;
                case 6: x.setPrefWidth(this.prefWidth); break;
                case 7: x.setSkin(this.skin); break;
                case 8: x.setStyle(this.style); break;
                case 9: x.getStyleClass().addAll(this.styleClass); break;
            }
        }
    }
    
    private java.lang.String id;
    /**
    Set the value of the {@link javafx.scene.control.PopupControl#getId() id} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B id(java.lang.String x) {
        this.id = x;
        __set(0);
        return (B) this;
    }
    
    private double maxHeight;
    /**
    Set the value of the {@link javafx.scene.control.PopupControl#getMaxHeight() maxHeight} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B maxHeight(double x) {
        this.maxHeight = x;
        __set(1);
        return (B) this;
    }
    
    private double maxWidth;
    /**
    Set the value of the {@link javafx.scene.control.PopupControl#getMaxWidth() maxWidth} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B maxWidth(double x) {
        this.maxWidth = x;
        __set(2);
        return (B) this;
    }
    
    private double minHeight;
    /**
    Set the value of the {@link javafx.scene.control.PopupControl#getMinHeight() minHeight} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B minHeight(double x) {
        this.minHeight = x;
        __set(3);
        return (B) this;
    }
    
    private double minWidth;
    /**
    Set the value of the {@link javafx.scene.control.PopupControl#getMinWidth() minWidth} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B minWidth(double x) {
        this.minWidth = x;
        __set(4);
        return (B) this;
    }
    
    private double prefHeight;
    /**
    Set the value of the {@link javafx.scene.control.PopupControl#getPrefHeight() prefHeight} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B prefHeight(double x) {
        this.prefHeight = x;
        __set(5);
        return (B) this;
    }
    
    private double prefWidth;
    /**
    Set the value of the {@link javafx.scene.control.PopupControl#getPrefWidth() prefWidth} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B prefWidth(double x) {
        this.prefWidth = x;
        __set(6);
        return (B) this;
    }
    
    private javafx.scene.control.Skin<?> skin;
    /**
    Set the value of the {@link javafx.scene.control.PopupControl#getSkin() skin} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B skin(javafx.scene.control.Skin<?> x) {
        this.skin = x;
        __set(7);
        return (B) this;
    }
    
    private java.lang.String style;
    /**
    Set the value of the {@link javafx.scene.control.PopupControl#getStyle() style} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B style(java.lang.String x) {
        this.style = x;
        __set(8);
        return (B) this;
    }
    
    private java.util.Collection<? extends java.lang.String> styleClass;
    /**
    Add the given items to the List of items in the {@link javafx.scene.control.PopupControl#getStyleClass() styleClass} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B styleClass(java.util.Collection<? extends java.lang.String> x) {
        this.styleClass = x;
        __set(9);
        return (B) this;
    }
    
    /**
    Add the given items to the List of items in the {@link javafx.scene.control.PopupControl#getStyleClass() styleClass} property for the instance constructed by this builder.
    */
    public B styleClass(java.lang.String... x) {
        return styleClass(java.util.Arrays.asList(x));
    }
    
    /**
    Make an instance of {@link javafx.scene.control.PopupControl} based on the properties set on this builder.
    */
    public javafx.scene.control.PopupControl build() {
        javafx.scene.control.PopupControl x = new javafx.scene.control.PopupControl();
        applyTo(x);
        return x;
    }
}
