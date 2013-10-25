import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class BmpParse {

  private File srcImage;
  private Graphics2D graph;

  public BmpParse(String imagefile) {
    srcImage = new File(imagefile);
  }

  public BmpParse(File imagefile) {
    srcImage = imagefile;
  }

  public boolean Reader(Graphics2D graph) {
    Image image; //target image

    if (!srcImage.exists())
    {
      System.out.println("bmp file is not exist!");
      return false;
    }

    try {
      FileInputStream fs = new FileInputStream(srcImage);
      int bflen = 14;
      byte bf[] = new byte[bflen];
      fs.read(bf, 0, bflen); //read 14 byte the head of bmp file
      int bilen = 40;
      byte bi[] = new byte[bilen];
      fs.read(bi, 0, bilen); //read 40 byte the head of bmp info

      // /u83B7取一些重要数据
      int nwidth = ( ( (int) bi[7] & 0xff) << 24) //width of source file
          | ( ( (int) bi[6] & 0xff) << 16)
          | ( ( (int) bi[5] & 0xff) << 8)
          | (int) bi[4] & 0xff;
      System.out.println("Width:" + nwidth);
      int nheight = ( ( (int) bi[11] & 0xff) << 24) //heigth of source file
          | ( ( (int) bi[10] & 0xff) << 16)
          | ( ( (int) bi[9] & 0xff) << 8)
          | (int) bi[8] & 0xff;
      System.out.println("Heigth:" + nheight);
      //bits of source file
      int nbitcount = ( ( (int) bi[15] & 0xff) << 8) | (int) bi[14] & 0xff;
      System.out.println("Bits Count:" + nbitcount);
      //size of source file
      int nsizeimage = ( ( (int) bi[23] & 0xff) << 24)
          | ( ( (int) bi[22] & 0xff) << 16)
          | ( ( (int) bi[21] & 0xff) << 8)
          | (int) bi[20] & 0xff;
      System.out.println("Size:" + nsizeimage);

      //parse the 24 bit bmp file
      if (nbitcount == 24) {
        int npad = (nsizeimage / nheight) - nwidth * 3;
        int ndata[] = new int[nheight * nwidth];
        byte brgb[] = new byte[ (nwidth + npad) * 3 * nheight];
        fs.read(brgb, 0, (nwidth + npad) * 3 * nheight);
        int nindex = 0;
        for (int j = 0; j < nheight; j++) {
          for (int i = 0; i < nwidth; i++) {
            ndata[nwidth * (nheight - j - 1) + i] =
                (255 & 0xff) << 24
                | ( ( (int) brgb[nindex + 2] & 0xff) << 16)
                | ( ( (int) brgb[nindex + 1] & 0xff) << 8)
                | (int) brgb[nindex] & 0xff;
            nindex += 3;
          }
          nindex += npad;
        }
        Toolkit kit = Toolkit.getDefaultToolkit();
        image = kit.createImage(new MemoryImageSource(nwidth, nheight,
            ndata, 0, nwidth));
        System.out.println("get image from bmp file");
      }
      else {
        image = (Image)null;
        System.out.println("not 24 bit bitmap, failed!");
        return false;
      }
      fs.close(); //close input stream

      // start extract image file
      int wideth = image.getWidth(null);
      int height = image.getHeight(null);
      BufferedImage tag = new BufferedImage(wideth, height,
                                            BufferedImage.TYPE_INT_RGB);

/*      tag.getGraphics().drawImage(image, 0, 0, wideth / 2, height / 2, null);
      FileOutputStream out = new FileOutputStream("newfile.jpg");
      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
      encoder.encode(tag);
      out.close(); //close output stream
  */
      graph.drawImage(image, 0, 0, wideth, height, null);
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  /**
  loadbitmap() method converted from Windows C code.
  Reads only uncompressed 24- and 8-bit images.  Tested with
  images saved using Microsoft Paint in Windows 95.  If the image
  is not a 24- or 8-bit image, the program refuses to even try.
  I guess one could include 4-bit images by masking the byte
  by first 1100 and then 0011.  I am not really
  interested in such images.  If a compressed image is attempted,
  the routine will probably fail by generating an IOException.
  Look for variable ncompression to be different from 0 to indicate
  compression is present.

  Arguments:
      sdir and sfile are the result of the FileDialog()
      getDirectory() and getFile() methods.

  Returns:
      Image Object, be sure to check for (Image)null !!!! */

 //public Image loadbitmap(Graphics2D graph) {
  public Image loadbitmap() {
   Image image;
   System.out.println("loading:" + srcImage.getName());
   try {
     FileInputStream fs = new FileInputStream(srcImage);
     int bflen = 14; // 14 byte BITMAPFILEHEADER
     byte bf[] = new byte[bflen];
     fs.read(bf, 0, bflen);
     int bilen = 40; // 40-byte BITMAPINFOHEADER
     byte bi[] = new byte[bilen];
     fs.read(bi, 0, bilen);

     // Interperet data.
     int nsize = ( ( (int) bf[5] & 0xff) << 24)
         | ( ( (int) bf[4] & 0xff) << 16)
         | ( ( (int) bf[3] & 0xff) << 8)
         | (int) bf[2] & 0xff;
     System.out.println("File type is :" + (char) bf[0] + (char) bf[1]);
     System.out.println("Size of file is :" + nsize);

     // Head info data
     int nbisize = ( ( (int) bi[3] & 0xff) << 24)
         | ( ( (int) bi[2] & 0xff) << 16)
         | ( ( (int) bi[1] & 0xff) << 8)
         | (int) bi[0] & 0xff;
     System.out.println("Size of bitmapinfoheader is :" + nbisize);

     int nwidth = ( ( (int) bi[7] & 0xff) << 24)
         | ( ( (int) bi[6] & 0xff) << 16)
         | ( ( (int) bi[5] & 0xff) << 8)
         | (int) bi[4] & 0xff;
     System.out.println("Width is :" + nwidth);

     int nheight = ( ( (int) bi[11] & 0xff) << 24)
         | ( ( (int) bi[10] & 0xff) << 16)
         | ( ( (int) bi[9] & 0xff) << 8)
         | (int) bi[8] & 0xff;
     System.out.println("Height is :" + nheight);

     // number of planes in this bitmap
     int nplanes = ( ( (int) bi[13] & 0xff) << 8) | (int) bi[12] & 0xff;
     System.out.println("Planes is :" + nplanes);

     // bits per pixel used to store palette entry
     // information. this also identifies in an indirect way
     // the number of possible colors. possible values are: 1, 4, 8, 16, 24, 32
     int nbitcount = ( ( (int) bi[15] & 0xff) << 8) | (int) bi[14] & 0xff;
     System.out.println("BitCount is :" + nbitcount);

     // Look for non-zero values to indicate compression
     int ncompression = ( ( (int) bi[19]) << 24)
         | ( ( (int) bi[18]) << 16)
         | ( ( (int) bi[17]) << 8)
         | (int) bi[16];
     System.out.println("Compression is :" + ncompression);

     int nsizeimage = ( ( (int) bi[23] & 0xff) << 24)
         | ( ( (int) bi[22] & 0xff) << 16)
         | ( ( (int) bi[21] & 0xff) << 8)
         | (int) bi[20] & 0xff;
     System.out.println("SizeImage is :" + nsizeimage);

     int nxpm = ( ( (int) bi[27] & 0xff) << 24)
         | ( ( (int) bi[26] & 0xff) << 16)
         | ( ( (int) bi[25] & 0xff) << 8)
         | (int) bi[24] & 0xff;
     System.out.println("X-Pixels per meter is :" + nxpm);

     int nypm = ( ( (int) bi[31] & 0xff) << 24)
         | ( ( (int) bi[30] & 0xff) << 16)
         | ( ( (int) bi[29] & 0xff) << 8)
         | (int) bi[28] & 0xff;
     System.out.println("Y-Pixels per meter is :" + nypm);

     int nclrused = ( ( (int) bi[35] & 0xff) << 24)
         | ( ( (int) bi[34] & 0xff) << 16)
         | ( ( (int) bi[33] & 0xff) << 8)
         | (int) bi[32] & 0xff;
     System.out.println("Colors used are :" + nclrused);

     int nclrimp = ( ( (int) bi[39] & 0xff) << 24)
         | ( ( (int) bi[38] & 0xff) << 16)
         | ( ( (int) bi[37] & 0xff) << 8)
         | (int) bi[36] & 0xff;
     System.out.println("Colors important are :" + nclrimp);

     // Some bitmaps do not have the sizeimage field calculated
     // Ferret out these cases and fix 'em.
     if (nsizeimage == 0) {
       nsizeimage = ( ( ( (nwidth * nbitcount) + 31) & ~31) >> 3);
       nsizeimage *= nheight;
       System.out.println("nsizeimage (backup) is" + nsizeimage);
     }

     if (nbitcount == 32)
     {
       // No Palatte data for 32-bit format but scan lines are
       // padded out to even 4-byte boundaries.
       int npad = (nsizeimage / nheight) - nwidth * 4;
       int ndata[] = new int[nheight * nwidth];
       byte brgb[] = new byte[ (nwidth + npad) * 4 * nheight];
       fs.read(brgb, 0, (nwidth + npad) * 4 * nheight);
       int nindex = 0;
       for (int j = 0; j < nheight; j++) {
         for (int i = 0; i < nwidth; i++) {
           ndata[nwidth * (nheight - j - 1) + i] =
               (255 & 0xff) << 24
               | ( ( (int) brgb[nindex + 2] & 0xff) << 16)
               | ( ( (int) brgb[nindex + 1] & 0xff) << 8)
               | (int) brgb[nindex] & 0xff;
/*           System.out.println("Encoded Color at ("
                              + i + "," + j + ")is:" + brgb + " (R,G,B)= ("
                              + ( (int) (brgb[2]) & 0xff) + ","
                              + ( (int) brgb[1] & 0xff) + ","
                              + ( (int) brgb[0] & 0xff) + ")"); */
           nindex += 4;
         }
         nindex += npad;
       }

       image = Toolkit.getDefaultToolkit().createImage
           (new MemoryImageSource(nwidth, nheight,
                                  ndata, 0, nwidth));
     }
     else if (nbitcount == 24) {
       // No Palatte data for 24-bit format but scan lines are
       // padded out to even 4-byte boundaries.
       int npad = (nsizeimage / nheight) - nwidth * 3;
       int ndata[] = new int[nheight * nwidth];
       byte brgb[] = new byte[ (nwidth + npad) * 3 * nheight];
       fs.read(brgb, 0, (nwidth + npad) * 3 * nheight);
       int nindex = 0;
       for (int j = 0; j < nheight; j++) {
         for (int i = 0; i < nwidth; i++) {
           ndata[nwidth * (nheight - j - 1) + i] =
               (255 & 0xff) << 24
               | ( ( (int) brgb[nindex + 2] & 0xff) << 16)
               | ( ( (int) brgb[nindex + 1] & 0xff) << 8)
               | (int) brgb[nindex] & 0xff;
/*           System.out.println("Encoded Color at ("
                              + i + "," + j + ")is:" + brgb + " (R,G,B)= ("
                              + ( (int) (brgb[2]) & 0xff) + ","
                              + ( (int) brgb[1] & 0xff) + ","
                              + ( (int) brgb[0] & 0xff) + ")");*/
           nindex += 3;
         }
         nindex += npad;
       }

       image = Toolkit.getDefaultToolkit().createImage
           (new MemoryImageSource(nwidth, nheight,
                                  ndata, 0, nwidth));
     }
     else if (nbitcount == 16) {
       // No Palatte data for 16-bit format but scan lines are
       // padded out to even 4-byte boundaries.
       int npad = (nsizeimage / nheight) - nwidth * 2;
       int ndata[] = new int[nheight * nwidth];
       byte brgb[] = new byte[ (nwidth + npad) * 2 * nheight];
       fs.read(brgb, 0, (nwidth + npad) * 2 * nheight);
       int nindex = 0;
       for (int j = 0; j < nheight; j++) {
         for (int i = 0; i < nwidth; i++) {
           ndata[nwidth * (nheight - j - 1) + i] =
               (255 & 0xff) << 24
               | ( ( ( ((int)brgb[nindex + 1] >>> 2) & 0x3f) | 0x60)<<3 << 16)
               | ( ( ( (int) (((brgb[nindex + 1]&0x3)<<3) | ((brgb[nindex]&0xe0)>>>5))) | 0x60)<<3 << 8)
               | ( ( ( (int) brgb[nindex] & 0x1f) | 0x60)<<3);

           nindex += 2;
         }
         nindex += npad;
       }

       image = Toolkit.getDefaultToolkit().createImage
           (new MemoryImageSource(nwidth, nheight,
                                  ndata, 0, nwidth));
     }
     else if (nbitcount == 8) {
       // Have to determine the number of colors, the clrsused
       // parameter is dominant if it is greater than zero.  If
       // zero1, calculate colors based on bitsperpixel.
       int nNumColors = 0;
       if (nclrused > 0) {
         nNumColors = nclrused;
       }
       else {
         nNumColors = (1 & 0xff) << nbitcount;
       }
       System.out.println("The number of Colors is" + nNumColors);

       // Read the palatte colors.
       int npalette[] = new int[nNumColors];
       byte bpalette[] = new byte[nNumColors * 4];
       fs.read(bpalette, 0, nNumColors * 4);
       int nindex8 = 0;
       for (int n = 0; n < nNumColors; n++) {
         npalette[n] = (255 & 0xff) << 24
             | ( ( (int) bpalette[nindex8 + 2] & 0xff) << 16)
             | ( ( (int) bpalette[nindex8 + 1] & 0xff) << 8)
             | (int) bpalette[nindex8] & 0xff;
/*         System.out.println("Palette Color " + n
                            + " is:" + npalette[n] + " (res,R,G,B)= ("
                            + ( (int) (bpalette[nindex8 + 3]) & 0xff) + ","
                            + ( (int) (bpalette[nindex8 + 2]) & 0xff) + ","
                            + ( (int) bpalette[nindex8 + 1] & 0xff) + ","
                            + ( (int) bpalette[nindex8] & 0xff) + ")"); */
         nindex8 += 4;
       }

       // Read the image data (actually indices into the palette)
       // Scan lines are still padded out to even 4-byte
       // boundaries.
       int npad8 = (nsizeimage / nheight) - nwidth;
       System.out.println("nPad is:" + npad8);

       int ndata8[] = new int[nwidth * nheight];
       byte bdata[] = new byte[ (nwidth + npad8) * nheight];
       fs.read(bdata, 0, (nwidth + npad8) * nheight);
       nindex8 = 0;
       for (int j8 = 0; j8 < nheight; j8++) {
         for (int i8 = 0; i8 < nwidth; i8++) {
           ndata8[nwidth * (nheight - j8 - 1) + i8] =
               npalette[ ( (int) bdata[nindex8] & 0xff)];
           nindex8++;
         }
         nindex8 += npad8;
       }

       image = Toolkit.getDefaultToolkit().createImage
           (new MemoryImageSource(nwidth, nheight,
                                  ndata8, 0, nwidth));
     }
     else if (nbitcount == 4) {
         // Have to determine the number of colors, the clrsused
         // parameter is dominant if it is greater than zero.  If
         // zero1, calculate colors based on bitsperpixel.
         int nNumColors = 0;
         if (nclrused > 0) {
           nNumColors = nclrused;
         }
         else {
           nNumColors = (1 & 0xff) << nbitcount;
         }
         System.out.println("The number of Colors is" + nNumColors);

         // Read the palatte colors.
         int npalette[] = new int[nNumColors];
         byte bpalette[] = new byte[nNumColors * 4];
         fs.read(bpalette, 0, nNumColors * 4);
         int nindex4 = 0;
         for (int n = 0; n < nNumColors; n++) {
           npalette[n] = (255 & 0xff) << 24
               | ( ( (int) bpalette[nindex4 + 2] & 0xff) << 16)
               | ( ( (int) bpalette[nindex4 + 1] & 0xff) << 8)
               | (int) bpalette[nindex4] & 0xff;
  /*         System.out.println("Palette Color " + n
                              + " is:" + npalette[n] + " (res,R,G,B)= ("
                              + ( (int) (bpalette[nindex8 + 3]) & 0xff) + ","
                              + ( (int) (bpalette[nindex8 + 2]) & 0xff) + ","
                              + ( (int) bpalette[nindex8 + 1] & 0xff) + ","
                              + ( (int) bpalette[nindex8] & 0xff) + ")"); */
           nindex4 += 4;
       }

       // Scan line is padded with zeroes to be a multiple of four bytes
       int scanLineSize = ( ( (nwidth * nbitcount) + 31) & ~31) >> 3;

       // Read the image data (actually indices into the palette)
       // Scan lines are still padded out to even 4-byte
       // boundaries.
       //int npad4 = (nsizeimage / nheight) - nwidth / 2;
       /*int npad4 = 0;
       if ((nwidth%2) == 0)
         npad4 = scanLineSize - nwidth/2;
       else
         npad4 = scanLineSize - nwidth/2 - 1;
       System.out.println("nPad is:" + npad4);*/

       int ndata4[] = new int[nwidth * nheight];
       //byte bdata[] = new byte[ scanLineSize * nheight];
       byte blinedata[] = new byte[scanLineSize];
       //fs.read(bdata, 0, scanLineSize * nheight);
       nindex4 = 0;
       for (int j4 = 0; j4 < nheight; j4++) {
         fs.read(blinedata, 0, scanLineSize);
         nindex4 = 0;

         for (int i4 = 0; i4 < nwidth; i4++) {

           if (nwidth * (nheight - j4 - 1) + i4 > nwidth * nheight-1)
             break;
           if (nindex4 > scanLineSize * nheight - 1)
             break;

           for (int pixPerByte = 0; pixPerByte < 2; pixPerByte++)
           {
             if (pixPerByte == 0)
             {
               ndata4[nwidth * (nheight - j4 - 1) + i4] =
                   npalette[ ( (int) (blinedata[nindex4]>>4) & 0xf)];
               i4++;
               if (i4 >= nwidth)
                 break;
             }
             else
             {
               ndata4[nwidth * (nheight - j4 - 1) + i4] =
                   npalette[ ( (int) blinedata[nindex4] & 0xf)];
             }
           }

           nindex4++;
         }
       }

       image = Toolkit.getDefaultToolkit().createImage
           (new MemoryImageSource(nwidth, nheight,
                                  ndata4, 0, nwidth));
     }
     else if (nbitcount == 1) {
       // Have to determine the number of colors, the clrsused
       // parameter is dominant if it is greater than zero.  If
       // zero1, calculate colors based on bitsperpixel.
       int nNumColors = 0;
       if (nclrused > 0) {
         nNumColors = nclrused;
       }
       else {
         nNumColors = (1 & 0xff) << nbitcount;
       }
       System.out.println("The number of Colors is" + nNumColors);

       // Read the palatte colors.
       int npalette[] = new int[nNumColors];
       byte bpalette[] = new byte[nNumColors * 4];
       fs.read(bpalette, 0, nNumColors * 4);
       int nindex1 = 0;
       for (int n = 0; n < nNumColors; n++) {
         npalette[n] = (255 & 0xff) << 24
             | ( ( (int) bpalette[nindex1 + 2] & 0xff) << 16)
             | ( ( (int) bpalette[nindex1 + 1] & 0xff) << 8)
             | (int) bpalette[nindex1] & 0xff;
         nindex1 += 4;
       }

       // Scan line is padded with zeroes to be a multiple of four bytes
       int scanLineSize = ( ( (nwidth * nbitcount) + 31) & ~31) >> 3;

       int ndata1[] = new int[nwidth * nheight];
       //byte bdata[] = new byte[ scanLineSize * nheight];
       byte blinedata[] = new byte[scanLineSize];
       //fs.read(bdata, 0, scanLineSize * nheight);
       nindex1 = 0;
       for (int j1 = 0; j1 < nheight; j1++) {
         fs.read(blinedata, 0, scanLineSize);
         nindex1 = 0;

         for (int i1 = 0; i1 < nwidth; i1++) {

           if (nwidth * (nheight - j1 - 1) + i1 > nwidth * nheight-1)
             break;
           if (nindex1 > scanLineSize * nheight - 1)
             break;

           for (int pixPerByte = 0; pixPerByte < 8; pixPerByte++)
           {
             int shift = 8 - pixPerByte - 1;
             ndata1[nwidth * (nheight - j1 - 1) + i1] =
                 npalette[ ( (int) (blinedata[nindex1]>>shift) & 0x1)];
             if (pixPerByte != 7)
             {
               i1++;
               if (i1 >= nwidth)
                 break;
             }
           }

           nindex1++;
         }
       }

       image = Toolkit.getDefaultToolkit().createImage
           (new MemoryImageSource(nwidth, nheight,
                                  ndata1, 0, nwidth));
     }
     else {
       System.out.println("Not a 32-bit, 24-bit, 16-bit, 8-bit, 4-bit and 1-bit Windows Bitmap, aborting...");
       image = (Image)null;
     }

     fs.close();
     return image;
   }
   catch (Exception e) {
     e.printStackTrace(System.out);
     System.out.println("Caught exception in loadbitmap!");
   }
   return (Image)null;
 }
}