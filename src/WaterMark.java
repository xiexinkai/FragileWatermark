import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class WaterMark {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WaterMark main = new WaterMark();
		String fileNameHead = "faeriedragon";
		String fileNameHead2 = "faeriedragon_watermark3";
		String fileNameTail = "bmp";
		// main.go();
		// main.deal(fileNameHead,fileNameTail);
		//main.check(fileNameHead2, fileNameTail);
		WaterMark.calPSNR("faeriedragon.bmp", "faeriedragon_watermark1_repair.bmp");

	}

	JFrame frame;

	/*
	 * public void go() { frame = new JFrame("Fragile Watermark");
	 * frame.setSize(600, 600);
	 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); CrabPanel panel =
	 * new CrabPanel("lena.bmp"); frame.getContentPane().add(panel);
	 * frame.setVisible(true); }
	 */

	int height;
	int width;
	int numBands;
	int data[][][];
	BufferedImage image;
	WritableRaster raster;

	public void readPic(String fileNameHead, String fileNameTail) {
		try {
			// Read pic
			System.out.print("Read:"+fileNameHead + "." + fileNameTail+"     ");
			image = ImageIO.read(new File(fileNameHead + "." + fileNameTail));
			System.out.print("width=" + image.getWidth() + "  height="
					+ image.getHeight());
			// System.out.println(image.getType());
			raster = image.getRaster();
			height = image.getHeight();
			width = image.getWidth();
			numBands = raster.getNumBands();
			data = new int[height][width][numBands];
			System.out.println("   NumBands=" + raster.getNumBands());
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					raster.getPixel(j, i, data[i][j]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * save the pic in "xxx_watermark.bmp",bmp only.
	 * 
	 * @param fileNameHead
	 */
	public void savePic(String fileName) {
		try {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					raster.setPixel(j, i, data[i][j]);
				}
			}
			ImageIO.write(image, "bmp", new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * add watermark,and save the new picture
	 * 
	 * @param fileNameHead
	 * @param fileNameTail
	 */
	public void deal(String fileNameHead, String fileNameTail) {
		// CrabPanel panel=new CrabPanel("lena.bmp");
		// BmpParse bmp=new BmpParse("lena_attack.bmp");
		// Graphics2D graph=new Graphics2D();
		// BufferedImage image=(BufferedImage)(bmp.loadbitmap());
		// ImageIcon icon=new ImageIcon(bmp.loadbitmap());
		// BufferedImage image=new BufferedImage();
		// System.out.println(icon.getIconHeight()+"   "+icon.getIconWidth());
		// return panel;
		// Image image =
		// Toolkit.getDefaultToolkit().getImage("lena_attack.bmp");
		// ImageIcon icon=new ImageIcon(image);
		readPic(fileNameHead, fileNameTail);
		/*
		 * Start to add watermark
		 */
		/*
		 * create the WATERCRAB[][] code int temp; for(int i=0;i<height;i++){
		 * System.out.print("{"); for(int j=0;j<width;j++){
		 * if(data[i][j][0]==0){ temp=0; }else{ temp=1; }
		 * System.out.print(temp); if(j!=width-1){ System.out.print(","); } }
		 * System.out.println("},"); }
		 */
		// The first step:add a 0-1 picture in the 6th bit
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				for (int k = 0; k < 1; k++) {
					if (calSum(data[i][j][k], WATERCRAB[i % WATERCRABSIZE][j
							% WATERCRABSIZE]) == 0) {
						data[i][j][k] &= 0xfd;
					} else {
						data[i][j][k] |= 0x2;
					}
				}
			}
		}
		//--------------
		int pW = width / 3;
		int pH = height / 3;
		for (int k = 0; k < numBands; k++) {
			for (int i = 0; i < pH; i++) {
				for (int j = 0; j < pW; j++) {
					LSBCode rc = new LSBCode(data[i][j][k], data[i][j + pW][k],
							data[i][j + 2 * pW][k], data[i + pH][j][k], data[i
									+ pH][j + pW][k],
							data[i + pH][j + 2 * pW][k],
							data[i + 2 * pH][j][k],
							data[i + 2 * pH][j + pW][k], data[i + 2 * pH][j + 2
									* pW][k]);
					rc.calCode();
					data[i][j][k]=rc.a[0];
					data[i][j+pW][k]=rc.a[1];
					data[i][j+2*pW][k]=rc.a[2];
					data[i+pH][j][k]=rc.a[7];
					data[i+pH][j+pW][k]=rc.a[8];
					data[i+pH][j+2*pW][k]=rc.a[3];
					data[i+2*pH][j][k]=rc.a[6];
					data[i+2*pH][j+pW][k]=rc.a[5];
					data[i+2*pH][j+2*pW][k]=rc.a[4];
				}
			}
		}
		// Save the picture
		savePic(fileNameHead+"_watermark.bmp");
		System.out.println("---------Add Watermark succeed!--------");
	}

	/**
	 * check the picture;
	 * 
	 * @param fileNameHead
	 * @param fileNameTail
	 */
	public void check(String fileNameHead, String fileNameTail) {
		readPic(fileNameHead, fileNameTail);
		int count = 0, temp;
		boolean flag[][] = new boolean[height][width];
		int minW = width, maxW = 0, minH = height, maxH = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				for (int k = 0; k < 1; k++) {
					if (!checkSum(data[i][j][k], WATERCRAB[i % WATERCRABSIZE][j
							% WATERCRABSIZE])) {
						if (minW > j) {
							minW = j;
						}
						if (maxW < j) {
							maxW = j;
						}
						if (minH > i) {
							minH = i;
						}
						if (maxH < i) {
							maxH = i;
						}
						flag[i][j] = true;
						count++;
						k = numBands;
					}
				}
			}
		}
		if(numBands==3){
			//System.out.println("The estimate wrong point:" + (int)(count*2) + " of " + width * height);
		}else{
			//System.out.println("The estimate wrong point:" + (int)(count*1.95) + " of " + width * height);
		}
				
		// System.out.println("!!!I think the wrong place is: ("+minW+","+minH+") to ("+maxW+","+maxH+") ");
		if (count < 2) {
			System.out.println("Completely the same");
			return;
		}
		if (count * 20 > width * height) {
			System.out
					.println("Might not add watermark in this picture,or is completely destroyed");
			return;
		}
		if ((maxW - minW) * (maxH - minH) * 20 > width * height) {
			System.out.println("Lots of places has been changed");
			return;
		}
		if(maxW-minW>width/3 ||maxH-minH>height/3){
			System.out.println("Sry,cannot restore..");
			return;
		}
		/*
		 * try to repair it
		 */
		int pH=height/3;
		int pW=width/3;
		for (int k = 0; k < numBands; k++) {
			for (int i = 0; i < pH; i++) {
				for (int j = 0; j < pW; j++) {
					LSBCode rc = new LSBCode(data[i][j][k], data[i][j + pW][k],
							data[i][j + 2 * pW][k], data[i + pH][j][k], data[i
									+ pH][j + pW][k],
							data[i + pH][j + 2 * pW][k],
							data[i + 2 * pH][j][k],
							data[i + 2 * pH][j + pW][k], data[i + 2 * pH][j + 2
									* pW][k]);
					if(!rc.checkCode()){
						int wrong=-1;
						if(isIn(i,j,minH,maxH,minW,maxW)){
							wrong=0;
							//System.out.println("Wrong=0");
						}
						if(isIn(i,j+pW,minH,maxH,minW,maxW)){
							wrong=1;
							//System.out.println("Wrong=1");
						}
						if(isIn(i,j+2*pW,minH,maxH,minW,maxW)){
							wrong=2;
							//System.out.println("Wrong=2");
						}
						if(isIn(i+pH,j,minH,maxH,minW,maxW)){
							wrong=7;
							//System.out.println("Wrong=7");
						}
						if(isIn(i+pH,j+pW,minH,maxH,minW,maxW)){
							wrong=8;
							//System.out.println("Wrong=8");
						}
						if(isIn(i+pH,j+pW*2,minH,maxH,minW,maxW)){
							wrong=3;
							//System.out.println("Wrong=3");
						}
						if(isIn(i+2*pH,j,minH,maxH,minW,maxW)){
							wrong=6;
							//System.out.println("Wrong=6");
						}
						if(isIn(i+2*pH,j+pW,minH,maxH,minW,maxW)){
							wrong=5;
							//System.out.println("Wrong=5");
						}
						if(isIn(i+2*pH,j+2*pW,minH,maxH,minW,maxW)){
							wrong=4;
							//System.out.println("Wrong=4");
						}
						rc.correctCode(wrong);
						data[i][j][k]=rc.a[0];
						data[i][j+pW][k]=rc.a[1];
						data[i][j+2*pW][k]=rc.a[2];
						data[i+pH][j][k]=rc.a[7];
						data[i+pH][j+pW][k]=rc.a[8];
						data[i+pH][j+2*pW][k]=rc.a[3];
						data[i+2*pH][j][k]=rc.a[6];
						data[i+2*pH][j+pW][k]=rc.a[5];
						data[i+2*pH][j+2*pW][k]=rc.a[4];
					}
				}
			}
		}
		savePic(fileNameHead+"_repair.bmp");
		System.out.println("---------Restore Picture succeed!--------");
		// needs an algorithm to find the place(s);
		/*
		while (true) {
			minW = width;
			minH = height;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (flag[i][j]) {
						minW = j;
						minH = i;
						i = height;
						j = width;
					}
				}
			}
			if (minW == width || minH == height) {
				break;
			}
			int a = 7, b = 7, k;
			do {
				k = 0;
				int i = minH + a;
				a++;
				if (i >= height || minW + b >= width)
					break;
				for (int j = 0; j < b; j++) {
					if (flag[i][j + minW]) {
						k++;
						break;
					}
				}
			} while (k != 0);
			do {
				k = 0;
				int j = minW - 1;
				minW--;
				if (minW < 0 || minH + a >= height)
					break;
				for (int i = 0; i < a; i++) {
					if (flag[i + minH][j]) {
						k++;
						break;
					}
				}
			} while (k != 0);
			minW++;
			do {
				k = 0;
				int j = minW + b;
				b++;
				if (j >= width || minH + a >= height)
					break;
				for (int i = 0; i < a; i++) {
					if (flag[i + minH][j]) {
						k++;
						break;
					}
				}
			} while (k != 0);
			do {
				k = 0;
				int i = minH + a;
				a++;
				if (i >= height || minW + b >= width)
					break;
				for (int j = 0; j < b; j++) {
					if (flag[i][j + minW]) {
						k++;
						break;
					}
				}
			} while (k != 0);
			//
			b -= 1;
			a -= 2;
			for (int i = minH; i < minH + a; i++) {
				for (int j = minW; j < minW + b; j++) {
					flag[i][j] = false;
				}
			}
			if (a * b > 100) {
				System.out.print("The wrong place is: (" + minW + "," + minH
						+ ") to (" + (minW + b - 1) + "," + (minH + a - 1)
						+ ") ");
				System.out.println("   Area:" + b * a);
			} else {
				System.out.println("There are a small wrong area in (" + minW
						+ "," + minH + ")");
			}

		}*/

		//

	}

	
	private boolean isIn(int i,int j,int minH,int maxH,int minW,int maxW){
		if((i-minH)*(i-maxH)<=0 && (j-minW)*(j-maxW)<=0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * calculate the 0-5th-bit
	 * 
	 * @param a
	 *            is 8-bit RGB
	 * @param b
	 *            is the WATERCRAB[][]
	 * @return the answer using xor
	 */
	private int calSum(int a, int b) {
		int ret = b, k = 4;
		for (int i = 0; i < 6; i++) {
			if ((a & k) != 0) {
				ret++;
			}
			k *= 2;
		}
		return ret % 2;
	}

	private boolean checkSum(int a, int b) {
		if ((a & 2) == calSum(a, b) * 2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * It's a watermark in the 6th bit,26*26,
	 */
	int WATERCRABSIZE = 26;
	int WATERCRAB[][] = {
			{ 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
					0, 1, 1, 1 },
			{ 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1,
					0, 0, 0, 1 },
			{ 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1,
					1, 0, 0, 1 },
			{ 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1,
					1, 0, 0, 1 },
			{ 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1,
					0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1,
					0, 0, 1, 1 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0,
					0, 0, 1, 1 },
			{ 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1,
					1, 1, 1, 1 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
					1, 1, 1, 1 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 1 },
			{ 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
					0, 0, 0, 1 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1,
					1, 1, 1, 1 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0,
					0, 0, 0, 1 },
			{ 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0 },
			{ 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 1, 1 },
			{ 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1,
					1, 1, 1, 1 },
			{ 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
					1, 1, 1, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					1, 1, 1, 1 },
			{ 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0,
					1, 1, 1, 1 },
			{ 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0,
					1, 1, 1, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 1, 1, 1 },
			{ 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
					0, 0, 0, 1 },
			{ 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
					1, 0, 0, 1 } };
	
	public static void calPSNR(String fileName1,String fileName2){
		try {
			// Read pic
			BufferedImage image1 = ImageIO.read(new File(fileName1));
			BufferedImage image2 = ImageIO.read(new File(fileName2));
			System.out.print("width=" + image1.getWidth() + "  height="
					+ image1.getHeight());
			// System.out.println(image.getType());
			WritableRaster raster1 = image1.getRaster();
			WritableRaster raster2 = image2.getRaster();
			int height = image1.getHeight();
			int width = image1.getWidth();
			int numBands = raster1.getNumBands();
			System.out.println("   NumBands=" + raster1.getNumBands());
			int[][][] data1 = new int[height][width][numBands];
			int[][][] data2 = new int[height][width][numBands];
			double sumD=0;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					raster1.getPixel(j, i, data1[i][j]);
					raster2.getPixel(j, i, data2[i][j]);
					for(int k=0;k<numBands;k++){
						sumD+=(data1[i][j][k]-data2[i][j][k])*(data1[i][j][k]-data2[i][j][k]);
						if((data1[i][j][k]-data2[i][j][k])*(data1[i][j][k]-data2[i][j][k])>10000){
							System.out.println(data1[i][j][k]+" "+data2[i][j][k]);
						}
					}
				}
			}
			//
			sumD=sumD/width/height/numBands;
			//System.out.println("N="+width*height*numBands);
			//System.out.println("SumD="+sumD);
			double PSNR=-10*Math.log10(256*256/sumD);
			System.out.println("PSNR="+PSNR);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

/*
 * class CrabPanel extends JPanel { public CrabPanel(String fileName) { super();
 * this.fileName = fileName; // bmp=new BmpParse(fileName); setImage(); }
 * 
 * public void setImage() { try { BufferedImage temp = ImageIO.read(new
 * File(fileName)); image = (Image) temp; } catch (IOException e) {
 * e.printStackTrace(); } }
 * 
 * private String fileName; // private BmpParse bmp; private Image image;
 * 
 * @Override protected void paintComponent(Graphics g) {
 * 
 * g.drawImage(image, 0, 0, null); } }
 */