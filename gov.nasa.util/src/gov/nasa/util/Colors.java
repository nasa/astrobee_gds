/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.util;

/**
 * Encapsulates standardized color sets such as X11/WC3 web colors,
 * ISS DGCS colors, etc.
 */
public interface Colors {
    public int[] rgb();
    
    /**
     * Standard X11/WC3 color names as described at:<br> 
     * <a href=http://en.wikipedia.org/wiki/Web_colors#X11_color_names>http://en.wikipedia.org/wiki/Web_colors</a> 
     */
    public enum X11 implements Colors {
        IndianRed           (205,  92,  92),
        LightCoral          (240, 128, 128),
        Salmon              (250, 128, 114),
        DarkSalmon          (233, 150, 122),
        LightSalmon         (255, 160, 122),
        Red                 (255,   0,   0),
        Crimson             (220,  20,  60),
        FireBrick           (178,  34,  34),
        DarkRed             (139,   0,   0),
        Pink                (255, 192, 203),
        LightPink           (255, 182, 193),
        HotPink             (255, 105, 180),
        DeepPink            (255,  20, 147),
        MediumVioletRed     (199,  21, 133),
        PaleVioletRed       (219, 112, 147),
        Coral               (255, 127,  80),
        Tomato              (255,  99,  71),
        OrangeRed           (255,  69,   0),
        DarkOrange          (255, 140,   0),
        Orange              (255, 165,   0),
        Gold                (255, 215,   0),
        Yellow              (255, 255,   0),
        Yellow1             (237, 237,   0),
        Yellow2             (205, 205,   0),
        Yellow3             (140, 140,   0),
        LightYellow         (255, 255, 224),
        LemonChiffon        (255, 250, 205),
        LightGoldenrodYellow(250, 250, 210),
        PapayaWhip          (255, 239, 213),
        Moccasin            (255, 228, 181),
        PeachPuff           (255, 218, 185),
        PaleGoldenrod       (238, 232, 170),
        Khaki               (240, 230, 140),
        DarkKhaki           (189, 183, 107),
        Lavender            (230, 230, 250),
        Thistle             (216, 191, 216),
        Plum                (221, 160, 221),
        Violet              (238, 130, 238),
        Orchid              (218, 112, 214),
        Fuchsia             (255,   0, 255),
        Magenta             (255,   0, 255),
        MediumOrchid        (186,  85, 211),
        MediumPurple        (147, 112, 219),
        BlueViolet          (138,  43, 226),
        DarkViolet          (148,   0, 211),
        DarkOrchid          (153,  50, 204),
        DarkMagenta         (139,   0, 139),
        Purple              (128,   0, 128),
        Indigo              ( 75,   0, 130),
        DarkSlateBlue       ( 72,  61, 139),
        SlateBlue           (106,  90, 205),
        MediumSlateBlue     (123, 104, 238),
        GreenYellow         (173, 255,  47),
        Chartreuse          (127, 255,   0),
        LawnGreen           (124, 252,   0),
        Lime                (  0, 255,   0),
        LimeGreen           ( 50, 205,  50),
        PaleGreen           (152, 251, 152),
        LightGreen          (144, 238, 144),
        MediumSpringGreen   (  0, 250, 154),
        SpringGreen         (  0, 255, 127),
        MediumSeaGreen      ( 60, 179, 113),
        SeaGreen            ( 46, 139,  87),
        ForestGreen         ( 34, 139,  34),
        Green               (  0, 128,   0),
        DarkGreen           (  0, 100,   0),
        YellowGreen         (154, 205,  50),
        OliveDrab           (107, 142,  35),
        Olive               (128, 128,   0),
        DarkOliveGreen      ( 85, 107,  47),
        MediumAquamarine    (102, 205, 170),
        DarkSeaGreen        (143, 188, 143),
        LightSeaGreen       ( 32, 178, 170),
        DarkCyan            (  0, 139, 139),
        Teal                (  0, 128, 128),
        Aqua                (  0, 255, 255),
        Cyan                (  0, 255, 255),
        LightCyan           (224, 255, 255),
        PaleTurquoise       (175, 238, 238),
        Aquamarine          (127, 255, 212),
        Turquoise           ( 64, 224, 208),
        MediumTurquoise     ( 72, 209, 204),
        DarkTurquoise       (  0, 206, 209),
        CadetBlue           ( 95, 158, 160),
        SteelBlue           ( 70, 130, 180),
        LightSteelBlue      (176, 196, 222),
        PowderBlue          (176, 224, 230),
        LightBlue           (173, 216, 230),
        SkyBlue             (135, 206, 235),
        LightSkyBlue        (135, 206, 250),
        DeepSkyBlue         (  0, 191, 255),
        DodgerBlue          ( 30, 144, 255),
        CornflowerBlue      (100, 149, 237),
        RoyalBlue           ( 65, 105, 225),
        Blue                (  0,   0, 255),
        MediumBlue          (  0,   0, 205),
        DarkBlue            (  0,   0, 139),
        Navy                (  0,   0, 128),
        MidnightBlue        ( 25,  25, 112),
        Cornsilk            (255, 248, 220),
        BlanchedAlmond      (255, 235, 205),
        Bisque              (255, 228, 196),
        NavajoWhite         (255, 222, 173),
        Wheat               (245, 222, 179),
        BurlyWood           (222, 184, 135),
        Tan                 (210, 180, 140),
        RosyBrown           (188, 143, 143),
        SandyBrown          (244, 164,  96),
        Goldenrod           (218, 165,  32),
        DarkGoldenrod       (184, 134,  11),
        Peru                (205, 133,  63),
        Chocolate           (210, 105,  30),
        SaddleBrown         (139,  69,  19),
        Sienna              (160,  82,  45),
        Brown               (165,  42,  42),
        Maroon              (128,   0,   0),
        White               (255, 255, 255),
        Snow                (255, 250, 250),
        Honeydew            (240, 255, 240),
        MintCream           (245, 255, 250),
        Azure               (240, 255, 255),
        AliceBlue           (240, 248, 255),
        GhostWhite          (248, 248, 255),
        WhiteSmoke          (245, 245, 245),
        Seashell            (255, 245, 238),
        Beige               (245, 245, 220),
        OldLace             (253, 245, 230),
        FloralWhite         (255, 250, 240),
        Ivory               (255, 255, 240),
        AntiqueWhite        (250, 235, 215),
        Linen               (250, 240, 230),
        LavenderBlush       (255, 240, 245),
        MistyRose           (255, 228, 225),
        Gainsboro           (220, 220, 220),
        LightGray           (211, 211, 211),
        Silver              (192, 192, 192),
        DarkGray            (169, 169, 169),
        Gray                (128, 128, 128),
        DimGray             (105, 105, 105),
        LightSlateGray      (119, 136, 153),
        SlateGray           (112, 128, 144),
        DarkSlateGray       ( 47,  79,  79),
        Black               (  0,   0,   0),
        Gray10              ( 26,  26,  26),
        Gray20              ( 51,  51,  51),
        Gray30              ( 77,  77,  77),
        Gray40              (102, 102, 102),
        Gray50              (128, 128, 128),
        Gray60              (153, 153, 153),
        Gray70              (179, 179, 179),
        Gray80              (204, 204, 204),
        Gray90              (230, 230, 230),
        ;

        private final int[] rgb = new int[3];
        X11(int r, int g, int b) {
            rgb[0] = r;
            rgb[1] = g;
            rgb[2] = b;
        }
        public int[] rgb() {
            return rgb;
        }
    }
    
    /**
     * Colors as defined in SSP 50313, ISS Display and Graphics Commonality Standards (DGCS). 
     */
    public enum ISS {
        Aquamarine3 (102, 205, 170),
        Bisque3     (205, 183, 158),
        Black       (  0,   0,   0),
        Blue4       (  0,   0, 139),
        CobaltBlue  (  0, 135, 237),
        DodgerBlue  ( 30, 144, 255),
        LightBlue   (173, 216, 230),
        RoyalBlue   ( 65, 105, 225),
        Brown       (165,  42,  42),
        DarkBrown   (102,  51,   0),
        LightBrown  (204, 127,  50),
        SandyBrown  (244, 164,  96),
        Cyan        (  0, 255, 255),
        Gray83UNIX  (212, 212, 212),
        Gray83PC    (192, 192, 192),
        Green       (  0, 255,   0),
        DarkGreen   (  0, 105,   0),
        Green4      (  0, 139,   0),
        LightGreen  (144, 238, 144),
        Palegreen2  (144, 238, 144),
        Palegreen   (152, 251, 152),
        LightGreen2 (204, 238, 204),
        Honeydew3   (193, 205, 193),
        Magenta     (255,   0, 255),
        MistyRose3  (205, 183, 181),
        OliveDrab   (107, 142,  35),
        Orange      (255, 165,   0),
        BurntOrange (253, 118,  35),
        DarkOrange  (255, 153,   0),
        DarkOrchid  (153,  50, 204),
        HotPink     (255, 105, 180),
        Plum        (221, 160, 221),
        Purple      (160,  32, 240),
        Red         (255,   0,   0),
        Teal        (  0, 128, 128),
        Violet      (238, 130, 238),
        White       (255, 255, 255),
        Yellow      (255, 255,   0),
        DarkYellow  (128, 128,   0),
        PaleYellow  (255, 255, 225),
        MidnightBlue( 25,  25, 112),
        DarkRed     (205,   0,   0);
        
        private final int[] rgb = new int[3];
        ISS(int r, int g, int b) {
            rgb[0] = r;
            rgb[1] = g;
            rgb[2] = b;
        }   
        public int[] rgb() {
            return rgb;
        }
    }
    
}
