package works.hop.field.graph;

public class MazeArray {

    final int startRow;
    final int startCol;
    final int endRow;
    final int endCol;
    final int[][] maze;
    final boolean[][] visited;

    public MazeArray(int[][] maze, int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.maze = maze;
        this.visited = new boolean[maze.length][maze.length];
    }

    public static void main(String[] args) {
        final int[][] grid = {
                {1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 1, 1, 0, 0, 1},
                {0, 1, 0, 1, 0, 1, 0, 0, 1, 1},
                {0, 1, 1, 1, 0, 0, 0, 1, 0, 0},
                {1, 1, 0, 0, 1, 0, 0, 1, 0, 1},
                {0, 0, 1, 1, 0, 0, 1, 1, 0, 1},
                {1, 0, 0, 1, 1, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                {0, 0, 0, 1, 1, 0, 0, 0, 0, 1},
                {0, 0, 0, 1, 1, 0, 1, 1, 0, 1}
        };

        MazeArray maze = new MazeArray(grid, 1, 1, 9, 8);
        maze.findWay();
    }

    public void findWay() {
        if (dfs(startRow, startCol))
            System.out.println("Path exists");
        else
            System.out.println("No path exists");
    }

    private int cell(int r, int c) {
        return (r * maze.length) + c;
    }

    private boolean dfs(int r, int c) {
        if (r == endRow && c == endCol)
            return true;
        if (isFeasible(r, c)) {
            visited[r][c] = true;
            System.out.println("Visiting " + cell(r, c));
            if (dfs(r - 1, c)) //try up
                return true;
            if (dfs(r, c + 1)) // try right
                return true;
            if (dfs(r + 1, c)) //try down
                return true;
            return dfs(r, c - 1); //try left
        }
        return false;
    }

    private boolean isFeasible(int r, int c) {
        if (r < 0 || r > maze.length - 1) return false;
        if (c < 0 || c > maze.length - 1) return false;
        if (maze[r][c] == 1) return false;
        return !visited[r][c];
    }
}
