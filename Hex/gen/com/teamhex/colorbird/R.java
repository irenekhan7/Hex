/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * aapt tool from the resource data it found.  It
 * should not be modified by hand.
 */

package com.teamhex.colorbird;

public final class R {
    public static final class array {
        public static final int edit_options_array=0x7f060000;
        public static final int select_options_array=0x7f060001;
    }
    public static final class attr {
        /** <p>Must be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static final int exampleColor=0x7f010002;
        /** <p>Must be a dimension value, which is a floating point number appended with a unit such as "<code>14.5sp</code>".
Available units are: px (pixels), dp (density-independent pixels), sp (scaled pixels based on preferred font size),
in (inches), mm (millimeters).
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static final int exampleDimension=0x7f010001;
        /** <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static final int exampleDrawable=0x7f010003;
        /** <p>Must be a string value, using '\\;' to escape characters such as '\\n' or '\\uxxxx' for a unicode character.
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static final int exampleString=0x7f010000;
    }
    public static final class color {
        /**  Retrieved via Resources.getColor() and friends. 
         */
        public static final int red=0x7f040000;
    }
    public static final class dimen {
        /**  Default screen margins, per the Android Design guidelines. 

         Customize dimensions originally defined in res/values/dimens.xml (such as
         screen margins) for sw720dp devices (e.g. 10" tablets) in landscape here.
    
         */
        public static final int activity_horizontal_margin=0x7f050000;
        public static final int activity_vertical_margin=0x7f050001;
    }
    public static final class drawable {
        public static final int hexcolor=0x7f020000;
        /**  Retrieved via Resources.getDrawable() and friends. 
         */
        public static final int semi_black=0x7f020001;
    }
    public static final class id {
        public static final int LinearLayout1=0x7f0a0002;
        public static final int RelativeLayout1=0x7f0a0000;
        public static final int action_settings=0x7f0a0015;
        public static final int button_capture=0x7f0a0004;
        public static final int button_delete=0x7f0a0012;
        public static final int button_edit=0x7f0a0013;
        public static final int button_import=0x7f0a0005;
        public static final int button_open_library=0x7f0a0006;
        public static final int button_save=0x7f0a000e;
        public static final int button_share=0x7f0a0011;
        public static final int camera_preview=0x7f0a0003;
        public static final int colorInfo=0x7f0a0010;
        public static final int drawing_view=0x7f0a0007;
        public static final int editName=0x7f0a000b;
        public static final int name=0x7f0a0014;
        public static final int paletteName=0x7f0a000f;
        public static final int palette_view=0x7f0a000c;
        public static final int preview_palette=0x7f0a0009;
        public static final int save_create_button=0x7f0a000a;
        public static final int schemeList=0x7f0a0001;
        public static final int spinner_edit=0x7f0a000d;
        public static final int spinner_select=0x7f0a0008;
    }
    public static final class layout {
        public static final int activity_library=0x7f030000;
        public static final int activity_main=0x7f030001;
        public static final int activity_palette_create=0x7f030002;
        public static final int activity_palette_edit=0x7f030003;
        public static final int activity_palette_info=0x7f030004;
        public static final int listcolor=0x7f030005;
    }
    public static final class menu {
        public static final int palette_edit=0x7f090000;
        public static final int scheme_info=0x7f090001;
    }
    public static final class string {
        public static final int action_settings=0x7f070005;
        public static final int app_name=0x7f070007;
        public static final int back=0x7f070001;
        public static final int clear=0x7f070002;
        public static final int hello_world=0x7f070006;
        /**  This is a complex string containing style runs. 
         */
        public static final int main_label=0x7f070003;
        /**  Simple strings. 
         */
        public static final int skeleton_app=0x7f070000;
        public static final int title_activity_palette_edit=0x7f070008;
        public static final int title_activity_scheme_info=0x7f070004;
    }
    public static final class style {
        public static final int HexTheme=0x7f080000;
        public static final int HexTheme_NoTitleBar=0x7f080001;
    }
    public static final class styleable {
        /** Attributes that can be used with a PaletteView.
           <p>Includes the following attributes:</p>
           <table>
           <colgroup align="left" />
           <colgroup align="left" />
           <tr><th>Attribute</th><th>Description</th></tr>
           <tr><td><code>{@link #PaletteView_exampleColor com.teamhex.colorbird:exampleColor}</code></td><td></td></tr>
           <tr><td><code>{@link #PaletteView_exampleDimension com.teamhex.colorbird:exampleDimension}</code></td><td></td></tr>
           <tr><td><code>{@link #PaletteView_exampleDrawable com.teamhex.colorbird:exampleDrawable}</code></td><td></td></tr>
           <tr><td><code>{@link #PaletteView_exampleString com.teamhex.colorbird:exampleString}</code></td><td></td></tr>
           </table>
           @see #PaletteView_exampleColor
           @see #PaletteView_exampleDimension
           @see #PaletteView_exampleDrawable
           @see #PaletteView_exampleString
         */
        public static final int[] PaletteView = {
            0x7f010000, 0x7f010001, 0x7f010002, 0x7f010003
        };
        /**
          <p>This symbol is the offset where the {@link com.teamhex.colorbird.R.attr#exampleColor}
          attribute's value can be found in the {@link #PaletteView} array.


          <p>Must be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          @attr name com.teamhex.colorbird:exampleColor
        */
        public static final int PaletteView_exampleColor = 2;
        /**
          <p>This symbol is the offset where the {@link com.teamhex.colorbird.R.attr#exampleDimension}
          attribute's value can be found in the {@link #PaletteView} array.


          <p>Must be a dimension value, which is a floating point number appended with a unit such as "<code>14.5sp</code>".
Available units are: px (pixels), dp (density-independent pixels), sp (scaled pixels based on preferred font size),
in (inches), mm (millimeters).
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          @attr name com.teamhex.colorbird:exampleDimension
        */
        public static final int PaletteView_exampleDimension = 1;
        /**
          <p>This symbol is the offset where the {@link com.teamhex.colorbird.R.attr#exampleDrawable}
          attribute's value can be found in the {@link #PaletteView} array.


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          @attr name com.teamhex.colorbird:exampleDrawable
        */
        public static final int PaletteView_exampleDrawable = 3;
        /**
          <p>This symbol is the offset where the {@link com.teamhex.colorbird.R.attr#exampleString}
          attribute's value can be found in the {@link #PaletteView} array.


          <p>Must be a string value, using '\\;' to escape characters such as '\\n' or '\\uxxxx' for a unicode character.
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          @attr name com.teamhex.colorbird:exampleString
        */
        public static final int PaletteView_exampleString = 0;
    };
}
