import java.util.BitSet;

public class RollingBalls {

	int H, W, WH;
	BitSet goal;

	public String[] restorePattern(String[] start, String[] target) {

		if (true) {
			for (String s : start)
				System.out.println(s);
			System.out.println();
			for (String s : target)
				System.out.println(s);
			System.out.println();
		}

		H = start.length;
		W = start[0].length();
		WH = W * H;

		return new String[0];
	}
}
