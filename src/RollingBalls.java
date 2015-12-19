import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RollingBalls {

	private static final int WALL = -2;
	private static final int NONE = -1;

	int D[];
	int H, W, WH;
	int balls[], goal[];

	public String[] restorePattern(String[] start, String[] target) {
		{
			H = start.length + 2;
			W = start[0].length() + 2;
			WH = W * H;
			D = new int[] { -1, W, 1, -W };
		}
		{
			balls = new int[WH];
			int p = 0;
			for (int i = 1; i + 1 < H; ++i) {
				for (int j = 1; j + 1 < W; ++j) {
					char c = target[i - 1].charAt(j - 1);
					if ('0' <= c && c <= '9') balls[p++] = i * W + j;
				}
			}
			balls = Arrays.copyOf(balls, p);
			goal = parse(target);
		}
		final int size = 0xfff;
		State init = new State(start), best = init;
		State queue[] = new State[size];
		State next[] = new State[size];
		int qs = 1, ns = 0;
		queue[0] = init;
		while (0 < qs) {
			max: for (int i = 0; i < qs; ++i) {
				State s = queue[i];
				for (State c : s.child()) {
					next[ns++] = c;
					if (best.score < c.score) best = c;
					if (ns == size) break max;
				}
			}

			State tmp[] = next;
			next = queue;
			queue = tmp;

			qs = ns;
			ns = 0;
		}
		return toAnswer(best);
	}

	class State {
		State parent;
		int score, board[], moves[][];

		State(String[] start) {
			parent = null;
			board = parse(start);
			for (int i = 0; i < WH; ++i)
				if (goal[i] >= 0 && board[i] == goal[i]) board[i] = WALL;
			score = 0;
		}

		State(State s) {
			this.parent = s;
			this.score = s.score;
			this.board = Arrays.copyOf(s.board, s.board.length);
		}

		int[] move() {
			boolean[] used = new boolean[WH];
			int queue[] = new int[WH], qi, qs, res[] = new int[WH];
			int move[][] = new int[WH][4];
			Arrays.fill(res, -1);
			for (int i = 0; i < WH; ++i) {
				if (board[i] >= 0) {
					Arrays.fill(used, false);
					res[i] = i;
					qi = 0;
					qs = 1;
					queue[0] = i;
					while (qi < qs) {
						int p = queue[qi++];
						for (int j = 0; j < 4; ++j) {
							int n = move[p][j];
							if (n == 0) {
								int j2 = (j + 2) & 3, d = D[j], rev = -1;
								if (board[p] == NONE) {
									if (board[p - d] != NONE) rev = p;
								} else {
									if (board[p + d] != NONE) rev = p + d;
								}
								n = p;
								while (board[n + d] == NONE) {
									n += d;
									if (rev != -1) move[n][j2] = rev;
								}
								if (n != p && n + d != i && !used[n]) {
									used[n] = true;
									queue[qs++] = n;
									if (res[n] == -1 && goal[n] == board[i]) res[n] = i;
								}
								move[p][j] = n;
							} else if (n != p && n + D[j] != i && !used[n]) {
								used[n] = true;
								queue[qs++] = n;
								if (res[n] == -1 && goal[n] == board[i]) res[n] = i;
							}
						}
					}
				}
			}
			return res;
		}

		State[] child() {
			State next[] = new State[balls.length];
			int ni = 0, move[] = move();
			for (int p : balls) {
				if (board[p] == NONE && move[p] != -1 && goal[p] == board[move[p]]) {
					State s = new State(this);
					if (goal[p] >= 0) s.score += goal[p] == s.board[move[p]] ? 2 : 1;
					s.board[p] = WALL;
					s.board[move[p]] = NONE;
					s.moves = new int[][] { { move[p], p } };
					next[ni++] = s;
				}
			}
			return Arrays.copyOf(next, ni);
		}
	}

	String[] toAnswer(State s) {
		ArrayList<ArrayList<String>> res = new ArrayList<>();
		while (s.parent != null) {
			final int board[] = Arrays.copyOf(s.parent.board, WH);
			for (int[] x : s.moves) {
				final int from = x[0], to = x[1];
				final ArrayList<String> course = new ArrayList<>();
				int prev[] = new int[WH], queue[] = new int[WH], qi = 0, qs = 1;
				Arrays.fill(prev, -1);
				queue[0] = from;
				while (qi < qs && prev[to] == -1) {
					int p = queue[qi++];
					for (int d : D) {
						int n = p;
						while (board[n + d] == NONE)
							n += d;
						if (n + d != from && prev[n] == -1) {
							prev[n] = p;
							queue[qs++] = n;
						}
					}
				}
				int p = to;
				while (true) {
					int n = prev[p];
					int d = -1;
					if (n / W == p / W && n > p) d = 0;
					else if (n % W == p % W && n < p) d = 1;
					else if (n / W == p / W && n < p) d = 2;
					else if (n % W == p % W && n > p) d = 3;
					course.add(String.format("%d %d %d", (n / W) - 1, (n % W) - 1, d));
					p = n;
					if (p == from) break;
				}
				Collections.reverse(course);
				res.add(course);
			}
			s = s.parent;
		}
		Collections.reverse(res);
		ArrayList<String> tmp = new ArrayList<>();
		for (ArrayList<String> list : res)
			tmp.addAll(list);
		return tmp.toArray(new String[tmp.size()]);
	}

	int[] parse(String[] x) {
		int[] res = new int[WH];
		Arrays.fill(res, WALL);
		for (int i = 1; i + 1 < H; ++i) {
			for (int j = 1; j + 1 < W; ++j) {
				char c = x[i - 1].charAt(j - 1);
				if (c == '.') {
					res[i * W + j] = NONE;
				} else if ('0' <= c && c <= '9') {
					res[i * W + j] = c - '0';
				}
			}
		}
		return res;
	}

	private void debug(Object... o) {
		System.err.println(Arrays.deepToString(o));
	}
}
