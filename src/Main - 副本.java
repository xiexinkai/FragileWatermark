import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();
		String fileNameHead = "qubi";
		String fileNameHead2 = "qubi";
		String fileNameTail = "bmp";
		// main.go();
		//main.deal(fileNameHead,fileNameTail);
		main.check(fileNameHead2,fileNameTail);

	}

	JFrame frame;

	/*
	public void go() {
		frame = new JFrame("Fragile Watermark");
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CrabPanel panel = new CrabPanel("lena.bmp");
		frame.getContentPane().add(panel);
		frame.setVisible(true);
	}*/

	int height;
	int width;
	int numBands;
	int data[][][];
	BufferedImage image;
	WritableRaster raster;

	public void readPic(String fileNameHead,String fileNameTail) {
		try {
			//Read pic
			image = ImageIO.read(new File(fileNameHead + "." + fileNameTail));
			System.out.print("width="+image.getWidth() + "  height="
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
	 * @param fileNameHead
	 */
	public void savePic(String fileNameHead){
		try {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					raster.setPixel(j, i, data[i][j]);
				}
			}
			ImageIO.write(image, "bmp", new File(fileNameHead
					+ "_watermark." + "bmp"));
			System.out.println("---------Add Watermark succeed!--------");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * add watermark,and save the new picture
	 * @param fileNameHead
	 * @param fileNameTail
	 */
	public void deal(String fileNameHead,String fileNameTail) {
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
		readPic(fileNameHead,fileNameTail);
		/*
		 * Start to add watermark
		 */
		/*
		 * create the WATERCRAB[][] code 
		 * int temp; for(int i=0;i<height;i++){
		 * System.out.print("{"); for(int j=0;j<width;j++){
		 * if(data[i][j][0]==0){ temp=0; }else{ temp=1; }
		 * System.out.print(temp); if(j!=width-1){ System.out.print(","); } }
		 * System.out.println("},"); }
		 */
		// The first step:add a 0-1 picture in the 6th bit
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				for(int k=0;k<numBands;k++){
					if(calSum(data[i][j][k],WATERCRAB[i%WATERCRABSIZE][j%WATERCRABSIZE])==0){
						data[i][j][k]&=0xfe;
					}else{
						data[i][j][k]|=0x1;
					}
				}
			}
		}
		//Save the picture
		savePic(fileNameHead);
	}

	/**
	 * check the picture;
	 * @param fileNameHead
	 * @param fileNameTail
	 */
	public void check(String fileNameHead,String fileNameTail){
		readPic(fileNameHead,fileNameTail);
		int count=0,temp;
		boolean flag[][]=new boolean[height][width];
		int minW=width,maxW=0,minH=height,maxH=0;
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				for(int k=0;k<numBands;k++){
					if(!checkSum(data[i][j][k],WATERCRAB[i%WATERCRABSIZE][j%WATERCRABSIZE])){
							if(minW>j){
								minW=j;
							}
							if(maxW<j){
								maxW=j;
							}
							if(minH>i){
								minH=i;
							}
							if(maxH<i){
								maxH=i;
							}
						flag[i][j]=true;
						count++;
						k=numBands;
					}
				}			
			}
		}
		System.out.println("The wrong point:"+count+" of "+width*height);
		//System.out.println("!!!I think the wrong place is: ("+minW+","+minH+") to ("+maxW+","+maxH+") ");
		if(count<2){
			System.out.println("Completely the same");
			return;
		}
		if(count*20>width*height){
			System.out.println("Might not add watermark in this picture,or is completely destroyed");
			return;
		}
		//needs an algorithm to find the place(s);
		while(true){
			minW=width;minH=height;
			for(int i=0;i<height;i++){
				for(int j=0;j<width;j++){
					if(flag[i][j]){
						minW=j;
						minH=i;
						i=height;
						j=width;
					}
				}
			}
			if(minW==width||minH==height){
				break;
			}
			int a=7,b=7,k;
			do{
				k=0;
				int i=minH+a;
				a++;
				if(i>=height||minW+b>=width) break;
				for(int j=0;j<b;j++){
					if(flag[i][j+minW]){
						k++;
						break;
					}
				}
			}while(k!=0);
			do{
				k=0;
				int j=minW-1;
				minW--;
				if(minW<0||minH+a>=height) break;
				for(int i=0;i<a;i++){
					if(flag[i+minH][j]){
						k++;
						break;
					}
				}
			}while(k!=0);
			minW++;
			do{
				k=0;
				int j=minW+b;
				b++;
				if(j>=width||minH+a>=height) break;
				for(int i=0;i<a;i++){
					if(flag[i+minH][j]){
						k++;
						break;
					}
				}
			}while(k!=0);
			do{
				k=0;
				int i=minH+a;
				a++;
				if(i>=height||minW+b>=width) break;
				for(int j=0;j<b;j++){
					if(flag[i][j+minW]){
						k++;
						break;
					}
				}
			}while(k!=0);
			//
			b-=1;a-=2;
			for(int i=minH;i<minH+a;i++){
				for(int j=minW;j<minW+b;j++){
					flag[i][j]=false;
				}
			}
			if(a*b>100){
				System.out.print("The wrong place is: ("+minW+","+minH+") to ("+(minW+b-1)+","+(minH+a-1)+") ");
				System.out.println("   Area:"+b*a);
			}else{
				System.out.println("There are a small wrong area in ("+minW+","+minH+")");
			}

		}
		
		//


		
	}
	
	/**
	 * calculate the 7th-bit
	 * @param a is 8-bit RGB
	 * @param b is the WATERCRAB[][]
	 * @return  the answer using xor
	 */
	private int calSum(int a,int b){
		int ret=b,k=2;
		for(int i=0;i<7;i++){
			if((a&k)!=0){
				ret++;
			}
			k*=2;
		}
		return ret%2;
	}
	private boolean checkSum(int a,int b){
		if((a&1)==calSum(a,b)){
			return true;
		}else{
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
}

/*
class CrabPanel extends JPanel {
	public CrabPanel(String fileName) {
		super();
		this.fileName = fileName;
		// bmp=new BmpParse(fileName);
		setImage();
	}

	public void setImage() {
		try {
			BufferedImage temp = ImageIO.read(new File(fileName));
			image = (Image) temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String fileName;
	// private BmpParse bmp;
	private Image image;

	@Override
	protected void paintComponent(Graphics g) {

		g.drawImage(image, 0, 0, null);
	}
}*/