import java.util.Arrays;
import java.util.BitSet;

public class RollingBalls {

	private static final int WALL = -2;
	private static final int NONE = -1;
	private static final int dx[] = new int[] { 0, 0, 1, -1 };
	private static final int dy[] = new int[] { 1, -1, 0, 0 };

	int H, W, WH;
	BitSet goal;
	int balls[][];

	public String[] restorePattern(String[] start, String[] target) {

		if (true) {
			for (String s : start)
				System.out.println(s);
			System.out.println();
			for (String s : target)
				System.out.println(s);
			System.out.println();
		}

		H = start.length + 2;
		W = start[0].length() + 2;
		WH = W * H;

		{
			balls = new int[WH][3];
			int p = 0;
			for (int i = 1; i + 1 < H; ++i) {
				for (int j = 1; j + 1 < W; ++j) {
					char c = target[i - 1].charAt(j - 1);
					if ('0' <= c && c <= '9') {
						balls[p][0] = i;
						balls[p][1] = j;
						balls[p][2] = c - '0';
						++p;
					}
				}
			}
			balls = Arrays.copyOf(balls, p);
		}

		return new String[0];
	}

	class State {
		int score;
		int board[][];

		State(String[] start) {
			board = new int[H][W];
			for (int i = 0; i < H; ++i)
				Arrays.fill(board, WALL);
			for (int i = 1; i + 1 < H; ++i) {
				for (int j = 1; j + 1 < W; ++j) {
					char c = start[i - 1].charAt(j - 1);
					if (c == '.') board[i][j] = NONE;
					else if ('0' <= c && c <= '9') board[i][j] = c - '0';
				}
			}
			score = 0;
		}

		void next() {
			for (int pos[] : balls) {
				int i = pos[0];
				int j = pos[1];
				if (board[i][j] == NONE) {

				}
			}
		}

		boolean dfs(int i, int j, int depth) {
			if (depth < 0) return false;
			for (int k = 0; k < 4; ++k) {
				int a = i + dy[k];
				int b = j + dx[k];
			}
			return false;
		}
	}
}
