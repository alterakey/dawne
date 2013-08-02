README
=======

Copyright (C) 2011-2013 Takahiro Yoshimura <altakey@gmail.com>

This is a simple text-like file viewer application for the Android
platform.  It is also my learning project.


0. HOW TO BUILD
=================

[Fire emulator/device up...]

$ ant clean uninstall install
...
BUILD SUCCESSFUL
Total time: xx seconds


If you have my lil' launcher script installed somewhere in your PATH,
then you can use the 'run' Ant task to quickly run/test it.

It is available at: https://gist.github.com/1223663 .


1. FEATURES
=============

 * Attempts to auto-detect character sets

   Recognizes UTF-8, UTF-16BE, UTF-16LE, Japanese, Chinese (Simplified
   and Traditional), Korean character sets.

   Thanks to the jchardet project (http://jchardet.sourceforge.net/).

 * 4 color themes

   White-on-black (energy saving on OLED display, and eye saver under
   dark conditions) and black-on-white are supported.
   Update: Solarized Color theme (dark & light)
 
 * Configurable font size 

 * Scrolling with volume keys, with configurable amount
 
 * Words-searching/finding

 * Selectable/copyable text (only in Honeycomb or above)
 
 * Now, it can choose text file from installed file manager
 
 * Recent files


2. BUGS
=========

 * None known.

3. ACKNOWLEDGES
=================

Icon is from the Nuovo theme, is work of SILVESTREHERRERA
(http://www.silvestre.com.ar/?p=5.)

Character set detection is done with the jchardet project
(http://jchardet.sourceforge.net/.)

Word searching, text selection/copy feature is kindly contributed by
Renjaya Raga Zenta <ragazenta@gmail.com>.

Solarized Color: Precision colors for machines and people
(http://ethanschoonover.com/solarized)

Saving recent files use ObjectSerializer (https://github.com/apache/pig)
