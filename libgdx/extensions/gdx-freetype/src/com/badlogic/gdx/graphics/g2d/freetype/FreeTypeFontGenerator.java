package com.badlogic.gdx.graphics.g2d.freetype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.PixmapPacker.GuillotineStrategy;
import com.badlogic.gdx.graphics.g2d.PixmapPacker.PackStrategy;
import com.badlogic.gdx.graphics.g2d.PixmapPacker.PixmapPackerRectangle;
import com.badlogic.gdx.graphics.g2d.PixmapPacker.SkylineStrategy;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Bitmap;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Face;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.GlyphMetrics;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.GlyphSlot;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Library;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.SizeMetrics;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Stroker;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * Generates {@link BitmapFont} and {@link BitmapFontData} instances from TrueType, OTF, and other FreeType supported fonts.
 * </p>
 * <p>
 * Usage example:
 *
 * <pre>
 * FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(&quot;myfont.ttf&quot;));
 * BitmapFont font = gen.generateFont(16);
 * gen.dispose(); // Don't dispose if doing incremental glyph generation.
 * </pre>
 * <p>
 * The generator has to be disposed once it is no longer used. The returned {@link BitmapFont} instances are managed by the user
 * and have to be disposed as usual.
 */
public class FreeTypeFontGenerator implements Disposable {
    static public final String DEFAULT_CHARS = "\u0000ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890\"!`?'.,;:()[]{}<>|/@\\^$€-%+=#_&~*\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F\u00A0\u00A1\u00A2\u00A3\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA\u00AB\u00AC\u00AD\u00AE\u00AF\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u00B7\u00B8\u00B9\u00BA\u00BB\u00BC\u00BD\u00BE\u00BF\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF\u00D0\u00D1\u00D2\u00D3\u00D4\u00D5\u00D6\u00D7\u00D8\u00D9\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF\u00F0\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F7\u00F8\u00F9\u00FA\u00FB\u00FC\u00FD\u00FE\u00FF";

    /**
     * A hint to scale the texture as needed, without capping it at any maximum size
     */
    static public final int NO_MAXIMUM = -1;

    /**
     * The maximum texture size allowed by generateData, when storing in a texture atlas. Multiple texture pages will be created
     * if necessary. Default is 1024.
     *
     * @see #setMaxTextureSize(int)
     */
    static private int maxTextureSize = 1024;

    final Library library;
    final Face face;
    final String name;
    boolean bitmapped = false;
    private int pixelWidth, pixelHeight;

    /**
     * {@link #FreeTypeFontGenerator(FileHandle, int)}
     */
    public FreeTypeFontGenerator(FileHandle fontFile) {
        this(fontFile, 0);
    }

    /**
     * Creates a new generator from the given font file. Uses {@link FileHandle#length()} to determine the file size. If the file
     * length could not be determined (it was 0), an extra copy of the font bytes is performed. Throws a
     * {@link GdxRuntimeException} if loading did not succeed.
     */
    public FreeTypeFontGenerator(FileHandle fontFile, int faceIndex) {
        name = fontFile.nameWithoutExtension();
        library = FreeType.initFreeType();
        face = library.newFace(fontFile, faceIndex);
        if (checkForBitmapFont()) return;
        setPixelSizes(0, 15);
    }

    /**
     * Returns the maximum texture size that will be used by generateData() when creating a texture atlas for the glyphs.
     *
     * @return the power-of-two max texture size
     */
    public static int getMaxTextureSize() {
        return maxTextureSize;
    }

    /**
     * Sets the maximum size that will be used when generating texture atlases for glyphs with <tt>generateData()</tt>. The
     * default is 1024. By specifying {@link #NO_MAXIMUM}, the texture atlas will scale as needed.
     * <p>
     * The power-of-two square texture size will be capped to the given <tt>texSize</tt>. It's recommended that a power-of-two
     * value be used here.
     * <p>
     * Multiple pages may be used to fit all the generated glyphs. You can query the resulting number of pages by calling
     * <tt>bitmapFont.getRegions().length</tt> or <tt>freeTypeBitmapFontData.getTextureRegions().length</tt>.
     * <p>
     * If PixmapPacker is specified when calling generateData, this parameter is ignored.
     *
     * @param texSize the maximum texture size for one page of glyphs
     */
    public static void setMaxTextureSize(int texSize) {
        maxTextureSize = texSize;
    }

    private int getLoadingFlags(FreeTypeFontParameter parameter) {
        int loadingFlags = FreeType.FT_LOAD_DEFAULT;
        switch (parameter.hinting) {
            case None:
                loadingFlags |= FreeType.FT_LOAD_NO_HINTING;
                break;
            case Slight:
                loadingFlags |= FreeType.FT_LOAD_TARGET_LIGHT;
                break;
            case Medium:
                loadingFlags |= FreeType.FT_LOAD_TARGET_NORMAL;
                break;
            case Full:
                loadingFlags |= FreeType.FT_LOAD_TARGET_MONO;
                break;
            case AutoSlight:
                loadingFlags |= FreeType.FT_LOAD_FORCE_AUTOHINT | FreeType.FT_LOAD_TARGET_LIGHT;
                break;
            case AutoMedium:
                loadingFlags |= FreeType.FT_LOAD_FORCE_AUTOHINT | FreeType.FT_LOAD_TARGET_NORMAL;
                break;
            case AutoFull:
                loadingFlags |= FreeType.FT_LOAD_FORCE_AUTOHINT | FreeType.FT_LOAD_TARGET_MONO;
                break;
        }
        return loadingFlags;
    }

    private boolean loadChar(int c) {
        return loadChar(c, FreeType.FT_LOAD_DEFAULT | FreeType.FT_LOAD_FORCE_AUTOHINT);
    }

    private boolean loadChar(int c, int flags) {
        return face.loadChar(c, flags);
    }

    private boolean checkForBitmapFont() {
        int faceFlags = face.getFaceFlags();
        if (((faceFlags & FreeType.FT_FACE_FLAG_FIXED_SIZES) == FreeType.FT_FACE_FLAG_FIXED_SIZES)
                && ((faceFlags & FreeType.FT_FACE_FLAG_HORIZONTAL) == FreeType.FT_FACE_FLAG_HORIZONTAL)) {
            if (loadChar(32)) {
                GlyphSlot slot = face.getGlyph();
                if (slot.getFormat() == 1651078259) {
                    bitmapped = true;
                }
            }
        }
        return bitmapped;
    }

    public BitmapFont generateFont(FreeTypeFontParameter parameter) {
        return generateFont(parameter, new FreeTypeBitmapFontData());
    }

    /**
     * Generates a new {@link BitmapFont}. The size is expressed in pixels. Throws a GdxRuntimeException if the font could not be
     * generated. Using big sizes might cause such an exception.
     *
     * @param parameter configures how the font is generated
     */
    public BitmapFont generateFont(FreeTypeFontParameter parameter, FreeTypeBitmapFontData data) {
        boolean updateTextureRegions = data.regions == null && parameter.packer != null;
        if (updateTextureRegions) data.regions = new Array();
        generateData(parameter, data);
        if (updateTextureRegions)
            parameter.packer.updateTextureRegions(data.regions, parameter.minFilter, parameter.magFilter, parameter.genMipMaps);
        if (data.regions.isEmpty()) throw new GdxRuntimeException("Unable to create a font with no texture regions.");
        BitmapFont font = newBitmapFont(data, data.regions, true);
        font.setOwnsTexture(parameter.packer == null);
        return font;
    }

    /**
     * Called by generateFont to create a new {@link BitmapFont} instance. This allows injecting a customized {@link BitmapFont},
     * eg for a RTL font.
     *
     * @see BitmapFont#BitmapFont(BitmapFontData, Array, boolean)
     */
    protected BitmapFont newBitmapFont(BitmapFontData data, Array<TextureRegion> pageRegions, boolean integer) {
        return new BitmapFont(data, pageRegions, integer);
    }

    /**
     * Uses ascender and descender of font to calculate real height that makes all glyphs to fit in given pixel size. Source:
     * http://nothings.org/stb/stb_truetype.h / stbtt_ScaleForPixelHeight
     */
    public int scaleForPixelHeight(int height) {
        setPixelSizes(0, height);
        SizeMetrics fontMetrics = face.getSize().getMetrics();
        int ascent = FreeType.toInt(fontMetrics.getAscender());
        int descent = FreeType.toInt(fontMetrics.getDescender());
        return height * height / (ascent - descent);
    }

    /**
     * Uses max advance, ascender and descender of font to calculate real height that makes any n glyphs to fit in given pixel
     * width.
     *
     * @param width    the max width to fit (in pixels)
     * @param numChars max number of characters that to fill width
     */
    public int scaleForPixelWidth(int width, int numChars) {
        SizeMetrics fontMetrics = face.getSize().getMetrics();
        int advance = FreeType.toInt(fontMetrics.getMaxAdvance());
        int ascent = FreeType.toInt(fontMetrics.getAscender());
        int descent = FreeType.toInt(fontMetrics.getDescender());
        int unscaledHeight = ascent - descent;
        int height = unscaledHeight * width / (advance * numChars);
        setPixelSizes(0, height);
        return height;
    }

    /**
     * Uses max advance, ascender and descender of font to calculate real height that makes any n glyphs to fit in given pixel
     * width and height.
     *
     * @param width    the max width to fit (in pixels)
     * @param height   the max height to fit (in pixels)
     * @param numChars max number of characters that to fill width
     */
    public int scaleToFitSquare(int width, int height, int numChars) {
        return Math.min(scaleForPixelHeight(height), scaleForPixelWidth(width, numChars));
    }

    /**
     * Returns null if glyph was not found in the font. If there is nothing to render, for example with various space characters,
     * then {@link GlyphAndBitmap#bitmap} will be null.
     */
    public @Nullable GlyphAndBitmap generateGlyphAndBitmap(int c, int size, boolean flip) {
        setPixelSizes(0, size);

        SizeMetrics fontMetrics = face.getSize().getMetrics();
        int baseline = FreeType.toInt(fontMetrics.getAscender());

        // Check if character exists in this font.
        // 0 means 'undefined character code'
        if (face.getCharIndex(c) == 0) {
            return null;
        }

        // Try to load character
        if (!loadChar(c)) {
            throw new GdxRuntimeException("Unable to load character!");
        }

        GlyphSlot slot = face.getGlyph();

        // Try to render to bitmap
        Bitmap bitmap;
        if (bitmapped) {
            bitmap = slot.getBitmap();
        } else if (!slot.renderGlyph(FreeType.FT_RENDER_MODE_NORMAL)) {
            bitmap = null;
        } else {
            bitmap = slot.getBitmap();
        }

        GlyphMetrics metrics = slot.getMetrics();

        Glyph glyph = new Glyph();
        if (bitmap != null) {
            glyph.width = bitmap.getWidth();
            glyph.height = bitmap.getRows();
        } else {
            glyph.width = 0;
            glyph.height = 0;
        }
        glyph.xoffset = slot.getBitmapLeft();
        glyph.yoffset = flip ? -slot.getBitmapTop() + baseline : -(glyph.height - slot.getBitmapTop()) - baseline;
        glyph.xadvance = FreeType.toInt(metrics.getHoriAdvance());
        glyph.srcX = 0;
        glyph.srcY = 0;
        glyph.id = c;

        GlyphAndBitmap result = new GlyphAndBitmap();
        result.glyph = glyph;
        result.bitmap = bitmap;
        return result;
    }

    /**
     * Generates a new {@link BitmapFontData} instance, expert usage only. Throws a GdxRuntimeException if something went wrong.
     *
     * @param size the size in pixels
     */
    public FreeTypeBitmapFontData generateData(int size) {
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        return generateData(parameter);
    }

    public FreeTypeBitmapFontData generateData(FreeTypeFontParameter parameter) {
        return generateData(parameter, new FreeTypeBitmapFontData());
    }

    void setPixelSizes(int pixelWidth, int pixelHeight) {
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        if (!bitmapped && !face.setPixelSizes(pixelWidth, pixelHeight))
            throw new GdxRuntimeException("Couldn't set size for font");
    }

    /**
     * Generates a new {@link BitmapFontData} instance, expert usage only. Throws a GdxRuntimeException if something went wrong.
     *
     * @param parameter configures how the font is generated
     */
    public FreeTypeBitmapFontData generateData(FreeTypeFontParameter parameter, FreeTypeBitmapFontData data) {
        data.name = name + "-" + parameter.size;
        char[] characters = parameter.characters.toCharArray();
        int charactersLength = characters.length;
        boolean incremental = parameter.incremental;
        int flags = getLoadingFlags(parameter);

        setPixelSizes(0, parameter.size);

        // set general font data
        SizeMetrics fontMetrics = face.getSize().getMetrics();
        data.flipped = parameter.flip;
        data.ascent = FreeType.toInt(fontMetrics.getAscender());
        data.descent = FreeType.toInt(fontMetrics.getDescender());
        data.lineHeight = FreeType.toInt(fontMetrics.getHeight());
        float baseLine = data.ascent;

        // if bitmapped
        if (bitmapped && (data.lineHeight == 0)) {
            for (int c = 32; c < (32 + face.getNumGlyphs()); c++) {
                if (loadChar(c, flags)) {
                    int lh = FreeType.toInt(face.getGlyph().getMetrics().getHeight());
                    data.lineHeight = (lh > data.lineHeight) ? lh : data.lineHeight;
                }
            }
        }
        data.lineHeight += parameter.spaceY;

        // determine space width
        if (loadChar(' ', flags) || loadChar('l', flags)) {
            data.spaceXadvance = FreeType.toInt(face.getGlyph().getMetrics().getHoriAdvance());
        } else {
            data.spaceXadvance = face.getMaxAdvanceWidth(); // Possibly very wrong.
        }

        // determine x-height
        for (char xChar : data.xChars) {
            if (!loadChar(xChar, flags)) continue;
            data.xHeight = FreeType.toInt(face.getGlyph().getMetrics().getHeight());
            break;
        }
        if (data.xHeight == 0) throw new GdxRuntimeException("No x-height character found in font");

        // determine cap height
        for (char capChar : data.capChars) {
            if (!loadChar(capChar, flags)) continue;
            data.capHeight = FreeType.toInt(face.getGlyph().getMetrics().getHeight()) + Math.abs(parameter.shadowOffsetY);
            break;
        }
        if (!bitmapped && data.capHeight == 1) throw new GdxRuntimeException("No cap character found in font");

        data.ascent -= data.capHeight;
        data.down = -data.lineHeight;
        if (parameter.flip) {
            data.ascent = -data.ascent;
            data.down = -data.down;
        }

        boolean ownsAtlas = false;

        PixmapPacker packer = parameter.packer;

        if (packer == null) {
            // Create a packer.
            int size;
            PackStrategy packStrategy;
            if (incremental) {
                size = maxTextureSize;
                packStrategy = new GuillotineStrategy();
            } else {
                int maxGlyphHeight = (int) Math.ceil(data.lineHeight);
                size = MathUtils.nextPowerOfTwo((int) Math.sqrt(maxGlyphHeight * maxGlyphHeight * charactersLength));
                if (maxTextureSize > 0) size = Math.min(size, maxTextureSize);
                packStrategy = new SkylineStrategy();
            }
            ownsAtlas = true;
            packer = new PixmapPacker(size, size, Format.RGBA8888, 1, false, packStrategy);
            packer.setTransparentColor(parameter.color);
            packer.getTransparentColor().a = 0;
            if (parameter.borderWidth > 0) {
                packer.setTransparentColor(parameter.borderColor);
                packer.getTransparentColor().a = 0;
            }
        }

        if (incremental) data.glyphs = new Array(charactersLength + 32);

        Stroker stroker = null;
        if (parameter.borderWidth > 0) {
            stroker = library.createStroker();
            stroker.set((int) (parameter.borderWidth * 64f),
                    parameter.borderStraight ? FreeType.FT_STROKER_LINECAP_BUTT : FreeType.FT_STROKER_LINECAP_ROUND,
                    parameter.borderStraight ? FreeType.FT_STROKER_LINEJOIN_MITER_FIXED : FreeType.FT_STROKER_LINEJOIN_ROUND, 0);
        }

        // Create glyphs largest height first for best packing.
        int[] heights = new int[charactersLength];
        for (int i = 0; i < charactersLength; i++) {
            char c = characters[i];

            int height = loadChar(c, flags) ? FreeType.toInt(face.getGlyph().getMetrics().getHeight()) : 0;
            heights[i] = height;

            if (c == '\0') {
                Glyph missingGlyph = createGlyph('\0', data, parameter, stroker, baseLine, packer);
                if (missingGlyph != null && missingGlyph.width != 0 && missingGlyph.height != 0) {
                    data.setGlyph('\0', missingGlyph);
                    data.missingGlyph = missingGlyph;
                    if (incremental) data.glyphs.add(missingGlyph);
                }
            }
        }
        int heightsCount = heights.length;
        while (heightsCount > 0) {
            int best = 0, maxHeight = heights[0];
            for (int i = 1; i < heightsCount; i++) {
                int height = heights[i];
                if (height > maxHeight) {
                    maxHeight = height;
                    best = i;
                }
            }

            char c = characters[best];
            if (data.getGlyph(c) == null) {
                Glyph glyph = createGlyph(c, data, parameter, stroker, baseLine, packer);
                if (glyph != null) {
                    data.setGlyph(c, glyph);
                    if (incremental) data.glyphs.add(glyph);
                }
            }

            heightsCount--;
            heights[best] = heights[heightsCount];
            char tmpChar = characters[best];
            characters[best] = characters[heightsCount];
            characters[heightsCount] = tmpChar;
        }

        if (stroker != null && !incremental) stroker.dispose();

        if (incremental) {
            data.generator = this;
            data.parameter = parameter;
            data.stroker = stroker;
            data.packer = packer;
        }

        // Generate kerning.
        parameter.kerning &= face.hasKerning();
        if (parameter.kerning) {
            for (int i = 0; i < charactersLength; i++) {
                char firstChar = characters[i];
                Glyph first = data.getGlyph(firstChar);
                if (first == null) continue;
                int firstIndex = face.getCharIndex(firstChar);
                for (int ii = i; ii < charactersLength; ii++) {
                    char secondChar = characters[ii];
                    Glyph second = data.getGlyph(secondChar);
                    if (second == null) continue;
                    int secondIndex = face.getCharIndex(secondChar);

                    int kerning = face.getKerning(firstIndex, secondIndex, 0); // FT_KERNING_DEFAULT (scaled then rounded).
                    if (kerning != 0) first.setKerning(secondChar, FreeType.toInt(kerning));

                    kerning = face.getKerning(secondIndex, firstIndex, 0); // FT_KERNING_DEFAULT (scaled then rounded).
                    if (kerning != 0) second.setKerning(firstChar, FreeType.toInt(kerning));
                }
            }
        }

        // Generate texture regions.
        if (ownsAtlas) {
            data.regions = new Array();
            packer.updateTextureRegions(data.regions, parameter.minFilter, parameter.magFilter, parameter.genMipMaps);
        }

        // Set space glyph.
        Glyph spaceGlyph = data.getGlyph(' ');
        if (spaceGlyph == null) {
            spaceGlyph = new Glyph();
            spaceGlyph.xadvance = (int) data.spaceXadvance + parameter.spaceX;
            spaceGlyph.id = ' ';
            data.setGlyph(' ', spaceGlyph);
        }
        if (spaceGlyph.width == 0) spaceGlyph.width = (int) (spaceGlyph.xadvance + data.padRight);

        return data;
    }

    /**
     * @return null if glyph was not found.
     */
    protected @Nullable Glyph createGlyph(char c, FreeTypeBitmapFontData data, FreeTypeFontParameter parameter, Stroker stroker,
                                      float baseLine, PixmapPacker packer) {

        boolean missing = face.getCharIndex(c) == 0 && c != 0;
        if (missing) return null;

        if (!loadChar(c, getLoadingFlags(parameter))) return null;

        GlyphSlot slot = face.getGlyph();
        FreeType.Glyph mainGlyph = slot.getGlyph();
        try {
            mainGlyph.toBitmap(parameter.mono ? FreeType.FT_RENDER_MODE_MONO : FreeType.FT_RENDER_MODE_NORMAL);
        } catch (GdxRuntimeException e) {
            mainGlyph.dispose();
            Gdx.app.log("FreeTypeFontGenerator", "Couldn't render char: " + c);
            return null;
        }
        Bitmap mainBitmap = mainGlyph.getBitmap();
        Pixmap mainPixmap = mainBitmap.getPixmap(Format.RGBA8888, parameter.color, parameter.gamma);

        if (mainBitmap.getWidth() != 0 && mainBitmap.getRows() != 0) {
            int offsetX = 0, offsetY = 0;
            if (parameter.borderWidth > 0) {
                // execute stroker; this generates a glyph "extended" along the outline
                int top = mainGlyph.getTop(), left = mainGlyph.getLeft();
                FreeType.Glyph borderGlyph = slot.getGlyph();
                borderGlyph.strokeBorder(stroker, false);
                borderGlyph.toBitmap(parameter.mono ? FreeType.FT_RENDER_MODE_MONO : FreeType.FT_RENDER_MODE_NORMAL);
                offsetX = left - borderGlyph.getLeft();
                offsetY = -(top - borderGlyph.getTop());

                // Render border (pixmap is bigger than main).
                Bitmap borderBitmap = borderGlyph.getBitmap();
                Pixmap borderPixmap = borderBitmap.getPixmap(Format.RGBA8888, parameter.borderColor, parameter.borderGamma);

                // Draw main glyph on top of border.
                for (int i = 0, n = parameter.renderCount; i < n; i++)
                    borderPixmap.drawPixmap(mainPixmap, offsetX, offsetY);

                mainPixmap.dispose();
                mainGlyph.dispose();
                mainPixmap = borderPixmap;
                mainGlyph = borderGlyph;
            }

            if (parameter.shadowOffsetX != 0 || parameter.shadowOffsetY != 0) {
                int mainW = mainPixmap.getWidth(), mainH = mainPixmap.getHeight();
                int shadowOffsetX = Math.max(parameter.shadowOffsetX, 0), shadowOffsetY = Math.max(parameter.shadowOffsetY, 0);
                int shadowW = mainW + Math.abs(parameter.shadowOffsetX), shadowH = mainH + Math.abs(parameter.shadowOffsetY);
                // use the Gdx2DPixmap constructor to avoid filling the pixmap twice
                Pixmap shadowPixmap = new Pixmap(
                        new Gdx2DPixmap(shadowW, shadowH, Pixmap.Format.toGdx2DPixmapFormat(mainPixmap.getFormat())));
                shadowPixmap.setColor(packer.getTransparentColor());
                shadowPixmap.fill();

                Color shadowColor = parameter.shadowColor;
                float a = shadowColor.a;
                if (a != 0) {
                    byte r = (byte) (shadowColor.r * 255), g = (byte) (shadowColor.g * 255), b = (byte) (shadowColor.b * 255);
                    ByteBuffer mainPixels = mainPixmap.getPixels();
                    ByteBuffer shadowPixels = shadowPixmap.getPixels();
                    for (int y = 0; y < mainH; y++) {
                        int shadowRow = shadowW * (y + shadowOffsetY) + shadowOffsetX;
                        for (int x = 0; x < mainW; x++) {
                            int mainPixel = (mainW * y + x) * 4;
                            byte mainA = mainPixels.get(mainPixel + 3);
                            if (mainA == 0) continue;
                            int shadowPixel = (shadowRow + x) * 4;
                            shadowPixels.put(shadowPixel, r);
                            shadowPixels.put(shadowPixel + 1, g);
                            shadowPixels.put(shadowPixel + 2, b);
                            shadowPixels.put(shadowPixel + 3, (byte) ((mainA & 0xff) * a));
                        }
                    }
                }

                // Draw main glyph (with any border) on top of shadow.
                for (int i = 0, n = parameter.renderCount; i < n; i++)
                    shadowPixmap.drawPixmap(mainPixmap, Math.max(-parameter.shadowOffsetX, 0), Math.max(-parameter.shadowOffsetY, 0));
                mainPixmap.dispose();
                mainPixmap = shadowPixmap;
            } else if (parameter.borderWidth == 0) {
                // No shadow and no border, draw glyph additional times.
                for (int i = 0, n = parameter.renderCount - 1; i < n; i++)
                    mainPixmap.drawPixmap(mainPixmap, 0, 0);
            }

            if (parameter.padTop > 0 || parameter.padLeft > 0 || parameter.padBottom > 0 || parameter.padRight > 0) {
                Pixmap padPixmap = new Pixmap(mainPixmap.getWidth() + parameter.padLeft + parameter.padRight,
                        mainPixmap.getHeight() + parameter.padTop + parameter.padBottom, mainPixmap.getFormat());
                padPixmap.setBlending(Blending.None);
                padPixmap.drawPixmap(mainPixmap, parameter.padLeft, parameter.padTop);
                mainPixmap.dispose();
                mainPixmap = padPixmap;
            }
        }

        GlyphMetrics metrics = slot.getMetrics();
        Glyph glyph = new Glyph();
        glyph.id = c;
        glyph.width = mainPixmap.getWidth();
        glyph.height = mainPixmap.getHeight();
        glyph.xoffset = mainGlyph.getLeft();
        if (parameter.flip)
            glyph.yoffset = -mainGlyph.getTop() + (int) baseLine;
        else
            glyph.yoffset = -(glyph.height - mainGlyph.getTop()) - (int) baseLine;
        glyph.xadvance = FreeType.toInt(metrics.getHoriAdvance()) + (int) parameter.borderWidth + parameter.spaceX;

        if (bitmapped) {
            mainPixmap.setColor(Color.CLEAR);
            mainPixmap.fill();
            ByteBuffer buf = mainBitmap.getBuffer();
            int whiteIntBits = Color.WHITE.toIntBits();
            int clearIntBits = Color.CLEAR.toIntBits();
            for (int h = 0; h < glyph.height; h++) {
                int idx = h * mainBitmap.getPitch();
                for (int w = 0; w < (glyph.width + glyph.xoffset); w++) {
                    int bit = (buf.get(idx + (w / 8)) >>> (7 - (w % 8))) & 1;
                    mainPixmap.drawPixel(w, h, ((bit == 1) ? whiteIntBits : clearIntBits));
                }
            }
        }

        PixmapPackerRectangle rect = packer.pack(mainPixmap);
        glyph.page = packer.getPages().indexOf(rect.page, true);
        glyph.srcX = (int) rect.x;
        glyph.srcY = (int) rect.y;

        // If a page was added, create a new texture region for the incrementally added glyph.
        if (parameter.incremental && data.regions != null && data.regions.size <= glyph.page)
            packer.updateTextureRegions(data.regions, parameter.minFilter, parameter.magFilter, parameter.genMipMaps);

        mainPixmap.dispose();
        mainGlyph.dispose();

        return glyph;
    }

    /**
     * check the font glyph exists for single UTF-32 code point
     */
    public boolean hasGlyph(int charCode) {
        // 0 stand for undefined character code
        return face.getCharIndex(charCode) != 0;
    }

    public String toString() {
        return name;
    }

    /**
     * Cleans up all resources of the generator. Call this if you no longer use the generator.
     */
    @Override
    public void dispose() {
        face.dispose();
        library.dispose();
    }

    /**
     * Font smoothing algorithm.
     */
    public enum Hinting {
        /**
         * Disable hinting. Generated glyphs will look blurry.
         */
        None,
        /**
         * Light hinting with fuzzy edges, but close to the original shape
         */
        Slight,
        /**
         * Average hinting
         */
        Medium,
        /**
         * Strong hinting with crisp edges at the expense of shape fidelity
         */
        Full,
        /**
         * Light hinting with fuzzy edges, but close to the original shape. Uses the FreeType auto-hinter.
         */
        AutoSlight,
        /**
         * Average hinting. Uses the FreeType auto-hinter.
         */
        AutoMedium,
        /**
         * Strong hinting with crisp edges at the expense of shape fidelity. Uses the FreeType auto-hinter.
         */
        AutoFull,
    }

    /**
     * {@link BitmapFontData} used for fonts generated via the {@link FreeTypeFontGenerator}. The texture storing the glyphs is
     * held in memory, thus the {@link #getImagePaths()} and {@link #getFontFile()} methods will return null.
     */
    static public class FreeTypeBitmapFontData extends BitmapFontData implements Disposable {
        public Array<TextureRegion> regions;

        // Fields for incremental glyph generation.
        FreeTypeFontGenerator generator;
        FreeTypeFontParameter parameter;
        Stroker stroker;
        PixmapPacker packer;
        Array<Glyph> glyphs;
        private boolean dirty;

        @Override
        public Glyph getGlyph(char ch) {
            Glyph glyph = super.getGlyph(ch);
            if (glyph == null && generator != null) {
                generator.setPixelSizes(0, parameter.size);
                float baseline = ((flipped ? -ascent : ascent) + capHeight) / scaleY;
                glyph = generator.createGlyph(ch, this, parameter, stroker, baseline, packer);
                if (glyph == null) return missingGlyph;

                setGlyphRegion(glyph, regions.get(glyph.page));
                setGlyph(ch, glyph);
                glyphs.add(glyph);
                dirty = true;

                Face face = generator.face;
                if (parameter.kerning) {
                    int glyphIndex = face.getCharIndex(ch);
                    for (int i = 0, n = glyphs.size; i < n; i++) {
                        Glyph other = glyphs.get(i);
                        int otherIndex = face.getCharIndex(other.id);

                        int kerning = face.getKerning(glyphIndex, otherIndex, 0);
                        if (kerning != 0) glyph.setKerning(other.id, FreeType.toInt(kerning));

                        kerning = face.getKerning(otherIndex, glyphIndex, 0);
                        if (kerning != 0) other.setKerning(ch, FreeType.toInt(kerning));
                    }
                }
            }
            return glyph;
        }

        public void getGlyphs(GlyphRun run, CharSequence str, int start, int end, Glyph lastGlyph) {
            if (packer != null)
                packer.setPackToTexture(true); // All glyphs added after this are packed directly to the texture.
            super.getGlyphs(run, str, start, end, lastGlyph);
            if (dirty) {
                dirty = false;
                packer.updateTextureRegions(regions, parameter.minFilter, parameter.magFilter, parameter.genMipMaps);
            }
        }

        @Override
        public void dispose() {
            if (stroker != null) stroker.dispose();
            if (packer != null) packer.dispose();
        }
    }

    /**
     * Parameter container class that helps configure how {@link FreeTypeBitmapFontData} and {@link BitmapFont} instances are
     * generated.
     * <p>
     * The packer field is for advanced usage, where it is necessary to pack multiple BitmapFonts (i.e. styles, sizes, families)
     * into a single Texture atlas. If no packer is specified, the generator will use its own PixmapPacker to pack the glyphs into
     * a power-of-two sized texture, and the resulting {@link FreeTypeBitmapFontData} will have a valid {@link TextureRegion} which
     * can be used to construct a new {@link BitmapFont}.
     */
    public static class FreeTypeFontParameter {
        /**
         * The size in pixels
         */
        public int size = 16;
        /**
         * If true, font smoothing is disabled.
         */
        public boolean mono;
        /**
         * Strength of hinting
         */
        public Hinting hinting = Hinting.AutoMedium;
        /**
         * Foreground color (required for non-black borders)
         */
        public Color color = Color.WHITE;
        /**
         * Glyph gamma. Values > 1 reduce antialiasing.
         */
        public float gamma = 1.8f;
        /**
         * Number of times to render the glyph. Useful with a shadow or border, so it doesn't show through the glyph.
         */
        public int renderCount = 2;
        /**
         * Border width in pixels, 0 to disable
         */
        public float borderWidth = 0;
        /**
         * Border color; only used if borderWidth > 0
         */
        public Color borderColor = Color.BLACK;
        /**
         * true for straight (mitered), false for rounded borders
         */
        public boolean borderStraight = false;
        /**
         * Values < 1 increase the border size.
         */
        public float borderGamma = 1.8f;
        /**
         * Offset of text shadow on X axis in pixels, 0 to disable
         */
        public int shadowOffsetX = 0;
        /**
         * Offset of text shadow on Y axis in pixels, 0 to disable
         */
        public int shadowOffsetY = 0;
        /**
         * Shadow color; only used if shadowOffset > 0. If alpha component is 0, no shadow is drawn but characters are still offset
         * by shadowOffset.
         */
        public Color shadowColor = new Color(0, 0, 0, 0.75f);
        /**
         * Pixels to add to glyph spacing when text is rendered. Can be negative.
         */
        public int spaceX, spaceY;
        /**
         * Pixels to add to the glyph in the texture. Cannot be negative.
         */
        public int padTop, padLeft, padBottom, padRight;
        /**
         * The characters the font should contain. If '\0' is not included then {@link BitmapFontData#missingGlyph} is not set.
         */
        public String characters = DEFAULT_CHARS;
        /**
         * Whether the font should include kerning
         */
        public boolean kerning = true;
        /**
         * The optional PixmapPacker to use for packing multiple fonts into a single texture.
         *
         * @see FreeTypeFontParameter
         */
        public PixmapPacker packer = null;
        /**
         * Whether to flip the font vertically
         */
        public boolean flip = false;
        /**
         * Whether to generate mip maps for the resulting texture
         */
        public boolean genMipMaps = false;
        /**
         * Minification filter
         */
        public TextureFilter minFilter = TextureFilter.Nearest;
        /**
         * Magnification filter
         */
        public TextureFilter magFilter = TextureFilter.Nearest;
        /**
         * When true, glyphs are rendered on the fly to the font's glyph page textures as they are needed. The
         * FreeTypeFontGenerator must not be disposed until the font is no longer needed. The FreeTypeBitmapFontData must be
         * disposed (separately from the generator) when the font is no longer needed. The FreeTypeFontParameter should not be
         * modified after creating a font. If a PixmapPacker is not specified, the font glyph page textures will use
         * {@link FreeTypeFontGenerator#getMaxTextureSize()}.
         */
        public boolean incremental;
    }

    public class GlyphAndBitmap {
        public Glyph glyph;
        public @Nullable Bitmap bitmap;
    }
}
