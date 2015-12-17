import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RollingBalls {

	private static final int WALL = -2;
	private static final int NONE = -1;

	int D[];
	int H, W, WH;
	int balls[][], goal[];

	public String[] restorePattern(String[] start, String[] target) {

		if (false) {
			for (String s : start)
				System.out.println(s);
			System.out.println();
			for (String s : target)
				System.out.println(s);
			System.out.println();
		}
		{
			H = start.length + 2;
			W = start[0].length() + 2;
			WH = W * H;
			D = new int[] { -1, W, 1, -W };
		}
		{
			balls = new int[WH][2];
			int p = 0;
			for (int i = 1; i + 1 < H; ++i) {
				for (int j = 1; j + 1 < W; ++j) {
					char c = target[i - 1].charAt(j - 1);
					if ('0' <= c && c <= '9') {
						balls[p][0] = i * W + j;
						balls[p][1] = c - '0';
						++p;
					}
				}
			}
			balls = Arrays.copyOf(balls, p);
			goal = parse(target);
		}

		State init = new State(start), best = init;
		final int size = 0xfff;
		State queue[] = new State[size + 1];
		int qi = 0, qs = 1;
		queue[0] = init;
		while (qi != qs) {
			State s = queue[qi];
			qi = (qi + 1) & size;
			for (State c : s.child()) {
				queue[qs] = c;
				qs = (qs + 1) & size;
				if (best.score < c.score) best = c;
			}
		}
		return toAnswer(best);
	}

	class State {
		State parent;
		int score, board[], moves[][];

		State(String[] start) {
			parent = null;
			board = parse(start);
			score = 0;
		}

		State(State s) {
			parent = s;
			this.score = s.score;
			this.board = Arrays.copyOf(s.board, s.board.length);
		}

		int[] move() {
			boolean used[] = new boolean[WH];
			int queue[] = new int[WH], qi, qs, res[] = new int[WH];
			int move[][] = new int[WH][5];
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
						if (move[p][0] == 0) {
							for (int d : D) {
								int n = p;
								while (board[n + d] == NONE)
									n += d;
								if (n != p) {
									if (n + d != i && !used[n]) {
										used[n] = true;
										queue[qs++] = n;
										if (res[n] == -1 || goal[n] == board[i]) res[n] = i;
									}
									// move[p][++move[p][0]] = n;
								}
							}
						} else {
							for (int j = 1; j <= move[p][0]; ++j) {
								int n = move[p][j];
								if (!used[n]) {
									used[n] = true;
									queue[qs++] = n;
									if (res[n] == -1 || goal[n] == board[i]) res[n] = i;
								}
							}
						}
					}
				}
			}
			return res;
		}

		State[] child() {
			State next[] = new State[0xfff];
			int ni = 0;
			int move[] = move();
			for (int pos[] : balls) {
				int p = pos[0];
				if (board[p] == NONE && move[p] != -1) {
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
