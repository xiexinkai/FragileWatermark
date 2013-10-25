public class HammingCode {
	int BIT[] = { 1, 2, 4, 8, 16, 32, 64, 128 };
	public int a[] = new int[9];

	public HammingCode(int a00, int a01, int a02, int a10, int a11, int a12,
			int a20, int a21, int a22) {
		a[0] = a00;
		a[1] = a01;
		a[2] = a02;
		a[3] = a10;
		a[4] = a11;
		a[5] = a12;
		a[6] = a20;
		a[7] = a21;
		a[8] = a22;
	}

	/**
	 * calculate the HammingCode data in all 64bit. a[4] 0-7th bit,others 1-7th
	 * bit check in all 8bit. a[0-3,5-7] 0th bit
	 */
	public void calHammingCode() {
		// define
		int temp[] = new int[256];
		for (int i = 0; i < 256; i++)
			temp[i] = 0;
		int count = 1;
		// init
		count = initArray(temp);
		// calculate
		for (int i = 1; i <= count; i++) {// 64+7+1
			for (int j = 0; j < 8; j++) {
				if (temp[i] == 1 && (i & BIT[j]) != 0) {
					temp[BIT[j]] ^= 1;
				}
			}
		}
		// back to the LSB
		a[0] = setLowestBit(a[0], temp[BIT[0]]);
		a[1] = setLowestBit(a[1], temp[BIT[1]]);
		a[2] = setLowestBit(a[2], temp[BIT[2]]);
		a[3] = setLowestBit(a[3], temp[BIT[3]]);
		a[5] = setLowestBit(a[5], temp[BIT[4]]);
		a[6] = setLowestBit(a[6], temp[BIT[5]]);
		a[7] = setLowestBit(a[7], temp[BIT[6]]);
		a[8] = setLowestBit(a[8], temp[BIT[7]]);
	}

	/**
	 * check but not to correct
	 */
	public boolean checkHammingCode() {
		// define
		int temp[] = new int[256];
		for (int i = 0; i < 256; i++)
			temp[i] = 0;
		int sum[] = new int[8];
		int count = 1;
		// init
		count = initArray(temp);
		// printTemp(temp,69);
		// check
		for (int i = 1; i <= count; i++) {
			for (int j = 0; j < 8; j++) {// BIT[]
				if (temp[i] == 1 && (i & BIT[j]) != 0) {
					sum[j]++;
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			// System.out.println("Sum "+i+" = "+sum[i]);
			if (sum[i] % 2 != (a[i] & 1)) {
				return false;
			}
		}
		for (int i = 4; i < 8; i++) {
			// System.out.println("Sum "+i+" = "+sum[i]);
			if (sum[i] % 2 != (a[i + 1] & 1)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * correct the pic
	 * 
	 * @return 1:correct succesfully 0:is it right -1:error
	 */
	public int correctHammingCode() {
		// define
		int temp[] = new int[256];
		for (int i = 0; i < 256; i++)
			temp[i] = 0;
		int sum[] = new int[8];
		int count = 1;
		// init
		count = initArray(temp);
		// check
		for (int i = 1; i <= count; i++) {
			for (int j = 0; j < 8; j++) {// BIT[]
				if (temp[i] == 1 && (i & BIT[j]) != 0) {
					sum[j]++;
				}
			}
		}
		boolean needCorrect = false;
		for (int i = 0; i < 4; i++) {
			// System.out.println("Sum "+i+" = "+sum[i]);
			if (sum[i] % 2 != (a[i] & 1)) {
				sum[i] = 1;
				needCorrect = true;
			} else {
				sum[i] = 0;
			}
		}
		for (int i = 4; i < 8; i++) {
			// System.out.println("Sum "+i+" = "+sum[i]);
			if (sum[i] % 2 != (a[i + 1] & 1)) {
				sum[i] = 1;
				needCorrect = true;
			} else {
				sum[i] = 0;
			}
		}
		if (!needCorrect)
			return 0;
		// correct
		int num = 0;
		for (int i = 0; i < 8; i++) {
			if (sum[i] == 1) {
				num += BIT[i];
			}
		}
		System.out.println("correct bit:" + num);
		return 0;
	}

	/**
	 * init nine int to a 0-1 array
	 * 
	 * @return count:temp[1],...,temp[count] is valid
	 */
	private int initArray(int[] temp) {
		int place = 1;
		for (int i = 0; i < 9; i++) {// a[0-8]
			int j;
			if (i == 4) {
				j = 0;
			} else {
				j = 1;
			}
			for (; j < 8; j++) {// BIT[1-7]
				boolean flag = false;
				while (!flag) {
					flag = true;
					for (int k = 0; k < 8; k++) {
						if (place == BIT[k]) {
							flag = false;
							break;
						}
					}
					if (!flag)
						place++;
				}
				temp[place] = (a[i] & BIT[j]) / BIT[j];
				place++;
			}
		}
		return place;
	}

	/**
	 * set the lowest bit in int dst
	 */
	private int setLowestBit(int dst, int flag) {
		int ret;
		if (flag != 0) {
			ret = dst | 1;
		} else {
			ret = dst & 0xFE;
		}
		return ret;
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
		int a5 = 0;
		int a6 = 0;
		int a7 = 0;
		int a8 = 0;
		HammingCode hmCode = new HammingCode(a0, a1, a2, a3, a4, a5, a6, a7, a8);
		hmCode.calHammingCode();
		hmCode.printArray();
		if (hmCode.checkHammingCode()) {
			System.out.println("Check Ok!");
		} else {
			System.out.println("Check Wrong...");
		}
	}
}
