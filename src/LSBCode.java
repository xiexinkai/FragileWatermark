public class LSBCode {
	static int BIT[] = { 1, 2, 4, 8, 16, 32, 64, 128 };
	public int a[] = new int[9];

	public LSBCode(int a00, int a01, int a02, int a10, int a11, int a12,
			int a20, int a21, int a22) {
		a[0] = a00;
		a[1] = a01;
		a[2] = a02;
		a[3] = a12;
		a[4] = a22;
		a[5] = a21;
		a[6] = a20;
		a[7] = a10;
		a[8] = a11;
	}

	/**
	 * calculate and save to the LSB
	 */
	public void calCode() {
		int temp[] = new int[8];
		this.encode(temp);
		// save to LSB
		for (int i = 0; i < 8; i++) {
			if (temp[i] == 1) {
				a[i] |= 0x1;
			} else {
				a[i] &= 0xfe;
			}
		}
	}

	/**
	 * check but not to correct
	 */
	public boolean checkCode() {
		int temp[] = new int[8];
		this.encode(temp);
		for (int i = 0; i < 8; i++) {
			if (temp[i] != (a[i] & BIT[0])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * correct the wrong bit,in the piece n
	 */
	public void correctCode(int piece) {
		int temp[] = new int[8];
		this.encode(temp);
		//bit 1-7
		for(int i=1;i<=7;i++){
			if(i!=piece){
				a[piece]^=(temp[i]^(a[i]&BIT[0]))*BIT[i];
			}
		}
		//bit i
		if(piece>=1 && piece<=7){
			a[piece]^=(temp[0]^(a[0]&BIT[0]))*BIT[piece];
		}
		//piece==8
		if(piece==8){
			a[piece]^=(temp[0]^(a[0]&BIT[0]))*BIT[0];
		}
		//
		this.calCode();
	}

	/**
	 * encode
	 */
	private void encode(int[] temp) {
		// init
		for (int i = 0; i < temp.length; i++) {
			temp[i] = 0;
		}
		// encode 1-7th bit
		for (int i = 1; i <= 7; i++) {
			for (int j = 0; j <= 8; j++) {
				if ((a[j] & BIT[i]) != 0 && i != j) {
					temp[i] ^= 1;
				}
			}
		}
		// encode 0th bit
		for (int i = 1; i <= 7; i++) {
			temp[0] ^= (a[i] & BIT[i]) / BIT[i];
		}
		temp[0] ^= (a[8] & BIT[0]);
	}

	/**
	 * functions below is only for test
	 */
	public void printTemp(int[] temp, int count) {
		// for test
		for (int i = 0; i <= count; i++) {
			if (i % 10 == 0 && i > 0) {
				System.out.println("----" + (i - 10));
			}
			System.out.print(temp[i] + " ");
		}
		System.out.println();
		//
	}

	public void printArray() {
		System.out.print("a[]=");
		for (int i = 0; i < 9; i++) {
			System.out.print(Integer.toHexString(a[i]) + ",");
		}
		System.out.println();
	}

	public static void main(String[] args) {
		int a0 = 0x0;
		int a1 = 0xfc;
		int a2 = 0x0;
		int a3 = 0x0;
		int a4 = 0x3;
		int a5 = 0xea;
		int a6 = 0;
		int a7 = 0;
		int a8 = 0;
		LSBCode code = new LSBCode(a0, a1, a2, a3, a4, a5, a6, a7, a8);
		code.calCode();
		code.printArray();
		code.a[3]=0xeb;
		if(code.checkCode()){
			System.out.println("Right!");
		}else{
			System.out.println("Wrong!");
		}
		code.correctCode(3);
		code.printArray();
	}
}
